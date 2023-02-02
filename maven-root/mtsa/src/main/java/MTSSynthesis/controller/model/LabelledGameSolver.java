package MTSSynthesis.controller.model;

public interface LabelledGameSolver<S, A, M> extends GameSolver<S, M> {
	public LabelledGame<S,A> getLabelledGame();
}
