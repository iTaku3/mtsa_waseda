/**
 * 
 */
package MTSTools.ac.ic.doc.mtstools.model.impl;

import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;

/**
 * This class represents a transition as an entity with destination state
 * <code>to</code>, an action <code>action</code> and a type
 * <code>type</code>.
 * 
 * @author srdipi
 * TODO el compositionState aca no esta nada bueno y ademas no queda claro para que esta aqui
 * @param <Action>
 */
public class Transition<Action> {
	private Long to;
	private Action action;
	private TransitionType type;
	private CompositionState compositionState; 

	public Transition(Long to, Action action, TransitionType type) {
		super();
		this.to = to;
		this.action = action;
		this.type = type;
	}

	/**
	 * @return the action
	 */
	public Action getAction() {
		return action;
	}

	/**
	 * @param action
	 *            the action to set
	 */
	protected void setAction(Action action) {
		this.action = action;
	}

	/**
	 * @return the to
	 */
	public Long getTo() {
		return to;
	}

	/**
	 * @param to
	 *            the to to set
	 */
	protected void setTo(Long to) {
		this.to = to;
	}

	/**
	 * @return the type
	 */
	protected TransitionType getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	protected void setType(TransitionType type) {
		this.type = type;
	}

	public String toString() {
		return new StringBuffer().append(
				"To: " + this.getTo() + " - Action: " + this.getAction() + " - Type: " + this.getType()).toString();
	}

	/**
	 * @return the compositionState
	 */
	protected CompositionState getCompositionState() {
		return compositionState;
	}

	/**
	 * @param compositionState the compositionState to set
	 */
	protected void setCompositionState(CompositionState compositionState) {
		this.compositionState = compositionState;
	}

}