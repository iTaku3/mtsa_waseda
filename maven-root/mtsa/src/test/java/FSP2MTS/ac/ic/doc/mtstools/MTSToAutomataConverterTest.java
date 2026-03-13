package FSP2MTS.ac.ic.doc.mtstools;

import ltsa.lts.CompactState;
import ltsa.lts.MyList;
import ltsa.lts.util.MTSUtils;

import MTSTools.ac.ic.doc.mtstools.model.MTS;
import FSP2MTS.ac.ic.doc.mtstools.test.util.MTSTestBase;
import ltsa.ac.ic.doc.mtstools.util.fsp.MTSToAutomataConverter;

public class MTSToAutomataConverterTest extends MTSTestBase {


	public void testMTSUnMTSAEstadoPorA() throws Exception {
		MTS<Long, String> mts = this.buildBasicMTS();
		
		MTSToAutomataConverter converter = new MTSToAutomataConverter();
		CompactState automata = (CompactState) converter.convert(mts, "dipi");
		assertNotNull(automata);
		
		String[] alphabet = automata.getTransitionsLabels();
		assertEquals(alphabet[0], TAU_ACTION);
		assertEquals(alphabet[1], TAU_MAYBE_ACTION);
		assertEquals(alphabet[2], A_ACTION);
		assertEquals(alphabet[3], A_MAYBE_ACTION);
		
		MyList transitions = automata.getTransitions(MTSUtils.encode(0));
		assertEquals(0, transitions.getFrom());
		assertEquals(A_ACTION, alphabet[transitions.getAction()]);
		assertEquals(0, MTSUtils.decode(transitions.getTo()));
		
		assertEquals(1, automata.states.length);
	}

	/*public void testMTSUnMTSAEstadoPorAMaybe() throws Exception {
		MTS<Long, String> mts = buildBasicMTS();
		mts.addAction(A_ACTION);
		mts.addState(ESTADO_CERO);
		mts.addTransition(ESTADO_CERO, A_ACTION, ESTADO_CERO, TransitionType.MAYBE);
		
		MTSToAutomataConverter converter = new MTSToAutomataConverter();
		CompactState automata = (CompactState) converter.convert(mts, "dipi");
		assertNotNull(automata);
		
		String[] alphabet = automata.getTransitionsLabels();
		Set<String> alphaNoTau = new HashSet<String>();
		for (int i = 0; i < alphabet.length; i++) {
			if (!alphabet[i].contains("tau")) {
				alphaNoTau.add(alphabet[i]);
			}
		}
		
		Set<String> expectedAlpha = new HashSet<String>();
		expectedAlpha.add("a");
		expectedAlpha.add("a?");
		
		assertEquals(alphaNoTau, expectedAlpha);
		
		MyList transitions = automata.getTransitions(MTSUtils.encode(0));

		assertEquals(0, transitions.getFrom());
		assertEquals(0, MTSUtils.decode(transitions.getTo()));
		assertEquals("a?", alphabet[transitions.getAction()]);
		
		assertEquals(1, automata.states.length);
	}*/
	
	/*public void testMTSDosMTSAEstadoPorAMaybe() throws Exception {
		MTS<Long, String> mts = buildBasicMTS();
		mts.addAction("a");
		mts.addState(ESTADO_CERO);
		mts.addState(ESTADO_UNO);
		mts.addTransition(ESTADO_CERO, "a", ESTADO_UNO, TransitionType.MAYBE);
		
		MTSToAutomataConverter converter = new MTSToAutomataConverter();
		CompactState automata = (CompactState) converter.convert(mts, "dipi");
		assertNotNull(automata);
		
		String[] alphabet = automata.getTransitionsLabels();
		assertEquals(alphabet[0], "a");
		assertEquals(alphabet[1], "a?");
		
		MyList transitions = automata.getTransitions(MTSUtils.encode(0));

		assertEquals(0, transitions.getFrom());
		assertEquals(1, MTSUtils.decode(transitions.getTo()));
		assertEquals("a?", alphabet[transitions.getAction()]);
		
		assertEquals(2, automata.states.length);
	}*/

	/*public void testMTSDosMTSAEstadoPorA() throws Exception {
		MTS<Long, String> mts = buildBasicMTS();
		mts.addAction("a");
		mts.addState(ESTADO_CERO);
		mts.addState(ESTADO_UNO);
		mts.addTransition(ESTADO_CERO, "a", ESTADO_UNO, TransitionType.REQUIRED);
		
		MTSToAutomataConverter converter = new MTSToAutomataConverter();
		CompactState automata = (CompactState) converter.convert(mts, "dipi");
		assertNotNull(automata);
		
		String[] alphabet = automata.getTransitionsLabels();
		assertEquals(alphabet[0], "a");
		assertEquals(alphabet[1], "a?");
		
		MyList transitions = automata.getTransitions(MTSUtils.encode(0));

		assertEquals(0, transitions.getFrom());
		assertEquals(1, MTSUtils.decode(transitions.getTo()));
		assertEquals("a", alphabet[transitions.getAction()]);
		
		assertEquals(2, automata.states.length);
	} */

	/*public void testMTSDosMTSAEstadoPorAyCicloEnCeroPorB() throws Exception {
		MTS<Long, String> mts = buildBasicMTS();
		mts.addAction("a");
		mts.addAction("b");
		mts.addState(ESTADO_CERO);
		mts.addState(ESTADO_UNO);
		mts.addTransition(ESTADO_CERO, "a", ESTADO_UNO, TransitionType.REQUIRED);
		mts.addTransition(ESTADO_CERO, "b", ESTADO_CERO, TransitionType.REQUIRED);
		
		MTSToAutomataConverter converter = new MTSToAutomataConverter();
		CompactState automata = (CompactState) converter.convert(mts, "dipi");
		assertNotNull(automata);
		
		String[] alphabet = automata.getTransitionsLabels();
		Set<String> alphabetSet = new HashSet<String>();
		Set<String> expectedAlphabetSet = new HashSet<String>();
		CollectionUtils.addAll(alphabetSet, alphabet);
		CollectionUtils.addAll(expectedAlphabetSet, new String[]{"a", "a?", B_ACTION, B_MAYBE_ACTION});
		assertEquals(expectedAlphabetSet, alphabetSet);
		
		MyList transitions = automata.getTransitions(MTSUtils.encode(0));
		
		if (alphabet[transitions.getAction()].equals(B_ACTION)) {
			assertEquals(0, transitions.getFrom());
			assertEquals(0, MTSUtils.decode(transitions.getTo()));
			assertEquals(B_ACTION, alphabet[transitions.getAction()]);
		} else {
			assertEquals(0, transitions.getFrom());
			assertEquals(1, MTSUtils.decode(transitions.getTo()));
			assertEquals("a", alphabet[transitions.getAction()]);
		}
		
		assertEquals(2, automata.states.length);
	}*/

}
