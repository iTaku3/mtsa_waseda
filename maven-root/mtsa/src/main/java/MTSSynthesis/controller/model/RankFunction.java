package MTSSynthesis.controller.model;

import java.util.Set;

public interface RankFunction<State> {

	public abstract void increase(State state);

	public abstract boolean isInfinity(State state);

	public abstract void setRank(State state, Rank rank);

	public abstract Rank getRank(State state);
	
	public abstract RankContext getContext();

	public abstract Rank getMinimum(Set<State> states);

	public abstract Rank getMaximum(Set<State> states);

}