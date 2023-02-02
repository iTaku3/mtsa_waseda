package MTSSynthesis.controller.gr.time.model;

import java.util.Set;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.RealExpr;

public class TimedTrace {
	String label;
	BoolExpr condition;
	Set<RealExpr> resetedClocks;
	
	public TimedTrace(BoolExpr condition, Set<RealExpr> resetedClocks, String label){ 
		if(condition == null)
			throw new RuntimeException();
		this.label = label;
		this.condition = condition;
		this.resetedClocks =resetedClocks;
	}
	
	public String getLabel() {
		return label;
	}
	
	public BoolExpr getCondition() {
		return condition;
	}
	
	public Set<RealExpr> getResetedClocks() {
		return resetedClocks;
	}
	
	@Override
	public boolean equals(Object obj) {
		TimedTrace other = (TimedTrace) obj;
		return this.label.equals(other.label)&&
				this.condition.equals(other.condition)&&
				this.resetedClocks.equals(other.resetedClocks);
	}
	
	@Override
	public String toString() {
		return "<Label: "+label + ", Condition: " + condition +", Clocks: "+ resetedClocks.toString()+">";
	}
}
