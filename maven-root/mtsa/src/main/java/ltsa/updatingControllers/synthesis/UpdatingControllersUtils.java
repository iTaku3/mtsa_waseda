package ltsa.updatingControllers.synthesis;

import MTSSynthesis.ar.dc.uba.model.language.*;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.impl.MTSImpl;
import MTSTools.ac.ic.doc.mtstools.utils.GraphUtils;
import MTSSynthesis.ar.dc.uba.model.condition.Fluent;
import MTSSynthesis.ar.dc.uba.model.condition.FluentImpl;
import MTSSynthesis.ar.dc.uba.model.condition.FluentPropositionalVariable;
import ltsa.ac.ic.doc.mtstools.util.fsp.AutomataToMTSConverter;
import ltsa.ac.ic.doc.mtstools.util.fsp.MTSToAutomataConverter;
import ltsa.control.ControllerGoalDefinition;
import MTSSynthesis.controller.model.ControllerGoal;
import ltsa.lts.*;
import MTSTools.ac.ic.doc.mtstools.model.impl.MarkedMTS;
import ltsa.lts.Symbol;
import ltsa.lts.chart.util.FormulaUtils;
import ltsa.lts.ltl.AssertDefinition;
import ltsa.updatingControllers.UpdateConstants;

import java.util.*;

import static ltsa.updatingControllers.UpdateConstants.*;

public class UpdatingControllersUtils {

	public static final Set<Fluent> UPDATE_FLUENTS = new HashSet<Fluent>();
	public static final Fluent beginFluent;
	public static final Fluent stopFluent;
	public static final Fluent reconFluent;
	public static final Fluent startFluent;
	public static final Set<Fluent> ACTION_FLUENTS_FOR_UPDATE = new HashSet<Fluent>();

	static {
		HashSet<MTSSynthesis.ar.dc.uba.model.language.Symbol> beginAction = new HashSet<MTSSynthesis.ar.dc.uba.model.language.Symbol>();
		HashSet<MTSSynthesis.ar.dc.uba.model.language.Symbol> stopAction = new HashSet<MTSSynthesis.ar.dc.uba.model.language.Symbol>();
		HashSet<MTSSynthesis.ar.dc.uba.model.language.Symbol> reconfigureAction = new HashSet<MTSSynthesis.ar.dc.uba.model.language.Symbol>();
		HashSet<MTSSynthesis.ar.dc.uba.model.language.Symbol> startAction = new HashSet<MTSSynthesis.ar.dc.uba.model.language.Symbol>();
		beginAction.add(new SingleSymbol(UpdateConstants.BEGIN_UPDATE));
		stopAction.add(new SingleSymbol(UpdateConstants.STOP_OLD_SPEC));
		reconfigureAction.add(new SingleSymbol(UpdateConstants.RECONFIGURE));
		startAction.add(new SingleSymbol(UpdateConstants.START_NEW_SPEC));

		beginFluent = new FluentImpl("BeginUpdate", beginAction, new HashSet<MTSSynthesis.ar.dc.uba.model.language.Symbol>(), false);
		stopFluent = new FluentImpl("StopOldSpec", stopAction, new HashSet<MTSSynthesis.ar.dc.uba.model.language.Symbol>(), false);
		reconFluent = new FluentImpl("Reconfigure", reconfigureAction, new HashSet<MTSSynthesis.ar.dc.uba.model.language.Symbol>(), false);
		startFluent = new FluentImpl("StartNewSpec", startAction, new HashSet<MTSSynthesis.ar.dc.uba.model.language.Symbol>(), false);
		
		UpdatingControllersUtils.UPDATE_FLUENTS.add(beginFluent);
		UpdatingControllersUtils.UPDATE_FLUENTS.add(stopFluent);
		UpdatingControllersUtils.UPDATE_FLUENTS.add(reconFluent);
		UpdatingControllersUtils.UPDATE_FLUENTS.add(startFluent);
	}


	public static Set<Fluent> compileFluents(List<Symbol> updPropertyDef) {
		Set<Fluent> compiledFluents = new HashSet<Fluent>();
		for (Symbol toCompileProperty : updPropertyDef) {

			Set<Fluent> involvedFluents = new HashSet<Fluent>();

			LTSCompiler.makeFluents(toCompileProperty, involvedFluents);

			if (involvedFluents.size() == 1) {
				compiledFluents.add(involvedFluents.iterator().next());
			} else {
//				System.out.println("TWO OR MORE FLUENT NOT EXPECTED");
			}
		}
		return compiledFluents;
	}

