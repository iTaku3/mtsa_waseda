package MTSSynthesis.controller.gr;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import MTSSynthesis.controller.model.Guarantee;
import MTSSynthesis.controller.model.Rank;
import MTSSynthesis.controller.model.RankBasedGameSolver;
import MTSSynthesis.controller.model.Strategy;
import org.apache.commons.lang.Validate;

import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSSynthesis.controller.model.gr.GRGame;
import MTSSynthesis.controller.model.gr.GRGoal;

/**
 * For the computations of successors and predecessors: A state s is
 * uncontrollable if it has some uncontrollable action enabled (at least one) A
 * state s is controllable if ALL the enabled actions are controllable Consider
 * a transition s --a--> t t is a successor of s if s is uncontrollable and a is
 * uncontrollable or if s is controllable (and then obviously a is controllable)
 * s is a predecessor of t if s is uncontrollable and a is uncontrollable or if
 * s is controllable (and then obviously a is controllable)
 */
public abstract class OppositeGRGameSolver<S> extends RankBasedGameSolver<S, Integer> {

	private GRRankSystem<S> rankSystem;

	// PLEASE REMOVE!!!
	private Set<Pair<StrategyState<S, Integer>, StrategyState<S, Integer>>> worseRank = new HashSet<Pair<StrategyState<S, Integer>, StrategyState<S, Integer>>>();

	@Override
	public void solveGame() {
		// TODO Auto-generated method stub
		super.solveGame();
	}
	
	protected void updateRank(StrategyState<S, Integer> strategyState,
			Rank bestRank) {
		S state = strategyState.getState();

		if (bestRank.isInfinity()) {
			// Sets infinite rank for state for all guarantees.
			for (int i = 1; i <= getGRGoal().getGuaranteesQuantity(); i++) {
				// TODO: getInfinityFor, may need to be moved to GRRankSystem
				GRRank infinityFor = GRRank.getInfinityFor(this.getRankSystem()
						.getContext(new StrategyState<S, Integer>(state, i)));
				this.getRankSystem().set(
						new StrategyState<S, Integer>(state, i), infinityFor);
			}
		} else {
			this.getRankSystem().set(strategyState, bestRank);
		}
	}

	protected void addIfNotIn(Collection<StrategyState<S, Integer>> pending,
			StrategyState<S, Integer> state) {
		if (!pending.contains(state)) {
			pending.add(state);
		}
	}

