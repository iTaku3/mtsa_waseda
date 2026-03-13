package MTSSynthesis.ar.dc.uba.model.structure;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import MTSSynthesis.ar.dc.uba.model.language.Word;

/**
 * @author gsibay
 *
 */
public class ObligationsFromExistential implements Obligations {

	private Set<Word> requiredPaths;
	
	public ObligationsFromExistential(Set<Word> requiredPaths) {
		this.requiredPaths = Collections.unmodifiableSet(new HashSet<Word>(requiredPaths));
	}
	
	public Set<Word> getRequiredPaths() {
		return requiredPaths;
	}

	/* (non-Javadoc)
	 * @see ar.dc.uba.model.structure.Obligations#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return this.requiredPaths.isEmpty();
	}

	public String toString() {
		return this.getRequiredPaths().toString();
	}
	
	
	public boolean equals(Object anObject) {
		if (this == anObject) {
			return true;
		}
		if (anObject instanceof ObligationsFromExistential) {
			ObligationsFromExistential obl = (ObligationsFromExistential) anObject;
			return (new EqualsBuilder()).append(obl.getRequiredPaths(), this.getRequiredPaths()).isEquals();
		} else {
			return false;
		}
	}

	public int hashCode() {
		return new HashCodeBuilder(17, 39).append(this.getRequiredPaths()).toHashCode();
	}
}
