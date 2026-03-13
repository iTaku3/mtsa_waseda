package MTSSynthesis.controller.bgr;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import MTSSynthesis.controller.game.gr.GRGameSolverBaseTestCase;
import org.junit.Test;

import MTSSynthesis.controller.model.Assume;
import MTSSynthesis.controller.model.Assumptions;
import MTSSynthesis.controller.model.Guarantee;
import MTSSynthesis.controller.model.Guarantees;
import MTSSynthesis.controller.model.Strategy;

public class BGRGameSolverTest extends GRGameSolverBaseTestCase {

	@Test
	public void bgrEmptyBuchiExample() throws Exception {
		long state1 = 1;
		long state2 = 2;
		long state3 = 3;
		long state4 = 4;
		long state5 = 5;

		Set<Long> states = new HashSet<Long>();	states.add(state1);	states.add(state2); states.add(state3);
		states.add(state4); states.add(state5);	

		Assumptions<Long> assumptions =  new Assumptions<Long>();
		Assume<Long> assume = new Assume<Long>();
		assume.addState(state1);
		assumptions.addAssume(assume);
		Guarantees<Long> guarantees = new Guarantees<Long>();
		Guarantee<Long> guarantee = new Guarantee<Long>();
		guarantee.addState(state1); guarantee.addState(state2);guarantee.addState(state3);guarantee.addState(state4);guarantee.addState(state5);
		guarantees.addGuarantee(guarantee);
		
		Set<Long> buchi = new HashSet<Long>();
		Set<Long> faults = new HashSet<Long>();
		
		BGRGoal<Long> bgrGoal = new BGRGoal<Long>(guarantees, assumptions, faults, buchi);
		Set<Long> initialStates = new HashSet<Long>();
		initialStates.add(state1);
		BGRGame<Long> game = new BGRGame<Long>(initialStates, states, bgrGoal);
		game.addControllableSuccessor(state1, state3);
		game.addControllableSuccessor(state1, state2);
		game.addControllableSuccessor(state3, state1);
		game.addControllableSuccessor(state3, state2);
		game.addControllableSuccessor(state3, state5);
		game.addControllableSuccessor(state4, state1);
		game.addControllableSuccessor(state5, state2);
		
		game.addUncontrollableSuccessor(state2, state3);
		game.addUncontrollableSuccessor(state2, state4);
		
		this.fillPredecessors(game);
		
		BGRRankSystem<Long> rankSystem = new BGRRankSystem<Long>(states, guarantees, assumptions, faults, buchi);		
		
		BGRGameSolver<Long> solver = new BGRGameSolver<Long>(game, rankSystem);
		Strategy<Long, Integer> strategy = solver.buildStrategy();
	
		System.out.println(rankSystem);
		System.out.println(strategy);
		
		assertTrue(strategy.getStates().isEmpty());
	}

	@Test
	public void bgrNegativeExample() throws Exception {
		long state1 = 1;
		long state2 = 2;
		long state3 = 3;
		long state4 = 4;

		Set<Long> states = new HashSet<Long>();	states.add(state1);	states.add(state2); states.add(state3);
		states.add(state4);	

		Assumptions<Long> assumptions =  new Assumptions<Long>();
		Assume<Long> assume = new Assume<Long>();
		assume.addState(state1);
		assumptions.addAssume(assume);
		Guarantees<Long> guarantees = buildSingleStateGuarantee(state4);

		Set<Long> buchi = new HashSet<Long>();
		Set<Long> faults = new HashSet<Long>();

		buchi.add(state1);
		
		BGRGoal<Long> bgrGoal = new BGRGoal<Long>(guarantees, assumptions, faults, buchi);
		Set<Long> initialStates = new HashSet<Long>();
		initialStates.add(state1);
		BGRGame<Long> game = new BGRGame<Long>(initialStates, states, bgrGoal);		
		game.addControllableSuccessor(state1, state3);
		game.addControllableSuccessor(state1, state2);
		game.addControllableSuccessor(state3, state1);
		game.addControllableSuccessor(state3, state2);
		game.addControllableSuccessor(state4, state1);
		
		game.addUncontrollableSuccessor(state2, state3);
		game.addUncontrollableSuccessor(state2, state4);
		
		this.fillPredecessors(game);
		
		BGRRankSystem<Long> rankSystem = new BGRRankSystem<Long>(states, guarantees, assumptions, faults, buchi);		
		
		BGRGameSolver<Long> solver = new BGRGameSolver<Long>(game, rankSystem);
		Strategy<Long, Integer> strategy = solver.buildStrategy();
	
		System.out.println(rankSystem);
		System.out.println(strategy);
		
		assertTrue(strategy.getStates().isEmpty());
	}

