package MTSSynthesis.controller.gr.time.model;

import java.util.HashSet;
import java.util.Set;

public class TimedAutomata {
	
	TimedState inicial;
	Set<TimedState> winningStates;
//	Set<TimedState> states;
	
	public TimedAutomata(TimedState inicial) {
		this.inicial = inicial;
		winningStates  = new HashSet<TimedState>();
//		this.states = new HashSet<TimedState>();
	}
	
	public TimedState getInicial() {
		return inicial;
	}	
	public void addWinning(TimedState state){
		winningStates.add(state);
	}
	
	public Set<TimedState> getWinningStates() {
		return winningStates;
	}
	
	@Override
	public boolean equals(Object obj) {
		TimedAutomata other = (TimedAutomata) obj;
		return 
//		this.states.equals(other.states)&&
		this.winningStates.equals(other.winningStates)&&
		this.inicial.equals(other.inicial);
	}
	
}
