package MTSTools.ac.ic.doc.mtstools.model;

public interface MTSTransition<Event, State> {

	/**
	 * @return the event
	 */
	public abstract Event getEvent();

	/**
	 * @param event the event to set
	 */
	public abstract void setEvent(Event event);

	/**
	 * @return the stateTo
	 */
	public abstract State getStateTo();

	/**
	 * @param stateTo the stateTo to set
	 */
	public abstract void setStateTo(State stateTo);

	/**
	 * @return the stateFrom
	 */
	public abstract State getStateFrom();

	/**
	 * @param stateFrom the stateFrom to set
	 */
	public abstract void setStateFrom(State stateFrom);

}