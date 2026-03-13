package ltsa.updatingControllers.synthesis;

import MTSSynthesis.ar.dc.uba.model.condition.FluentUtils;
import MTSSynthesis.ar.dc.uba.model.condition.Formula;
import MTSSynthesis.controller.gr.GRGameSolver;
import MTSSynthesis.controller.gr.GRRankSystem;
import MTSSynthesis.controller.gr.StrategyState;
import MTSSynthesis.controller.gr.knowledge.KnowledgeGRGame;
import MTSSynthesis.controller.gr.knowledge.KnowledgeGRGameSolver;
import MTSSynthesis.controller.gr.perfect.PerfectInfoGRGameSolver;
import MTSSynthesis.controller.model.*;
import MTSSynthesis.controller.util.FluentStateValuation;
import MTSSynthesis.controller.util.GRGameBuilder;
import MTSSynthesis.controller.util.GameStrategyToMTSBuilder;
import MTSSynthesis.controller.util.SubsetConstructionBuilder;
import MTSSynthesis.controller.model.gr.GRGame;
import MTSSynthesis.controller.model.gr.GRGoal;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.impl.LTSAdapter;
import MTSTools.ac.ic.doc.mtstools.model.impl.MTSAdapter;
import MTSTools.ac.ic.doc.mtstools.utils.GenericMTSToLongStringMTSConverter;
import ltsa.ac.ic.doc.mtstools.util.fsp.MTSToAutomataConverter;
import ltsa.lts.CompactState;
import ltsa.lts.LTSOutput;
import ltsa.updatingControllers.structures.UpdatingControllerCompositeState;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Created by lnahabedian on 06/07/16.
 */
public class UpdatingControllerGRSynthesizer {

    public static void synthesizeGR(CompactState compactSafetyEnv, UpdatingControllerCompositeState uccs, MTS<Long, String> safetyEnv, LTSOutput output) {

        output.outln("Synthezising GR");
        if (compactSafetyEnv.isNonDeterministic()){
            output.outln("Environment after safety is non-deterministic");
            output.outln("Solving a non-deterministic controller synthesis");
            nonBlockingGR(uccs, output, safetyEnv);
        } else {
            output.outln("Environment after safety is deterministic");
            output.outln("Solving a deterministic controller synthesis");
            synthesizeGRDeterministic(uccs, output, safetyEnv);
        }

    }

    private static void nonBlockingGR(UpdatingControllerCompositeState uccs, LTSOutput output, MTS<Long, String> safetyEnv) {

        KnowledgeGRGame<Long, String> game;
        GRGoal<Set<Long>> grGoal;
        MTS<Set<Long>, String> perfectInfoGame;
        SubsetConstructionBuilder<Long, String> subsetConstructionBuilder;

        FluentUtils fluentUtils = FluentUtils.getInstance();

        subsetConstructionBuilder = new SubsetConstructionBuilder<Long, String>(safetyEnv);

        perfectInfoGame = subsetConstructionBuilder.build();

        FluentStateValuation<Set<Long>> valuation = fluentUtils.buildValuation(perfectInfoGame, uccs.getUpdateGRGoal().getFluents());
        Assumptions<Set<Long>> assumptions = formulasToAssumptions(perfectInfoGame.getStates(), uccs.getUpdateGRGoal().getAssumptions(), valuation);
        Guarantees<Set<Long>> guarantees = formulasToGuarantees(perfectInfoGame.getStates(), uccs.getUpdateGRGoal().getGuarantees(), valuation);
        Set<Set<Long>> faults = new HashSet<Set<Long>>();

        grGoal = new GRGoal<Set<Long>>(guarantees, assumptions, faults, uccs.getUpdateGRGoal().isPermissive());
        Set<Set<Long>> initialStates = new HashSet<Set<Long>>();
        Set<Long> initialState = new HashSet<Long>();
        initialState.add(safetyEnv.getInitialState());
        initialStates.add(initialState);

        game = new KnowledgeGRGame<Long, String>(initialStates, safetyEnv, perfectInfoGame, uccs.getUpdateGRGoal().getControllableActions(), grGoal);

        GRRankSystem<Set<Long>> system = new GRRankSystem<Set<Long>>(game.getStates(), grGoal.getGuarantees(), grGoal.getAssumptions(), grGoal.getFailures());

        KnowledgeGRGameSolver<Long, String> solver = new KnowledgeGRGameSolver<Long, String>(game, system);
        solver.solveGame();

        if (solver.isWinning(perfectInfoGame.getInitialState())) {
            Strategy<Set<Long>, Integer> strategy = solver.buildStrategy();

            Set<Pair<StrategyState<Set<Long>, Integer>, StrategyState<Set<Long>, Integer>>> worseRank = solver.getWorseRank();
            MTS<StrategyState<Set<Long>, Integer>, String> result = GameStrategyToMTSBuilder.getInstance().buildMTSFrom(perfectInfoGame, strategy, worseRank);

            result.removeUnreachableStates();
            LTSAdapter<StrategyState<Set<Long>, Integer>, String> ltsAdapter = new LTSAdapter<StrategyState<Set<Long>,Integer>, String>(result, MTS.TransitionType.POSSIBLE);
            MTS<StrategyState<Set<Long>, Integer>, String> synthesised  = new MTSAdapter<StrategyState<Set<Long>,Integer>, String>(ltsAdapter);
            MTS<Long, String> plainController = new GenericMTSToLongStringMTSConverter<StrategyState<Set<Long>, Integer>, String>().transform(synthesised);

            output.outln("Controller [" + plainController.getStates().size() + "] generated successfully.");
            CompactState compactState = MTSToAutomataConverter.getInstance().convert(plainController, uccs.getName(), false);
            uccs.setComposition(compactState);
        } else {
            output.outln("There is no controller for model " + uccs.name + " for the given setting.");
            uccs.setComposition(null);
        }
    }

