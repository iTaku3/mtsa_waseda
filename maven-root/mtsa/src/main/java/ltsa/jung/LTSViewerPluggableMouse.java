package ltsa.jung;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;

import ltsa.jung.LTSGraph.LTSNavigator;
import ltsa.jung.LTSJUNGCanvas.EnumLayout;
import ltsa.jung.LTSJUNGCanvas.EnumMode;
import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.control.AbstractGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.GraphMousePlugin;
import edu.uci.ics.jung.visualization.control.PickingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.PluggableGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.TranslatingGraphMousePlugin;
import edu.uci.ics.jung.visualization.transform.MutableTransformer;

/**
 * Mouse behavior for the LTSViewer
 * @author Cédric Delforge
 */
public class LTSViewerPluggableMouse extends PluggableGraphMouse {
	/**
	 * used by the scaling plugins for zoom in
	 */
    protected float in;
    /**
     * used by the scaling plugins for zoom out
     */
    protected float out;
    
    protected EnumMode mode = EnumMode.Edit;

    protected GraphMousePlugin pickingPlugin;
    protected GraphMousePlugin translatingPlugin;
    protected GraphMousePlugin scalingPlugin;
    protected GraphMousePlugin rotatingPlugin;
    protected GraphMousePlugin popupPlugin;
    protected GraphMousePlugin navigatingPlugin;
    

    public LTSViewerPluggableMouse() {
        this(1.1f, 1/1.1f);
    }
    public LTSViewerPluggableMouse(float in, float out) {
		this.in = in;
		this.out = out;
		loadPlugins();
	}
    
	public void setMode(EnumMode m) {
		mode = m;
       	if (mode == EnumMode.Edit) {
       		setEdit();
       	} else {
       		setActivate();
       	}
	}

	/**
     * creates the plugins, and loads the plugins
     *
     */
    protected void loadPlugins() {
    	scalingPlugin = new ScalingGraphMousePlugin(new CrossoverScalingControl(), 0, in, out);
    	popupPlugin = new PopupTranslatingGraphMousePlugin();
    	pickingPlugin = new PickingGraphMousePlugin<StateVertex,TransitionEdge>();
    	translatingPlugin = new TranslatingGraphMousePlugin(MouseEvent.BUTTON3_MASK);
    	navigatingPlugin = new NavigationGraphMousePlugin();
    	add(scalingPlugin);
       	if (mode == EnumMode.Edit) {
       		setEdit();
       	} else {
       		setActivate();
       	}
    }
    
    private void setEdit() {
   	    remove(translatingPlugin);
   	    remove(navigatingPlugin);
   	    add(pickingPlugin);
    	add(popupPlugin);   	
    }
    private void setActivate() {
   		remove(popupPlugin);
   		remove(pickingPlugin);
   		add(translatingPlugin);
   		add(navigatingPlugin);
    }

    /**
     * @param zoomAtMouse The zoomAtMouse to set.
     */
    public void setZoomAtMouse(boolean zoomAtMouse) {
        ((ScalingGraphMousePlugin) scalingPlugin).setZoomAtMouse(zoomAtMouse);
    }
    
