package ltsa.control.util;

import MTSSynthesis.ar.dc.uba.model.condition.Fluent;
import MTSSynthesis.controller.model.ControllerGoal;
import ltsa.control.ControllerGoalDefinition;
import ltsa.lts.Diagnostics;
import ltsa.lts.Symbol;
import ltsa.lts.chart.util.FormulaUtils;
import ltsa.lts.ltl.AssertDefinition;
import ltsa.lts.ltl.FormulaFactory;
import ltsa.lts.ltl.PredicateDefinition;

import java.util.HashSet;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class GoalDefToControllerGoal {
	private static final GoalDefToControllerGoal instance = new GoalDefToControllerGoal();
	public static GoalDefToControllerGoal getInstance() {return instance;}
	
	public ControllerGoal<String> buildControllerGoal(ControllerGoalDefinition goalDef) {
		ControllerGoal<String> result = new ControllerGoal<String>();
		result.setPermissive(goalDef.isPermissive());
		this.buildSubGoals(result, goalDef);
		result.setNonBlocking(goalDef.isNonBlocking());
		result.setExceptionHandling(goalDef.isExceptionHandling());
		this.buildControllableActionSet(result, goalDef);
		result.setLazyness(goalDef.getLazyness());
		result.setNonTransient(goalDef.isNonTransient());
        result.setReachability(goalDef.isReachability());
        if(goalDef.isTestLatency()){
            result.setTestLatency(goalDef.getMaxSchedulers(), goalDef.getMaxControllers());
        }
        buildHeuristicActionSets(result, goalDef);
		return result;
	}

	private void buildControllableActionSet(ControllerGoal<String> goal, ControllerGoalDefinition goalDef) {

		Vector<String> actions = goalDef.getControllableActionSet();
		Vector<String> comparison = goalDef.getComparisonActionSet();
		if (actions == null) {
			if (goalDef.getMarkingDefinitions().isEmpty())
				Diagnostics.fatal("Controllable actions set not defined.");
			else
			{actions = new Vector<>();
				comparison = new Vector<>();}
		}
		goal.addAllControllableActions(new HashSet<>(actions));
		List<String> comparisonList = new ArrayList<>(comparison);
		//System.out.println(comparisonList);
		goal.addAllComparisonActions(new ArrayList<>(comparisonList));
	}
	
	private void buildHeuristicActionSets(ControllerGoal<String> goal, ControllerGoalDefinition goalDef) {

		Vector<String> reachDefinitions = goalDef.getMarkingDefinitions();
		Set<String> reach = new HashSet<String>();
		goal.setMarking(reach);
		if (reachDefinitions != null)
			reach.addAll(reachDefinitions);

		Vector<String> disturbances = goalDef.getDisturbanceActions();
		if (disturbances != null){
			goal.setDisturbances(new HashSet<>(disturbances));
		}else{
			goal.setDisturbances(new HashSet<>());
		}
	}

	private void buildSubGoals(ControllerGoal<String> result, ControllerGoalDefinition goalDef) {

		Set<Fluent> fluentsInFaults = new HashSet<>();
		//Convert faults to Set<Formula> 
		for (Symbol faultDefinition : goalDef.getFaultsDefinitions()) {
			AssertDefinition def = AssertDefinition.getDefinition(faultDefinition.getName());
			if (def!=null){
				result.addFault(FormulaUtils.adaptFormulaAndCreateFluents(def.getFormula(true), fluentsInFaults));
			} else {
				Diagnostics.fatal("Assertion not defined [" + faultDefinition.getName() + "].");
			}
		}
		Set<Fluent> involvedFluents = new HashSet<>(fluentsInFaults);
		result.addAllFluentsInFaults(fluentsInFaults);

		//Convert assumptions to Set<Formula> 
		for (ltsa.lts.Symbol assumeDefinition : goalDef.getAssumeDefinitions()) {
			AssertDefinition def = AssertDefinition.getDefinition(assumeDefinition.getName());
			if (def!=null){
				result.addAssume(FormulaUtils.adaptFormulaAndCreateFluents(def.getFormula(true), involvedFluents));
			} else {
				Diagnostics.fatal("Assertion not defined [" + assumeDefinition.getName() + "].");
			}
		}

		//Convert buchi to Set<Formula>
		for (ltsa.lts.Symbol buchiDefinition : goalDef.getBuchiDefinitions()) {
			AssertDefinition def = AssertDefinition.getDefinition(buchiDefinition.getName());
			if (def!=null){
				result.addBuchi(FormulaUtils.adaptFormulaAndCreateFluents(def.getFormula(true), involvedFluents));
			} else {
				Diagnostics.fatal("Assertion not defined [" + buchiDefinition.getName() + "].");
			}
		}

		//Convert guarantees to Set<Formula> 
		for (ltsa.lts.Symbol guaranteeDefinition : goalDef.getGuaranteeDefinitions()) {
			AssertDefinition def = AssertDefinition.getDefinition(guaranteeDefinition.getName());
			if (def!=null){
				result.addGuarantee(FormulaUtils.adaptFormulaAndCreateFluents(def.getFormula(true), involvedFluents));
			} else {
			  PredicateDefinition fdef = PredicateDefinition.get(guaranteeDefinition.getName());
			  if (fdef != null)
			    result.addGuarantee(FormulaUtils.adaptFormulaAndCreateFluents(new FormulaFactory().make
					    (guaranteeDefinition), involvedFluents));
			  else
				  //Diagnostics.fatal("Assertion not defined [" + guaranteeDefinition.getName() + "].");
			    Diagnostics.fatal("Fluent/assertion not defined [" + guaranteeDefinition.getName() + "].");
			}
		}
		
		Set<Fluent> concurrencyFluents = new HashSet<Fluent>();
		//Convert faults to Set<Formula> 
		for (ltsa.lts.Symbol concurrencyDefinition : goalDef.getConcurrencyDefinitions()) {
			AssertDefinition def = AssertDefinition.getDefinition(concurrencyDefinition.getName());
			if (def!=null){
				FormulaUtils.adaptFormulaAndCreateFluents(def.getFormula(true), concurrencyFluents);
			} else {
				Diagnostics.fatal("Assertion not defined [" + concurrencyDefinition.getName() + "].");
			}
		}
		result.addAllConcurrencyFluents(concurrencyFluents);
		involvedFluents.addAll(concurrencyFluents);
		

		Set<Fluent> activityFluents = new HashSet<Fluent>();
		//Convert faults to Set<Formula> 
		for (ltsa.lts.Symbol activityDefinition : goalDef.getActivityDefinitions()) {
			AssertDefinition def = AssertDefinition.getDefinition(activityDefinition.getName());
			if (def!=null){
				FormulaUtils.adaptFormulaAndCreateFluents(def.getFormula(true), activityFluents);
			} else {
				Diagnostics.fatal("Assertion not defined [" + activityDefinition.getName() + "].");
			}
		}
		result.addAllActivityFluents(activityFluents);
		involvedFluents.addAll(activityFluents);
		
		
		
		result.addAllFluents(involvedFluents);
		
		
		
	}
}