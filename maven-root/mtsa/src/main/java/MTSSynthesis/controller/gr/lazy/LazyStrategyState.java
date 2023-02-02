package MTSSynthesis.controller.gr.lazy;

import MTSSynthesis.controller.gr.StrategyState;

public class LazyStrategyState<State,Memory> extends StrategyState<State, Memory>{
	
	private Integer lazyness;
	
	public LazyStrategyState(State state, Memory memory) {
		super(state, memory);
		this.lazyness = 0;
	}
	
	public LazyStrategyState(State state, Memory memory, int lazyness) {
		super(state, memory);
		this.lazyness = lazyness;
	}
	
	public Integer getLazyness() {
		return lazyness;
	}
	
	public void decrementLazyness(){
		if(lazyness > 0)
			lazyness--;
	}
	
	public boolean isMinimum(){
		return lazyness == 0;
	}
	
	
	@Override
	public boolean equals(Object arg0) {
		if (this == arg0) {
			return true;
		}
		if (arg0 instanceof StrategyState) {
			@SuppressWarnings("unchecked")
			LazyStrategyState<State,Memory> other = (LazyStrategyState<State, Memory>) arg0;
			return this.getState().equals(other.getState()) 
					&& this.getMemory().equals(other.getMemory())
					&& this.getLazyness().equals(other.getLazyness());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}

	@Override
	public String toString() {
		return "<" + this.getState() + ", " + this.getMemory() +", " +this.getLazyness() +">";
	}
	
	
}
