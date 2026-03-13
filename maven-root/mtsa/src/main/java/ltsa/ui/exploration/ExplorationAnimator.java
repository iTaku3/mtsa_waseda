package ltsa.ui.exploration;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import ltsa.ui.Transition;
import ltsa.lts.Animator;
import ltsa.lts.CompactState;
import ltsa.lts.CompositeState;
import ltsa.lts.EventManager;
import ltsa.lts.LTSEvent;
import MTSTools.ac.ic.doc.mtstools.model.MTS;

public class ExplorationAnimator implements Animator {
		
	EnvironmentView environmentView;
	EnvironmentModel environmentModel;
	
	// To keep track of the current states
	HashMap<String, Long> currentStates;
	// TODO: only save the last transition or I need more?
	Transition lastTransition;
	
	// The BitSet which is passed to the AnimWindow (note that indexing starts from 1!)
	BitSet menuOptionBitSet;
	ArrayList<String> menuActionNames;
	
	// Only need to check whether there is an error state once
	Long errorState = null; 
	
	// Fields for the error trace. This is _not_ passed in the initialise,
	// but as part of CompositeState
	ArrayList<Transition> errorTrace = null;
	
	// Notify this object whenever the state changes
	EventManager eventManager;
	
	RefinedMTS refinedMTS; 
	
	public ExplorationAnimator(CompositeState _environmentView, CompositeState _environmentModel, EventManager _eventManager) {
		eventManager = _eventManager;
		
		environmentView = new EnvironmentView(_environmentView); 
		environmentModel = new EnvironmentModel(_environmentModel);
				
		int[] transitionIndeces;
		transitionIndeces = new int[_environmentModel.getMachines().size() + 1];
		for(int i = 0; i < transitionIndeces.length; i++)
			transitionIndeces[i] = 0;
		
		eventManager.post(new LTSEvent(LTSEvent.NEWSTATE, transitionIndeces));
		
		menuActionNames = new ArrayList<String>();
		menuOptionBitSet = new BitSet();
		
		refinedMTS = new RefinedMTS();
		
	}

	
	/**
	 * Initialize the animator, generating all dictionaries for
	 * easier use in this class, setting the inital state etc.
	 * 
	 * @param menu is not being used. Declarated just to Override
	 * @return
	 */
	public BitSet initialise(Vector menu) {
		
		environmentView.initialise();
		environmentModel.initialise();

		// Initialize the current states 
		currentStates = firstState();
		
		// Now finally create the initial BitSets for each MTS with the indeces of all outgoing transitions of state 0, set to 1
		generateMenuActionNames();
		TransitionsComparator transitionsComparator = calculateIntersections();
		menuOptionBitSet = generateBitSetFromStates(transitionsComparator);
		
//		Vector<String> originalErrorTrace = this.originalLTS.getErrorTrace();
//		if(originalErrorTrace != null
//				&& originalErrorTrace.size() != 0 
//				&& originalErrorTrace.firstElement() instanceof String) {
//			this.errorTrace = this.generateTransitionListFromTrace(originalErrorTrace);
//		}
		
		return menuOptionBitSet;
	}
	
	private HashMap<String, Long> firstState() {
		HashMap<String, Long> resultList = new HashMap<String, Long>();
		
		MTS<Long, String> viewMTS = environmentView.getMTS();
		Long viewState = (Long)(viewMTS.getStates().toArray())[0];
		viewState = checkIfError(viewState, viewMTS);
		resultList.put(new String("Environment View"), viewState);
				
		for (java.util.Map.Entry<String, MTS<Long, String>> entry : environmentModel.getMTSs().entrySet()) {
			Long modelState = (Long)(entry.getValue().getStates().toArray())[0];
			viewState = checkIfError(modelState, entry.getValue());
			resultList.put(entry.getKey(), modelState);
		}
				
		return resultList;
	}

	private Long checkIfError(Long state, MTS<Long, String> mts) {
		if(state.compareTo(new Long(-1)) == 0) {
			errorState = state;
			state = (Long)(mts.getStates().toArray())[1];
		}
		return state;
	}

	private void generateMenuActionNames() {
		menuActionNames.clear();
		menuActionNames.addAll(environmentView.getMTS().getActions());
		Collections.sort(this.menuActionNames);
	}		
	
