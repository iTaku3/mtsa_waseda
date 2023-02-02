package ltsa.jung;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;

import edu.uci.ics.jung.algorithms.layout.AggregateLayout;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.layout.ObservableCachingLayout;
import edu.uci.ics.jung.visualization.picking.PickedState;

/**
 * Canvas for JUNG graph manipulation
 * @author Cédric Delforge
 */
@SuppressWarnings("serial")
public class LTSJUNGCanvas extends JPanel {
    public static enum EnumLayout {
    	KamadaKawai("Force-directed KK"), 
    	FruchtermanReingold("Force-directed FR"), 
    	ISOM("Space-filling ISOM"), 
    	Circle("Circle"), 
    	TreeLikeLTS("Top-down tree"), 
    	RadialLTS("Radial tree"),
    	Aggregate("Aggregate");
    	
    	private String name;
    	
    	private EnumLayout(String name) {
    		this.name = name;
    	}

    	@Override
    	public String toString() {
    		return name;
    	};
    }
	public static enum EnumMode {Edit, Activate};
	public static enum LayoutOptions {KK_length_factor, KK_distance, KK_max_iterations,
										FR_attraction, FR_repulsion, FR_max_iterations,
										Tree_distX, Tree_distY, Radial_distX, Radial_distY};
    
    private LTSViewer view;
    private GraphZoomScrollPane scrollView; //the scrollview holding the view
    private ArrayList<LTSGraph> graphs;
    private Map<LTSGraph,EnumLayout> enumLayouts;
    private Map<StateVertex,Point2D> savedPositions;
    private Set<TransitionEdge> selectedTrans;

	public void setSelectedStates(Set<StateVertex> selectedStates) {
		this.selectedStates = selectedStates;
	}

	private Set<StateVertex> selectedStates;
    
    private boolean singleMode = true;
    private boolean colorSCC = false;
    
    public static float edgeCurve = 20.f;
    public static double KK_length_factor = 0.9;
    public static double KK_distance = 0.5;
    public static int KK_max_iterations = 500;
    public static double FR_attraction = 0.75;
    public static double FR_repulsion = 0.75;
    public static int FR_max_iterations = 500;
    public static int Tree_distX = 100;
    public static int Tree_distY = 100;
    public static int Radial_distX = 100;
    public static int Radial_distY = 100;
    
    public LTSJUNGCanvas() {
    	super();
		cleanUp();
		addComponentListener(new ComponentAdapter() {
			public final void componentResized(ComponentEvent e) {
				if (view != null) {
					view.setPreferredSize(getSize());
					view.setLocation(0, 0);
					remove();
					display();
					//view.scaleToLayout(new CrossoverScalingControl());
				}
	        }			
		});
    }
    
	private void cleanUp() {
		view = null;
		scrollView = null;
		savedPositions = null;
		graphs = new ArrayList<LTSGraph>();
		enumLayouts = new HashMap<LTSGraph, EnumLayout>();
		selectedTrans = new HashSet<TransitionEdge>();
		selectedStates = new HashSet<StateVertex>();
	}
	
//-----------------------------------------------------------------------------
// Parameter setters
//-----------------------------------------------------------------------------
	public void setMode(boolean mode) {
		if (mode != singleMode)
			clear();
		singleMode = mode;	
	}
	public boolean shouldColorSCC() {
		return colorSCC;
	}
	public void colorSCC(boolean color) {
		colorSCC = color;
		if (view != null) {
			view.refresh();
		}
	}
	public float getCurve() {
		return edgeCurve;
	}
	public void setCurve(float curve) {
		edgeCurve = curve;
		if (view != null) {
			view.refresh();
		}
	}
	public void next() {
		if (view != null) {
			view.selectNext();
		}
	}
	public void reachable() {
		if (view != null) {
			view.selectReachable();
		}
	}
	public void previous() {
		if (view != null) {
			view.selectPrevious();
		}
	}
	public void reaching() {
		if (view != null) {
			view.selectReaching();
		}
	}
	public void zoomIn() {
		if (view != null) {
			final ScalingControl scaler = new CrossoverScalingControl();
			scaler.scale(view, 1.1f, view.getCenter());
		}
	}
	public void zoomOut() {
		if (view != null) {
			final ScalingControl scaler = new CrossoverScalingControl();
			scaler.scale(view, 1/1.1f, view.getCenter());
		}
	}
	public void setInteraction(EnumMode m) {
		if (view != null) {
			view.setInteraction(m);
		}
	}
	public Set<TransitionEdge> getSelectedTransitions() {
		return Collections.unmodifiableSet(selectedTrans);
	}
	public Set<StateVertex> getSelectedVertices() {
		return Collections.unmodifiableSet(selectedStates);
	}
//-----------------------------------------------------------------------------
// Interactions
//-----------------------------------------------------------------------------
	/*
	 * Draws graph on the canvas under the given layout
	 */
	public void draw(final LTSGraph graph, EnumLayout layout)
	{
		Layout<StateVertex,TransitionEdge> layoutToDisplay;
		
		if (singleMode || graphs.size() == 0)
		{
			graphs.clear();
			enumLayouts.clear();
			
			graphs.add(new LTSGraph());
			enumLayouts.put(graphs.get(0), EnumLayout.Aggregate);
			
			if (!singleMode) graphs.get(0).mergeWith(graph);
			
			graphs.add(graph);
			enumLayouts.put(graph, layout);
			
			layoutToDisplay = getLayout(graph,EnumLayout.Aggregate,this.getSize());
		}
		else
		{
			graphs.get(0).mergeWith(graph);
			graphs.add(graph);
			enumLayouts.put(graph, layout);
			
			layoutToDisplay = getLayout(graphs.get(0),EnumLayout.Aggregate,this.getSize());
		}
		
		remove();
		renew(layoutToDisplay);
		display();
	}
	
