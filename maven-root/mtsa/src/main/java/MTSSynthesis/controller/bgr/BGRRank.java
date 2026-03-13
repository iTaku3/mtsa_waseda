package MTSSynthesis.controller.bgr;

import MTSSynthesis.controller.model.Rank;
import org.apache.commons.lang.Validate;

import MTSSynthesis.controller.gr.GRRank;

public class BGRRank extends GRRank {

	private int bValue;
//	private BGRRankContext context;

	public BGRRank(BGRRankContext context) {
		super(context);
//		this.context = context;
	}
	
	public BGRRank(BGRRankContext context, int gValue, int assume, int bValue) {
		super(context, gValue, assume);
		this.bValue = bValue;
//		this.context = context;
	}
	

	public static BGRRank getInfinityFor(BGRRankContext ctx) {
		BGRRank rank = new BGRRank(ctx);
		rank.setToInfinity();
		return rank;
	}

	// Should there be an increase that immediately increases the super.
	// But this can also be done by increasing the bValue to max and then call
	// increase

	/**
	 * This increases the Buchi part of the rank first, and when it reaches 
	 * the maximum it increases the GR part and resets the Buchi part.  
	 */
	@Override
	public void increase() {
		if (bValue < this.getContext().getBHeight()) {
			bValue++;
		} else {
			this.increaseGR();
		}
	}
	
	/**
	 * This increases the GR part of the rank and resets the Buchi part. 
	 * Since an assumption was found the count of buchi states 
	 * while not in assumption must be reset, and the number of assumptions 
	 * seeing before a liveness goal must increase.  
	 */
	public void increaseGR() {
		super.increase();
		this.resetBuchi();
	}
	

	public void resetBuchi() {
		bValue = 0;
	}

	@Override
	public void set(Rank rank) {
		boolean instance = rank instanceof BGRRank;
		Validate.isTrue(instance, "Rank of type " + rank.getClass()
				+ " cannot be asigned to BGRRank");
		BGRRank newRank = (BGRRank) rank;
		this.set(newRank.getValue(), newRank.getAssume());
		this.bValue = newRank.bValue;
	}

	@Override
	protected BGRRankContext getContext() {
		// TODO Auto-generated method stub
		return (BGRRankContext) super.getContext();
	}
	
	@Override
	public int compareTo(Rank other) {
		if (this.equals(other)) {
			return 0;
		}
		boolean instance = other instanceof BGRRank;
		Validate.isTrue(instance, "Ranks are not comparable." + "[" + this
				+ ", " + other + "]");
		BGRRank otherRank = (BGRRank) other;
		int compareTo = super.compareTo(other);
		if (compareTo != 0) {
			return compareTo;
		} else {
			return bValue - otherRank.bValue;
		}
	}

	@Override
	public boolean equals(Object anObject) {
		if (super.equals(anObject)) {
			if (anObject instanceof BGRRank) {
				BGRRank aBGRRank = (BGRRank) anObject;
				return this.bValue == aBGRRank.bValue;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return "[gvalue:" + this.getValue()  + ", assume:" + this.getAssume() + ", bvalue:" + this.bValue + "]";
	}

}
