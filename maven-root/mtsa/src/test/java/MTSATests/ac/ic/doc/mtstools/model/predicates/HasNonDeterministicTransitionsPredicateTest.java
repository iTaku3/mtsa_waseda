package MTSATests.ac.ic.doc.mtstools.model.predicates;

import static org.junit.Assert.assertTrue;

import java.util.Set;

import MTSTools.ac.ic.doc.mtstools.model.predicates.HasNonDeterministicTransitionsStatePredicate;
import org.apache.commons.collections15.Predicate;
import org.junit.Test;

import MTSATests.util.TestModels;
import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;

/**
 * 
 * @author gsibay
 *
 */
public class HasNonDeterministicTransitionsPredicateTest {

	@Test
	public void testOnDeterministicMTSs() throws Exception {
		TestModels testModels = TestModels.getInstance();
		assertTrue(this.allStatesDeterministic(testModels.getMTS(0)));
		assertTrue(this.allStatesDeterministic(testModels.getMTS(1)));
		assertTrue(this.allStatesDeterministic(testModels.getMTS(2)));
		assertTrue(this.allStatesDeterministic(testModels.getMTS(3)));
		assertTrue(this.allStatesDeterministic(testModels.getMTS(4)));
		assertTrue(this.allStatesDeterministic(testModels.getMTS(6)));
		assertTrue(this.allStatesDeterministic(testModels.getMTS(8)));
	}

	@Test
	public void testOnNonDeterministicMTSs() throws Exception {
		TestModels testModels = TestModels.getInstance();
		assertTrue(!this.allStatesDeterministic(testModels.getMTS(5)));
		assertTrue(!this.allStatesDeterministic(testModels.getMTS(7)));
		assertTrue(!this.allStatesDeterministic(testModels.getMTS(9)));
	}

	@Test
	public void testHasNonDetTransitionsFromState() throws Exception {
		TestModels testModels = TestModels.getInstance();
		
		MTS<Long, String> I0 = testModels.getMTS(0);
		
		HasNonDeterministicTransitionsStatePredicate<Long, String> predicate =
				new HasNonDeterministicTransitionsStatePredicate<Long, String>(I0, TransitionType.POSSIBLE);
		assertTrue(!predicate.evaluate(I0.getInitialState()));
		
		predicate = new HasNonDeterministicTransitionsStatePredicate<Long, String>(I0, TransitionType.REQUIRED);
		assertTrue(!predicate.evaluate(I0.getInitialState()));
		
		predicate = new HasNonDeterministicTransitionsStatePredicate<Long, String>(I0, TransitionType.MAYBE);
		assertTrue(!predicate.evaluate(I0.getInitialState()));

		
		MTS<Long, String> I5 = testModels.getMTS(5);
		
		predicate = new HasNonDeterministicTransitionsStatePredicate<Long, String>(I5, TransitionType.POSSIBLE);
		assertTrue(predicate.evaluate(I5.getInitialState()));
		
		predicate = new HasNonDeterministicTransitionsStatePredicate<Long, String>(I5, TransitionType.REQUIRED);
		assertTrue(!predicate.evaluate(I5.getInitialState()));
		
		predicate = new HasNonDeterministicTransitionsStatePredicate<Long, String>(I5, TransitionType.MAYBE);
		assertTrue(!predicate.evaluate(I5.getInitialState()));
	}

	
	@Test
	public void testOnATrace() throws Exception {
		TestModels testModels = TestModels.getInstance();
		MTS<Long, String> mts = testModels.getMTS(9);
		Predicate<Long> hasNonDeterministicTransition = 
				new HasNonDeterministicTransitionsStatePredicate<Long, String>(mts, TransitionType.POSSIBLE);
		
		BinaryRelation<String, Long> transitions = 
				mts.getTransitions(mts.getInitialState(), TransitionType.POSSIBLE);
		Set<Long> states = transitions.getImage("c");
		
		assertTrue(states.size() == 1);
		Long stateQ1 = states.iterator().next();
		
		assertTrue(hasNonDeterministicTransition.evaluate(stateQ1));
		
		transitions = mts.getTransitions(stateQ1, TransitionType.MAYBE);
		states = transitions.getImage("b");
		
		assertTrue(states.size() == 2);
		
		for (Long state : states) {
			assertTrue( ! hasNonDeterministicTransition.evaluate(state) );
		}
		
	}
	
	private boolean allStatesDeterministic(MTS<Long, String> mts) {
		boolean result = true;
		Predicate<Long> hasNonDeterministicTransition = 
				new HasNonDeterministicTransitionsStatePredicate<Long, String>(mts, TransitionType.POSSIBLE);
		Set<Long> states = mts.getStates();
		for (Long state : states) {
			result = result && !(hasNonDeterministicTransition.evaluate(state));
		}
		return result;
	}

}
