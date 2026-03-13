package MTSSynthesis.controller;

import MTSSynthesis.ar.dc.uba.model.condition.Fluent;
import MTSSynthesis.ar.dc.uba.model.condition.FluentUtils;
import MTSSynthesis.ar.dc.uba.model.condition.Formula;
import MTSSynthesis.ar.dc.uba.model.language.Symbol;
import MTSSynthesis.controller.gr.GRRankSystem;
import MTSSynthesis.controller.gr.StrategyState;
import MTSSynthesis.controller.gr.knowledge.KnowledgeGRGame;
import MTSSynthesis.controller.gr.knowledge.KnowledgeGRGameSolver;
import MTSSynthesis.controller.model.*;
import MTSSynthesis.controller.util.FluentStateValuation;
import MTSSynthesis.controller.util.GameStrategyToMTSBuilder;
import MTSSynthesis.controller.util.SubsetConstructionBuilder;
import MTSSynthesis.controller.model.gr.GRGoal;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;
import MTSTools.ac.ic.doc.mtstools.model.impl.LTSAdapter;
import org.apache.commons.lang.Validate;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class NondetControlProblem<S, A> implements ControlProblem<StrategyState<Set<S>, Integer>, A> {

	private KnowledgeGRGame<S, A> game;
	private GRGoal<Set<S>> grGoal;
	private MTS<Set<S>, A> perfectInfoGame;
	private SubsetConstructionBuilder<S, A> subsetConstructionBuilder;

	//TODO MTS -> LTS
	public NondetControlProblem(MTS<S, A> env, ControllerGoal<A> goal) {

		//ToDani: aca deberiamos embeber fluents. 

		// TODO dependency injection
		FluentUtils fluentUtils = FluentUtils.getInstance();
		subsetConstructionBuilder = new SubsetConstructionBuilder<S, A>(env);
		
		perfectInfoGame = subsetConstructionBuilder.build();

		// TODO move this code to a helper. SAme code is also used in
		// MTSToGRGameBuilder
		this.validateActions(perfectInfoGame, goal); // copied this from
														// MTSToGRGameBuilder
		FluentStateValuation<Set<S>> valuation = fluentUtils.buildValuation(perfectInfoGame, goal.getFluents());
		Assumptions<Set<S>> assumptions = this.formulasToAssumptions(perfectInfoGame.getStates(), goal.getAssumptions(), valuation);
		Guarantees<Set<S>> guarantees = this.formulasToGuarantees(perfectInfoGame.getStates(), goal.getGuarantees(), valuation);
		Set<Set<S>> faults = this.formulaToStateSet(perfectInfoGame.getStates(), goal.getFaults(), valuation);
		grGoal = new GRGoal<Set<S>>(guarantees, assumptions, faults, goal.isPermissive());
		Set<Set<S>> initialStates = new HashSet<Set<S>>();
		Set<S> initialState = new HashSet<S>();
		initialState.add(env.getInitialState());
		initialStates.add(initialState);
		game = new KnowledgeGRGame<S, A>(initialStates, env, perfectInfoGame, goal.getControllableActions(), grGoal);
	}

	@Override
	public LTS<StrategyState<Set<S>, Integer>, A> solve() {
		//ToDani: loguear que problema de control estamos resolviendo
		
		GRRankSystem<Set<S>> system = new GRRankSystem<Set<S>>(game.getStates(), grGoal.getGuarantees(), grGoal.getAssumptions(), grGoal.getFailures());

		KnowledgeGRGameSolver<S, A> solver = new KnowledgeGRGameSolver<S, A>(game, system);
		solver.solveGame();

		if (solver.isWinning(perfectInfoGame.getInitialState())) {
			Strategy<Set<S>, Integer> strategy = solver.buildStrategy();

			// Permissive Controllers!?
			Set<Pair<StrategyState<Set<S>, Integer>, StrategyState<Set<S>, Integer>>> worseRank = solver.getWorseRank();
			MTS<StrategyState<Set<S>, Integer>, A> result = GameStrategyToMTSBuilder.getInstance().buildMTSFrom(perfectInfoGame, strategy, worseRank);

			result.removeUnreachableStates();
			return new LTSAdapter<StrategyState<Set<S>,Integer>, A>(result, TransitionType.POSSIBLE);
		} else {
			return null;
		}
	}

	// DIPI refactor these validation methods to a parametric helper class
	private Set<Set<S>> formulaToStateSet(Set<Set<S>> states, List<Formula> formulas, FluentStateValuation<Set<S>> valuation) {

		Set<Set<S>> faults = new HashSet<Set<S>>();
		for (Formula formula : formulas) {
			for (Set<S> state : states) {
				valuation.setActualState(state);
				if (formula.evaluate(valuation)) {
					faults.add(state);
				}
			}
			return faults;
		}
		return faults;
	}

	private void validateActions(MTS<Set<S>, A> mts, ControllerGoal<A> goal) {
		Set<A> actions = mts.getActions();
		if (!actions.containsAll(goal.getControllableActions())) {
			Collection<A> controllableNotIn = new HashSet<A>();

			for (A action : goal.getControllableActions()) {
				if (!actions.contains(action)) {
					controllableNotIn.add(action);
				}
			}
//			System.out.println("WARNING: " + "The following actions in the controller " + "Goal does not belong to the mts action set.\n" + controllableNotIn);
		}

		for (Fluent fluent : goal.getFluents()) {
			this.validateFluentSymbols(fluent, actions, fluent.getInitiatingActions());
			this.validateFluentSymbols(fluent, actions, fluent.getTerminatingActions());
		}
	}

	private void validateFluentSymbols(Fluent fluent, Set<A> actions, Set<Symbol> initiatingActions) {
		for (Symbol symbol : initiatingActions) {
			Validate.isTrue(actions.contains(symbol.toString()), "\n Every action in " + fluent + " must be included in model action set. \n A: " + symbol.toString()
					+ " does not belong to actions set.");
		}
	}

	private Assumptions<Set<S>> formulasToAssumptions(Set<Set<S>> states, List<Formula> formulas, FluentStateValuation<Set<S>> valuation) {

		Assumptions<Set<S>> assumptions = new Assumptions<Set<S>>();
		for (Formula formula : formulas) {
			Assume<Set<S>> assume = new Assume<Set<S>>();
			for (Set<S> state : states) {
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
			Assume<Set<S>> trueAssume = new Assume<Set<S>>();
			trueAssume.addStates(states);
			assumptions.addAssume(trueAssume);
		}

		return assumptions;
	}

	private Guarantees<Set<S>> formulasToGuarantees(Set<Set<S>> states, List<Formula> formulas, FluentStateValuation<Set<S>> valuation) {

		Guarantees<Set<S>> guarantees = new Guarantees<Set<S>>();
		for (Formula formula : formulas) {
			Guarantee<Set<S>> guarantee = new Guarantee<Set<S>>();
			for (Set<S> state : states) {
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
			Guarantee<Set<S>> trueAssume = new Guarantee<Set<S>>();
			trueAssume.addStates(states);
			guarantees.addGuarantee(trueAssume);
		}

		return guarantees;
	}

}
