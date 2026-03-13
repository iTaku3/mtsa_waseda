package MTSATests.ac.ic.doc.distribution;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import MTSSynthesis.ac.ic.doc.distribution.ComponentBuilder;
import junit.framework.Assert;

import org.apache.commons.collections15.map.HashedMap;
import org.junit.Test;

import ltsa.ui.StandardOutput;
import MTSSynthesis.ac.ic.doc.distribution.model.ComponentBuiltFromDistribution;
import MTSSynthesis.ac.ic.doc.distribution.model.DeterminisationModalityMismatch;
import MTSAClient.ac.ic.doc.mtsa.MTSCompiler;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;
import MTSTools.ac.ic.doc.mtstools.model.MTSConstants;
import MTSTools.ac.ic.doc.mtstools.model.impl.WeakBisimulationSemantics;
import ltsa.dispatcher.TransitionSystemDispatcher;

/**
 * Tests for ComponentBuilder
 * @author gsibay
 *
 */
public class ComponentBuilderTest {

	@Test
	public void testSimpleModelFullProjection() throws Exception {
		String mStr = "I = (a? -> STOP).";

		MTSCompiler compiler = MTSCompiler.getInstance();
		MTS<Long, String> m = compiler.compileMTS("M", mStr);

		HashSet<String> alpha1 = new HashSet<String>();
		alpha1.add("a");
		
		ComponentBuilder<Long, String> componentBuilder = new ComponentBuilder<Long, String>();
		
		ComponentBuiltFromDistribution<Long, String> result = componentBuilder.buildComponent(m, alpha1, alpha1);
		MTS<Set<Long>, String> component = result.getComponent();
		
		//To make the alphabets equal TAU has to be added (this is so because The MTS compiler adds tau to mStr).
		component.addActions(Collections.singleton(MTSConstants.TAU)); 
		
		Assert.assertTrue(result.getDeterminisationModalityMismatches().isEmpty());
		
		WeakBisimulationSemantics bisimulationSemantics = new WeakBisimulationSemantics(Collections.emptySet());
		
		boolean bisimilar = TransitionSystemDispatcher.isRefinement(m, " original ", component, " synthesised ", bisimulationSemantics, new StandardOutput());

		Assert.assertTrue(bisimilar);

	}

