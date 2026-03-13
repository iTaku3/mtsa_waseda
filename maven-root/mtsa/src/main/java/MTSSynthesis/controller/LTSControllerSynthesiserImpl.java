package MTSSynthesis.controller;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSSynthesis.ar.dc.uba.model.condition.FluentUtils;
import MTSSynthesis.ar.dc.uba.model.condition.Formula;
import MTSSynthesis.ar.dc.uba.model.language.Symbol;
import MTSSynthesis.controller.gr.GRGameSolver;
import MTSSynthesis.controller.gr.GRRank;
import MTSSynthesis.controller.gr.GRRankSystem;
import MTSSynthesis.controller.gr.StrategyState;
import MTSSynthesis.controller.gr.lazy.LazyGRGameSolver;
import MTSSynthesis.controller.gr.perfect.PerfectInfoGRGameSolver;
import MTSSynthesis.controller.model.GameSolver;
import MTSSynthesis.controller.model.Strategy;
import MTSSynthesis.controller.util.GRGameBuilder;
import MTSSynthesis.controller.util.GameStrategyToMTSBuilder;
import MTSSynthesis.controller.model.ControllerGoal;
import MTSSynthesis.controller.model.gr.GRGame;

public class LTSControllerSynthesiserImpl<S,A> implements LTSControllerSynthesiser<S,A> {

  private GRGame<S> game;
  
	/* (non-Javadoc)
	 * @see controller.LTSControllerSynthesiser#synthesise(ac.ic.doc.mtstools.model.MTS, controller.model.ControllerGoal)
	 */
	@Override
	public MTS<StrategyState<S, Integer>, A> synthesiseGR(MTS<S, A> plant, ControllerGoal<A> goal) {

		//TODO: Remove this when ConcurrencyGRGameSolver is ready.
		int maxLazyness = goal.getLazyness();
		GameSolver<S, Integer> solver;
		GRGameSolver<S> gSolver;
		
		if(maxLazyness > 0 ){
			game = new GRGameBuilder<S, A>().buildGRGameFrom(plant, goal);
			GRRankSystem<S> system = new GRRankSystem<S>(game.getStates(), game.getGoal().getGuarantees(), game.getGoal().getAssumptions(), game.getGoal().getFailures());
			gSolver = new LazyGRGameSolver<S>(game, system, maxLazyness);
			solver = gSolver;
		}else{
			game = new GRGameBuilder<S, A>().buildGRGameFrom(plant, goal);
			GRRankSystem<S> system = new GRRankSystem<S>(game.getStates(), game.getGoal().getGuarantees(), game.getGoal().getAssumptions(), game.getGoal().getFailures());
			gSolver = new PerfectInfoGRGameSolver<S>(game, system);
			solver = gSolver;
		}
		
		solver.solveGame();
		if (solver.isWinning(plant.getInitialState())) {
			Strategy<S, Integer> strategy = solver.buildStrategy();
			//TODO refactor permissive
			GRGameSolver<S> grSolver = (GRGameSolver<S>) gSolver;
			Set<Pair<StrategyState<S, Integer>, StrategyState<S, Integer>>> worseRank = grSolver.getWorseRank();
			MTS<StrategyState<S, Integer>, A> result = GameStrategyToMTSBuilder.getInstance().buildMTSFrom(plant, strategy, worseRank, maxLazyness);
			
			result.removeUnreachableStates();
			return result;
		} else {
			return null;
		}
	}

	

	/* (non-Javadoc)
	 * @see controller.LTSControllerSynthesiser#checkAssumptionsCompatibility(ac.ic.doc.mtstools.model.MTS, controller.model.ControllerGoal)
	 */
	@Override
	public boolean checkGRAssumptionsCompatibility(MTS<S, A> plant, ControllerGoal<A> goal) {
		
		if (!goal.getFaults().isEmpty()){
			Set<Symbol> symbols = FluentUtils.getInstance().getInitiatingSymbols(goal.getFluentsInFaults());
			goal.addAllControllableActions(this.symbolsToActions(symbols));
		}

		//no goals needed.
		goal.getGuarantees().clear();
		goal.getFaults().clear();
		
		goal.addGuarantee(Formula.FALSE_FORMULA);
		GRGame<S> game = new GRGameBuilder<S,A>().buildGRGameFrom(plant, goal);
		GRRankSystem<S> system = new GRRankSystem<S>(game.getStates(), game.getGoal().getGuarantees(), game.getGoal().getAssumptions(), game.getGoal().getFailures());
		
		GameSolver<S, Integer> solver = new PerfectInfoGRGameSolver<S>(game, system);
		solver.solveGame();
		
		/* 
		 * If for every reachable state the controller cannot force the assumptions 
		 * not to hold, then the assumptions are compatible with the plant.  
		 */
		Boolean stateRemoved = plant.removeUnreachableStates();
		Logger.getAnonymousLogger().log(Level.INFO, "Environment model with"+ (stateRemoved?"": "out") + " unreachable states");
		for (S state : plant.getStates()) {
			GRRank rank = system.getRank(new StrategyState<S,Integer>(state, 1));
			if (!rank.isInfinity()) {
				return false;
			}
		}
		return true;
	}

	/*
	 * This method is a hack needed to handle Symbols
	 */
	@SuppressWarnings("unchecked")
	private Set<A> symbolsToActions(Set<Symbol> symbols) {
		Set<A> actions = new HashSet<A>();
		for (Symbol symbol : symbols) {
			//TODO Symbol class needs refactor. Actually it's not needed. 
			actions.add((A)symbol.toString());
		}
		return actions;
	}

  @Override
  public GRGame<S> getGame()
  {
    return game;
  }
}
