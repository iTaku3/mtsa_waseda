package ltsa.lts;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;
import MTSTools.ac.ic.doc.mtstools.model.impl.LTSAdapter;

import ltsa.ac.ic.doc.mtstools.util.fsp.AutomataToMTSConverter;
import ltsa.control.ControllerGoalDefinition;


/** This abstract class contains the basic logic and data structures shared by
  * translators from LTSs to different output formats. */
public abstract class AbstractTranslator {

	
	/** LTSs that conform the composed environment. */
	protected Map<String, LTS<Long, String>> ltss = new HashMap<>();
	
	/** Control problem alphabet. */
	protected Set<String> alphabet = new HashSet<>();
	
	/** Uncontrollable actions. */
	protected Set<String> uncontrollable = new HashSet<>();
	
	/** Events used for marking states. */
	protected Set<String> marking = new HashSet<>();

	/** Disturbance events. */
	protected Set<String> disturbances = new HashSet<>();

	/** Indicates whether we want a non-blocking solution or not. */
	protected boolean nonblocking = false;
	
	/** Indicates whether we want a reachability goal or a liveness goal. */
	protected boolean reachability = false;
	
	/** Name of the solution to the control problem. */
	protected String name;
	
	/** Name of the goal in the control problem. */
	protected String goal;
	
	/** Name of the environment in the control problem. . */
	protected String environment;
	
	/** Internal StringBuilder with the output. */
	protected StringBuilder buffer = new StringBuilder();
	
	/** Internal indentation string for pretty printing. */
	protected String indent = "";

	/** The base composite state to be translated. */
	protected CompositeState base_composite = null;


	/** Template method that populates internal data structures shared by
	  * Translators and then invokes the actual translation through the
	  * <code>doTranslate</code> method.
	  * Finally printing the internal buffer with the result of the translation
	  * to the output stream. */
	public void translate(CompositeState composite, PrintStream output) {
		if (!checkGoal())
			throw new UnsupportedOperationException(
				"The combination of translation and goal type selected is not supported.");
		base_composite = composite;
		name = composite.getName();
		CompositionExpression ce = LTSCompiler.getComposite(name);
		List<CompactState> machines = new ArrayList<>(ce.compiledProcesses.values());
		ControllerGoalDefinition goalDef = ControllerGoalDefinition.getDefinition(ce.goal);
		Vector<String> controllableActions = goalDef.getControllableActionSet();
		addAll(marking, goalDef.getMarkingDefinitions());
		addAll(disturbances, goalDef.getDisturbanceActions());
		nonblocking = goalDef.isNonBlocking();
		reachability = goalDef.isReachability();
		for (CompactState machine : machines) {
			ltss.put(machine.getName(), new LTSAdapter<Long, String>(
				AutomataToMTSConverter.getInstance().convert(machine), TransitionType.REQUIRED));
			for (String label : machine.getAlphabet())
				if (validAction(label))
					alphabet.add(label);
		}
		if (controllableActions != null) { // when no controllable actions are declared assume everything controllable
			Set<String> controllable = new HashSet<>(controllableActions);
			for (String action : alphabet)
				if (!controllable.contains(action))
					uncontrollable.add(action);
		}
		goal = goalDef.getNameString();
		environment = composite.env.getName();
		int i = environment.indexOf(".");
		if (i > 0)
			environment = environment.substring(0, i);
		
		doTranslate();
		
		output.print(buffer.toString());
	}
	
	
	/** Abstract translation method. Override this to take advantage of the
	  * template method <code>translate</code>. */
	protected abstract void doTranslate();

	
	/** Returns whether the selected goal is supported by this translation. */
	protected boolean checkGoal() {
		return true;
	}
	
	/** Filters alphabet sets retaining only reachable events (and returns them). */
	protected Set<String> filterAlphabet() {
		Set<String> events = new HashSet<>();
		for (Entry<String, LTS<Long,String>> lts : ltss.entrySet()) {
			for (Long source : lts.getValue().getStates()) {
				for (Pair<String,Long> transition : lts.getValue().getTransitions(source))
					events.add(transition.getFirst());
			}
		}
		alphabet.retainAll(events);
		uncontrollable.retainAll(events);
		marking.retainAll(events);
		disturbances.retainAll(events);
		return events;
	}
	
	/** Adds all elements from the data collection into the set (or nothing if data is null). */
	protected void addAll(Set<String> set, Collection<String> data) {
		if (data != null)
			set.addAll(data);
	}
	
	
	/** Increases the indentation depth for the next lines. */
	protected void incIndent() {
		indent += "  ";
	}
	
	
	/** Decreases the indentation depth for the next lines. */
	protected void decIndent() {
		indent = indent.substring(2);
	}
	
	
	/** Indicates whether an action is valid for translation purposes. */
	protected boolean validAction(String label) {
		return !label.equals("tau") && !label.endsWith("?");
	}
	
	
	/** Flushes a new line into the PDDL. */
	protected void newLine() {
		buffer.append("\n");
		buffer.append(indent);
	}
	
	
	/** Opens an indented block. */
	protected void open(String block) {
		incIndent();
		buffer.append(block);
		newLine();
	}
	
	
	/** Closes an indented block. */
	protected void close(String p) {
		decIndent();
		buffer.delete(buffer.length()-2, buffer.length());
		if (!p.isEmpty())
			add(p);
	}
	
	
	/** Adds a line to an indented block. */
	protected void add(String line) {
		buffer.append(line);
		newLine();
	}
	
	
	/** Removes the last occurrence of a given string in the internal buffer. */
	protected void removeLast(String c) {
		int i = buffer.lastIndexOf(c);
		buffer.delete(i, i+c.length());
	}
	
	
	/** Returns the goal type codified:
	 *    0) blocking + liveness
	 *    1) non-blocking + liveness
	 *    2) blocking + reachability
	 *    3) non-blocking + reachability */
	protected int getGoalType() {
		return (nonblocking ? 1 : 0) | (reachability ? 2 : 0);
	}

}
