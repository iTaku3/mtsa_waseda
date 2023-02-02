package MTSTools.ac.ic.doc.mtstools.model.impl;

import MTSTools.ac.ic.doc.mtstools.model.MTSTransition;

public abstract class AbstractMTSTransition<Event, State> implements MTSTransition<Event, State> {

	protected Event event;
	protected State stateTo;
	protected State stateFrom;

	@Override
	public Event getEvent() {
		return event;
	}

	@Override
	public void setEvent(Event event) {
		this.event = event;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj==this) {
			return true;
		}
		if (obj instanceof MTSTransition) {
			MTSTransition<Event, State> es = (MTSTransition<Event, State>) obj;
			return this.getEvent().equals(es.getEvent()) 
					&& this.getStateFrom().equals(es.getStateFrom())		
					&& this.getStateTo().equals(es.getStateTo());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.getEvent().hashCode() 
				^ this.getStateFrom().hashCode() 
				^ this.getStateTo().hashCode();
	}

	@Override
	public State getStateTo() {
		return stateTo;
	}

	@Override
	public void setStateTo(State stateTo) {
		this.stateTo = stateTo;
	}

	@Override
	public State getStateFrom() {
		return stateFrom;
	}

	@Override
	public void setStateFrom(State stateFrom) {
		this.stateFrom = stateFrom;
	}

}