package MTSSynthesis.controller.gr.knowledge;

import static MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType.POSSIBLE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import MTSSynthesis.controller.model.Assume;
import MTSSynthesis.controller.model.Game;
import MTSSynthesis.controller.model.Rank;
import MTSSynthesis.controller.model.Strategy;
import org.apache.commons.lang.Validate;

import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSSynthesis.controller.gr.GRRank;
import MTSSynthesis.controller.gr.GRRankContext;
import MTSSynthesis.controller.gr.GRRankSystem;
import MTSSynthesis.controller.gr.StrategyState;
import MTSSynthesis.controller.model.RankBasedGameSolver;
import MTSSynthesis.controller.model.gr.GRGoal;

//TODO Inherit from GRGameSolver?
public class KnowledgeGRGameSolver<S,A> extends RankBasedGameSolver<Set<S>, Integer> {

	private KnowledgeGRGame<S, A> game;
	private GRRankSystem<Set<S>> rankSystem;
	private Set<Pair<StrategyState<Set<S>,Integer>,StrategyState<Set<S>,Integer>>> worseRank = new HashSet<Pair<StrategyState<Set<S>,Integer>,StrategyState<Set<S>,Integer>>>();
	
	public KnowledgeGRGameSolver(KnowledgeGRGame<S, A> game, GRRankSystem<Set<S>> rankSystem) {
		this.setGame(game);
		this.setRankSystem(rankSystem);
	}
	
	private GRRank getBestFromSuccessors(Set<S> state, int guaranteeId) {
		//TODO needs refactor. 

		final int nextGuarantee = this.getNextGuarantee(guaranteeId, state);
		
		StrategyState<Set<S>, Integer> pair = new StrategyState<Set<S>,Integer>(state, nextGuarantee);
		GRRankContext context = getRankSystem().getContext(pair);
		GRRank bestRank = new GRRank(context);
		Set<A> candidateActionSet = new HashSet<A>();
		

//		SortedSet<Pair<A, Set<S>>> toSortActionSet = new TreeSet<Pair<A, Set<S>>>(getRankComparator(nextGuarantee)); 
		List<Pair<A, Set<S>>> toSortActionSet = new ArrayList<Pair<A, Set<S>>>();
		
		// Compute the max rank of an uncontrollable successor
		for (Set<S> succ : getGame().getUncontrollableSuccessors(state)) {
			GRRank succRank = getRankSystem().getRank(new StrategyState<Set<S>,Integer>(succ, nextGuarantee));
			if (bestRank.compareTo(succRank) < 0) {
				bestRank.set(succRank);
//				throw new RuntimeException("BUG!");
//				bestRank = succRank; 
			}
		}
		
		
		
		candidateActionSet.addAll(getKnowdlegeGRGame().getUncontrollable());
		
		BinaryRelation<A, Set<S>> trs = getKnowdlegeGRGame().getDetMTS().getTransitions(state, POSSIBLE);
		for (Pair<A, Set<S>> tr : trs) {
			if (getKnowdlegeGRGame().getControllable().contains(tr.getFirst())) {
				GRRank rank = getRankSystem().getRank(new StrategyState<Set<S>,Integer>(tr.getSecond(),nextGuarantee));
				if (bestRank.compareTo(rank)>=0) {
					candidateActionSet.add(tr.getFirst());
				} else {
					toSortActionSet.add(tr);
				}
			}
		}
		
		Collections.sort(toSortActionSet, getRankComparator(nextGuarantee));
		
		if (getKnowdlegeGRGame().isEnabled(state, candidateActionSet)) {
			return bestRank;
		}
		else {
			for (Pair<A, Set<S>> tr : toSortActionSet) {
				A action = tr.getFirst();
				candidateActionSet.add(action);
				Set<Set<S>> succState = getKnowdlegeGRGame().getDetMTS().getTransitions(state, POSSIBLE).getImage(action);
				GRRank succRank = getRankSystem().getRank(new StrategyState<Set<S>,Integer>(succState.iterator().next(), nextGuarantee));
				bestRank.set(succRank);
//				bestRank = succRank;  //BUG!!!!
				if (getKnowdlegeGRGame().isEnabled(state,candidateActionSet)) {
					return bestRank;
				}
			}
		}
		
		//This code is not reachable since reaching this point would mean that <i>state</i> is a dead end. 
		GRRankContext nextContext = getRankSystem().getContext(new StrategyState<Set<S>,Integer>(state, nextGuarantee));
		return GRRank.getInfinityFor(nextContext);
	}

