package MTSSynthesis.ac.ic.doc.distribution;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.PredicateUtils;
import org.apache.commons.collections15.SetUtils;

import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSSynthesis.ac.ic.doc.distribution.model.AlphabetDistribution;
import MTSSynthesis.ac.ic.doc.distribution.model.ComponentBuiltFromDistribution;
import MTSSynthesis.ac.ic.doc.distribution.model.DeterminisationModalityMismatch;
import MTSSynthesis.ac.ic.doc.distribution.model.DistributionFeedbackOnFullAlphabet;
import MTSSynthesis.ac.ic.doc.distribution.model.DistributionFeedbackOnModalityMismatch;
import MTSSynthesis.ac.ic.doc.distribution.model.DistributionFeedbackOnNonDeterminism;
import MTSSynthesis.ac.ic.doc.distribution.model.DistributionFeedbackOnTracesAddedByComposition;
import MTSSynthesis.ac.ic.doc.distribution.model.DistributionResult;
import MTSSynthesis.ac.ic.doc.distribution.model.DistributionResultImpl;
import MTSTools.ac.ic.doc.mtstools.facade.MTSAFacade;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;
import MTSTools.ac.ic.doc.mtstools.model.MTSConstants;
import MTSTools.ac.ic.doc.mtstools.model.MTSTrace;
import MTSTools.ac.ic.doc.mtstools.model.MTSTransition;
import MTSTools.ac.ic.doc.mtstools.model.impl.CompositionRuleApplier;
import MTSTools.ac.ic.doc.mtstools.model.impl.LinkedListMTSTrace;
import MTSTools.ac.ic.doc.mtstools.model.impl.MTSMultipleComposer;
import MTSTools.ac.ic.doc.mtstools.model.impl.MTSTransitionWithModalityInfo;
import MTSTools.ac.ic.doc.mtstools.model.predicates.HasNonDeterministicTransitionsStatePredicate;
import MTSTools.ac.ic.doc.mtstools.utils.GenericMTSToLongStringMTSConverter;
import MTSTools.ac.ic.doc.mtstools.utils.MTSUtils;

/**
 * @author gsibay
 *
 */
public class DistributionAlgorithmDefaultImpl<S, A> implements DistributionAlgorithm<S, A> {

	@Override
	public DistributionResult<S, A> tryDistribute(MTS<S, A> monolithicModel, AlphabetDistribution<A> alphabetDistribution, A tauAction) {
		DistributionResult<S, A> distributionResult = new DistributionResultImpl<S, A>(monolithicModel);
		boolean isDistributable = false;
		boolean validPreconditions = this.validatePreconditionsAndAddFeedback(monolithicModel, alphabetDistribution, distributionResult, tauAction);  
		if (validPreconditions) {
			isDistributable = this.doTryDistribute(monolithicModel, alphabetDistribution, distributionResult);
		}
		
		distributionResult.setDistributable(isDistributable);
		return distributionResult;
	}

	private boolean doTryDistribute(MTS<S, A> systemModel, AlphabetDistribution<A> alphabetDistribution, 	
			DistributionResult<S, A> distributionResult) {
		Set<Set<A>> componentsAlphabet = alphabetDistribution.getAlphabets();
		
		ComponentBuilder<S, A> componentBuilder = new ComponentBuilder<S, A>();
		
		Set<A> fullAlphabet = alphabetDistribution.getFullAlphabet();
		Map<Set<A>, Set<DeterminisationModalityMismatch<S, A>>> mismatchesByComponentAlphabet = new HashMap<Set<A>, Set<DeterminisationModalityMismatch<S, A>>>();
		Map<Set<A>, MTS<Set<S>, A>> componentByComponentAlphabet = new HashMap<Set<A>, MTS<Set<S>,A>>();
		for (Set<A> componentAlphabet : componentsAlphabet) {
			ComponentBuiltFromDistribution<S, A> builtComponentResult = componentBuilder.buildComponent(systemModel, componentAlphabet, fullAlphabet);
			componentByComponentAlphabet.put(componentAlphabet, builtComponentResult.getComponent());
			mismatchesByComponentAlphabet.put(componentAlphabet, builtComponentResult.getDeterminisationModalityMismatches());
			distributionResult.setComponent(componentAlphabet, builtComponentResult.getComponent());
		}
		
		// Check modal inconsistencies and add to feedback report
		boolean isModalConsistent = this.checkModalConsistencyAndAddFeedback(systemModel, mismatchesByComponentAlphabet, alphabetDistribution, componentByComponentAlphabet, distributionResult);
		
		// Check if there is the language of the composition is bigger than the language of the monolithic model. If so add to feedback. 
		boolean doesCompositionOfComponentAddMoreBehaviour = this.checkIfComponentsAddNewTracesAndAddFeedback(systemModel, componentByComponentAlphabet, distributionResult);
		
		boolean isDistributable = isModalConsistent && !doesCompositionOfComponentAddMoreBehaviour;
		
		return isDistributable;
	}

