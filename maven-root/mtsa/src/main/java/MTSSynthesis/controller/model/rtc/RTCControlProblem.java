package MTSSynthesis.controller.model.rtc;

import MTSSynthesis.ar.dc.uba.model.condition.Fluent;
import MTSSynthesis.ar.dc.uba.model.condition.FluentImpl;
import MTSSynthesis.ar.dc.uba.model.language.SingleSymbol;
import MTSSynthesis.ar.dc.uba.model.language.Symbol;
import MTSSynthesis.controller.TransformationRecorder;
import MTSSynthesis.controller.bgr.BGRGame;
import MTSSynthesis.controller.bgr.BGRGameSolver;
import MTSSynthesis.controller.bgr.BGRRankSystem;
import MTSSynthesis.controller.model.Strategy;
import MTSSynthesis.controller.util.GameStrategyToLTSBuilder;
import MTSSynthesis.controller.model.ControlProblem;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSSynthesis.controller.gr.StrategyState;
import MTSSynthesis.controller.util.bgr.BGRGameBuilder;
import MTSSynthesis.controller.model.ControllerGoal;
import MTSTools.ac.ic.doc.mtstools.model.impl.LTSAdapter;
import MTSTools.ac.ic.doc.mtstools.model.impl.LTSImpl;
import MTSTools.ac.ic.doc.mtstools.model.impl.MTSAdapter;
import ltsa.ac.ic.doc.mtstools.util.fsp.AutomataToMTSConverter;
import ltsa.ac.ic.doc.mtstools.util.fsp.MTSToAutomataConverter;
import ltsa.control.util.ControllerUtils;
import ltsa.lts.CompactState;
import ltsa.lts.CompositeState;
import ltsa.lts.LTSOutput;
import ltsa.ui.StandardOutput;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType.REQUIRED;

public class RTCControlProblem implements ControlProblem<StrategyState<Long, Integer>, String> {
	private final BGRGame<Long> game;
	private final MTS<Long, String> env;
	private final ControllerGoal<String> goal;
	private final TransformationRecorder recorder;
	private final YieldRemoval<String> yieldRemoval;

	public RTCControlProblem(MTS<Long, String> env,
							 ControllerGoal<String> goal,
							 LTSOutput output,
							 TransformationRecorder recorder) {
		this.recorder = recorder;
		this.goal = goal.copy();

		String envYieldsAction = "envYields";
		String contYieldsAction = "contYields";

		this.yieldRemoval = new YieldRemoval<>(goal, envYieldsAction, contYieldsAction);

		Set<Symbol> actions = env.getTransitions(MTS.TransitionType.REQUIRED).values().stream()
				.flatMap((pair) -> pair.stream().map(Pair::getFirst))
				.map((SingleSymbol::new)).collect(Collectors.toSet());
		Fluent actionFluent = new FluentImpl("A",actions, actions, false);

		// States with only controllable actions
		Set<Long> controllableStates = env.getStates().stream()
				.filter(s -> s != -1)
				.filter(s -> env.getTransitions(s, MTS.TransitionType.REQUIRED).stream()
						.map(Pair::getFirst)
						.allMatch(action -> this.goal.getControllableActions().contains(action)))
				.collect(Collectors.toSet());

		// States with only uncontrollable actions
		Set<Long> uncontrollableStates = env.getStates().stream()
				.filter(s -> s != -1)
				.filter(s -> env.getTransitions(s, MTS.TransitionType.REQUIRED).stream()
						.map(Pair::getFirst)
						.noneMatch(action -> goal.getControllableActions().contains(action)))
				.collect(Collectors.toSet());

		// controllableStateAction and uncontrollableStateAction are marks on states to that after composing we can
		// identify states which had no controllable or uncontrollable actions
		String controllableStateAction = "controllable";
		String uncontrollableStateAction = "uncontrollable";

		env.addAction(controllableStateAction);
		env.addAction(uncontrollableStateAction);

		controllableStates.forEach(s -> env.addRequired(s, controllableStateAction, s));
		uncontrollableStates.forEach(s -> env.addRequired(s, uncontrollableStateAction, s));


		MTS<Long, String> synchronous = getYieldComposition(env, goal, controllableStateAction, uncontrollableStateAction, envYieldsAction, contYieldsAction);
		synchronous = ControllerUtils.embedFluents(synchronous, goal, output);

		this.env = synchronous;
		recorder.record(synchronous, "live(yield||env)");

		this.game = new BGRGameBuilder<>(synchronous, goal)
				.buildBGRGameForRTC(envYieldsAction,
						contYieldsAction,
						controllableStateAction,
						uncontrollableStateAction,
						actionFluent);
		recorder.record(this.env, "removing controllable/uncontrollable self loops");
	}

