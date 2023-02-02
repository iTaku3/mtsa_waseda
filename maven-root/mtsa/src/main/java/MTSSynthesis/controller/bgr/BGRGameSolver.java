package MTSSynthesis.controller.bgr;

import java.util.Collection;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import MTSSynthesis.controller.gr.GRRank;
import MTSSynthesis.controller.gr.GRUtils;
import MTSSynthesis.controller.gr.StrategyState;
import org.apache.commons.lang.Validate;

import MTSSynthesis.controller.model.Assume;
import MTSSynthesis.controller.model.Game;
import MTSSynthesis.controller.model.Rank;
import MTSSynthesis.controller.model.RankBasedGameSolver;
import MTSSynthesis.controller.model.Strategy;

//TODO: See inheritance from PerfectInfoGRGameSolver...

public class BGRGameSolver<S> extends RankBasedGameSolver<S, Integer> {
	private BGRRankSystem<S> rankSystem;
	private BGRGame<S> game;

	public BGRGameSolver(BGRGame<S> game, BGRRankSystem<S> rankSystem) {
		this.game = game;
		this.rankSystem = rankSystem;
	}

	@Override
	public boolean isWinning(S state) {
		if (!isGameSolved()) {
			this.solveGame();
		}
		return !this.getRankSystem()
				.getRank(new StrategyState<S, Integer>(state, 1))
				.isInfinity();
	}

	@Override
	public Strategy<S, Integer> buildStrategy() {
		this.solveGame();
		
		Strategy<S, Integer> result = new Strategy<S, Integer>();
		
		Set<S> winningStates = this.getWinningStates();

		for (S state : winningStates) {
			for (int guaranteeId = 1 ; guaranteeId <= this.getBGRGoal().livenessSize() ; guaranteeId++) {
				StrategyState<S,Integer> source = new StrategyState<S,Integer>(state, guaranteeId);
				
				int nextGuaranteeId = this.getNextGuaranteeStrategy(guaranteeId, state);

				boolean rankIncrease = nextGuaranteeId != guaranteeId;

				//If either a guarantee or a failure was just visited then it is ok for the successor of state to have higher rank.
				boolean isGoalState = this.isGuarantee(guaranteeId) && this.isGoal(state, guaranteeId);
				boolean isBuchiState = this.isBuchi(state);
				
				Set<StrategyState<S,Integer>> successors = new HashSet<>();
				for (S succ : this.getGame().getUncontrollableSuccessors(state)) {
					StrategyState<S, Integer> succStrategy = new StrategyState<>(succ, nextGuaranteeId);
					BGRRank succRank = this.rankSystem.getRank(succStrategy);
					Validate.isTrue(rankIncrease && !succRank.isInfinity() || this.isBetterThan(source, succStrategy, isGoalState, isBuchiState),
							"State: " + succ + " must have a better rank than state: " + state);
					successors.add(succStrategy);
				}
				
				for (S succ : this.getGame().getControllableSuccessors(state)) {
					StrategyState<S, Integer> succStrategy = new StrategyState<>(succ, nextGuaranteeId);
					BGRRank succRank = this.rankSystem.getRank(succStrategy);
					if (rankIncrease && !succRank.isInfinity() || !rankIncrease && this.isBetterThan(source, succStrategy, isGoalState, isBuchiState)) {
						Validate.isTrue(this.isWinning(succ), "state: " + succ + " it's not winning.");
						successors.add(succStrategy);
					}
				}
				assert !successors.isEmpty(); // notEmpty(successors, "\n State:" + source + " should have at least one successor.");
				result.addSuccessors(source, successors);
			}
		}

		return result;
	}

	private boolean isBetterThan(StrategyState<S, Integer> state,
			StrategyState<S, Integer> succ, boolean isGoalState,
			boolean isBuchiState) {
		BGRRank stateRank = this.getRankSystem().getRank(state);
		BGRRank succRank = this.getRankSystem().getRank(succ);
		GRRank stateGRRank = new GRRank(stateRank);
		GRRank succGRRank = new GRRank(succRank);

		boolean bgrDecrease = stateRank.compareTo(succRank) > 0;
		int grStateVSsuccessor = stateGRRank.compareTo(succGRRank);
		boolean grDecrease = grStateVSsuccessor > 0;
		boolean possibleBuchiIncrease = grStateVSsuccessor == 0;
		
		boolean isInAssumption = this.isAssumption(state.getState(), stateRank.getAssume());
		if (this.isGuarantee(state.getMemory())) {
			return ((isGoalState && !succRank.isInfinity()) ||
					(!isGoalState && isInAssumption && grDecrease) ||
					(!isGoalState && !isInAssumption && isBuchiState && (grDecrease || possibleBuchiIncrease)) ||
					(!isGoalState && !isInAssumption && !isBuchiState && bgrDecrease));
		} else {
			return ((isBuchiState && !succRank.isInfinity()) ||
					(!isBuchiState && bgrDecrease)); 
		}
	}

