package MTSSynthesis.controller.gb;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import MTSSynthesis.controller.gr.StrategyState;
import org.apache.commons.lang.Validate;

import MTSSynthesis.controller.model.Assume;
import MTSSynthesis.controller.model.Game;
import MTSSynthesis.controller.model.GameSolver;
import MTSSynthesis.controller.model.Guarantee;
import MTSSynthesis.controller.model.Strategy;
import MTSSynthesis.controller.model.gr.GRGame;

/**
 *  For the computations of successors and predecessors:
 A state s is uncontrollable if it has some uncontrollable action enabled (at least one)
  A state s is controllable if ALL the enabled actions are controllable
 Consider a transition s --a--> t
 t is a successor of s if s is uncontrollable and a is uncontrollable
                     or if s is controllable (and then obviously a is controllable)
 s is a predecessor of t if s is uncontrollable and a is uncontrollable
 *                   or if s is controllable (and then obviously a is controllable)  
 */
public class GBGameSolver<S>  implements GameSolver<S,Integer> {
	private GRGame<S> game;
	private GBRankSystem<S> rankSystem;
	private boolean gameSolved;
	
	public GBGameSolver(GRGame<S> game, GBRankSystem<S> rankSystem) {
		this.game = game;
		this.rankSystem = rankSystem;
	}
	
	public Game<S> getGame(){
		return game;
	}
	
	/* (non-Javadoc)
	 * @see controller.GameSolver#solveGame()
	 */
	public void solveGame() {

		Queue<StrategyState<S,Integer>> pending = new LinkedList<StrategyState<S, Integer>>();

		this.initialise(pending);

		// Handle the pending states
		while (!pending.isEmpty()) {

			StrategyState<S, Integer> pair = pending.poll();
			S state = pair.getState();
			Integer guaranteeId = pair.getMemory();

			// The current rank of the state s
			GBRank rank = this.rankSystem.getRank(state, guaranteeId);

			// If current rank is already infinity, it obviously should
			// not be increased.
			if (rank.isInfinity()) {
				continue;
			}

			// What is the best possible ranking that s could have according to
			// it's successors?
			GBRank bestRank = this.best(state, guaranteeId);

			// The existing ranking is already higher or equal then nothing needs to be
			// done. Go to the next state in the set of pending
			if (bestRank.compareTo(rank) <= 0) {
				continue;
			}

			// set the new ranking of the state to the computed best value
			// If the new value is infinity it can be set for all rankings.
			if (bestRank.isInfinity()) {
				for (int i = 1; i <= this.game.getGoal().getGuaranteesQuantity(); i++) {
					this.rankSystem.set(state, i, GBRank.getInfinityFor(this.rankSystem.getContext(i)));
				}
			} else {
				this.rankSystem.set(state, guaranteeId, bestRank);
			}

			addPredecessorsTo(pending, state, guaranteeId, bestRank);
		}
		this.gameSolved = true;
	}
	
	/*
	 * Handle predecessors. 
	 * There is a more efficient implementation 
	 * of this part by storing the best values 
	 * and not recomputing them again and again 
	 * whenever we meet a state. 
	 * This is currently ignored.
	 */
	private void addPredecessorsTo(Collection<StrategyState<S, Integer>> pending,
			S state, int guaranteeId, GBRank bestRank) {

		Set<S> predecessors = this.game.getPredecessors(state);
		for (S pred : predecessors) {

			if (bestRank.isInfinity()) {
				if (this.game.isUncontrollable(pred)) {
					pending.add(new StrategyState<S, Integer>(pred, 1));
				} else {
					for (int i = 1; i <= this.game.getGoal().getGuaranteesQuantity(); i++) {
						if (this.needsToBeUpdated(i, pred)) {
							addIfNotIn(pending, pred, i);
						}
					}
							}
			} else if (this.needsToBeUpdated(guaranteeId, pred)) { 
				addIfNotIn(pending, pred, guaranteeId);

			}
		}
	}
	private void addIfNotIn(Collection<StrategyState<S, Integer>> pending,
			S state, int guaranteeId) {
		StrategyState<S, Integer> newRankedState = new StrategyState<S, Integer>(state, guaranteeId);
		if (!pending.contains(newRankedState)) {
			pending.add(newRankedState);
		}
	}

