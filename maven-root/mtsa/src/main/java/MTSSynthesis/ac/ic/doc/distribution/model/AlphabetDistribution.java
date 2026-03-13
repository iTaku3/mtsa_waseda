package MTSSynthesis.ac.ic.doc.distribution.model;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections15.SetUtils;
import org.apache.commons.collections15.set.UnmodifiableSet;
import org.apache.commons.lang.Validate;

/**
 * @author gsibay
 *
 */
public class AlphabetDistribution<A> {

	private Set<Set<A>> alphabets;
	private Set<A> fullAlphabet;
	
	public Set<Set<A>> getAlphabets() {
		return alphabets;
	}
	
	public Set<A> getFullAlphabet() {
		return this.fullAlphabet;
	}

	private void setAlphabets(Set<Set<A>> alphabets) {
		this.alphabets = SetUtils.unmodifiableSet(alphabets);
	}

	public Set<Set<A>> getAlphabetsContainingAction(A action) {
		HashSet<Set<A>> result = new HashSet<Set<A>>();
		for (Set<A> alphabet : alphabets) {
			if(alphabet.contains(action)) {
				result.add(alphabet);
			}
		}
		return result;
	}
	
	/**
	 * Creates an alphabet distribution 
	 * @param alphabets
	 */
	public AlphabetDistribution(Set<Set<A>> alphabets) {
		Validate.notEmpty(alphabets);
		Set<Set<A>> componentAlphabets = new HashSet<Set<A>>();
		this.fullAlphabet = new HashSet<A>();
		for (Set<A> alphabet : alphabets) {
			Validate.notEmpty(alphabet);
			componentAlphabets.add(UnmodifiableSet.decorate(alphabet));
			this.fullAlphabet.addAll(alphabet);
		}
		componentAlphabets = UnmodifiableSet.decorate(componentAlphabets);
		this.fullAlphabet = UnmodifiableSet.decorate(this.fullAlphabet);
		this.setAlphabets(componentAlphabets);
	}

	public String toString() {
		return this.alphabets.toString();
	}
}
