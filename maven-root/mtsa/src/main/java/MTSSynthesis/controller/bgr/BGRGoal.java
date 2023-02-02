package MTSSynthesis.controller.bgr;

import java.util.Set;

import MTSSynthesis.controller.model.Assumptions;
import MTSSynthesis.controller.model.Guarantees;
import MTSSynthesis.controller.model.gr.GRGoal;

public class BGRGoal<State> extends GRGoal<State>{

	private Set<State> buchi;

	public BGRGoal(Guarantees<State> guarantees,
                   Assumptions<State> assumptions, Set<State> faults,
                   Set<State> buchi) {
		super(guarantees, assumptions, faults, false);
		this.setBuchi(buchi);
	}

	private void setBuchi(Set<State> buchi) {
		this.buchi = buchi;
	}
	
	public Set<State> getBuchi() {
		return this.buchi;
	}
	
	public int livenessSize() {
		return this.getGuaranteesQuantity()+1;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer()
		.append("Buchi goal: ").append(buchi);
		return sb.toString();
	}

}
