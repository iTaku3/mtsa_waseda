package FSP2MTS.ac.ic.doc.mtstools.model.operations;

import MTSTools.ac.ic.doc.mtstools.model.operations.MTSDeadLockManipulator;
import MTSTools.ac.ic.doc.mtstools.model.operations.MTSDeadLockManipulatorImpl;
import ltsa.lts.CompactState;
import ltsa.lts.CompositeState;
import ltsa.lts.LTSOutput;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;
import MTSTools.ac.ic.doc.mtstools.model.MTSTrace;
import MTSTools.ac.ic.doc.mtstools.model.impl.LinkedListMTSTrace;
import MTSTools.ac.ic.doc.mtstools.model.impl.MTSTransitionImpl;
import FSP2MTS.ac.ic.doc.mtstools.test.util.LTSATestUtils;
import FSP2MTS.ac.ic.doc.mtstools.test.util.MTSTestBase;
import FSP2MTS.ac.ic.doc.mtstools.test.util.TestLTSOuput;
import ltsa.ac.ic.doc.mtstools.util.fsp.AutomataToMTSConverter;
import ltsa.dispatcher.TransitionSystemDispatcher;

public class DeadlockManipulatorTest extends MTSTestBase {

	protected LTSOutput ltsOutput;
	private MTSDeadLockManipulator<Long, String> deadlockFinder;
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.ltsOutput = new TestLTSOuput();
		this.deadlockFinder = new MTSDeadLockManipulatorImpl<Long, String>();
	}

	public void testDeadlockMaybeMTS() throws Exception {
		String sourceString = "A = (a?->STOP). \r\n";
		CompositeState compositeB = LTSATestUtils.buildAutomataFromSource(sourceString);
		TransitionSystemDispatcher.parallelComposition(compositeB, ltsOutput);
		MTS<Long, String> mts = AutomataToMTSConverter.getInstance().convert((CompactState) compositeB.getComposition());
		assertTrue(deadlockFinder.getDeadlockStatus(mts)==1);
	}

	public void testDeadlockSimpleMTS() throws Exception {
		String sourceString = "A = (a->b->STOP). \r\n";
		CompositeState compositeB = LTSATestUtils.buildAutomataFromSource(sourceString);
		TransitionSystemDispatcher.parallelComposition(compositeB, ltsOutput);
		MTS<Long, String> mts = AutomataToMTSConverter.getInstance().convert((CompactState) compositeB.getComposition());
		MTSTrace<String, Long> trace = new LinkedListMTSTrace<String, Long>();
		assertTrue(deadlockFinder.getTransitionsToDeadlock(mts, trace));
		MTSTrace<String, Long> expectedTrace = new LinkedListMTSTrace<String, Long>();
		expectedTrace.add(MTSTransitionImpl.createMTSEventState(0L, "a", 1L));
		expectedTrace.add(MTSTransitionImpl.createMTSEventState(1L, "b", 2L));
		assertEquals(expectedTrace, trace);		
		System.out.println("A = (a->b->STOP). :: Trace to deadlock: " + trace);
		
	}

	public void testDeadlockEmptyMTS() throws Exception {
		String sourceString = "A = (a->b->A). B = (b->a->B). ||C = (A||B). \r\n";
		CompositeState compositeB = LTSATestUtils.buildAutomataFromSource(sourceString);
		TransitionSystemDispatcher.parallelComposition(compositeB, ltsOutput);
		MTS<Long, String> mts = AutomataToMTSConverter.getInstance().convert((CompactState) compositeB.getComposition());
		MTSTrace<String, Long> trace = new LinkedListMTSTrace<String, Long>();
		assertTrue(deadlockFinder.getTransitionsToDeadlock(mts, trace));
		MTSTrace<String, Long> expectedTrace = new LinkedListMTSTrace<String, Long>();
		assertEquals(expectedTrace, trace);		
		System.out.println("A = (a->b->A). B = (b->a->B). ||C = (A||B). :: Trace to deadlock: " + trace);
	}
	
	public void testSimpleDeadlock() throws Exception {
		String sourceString = "A = (a->b->c?->A). \r\n";
		CompositeState composite = LTSATestUtils.buildCompositeState(sourceString, ltsOutput);
		MTS<Long, String> mts = AutomataToMTSConverter.getInstance().convert((CompactState) composite.getComposition());
		MTSTrace<String, Long> trace = new LinkedListMTSTrace<String, Long>();
		assertFalse(deadlockFinder.getTransitionsToDeadlock(mts, trace));
		MTSTrace<String, Long> expectedTrace = new LinkedListMTSTrace<String, Long>();
		assertEquals(expectedTrace, trace);		
		System.out.println("A = (a->b->c?->A). :: Trace to deadlock: " + trace);
	}
	
	public void testDeadLockPathForDeadlockState() throws Exception {
		String sourceString = "A = (a->(c?->A|llegaElDeadlock->STOP)).\r\n";
		CompositeState composite = LTSATestUtils.buildCompositeState(sourceString, ltsOutput);
		MTS<Long, String> mts = AutomataToMTSConverter.getInstance().convert((CompactState) composite.getComposition());
		MTSTrace<String, Long> trace = new LinkedListMTSTrace<String, Long>();
		assertTrue(deadlockFinder.getTransitionsToDeadlock(mts, trace));
		MTSTrace<String, Long> expectedTrace = new LinkedListMTSTrace<String, Long>();
		expectedTrace.add(MTSTransitionImpl.createMTSEventState(0L, "a", 1L));
		expectedTrace.add(MTSTransitionImpl.createMTSEventState(1L, "llegaElDeadlock", 2L));
		assertEquals(expectedTrace, trace);		
		System.out.println("A = (a->(c?->A|llegaElDeadlock->STOP)). :: Trace to deadlock: " + trace);
	}
	
	public void testComplicatedDeadLockPath() throws Exception {
//		String sourceString = "A = (b->(x->v->STOP | y->A) | c->A ).\r\n"; 
		String sourceString = "A = (b->XY | c->D ),\r\n" + 
								"XY = (x->v->STOP | y->D),\r\n" + 
								"D = (d->D).\r\n";
		
		CompositeState composite = LTSATestUtils.buildCompositeState(sourceString, ltsOutput);
		MTS<Long, String> mts = AutomataToMTSConverter.getInstance().convert((CompactState) composite.getComposition());
		MTSTrace<String, Long> trace = new LinkedListMTSTrace<String, Long>();
		assertTrue(deadlockFinder.getTransitionsToDeadlock(mts, trace));
		System.out.println("A = (b->XY | c->D ), XY = (x->v->STOP | y->D), D = (d->D).");
		System.out.println("Trace to deadlock: " + trace);
	}
	
	public void testRemoveTransitionsToDeadlock() throws Exception {
		String sourceString = "A = (g->w->A | c->z?->A).\r\n";
		CompositeState composite = LTSATestUtils.buildAutomataFromSource(sourceString);
		TransitionSystemDispatcher.parallelComposition(composite, ltsOutput);
		CompactState compactState = TransitionSystemDispatcher.getPessimistModel(composite.getComposition());
		MTS<Long, String> mts = AutomataToMTSConverter.getInstance().convert((CompactState) compactState);
		for (Long state : mts.getStates()) {
			assertTrue(mts.getTransitions(state, TransitionType.POSSIBLE).size()==1);
		}
	}

	public void testDeadLockPathForOnlyMaybeOutgoing() throws Exception {
		/*
		String sourceString = "A = (g->w->A1 | c->z->A2), A1 = (t?->A|aaa->n->A), A2 = (dead2->STOP|dead1?->n->A).\r\n";
		CompositeState composite = LTSATestUtils.buildCompositeState(sourceString, ltsOutput);
		MTS<Long, String> mts = AutomataToMTSConverter.getInstance().convert((CompactState) composite.getComposition());
		MTSTrace<String, Long> trace = new LinkedListMTSTrace<String, Long>();
		assertTrue(deadlockFinder.getTransitionsToDeadlock(mts, trace));
		System.out.println("A = (g->w->A1 | c->z->A2), A1 = (t?->A|aaa->n->A), A2 = (dead2->STOP|dead1?->n->A).");
		System.out.println("Trace to deadlock: " + trace);
		assertTrue(mts.getTransitions(7L, TransitionType.POSSIBLE).size()==0);
		assertEquals(1L, deadlockFinder.getDeadlockStatus(mts));
		 */
	}
	

}
