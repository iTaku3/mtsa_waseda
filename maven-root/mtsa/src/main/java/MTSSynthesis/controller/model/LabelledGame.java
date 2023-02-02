package MTSSynthesis.controller.model;


public interface LabelledGame<State, Action> extends Game<State> {
	public abstract Action getLabel(State outgoing, State incoming);
	
	public abstract void addControllableSuccessor(State state1, Action label, State state2);

	public abstract void addUncontrollableSuccessor(State predecessor, Action label, State successor);
	
}
