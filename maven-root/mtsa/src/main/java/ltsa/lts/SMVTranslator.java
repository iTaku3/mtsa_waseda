package ltsa.lts;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.LTS;


/** This class includes the logic to export a directed control problem to an
 * SMV model as supported by MBP.
 */
public class SMVTranslator extends AbstractTranslator {

	/** Name of the controllable signal. */
	private final String CONTROLLABLE = "c";
	
	/** Name of the uncontrollable signal. */
	private final String UNCONTROLLABLE = "u";

	/** Name of the assumption violation signal. */
	private final String VIOLATION = "violation";
	
	/** Name of the special event indicating no choice. */
	private final String LAMBDA = "lambda";
	
	
	/** Performs the SMV translation storing the representation in an
	 * internal buffer. This completes the template method
	 * <code>translate</code> of the <code>AbstractTranslator</code>,
	 * which flushes the internal buffer into an output stream. */
	@Override
	protected void doTranslate() {
		filterAlphabet();
		
		add("MODULE main");
		add("DOMAINNAME " + name);
		add("PROBLEMNAME " + goal);
		
		genIVar();
		genVar();
		genInit();
		genTrans();
		genGoal();		
	}
	
	
	/** Generates the SMV IVAR definitions, containing the actions in the problem. */
	protected void genIVar() {
		open("IVAR");
		genConrolableVar();
		close("");
	}
	
	
	/** Generates the c signal (used to choose the event to execute). */
	protected void genConrolableVar() {
		open(CONTROLLABLE + ": {");
		add(LAMBDA + ",");
		for (String label : alphabet)
			add(action(label) + ",");
		removeLast(",");
		close("};");
	}
	
	
	/** Generates the SMV VAR definition, containing the problem fluents. */
	protected void genVar() {
		open("VAR");
		genAtVars();
		genUncontrollableVar();
		genViolationVar();
		close("");
	}
	
	
	/** Generates the 'At' fluents definition. */
	protected void genAtVars() {
		for (Entry<String, LTS<Long,String>> lts : ltss.entrySet()) {
			Set<Long> states = lts.getValue().getStates();
			String start = states.contains(-1l) ? "-1" : "0";
			String end = String.valueOf(states.size()-1);
			add(at(lts) + ":" + start + ".." + end + ";");
		}
	}
	
	
	/** Generates the u signal (used to choose the uncontrollable event to execute). */
	protected void genUncontrollableVar() {
		open(UNCONTROLLABLE + ": {");
		add(LAMBDA + ",");
		for (String label : uncontrollable)
			add(action(label) + ",");
		removeLast(",");
		close("};");
	}
	
	
	/** Generates the violation signal (used to track assumptions violations). */
	protected void genViolationVar() {
		add(VIOLATION + ":0..1;");
	}
	
	
	/** Generates the SMV INIT definition, containing the initialization of fluents. */
	protected void genInit() {
		open("INIT (");
		for (Entry<String, LTS<Long,String>> lts : ltss.entrySet())
			add(set(at(lts),0) + "&");
		removeLast("&");
		close(")");
	}
	
	
	/** Generates the SMV TRANS definition, containing the transition system emerging from the translation. */
	protected void genTrans() {
		open("TRANS");
		add("(" + set(VIOLATION,1) + " -> " + next(VIOLATION,1) + ") &");
		add("(" + CONTROLLABLE + "=" + LAMBDA + " -> " + set(VIOLATION,1) + ")");
		close("");
		for (Entry<String, LTS<Long, String>> lts : ltss.entrySet()) {
			open("TRANS");
			for (String label : alphabet) {
				if (lts.getValue().getActions().contains(label))
					genReady(label, lts);
				genAction(label, lts);
			}
			removeLast("&");
			close("");
		}
	}
	
	
	/** Generates the SMV formula constraining events to those ready. */
	protected void genReady(String label, Entry<String, LTS<Long, String>> lts) {
		String ready = getReady(label, lts);
		if (uncontrollable.contains(label))
			add("((" + UNCONTROLLABLE + "=" + action(label) + " & !" + ready + ") -> (" + next(VIOLATION,1) + ")) &");
		add("((" + CONTROLLABLE + "=" + action(label) + ") -> " + ready + ") &");
	}
	
	
	/** Returns the SMV formula indicating whether the label is ready in the LTS. */
	protected String getReady(String label, Entry<String, LTS<Long, String>> lts) {
		String ready = "";
		for (Long s : lts.getValue().getStates()) {
			for (Pair<String, Long> transition : lts.getValue().getTransitions(s)) {
				if (transition.getFirst().equals(label))
					ready += at(lts, s) + " | ";
			}
		}
		ready = ready.isEmpty() ? "FALSE" : "(" + ready.substring(0, ready.length()-3) + ")";
		return ready;
	}
	
	
	/** Generates the SMV formula representing the action step. */
	protected void genAction(String label, Entry<String, LTS<Long, String>> lts) {
		String unc = "";
		if (uncontrollable.contains(label))
			unc = "(" + UNCONTROLLABLE + "=" + action(label) + ") | ";
		open("((" + unc + "(" + CONTROLLABLE + "=" + action(label) + " & " + UNCONTROLLABLE + "=" + LAMBDA + ")) -> (");
		genStep(label, lts);
		close(")) &");
	}
	
	
	/** Generates the representation of a step with the given label and LTS. */
	protected void genStep(String label, Entry<String, LTS<Long, String>> lts) {
		if (lts.getValue().getActions().contains(label)) {
			boolean effect = false;
			for (Long s : lts.getValue().getStates()) {
				for (Long t : lts.getValue().getTransitions(s).getImage(label)) {
					add("(" + at(lts,s) + " -> " + next(at(lts),t) + ") &");
					effect = true;
				}
			}
			if (effect)
				removeLast("&");
			else
				add(next(at(lts),at(lts)));
		} else {
			add(next(at(lts),at(lts)));
		}
	}
	
	
	/** Generates the SMV goal definition. */
	protected void genGoal() {
		int goalType = getGoalType();
		switch (goalType) {
			case 0: // blocking + liveness
				open("FULL_OBS_CTL_GOAL");
				open("AG(AF(");
				break;
			case 1: // non-blocking + liveness
				open("FULL_OBS_CTL_GOAL");
				open("AG(EF(");
				break;
			case 2: // blocking + reachability
				open("FULL_OBS_STRONG_GOAL"); // I could express these in CTL, but MBP may behave better if I use the specific goal specification
				break;
			case 3: // non-blocking + reachability
				open("FULL_OBS_STRONG_CYCLIC_GOAL");
				break;
		}
		open(VIOLATION + " | (");
		for (Entry<String, LTS<Long, String>> lts : ltss.entrySet()) {
			Set<Long> marked = new HashSet<>();
			for (String mark : marking) {
				if (lts.getValue().getActions().contains(mark)) {
					for (Long s : lts.getValue().getStates()) {
						for (Long t : lts.getValue().getTransitions(s).getImage(mark))
							marked.add(t);
					}
				}
			}
			if (!marked.isEmpty()) {
				buffer.append("(");
				for (Long m : marked)
					buffer.append(at(lts,m) + " | ");
				removeLast(" | ");
				add(") &");
			} else {
//				add("!(" + at(lts,-1l) + ") &");
			}
		}
		removeLast("&");
		close(")");
		if (goalType < 2)
			close("))");
		close("");
	}
	
	
	/** Returns the SMV formula for the 'at' fluent for a given LTS. */
	protected String at(Entry<String, LTS<Long, String>> lts) {
		return "\"" + machine(lts) + "\"";
	}
	
	
	/** Returns the SMV formula that checks if an LTS is at a given state. */
	protected String at(Entry<String, LTS<Long, String>> lts, Long s) {
		return set(at(lts), s);
	}
	
	
	/** Returns the SMV formula that checks if an atom has a given value. */
	protected String set(String atom, long value) {
		return atom + "=" + value;
	}
	
	
	/** Auxiliary function that returns the name of a machine in SMV. */
	protected String machine(Entry<String, LTS<Long, String>> lts) {
		return lts.getKey().replaceAll("[(]", "_").replaceAll("[)]", "");
	}
	
	
	/** Returns the SMV formula that sets the value of a fluent for the next stage. */
	protected String next(String atom, String value) {
		return "next(" + atom + ")=" + value;
	}
	
	
	/** Returns the SMV formula that sets the value of a fluent for the next stage. */
	protected String next(String atom, long value) {
		return next(atom, String.valueOf(value));
	}
	
	
	/** Auxiliary function that returns the name of an action in SMV. */	
	protected String action(String label) {
		return "\"" + label.replaceAll("[.]", "_") + "\"";
	}
	
}