	private boolean checkIfComponentsAddNewTracesAndAddFeedback(MTS<S, A> systemModel, Map<Set<A>, MTS<Set<S>, A>> componentByComponentAlphabet,
			DistributionResult<S, A> distributionResult) {
		boolean compositionAddsMoreBehaviour = false;
		//XXX: the composer is not generic enough so far. 
		CompositionRuleApplier compositionRuleApplier = new CompositionRuleApplier();
		List<MTS<Long, String>> componentsAsMTSLongString = new LinkedList<MTS<Long, String>>();
		
		CollectionUtils.collect(componentByComponentAlphabet.values(), new GenericMTSToLongStringMTSConverter<Set<S>, A>(), componentsAsMTSLongString);
		
		MTS<Long, String> compositionResult = new MTSMultipleComposer<S, String>(compositionRuleApplier).compose(componentsAsMTSLongString);
		
		//XXX the system Model is transformed because the composer is not generic...
		MTS<Long, String> transformedSystemModel = new GenericMTSToLongStringMTSConverter<S, A>().transform(systemModel);
		
		Set<MTSTrace<String, Long>> tracesAddedByComposition = this.getTracesAddedByComposition(compositionResult.getInitialState(), compositionResult, transformedSystemModel.getInitialState(), transformedSystemModel, new HashSet<Pair<Long,Long>>());
		if(tracesAddedByComposition.size()!= 0) {
			distributionResult.addFeedbackItem(new DistributionFeedbackOnTracesAddedByComposition<Long, String>(tracesAddedByComposition));
			compositionAddsMoreBehaviour = true;
		}
		return compositionAddsMoreBehaviour;
	}

	/**
	 * 
	 * @param currentStateInComposition
	 * @param mts
	 * @param traces
	 * @param visited
	 * @return
	 */
	private Set<MTSTrace<String, Long>> getTracesAddedByComposition(Long currentStateInComposition, MTS<Long, String> compositionMTS, Long currentStateInSystemModel, 
			MTS<Long, String> systemModel,  Set<Pair<Long,Long>> visited) {
		//XXX This method should and could be easily made generic but that depends on MTSMultipleComposer be made generic.  
		Set<MTSTrace<String, Long>> result = new HashSet<MTSTrace<String, Long>>();
		visited.add(Pair.create(currentStateInComposition, currentStateInSystemModel));
		
		Iterator<Pair<String, Long>> transitionsFromCurrentStateInCompositionIt = compositionMTS.getTransitions(currentStateInComposition, TransitionType.POSSIBLE).iterator();
		while (transitionsFromCurrentStateInCompositionIt.hasNext()) {
			Pair<String, Long> transitionFromCurrentStateInComposition = transitionsFromCurrentStateInCompositionIt.next();
			Long nextStateInComposition = transitionFromCurrentStateInComposition.getSecond();

			// get the transitions of the original model on the current action
			Set<Long> transitionsFromCurrentStateInSystemModel = 
					systemModel.getTransitions(currentStateInSystemModel, TransitionType.POSSIBLE).getImage(transitionFromCurrentStateInComposition.getFirst());

			boolean isMaybeTransition;
			if(compositionMTS.getTransitions(currentStateInComposition, TransitionType.MAYBE).contains(transitionFromCurrentStateInComposition)) {
				isMaybeTransition = true;
			} else {
				isMaybeTransition = false;
			}
			MTSTransition<String, Long> transitionFromCurrentToNextStateInComposition = 
					new MTSTransitionWithModalityInfo<String, Long>(currentStateInComposition, transitionFromCurrentStateInComposition.getFirst(), nextStateInComposition, isMaybeTransition);
			
			if(transitionsFromCurrentStateInSystemModel.isEmpty()) {
				// this is a transition not present in the original model, add it to the result as a single transition trace.
				LinkedListMTSTrace<String, Long> traceNotPresentInSystemModel = new LinkedListMTSTrace<String, Long>();
				traceNotPresentInSystemModel.add(transitionFromCurrentToNextStateInComposition); 
				result.add(traceNotPresentInSystemModel);
			} else {
				for (Long nextStateInSystemModel : transitionsFromCurrentStateInSystemModel) {
					if(!visited.contains(Pair.create(nextStateInComposition, nextStateInSystemModel))) {
						Set<MTSTrace<String, Long>> tracesAddedByCompositionFromNextState = 
								this.getTracesAddedByComposition(nextStateInComposition, compositionMTS, nextStateInSystemModel, systemModel, visited);
						// add the transition to the first position of each of the tracesToStateSatisfyingPredicateFromNextState and add it to the final result
						this.appendTransitionToTracesAndAddToResult(transitionFromCurrentToNextStateInComposition, tracesAddedByCompositionFromNextState, result);
					}
				}
			}
		}
		return result;
	}

