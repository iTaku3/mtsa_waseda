package MTSATests.controller;

import java.io.IOException;
import java.util.Set;


import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import MTSSynthesis.controller.gr.StrategyState;
import MTSSynthesis.controller.model.SafeGameSolver;
import MTSSynthesis.controller.model.Strategy;
import MTSSynthesis.controller.util.GameStrategyToLTSBuilder;
import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSTools.ac.ic.doc.mtstools.model.impl.LTSSimulationSemantics;
import MTSTools.ac.ic.doc.mtstools.model.impl.MTSAdapter;
import MTSTools.ac.ic.doc.mtstools.utils.GenericLTSToLongStringLTSConverter;

public class SafeGameSolverTests{
	
	
	private LTS<StrategyState<Long, Integer>, String> getLTSSafeGameStrategyFromFile(String ltsFilename, String modelName, String controllerName){
		//parse input file
		LTS<Long, String> env = LTSTestHelper.getInstance().getLTSFromFile(ltsFilename, modelName);
		
		Set<String> controllableActions = LTSTestHelper.getInstance().getControllableActionsFromFile(ltsFilename, controllerName);
		
		SafeGameSolver<Long, String> gameSolver = new SafeGameSolver<Long,String>(env, controllableActions);
		gameSolver.solveGame();
		if (gameSolver.isWinning(env.getInitialState())) {
			Strategy<Long, Integer> strategy = gameSolver.buildStrategy();

			LTS<StrategyState<Long, Integer>, String> result = GameStrategyToLTSBuilder.getInstance().buildLTSFrom(env, strategy);

			result.removeUnreachableStates();
			return result;
		} else {
			return null;
		}		
		
	}
	
	@DataProvider(name = "safeGameTest")
	public Object[][] safeGameParameters() {
		return new Object[][] {
		{"SafetyCuts/Tests/safe_game_test.lts","MUT","EXP","C","G1","SafetyCuts/Results/safe_game_test.results"}
		};
	}	
	
	@Test(dataProvider = "safeGameTest")
	private void SafeGameTest(String filename, String mutName,
			String expName, String controllerName, String controllerGoalName,
			String resultFileName) throws Exception, IOException {
		// TODO: check expected throwing null and gamesolver not adding states
		// to winning
		// TODO: this part is not consistent with our theory, but since we're
		// loading our process and losing part of the alphabet
		// we need it to keep the evaluation working, next step should be to
		// extend the process alphabet to the one defined whithin the lts file
		LTS<Long, String> mut = getMut(filename, mutName, controllerName, controllerGoalName);
		LTS<Long, String> exp = getExpected(filename, expName);
		LTSTestHelper.getInstance().removeUnusedActionsFromLTS(mut);
		LTSTestHelper.getInstance().removeUnusedActionsFromLTS(exp);
		MTSAdapter<Long, String> mutAdapter = new MTSAdapter<Long, String>(mut);
		MTSAdapter<Long, String> expAdapter = new MTSAdapter<Long, String>(exp);
		LTSSimulationSemantics simulationSemantics = new LTSSimulationSemantics();
		Assert.assertTrue(simulationSemantics.isARefinement(mutAdapter, expAdapter));
		Assert.assertTrue(simulationSemantics.isARefinement(expAdapter, mutAdapter));		
	}

	protected LTS<Long, String> getMut(String filename, String mutName,
			String controllerName, String controllerGoalName) {
		LTS<StrategyState<Long, Integer>, String> strategyMut = getLTSSafeGameStrategyFromFile(filename, mutName, controllerName);
		LTS<Long, String> mut = new GenericLTSToLongStringLTSConverter<StrategyState<Long, Integer>, String>().transform(strategyMut);
		LTSTestHelper.getInstance().removeUnusedActionsFromLTS(mut);
		return mut;
	}

	protected LTS<Long, String> getExpected(String filename, String expectedName) {
		LTS<Long, String> exp = LTSTestHelper.getInstance().getLTSFromFile(filename, expectedName);
		LTSTestHelper.getInstance().removeUnusedActionsFromLTS(exp);
		return exp;
	} 
	
}
