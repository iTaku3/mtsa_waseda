package ltsa.ui.exploration;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ltsa.ui.Transition;
import ltsa.lts.CompactState;
import ltsa.lts.CompositeState;
import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;
import MTSTools.ac.ic.doc.mtstools.model.TransitionSystem;
import ltsa.ac.ic.doc.mtstools.util.fsp.AutomataToMTSConverter;

public class EnvironmentView {

	CompositeState originalLTS;
	
	MTS<Long, String> mts;
	HashMap<String, ArrayList<Transition>> transitionsByNameTable; // action -> transitions
	HashMap<Long, ArrayList<Transition>> transitionsByStateTable; // state -> outgoing transitions
	
	public EnvironmentView(CompositeState _environmentView) {
		
		// Generate a LTS that simulates the environment view
		originalLTS = _environmentView;
		AutomataToMTSConverter mtsConverter = AutomataToMTSConverter.getInstance();
		CompactState machine = _environmentView.machines.get(0);
		mts = mtsConverter.convert(machine);
		Set<String> viewActions = new HashSet<String>(); 
		viewActions.addAll(mts.getActions());
	
	}
	public void initialise(){
		// Build two dictionaries:
		//	One to access transitions by action name
		// 	The other to access transitions by their from-state
		transitionsByNameTable = new HashMap<String, ArrayList<Transition>>();
		transitionsByStateTable = new HashMap<Long, ArrayList<Transition>>();
		
		for(MTS.TransitionType type : new MTS.TransitionType[]{MTS.TransitionType.REQUIRED, MTS.TransitionType.MAYBE}) {
			Map<Long, BinaryRelation<String,Long>> transitions = mts.getTransitions(type);
			iterateTransitions(transitions, type);
		}
	}
		
	private void iterateTransitions(Map<Long, BinaryRelation<String, Long>> transitions, TransitionType type) {
		for(Long from : transitions.keySet()) {
			ArrayList<Transition> transitionsByState = new ArrayList<Transition>();
			for(Pair<String, Long> to : transitions.get(from)) {
				Transition t = new Transition(from, to, type);
				
				transitionsByState.add(t);
				
				ArrayList<Transition> transitionsByAction = transitionsByNameTable.get(t.name());
				if(transitionsByAction == null) {
					transitionsByAction = new ArrayList<Transition>();
					transitionsByAction.add(t);
					transitionsByNameTable.put(t.name(), transitionsByAction);
				}
				else
					transitionsByAction.add(t);
			}
			
			ArrayList<Transition> existingTransitions = transitionsByStateTable.get(from);
			if(existingTransitions == null)
				transitionsByStateTable.put(from, transitionsByState);
			else
				existingTransitions.addAll(transitionsByState);
		}
	}
	public MTS<Long, String> getMTS() {
		return mts;
	}
	public HashMap<Long, ArrayList<Transition>> getTransitionsByState() {
		return transitionsByStateTable;
	}
	public HashMap<String, ArrayList<Transition>> getTransitionsByName() {
		return transitionsByNameTable;
	}
	public CompositeState getOriginalLTS() {
		return originalLTS;
	}
}
