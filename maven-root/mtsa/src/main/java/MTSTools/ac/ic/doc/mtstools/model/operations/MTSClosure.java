package MTSTools.ac.ic.doc.mtstools.model.operations;

import MTSTools.ac.ic.doc.mtstools.model.MTS;
import java.util.Set;

public interface MTSClosure {
  public <State, Action> void applyMTSClosure(MTS<State, Action> mts, Set<Action> silentActions);
}
