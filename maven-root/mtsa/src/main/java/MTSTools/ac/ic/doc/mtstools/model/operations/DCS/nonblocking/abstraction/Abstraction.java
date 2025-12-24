package MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking.abstraction;

import MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking.Compostate;
import java.util.List;
import java.util.Set;

/** Abstract class for abstractions. */
public abstract class Abstraction<State, Action> {
  public boolean shouldRecomputeEstimatesOnChange() {
    return true;
  }

  public abstract void eval(
      Compostate<State, Action> compostate, List<Set<State>> knownMarked, List<Set<State>> goals);
}
