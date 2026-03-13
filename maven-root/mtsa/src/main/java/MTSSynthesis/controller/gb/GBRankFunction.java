package MTSSynthesis.controller.gb;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GBRankFunction<State> {

	private Map<State,GBRank> function; // This is a ranking function. It maps every state to a Rank
	private GBRankContext context;
	
	public GBRankFunction(Set<State> allStates,int height,int width) {
		this.context = new GBRankContext(height);
		this.function = new HashMap<State, GBRank>();
		for (State state : allStates) {
			GBRank initialRank = new GBRank(this.context);
			function.put(state, initialRank);
		}
	}
	
	public void increase(State state) {
		this.function.get(state).increase();
	}
	
	public boolean isInfinity(State state) {
		return this.function.get(state).isInfinity();
	}

	public void updateRank(State state, GBRank rank) {
		this.function.get(state).set(rank);
	}
	
	public GBRank getRank(State state) {
		return this.function.get(state);
	}
	
	public GBRankContext getContext() {
		return this.context;
	}
	/*
	 *  This should actually be used only by RankSystem and not be public
	 */
	protected GBRank getMinimum(Set<State> states) {
		GBRank minimum = new GBRank(this.context);
		minimum.setToInfinity();
		
		for (State state : states) {
			GBRank rank = this.getRank(state);
			if (rank.compareTo(minimum)<0)
				minimum.set(rank);
		}
		return minimum;
	}
	
	/*
	 * This should actually be used only by RankSystem and not be public
	 */
	protected GBRank getMaximum(Set<State> states) {
		GBRank maximum = new GBRank(this.context);
			
		for (State state : states) {
			GBRank rank = this.getRank(state);
			if (rank.compareTo(maximum)>0)
				maximum.set(rank);
		}
		return maximum;
	}
	@Override
	public String toString() {
		return "Context: " + this.context.toString() + "\n"
		+ "Rank Function: " + this.function.toString();
	}
}
