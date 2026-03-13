package ltsa.updatingControllers.synthesis;

import MTSSynthesis.ar.dc.uba.model.condition.Fluent;
import MTSSynthesis.ar.dc.uba.model.condition.FluentUtils;
import MTSSynthesis.ar.dc.uba.model.condition.Formula;
import MTSSynthesis.ar.dc.uba.model.language.SingleSymbol;
import MTSSynthesis.controller.util.FluentStateValuation;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.impl.MTSImpl;
import ltsa.ac.ic.doc.mtstools.util.fsp.AutomataToMTSConverter;
import ltsa.ac.ic.doc.mtstools.util.fsp.MTSToAutomataConverter;
import ltsa.lts.CompactState;
import ltsa.lts.CompositeState;
import ltsa.ui.StandardOutput;
import ltsa.updatingControllers.UpdateConstants;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by lnahabedian on 06/07/16.
 */
public class UpdatingControllerSafetySynthesizer {


    public static MTS<Long, String> synthesizeSafety(MTS<Long, String> metaEnvironment, Set<Fluent> goalFluents, List<Formula> safetyFormulas, Set<String> controllableActions) {

        makeOldActionsUncontrollable(controllableActions, metaEnvironment);

        FluentStateValuation<Long> fluentStateValuation = buildValuations(metaEnvironment, goalFluents);

        MTS<Long, String> safetyEnv = valuateSafety(safetyFormulas, metaEnvironment, fluentStateValuation);

		return getDontDoTwiceGoals(safetyEnv);

    }

    private static FluentStateValuation<Long> buildValuations(MTS<Long, String> metaEnv, Set<Fluent> fluents) {

        FluentStateValuation<Long> fsv = new FluentStateValuation<Long>(metaEnv.getStates());

        // BFS
        Queue<Long> toVisit = new LinkedList<Long>();
        Long firstState = new Long(metaEnv.getInitialState());
        toVisit.add(firstState);
        ArrayList<Long> discovered = new ArrayList<Long>();

        // add initially true fluents to the initial state
        for (Fluent fl : fluents){

            if (fl.isInitialValue()){
                fsv.addHoldingFluent(firstState,fl);
            }
        }

        while (!toVisit.isEmpty()) {
            Long actualInMetaEnv = toVisit.remove();
            if (!discovered.contains(actualInMetaEnv)) {
                discovered.add(actualInMetaEnv);

                for (Pair<String, Long> action_toStateInMetaEnv : metaEnv.getTransitions(actualInMetaEnv,MTS.TransitionType.REQUIRED)) {

                    String action = action_toStateInMetaEnv.getFirst();
                    Long toState = action_toStateInMetaEnv.getSecond();

                    if (UpdatingControllersUtils.isOld(action)){
                        action = UpdatingControllersUtils.withoutOld(action);
                    }

                    // put same fluents from last state if not terminating
                    for(Fluent fl : fsv.getFluentsFromState(actualInMetaEnv)){

                        if (!fl.getTerminatingActions().contains(new SingleSymbol(action))){
                            fsv.addHoldingFluent(toState,fl);
                        }
                    }

                    // Check If a new fluent turns on
                    for (Fluent fl : fluents) {

                        if (fl.getInitiatingActions().contains(new SingleSymbol(action))) {
                            fsv.addHoldingFluent(toState, fl);
                        }
                    }

                    toVisit.add(toState);
                }
            }
        }

        return fsv;
    }

    private static MTS<Long, String> valuateSafety(List<Formula> safetyFormulas , MTS<Long, String> metaEnvironment, FluentStateValuation<Long> fluentStateValuation) {

        HashSet<Long> toBuild = new HashSet<Long>();
        formulaToStateSet(toBuild, metaEnvironment.getStates(), safetyFormulas,	fluentStateValuation);

        MTS<Long, String> safetyEnv = applySafetyInEnvironment(metaEnvironment, toBuild);
        return safetyEnv;
    }

    private static void formulaToStateSet(Set<Long> toBuild, Set<Long> allStates, List<Formula> formulas, FluentStateValuation<Long> valuation) {

        for (Formula formula : formulas) {
            for (Long state : allStates) {
                formulaToStateSet(toBuild, formula, state, valuation);
            }
            if (toBuild.isEmpty()) {
                Logger.getAnonymousLogger().log(Level.WARNING, "No state satisfies formula: " + formula);
            }
        }
    }

