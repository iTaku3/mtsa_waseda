package MTSSynthesis.controller.gr;

import MTSSynthesis.controller.model.RankContext;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class GRRankContext implements RankContext {

	private int height;
	private int width;

	public GRRankContext(int height, int width) {
		this.height = height;
		this.width = width;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see controller.game.gr.RankContext#inRange(int, int)
	 */
	@Override
	public boolean inRange(int value, int assume) {
		return ((value >= 0 && value <= this.height) && (assume > 0 && assume <= this.width) && (value < this.height || assume == 1));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see controller.game.gr.RankContext#getHeight()
	 */
	public int getHeight() {
		return this.height;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see controller.game.gr.RankContext#getWidth()
	 */
	public int getWidth() {
		return this.width;
	}

	@Override
	public String toString() {
		return "[width:" + this.width + ", height:" + this.height + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		GRRankContext grRankContext = (GRRankContext) obj;

		return new EqualsBuilder().append(this.width, grRankContext.getWidth()).append(this.height, grRankContext.getHeight()).isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder(19, 37).append(this.width).append(this.height).toHashCode();
	}
}