	public static ControllerGoal<String> generateGRUpdateGoal(UpdatingControllersDefinition updContDef,
															  ControllerGoalDefinition oldGoalDef,
															  ControllerGoalDefinition newGoalDef, Set<String>
																	controllableSet) {
		ControllerGoal<String> grcg = new ControllerGoal<String>();

		grcg.setNonBlocking(updContDef.isNonblocking());
		grcg.addAllControllableActions(controllableSet);
		Set<Fluent> involvedFluents = new HashSet<Fluent>();

		addFluentAndAssumption(grcg, involvedFluents, BEGIN_UPDATE);
		addFluentAndGuarantee(grcg, involvedFluents, STOP_OLD_SPEC);
		addFluentAndGuarantee(grcg, involvedFluents, START_NEW_SPEC);
		addFluentAndGuarantee(grcg, involvedFluents, RECONFIGURE);
		addFailures(grcg, involvedFluents, oldGoalDef);
		addFailures(grcg, involvedFluents, newGoalDef);
		grcg.addAllFluents(involvedFluents);
		return grcg;
	}

	public static ControllerGoalDefinition generateSafetyGoalDef(UpdatingControllersDefinition updContDef,
																 ControllerGoalDefinition oldGoalDef,
																 ControllerGoalDefinition newGoalDef,
																 Set<String> controllableSetSymbol,
																 LTSOutput output) {
		ControllerGoalDefinition cgd = new ControllerGoalDefinition(updContDef.getName());
		cgd.addAssumeDefinition(new Symbol(123, "BeginUpdate")); //is this useless?we use the assumption in GR not here
		cgd.addGuaranteeDefinition(new Symbol(123, "StopOldSpec")); //is this useless? we use Guarantee in GR not here
		cgd.addGuaranteeDefinition(new Symbol(123, "StartNewSpec")); //besides the symbol redirects to nothing.
		cgd.addGuaranteeDefinition(new Symbol(123, "Reconfigure"));
		addFailures(cgd, oldGoalDef); //same here, failures are for liveness right?
		addFailures(cgd, newGoalDef);

		cgd.setControllableActionSet(new Vector<String>(controllableSetSymbol));
		cgd.setNonBlocking(updContDef.isNonblocking());

		generateUpdateGoals(oldGoalDef, newGoalDef, updContDef.getTransitionGoals(), cgd);

		// adding ControllerGoalDefinition
		ControllerGoalDefinition.addDefinition(cgd.getNameString(), cgd);
		AssertDefinition.compileAll(output); // this is for filling the fac attribute in constraints added before
		return cgd;
	}

	/**
	 * @param action
	 * @return whether action is not one of the controller update special actions.
	 */
	public static boolean isNotUpdateAction(String action) {
		return !START_NEW_SPEC.equals(action) && !STOP_OLD_SPEC.equals(action) &&
			!RECONFIGURE.equals(action);
	}

	/**
	 * @param cgd
	 * @param controllerGoalDef
	 */
	private static void addFailures(ControllerGoalDefinition cgd, ControllerGoalDefinition controllerGoalDef) {
		//Check with dipi. we are not sure if this will work as expected
		List<Symbol> faultsDefinitions = controllerGoalDef.getFaultsDefinitions();
		for (Symbol faultsDefinition : faultsDefinitions) {
			cgd.addFaultDefinition(faultsDefinition);
		}
	}

	/**
	 * @param grcg
	 * @param involvedFluents
	 * @param controllerGoalDefinition
	 */
	private static void addFailures(ControllerGoal<String> grcg, Set<Fluent> involvedFluents,
									ControllerGoalDefinition controllerGoalDefinition) {
		// TODO: refactor, code copied from GoalDefToControllerGoal.
		// Check with dipi. we are not sure if this will work as expected
		Set<Fluent> fluentsInFaults = new HashSet<Fluent>();
		for (ltsa.lts.Symbol faultsDefinition : controllerGoalDefinition.getFaultsDefinitions()) {
			AssertDefinition def = AssertDefinition.getDefinition(faultsDefinition.getName());
			if (def != null) {
				grcg.addFault(FormulaUtils.adaptFormulaAndCreateFluents(def.getFormula(true), fluentsInFaults));
			} else {
				Diagnostics.fatal("Assertion not defined [" + faultsDefinition.getName() + "].");
			}
		}
		involvedFluents.addAll(fluentsInFaults);
		grcg.addAllFluentsInFaults(fluentsInFaults);
	}

	/**
	 * @param grcg
	 * @param fluents
	 * @param action
	 */
	private static void addFluentAndAssumption(ControllerGoal<String> grcg, Set<Fluent> fluents, String action) {
		FluentPropositionalVariable fluentPropositionalVariable = generateAndAddFluent(fluents, action);
		grcg.addAssume(fluentPropositionalVariable);
	}

