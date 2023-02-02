package MTSSynthesis.controller.gr.concurrency;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import MTSSynthesis.controller.model.Game;
import MTSSynthesis.controller.model.Rank;
import MTSSynthesis.controller.model.Strategy;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSSynthesis.controller.gr.StrategyState;
import MTSSynthesis.controller.model.RankBasedGameSolver;

public abstract class HeuristicPrunningGameSolver<S,A> extends RankBasedGameSolver<S,Integer>{
	
	//This means that you always want to do something else or prefer to relax and wait. 
	private static boolean proactive = true;
	private static boolean bad = false;
	
	Game<S> game;
	DoubleRankFunction<S> function;
	protected LTS<S,A> lts;
	private Strategy<S,Integer> concurrentStrategy;
	private Set<S> finalStates;

	public HeuristicPrunningGameSolver(Game<S> game, LTS<S, A> lts, DoubleRankFunction<S> function, Set<S> finalStates) {
		this.finalStates = finalStates;
		this.lts = lts;
		this.function = function;
		this.game = game;
	}

	@Override
	public Set<S> getWinningStates() {
		Set<S> result = new HashSet<S>();
		if (!isGameSolved()) {
			this.solveGame();
		}
		
		result.addAll(lts.getStates());
		
		return result;
	}

	@Override
	public boolean isWinning(S state) {
		//TODO: Change this to a condition of winning for the concurrency game.
		return lts.getStates().contains(state);
	}

	@Override
	public Game<S> getGame() {
		return game;
	}
	
	@Override
	public Strategy<S, Integer> buildStrategy() {
		if (!isGameSolved())
			this.solveGame();
		if(this.concurrentStrategy==null){
			this.concurrentStrategy = new Strategy<S,Integer>();
			for (S state : lts.getStates()) { 
				Set<S> candidates = new HashSet<S>();
				Set<S> u = new HashSet<S>();
				Set<S> controllables = getGame().getControllableSuccessors(state);
				for (Pair<A, S> succ : lts.getTransitions(state)) {
					if(isWinning(succ.getSecond())){
						if(controllables.contains(succ.getSecond())){
							//Select from the original strategy, the successors that has  
							//more ConcuRank from the Controllable actions. 
							updateBestCandidates(succ.getSecond(), candidates);
						}else{
							//The uncontrollable actions, just happen... so we need to add them either way.
							u.add(succ.getSecond());
						}
					}
				}
				
				if(!proactive && isBetterToRelaxAndWait(u,candidates)){
					Set<StrategyState<S, Integer >> succs = new HashSet<StrategyState<S,Integer>>();
					for (S s : u) {
						succs.add(new StrategyState<S,Integer>(s,1));
					}
					this.concurrentStrategy.addSuccessors(new StrategyState<S,Integer>(state,1), succs);
				}else{
					candidates.addAll(u);
					Set<StrategyState<S, Integer >> succs = new HashSet<StrategyState<S,Integer>>();
					for (S s : candidates) {
						succs.add(new StrategyState<S,Integer>(s,1));
					}
					this.concurrentStrategy.addSuccessors(new StrategyState<S,Integer>(state,1), succs);
				}
			}
		}
		
		return this.concurrentStrategy;
	}
	
	private boolean isBetterToRelaxAndWait(Set<S> u, Set<S> candidates) {
		boolean result = false;
		
		for (S controllableState : candidates) {
			for (S uncontrollableState : u) {
				result = result || isBetterThan(uncontrollableState, controllableState) <= 0;
			}
		}
		
		return result;
	}

	private void updateBestCandidates(S newCandidate, Set<S> bestCandidates) {
		boolean better = bestCandidates.isEmpty();
		Set<S> removableCandidates = new HashSet<S>();
		for (S candidate : bestCandidates) {
			int compare = this.isBetterThan(newCandidate, candidate);
			if(betterCondition(compare)){
				removableCandidates.add(candidate);
				better = true;
			}else if(compare ==0){
				better = true;
			}
		}
		
		bestCandidates.removeAll(removableCandidates);
		
		if(better)
			bestCandidates.add(newCandidate);
	}

