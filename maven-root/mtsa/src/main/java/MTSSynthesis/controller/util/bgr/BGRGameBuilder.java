package MTSSynthesis.controller.util.bgr;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import MTSSynthesis.ar.dc.uba.model.condition.*;
import MTSSynthesis.controller.bgr.BGRGame;
import MTSSynthesis.controller.bgr.BGRGoal;
import MTSSynthesis.controller.model.Assume;
import MTSSynthesis.controller.model.Assumptions;
import MTSSynthesis.controller.model.Guarantees;
import MTSSynthesis.controller.util.FluentStateValuation;
import MTSSynthesis.controller.util.GRGameBuilder;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSSynthesis.ar.dc.uba.model.language.SingleSymbol;
import MTSSynthesis.ar.dc.uba.model.language.Symbol;
import MTSSynthesis.controller.model.ControllerGoal;
import org.apache.commons.lang.Validate;

public class BGRGameBuilder<State, Action> extends GRGameBuilder<State, Action> {
	private final MTS<State, Action> env;
	private final ControllerGoal<Action> goal;


	public BGRGameBuilder(MTS<State, Action> env,
						  ControllerGoal<Action> goal) {
		this.env = env;
		this.goal = goal;
	}

	public BGRGame<State> buldBGRGame() {
		this.validateActions(this.env, this.goal);
		Validate.isTrue(this.goal.getBuchi().size() <= 1,
				"\n Synthesizing a BGR controller with more than one  " +
				"buchi set is not supported.");

		Set<State> initialStates = new HashSet<>();
		initialStates.add(this.env.getInitialState());

		Assumptions<State> assumptions = new Assumptions<>();
		Guarantees<State> guarantees = new Guarantees<>();
		Set<State> failures = new HashSet<>();
		Set<State> buchi = new HashSet<>();

		FluentStateValuation<State> valuation = super.buildGoalComponents(this.env, this.goal, assumptions, guarantees, failures);
		for (Formula formula : this.goal.getBuchi()) {
			for (State state : this.env.getStates()) {
				this.formulaToStateSet(buchi, formula, state, valuation);
			}
			if (buchi.isEmpty()) {
				Logger.getAnonymousLogger().log(Level.WARNING, "No state satisfies formula: " + formula);
			}
		}

		BGRGoal<State> bgrGoal = new BGRGoal<>(guarantees, assumptions, failures, buchi);
		BGRGame<State> game = new BGRGame<>(initialStates, this.env.getStates(), bgrGoal);

		for (State from : this.env.getStates()) {
			for (Pair<Action, State> tr : this.env.getTransitions(from, MTS.TransitionType.REQUIRED)) {
				State to = tr.getSecond();
				if (!this.goal.getControllableActions().contains(tr.getFirst())) {
					game.addUncontrollableSuccessor(from, to);
				} else {
					game.addControllableSuccessor(from, to);
				}
			}
			if (!game.isUncontrollable(from)) {
				for (State state : game.getControllableSuccessors(from)) {
					game.addPredecessor(from, state);
				}
			}
		}
		return game;
	}

