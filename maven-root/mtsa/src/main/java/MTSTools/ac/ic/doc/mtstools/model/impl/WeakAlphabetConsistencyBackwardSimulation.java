package MTSTools.ac.ic.doc.mtstools.model.impl;

import static MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType.POSSIBLE;
import static MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType.REQUIRED;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections15.CollectionUtils;

import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;

public class WeakAlphabetConsistencyBackwardSimulation<A> extends
		AbstractBackwardSimulation<A> implements Simulation<A>{

	private Set<A> silentActions;
	private Set<A> nonSharedActions;
	private Set<A> commonAlphabet;
	
	public WeakAlphabetConsistencyBackwardSimulation(MTS<?,A> m, MTS<?,A> n, Set<A> silentActions) {
		super(REQUIRED,POSSIBLE);
		this.setSilentActions(silentActions);
		
		Set<A> nonSharedActions = new HashSet<A>(CollectionUtils.subtract(m.getActions(), n.getActions()));
		nonSharedActions.addAll(this.getSilentActions());
		this.setNonSharedActions(nonSharedActions);
		
		Set<A> commonAlphabet = new HashSet<A>(CollectionUtils.intersection(m.getActions(), n.getActions()));
		commonAlphabet.addAll(silentActions);
		this.setCommonAlphabet(commonAlphabet);
	}
	
	@Override
	protected <S> Iterator<List<Pair<A, S>>> getPaths(MTS<S, A> mts, S state, A label) {
		if (this.getCommonAlphabet().contains(label)) {
			return PathBuilder.getInstance().getPathsIterator(mts, state, label, this.getSimulatorTransitionType(), this.getNonSharedActions());
		} else {
			A silentAction = this.getSilentActions().iterator().next();											
			return ClousurePathBuilder.getInstance().getPathsIterator(mts, state, silentAction, this.getSimulatorTransitionType(), this.getSilentActions());
		}
	}

	@Override
	protected <S1, S2> boolean simulate(S2 s2, Pair<A, S2> transition, S1 s1,
			List<Pair<A, S1>> path, Set<Pair<S1, S2>> relation) {
		
		S1 prevIntermidiateState = null;
		Iterator<Pair<A,S1>> it = path.iterator();
		Pair<?,S1> pathTransition = it.next(); 
		
		while(it.hasNext()) {
			pathTransition = it.next();
			Object action = pathTransition.getFirst();
			if (!this.getSilentActions().contains(action)) {
				if (prevIntermidiateState!=null &&
					!relation.contains(Pair.create(prevIntermidiateState,s2))) {				
					return false;
				}
				prevIntermidiateState = pathTransition.getSecond();
			}			
		}
		
		return relation.contains(Pair.create(pathTransition.getSecond(), transition.getSecond()));
	}

	private Set<A> getSilentActions() {
		return this.silentActions;
	}

	@SuppressWarnings("unchecked")
	private void setSilentActions(Set<A> silentActions) {
		if (silentActions == null || silentActions.isEmpty()) {
			this.silentActions = (Set<A>) Collections.singleton(new Object()); // hack since silentActions shouldn't be typed
		} else {
			this.silentActions = silentActions;
		}
	}

	private Set<A> getNonSharedActions() {
		return nonSharedActions;
	}

	private void setNonSharedActions(Set<A> nonSharedActions) {
		this.nonSharedActions = nonSharedActions;
	}

	private Set<A> getCommonAlphabet() {
		return commonAlphabet;
	}

	private void setCommonAlphabet(Set<A> commonAlphabet) {
		this.commonAlphabet = commonAlphabet;
	}


}
