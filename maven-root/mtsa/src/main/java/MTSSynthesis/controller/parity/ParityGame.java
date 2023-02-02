package MTSSynthesis.controller.parity;

import java.util.Set;

import MTSSynthesis.controller.model.StateBasedGame;

public class ParityGame<State> extends StateBasedGame<State> {
	
	private ParityGoal<State> goal;
	
	
	public ParityGame(Set<State> initialStates, Set<State> states) {
		super(initialStates, states);
	}
	
	public Integer getPriority(State state){
		return this.getGoal().getPriority(state);
	}
	
	@Override
	public ParityGoal<State> getGoal() {
		return this.goal;
	}
}
