package MTSSynthesis.controller.gr;

import java.util.Set;

import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSSynthesis.controller.SimpleLabelledGame;
import MTSSynthesis.controller.model.gr.GRGoal;

public class GRLabelledGame<S, A> extends SimpleLabelledGame<S, A> {
	private GRGoal<S> goal;
	
	public GRGoal<S> getGoal() {
		return goal;
	}

	public GRLabelledGame(LTS<S, A> env, Set<A> controllable, GRGoal<S> goal) {
		super(env, controllable);
		this.goal 	= goal;		
	}
	
}
