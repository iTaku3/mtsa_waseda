package MTSSynthesis.controller.parity;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;

import MTSSynthesis.controller.model.Rank;

public class ParityRank implements Rank {
	private List<Integer> values;
	private Boolean isInfinity;
	
	public ParityRank(int statePriority) {
		this.values = new ArrayList<Integer>();
		int i = 1;
		while (i<=statePriority) {
			this.values.add(0);
			i=+2;
		}
		isInfinity=false;
		
	}

	@Override
	public int compareTo(Rank aRank) {
		if (this.equals(aRank)) {
			return 0;
		}
		boolean instance = aRank instanceof ParityRank;
		Validate.isTrue(instance, "Ranks are not comparable. ["+ this + ", " + aRank + "]");
		ParityRank aParityRank = (ParityRank) aRank;
		int length = Math.min(this.values.size(), aParityRank.size());
		int i = 0;
		
		while (i<length) {
			if (this.getValue(i) > aParityRank.getValue(i)) {
				return 1;
			} else if (this.getValue(i) < aParityRank.getValue(i)) {
				return -1;
			}
			i++;
		}
		while (i<this.values.size()) {
			if (this.getValue(i) > 0)
				return 1;
			i++;
		}
		while (i<aParityRank.size()) {
			if (this.getValue(i) > 0)
				return -1;
			i++;
		}
		return 0;
	}

	private Integer getValue(int index) {
		return this.values.get(index);
	}
	
	public Integer size() {
		return this.values.size();
	}

	public void increase(Integer priority, ParityRankSystem<?> rankSystem) {
		int index = (priority+1)/2;
		while (this.size()>index) {
			this.values.remove(index);
		}
		while (this.size()<index) {
			this.values.add(0);
		}
		
		int currentIndex = index;
		while (currentIndex > 0) {
			if (this.getValue(currentIndex) < rankSystem.max(currentIndex)) {
				this.values.set(currentIndex, this.values.get(currentIndex)+1);
				return;
			} else {
				this.values.set(currentIndex, 0);
				currentIndex--;
			}
		}
		this.setToInfinity(); 
	}

	@Override
	public boolean isInfinity() {
		return isInfinity; 
	}

	public void truncate(Integer priority) {
		for (int i = (priority+1)/2; i < this.values.size(); i++) {
			this.values.remove(i);
		}
	}

	@Override
	public void setToInfinity() {
		this.isInfinity = true;
	}

	@Override
	public void increase() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void set(Rank rank) {
		if (! (rank instanceof ParityRank) ) {
			throw new IllegalArgumentException("Setting a parity rank with a " 
					+ rank.getClass() + " type of rank.");
		}
		ParityRank newRank = (ParityRank) rank;
		this.isInfinity = newRank.isInfinity();
		this.values.clear();
		for (Integer value : newRank.values) {
			this.values.add(value);
		}
	}
}