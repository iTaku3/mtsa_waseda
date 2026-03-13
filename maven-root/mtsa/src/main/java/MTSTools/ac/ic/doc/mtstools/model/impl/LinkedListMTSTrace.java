package MTSTools.ac.ic.doc.mtstools.model.impl;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.Validate;

import MTSTools.ac.ic.doc.mtstools.model.MTSTransition;

/**
 * Implementation backed by a LinkedList of transitions
 * @author gsibay
 *
 */
public class LinkedListMTSTrace<A, S> extends AbstractListMTSTrace<A, S> {

	private List<MTSTransition<A, S>> transitionsList;

	public LinkedListMTSTrace() {
		this.setTransitionsList(new LinkedList<MTSTransition<A, S>>());
	}


	@Override
	protected void setTransitionsList(List<MTSTransition<A, S>> trace) {
		Validate.notNull(trace);
		this.transitionsList = trace;
	}

	@Override
	protected List<MTSTransition<A, S>> getTransitionsList() {
		return this.transitionsList;
	}
}
