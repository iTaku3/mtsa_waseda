package ltsa.updatingControllers.synthesis;

import MTSSynthesis.ar.dc.uba.model.condition.Fluent;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.impl.MarkedMTS;
import ltsa.control.util.ControlConstants;
import ltsa.dispatcher.TransitionSystemDispatcher;
import ltsa.lts.CompositionExpression;
import ltsa.lts.Diagnostics;
import ltsa.lts.LTSCompiler;
import ltsa.lts.LTSException;
import ltsa.lts.LTSOutput;
import ltsa.lts.Symbol;
import ltsa.updatingControllers.structures.UpdateGraphDefinition;
import ltsa.updatingControllers.structures.UpdatingControllerCompositeState;
import ltsa.lts.UpdatingControllersDefinition;
import ltsa.updatingControllers.structures.graph.UpdateGraph;
import ltsa.updatingControllers.structures.graph.UpdateNode;
import ltsa.updatingControllers.structures.graph.UpdateTransition;

import java.text.ParseException;
import java.util.*;

/**
 * Created by Victor Wjugow on 10/06/15.
 */
public class UpdateGraphGenerator {

	private static Map<String, UpdateGraphDefinition> graphDefinitions = new HashMap<String, UpdateGraphDefinition>();
	private static Map<String, UpdateGraph> graphs = new HashMap<String, UpdateGraph>();

	private UpdateGraphGenerator() {
	}

	public static UpdateGraph generateGraph(String name, LTSCompiler comp, LTSOutput output) throws ParseException {
		UpdateGraph updateGraph = null;
		UpdateGraphDefinition updateGraphDef = graphDefinitions.get(name);
		if (updateGraphDef != null) {
			updateGraph = new UpdateGraph();
			for (Symbol controllerSpecSymbol : updateGraphDef.getTransitions()) {
				UpdatingControllersDefinition updateSpec = getUpdateControllerSpec(controllerSpecSymbol);
				UpdatingControllerCompositeState updateController = (UpdatingControllerCompositeState) comp
						.continueCompilation(updateSpec.getName().getName());
				TransitionSystemDispatcher.applyComposition(updateController, output);
				if (updateController.getComposition().getName().endsWith(ControlConstants.NO_CONTROLLER)) {
					throw new LTSException("Graph could not be generated because controller " + name + " could " +
							"not be generated");
				}
				UpdateTransition transition = buildTransition(updateController);
				UpdateNode fromNode = buildFromNode(updateSpec, updateController);
				UpdateNode toNode = buildToNode(updateSpec, updateController);
				updateFromNode(updateGraph, fromNode);
				updateGraph.addEdge(transition, fromNode, toNode);
				if (controllerSpecSymbol.equals(updateGraphDef.getInitialProblem())) {
					updateGraph.setInitialState(fromNode);
				}
			}
			output.outln("Graph generated successfully!");
			graphs.put(updateGraphDef.getName(), updateGraph);
		} else {
			throw new ParseException("Could not find Graph definition: " + name, -1);
		}
		return updateGraph;
	}

	public static void addGraphDefinition(UpdateGraphDefinition graphDefinition) {
		if (graphDefinition != null) {
			graphDefinitions.put(graphDefinition.getName(), graphDefinition);
		}
	}

	public static Set<String> getGraphNames() {
		return graphDefinitions.keySet();
	}

	public static UpdateGraph getGraph(String name) {
		return graphs.get(name);
	}

	/*
		Compiles the given Update Controller symbol
	 */
	private static UpdatingControllersDefinition getUpdateControllerSpec(Symbol controllerSpecSymbol) {
		CompositionExpression composite = LTSCompiler.getComposite(controllerSpecSymbol.getName());
		validate(composite, controllerSpecSymbol);
		UpdatingControllersDefinition updateSpec = (UpdatingControllersDefinition) composite;
		return updateSpec;
	}

	private static void validate(CompositionExpression composite, Symbol symbol) {
		if (composite == null) {
			Diagnostics.fatal("Updating Controller not found", symbol);
		} else if (!(composite instanceof UpdatingControllersDefinition)) {
			Diagnostics.fatal("Transitions of 'graphUpdate' should be 'updatingControllers'", symbol);
		}
	}

	private static UpdateTransition buildTransition(UpdatingControllerCompositeState updtControllerCS) {
		UpdateTransition transition = new UpdateTransition();
		//transition.setFluents((List<Fluent>) updtControllerCS.getUpdateFluents());
		MTS<Long, String> updateController = updtControllerCS.getUpdateController();
		MarkedMTS<Long, String> marked = UpdatingControllersUtils.markCuTerminalSet(updateController);
		transition.setUpdateController(marked);
		transition.setControllableActions(updtControllerCS.getUpdateGRGoal().getControllableActions());
		return transition;
	}

	private static UpdateNode buildFromNode(UpdatingControllersDefinition spe, UpdatingControllerCompositeState uccs) {
		String goalName = spe.getOldGoal().toString();
//		return buildNode(uccs.getOldEnvironment(), uccs.getOldController(), uccs.getControllableActions(), goalName);
		return null;
	}

	private static UpdateNode buildToNode(UpdatingControllersDefinition spec, UpdatingControllerCompositeState uccs) {
		//If there's a transition FROM this node, eventually this controller will be assigned.
		//If this node is a final one, then we don't need to generate the controller since it will run the marked Cu.
		//However, we do need at least the new environment to map the states when cutting down the Cu to C'.
//		MTS<Long, String> newController = uccs.getNewEnvironment();
		String goalName = spec.getNewGoal().toString();
//		return buildNode(uccs.getNewEnvironment(), newController, uccs.getControllableActions(), goalName);
		return null;
	}

	private static UpdateNode buildNode(MTS<Long, String> environment, MTS<Long, String> controller, Set<String>
			controllableActions, String goalName) {
		UpdateNode node = new UpdateNode();
		node.setEnvironment(environment);
		node.setController$environment(controller);
		node.setGoalName(goalName);
		//TODO think about removing update actions
		node.setControllableActions(controllableActions);
		return node;
	}

	/*
		Updates the From Node, in case it had previously been a To Node, in which case will have a null controller.
	 */
	private static void updateFromNode(UpdateGraph updateGraph, UpdateNode fromNode) {
		if (updateGraph.containsVertex(fromNode)) {
			Iterator<UpdateNode> it = updateGraph.getVertices().iterator();
			UpdateNode node = null;
			while (it.hasNext() && node == null) {
				UpdateNode next = it.next();
				if (next.equals(fromNode)) {
					node = next;
				}
			}
			node.setController$environment(fromNode.getController$environment());
		}
	}
}