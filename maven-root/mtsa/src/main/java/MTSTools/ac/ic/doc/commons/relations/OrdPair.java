package MTSTools.ac.ic.doc.commons.relations;


public class OrdPair<T1 extends Comparable<T1>, T2 extends Comparable<T2>>
		extends Pair<T1,T2>	implements Comparable<OrdPair<T1,T2>> {

	public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>>
	OrdPair<T1,T2> create(T1 first, T2 second) {
		return new OrdPair<T1,T2>(first, second);
	}
	
	public OrdPair(T1 first, T2 second) {
		super(first, second);
	}

	@Override
	public int compareTo(OrdPair<T1, T2> other) {
		int result = this.getFirst().compareTo(other.getFirst());
		if (result == 0)
			result = this.getSecond().compareTo(other.getSecond());
		return result;
	}

}
