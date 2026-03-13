package ltsa.lts.chart;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * It's the Condition used
 * in a basic chart. 
 * @author gsibay
 *
 */
public class ConditionLocation implements Location {

	// Set of instances that the condition affects
	private Set<String> instances;

	private String id;
	
	public ConditionLocation(String id, Set<String> instances) {
		this.id = id;
		this.instances = Collections.unmodifiableSet(new HashSet<String>(instances));
	}

	public Set<String> getInstances() {
		return instances;
	}
	
	public String getId() {
		return id;
	}
	
	public String toString() {
		return this.id + ": " + this.getInstances().toString();
	}
}
