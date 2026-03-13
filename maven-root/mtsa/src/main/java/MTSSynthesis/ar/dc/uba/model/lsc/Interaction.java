package MTSSynthesis.ar.dc.uba.model.lsc;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;


/**
 * 
 * @author gsibay
 *
 */
public class Interaction implements Location {

	private String source;
	private String target;
	private String message;
	
	public Interaction(String source, String message, String target) {
		this.source = source;
		this.target = target;
		this.message = message;
	}
	
	public String getSource() {
		return source;
	}

	public String getTarget() {
		return target;
	}

	public String getMessage() {
		return message;
	}
	
	public String toString() {
		return this.getSource() + " -> " + this.getMessage() + " -> " + this.getTarget();
	}

	public Set<String> getInstances() {
		Set<String> instances = new HashSet<String>();
		instances.add(this.source);
		instances.add(this.target);
		return instances;
	}

	public boolean equals(Object anObject) {
		if(this == anObject) {
			return true;
		}
		if(anObject instanceof Interaction) {
			Interaction interaction = (Interaction) anObject;
			return new EqualsBuilder().append(this.getSource(),
					interaction.getSource()).append(this.getMessage(),
					interaction.getMessage()).append(this.getTarget(),
					interaction.getTarget()).isEquals();
			
		} else {
			return false;
		}
	}
	
	public int hashCode() {
		return new HashCodeBuilder(13, 33).append(this.getSource()).append(
				this.getMessage()).append(this.getTarget()).toHashCode();
	}

	@Override
	public String getName(LocationNamingStrategy locationNamingStrategy) {
		return locationNamingStrategy.calculateName(this);
	}

}