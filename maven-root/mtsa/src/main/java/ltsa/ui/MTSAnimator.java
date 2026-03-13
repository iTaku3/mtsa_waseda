package ltsa.ui;


import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import ltsa.updatingControllers.structures.UpdatingControllerCompositeState;
import ltsa.lts.Animator;
import ltsa.lts.CompactState;
import ltsa.lts.CompositeState;
import ltsa.lts.EventManager;
import ltsa.lts.LTSEvent;
import ltsa.lts.util.MTSUtils;
import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.impl.CompositionRuleApplier;
import MTSTools.ac.ic.doc.mtstools.model.impl.MTSMultipleComposer;
import ltsa.ac.ic.doc.mtstools.util.fsp.AutomataToMTSConverter;



/**
 * This class provides the capability of animating MTSs.
 * 
 * Opposed to the drawings of LTS, this animator can only show changes in either single
 * machines or in the composite machine, however not in submachines of a composite.
 * This would require tracking the current state within every submachine what this class
 * does not do.
 * 
 */

public class MTSAnimator implements Animator {
	CompositeState originalLTS;
	
	MTS<Long, String> mts;
	ArrayList<String> menuActionNames;
	Dictionary<String, ArrayList<Transition>> transitionsByNameTable; // action -> transitions
	Dictionary<Long, ArrayList<Transition>> transitionsByStateTable; // state -> outgoing transitions
	
	// To keep track of the current state
	Long currentState;
	Transition lastTransition;
	
	// The BitSet which is passed to the AnimWindow (note that indexing starts from 1!)
	BitSet menuOptionBitSet;
	
	// Only need to check whether there is an error state once
	Long errorState = null; 
	
	// Fields for the error trace. This is _not_ passed in the initialise,
	// but as part of CompositeState
	ArrayList<Transition> errorTrace = null;
	
	// Notify this object whenever the state changes
	EventManager eventManager;
	
	public MTSAnimator(CompositeState state, EventManager em) {
		
		AutomataToMTSConverter mtsConverter = AutomataToMTSConverter.getInstance();
		ArrayList<MTS<Long, String>> mtss = new ArrayList<MTS<Long, String>>();
		
		if (state instanceof UpdatingControllerCompositeState) {
			// Generate the compact state for UpdateController Problem
			
			mtss.add(mtsConverter.convert(state.getComposition()));
			
		} else if(!MTSUtils.isMTSRepresentation(state)){
//			System.out.println("State is not in MTS representation. Use the LTS animator.");
			
		} else {
			// Generate the compact state in MTS format
			
			for(Object cstate : state.machines) {
				if(cstate instanceof CompactState)
					mtss.add(mtsConverter.convert((CompactState)cstate));
			}
		}
		// Generate MTS composition if there is more than one machine
			
		if(mtss.size() == 1)
			this.mts = mtss.get(0);
		else {
			MTSMultipleComposer<Long, String> composer = new MTSMultipleComposer<Long, String>(new CompositionRuleApplier());
			this.mts = composer.compose(mtss);
		}
		
		// The event manager gets passed an array of the current state in each
		// machine, the state in the composite machine (if it's composite)
		// plus the index of the transition (see originalIndexForTransition())
		// Init this array to 0's
		
		int[] transitionIndeces;
		if(state.getMachines().size() == 1)
			transitionIndeces = new int[state.getMachines().size() + 1];
		else
			transitionIndeces = new int[state.getMachines().size() + 2];
		for(int i = 0; i < transitionIndeces.length; i++)
			transitionIndeces[i] = 0;
		
		eventManager = em;
		em.post(new LTSEvent(LTSEvent.NEWSTATE, transitionIndeces));
		
		menuActionNames = new ArrayList<String>(this.mts.getActions().size());
		menuOptionBitSet = new BitSet(this.mts.getActions().size()+1);
		
		originalLTS = state;
	}

	/**
	 * Initialize the animator, generating all dictionaries for
	 * easier use in this class, setting the inital state etc.
	 * 
	 * @param menu A list of strings describing a trace
	 * @return
	 */
	
