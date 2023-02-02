package MTSSynthesis.controller.gr.time.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ActivityDefinitions<A> {

	private Set<Activity<A>> activities;
	private Map<A,Activity<A>> relatedActivity;
	private Set<A> initiatingActions;
	private Set<A> terminatingActions;
	
	public ActivityDefinitions(Set<Activity<A>> activities) {
		this.activities = activities;
		this.initiatingActions = new HashSet<A>();
		this.terminatingActions = new HashSet<A>();
		this.relatedActivity = new HashMap<A,Activity<A>>();
		for (Activity<A> activity : activities) {
			for(A a: activity.getInitiatingActions()){
				relatedActivity.put(a, activity);
				initiatingActions.add(a);
			}
			for(A a: activity.getTerminatingActions()){
				relatedActivity.put(a, activity);
				terminatingActions.add(a);
			}
		}
	}
	
	
	public boolean isIntitiatingAction(A a){
		return initiatingActions.contains(a);
	}
	
	public boolean isTerminatingAction(A a){
		return terminatingActions.contains(a);
	}
	
	public boolean hasRelatedActions(A a){
		return this.getRelatedActions(a) != null && this.getRelatedActions(a).size() > 1;
	}
	
	public Set<A> getRelatedActions(A a){
		Activity<A> activity = relatedActivity.get(a);
		if(activity != null)
			if(initiatingActions.contains(a))
				return activity.getInitiatingActions();
			else
				return activity.getTerminatingActions();
		else
			return null;
	}
	
	public Activity<A> getAssociatedActivity(A a) {
		return relatedActivity.get(a);
	}
	
	public Set<Activity<A>> getActivities() {
		return activities;
	}
	
	@Override
	public String toString() {
		return new StringBuilder().append("[Activities: ").append(activities)
				.append(", starts: ").append(initiatingActions)
				.append(", ends: ").append(terminatingActions)
				.append("]").toString();
	}
}
