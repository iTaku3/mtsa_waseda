package MTSSynthesis.controller.model.gr.concurrency;

import MTSSynthesis.controller.model.gr.GRGame;
import java.util.Set;

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