	// param menu is not being used
	public BitSet initialise(Vector menu) {
		// Build two dictionaries:
		//	One to access transitions by action name
		// 	The other to access transitions by their from-state
		
		this.transitionsByNameTable = new Hashtable<String, ArrayList<Transition>>();
		this.transitionsByStateTable = new Hashtable<Long, ArrayList<Transition>>();
		
		for(MTS.TransitionType type : new MTS.TransitionType[]{MTS.TransitionType.REQUIRED, MTS.TransitionType.MAYBE}) {
			Map<Long, BinaryRelation<String,Long>> transitions = this.mts.getTransitions(type);
			for(Long from : transitions.keySet()) {
				ArrayList<Transition> transitionsByState = new ArrayList<Transition>();
				for(Pair<String, Long> to : transitions.get(from)) {
					Transition t = new Transition(from, to, type);
					
					transitionsByState.add(t);
					
					ArrayList<Transition> transitionsByAction = this.transitionsByNameTable.get(t.name());
					if(transitionsByAction == null) {
						transitionsByAction = new ArrayList<Transition>();
						transitionsByAction.add(t);
						transitionsByNameTable.put(t.name(), transitionsByAction);
					}
					else
						transitionsByAction.add(t);
				}
				
				ArrayList<Transition> existingTransitions = this.transitionsByStateTable.get(from);
				if(existingTransitions == null)
					this.transitionsByStateTable.put(from, transitionsByState);
				else
					existingTransitions.addAll(transitionsByState);
			}	
		}
	
		// Initialize the currentState to state 0
		
		currentState = this.firstState();
		
		// Now finally create the initial BitSet with the indeces of
		// all outgoing transitions of state 0 set to 1

		this.generateMenuActionNames();
		this.menuOptionBitSet = this.generateBitSetForState(currentState);
		
		Vector originalErrorTrace = this.originalLTS.getErrorTrace();
		if(originalErrorTrace != null
				&& originalErrorTrace.size() != 0 
				&& originalErrorTrace.firstElement() instanceof String) {
			this.errorTrace = this.generateTransitionListFromTrace(originalErrorTrace);
		}
		
		return this.menuOptionBitSet;
	}
	
	public int actionChosen() {
		return this.indexForTransition(this.lastTransition);
	}
	
	public String actionNameChosen() {
		if(this.lastTransition.type() == MTS.TransitionType.REQUIRED)
			return this.lastTransition.name();
		else
			return this.lastTransition.name().concat("?");
	}

	public String[] getAllNames() {
		return this.getMenuNames();
	}
	
	/**
	 * This is called after every animation step. 
	 * TODO document properly.
	 * 
	 */
	public String[] getMenuNames() {
		// Reindex since indeces in the AnimWindow start from 1...
		
		ArrayList<String> relabeled = relabelMaybeTransitionNames(this.menuActionNames);
		
		String[] ret = new String[this.menuActionNames.size()+1];
		ret[0] = "";
		int i = 1;
		for(String action : relabeled)
			ret[i++] = action;
		return ret;
	}

	public boolean getPriority() {
		// No priority actions in MTSA...
		return false;
	}

	public BitSet getPriorityActions() {
		// Again, no priority in MTSA...
		return null;
	}

	public boolean isEnd() {
		return (this.transitionsByStateTable.get(this.currentState).size() == 0);
	}

	public boolean isError() {
		return this.currentState == this.errorState;
	}

	public BitSet menuStep(int choice) {
		Transition chosen = this.transitionForIndex(choice);
		this.performTransition(chosen);
		
		return this.menuOptionBitSet;
	}

	public void message(String msg) {
		// Not necessary
	}

	public boolean nonMenuChoice() {
		// Not necessary
		return true;
	}

	/**
	 * Change the current state to the next given a transition
	 * and make all necessary modifications including setting 
	 * the now possible transitions and notifying the event manager
	 * 
	 * @return
	 */
	
	private void performTransition(Transition chosenTransition) {
		this.currentState = chosenTransition.to();
		this.menuOptionBitSet = this.generateBitSetForState(this.currentState);
		this.lastTransition = chosenTransition;
		
		// Notify the EventManager (primarily used for the drawing)
		// Once again, it's very icky:
		// The event manager requires an array of the current state of every machine
		// in the MTS plus the composite state and the index of the executed transition
		// Since we're not keeping track of every submachine though, they're all 0
		// except for 
		// - the second to last index (the state of the composite)
		// - and the last index which is the transition index (inside the original alphabet) 
		
		int[] transitionIndeces;
		int size = this.originalLTS.getMachines().size();
		if(size == 1)
			transitionIndeces = new int[size + 1];
		else
			transitionIndeces = new int[size + 2];
		int i = 0;
		for(; i < transitionIndeces.length - 2; i++)
			transitionIndeces[i] = 0;
		transitionIndeces[i++] = chosenTransition.to().intValue();
		transitionIndeces[i++] = this.indexForTransition(chosenTransition);
		
		LTSEvent event = new LTSEvent(LTSEvent.NEWSTATE, transitionIndeces, this.actionNameChosen());
		this.eventManager.post(event);
	}
	
	public BitSet singleStep() {
		// Choose a transition to follow from the current state
		
		ArrayList<Transition> transitions = this.transitionsByStateTable.get(this.currentState);
		Transition chosen = this.chooseTransition(transitions);
		
		this.performTransition(chosen);
		
		return this.menuOptionBitSet;
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
		
		// Cases:
		// 1) There is a required transition and at least one maybe, pick the req
		// 2) There is more than one required transition, so choose randomly from those
		// 3) There are only maybe transitions, pick randomly from the maybe ones
		/*
		if(req.size() == 1)
			return req.get(0);
		else if(req.size() > 1)
			return req.get((int)Math.floor(Math.random()*req.size()));
		else*/
			return transitions.get((int)Math.floor(Math.random()*transitions.size()));
	}
	
	private int indexForTransition(Transition t) {
		return this.menuActionNames.indexOf(t.name()) + 1;
	}
	