	@Override
	protected void addPredecessorsTo(
			Queue<StrategyState<S, Integer>> pending,
			StrategyState<S, Integer> strategyState, Rank bestRank) {
		S state = strategyState.getState();
		int bgrId = strategyState.getMemory();

		Set<S> predecessors = this.getGame().getPredecessors(state);
		for (S pred : predecessors) {

			if (bestRank.isInfinity()) {
				if (this.getGame().isUncontrollable(pred)) {
					pending.add(new StrategyState<S, Integer>(pred, 1));
				} else {
					for (int i = 1; i <= this.getBGRGame().getGoal().getGuaranteesQuantity(); i++) {
						// This check may not be required. If I add the state to
						// the queue then I'll recompute the best.
						// Essentially, pred does not need to be updated we'll
						// skip it in the main loop.
						StrategyState<S, Integer> predecessor = new StrategyState<S, Integer>(pred, i);
						if (this.needsToBeUpdated(predecessor)) {
							addIfNotIn(pending, predecessor);
						}
					}
				}
			} else {
				StrategyState<S, Integer> predecessor = new StrategyState<S, Integer>(pred, bgrId);
				if (this.needsToBeUpdated(predecessor)) {
					addIfNotIn(pending, predecessor);
				}
			}

			// Handle the buchi goal
			int buchiId = this.getBGRGame().getGoal().livenessSize();
			if (bestRank.isInfinity() && !this.getGame().isUncontrollable(pred)) {
				StrategyState<S, Integer> predecessor = new StrategyState<S, Integer>(
						pred, buchiId);
				if (this.needsToBeUpdated(predecessor)) {
					addIfNotIn(pending, predecessor);
				}
			}

		}
	}

	protected boolean needsToBeUpdated(StrategyState<S, Integer> predStrategyState) {
		// copied from GRGameSolver
		Rank best = this.best(predStrategyState);
		Rank rank = this.getRankSystem().getRank(predStrategyState);
		return best.compareTo(rank) > 0;
	}

	protected void addIfNotIn(Collection<StrategyState<S, Integer>> pending, StrategyState<S, Integer> state) {
		// copied from GRGameSolver
		if (!pending.contains(state)) {
			pending.add(state);
		}
	}

	public Game<S> getGame() {
		return this.game;
	}
	
	public BGRGame<S> getBGRGame() {
		return (BGRGame<S>)this.game;
	}	

	@Override
	protected void updateRank(StrategyState<S, Integer> strategyState, Rank bestRank) {
		//TODO: Code almost identical to updateRank in GRGameSolver
		//      The only difference is the bound on the for loop
		S state = strategyState.getState();
		
		if (bestRank.isInfinity()) {
			for (int i=1 ; i<= this.getBGRGoal().livenessSize(); i++) {
				StrategyState<S, Integer> strategyState2 = new StrategyState<S,Integer>(state,i);
				BGRRank infinityFor = BGRRank.getInfinityFor(this.getRankSystem().getContext(strategyState2));
				this.getRankSystem().set(strategyState2,infinityFor);
			}
		} else {
			this.getRankSystem().set(strategyState, bestRank);
		}
	}

	@Override
	protected void initialise(Queue<StrategyState<S, Integer>> pending) {
		int guaranteesQuantity = this.getBGRGame().getGoal()
				.getGuaranteesQuantity();
		int livenessSize = this.getBGRGame().getGoal().livenessSize();

		// Set the rank of every state that is a dead end to infinity (i.e
		// safety goals included)
		// Add it's predecessors (if necessary) to the pending states
		for (S state : this.getGame().getStates()) {
			if (this.isDeadlock(state)) {
				for (int guaranteeId = 1; guaranteeId <= livenessSize; guaranteeId++) {
					StrategyState<S, Integer> strategyState = new StrategyState<S, Integer>(
							state, guaranteeId);
					BGRRank infinity = BGRRank.getInfinityFor(this
							.getRankSystem().getContext(strategyState));
					this.getRankSystem().set(strategyState, infinity);
				}
				StrategyState<S, Integer> firstGuaranteeRank = new StrategyState<S, Integer>(
						state, 1);
				Rank infinity = this.getRankSystem()
						.getRank(firstGuaranteeRank);
				this.addPredecessorsTo(pending, firstGuaranteeRank, infinity);
			}
		}

		for (S state : this.getGame().getStates()) {
			for (int i = 1; i <= guaranteesQuantity; i++) {
				if (!isGoal(state, i)
						&& (isFirstAssumption(state) || !isBuchi(state))) {

					pending.add(new StrategyState<S, Integer>(state, i));
				}
			}
			// handle buchi
			if (!isBuchi(state)) {
				pending.add(new StrategyState<S, Integer>(state, livenessSize));
			}
		}

	}

