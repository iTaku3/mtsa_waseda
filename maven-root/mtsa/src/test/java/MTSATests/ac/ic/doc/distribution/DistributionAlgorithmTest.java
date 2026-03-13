package MTSATests.ac.ic.doc.distribution;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import MTSSynthesis.ac.ic.doc.distribution.DistributionAlgorithm;
import MTSSynthesis.ac.ic.doc.distribution.DistributionAlgorithmDefaultImpl;
import junit.framework.Assert;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.PredicateUtils;
import org.apache.commons.collections15.map.HashedMap;
import org.junit.Test;

import MTSATests.util.TestModels;
import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;
import MTSSynthesis.ac.ic.doc.distribution.model.AlphabetDistribution;
import MTSSynthesis.ac.ic.doc.distribution.model.DeterminisationModalityMismatch;
import MTSSynthesis.ac.ic.doc.distribution.model.DistributionFeedbackItem;
import MTSSynthesis.ac.ic.doc.distribution.model.DistributionFeedbackOnFullAlphabet;
import MTSSynthesis.ac.ic.doc.distribution.model.DistributionFeedbackOnModalityMismatch;
import MTSSynthesis.ac.ic.doc.distribution.model.DistributionFeedbackOnNonDeterminism;
import MTSSynthesis.ac.ic.doc.distribution.model.DistributionResult;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTSConstants;
import MTSTools.ac.ic.doc.mtstools.model.MTSTransition;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;
import MTSTools.ac.ic.doc.mtstools.model.MTSTrace;
import MTSTools.ac.ic.doc.mtstools.model.predicates.HasNonDeterministicTransitionsStatePredicate;
import MTSTools.ac.ic.doc.mtstools.model.predicates.IsDeterministicMTSPredicate;

/**
 * 
 * @author gsibay
 *
 */
public class DistributionAlgorithmTest {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testDistributionFeedbackOnFullAlphabetNotPresent() throws Exception {
		DistributionAlgorithm<Long ,String> distAlg = new DistributionAlgorithmDefaultImpl<Long, String>();
		DistributionResult<Long, String> distResult;
		TestModels testModelsInstance = TestModels.getInstance();
		
		MTS<Long, String> model;
		model = testModelsInstance.getMTS(3);
		
		Set<Set<String>> alphabets = new HashSet<Set<String>>();
		Set<String> alpha1 = new HashSet<String>();
		alpha1.add("a");
		
		alphabets.add(alpha1);
		
		Set<String> alpha2 = new HashSet<String>();
		alpha2.add("b");
		
		alphabets.add(alpha2);
	    
		AlphabetDistribution<String> alphabetDistribution = new AlphabetDistribution<String>(alphabets);

		distResult = distAlg.tryDistribute(model, alphabetDistribution, MTSConstants.TAU);
		
		Predicate isDistributionFeedbackOnFullAlphabet = PredicateUtils.instanceofPredicate(DistributionFeedbackOnFullAlphabet.class);
		
		Assert.assertTrue(!CollectionUtils.exists(distResult.getFeedback(), isDistributionFeedbackOnFullAlphabet));
		
		//------------
		
		model = testModelsInstance.getMTS(4);
		distResult = distAlg.tryDistribute(model, alphabetDistribution, MTSConstants.TAU);
		Assert.assertTrue(!CollectionUtils.exists(distResult.getFeedback(), isDistributionFeedbackOnFullAlphabet));
		//------------
		