	private Transition transitionForIndex(int index) {
		// Retrieve the transition chosen by the user
		// Can be non-deterministic, if so, choose randomly according to the rules
		
		String transitionName = this.menuActionNames.get(index - 1);
		ArrayList<Transition> transitionsUnfiltered = this.transitionsByNameTable.get(transitionName);
		ArrayList<Transition> transitions = new ArrayList<Transition>();
		for(Transition t : transitionsUnfiltered) {
			if(t.from().equals(this.currentState))
				transitions.add(t);
		}
		
		return this.chooseTransition(transitions);
	}
	
	/**
	 * Generate a BitSet that has 1 for all possible transitions
	 * 
	 * @return
	 */
	
	private BitSet generateBitSetForState(Long state) {
		BitSet bs = new BitSet(this.mts.getActions().size()+1);
		
		ArrayList<Transition> transitions = this.transitionsByStateTable.get(state);
		for(Transition t : transitions) {
			bs.set(this.indexForTransition(t));
		}
		
		return bs;
	}
	
	private void generateMenuActionNames() {
		this.menuActionNames.clear();
		this.menuActionNames.addAll(this.mts.getActions());
		Collections.sort(this.menuActionNames);
	}		


	/**
	 * If for an action there are only maybe transitions, append a question mark
	 * However, default to required action name
	 * 
	 * @return
	 */
	
	private ArrayList<String> relabelMaybeTransitionNames(ArrayList<String> names) {
		ArrayList<String> relabeled = new ArrayList<String>();
		
		for(String actionName : this.menuActionNames) {
			ArrayList<Transition> transitions = this.transitionsByNameTable.get(actionName);
			if(transitions != null) {
				boolean isMaybeOnly = true;
				boolean hasMaybe = false;
				for(Transition t : transitions) {
					if(t.from() == this.currentState) {
						if(t.type() == MTS.TransitionType.REQUIRED) {
							isMaybeOnly = false;
							break;
						}
						else
							hasMaybe = true;
					}
				}
				if(isMaybeOnly && hasMaybe)
					relabeled.add(actionName);
				else
					relabeled.add(actionName);
			}
			else
				relabeled.add(actionName);
		}
		return relabeled;
	}
	
	/**
	 * Return the state 0 of the MTS 
	 * 
	 * @return
	 */
	
	private Long firstState() {
		Long state = (Long)(this.mts.getStates().toArray())[0];
		if(state.compareTo(new Long(-1)) == 0) {
			this.errorState = state;
			state = (Long)(this.mts.getStates().toArray())[1];
		}
		return state;
	}
	
	/* Methods to support traces */
	
	public boolean hasErrorTrace() {
		return (this.errorTrace != null);
	}

	/**
	 * Check whether the next part in the trace is still valid 
	 * in the current state
	 * 
	 * @return
	 */
	
	public boolean traceChoice() {
		if(this.errorTrace.size() > 0)
			return (this.errorTrace.get(0).from() == this.currentState);
		return false;
	}
	
	/**
	 * Perform the next step in the trace by 
	 * using the first step in the trace to generate the BitSet to return
	 * 
	 * @return
	 */
	
	public BitSet traceStep() {
		if(this.errorTrace.get(0).from() == this.currentState);
			this.performTransition(this.errorTrace.get(0));
		this.errorTrace.remove(0);
		return this.menuOptionBitSet;
	}
	
	/**
	 * Convert a trace into a list of Transitions
	 * 
	 * @param trace
	 * @return
	 */
	
	private ArrayList<Transition> generateTransitionListFromTrace(Vector<String> trace) {
		ArrayList<Transition> traceTransitions = new ArrayList<Transition>();
		
		// From the first state (0) turn every string into a Transition
		
		Long state = this.firstState();
		
		for(String step : trace) {
			// Cross reference transitionsByName and transitionsByState

			// If step has ? appended, check if there's a transition that corresponds
			// to this step and is maybe

			boolean isMaybe = step.contains("?");

			if(isMaybe)
				step = this.getActionNameByStringComparison(step.substring(0, step.length()-1));

			ArrayList<Transition> nameTransitions = this.transitionsByNameTable.get(step);
			ArrayList<Transition> stateTransitions = this.transitionsByStateTable.get(state);
			
			ArrayList<Transition> possibleTransitions 
				= new ArrayList<Transition>(Math.max(nameTransitions.size(), stateTransitions.size()));
			
			for(Transition t : nameTransitions) {
				if(stateTransitions.contains(t) && (t.type() == MTS.TransitionType.MAYBE) == isMaybe)
					possibleTransitions.add(t);
			}
			
			// Optimally, there should only be a single transition left now, else there is indeterminism
			Transition chosen = this.chooseTransition(possibleTransitions);
			traceTransitions.add(chosen);
			state = chosen.to();
		}
		return traceTransitions;
	}
	
	/**
	 * Return the correct string object of the action list 
	 * which matches the argument
	 * 
	 * @param s
	 * @return
	 */
	
	private String getActionNameByStringComparison(String s) {
		for(String str : this.mts.getActions()) {
			if(str.compareTo(s) == 0)
				return str;
		}
		return null;
	}
}