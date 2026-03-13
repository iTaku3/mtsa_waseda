package MTSSynthesis.controller.model;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import MTSSynthesis.controller.gr.StrategyState;

public abstract class RankBasedLabelledGameSolver<S, A, M> implements LabelledGameSolver<S, A, M> {
	protected static final long TIME_TO_LOG = 15000;

	protected abstract void addPredecessorsTo(Queue<StrategyState<S,M>> pending, StrategyState<S,M> strategyState, Rank bestRank);
	protected abstract void updateRank(StrategyState<S,M> strategyState, Rank bestRank);
	protected abstract void initialise(Queue<StrategyState<S,M>> pending);
	protected abstract Rank best(StrategyState<S,M> strategyState);
	protected abstract Rank getRank(StrategyState<S,M> strategyState);
	protected abstract Set<S> getGameStates();

	private boolean gameSolved;

	public boolean isGameSolved() {
		return gameSolved;
	}
	protected void gameSolved() {
		this.gameSolved = true;
	}
	
	@Override
	public void solveGame() {
		if (this.isGameSolved()) {
			return; 
		}
		Queue<StrategyState<S,M>> pending = new LinkedList<StrategyState<S,M>>();
		
//		System.out.println("Logging interval: " + TIME_TO_LOG/1000 + " seconds." );
		
		this.initialise(pending);
		long processed = 0;
		long time = System.currentTimeMillis();
		// Handle the pending states
		while (!pending.isEmpty()) {

			StrategyState<S,M> state = pending.poll();

			// The current rank of the state s
			Rank rank = this.getRank(state);

			// If current rank is already infinity, it obviously should
			// not be increased.
			if (rank.isInfinity()) {
				continue;
			}

			// What is the best possible ranking that s could have according to
			// it's successors?
			Rank bestRank = this.best(state);

			// The existing ranking is already higher or equal then nothing needs to be
			// done. Go to the next state in the set of pending
			if (bestRank.compareTo(rank) <= 0) {
				continue;
			}

			// set the new ranking of the state to the computed best value
			// If the new value is infinity it can be set for all rankings.
			this.updateRank(state, bestRank);

			this.addPredecessorsTo(pending, state, bestRank);

			processed++;
			time = log(processed, time);
		}
		this.gameSolved = true;
	}
	
	private long log(long processed, long time) {
		if((System.currentTimeMillis()-time)>TIME_TO_LOG) {
//			System.out.println("[" + processed + "]");
			time = System.currentTimeMillis();
		}
		return time;
	}	
	
	/* (non-Javadoc)
	 * @see controller.GameSolver#getWinningStates()
	 */
	public Set<S> getWinningStates() {
		Set<S> winning = new HashSet<S>();
		if (!isGameSolved()) {
			this.solveGame();
		}
		for (S state : this.getGameStates()) {
			if (isWinning(state)) {
				winning.add(state);
			}
		}
		return winning;
	}
}
