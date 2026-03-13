package MTSSynthesis.controller.gr.time;

import MTSSynthesis.controller.gr.time.model.Choice;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.impl.LTSAdapter;

import java.util.List;
import java.util.Map;
import java.util.Set;


public class ControllableSchedulerIterator<S,A> extends StrategyIterator<S,A> {
	private ChoicesBuilder<S,A> choicesBuilder;

	public ControllableSchedulerIterator(MTS<S, A> mts, Set<A> controllableActions, Set<S> finalStates){
		super(mts, controllableActions, finalStates);
		this.choicesBuilder = new ControllerChoicesBuilder<S,A>(new LTSAdapter(mts, MTS.TransitionType.REQUIRED), controllableActions, finalStates);
	}
	
	@Override
	public Map<S , List<Choice<A>>> getChoices() {
		return choicesBuilder.getAllChoices();
	}
}
