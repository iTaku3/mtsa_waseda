/*
 * Copyright (c) 2005, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 *
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 *
 * Created on Jul 9, 2005
 */

package ltsa.jung;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.map.LazyMap;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;

/**
 * A tree layout algorithm for graphs
 * 
 * @author Karlheinz Toni
 * @author Tom Nelson - converted to jung2
 * @author Cédric Delforge - Graph handling
 * @author Charles Pecheur - fixes, support resizing
 */

public class TreeLikeGraphLayout<V,E> implements Layout<V,E> {

	/**
	 * Margins for layout (from edge to center of nodes)
	 */
	protected static final int MARGIN = 30;
	
	protected Dimension size = new Dimension(600,600);
	protected Graph<V,E> graph;
	protected Map<V,Integer> treeWidth = new HashMap<V,Integer>();
	protected V root;
	protected transient Map<V,Integer> depths = new HashMap<V,Integer>();
	protected transient int maxDepth = 0; 

    protected Map<V, Point2D> locations = 
    	LazyMap.decorate(new HashMap<V, Point2D>(),
    			new Transformer<V,Point2D>() {
					public Point2D transform(V arg0) {
						return new Point2D.Double();
					}});
    
    
    /**
     * The horizontal vertex spacing.  Defaults to {@code DEFAULT_XDIST}.
     */
    protected int distX = 0;
    
    /**
     * The vertical vertex spacing.  Defaults to {@code DEFAULT_YDIST}.
     */
    protected int distY = 0;
    

    /**
     * Creates an instance for the specified graph.
     */
    public TreeLikeGraphLayout(Graph<V,E> g) {
        if (g == null)
            throw new IllegalArgumentException("Graph must be non-null");

    	this.graph = g;

    	setRoot();
    	computeDepth();
    	computeTreeWidth();
    	buildTree();
    }
    
    /**
     * Computes the depths of every node by a BFS on the graph.  Stores 
     * the depth in {depths} and the maximal depth in {maxDepth}.
     */
    protected void computeDepth() {
    	if (root == null || graph == null)
    		return;
    	LinkedList<V> queue = new LinkedList<V>();
    	queue.add(root);
    	depths.put(root,0);
    	
    	while (!queue.isEmpty()) {
    		V v = queue.removeFirst();  // FIFO for BFS order
    		
    		for (V successor: graph.getSuccessors(v)) {
    			if (!depths.containsKey(successor)) {
        			int depth = depths.get(v)+1;
    				depths.put(successor, depth);
    				if (depth > maxDepth) maxDepth = depth;
    				queue.add(successor);
    			}
    		}
    	}
    }
    

    private transient Set<V> alreadyDone = new HashSet<V>();  // used in buildTree()
    
    /**
     * Set locations of all nodes. 
     */
	protected void buildTree() {
		assert root != null;
		assert graph != null;
		assert depths.size() > 0;
		assert treeWidth.size() > 0;
		int width = treeWidth.get(root); 
		distX = (width == 0 ? 0 : (size.width - 2 * MARGIN) / width);
		distY = (maxDepth == 0 ? 0 : (size.height - 3 * MARGIN) / maxDepth);
		alreadyDone.clear();
		if (width == 0) {
			// zero-width tree, draw in middle
			buildTree(root, size.width / 2, 2 * MARGIN);
		} else {
			buildTree(root, MARGIN, 2 * MARGIN);
		}
    }

    /**
     * Set locations of nodes for subtree from {v} within rectangle such that
     * top-left corner is ({x}, {y}).  Ignore nodes in {alreadyDone}.
     */
	protected void buildTree(V v, int x, int y) {
        assert !alreadyDone.contains(v);
        int depth = depths.get(v);
        {
        	alreadyDone.add(v);
            int currentwidth = treeWidth.get(v);
        	Point p = new Point(x + currentwidth * distX / 2, y);
            locations.get(v).setLocation(p);
            
            int lastX = x;

            List<V> successors = new ArrayList<V>(graph.getSuccessors(v));
            Collections.shuffle(successors);  // for diversity
            for (V element : successors) {
            	if (depths.get(element) == depth+1 && 
            			!alreadyDone.contains(element)) {
            		// this is a successor in the tree
            		buildTree(element, lastX, y + distY);
            		lastX = lastX + (treeWidth.get(element) + 1) * distX;
            	}
            }
        }
    }

    
    private int computeTreeWidth() { 	
    	return computeTreeWidth(root, new HashSet<V>());
    }
    
    /**
     * Calculates the total width (nbr nodes - 1) of the subtree rooted at node {v}.
     * Stores it in {treeWidth}. {depths} must have been previously computed.
     * @param v the root of the subtree
     * @param frontier already computed nodes
     * @return the breadth computed
     */
    private int computeTreeWidth(V v, Set<V> frontier) { 	
        int size = -1;
        int depth = depths.get(v);

        for (V element : graph.getSuccessors(v)) {
        	if (depths.get(element) == depth+1 && 
        			!frontier.contains(element)) {
        		// a successor in the tree
        		frontier.add(element);
        		size += computeTreeWidth(element, frontier) + 1;
        	}
        }
        if (size < 0) size = 0;  // leaf node
        treeWidth.put(v, size);
        return size;
    }
    
    protected void setRoot() {
    	if (graph.getVertices().size() > 0) {
    		this.root = graph.getVertices().iterator().next();
    		return;
    	}
    	this.root = null; //empty graph
    }
    
    public void setSize(Dimension size) {
    	this.size = size;
    	buildTree();
    }


	public Graph<V,E> getGraph() {
		return graph;
	}

	public Dimension getSize() {
		return size;
	}

	public void initialize() {

	}

	public boolean isLocked(V v) {
		return false;
	}

	public void lock(V v, boolean state) {
	}

	public void reset() {
	}
	
	public void setGraph(Graph<V,E> graph) {
		this.graph = graph;
		buildTree();
	}

	public void setInitializer(Transformer<V, Point2D> initializer) {
	}
	
    /**
     * Returns the center of this layout's area.
     */
	public Point2D getCenter() {
		return new Point2D.Double(size.getWidth()/2,size.getHeight()/2);
	}

	public void setLocation(V v, Point2D location) {
		locations.get(v).setLocation(location);
	}
	
	public Point2D transform(V v) {
		return locations.get(v);
	}
}
