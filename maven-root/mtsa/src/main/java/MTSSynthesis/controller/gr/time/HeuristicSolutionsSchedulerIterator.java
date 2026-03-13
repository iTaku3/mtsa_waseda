package MTSSynthesis.controller.gr.time;

import MTSSynthesis.controller.gr.time.model.Choice;
import MTSTools.ac.ic.doc.mtstools.model.MTS;

import java.util.List;
import java.util.Map;
import java.util.Set;


public class HeuristicSolutionsSchedulerIterator<S,A> extends StrategyIterator<S,A> {
	private ChoicesBuilder<S,A> choicesBuilder;

	public HeuristicSolutionsSchedulerIterator(MTS<S, A> mts, Set<A> controllableActions, Set<S> finalStates) {
		super.init(mts, controllableActions, finalStates);
	}
	
	@Override
	protected Map<S , List<Choice<A>>> getChoices() {
		return choicesBuilder.getAllChoices();
	}

}
