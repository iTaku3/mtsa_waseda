package MTSSynthesis.controller.model.bgr;

import MTSSynthesis.controller.bgr.BGRGame;
import MTSSynthesis.controller.bgr.BGRGameSolver;
import MTSSynthesis.controller.bgr.BGRRankSystem;
import MTSSynthesis.controller.gr.StrategyState;
import MTSSynthesis.controller.model.Strategy;
import MTSSynthesis.controller.util.GameStrategyToLTSBuilder;
import MTSSynthesis.controller.util.bgr.BGRGameBuilder;
import MTSSynthesis.controller.model.ControlProblem;
import MTSSynthesis.controller.model.ControllerGoal;
import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.impl.LTSAdapter;
import org.apache.commons.lang.Validate;

import java.util.Set;

public class BGRControlProblem<S, A> implements ControlProblem<StrategyState<S, Integer>, A> {

	protected ControllerGoal<A> controllerGoal;
	protected MTS<S,A> environment;
	protected Set<A> controllable;
	protected boolean problemSolved;
	private LTS<StrategyState<S, Integer>,A> solution;

	public BGRControlProblem(MTS<S, A> environment,
                             ControllerGoal<A> controllerGoal){
		Validate.isTrue(controllerGoal.getBuchi().size() <= 1,
				"Cannot solve BGR goals with more than one buchi set");
		this.environment = environment;
		this.controllerGoal = controllerGoal;
		this.problemSolved = false;
	}

	@Override
	public LTS<StrategyState<S, Integer>, A> solve() {
		if(!problemSolved){
			BGRGame<S> game = new BGRGameBuilder<>(environment, controllerGoal).buldBGRGame();
			BGRRankSystem<S> rankSystem = new BGRRankSystem<>(game.getStates(),
					game.getGoal().getGuarantees(),
					game.getGoal().getAssumptions(),
					game.getGoal().getFailures(),
					game.getGoal().getBuchi());

			BGRGameSolver<S> bgrGameSolver = new BGRGameSolver<>(game, rankSystem);
			if (game.getInitialStates().stream().allMatch(bgrGameSolver::isWinning)) {
				Strategy<S, Integer> strategy = bgrGameSolver.buildStrategy();

				LTS<StrategyState<S, Integer>, A> result = GameStrategyToLTSBuilder.getInstance()
						.buildLTSFrom(new LTSAdapter<>(environment, MTS.TransitionType.REQUIRED), strategy);
				result.removeUnreachableStates();
				solution = result;
			}
		}
		return solution;
	}
}