	private Comparator<Pair<A, Set<S>>> getRankComparator(final int nextGuarantee) {
		Comparator<Pair<A, Set<S>>> rankComparator = new Comparator<Pair<A, Set<S>>>() {
			@Override
			public int compare(Pair<A, Set<S>> tr1, Pair<A, Set<S>> tr2) {
				StrategyState<Set<S>, Integer> pair1 = new StrategyState<Set<S>, Integer>(tr1.getSecond(), nextGuarantee);
				StrategyState<Set<S>, Integer> pair2 = new StrategyState<Set<S>, Integer>(tr2.getSecond(), nextGuarantee);
				GRRank rank1 = getRankSystem().getRank(pair1);
				GRRank rank2 = getRankSystem().getRank(pair2);
				return rank1.compareTo(rank2);
			}
		};
		return rankComparator;
	}
	
	
	// All following methods are copied verbatim from GRGameSolver!

	protected void addIfNotIn(Collection<StrategyState<Set<S>, Integer>> pending,
			StrategyState<Set<S>, Integer> state) {
		if (!pending.contains(state)) {
			pending.add(state);
		}
	}

	protected boolean needsToBeUpdated(StrategyState<Set<S>, Integer> predecesorStrategyState) {

		Rank best = this.best(predecesorStrategyState);
		Rank rank = this.getRankSystem().getRank(predecesorStrategyState);
		return best.compareTo(rank) > 0;
	}

	@Override
	protected void addPredecessorsTo(Queue<StrategyState<Set<S>, Integer>> pending, 
									StrategyState<Set<S>, Integer> strategyState, Rank bestRank) {
		Set<S> state = strategyState.getState();
		int guaranteeId = strategyState.getMemory();
	
		Set<Set<S>> predecessors = this.getGame().getPredecessors(state);
		for (Set<S> pred : predecessors) {
	
			if (bestRank.isInfinity()) {
				for (int i = 1; i <= this.getKnowdlegeGRGame().getGoal().getGuaranteesQuantity(); i++) {
					StrategyState<Set<S>, Integer> predecessor = new StrategyState<Set<S>, Integer>(pred, i);
					addIfNotIn(pending, predecessor);
				}
			} else {
				StrategyState<Set<S>, Integer> predecessor = new StrategyState<Set<S>, Integer>(pred, guaranteeId);
				addIfNotIn(pending, predecessor);
			}
		}
	}
	

	
	private int getNextGuarantee(int guaranteeId, Set<S> state) {
		if (getGRGoal().getGuarantee(guaranteeId).contains(state) ||
				getGRGoal().getFailures().contains(state)) {
			return this.increaseGuarantee(guaranteeId);
		} else {
			return guaranteeId;
		}
	}


