package MTSATests.controller;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import ltsa.control.util.ControllerUtils;

import junit.framework.TestCase;
import MTSTools.ac.ic.doc.commons.collections.PowerSet;
import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;
import MTSTools.ac.ic.doc.commons.relations.BinaryRelationUtils;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;
import MTSTools.ac.ic.doc.mtstools.model.impl.MTSImpl;
import FSP2MTS.ac.ic.doc.mtstools.test.util.MTSTestUtils;

public class StarEnvTests extends TestCase {

	Long state0 = 0L;
	Long state1 = 1L;
	Long state2 = 2L;
	Long state3 = 3L;
	Long state4 = 4L;
	
	String u1 = "u1";
	String u2 = "u2";
	String c1 = "c1";
	String c2 = "c2";
	
	/**
	 * Running example of paper 2011-MTS Control
	 * 
	 * @throws Exception
	 */
	public void testSimpleStarredModel() throws Exception {
		MTS<Long, String> mts = new MTSImpl<Long, String>(state0);
		mts.addAction(u1);
		mts.addAction(u2);
		mts.addAction(c1);
		mts.addAction(c2);
		mts.addState(state1);
		mts.addState(state2);
		mts.addState(state3);
		mts.addState(state4);
		mts.addRequired(state0, c1, state1);
		mts.addRequired(state0, u1, state2);
		mts.addPossible(state0, c2, state3);
		mts.addPossible(state0, u2, state4);
		
		MTS<Pair<Long, Set<String>>, String> generateStarredEnvModel = ControllerUtils.generateStarredEnvModel(mts);
		
		Set<Pair<Long, Set<String>>> states = generateStarredEnvModel.getStates();
		System.out.println(states);
		assertEquals(9, states.size());
		
		Pair<Long, Set<String>> state0Bis = Pair.create(state0, (Set<String>)null);
		BinaryRelation<String, Pair<Long, Set<String>>> transitionsFrom0Bis = generateStarredEnvModel.getTransitions(state0Bis, TransitionType.POSSIBLE);

		Set<String> labelsFrom0Bis = BinaryRelationUtils.getDomain(transitionsFrom0Bis);

		assertEquals(4, labelsFrom0Bis.size());
		
		PowerSet<String> ps = new PowerSet<String>(BinaryRelationUtils.getDomain(mts.getTransitions(state0, TransitionType.MAYBE)));
		for (Set<String> set : ps) {
			labelsFrom0Bis.containsAll(set);
		}

		for (Long state : mts.getStates()) {
			if(!state.equals(state0)){			
				Pair<Long, Set<String>> stateBis = Pair.create(state, (Set<String>)null);
				assertTrue(generateStarredEnvModel.getStates().contains(stateBis));
				BinaryRelation<String, Pair<Long, Set<String>>> transitions = generateStarredEnvModel.getTransitions(stateBis, TransitionType.POSSIBLE);
				assertNotNull(transitions);
	//			assertTrue(transitions.isEmpty());
				assertEquals(0, transitions.size());
			}
		}
	}
	
	
	public void testStateWithNoReqs() throws Exception {
			MTS<Long, String> mts = new MTSImpl<Long, String>(state0);
			mts.addAction(u1);
			mts.addState(state1);
			mts.addPossible(state0, u1, state1);
			
			MTS<Pair<Long, Set<String>>, String> starE = ControllerUtils.generateStarredEnvModel(mts);
			
			Pair<Long, Set<String>> initialState = Pair.create(state0, (Set<String>)null);
			MTS<Pair<Long, Set<String>>, String> expected = new MTSImpl<Pair<Long,Set<String>>, String>(initialState);
			expected.addActions(mts.getActions());
			Set<String> label = new HashSet<String>();
			label.add(u1);
			expected.addAction(label.toString());
			Pair<Long, Set<String>> initialBis = Pair.create(state0, label);
			Pair<Long, Set<String>> oneBis = Pair.create(state1, (Set<String>)null);
			expected.addState(initialBis);
			expected.addState(oneBis);
			expected.addRequired(initialState, label.toString(), initialBis);
			expected.addRequired(initialBis, u1, oneBis);
			MTSTestUtils.areEquivalent(starE, expected, Collections.singleton("tau"));
	}
	
}
