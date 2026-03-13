package ltsa.ui.update.events;

import ltsa.updatingControllers.structures.graph.UpdateNode;
import ltsa.updatingControllers.structures.graph.UpdateTransition;

/**
 * Created by Victor Wjugow on 19/06/15.
 */
public class NodeClickedEvent extends UpdateGraphEvent {

	private final UpdateTransition updateTransition;
	private final UpdateNode nextNode;

	public NodeClickedEvent(UpdateTransition updateTransition, UpdateNode nextNode) {
		this.updateTransition = updateTransition;
		this.nextNode = nextNode;
	}

	public UpdateTransition getUpdateTransition() {
		return updateTransition;
	}

	public UpdateNode getNextNode() {
		return nextNode;
	}
}