	// Copied verbatim from GRGameSolver
	@Override
	protected void initialise(Queue<StrategyState<Set<S>, Integer>> pending) {
		
		// Set the rank of every state that is a dead end to infinity (i.e safety goals included)
		// Add it's predecessors (if necessary) to the pending states
		for (Set<S> state : this.getGame().getStates()) {
			if (this.getGame().getControllableSuccessors(state).isEmpty() &&
					this.getGame().getUncontrollableSuccessors(state).isEmpty()) {
				for (int guaranteeId = 1 ; guaranteeId <= this.getKnowdlegeGRGame().getGoal().getGuaranteesQuantity() ; guaranteeId++) {
					StrategyState<Set<S>, Integer> strategyState = new StrategyState<Set<S>, Integer>(state, guaranteeId);
					GRRank infinity = GRRank.getInfinityFor(this.getRankSystem().getContext(strategyState)); 
					this.getRankSystem().set(strategyState, infinity);
				}
				StrategyState<Set<S>, Integer> firstGuaranteeRank = new StrategyState<Set<S>,Integer>(state, 1);
				Rank infinity = this.getRankSystem().getRank(firstGuaranteeRank);
				this.addPredecessorsTo(pending, firstGuaranteeRank, infinity);					
			}
		}
	
		Assume<Set<S>> firstAssumption = this.getKnowdlegeGRGame().getGoal().getAssumption(1);
		for (Set<S> state : this.getGame().getStates()) {
			for (int i = 1; i <= this.getKnowdlegeGRGame().getGoal().getGuaranteesQuantity(); i++) {
				if (!this.getKnowdlegeGRGame().getGoal().getGuarantee(i).contains(state) &&
						!this.getKnowdlegeGRGame().getGoal().getFailures().contains(state) &&
						firstAssumption.contains(state)) {
			
					pending.add(new StrategyState<Set<S>,Integer>(state, i));
				}
			}
		}
	}

	
	@Override
	public boolean isWinning(Set<S> state) {
		if (!isGameSolved()) {
			this.solveGame();
		}
		return !this.getRankSystem().getRank(new StrategyState<Set<S>, Integer>(state, 1)).isInfinity();
	}

	@Override
	public Strategy<Set<S>,Integer> buildStrategy() {
		this.solveGame();
		
		Strategy<Set<S>,Integer> result = new Strategy<Set<S>,Integer>();
		
		Set<Set<S>> winningStates = this.getWinningStates();

		for (Set<S> state : winningStates) {
			for (int guaranteeId = 1 ; guaranteeId <= getGRGoal().getGuaranteesQuantity() ; guaranteeId++) {
				StrategyState<Set<S>,Integer> source = new StrategyState<Set<S>,Integer>(state, guaranteeId);
				
				int nextGuaranteeId = this.getNextGuaranteeStrategy(guaranteeId, state);
				
				//If either a guarantee or a failure was just visited then it is ok for the successor of state to have higher rank.
				boolean rankMayIncrease = getGRGoal().getGuarantee(guaranteeId).contains(state) || getGRGoal().getFailures().contains(state);
				
				Set<StrategyState<Set<S>,Integer>> successors = new HashSet<StrategyState<Set<S>,Integer>>();
				for (Set<S> succ : this.getGame().getUncontrollableSuccessors(state)) {
					Validate.isTrue(this.isWinning(succ), "state: " + succ + " it's not winning.");
					Validate.isTrue(this.isBetterThan(source,new StrategyState<Set<S>,Integer>(succ, nextGuaranteeId),rankMayIncrease), "Set<S>: " + succ + " must have a better rank than state: " + state);
					StrategyState<Set<S>,Integer> target = new StrategyState<Set<S>,Integer>(succ, nextGuaranteeId);
					successors.add(target);
				}
				
				for (Set<S> succ : this.getGame().getControllableSuccessors(state)) {
					if (this.isBetterThan(source,new StrategyState<Set<S>,Integer>(succ, nextGuaranteeId), rankMayIncrease)) {
						Validate.isTrue(this.isWinning(succ), "state: " + succ + " it's not winning.");
						StrategyState<Set<S>,Integer> target = new StrategyState<Set<S>,Integer>(succ, nextGuaranteeId);
						successors.add(target);
					} 
					
					else if (getGRGoal().buildPermissiveStrategy() && this.isWinning(succ)) {
						StrategyState<Set<S>,Integer> target = new StrategyState<Set<S>, Integer>(succ, nextGuaranteeId);
						successors.add(target);		
						this.getWorseRank().add(Pair.create(source, target));
					}
				}

				Validate.notEmpty(successors, "\n State:" + source + " should have at least one successor.");
				result.addSuccessors(source, successors);

			}
		}

		return result;
	}

