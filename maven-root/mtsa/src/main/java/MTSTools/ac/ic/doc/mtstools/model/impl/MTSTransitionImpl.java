package MTSTools.ac.ic.doc.mtstools.model.impl;

/**
 * 
 * MTS transition where type is irrelevant
 * @author gsibay
 *
 * @param <Event>
 * @param <State>
 */
public class MTSTransitionImpl<Event, State> extends AbstractMTSTransition<Event, State> {
	
	public static <Event, State> MTSTransitionImpl<Event, State> createMTSEventState(State stateFrom, Event event, State stateTo){
		return new MTSTransitionImpl<Event, State>(stateFrom, event, stateTo);
	}
	
	public MTSTransitionImpl(State stateFrom, Event event, State stateTo){
		this.event = event;
		this.stateFrom = stateFrom;
		this.stateTo = stateTo;
	}
	
	@Override
	public String toString() {
		return "(" + this.getStateFrom() + ", " + this.getEvent() + ", " + this.getStateTo() + ")";
	}

}
