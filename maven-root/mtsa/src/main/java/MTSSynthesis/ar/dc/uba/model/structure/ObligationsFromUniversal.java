package MTSSynthesis.ar.dc.uba.model.structure;

import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import MTSSynthesis.ar.dc.uba.model.language.Word;

/**
 * @author gsibay
 *
 */
public class ObligationsFromUniversal implements Obligations {

	private Set<Word> requiredPaths;

	private Set<Word> maybePaths;

	public ObligationsFromUniversal(Set<Word> requiredPaths, Set<Word> possiblePaths) {
		this.requiredPaths = requiredPaths;
		this.maybePaths = possiblePaths;
	}
	
	/* (non-Javadoc)
	 * @see ar.dc.uba.model.structure.Obligations#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return this.requiredPaths.isEmpty() && this.maybePaths.isEmpty();
	}

	public Set<Word> getRequiredPaths() {
		return requiredPaths;
	}

	public Set<Word> getMaybePaths() {
		return maybePaths;
	}

	public String toString() {
		StringBuffer buff = new StringBuffer();
		buff.append("Req Paths = ").append(this.requiredPaths)
			.append("May Paths = ").append(this.maybePaths);
		return buff.toString();
	}
	
	public boolean equals(Object anObject) {
		if (this == anObject) {
			return true;
		}
		if (anObject instanceof ObligationsFromUniversal) {
			ObligationsFromUniversal obl = (ObligationsFromUniversal) anObject;
			return (new EqualsBuilder()).append(obl.getRequiredPaths(), this.getRequiredPaths())
				.append(obl.getMaybePaths(), this.getMaybePaths()).isEquals();
		} else {
			return false;
		}
	}

	public int hashCode() {
		return new HashCodeBuilder(17, 39).append(this.getRequiredPaths()).append(this.getMaybePaths()).toHashCode();
	}
}
