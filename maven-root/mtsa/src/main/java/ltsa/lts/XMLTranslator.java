package ltsa.lts;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.LTS;


/** This class includes the logic to export a directed control problem to an
  * XML model as supported by Supremica. */
public class XMLTranslator extends AbstractTranslator {

	/** Maps each label to an unique id. */
	protected Map<String, Integer> ids = new HashMap<>();
	
	
	/** Performs the XML translation storing the representation in an
	  * internal buffer. This completes the template method
	  * <code>translate</code> of the <code>AbstractTranslator</code>,
	  * which flushes the internal buffer into an output stream. */
	@Override
	protected void doTranslate() {
		if (!nonblocking || reachability)
			throw new UnsupportedOperationException(
				"Translation for blocking/reachability problems to XML is not supported.");
		add("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
		open("<Automata name=" + quote(environment) + " major=\"0\" minor=\"1\">");
		genPlant();
		genSpecification();
		close("</Automata>");
	}
	
	
	/** Generates the XML plant specification. */
	protected void genPlant() {
		for (Entry<String,  LTS<Long,String>> lts : ltss.entrySet())
			genAutomaton(lts);
	}
	
	
	/** Generates an automaton per each LTS in the problem. */
	protected void genAutomaton(Entry<String, LTS<Long,String>> lts) {
		open("<Automaton name=" + quote(machine(lts)) + " type=\"Plant\">");
		genEvents(lts.getValue().getActions());
		genStates(lts);
		genTransitions(lts);
		close("</Automaton>");
	}
	
	
	/** Generates a list of events from a set of labels. */
	protected void genEvents(Set<String> labels) {
		open("<Events>");
		for (String label : labels) {
			if (validAction(label)) {
				boolean isUncontrollable = uncontrollable.contains(label); 
				add("<Event id=" + id(label) + " label=" + action(label) + // labels should be optional
					(isUncontrollable ? " controllable=\"false\"" : "") + "/>");
			}
		}
		close("</Events>");
	}
	
	
	/** Generates the list of states of a given LTS. */
	protected void genStates(Entry<String, LTS<Long,String>> lts) {
		open("<States>");
		for (Long state : lts.getValue().getStates()) {
			boolean isInitial = state.equals(lts.getValue().getInitialState());
			add("<State id=" + state(state) +
				" name=" + quote(machine(lts) + "[" + state + "]") + // names should be optional
				" accepting=" + (state.equals(-1l) ? "\"false\"" : "\"true\"") +
				(isInitial ? " initial=\"true\"" : "") +
				"/>");
		}
		close("</States>");
	}
	
	
	/** Generates the list of transitions of a given LTS. */
	protected void genTransitions(Entry<String, LTS<Long,String>> lts) {
		open("<Transitions>");
		for (Entry<Long, BinaryRelation<String, Long>> transition : lts.getValue().getTransitions().entrySet()) {
			Long source = transition.getKey();
			for (Pair<String, Long> pair : transition.getValue()) {
				String label = pair.getFirst();
				Long dest = pair.getSecond();
				add("<Transition source=" + state(source) + " dest=" + state(dest) + " event=" + id(label) + "/>");
			}
		}
		close("</Transitions>");
	}
	
	
	/** Generates the XML goal specification. */
	protected void genSpecification() {
		open("<Automaton name=" + quote(goal) + " type=\"Specification\">");
		
		genEvents(alphabet);
		
		open("<States>");
		add("<State id=\"0\"  name=\"init\" initial=\"true\"/>");
		add("<State id=\"1\"  name=\"goal\" accepting=\"true\"/>");
		close("</States>");
		
		open("<Transitions>");
		for (String label : alphabet) {
			if (marking.contains(label)) {
				add("<Transition source=\"0\" dest=\"1\" event=" + id(label) + "/>");
				add("<Transition source=\"1\" dest=\"1\" event=" + id(label) + "/>");
			} else {
				add("<Transition source=\"0\" dest=\"0\" event=" + id(label) + "/>");
				add("<Transition source=\"1\" dest=\"0\" event=" + id(label) + "/>");
			}
		}
		close("</Transitions>");
		
		close("</Automaton>");
	}
	
	
	/** Returns the textual representation of an state. */
	protected String state(Long l) {
		return quote(String.valueOf(l));
	}
	
	
	/** Returns the textual representation of an action. */
	protected String action(String label) {
		return quote(label);
	}
	
	
	/** Returns the textual representation of an LTS. */
	protected String machine(Entry<String, LTS<Long, String>> lts) {
		return lts.getKey();
	}
	
	
	/** Returns an unique id for each label. */
	protected String id(String label) {
		Integer result = ids.get(label);
		if (result == null)
			ids.put(label, result = ids.size());
		return quote(String.valueOf(result));
	}

	
	/** Surrounds a string with quotes to generate a string for the XML translation. */
	protected String quote(String s) {
		return "\"" + s + "\"";
	}

	
}