	private void appendTransitionToTracesAndAddToResult(MTSTransition<String, Long> transitionFromCurrentToNextState, 
			Set<MTSTrace<String, Long>> tracesNotPresentInAFromNextState, Set<MTSTrace<String, Long>> result) {
		
		for (MTSTrace<String,Long> traceNotPresentInAFromNextState : tracesNotPresentInAFromNextState) {
			traceNotPresentInAFromNextState.addFirst(transitionFromCurrentToNextState);
			result.add(traceNotPresentInAFromNextState);
		}
	}

	
	/**
	 * Not every mismatch is a problem. If there is a component that can differentiate the candidate mismatch then 
	 * it is not a real mismatch and there is no problem
	 * 
	 * @param mismatchesByComponent
	 * @param alphabetDistribution
	 * @param componentByComponentAlphabet
	 * @param distributionResult
	 */
	private boolean checkModalConsistencyAndAddFeedback(MTS<S,A> systemModel, Map<Set<A>, Set<DeterminisationModalityMismatch<S, A>>> mismatchesByComponent,
			AlphabetDistribution<A> alphabetDistribution, Map<Set<A>, MTS<Set<S>, A>> componentByComponentAlphabet, DistributionResult<S, A> distributionResult) {

		boolean isModalConsistent = true;
		Set<Entry<Set<A>, Set<DeterminisationModalityMismatch<S, A>>>> entrySet = mismatchesByComponent.entrySet();
		
		for (Entry<Set<A>, Set<DeterminisationModalityMismatch<S, A>>> entry : entrySet) {
			Set<A> componentAlphabet = entry.getKey();
			Set<DeterminisationModalityMismatch<S, A>> mismatchesForComponent = entry.getValue();

			for (DeterminisationModalityMismatch<S, A> aMismatch : mismatchesForComponent) {
				if(this.isRealConflict(aMismatch, alphabetDistribution, componentAlphabet, componentByComponentAlphabet)) {
					DistributionFeedbackOnModalityMismatch<S, A> feedbackOnModalityMismatch = new DistributionFeedbackOnModalityMismatch<S, A>(aMismatch);
					
					feedbackOnModalityMismatch.setTraceToStateWithReqTransition(
							this.getTraceToState(aMismatch.getStateByModality(TransitionType.REQUIRED), systemModel));
					
					feedbackOnModalityMismatch.setTraceToStateWithMayTransition(
							this.getTraceToState(aMismatch.getStateByModality(TransitionType.MAYBE), systemModel));
					
					distributionResult.addFeedbackItem(feedbackOnModalityMismatch);
					isModalConsistent = false;
				}
				
			}
		}
		return isModalConsistent;
	}