	private boolean isBuchi(S state) {
		return this.getBGRGame().getGoal().getBuchi().contains(state);
	}

	private boolean isFirstAssumption(S state) {
		return isAssumption(state, 1);
	}

	private boolean isAssumption(S state, int assumeId) {
		Assume<S> fa = this.getBGRGame().getGoal().getAssumption(assumeId);
		return fa.contains(state);
	}

	private boolean isGoal(S state, int i) {
		assert this.isGuarantee(i);
		return isGuaranteeState(state,i) || this.getBGRGame().getGoal().getFailures().contains(state);
	}

	private boolean isGuaranteeState(S state, int i) {
		assert this.isGuarantee(i);
		return this.getBGRGame().getGoal().getGuarantee(i).contains(state);
	}
	
	private boolean isDeadlock(S state) {
		return this.getGame().getSuccessors(state).isEmpty();
	}

	@Override
	protected BGRRank best(StrategyState<S, Integer> strategyState) {
		S state = strategyState.getState();
		Integer guaranteeOrBuchi = strategyState.getMemory();

		BGRRank bestRank = this.getBestFromSuccessors(state, guaranteeOrBuchi);

		if (isGuarantee(guaranteeOrBuchi) && this.isGoal(state, guaranteeOrBuchi) ||
				 !isGuarantee(guaranteeOrBuchi) && this.isBuchi(state)) {
			BGRRankContext myContext = this.getRankSystem().getContext(
					strategyState);

			if (bestRank.isInfinity()) {
				return BGRRank.getInfinityFor(myContext);
			} else {
				return new BGRRank(myContext);
			}
		} else if (isGuarantee(guaranteeOrBuchi) /*
												 * && !this.isGoal(state,
												 * guarnteeOrBuchi)
												 */) {
			if (this.isAssumption(state, bestRank.getAssume())) {
				bestRank.increaseGR();
			} else if (this.isBuchi(state)) {
				bestRank.resetBuchi();
			} else {
				bestRank.increase();
			}
		} else if (!isGuarantee(guaranteeOrBuchi) /* && !this.isBuchi(state) */) {
			bestRank.increase();
		}
		return bestRank;
	}

	private boolean isGuarantee(Integer guaranteeOrBuchi) {
		Integer maxGuarantee = this.getBGRGame().getGoal().getGuaranteesQuantity();
		return guaranteeOrBuchi <= maxGuarantee;
	}

	protected BGRRankSystem<S> getRankSystem() {
		return this.rankSystem;
	}

	private BGRRank getBestFromSuccessors(S state, Integer guaranteeOrBuchi) {
		BGRRank bestRank;
		int nextGuarantee = this.getNextGuarantee(guaranteeOrBuchi, state);
		if (getGame().isUncontrollable(state)) {
			bestRank = this.getRankSystem().getMax(
					GRUtils.getStrategyStatesFrom(nextGuarantee, this.getGame()
							.getUncontrollableSuccessors(state)));
		} else {
			bestRank = this.getRankSystem().getMin(
					GRUtils.getStrategyStatesFrom(nextGuarantee, this.getGame()
							.getControllableSuccessors(state)));
		}
		return bestRank;
	}

	private int getNextGuarantee(int guaranteeId, S state) {
		if ((this.isGuarantee(guaranteeId) && this.isGoal(state, guaranteeId))
				|| !this.isGuarantee(guaranteeId) && this.isBuchi(state)) {
			return this.increaseGuarantee(guaranteeId);
		} else {
			return guaranteeId;
		}
	}

	private int getNextGuaranteeStrategy(int guaranteeId, S state) {
		if ((this.isGuarantee(guaranteeId) && this.isGuaranteeState(state, guaranteeId))
				|| !this.isGuarantee(guaranteeId) && this.isBuchi(state)) {
			return this.increaseGuarantee(guaranteeId);
		} else {
			return guaranteeId;
		}
	}
	private int increaseGuarantee(int guaranteeId) {
		return (guaranteeId % getBGRGoal().livenessSize()) + 1;
	}

	private BGRGoal<S> getBGRGoal() {
		return this.getBGRGame().getGoal();
	}

	@Override
	protected Rank getRank(StrategyState<S, Integer> strategyState) {
		return this.getRankSystem().getRank(strategyState);
	}

	@Override
	protected Set<S> getGameStates() {
		return this.getGame().getStates();
	}

}
