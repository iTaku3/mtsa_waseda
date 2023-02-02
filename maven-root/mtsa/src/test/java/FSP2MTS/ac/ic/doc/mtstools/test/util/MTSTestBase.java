package FSP2MTS.ac.ic.doc.mtstools.test.util;

import junit.framework.TestCase;
import ltsa.lts.LTSOutput;
import MTSTools.ac.ic.doc.mtstools.model.impl.MTSImpl;


public class MTSTestBase extends TestCase {

	public static final String TAU_ACTION = "tau";
	public static final String TAU_MAYBE_ACTION = "tau?";
	
	public static final String A_ACTION = "a";
	public static final String A_MAYBE_ACTION = "a?";
	
	public static final String B_ACTION = "b";
	public static final String B_MAYBE_ACTION = "b?";

	public static final String C_ACTION = "c";
	public static final String C_MAYBE_ACTION = "c?";
	
	public static final Long ESTADO_CERO = new Long(0);
	public static final Long ESTADO_UNO = new Long(1);
	public static final Long ESTADO_MENOS_UNO = new Long(-1);
	public static final Long ESTADO_DOS = new Long(2);
	public static final Long ESTADO_TRES = new Long(3);
	public static final Long ESTADO_CUATRO = new Long(4);
	
	public LTSOutput ltsOutput = new TestLTSOuput();
	
	/**
	 * Devuelve un mts correspondiente al siguiente FSP:
	 * 	A = (a->A).
	 * 	Alphabet A = {tau, a}.
	 * 
	 * @return mts
	 */
	protected MTSImpl<Long, String> buildBasicMTS() {
		
		MTSImpl<Long, String> mts = buildEmptyMTS();
		mts.addAction(A_ACTION);
		mts.addState(ESTADO_CERO);
		mts.addRequired(ESTADO_CERO, A_ACTION, ESTADO_CERO);
	
		return mts;
	}

	/**
	 * Devuelve un mts sin transiciones solo con tau en el alfabeto
	 * @return
	 */
	protected MTSImpl<Long, String> buildEmptyMTS() {
		MTSImpl<Long, String> mts = new MTSImpl<Long, String>(ESTADO_CERO);
		mts.addAction(TAU_ACTION);
		return mts;
	}

	public void testDummy() {
		return; // Needed to avoid exception junit.framework.AssertionFailedError
	}

}
