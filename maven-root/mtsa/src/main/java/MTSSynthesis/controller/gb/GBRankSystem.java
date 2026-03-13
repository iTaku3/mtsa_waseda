package MTSSynthesis.controller.gb;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import MTSSynthesis.controller.model.Assume;
import MTSSynthesis.controller.model.Assumptions;
import MTSSynthesis.controller.model.Guarantee;
import MTSSynthesis.controller.model.Guarantees;

public class GBRankSystem<State> /* implements RankSystem<State>*/  {
	// This is a map from the guarantees (each guarantee is represented here
	// by its index in the array
	// to a ranking function.
	// Equally, it could be an array of RankFunction where the location in the
	// array
	// matches the location of the guarantee in guarantees
	private Map<Integer, GBRankFunction<State>> system;

	public GBRankSystem(Set<State> allStates, Guarantees<State> guarantees,
			Assumptions<State> assumptions) {
		// Initialise the functions for each of the guarantees to the function
		// that sets
		// all values to the minimal value
		this.system = new HashMap<Integer, GBRankFunction<State>>();

		for (int guaranteeId = 1; guaranteeId <= guarantees.size(); guaranteeId++) {
			Guarantee<State> guarantee = guarantees.getGuarantee(guaranteeId);

			// For each guarantee compute the assumption that has the maximal
			// number of states
			// that is not in this guarantee
			int maxAssumptionSize = 0; // allStates.size();
			for (int assumptionId = 1; assumptionId <= assumptions.getSize(); ++assumptionId) {
				Assume<State> assume = assumptions.getAssume(assumptionId);
				int assumeMinusGuarantee = assume.getDifferenceSize(guarantee.getStateSet());
				if (assumeMinusGuarantee > maxAssumptionSize) {
					maxAssumptionSize = assumeMinusGuarantee;
				}
			}
			// The maximal rank that the RankFunction related to this guarantee
			// can have
			// is the size of the maximal assumption
			this.system.put(guaranteeId, new GBRankFunction<State>(allStates,
					maxAssumptionSize + 1, assumptions.getSize()));
			// this.system.put(guaranteeId, new GRRankFunction<State>(allStates,
			// allStates.size(), assumptions.getSize()));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see controller.game.gr.RankSystem#getMaximum(int, java.util.Set)
	 */
	public GBRank getMaximum(int guaranteeId, Set<State> states) {
		return this.system.get(guaranteeId).getMaximum(states);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see controller.game.gr.RankSystem#getMinimum(int, java.util.Set)
	 */
	public GBRank getMinimum(int guaranteeId, Set<State> states) {
		return this.system.get(guaranteeId).getMinimum(states);
	}

	// I think that this function should not be available
	/*
	 * (non-Javadoc)
	 * 
	 * @see controller.game.gr.RankSystem#increase(State, int)
	 */
	public void increase(State state, int guarantee) {
		this.system.get(guarantee).increase(state);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see controller.game.gr.RankSystem#isInfinity(State, int)
	 */
	public boolean isInfinity(State state, int guarantee) {
		return this.system.get(guarantee).isInfinity(state);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see controller.game.gr.RankSystem#set(State, int,
	 * controller.game.gr.GRRank)
	 */
	public void set(State state, int guarantee, GBRank rank) {
		this.system.get(guarantee).updateRank(state, rank);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see controller.game.gr.RankSystem#getRank(State, int)
	 */
	public GBRank getRank(State state, int guaranteeIndex) {
		return this.system.get(guaranteeIndex).getRank(state);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see controller.game.gr.RankSystem#getContext(int)
	 */
	public GBRankContext getContext(int guaranteeId) {
		return this.system.get(guaranteeId).getContext();
	}

	@Override
	public String toString() {
		return this.system.toString();
	}
}
