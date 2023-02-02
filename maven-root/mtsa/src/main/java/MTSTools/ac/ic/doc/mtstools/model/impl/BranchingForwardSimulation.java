package MTSTools.ac.ic.doc.mtstools.model.impl;

import static MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType.REQUIRED;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;

public class BranchingForwardSimulation<A> extends AbstractForwardSimulation<A> {

	private Set<A> silentActions;

	public BranchingForwardSimulation(Set<A> silentActions) {
		this(silentActions, REQUIRED, REQUIRED);
	}
	
	public BranchingForwardSimulation(Set<A> silentActions, MTS.TransitionType actualTransitionType, MTS.TransitionType simulatorTransitionType) {
		super(actualTransitionType, simulatorTransitionType);
		this.silentActions = silentActions;
	}
	
	
	@Override
	protected <S> Iterator<List<Pair<A, S>>> getPaths(MTS<S, A> mts, S state, A label) {
		return PathBuilder.getInstance().getPathsIterator(mts, state, label, this.getSimulatorTransitionType(), this.silentActions);
	}

	@Override
	protected <S1,S2> boolean simulate(S1 s1, Pair<A, S1> transition, S2 s2, List<Pair<A, S2>> path, Set<Pair<S1, S2>> relation) {
		Iterator<Pair<A,S2>> it = path.iterator();
		Pair<?,S2> pathTransition = it.next();
		
		while(it.hasNext()) {
			if (!relation.contains(Pair.create(s1,pathTransition.getSecond()))) {
				return false;
			}
			pathTransition = it.next();
		}
		
		return relation.contains(Pair.create(transition.getSecond(), pathTransition.getSecond()));
	}

}
