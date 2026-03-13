package MTSTools.ac.ic.doc.commons.relations;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class Pair<E1,E2>{ 
	
	protected E1 first;

	protected E2 second;
	
	public Pair(E1 first, E2 second) {
		this.first = first;
		this.second = second;
	}

	public E1 getFirst() {
		return first;
	}

	public E2 getSecond() {
		return second;
	}
	
	public static <S1,S2> Pair<S1,S2> create(S1 first, S2 second) {
		return new Pair<S1,S2>(first,second);
	}
	
	public String toString() {
		return "(" + this.first + "," + this.second + ")";
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Pair) ) {
			return false;		
		}
		
		@SuppressWarnings("unchecked")
		Pair<E1, E2> rhs = (Pair<E1, E2>) obj;
		
		return new EqualsBuilder()
			.append(this.getFirst(), rhs.getFirst())
			.append(this.getSecond(),rhs.getSecond())
			.isEquals();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(this.getFirst())
			.append(this.getSecond())
			.toHashCode();
	}
	
	
	
}