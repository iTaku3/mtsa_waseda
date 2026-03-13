package MTSSynthesis.controller.gr;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class StrategyState<S, M>{
	
	private M memory;
	private S state;
	private Integer lazyness;
	
	public StrategyState(S state,M memory) {
		this.memory = memory;
		this.state = state;
		this.lazyness = 0;
	}
	
	public StrategyState(S state,M memory, Integer lazyness) {
		this.memory = memory;
		this.state = state;
		this.lazyness = lazyness;
	}
	public M getMemory() {
		return this.memory;
	}

	public S getState() {
		return this.state;
	}
	
	public Integer getLazyness() {
		return lazyness;
	}
	
	@Override
	public boolean equals(Object arg0) {
		if (this == arg0) {
			return true;
		}
		if (arg0 instanceof StrategyState) {
			@SuppressWarnings("unchecked")
			StrategyState<S,M> other = (StrategyState<S, M>) arg0;
			return new EqualsBuilder()
            .append(this.getState(), other.getState())
            .append(this.getMemory(),other.getMemory())
            .append(this.getLazyness(), other.getLazyness())
            .isEquals();
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
        .append(this.getState())
        .append(this.getMemory())
        .append(this.getLazyness())
        .toHashCode();
	}

	@Override
	public String toString() {
		return "<" + this.getState() + ", " + this.getMemory() +", " +this.getLazyness() +">";
	}
}
