package MTSSynthesis.controller.util;


import MTSSynthesis.ar.dc.uba.model.condition.Fluent;
import MTSSynthesis.ar.dc.uba.model.condition.FluentUtils;
import MTSSynthesis.ar.dc.uba.model.condition.Formula;
import MTSSynthesis.ar.dc.uba.model.language.Symbol;
import MTSSynthesis.controller.model.Assume;
import MTSSynthesis.controller.model.Assumptions;
import MTSSynthesis.controller.model.Guarantee;
import MTSSynthesis.controller.model.Guarantees;
import MTSSynthesis.controller.model.*;
import MTSSynthesis.controller.model.gr.GRGame;
import MTSSynthesis.controller.model.gr.GRGoal;
import MTSSynthesis.controller.model.gr.concurrency.ConcurrencyLevel;
import MTSSynthesis.controller.model.gr.concurrency.GRCGame;
import MTSSynthesis.controller.model.gr.concurrency.GRCGoal;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;
import org.apache.commons.lang.Validate;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Builds a GR game.
 * 
 * @author dipi
 * 
 */
//TODO MTS -> LTS
public class GRGameBuilder<State, Action> {
	
	/**
	 * Builds a GRGame from an MTS. The MTS is assumed to be an LTS
	 * representation (i.e. only required transitions). 
	 * 
	 */
	public GRGame<State> buildGRGameFrom(MTS<State, Action> mts, ControllerGoal<Action> goal) {
		this.validateActions(mts, goal); 
		
		Assumptions<State> assumptions = new Assumptions<State>();
		Guarantees<State> guarantees = new Guarantees<State>();
		Set<State> failures = new HashSet<State>();
		FluentStateValuation<State> valuation = buildGoalComponents(mts, goal, assumptions, guarantees, failures);
		
		
		GRGoal<State> grGoal = new GRGoal<State>(guarantees, assumptions, failures,  goal.isPermissive());
		Set<State> initialStates = new HashSet<State>();
		initialStates.add(mts.getInitialState());			
		GRGame<State> game = new GRGame<State>(initialStates, mts.getStates(), grGoal);
		this.initialiseSuccessors(mts, goal, game);
		return game;
	}
	
	
	public GRCGame<State> buildGRCGameFrom(MTS<State, Action> mts, ControllerGoal<Action> goal) {
		this.validateActions(mts, goal); 
		
		Assumptions<State> assumptions = new Assumptions<State>();
		Guarantees<State> guarantees = new Guarantees<State>();
		Set<State> failures = new HashSet<State>();
		ConcurrencyLevel<State> concurrencyFluents = new ConcurrencyLevel<State>();
		FluentStateValuation<State> valuation = buildGoalComponents(mts, goal, assumptions, guarantees, failures, concurrencyFluents);
		
		GRCGoal<State> grGoal = new GRCGoal<State>(guarantees, assumptions, failures,  goal.isPermissive(), concurrencyFluents);
		Set<State> initialStates = new HashSet<State>();
		initialStates.add(mts.getInitialState());				
		GRCGame<State> game = new GRCGame<State>(initialStates, mts.getStates(), grGoal);
		this.initialiseSuccessors(mts, goal, game);
		return game;
	}
	
	
	protected FluentStateValuation<State> buildGoalComponents(MTS<State, Action> mts,
															  ControllerGoal<Action> goal, Assumptions<State> assumptions,
															  Guarantees<State> guarantees, Set<State> failures, ConcurrencyLevel<State> concurrencyFluents ){
			FluentStateValuation<State> valuation = buildGoalComponents(mts, goal, assumptions, guarantees, failures);
			this.formulasToConcurrency(concurrencyFluents, mts.getStates(), goal.getConcurrencyFluents(), valuation);
			return valuation;
	}

	protected FluentStateValuation<State> buildGoalComponents(MTS<State, Action> mts,
															  ControllerGoal<Action> goal, Assumptions<State> assumptions,
															  Guarantees<State> guarantees, Set<State> failures) {
		FluentStateValuation<State> valuation = FluentUtils.getInstance().buildValuation(mts, goal.getFluents());
		
		this.formulasToAssumptions(assumptions, mts.getStates(), goal.getAssumptions(),valuation);
		this.formulasToGuarantees(guarantees, mts.getStates(),goal.getGuarantees(),valuation);
		this.formulaToStateSet(failures, mts.getStates(), goal.getFaults(), valuation);
//		for (Fluent f : goal.getFluents())
//      for (State s : valuation.getStates())
//        System.out.println("s"+s+" f "+f.getName()+" = "+valuation.isTrue(s, f));
		return valuation; //this contains the marked states, only fluents not a goal formula (even propositional), need to output formula itself into prism and evaluate there
	}

