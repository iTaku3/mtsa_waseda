package MTSSynthesis.controller.gb;


public class GBRank /* implements Rank */ {
	public static final int DEFAULT_INITIAL_HEIGHT = 0;

	private int value;
	private GBRankContext context;
	
	/* (non-Javadoc)
	 * @see controller.game.gr.Rank#init(controller.game.gr.GRRankContext)
	 */
	public void init(GBRankContext context) {
		this.context = context;
	}

	public GBRank(GBRankContext context) {
		this(context, DEFAULT_INITIAL_HEIGHT);
		this.context = context;
	}
	
	public GBRank(GBRankContext context, int value) {
		this.context = context;
		if (this.context.inRange(value)) {
			this.value=value;
		} else {
			throw new RuntimeException("value is out of range.");
		}
	}
	
//	static public void setHeight(int height) { // this function should be available to set the height before
//		                                             // any rank is available
//		Rank.height = height;
//	}
//	
//	static public void setWidth(int width) { // this function should be available to set the width before
//		                                              // any rank is available
//		Rank.width = width;
//	}
	
	/* (non-Javadoc)
	 * @see controller.game.gr.Rank#increase()
	 */
	public void increase() {
		if (this.context.getHeight() == value) {
			return;
		} else
			value++;
	}

	public void setToInfinity() {
		value = this.context.getHeight();
	}
	
	/* (non-Javadoc)
	 * @see controller.game.gr.Rank#reset()
	 */
	public void reset() {
		value = 0;
	}
	
	public void set(int value) {
		this.value = value;
	}

	public void set(GBRank rank) {
		this.set(rank.getValue());
	}

	/* (non-Javadoc)
	 * @see controller.game.gr.Rank#isInfinity()
	 */
	public boolean isInfinity() {
		return value==(this.context.getHeight());
	}
	
	/* (non-Javadoc)
	 * @see controller.game.gr.Rank#getValue()
	 */
	public int getValue() {
		return value;
	}
		
	/* (non-Javadoc)
	 * @see controller.game.gr.Rank#compareTo(controller.game.gr.GRRank)
	 */
	public int compareTo(GBRank other) {
		if (this.value < other.getValue()) {
			return -1;
		} else if (this.value > other.getValue()) {
			return 1;
		}  else  { // this.equals(other)
			return 0;
		} 
	}

	@Override
	public boolean equals(Object anObject) {
		if(this == anObject) {
			return true;
		}
		if(anObject instanceof GBRank) {
			GBRank gbRank = (GBRank) anObject;
			return this.value==gbRank.getValue();
		} else {
			return false;
		}
	}
	
	
	@Override
	public String toString() {
		return "[value:" + this.value + "]";
	}

	public static GBRank getInfinityFor(GBRankContext ctx) {
		GBRank rank = new GBRank(ctx);
		rank.setToInfinity();
		return rank;
	}
}
