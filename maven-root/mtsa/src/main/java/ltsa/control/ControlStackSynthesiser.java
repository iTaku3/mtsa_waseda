package ltsa.control;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import ltsa.lts.CompactState;
import ltsa.lts.CompositeState;
import ltsa.lts.CompositionExpression;
import ltsa.lts.LTSOutput;
import ltsa.lts.Minimiser;
import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;
import MTSTools.ac.ic.doc.mtstools.model.impl.LTSSimulationSemantics;
import ltsa.ac.ic.doc.mtstools.util.fsp.AutomataToMTSConverter;
import ltsa.ac.ic.doc.mtstools.util.fsp.MTSToAutomataConverter;
import ltsa.control.util.GoalDefToControllerGoal;
import MTSSynthesis.controller.model.ControllerGoal;
import ltsa.dispatcher.TransitionSystemDispatcher;

public class ControlStackSynthesiser
{  
  private static final long SPECIAL_INIT_STATE = 9997;
  private static final long SPECIAL_EXCEP_STATE1 = 9998;
  private static final long SPECIAL_EXCEP_STATE2 = 9999;
  
  /*
  Synthesise a control stack consisting of a number of GR control problems (tiers).
  The lowest tier has the most realistic environment model, and the highest tier has the most idealised.
  The controller of lower tiers restricts the environment of the upper tiers.
  Daniel Sykes 2013
  */
  public static CompactState synthesiseControlStack(CompositeState cStackDef, LTSOutput output)
  {
    ControlStackDefinition stackDef = ControlStackDefinition.getDefinition(cStackDef.name);
    output.outln("Building control stack '"+stackDef.getName()+"'");
    List<ControlTierDefinition> tiers = stackDef.getTiers();
    
    //sanity check number for specific tier
    if (cStackDef.controlStackSpecificTier < -1 || cStackDef.controlStackSpecificTier > stackDef.tierCount() || cStackDef.controlStackSpecificTier == 0)
    {
      output.outln("Control stack synthesis FAILED: Parameter "+cStackDef.controlStackSpecificTier+" is not a valid tier number.");
      return null;
    }
    
    //sanity check simulation first
    if (!checkStackSimulation(cStackDef, output))
      return null;
    
    long startTime = System.currentTimeMillis();
    
    List<CompactState> controllers = new Vector<CompactState>();
    Vector<CompactState> exceptionControllers = new Vector<CompactState>();
    
    CompactState envWithInitialTrace = null; //environment where initial states are specified (with a trace)
    List<Long> initialStates = null; //those states
    
    for (int i = tiers.size()-1; i >= 0; i--)
    {
      ControlTierDefinition tier = tiers.get(i);
      int tierNum = tiers.size()-i;
      
      //build environment
//      System.out.println("building environment");
      CompactState envModel = getControlStackEnvironment(cStackDef, tier.getEnvModel().toString(), output);
      if (envModel == null)
        throw new RuntimeException("Failed to get environment model '"+tier.getEnvModel().toString()+"' from "+cStackDef.controlStackEnvironments);
      
      //set initial states from trace, if provided
      List<String> initialTrace = tier.getInitialTrace();
      if (initialTrace != null && initialTrace.size() > 0)
      {
        if (envWithInitialTrace != null)
        {
          output.outln("ERROR: an initial trace has already been specified in another tier ("+envWithInitialTrace.getName()+")");
          return null;
        }
        
//        System.out.println("initial trace provided in tier "+tierNum+": "+initialTrace);
        output.outln("Using initial trace "+initialTrace+" in tier "+tierNum);
        envWithInitialTrace = envModel;
        MTS<Long,String> env = AutomataToMTSConverter.getInstance().convert(envWithInitialTrace);
        initialStates = LTSWalker.walk(env, initialTrace);
        if (initialStates.size() > 0)
        {
          output.outln("Adding taus to new initial states "+initialStates);
          setInitialStates(env, envWithInitialTrace.getName(), initialStates);
          envModel = MTSToAutomataConverter.getInstance().convert(env, envWithInitialTrace.getName());
        }
        else
        {
          output.outln("ERROR: initial trace not present in environment "+envModel.getName());
          return null;
        }
      }
      
      //set initial states from states in lower tier, if provided
      if (envWithInitialTrace != null && initialStates != null)
      {
//        System.out.println("setting initial states to those simulating initial states of lower tier");
        
        //find the matching initial states
        MTS<Long,String> lowerEnv = AutomataToMTSConverter.getInstance().convert(envWithInitialTrace);
        MTS<Long,String> upperEnv = AutomataToMTSConverter.getInstance().convert(envModel);
        BinaryRelation<Long,Long> stateMapping = new LTSSimulationSemantics().getLargestRelation(lowerEnv, upperEnv);
        List<Long> upperInitialStates = new Vector<Long>();
        for (Long s : initialStates)
          upperInitialStates.addAll(stateMapping.getImage(s));
        
        output.outln("Adding taus to new initial states "+upperInitialStates);
        setInitialStates(upperEnv, envModel.getName(), upperInitialStates);
        envModel = MTSToAutomataConverter.getInstance().convert(upperEnv, envModel.getName());
      }
      
      //push out a Prism representation
      generatePrismMDP(envModel, stackDef.getControllableActions());
      
      //build a gr goal
//      System.out.println("building goal "+tier.getGoal());
      ControllerGoalDefinition tierGoalDef = ControllerGoalDefinition.getDefinition(tier.getGoal());
      Collection<CompactState> safetyMachines = CompositionExpression.preProcessSafetyReqs(tierGoalDef, output);
      ControllerGoal<String> tierGoal = GoalDefToControllerGoal.getInstance().buildControllerGoal(tierGoalDef);
      //cStackDef.machines.addAll(safetyMachines); //for output
      
      //synthesise
//      System.out.println("synthesising...");
      
      //solve safety!
      if (tierNum > 1) //restrict with prior controller
        safetyMachines.add(controllers.get(0));
      envModel = solveSafety(envModel, safetyMachines, output);
      
      //ensure there are no taus
      Minimiser d = new Minimiser(envModel, output);
      envModel = d.trace_minimise();
      
      Vector<CompactState> machines = new Vector<CompactState>();
      machines.add(envModel);
      CompositeState envComposite = new CompositeState(envModel.name, machines);
      envComposite.composition = envModel;
      envComposite.goal = tierGoal;
      
//      //System.out.println("ENV-Alpha "+tierNum+"   "+envComposite.composition.getAlphabetV());
      
      CompactState controller = TransitionSystemDispatcher.synthesise(envComposite, tierGoal, output);
      if (controller != null)
      {
//        //System.out.println("Alpha "+tierNum+" pre-clean "+controller.getAlphabetV());
        //controller = cleanUpAlphabet(controller);
//        //System.out.println("Alpha "+tierNum+" post-clean "+controller.getAlphabetV());
        controller.name = stackDef.getName().toString()+"_CONTROLLER"+tierNum;
        output.outln("Control stack '"+stackDef.getName()+"': controller for tier "+tierNum+" synthesised.");
//        System.out.println("CONTROLLER LEVEL "+tierNum+" SYNTHESISED");
        controllers.add(0, controller);
        
        if (cStackDef.controlStackSpecificTier == tierNum) //return this tier, not the whole shebang
        {
//          System.out.println("Stopping at tier "+tierNum);
          cStackDef.machines.addAll(controllers);
          cStackDef.composition = controller;
          return cStackDef.composition;
        }
        
        CompactState excController = addControllerExceptions(tierNum, controller, stackDef.getControllableActions());
        excController.name += ".EX";
        exceptionControllers.add(0, excController);
//        System.out.println("Exception states added");
      }
      else
      {
        output.outln("Control stack synthesis FAILED at tier "+tierNum);
        return null;
      }
    }
    //compose everything
    output.outln("Control stack '"+stackDef.getName()+"': composing all tiers...");
    CompositeState controlStack = new CompositeState(stackDef.getName().toString(), exceptionControllers);
    TransitionSystemDispatcher.applyComposition(controlStack, output);
    cStackDef.composition = controlStack.composition;
    cStackDef.machines.addAll(controllers);
    cStackDef.machines.addAll(exceptionControllers);
    //cStackDef.composition = cleanUpAlphabet(cStackDef.composition);
//    System.out.println("Final Alphabet "+cStackDef.composition.getAlphabetV());
    output.outln("Control stack synthesis took "+(System.currentTimeMillis() - startTime)+" milliseconds");
    return cStackDef.composition;
  }

