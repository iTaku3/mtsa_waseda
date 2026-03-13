package ltsa.lts;

import static java.lang.Math.ceil;
import static java.lang.Math.log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import MTSTools.ac.ic.doc.mtstools.model.LTS;


/** This class includes the logic to export a directed control problem to a
  * CTL formula in python format as supported by party-elli. */
public class CTLPYTranslator extends AbstractTranslator {

	/** Set of controllable events. */
	protected Set<String> controllable;
	
	/** Number of signals required to encode controllable options. */
	protected int controlBits;
	
	/** Number of signals required to encode uncontrollable options. */
	protected int ucontrolBits;
	
	/** Map of controllable event labels to numeric identifiers. */
	protected Map<String,Integer> controlId;
	
	/** Map of uncontrollable event labels to numeric identifiers. */
	protected Map<String,Integer> ucontrolId;
	
	/** Map of LTS name to number of bits used to encode its states. */
	protected Map<String, Integer> ltsBits;
	
	/** List of input signals. */
	protected List<String> inputs;
	
	/** List of output signals. */
	protected List<String> outputs;
	
	
	/** Internal data initialization. */
	protected void init() {
		controlId = new HashMap<>();
		ucontrolId = new HashMap<>();
		ltsBits = new HashMap<>();
		inputs = new ArrayList<>();
		outputs = new ArrayList<>();
	}
	
	
	/** Filters alphabet sets retaining only reachable events (and returns them). */
	@Override
	protected Set<String> filterAlphabet() {
		Set<String> events = super.filterAlphabet();
		alphabet.add(lambda());
		uncontrollable.add(lambda());
		controllable = new HashSet<>(alphabet);
		controllable.removeAll(uncontrollable);
		return events;
	}
	
	
	/** Performs the translation to LTL storing the representation in an
	 * internal buffer. This completes the template method <code>translate</code>
	 * of the <code>AbstractTranslator</code> which flushes the internal buffer
	 * into an output stream. */
	@Override
	protected void doTranslate() {
		init();
		filterAlphabet();
		genHeader();
		genSignals();
		genFormula();
		genFooter();
	}
	
	
	/** Returns whether the selected goal is supported by this translation. */
	@Override
	protected boolean checkGoal() {
		return getGoalType() < 3;
	}
	
	
	/** Generates the header for the PY file. */
	protected void genHeader() {
		add("from helpers.spec_helper import *");
		add("from interfaces.spec import Spec");
		newLine();
	}
	
	
	/** Generates signals to encode states and actions. */
	protected void genSignals() {
		genViolationSignals();
		newLine();
		genStateSignals();
		newLine();
		genUncontrollableSignals();
		newLine();
		genControllableSignals();
		newLine();
		add("inputs = " + inputs);
		add("outputs = " + outputs);
		newLine();
	}
	
	
	/** Generates signals to encode assumptions violations. */
	protected void genViolationSignals() {
		addSignal(violation(), input(), inputs);
	}
	
	
	/** Generates signals to enconde states. */
	protected void genStateSignals() {
		for (Entry<String, LTS<Long,String>> lts : ltss.entrySet()) {
			int bits = getBits(lts.getValue().getStates().size());
			ltsBits.put(lts.getKey(), bits);
			for (int i = 0; i < bits; ++i)
				addSignal(bit(machine(lts),i), output(), outputs);
		}
	}
	
	
	/** Generates signals to encode controllable events. */
	protected void genControllableSignals() {
		int size = alphabet.size();
		controlBits = getBits(size);
		int c = 0;
		for (String label : alphabet)
			controlId.put(label, c++);
		for (int i = 0; i < controlBits; ++i)
			addSignal(bit(control(),i), output(), outputs);
	}
	
	
	/** Generates signals to encode uncontrollable events. */
	protected void genUncontrollableSignals() {
		int size = uncontrollable.size();
		ucontrolBits = getBits(size);
		int u = 0;
		for (String label : uncontrollable)
			ucontrolId.put(label, u++);
		for (int i = 0; i < ucontrolBits; ++i)
			addSignal(bit(ucontrol(),i), input(), inputs);
	}
	
	
	/** Adds a signal definition. */
	protected void addSignal(String name, String type, List<String> groups) {
		String signal = name;
		String group = signal + "_g";
		add(group + ", " + signal + " = sig_prop('" + signal + "')");
		groups.add(group);
	}
	
	
	/** Generates the CTL formula. */
	protected void genFormula() {
		open("formula = " + end());
		genViolationConstraints();
		genExtraConstraints();
		for (Entry<String, LTS<Long,String>> lts : ltss.entrySet()) {
			genInit(lts);
			genConstraints(lts);
			genTransitions(lts);
		}
		genGoal();
		close("");
		newLine();
	}
	
	
	/** Generates assumption violation constraints (i.e., once a violation is
	  * detected it can never be turned off and the controller may not act). */
	protected void genViolationConstraints() {
		add(always() + "(" + violation() + then() + next(violation()) + ")" + and() + end());
		add(always() + "(" + controllable(lambda()) + then() + violation() + ")" + and() + end());
	}
	
	
	/** Generates constraints to prevent the selection of invalid events. */
	protected void genExtraConstraints() {
		for (int c = alphabet.size(); c < (1 << controlBits); ++c)
			add(always() + "(" + not() + codeBits(control(), controlBits, c) + ")" + and() + end());
		for (int u = uncontrollable.size(); u < (1 << ucontrolBits); ++u)
			add(always() + "(" + codeBits(ucontrol(), ucontrolBits, u) + then() + next(violation()) + ")" + and() + end());
	}
	
	
	/** Generates the initial state configuration for a given LTS. */
	protected void genInit(Entry<String, LTS<Long,String>> lts) {
		add(state(lts, lts.getValue().getInitialState()) + and() + end());
	}
	
	
	/** Generates the constraints to select only ready events. */
	protected void genConstraints(Entry<String, LTS<Long,String>> lts) {
		for (String label : alphabet) {
			if (lts.getValue().getActions().contains(label)) {
				String ready = ready(lts, label);
				if (uncontrollable.contains(label))
					add(always() + "((" + uncontrollable(label) + and() + not() + ready + ")" + then() + next(violation()) +")" + and() + end());
				add(always() + "(" + controllable(label) + then() + ready + ")" + and() + end());
			}
		}
	}
	
	
	/** Generates the transition relation signal updates. */
	protected void genTransitions(Entry<String, LTS<Long,String>> lts) {
		for (String label : alphabet) {
			if (!label.equals(lambda())) {
				String uchoice = "";
				if (uncontrollable.contains(label))
					uchoice = uncontrollable(label) + or();
				add(always() + "((" + uchoice + "(" + controllable(label) + and() + uncontrollable(lambda()) + "))" + then() + step(lts,label) + ")" + and() + end());
			}
		}
	}
	
	
	/** Generates the goal requirement. */
	protected void genGoal() {
		switch (getGoalType()) {
			case 0: // blocking + liveness
				open("AG(AF(" + end()); break;
			case 1: // non-blocking + liveness
				open("AG(EF(" + end()); break;
			case 2: // blocking + reachability
				open("AF((" + end()); break;
		}
		open(violation() + or() + "(" + end());
		genMarked();
		removeLast(and() + end());
		close(")");
		close("))");
	}
	
	
	/** Generates a formula characterizing marked states. */
	protected void genMarked() {
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
			if (marked.isEmpty()) {
//				TODO Should I pursue not getting to ERROR states?
//				if (lts.getValue().getStates().contains(-1l))
//					ltsGoal = not() + state(lts, -1l);
			} else {
				for (Long m : marked)
					ltsGoal += state(lts, m) + or();
				ltsGoal = removeLast(ltsGoal, or());
			}
			if (!ltsGoal.isEmpty())
				add("(" + ltsGoal + ")" + and() + end());
		}
	}
	
	
	/** Generates the PY footer block. */
	protected void genFooter() {
		newLine();
		add("spec = Spec(inputs, outputs, formula)");
	}
	
	
	/** Returns the number of bits required to encode the given number. */
	protected int getBits(int size) {
		if (size == 1) size = 2;
		return (int)ceil(log(size)/log(2));
	}
	
	
	/** Returns the encoding of the i-th bit of a given element. */
	protected String bit(String element, int i) {
		return element + "_" + i;
	}

	
	/** Returns the name of an LTS to use within the PY specification. */
	protected String machine(Entry<String, LTS<Long,String>> lts) {
		return lts.getKey().replaceAll("[(|,]", "_").replaceAll("[)]", "");
	}
	
	
	/** Returns the representation of a given controllable event. */
	protected String controllable(String label) {
		return codeBits(control(), controlBits, controlId.get(label));
	}
	
	
	/** Returns the representation of a given uncontrollable event. */
	protected String uncontrollable(String label) {
		return codeBits(ucontrol(), ucontrolBits, ucontrolId.get(label));
	}
	
	
	/** Returns the representation of a given state. */
	protected String state(Entry<String, LTS<Long,String>> lts, Long s) {
		return codeBits(machine(lts), ltsBits.get(lts.getKey()), s);
	}
	
	
	/** Returns the ready condition for a given event. */
	protected String ready(Entry<String, LTS<Long,String>> lts, String label) {
		String result = "";
		for (Long source : lts.getValue().getStates()) {
			if (!lts.getValue().getTransitions(source).getImage(label).isEmpty())
				result += state(lts, source) + or();
		}
		result = "(" + removeLast(result, or()) + ")";
		return result;
	}
	
	
	/** Returns the step transition for a given event. */
	protected String step(Entry<String, LTS<Long,String>> lts, String label) {
		String result = "";
		if (lts.getValue().getActions().contains(label)) {
			for (Long source : lts.getValue().getStates()) {
				for (Long destination : lts.getValue().getTransitions(source).getImage(label))
					result += "(" + state(lts, source) + then() + next(state(lts, destination)) + ")" + and();
			}
		} else {
			int bits = ltsBits.get(lts.getKey());
			for (int i = 0; i < bits; ++i) {
				String biti = bit(machine(lts),i);
				result += "(" + biti + then() + next(biti) + ")" + and() +
						  "(" + not() + biti + then() + next(not() + biti) + ")" + and();
			}
		}
		return result = "(" + removeLast(result, and()) + ")";
	}
	
	
	/** Encodes the value of an element in a representation based on bits. */
	protected String codeBits(String element, Integer bits, long value) {
		String result = "";
		for (int i = 0; i < bits; ++i)
			result += ((value&(1<<i))==0 ? not() : "") + bit(element,i) + and();
		result = "(" + removeLast(result, and()) + ")";
		return result;
	}
	
	
	/** Returns the prefix for controllable signals. */
	protected String control() {
		return "c";
	}
	
	
	/** Returns the prefix for uncontrollable signals. */
	protected String ucontrol() {
		return "u";
	}
	
	
	/** Returns the name of the special lambda event. */
	protected String lambda() {
		return "lambda";
	}

	
	/** Returns the string for the type of input signals. */
	protected String input() {
		return "i";
	}
	
	
	/** Returns the string for the type of output signals. */
	protected String output() {
		return "o";
	}
	
	
	/** Returns the name of the violation signal. */
	protected String violation() {
		return "v";
	}
	
	
	/** Returns the CTL always operator. */
	protected String always() {
		return "AG";
	}
	
	
	/** Returns the CTL eventually operator. */
	protected String eventually() {
		return "EF";
	}
	
	
	/** Returns the CTL implication operator. */
	protected String then() {
		return " >> ";
	}
	
	
	/** Returns the CTL conjunction operator. */
	protected String and() {
		return " & ";
	}
	
	
	/** Returns the CTL disjunction operator. */
	protected String or() {
		return " | ";
	}
	
	
	/** Returns the CTL negation operator. */
	protected String not() {
		return "~";
	}
	
	
	/** Returns the CTL next operator. */
	protected String next() {
		return "X";
	}
	
	
	/** Returns the CTL next operator applied to a given formula. */
	protected String next(String formula) {
		return next() + "(" + formula + ")";
	}
	
	
	/** Returns the line-ending character. */
	protected String end() {
		return "\\";
	}
	
	
	/** Auxiliary function to remove the last occurrence of a substring. */
	protected String removeLast(String s, String r) {
		int i = s.lastIndexOf(r);
		String result = i == -1 ? s :
			s.substring(0, i) + s.substring(i + r.length());
		return result;
	}
	

}