		model = testModelsInstance.getMTS(6);
		distResult = distAlg.tryDistribute(model, alphabetDistribution, MTSConstants.TAU);
		Assert.assertTrue(!CollectionUtils.exists(distResult.getFeedback(), isDistributionFeedbackOnFullAlphabet));
		//------------
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testDistributionFeedbackOnFullAlphabetPresent() throws Exception {
		DistributionAlgorithm<Long ,String> distAlg = new DistributionAlgorithmDefaultImpl<Long, String>();
		DistributionResult<Long, String> distResult;
		TestModels testModelsInstance = TestModels.getInstance();
		
		MTS<Long, String> model = testModelsInstance.getMTS(0);

		// create the distributed alphabet
		Set<Set<String>> alphabets = new HashSet<Set<String>>();
		HashSet<String> alpha1 = new HashSet<String>();
		alpha1.add("a");
		alpha1.add("b");
		
		alphabets.add(alpha1);
		
		HashSet<String> alpha2 = new HashSet<String>();
		alpha2.add("b");
		alpha2.add("c");
		
		alphabets.add(alpha2);
	    
		AlphabetDistribution<String> alphabetDistribution = new AlphabetDistribution<String>(alphabets);
	    
		distResult = distAlg.tryDistribute(model, alphabetDistribution, MTSConstants.TAU);
		
		@SuppressWarnings("rawtypes")
		Predicate isDistributionFeedbackOnFullAlphabet = PredicateUtils.instanceofPredicate(DistributionFeedbackOnFullAlphabet.class);
		
		// there should be a feedback on full alphabet
		Assert.assertTrue(CollectionUtils.exists(distResult.getFeedback(), isDistributionFeedbackOnFullAlphabet));
		
		//------------

		model = testModelsInstance.getMTS(4);
		distResult = distAlg.tryDistribute(model, alphabetDistribution, MTSConstants.TAU);
		Assert.assertTrue(CollectionUtils.exists(distResult.getFeedback(), isDistributionFeedbackOnFullAlphabet));

		//-------------

		model = testModelsInstance.getMTS(5);
		distResult = distAlg.tryDistribute(model, alphabetDistribution, MTSConstants.TAU);
		Assert.assertTrue(CollectionUtils.exists(distResult.getFeedback(), isDistributionFeedbackOnFullAlphabet));

		//-------------

		model = testModelsInstance.getMTS(6);
		distResult = distAlg.tryDistribute(model, alphabetDistribution, MTSConstants.TAU);
		Assert.assertTrue(CollectionUtils.exists(distResult.getFeedback(), isDistributionFeedbackOnFullAlphabet));

		//-------------

		model = testModelsInstance.getMTS(7);
		distResult = distAlg.tryDistribute(model, alphabetDistribution, MTSConstants.TAU);
		Assert.assertTrue(CollectionUtils.exists(distResult.getFeedback(), isDistributionFeedbackOnFullAlphabet));

		//------------
		
		alphabets = new HashSet<Set<String>>();
		alpha1.add("a");
		
		alphabets.add(alpha1);
		
		alpha2 = new HashSet<String>();
		alpha2.add("b");
		
		alphabets.add(alpha2);
	    
		alphabetDistribution = new AlphabetDistribution<String>(alphabets);
		
		//-------------

		model = testModelsInstance.getMTS(5);
		distResult = distAlg.tryDistribute(model, alphabetDistribution, MTSConstants.TAU);
		Assert.assertTrue(CollectionUtils.exists(distResult.getFeedback(), isDistributionFeedbackOnFullAlphabet));

		//-------------

		model = testModelsInstance.getMTS(7);
		distResult = distAlg.tryDistribute(model, alphabetDistribution, MTSConstants.TAU);
		Assert.assertTrue(CollectionUtils.exists(distResult.getFeedback(), isDistributionFeedbackOnFullAlphabet));

	}

	
	@Test
	public void testNonDeterminismFeedback() throws Exception {

		// create a distributed alphabet
		Set<Set<String>> alphabets = new HashSet<Set<String>>();
		HashSet<String> alpha1 = new HashSet<String>();
		alpha1.add("a");
		alpha1.add("b");
		
		alphabets.add(alpha1);
		
		HashSet<String> alpha2 = new HashSet<String>();
		alpha2.add("b");
		alpha2.add("c");
		
		alphabets.add(alpha2);
	    
		AlphabetDistribution<String> alphabetDistribution = new AlphabetDistribution<String>(alphabets);
		
		this.testNonDeterminismFeedback(alphabetDistribution);
				
		//
		alphabets = new HashSet<Set<String>>();
		
		alpha1 = new HashSet<String>();
		alpha1.add("a");
		
		alphabets.add(alpha1);
		
		alpha2 = new HashSet<String>();
		alpha2.add("b");
		
		alphabets.add(alpha2);
	    
		HashSet<String> alpha3 = new HashSet<String>();
		alpha3.add("b");
		alpha3.add("d");
		alphabets.add(alpha3);

		HashSet<String> alpha4 = new HashSet<String>();
		alpha4.add("a");
		alpha4.add("d");
		alphabets.add(alpha4);

		
		alphabetDistribution = new AlphabetDistribution<String>(alphabets);
		
		this.testNonDeterminismFeedback(alphabetDistribution);
	}