    private static void formulaToStateSet(Set<Long> toBuild, Formula formula, Long state, FluentStateValuation<Long> valuation) {

        valuation.setActualState(state);
        if (formula.evaluate(valuation)) {
            toBuild.add(state);
        }
    }

    private static MTS<Long, String> applySafetyInEnvironment(MTS<Long, String> metaEnvironment, HashSet<Long> toBuild) {

        MTS<Long, String> result = new MTSImpl<Long, String>(metaEnvironment.getInitialState());

        for (Long state : metaEnvironment.getStates()) {
            result.addState(state);
            if (!toBuild.contains(state)){
                for (Pair<String, Long> transition : metaEnvironment.getTransitions(state, MTS.TransitionType.REQUIRED)) {

                    result.addState(transition.getSecond());
                    result.addAction(transition.getFirst());

                    result.addRequired(state, transition.getFirst(), transition.getSecond());
                }
            }

        }
        result.removeUnreachableStates();
        return result;

    }

    private static void makeOldActionsUncontrollable(Set<String> controllableActions, MTS<Long, String> env) {

        Set<Fluent> fluentSet = new HashSet<Fluent>();
        fluentSet.add(UpdatingControllersUtils.beginFluent);

        FluentStateValuation<Long> beginUpdateValuation = FluentUtils.getInstance().buildValuation(env, fluentSet);

        for (Long state : env.getStates()) {
            if (! beginUpdateValuation.isTrue(state, UpdatingControllersUtils.beginFluent)) {

                List<Pair<String, Long>> toBeChanged = new ArrayList<Pair<String, Long>>();
                for (Pair<String, Long> action_toState : env.getTransitions(state, MTS.TransitionType.REQUIRED)) {
                    if (controllableActions.contains(action_toState.getFirst())) {
                        toBeChanged.add(action_toState);
                    }
                }
                for (Pair<String, Long> action_toState : toBeChanged) {
                    String action = action_toState.getFirst();
                    Long toState = action_toState.getSecond();
                    env.removeRequired(state, action, toState);
                    String actionWithOld = action + UpdateConstants.OLD_LABEL;
                    env.addAction(actionWithOld);
                    env.addRequired(state, actionWithOld, toState);
                }
            }
        }
        // add all .old accions to MTS so as to avoid problems while parallel composition
        // I think is useless but I want to keep the structure consistent
        for (String action : controllableActions) {
            if (UpdatingControllersUtils.isNotUpdateAction(action)) {
                env.addAction(action + UpdateConstants.OLD_LABEL);
            }
        }
    }

    public static MTS<Long, String> getDontDoTwiceGoals(MTS<Long, String> SafetyEnv) {

        Vector<CompactState> machinesToCompose = new Vector<CompactState>();
        machinesToCompose.add(MTSToAutomataConverter.getInstance().convert(SafetyEnv, "safetyEnv"));

        // add machines from models that specify that special events cant be done twice
        machinesToCompose.add(dontDoTwiceModel("stopOldSpec", SafetyEnv.getActions()));
        machinesToCompose.add(dontDoTwiceModel("startNewSpec", SafetyEnv.getActions()));

        CompositeState c = new CompositeState(machinesToCompose);
        c.compose(new StandardOutput());
        return AutomataToMTSConverter.getInstance().convert(c.composition);
    }

    private static CompactState dontDoTwiceModel(String dontDoTwiceAction, Set<String> alphabet) {

        // states
        Long initialState = new Long(0);
        Long secondState = new Long(1);
        Long errorState = new Long(-1);

        // add states to model
        MTS<Long, String> model = new MTSImpl<Long, String>(initialState);
        model.addState(secondState);
        model.addState(errorState);

        // when I do the action twice I must go to ERROR
        model.addAction(dontDoTwiceAction);
        model.addRequired(initialState,dontDoTwiceAction,secondState);
        model.addRequired(secondState,dontDoTwiceAction,errorState);

        // In state 0 and 1 we can signaled any action
        for(String action : alphabet){

            if (! action.equals(dontDoTwiceAction)){
                model.addAction(action);
                model.addRequired(initialState,action,initialState);
                model.addRequired(secondState,action,secondState);
            }
        }
        return MTSToAutomataConverter.getInstance().convert(model,"dontDo"+dontDoTwiceAction.toUpperCase());
    }

}
