package MTSSynthesis.controller.bgr;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import MTSSynthesis.controller.model.AbstractRankFunction;
import MTSSynthesis.controller.model.RankFunction;

public class BGRRankFunction<State> extends AbstractRankFunction<State> implements RankFunction<State> {
	
	private Map<State,BGRRank> function; 
	private BGRRankContext context;
	
	@Override
	public BGRRank getMinimum(Set<State> states) {
		return (BGRRank) super.getMinimum(states);
	}

	@Override
	public BGRRank getMaximum(Set<State> states) {
		return (BGRRank) super.getMaximum(states);
	}

	public BGRRankFunction(Set<State> allStates, int gHeight, int gWidth, int bHeight) {
		this.context = new BGRRankContext(gHeight, gWidth, bHeight);
		this.function = new HashMap<State, BGRRank>();
		for (State state : allStates) {
			BGRRank rank = new BGRRank(this.context);
			this.function.put(state, rank);
		}
	}

	@Override
	public BGRRank getNewRank() {
		return new BGRRank(this.getContext());
	}

	@Override
	public BGRRank getRank(State state) {
		return this.function.get(state);
	}

	@Override
	public BGRRankContext getContext() {
		return this.context;
	}
	
	@Override
	public String toString() {
		return "Context: " + this.context.toString() + "\n"
		+ "Rank Function: " + this.function.toString();
	}



}
