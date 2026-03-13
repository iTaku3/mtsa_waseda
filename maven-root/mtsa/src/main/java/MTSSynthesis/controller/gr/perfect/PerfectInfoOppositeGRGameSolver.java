package MTSSynthesis.controller.gr.perfect;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import MTSSynthesis.controller.model.Assume;
import MTSSynthesis.controller.model.Rank;

import MTSSynthesis.controller.gr.GRRankContext;
import MTSSynthesis.controller.gr.GRUtils;
import MTSSynthesis.controller.gr.OppositeGRGameSolver;
import MTSSynthesis.controller.gr.GRRank;
import MTSSynthesis.controller.gr.GRRankSystem;
import MTSSynthesis.controller.gr.StrategyState;
import MTSSynthesis.controller.model.gr.GRGame;

public class PerfectInfoOppositeGRGameSolver<State> extends OppositeGRGameSolver<State> {

	protected GRGame<State> game;
	private Set<State> deadEnds;

	public PerfectInfoOppositeGRGameSolver(GRGame<State> game, GRRankSystem<State> rankSystem) {
		this.setGame(game);
		this.setRankSystem(rankSystem);
		deadEnds = new HashSet<State>();
	}
	
	protected void setGame(GRGame<State> game) {
		this.game = game;
	}

	protected void addPredecessorsTo(Queue<StrategyState<State, Integer>> pending, StrategyState<State, Integer> strategyState, Rank bestRank) {
		State state = strategyState.getState();
		int guaranteeId = strategyState.getMemory();

		Set<State> predecessors = this.getGame().getPredecessors(state);
		for (State pred : predecessors) {

			if (bestRank.isInfinity()) {
				if (this.getGame().isUncontrollable(pred)) {
					pending.add(new StrategyState<State, Integer>(pred, 1));
				} else {
					for (int i = 1; i <= this.getGame().getGoal().getGuaranteesQuantity(); i++) {
						// This check may not be required. If I add the state to
						// the queue then I'll recompute the best.
						// Essentially, pred does not need to be updated we'll
						// skip it in the main loop.
						StrategyState<State, Integer> predecessor = new StrategyState<State, Integer>(pred, i);
						if (this.needsToBeUpdated(predecessor)) {
							addIfNotIn(pending, predecessor);
						}
					}
				}
			} else {
				StrategyState<State, Integer> predecessor = new StrategyState<State, Integer>(pred, guaranteeId);
				if (this.needsToBeUpdated(predecessor)) {
					addIfNotIn(pending, predecessor);

				}
			}
		}
	}

	protected void initialise(Queue<StrategyState<State, Integer>> pending) {
		initializeEndingStates(pending);
		initializeStates(pending);
	}

	//TODO:check states initalization
	protected void initializeStates(Queue<StrategyState<State, Integer>> pending) {
		Assume<State> firstAssumption = this.getGame().getGoal().getAssumption(1);
		for (State state : this.getGame().getStates()) {
			for (int i = 1; i <= this.getGame().getGoal().getGuaranteesQuantity(); i++) {
				if (!this.getGame().getGoal().getGuarantee(i).contains(state) && !this.getGame().getGoal().getFailures().contains(state)
						&& firstAssumption.contains(state)) {
					pending.add(new StrategyState<State, Integer>(state, i));
				}
			}
		}
	}

	protected void initializeEndingStates(Queue<StrategyState<State, Integer>> pending) {
		// Set the rank of every state that is a dead end to infinity (i.e
		// safety goals included)
		// Add it's predecessors (if necessary) to the pending states
		for (State state : this.getGame().getStates()) {
			if (this.getGame().getControllableSuccessors(state).isEmpty() && this.getGame().getUncontrollableSuccessors(state).isEmpty()) {
				for (int guaranteeId = 1; guaranteeId <= this.getGame().getGoal().getGuaranteesQuantity(); guaranteeId++) {
					StrategyState<State, Integer> strategyState = new StrategyState<State, Integer>(state, guaranteeId);
					setInfinity(strategyState);
					deadEnds.add(state);
				}
				StrategyState<State, Integer> firstGuaranteeRank = new StrategyState<State, Integer>(state, 1);
				//Infinity has just been set. 
				Rank infinity = this.getRankSystem().getRank(firstGuaranteeRank);
				this.addPredecessorsTo(pending, firstGuaranteeRank, infinity);
			}
		}
	}

	/*
	 * Returns the best possible rank value for state according to guaranteeId.
	 */
	@Override
	protected Rank best(StrategyState<State, Integer> strategyState) {
		// Different ranks have different infinity values.
		State state = strategyState.getState();
		Integer guarantee = strategyState.getMemory();

		// TODO: getNExtGuarantee should get a strategy state?
		GRRank bestRank = this.getBestFromSuccessors(state, guarantee);

		if (getGRGoal().getGuarantee(guarantee).contains(state)
				|| getGRGoal().getFailures().contains(state)) {
			GRRankContext initialGuaranteeContext = this.getRankSystem()
					.getContext(strategyState);

			// In this case, the infinity value of bestRank and returnRank may
			// be different.
			// We set returnRank to the desired return value and return it
			// (either (0,1) or infinity)
			if (bestRank.isInfinity()) {
				// for this guaranteeId the rank is infinity
				return GRRank.getInfinityFor(initialGuaranteeContext);
			} else {
				// for this guaranteeId the rank is "zero"
				return new GRRank(initialGuaranteeContext);
			}
		}if (getGRGoal().getAssumption(bestRank.getAssume()).contains(
				state)) {
			bestRank.increase();
		}

		return bestRank;
	}

	private GRRank getBestFromSuccessors(State state, Integer guarantee) {
		int nextGuarantee = this.getNextGuarantee(guarantee, state);
		if (getGame().isUncontrollable(state)) {
			// there is no assumption about the environment
			return this.getRankSystem().getMin(
					GRUtils.getStrategyStatesFrom(nextGuarantee, this.getGame()
							.getUncontrollableSuccessors(state)));
		} else {
			//TODO: check this, we are avoiding dead ends in the controllable succesors computation
			Set<State> controllableSuccesors = this.getGame().getControllableSuccessors(state);
			/*
			SetView<State> nonDeadSuccessors = Sets.difference(controllableSuccesors, deadEnds);
			return this.getRankSystem().getMax(
					GRUtils.getStrategyStatesFrom(nextGuarantee, nonDeadSuccessors));
					*/
			return this.getRankSystem().getMax(
					GRUtils.getStrategyStatesFrom(nextGuarantee, controllableSuccesors));
		}
	}

	protected void setInfinity(StrategyState<State, Integer> strategyState) {
		GRRank infinity = GRRank.getInfinityFor(this.getRankSystem().getContext(strategyState));
		this.getRankSystem().set(strategyState, infinity);
	}

	@Override
	public GRGame<State> getGame() {
		return this.game;
	}

}
