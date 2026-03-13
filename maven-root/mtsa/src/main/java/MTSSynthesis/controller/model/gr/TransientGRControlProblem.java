package MTSSynthesis.controller.model.gr;

import MTSSynthesis.controller.model.ControllerGoal;
import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSSynthesis.controller.gr.StrategyState;
import MTSSynthesis.controller.util.GenericLTSStrategyStateToStateConverter;
import MTSSynthesis.controller.model.GRGameControlProblem;
import MTSSynthesis.controller.model.PerfectInfoGRControlProblem;

public class TransientGRControlProblem<S,A, M> extends GRGameControlProblem<S, A, M>{
	
	PerfectInfoGRControlProblem<S, A> perfectInfoGRControlProblem;

	public TransientGRControlProblem(LTS<S, A> environment, ControllerGoal<A> controllerGoal){
		super(environment, controllerGoal);
	}
	
	@Override
	protected LTS<S, A> primitiveSolve() {
		return new GenericLTSStrategyStateToStateConverter<S, A, Integer>().transform(rawSolve()); 
	}
	
	@Override
	public	LTS<StrategyState<S, Integer>, A>  rawSolve(){
		perfectInfoGRControlProblem = new PerfectInfoGRControlProblem<S,A>(environment, controllerGoal);
		LTS<S,A> env = perfectInfoGRControlProblem.solve();
		TransientControlProblem<S,A,M> tcp = new TransientControlProblem<S,A,M>(env, controllerGoal);
		LTS<StrategyState<S, Integer>, A> result = tcp.rawSolve();
		g = tcp.getGRGame();
		return result;
	}
	
}
