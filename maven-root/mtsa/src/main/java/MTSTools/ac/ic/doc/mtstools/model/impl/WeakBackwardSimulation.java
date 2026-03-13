package MTSTools.ac.ic.doc.mtstools.model.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;

import static MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType.POSSIBLE;

public class WeakBackwardSimulation<A> extends AbstractBackwardSimulation<A> {

	private Set<A> silentActions;

	public WeakBackwardSimulation(Set<A> silentActions) {
		this(silentActions, POSSIBLE, POSSIBLE);
	}
	
	public WeakBackwardSimulation(Set<A> silentActions, MTS.TransitionType actualTransitionType, MTS.TransitionType simulatorTransitionType) {
		super(actualTransitionType, simulatorTransitionType);
		this.silentActions = silentActions;
	}
	
	
	
	@Override
	protected <S> Iterator<List<Pair<A, S>>> getPaths(MTS<S, A> mts, S state, A label) {
		return ClousurePathBuilder.getInstance().getPathsIterator(mts,state,label,this.getSimulatorTransitionType(), this.silentActions);
	}

	@Override
	protected <S1,S2> boolean simulate(S2 s2, Pair<A, S2> transition, S1 s1, List<Pair<A, S1>> path, Set<Pair<S1, S2>> relation) {
		Pair<A,S1> lastTransition = path.get(path.size()-1);
				
		return relation.contains(Pair.create(lastTransition.getSecond(), transition.getSecond()));
	}
}
