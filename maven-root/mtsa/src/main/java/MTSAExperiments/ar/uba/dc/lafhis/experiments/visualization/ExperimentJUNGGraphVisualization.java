package MTSAExperiments.ar.uba.dc.lafhis.experiments.visualization;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.commons.collections15.Transformer;

import MTSAExperiments.ar.uba.dc.lafhis.experiments.exchange.JSONCompatible;
import MTSAExperiments.ar.uba.dc.lafhis.experiments.exchange.JSONCompatibleObject;
import MTSAExperiments.ar.uba.dc.lafhis.experiments.jung.ExperimentJUNGGameEdgeValue;
import MTSAExperiments.ar.uba.dc.lafhis.experiments.jung.ExperimentJUNGGameNodeValue;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout2;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.MultiGraph;
import edu.uci.ics.jung.graph.util.Context;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import edu.uci.ics.jung.visualization.VisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse.Mode;
import edu.uci.ics.jung.visualization.decorators.DirectionalEdgeArrowTransformer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

public class ExperimentJUNGGraphVisualization extends ExperimentResultVisualization {

	public static int MARGIN_W 		= 50;
	public static int MARGIN_H 		= 50;
	
	protected Dimension dimension;
	
	protected VisualizationViewer<ExperimentJUNGGameNodeValue<Long>, ExperimentJUNGGameEdgeValue<String,Long>> visualizationViewer;
	protected VisualizationImageServer<ExperimentJUNGGameNodeValue<Long>, ExperimentJUNGGameEdgeValue<String,Long>> visualizationServer;
	
	public Dimension getDimension(){
		return dimension;
	}
	
	public ExperimentJUNGGraphVisualization(JSONCompatibleObject jsonGraph, Dimension dimension) throws Exception{
		super(jsonGraph);
		this.dimension = dimension;
		initializeViewer();
		initializeServer();
	}
	
	public VisualizationViewer<ExperimentJUNGGameNodeValue<Long>, ExperimentJUNGGameEdgeValue<String,Long>> getVisualizationViewer(){
		return visualizationViewer;
	}
	
	public BufferedImage getGraphImage(){

		// Create the buffered image
		BufferedImage image = (BufferedImage) visualizationServer.getImage(
		    new Point2D.Double(visualizationViewer.getGraphLayout().getSize().getWidth() / 2,
		    		visualizationViewer.getGraphLayout().getSize().getHeight() / 2),
		    new Dimension(visualizationViewer.getGraphLayout().getSize()));
		
		return image;
	}
	