	@Test
	public void testNonFullProjection() throws Exception {
		String iStr = "I = (a? -> STOP | b? -> STOP).";
		
		String expectedStr = "I = (a? -> STOP).";

		MTSCompiler compiler = MTSCompiler.getInstance();

		MTS<Long, String> i = compiler.compileMTS("I", iStr);
		MTS<Long, String> expected = compiler.compileMTS("Expected", expectedStr);

		HashSet<String> alpha1 = new HashSet<String>();
		alpha1.add("a");
		
		HashSet<String> fullAlphabet = new HashSet<String>();
		fullAlphabet.add("a");
		fullAlphabet.add("b");
		
		// Get component from I
		ComponentBuilder<Long, String> componentBuilder = new ComponentBuilder<Long, String>();
		ComponentBuiltFromDistribution<Long, String> result = componentBuilder.buildComponent(i, alpha1, fullAlphabet);
		MTS<Set<Long>, String> componentFromI = result.getComponent();
		
		//To make the alphabets equal TAU has to be added (this is so because The MTS compiler adds tau to mStr).
		componentFromI.addActions(Collections.singleton(MTSConstants.TAU)); 
		
		Assert.assertTrue(result.getDeterminisationModalityMismatches().isEmpty());
		
		WeakBisimulationSemantics bisimulationSemantics = new WeakBisimulationSemantics(Collections.emptySet());
		
		boolean bisimilar = TransitionSystemDispatcher.isRefinement(expected, " original ", componentFromI, " synthesised ", bisimulationSemantics, new StandardOutput());

		Assert.assertTrue(bisimilar);

	}

	
	@Test
	public void testSimpleModelNonDetNonFullProjection() throws Exception {
		String iStr = "I = (a? -> Q1 | c? -> Q2), Q1 = ( c? -> Q3), Q2 = (a? -> Q3), Q3 = (b -> STOP).";
		
		String i2Str = "I2 = (a? -> Q1 | c? -> Q2), Q1 = ( c? -> Q4), Q2 = (a? -> Q5), Q4 = (b -> STOP), Q5 = (b -> STOP).";

		String expectedStr = "I = (a? -> STOP).";

		MTSCompiler compiler = MTSCompiler.getInstance();

		MTS<Long, String> i = compiler.compileMTS("I", iStr);
		MTS<Long, String> i2 = compiler.compileMTS("I2", i2Str);
		MTS<Long, String> expected = compiler.compileMTS("Expected", expectedStr);

		HashSet<String> alpha1 = new HashSet<String>();
		alpha1.add("a");
		
		HashSet<String> fullAlphabet = new HashSet<String>();
		fullAlphabet.add("a");
		fullAlphabet.add("b");
		fullAlphabet.add("c");
		
		// Get component from I
		ComponentBuilder<Long, String> componentBuilder = new ComponentBuilder<Long, String>();
		ComponentBuiltFromDistribution<Long, String> result = componentBuilder.buildComponent(i, alpha1, fullAlphabet);
		MTS<Set<Long>, String> componentFromI = result.getComponent();
		
		//To make the alphabets equal TAU has to be added (this is so because The MTS compiler adds tau to mStr).
		componentFromI.addActions(Collections.singleton(MTSConstants.TAU)); 
		
		Assert.assertTrue(result.getDeterminisationModalityMismatches().isEmpty());
		
		WeakBisimulationSemantics bisimulationSemantics = new WeakBisimulationSemantics(Collections.emptySet());
		
		boolean bisimilar = TransitionSystemDispatcher.isRefinement(expected, " original ", componentFromI, " synthesised ", bisimulationSemantics, new StandardOutput());

		Assert.assertTrue(bisimilar);

		// Get component from I2
		result = componentBuilder.buildComponent(i2, alpha1, fullAlphabet);
		MTS<Set<Long>, String> componentFromI2 = result.getComponent();
		
		//To make the alphabets equal TAU has to be added (this is so because The MTS compiler adds tau to mStr).
		componentFromI2.addActions(Collections.singleton(MTSConstants.TAU)); 
		
		Assert.assertTrue(result.getDeterminisationModalityMismatches().isEmpty());

		bisimilar = TransitionSystemDispatcher.isRefinement(expected, " original ", componentFromI2, " synthesised ", bisimulationSemantics, new StandardOutput());

		Assert.assertTrue(bisimilar);
		
		bisimilar = TransitionSystemDispatcher.isRefinement(componentFromI, " component from I ", componentFromI2, " component from I2 ", bisimulationSemantics, new StandardOutput());

		Assert.assertTrue(bisimilar);
	}
	
