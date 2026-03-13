package MTSSynthesis.controller.model;

import MTSSynthesis.controller.SimpleLabelledGame;
import MTSSynthesis.controller.gr.StrategyState;
import MTSTools.ac.ic.doc.mtstools.model.LTS;

import java.util.*;

//import gnu.trove.list.linked.TLongLinkedList;
//import gnu.trove.set.hash.TLongHashSet;

public class OppositeSafeGameSolver<S, A> extends
		StateSpaceCuttingGameSolver<S, A> {

	protected static int DUMMY_GOAL = 1;

	protected Set<S> assumptionsStates;
	
	boolean relaxAllControllables;
	boolean relaxOnAssumptions;
	boolean relaxSelfLoops;
	
	//protected TLongHashSet tLosingStates;	
	
	public OppositeSafeGameSolver(LTS<S, A> env, Set<A> controllable,
			Set<Set<S>> goalStates, Set<S> assumptionsStates, boolean relaxAllControllables, boolean relaxOnAssumptions, boolean relaxSelfLoops) {
		super(new SimpleLabelledGame<S, A>(env, controllable),goalStates);
		this.relaxAllControllables	= relaxAllControllables;
		this.assumptionsStates = assumptionsStates;
		this.relaxOnAssumptions = relaxOnAssumptions;
		this.relaxSelfLoops		= relaxSelfLoops;
	}

	@Override
	public LabelledGame<S, A> getLabelledGame() {
		return game;
	}	

	public void solveGame() {
		// Initialize
		//tLosingStates = new TLongHashSet();
		losingStates = new HashSet<S>();
		
		Queue<S> losing = new LinkedList<S>();
		List<Set<S>> uncontrollableSuccessors = new ArrayList<Set<S>>();
		boolean[] isUncontrollable = new boolean[game.getStates().size()+1];
		HashMap<S, Integer> stateToIndex = new HashMap<S, Integer>();
		
		Set<S> currentUncontrollableSuccesors;
		Set<S> currentSet; 
		int i;
		int indexCount = 0;
		for(S state:game.getStates()){
			stateToIndex.put(state, indexCount);
			 //if(i == -1)
				//continue;
			currentUncontrollableSuccesors = ((Set<S>)this.game.getUncontrollableSuccessors(state));
			currentSet = new HashSet<S>();
	
			if(!relaxAllControllables){
				for(S s: currentUncontrollableSuccesors){
					if((!relaxOnAssumptions || (!assumptionsStates.contains(s) || assumptionsStates.contains(state))
							&& (!relaxSelfLoops || (!s.equals(state)))))
	
						currentSet.add(s);
				}
			}
			 uncontrollableSuccessors.add(currentSet);
			 isUncontrollable[indexCount] = currentSet.size() > 0;
			 indexCount+=1;
		}

		losingStates.addAll(game.getStates());
		
		Set<S> currentLosingStates;
		for (Set<S> actualGoalSet : this.goalStates) {
			losing.addAll(actualGoalSet);
			currentLosingStates	= new HashSet<S>();
			
			// Handle the pending states
			while (!losing.isEmpty()) {
				S state = losing.poll();

				
				currentLosingStates.add(state);

				Set<S> predecessors = this.game.getPredecessors(state);
				for (S pred : predecessors) {
					if(losing.contains(pred) || currentLosingStates.contains(pred))
						continue;
					
					i = stateToIndex.get(pred);
					//if(i == -1)
						//continue;
					if (!isUncontrollable[i]) {
						losing.add(pred);
					}else{
						uncontrollableSuccessors.get(i).remove(state);
						if(uncontrollableSuccessors.get(i).isEmpty())
							losing.add(pred);
					}
				}
			}
			
			losingStates.retainAll(currentLosingStates);
			
		}


//		System.out.println("LOSING STATES SIZe : " + losingStates.size());
		
		this.gameSolved = true;
	}	
	
	public void solveGame5() {
		// Initialize
		//tLosingStates = new TLongHashSet();
		losingStates = new HashSet<S>();
		
		Queue<S> losing = new LinkedList<S>();
		List<Set<S>> uncontrollableSuccessors = new ArrayList<Set<S>>();
		boolean[] isUncontrollable = new boolean[game.getStates().size()+1];
		HashMap<S, Integer> stateToIndex = new HashMap<S, Integer>();
		
		Set<S> currentUncontrollableSuccesors;
		Set<S> currentSet; 
		int i;
		int indexCount = 0;
		for(S state:game.getStates()){
			stateToIndex.put(state, indexCount);
			 //if(i == -1)
				//continue;
			currentUncontrollableSuccesors = ((Set<S>)this.game.getUncontrollableSuccessors(state));
			currentSet = new HashSet<S>();
			for(S s: currentUncontrollableSuccesors){
				if((!relaxOnAssumptions || (!assumptionsStates.contains(s) || assumptionsStates.contains(state))
						&& (!relaxSelfLoops || (!s.equals(state)))))
					currentSet.add(s);
			}
			 uncontrollableSuccessors.add(currentSet);
			 isUncontrollable[indexCount] = currentSet.size() > 0;
			 indexCount+=1;
		}

		losingStates.addAll(game.getStates());
		
		Set<S> currentLosingStates;
		for (Set<S> actualGoalSet : this.goalStates) {
			losing.addAll(actualGoalSet);
			currentLosingStates	= new HashSet<S>();
			
			// Handle the pending states
			while (!losing.isEmpty()) {
				S state = losing.poll();

				
				currentLosingStates.add(state);

				Set<S> predecessors = this.game.getPredecessors(state);
				for (S pred : predecessors) {
					if(losing.contains(pred) || currentLosingStates.contains(pred))
						continue;
					
					i = stateToIndex.get(pred);
					//if(i == -1)
						//continue;
					if (!isUncontrollable[i]) {
						losing.add(pred);
					}else{
						uncontrollableSuccessors.get(i).remove(state);
						if(uncontrollableSuccessors.get(i).isEmpty())
							losing.add(pred);
					}
				}
			}
			
			losingStates.retainAll(currentLosingStates);
			
		}


//		System.out.println("LOSING STATES SIZe : " + losingStates.size());
		
		this.gameSolved = true;
	}		
	
	
	public void solveGame4() {
		// Initialize
		//tLosingStates = new TLongHashSet();
		losingStates = new HashSet<S>();
		
		Queue<S> losing = new LinkedList<S>();
		List<Set<S>> uncontrollableSuccessors = new ArrayList<Set<S>>();
		boolean[] isUncontrollable = new boolean[game.getStates().size()+1];
		HashMap<S, Integer> stateToIndex = new HashMap<S, Integer>();
		
		Set<S> currentUncontrollableSuccesors;
		Set<S> currentSet; 
		int i;
		int indexCount = 0;
		for(S state:game.getStates()){
			stateToIndex.put(state, indexCount);
			 //if(i == -1)
				//continue;
			currentUncontrollableSuccesors = ((Set<S>)this.game.getUncontrollableSuccessors(state));
			currentSet = new HashSet<S>();
			for(S s: currentUncontrollableSuccesors){
				if((!relaxOnAssumptions || (!assumptionsStates.contains(s) || assumptionsStates.contains(state))
						&& (!relaxSelfLoops || (!s.equals(state)))))
					currentSet.add(s);
			}
			 uncontrollableSuccessors.add(currentSet);
			 isUncontrollable[indexCount] = currentSet.size() > 0;
			 indexCount+=1;
		}
		
		for (Set<S> actualGoalSet : this.goalStates) {
			losing.addAll(actualGoalSet);
		}

		// Handle the pending states
		while (!losing.isEmpty()) {
			S state = losing.poll();

			
			losingStates.add(state);

			Set<S> predecessors = this.game.getPredecessors(state);
			for (S pred : predecessors) {
				if(losing.contains(pred) || losingStates.contains(pred))
					continue;
				
				i = stateToIndex.get(pred);
				//if(i == -1)
					//continue;
				if (!isUncontrollable[i]) {
					losing.add(pred);
				}else{
					uncontrollableSuccessors.get(i).remove(state);
					if(uncontrollableSuccessors.get(i).isEmpty())
						losing.add(pred);
				}
			}
		}

//		System.out.println("LOSING STATES SIZe : " + losingStates.size());
		
		this.gameSolved = true;
	}		
	
	
	public void solveGame3() {
		// Initialize
		//tLosingStates = new TLongHashSet();
		losingStates = new HashSet<S>();
		Queue<S> losing = new LinkedList<S>();
		int[] uncontrollableCount = new int[game.getStates().size()+1];
		int[] uncontrollableLosingCount = new int[game.getStates().size()+1];
		boolean[] isUncontrollable = new boolean[game.getStates().size()+1];
		HashMap<S, Integer> stateToIndex = new HashMap<S, Integer>();
		
		Set<S> uncontrollableSuccesors;
		int i;
		int indexCount = 0;
		for(S state:game.getStates()){
			stateToIndex.put(state, indexCount);
			 //if(i == -1)
				//continue;
			 uncontrollableSuccesors = ((Set<S>)this.game.getUncontrollableSuccessors(state));

			 uncontrollableCount[indexCount] = uncontrollableSuccesors.size();
			 uncontrollableLosingCount[indexCount] = 0;
			 isUncontrollable[indexCount] = game.isUncontrollable(state);
			 indexCount+=1;
		}
		
		for (Set<S> actualGoalSet : this.goalStates) {
			losing.addAll(actualGoalSet);
		}

		// Handle the pending states
		while (!losing.isEmpty()) {
			S state = losing.poll();

			
			losingStates.add(state);

			Set<S> predecessors = this.game.getPredecessors(state);
			for (S pred : predecessors) {
				if(losing.contains(pred) || losingStates.contains(pred))
					continue;
				
				i = stateToIndex.get(pred);
				//if(i == -1)
					//continue;
				if (!isUncontrollable[i]) {
					losing.add(pred);
				}else{
					uncontrollableLosingCount[i]++;
					if (uncontrollableLosingCount[i] == uncontrollableCount[i])
						losing.add(pred);
				}
			}
		}

//		System.out.println("LOSING STATES SIZe : " + losingStates.size());
		
		this.gameSolved = true;
	}
	
	
	public void solveGame2() {
		// Initialize
		losingStates = new HashSet<S>();
		Queue<S> losing = new LinkedList<S>();
		for (Set<S> actualGoalSet : this.goalStates) {
			losing.addAll(actualGoalSet);
		}

		// Handle the pending states
		while (!losing.isEmpty()) {
			S state = losing.poll();

			losingStates.add(state);


			Set<S> predecessors = this.game.getPredecessors(state);
			for (S pred : predecessors) {
				if (!(game.isUncontrollable(pred))) {
					if(!losingStates.contains(pred) && !losing.contains(pred))
						losing.add(pred);
				}else{
					Set<S> uncontrollableSuccesors = this.game.getUncontrollableSuccessors(pred);
					int losingSuccesors = 0;
					for (S succ : uncontrollableSuccesors) {
							
						if (losing.contains(succ)
								|| losingStates.contains(succ) || succ == pred) {
							losingSuccesors++;
						}
					}
					if (losingSuccesors == uncontrollableSuccesors.size())
						if(!losingStates.contains(pred) && !losing.contains(pred))
							losing.add(pred);
				}
			}
		}

		this.gameSolved = true;
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

	// remove any transition to a winning state
	public Strategy<S, Integer> buildStrategy() {
		Strategy<S, Integer> result = new Strategy<S, Integer>();

		Set<S> winningStates = this.getWinningStates();

		for (S state : losingStates) {
			StrategyState<S, Integer> source = new StrategyState<S, Integer>(
					state, DUMMY_GOAL);
			Set<StrategyState<S, Integer>> successors = new HashSet<StrategyState<S, Integer>>();
			// if its uncontrollable and winning it must have winning succesors
			for (S succ : this.game.getSuccessors(state)) {
				if (!winningStates.contains(succ)) {
					StrategyState<S, Integer> target = new StrategyState<S, Integer>(
							succ, DUMMY_GOAL);
					successors.add(target);
				}
			}
			result.addSuccessors(source, successors);
		}
		return result;
	}

	public Strategy<S, Integer> buildEnvironmentStrategy() {
		Strategy<S, Integer> result = new Strategy<S, Integer>();

		Set<S> winningStates = this.getWinningStates();
		for (S state : winningStates) {
			StrategyState<S, Integer> source = new StrategyState<S, Integer>(
					state, DUMMY_GOAL);
			Set<StrategyState<S, Integer>> successors = new HashSet<StrategyState<S, Integer>>();
			// if its uncontrollable and winning it must have winning succesors
			if (this.game.isUncontrollable(state)) {
				for (S succ : this.game.getUncontrollableSuccessors(state)) {
					if (!this.losingStates.contains(succ)) {
						StrategyState<S, Integer> target = new StrategyState<S, Integer>(
								succ, DUMMY_GOAL);
						successors.add(target);
					}
				}
			} else { // Controllable State
				for (S succ : this.game.getControllableSuccessors(state)) {
					StrategyState<S, Integer> target = new StrategyState<S, Integer>(
							succ, DUMMY_GOAL);
					successors.add(target);
				}
			}
			result.addSuccessors(source, successors);
		}
		return result;
	}




}