	protected abstract boolean betterCondition(int compare);
	
//	{
//		if(strategy.equals(PrunningStrategy.Maximise)){
//			return compare < 0;
//		}else if (strategy.equals(PrunningStrategy.Minimise)){
//			return compare > 0;
//		}else{
//			throw new RuntimeException("Better condition not defined for the Prunning Strategy " + strategy);
//		}
//	}
	
	
	private int isBetterThan(S newCandidate, S candidate) {
		DoubleRank newRank = this.function.getRank(newCandidate);
		DoubleRank oldRank = this.function.getRank(candidate);
		return oldRank.compareTo(newRank);
	}

	@Override
	protected void addPredecessorsTo(Queue<StrategyState<S, Integer>> pending, StrategyState<S, Integer> strategyState, Rank bestRank) {
		for(S pred: lts.getStates()){
			StrategyState<S,Integer> strategyPred = new StrategyState<S,Integer>(pred,1);
			if(isSuccessor(strategyState.getState(), pred) && isWinning(strategyState.getState())
					&& needsToBeUpdated(strategyPred) && !isTheLastGoal(strategyState.getState(), strategyPred.getState())){
				pending.add(new StrategyState<S,Integer>(pred,1));
			}
		}
	}	
	
	
	private boolean isSuccessor(S s, S pred){
		for(Pair<A, S> pair : lts.getTransitions(pred)){
			if(pair.getSecond().equals(s)){
				return true;
			}
		}
		return false;
	}
	
	protected boolean isTheLastGoal(S succ, S state) {
		
		return finalStates.contains(state);
		//game.getInitialStates().contains(succ);
//				boolean looppingFromLastToFirstGoal = succ.getMemory().compareTo(state.getMemory()) < 0;
//				looppingFromLastToFirstGoal || 
//				(getGRGoal().getGuarantee(succ.getMemory()).contains(succ.getState()) 
//						&& succ.getMemory().compareTo(state.getMemory())==0);
		
	}
	

	protected boolean needsToBeUpdated(StrategyState<S, Integer> predecesorStrategyState) {
		Rank best = this.best(predecesorStrategyState);
		Rank rank = this.function.getRank(predecesorStrategyState.getState());
		return best.compareTo(rank) > 0;
	}
	
	protected void updateRank(StrategyState<S, Integer> strategyState, Rank bestRank) {
		this.function.setRank(strategyState.getState(), bestRank);
	}
	
	private Set<S> getSuccessors(S s){
		Set<S> succs = new HashSet<S>();
		for(Pair<A,S> pair : lts.getTransitions(s)){
			succs.add(pair.getSecond());
		}
		return succs;
	}
	
	protected Rank getBestFromSuccessors(S strategyState) {
		//Exist at least one successor... this came from the GR strategy.		
		//We are in the winning region, is like a secure zone, so the best concurrency
		//ranking is just the biggest one. 
		Set<S> strategySuccesors = getSuccessors(strategyState);
		for (S strategySuccesor : strategySuccesors) {
			if(isWinning(strategySuccesor) && !isTheLastGoal(strategySuccesor,strategyState))
				strategySuccesors.add(strategySuccesor);
		}
		
		//If this is the last state for a strategy that satisfies G1...Gn in that order(satisfies Gn)
		//We don't want to make the rank loop infinitely, so this is the stop point.  
		//That's why the best ranking is its own ranking. 
		if(strategySuccesors.isEmpty())
			return this.function.getRank(strategyState);
		
		
		if(getGame().isUncontrollable(strategyState)){
			if(bad)
				return this.function.getAverage(getGame().getUncontrollableSuccessors(strategyState));
			else 
				return this.function.getAverage(strategySuccesors);
		}
		
		return getBest(strategySuccesors);
	}

	protected abstract Rank getBest(Set<S> strategySuccesors);

	@Override
	protected Rank getRank(StrategyState<S, Integer> strategyState) {
		return this.function.getRank(strategyState.getState());
	}
	
	@Override
	protected Set<S> getGameStates() {
		return getGame().getStates();
	}

}
