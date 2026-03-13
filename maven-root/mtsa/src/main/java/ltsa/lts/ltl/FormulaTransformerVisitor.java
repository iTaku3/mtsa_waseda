package ltsa.lts.ltl;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.lang.Validate;

import MTSSynthesis.ar.dc.uba.model.condition.AndFormula;
import MTSSynthesis.ar.dc.uba.model.condition.Fluent;
import MTSSynthesis.ar.dc.uba.model.condition.FluentImpl;
import MTSSynthesis.ar.dc.uba.model.condition.FluentPropositionalVariable;
import MTSSynthesis.ar.dc.uba.model.condition.Formula;
import MTSSynthesis.ar.dc.uba.model.condition.NotFormula;
import MTSSynthesis.ar.dc.uba.model.condition.OrFormula;
import MTSSynthesis.ar.dc.uba.model.language.SingleSymbol;
import MTSSynthesis.ar.dc.uba.model.language.Symbol;

/**
 * Visits a formula and stores the resulting 
 * transformed Formula and the Fluents occurring in 
 * the Formula
 * @author gsibay
 *
 */
public class FormulaTransformerVisitor implements FormulaVisitor {

	private MTSSynthesis.ar.dc.uba.model.condition.Formula transformedFormula;
	private Set<Fluent> involvedFluents = new HashSet<Fluent>();
	private Set<Fluent> actionFluentsForUpdate = new HashSet<Fluent>();

	public Set<Fluent> getInvolvedFluents() {
		return this.involvedFluents;
	}

	public Set<Fluent> getActionFluentsForUpdate() {
		return this.actionFluentsForUpdate;
	}


	public MTSSynthesis.ar.dc.uba.model.condition.Formula getTransformedFormula() {
		return this.transformedFormula;
	}
	
	/* (non-Javadoc)
	 * @see lts.ltl.FormulaVisitor#visit(lts.ltl.True)
	 */
	@Override
	public ltsa.lts.ltl.Formula visit(True t) {
		this.transformedFormula = MTSSynthesis.ar.dc.uba.model.condition.Formula.TRUE_FORMULA;
		return null;
	}

	/* (non-Javadoc)
	 * @see lts.ltl.FormulaVisitor#visit(lts.ltl.False)
	 */
	@Override
	public ltsa.lts.ltl.Formula visit(False f) {
		this.transformedFormula = MTSSynthesis.ar.dc.uba.model.condition.Formula.FALSE_FORMULA;
		return null;
	}
	
	/* (non-Javadoc)
	 * @see lts.ltl.FormulaVisitor#visit(lts.ltl.Proposition)
	 */
	@Override
	public ltsa.lts.ltl.Formula visit(Proposition p) {
		Fluent fluent = this.createFluent(p);
		this.involvedFluents.add(fluent);
		
		this.transformedFormula = new FluentPropositionalVariable(fluent);
		return null;
	}

	/* (non-Javadoc)
	 * @see lts.ltl.FormulaVisitor#visit(lts.ltl.Not)
	 */
	@Override
	public ltsa.lts.ltl.Formula visit(Not n) {
		// transform the n formula
		n.getNext().accept(this);
		// negate the formula and set it to the transformer
		this.transformedFormula = new NotFormula(this.getTransformedFormula());
		return null;
	}

	/* (non-Javadoc)
	 * @see lts.ltl.FormulaVisitor#visit(lts.ltl.And)
	 */
	@Override
	public ltsa.lts.ltl.Formula visit(And a) {
		a.getLeft().accept(this);
		Formula left = this.getTransformedFormula();
		
		a.getRight().accept(this);
		Formula right = this.getTransformedFormula();
		
		this.transformedFormula = new AndFormula(left, right);
		return null;
	}

	/* (non-Javadoc)
	 * @see lts.ltl.FormulaVisitor#visit(lts.ltl.Or)
	 */
	@Override
	public ltsa.lts.ltl.Formula visit(Or o) {
		o.getLeft().accept(this);
		Formula left = this.getTransformedFormula();
		
		o.getRight().accept(this);
		Formula right = this.getTransformedFormula();
		
		this.transformedFormula = new OrFormula(left, right);
		return null;
	}

	/* (non-Javadoc)
	 * @see lts.ltl.FormulaVisitor#visit(lts.ltl.Until)
	 */
	@Override
	public ltsa.lts.ltl.Formula visit(Until u) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see lts.ltl.FormulaVisitor#visit(lts.ltl.Release)
	 */
	@Override
	public ltsa.lts.ltl.Formula visit(Release r) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see lts.ltl.FormulaVisitor#visit(lts.ltl.Next)
	 */
	@Override
	public ltsa.lts.ltl.Formula visit(Next n) {
		throw new UnsupportedOperationException();		
	}

	private Fluent createFluent(Proposition proposition) {
		String name = proposition.toString();
		PredicateDefinition predicateDefinition = PredicateDefinition.get(name);

		// the predicate definition is an event. Create a fluent.
		if (predicateDefinition == null){
			Fluent resultantFluent = generateFluentFromEvent(name);
			this.actionFluentsForUpdate.add(resultantFluent);
			return resultantFluent;
		}

		PredicateDefinition.compile(predicateDefinition);
		
		// if initial is 1 then is true. If it's -1 it's false.
		boolean initialValue = (predicateDefinition.initial() == 1) ? true : false;
		
		Set<Symbol> initiatingActions = this.transformFluentActions(predicateDefinition.getInitiatingActions());
		Set<Symbol> terminatingActions = this.transformFluentActions(predicateDefinition.getTerminatingActions());
		
		return new FluentImpl(name, initiatingActions, terminatingActions, initialValue);
	}

	private Fluent generateFluentFromEvent(String name) {
		HashSet<Symbol> initiating = new HashSet<>();
		String fluentName = name + "_a";

		if (name.contains(new String("[")) || name.contains(new String("["))){
			String[] splited = name.split(new String("\\["));
			String value = splited[1].split(new String("\\]"))[0];
			initiating.add(new SingleSymbol(splited[0] + "." +value));
			fluentName = splited[0] + "." +value + "_a";
		} else {
			initiating.add(new SingleSymbol(name));
		}

		HashSet<Symbol> terminating = new HashSet<>();
		terminating.add(new SingleSymbol("*"));
		return new FluentImpl(fluentName, initiating, terminating, false);
	}

	private Set<Symbol> transformFluentActions(Vector fluentActions) {
		Set<Symbol> symbols = new HashSet<Symbol>();
		for (Object fluentAction : fluentActions) {
			// The fluentAction must have a toString method representing the action
			symbols.add(new SingleSymbol(fluentAction.toString()));
		}
		return symbols;
	}
	
}
