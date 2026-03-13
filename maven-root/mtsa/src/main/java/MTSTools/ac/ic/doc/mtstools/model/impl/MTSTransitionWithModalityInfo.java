package MTSTools.ac.ic.doc.mtstools.model.impl;


/**
 * The modality is only used during display with toString method
 * @author gsibay
 *
 */
public class MTSTransitionWithModalityInfo<A, S> extends AbstractMTSTransition<A, S> {

	private boolean isMaybe;
	
	public MTSTransitionWithModalityInfo(S stateFrom, A event, S stateTo, boolean isMaybe){
		this.event = event;
		this.stateFrom = stateFrom;
		this.stateTo = stateTo;
		this.isMaybe = isMaybe;
	}
	
	@Override
	public String toString() {
		return "(" + this.getStateFrom() + ", " + this.getEvent() + 
				(this.isMaybe ? "?, " : ", ") + this.getStateTo() + ")";
	}

	public String toStringWithModality() {
		if(this.isMaybe) {
			return this.getEvent() + "?";
		} else {
			return this.getEvent().toString();
		}
	}
}
