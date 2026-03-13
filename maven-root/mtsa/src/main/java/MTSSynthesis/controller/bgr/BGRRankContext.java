package MTSSynthesis.controller.bgr;

import MTSSynthesis.controller.gr.GRRankContext;

public class BGRRankContext extends GRRankContext{

	private int bHeight;

	public BGRRankContext(int gHeight, int gWidth, int bHeight) {
		super(gHeight, gWidth);
		this.bHeight = bHeight;
	}

	public int getBHeight() {
		return bHeight;
	}

	@Override
	public String toString() {
		return "[gheight:" + this.getHeight() + ", width:" + this.getWidth() + ", bheight:" + this.getBHeight() + "]";
	}


}
