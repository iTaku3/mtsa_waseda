package MTSTools.ac.ic.doc.mtstools.model.impl;

import java.util.EnumMap;
import java.util.Map;

import MTSTools.ac.ic.doc.commons.collections.HierarchicalMap;
import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;
import MTSTools.ac.ic.doc.commons.relations.MapSetBinaryRelation;
import MTSTools.ac.ic.doc.mtstools.model.MTS;

public class MTSQuickClone<State, Action> extends MTSImpl<State, Action> {

	public MTSQuickClone(MTS<State, Action> mts) {
		super(mts.getInitialState());
		this.addActions(mts.getActions());
		this.addStates(mts.getStates());
		
		//adds transitions
		EnumMap<TransitionType, Map<State, BinaryRelation<Action, State>>> trasitionsByType;
		trasitionsByType = new EnumMap<TransitionType, Map<State, BinaryRelation<Action, State>>>(TransitionType.class);
		for (TransitionType type : TransitionType.values()) {
			HierarchicalMap<State, BinaryRelation<Action, State>> transitions = new HierarchicalMap<State, BinaryRelation<Action, State>>();
			transitions.setParent(mts.getTransitions(type));
			trasitionsByType.put(type, transitions);
		}
		this.setTransitionsByType(trasitionsByType);
	}

	@Override
	protected BinaryRelation<Action, State> getTransitionsForInternalUpdate(State state, TransitionType type) {
		HierarchicalMap<State, BinaryRelation<Action, State>> transitions = (HierarchicalMap<State, BinaryRelation<Action, State>>) this.getTransitions(type);
		if (transitions.isKeyInherited(state)) {
			for (TransitionType t : TransitionType.values()) {
				BinaryRelation<Action, State> newRelation = new MapSetBinaryRelation<Action, State>();
				newRelation.addAll(this.getTransitions(t).get(state));
				this.getTransitions(t).put(state, newRelation);
			}	
		}
		return this.getTransitions(state, type);
	}
	
	/**
	 * Ensures that the transitions over that state are deep cloned from its parent.
	 * @param state
	 */
	public void deepClone(State state) {
		this.getTransitionsForInternalUpdate(state, TransitionType.REQUIRED);
	}
	

}
