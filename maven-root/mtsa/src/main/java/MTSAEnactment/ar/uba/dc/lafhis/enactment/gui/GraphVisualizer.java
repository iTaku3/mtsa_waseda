/**
 * 
 */
package MTSAEnactment.ar.uba.dc.lafhis.enactment.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JPanel;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.handler.mxRubberband;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxEdgeStyle;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.BaseController;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.TakeFirstController;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.ITransitionEventListener;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.TransitionEvent;

/**
 * LTS Graph Visualizer
 * 
 * @author Julio
 *
 */
public class GraphVisualizer<State, Action> extends JPanel implements
		ITransitionEventListener<Action> {
	
	private mxGraphComponent graphComponent;
	private MyGraph graph;
	private mxGraphModel model;
	
	private class MyGraph extends mxGraph
	{
		@Override
		public boolean isCellEditable(Object cell) {

			return false;
		}
		@Override
		public boolean isCellMovable(Object cell) {
			if(cell instanceof mxCell && ((mxCell)cell).isVertex()){
		           return true;
			}
			return false;
		}
		@Override
		public boolean isCellDisconnectable(Object obj1, Object obj2, boolean flag)
		{
			return false;
		}
		@Override
		public boolean isLabelMovable(Object obj)
		{
			return false;
		}
		@Override
		public boolean isCellConnectable(Object obj)
		{
			return false;
		}
	}
	
	private BaseController<State, Action> scheduler;

	private State initialState;
	private State currentState = null;
	private State previousState = null;
	private Iterator<Pair<Action,State>> stateIterator = null;
	private Action lastAction = null;
	
	private static String STATE_STYLE_INITIAL = "INITIAL_STATE";
	private static String STATE_STYLE_CURRENT = "CURRENT_STATE";
	private static String STATE_STYLE_NORMAL = "NORMAL_STATE";

	private int nextNodeX = 30;
	/* (non-Javadoc)
	 * @see ar.uba.dc.lafhis.enactment.ITransitionEventListener#handleTransitionEvent(ar.uba.dc.lafhis.enactment.TransitionEvent)
	 */
	@Override
	public void handleTransitionEvent(TransitionEvent<Action> transitionEvent)
			throws Exception {
		

		//Must be after LTS is updated
		//TODO: find a way to wait until LTS reflects the change
		
		this.setPreviousState(this.getCurrentState());
		this.setCurrentState(this.getScheduler().getCurrentState());

		this.setStateIterator(this.getScheduler().getLts().getTransitions(currentState).iterator());
		this.setLastAction(transitionEvent.getAction());
		
		
		this.drawGraph2();
		applyStateStyles();
		
		
		repaint();
	}
	
	
	public GraphVisualizer()
	{
		this.setLayout(new BorderLayout());
	}
	
	public void initialize(LTS<State, Action> lts, State initialState, BaseController<State, Action> scheduler)
	{
		this.scheduler = scheduler;
		this.initialState = lts.getInitialState();
		this.currentState = this.initialState;
		this.previousState = null;
				
		//this.drawGraph();
		this.drawGraph2();
		
		scheduler.addTransitionEventListener(this);
		 
	}
	
	public void zoomToFixViewPort()
	{
		 double newScale = 1;

         Dimension graphSize = graphComponent.getGraphControl().getSize();
         Dimension viewPortSize = graphComponent.getViewport().getSize();

         int gw = (int) graphSize.getWidth();
         int gh = (int) graphSize.getHeight();

         if (gw > 0 && gh > 0) {
             int w = (int) viewPortSize.getWidth();
             int h = (int) viewPortSize.getHeight();

             if (w != gw || h != gh)
             {
            	 newScale = Math.min((double) w / gw, (double) h / gh);
             } else {
            	 
             }
             
         }

         graphComponent.zoom(newScale);
	}
	
	public void center()
	{
		 Dimension graphSize = graphComponent.getGraphControl().getSize();
         Dimension viewPortSize = graphComponent.getViewport().getSize();

         int x = graphSize.width/2 - viewPortSize.width/2;
         int y = graphSize.height/2 - viewPortSize.height/2;
         int w = viewPortSize.width;
         int h = viewPortSize.height;

         graphComponent.getGraphControl().scrollRectToVisible( new Rectangle( x, y, w, h));
         graphComponent.getGraphControl().invalidate();
	}
	
	public void zoomIn()
	{
		graphComponent.zoomIn();
	}
	
	public void zoomOut()
	{
		graphComponent.zoomOut();
	}
	
	private String getStateStyle(State state)
	{
		if (this.initialState.equals(state))
		{
			return STATE_STYLE_INITIAL;
		} else if (this.currentState.equals(state))
		{
			return STATE_STYLE_CURRENT;
		}
		return STATE_STYLE_NORMAL;
	}
	
	private synchronized void applyStateStyles()
	{
		if (this.getGraphComponent()==null) return;
		if (this.getPreviousState().equals(this.getCurrentState())) return;
		
		this.getGraphComponent().getGraph().getModel().beginUpdate();
		try
		{
				
				Object prevCell = model.getCell(getStateCellId(this.getPreviousState()));
				if (prevCell != null)
				{					
					
					Object[] cells = new Object[1];
					cells[0] = prevCell;
					if (!this.getPreviousState().equals(this.getInitialState()))
						this.getGraphComponent().getGraph().setCellStyle(STATE_STYLE_NORMAL, cells);
					else
						this.getGraphComponent().getGraph().setCellStyle(STATE_STYLE_INITIAL, cells);

				}
			
			Object currentCell = model.getCell(getStateCellId(this.getCurrentState()));
			if (currentCell != null)
			{
				Object[] cells = new Object[1];
				cells[0] = currentCell;
				this.getGraphComponent().getGraph().setCellStyle(STATE_STYLE_CURRENT, cells);
			}
			
		} finally
		{
			this.getGraphComponent().getGraph().getModel().endUpdate();
		}
		//this.getGraphComponent().refresh();
		this.getGraphComponent().repaint();
		
	}
	
	private String getStateCellId(State state)
	{
		return "state_" + state.toString();
	}
	
	private void drawGraph2()
	{
		Object lastInserted = null;
		
		if(getScheduler().getLts() == null)
			return;
		//this.removeAll();
		//this.setGraphComponent(null);
		
		if (this.graph == null) this.graph = new MyGraph();

		if (this.model == null) {
			this.model = new mxGraphModel();
			graph.setModel(model);
		}
					
		Object parent = graph.getDefaultParent();
		//model.beginUpdate();
		this.graph.getModel().beginUpdate();
		
		
		try
		{
			mxStylesheet stylesheet = graph.getStylesheet();
			Hashtable<String, Object> style = new Hashtable<String, Object>();
			style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
			style.put(mxConstants.STYLE_OPACITY, 100);
			style.put(mxConstants.STYLE_FONTCOLOR, "#774444");
			stylesheet.putCellStyle("ROUNDED", style);
			
			Hashtable<String, Object> edgeStyle = new Hashtable<String, Object>();
			edgeStyle.put( mxConstants.STYLE_ROUNDED, true );
			edgeStyle.put( mxConstants.STYLE_ENDARROW, mxConstants.ARROW_CLASSIC );
			edgeStyle.put( mxConstants.STYLE_SHAPE, mxConstants.SHAPE_CONNECTOR );
			edgeStyle.put( mxConstants.STYLE_EDGE, mxEdgeStyle.EntityRelation );
			stylesheet.putCellStyle("MY_EDGE", edgeStyle);
			
			Hashtable<String, Object> stateStyle = new Hashtable<String, Object>();
			stateStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
			stateStyle.put(mxConstants.STYLE_OPACITY, 100);
			stateStyle.put(mxConstants.STYLE_FONTCOLOR, "#774444");
			stateStyle.put(mxConstants.STYLE_FILLCOLOR, "#DCDCDC");
			stylesheet.putCellStyle(STATE_STYLE_NORMAL, stateStyle);
			
			Hashtable<String, Object> currentStateStyle = new Hashtable<String, Object>();
			currentStateStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
			currentStateStyle.put(mxConstants.STYLE_OPACITY, 100);
			currentStateStyle.put(mxConstants.STYLE_FONTCOLOR, "#774444");
			currentStateStyle.put(mxConstants.STYLE_FILLCOLOR, "#1E90FF");
			stylesheet.putCellStyle(STATE_STYLE_CURRENT, currentStateStyle);
			
			Hashtable<String, Object> initialStateStyle = new Hashtable<String, Object>();
			initialStateStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
			initialStateStyle.put(mxConstants.STYLE_OPACITY, 50);
			initialStateStyle.put(mxConstants.STYLE_FONTCOLOR, "#774444");
			initialStateStyle.put(mxConstants.STYLE_FILLCOLOR, "#555555");
			stylesheet.putCellStyle(STATE_STYLE_INITIAL, initialStateStyle);
		
			//Build Graph
			int y = 90;		
			String styleToApply = STATE_STYLE_NORMAL;			
			
			if (this.getPreviousState() != null) {
				if (model.getCell(getStateCellId(this.getPreviousState())) == null) {
					graph.insertVertex(parent, getStateCellId(this.getPreviousState()), this.getPreviousState().toString(), this.nextNodeX, y, 60, 60, styleToApply);
					this.nextNodeX += 120;					
				} 
			}
			if (this.getCurrentState() != null) {
				if (model.getCell(getStateCellId(this.getCurrentState())) == null) {
					graph.insertVertex(parent, getStateCellId(this.getCurrentState()), this.getCurrentState().toString(), this.nextNodeX, y, 60, 60, styleToApply);
					this.nextNodeX += 120;
				}
			}
			if (this.getPreviousState() != null && this.getCurrentState() != null)
			{
				Object stateFrom = model.getCell(getStateCellId(this.getPreviousState()));
				Object stateTo = model.getCell(getStateCellId(this.getCurrentState()));
				String actionLabel = "";
				if (this.getLastAction() != null) actionLabel = this.getLastAction().toString();
				if (mxGraphModel.getEdgesBetween(model, stateFrom, stateTo).length == 0)
					graph.insertEdge(parent, null, actionLabel, stateFrom, stateTo, "MY_EDGE");
				
			}
			
			
			
			if (this.getStateIterator() != null)
			{
				boolean newCells = false;
				
				while(stateIterator.hasNext()){
					Pair<Action, State> currentPair	= stateIterator.next();
					State nextState = currentPair.getSecond();
					
					if (model.getCell(getStateCellId(nextState)) == null) 
					{
						if (!newCells)
						{						
							this.nextNodeX += 120;
						}
						Object newCell = graph.insertVertex(parent, getStateCellId(nextState), nextState.toString(), this.nextNodeX, y, 60, 60, styleToApply);
						if (!newCells)
						{
							lastInserted = newCell;
							newCells = true;
									
						}
						y += 120;
					}
						
					if (mxGraphModel.getEdgesBetween(model, model.getCell(getStateCellId(this.getCurrentState())), model.getCell(getStateCellId(nextState))).length == 0)
						graph.insertEdge(parent, null, currentPair.getFirst().toString(), model.getCell(getStateCellId(this.getCurrentState())), model.getCell(getStateCellId(nextState)), "MY_EDGE");
					
					
				}									
			}
			
		} finally
		{
			graph.getModel().endUpdate();
		}
		
		if(this.graphComponent == null) {
			this.graphComponent = new mxGraphComponent(graph);
			add(graphComponent, BorderLayout.CENTER);
		}
		
		this.graphComponent.refresh();
		this.graphComponent.repaint();
		
		
		if (lastInserted != null)
		{			
			this.graphComponent.scrollCellToVisible(lastInserted, true);	
		}
	}			
	private void drawGraph()
	{
		if(getScheduler().getLts() == null)
			return;
		Set<State> states	= getScheduler().getLts().getStates();
		

		this.removeAll();
		this.setGraphComponent(null);
		
		this.graph = new MyGraph();

		this.model = new mxGraphModel();
		graph.setModel(model);
		
		Object parent = graph.getDefaultParent();
		//model.beginUpdate();
		this.graph.getModel().beginUpdate();
		try
		{
			mxStylesheet stylesheet = graph.getStylesheet();
			Hashtable<String, Object> style = new Hashtable<String, Object>();
			style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
			style.put(mxConstants.STYLE_OPACITY, 100);
			style.put(mxConstants.STYLE_FONTCOLOR, "#774444");
			stylesheet.putCellStyle("ROUNDED", style);
			
			Hashtable<String, Object> edgeStyle = new Hashtable<String, Object>();
			edgeStyle.put( mxConstants.STYLE_ROUNDED, true );
			edgeStyle.put( mxConstants.STYLE_ENDARROW, mxConstants.ARROW_CLASSIC );
			edgeStyle.put( mxConstants.STYLE_SHAPE, mxConstants.SHAPE_CONNECTOR );
			edgeStyle.put( mxConstants.STYLE_EDGE, mxEdgeStyle.EntityRelation );
			stylesheet.putCellStyle("MY_EDGE", edgeStyle);
			
			Hashtable<String, Object> stateStyle = new Hashtable<String, Object>();
			stateStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
			stateStyle.put(mxConstants.STYLE_OPACITY, 100);
			stateStyle.put(mxConstants.STYLE_FONTCOLOR, "#774444");
			stateStyle.put(mxConstants.STYLE_FILLCOLOR, "#DCDCDC");
			stylesheet.putCellStyle(STATE_STYLE_NORMAL, stateStyle);
			
			Hashtable<String, Object> currentStateStyle = new Hashtable<String, Object>();
			currentStateStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
			currentStateStyle.put(mxConstants.STYLE_OPACITY, 100);
			currentStateStyle.put(mxConstants.STYLE_FONTCOLOR, "#774444");
			currentStateStyle.put(mxConstants.STYLE_FILLCOLOR, "#1E90FF");
			stylesheet.putCellStyle(STATE_STYLE_CURRENT, currentStateStyle);
			
			Hashtable<String, Object> initialStateStyle = new Hashtable<String, Object>();
			initialStateStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
			initialStateStyle.put(mxConstants.STYLE_OPACITY, 50);
			initialStateStyle.put(mxConstants.STYLE_FONTCOLOR, "#774444");
			initialStateStyle.put(mxConstants.STYLE_FILLCOLOR, "#555555");
			stylesheet.putCellStyle(STATE_STYLE_INITIAL, initialStateStyle);
			
			
			//Build Graph
			int x = 30, y = 90;
			for(State state : states){
				String styleToApply = STATE_STYLE_NORMAL;
				if (state.equals(this.initialState)) styleToApply = STATE_STYLE_INITIAL;
				graph.insertVertex(parent, getStateCellId(state), state.toString(), x, y, 60, 60, styleToApply);
				x += 60;
			}
			for(State state : states){
				Iterator<Pair<Action, State>> actionIterator	= getScheduler().getLts().getTransitions(state).iterator();
				String stateName = "";
				Map<State, String> transitionLabels = new HashMap<State, String>();
				
				while(actionIterator.hasNext()){
					Pair<Action, State> actionToDraw	= actionIterator.next();
					State stateToArrive					= actionToDraw.getSecond();
					
					String prevLabel = "";
					if (transitionLabels.containsKey(stateToArrive))
						prevLabel = transitionLabels.get(stateToArrive);
						
					prevLabel += (prevLabel.equals("")?"":", " )  + actionToDraw.getFirst().toString();
					
					transitionLabels.put(stateToArrive, prevLabel);
				}
				
				for (Entry<State, String> label : transitionLabels.entrySet())
				{
					Object stateFrom = model.getCell(getStateCellId(state));
					Object stateTo = model.getCell(getStateCellId(label.getKey()));					
					graph.insertEdge(parent, null, label.getValue(), stateFrom, stateTo, "MY_EDGE");

				}
					
			}
			
			final mxIGraphLayout layout = new mxCircleLayout(graph);
			Object cell = graph.getDefaultParent();
			layout.execute(cell);

			
		}finally
		{
			graph.getModel().endUpdate();
		}
		
		this.graphComponent = new mxGraphComponent(graph);
		add(graphComponent, BorderLayout.CENTER);
		
		// add rubberband zoom
        new mxRubberband(graphComponent) {

            public void mouseReleased(MouseEvent e)
            {
                // get bounds before they are reset
                Rectangle rect = bounds;

                // invoke usual behaviour
                super.mouseReleased(e);

                if( rect != null) {

                    double newScale = 1;

                    Dimension graphSize = new Dimension( rect.width, rect.height);
                    Dimension viewPortSize = graphComponent.getViewport().getSize();

                    int gw = (int) graphSize.getWidth();
                    int gh = (int) graphSize.getHeight();

                    if (gw > 0 && gh > 0) {
                        int w = (int) viewPortSize.getWidth();
                        int h = (int) viewPortSize.getHeight();

                        newScale = Math.min((double) w / gw, (double) h / gh);
                    }

                    // zoom to fit selected area
                    graphComponent.zoom(newScale);

                    // make selected area visible 
                    graphComponent.getGraphControl().scrollRectToVisible( new Rectangle( (int) (rect.x * newScale), (int) (rect.y * newScale),  (int) (rect.width * newScale),  (int) (rect.height * newScale)));

                }

            }

        };
		
	}
	
	/**
	 * @return the initialState
	 */
	private synchronized State getInitialState() {
		return initialState;
	}

	/**
	 * @param initialState the initialState to set
	 */
	private synchronized void setInitialState(State initialState) {
		this.initialState = initialState;
	}

	/**
	 * @return the currentState
	 */
	private synchronized State getCurrentState() {
		return currentState;
	}

	/**
	 * @param currentState the currentState to set
	 */
	private synchronized void setCurrentState(State currentState) {
		this.currentState = currentState;
	}

	/**
	 * @return the previousState
	 */
	private synchronized State getPreviousState() {
		return previousState;
	}

	/**
	 * @param previousState the previousState to set
	 */
	private synchronized void setPreviousState(State previousState) {
		this.previousState = previousState;
	}

	/**
	 * @param scheduler the scheduler to set
	 */
	private synchronized void setScheduler(TakeFirstController<State, Action> scheduler) {
		this.scheduler = scheduler;
	}
	/**
	 * @return the scheduler
	 */
	private synchronized BaseController<State, Action> getScheduler() {		
		return scheduler;
	}


	/**
	 * @return the graphComponent
	 */
	private synchronized mxGraphComponent getGraphComponent() {
		return graphComponent;
	}


	/**
	 * @param graphComponent the graphComponent to set
	 */
	private synchronized void setGraphComponent(mxGraphComponent graphComponent) {
		this.graphComponent = graphComponent;
	}


	/**
	 * @return the stateIterator
	 */
	private synchronized Iterator<Pair<Action, State>> getStateIterator() {
		return stateIterator;
	}


	/**
	 * @param stateIterator the stateIterator to set
	 */
	private synchronized void setStateIterator(
			Iterator<Pair<Action, State>> stateIterator) {
		this.stateIterator = stateIterator;
	}


	/**
	 * @return the lastAction
	 */
	private synchronized Action getLastAction() {
		return lastAction;
	}


	/**
	 * @param lastAction the lastAction to set
	 */
	private synchronized void setLastAction(Action lastAction) {
		this.lastAction = lastAction;
	}

	

}
