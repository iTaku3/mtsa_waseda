package ltsa.ui.update;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import org.apache.commons.collections15.BidiMap;
import org.apache.commons.collections15.bidimap.DualHashBidiMap;
import ltsa.ui.update.events.EndedEvent;
import ltsa.ui.update.events.NodeClickedEvent;
import ltsa.ui.update.events.StartedEvent;
import ltsa.ui.update.events.UpdateGraphEvent;
import ltsa.ui.update.utilities.NonEditableGraphView;
import ltsa.updatingControllers.structures.graph.UpdateGraph;
import ltsa.updatingControllers.structures.graph.UpdateNode;
import ltsa.updatingControllers.structures.graph.UpdateTransition;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;

import static ltsa.ui.update.utilities.NonEditableGraphView.INITIAL_STYLE;
import static ltsa.ui.update.utilities.NonEditableGraphView.NEXT_STYLE;
import static ltsa.ui.update.utilities.NonEditableGraphView.REACHABLE_STYLE;
import static ltsa.ui.update.utilities.NonEditableGraphView.UNREACHABLE_STYLE;

/**
 * Created by Victor Wjugow on 15/06/15.
 */
public class UpdateGraphGUI extends JPanel implements UpdateGraphEventDispatcher {

	public static final double DEFAULT_DIAMETER = 100;
	private final NonEditableGraphView graph = new NonEditableGraphView();
	private final Set<UpdateGraphEventListener> eventListeners = new HashSet<UpdateGraphEventListener>();
	private boolean updating = false;
	private mxGraphComponent graphComponent;
	private UpdateGraph updateGraph;
	private Object currentCell = null;
	private Object nextCell;
	private UpdateNode currentNode = null;
	private UpdateNode nextNode;
	private BidiMap<UpdateNode, Object> updateNodeToViewNode;

	public UpdateGraphGUI() {
		super();
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.graph.setModel(new mxGraphModel());
		this.graph.setDefaultStyle();
		super.repaint();
	}

	public void init(UpdateGraph updateGraph) {
		if (graphComponent != null) {
			graphComponent.setVisible(false);
			super.remove(graphComponent);
			super.repaint();
		}
		this.graphComponent = new mxGraphComponent(this.graph);
		this.updateGraph = updateGraph;
		this.buildGraphUI(updateGraph, this.graph, this.graphComponent);
		super.add(graphComponent);
		super.repaint();
		this.fireStartedEvent(this.currentNode);
	}

	private void buildGraphUI(UpdateGraph updateGraph, NonEditableGraphView graph, mxGraphComponent graphComponent) {
		if (updateGraph != null) {
			this.updateNodeToViewNode = this.insertNodes(graph, updateGraph);
			this.insertEdges(graph, updateGraph);
			this.setLayout(graph);
			this.addNodeClickHandler(graphComponent, graph, updateGraph);
			graphComponent.refresh();
			graphComponent.repaint();
		}
	}

	private BidiMap<UpdateNode, Object> insertNodes(NonEditableGraphView graph, UpdateGraph updGraph) {
		double x = DEFAULT_DIAMETER, y = DEFAULT_DIAMETER;
		Object defaultParent = graph.getDefaultParent();
		BidiMap<UpdateNode, Object> updateNodeToViewNode = new DualHashBidiMap<UpdateNode, Object>();
		String style;
		this.currentNode = updGraph.getInitialState();
		for (UpdateNode node : updGraph.getVertices()) {
			style = this.getStyle(node, this.currentNode, updGraph);
			graph.getModel().beginUpdate();
			Object viewNode = graph.insertVertex(defaultParent, node.getGoalName(), node.getGoalName(), x, y,
				DEFAULT_DIAMETER, DEFAULT_DIAMETER, style);
			graph.getModel().endUpdate();
			x += 1.5 * DEFAULT_DIAMETER;
			if (x > 1000) {
				x = DEFAULT_DIAMETER;
				y += 1.5 * DEFAULT_DIAMETER;
			}
			updateNodeToViewNode.put(node, viewNode);
		}
		this.currentCell = updateNodeToViewNode.get(currentNode);
		return updateNodeToViewNode;
	}