	public BGRGame<State> buildBGRGameForRTC(Action envYields,
											 Action contYields,
											 Action controllableState,
											 Action uncontrollableState,
											 Fluent actionFluent) {
		this.validateActions(this.env, this.goal);

		Set<State> initialStates = Collections.singleton(this.env.getInitialState());

		Assumptions<State> assumptions = new Assumptions<>();
		Guarantees<State> guarantees = new Guarantees<>();
		Set<State> failures = new HashSet<>();

		super.buildGoalComponents(this.env, this.goal, assumptions, guarantees, failures);

		//TODO symbol should be parametrised.
		Symbol envY = new SingleSymbol(envYields.toString());
		Symbol contY = new SingleSymbol(contYields.toString());

		// Fluents indicating the environment giving control back and the controller giving control back.
		// The environment starts with control.
		Fluent en_m = new FluentImpl("Environment Yields", envY, contY, false);
		Fluent en_e = new FluentImpl("Controller Yields", contY, envY, true);
		FluentStateValuation<State> yieldValuations = FluentUtils.getInstance()
				.buildValuation(env, new HashSet<>(Arrays.asList(en_m, en_e)));


		// States which only had controllable actions on environment
		Set<State> controllableStates = this.env.getStates().stream().filter(s ->
				this.env.getTransitions(s, MTS.TransitionType.REQUIRED).stream().anyMatch((transition) ->
						transition.getFirst() == controllableState
				)).collect(Collectors.toSet());

		Set<State> uncontrollableStates = this.env.getStates().stream().filter(s ->
				this.env.getTransitions(s, MTS.TransitionType.REQUIRED).stream().anyMatch((transition) ->
						transition.getFirst() == uncontrollableState
				)).collect(Collectors.toSet());


		// States in which the environment has control due to the controller yielding it
		Set<State> buchi = this.env.getStates().stream()
				.filter((s) -> yieldValuations.isTrue(s, en_e))
				.collect(Collectors.toSet());
		buchi.addAll(controllableStates);

		// We consider every state to satisfy the buchi condition if there are no buchi states.
		if (buchi.isEmpty()) {
			buchi.addAll(this.env.getStates());
		}

		// States on which the controller has control due to the environment yielding or the state only having
		// uncontrollable actions on the environment
		Assume<State> envYieldsAssumptions = new Assume<>();
		envYieldsAssumptions.addStates(this.env.getStates().stream()
				.filter((s) -> yieldValuations.isTrue(s, en_m)).collect(Collectors.toSet()));
		envYieldsAssumptions.addStates(uncontrollableStates);
		assumptions.addAssume(envYieldsAssumptions);

		// We force the environment to do a non yielding action
		Assume<State> A = new Assume<>();
		FluentStateValuation<State> actionValuations = FluentUtils.getInstance()
				.buildValuation(env, Collections.singleton(actionFluent));
		A.addStates(this.env.getStates().stream()
				.filter((s) -> actionValuations.isTrue(s, actionFluent))
				.collect(Collectors.toSet()));
		assumptions.addAssume(A);

		controllableStates.forEach(s1 -> {
			Set<State> image = this.env.getTransitions(s1, MTS.TransitionType.REQUIRED).getImage(controllableState);
			image.forEach(s2 -> this.env.removeTransition(s1, controllableState, s2, MTS.TransitionType.REQUIRED));
		});
		uncontrollableStates.forEach(s1 -> {
			Set<State> image = this.env.getTransitions(s1, MTS.TransitionType.REQUIRED).getImage(uncontrollableState);
			image.forEach(s2 -> this.env.removeTransition(s1, uncontrollableState, s2, MTS.TransitionType.REQUIRED));
		});
		this.env.removeAction(controllableState);
		this.env.removeAction(uncontrollableState);

		Set<Action> controllableActions = new HashSet<>(this.goal.getControllableActions());
		controllableActions.add(contYields);

		BGRGoal<State> bgrGoal = new BGRGoal<>(guarantees, assumptions, failures, buchi);
		BGRGame<State> game = new BGRGame<>(initialStates, this.env.getStates(), bgrGoal);

		for (State from : this.env.getStates()) {
			for (Pair<Action, State> tr : this.env.getTransitions(from, MTS.TransitionType.REQUIRED)) {
				State to = tr.getSecond();
				if (!controllableActions.contains(tr.getFirst())) {
					game.addUncontrollableSuccessor(from, to);
				} else {
					game.addControllableSuccessor(from, to);
				}
			}
			if (!game.isUncontrollable(from)) {
				for (State state : game.getControllableSuccessors(from)) {
					game.addPredecessor(from, state);
				}
			}
		}
		return game;
	}


	@Override
	public void validateActions(MTS<State, Action> mts, ControllerGoal<Action> goal) {
		super.validateActions(mts, goal);
	}
}
