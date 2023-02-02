package MTSSynthesis.controller.util;

import java.util.Set;

import org.apache.commons.lang.Validate;

import MTSSynthesis.controller.model.Assume;
import MTSSynthesis.controller.model.Assumptions;
import MTSSynthesis.controller.model.Guarantee;
import MTSSynthesis.controller.model.Guarantees;
import MTSSynthesis.controller.model.Game;
import MTSSynthesis.controller.model.gr.GRGoal;

public class GameValidationHelper {
	public static <State> void areValidAssumptions(Game<State> game, Assumptions<State> assumptions) {
		for (int i = 1; i <= assumptions.getSize(); ++i) {
			Assume<State> assume = assumptions.getAssume(i);
			areValid(game, assume.getStateSet(), "Assumptions");
		}
	}
	
	public static <State> void areValidGuarantees(Game<State> game, Guarantees<State> guarantees) {
		for (int i = 1; i <= guarantees.size(); ++i) {
			Guarantee<State> guarantee = guarantees.getGuarantee(i);
			areValid(game, guarantee.getStateSet(), "Liveness Goals");
		}
	}

	private static void validateTrue(boolean valid, String message) {
		Validate.isTrue(valid, "Every state in the " + message +" set must be in game's state set.");
	}

	public static <State> void areValid(Game<State> game, Set<State> stateSet, String setName) {
		boolean valid = game.getStates().containsAll(stateSet);
		validateTrue(valid, setName);
	}
	
	public static <State> void validateGRGoal(Game<State> game, GRGoal<State> goal) {
		//TODO assumptions and guarantees should be just sets, and not special classes. These three methods must be collapsed to a single one.
		areValidAssumptions(game, goal.getAssumptions());
		areValidGuarantees(game, goal.getGuarantees());
		areValid(game, goal.getFailures(), "Failures");
	}

}
