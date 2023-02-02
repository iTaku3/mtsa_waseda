package MTSSynthesis.controller.model;

import java.util.HashSet;
import java.util.Set;

import MTSTools.ac.ic.doc.mtstools.model.LTS;


public abstract class StateSpaceCuttingGameSolver<S, A> implements LabelledGameSolver<S,A,Integer> {

	protected LTS<S, A> originalEnvironment;
	protected LabelledGame<S,A> game;
	protected Set<S> losingStates;
	protected boolean gameSolved;
	protected Set<Set<S>> goalStates;

	
	public StateSpaceCuttingGameSolver(LabelledGame<S,A> game, Set<Set<S>> goalStates){
		this.game = game;
		losingStates = new HashSet<S>();
		gameSolved = false;
		this.goalStates = goalStates;
	}
	
	public Game<S> getGame(){
		return game;
	}
	

}

