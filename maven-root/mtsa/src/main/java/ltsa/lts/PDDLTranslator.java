package ltsa.lts;

import java.util.Map.Entry;

import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.LTS;


/** This class includes the logic to export an directed control problem to a
  * PDDL model as supported by PRP. */
public class PDDLTranslator extends AbstractTranslator {

	/** Contains the problem representation in PDDL. */
	protected String problemPDDL;

	/** Indicates whether to exclude the problem representation from the output. */
	protected boolean excludeProblemPDDL = false;
	

	/** Constructor for a PDDLTranslator. */
	public PDDLTranslator() {
		this(false);
	}
	
	
	/** Constructor for a PDDLTranslator that allows to exclude the problem definition. */
	public PDDLTranslator(boolean excludeProblem) {
		excludeProblemPDDL = excludeProblem;
	}

	/** Performs the PDDL translation storing the representation in an
	  * internal buffer. This completes the template method
	  * <code>translate</code> of the <code>AbstractTranslator</code>,
	  * which flushes the internal buffer into an output stream. */
	@Override
	public void doTranslate() {
		genDomainDefinition();
		genProblemDefinition();
	}
	
	
	/** Generates the PDDL domain definition. */
	protected void genDomainDefinition() {
		open("(define");
		add("(domain " + name + ")");
		genRequirements();
		genTypes();
		genConstants();
		genPredicates();
		genReset();
		genSetReady();
		genSetEnabled();
		if (!uncontrollable.isEmpty()) { // if there are uncontrollable actions add the environment's non-deterministic choice
			genPick();
			genDefault();
		}
		if (!reachability) { // if we want liveness add the loop action
			genLoop();
		}
		genActions();
		close(")");
	}
	
	
	/** Generates the PDDL problem definition. */
	protected void genProblemDefinition() {
		newLine();
		int current = buffer.length();
		
		open("(define");
		
		add("(problem " + goal + ")");
		
		add("(:domain " + name + ")");
		
		open("(:init");
		for (Entry<String, LTS<Long, String>> lts : ltss.entrySet())
			add("(at " + state(lts.getValue().getInitialState()) + " " + machine(lts) + ")");
		close(")");
		
		open("(:" + goal());
		open("(and");
		if (!reachability) { // if we want liveness the goal is to close a loop over a completed task (i.e., hoop)
			add("(status event)");
			for (Entry<String, LTS<Long, String>> lts : ltss.entrySet())
				add("(hoop " + machine(lts) + ")");
		} else { // otherwise the goal is to complete a task
			add("(status complete)");
		}
		close(")");
		close(")");
		
		close(")");
		
		int end = buffer.length();
		problemPDDL = buffer.substring(current, end);
		if (excludeProblemPDDL)
			buffer.delete(current, end);
	}
	
	
	/** Returns the goal keyword. */
	protected String goal() {
		return "goal";
	}
	
	
	/** Generates the PDDL requirements. */
	protected void genRequirements() {
		newLine();
		open("(:requirements");
		add(":typing");
		add(":non-deterministic");
		add(":conditional-effects");
		close(")");
	}
	
	
	/** Generates the PDDL types required to model the control problem. */
	private void genTypes() {
		newLine();
		open("(:types");
		add("LTS");
		add("State");
		add("Label");
		add("Phase");
		close(")");
	}
	
	
	/** Generates the PDDL constants required to model the control problem. */
	private void genConstants() {
		newLine();
		open("(:constants");
		genLTSs();
		genStates();
		genLabels();
		genStatus();
		close(")");
	}
	
	
	/** Generates the LTS constants. */
	private void genLTSs() {
		for (Entry<String, LTS<Long, String>> lts : ltss.entrySet()) {
			buffer.append(machine(lts));
			buffer.append(" ");
		}
		add("- LTS");
	}
	
	
	/** Generates the States constants. */
	private void genStates() {
		int maxstates = 0;
		int i = 0;
		for (LTS<Long, String> lts : ltss.values()) {
			int numstates = lts.getStates().size();
			if (lts.getStates().contains((long)-1)) {
				i = -1;
				--numstates;
			}
			maxstates = Math.max(maxstates, numstates);
		}
		for (; i < maxstates; ++i) {
			buffer.append(state(i));
			buffer.append(" ");
		}
		add("- State");
	}
	
	
	/** Generates the Labels constants. */
	private void genLabels() {
		for (String label : alphabet) {
			buffer.append(action(label));
			buffer.append(" ");
		}
		add("- Label");
	}
	
	
	/** Generates the Status constants. */
	private void genStatus() {
		String liveness = "";
		if (!reachability)
			liveness = "looping event";
		add("setup idle busy complete uncontrollable " + liveness + " - Phase"); // error
	}
	
	
	/** Generates the PDDL predicates required to model the control problem. */
	private void genPredicates() {
		newLine();
		open("(:predicates");
		add("(at ?s - State ?m - LTS)");
		add("(ready ?a - Label ?m - LTS)");
		if (!reachability) { // for pursuing liveness we need additional fluents
			add("(marked ?s - State ?m - LTS)");
			add("(hoop ?m - LTS)");
		}
		add("(enabled ?a - Label)");
		add("(inprogress ?a - Label)");
		add("(status ?c - Phase)");
		close(")");
	}
	
	
	/** Generates the reset action, which clears the ready and enabled fluents. */
	private void genReset() {
		newLine();
		open("(:action reset");
		
		open(":precondition");
		open("(and");
		add("(not (status setup))");
		add("(not (status idle))");
		add("(not (status busy))");
		close(")");
		close("");
		
		open(":effect");
		open("(and");
		add("(status setup)");
		add("(not (status uncontrollable))");
		add("(not (status complete))");
		for (String label : alphabet)
			add("(not (enabled " + action(label) + "))");
		for (String label : uncontrollable)
			add("(not (inprogress " + action(label) + "))");
		for (Entry<String, LTS<Long, String>> lts : ltss.entrySet()) {
			for (String label : lts.getValue().getActions())
				if (validAction(label))
					add("(not (ready " + action(label) + " " + machine(lts) + "))");
		}
		if (!reachability) {
			for (Entry<String, LTS<Long, String>> lts : ltss.entrySet())
				add("(not (hoop " + machine(lts) + "))");
		}
		close(")");
		close("");
		
		close(")");
	}
	
	
	/** Generates the set action, which sets the ready fluents from the at fluents. */
	private void genSetReady() {
		newLine();
		open("(:action setReady");
		
		open(":precondition");
		open("(and");
		add("(status setup)");
		add("(not (status busy))");
		close(")");
		close("");
		
		open(":effect");
		open("(and");
		add("(status busy)");
		for (Entry<String, LTS<Long, String>> lts : ltss.entrySet()) {
			for (Long s : lts.getValue().getStates()) {
				if (!lts.getValue().getTransitions(s).isEmpty()) { // for each transition s -l-> t available from the source state s set the label l as ready
					BinaryRelation<String, Long> transitions = lts.getValue().getTransitions(s);
					open("(when (at " + state(s) + " " + machine(lts) + ")");
					if (transitions.size() != 1)
						open("(and");
					for (Pair<String, Long> t : transitions)
						add("(ready " + action(t.getFirst()) + " " + machine(lts) + ")");
					if (transitions.size() != 1)
						close(")");
					close(")");
				}
			}
		}
		if (!reachability) {
			for (Entry<String, LTS<Long, String>> lts : ltss.entrySet()) {
				for (Long s : lts.getValue().getStates()) {
					open("(when (and (at " + state(s) + " " + machine(lts) + ") (marked " + state(s) + " " + machine(lts) + "))");
					add("(hoop " + machine(lts) + ")");
					close(")");
				}
			}
		}
		close(")");
		close("");
		close(")");
	}
	
	
	/** Generates the synch action, which sets the enabled fluents from the ready fluents. */
	private void genSetEnabled() {
		newLine();
		open("(:action setEnabled");
		
		open(":precondition");
		open("(and");
		add("(status setup)");
		add("(status busy)");
		close(")");
		close("");
		
		open(":effect");
		open("(and");
		add("(not (status setup))");
		add("(not (status busy))");
		if (!reachability)
			add("(not (status event))");
		add("(status idle)");
		for (String label : alphabet) {
			open("(when");
			open("(and");
			for (Entry<String, LTS<Long, String>> lts : ltss.entrySet())
				if (lts.getValue().getActions().contains(label)) // every machine with the label in its alphabet must be ready in order to enable the action
					add("(ready " + action(label) + " " + machine(lts) + ")");
			close(")");
			/**/
			add("(enabled " + action(label) + ")"); // XXX I am changing (status uncontrollable) to indicate whether an uncontrollable event was actually picked non-deterministcally
//			if (uncontrollable.contains(label)) { // if an uncontrollable action is enabled deal with events
//				open("(and");
//				add("(enabled " + action(label) + ")");
//				add("(status uncontrollable)");
////				if (avoid.contains(label)) // enabling an uncontrollable avoid action is an error already
////					add("(status error)");
//				close(")");
//			} else { // controllable
//				add("(enabled " + action(label) + ")");
//			}
			/**/
			
			close(")");
		}
		close(")");
		close("");
		
		close(")");
	}
	
	
	/** Generates the choice action, which non-deterministically chooses an uncontrollable action. */
	private void genPick() {
		newLine();
		open("(:action pick");
		
		open(":precondition");
		open("(and");
		add("(status idle)");
		//add("(status uncontrollable)"); // I changed the meaning of (status uncontrollable)
		close(")");
		close("");
		
		open(":effect");
		open("(and");
		add("(not (status idle))");
		add("(status busy)");
		open("(oneof");
		for (String label : uncontrollable)
			add("(when (enabled " + action(label) + ") (and (inprogress " + action(label) + ") (status uncontrollable)))");
		add("(when (true) (true))"); // XXX check this dummy option works
		close(")");
		close(")");
		close("");
		
		close(")");
	}
	
	
	/** Generates the default action, which allows the planner to choose an
	 *  enabled uncontrollable action after a non-deterministic selection step
	 *  selected an invalid action. */
	private void genDefault() {
		newLine();
		open("(:action default");
		
		open(":precondition");
		open("(and");
		add("(not (status setup))");
		add("(status busy)");
		for (String label : uncontrollable)
			add("(not (inprogress " + action(label) + "))");
		close(")");
		close("");
		
		open(":effect");
		open("(and");
		for (String label : uncontrollable)
			add("(inprogress " + action(label) + ")");
		close(")");
		close("");
		
		close(")");
	}
	
	
	/** Generates the loop action that "marks" a state as the goal state to close a lasso-loop. */
	private void genLoop() {
		newLine();
		open("(:action loop");

		open(":precondition");
		open("(and");
		add("(status complete)");
		add("(status event)");
		add("(not (status looping))");
		close(")");
		close("");
		
		open(":effect");
		open("(and");
		add("(not (status event))");
		add("(status looping)");
		for (Entry<String, LTS<Long, String>> lts : ltss.entrySet()) {
			for (Long s : lts.getValue().getStates()) {
				add("(when (at " + state(s) + " " + machine(lts) + ") (" + "marked " + state(s) + " " + machine(lts) + "))"); 
			}
		}
		close(")");
		close("");
		
		close(")");
	}
	
	
	/** For each action in the alphabet generates a PDDL action that updates
	 *  the LTSs local states. */
	private void genActions() {
		for (String label : alphabet) {
			newLine();
			open("(:action " + getActionPrefix() + action(label));
			
			open(":precondition");
			open("(and");
			add("(status busy)");
			add("(enabled " + action(label) + ")");
			if (uncontrollable.contains(label))
				add("(inprogress " + action(label) + ")");
			else
				add("(not (status uncontrollable))");
			close(")");
			close("");
			
			open(":effect");
			open("(and");
			
			if (!reachability)
				add("(status event)");
			
			add("(not (status busy))");
			
			if (marking.contains(label))
				add("(status complete)");
			
			for (Entry<String, LTS<Long, String>> lts : ltss.entrySet()) {
				for (Long s : lts.getValue().getStates()) {
					for (Long t : lts.getValue().getTransitions(s).getImage(label)) {
						if (!s.equals(t)) {
							open("(when (at " + state(s) + " " + machine(lts) + ")");
							add("(and (not (at " + state(s) + " " + machine(lts) + ")) (at " + state(t) + " " + machine(lts) + "))");
							close(")");
						}
					}
				}
			}
			close(")");
			close("");
			
			close(")");
		}
	}
	
	
	/** Returns the planner action prefix. */
	protected String getActionPrefix() {
		return "do";
	}
	
	/** Returns the textual representation of an state. */
	protected String state(long i) {
		return "$" + i;
	}
	
	
	/** Returns the textual representation of an action. */
	protected String action(String label) {
		return "$" + label.replaceAll("[.]", "-");
	}
	
	
	/** Returns the textual representation of an LTS. */
	protected String machine(Entry<String, LTS<Long, String>> lts) {
		return "$" + lts.getKey().replaceAll("[(]", "-").replaceAll("[)]", "");
	}
	
	
	/** Returns the string representation of the problem definition. */
	public String getProblem() {
		return problemPDDL;
	}
	
}
