package ltsa.lts;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import MTSTools.ac.ic.doc.mtstools.model.LTS;


/** This class contains the logic to export a directed control problem to an LTL
  * formula with a GR(1) goal in the structuredslugs format as supported by slugs. */
public class SlugsTranslator extends CTLPYTranslator {
	

	/** Filters alphabet sets retaining only reachable events (and returns them). */
	@Override
	protected Set<String> filterAlphabet() {
		Set<String> events = super.filterAlphabet();
		alphabet.remove(lambda());
		return events;
	}
	
	
	/** Returns whether the selected goal is supported by this translation. */
	@Override
	protected boolean checkGoal() {
		return getGoalType() == 0;
	}
	
	
	/** Generates signals to encode states and actions. */
	@Override
	protected void genSignals() {
		add("[INPUT]");
		genStateSignals();
		genUncontrollableSignals();
		newLine();
		add("[OUTPUT]");
		genControllableSignals();
		newLine();
	}
	
	
	/** Adds a signal definition. */
	@Override
	protected void addSignal(String name, String type, List<String> groups) {
		add(name);
		groups.add(name);
	}
	
	
	/** Generates the LTL formula. */
	@Override
	protected void genFormula() {
		add("[ENV_INIT]");
		for (Entry<String, LTS<Long,String>> lts : ltss.entrySet())
			genInit(lts);
		newLine();
		
		add("[ENV_TRANS]");
		genExtraUncontrollableConstraints();
		for (Entry<String, LTS<Long,String>> lts : ltss.entrySet()) {
			genUncontrollableConstraints(lts);
			genTransitions(lts);
		}
		newLine();
		
		add("[SYS_TRANS]");
		genExtraControllableConstraints();
		for (Entry<String, LTS<Long,String>> lts : ltss.entrySet())
			genControllableConstraints(lts);
		newLine();
		
		add("[SYS_LIVENESS]");
		genGoal();
	}
	
	
	/** Generates the initial state configuration for a given LTS. */
	@Override
	protected void genInit(Entry<String, LTS<Long, String>> lts) {
		super.genInit(lts);
		removeLast(and());
	}
	
	
	/** Generates the transition relation signal updates. */
	@Override
	protected void genTransitions(Entry<String, LTS<Long,String>> lts) {
		for (String label : alphabet) {
			String uchoice = "";
			if (uncontrollable.contains(label))
				uchoice = uncontrollable(label) + or();
			add("((" + uchoice + "(" + controllable(label) + and() + uncontrollable(lambda()) + "))" + then() + step(lts,label) + ")");
		}
	}
	
	
	/** Generates constraints to prevent the selection of invalid uncontrollable events. */
	protected void genExtraUncontrollableConstraints() {
		for (int u = uncontrollable.size(); u < (1 << ucontrolBits); ++u)
			add("(" + not() + codeBits(ucontrol(), ucontrolBits, u) + ")");
	}
	
	
	/** Generates constraints to prevent the selection of invalid controllable events. */
	protected void genExtraControllableConstraints() {
		for (int c = alphabet.size(); c < (1 << controlBits); ++c)
			add("(" + not() + codeBits(control(), controlBits, c) + ")");
	}
	
	
	/** Generates the constraints to select only ready uncontrollable events. */
	protected void genUncontrollableConstraints(Entry<String, LTS<Long,String>> lts) {
		for (String label : uncontrollable) {
			if (lts.getValue().getActions().contains(label))
				add("(" + uncontrollable(label) + then() + ready(lts, label) + ")");
		}
	}
	
	
	/** Generates the constraints to select only ready uncontrollable events. */
	protected void genControllableConstraints(Entry<String, LTS<Long,String>> lts) {
		for (String label : alphabet) {
			if (lts.getValue().getActions().contains(label))
				add("(" + controllable(label) + then() + ready(lts, label) + ")");
		}
	}
	
	
	/** Generates the goal requirement. */
	@Override
	protected void genGoal() {
		buffer.append("(");
		for (Entry<String, LTS<Long,String>> lts : ltss.entrySet()) {
			Set<Long> marked = new HashSet<>();
			for (String label : marking) {
				if (lts.getValue().getActions().contains(label)) {
					for (Long s : lts.getValue().getStates()) {
						for (Long d : lts.getValue().getTransitions(s).getImage(label))
							marked.add(d);
					}
				}
			}
			String ltsGoal = "";
			if (!marked.isEmpty()) {
				for (Long m : marked)
					ltsGoal += state(lts, m) + or();
				ltsGoal = removeLast(ltsGoal, or());
			}
			if (!ltsGoal.isEmpty())
				buffer.append("(" + ltsGoal + ")" + and());
		}
		removeLast(and());
		add(")");
	}
	
	
	/** Returns the line-ending character. */
	@Override
	protected String end() {
		return "";
	}
	
	
	/** Returns slug's implication operator. */
	@Override
	protected String then() {
		return " -> ";
	}
	
	
	/** Returns slug's conjunction operator. */
	@Override
	protected String and() {
		return " && ";
	}
	
	/** Returns slug's disjunction operator. */
	@Override
	protected String or() {
		return " || ";
	}
	
	/** Returns an empty always operator (slug does not use this operator and
	  * hence we remove it from super's behavior). */
	@Override
	protected String always() {
		return "";
	}
	
	/** Returns the CTL negation operator. */
	@Override
	protected String not() {
		return "!";
	}

	
	/** Generates an empty header block. */
	@Override
	protected void genHeader() {}
	
	
	/** Generates an empty footer block. */
	@Override
	protected void genFooter() {}

	
}
