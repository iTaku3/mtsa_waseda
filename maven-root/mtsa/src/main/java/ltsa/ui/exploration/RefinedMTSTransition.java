package ltsa.ui.exploration;

import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;

public class RefinedMTSTransition {
	
	RefinedMTSState from;
	String event;
	RefinedMTSState to;
	TransitionType type;
	
	public RefinedMTSTransition(RefinedMTSState _from, String action, RefinedMTSState _to, TransitionType _type){
		
		from = _from;
		event = action;
		to = _to;
		type = _type;
		
	}

	public TransitionType type() {
		return type;
	}

	public String getEvent(){
		return event;
	}
	
	public RefinedMTSState to(){
		return to;
	}
	
	public RefinedMTSState from(){
		return from;
	}
	
	public void setTo(RefinedMTSState state){
		to = state;
	}
	
	
	public boolean equals(RefinedMTSState _from, String action, RefinedMTSState _to, TransitionType _type) {
		return (from.equals(_from) && event.equals(action) && to.equals(_to) && type == _type);
	}

	public String toString(){
		return "("+from.toString()+", "+event+", "+to.toString()+")";
	}
}