  public static boolean checkStackSimulation(CompositeState cStackDef, LTSOutput output)
  {
    List<ControlTierDefinition> tiers = ControlStackDefinition.getDefinition(cStackDef.name).getTiers();
    for (int i = tiers.size()-1; i >= 1; i--)
    {
      ControlTierDefinition tierLower = tiers.get(i);
      ControlTierDefinition tierHigher = tiers.get(i-1);
      int tierNum = tiers.size()-i;

      CompactState lowerEnv = getControlStackEnvironment(cStackDef, tierLower.getEnvModel().toString(), output);
      CompactState higherEnv = getControlStackEnvironment(cStackDef, tierHigher.getEnvModel().toString(), output);
      
      boolean simulates = true;
      if (higherEnv != lowerEnv) //shortcut for identical environs
        simulates = TransitionSystemDispatcher.isLTSRefinement(higherEnv, lowerEnv, output);
      
      if (simulates)
        output.outln("'"+lowerEnv.name+"' simulates '"+higherEnv.name+"'.");
      else
      {
        output.outln("Control stack synthesis FAILED: Tier "+tierNum+" environment does not simulate tier "+(tierNum+1)+" environment.");
        return false;
      }
    }
    return true;
  }
  
  private static void setInitialStates(MTS<Long,String> env, String envName, List<Long> initialStates)
  {
    env.addState(SPECIAL_INIT_STATE);
    for (Long s : initialStates)
    {
//      System.out.println("  adding initial tau to "+s);
      env.addAction("init_"+envName);
      env.addTransition(SPECIAL_INIT_STATE, "init_"+envName, s, TransitionType.REQUIRED);
    }
    env.setInitialState(SPECIAL_INIT_STATE);
  }
  
