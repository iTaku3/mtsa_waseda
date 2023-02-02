package MTSTools.ac.ic.doc.mtstools.model.impl;

import java.util.List;

import MTSTools.ac.ic.doc.mtstools.model.MTSConstants;

public class CompositionState {
	private Long compositeState;
	private List<Long> individualStates;
	
	public CompositionState(Long aLong, List<Long> states){
		this.compositeState = aLong;
		this.individualStates = states;
	}

	/**
	 * @return the compositeState
	 */
	protected Long getCompositeState() {
		return compositeState;
	}

	/**
	 * @return the individualStates
	 */
	protected List<Long> getIndividualStates() {
		return individualStates;
	}
	
	@Override
	public String toString() {
		return "CompositeState: " + getCompositeState() + "\n" + "Individual States: " + getIndividualStates();
	}
	
	
	
	@Override
	public boolean equals(Object obj) {
		if (obj==this) {
			return true;
		}
		try {
			CompositionState toEquals = (CompositionState) obj;
			return getIndividualStates().equals(toEquals.getIndividualStates());
			
		} catch (RuntimeException e) {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return getIndividualStates().hashCode();
	}

	public boolean isErrorState() {
		return getCompositeState().equals(MTSConstants.ERROR_STATE);
	}
}