	private void insertEdges(NonEditableGraphView graph, UpdateGraph updateGraph) {
		Object defaultParent = graph.getDefaultParent();
		for (UpdateTransition updateTransition : updateGraph.getEdges()) {
			Object[] nodes = updateGraph.getIncidentVertices(updateTransition).toArray();
			UpdateNode fromUpdNode = (UpdateNode) nodes[0];
			UpdateNode toUpdNode = (UpdateNode) nodes[1];
			Object fromNode = this.updateNodeToViewNode.get(fromUpdNode);
			Object toNode = this.updateNodeToViewNode.get(toUpdNode);
			StringBuilder b = new StringBuilder(fromUpdNode.getGoalName()).append("-").append(toUpdNode.getGoalName());
			graph.getModel().beginUpdate();
			graph.insertEdge(defaultParent, b.toString(), b.toString(), fromNode, toNode);
			graph.getModel().endUpdate();
		}
	}

	private void addNodeClickHandler(final mxGraphComponent graphComponent, final NonEditableGraphView graph, final
	UpdateGraph updtGraph) {
		MouseAdapter mouseAdapter = new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				if (!updating && e.getClickCount() > 1) {
					Object cell = graphComponent.getCellAt(e.getX(), e.getY());
					if (isReachable(cell)) {
						Object[] cells = {cell};
						graph.setCellStyle(NEXT_STYLE, cells);
						nextNode = updateNodeToViewNode.getKey(cell);
						nextCell = cell;
						fireNodeClickedEvent(updtGraph.findEdge(currentNode, nextNode));
						updating = true;
					}
				}
			}
		};
		graphComponent.getGraphControl().addMouseListener(mouseAdapter);
	}

	private String getStyle(UpdateNode node, UpdateNode currentNode, UpdateGraph updateGraph) {
		String style;
		if (node.equals(updateGraph.getInitialState())) {
			style = INITIAL_STYLE;
		} else if (isReachable(node, currentNode, updateGraph)) {
			style = REACHABLE_STYLE;
		} else {
			style = UNREACHABLE_STYLE;
		}
		return style;
	}

	private boolean isReachable(UpdateNode node, UpdateNode currentNode, UpdateGraph updateGraph) {
		return updateGraph.getSuccessors(currentNode).contains(node);
	}

	private boolean isReachable(Object cell) {
		Object[] outgoingEdges = graph.getOutgoingEdges(currentCell);
		Object[] incomingEdges = graph.getIncomingEdges(cell);
		for (Object incomingEdge : incomingEdges) {
			for (Object outgoingEdge : outgoingEdges) {
				if (incomingEdge == outgoingEdge) {
					return true;
				}
			}
		}
		return false;
	}

	private void setLayout(NonEditableGraphView graph) {
		mxIGraphLayout layout = new mxHierarchicalLayout(graph);
		graph.getModel().beginUpdate();
		try {
			layout.execute(graph.getDefaultParent());
		} finally {
			graph.getModel().endUpdate();
		}
	}

	//Think about moving this to UpdateGraphSimulation
	private void fireStartedEvent(UpdateNode initialState) {
		StartedEvent startedEvent = UpdateGraphEvent.getStartedEvent(initialState);
		for (UpdateGraphEventListener eventListener : this.eventListeners) {
			eventListener.handleUpdateGraphEvent(startedEvent);
		}
	}

	private void fireNodeClickedEvent(UpdateTransition updateTransition) {
		NodeClickedEvent clickedEvent = UpdateGraphEvent.getNodeClickedEvent(updateTransition, currentNode, nextNode);
		for (UpdateGraphEventListener eventListener : this.eventListeners) {
			eventListener.handleUpdateGraphEvent(clickedEvent);
		}
	}

	public void end() {
		EndedEvent endedEvent = UpdateGraphEvent.getEndedEvent();
		for (UpdateGraphEventListener eventListener : this.eventListeners) {
			eventListener.handleUpdateGraphEvent(endedEvent);
		}
	}

	public void updateFinished() {
		this.updating = false;
		this.currentNode = this.nextNode;
		this.currentCell = this.nextCell;
		this.nextCell = null;
		this.nextNode = null;
		Set<Object> available = new HashSet<Object>(), forbidden = new HashSet<Object>();
		Object[] initial = {currentCell};
		for (UpdateNode node : updateGraph.getVertices()) {
			if (!currentNode.equals(node)) {
				if (isReachable(node, currentNode, updateGraph)) {
					available.add(updateNodeToViewNode.get(node));
				} else {
					forbidden.add(updateNodeToViewNode.get(node));
				}
			}
		}
		graph.setCellStyle(REACHABLE_STYLE, available.toArray());
		graph.setCellStyle(UNREACHABLE_STYLE, forbidden.toArray());
		graph.setCellStyle(INITIAL_STYLE, initial);
	}

	@Override
	public void addUpdateGraphEventListener(UpdateGraphEventListener listener) {
		this.eventListeners.add(listener);
	}

	@Override
	public void removeAllListeners() {
		this.eventListeners.clear();
	}
}