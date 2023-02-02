package MTSTools.ac.ic.doc.mtstools.model.impl;

import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;
import MTSTools.ac.ic.doc.mtstools.model.Refinement;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class BranchingByTransformationSemantics implements Refinement {

	/**
	 * @uml.property  name="refinement"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private Refinement refinement;
	/**
	 * @uml.property  name="expansion"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private BranchingExpansion expansion;
		
	/**
	 * @param silentActions
	 */
	public BranchingByTransformationSemantics(Set<?> silentActions) {
		this.setRefinement(new BranchingSemantics(silentActions));
		this.setExpansion(new BranchingExpansion(silentActions));
		
	}


	public <S1, S2, A> boolean isARefinement(MTS<S1, A> m, MTS<S2, A> n) {
		MTS<Integer,A> nExpanded = this.getExpansion().transform(n);
		return this.getRefinement().isARefinement(m,nExpanded);
	}

	public <S1, S2, A> BinaryRelation<S1, S2> getRefinement(MTS<S1, A> m, MTS<S2, A> n) {
		MTS<Integer,A> nExpanded = this.getExpansion().transform(n);
		return (BinaryRelation<S1, S2>) this.getRefinement().getRefinement(m,nExpanded);
	}



	/**
	 * @return  Returns the expansion.
	 * @uml.property  name="expansion"
	 */
	protected BranchingExpansion getExpansion() {
		return expansion;
	}

	/**
	 * @param expansion  The expansion to set.
	 * @uml.property  name="expansion"
	 */
	protected void setExpansion(BranchingExpansion expansion) {
		this.expansion = expansion;
	}

	/**
	 * @return  Returns the refinement.
	 * @uml.property  name="refinement"
	 */
	protected Refinement getRefinement() {
		return this.refinement;
	}

	/**
	 * @param refinement  The refinement to set.
	 * @uml.property  name="refinement"
	 */
	protected void setRefinement(Refinement refinement) {
		this.refinement = refinement;
	}

	/**
	 * @author  srdipi
	 */
	public static class BranchingExpansion {

		Set<?> silentActions;
		
		public BranchingExpansion(Set<?> silentActions) {
			this.setSilentActions(silentActions);
		}

		public <S,A> MTS<Integer, A> transform(MTS<S, A> input) {
			A silentAction = null;
			if ( !silentActions.isEmpty()) {
				silentAction = (A) this.getSilentActions().iterator().next();
			}
			
			int nextState = 0;
			Map<S,Integer> state2int = new HashMap<S,Integer>(input.getStates().size());
			for (S state : input.getStates()) {
				state2int.put(state,nextState++);
			}
						
			MTS<Integer,A> result = new MTSImpl<Integer,A>(state2int.get(input.getInitialState()));
			result.addActions(input.getActions());
			result.addAction(silentAction);
			
			
			for (S state : input.getStates()) {
				result.addState(state2int.get(state));
			}
			
			for (S source : input.getStates()) {
				Integer iSource = state2int.get(source);
				for(Pair<A,S> transition : input.getTransitions(source,TransitionType.REQUIRED)) {
					result.addTransition(iSource,transition.getFirst(), state2int.get(transition.getSecond()), TransitionType.REQUIRED);
				}
				for(Pair<A,S> transition : input.getTransitions(source,TransitionType.MAYBE)) {
					if (silentActions.contains(transition.getFirst())) {
						result.addTransition(iSource,transition.getFirst(), state2int.get(transition.getSecond()), TransitionType.MAYBE);
					} else {
						Integer proxyState = nextState++;
						result.addState(proxyState);
						
						result.addTransition(iSource,silentAction, proxyState, TransitionType.MAYBE);
						result.addTransition(proxyState,silentAction,iSource,TransitionType.REQUIRED);
						result.addTransition(proxyState,transition.getFirst(),state2int.get(transition.getSecond()),TransitionType.REQUIRED);
					}
				}
			}
						
			
			return result;	
		}

		/**
		 * @return  Returns the silentActions.
		 * @uml.property  name="silentActions"
		 */
		protected Set<?> getSilentActions() {
			return silentActions;
		}
		
		/**
		 * @param silentActions  The silentActions to set.
		 * @uml.property  name="silentActions"
		 */
		protected void setSilentActions(Set<?> silentActions) {
			this.silentActions = silentActions;
		}		
	}
	
}