	protected void initializeViewer() throws Exception{
		if(!(jsonValue instanceof JSONCompatibleObject))
			throw new Exception("ExperimentJUNGGraphVisualization::buildComponent value should be of type JSONCompatibleObject");
		JSONCompatibleObject jsonObject = (JSONCompatibleObject)jsonValue;
		if(!(jsonObject.getValue() instanceof DirectedSparseMultigraph))
			throw new Exception("ExperimentJUNGGraphVisualization::ExperimentJUNGGraphVisualization value should be of type DirectedGraph");

		DirectedSparseMultigraph<ExperimentJUNGGameNodeValue<Long>, ExperimentJUNGGameEdgeValue<String,Long>> graph = (DirectedSparseMultigraph)jsonObject.getValue();
		
		 // The Layout<V, E> is parameterized by the vertex and edge types
		 Layout<ExperimentJUNGGameNodeValue<Long>, ExperimentJUNGGameEdgeValue<String,Long>> layout = new ISOMLayout<ExperimentJUNGGameNodeValue<Long>, ExperimentJUNGGameEdgeValue<String,Long>>((Graph<ExperimentJUNGGameNodeValue<Long>, ExperimentJUNGGameEdgeValue<String, Long>>) graph);
		 layout.setSize(dimension); // sets the initial size of the space
		 // The BasicVisualizationServer<V,E> is parameterized by the edge types
		 
		 
		visualizationViewer =
		 new VisualizationViewer<ExperimentJUNGGameNodeValue<Long>, ExperimentJUNGGameEdgeValue<String,Long>>(layout);
		 Dimension visibleDimension = new Dimension(dimension.width + MARGIN_W, dimension.height + MARGIN_H);
		 visualizationViewer.setPreferredSize(visibleDimension); //Sets the viewing area size

		 Transformer<ExperimentJUNGGameEdgeValue<String,Long>, Font> edgeFont	= new Transformer<ExperimentJUNGGameEdgeValue<String,Long>, Font>(){
			 public Font transform(ExperimentJUNGGameEdgeValue<String,Long> edge) {
				 return new Font ("Garamond", Font.PLAIN , 11);
			 }			 
		 };
		 Transformer<ExperimentJUNGGameNodeValue<Long>, Font> vertexFont	= new Transformer<ExperimentJUNGGameNodeValue<Long>, Font>(){
			 public Font transform(ExperimentJUNGGameNodeValue<Long> vertex) {
				 return new Font ("Garamond", Font.PLAIN , 11);
			 }			 
		 };		 
		 
		 // Setup up a new vertex to paint transformer...
		 Transformer<ExperimentJUNGGameNodeValue<Long>,Paint> vertexPaint = new Transformer<ExperimentJUNGGameNodeValue<Long>,Paint>() {
		 public Paint transform(ExperimentJUNGGameNodeValue<Long> vertex) {
			 if(vertex.getIsWinning())
				return Color.GREEN;
			 else
				 return Color.RED;
			 
		 }
		 };
		 // Set up a new stroke Transformer for the edges
		 float nodeDash[]			= {7.0f, 2.0f};		 
		 final Stroke normalStroke 	= new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f);
		 final Stroke initialStroke = new BasicStroke(3.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, nodeDash, 0.0f);
		 Transformer<ExperimentJUNGGameNodeValue<Long>,Stroke> vertexStroke = new Transformer<ExperimentJUNGGameNodeValue<Long>,Stroke>() {
		 public Stroke transform(ExperimentJUNGGameNodeValue<Long> vertex) {
			 if(vertex.getIsInitial())
				return initialStroke;
			 else 
				return normalStroke;
		 }
		 };		 
		 // Set up a new stroke Transformer for the edges
		 float dash[] 							= {10.0f};
		 final Stroke noncontrollableStroke 		= new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
		 final Stroke controllableStroke 	= new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f);
		 Transformer<ExperimentJUNGGameEdgeValue<String,Long>, Stroke> edgeStrokeTransformer = new Transformer<ExperimentJUNGGameEdgeValue<String,Long>, Stroke>() {
		 public Stroke transform(ExperimentJUNGGameEdgeValue<String,Long> edge) {
			 if(edge.getIsControllable())
				 return controllableStroke;
			 else
				 return noncontrollableStroke;
		 }
		 };
	     Transformer<ExperimentJUNGGameNodeValue<Long>,Shape> vertexSize = new Transformer<ExperimentJUNGGameNodeValue<Long>,Shape>(){
	            public Shape transform(ExperimentJUNGGameNodeValue<Long> vertex){
	                return new Ellipse2D.Double(-5, -5, 10, 10);
	            }
	     };		 
		 
		 Transformer<Context<Graph<ExperimentJUNGGameNodeValue<Long>, ExperimentJUNGGameEdgeValue<String,Long>>, ExperimentJUNGGameEdgeValue<String,Long>>,Shape> edgeArrowTransformer = 
				 new DirectionalEdgeArrowTransformer<ExperimentJUNGGameNodeValue<Long>, ExperimentJUNGGameEdgeValue<String,Long>>(10, 8, 4);
		 DefaultModalGraphMouse<ExperimentJUNGGameNodeValue<Long>, ExperimentJUNGGameEdgeValue<String,Long>> gm = new DefaultModalGraphMouse<ExperimentJUNGGameNodeValue<Long>, ExperimentJUNGGameEdgeValue<String,Long>>();
		 gm.setMode(Mode.TRANSFORMING);
		 visualizationViewer.setGraphMouse(gm);
		 visualizationViewer.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
		 visualizationViewer.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
		 visualizationViewer.setBackground(Color.WHITE);
		 visualizationViewer.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
		 visualizationViewer.getRenderContext().setVertexStrokeTransformer(vertexStroke);
		 visualizationViewer.getRenderContext().setEdgeStrokeTransformer(edgeStrokeTransformer);
		 visualizationViewer.getRenderContext().setEdgeFontTransformer(edgeFont);
		 visualizationViewer.getRenderContext().setVertexFontTransformer(vertexFont);
		 visualizationViewer.getRenderContext().setVertexShapeTransformer(vertexSize);
		 
		 visualizationViewer.getRenderContext().setEdgeArrowStrokeTransformer(edgeStrokeTransformer);
		 visualizationViewer.getRenderContext().setEdgeArrowTransformer(edgeArrowTransformer);
		 
		 visualizationViewer.getRenderer().getVertexLabelRenderer().setPosition(Position.S);

	}
	
	protected void initializeServer() throws Exception{
		// Create the VisualizationImageServer
		// vv is the VisualizationViewer containing my graph
		visualizationServer =
		    new VisualizationImageServer<ExperimentJUNGGameNodeValue<Long>, ExperimentJUNGGameEdgeValue<String,Long>>(
		    		visualizationViewer.getGraphLayout(), visualizationViewer.getGraphLayout().getSize());

		// Configure the VisualizationImageServer the same way
		// you did your VisualizationViewer. In my case e.g.

		 // Setup up a new vertex to paint transformer...
		 Transformer<ExperimentJUNGGameNodeValue<Long>,Paint> vertexPaint = new Transformer<ExperimentJUNGGameNodeValue<Long>,Paint>() {
		 public Paint transform(ExperimentJUNGGameNodeValue<Long> vertex) {
			 if(vertex.getIsWinning())
				return Color.GREEN;
			 else
				 return Color.RED;
			 
		 }
		 };
		 // Set up a new stroke Transformer for the edges
		 float nodeDash[]			= {7.0f, 2.0f};		 
		 final Stroke normalStroke 	= new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f);
		 final Stroke initialStroke = new BasicStroke(3.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, nodeDash, 0.0f);
		 Transformer<ExperimentJUNGGameNodeValue<Long>,Stroke> vertexStroke = new Transformer<ExperimentJUNGGameNodeValue<Long>,Stroke>() {
		 public Stroke transform(ExperimentJUNGGameNodeValue<Long> vertex) {
			 if(vertex.getIsInitial())
				return initialStroke;
			 else 
				return normalStroke;
		 }
		 };		 
		 // Set up a new stroke Transformer for the edges
		 float dash[] 							= {10.0f};
		 final Stroke noncontrollableStroke 		= new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
		 final Stroke controllableStroke 	= new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f);
		 Transformer<ExperimentJUNGGameEdgeValue<String,Long>, Stroke> edgeStrokeTransformer = new Transformer<ExperimentJUNGGameEdgeValue<String,Long>, Stroke>() {
		 public Stroke transform(ExperimentJUNGGameEdgeValue<String,Long> edge) {
			 if(edge.getIsControllable())
				 return controllableStroke;
			 else
				 return noncontrollableStroke;
		 }
		 };
		 
		 visualizationServer.setBackground(Color.WHITE);
		 visualizationServer.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
		 visualizationServer.getRenderContext().setVertexStrokeTransformer(vertexStroke);
		 visualizationServer.getRenderContext().setEdgeStrokeTransformer(edgeStrokeTransformer);
		 visualizationServer.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
		 visualizationServer.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
		 visualizationServer.getRenderer().getVertexLabelRenderer().setPosition(Position.SE);
	
	}
	
	@Override
	protected JComponent buildComponent(JSONCompatible jsonGraph) throws Exception{
		 
		 JPanel panel = new JPanel();
		 panel.add(visualizationViewer);
		 panel.setVisible(true); 		
		
		return panel;
	}

}