    private static Assumptions<Set<Long>> formulasToAssumptions(Set<Set<Long>> states, List<Formula> formulas, FluentStateValuation<Set<Long>> valuation) {

        Assumptions<Set<Long>> assumptions = new Assumptions<Set<Long>>();
        for (Formula formula : formulas) {
            Assume<Set<Long>> assume = new Assume<Set<Long>>();
            for (Set<Long> state : states) {
                valuation.setActualState(state);
                if (formula.evaluate(valuation)) {
                    assume.addState(state);
                }
            }
            if (assume.isEmpty()) {
                Logger.getAnonymousLogger().warning("There is no state satisfying formula:" + formula);
            }
            assumptions.addAssume(assume);
        }

        if (assumptions.isEmpty()) {
            Assume<Set<Long>> trueAssume = new Assume<Set<Long>>();
            trueAssume.addStates(states);
            assumptions.addAssume(trueAssume);
        }

        return assumptions;
    }

    private static Guarantees<Set<Long>> formulasToGuarantees(Set<Set<Long>> states, List<Formula> formulas, FluentStateValuation<Set<Long>> valuation) {

        Guarantees<Set<Long>> guarantees = new Guarantees<Set<Long>>();
        for (Formula formula : formulas) {
            Guarantee<Set<Long>> guarantee = new Guarantee<Set<Long>>();
            for (Set<Long> state : states) {
                valuation.setActualState(state);
                if (formula.evaluate(valuation)) {
                    guarantee.addState(state);
                }
            }
            if (guarantee.isEmpty()) {
                Logger.getAnonymousLogger().warning("There is no state satisfying formula:" + formula);
            }
            guarantees.addGuarantee(guarantee);
        }

        if (guarantees.isEmpty()) {
            Guarantee<Set<Long>> trueAssume = new Guarantee<Set<Long>>();
            trueAssume.addStates(states);
            guarantees.addGuarantee(trueAssume);
        }

        return guarantees;
    }

    private static void synthesizeGRDeterministic(UpdatingControllerCompositeState uccs, LTSOutput output, MTS<Long, String> safetyEnv) {
        GRGame<Long> game;

        game = new GRGameBuilder<Long, String>().buildGRGameFrom(safetyEnv,uccs.getUpdateGRGoal());
        GRRankSystem<Long> system = new GRRankSystem<Long>(game.getStates(),game.getGoal().getGuarantees(),
                game.getGoal().getAssumptions(), game.getGoal().getFailures());
        PerfectInfoGRGameSolver<Long> solver = new PerfectInfoGRGameSolver<Long>(game, system);
        solver.solveGame();

        if (solver.isWinning(safetyEnv.getInitialState())) {
            Strategy<Long, Integer> strategy = solver.buildStrategy();
            GRGameSolver<Long> grSolver = (GRGameSolver<Long>) solver;
            Set<Pair<StrategyState<Long, Integer>, StrategyState<Long, Integer>>> worseRank = grSolver.getWorseRank();
            MTS<StrategyState<Long, Integer>, String> result = GameStrategyToMTSBuilder.getInstance().buildMTSFrom(safetyEnv, strategy, worseRank, uccs.getUpdateGRGoal().getLazyness());

            if (result == null) {
                output.outln("There is no controller for model " + uccs.name + " for the given setting.");
                uccs.setComposition(null);
            } else {
                GenericMTSToLongStringMTSConverter<StrategyState<Long, Integer>, String> transformer = new GenericMTSToLongStringMTSConverter<StrategyState<Long, Integer>, String>();
                MTS<Long, String> plainController = transformer.transform(result);

                output.outln("Controller [" + plainController.getStates().size() + "] generated successfully.");
                CompactState convert = MTSToAutomataConverter.getInstance().convert(plainController, uccs.getName());
                uccs.setComposition(convert);
            }
        } else {
            output.outln("There is no controller for model " + uccs.name + " for the given setting.");
            uccs.setComposition(null);
        }

    }

}
