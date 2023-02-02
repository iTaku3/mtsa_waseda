package MTSATests.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import MTSSynthesis.controller.ControllerSynthesisFacade;
import ltsa.lts.CompositeState;
import ltsa.lts.LTSCompositionException;

import org.junit.Test;

import ltsa.ui.StandardOutput;
import MTSATests.util.TestConstants;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSAClient.ac.ic.doc.mtsa.MTSCompiler;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTSConstants;
import MTSTools.ac.ic.doc.mtstools.model.impl.MTSImpl;
import MTSTools.ac.ic.doc.mtstools.model.impl.WeakSemantics;
import ltsa.ac.ic.doc.mtstools.util.fsp.AutomataToMTSConverter;
import MTSTools.ac.ic.doc.mtstools.utils.GenericMTSToLongStringMTSConverter;
import MTSSynthesis.ar.dc.uba.model.condition.Fluent;
import MTSSynthesis.ar.dc.uba.model.condition.FluentImpl;
import MTSSynthesis.ar.dc.uba.model.condition.FluentPropositionalVariable;
import ltsa.control.util.ControlConstants;
import MTSSynthesis.controller.gr.GRRankSystem;
import MTSSynthesis.controller.gr.StrategyState;
import MTSSynthesis.controller.gr.perfect.PerfectInfoGRGameSolver;
import MTSSynthesis.controller.model.GameSolver;
import MTSSynthesis.controller.model.Strategy;
import MTSSynthesis.controller.util.GRGameBuilder;
import MTSSynthesis.controller.model.ControllerGoal;
import MTSSynthesis.controller.model.gr.GRGame;
import ltsa.dispatcher.TransitionSystemDispatcher;

public class NonDetControllerSynthesisTests {
	private MTSCompiler compiler = MTSCompiler.getInstance();

	@Test(expected = LTSCompositionException.class)
	public void test1() throws Exception {
		CompositeState model = compiler.compileCompositeState("C", TestConstants.fileFrom("nondet1.lts"));
	}
	
	@Test
	public void test2() throws Exception {
		String modelName = "C";
		String string = "nondet2.lts";
		String modelName2 = "E_C";
		testSynthesisedController(modelName, modelName2, string);
	}

	@Test
	public void test3() throws Exception {
		String modelName = "C";
		String string = "nondet3.lts";
		String modelName2 = "E_C";
		testSynthesisedController(modelName, modelName2, string);
	}

	@Test
	public void test4() throws Exception {
		String modelName = "C";
		String string = "nondet4.lts";
		String modelName2 = "E_C";
		testSynthesisedController(modelName, modelName2, string);
	}


	@Test
	public void test5() throws Exception {
		CompositeState model = compiler.compileCompositeState("C", TestConstants.fileFrom("nondet5.lts"));
		String name = model.getComposition().name;
		assertTrue("There is no controller for C", !name.contains(ControlConstants.NO_CONTROLLER));
	}
	
	@Test(expected = LTSCompositionException.class)
	public void test6() throws Exception {
		CompositeState model = compiler.compileCompositeState("C", TestConstants.fileFrom("nondet6.lts"));
	}

	/**
	 * Takes both the resultName and expectedName compositions defined in the file located at fileName 
	 * and checks if they are equivalent as weak and strong refinements
	 * @param resultName the name of the composition that will result from synthesis
	 * @param expectedName the name of the expected result
	 * @param fileName the location of the file containing both the expected and the to be synthesize definitions
	 * @throws IOException
	 */
	private void testSynthesisedController(String resultName, String expectedName, String fileName) throws IOException {
		CompositeState resultController = compiler.compileCompositeState(resultName, TestConstants.fileFrom(fileName));
		MTS<Long, String> result = AutomataToMTSConverter.getInstance().convert(resultController.getComposition());
		String name = resultController.getComposition().name;
		assertTrue("There is no controller for C", !name.contains(ControlConstants.NO_CONTROLLER));
		CompositeState expectedController = compiler.compileCompositeState(expectedName, TestConstants.fileFrom(fileName));
		MTS<Long, String> expected = AutomataToMTSConverter.getInstance().convert(expectedController.getComposition());
		//MTSTestUtils.areEquivalent(expected, result, new HashSet<String>());
	}
	
	public void testN() throws Exception {

		Long state0 = 0L;
		Long state1 = 1L;
		Long state2 = 2L;

		HashSet<String> controllableActions = new HashSet<String>();
		controllableActions.add("a");

		MTS<Long, String> mts = new MTSImpl<Long, String>(state0);
		mts.addAction("a");
		mts.addAction("b");
		mts.addAction("c");
		mts.addState(state1);
		mts.addState(state2);
		mts.addRequired(state0, "a", state1);
		mts.addRequired(state1, "b", state2);
		mts.addRequired(state2, "c", state0);

		ControllerGoal<String> goal = new ControllerGoal<String>();
		goal.addAllControllableActions(controllableActions);

		Fluent fluentA = new FluentImpl("fluentA", ControllerTestsUtils.buildSymbolSetFrom(new String[] { "a" }),
				ControllerTestsUtils.buildSymbolSetFrom(new String[] { "b", "c" }), false);
		Fluent fluentB = new FluentImpl("fluentB", ControllerTestsUtils.buildSymbolSetFrom(new String[] { "b" }),
				ControllerTestsUtils.buildSymbolSetFrom(new String[] { "a", "c" }), false);
		Fluent fluentC = new FluentImpl("fluentC", ControllerTestsUtils.buildSymbolSetFrom(new String[] { "c" }),
				ControllerTestsUtils.buildSymbolSetFrom(new String[] { "a", "b" }), true);

		Set<Fluent> fluents = new HashSet<Fluent>();
		fluents.add(fluentA);
		fluents.add(fluentB);
		fluents.add(fluentC);

		goal.addAllFluents(fluents);

		goal.addAssume(new FluentPropositionalVariable(fluentC));
		goal.addGuarantee(new FluentPropositionalVariable(fluentB));

		GRGame<Long> game = new GRGameBuilder<Long, String>().buildGRGameFrom(mts, goal);

		GRRankSystem<Long> rankSystem = new GRRankSystem<Long>(game.getStates(), game.getGoal().getGuarantees(),
				game.getGoal().getAssumptions(), new HashSet<Long>());

		GameSolver<Long, Integer> solver = new PerfectInfoGRGameSolver<Long>(game, rankSystem);
		Strategy<Long, Integer> strategy = solver.buildStrategy();

		assertEquals(game.getStates(), solver.getWinningStates());

		assertEquals(Collections.singleton(Pair.create(state1, 1)), strategy.getSuccessors(new StrategyState<Long, Integer>(state0, 1)));
		assertEquals(Collections.singleton(Pair.create(state2, 1)), strategy.getSuccessors(new StrategyState<Long, Integer>(state1, 1)));
		assertEquals(Collections.singleton(Pair.create(state0, 1)), strategy.getSuccessors(new StrategyState<Long, Integer>(state2, 1)));

		MTS<StrategyState<Long, Integer>, String> synthesiseController = new ControllerSynthesisFacade<Long, String, Integer>().synthesiseController(mts, goal);
		MTS<Long, String> synMTS = new GenericMTSToLongStringMTSConverter<StrategyState<Long, Integer>, String>().transform(synthesiseController);

		WeakSemantics weakSemantics = new WeakSemantics(Collections.singleton(MTSConstants.TAU));

		boolean refinement = TransitionSystemDispatcher.isRefinement(synMTS, " synthesised ", mts, " original ",
				weakSemantics, new StandardOutput());

		assertTrue(refinement);
	}
}
