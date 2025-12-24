package MTSSynthesis.controller.bgr;

import MTSSynthesis.controller.model.StateBasedGame;
import MTSSynthesis.controller.util.GameValidationHelper;
import java.util.Set;

public class BGRGame<State> extends StateBasedGame<State> {

  public BGRGoal<State> getGoal() {
    return goal;
  }

  private BGRGoal<State> goal;

  public BGRGame(Set<State> initialStates, Set<State> states, BGRGoal<State> goal) {
    super(initialStates, states);
    // TODO validations for assumptions, goals and B
    GameValidationHelper.validateGRGoal(this, goal);
    GameValidationHelper.areValid(this, goal.getBuchi(), "Buchi Goal");
    this.goal = goal;
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer().append(super.toString()).append(goal);
    return sb.toString();
  }
}
