package MTSSynthesis.controller.model.gr.concurrency;

import java.util.Set;

import MTSSynthesis.controller.model.Assumptions;
import MTSSynthesis.controller.model.Guarantees;
import MTSSynthesis.controller.model.gr.GRGoal;

public class GRCGoal<State> extends GRGoal<State> {

	private ConcurrencyLevel<State> concurrency;
	
	public GRCGoal(Guarantees<State> guarantees,
				   Assumptions<State> assumptions, Set<State> faults,
				   boolean permissive, ConcurrencyLevel<State> concurrency) {
		super(guarantees, assumptions, faults, permissive);
		this.concurrency = concurrency;
	}
	
	public ConcurrencyLevel<State> getConcurrencyInformation() {
		return this.concurrency;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer()
		.append(super.toString())
		.append("Concurrency: ").append(this.getConcurrencyInformation());
		return sb.toString();
	}

}