	@Test
	public void testSimpleModelNonDetDifferentModalities() throws Exception {
		String iStr = "I = (a -> Q1 | c? -> Q2), Q1 = ( c? -> Q3), Q2 = (a -> Q3), Q3 = (b -> STOP).";
		
		String i2Str = "I2 = (a? -> Q1 | c? -> Q2), Q1 = ( c? -> Q3), Q2 = (a -> Q3), Q3 = (b -> STOP).";
		
		String i3Str = "I3 = (a -> Q1 | c? -> Q2), Q1 = ( c? -> Q3), Q2 = (a? -> Q3), Q3 = (b -> STOP).";

		String expectedOnAStr = "IEA = (a -> STOP).";
		
		String expectedOnBStr = "IEB = (b -> STOP).";

		MTSCompiler compiler = MTSCompiler.getInstance();

		MTS<Long, String> i = compiler.compileMTS("I", iStr);
		MTS<Long, String> i2 = compiler.compileMTS("I2", i2Str);
		MTS<Long, String> i3 = compiler.compileMTS("I3", i3Str);
		MTS<Long, String> expectedOnA = compiler.compileMTS("IEA", expectedOnAStr);
		MTS<Long, String> expectedOnB = compiler.compileMTS("IEB", expectedOnBStr);
		

		HashSet<String> alpha1 = new HashSet<String>();
		alpha1.add("a");
		
		HashSet<String> fullAlphabet = new HashSet<String>();
		fullAlphabet.add("a");
		fullAlphabet.add("b");
		fullAlphabet.add("c");
		
		// Get component from I
		ComponentBuilder<Long, String> componentBuilder = new ComponentBuilder<Long, String>();
		ComponentBuiltFromDistribution<Long, String> result = componentBuilder.buildComponent(i, alpha1, fullAlphabet);
		MTS<Set<Long>, String> componentFromI = result.getComponent();
		
		//To make the alphabets equal TAU has to be added (this is so because The MTS compiler adds tau to mStr).
		componentFromI.addActions(Collections.singleton(MTSConstants.TAU)); 
		
		Assert.assertTrue(result.getDeterminisationModalityMismatches().isEmpty());
		
		WeakBisimulationSemantics bisimulationSemantics = new WeakBisimulationSemantics(Collections.emptySet());
		
		boolean bisimilar = TransitionSystemDispatcher.isRefinement(expectedOnA, " original ", componentFromI, " synthesised ", bisimulationSemantics, new StandardOutput());

		Assert.assertTrue(bisimilar);

		// Get component from I2
		result = componentBuilder.buildComponent(i2, alpha1, fullAlphabet);
		MTS<Set<Long>, String> componentFromI2 = result.getComponent();
		
		//To make the alphabets equal TAU has to be added (this is so because The MTS compiler adds tau to mStr).
		componentFromI2.addActions(Collections.singleton(MTSConstants.TAU)); 
		
		Map<String, Long> expectedMissmatchingActions = new HashedMap<String, Long>();

		// expected action with a mismatch and quantity of mismatches for that action
		expectedMissmatchingActions.put("a", new Long(1));
		
		this.checkMissmatches(result, expectedMissmatchingActions, i2);

		bisimilar = TransitionSystemDispatcher.isRefinement(expectedOnA, " expected ", componentFromI2, " synthesised ", bisimulationSemantics, new StandardOutput());

		Assert.assertTrue(bisimilar);
		
		bisimilar = TransitionSystemDispatcher.isRefinement(componentFromI, " component from I ", componentFromI2, " component from I2 ", bisimulationSemantics, new StandardOutput());

		Assert.assertTrue(bisimilar);
		
		// Get component from I3
		result = componentBuilder.buildComponent(i3, alpha1, fullAlphabet);
		MTS<Set<Long>, String> componentFromI3 = result.getComponent();
		//To make the alphabets equal TAU has to be added (this is so because The MTS compiler adds tau to mStr).
		componentFromI3.addActions(Collections.singleton(MTSConstants.TAU));
		
		this.checkMissmatches(result, expectedMissmatchingActions, i3);
		
		bisimilar = TransitionSystemDispatcher.isRefinement(expectedOnA, " expected ", componentFromI3, " synthesised ", bisimulationSemantics, new StandardOutput());

		Assert.assertTrue(bisimilar);
		
		// Get component from I on b
		HashSet<String> alpha2 = new HashSet<String>();
		alpha2.add("b");
		result = componentBuilder.buildComponent(i, alpha2, fullAlphabet);
		MTS<Set<Long>, String> componentFromIOnB = result.getComponent();
		componentFromIOnB.addAction(MTSConstants.TAU);
		
		Assert.assertTrue(result.getDeterminisationModalityMismatches().isEmpty());
		Assert.assertTrue(TransitionSystemDispatcher.isRefinement(expectedOnB, " expected ", componentFromIOnB, " component from I on b ", bisimulationSemantics, new StandardOutput()));

		// Get component from I2 on b
		result = componentBuilder.buildComponent(i2, alpha2, fullAlphabet);
		
		MTS<Set<Long>, String> componentFromI2OnB = result.getComponent();
		componentFromI2OnB.addAction(MTSConstants.TAU);
		
		Assert.assertTrue(result.getDeterminisationModalityMismatches().isEmpty());
		Assert.assertTrue(TransitionSystemDispatcher.isRefinement(expectedOnB, " expected ", componentFromI2OnB, " component from I2 on b ", bisimulationSemantics, new StandardOutput()));
		
		// Get component from I3 on b
		result = componentBuilder.buildComponent(i3, alpha2, fullAlphabet);
		
		MTS<Set<Long>, String> componentFromI3OnB = result.getComponent();
		componentFromI3OnB.addAction(MTSConstants.TAU);
		
		Assert.assertTrue(result.getDeterminisationModalityMismatches().isEmpty());
		Assert.assertTrue(TransitionSystemDispatcher.isRefinement(expectedOnB, " expected ", componentFromI3OnB, " component from I3 on b ", bisimulationSemantics, new StandardOutput()));

	}

