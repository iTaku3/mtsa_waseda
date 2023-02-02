package MTSSynthesis.controller.model.gr;

import MTSSynthesis.controller.model.ControllerGoal;
import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSSynthesis.controller.gr.StrategyState;
import MTSSynthesis.controller.util.GenericLTSStrategyStateToStateConverter;
import MTSSynthesis.controller.model.GRGameControlProblem;
import MTSSynthesis.controller.model.LazyGRControlProblem;

public class TransientLazyGRControlProblem<S,A, M> extends GRGameControlProblem<S, A, M>{
	
	LazyGRControlProblem<S, A> lazyGRControlProblem;
	
	public TransientLazyGRControlProblem(LTS<S, A> environment, ControllerGoal<A> controllerGoal){
		super(environment, controllerGoal);
	}
	
	@Override
	protected LTS<S, A> primitiveSolve() {
		return new GenericLTSStrategyStateToStateConverter<S, A, Integer>().transform(rawSolve()); 
	}
	
	@Override
	public	LTS<StrategyState<S, Integer>, A>  rawSolve(){
		lazyGRControlProblem = new LazyGRControlProblem<S,A>(environment, controllerGoal);
		LTS<S,A> env = lazyGRControlProblem.solve();
		TransientControlProblem<S,A,M> tcp = new TransientControlProblem<S,A,M>(env, controllerGoal);
		LTS<StrategyState<S, Integer>, A> result = tcp.rawSolve();
		g = tcp.getGRGame();
		return result;
	}
}