  private static CompactState cleanUpAlphabet(CompactState machine)
  {
    MTS<Long,String> m2 = AutomataToMTSConverter.getInstance().convert(machine);
    Vector<String> toRemove = new Vector<String>();
    toRemove.add("tau");
    toRemove.add("-1");
    for (String a : m2.getActions())
      if (a.startsWith("@")) //a.endsWith("?") || 
        toRemove.add(a);
    for (String a : toRemove)
      m2.removeAction(a);
    return MTSToAutomataConverter.getInstance().convert(m2, machine.name, false);
  }
  
  private static CompactState solveSafety(CompactState envModel, Collection<CompactState> safetyMachines, LTSOutput output)
  {
//    //System.out.println("ENV pre-SAFETY "+envModel.getAlphabetV());
    Vector<CompactState> machines = new Vector<CompactState>();
    machines.add(envModel);
    machines.addAll(safetyMachines);
    CompositeState parallel = new CompositeState("SAFE_ENVIRON", machines);
    TransitionSystemDispatcher.applyComposition(parallel, output);
//    //System.out.println("ENV post-SAFETY "+parallel.composition.getAlphabetV());
    return cleanUpAlphabet(parallel.composition);
  }
  
  public static CompactState addControllerExceptions(int tier, CompactState controller, List<String> controlledActions)
  {
    MTS<Long,String> controller2 = AutomataToMTSConverter.getInstance().convert(controller);
    controller2.removeAction("tau"); //what?
    controller2.removeAction("-1"); //what?
    
    controller2 = addControllerExceptions(tier, controller2, controlledActions); //was called with controller not controller2 -> inf loop, why never happened before?
    //had to change type of method below to MTS
    
    return MTSToAutomataConverter.getInstance().convert(controller2, controller.name, false);
  }
  
  public static MTS<Long,String> addControllerExceptions(int tier, MTS<Long,String> controller, List<String> controlledActions)
  {
    //prepare sets of actions
    Set<String> alphabet = controller.getActions();
    List<String> uncontrolledActions = new Vector<String>();
    uncontrolledActions.addAll(alphabet);
    uncontrolledActions.removeAll(controlledActions);

    //build exception states
    // (s) -- exception --> (-1) -- tier_disabled --> (-2) -- alphabet --> (-2)  
    controller.addState(SPECIAL_EXCEP_STATE1);
    controller.addState(SPECIAL_EXCEP_STATE2);
    for (String action : alphabet)
      controller.addTransition(SPECIAL_EXCEP_STATE2, action, SPECIAL_EXCEP_STATE2, TransitionType.REQUIRED);
    String disabledAction = "tier_disabled"+tier+"";
    controller.addAction(disabledAction);
    controller.addTransition(SPECIAL_EXCEP_STATE1, disabledAction, SPECIAL_EXCEP_STATE2, TransitionType.REQUIRED);
    
    //complete states with missing actions to exception state
    for (long state : controller.getStates())
    {
      if (state != SPECIAL_EXCEP_STATE1 && state != SPECIAL_EXCEP_STATE2)
      {
        List<String> missingUncontrolled = new Vector<String>();
        missingUncontrolled.addAll(uncontrolledActions);
        for (Pair<String,Long> transition : controller.getTransitions(state, TransitionType.REQUIRED))
          missingUncontrolled.remove(transition.getFirst());
        for (String uncon : missingUncontrolled)
          controller.addTransition(state, uncon, SPECIAL_EXCEP_STATE1, TransitionType.REQUIRED);
      }
    }
    return controller;
  }