	private LTSGraph aggregate() {
		return graphs.get(singleMode ? 1 : 0);
	}
	
	/*
	 * Adds a layout for a subgraph defined as the set of picked states
	 */
	public void addLayout(final PickedState<StateVertex> picked, EnumLayout layout) {
		LTSGraph graph = new LTSGraph(picked,aggregate());
		CenteredArea ca = new CenteredArea(picked);
		Point2D center = ca.getCenter();
		Dimension dimension = ca.getDimension();
		
		Layout<StateVertex,TransitionEdge> graphLayout = getLayout(graph, layout, dimension);
		AggregateLayout<StateVertex,TransitionEdge> oldAggregate;
		if ((view.getGraphLayout() instanceof ObservableCachingLayout
				&& ((ObservableCachingLayout<StateVertex,TransitionEdge>)view.getGraphLayout()).getDelegate() instanceof AggregateLayout)
				|| (view.getGraphLayout() instanceof AggregateLayout)) {
			if (view.getGraphLayout() instanceof AggregateLayout)
				oldAggregate = ((AggregateLayout<StateVertex,TransitionEdge>)view.getGraphLayout());
			else
				oldAggregate = ((AggregateLayout<StateVertex,TransitionEdge>)((ObservableCachingLayout<StateVertex,TransitionEdge>)view.getGraphLayout()).getDelegate());
			
			AggregateLayout<StateVertex,TransitionEdge> newAggregate = (AggregateLayout<StateVertex,TransitionEdge>) getLayout((LTSGraph) oldAggregate.getGraph(), EnumLayout.Aggregate, oldAggregate.getSize());
			newAggregate.setInitializer(oldAggregate);
			newAggregate.put(graphLayout, center);
			
			
			view.setGraphLayout(newAggregate);
			view.repaint();
		}
	}
	
	public void cluster(final PickedState<StateVertex> picked) {
		CenteredArea ca = new CenteredArea(picked);
		Point2D center = ca.getCenter();

		AggregateLayout<StateVertex,TransitionEdge> oldAggregate;
		
		if ((view.getGraphLayout() instanceof ObservableCachingLayout
				&& ((ObservableCachingLayout<StateVertex,TransitionEdge>)view.getGraphLayout()).getDelegate() instanceof AggregateLayout)
				|| (view.getGraphLayout() instanceof AggregateLayout)) {
			if (view.getGraphLayout() instanceof AggregateLayout)
				oldAggregate = ((AggregateLayout<StateVertex,TransitionEdge>)view.getGraphLayout());
			else
				oldAggregate = ((AggregateLayout<StateVertex,TransitionEdge>)((ObservableCachingLayout<StateVertex,TransitionEdge>)view.getGraphLayout()).getDelegate());
			
			LTSGraph inGraph = (LTSGraph) oldAggregate.getGraph();
			LTSGraph clusterGraph = new LTSGraph(picked,aggregate());

            inGraph.separateFrom(clusterGraph);
            
            StateVertex cluster = makeVertex(clusterGraph);
            
            inGraph.addVertex(cluster);
			oldAggregate.setGraph(inGraph);
			oldAggregate.setLocation(cluster, center);
			view.getPickedEdgeState().clear();
			view.repaint();
		}
	}
	
