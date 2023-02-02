package MTSSynthesis.controller.gr.concurrency;

import MTSSynthesis.controller.model.Rank;
import org.apache.commons.lang.Validate;

import MTSSynthesis.controller.gr.GRRank;

public class DoubleRank implements Rank {
	
	private double value;
	private DoubleRankContext context;
	
	public DoubleRank(double d, DoubleRankContext context) {
		this.value = d;
		this.context = context;
	}

	@Override
	public int compareTo(Rank other) {
		if (this.equals(other)) {
			return 0;
		}
		boolean instance = other instanceof DoubleRank;
		Validate.isTrue(instance, "Ranks are not comparable. ["+ this + ", " + other + "]");
		DoubleRank cOther = (DoubleRank) other; 
			if (this.value < cOther.getValue()) {
				return -1;
			} else if (this.value > cOther.getValue()) {
				return 1;
			}  else  { 
				return 0;
			} 
	}
	
	
	@Override
	public boolean equals(Object anObject) {
		if(this == anObject) {
			return true;
		}
		if(anObject instanceof GRRank) {
			DoubleRank cRank = (DoubleRank) anObject;
			return this.value==cRank.getValue();
		} else {
			return false;
		}
	}

	public double getValue() {
		return this.value;
	}

	@Override
	public boolean isInfinity() {
		return value==(this.context.getMax());
	}

	@Override
	public void increase() {
		this.value = this.value + 1;
	}

	@Override
	public void setToInfinity() {
		this.value = this.context.getMax();
	}

	@Override
	public void set(Rank rank) {
		boolean instance = rank instanceof DoubleRank;
		Validate.isTrue(instance, "Ranks are not comparable. ["+ this + ", " + rank + "]");
		DoubleRank cRank = (DoubleRank) rank; 
		this.set(cRank.getValue());
	}


	private void set(double value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return "[value:" + this.value + "]";
	}

}
