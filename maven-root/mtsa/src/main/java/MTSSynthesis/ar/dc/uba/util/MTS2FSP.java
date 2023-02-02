package MTSSynthesis.ar.dc.uba.util;

import java.util.*;
import java.util.Map.Entry;

import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;
import MTSSynthesis.ar.dc.uba.model.language.Alphabet;

/**
 * 
 * @author gsibay
 *
 */
public class MTS2FSP<State, Action> {

	private Map<State, String> statesId = new HashMap<State, String>();
	
	public String toFSP(MTS<State, Action> mts, String mtsName) {
		StringBuffer fsp = new StringBuffer();
		
		this.assignStatesId(mts, mtsName);
		
		//TODO: presentar
		// First write the process for the initial state 
		State initialState = mts.getInitialState();
		Set<State> states = new HashSet<State>(mts.getStates());
		states.remove(initialState);
		
		// Put a minimal at the begining so the MTS is minimized.
		fsp.append("minimal ");
		this.writeProccessForState(initialState, mts, fsp);
		fsp.append(",\n");
		
		// Write the other processes
		for (State state : states) {
			this.writeProccessForState(state, mts, fsp);
			fsp.append(",\n");
		}
		// After the last process put a "\{TAU_SYMBOL}." instead of a ",". End of processes.
		// The TAU_SYMBOL can not be tau for MTSA's implementations details.
		// The workarround is to use another symbol for tau and then hide it (so it becomes a tau).
		fsp.delete(fsp.length()-2, fsp.length()).append("\\{").append(Alphabet.TAU).append("}.\n");
		
		this.writeStatesIdMapping(fsp);
		
		return fsp.toString();
	}

	/**
	 * Writes as a comment the mapping between a 
	 * FSP's Proccess and a MTS' State.
	 * @param fsp
	 */
	private void writeStatesIdMapping(StringBuffer fsp) {
		// Start comment
		fsp.append("/*\n");
		
		Set<Entry<State, String>> mapEntry = this.statesId.entrySet();
		for (Entry<State, String> entry : mapEntry) {
			fsp.append(entry.getValue()).append(" = ").append(entry.getKey()).append("\n");
		}
		
		// End comment
		fsp.append("*/\n");
	}

	/**
	 * Writes the state as a Process in FSP.
	 * Example:
	 * E_1 = (k? -> E_1
	 *		|t -> E_2
	 *		|t? -> E_3
	 *		|k -> E_4
	 *		),
	 *	E_2 = STOP,
	 *	E_3 = STOP,
	 *	E_4 = STOP.
	 * @param state
	 * @param mts
	 * @param fsp
	 */
	private void writeProccessForState(State state, MTS<State, Action> mts, StringBuffer fsp) {
		// Write the proccess id
		String proccessId = this.statesId.get(state);
		fsp.append(proccessId).append(" = ");
		
		// Get the required and maybe transitions from the state
		BinaryRelation<Action, State> reqTransitions = mts.getTransitions(state, TransitionType.REQUIRED);
		BinaryRelation<Action, State> mayTransitions = mts.getTransitions(state, TransitionType.MAYBE);
		if(reqTransitions.isEmpty() && mayTransitions.isEmpty()) { // There are no transitions from this state
			// The process is END
			fsp.append("END");
		} else {
			fsp.append("(");
			this.writeTransitions(state, reqTransitions, false, fsp);
			this.writeTransitions(state, mayTransitions, true, fsp);

			// Delete the last "|" if pressent.
			if(fsp.charAt(fsp.length()-1) == '|') {
				fsp.deleteCharAt(fsp.length()-1);
			}
			fsp.append(")");
		}
	}

	/**
	 * Prints the transitions to the states.
	 * 
	 * Example:
	 * 
	 * k? -> E_1
	 *		|t? -> E_3
	 *		|r? -> E_4
	 *		|
	 * @param fromState
	 * @param reqTransitions
	 * @param isMaybe
	 * @param fsp 
	 */
	private void writeTransitions(State fromState, BinaryRelation<Action, State> transitions, boolean isMaybe, StringBuffer fsp) {
		for (Pair<Action, State> pair : transitions) {
			fsp.append(pair.getFirst());
			if(isMaybe) {
				fsp.append("?");
			}
			fsp.append(" -> ").append(this.statesId.get(pair.getSecond())).append("\n").append("\t|");
		}
	}

	/**
	 * Assigns id to the states. 
	 * The initial state has the mtsName, and then they are named E_ with a number starting from 1.
	 * The states have consecutives numbers (except the first one).
	 * The state is then identified as E_ and then the number.
	 * For instance, if there are 8 different states, they will be
	 * numerated from 1 to 8. The states will be E_1 to E_8.
	 * @param mts
	 */
	private void assignStatesId(MTS<State, Action> mts, String mtsName) {
		int nextStateId = 1;
		// The initial state has a special id
		this.statesId.put(mts.getInitialState(), mtsName);
		for (State state : mts.getStates()) {
			if(!this.statesId.containsKey(state)) {
				this.statesId.put(state, mtsName + "_E_" + nextStateId);
				nextStateId++;
			}
		}
	}
	
	
}