	/*
	 * Refreshes all the graphs visualized with the given layout
	 */
	public void refresh(EnumLayout layout) {
		Layout<StateVertex,TransitionEdge> layoutToDisplay;
		
		if (graphs.size() > 0) {
			for (LTSGraph g : enumLayouts.keySet()) {
				if (enumLayouts.get(g) != EnumLayout.Aggregate) {
					enumLayouts.put(g, layout);
				}
			}

			layoutToDisplay = getLayout(aggregate(),EnumLayout.Aggregate,this.getSize());
			
			remove();
			renew(layoutToDisplay);
			display();	
		}
	}
	
	/*
	 * Stops the rendering
	 */
	public void stop() {
		if (view != null) {
			view.getModel().getRelaxer().stop();
		}
	}
	
	/*
	 * Clears a graph from the canvas
	 */
	public void clear(final LTSGraph graph) {
		if (singleMode || graphs.size() <= 2) {
			clear();
		} else {
			graphs.remove(graph);
			enumLayouts.remove(graph);
			aggregate().separateFrom(graph);

			remove();
			renew(getLayout(aggregate(),EnumLayout.Aggregate,this.getSize()));
			display();
		}
	}
	
	/*
	 * Clears the canvas
	 */
	public void clear() {
		remove();
		cleanUp();
		display();
	}
	
    public void savePositions() {
    	if (graphs != null && graphs.size() > 0 && view != null && view.getGraphLayout() != null) {
    		savedPositions = new HashMap<StateVertex,Point2D>();
    		
    		for (StateVertex v: aggregate().getVertices()) {
    			savedPositions.put(v, view.getGraphLayout().transform(v));
    		}
    	}
    }
    
    public void loadPositions() {
    	if (view != null && savedPositions != null) {
    		for (StateVertex v: savedPositions.keySet()) {
    			view.getGraphLayout().setLocation(v, savedPositions.get(v));
    		}
    	}
    }
    
    public void select(Set<TransitionEdge> trans, Set<StateVertex> states) {
    	selectedTrans = trans;
    	selectedStates = states;
    	if (view != null) {
    		view.refresh();
    	}
    }
//-----------------------------------------------------------------------------
// Utils
//-----------------------------------------------------------------------------	
	public Dimension getSize() {
		return new Dimension(super.getSize().width-20,super.getSize().height-20);
	}

	private void display() {
		if (view != null) {
			scrollView = new GraphZoomScrollPane(view);
	        add(scrollView);
		}
		setVisible(true);
	}
	
	private void renew(Layout<StateVertex,TransitionEdge> l) {			
		//how many squares will be needed to display all the graphs, x horizontally and y vertically
		int x = (int) Math.ceil(Math.sqrt(graphs.size()-1));
		int y = x == 0 ? 0 : (int) Math.ceil((double)(graphs.size()-1)/x);
		
		//allocated width and height of each square in the total canvas size
		int width = x == 0 ? 0 : this.getSize().width/x;
		int height = y == 0 ? 0 : this.getSize().height/y;
		
		//size of each square
		Dimension dimension = new Dimension(width,height);
		
		int x_i= 0, y_i = 0;

		for (int i = 1; i < graphs.size(); ++i) {
			final Point2D center = new Point2D.Double();
			//centered on the width and height of each square
			center.setLocation(width*x_i+width/2, height*y_i+height/2);
			
			//make a new sublayout of the size of a square and centered on one, add it to the aggregate layout
			((AggregateLayout<StateVertex,TransitionEdge>) l).put(getLayout(graphs.get(i),enumLayouts.get(graphs.get(i)),dimension), center);
			
			x_i += 1;
			if (x_i == x) {
				x_i = 0;
				y_i += 1;
			}
		}
		
		view = new LTSViewer(l,this.getSize(),this);
	}
	
	private void remove() {
		if (view != null) {
			this.remove(scrollView);
		}
		setVisible(false);
	}
	
