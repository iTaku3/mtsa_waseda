package ltsa.ac.ic.doc.mtstools.util.fsp;


import MTSTools.ac.ic.doc.mtstools.model.impl.MDP;
import ltsa.lts.CompactState;
import ltsa.lts.EventState;
import ltsa.lts.ProbabilisticEventState;
import ltsa.ui.HPWindow;

import java.util.*;

/**
Very closely based on AutomataToMTSConverter
Daniel Sykes 2014
**/

public class AutomataToMDPConverter
{
  private static AutomataToMDPConverter instance;
  
  private String[] indexToAction;
  private MDP mdp;
  private ModelConverterUtils modelConverterUtils;

  private AutomataToMDPConverter() {
    modelConverterUtils = new ModelConverterUtils();
  }

  public static AutomataToMDPConverter getInstance() {
    if (instance == null) {
      instance = new AutomataToMDPConverter();
    }
    return instance;
  }


  public MDP convert(CompactState automata) {
    this.mdp = new MDP(modelConverterUtils.rank(automata.START()));
    
    indexToAction = new String[automata.getTransitionsLabels().length];
    
    this.addActions(automata);
    this.addTransitions(automata);
    
    //printEverything(automata);
    
    return mdp;
  }
  

  private void addActions(CompactState automata) {
    String[] alphabet = automata.getTransitionsLabels();
    Map<String,Integer> reverseMap = new HashMap<String,Integer>();

    for(int i = 0; i<alphabet.length; i++) {
      String action = alphabet[i];
    
      if (reverseMap.containsKey(action)) {
        indexToAction[i] = indexToAction[reverseMap.get(action)];
      } else {
        mdp.addAction(action);
        reverseMap.put(action,i);
        indexToAction[i] = action;
      }
    }
  }

  /*private void printEverything(CompactState automaton)
  {
    for (int i = 0; i < automaton.states.length; i++)
      if (automaton.states[i] != null)
        printEverything(i, automaton.states[i]);
  }
  
  private void printEverything(int s, EventState e)
  {
//    System.out.println("*"+s+"--"+indexToAction[e.getEvent()]+"-->"+e.getNext());
    for (Object o : Collections.list(e.elements()))
      if (o != e)
        printEverything(s, (EventState) o);
    if (EventState.hasNonDet(e))
      for (Object o2 : Collections.list(e.elements()))
        if (o2 != e)
          printEverything(s, (EventState) o2);
    if (e instanceof ProbabilisticEventState)
    {
      ProbabilisticEventState pe = (ProbabilisticEventState) e;
      if (pe.getBundleTransitions() != null)
        for (Object o2 : Collections.list(pe.getBundleTransitions().elements()))
          if (o2 != e)
            printEverything(s, (EventState) o2);
    }
    
  }*/

  private void addTransitions(CompactState automaton)
  {
    Queue<Long> stateQueue = new LinkedList<Long>();
    
    stateQueue.add(mdp.getInitialState());
    while(!stateQueue.isEmpty()) {
      long actualState = stateQueue.remove();
//      System.out.println("Processing "+actualState);
      if (automaton.isAccepting((int) actualState)) {
        mdp.addStateLabel(actualState, "accepting");
      }
      if (automaton.states[(int) actualState] != null)
      for (Object o : Collections.list(automaton.states[(int) actualState].elements()))
      {
        EventState ev = (EventState) o;
        long next = ev.getNext();
        String action = indexToAction[ev.getEvent()];
        if (!mdp.getStates().contains(next))
        {

          mdp.addState(next);
          stateQueue.add(next);
        }
        if (ev instanceof ProbabilisticEventState)
        {
          ProbabilisticEventState pev = (ProbabilisticEventState) ev;
          if (pev.getBundleTransitions() != null)
            for (Object o2 : Collections.list(pev.getBundleTransitions().elements())) //this ignores anything chained in .probTr
            {
              if (o2 != o)
              {
                ProbabilisticEventState pev2 = (ProbabilisticEventState) o2;
                long next2 = pev2.getNext();
                String action2 = indexToAction[pev2.getEvent()];
                if (!mdp.getStates().contains(next2))
                {
                  mdp.addState(next2);
                  stateQueue.add(next2);
                }
//                System.out.println("adding "+actualState+" --"+action2+","+pev2.getProbability()+","+pev2.getBundle()+"--> "+next2);
                mdp.addTransition(actualState, action2, next2, pev2.getProbability().doubleValue(), pev2.getBundle());

                while (pev2.getBundleTransitions() != null) //catch the ones chained in .probTr that are ignored in the outer loop
                {
                  pev2 = (ProbabilisticEventState) pev2.getBundleTransitions();
                  next2 = pev2.getNext();
                  action2 = indexToAction[pev2.getEvent()];
                  if (!mdp.getStates().contains(next2))
                  {
                    mdp.addState(next2);
                    stateQueue.add(next2);
                  }
//                  System.out.println("adding "+actualState+" --"+action2+","+pev2.getProbability()+","+pev2.getBundle()+"--> "+next2);
                  mdp.addTransition(actualState, action2, next2, pev2.getProbability().doubleValue(), pev2.getBundle());
                }
              }
            }
//          System.out.println("adding "+actualState+" --"+action+","+pev.getProbability()+","+pev.getBundle()+"--> "+next);
          mdp.addTransition(actualState, action, next, pev.getProbability().doubleValue(), pev.getBundle());
        }
        else
        {
//          System.out.println("adding "+actualState+" --"+action+"--> "+next);
          mdp.addTransition(actualState, action, next);
        }

        if (EventState.hasNonDet(ev))
        {
//          System.out.println(indexToAction[ev.getEvent()]+"-->"+ev.getNext()+" has nondet (mdpconverter)");
          for (Object o2 : Collections.list(ev.elements()))
          {
            if (o2 != o)
            {
              EventState ev2 = (EventState) o2;
              long next2 = ev2.getNext();
              if (!mdp.getStates().contains(next2))
              {
                mdp.addState(next2);
                stateQueue.add(next2);
              }
//              System.out.println("adding "+actualState+" --"+indexToAction[ev2.getEvent()]+"--> "+next2);
              mdp.addTransition(actualState, indexToAction[ev2.getEvent()], next2); //but if it's nondet+prob?
            }
          }
        }
        else
          HPWindow.instance.ltsOutput.outln(indexToAction[ev.getEvent()]+"-->"+ev.getNext()+" has NO nondet (mdpconverter)");
      }
    }
  }
}
