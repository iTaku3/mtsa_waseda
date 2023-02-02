package ltsa.ui.update.events;

import ltsa.updatingControllers.structures.graph.UpdateNode;

/**
 * Created by Victor Wjugow on 19/06/15.
 */
public class StartedEvent extends UpdateGraphEvent {

	private final UpdateNode initialNode;

	protected StartedEvent(UpdateNode initialNode) {
		this.initialNode = initialNode;
	}

	public UpdateNode getInitialNode() {
		return initialNode;
	}
}