	protected void formulaToStateSet(Set<State> toBuild,
			Set<State> allStates, List<Formula> formulas, FluentStateValuation<State> valuation) {

		for (Formula formula : formulas) {
			for (State state : allStates) {
				this.formulaToStateSet(toBuild, formula, state, valuation);
			}
			if (toBuild.isEmpty()) {
				Logger.getAnonymousLogger().log(Level.WARNING, "No state satisfies formula: " + formula);
			}
		}
	}

	protected void formulaToStateSet(Set<State> toBuild, Formula formula, State state, FluentStateValuation<State> valuation) {
		valuation.setActualState(state);
		if (formula.evaluate(valuation)) {
			toBuild.add(state);
		}
	}

	
	public void validateActions(MTS<State, Action> mts, ControllerGoal<Action> goal) {
		Set<Action> actions = mts.getActions();
		if (!actions.containsAll(goal.getControllableActions())) {
			Collection<Action> controllableNotIn = new HashSet<Action>();
			
			for (Action action : goal.getControllableActions()) {
				if (!actions.contains(action)) {
					controllableNotIn.add(action);
				}
			}
			Logger.getAnonymousLogger().log(Level.WARNING, "The following actions in the controller " +
					"Goal does not belong to the mts action set.\n" + controllableNotIn);
		}
		
		for (Fluent fluent : goal.getFluents()) {
			this.validateFluentSymbols(fluent, actions, fluent.getInitiatingActions());
			this.validateFluentSymbols(fluent, actions, fluent.getTerminatingActions());
		}
	}

	private void validateFluentSymbols(Fluent fluent, Set<Action> actions,
			Set<Symbol> initiatingActions) {
		for (Symbol symbol : initiatingActions) {
			Validate.isTrue(actions.contains(symbol.toString()), "\n Every action in " + fluent
					+ " must be included in model action set. \n Action: " + symbol.toString() 
					+ " does not belong to actions set.");
		}
	}

	private void initialiseSuccessors(MTS<State, Action> mts, ControllerGoal<Action> goal, GRGame<State> game) {
		Set<Action> controllableActions = goal.getControllableActions();

		for (State from : mts.getStates()) {
			for (Pair<Action, State> tr : mts.getTransitions(from, TransitionType.REQUIRED)) {
				State to = tr.getSecond();
				if (!controllableActions.contains(tr.getFirst())) {
					game.addUncontrollableSuccessor(from, to);
				} else {
					game.addControllableSuccessor(from, to);
				}
			}
			if (!game.isUncontrollable(from)) {
				for (State state : game.getControllableSuccessors(from)) {
					game.addPredecessor(from, state);
				}
			}
		}
	}

	private void formulasToAssumptions(Assumptions<State> assumptions, Set<State> states, List<Formula> formulas, FluentStateValuation<State> valuation) {

		for (Formula formula : formulas) {
			Assume<State> assume = new Assume<State>();
			for (State state : states) {
				valuation.setActualState(state);
				if (formula.evaluate(valuation)) {
					assume.addState(state);
				}
			}
			if (assume.isEmpty()) {
				Logger.getAnonymousLogger().warning("There is no state satisfying formula:" + formula);
			}
			assumptions.addAssume(assume);
		}
		
		if (assumptions.isEmpty()) {
			Assume<State> trueAssume = new Assume<State>();
			trueAssume.addStates(states);
			assumptions.addAssume(trueAssume);
		}
	}

	private void formulasToGuarantees(Guarantees<State> guarantees, Set<State> states,
			List<Formula> formulas, FluentStateValuation<State> valuation) {

		for (Formula formula : formulas) {
			Guarantee<State> guarantee = new Guarantee<State>();
			for (State state : states) {
				valuation.setActualState(state);
				if (formula.evaluate(valuation)) {
					guarantee.addState(state);
				}
			}
			if (guarantee.isEmpty()) {
				Logger.getAnonymousLogger().warning("There is no state satisfying formula:" + formula);
			}
			guarantees.addGuarantee(guarantee);
		}
		
		if (guarantees.isEmpty()) {
			Guarantee<State> trueAssume = new Guarantee<State>();
			trueAssume.addStates(states);
			guarantees.addGuarantee(trueAssume);
		}
	}
	
	private void formulasToConcurrency(ConcurrencyLevel<State> concurrencyLevel, Set<State> states,
			Set<Fluent> set, FluentStateValuation<State> valuation) {

		for (State state : states) {
			Integer level = 0;
			valuation.setActualState(state);
			for (Fluent fluent : set) {
				if (valuation.isTrue(state, fluent)) {
					level++;
				}
			}
			concurrencyLevel.updateLevel(state, level);
		}
	}
}