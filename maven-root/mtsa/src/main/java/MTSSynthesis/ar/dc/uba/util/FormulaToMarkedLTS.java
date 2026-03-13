package MTSSynthesis.ar.dc.uba.util;

import java.util.*;

import MTSTools.ac.ic.doc.commons.collections.InitMap;
import MTSTools.ac.ic.doc.commons.collections.InitMap.Factory;
import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.impl.MarkedLTSImpl;
import MTSSynthesis.ar.dc.uba.model.condition.AndFormula;
import MTSSynthesis.ar.dc.uba.model.condition.Fluent;
import MTSSynthesis.ar.dc.uba.model.condition.FluentPropositionalVariable;
import MTSSynthesis.ar.dc.uba.model.condition.Formula;
import MTSSynthesis.ar.dc.uba.model.condition.NotFormula;
import MTSSynthesis.ar.dc.uba.model.condition.OrFormula;
import MTSSynthesis.ar.dc.uba.model.language.Symbol;


/** This class allows translating boolean formulas over fluents into an LTS
 *  with marked states. */
public class FormulaToMarkedLTS {
	
	/** Enumeration of supported connectives. */
	private enum BinaryOperation { AND, OR }
	
	
	/** Returns the LTS representing the formula A => G, where A is a
	 *  conjunction of assumptions and G is the conjunction of guarantees. */
	public MarkedLTSImpl<Long, String> translate(List<Formula> assumptions, List<Formula> guarantees) {
		Formula formula = conjunct(guarantees);
		if (!assumptions.isEmpty()) {
			Formula assumption = conjunct(assumptions);
			formula = new OrFormula(new NotFormula(assumption), formula);
		}
		return translate(formula);
	}

	public MarkedLTSImpl<Long, String> translate(List<Formula> assumptions, Formula guarantee) {
		List<Formula> guarantees = new ArrayList<>();
		guarantees.add(guarantee);
		return translate(assumptions, guarantees);
	}
	
