package MTSTools.ac.ic.doc.mtstools.model.impl;

import java.util.Vector;

import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;

public class PlusCRRulesApplier extends PlusOperatorApplier {

	public TransitionType applyCompositionRules(Pair<Long, TransitionType> transitionOnActualAction,
			Pair<Vector<Long>, TransitionType> acumulatedState) {
		TransitionType compositeTransitionType;
		if (acumulatedState.getSecond() == TransitionType.MAYBE
				&& transitionOnActualAction.getSecond() == TransitionType.MAYBE) {
			compositeTransitionType = TransitionType.MAYBE;
		} else {
			compositeTransitionType = TransitionType.REQUIRED;
		}
		return compositeTransitionType;
	}

	public TransitionType applyCompositionRules(TransitionType transitionType) {
		return TransitionType.REQUIRED;
	}

}
