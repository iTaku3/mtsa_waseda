package MTSSynthesis.controller.game.gr;


import static junit.framework.Assert.assertEquals;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import MTSSynthesis.controller.gr.GRRankSystem;
import MTSSynthesis.controller.gr.StrategyState;
import MTSSynthesis.controller.gr.perfect.PerfectInfoGRGameSolver;
import MTSSynthesis.controller.model.Assume;
import MTSSynthesis.controller.model.Assumptions;
import MTSSynthesis.controller.model.GameSolver;
import MTSSynthesis.controller.model.Guarantee;
import MTSSynthesis.controller.model.Guarantees;
import MTSSynthesis.controller.model.Strategy;
import MTSSynthesis.controller.model.gr.GRGame;
import MTSSynthesis.controller.model.gr.GRGoal;

public class GameSolverSimplePositiveTests extends GRGameSolverBaseTestCase {
	
	protected Guarantee<Long> buildGuarantee(Long[] longs) {
		Guarantee<Long> result = new Guarantee<Long>();
		for (int i = 0; i < longs.length; i++) {
			result.addState(longs[i]);
		}
		return result;
	}


	/*
	 * CASE 1: This case has a couple of variations. The simplest one is the one that's 
	 * runnable at the moment, which is basically 3 states in a "cycle" 
	 * of the form: state1 -> state2 -> state3 -> state1
	 * with states 1 and 3 controllable and state 2 uncontrollable. 
	 * Assumptions: 1= state1, Guarantees: 1= state3
	 * Using this configuration we get exactly what we wanted 
	 * a strategy such that it's the game. There's no choice for the players.
	 * 
	 *  CASE 2: If we delete the comment lines related to state4 (except for state4 -> state4 .. cycle)
	 *  we get the same game but with a dead end in state4 and again we get a correct strategy.
	 *  
	 *  CASE 3: If we add every state (from 1 to 4) to the assume of the game, it breaks the algorithm. 
	 *  Independently of the algorithm. It's odd, because if we have a dead end as part of an assume
	 *  then there should be a strategy because there is no way to ensure that always eventually assume 
	 *  holds. Am I correct?
	 *  
	 *  Note: I delete the "atLeastOne" check, to see what is the "output". 
	 */
	public void testSimpleGame21() throws Exception {
		long state1 = 1L;
		long state2 = 2L;
		long state3 = 3L;
		long state4 = 4L;

		Set<Long> states = new HashSet<Long>();	states.add(state1);	states.add(state2); states.add(state3);
		states.add(state4);

		Assumptions<Long> assumptions =  new Assumptions<Long>();
		Assume<Long> assume = new Assume<Long>();
		assume.addState(state1);
		assume.addState(state2);
		assume.addState(state3);
		assume.addState(state4);
		
		assumptions.addAssume(assume);
		Guarantees<Long> guarantees = buildSingleStateGuarantee(state3);

		Set<Long> faults = new HashSet<Long>();
		
		GRGoal<Long> grGoal = new GRGoal<Long>(guarantees, assumptions, faults, false);
		Set<Long> initialStates = new HashSet<Long>();
		initialStates.add(state1);		
		GRGame<Long> game = new GRGame<Long>(initialStates, states, grGoal);		
		game.addControllableSuccessor(state1, state2);
		game.addControllableSuccessor(state3, state1);
		game.addControllableSuccessor(state3, state4);
		
		game.addUncontrollableSuccessor(state2, state3);
				
		GRRankSystem<Long> rankSystem = new GRRankSystem<Long>(states, guarantees, assumptions,faults);		
		
		GameSolver<Long, Integer> solver = new PerfectInfoGRGameSolver<Long>(game, rankSystem);
		Strategy<Long, Integer> strategy = solver.buildStrategy();

		System.out.println(rankSystem);
		System.out.println(strategy);
		System.out.println(solver.getWinningStates());
		
		states.remove(state4);
		
		assertEquals(states, solver.getWinningStates());
		
		assertEquals(Collections.singleton(new StrategyState<Long, Integer>(state2,1)), strategy.getSuccessors(new StrategyState<Long, Integer>(state1,1)));
		assertEquals(Collections.singleton(new StrategyState<Long, Integer>(state3,1)), strategy.getSuccessors(new StrategyState<Long, Integer>(state2,1)));
		assertEquals(Collections.singleton(new StrategyState<Long, Integer>(state1,1)), strategy.getSuccessors(new StrategyState<Long, Integer>(state3,1)));
	}
	

}
