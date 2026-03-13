package MTSTools.ac.ic.doc.mtstools.model;

import edu.uci.ics.jung.graph.DirectedSparseGraph;

/**
 * Created by Victor Wjugow on 09/06/15.
 */
public class GraphWithInitialNode<V, E> extends DirectedSparseGraph<V, E> {
	private V initialState;

	public V getInitialState() {
		return initialState;
	}

	public void setInitialState(V initialState) {
		this.initialState = initialState;
	}
}