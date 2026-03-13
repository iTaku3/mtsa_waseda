package MTSSynthesis.controller;

import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSSynthesis.controller.gr.StrategyState;
import MTSSynthesis.controller.model.ControllerGoal;
import MTSSynthesis.controller.model.gr.GRGame;

public interface LTSControllerSynthesiser<S, A> {

	/**
	 * Synthesise a controller for <i>plant</i> for the winning condition <i>goal</i>. 
	 * 
	 * @param <State>
	 * @param <Action>
	 * @param plant
	 * @param goal
	 * @return
	 */
	MTS<StrategyState<S, Integer>, A> synthesiseGR(MTS<S, A> env, ControllerGoal<A> goal);
//TODO return type must be an LTS
	/**
	 * Checks if the assumptions in <i>goal</i> are compatible with the <i>domain model</i> according
	 * to the definition provided in FSE 2010 paper. 
	 * Essentially, the compatibility check is done by checking if there is a strategy for a controller
	 * to win a game in which the only way for he to win is to violate the assumptions. This is, the game 
	 * with all the assumptions, the safety specification and no goals.    
	 * @param mts
	 * @param goal
	 */

	boolean checkGRAssumptionsCompatibility(MTS<S, A> env, ControllerGoal<A> goal);
//TODO env must be am LTS. assumptions compatibility is not defined for MTSs.
	
	/*
	 * Get the game the controller was synthesised from.
	 */
	GRGame<S> getGame();
}