	@Test
	public void bgrPositiveExample() throws Exception {
		long state1 = 1;
		long state2 = 2;
		long state3 = 3;
		long state4 = 4;
		long state5 = 5;

		Set<Long> states = new HashSet<Long>();	states.add(state1);	states.add(state2); states.add(state3);
		states.add(state4); states.add(state5);	

		Assumptions<Long> assumptions =  new Assumptions<Long>();
		Assume<Long> assume = new Assume<Long>();
		assume.addState(state1);
		assumptions.addAssume(assume);
		Guarantees<Long> guarantees = buildSingleStateGuarantee(state4);

		Set<Long> buchi = new HashSet<Long>();
		Set<Long> faults = new HashSet<Long>();

		buchi.add(state1); buchi.add(state5);
		
		BGRGoal<Long> bgrGoal = new BGRGoal<Long>(guarantees, assumptions, faults, buchi);
		Set<Long> initialStates = new HashSet<Long>();
		initialStates.add(state1);
		BGRGame<Long> game = new BGRGame<Long>(initialStates, states, bgrGoal);
		game.addControllableSuccessor(state1, state3);
		game.addControllableSuccessor(state1, state2);
		game.addControllableSuccessor(state3, state1);
		game.addControllableSuccessor(state3, state2);
		game.addControllableSuccessor(state3, state5);
		game.addControllableSuccessor(state4, state1);
		game.addControllableSuccessor(state5, state2);
		
		game.addUncontrollableSuccessor(state2, state3);
		game.addUncontrollableSuccessor(state2, state4);
		
		this.fillPredecessors(game);
		
		BGRRankSystem<Long> rankSystem = new BGRRankSystem<Long>(states, guarantees, assumptions, faults, buchi);		
		
		BGRGameSolver<Long> solver = new BGRGameSolver<Long>(game, rankSystem);
		Strategy<Long, Integer> strategy = solver.buildStrategy();
	
		System.out.println(rankSystem);
		System.out.println(strategy);
		
		assertTrue(!strategy.getStates().isEmpty());
	}
	
	@Test
	public void bgrEmptyGRExample() throws Exception {
		long state1 = 1;
		long state2 = 2;
		long state3 = 3;
		long state4 = 4;
		long state5 = 5;

		Set<Long> states = new HashSet<Long>();	states.add(state1);	states.add(state2); states.add(state3);
		states.add(state4); states.add(state5);	

		Assumptions<Long> assumptions =  new Assumptions<Long>();
		Assume<Long> assume = new Assume<Long>();
		assume.addState(state1);
		assumptions.addAssume(assume);
		Guarantees<Long> guarantees = buildSingleEmptyGuarantee();
		
		Set<Long> buchi = new HashSet<Long>();
		Set<Long> faults = new HashSet<Long>();

		buchi.add(state1); buchi.add(state5);
		
		BGRGoal<Long> bgrGoal = new BGRGoal<Long>(guarantees, assumptions, faults, buchi);
		Set<Long> initialStates = new HashSet<Long>();
		initialStates.add(state1);
		BGRGame<Long> game = new BGRGame<Long>(initialStates, states, bgrGoal);
		game.addControllableSuccessor(state1, state3);
		game.addControllableSuccessor(state1, state2);
		game.addControllableSuccessor(state3, state1);
		game.addControllableSuccessor(state3, state2);
		game.addControllableSuccessor(state3, state5);
		game.addControllableSuccessor(state4, state1);
		game.addControllableSuccessor(state5, state2);
		
		game.addUncontrollableSuccessor(state2, state3);
		game.addUncontrollableSuccessor(state2, state4);
		
		this.fillPredecessors(game);
		
		BGRRankSystem<Long> rankSystem = new BGRRankSystem<Long>(states, guarantees, assumptions, faults, buchi);		
		
		BGRGameSolver<Long> solver = new BGRGameSolver<Long>(game, rankSystem);
		Strategy<Long, Integer> strategy = solver.buildStrategy();
	
		System.out.println(rankSystem);
		System.out.println(strategy);
		
		assertTrue(strategy.getStates().isEmpty());
	}
	
	@Test
	public void bgrNoGuaranteeExample() throws Exception {
		long state1 = 1;
		long state2 = 2;
		long state3 = 3;
		long state4 = 4;
		long state5 = 5;

		Set<Long> states = new HashSet<Long>();	states.add(state1);	states.add(state2); states.add(state3);
		states.add(state4); states.add(state5);	

		Assumptions<Long> assumptions =  new Assumptions<Long>();
		Assume<Long> assume = new Assume<Long>();
		assume.addState(state1);
		assumptions.addAssume(assume);
		Guarantees<Long> guarantees = new Guarantees<Long>();
		
		Set<Long> buchi = new HashSet<Long>();
		Set<Long> faults = new HashSet<Long>();

		buchi.add(state1); buchi.add(state5);
		
		BGRGoal<Long> bgrGoal = new BGRGoal<Long>(guarantees, assumptions, faults, buchi);
		Set<Long> initialStates = new HashSet<Long>();
		initialStates.add(state1);
		BGRGame<Long> game = new BGRGame<Long>(initialStates, states, bgrGoal);
		game.addControllableSuccessor(state1, state3);
		game.addControllableSuccessor(state1, state2);
		game.addControllableSuccessor(state3, state1);
		game.addControllableSuccessor(state3, state2);
		game.addControllableSuccessor(state3, state5);
		game.addControllableSuccessor(state4, state1);
		game.addControllableSuccessor(state5, state2);
		
		game.addUncontrollableSuccessor(state2, state3);
		game.addUncontrollableSuccessor(state2, state4);
		
		this.fillPredecessors(game);
		
		BGRRankSystem<Long> rankSystem = new BGRRankSystem<Long>(states, guarantees, assumptions, faults, buchi);		
		
		BGRGameSolver<Long> solver = new BGRGameSolver<Long>(game, rankSystem);
		Strategy<Long, Integer> strategy = solver.buildStrategy();
	
		System.out.println(rankSystem);
		System.out.println(strategy);
		
		assertFalse(strategy.getStates().isEmpty());
	}


}
