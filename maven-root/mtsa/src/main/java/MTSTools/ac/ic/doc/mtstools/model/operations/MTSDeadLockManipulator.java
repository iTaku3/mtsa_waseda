package MTSTools.ac.ic.doc.mtstools.model.operations;

import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTSTrace;

public interface MTSDeadLockManipulator<State, Action> {
	
	public abstract boolean getTransitionsToDeadlock(MTS<State, Action> mts, MTSTrace<Action, State> traceToDeadlock);
	public abstract void deleteTransitionsToDeadlock(MTS<State, Action> mtsWithoutDeadlock);
	public int getDeadlockStatus(MTS<State, Action> mts);
}