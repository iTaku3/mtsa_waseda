package MTSSynthesis.controller.model;

import java.util.Set;

import com.google.common.collect.Sets;

import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSTools.ac.ic.doc.mtstools.model.impl.MTSAdapter;
import MTSSynthesis.controller.util.GRGameBuilder;
import MTSSynthesis.controller.model.gr.GRGoal;

public abstract class StateSpaceCuttingControlProblem<S, A> extends
	GRControProblem<S, A, Integer> {

	protected S trapState;
	
	protected LabelledGameSolver<S, A, Integer> gameSolver;

	public LabelledGameSolver<S, A, Integer> getGameSolver(){
		if(gameSolver == null)
			gameSolver = buildGameSolver();
		return gameSolver;
	}

	public ControllerGoal<A> getGRControllerGoal(){
		return controllerGoal;
	}
	
	public GRGoal<S> getGRGoal(){
		return getGRGoalFromControllerGoal(environment, controllerGoal);
	}
	
	
	protected abstract LabelledGameSolver<S, A, Integer> buildGameSolver();	
	
	public StateSpaceCuttingControlProblem(LTS<S, A> env,
										   ControllerGoal<A> grControlGoal, S trapState) {
		super(env, grControlGoal);
		this.trapState = trapState;
		if(!env.getStates().contains(trapState))
			env.addState(trapState);		
	}

	@Override
	protected LTS<S, A> primitiveSolve() {
		this.gameSolver = buildGameSolver();
		gameSolver.solveGame();	
		return environment;
	}
	
	protected LTS<S, A> keepWinningStates(Set<S> winningStates) {

		Set<S> gameStates = gameSolver.getGame().getStates();
		// remove transitions to losing
		for (S s : gameStates) {
			for (Pair<A,S> p : environment.getTransitions(s)) {
				if (!winningStates.contains(p.getSecond())){
					environment.removeTransition(s, p.getFirst(),
							p.getSecond());
					//add transition to trap state
					environment.addTransition(s, p.getFirst(), p.getSecond());
				}
			}
		}
		// remove transitions from losing
		for (S s : winningStates) {
			if(environment.getTransitions(s) != null){
				for (Pair<A, S> p : environment.getTransitions(s)) {
					environment.removeTransition(s, p.getFirst(),
							p.getSecond());
				}
			}
		}
		environment.removeUnreachableStates();
		return environment;
	}	
	
	public Set<S> getWinningStates(){
		gameSolver.solveGame();
		return gameSolver.getWinningStates();
	}

	public LTS<S, A> removeLosingStates(){
		return removeInnerStates(gameSolver.getWinningStates());
	}	
	
	public LTS<S, A> removeStates(){
		return removeInnerStates(Sets.difference(gameSolver.getGame().getStates(), gameSolver.getWinningStates()));
	}
	
	
	public LTS<S, A> removeInnerStates(Set<S> innerStates) {
		
		if(!environment.getStates().contains(trapState))
			environment.addState(trapState);
		// remove transitions from trapState, just in case
		for (Pair<A, S> p : environment.getTransitions(trapState)) {
				environment.removeTransition(trapState, p.getFirst(),
						p.getSecond());
		}
		
		// remove transitions to winningstates
		for (S s : environment.getStates()) {
			for (Pair<A, S> p : environment.getTransitions(s)) {
				if (innerStates.contains(p.getSecond())){
					environment.removeTransition(s, p.getFirst(),
							p.getSecond());
					//add transition to TrapState
					environment.addTransition(s, p.getFirst(), trapState);
				}
			}
		}
		// remove transitions from winningstates
		for (S s : innerStates) {
			for (Pair<A, S> p : environment.getTransitions(s)) {
				environment.removeTransition(s, p.getFirst(),
						p.getSecond());
			}
		}
		environment.removeUnreachableStates();
		return environment;
	}
	
	protected GRGoal<S> getGRGoalFromControllerGoal(LTS<S,A> environment, ControllerGoal<A> controllerGoal){
		GRGameBuilder<S, A> grGameBuilder = new GRGameBuilder<S,A>();
		return grGameBuilder.buildGRGameFrom(new MTSAdapter<S,A>(environment), controllerGoal).getGoal();
	}	
		
}
