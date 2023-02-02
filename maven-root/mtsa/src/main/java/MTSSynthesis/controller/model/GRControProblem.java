package MTSSynthesis.controller.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.google.common.collect.Sets;

import MTSSynthesis.controller.util.FluentStateValuation;

import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSTools.ac.ic.doc.mtstools.model.impl.MTSAdapter;
import MTSSynthesis.ar.dc.uba.model.condition.FluentUtils;
import MTSSynthesis.ar.dc.uba.model.condition.Formula;

public abstract class GRControProblem<S, A, M> implements ControlProblem<S, A> {

	protected ControllerGoal<A> controllerGoal;
	protected LTS<S,A> environment;	
	protected Set<A> controllable;
	protected boolean problemSolved;
	private LTS<S,A> solution;


	
	public GRControProblem(LTS<S, A> environment,
			ControllerGoal<A> controllerGoal){
		this.environment = environment;
		this.controllerGoal = controllerGoal;
		this.controllable	= controllerGoal.getControllableActions();
		this.problemSolved = false;
	}
	
	@Override
	public LTS<S, A> solve() {
		if(!problemSolved){
			solution = primitiveSolve();
		}
		return solution;
	}
	
	protected abstract LTS<S, A> primitiveSolve();
	
	protected Set<Set<S>> buildGuarantees() {
		FluentStateValuation<S> valuation = FluentUtils.getInstance()
				.buildValuation(new MTSAdapter<S, A>(environment), controllerGoal.getFluents());

		Guarantees<S> guarantees = new Guarantees<S>();
		formulasToGuarantees(guarantees, environment.getStates(), controllerGoal.getGuarantees(),
				valuation);
		/*for (Fluent f : grControllerGoal.getFluents())
			for (S s : valuation.getStates())
//				System.out.println("s" + s + " f " + f.getName() + " = "
						+ valuation.isTrue(s, f));*/
		Set<Set<S>> returnSet = new HashSet<Set<S>>();
		for (Guarantee<S> g : guarantees) {
			returnSet.add(g.getStateSet());
		}
		return returnSet; // this contains the marked states, only fluents not a
							// goal formula (even propositional), need to output
							// formula itself into prism and evaluate there
	}	
	
	private void formulasToGuarantees(Guarantees<S> guarantees,
			Set<S> states, List<Formula> formulas,
			FluentStateValuation<S> valuation) {

		for (Formula formula : formulas) {
			Guarantee<S> guarantee = new Guarantee<S>();
			for (S state : states) {
				valuation.setActualState(state);
				if (formula.evaluate(valuation)) {
					guarantee.addState(state);
				}
			}
			if (guarantee.isEmpty()) {
				Logger.getAnonymousLogger().warning(
						"There is no state satisfying formula:" + formula);
			}
			guarantees.addGuarantee(guarantee);
		}

		if (guarantees.isEmpty()) {
			Guarantee<S> trueAssume = new Guarantee<S>();
			trueAssume.addStates(states);
			guarantees.addGuarantee(trueAssume);
		}
	}	
	
	protected Set<S> buildAssumptions() {
		FluentStateValuation<S> valuation = FluentUtils.getInstance()
				.buildValuation(new MTSAdapter<S, A>(environment), controllerGoal.getFluents());

		Assumptions<S> assumptions = new Assumptions<S>();
		formulasToAssumptions(assumptions, environment.getStates(), controllerGoal.getGuarantees(),
				valuation);
		Set<S> returnSet = new HashSet<S>();
		for(int i = 1; i <= assumptions.getSize(); i++){
			returnSet = Sets.union(returnSet, assumptions.getAssume(i).getStateSet());
		}
		return returnSet; // this contains the marked states, only fluents not a
							// goal formula (even propositional), need to output
							// formula itself into prism and evaluate there
	}		
	
	private void formulasToAssumptions(Assumptions<S> assumptions, Set<S> states, List<Formula> formulas, FluentStateValuation<S> valuation) {

		for (Formula formula : formulas) {
			Assume<S> assume = new Assume<S>();
			for (S state : states) {
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
			Assume<S> trueAssume = new Assume<S>();
			trueAssume.addStates(states);
			assumptions.addAssume(trueAssume);
		}
	}	
}