	@Test
	public void testSimpleModelNonDetDifferentModalitiesWithLoops() throws Exception {
		String iStr = "I = (a -> Q1 | c? -> Q2), Q1 = ( c? -> Q3), Q2 = (a -> Q3), Q3 = (b -> I).";
		
		String i2Str = "I2 = (a? -> Q1 | c? -> Q2), Q1 = ( c? -> Q3), Q2 = (a -> Q3), Q3 = (b -> I2).";
		
		String i3Str = "I3 = (a -> Q1 | c? -> Q2), Q1 = ( c? -> Q3), Q2 = (a? -> Q3), Q3 = (b -> I3).";

		String expectedOnAStr = "IEA = (a -> IEA).";
		
		String expectedOnBStr = "IEB = (b -> IEB).";

		MTSCompiler compiler = MTSCompiler.getInstance();

		MTS<Long, String> i = compiler.compileMTS("I", iStr);
		MTS<Long, String> i2 = compiler.compileMTS("I2", i2Str);
		MTS<Long, String> i3 = compiler.compileMTS("I3", i3Str);
		MTS<Long, String> expectedOnA = compiler.compileMTS("IEA", expectedOnAStr);
		MTS<Long, String> expectedOnB = compiler.compileMTS("IEB", expectedOnBStr);
		

		HashSet<String> alpha1 = new HashSet<String>();
		alpha1.add("a");
		
		HashSet<String> fullAlphabet = new HashSet<String>();
		fullAlphabet.add("a");
		fullAlphabet.add("b");
		fullAlphabet.add("c");
		
		// Get component from I
		ComponentBuilder<Long, String> componentBuilder = new ComponentBuilder<Long, String>();
		ComponentBuiltFromDistribution<Long, String> result = componentBuilder.buildComponent(i, alpha1, fullAlphabet);
		MTS<Set<Long>, String> componentFromI = result.getComponent();
		
		//To make the alphabets equal TAU has to be added (this is so because The MTS compiler adds tau to mStr).
		componentFromI.addActions(Collections.singleton(MTSConstants.TAU)); 
		
		Assert.assertTrue(result.getDeterminisationModalityMismatches().isEmpty());
		
		WeakBisimulationSemantics bisimulationSemantics = new WeakBisimulationSemantics(Collections.emptySet());
		
		boolean bisimilar = TransitionSystemDispatcher.isRefinement(expectedOnA, " original ", componentFromI, " synthesised ", bisimulationSemantics, new StandardOutput());

		Assert.assertTrue(bisimilar);

		// Get component from I2
		result = componentBuilder.buildComponent(i2, alpha1, fullAlphabet);
		MTS<Set<Long>, String> componentFromI2 = result.getComponent();
		
		//To make the alphabets equal TAU has to be added (this is so because The MTS compiler adds tau to mStr).
		componentFromI2.addActions(Collections.singleton(MTSConstants.TAU)); 
		
		Map<String, Long> expectedMissmatchingActions = new HashedMap<String, Long>();

		// expected action with a mismatch and quantity of mismatches for that action
		expectedMissmatchingActions.put("a", new Long(1));
		
		this.checkMissmatches(result, expectedMissmatchingActions, i2);

		bisimilar = TransitionSystemDispatcher.isRefinement(expectedOnA, " expected ", componentFromI2, " synthesised ", bisimulationSemantics, new StandardOutput());

		Assert.assertTrue(bisimilar);
		
		bisimilar = TransitionSystemDispatcher.isRefinement(componentFromI, " component from I ", componentFromI2, " component from I2 ", bisimulationSemantics, new StandardOutput());

		Assert.assertTrue(bisimilar);
		
		// Get component from I3
		result = componentBuilder.buildComponent(i3, alpha1, fullAlphabet);
		MTS<Set<Long>, String> componentFromI3 = result.getComponent();
		//To make the alphabets equal TAU has to be added (this is so because The MTS compiler adds tau to mStr).
		componentFromI3.addActions(Collections.singleton(MTSConstants.TAU));
		
		this.checkMissmatches(result, expectedMissmatchingActions, i3);
		
		bisimilar = TransitionSystemDispatcher.isRefinement(expectedOnA, " expected ", componentFromI3, " synthesised ", bisimulationSemantics, new StandardOutput());

		Assert.assertTrue(bisimilar);
		
		// Get component from I on b
		HashSet<String> alpha2 = new HashSet<String>();
		alpha2.add("b");
		result = componentBuilder.buildComponent(i, alpha2, fullAlphabet);
		MTS<Set<Long>, String> componentFromIOnB = result.getComponent();
		componentFromIOnB.addAction(MTSConstants.TAU);
		
		Assert.assertTrue(result.getDeterminisationModalityMismatches().isEmpty());
		Assert.assertTrue(TransitionSystemDispatcher.isRefinement(expectedOnB, " expected ", componentFromIOnB, " component from I on b ", bisimulationSemantics, new StandardOutput()));

		// Get component from I2 on b
		result = componentBuilder.buildComponent(i2, alpha2, fullAlphabet);
		
		MTS<Set<Long>, String> componentFromI2OnB = result.getComponent();
		componentFromI2OnB.addAction(MTSConstants.TAU);
		
		Assert.assertTrue(result.getDeterminisationModalityMismatches().isEmpty());
		Assert.assertTrue(TransitionSystemDispatcher.isRefinement(expectedOnB, " expected ", componentFromI2OnB, " component from I2 on b ", bisimulationSemantics, new StandardOutput()));
		
		// Get component from I3 on b
		result = componentBuilder.buildComponent(i3, alpha2, fullAlphabet);
		
		MTS<Set<Long>, String> componentFromI3OnB = result.getComponent();
		componentFromI3OnB.addAction(MTSConstants.TAU);
		
		Assert.assertTrue(result.getDeterminisationModalityMismatches().isEmpty());
		Assert.assertTrue(TransitionSystemDispatcher.isRefinement(expectedOnB, " expected ", componentFromI3OnB, " component from I3 on b ", bisimulationSemantics, new StandardOutput()));

	}