	private MTS<Long, String> getYieldComposition(MTS<Long, String> env,
												  ControllerGoal<String> goal,
												  String controllableStateAction,
												  String uncontrollableStateAction,
												  String envYieldsAction,
												  String contYieldsAction) {
		LTS<Long, String> sync = new LTSImpl<>(0L);
		sync.addActions(env.getActions());
		sync.addState(1L);
		sync.addAction(contYieldsAction);
		sync.addAction(envYieldsAction);
		sync.addTransition(0L, envYieldsAction, 1L);
		sync.addTransition(1L, contYieldsAction, 0L);

		// We only use actions that are currently being used.
		// There may be issues with actions being considered uncontrollable and added to this DLTS, "tau" for example.
		Set<String> actions = env.getTransitions(REQUIRED).values().stream()
				.flatMap(Collection::stream)
				.map(Pair::getFirst)
				.collect(Collectors.toSet());

		for (String action : actions) {
			if (goal.getControllableActions().contains(action)) {
				sync.addTransition(1L, action, 1L);
			} else {
				if (!action.equals(controllableStateAction) && !action.equals(uncontrollableStateAction)) {
					sync.addTransition(0L, action, 0L);
				}
			}
		}

		sync.addTransition(1L, controllableStateAction, 1L);
		sync.addTransition(0L, uncontrollableStateAction, 0L);

		MTSToAutomataConverter instance = MTSToAutomataConverter.getInstance();
		Vector<CompactState> machinesToCompose = new Vector<>();

		machinesToCompose.add(instance.convert(env, " "));
		machinesToCompose.add(instance.convert(new MTSAdapter<>(sync), " "));

		CompositeState c = new CompositeState(machinesToCompose);
		c.compose(new StandardOutput());

		MTS<Long, String> mtsComposition = AutomataToMTSConverter.getInstance().convert(c.composition);

		Predicate<Long> onlyYieldsBack = (s) -> mtsComposition.getTransitions(s, REQUIRED).stream()
				.allMatch((t) -> t.getFirst().equals(contYieldsAction)
						|| t.getFirst().equals(envYieldsAction)
						|| t.getFirst().equals(controllableStateAction)
						|| t.getFirst().equals(uncontrollableStateAction)
				);

		recorder.record(mtsComposition, "yield||env");
		List<Runnable> toRemove = new ArrayList<>();
		// We remove all envYields and contYields transitions that can afterwards only yield back (live(E||Y))
		mtsComposition.getTransitions(REQUIRED).forEach((from, transitions) -> {
			transitions.getImage(contYieldsAction).stream().filter(onlyYieldsBack).forEach(to ->
					toRemove.add(() ->
							mtsComposition.removeTransition(from, contYieldsAction, to, REQUIRED)));
			transitions.getImage(envYieldsAction).stream().filter(onlyYieldsBack).forEach(to ->
					toRemove.add(() ->
							mtsComposition.removeTransition(from, envYieldsAction, to, REQUIRED)));
		});
		toRemove.forEach(Runnable::run);
		mtsComposition.removeUnreachableStates();
		return mtsComposition;
	}

	public LTS<StrategyState<Long, Integer>, String> solve() {
		BGRRankSystem<Long> rankSystem = new BGRRankSystem<>(
				game.getStates(),
				game.getGoal().getGuarantees(),
				game.getGoal().getAssumptions(),
				game.getGoal().getFailures(),
				game.getGoal().getBuchi()
		);

		BGRGameSolver<Long> solver = new BGRGameSolver<>(game, rankSystem);
		Strategy<Long, Integer> strategy = solver.buildStrategy();
		LTS<StrategyState<Long, Integer>, String> result = GameStrategyToLTSBuilder.getInstance()
				.buildLTSFrom(new LTSAdapter<>(env, MTS.TransitionType.REQUIRED), strategy);

		recorder.record(new MTSAdapter<>(result), "before removing yields");

		if (game.getInitialStates().stream().allMatch(solver::isWinning)) {
			result.removeUnreachableStates();
			yieldRemoval.removeYields(result);
			recorder.record(new MTSAdapter<>(result), "before removing unreachable");
			result.removeUnreachableStates();
			recorder.record(new MTSAdapter<>(result), "after removing unreachable");
			return result;
		}
		return null;
	}
}
