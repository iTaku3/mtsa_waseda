package ltsa.lts;

import MTSTools.ac.ic.doc.mtstools.model.MTS;
import ltsa.ac.ic.doc.mtstools.util.fsp.MTSToAutomataConverter;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by Victor Wjugow on 28/05/15.
 * This class has an extra property, that marks special states of a CompactState
 */
public class MarkedCompactState extends CompactState {

	private int[] markedStates;

	public MarkedCompactState(CompactState cs, int[] markedStates) {
		super.alphabet = cs.alphabet;
		super.endseq = cs.endseq;
		super.maxStates = cs.maxStates;
		super.name = cs.name;
		super.states = cs.states;
		this.markedStates = markedStates;
	}

	public MarkedCompactState(MTS<Long, String> mts, Set<Long> terminalSet, String name) {
		CompactState cs = MTSToAutomataConverter.getInstance().convert(mts, name);
		super.alphabet = cs.alphabet;
		super.endseq = cs.endseq;
		super.maxStates = cs.maxStates;
		super.name = cs.name;
		super.states = cs.states;
		this.markedStates = new int[terminalSet.size()];
		int i = 0;
		for (Iterator<Long> it = terminalSet.iterator(); it.hasNext(); ++i) {
			this.markedStates[i] = (int) it.next().longValue();
		}
	}

	public int[] getMarkedStates() {
		return markedStates;
	}

	public void setMarkedStates(int[] markedStates) {
		this.markedStates = markedStates;
	}
}