	private boolean needsToBeUpdated(int guaranteeId, S pred) {
		GBRank best = this.best(pred, guaranteeId);
		GBRank rank = this.rankSystem.getRank(pred, guaranteeId);
		return best.compareTo(rank) > 0;
	}

	// Increase the rank of dead ends to infinity.
	// Add their contoled predecessors to pending.
	// Add states that are not in guarantee and in assumption[1] to pending
	private void initialise(Collection<StrategyState<S, Integer>> pending) {
		// Set the rank of every state that is a dead end to infinity (i.e safety goals included)
		// Add it's predecessors (if necessary) to the pending states
		for (S state : this.game.getStates()) {
			if (this.game.getControllableSuccessors(state).isEmpty() &&
					this.game.getUncontrollableSuccessors(state).isEmpty()) {
				for (int guaranteeId = 1 ; guaranteeId <= this.game.getGoal().getGuaranteesQuantity() ; guaranteeId++) {
					GBRank infinity = GBRank.getInfinityFor(this.rankSystem.getContext(guaranteeId)); 
					this.rankSystem.set(state, guaranteeId, infinity);
				}
				GBRank infinity = this.rankSystem.getRank(state, 1);
				this.addPredecessorsTo(pending, state, 1, infinity);					
			}
		}

		Assume<S> firstAssumption = this.game.getGoal().getAssumption(1);
		for (S state : this.game.getStates()) {
			for (int i = 1; i <= this.game.getGoal().getGuaranteesQuantity(); i++) {
				if (!this.game.getGoal().getGuarantee(i).contains(state)
						&& firstAssumption.contains(state)) {
					pending.add(new StrategyState<S, Integer>(state, i));
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see controller.GameSolver#getWinningStates()
	 */
	public Set<S> getWinningStates() {
		Set<S> winning = new HashSet<S>();
		if (!gameSolved) {
			this.solveGame();
		}
		for (S state : this.game.getStates()) {
			boolean infinity = this.rankSystem.getRank(state, 1).isInfinity();
			if (!infinity) {
				winning.add(state);
			}
		}
		return winning;
	}

	/* (non-Javadoc)
	 * @see controller.GameSolver#isWinning(State)
	 */
	public boolean isWinning(S state) {
		if (!gameSolved) {
			this.solveGame();
		}
		return !this.rankSystem.getRank(state, 1).isInfinity();
	}
	
	public boolean isBetterThan(S state, S succ, int guaranteeId) {
		Guarantee<S> guarantee = this.game.getGoal().getGuarantee(guaranteeId);
		boolean stateInGuarantee = guarantee.contains(state);
		
		GBRank succRank = this.rankSystem.getRank(succ, this.getNextGuarantee(guaranteeId,state));
		GBRank stateRank = this.rankSystem.getRank(state, guaranteeId);
		int actualStateVSsuccessor = stateRank.compareTo(succRank);
		
		return ( (stateInGuarantee && !succRank.isInfinity()) ||
			     (!stateInGuarantee && actualStateVSsuccessor > 0));
	}

	private int getNextGuarantee(int guaranteeId, S state) {
		if (this.game.getGoal().getGuarantee(guaranteeId).contains(state)) {
			return this.increaseGuarantee(guaranteeId);
		}
		else
			return guaranteeId;
	}
	
	private int increaseGuarantee(int guaranteeId) {
		return (guaranteeId % this.game.getGoal().getGuaranteesQuantity()) + 1;
	}

	/* (non-Javadoc)
	 * @see controller.GameSolver#buildStrategy()
	 */
	public Strategy<S, Integer> buildStrategy() {
		if (!gameSolved) {
			this.solveGame();
		}
		
		Strategy<S, Integer> result = new Strategy<S, Integer>();
		
		Set<S> winningStates = this.getWinningStates();
		for (S state : winningStates) {
			for (int guaranteeId = 1 ; guaranteeId <= this.game.getGoal().getGuaranteesQuantity() ; guaranteeId++) {
				StrategyState<S,Integer> source = new StrategyState<S,Integer>(state, guaranteeId);
				
				int nextGuaranteeId = this.getNextGuarantee(guaranteeId, state);
				
				Set<StrategyState<S,Integer>> successors = new HashSet<StrategyState<S,Integer> >();
				if (this.game.isUncontrollable(state)) {
					for (S succ : this.game.getUncontrollableSuccessors(state)) {
						Validate.isTrue(this.isBetterThan(state,succ,guaranteeId), "State: " + succ + " must have a better rank than state: " + state);
						StrategyState<S,Integer>  target = new StrategyState<S,Integer>(succ, nextGuaranteeId);
						successors.add(target);
					}

					for (S succ : this.game.getControllableSuccessors(state)) {
						if (this.isBetterThan(state,succ,guaranteeId)) {
							StrategyState<S,Integer> target = new StrategyState<S,Integer>(succ, nextGuaranteeId);
							successors.add(target);
							Validate.isTrue(this.isWinning(state), "is state: " + state + " winning? 5");
							Validate.isTrue(this.isWinning(succ), "state: " + succ + " it's not winning. 2");
						}
					}
				} else { // Controllable State
					boolean atLeastOne = false;
					for (S succ : this.game.getControllableSuccessors(state)) {
						if (this.isBetterThan(state,succ,guaranteeId)) {
							StrategyState<S,Integer>  target = new StrategyState<S,Integer>(succ, nextGuaranteeId);
							successors.add(target);
							Validate.isTrue(this.isWinning(succ), "state: " + succ + " it's not winning. 3");
							atLeastOne = true;
						}
					}
					Validate.isTrue(atLeastOne, "\n State:" + source + " must have at least one successor.");
				}
				Validate.notEmpty(successors, "\n State:" + source + " should have at least one successor.");
				result.addSuccessors(source, successors);
			}
		}

		return result;
	}
	
	
	/**
	 * (Main ...) 
	 * What is the best possible value that the rank of a state can be?
	 */
	private GBRank best(S state, int guaranteeId) {

		GBRank bestRank = this.getBestFromSuccessors(state, guaranteeId);


		if (this.game.getGoal().getGuarantee(guaranteeId).contains(state)) {
			GBRankContext initialGuaranteeContext = this.rankSystem.getContext(guaranteeId);
			
			// In this case, the infinity value of bestRank and returnRank may be different.
			// We set returnRank to the desired return value and return it (either (0,1) or infinity)
			if (bestRank.isInfinity()) {
				//for this guaranteeId the rank is infinity
				return GBRank.getInfinityFor(initialGuaranteeContext);  
			} else {
				//for this guaranteeId the rank is "zero"
				return new GBRank(initialGuaranteeContext); 
			}
		} else {
			bestRank.increase(); 
		} 

		return bestRank;
	}

	private GBRank getBestFromSuccessors(S state, int guaranteeId) {
		GBRank bestRank;
		//if state belongs to guarantee i then the best rank for it 
		//should based on the rank for the next guarantee 
		//DIPI: Don't Understand. If nextGuarantee is the same as guaranteeId, 
		//why we compute the min/max 
		// of the successors for the same guarantee? the state it's already in the guarantee.
		
		//Nir
		// If the state is in the guarantee, then next guarantee should be what's written below.
		// If the state is not in the guarantee, then the next guarantee should be the same guaranteeId.
		// In addition,
		// In an uncontrollable state the best we can expect is worst, so we are going to get the max.
		// In a controllable state, the best we can expect is our choice, so we are going to get the min.
		
		int nextGuarantee = this.getNextGuarantee(guaranteeId, state);
		if (game.isUncontrollable(state)) {
			//there is no assumption about the environment
			bestRank = this.rankSystem.
				getMaximum(nextGuarantee, this.game.getUncontrollableSuccessors(state));
		} else {
			bestRank = this.rankSystem.
				getMinimum(nextGuarantee, this.game.getControllableSuccessors(state));
		}
		return bestRank;
	}

}
