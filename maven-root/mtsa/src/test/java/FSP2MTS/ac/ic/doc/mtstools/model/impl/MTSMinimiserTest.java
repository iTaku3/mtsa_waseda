package FSP2MTS.ac.ic.doc.mtstools.model.impl;

import java.util.Collections;

import ltsa.lts.CompactState;
import ltsa.lts.CompositeState;
import ltsa.dispatcher.TransitionSystemDispatcher;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTSConstants;
import FSP2MTS.ac.ic.doc.mtstools.test.util.LTSATestUtils;
import FSP2MTS.ac.ic.doc.mtstools.test.util.MTSTestBase;
import FSP2MTS.ac.ic.doc.mtstools.test.util.MTSTestUtils;
import FSP2MTS.ac.ic.doc.mtstools.test.util.TestLTSOuput;
import ltsa.ac.ic.doc.mtstools.util.fsp.AutomataToMTSConverter;

public class MTSMinimiserTest extends MTSTestBase {

	/*public void testLittleBrothersDesdeLaMismaTransaccion() throws Exception {
		String sourceString = "A = (y->CM | x->C), CM = (c?->CM), C = (c->C).\r\n";

		this.assertMinimisation(sourceString);		
	}*/
	
	/*public void testMinimisaCorreccionBug1SiBorrasLitleBrothersNoAnda() throws Exception {
		String sourceString = "A = (c->A | c->C), C = (c?->C).\r\n";

		this.assertMinimisation(sourceString);
	}*/
	
	public void testMinimisaEjemploLibroModelChecking() throws Exception {
		String sourceString = "X = (x?->A|x?->B),\r\n" + 
							"A = (a->(b->C1|b->D1)|a->b->D2),\r\n" + 
							"B = (a->(b->C2|b->D3)|a->b->C3),\r\n" + 
							"C1 = (c->C1),\r\n" + 
							"C2 = (c->C2),\r\n" + 
							"C3 = (c->C3),\r\n" + 
							"D1 = (d->D1),\r\n" + 
							"D2 = (d->D2),\r\n" + 
							"D3 = (d->D3).\r\n";

		this.assertMinimisation(sourceString);		
	}
	
	public void testMinimisaEjemploABCConEstadoFinalMaybe() throws Exception {
		String sourceString = "A = (a->(b->C1|b->C2) | a?->b->C3), " +
				"C1 = (c->C1), " +
				"C2 = (c?->C2), " + 
				"C3 = (c->C3). \r\n";

		this.assertMinimisation(sourceString);
	}

	public void testMinimisaEjemploPaper() throws Exception {
		String sourceString = "A = (a?->b->B | a?->(b->B | b->C) | a?->(b->c->e->A | b->C) ),\r\n" + 
				"C = (c->d->A | c->e->A),\r\n" + 
				"B = (c->d->A).\r\n";

		this.assertMinimisation(sourceString);		
	}

	public void testMinimisaEjemploABSimple() throws Exception {
		String sourceString = "A = (a?->b->A | a?->b->A).\r\n";
		this.assertMinimisation(sourceString);
	}

	private void assertMinimisation(String sourceString) throws Exception {
		TestLTSOuput testLTSOuput = new TestLTSOuput();
		CompositeState composite = LTSATestUtils.buildCompositeState(sourceString, testLTSOuput);
		MTS<Long, String> originalMTS = AutomataToMTSConverter.getInstance().convert((CompactState) composite.getComposition());
		
		TransitionSystemDispatcher.minimise(composite, testLTSOuput);
		MTS<Long, String> finalMTS = AutomataToMTSConverter.getInstance().convert((CompactState) composite.getComposition());

		assertFalse(finalMTS.equals(originalMTS));
		MTSTestUtils.areEquivalent(originalMTS, finalMTS, Collections.singleton(MTSConstants.TAU));
		assertTrue(originalMTS.getStates().size()>=finalMTS.getStates().size());
	}
}