	protected boolean needsToBeUpdated(
			StrategyState<S, Integer> predecesorStrategyState) {

		Rank best = this.best(predecesorStrategyState);
		Rank rank = this.getRankSystem().getRank(predecesorStrategyState);
		return best.compareTo(rank) > 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see controller.GameSolver#isWinning(State)
	 */
	public boolean isWinning(S state) {
		if (!isGameSolved()) {
			this.solveGame();
		}
		return !this.getRankSystem()
				.getRank(new StrategyState<S, Integer>(state, 1)).isInfinity();
	}
	

	/**
	 * 
	 * @param state
	 *            , is the state we're evaluating
	 * @param succ
	 * @param guaranteeId
	 * @param nextGuaranteeId
	 * @param mayIncrease
	 * @return
	 */
	public boolean isBetterThan(StrategyState<S, Integer> state,
			StrategyState<S, Integer> succ, boolean mayIncrease) {

		Rank succRank = this.getRankSystem().getRank(succ);

		GRRank stateRank = this.getRankSystem().getRank(state);

		// int actualStateVSsuccessor = stateRank.compareTo(succRank);

		boolean isInAssumption = getGRGoal().getAssumption(
				stateRank.getAssume()).contains(state.getState());

		return ((mayIncrease && !succRank.isInfinity()) ||

		(!mayIncrease &&

		((stateRank.compareTo(succRank) > 0) || (stateRank.compareTo(succRank) == 0 && !isInAssumption))));

	}

	public GRGoal<S> getGRGoal() {
		return getGame().getGoal();
	}

	public int getNextGuarantee(int guaranteeId, S state) {
		if (getGRGoal().getGuarantee(guaranteeId).contains(state)
				|| getGRGoal().getFailures().contains(state)) {
			return this.increaseGuarantee(guaranteeId);
		} else {
			return guaranteeId;
		}
	}

	protected int getNextGuaranteeStrategy(int guaranteeId, S state) {
		Guarantee<S> guarantee = getGRGoal().getGuarantee(guaranteeId);
		if (guarantee.contains(state)) {
			return this.increaseGuarantee(guaranteeId);
		} else {
			return guaranteeId;
		}
	}

	private int increaseGuarantee(int guaranteeId) {
		return (guaranteeId % getGRGoal().getGuaranteesQuantity()) + 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see controller.GameSolver#buildStrategy()
	 */
	public Strategy<S, Integer> buildStrategy() {
		if (!isGameSolved())
			this.solveGame();

		Strategy<S, Integer> result = new Strategy<S, Integer>();

		Set<S> winningStates = this.getWinningStates();

		for (S state : winningStates) {
			for (int guaranteeId = 1; guaranteeId <= getGRGoal()
					.getGuaranteesQuantity(); guaranteeId++) {
				StrategyState<S, Integer> source = new StrategyState<S, Integer>(
						state, guaranteeId);

				int nextMemoryToConsider = this.getNextGuaranteeStrategy(
						guaranteeId, state);

				// If either a guarantee or a failure was just visited then it
				// is ok for the successor of state to have higher rank.
				boolean rankMayIncrease = getGRGoal().getGuarantee(guaranteeId)
						.contains(state)
						|| getGRGoal().getFailures().contains(state);

				Set<StrategyState<S, Integer>> successors = new HashSet<StrategyState<S, Integer>>();

				this.addUncontrollableSuccesors(state, source,
						nextMemoryToConsider, rankMayIncrease, successors);

				this.addControllableSuccesors(state, source,
						nextMemoryToConsider, rankMayIncrease, successors);

				Validate.notEmpty(successors, "\n State:" + source
						+ " should have at least one successor.");
				result.addSuccessors(source, successors);
			}
		}
		return result;
	}

	protected void addControllableSuccesors(S state,
			StrategyState<S, Integer> source, int nextMemoryToConsider,
			boolean rankMayIncrease, Set<StrategyState<S, Integer>> successors) {
		for (S succ : this.getGame().getControllableSuccessors(state)) {
			/*
			if(!this.isBetterThan(source,
					new StrategyState<S, Integer>(succ, nextMemoryToConsider),
					rankMayIncrease))
					Validate.isTrue(this.isBetterThan(source,
					new StrategyState<S, Integer>(succ, nextMemoryToConsider),
					rankMayIncrease), "State: " + succ
					+ " must have a better rank than state: " + state);
					*/
			Validate.isTrue(this.isWinning(succ), "state: " + succ
					+ " it's not winning.");
			StrategyState<S, Integer> target = new StrategyState<S, Integer>(
					succ, nextMemoryToConsider);
			successors.add(target);
		}
	}

	protected void addUncontrollableSuccesors(S state,
			StrategyState<S, Integer> source, int nextMemoryToConsider,
			boolean rankMayIncrease, Set<StrategyState<S, Integer>> successors) {
		for (S succ : this.getGame().getUncontrollableSuccessors(state)) {
			if(this.isWinning(succ)){
	
				StrategyState<S, Integer> target = new StrategyState<S, Integer>(
						succ, nextMemoryToConsider);
				successors.add(target);
			}
		}
	}



	@Override
	protected Rank getRank(StrategyState<S, Integer> strategyState) {
		return this.getRankSystem().getRank(strategyState);
	}

	@Override
	protected Set<S> getGameStates() {
		return this.getGame().getStates();
	}

	protected GRRankSystem<S> getRankSystem() {
		return rankSystem;
	}

	protected void setRankSystem(GRRankSystem<S> rankSystem) {
		this.rankSystem = rankSystem;
	}

	public abstract GRGame<S> getGame();

	public Set<Pair<StrategyState<S, Integer>, StrategyState<S, Integer>>> getWorseRank() {
		return worseRank;
	}
}