	@Override
	protected void updateRank(StrategyState<Set<S>, Integer> strategyState, Rank bestRank) {
		Set<S> state = strategyState.getState();
		
		if (bestRank.isInfinity()) {
			//Sets infinite rank for state for all guarantees.
			for (int i = 1; i <= getGRGoal().getGuaranteesQuantity(); i++) {
				GRRank infinityFor = GRRank.getInfinityFor(this.getRankSystem().getContext(new StrategyState<Set<S>,Integer>(state, i)));
				this.getRankSystem().set(new StrategyState<Set<S>,Integer>(state, i), infinityFor);
			}
		} else {
			this.getRankSystem().set(strategyState, bestRank);
		}
	}

	@Override
	protected Rank best(StrategyState<Set<S>, Integer> strategyState) {
		// Different ranks have different infinity values. 
		Set<S> state = strategyState.getState();
		Integer guarantee = strategyState.getMemory();
		
		GRRank bestRank = this.getBestFromSuccessors(state, guarantee);

		//TODO needs refactor. copied from grgamesolver
		
		if (getGRGoal().getGuarantee(guarantee).contains(state) ||
				getGRGoal().getFailures().contains(state)) {
			GRRankContext initialGuaranteeContext = this.getRankSystem().getContext(strategyState);
			
			// In this case, the infinity value of bestRank and returnRank may be different.
			// We set returnRank to the desired return value and return it (either (0,1) or infinity)
			if (bestRank.isInfinity()) {
				//for this guaranteeId the rank is infinity
				return GRRank.getInfinityFor(initialGuaranteeContext);  
			} else {
				//for this guaranteeId the rank is "zero"
				return new GRRank(initialGuaranteeContext); 
			}
		} else if (getGRGoal().getAssumption(bestRank.getAssume()).contains(state)) {
			bestRank.increase(); 
		} 

		return bestRank;
	}

	@Override
	protected Rank getRank(StrategyState<Set<S>, Integer> strategyState) {
		return this.getRankSystem().getRank(strategyState);	
	}

	@Override
	protected Set<Set<S>> getGameStates() {
		return this.getGame().getStates();
	}
	
	public Game<Set<S>> getGame() {
		return game;
	}
	
	private KnowledgeGRGame<S, A> getKnowdlegeGRGame() {
		return game;
	}
	
	private void setGame(KnowledgeGRGame<S, A> game) {
		this.game = game;
	}
	
	private GRRankSystem<Set<S>> getRankSystem() {
		return rankSystem;
	}
	
	private void setRankSystem(GRRankSystem<Set<S>> rankSystem) {
		this.rankSystem = rankSystem;
	}

	private GRGoal<Set<S>> getGRGoal() {
		return game.getGoal();
	}
	
	private int increaseGuarantee(int guaranteeId) {
		return (guaranteeId % getGRGoal().getGuaranteesQuantity()) + 1;
	}
	
	private int getNextGuaranteeStrategy(int guaranteeId, Set<S> state) {
		if (getGRGoal().getGuarantee(guaranteeId).contains(state)) {
			return this.increaseGuarantee(guaranteeId);
		} else {
			return guaranteeId;
		}
	}

	public boolean isBetterThan(StrategyState<Set<S>, Integer> state, StrategyState<Set<S>, Integer> succ, boolean mayIncrease) {

		Rank succRank = this.getRankSystem().getRank(succ);
		GRRank stateRank = this.getRankSystem().getRank(state);

		boolean isInAssumption = getGRGoal().getAssumption(stateRank.getAssume()).contains(state.getState());

		return ( (mayIncrease && !succRank.isInfinity()) ||

		    (!mayIncrease &&
		   	((stateRank.compareTo(succRank) > 0) || (stateRank.compareTo(succRank) == 0 && !isInAssumption)))); 

		}
	
	public Set<Pair<StrategyState<Set<S>,Integer>,StrategyState<Set<S>,Integer>>> getWorseRank() {
		return worseRank;
	}
}
