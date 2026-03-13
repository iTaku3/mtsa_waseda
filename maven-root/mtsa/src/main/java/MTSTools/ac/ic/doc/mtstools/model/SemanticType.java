package MTSTools.ac.ic.doc.mtstools.model;

import java.util.Collections;
import java.util.Set;

import MTSTools.ac.ic.doc.mtstools.model.impl.AlphabetSemantics;
import MTSTools.ac.ic.doc.mtstools.model.impl.BranchingAlphabetConsistency;
import MTSTools.ac.ic.doc.mtstools.model.impl.BranchingConsistency;
import MTSTools.ac.ic.doc.mtstools.model.impl.BranchingSemantics;
import MTSTools.ac.ic.doc.mtstools.model.impl.WeakAlphabetConsistency;
import MTSTools.ac.ic.doc.mtstools.model.impl.WeakConsistency;
import MTSTools.ac.ic.doc.mtstools.model.impl.WeakSemantics;
import MTSTools.ac.ic.doc.mtstools.model.operations.Consistency;

public enum SemanticType {
	STRONG {
		public RefinementByRelation getRefinement() {
			return getRefinement(Collections.EMPTY_SET);
		}
		public RefinementByRelation getRefinement(Set<?> silentAction) {
			return new WeakSemantics(Collections.EMPTY_SET);
		}
		public Consistency getConsistency() {
			return getConsistency(Collections.EMPTY_SET);
		}
		public Consistency getConsistency(Set<?> silentAction) {
			return new WeakConsistency(Collections.EMPTY_SET);
		}
				
	}, 
	WEAK {
		public RefinementByRelation getRefinement() {
			return getRefinement(Collections.singleton(MTSConstants.TAU));
		}
		public RefinementByRelation getRefinement(Set<?> silentAction) {
			return new WeakSemantics(silentAction);
		}
		public Consistency getConsistency() {
			return getConsistency(Collections.singleton(MTSConstants.TAU));
		}
		public Consistency getConsistency(Set<?> silentAction) {
			return new WeakConsistency(silentAction);
		}
	},
	BRANCHING {
		public RefinementByRelation getRefinement() {
			return getRefinement(Collections.singleton(MTSConstants.TAU)); 
		}
		public RefinementByRelation getRefinement(Set<?> silentActions) {
			return new BranchingSemantics(silentActions);
		}
		public Consistency getConsistency() {
			return getConsistency(Collections.singleton(MTSConstants.TAU));
		}
		public Consistency getConsistency(Set<?> silentAction) {
			return new BranchingConsistency(silentAction);
		}
	},
	WEAK_ALPHABET {
		public RefinementByRelation getRefinement() {
			return getRefinement(Collections.singleton(MTSConstants.TAU));
		}
		public RefinementByRelation getRefinement(Set<?> silentAction) {
			return new AlphabetSemantics(silentAction, WEAK);
		}
		public Consistency getConsistency() {
			return getConsistency(Collections.singleton(MTSConstants.TAU));
		}
		public Consistency getConsistency(Set<?> silentAction) {
			return new WeakAlphabetConsistency(silentAction);
		}
	},
	BRANCHING_ALPHABET {
		public RefinementByRelation getRefinement() {
			return getRefinement(Collections.singleton(MTSConstants.TAU));
		}
		public RefinementByRelation getRefinement(Set<?> silentAction) {
			return new AlphabetSemantics(silentAction, BRANCHING);
		}
		public Consistency getConsistency() {
			return getConsistency(Collections.singleton(MTSConstants.TAU));
		}
		public Consistency getConsistency(Set<?> silentAction) {
			return new BranchingAlphabetConsistency(silentAction);
		}
	};
	public abstract RefinementByRelation getRefinement();
	public abstract RefinementByRelation getRefinement(Set<?> silentActions);
	
	public abstract Consistency getConsistency();
	public abstract Consistency getConsistency(Set<?> silentActions);
	
	
}