	@SuppressWarnings("unchecked")
	private void testNonDeterminismFeedback(AlphabetDistribution<String> alphabetDistribution) throws Exception {
		DistributionAlgorithm<Long ,String> distAlg = new DistributionAlgorithmDefaultImpl<Long, String>();
		DistributionResult<Long, String> distResult;
		MTS<Long, String>[] allTestModels = TestModels.getInstance().getAllModels();

		@SuppressWarnings("rawtypes")
		Predicate isDistributionFeedbackOnNonDeterminism = PredicateUtils.instanceofPredicate(DistributionFeedbackOnNonDeterminism.class);

		Predicate<MTS<Long, String>> isDeterministic = new IsDeterministicMTSPredicate<Long, String>(TransitionType.POSSIBLE);
		for (MTS<Long, String> mts : allTestModels) {
			distResult = distAlg.tryDistribute(mts, alphabetDistribution, MTSConstants.TAU);
			
			Assert.assertTrue("Invalid feedback on non determinism",isDeterministic.evaluate(mts) != CollectionUtils.exists(distResult.getFeedback(), isDistributionFeedbackOnNonDeterminism));
			
			if(CollectionUtils.exists(distResult.getFeedback(), isDistributionFeedbackOnNonDeterminism)) {
				List<DistributionFeedbackItem> feedback = distResult.getFeedback();
				for (DistributionFeedbackItem distributionFeedbackItem : feedback) {
					if(isDistributionFeedbackOnNonDeterminism.evaluate(distributionFeedbackItem)) {
						DistributionFeedbackOnNonDeterminism<Long, String> feedbackOnNonDeterminism = (DistributionFeedbackOnNonDeterminism<Long, String>)distributionFeedbackItem;
						Set<MTSTrace<String, Long>> tracesToNonDeterministicStates = feedbackOnNonDeterminism.getTracesToNonDeterministicStates();
						for (MTSTrace<String, Long> mtsTrace : tracesToNonDeterministicStates) {
							this.assertIsValidTrace(mts, mtsTrace);
							
							// check if the state where the trace leads to is non deterministic
							HasNonDeterministicTransitionsStatePredicate<Long, String> hasNonDetTransitionsFromStatePredicate = 
									new HasNonDeterministicTransitionsStatePredicate<Long, String>(mts, TransitionType.POSSIBLE);
							Long traceLastState;
							if(mtsTrace.size() == 0) {
								traceLastState = mts.getInitialState();
							} else {
								traceLastState = mtsTrace.get(mtsTrace.size()-1).getStateTo();
							}
							Assert.assertTrue("Trace does not lead to non deterministic state. Trace: " + mtsTrace + "\n. MTS: " + mts, hasNonDetTransitionsFromStatePredicate.evaluate(traceLastState));
						}
						
					}
				}
						
			}

		}
	}

	private void assertIsValidTrace(MTS<Long, String> mts, MTSTrace<String, Long> mtsTrace) {
		Long currentState = mts.getInitialState();
		for (MTSTransition<String, Long> mtsTransition : mtsTrace) {
			// check that the current transition is valid from current state
			BinaryRelation<String, Long> transitionsFromCurrentState = mts.getTransitions(currentState, TransitionType.POSSIBLE);
			Set<Long> statesAfterTransition = transitionsFromCurrentState.getImage(mtsTransition.getEvent());
			Assert.assertTrue("Trace is not valid for the mts. MTS: "+ mts +"\n Trace: " + mtsTrace ,statesAfterTransition.contains(mtsTransition.getStateTo()));
			// get the next state
			currentState = mtsTransition.getStateTo();
		}
	}

	@Test
	public void testSimpleModelNonDetDifferentModalities() throws Exception {
		
		MTS<Long, String> mi12 = TestModels.getInstance().getMTS(12);
		MTS<Long, String> mi13 = TestModels.getInstance().getMTS(13);
		MTS<Long, String> mi14 = TestModels.getInstance().getMTS(14);
		MTS<Long, String> mi15 = TestModels.getInstance().getMTS(15);

		HashSet<String> alpha1 = new HashSet<String>();
		alpha1.add("a");
		alpha1.add("b");
		
		HashSet<String> alpha2 = new HashSet<String>();
		alpha2.add("b");
		alpha2.add("c");
		
		Set<Set<String>> alphabets = new HashSet<Set<String>>();
		alphabets.add(alpha1);
		alphabets.add(alpha2);
		AlphabetDistribution<String> alphaDist = new AlphabetDistribution<String>(alphabets);
		
		DistributionAlgorithm<Long ,String> distAlg = new DistributionAlgorithmDefaultImpl<Long, String>();
		
		
		//////////////////// CHECK MODEL 12 ////////////////////////////////////////////////
		DistributionResult<Long, String> distResult = distAlg.tryDistribute(mi12, alphaDist, MTSConstants.TAU);
		
		Map<String, Long> expectedMissmatchingActions = new HashedMap<String, Long>();

		// no expected mismatches with this model
		this.checkMissmatches(distResult.getFeedback(), expectedMissmatchingActions, mi12);
		
		////////////////////CHECK MODEL 13 ////////////////////////////////////////////////
		distResult = distAlg.tryDistribute(mi13, alphaDist, MTSConstants.TAU);

		// expected action with a mismatch and quantity of mismatches for that action
		expectedMissmatchingActions = new HashedMap<String, Long>();
		expectedMissmatchingActions.put("a", new Long(1));
		
		this.checkMissmatches(distResult.getFeedback(), expectedMissmatchingActions, mi13);

		////////////////////CHECK MODEL 14 ////////////////////////////////////////////////
		distResult = distAlg.tryDistribute(mi14, alphaDist, MTSConstants.TAU);

		// expected action with a mismatch and quantity of mismatches for that action
		expectedMissmatchingActions = new HashedMap<String, Long>();
		expectedMissmatchingActions.put("a", new Long(1));
		
		this.checkMissmatches(distResult.getFeedback(), expectedMissmatchingActions, mi14);

		////////////////////CHECK MODEL 15 ////////////////////////////////////////////////
		distResult = distAlg.tryDistribute(mi15, alphaDist, MTSConstants.TAU);

		// expected action with a mismatch and quantity of mismatches for that action
		expectedMissmatchingActions = new HashedMap<String, Long>();
		expectedMissmatchingActions.put("a", new Long(1));
		expectedMissmatchingActions.put("c", new Long(1));
		
		this.checkMissmatches(distResult.getFeedback(), expectedMissmatchingActions, mi15);

	}

