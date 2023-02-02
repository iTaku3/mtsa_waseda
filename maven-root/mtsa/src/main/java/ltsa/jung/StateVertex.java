package ltsa.jung;

/**
 * A state of a LTS, for use as a JUNG vertex in LTSGraph
 * @author Cédric Delforge
 */
public class StateVertex {
	private String graphName;
	private int stateName;
	
	public int getStateName() {
		return stateName;
	}

	public String getGraphName() {
		return graphName;
	}
	
	public StateVertex(int i, String graph) {
		stateName = i;
		graphName = graph;
	}
	
	public String toString() {
		return String.valueOf(stateName);
	}
}