	private TransitionsComparator calculateIntersections(){
		
		// Calculate the intersection of actions available in the environmentView with the actions available in the environment model
		
		ArrayList<Transition> environmentViewTransitions = environmentView.getTransitionsByState().get(currentStates.get("Environment View"));
		ArrayList<Transition> environmentModelTransitions = new ArrayList<Transition>();
		for (String mtsName : currentStates.keySet()) {
			if (mtsName.equals("Environment View")){
				continue;
			}
			if (environmentModelTransitions.isEmpty()){
				environmentModelTransitions = environmentModel.getTransitionsByState().get(mtsName).get(currentStates.get(mtsName));
			} else {
				environmentModelTransitions = new TransitionsComparator(environmentModelTransitions, environmentModel.getTransitionsByState().get(mtsName).get(currentStates.get(mtsName)), true).intersection;
			}
		}
		TransitionsComparator transitionsComparator = new TransitionsComparator(environmentModelTransitions, environmentViewTransitions, false);
		return transitionsComparator;
				
	}
	
	private BitSet generateBitSetFromStates(TransitionsComparator transitionsComparator) {
		BitSet bs = new BitSet(environmentView.getMTS().getActions().size()+1);
		
		try{
			transitionsComparator.refineTransitions(refinedMTS);
		}catch (Exception e) {
//			 System.out.println(e.getMessage());
		}
		ArrayList<Transition> finalList = transitionsComparator.makeIntersection();
		
		for(Transition t : finalList) {
			bs.set(indexFromTransition(t));
		}
		
		return bs;
	}
		
	private int indexFromTransition(Transition t) {
		return menuActionNames.indexOf(t.name()) + 1;
	}
		 
	/**
	 * This is called after every animation step. 
	 * 
	 * @return String[] actions that can be made in one step
	 */
	public String[] getMenuNames() {
		// Reindex since indeces in the AnimWindow start from 1...
		
		String[] ret = new String[menuActionNames.size()+1];
		ret[0] = "";
		int i = 1;
		for(String action : menuActionNames)
			ret[i++] = action;
		return ret;
	}
	
	public String[] getAllNames() {
		return getMenuNames();
	}

	public BitSet menuStep(int choice) {
		HashMap<String, Transition> chosen = transitionForIndex(choice);
		performTransition(chosen);
		
		return menuOptionBitSet;
	}
	
	private HashMap<String, Transition> transitionForIndex(int index) {
		// Retrieve the transition chosen by the user
		// Can be non-deterministic, if so, choose randomly according to the rules
		
		String transitionName = menuActionNames.get(index - 1);
		HashMap<String, Transition> choices = new HashMap<String, Transition>(); 
		
		ArrayList<Transition> viewTransitionsUnfiltered = environmentView.getTransitionsByName().get(transitionName);
		ArrayList<Transition> viewTransitions = new ArrayList<Transition>();
		for(Transition t : viewTransitionsUnfiltered) {
			if(t.from() == currentStates.get("Environment View"))
				viewTransitions.add(t);
		}
		
		choices.put("Environment View", chooseTransition(viewTransitions));
		
		for (Entry<String, HashMap<String, ArrayList<Transition>>> mtsNameTransitions : environmentModel.getTransitionsByName().entrySet()) {
			ArrayList<Transition> transitionsUnfiltered = mtsNameTransitions.getValue().get(transitionName);
			ArrayList<Transition> transitions = new ArrayList<Transition>();
			for(Transition t : transitionsUnfiltered) {
				if(t.from() == currentStates.get(mtsNameTransitions.getKey()))
					transitions.add(t);
			}
			
			choices.put(mtsNameTransitions.getKey(), chooseTransition(transitions));
		}
		return choices;
	}
	
	/**
	 * Choose a transition from the list of transitions to follow
	 * based on the MTSA behavior;
	 * assumes that all transitions are valid, i.e. 'from' matches 'current state'
	 * 
	 * @return
	 */
	
