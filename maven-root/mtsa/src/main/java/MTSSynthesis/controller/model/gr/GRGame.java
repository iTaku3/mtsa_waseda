package MTSSynthesis.controller.model.gr;

import java.util.Set;

import MTSSynthesis.controller.model.StateBasedGame;
import MTSSynthesis.controller.util.GameValidationHelper;

public class GRGame<State> extends StateBasedGame<State> {

	private GRGoal<State> goal;
	
	public GRGoal<State> getGoal() {
		return goal;
	}

	public GRGame(Set<State> initialStates, Set<State> states, GRGoal<State> goal) {
		super(initialStates, states);
		GameValidationHelper.validateGRGoal(this,goal);
		this.initialize(goal);
	}

	protected void initialize(GRGoal<State> goal) {
		this.goal = goal;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer()
		.append(super.toString()).append(this.getGoal());
		return sb.toString();
	}
}
