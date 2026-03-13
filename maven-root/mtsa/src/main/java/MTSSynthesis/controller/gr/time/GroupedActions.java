package MTSSynthesis.controller.gr.time;

import MTSSynthesis.controller.gr.time.model.ChoiceType;

import java.util.HashSet;
import java.util.Set;

public class GroupedActions<A> {
	Set<A> uncontrollableActions;
	Set<A> controllableActions;
	Set<A> endActions;
	
	public GroupedActions(Set<A> actions, Set<A> controllableActions) {
		SchedulerUtils<A> su = new SchedulerUtils<A>();
		this.uncontrollableActions = new HashSet<A>();
		this.endActions = new HashSet<A>();
		this.controllableActions = controllableActions;
		for(A action : actions){
			ChoiceType type = su.getChoiceType(action, controllableActions);
			if(type.equals(ChoiceType.UNCONTROLLABLE))
				this.uncontrollableActions.add(action);
			if(type.equals(ChoiceType.ENDS)){
				this.endActions.add(action);
			}
		}
	}

	public Set<A> getUncontrollableActions() {
		return uncontrollableActions;
	}

	public Set<A> getEndActions() {
		return endActions;
	}

	public Set<A> getControllableActions() {
		return controllableActions;
	}
}