    /*
     * The mouse plugin for the navigation mode
     * Navigates states by clicking on them
     */
    private class NavigationGraphMousePlugin extends AbstractGraphMousePlugin
    implements MouseListener, MouseMotionListener { 
    	public NavigationGraphMousePlugin() {
    		super(MouseEvent.BUTTON1_MASK);
    	}
    	
        public void mousePressed(MouseEvent e) {
	            down = e.getPoint();
	        	StateVertex vertex = null;
	            LTSViewer vv = (LTSViewer)e.getSource();
	            LTSNavigator navigator = vv.getNavigator();
	            GraphElementAccessor<StateVertex,TransitionEdge> pickSupport = vv.getPickSupport();
				if(pickSupport != null) {
	                Layout<StateVertex,TransitionEdge> layout = vv.getGraphLayout();
	                // p is the screen point for the mouse event
	                Point2D ip = e.getPoint();
	                @SuppressWarnings("unused")
					TransitionEdge edge;
	
	                vertex = pickSupport.getVertex(layout, ip.getX(), ip.getY());
	                if(vertex != null) {
	                	if (navigator.getNext().contains(vertex)) {
	                		navigator.navigateTo(vertex);
	                		for (StateVertex v: navigator.getReached()) {
	                			vv.getPickedVertexState().pick(v, false);
	                		}
	                		for (StateVertex v: navigator.getCurrent()) {
	                			vv.getPickedVertexState().pick(v, true);
	                		}
	                		vv.repaint();
	                	}
	                } else if((edge = pickSupport.getEdge(layout, ip.getX(), ip.getY())) != null) {
	                    /*pickedEdgeState.clear();
	                    pickedEdgeState.pick(edge, true);*/
	                } else {
	                	/*pickedEdgeState.clear();
	                    pickedVertexState.clear();*/
	                }
	            }
	            if(vertex != null) e.consume();
        }

		public void mouseDragged(MouseEvent e) {}
		public void mouseMoved(MouseEvent e) {}
		public void mouseClicked(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
    	
    }
    
    /*
     * The mouse plugin for the combined right click actions
     * Displays a context menu when clicking on a state
     * Translates the canvas when clicking outside
     */
    private class PopupTranslatingGraphMousePlugin extends AbstractGraphMousePlugin
    implements MouseListener, MouseMotionListener  {
    	private boolean pressed;
        
        public PopupTranslatingGraphMousePlugin() {
            this(MouseEvent.BUTTON3_MASK);
        }
        public PopupTranslatingGraphMousePlugin(int modifiers) {
            super(modifiers);
            this.pressed = false;
            this.cursor = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
        }
        
        /**
         * If this event is over a Vertex, pop up a menu
         * @param e
         */
        @SuppressWarnings({ "serial" })
        private void handlePopup(MouseEvent event) {
            final LTSViewer view = (LTSViewer) event.getSource();
            Point2D p = down;
            
            GraphElementAccessor<StateVertex,TransitionEdge> pickSupport = view.getPickSupport();
            if(pickSupport != null) {
                final StateVertex v = pickSupport.getVertex(view.getGraphLayout(), p.getX(), p.getY());
                if(v != null) {
                	if (!view.getPickedVertexState().isPicked(v)) {
                		view.getPickedVertexState().clear();
                		view.getPickedVertexState().pick(v, true);
                	}
                    JPopupMenu popup = new JPopupMenu();
                    popup.add(new AbstractAction("Select next states") {
                        public void actionPerformed(ActionEvent e) {
                        	view.selectNext();
                            view.repaint();
                        }
                    });
                    popup.add(new AbstractAction("Select previous states") {
                        public void actionPerformed(ActionEvent e) {
                        	view.selectPrevious();
                            view.repaint();
                        }
                    });
                    popup.add(new AbstractAction("Select reachable states") {
                        public void actionPerformed(ActionEvent e) {
                        	view.selectReachable();
                            view.repaint();
                        }
                    });popup.add(new AbstractAction("Select reaching states") {
                        public void actionPerformed(ActionEvent e) {
                        	view.selectReaching();
                            view.repaint();
                        }
                    });
                    popup.add(new AbstractAction("Select LTS") {
                        public void actionPerformed(ActionEvent e) {
                        	view.selectLTS();
                            view.repaint();
                        }
                    });
                    popup.add(new AbstractAction("Select SCC"){
                        public void actionPerformed(ActionEvent e) {
                        	view.selectSCC();
                            view.repaint();
                        }
                    });
                    JMenu layouts = new JMenu("Apply layout to selection");
                    ArrayList<EnumLayout> layoutTypes = new ArrayList<EnumLayout>();
                    for(EnumLayout l: LTSJUNGCanvas.EnumLayout.values()) {
                  	  if (l != EnumLayout.Aggregate)
                  		  layoutTypes.add(l);
                    }
                    for (final EnumLayout l: layoutTypes) {
                    	layouts.add(new AbstractAction(l.toString()) {
                    		public void actionPerformed(ActionEvent e) {
                    			view.addLayout(l);
                    		}
                    	});
                    }
                    popup.add(layouts);
                    /*popup.add(new AbstractAction("Cluster selection") {
                        public void actionPerformed(ActionEvent e) {
                        	view.cluster();
                            view.repaint();
                        }
                    });*/
                    popup.show(view, event.getX(), event.getY());
                } else {
                    final TransitionEdge e = pickSupport.getEdge(view.getGraphLayout(), p.getX(), p.getY());
                    if(e != null) {
                    }
                }
            }
        }
        /**
         * Check the event modifiers. Set the 'down' point for later
         * use. If this event satisfies the modifiers, change the cursor
         * to the system 'move cursor'
    	 * @param e the event
    	 */
		public void mousePressed(MouseEvent e) {
    		LTSViewer vv = (LTSViewer)e.getSource();
    	    boolean accepted = checkModifiers(e);
    	    down = e.getPoint();
    	    if(accepted) {
    	        vv.setCursor(cursor);
    	    }
            if(e.isPopupTrigger()) {
                pressed = true;
                e.consume();
            }
    	}
        
    	/**
    	 * unset the 'down' point and change the cursor back to the system
         * default cursor
    	 */
		public void mouseReleased(MouseEvent e) {
            LTSViewer vv = (LTSViewer)e.getSource();
            vv.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            if(e.isPopupTrigger() || (pressed && down != null)) {
                handlePopup(e);
                e.consume();
                pressed = false;
            }
            down = null;
        }
        
        /**
         * chack the modifiers. If accepted, translate the graph according
         * to the dragging of the mouse pointer
         * @param e the event
    	 */
		public void mouseDragged(MouseEvent e) {
        	LTSViewer vv = (LTSViewer)e.getSource();
            boolean accepted = checkModifiers(e);
            if(accepted) {
                MutableTransformer modelTransformer = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT);
                vv.setCursor(cursor);
                try {
                    Point2D q = vv.getRenderContext().getMultiLayerTransformer().inverseTransform(down);
                    Point2D p = vv.getRenderContext().getMultiLayerTransformer().inverseTransform(e.getPoint());
                    float dx = (float) (p.getX()-q.getX());
                    float dy = (float) (p.getY()-q.getY());
                    
                    modelTransformer.translate(dx, dy);
                    down.x = e.getX();
                    down.y = e.getY();
                } catch(RuntimeException ex) {
                    System.err.println("down = "+down+", e = "+e);
                    throw ex;
                }
            
                e.consume();
                vv.repaint();
            }
        }

        public void mouseClicked(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {    
        }

        public void mouseMoved(MouseEvent e) {  
        }
    }
}