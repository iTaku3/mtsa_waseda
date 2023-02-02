package MTSTools.ac.ic.doc.mtstools.model.impl;

import java.util.List;
import java.util.Vector;

import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;

public class CompositionRuleApplier implements TransitionRulesApplier {

	/**
	 * TODO comment
	 * 
	 * @see ac.ic.doc.mtstools.model.impl.TransitionRulesApplier#applyCompositionRules(ac.ic.doc.commons.relations.Pair, ac.ic.doc.commons.relations.Pair)
	 */
	public TransitionType applyCompositionRules(Pair<Long, TransitionType> transitionOnActualAction,
			Pair<Vector<Long>, TransitionType> acumulatedState) {
		TransitionType compositeTransitionType;
		if (acumulatedState.getSecond() == TransitionType.REQUIRED
				&& transitionOnActualAction.getSecond() == TransitionType.REQUIRED) {
			compositeTransitionType = TransitionType.REQUIRED;
		} else {
			compositeTransitionType = TransitionType.MAYBE;
		}
		return compositeTransitionType;
	}

	public TransitionType applyCompositionRules(TransitionType transitionType) {
		return transitionType;
	}

	public <State, Action> boolean composableModels(List<MTS<State, Action>> mtss){
		return true;
	}

	public <State, Action> boolean composableStates(List<MTS<State, Action>> mtss, CompositionState state) {
		return true;
	}

	public <State, Action> void cleanUp(MTS<State, Action> mts) {
		//do nothing
	}
}