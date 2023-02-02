package MTSSynthesis.controller.gr.time.model;

import java.util.Set;

import org.apache.commons.lang.builder.HashCodeBuilder;

public class Activity<A> {

	private String name;
	private Set<A> initiatingActions;
	private Set<A> terminatingActions;
	
	public Activity(String name, Set<A> initiatingActions, Set<A> terminatingActions) {
		this.name = name;
		this.initiatingActions = initiatingActions;
		this.terminatingActions = terminatingActions;
	}
	
	boolean containsAction(A a){
		return initiatingActions.contains(a) || terminatingActions.contains(a);
	}
	
	Set<A> getInitiatingActions(){
		return initiatingActions;
	}
	
	Set<A> getTerminatingActions(){
		return terminatingActions;
	}
	
	public String getName(){
		return name;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Activity<?>){
			Activity<?> other = (Activity<?>) obj;
			return this.getName().equals(other.getName()) &&
					this.getInitiatingActions().equals(other.getInitiatingActions()) &&
					this.getTerminatingActions().equals(other.getTerminatingActions()) ; 
		}else{
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return	new HashCodeBuilder()
				.append(this.getName())
				.append(this.getTerminatingActions())
				.append(this.getInitiatingActions())
				.toHashCode();
	}

	@Override
	public String toString() {
		return new StringBuilder().append("[Activity: ").append(name)
				.append(", starts: ").append(initiatingActions)
				.append(", ends: ").append(terminatingActions)
				.append("]").toString();
	}
}
