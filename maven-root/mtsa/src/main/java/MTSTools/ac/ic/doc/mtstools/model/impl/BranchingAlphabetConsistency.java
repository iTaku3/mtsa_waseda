package MTSTools.ac.ic.doc.mtstools.model.impl;

import static MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType.POSSIBLE;
import static MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType.REQUIRED;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections15.CollectionUtils;

import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.operations.Consistency;

public class BranchingAlphabetConsistency extends AbstractConsistencyRelation
		implements Consistency {

	private Set<?> silentActions;

	public BranchingAlphabetConsistency(Set<?> silentActions) {
		this.setSilentActions(silentActions);
	}

	public Set<?> getSilentActions() {
		return silentActions;
	}

	public void setSilentActions(Set<?> silentActions) {
		this.silentActions = silentActions;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected <S1, S2, A> FixedPointRelationConstructor getRelationContructor(
			MTS<S1, A> mtsA, MTS<S2, A> mtsB) {

		Set extendedSiletActions = new HashSet(this.getSilentActions());
		extendedSiletActions.addAll(CollectionUtils.subtract(mtsA.getActions(),
				mtsB.getActions()));
		extendedSiletActions.addAll(CollectionUtils.subtract(mtsB.getActions(),
				mtsA.getActions()));

		return new FixedPointRelationConstructor(new SimulationChain().add(
				new BranchingForwardSimulation(extendedSiletActions, REQUIRED,
						POSSIBLE)).add(
				new BranchingBackwardSimulation(extendedSiletActions, REQUIRED,
						POSSIBLE)));
	}

	/*
	 * @Override public <S, A> boolean areConsisten(List<? extends MTS<S,A>>
	 * mtss) { List<S> initialStates = new ArrayList<S>(mtss.size()); for (MTS<S,A>
	 * mts : mtss) { initialStates.add(mts.getInitialState()); } return
	 * this.getConsistencyRelation(mtss).contains(initialStates); }
	 * 
	 * @Override public <S, A> NAryRelation<S> getConsistencyRelation( List<?
	 * extends MTS<S, A>> mtss) { return new ConsistencyRelationConstructor<S,A>(mtss).getConsistencyRelation(); }
	 * 
	 * 
	 * private class ConsistencyRelationConstructor<S, A> {
	 * 
	 * private List<List<PathBuilder2<S, A>>> paths; private NAryRelation<S>
	 * result;
	 * 
	 * public ConsistencyRelationConstructor(List<? extends MTS<S, A>> mtss) {
	 * Set<A> allTransitions = new HashSet<A>();
	 *  // Creates a universal n-ary relation across all the states List<Set<S>>
	 * statesList = new ArrayList<Set<S>>(mtss.size()); for (MTS<S,A> mts :
	 * mtss) { statesList.add(mts.getStates());
	 * allTransitions.addAll(mts.getActions()); } this.result = new
	 * UniversalNAryRelation<S>(statesList);
	 *  // Calculates all the possible path paths = new ArrayList<List<PathBuilder2<S,A>>>(mtss.size());
	 * for (MTS<S,A> mts2 : mtss) { paths }
	 * 
	 *  // Starts filtering the result relation until it reaches a fix-point int
	 * oldSize, size = result.size(); do { oldSize = size;
	 * 
	 * for (int i = 0; i < mtss.size(); i++) { result = filter(mtss, i, result); }
	 * 
	 * size = result.size(); } while (oldSize != size);
	 * 
	 *  }
	 * 
	 * private NAryRelation<S> filter(Collection<? extends MTS<S, A>> mtss,
	 * int i, NAryRelation<S> result2) { // TODO Auto-generated method stub
	 * return null; }
	 * 
	 * public NAryRelation<S> getConsistencyRelation() { return this.result; }
	 *  }
	 */

}