  private static CompactState getControlStackEnvironment(CompositeState cStackDef, String name, LTSOutput output)
  {
    Object compactOrComposite = cStackDef.controlStackEnvironments.get(name);
    if (compactOrComposite instanceof CompositeState)
    {
      TransitionSystemDispatcher.applyComposition((CompositeState) compactOrComposite, output);
      return ((CompositeState) compactOrComposite).composition;
    }
    else
      return (CompactState) compactOrComposite;
  }
  
  public static void generatePrismMDP(CompactState lts, List<String> controlledActions)
  {
    CompactState completed = addControllerExceptions(1, lts, controlledActions);
    
    MTS<Long,String> lts2 = AutomataToMTSConverter.getInstance().convert(completed);
    //prepare sets of actions
    Set<String> alphabet = lts2.getActions();
    List<String> uncontrolledActions = new Vector<String>();
    uncontrolledActions.addAll(alphabet);
    uncontrolledActions.removeAll(controlledActions);
    
    try
    {
      PrintWriter writer = new PrintWriter(new FileWriter(lts.name+".nm"));
      
      final String stateVar = "state_"+lts.name.substring(0, 2); //try to make them unique when combined with other LTSs
      final String responseVar = "response_"+lts.name.substring(0, 2);
      
      writer.println("mdp //generated by MTSA");
      writer.println("module "+lts.name);
      writer.println(stateVar+": [0.."+lts2.getStates().size()+"] init 0;");
      writer.println(responseVar+": [0.."+uncontrolledActions.size()+"] init 0;");
      
      writer.println("\n//environment actions");
      for (int r = 0; r < uncontrolledActions.size(); r++)
        writer.println("["+prismifyLabel(uncontrolledActions.get(r))+"] "+responseVar+"="+(r+1)+" -> 1.0:("+responseVar+"'=0);");
      
      writer.println("\n//controlled actions");
      long exceptionState = lts2.getStates().size()-2;
      for (long state : lts2.getStates())
      {
        List<Pair<String,Long>> envTrans = new Vector<Pair<String,Long>>();
        for (Pair<String,Long> transition : lts2.getTransitions(state, TransitionType.REQUIRED))
        {
          if (uncontrolledActions.contains(transition.getFirst()))
          {
            envTrans.add(transition);
            //if (transition.getFirst().equalsIgnoreCase("tier_disabled1"))
            //  exceptionState = state;
          }
          else
            writer.println("["+prismifyLabel(transition.getFirst())+"] "+stateVar+"="+state+" & "+responseVar+"=0 -> 1.0:("+stateVar+"'="+transition.getSecond()+");");
        }
        if (envTrans.size() > 0) //split env actions to unlabelled choice
        {
          String envChoice = "[] "+stateVar+"="+state+" & "+responseVar+"=0 -> ";
          int excCount = 0;
          for (Pair<String,Long> transition : envTrans)
            if (transition.getSecond() == exceptionState)
              excCount++;
          double excProb = 0.1/excCount;
          if (excCount == envTrans.size()) //edge case
            excProb = 1.0/excCount;
          double normalProb = 0.9/(envTrans.size()-excCount);
          if (excCount == 0) //edge case
            normalProb = 1.0/envTrans.size();
          
          for (int i = 0; i < envTrans.size(); i++)
          {//split prob between exp and non-exc
            Pair<String,Long> transition = envTrans.get(i);
            envChoice += (transition.getSecond() == exceptionState ? excProb : normalProb)+":("+responseVar+"'="+(uncontrolledActions.indexOf(transition.getFirst())+1)+")&("+stateVar+"'="+transition.getSecond()+")";
            envChoice += (i < envTrans.size()-1 ? " + " : ";");
          }
          writer.println(envChoice);
        }
      }

      writer.println("endmodule");
      
      if (exceptionState != -1)
        writer.println("\nlabel \"exception\" = "+stateVar+"="+exceptionState+";");
      
      writer.close();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
  
  private static String prismifyLabel(String label)
  {
    return label.replaceAll("\\.", "_");
  }
}
