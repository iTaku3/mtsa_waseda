package MTSSynthesis.controller.model;

import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSSynthesis.controller.gr.GRLabelledGame;
import MTSSynthesis.controller.gr.GRRankSystem;
import MTSSynthesis.controller.gr.StrategyState;
import MTSSynthesis.controller.gr.perfect.PerfectInfoOppositeGRLabelledGameSolver;
import MTSSynthesis.controller.util.GameStrategyToLTSBuilder;
import MTSSynthesis.controller.util.GenericLTSStrategyStateToStateConverter;
import MTSSynthesis.controller.model.gr.GRGoal;


public class PerfectInfoOppositeGRControlProblem<S,A> extends
	StateSpaceCuttingControlProblem<S, A>{

	public PerfectInfoOppositeGRControlProblem(LTS<S,A> originalEnvironment, ControllerGoal<A> controllerGoal, S trapState) {
		super(originalEnvironment, controllerGoal, trapState);
	}	
	
	@Override
	protected LTS<S,A> primitiveSolve() {
		//cut according to all the predefined game solvers
		if(gameSolver == null)
			gameSolver 	= buildGameSolver();
		gameSolver.solveGame();
		LTS<StrategyState<S, Integer>, A> result = GameStrategyToLTSBuilder
				.getInstance().buildLTSFrom(environment,
						gameSolver.buildStrategy());
		return new GenericLTSStrategyStateToStateConverter<S, A, Integer>()
				.transform(result);		
	}	
	
	@Override
	protected LabelledGameSolver<S, A, Integer> buildGameSolver() {
		// TODO Auto-generated method stub
		GRGoal<S> grGoal 		= getGRGoalFromControllerGoal(this.environment, controllerGoal);
		GRLabelledGame<S,A> game = new GRLabelledGame<>(environment, controllerGoal.getControllableActions(), grGoal);
		controllerGoal.cloneWithAssumptionsAsGoals();
		
		GRRankSystem<S> grRankSystem = new GRRankSystem<S>(
				game.getStates(), grGoal.getGuarantees(),
				grGoal.getAssumptions(), grGoal.getFailures());
		
		PerfectInfoOppositeGRLabelledGameSolver<S,A> perfectInfoGRGameSolver = new PerfectInfoOppositeGRLabelledGameSolver<S,A>(game,  grRankSystem);
		return perfectInfoGRGameSolver;
	}
}




