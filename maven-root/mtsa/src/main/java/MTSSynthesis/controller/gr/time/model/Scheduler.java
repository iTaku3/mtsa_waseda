package MTSSynthesis.controller.gr.time.model;

import java.util.HashSet;
import java.util.Set;

import MTSSynthesis.controller.gr.time.StrategyIterator;

public class Scheduler<S,A> extends Chooser<S,A> {
	private Set<A> uncontrollableChoices;

	public Scheduler(StrategyIterator<S, A> strategyIterator) {
		super(strategyIterator);
		this.setUncontrollableChoices(new HashSet<A>());
	}
	
	@Override
	public void setChoice(S s,Choice<A> c){
		super.setChoice(s, c);
		if(this.iterator.getUncontrollableActions().containsAll(c.getChoice())){
			getUncontrollableChoices().addAll(c.getChoice());
		}else if(c.hasAlternative() && this.iterator.getUncontrollableActions().containsAll(c.getAlternative())){
			getUncontrollableChoices().addAll(c.getAlternative());
		}
	}

	public Set<A> getUncontrollableChoices() {
		return uncontrollableChoices;
	}

	public void setUncontrollableChoices(Set<A> uncontrollableChoices) {
		this.uncontrollableChoices = uncontrollableChoices;
	}
}
