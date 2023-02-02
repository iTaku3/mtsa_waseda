package ltsa.ui.exploration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ltsa.ui.Transition;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;

public class TransitionsComparator {

	ArrayList<Transition> modelTransitions;
	HashMap<String, Transition> modelLabels;
	
	ArrayList<Transition> viewTransitions;
	HashMap<String, Transition> viewLabels;
	
	ArrayList<Transition> intersection;

	public TransitionsComparator(ArrayList<Transition> _modelTransitions, ArrayList<Transition> _viewTransitions, Boolean doIntersection) {
		modelTransitions = _modelTransitions;
		viewTransitions = _viewTransitions;
		modelLabels = getLabelsFromTransitions(modelTransitions);
		viewLabels = getLabelsFromTransitions(viewTransitions);
		if (doIntersection)
			intersection = makeIntersection();
	}

	public ArrayList<Transition> makeIntersection() {
		
		ArrayList<Transition> resultList = new ArrayList<Transition>();

		for (Map.Entry<String, Transition> labelTransition : modelLabels
				.entrySet()) {
			if (viewLabels.containsKey(labelTransition.getKey())) {
				resultList.add(labelTransition.getValue());
			}
		}
		return resultList;
	}

	private HashMap<String, Transition> getLabelsFromTransitions(ArrayList<Transition> list) {
		HashMap<String, Transition> result = new HashMap<String, Transition>();
		for (Transition transition : list) {
			result.put(transition.getEvent(), transition);
		}
		return result;
	}

	public void refineTransitions(RefinedMTS refinedMTS) throws Exception {
		
		ArrayList<Pair<Integer, Transition>> rulesValue = rules();
		
		for (Pair<Integer, Transition> pair : rulesValue) {
			if (pair.getFirst().equals(new Integer(1))){
				addTransitionsToUnknownStates(refinedMTS, pair.getSecond());
			} else if (pair.getFirst().equals(new Integer(2))){
				//TODO: maybe but not in the view environment RULE 6
			}
		}
		
	}
	
	public void refineWhenSelection(RefinedMTS refinedMTS, Transition t) {

		refinedMTS.performTransition(t.getEvent(), t.to());
	
	}
	
	private ArrayList<Pair<Integer, Transition>> rules() throws Exception {
		
		ArrayList<Pair<Integer, Transition>> result = new ArrayList<Pair<Integer, Transition>>();
		
		for (Transition mTransition : modelTransitions) {
			
			if (mTransition.type() == MTS.TransitionType.REQUIRED && !viewLabels.containsKey(mTransition.getEvent())){ // RULE 2
				
				throw new Exception("Model and View are inconsistent in " + mTransition.toString() + "transition from Model View");
				
				// RULE 1 may be here but, there's nothing to do
				
			} else if (mTransition.type() == MTS.TransitionType.MAYBE) {
				
				if(viewLabels.containsKey(mTransition.getEvent())) { // RULE 5
					
					// what happend if no deterministic?
					
					Pair<Integer, Transition> pair = new Pair<Integer, Transition>(new Integer(1), mTransition);
					result.add(pair);
					
					
				} else { // RULE 6
					
					Pair<Integer, Transition> pair = new Pair<Integer, Transition>(new Integer(2), mTransition);
					result.add(pair);
				}
			
			}
		}
		for (Transition vTransition : viewTransitions) {
			if (!modelTransitions.contains(vTransition)){ //RULE 3
				throw new Exception("Model and View are inconsistent in " + vTransition.toString() + "transition from Environment View");
			} // in else case I don't have to do nothing RULE 4
		}
		return result;
		
	}

	private void addTransitionsToUnknownStates(RefinedMTS refinedMTS, Transition modelT) {
		
		ArrayList<Long> modelStates = new ArrayList<Long>();
		modelStates.add(modelT.to());
		RefinedMTSState newState = new RefinedMTSState(new Long(-1), modelStates);
		
		refinedMTS.addTransition(modelT.getEvent(), newState, TransitionType.REQUIRED);
	}
	
	
	
//	private void maybeToDisable(RefinedMTS refinedMTS, Transition t) {
		
//	}

}