	@Test
	public void testComplexModelDifferentModalities() throws Exception {
		
		MTS<Long, String> mi11 = TestModels.getInstance().getMTS(11);

		HashSet<String> alpha1 = new HashSet<String>();
		alpha1.add("a");
		alpha1.add("b");
		
		HashSet<String> alpha2 = new HashSet<String>();
		alpha2.add("b");
		alpha2.add("c");
		alpha2.add("d");
		
		Set<Set<String>> alphabets = new HashSet<Set<String>>();
		alphabets.add(alpha1);
		alphabets.add(alpha2);
		
		AlphabetDistribution<String> alphaDist = new AlphabetDistribution<String>(alphabets);
		
		DistributionAlgorithm<Long ,String> distAlg = new DistributionAlgorithmDefaultImpl<Long, String>();
		
		
		//////////////////// CHECK MODEL 11 ////////////////////////////////////////////////
		DistributionResult<Long, String> distResult = distAlg.tryDistribute(mi11, alphaDist, MTSConstants.TAU);
		
		Map<String, Long> expectedMissmatchingActions = new HashedMap<String, Long>();

		// expected mismatches with this model
		expectedMissmatchingActions.put("a", new Long(1));
		this.checkMissmatches(distResult.getFeedback(), expectedMissmatchingActions, mi11);
		
		
		//TODO add more of these tests! Conflicts that are later dismissed...
	}


	private void checkMissmatches(List<DistributionFeedbackItem> feedback, Map<String, Long> expectedMissmatchingActions, MTS<Long, String> systemModel) {

		Set<DeterminisationModalityMismatch<Long, String>> actualMismatches = new HashSet<DeterminisationModalityMismatch<Long, String>>();
		// get the map count view of the actual mismatching actions
		Map<String, Long> actualMismatchesCountAsMap = new HashedMap<String, Long>();
		
		@SuppressWarnings("rawtypes")
		Predicate isDistributionFeedbackOnModalityMismatch = PredicateUtils.instanceofPredicate(DistributionFeedbackOnModalityMismatch.class);
		
		Collection feedbackOnMismatches = CollectionUtils.select(feedback, isDistributionFeedbackOnModalityMismatch);
		for (Object feedbackItemObj : feedbackOnMismatches) {
			DistributionFeedbackOnModalityMismatch<Long, String> feedbackItem = (DistributionFeedbackOnModalityMismatch<Long, String>)feedbackItemObj;
			String mismatchingAction = feedbackItem.getMismatch().getAction();

			// add the mismatch to the set of all mismatches
			actualMismatches.add(feedbackItem.getMismatch());
			
			if(!actualMismatchesCountAsMap.containsKey(mismatchingAction)) {
				actualMismatchesCountAsMap.put(mismatchingAction, new Long(1));
			} else {
				Long oldCount = actualMismatchesCountAsMap.get(mismatchingAction);
				actualMismatchesCountAsMap.put(mismatchingAction, new Long(oldCount.longValue() + 1));
			}
		}

		// compare the actual count with the expected one
		Assert.assertEquals(expectedMissmatchingActions, actualMismatchesCountAsMap);
		
		// Check for each mismatch that they have, as expected, different modality
		for (DeterminisationModalityMismatch<Long, String> mismatch : actualMismatches) {
			Long stateWithReqTransition = mismatch.getStateByModality(TransitionType.REQUIRED);
			Assert.assertTrue(!systemModel.getTransitions(stateWithReqTransition, TransitionType.REQUIRED).getImage(mismatch.getAction()).isEmpty());
			
			Long stateWithMayTransition = mismatch.getStateByModality(TransitionType.MAYBE);
			Assert.assertTrue(!systemModel.getTransitions(stateWithMayTransition, TransitionType.MAYBE).getImage(mismatch.getAction()).isEmpty());
		}
	}

}
