package MTSSynthesis.controller.gb;


public class GBRankContext /* implements RankContext */ {

	private int height;
	
	public GBRankContext(int height) {
		this.height = height;
	}
	
	/* (non-Javadoc)
	 * @see controller.game.gr.RankContext#inRange(int, int)
	 */
	public boolean inRange(int value) {
		return (value >= 0 && value <= this.height); 
	}

	/* (non-Javadoc)
	 * @see controller.game.gr.RankContext#getHeight()
	 */
	public int getHeight() {
		return this.height;
	}

	@Override
	public String toString() {
		return "[height:" + this.height + "]";
	}

}
