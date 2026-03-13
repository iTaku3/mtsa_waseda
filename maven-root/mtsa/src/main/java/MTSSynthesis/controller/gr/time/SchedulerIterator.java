package MTSSynthesis.controller.gr.time;

import MTSSynthesis.controller.gr.time.model.Choice;
import MTSSynthesis.controller.gr.time.model.Scheduler;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.impl.LTSAdapter;

import java.util.*;

public class SchedulerIterator<S,A> extends RandomStrategyIterator<S, A>{
	private Map<A,Integer> restrictions;
	private ArrayList<Set<A>> relatedActions;
	public SchedulerIterator(MTS<S, A> mts, Set<A> controllableActions, Set<S> finalStates, ArrayList<Set<A>> relatedActions) {
		restrictions = new HashMap<A,Integer>();
		this.relatedActions = relatedActions;
		for (int i=0; i< relatedActions.size();i++) {
			Set<A> set = relatedActions.get(i);
			for (A a : set) {
				restrictions.put(a, i);
			}
		}
		super.init(mts, controllableActions, finalStates);
		choicesBuilder = new RestrictedSchedulerChoicesBuilder<S,A>(new LTSAdapter<S,A>(mts, MTS.TransitionType.REQUIRED),controllableActions,finalStates);
	}

	@Override
	protected Map<S , List<Choice<A>>> getChoices() {
		return choicesBuilder.getAllChoices();
	}

	@Override
	protected boolean compatible(Scheduler<S, A> sl, Scheduler<S, A> sc) {
		return (super.compatible(sl, sc) && consistentUncontrollable(sl.getUncontrollableChoices(), sc.getUncontrollableChoices()));
	}

	private boolean consistentUncontrollable(Set<A> ucl, Set<A> ucc) {
		for(A u :ucl){
			if(restrictions.containsKey(u)){
				for (A u2: ucc) {
					if(relatedActions.get(restrictions.get(u)).contains(u2) && !u2.equals(u)){
						return false;
					}
				}
			}
		}
		return true;
	}
	
	public void setIter(int i){
		validateIndex(i);
		iter = i;
	}
	
	public Scheduler<S, A> get(int next) {
		validateIndex(next);
		return schedulers.get(next);
	}
	private void validateIndex(int next) {
		if(next >= schedulers.size()){
			throw new RuntimeException("Index out of bound for the Strategy Iterator");
		}
	}
	
}
