package MTSSynthesis.controller.model;

import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSSynthesis.controller.gr.StrategyState;
import MTSSynthesis.controller.model.gr.GRGame;

public abstract class GRGameControlProblem<S,A,M> extends GRControlProblem<S, A, M> {
	
	protected GRGame<S> g;
	
	public GRGameControlProblem(LTS<S, A> environment, ControllerGoal<A> controllerGoal) {
		super(environment, controllerGoal);
	}

	public GRGame<S> getGRGame(){
		return g;
	}

	//TODO: Temporal solution for Transition System Dispatcher need of a LTS<StrategyState..
	public abstract LTS<StrategyState<S, Integer>, A> rawSolve();

}
