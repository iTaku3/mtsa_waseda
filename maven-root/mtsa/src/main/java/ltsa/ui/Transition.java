package ltsa.ui;

import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;
import MTSTools.ac.ic.doc.mtstools.model.impl.MTSTransitionImpl;

/**
 * Concrete form of MTSTransition with convenience methods
 * XXX: use interface MTSTransition. See if MTSTransitionWithModalityInfo can be used
 */
public class Transition extends MTSTransitionImpl<String, Long>{
	private Long fromState;
	private Long toState;
	private String name;
	private MTS.TransitionType type;
	
	public Transition(Long from, Pair<String, Long> to, MTS.TransitionType type) {
		super(from, to.getFirst(), to.getSecond());
		this.type = type;
	}
	
	public Long from() {
		return this.getStateFrom();
	}
	
	public Long to() {
		return this.getStateTo();
	}
	
	public String name() {
		return this.getEvent();
	}
	
	public MTS.TransitionType type() {
		return this.type;
	}

	public boolean equals(Transition t) {
		return (this.fromState == t.from() && this.toState == t.to() 
					   && type == t.type() && name.equals(t.name()));
	}
	 
	/**
	 * @return Name with ? if 'maybe' like in the original LTS
	 * 
	 */

	public String ltsName() {
		if(this.type() == MTS.TransitionType.MAYBE)
			return this.name().concat("?");
		else
			return this.name();
	}
	
	/**
	 * change the type of the transition (for refinement purposes)
	 * 
	 */
	public void setType(TransitionType type ){
		this.type = type;
	}
}
