package MTSATests.ac.ic.doc.mtstools.utils;

import static org.junit.Assert.assertTrue;

import java.util.Set;

import MTSTools.ac.ic.doc.mtstools.utils.MTSUtils;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.PredicateUtils;
import org.junit.Test;

import MTSATests.util.TestModels;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;
import MTSTools.ac.ic.doc.mtstools.model.MTSTrace;
import MTSTools.ac.ic.doc.mtstools.model.predicates.HasNonDeterministicTransitionsStatePredicate;

/**
 * @author gsibay
 *
 */
public class MTSUtilsTest {
	
	
	@Test
	public void testIsEmpty() throws Exception {
		TestModels testModels = TestModels.getInstance();
		assertTrue(MTSUtils.isEmpty(testModels.getMTS(0)));
		assertTrue(!MTSUtils.isEmpty(testModels.getMTS(1)));
		assertTrue(!MTSUtils.isEmpty(testModels.getMTS(2)));
		assertTrue(!MTSUtils.isEmpty(testModels.getMTS(3)));
		assertTrue(!MTSUtils.isEmpty(testModels.getMTS(4)));
		assertTrue(!MTSUtils.isEmpty(testModels.getMTS(5)));
		assertTrue(!MTSUtils.isEmpty(testModels.getMTS(6)));
		assertTrue(!MTSUtils.isEmpty(testModels.getMTS(7)));
		assertTrue(!MTSUtils.isEmpty(testModels.getMTS(8)));
		assertTrue(!MTSUtils.isEmpty(testModels.getMTS(9)));
	}
	
	
	
	@Test
	public void testSizeForGetAllTracesToStateSatisfyingPredicateNonDeterministicStates() throws Exception {
		Set<MTSTrace<String, Long>> traces = this.getAllTracesToNonDeterministicStates(0, TransitionType.POSSIBLE);
		assertTrue(traces.size() == 0);
		
		traces = this.getAllTracesToNonDeterministicStates(1, TransitionType.POSSIBLE);
		assertTrue(traces.size() == 0);

		traces = this.getAllTracesToNonDeterministicStates(2, TransitionType.POSSIBLE);
		assertTrue(traces.size() == 0);

		traces = this.getAllTracesToNonDeterministicStates(3, TransitionType.POSSIBLE);
		assertTrue(traces.size() == 0);

		traces = this.getAllTracesToNonDeterministicStates(4, TransitionType.POSSIBLE);
		assertTrue(traces.size() == 0);

		traces = this.getAllTracesToNonDeterministicStates(5, TransitionType.POSSIBLE);
		assertTrue(traces.size() == 1);

		traces = this.getAllTracesToNonDeterministicStates(6, TransitionType.POSSIBLE);
		assertTrue(traces.size() == 0);

		traces = this.getAllTracesToNonDeterministicStates(7, TransitionType.POSSIBLE);
		assertTrue(traces.size() == 1);

		traces = this.getAllTracesToNonDeterministicStates(8, TransitionType.POSSIBLE);
		assertTrue(traces.size() == 0);

		traces = this.getAllTracesToNonDeterministicStates(9, TransitionType.POSSIBLE);
		assertTrue(traces.size() == 1);
		
		traces = this.getAllTracesToNonDeterministicStates(10, TransitionType.POSSIBLE);
		assertTrue(traces.size() == 3);
	}


	private Set<MTSTrace<String, Long>> getAllTracesToNonDeterministicStates( int i, TransitionType transitionType) throws Exception {
		return this.getAllTracesToNonDeterministicStates(TestModels.getInstance().getMTS(i), transitionType);
	}

	private Set<MTSTrace<String, Long>> getAllTracesToNonDeterministicStates(MTS<Long, String> mts, TransitionType transitionType) throws Exception {
		Predicate<Long> nonDeterministicStatePredicate = new HasNonDeterministicTransitionsStatePredicate<Long, String>(mts, transitionType);
		return MTSUtils.getAlltracesToStatesSatisfyingPredicate(mts, nonDeterministicStatePredicate, transitionType);
	}

	@Test
	public void testTracesSatisfyPredicateFromGetAllTracesToStateSatisfyingPredicate() throws Exception {
		TestModels testModels = TestModels.getInstance();
		Set<MTSTrace<String, Long>> traces;
		MTS<Long, String> mts;
		
		for(int i = 0; i < testModels.getSize(); i++) {
			// get traces to non deterministic states
			mts = testModels.getMTS(i);
			traces = this.getAllTracesToNonDeterministicStates(mts, TransitionType.POSSIBLE);
			
			Predicate<Long> hasNonDetTransitionsStatePredicate = new HasNonDeterministicTransitionsStatePredicate<Long, String>(mts, TransitionType.POSSIBLE);
			for (MTSTrace<String, Long> traceToStateSatifyingPredicate : traces) {
				Long finalState;
				if(traceToStateSatifyingPredicate.size() == 0) {
					finalState = mts.getInitialState();
				} else {
					finalState = traceToStateSatifyingPredicate.get(traceToStateSatifyingPredicate.size() - 1 ).getStateTo();
				}
				
				assertTrue(hasNonDetTransitionsStatePredicate.evaluate(finalState));
			}
			
		}
	}
	
	@Test
	public void testGetATraceToAParticularState() throws Exception {
		TestModels testModels = TestModels.getInstance();
		Set<MTSTrace<String, Long>> traces;
		MTS<Long, String> mts;
		
		for(int i = 0; i < testModels.getSize(); i++) {
			mts = testModels.getMTS(i);
			Set<Long> states = mts.getStates();
			for (Long state : states) {
				
				Predicate<Long> isThisStatePredicate = PredicateUtils.equalPredicate(state);
				traces = MTSUtils.getAlltracesToStatesSatisfyingPredicate(mts, isThisStatePredicate, TransitionType.POSSIBLE);
				assertTrue("There is only one state satysying the condition so there should only be one trace.\n MTS: " + i +
						".\n Traces = " + traces.toString() + ".\n State = " + state, traces.size() == 1);
				MTSTrace<String, Long> trace = traces.iterator().next();
				if(trace.size() == 0) {
					assertTrue("The empty trace was returned so the initial state should be the particular state", state.equals(mts.getInitialState()));
				} else {
					assertTrue("The trace must lead to the state \n MTS: " + i +
							".\n Trace = " + trace.get(trace.size()-1) + ".\n State = " + state, trace.get(trace.size()-1).getStateTo().equals(state));
				}
			}
		}
	}

}

