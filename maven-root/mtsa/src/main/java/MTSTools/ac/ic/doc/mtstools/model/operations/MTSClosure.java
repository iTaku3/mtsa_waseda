package MTSTools.ac.ic.doc.mtstools.model.operations;

import java.util.Set;

import MTSTools.ac.ic.doc.mtstools.model.MTS;

public interface MTSClosure {
	public <State, Action> void applyMTSClosure(MTS<State, Action> mts, Set<Action> silentActions);
}
