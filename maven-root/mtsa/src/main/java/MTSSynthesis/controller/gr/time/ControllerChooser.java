package MTSSynthesis.controller.gr.time;

import java.util.Set;

public class ControllerChooser<S,A> extends GenericChooser<S,A,S> {

	Set<A> controllableActions; 

	public ControllerChooser(Set<A> controllableActions) {
		super();
		this.controllableActions = controllableActions;
	}


	@Override
	protected Set<A> getControllableActions() {
		return controllableActions;
	}

}