	/**
	 * @param grcg
	 * @param fluents
	 * @param action
	 */
	private static void addFluentAndGuarantee(ControllerGoal<String> grcg, Set<Fluent> fluents, String action) {
		FluentPropositionalVariable fluentPropositionalVariable = generateAndAddFluent(fluents, action);
		grcg.addGuarantee(fluentPropositionalVariable);
	}

	/**
	 * @param fluents
	 * @param action
	 * @return
	 */
	private static FluentPropositionalVariable generateAndAddFluent(Set<Fluent> fluents, String action) {
		Fluent turnOnFluent = createOnlyTurnOnFluent(action);
		fluents.add(turnOnFluent);
		FluentPropositionalVariable fluentPropositionalVariable = new FluentPropositionalVariable(turnOnFluent);
		return fluentPropositionalVariable;
	}

	/**
	 * Changes the safety goals of each controller to (!StopOldSpec -> OLD) and (StartNewSpec -> NEW). Adds T too.
	 *
	 * @param oldGoalDef
	 * @param newGoalDef
	 * @param transitionGoalDef
	 * @param cgd
	 */
	private static void generateUpdateGoals(ControllerGoalDefinition oldGoalDef, ControllerGoalDefinition newGoalDef,
											List<Symbol> transitionGoalDef, ControllerGoalDefinition cgd) {
		
		for (Symbol formula : oldGoalDef.getSafetyDefinitions()) {
			UpdatingControllersGoalsMaker.addOldGoals(formula, cgd);
		}
		for (Symbol formula : newGoalDef.getSafetyDefinitions()) {
			UpdatingControllersGoalsMaker.addImplyUpdatingGoal(formula, cgd);
		}
		for (Symbol transitionSymbol : transitionGoalDef) {
			cgd.addSafetyDefinition(transitionSymbol);
		}
	}

	/**
	 * Marks the states of the update controller (Cu) that belong to the terminal set.
	 *
	 * @param updateController
	 * @return a minimized CompactState of updateController.
	 */
	public static MarkedMTS<Long, String> markCuTerminalSet(MTS<Long, String> updateController) {
		Set<Set<Long>> stronglyConnectedComponents = GraphUtils.getStronglyConnectedComponents(updateController);
		Set<Set<Long>> terminalSets = GraphUtils.getTerminalSets(stronglyConnectedComponents, updateController);
		//		MarkedCompactState markedCu = new MarkedCompactState(updateController, terminalSet, name);
		//		CompactState minimised = TransitionSystemDispatcher.minimise(markedCu, output);
		return new MarkedMTS<Long, String>(updateController, updateController.getInitialState(), terminalSets);
	}

    private static Fluent createOnlyTurnOnFluent(String initAction) {
        HashSet<MTSSynthesis.ar.dc.uba.model.language.Symbol> initiatingActions = new HashSet<MTSSynthesis.ar.dc.uba.model.language.Symbol>();
        initiatingActions.add(new SingleSymbol(initAction));

        Fluent onlyTurnOnFluent = new FluentImpl(new String(initAction), initiatingActions, new HashSet<MTSSynthesis.ar.dc.uba.model.language.Symbol>(), false);
        return onlyTurnOnFluent;
    }

	/////////////////////////////THIS CODE IS FOR RELABELING ACTIONS ///////////////////////////////
	public static void removeOldTransitions(CompositeState cs) {

		MTS<Long, String> mts = AutomataToMTSConverter.getInstance().convert(cs.composition);
		MTS<Long, String> resultMts = new MTSImpl<Long, String>(mts.getInitialState());
		for (String action : mts.getActions()) {
			if (!isOld(action)){
				resultMts.addAction(action);
			}
		}

		for (Long state : mts.getStates()) {

			resultMts.addState(state);

			for (Pair<String, Long> action_toState : mts.getTransitions(state, MTS.TransitionType.REQUIRED)) {

				if (!isOld(action_toState.getFirst())){
					resultMts.addState(action_toState.getSecond());
					resultMts.addRequired(state, action_toState.getFirst(), action_toState.getSecond());

				} else {
					resultMts.addState(action_toState.getSecond());
					resultMts.addRequired(state, withoutOld(action_toState.getFirst()), action_toState.getSecond());
				}
			}
		}
		cs.composition = MTSToAutomataConverter.getInstance().convert(resultMts, cs.composition.getName(), false);

	}

	public static String withoutOld(String action) {

		return action.substring(0, action.length() - UpdateConstants.OLD_LABEL.length());
	}

	public static boolean isOld(String action) {

		return action.contains(UpdateConstants.OLD_LABEL);
	}

}