package ltsa.ui.update.events;

import ltsa.updatingControllers.structures.graph.UpdateNode;
import ltsa.updatingControllers.structures.graph.UpdateTransition;

/**
 * Created by Victor Wjugow on 18/06/15.
 */
public abstract class UpdateGraphEvent {

	protected UpdateGraphEvent() {
	}

	public static StartedEvent getStartedEvent(UpdateNode initialNode) {
		return new StartedEvent(initialNode);
	}

	public static NodeClickedEvent getNodeClickedEvent(UpdateTransition updateTransition, UpdateNode currentNode,
													   UpdateNode nextNode) {
		return new NodeClickedEvent(updateTransition, nextNode);
	}

	public static EndedEvent getEndedEvent() {
		return new EndedEvent();
	}
}