	private Transition chooseTransition(ArrayList<Transition> transitions) {
		if(transitions.size() == 1)
			return transitions.get(0);
		
		// Get all required transitions first
		ArrayList<Transition> req = new ArrayList<Transition>(transitions.size());
		for(Transition t : transitions) {
			if(t.type() == MTS.TransitionType.REQUIRED)
				req.add(t);
		}
		
		return transitions.get((int)Math.floor(Math.random()*transitions.size()));
	}
	
	private void performTransition(HashMap<String,Transition> chosenTransition) {
		
		for (Map.Entry<String, Transition> mtsChoice : chosenTransition.entrySet()) {
			currentStates.put(mtsChoice.getKey(), mtsChoice.getValue().to());
		}
		
		lastTransition = chosenTransition.get("Environment View");
		
		TransitionsComparator transitionsComparator = calculateIntersections();
		
		try {
			transitionsComparator.refineWhenSelection(refinedMTS, lastTransition);
		} catch (Exception e) {
//			 System.out.println(e.getMessage());
		}
		
		menuOptionBitSet = generateBitSetFromStates(transitionsComparator);
		// TODO: Do I only save the last transition or I need more?
		 
		
		int[] transitionIndeces;
		int size = environmentView.getOriginalLTS().getMachines().size();
		if(size == 1)
			transitionIndeces = new int[size + 1];
		else
			transitionIndeces = new int[size + 2];
		int i = 0;
		for(; i < transitionIndeces.length - 2; i++)
			transitionIndeces[i] = 0;	
		transitionIndeces[i++] = lastTransition.to().intValue();
		for (CompactState compactState : environmentModel.getOriginalLTS().machines) {
			transitionIndeces[i++] = chosenTransition.get(compactState.getName()).to().intValue();
		}
		
		LTSEvent event = new LTSEvent(LTSEvent.NEWSTATE, transitionIndeces, actionNameChosen());
		eventManager.post(event);
	}
	
	public BitSet singleStep() {
		// Choose a transition to follow from the current states
		HashMap<String, Transition> chosenForEachMTS = new HashMap<String, Transition>();
		
		ArrayList<Transition> transitions = environmentView.getTransitionsByState().get(currentStates.get("Environment View"));
		chosenForEachMTS.put("Environment View", chooseTransition(transitions));
		
		for (Map.Entry<String, HashMap<Long, ArrayList<Transition>>> mtsNameWithMTS : environmentModel.getTransitionsByState().entrySet()) {
			ArrayList<Transition> mtsTransitions = mtsNameWithMTS.getValue().get(currentStates.get(mtsNameWithMTS.getKey()));
			chosenForEachMTS.put(mtsNameWithMTS.getKey(), chooseTransition(mtsTransitions));
		}
		
		performTransition(chosenForEachMTS);
		return menuOptionBitSet;
	}

	public int actionChosen() {
		return indexFromTransition(lastTransition);
	}

	public String actionNameChosen() {
		if(lastTransition.type() == MTS.TransitionType.REQUIRED)
			return lastTransition.name();
		else
			return lastTransition.name().concat("?");
	}

	public boolean isError() {
		return (currentStates.get("Environment View") == errorState);
	}

	public boolean isEnd() {
		return (environmentView.getTransitionsByState().get(currentStates.get("Environment View")).size() == 0);
	}

	public boolean nonMenuChoice() {
		// Not necessary
		return true;
	}

	public boolean getPriority() {
		// No priority actions in MTS Exploration...
		return false;
	}

	public BitSet getPriorityActions() {
		// Again, no priority in MTS Exploration...
		return null;
	}
	
	public void message(String msg) {
		// Not necessary
	}

	public boolean hasErrorTrace() {
		return (errorTrace != null);
	}

	/**
	 * Check whether the next part in the trace is still valid 
	 * in the current state
	 * 
	 * @return
	 */
	public boolean traceChoice() {
		if(errorTrace.size() > 0)
			return (errorTrace.get(0).from() == currentStates.get("Environment View"));
		return false;
	}

	/**
	 * Perform the next step in the trace by 
	 * using the first step in the trace to generate the BitSet to return
	 * 
	 * @return
	 */
	public BitSet traceStep() {
//		if(errorTrace.get(0).from() == currentStates.get("Environment View"));
//			performTransition(errorTrace.get(0));
//		errorTrace.remove(0);
		return menuOptionBitSet;
	}

}
