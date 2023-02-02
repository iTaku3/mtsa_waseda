package MTSSynthesis.controller.model;

import java.util.Set;

/**
 * 
 * @author srdipi
 *
 * @param <S> states in the original game
 * @param <M> is the memory structure required to solve the game 
 */
public interface GameSolver<S, M> {

	public abstract void solveGame();
	public abstract Set<S> getWinningStates();
	public abstract boolean isWinning(S state);
	public abstract Strategy<S,M> buildStrategy();	
	public abstract Game<S> getGame();
	
}