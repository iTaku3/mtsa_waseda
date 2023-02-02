package MTSSynthesis.controller.model.gr.concurrency;

import java.util.Set;

import MTSSynthesis.controller.model.gr.GRGame;

public class GRCGame<State> extends GRGame<State> {
	
	private GRCGoal<State> goal;

	public GRCGame(Set<State> initialStates, Set<State> states, GRCGoal<State> goal) {
		super(initialStates, states, goal);
		this.goal = goal;
	}
	
	@Override
	public GRCGoal<State> getGoal() {
		return goal;
	}	
}