	/** Returns the formula resulting from the conjunction of smaller formulas. */
	private Formula conjunct(List<Formula> formulas) {
		Formula result = formulas.get(0);
		for (int i = 1; i < formulas.size(); i++)
			result = new AndFormula(result, formulas.get(i));
		return result;
	}
	
	
	/** Returns the LTL representing a given formula. */
	public MarkedLTSImpl<Long, String> translate(Formula formula) {
		MarkedLTSImpl<Long, String> result;

		if (formula instanceof FluentPropositionalVariable) {
			result = fluent(((FluentPropositionalVariable) formula).getFluent());
		} else if (formula instanceof NotFormula) {
			result = not(((NotFormula) formula).getFormula());
		} else if (formula instanceof AndFormula) {
			AndFormula andFormula = (AndFormula) formula;
			result = binary(BinaryOperation.AND, andFormula.getLeftFormula(), andFormula.getRightFormula());
		} else if (formula instanceof OrFormula) {
			OrFormula orFormula = (OrFormula) formula;
			result = binary(BinaryOperation.OR, orFormula.getLeftFormula(), orFormula.getRightFormula());
		} else if (formula == Formula.FALSE_FORMULA) {
			result = new MarkedLTSImpl<>(0L);
		} else if (formula == Formula.TRUE_FORMULA) {
			result = new MarkedLTSImpl<>(0L);
			result.mark(0L);
		} else {
			throw new RuntimeException("Invalid formula " + formula);
		}
		result.removeAction("tau");

		return result;
	}

	
	/** Returns the LTS representing a single fluent. */
	private MarkedLTSImpl<Long, String> fluent(Fluent fluent) {
		boolean initial = fluent.isInitialValue();
		MarkedLTSImpl<Long, String> result = new MarkedLTSImpl<Long, String>(initial ? 1L : 0L);
		result.addState(0L);
		result.addState(1L);
		result.getMarkedStates().add(1L);
		for (Symbol s : fluent.getInitiatingActions()) {
			result.addAction(s.toString());
			result.addTransition(0L, s.toString(), 1L);
			result.addTransition(1L, s.toString(), 1L);
		}
		for (Symbol s : fluent.getTerminatingActions()) {
			result.addAction(s.toString());
			result.addTransition(1L, s.toString(), 0L);
			result.addTransition(0L, s.toString(), 0L);
		}
		return result;
	}

	
	/** Returns the LTS representing the negation of a formula. */
	private MarkedLTSImpl<Long, String> not(Formula formula) {
		MarkedLTSImpl<Long, String> result = translate(formula);
		Set<Long> states = new HashSet<>(result.getStates());
		Set<Long> marked = result.getMarkedStates();
		states.removeAll(marked);
		marked.clear();
		marked.addAll(states);
		return result;
	}

	
	/** Returns the LTS representing the application of a binary operation between two formulas. */
	private MarkedLTSImpl<Long, String> binary(BinaryOperation op, Formula leftFormula, Formula rightFormula) {
		MarkedLTSImpl<Long, String> left = translate(leftFormula);
		MarkedLTSImpl<Long, String> right = translate(rightFormula);

		//if either formula has the "*" transition. add copies of that transition for all new actions
		populateAsterisk(left, right.getActions());
		populateAsterisk(right, left.getActions());

		Pair<Long, Long> initial = Pair.create(left.getInitialState(), right.getInitialState());
		Map<Pair<Long, Long>, Long> states = new InitMap<>(new Factory<Long>() {
			long next = 0L;
			public Long newInstance() {
				return next++;
			}
		});
		MarkedLTSImpl<Long, String> result = new MarkedLTSImpl<Long, String>(states.get(initial));
		result.addActions(left.getActions());
		result.addActions(right.getActions());
		if(isResultMarked(op, left.isMarked(initial.getFirst()), right.isMarked(initial.getSecond()))) {
			result.mark(states.get(initial));
		}

		List<Pair<Long, Long>> pending = new ArrayList<>();
		pending.add(initial);
		while (!pending.isEmpty()) {
			Pair<Long, Long> current = pending.remove(pending.size() - 1);
			for(String label : result.getActions()){
				Set<Long> leftTargets = left.getTransitions(current.getFirst()).getImage(label);
				if(leftTargets.isEmpty()) leftTargets.add(current.getFirst());
				Set<Long> rightTargets = right.getTransitions(current.getSecond()).getImage(label);
				if(rightTargets.isEmpty()) rightTargets.add(current.getSecond());
				for(Long leftT : leftTargets){
					for(Long rightT : rightTargets){
						Pair<Long, Long> child = Pair.create(leftT, rightT);
						Long source = states.get(current);
						Long target = states.get(child);
						if (result.addState(target)) {
							pending.add(child);
							if (isResultMarked(op, left.isMarked(leftT), right.isMarked(rightT))){
								result.mark(target);
							}
						}
						result.addTransition(source, label, target);
					}
				}
			}
		}

		return result;
	}

	public void populateAsterisk(MarkedLTSImpl<Long, String> host, Set<String> newActions){
		if (host.getActions().contains("*")){
			Set<String> unknownActions = new HashSet<>(newActions);
			unknownActions.removeAll(host.getActions());
			host.addActions(newActions);
			for(Map.Entry<Long, BinaryRelation<String, Long>> Asterisk : host.getTransitions().entrySet()){
				Set<Long> targets = Asterisk.getValue().getImage("*");
				if (!targets.isEmpty()){
					for (Long newTarget : targets){
						for (String newAction : unknownActions){
							host.addTransition(Asterisk.getKey(), newAction, newTarget);
						}
					}
				}
			}
		}
	}

	private boolean isResultMarked(BinaryOperation op, boolean markedLeft, boolean markedRight){
		switch (op) {
			case AND:
				return markedLeft && markedRight;
			case OR:
				return markedLeft || markedRight;
			default:
				System.err.println("That LTL property (" + op + ") needs to be implemented here");
		}
		System.err.println("The switch statement for LTL composition is not working properly");
		return false;
	}

}
