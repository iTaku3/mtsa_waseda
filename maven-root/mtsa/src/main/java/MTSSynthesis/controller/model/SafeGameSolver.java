package MTSSynthesis.controller.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import MTSSynthesis.controller.gr.StrategyState;
import MTSSynthesis.controller.safe.SafeGame;

import MTSTools.ac.ic.doc.mtstools.model.LTS;


public class SafeGameSolver<S, A> implements GameSolver<S,Integer> {

	protected static int DUMMY_GOAL = 1;
	
	private SafeGame<S, A> game;
	private Set<S> losingStates;
	private boolean gameSolved;

	public SafeGameSolver(LTS<S, A> env, Set<A> controllable){
		game = new SafeGame<S,A>(env, controllable);
		losingStates = new HashSet<S>();
		gameSolved = false;
	}
	
	public Game<S> getGame(){
		return game;
	}
	
	public void solveGame() {
		// Initialize 
		Queue<S> losing = new LinkedList<S>();

		this.initialise(losing);

		// Handle the pending states
		while (!losing.isEmpty()) {
			S state = losing.poll();

			if (losingStates.contains(state)) {
				break;
			}

			losingStates.add(state);			
			
			Set<S> predecessors = this.game.getPredecessors(state);
			for (S pred : predecessors) {
				//a state will be losing if it has an uncontrollable losing succesor
				//TODO:check this logic try to simplify (make it more clear)
				boolean atLeastOneUncontrollableLosing = false;
				boolean allUncontrollableWinning = false;
				if (game.isUncontrollable(pred)) {
					for (S succ: this.game.getUncontrollableSuccessors(pred)) {
						if (losingStates.contains(succ)) {
							atLeastOneUncontrollableLosing = true;
							losing.add(pred);
							break;
						}						
					}
					allUncontrollableWinning = true;
				} 
				//it will also be losing if it has controllable succesors, but none is non losing
				if(!allUncontrollableWinning && !atLeastOneUncontrollableLosing && this.game.getControllableSuccessors(pred).size() > 0){
					boolean atLeastOne = false;
					for (S succ: this.game.getControllableSuccessors(pred)) {
						if (!losingStates.contains(succ)) {
							atLeastOne = true;
						}
					}
					if (!atLeastOne) {
						losing.add(pred);
					}
				}
			}
		}
		this.gameSolved = true;
	}

	private void initialise(Collection<S> pending) {
		for (S state : this.game.getStates()) {
			if (this.game.getControllableSuccessors(state).isEmpty() &&
					this.game.getUncontrollableSuccessors(state).isEmpty()) {
				pending.add(state);	
			}
		}
	}

	public Set<S> getWinningStates() {
		Set<S> winning = new HashSet<S>();
		if (!gameSolved) {
			this.solveGame();
		}
		for (S state : this.game.getStates()) {
			if (!this.losingStates.contains(state)) {
				winning.add(state);
			}
		}
		return winning;
	}

	public boolean isWinning(S state) {
		if (!gameSolved) {
			this.solveGame();
		}
		return !this.losingStates.contains(state);
	}

	public Strategy<S, Integer> buildStrategy() {
		Strategy<S, Integer> result = new Strategy<S, Integer>();

		Set<S> winningStates = this.getWinningStates();
		for (S state : winningStates) {
			StrategyState<S,Integer> source = new StrategyState<S,Integer>(state, DUMMY_GOAL);
			Set<StrategyState<S,Integer>> successors = new HashSet<StrategyState<S,Integer>>();
				if (this.game.isUncontrollable(state)) {
					for (S succ : this.game.getUncontrollableSuccessors(state)) {
						StrategyState<S,Integer> target = new StrategyState<S,Integer>(succ, DUMMY_GOAL);
						successors.add(target);
					}
					for (S succ : this.game.getControllableSuccessors(state)) {
						if (!this.losingStates.contains(succ)) {
							StrategyState<S,Integer> target = new StrategyState<S,Integer>(succ, DUMMY_GOAL);
							successors.add(target);
						}
					}
				} else { // Controllable State
					for (S succ : this.game.getControllableSuccessors(state)) {
						if (!this.losingStates.contains(succ)) {
							StrategyState<S,Integer> target = new StrategyState<S,Integer>(succ, DUMMY_GOAL);
							successors.add(target);
						}
					}
				}
				result.addSuccessors(source, successors);
		}
		return result;
	}

}
