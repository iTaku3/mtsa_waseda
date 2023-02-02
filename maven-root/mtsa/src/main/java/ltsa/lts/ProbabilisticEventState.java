package ltsa.lts;

import java.math.BigDecimal;

public class ProbabilisticEventState extends EventState {
	private int bundle;
	private BigDecimal prob;
	ProbabilisticEventState probTr;
	// probTr: probabilistic transitions on same bundle
	// nondet: other bundles with same event
	// list: all other bundles with other events
	
	public ProbabilisticEventState(int e, int i) {
		super(e, i);
	}

	public ProbabilisticEventState(int e, int i, BigDecimal prob, int bundle) {
		super(e, i);
		this.prob= prob;
		this.bundle= bundle;
	}
	
	public void setBundle(int bundle) {
		this.bundle= bundle;
	}
	
	public void setProbability(BigDecimal prob) {
		this.prob= prob;
	}
	
	public int getBundle() {
		return bundle;
	}
	
	public BigDecimal getProbability() {
		return prob;
	}
	
	public EventState getBundleTransitions()
	{
	  return probTr;
	}

	public static ProbabilisticEventState clone(ProbabilisticEventState anEventState) {
		ProbabilisticEventState aCopy = (ProbabilisticEventState) EventState.copy(anEventState);

		aCopy.setBundle(anEventState.bundle);
		aCopy.setProbability(anEventState.prob);

		return aCopy;
	}
}
