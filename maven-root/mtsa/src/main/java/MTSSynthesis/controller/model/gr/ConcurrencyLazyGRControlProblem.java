package MTSSynthesis.controller.model.gr;

import MTSSynthesis.controller.model.ControllerGoal;
import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSSynthesis.controller.gr.StrategyState;
import MTSSynthesis.controller.util.GenericLTSStrategyStateToStateConverter;
import MTSSynthesis.controller.model.GRGameControlProblem;
import MTSSynthesis.controller.model.LazyGRControlProblem;

public class ConcurrencyLazyGRControlProblem<S,A, M> extends GRGameControlProblem<S, A, M>{
	
	LazyGRControlProblem<S, A> lazyGRControlProblem;
	
	public ConcurrencyLazyGRControlProblem(LTS<S, A> environment, ControllerGoal<A> controllerGoal){
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
		ConcurrencyControlProblem<S,A,M> ccp = new ConcurrencyControlProblem<S,A,M>(env, controllerGoal);
		LTS<StrategyState<S, Integer>, A> result = ccp.rawSolve();
		g = ccp.getGRGame();
		return result;
	}
}
