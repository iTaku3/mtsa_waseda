package MTSAExperiments.ar.uba.dc.lafhis.experiments.exchange;

import org.json.simple.JSONAware;

import MTSAExperiments.ar.uba.dc.lafhis.experiments.jung.ExperimentJUNGGameFluent;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.SparseMultigraph;

public class JSONAwareMultiGraph<V,E> extends DirectedSparseMultigraph<V, E> implements JSONAware {

	@Override
	public String toJSONString() {

		String verticesString 	= null;
		String edgesString 		= null;
		for(V vertex: getVertices()){
			if(verticesString != null)
				verticesString += ",";
			else
				verticesString = "[";
			verticesString += (vertex instanceof JSONAware)?((JSONAware)vertex).toJSONString() : vertex.toString();
		}
		if(verticesString != null)
			verticesString += "]";
		else
			verticesString = "[]";
		for(E edge: getEdges()){
			if(edgesString != null)
				edgesString += ",";
			else
				edgesString = "[";
			edgesString += (edge instanceof JSONAware)?((JSONAware)edge).toJSONString() : edge.toString();
		}
		if(edgesString != null)
			edgesString += "]";
		else
			edgesString = "[]";		
		String jsonString		= "{\"Vertices\": "+verticesString+",\"Edges\":"+edgesString+"}";
		return jsonString;
	}

}
