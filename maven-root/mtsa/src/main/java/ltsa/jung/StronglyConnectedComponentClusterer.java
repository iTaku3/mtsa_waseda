package ltsa.jung;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.graph.DirectedGraph;

/**
 * Tarjan's SCC algorithm
 * Returns a list of sets of SCC
 * @author Cédric Delforge
 */
public class StronglyConnectedComponentClusterer<V,E> implements Transformer<DirectedGraph<V,E>, List<Set<V>>> 
{
	DirectedGraph<V,E> graph;
	
    List<Set<V>> clusterSets;
    Stack<V> stack;
    Map<V,Integer> indexes;
    Map<V,Integer> lowlinks;
    
    int index;

    public List<Set<V>> transform(DirectedGraph<V,E> graph) {
    	this.graph = graph;

        clusterSets = new ArrayList<Set<V>>();
        stack = new Stack<V>();
        indexes = new HashMap<V,Integer>();
        lowlinks = new HashMap<V,Integer>();
        
        index = 0;

        for (V v: graph.getVertices()) {
        	if (!indexes.containsKey(v)) {
        		SCC(v);
        	}
        }
        
        return clusterSets;
    }
    
    private void SCC(V v) {
    	indexes.put(v, index);
    	lowlinks.put(v, index);
    	index += 1;
    	
    	stack.push(v);
    	
    	for (V vs : graph.getSuccessors(v)) {
    		if (!indexes.containsKey(vs)) {
    			SCC(vs);
    			if (lowlinks.get(vs) < lowlinks.get(v)) {
    				lowlinks.put(v, lowlinks.get(vs));
    			}
    		} else if (stack.contains(vs)) {
    			if (indexes.get(vs) < lowlinks.get(v)) {
    				lowlinks.put(v, indexes.get(vs));
    			}
    		}
    	}
    	
    	if (lowlinks.get(v) == indexes.get(v)) {
    		Set<V> scc = new HashSet<V>();
    		
    		V sv = stack.pop();
    		while (sv != v) {
    			scc.add(sv);
    			sv = stack.pop();
    		}
    		scc.add(sv);
    		
    		clusterSets.add(scc);
    	}
    }
}