	private Layout<StateVertex,TransitionEdge> getLayout(final LTSGraph graph, final EnumLayout layout, Dimension dms) {
	    	switch (layout) {
	    	case KamadaKawai: {
	    		 final KKLayout<StateVertex,TransitionEdge> out = new KKLayout<StateVertex,TransitionEdge>(graph);
	    		 out.setSize(dms);
	    		 out.setLengthFactor(KK_length_factor);
	    		 out.setDisconnectedDistanceMultiplier(KK_distance);
	    		 out.setMaxIterations(KK_max_iterations);
	    		 return out;
	    	}
	    	case FruchtermanReingold: {
	    		final FRLayout<StateVertex,TransitionEdge> out = new FRLayout<StateVertex,TransitionEdge>(graph);
	    		 out.setSize(dms);
	    		 out.setAttractionMultiplier(FR_attraction);
	    		 out.setRepulsionMultiplier(FR_repulsion);
	    		 out.setMaxIterations(FR_max_iterations);
	    		 return out;
	    	}
	    	case Circle: {
	    		 final CircleLayout<StateVertex,TransitionEdge> out = new CircleLayout<StateVertex,TransitionEdge>(graph);
	    		 out.setSize(dms);
	    		 return out;
	    	}
	    	case ISOM: {
	    		 final ISOMLayout<StateVertex,TransitionEdge> out = new ISOMLayout<StateVertex,TransitionEdge>(graph);
	    		 out.setSize(dms);
	    		 return out;
	    	}
	    	case TreeLikeLTS: {
//	    		final TreeLikeLTSLayout out = new TreeLikeLTSLayout(graph,Tree_distX,Tree_distY);
	    		final TreeLikeLTSLayout out = new TreeLikeLTSLayout(graph);
	    		out.setSize(dms);
    			return out;
	    	}
	    	case RadialLTS: {
//	    		final RadialLTSLayout out = new RadialLTSLayout(graph,Radial_distX,Radial_distY);
	    		final RadialLTSLayout out = new RadialLTSLayout(graph);
	    		out.setSize(dms);
    			return out;
	    	}
	    	case Aggregate: {
	    		 final StaticLayout<StateVertex,TransitionEdge> out = new StaticLayout<StateVertex,TransitionEdge>(graph);
	    		 out.setSize(dms);
	    		return new AggregateLayout<StateVertex,TransitionEdge>(out);
	    	}
	    	default: {
	    		 final KKLayout<StateVertex,TransitionEdge> out = new KKLayout<StateVertex,TransitionEdge>(graph);
	    		 out.setSize(dms);
	    		 return out;
	    	}
	    	}
    }
    
	/*
	 * (non-Javadoc)
	 * @see javax.swing.JComponent#print(java.awt.Graphics)
	 */
    public void print(Graphics g) {
    	//view.setBackground(new Color(255,255,255,0));
    	if (view != null)
    		view.print(g);  	
    }
    
    /*
     * Returns the buffered image of the current viewer
     */
    public BufferedImage getImage() { 
    	if (view != null)
    		return view.getImage();
    	else
    		return null;
    }
    
    /*
     * Turns a clustered Graph into a vertex, incomplete
     */
	private StateVertex makeVertex(LTSGraph g) {
		HashSet<String> graphNames = new HashSet<String>();
		HashSet<Integer> states = new HashSet<Integer>();
		
		for (StateVertex j : g.getVertices()) {
			graphNames.add(j.getGraphName());
			states.add(j.getStateName());
		}
		
		String name = "";
		String state = "";
		for (String s : graphNames) {
			name += s + " ";
		}
		for (Integer i : states) {
			state += String.valueOf(i) + " ";
		}
		return new StateVertex(Integer.parseInt(state),name.trim());
	}
    
	/*
	 * Creates the smallest area such that it contains all the vertices in ps
	 */
    private class CenteredArea {
    	Dimension dimension;
    	Point2D center;
    	
    	@SuppressWarnings("unused")
		public CenteredArea(Dimension d, Point2D c) {
    		dimension = d;
    		center = c;
    	}
    	public CenteredArea(PickedState<StateVertex> ps) {
    		center = new Point2D.Double();
    		double x = 0;
    		double y = 0;
    		double min_x = -1;
    		double min_y = -1;
    		double max_x = -1;
    		double max_y = -1;
    		for(StateVertex vertex : ps.getPicked()) {
    			Point2D p = view.getGraphLayout().transform(vertex);
    			x += p.getX();
    			y += p.getY();
    			if (min_x  == -1 || min_x > p.getX())
    				min_x = p.getX();
    			if (min_y  == -1 || min_y > p.getY())
    				min_y = p.getY();
    			if (max_x  == -1 || max_x < p.getX())
    				max_x = p.getX();
    			if (max_y  == -1 || max_y < p.getY())
    				max_y = p.getY();	
    		}
    		x /= ps.getPicked().size();
    		y /= ps.getPicked().size();
    		center.setLocation(x,y);
    		dimension = new Dimension((int)(max_x - min_x),(int)(max_y - min_y));
    	}
    	public Dimension getDimension() {
    		return dimension;
    	}
    	public Point2D getCenter() {
    		return center;
    	}
    }
}
