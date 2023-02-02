package MTSTools.ac.ic.doc.mtstools.model.impl;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.facade.MTSAFacade;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.operations.Consistency;

public abstract class PlusOperatorApplier implements TransitionRulesApplier {

	private Consistency consistency;
	private Set largestRelation;
	
	public PlusOperatorApplier() {
		consistency = new WeakConsistency(Collections.emptySet());
	}
	
	public <State, Action> boolean composableModels(List<MTS<State, Action>> mtss) {
		if (mtss.size()>2){
			throw new IllegalArgumentException("Plus operator can only works with 2 models.");
		}
		Iterator<MTS<State, Action>> iterator = mtss.iterator();
		MTS<State, Action> mtsA = iterator.next();
		MTS<State, Action> mtsB = iterator.next();
		return consistency.areConsistent(mtsA, mtsB);
	}
	private <State, Action> Set getConsistecyRelation(List<MTS<State, Action>> mtss){
		if (mtss.size()>2){
			throw new IllegalArgumentException("Plus operator can only works with 2 models.");
		}
		Iterator<MTS<State, Action>> iterator = mtss.iterator();
		MTS<State, Action> mtsA = iterator.next();
		MTS<State, Action> mtsB = iterator.next();
		
		if (largestRelation==null){
			largestRelation = consistency.getConsistencyRelation(mtsA, mtsB);
		}
		return largestRelation;
	}
	
	public <State, Action> boolean composableStates(List<MTS<State, Action>> mtss, CompositionState state) {
		if (mtss.size()!=2) {
			throw new IllegalArgumentException("+CR operator only works with a pair of models.");
		}
		List<Long> individualStates = state.getIndividualStates();
		Set consistecyRelation = this.getConsistecyRelation(mtss);
		return consistecyRelation.contains(Pair.create(individualStates.get(0), individualStates.get(1)));
	}
	
	public <State, Action> void cleanUp(MTS<State, Action> mts) {
		mts.removeUnreachableStates();
		MTSAFacade.deleteTransitionsToDeadlock((MTS<Long, String>) mts);
		if (mts.getStates().isEmpty()){
			throw new RuntimeException("There isn't a deadlock Free Model.");
		}
	}
}
