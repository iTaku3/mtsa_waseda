package ltsa.jung;

import java.util.ArrayList;

import ltsa.lts.Alphabet;

/**
 * An action in a LTS, for use as an edge in LTSGraph
 * @author Cédric Delforge
 */
public class TransitionEdge {
	private ArrayList<String> transitionLabels;
	private String transitionLabel;
	private int originState;
	private int destinationState;
	
	public TransitionEdge(String name, int origin, int dest) {
		transitionLabels = new ArrayList<String>();
		transitionLabels.add(name);
		transitionLabel = null;
		originState = origin;
		destinationState = dest;
	}
	
	public void addLabel(String name) {
		transitionLabels.add(name);
		transitionLabel = null;
	}
	
	public String getFirstLabel() {
		return transitionLabels.get(0);
	}
	
	public int getOriginState() {
		return originState;
	}
	
	public int getDestinationState() {
		return destinationState;
	}
	
	/*
	 * This transition has an action for this label
	 */
	public boolean hasLabel(String label) {
		for (String s: transitionLabels) {
			if (s.equals(label)) {
				return true;
			}
		}
		return false;
	}
	
	/*
	 * The label of the transition is factorized according to the process in Alphabet
	 */
	public String toString() {
		if (transitionLabel == null) {
			Alphabet a = new Alphabet((String[])transitionLabels.toArray(new String[transitionLabels.size()]));
			transitionLabel = a.toString();
		}
		return transitionLabel;
	}
	
	/*public String toString() {
		if (transitionLabel.equals("")) {
			String label = "<html>";
			for (String l: transitionLabels) {
				label += l + "<br>";
			}
			label = label.substring(0, label.length()-4);
			label += "</html>";
//			//System.out.println(label);
			transitionLabel = label;
		}
		return transitionLabel;
	}*/
}
