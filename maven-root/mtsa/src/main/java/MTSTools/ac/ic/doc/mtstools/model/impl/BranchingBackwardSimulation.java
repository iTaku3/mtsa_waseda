package MTSTools.ac.ic.doc.mtstools.model.impl;

import static MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType.POSSIBLE;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;

public class BranchingBackwardSimulation<A> extends AbstractBackwardSimulation<A> {

	private Set<A> silentActions;

	public BranchingBackwardSimulation(Set<A> silentActions) {
		this(silentActions, POSSIBLE, POSSIBLE);
	}

	public BranchingBackwardSimulation(Set<A> silentActions, MTS.TransitionType actualTransitionType, MTS.TransitionType simulatorTransitionType) {
		super(actualTransitionType, simulatorTransitionType);
		this.silentActions = silentActions;
	}
	
	
	@Override
	protected <S> Iterator<List<Pair<A, S>>> getPaths(MTS<S, A> mts, S state, A label) {
		return PathBuilder.getInstance().getPathsIterator(mts,state,label,this.getSimulatorTransitionType(), this.silentActions);
	}

	@Override
	protected <S1,S2> boolean simulate(S2 s2, Pair<A, S2> transition, S1 s1, List<Pair<A, S1>> path, Set<Pair<S1, S2>> relation) {
		Iterator<Pair<A,S1>> it = path.iterator();
		Pair<A,S1> pathTransition = it.next();
		
		while(it.hasNext()) {
			if (!relation.contains(Pair.create(pathTransition.getSecond(),s2))) {
				return false;
			}
			pathTransition = it.next();
		}
		
		return relation.contains(Pair.create(pathTransition.getSecond(), transition.getSecond()));
	}



}