	@Test
	public void testTwoMistmatches() throws Exception {
		String iStr = "I = Q1, Q1 = (c? -> Q3 | a? -> Q2), Q2 = (c? -> Q4), Q3 = ( a -> Q4), Q4 = (b -> Q5), Q5 = (d -> Q6 | a? -> Q8 | c? -> Q7), " +
				"Q6 = (a? -> Q9), Q7 = (a? -> Q10), Q8 = (d -> Q9 | c? -> Q10), Q9 = (b -> Q1), Q10 = (b? -> Q1)."; 
		
		String expectedOnAlpha1Str = "IEA = Q1, Q1 = (a -> b -> a? -> b -> Q1)."; //TODO
		
		String expectedOnAlpha2Str = "IEB = Q1, Q1 = (c? -> b -> Q2), Q2 = (c? -> b? -> Q1 | d -> b -> Q1)."; //TODO

		MTSCompiler compiler = MTSCompiler.getInstance(); 

		MTS<Long, String> i = compiler.compileMTS("I", iStr);
		MTS<Long, String> expectedOnAlpha1 = compiler.compileMTS("IEA", expectedOnAlpha1Str);
		MTS<Long, String> expectedOnAlpha2 = compiler.compileMTS("IEB", expectedOnAlpha2Str);
		

		HashSet<String> alpha1 = new HashSet<String>();
		alpha1.add("a");
		alpha1.add("b");

		HashSet<String> alpha2 = new HashSet<String>();
		alpha2.add("b");
		alpha2.add("c");
		alpha2.add("d");
		

		HashSet<String> fullAlphabet = new HashSet<String>();
		fullAlphabet.add("a");
		fullAlphabet.add("b");
		fullAlphabet.add("c");
		fullAlphabet.add("d");
		
		// Get component from I on alpha1
		ComponentBuilder<Long, String> componentBuilder = new ComponentBuilder<Long, String>();
		ComponentBuiltFromDistribution<Long, String> result = componentBuilder.buildComponent(i, alpha1, fullAlphabet);
		MTS<Set<Long>, String> componentFromIOnAlpha1 = result.getComponent();
		
		//To make the alphabets equal TAU has to be added (this is so because The MTS compiler adds tau to mStr).
		componentFromIOnAlpha1.addActions(Collections.singleton(MTSConstants.TAU)); 

		// expected action with a mismatch and quantity of mismatches for that action
		Map<String, Long> expectedMissmatchingActions = new HashedMap<String, Long>();
		expectedMissmatchingActions.put("a", new Long(1));
		expectedMissmatchingActions.put("b", new Long(1));
		
		this.checkMissmatches(result, expectedMissmatchingActions, i);
		
		WeakBisimulationSemantics bisimulationSemantics = new WeakBisimulationSemantics(Collections.emptySet());
		
		boolean bisimilar = TransitionSystemDispatcher.isRefinement(expectedOnAlpha1, " expected ", componentFromIOnAlpha1, " synthesised ", bisimulationSemantics, new StandardOutput());

		Assert.assertTrue(bisimilar);

		// Get component from I on alpha2
		result = componentBuilder.buildComponent(i, alpha2, fullAlphabet);
		MTS<Set<Long>, String> componentFromIOnAlpha2 = result.getComponent();
		
		//To make the alphabets equal TAU has to be added (this is so because The MTS compiler adds tau to mStr).
		componentFromIOnAlpha2.addActions(Collections.singleton(MTSConstants.TAU)); 
		
		Assert.assertTrue(result.getDeterminisationModalityMismatches().isEmpty());

		bisimilar = TransitionSystemDispatcher.isRefinement(expectedOnAlpha2, " expected ", componentFromIOnAlpha2, " synthesised ", bisimulationSemantics, new StandardOutput());

		Assert.assertTrue(bisimilar);
		
	}

	private void checkMissmatches(ComponentBuiltFromDistribution<Long, String> result, Map<String, Long> expectedMissmatchingActions, MTS<Long, String> monolithicModel) {

		// get the map count view of the actual mismatching actions
		Map<String, Long> actualMismatchesCountAsMap = new HashedMap<String, Long>();
		Set<DeterminisationModalityMismatch<Long, String>> actualMismatches = result.getDeterminisationModalityMismatches();
		for (DeterminisationModalityMismatch<Long, String> determinisationModalityMismatch : actualMismatches) {
			String mismatchingAction = determinisationModalityMismatch.getAction();
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
			Assert.assertTrue(!monolithicModel.getTransitions(stateWithReqTransition, TransitionType.REQUIRED).getImage(mismatch.getAction()).isEmpty());
			
			Long stateWithMayTransition = mismatch.getStateByModality(TransitionType.MAYBE);
			Assert.assertTrue(!monolithicModel.getTransitions(stateWithMayTransition, TransitionType.MAYBE).getImage(mismatch.getAction()).isEmpty());
		}
	}

}
