package MTSTools.ac.ic.doc.mtstools.model.operations.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import junit.framework.TestCase;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;
import MTSTools.ac.ic.doc.mtstools.model.impl.MTSImpl;


public class MTSPropertyToBuchiConverterTest extends TestCase {
	public void testBuildProperty() throws Exception {
		long state0 = 0L;
		long state1 = 1L;
		long errorState = -1L;

		MTS<Long, String> testProperty = new MTSImpl<Long, String>(state0);
		testProperty.addState(state1);
		testProperty.addState(errorState);
		
		String labelA = "a";
		String labelB = "b";

		testProperty.addAction(labelA);
		testProperty.addAction(labelB);
		
		testProperty.addTransition(state0, labelA, state1, TransitionType.REQUIRED);
		testProperty.addTransition(state1, labelB, state0, TransitionType.REQUIRED);
		testProperty.addTransition(state0, labelB, errorState, TransitionType.REQUIRED);
		testProperty.addTransition(state1, labelA, errorState, TransitionType.REQUIRED);
		
		
		MTSPropertyToBuchiConverter.convert(testProperty, 2L, "@pedro");
		
		assertFalse("El automata tiene el estado de error", testProperty.getStates().contains(-1L));
		assertTrue("el automata no tiene el estado trampa", testProperty.getStates().contains(2L));
		assertEquals("El automata no tiene los ciclos sobre el estado trampa", 2, testProperty.getTransitions(2L, TransitionType.REQUIRED).size());
		
		for (Iterator it = testProperty.getStates().iterator(); it.hasNext();) {
			Long state = (Long) it.next();
			Set<String> outgoingActions = new HashSet<String>();
			for (Iterator it2 = testProperty.getTransitions(state, TransitionType.REQUIRED).iterator(); it2.hasNext();) {
				Pair<String, Long> pair = (Pair<String, Long>) it2.next();
				outgoingActions.add(pair.getFirst());
			}
			assertTrue(testProperty.getActions().size()<=outgoingActions.size()+1);
		}
	}
}
