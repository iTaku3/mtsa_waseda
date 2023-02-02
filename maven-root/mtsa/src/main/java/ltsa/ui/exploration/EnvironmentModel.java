package ltsa.ui.exploration;

import java.util.ArrayList;
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
import ltsa.ac.ic.doc.mtstools.util.fsp.AutomataToMTSConverter;

public class EnvironmentModel {

CompositeState originalLTS;
	
	HashMap<String, MTS<Long, String>> mtss; // key: name of the machine, value: the mts
	HashMap<String, HashMap<String, ArrayList<Transition>>> allTransitionsByNameTable; // {MTSname -> {action -> transition}}
	HashMap<String, HashMap<Long, ArrayList<Transition>>> allTransitionsByStateTable; // {MTSname -> {state -> transition}}
	
	public EnvironmentModel(CompositeState _environmentModel) {
		originalLTS = _environmentModel;
		
		// Generate a dictionary with each name and mts of the environment model
		AutomataToMTSConverter mtsConverter = AutomataToMTSConverter.getInstance();
		mtss = new HashMap<String, MTS<Long, String>>();
		Set<String> modelActions = new HashSet<String>();  
		for (Object machine : _environmentModel.machines) {
			if(machine instanceof CompactState){
				String name = ((CompactState) machine).getName();
				MTS<Long, String> mts = mtsConverter.convert((CompactState)machine);
				modelActions.addAll(mts.getActions()) ;
				mtss.put(name, mts);
			}
		}
	}
	
	public void initialise(){
		
		// Build two dictionaries:
		//	One to access transitions by action name
		// 	The other to access transitions by their from-state
		allTransitionsByNameTable = new HashMap<String, HashMap<String, ArrayList<Transition>>>() ; // {name -> {action -> transition}}
		allTransitionsByStateTable = new HashMap<String, HashMap<Long, ArrayList<Transition>>>(); // {name -> {state -> transition}}
		
		for(MTS.TransitionType type : new MTS.TransitionType[]{MTS.TransitionType.REQUIRED, MTS.TransitionType.MAYBE}) {
			for (Map.Entry<String,MTS<Long, String>> entry : mtss.entrySet()) {
				HashMap<String, ArrayList<Transition>> transitionsByNameTable = new HashMap<String, ArrayList<Transition>>();
				HashMap<Long, ArrayList<Transition>> transitionsByStateTable = new HashMap<Long, ArrayList<Transition>>();
				Map<Long, BinaryRelation<String,Long>> mtsTransitions = entry.getValue().getTransitions(type);
				iterateTransitions(mtsTransitions, type, transitionsByNameTable, transitionsByStateTable);
				// generate allTransitionByNameTable
				if (!allTransitionsByNameTable.containsKey(entry.getKey())) {
					allTransitionsByNameTable.put(entry.getKey(), transitionsByNameTable);
				} else {
					for (Map.Entry<String, ArrayList<Transition>> labelTransitions : transitionsByNameTable.entrySet()) {
						HashMap<String,ArrayList<Transition>> machine = allTransitionsByNameTable.get(entry.getKey());
						if (machine.containsKey(labelTransitions.getKey())) {
							machine.get(labelTransitions.getKey()).addAll(labelTransitions.getValue());
						} else {
							machine.put(labelTransitions.getKey(), labelTransitions.getValue());
						}
					}
				}
				// generate allTransitionByStateTable
				if (!allTransitionsByStateTable.containsKey(entry.getKey())){
					allTransitionsByStateTable.put(entry.getKey(), transitionsByStateTable);
				} else {
					for (Map.Entry<Long, ArrayList<Transition>> stateTransitions : transitionsByStateTable.entrySet()) {
						HashMap<Long,ArrayList<Transition>> machine = allTransitionsByStateTable.get(entry.getKey());
						if (machine.containsKey(stateTransitions.getKey())) {
							machine.get(stateTransitions.getKey()).addAll(stateTransitions.getValue());
						} else {
							machine.put(stateTransitions.getKey(), stateTransitions.getValue());
						}
					}
				}
			}
		}
	}
	
	private void iterateTransitions(Map<Long, BinaryRelation<String, Long>> transitions, TransitionType type, HashMap<String, ArrayList<Transition>> transitionsByNameTable, HashMap<Long, ArrayList<Transition>> transitionsByStateTable) {
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

	public HashMap<String, MTS<Long, String>> getMTSs() {
		return mtss;
	}
	public HashMap<String, HashMap<Long, ArrayList<Transition>>> getTransitionsByState() {
		return allTransitionsByStateTable;
	}
	public HashMap<String, HashMap<String, ArrayList<Transition>>> getTransitionsByName() {
		return allTransitionsByNameTable;
	}

	public CompositeState getOriginalLTS() {
		return originalLTS;
	}
	
}
