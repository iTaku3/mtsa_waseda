package MTSATests.ac.ic.doc.mtstools.model.predicates;

import static org.junit.Assert.assertTrue;

import MTSTools.ac.ic.doc.mtstools.model.predicates.IsDeterministicMTSPredicate;
import org.apache.commons.collections15.Predicate;
import org.junit.Test;

import MTSATests.util.TestModels;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;

public class IsDeterministicMTSPredicateTest {

	@Test
	public void testIsDeterministic() throws Exception {
		TestModels testModels = TestModels.getInstance();
		
		Predicate<MTS<Long, String>> isDeterministicPossiblePredicate = 
				new IsDeterministicMTSPredicate<Long, String>(TransitionType.POSSIBLE);
		
		Predicate<MTS<Long, String>> isDeterministicRequiredPredicate = 
				new IsDeterministicMTSPredicate<Long, String>(TransitionType.REQUIRED);

		Predicate<MTS<Long, String>> isDeterministicMaybePredicate = 
				new IsDeterministicMTSPredicate<Long, String>(TransitionType.MAYBE);

		assertTrue(isDeterministicPossiblePredicate.evaluate(testModels.getMTS(0)));
		assertTrue(isDeterministicRequiredPredicate.evaluate(testModels.getMTS(0)));
		assertTrue(isDeterministicMaybePredicate.evaluate(testModels.getMTS(0)));
		
		assertTrue(isDeterministicPossiblePredicate.evaluate(testModels.getMTS(1)));
		assertTrue(isDeterministicRequiredPredicate.evaluate(testModels.getMTS(1)));
		assertTrue(isDeterministicMaybePredicate.evaluate(testModels.getMTS(1)));
		

		assertTrue(isDeterministicPossiblePredicate.evaluate(testModels.getMTS(2)));
		assertTrue(isDeterministicRequiredPredicate.evaluate(testModels.getMTS(2)));
		assertTrue(isDeterministicMaybePredicate.evaluate(testModels.getMTS(2)));

		assertTrue(isDeterministicPossiblePredicate.evaluate(testModels.getMTS(3)));
		assertTrue(isDeterministicRequiredPredicate.evaluate(testModels.getMTS(3)));
		assertTrue(isDeterministicMaybePredicate.evaluate(testModels.getMTS(3)));

		assertTrue(isDeterministicPossiblePredicate.evaluate(testModels.getMTS(4)));
		assertTrue(isDeterministicRequiredPredicate.evaluate(testModels.getMTS(4)));
		assertTrue(isDeterministicMaybePredicate.evaluate(testModels.getMTS(4)));

		assertTrue(!isDeterministicPossiblePredicate.evaluate(testModels.getMTS(5)));
		assertTrue(isDeterministicRequiredPredicate.evaluate(testModels.getMTS(5)));
		assertTrue(isDeterministicMaybePredicate.evaluate(testModels.getMTS(5)));

		assertTrue(isDeterministicPossiblePredicate.evaluate(testModels.getMTS(6)));
		assertTrue(isDeterministicRequiredPredicate.evaluate(testModels.getMTS(6)));
		assertTrue(isDeterministicMaybePredicate.evaluate(testModels.getMTS(6)));

		assertTrue(!isDeterministicPossiblePredicate.evaluate(testModels.getMTS(7)));
		assertTrue(!isDeterministicRequiredPredicate.evaluate(testModels.getMTS(7)));
		assertTrue(isDeterministicMaybePredicate.evaluate(testModels.getMTS(7)));

		assertTrue(isDeterministicPossiblePredicate.evaluate(testModels.getMTS(8)));
		assertTrue(isDeterministicRequiredPredicate.evaluate(testModels.getMTS(8)));
		assertTrue(isDeterministicMaybePredicate.evaluate(testModels.getMTS(8)));

		assertTrue(!isDeterministicPossiblePredicate.evaluate(testModels.getMTS(9)));
		assertTrue(isDeterministicRequiredPredicate.evaluate(testModels.getMTS(1)));
		assertTrue(!isDeterministicMaybePredicate.evaluate(testModels.getMTS(9)));
	}

}
