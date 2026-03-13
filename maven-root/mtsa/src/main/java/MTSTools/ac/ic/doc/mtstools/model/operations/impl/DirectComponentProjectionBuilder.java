package MTSTools.ac.ic.doc.mtstools.model.operations.impl;

import java.util.Map;
import java.util.Set;

import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTSConstants;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;
import MTSTools.ac.ic.doc.mtstools.model.impl.MTSImpl;
import MTSTools.ac.ic.doc.mtstools.model.operations.ComponentProjectionBuilder;

/**
 * @author gsibay
 *
 */
public class DirectComponentProjectionBuilder implements
		ComponentProjectionBuilder {

	/* (non-Javadoc)
	 * @see ac.ic.doc.mtstools.model.operations.ComponentProjectionBuilder#buildComponentProjection(ac.ic.doc.mtstools.model.MTS, java.util.Set)
	 */
	@Override
	public MTS<Long, String> buildComponentProjection(
			MTS<Long, String> centralisedMTS, Set<String> componentActions) {

		MTS<Long, String> component = new MTSImpl<Long, String>( new Long(centralisedMTS.getInitialState().longValue()));
		
		Map<Long, BinaryRelation<String, Long>> maybeTransitions = centralisedMTS.getTransitions(TransitionType.MAYBE);
		
		// the actions that are not part of the componet are replaced by a TAU
		for (Long centralisedState : centralisedMTS.getStates()) {
			
			// create and add the state in the component
			Long componentState = new Long(centralisedState.longValue()); 
			component.addState(componentState);
			
			BinaryRelation<String, Long> maybeTransitionsForState = maybeTransitions.get(centralisedState);
			
			for (Pair<String, Long> transition : centralisedMTS.getTransitions(centralisedState, TransitionType.POSSIBLE)) {
				
				String transitionLabel = transition.getFirst();
				
				if(!componentActions.contains(transitionLabel)) {
					//this transition is not part of the component's alphabet. The transition is hidden
					
					Long targetState = transition.getSecond();
					Long targetComponentState = new Long(targetState.longValue());
					
					component.addAction(MTSConstants.TAU);
					
					// If the transition is maybe, then add a tau maybe. If not add a required one
					if(maybeTransitionsForState.getImage(transitionLabel).contains(targetState)) {
						component.addPossible(componentState, MTSConstants.TAU, targetComponentState);
					} else {
						component.addRequired(componentState, MTSConstants.TAU, targetComponentState);
					}
				}
				

			}
			
		}
		return component;
	}
}
