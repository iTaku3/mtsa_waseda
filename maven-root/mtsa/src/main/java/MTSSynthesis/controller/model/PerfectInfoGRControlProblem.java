package MTSSynthesis.controller.model;

import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSTools.ac.ic.doc.mtstools.model.impl.MTSAdapter;
import MTSSynthesis.controller.gr.GRRankSystem;
import MTSSynthesis.controller.gr.StrategyState;
import MTSSynthesis.controller.gr.perfect.PerfectInfoGRGameSolver;
import MTSSynthesis.controller.util.GRGameBuilder;
import MTSSynthesis.controller.util.GameStrategyToLTSBuilder;
import MTSSynthesis.controller.util.GenericLTSStrategyStateToStateConverter;
import MTSSynthesis.controller.model.gr.GRGame;


public class PerfectInfoGRControlProblem<S,A> extends GRControlProblem<S, A, Integer>{

	protected PerfectInfoGRGameSolver<S> perfectInfoGRGameSolver;
	private GRGame<S> g;

	public PerfectInfoGRControlProblem(LTS<S,A> originalEnvironment, ControllerGoal<A> controllerGoal) {
		super(originalEnvironment, controllerGoal);
	}
		
	@Override
	protected LTS<S,A> primitiveSolve() {
		GRGame<S> perfectInfoGRGame = new GRGameBuilder<S,A>().buildGRGameFrom(new MTSAdapter<S,A>(environment), controllerGoal);
		g = perfectInfoGRGame;
		GRRankSystem<S> grRankSystem = new GRRankSystem<S>(
				perfectInfoGRGame.getStates(), perfectInfoGRGame.getGoal().getGuarantees(),
				perfectInfoGRGame.getGoal().getAssumptions(), perfectInfoGRGame.getGoal().getFailures());
		perfectInfoGRGameSolver = new PerfectInfoGRGameSolver<S>(perfectInfoGRGame, grRankSystem);
		perfectInfoGRGameSolver.solveGame();
		LTS<StrategyState<S, Integer>, A> result = GameStrategyToLTSBuilder
				.getInstance().buildLTSFrom(environment,
						perfectInfoGRGameSolver.buildStrategy());
		result.removeUnreachableStates();
		return new GenericLTSStrategyStateToStateConverter<S, A, Integer>()
				.transform(result);		
	}	
	
	public GRGame<S> getGRGame() {
		return g;
	}
	
}