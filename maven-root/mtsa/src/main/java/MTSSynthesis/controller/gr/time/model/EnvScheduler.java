package MTSSynthesis.controller.gr.time.model;

import MTSSynthesis.controller.gr.time.GenericChooser;
import MTSTools.ac.ic.doc.commons.relations.Pair;

import java.util.Set;

public class EnvScheduler<S,A> extends GenericChooser<S,A,Pair<S,S>> {

	Set<A> controllableActions; 

	public EnvScheduler(Set<A> controllableActions) {
		super();
		this.controllableActions = controllableActions;
	}
	
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof EnvScheduler<?, ?>){
			EnvScheduler<?, ?> gc = (EnvScheduler<?, ?>) obj;
			return this.controllableActions.equals(gc.controllableActions)
					&& super.equals(gc);
		}
		else
			return false;	
	}

	@Override
	protected Set<A> getControllableActions() {
		return controllableActions;
	}

}