	/**
	 * Returns a trace to the state. The state must be a state of the model.
	 * @param state
	 * @param systemModel
	 * @return
	 */
	private MTSTrace<A, S> getTraceToState(S state, MTS<S, A> systemModel) {
		assert systemModel.getStates().contains(state);
		return MTSUtils.getAlltracesToStatesSatisfyingPredicate(systemModel, PredicateUtils.equalPredicate(state), TransitionType.POSSIBLE).iterator().next();
	}

	private boolean isRealConflict(DeterminisationModalityMismatch<S, A> aMismatch, AlphabetDistribution<A> alphabetDistribution, Set<A> componentAlphabet, 
			Map<Set<A>, MTS<Set<S>, A>> componentByComponentAlphabet) {
		boolean isRealConflict = true;
		A action = aMismatch.getAction();

		// get the alphabets from components sharing this action.
		Set<Set<A>> alphabetsContainingAction = alphabetDistribution.getAlphabetsContainingAction(action);
		alphabetsContainingAction.remove(componentAlphabet);
		
		//for each of those component see if there is at least one that can differentiate the conflicting states
		Iterator<Set<A>> alphabetsContainingActionIt = alphabetsContainingAction.iterator();
		while(isRealConflict && alphabetsContainingActionIt.hasNext()) {
			Set<A> alphabetContainingAction = alphabetsContainingActionIt.next();
			// get the component for the current alphabet
			MTS<Set<S>, A> component = componentByComponentAlphabet.get(alphabetContainingAction);
			
			// get the pair of conflicting states and put them in a set
			Pair<S, S> conflictingStates = aMismatch.getDeterministationJointStates();
			HashSet<S> conflictingStatesSet = new HashSet<S>();
			conflictingStatesSet.add(conflictingStates.getFirst());
			conflictingStatesSet.add(conflictingStates.getSecond());
			
			// Check if the pair is contained in one of the component state
			Iterator<Set<S>> componentStatesIt = component.getStates().iterator();
			boolean includedInAState = false;
			while (!includedInAState && componentStatesIt.hasNext()) {
				// check if it is not included in the current state
				includedInAState = componentStatesIt.next().containsAll(conflictingStatesSet);						
			}
			
			// if there is each conflicting state is in different composite 
			// states then there is no real conflict.
			isRealConflict = includedInAState;
		}
		return isRealConflict;
	}

	/**
	 * 
	 * @param monolithicModel
	 * @param alphabetDistribution
	 * @param distributionResult
	 * @param tauAction 
	 * @return
	 */
	private boolean validatePreconditionsAndAddFeedback(MTS<S, A> monolithicModel, AlphabetDistribution<A> alphabetDistribution, DistributionResult<S, A> distributionResult, 
			A tauAction) {
		
		boolean result = true;
		
		// Check if the distribution has alphabets covering the whole actions of the monolithic model
		Set<A> monolithicModelActionsWithoutTau = new HashSet<A>(monolithicModel.getActions());
		monolithicModelActionsWithoutTau.remove(tauAction);
		
		if(!monolithicModelActionsWithoutTau.equals(alphabetDistribution.getFullAlphabet())) {
			distributionResult.addFeedbackItem(new DistributionFeedbackOnFullAlphabet<A>(alphabetDistribution, monolithicModelActionsWithoutTau));
			distributionResult.setDistributable(false);
			result = false;
		}
		
		// clone and apply closure to the clone with the tau action. Tau actions can cause non determinism.
		MTS<S, A> closuredMonolithicModel = MTSUtils.cloneMTS(monolithicModel);
		MTSAFacade.applyClosure(closuredMonolithicModel, new HashSet<A>(Collections.singleton(tauAction)));
		
		// Check whether the monolithic model is deterministic
		Predicate<S> hasNonDetTransitionsFromState = new HasNonDeterministicTransitionsStatePredicate<S, A>(closuredMonolithicModel, TransitionType.POSSIBLE);
		Set<MTSTrace<A, S>> tracesToNonDetStates = MTSUtils.getAlltracesToStatesSatisfyingPredicate(closuredMonolithicModel, hasNonDetTransitionsFromState, TransitionType.POSSIBLE);

		if(!tracesToNonDetStates.isEmpty()) {
			distributionResult.addFeedbackItem(new DistributionFeedbackOnNonDeterminism<S, A>(tracesToNonDetStates));
			distributionResult.setDistributable(false);
			result = false;
		}

		return result;
	}
}
