package MTSSynthesis.controller.gr.time;

import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.LTS;
import java.util.Set;

public abstract class GR1toReachability {
  public static <S, A> void transform(LTS<S, A> lts, Set<S> finalStates) {
    for (S s : finalStates) {
      if (lts.getStates().contains(s)) {
        for (Pair<A, S> transition : lts.getTransitions(s)) {
          lts.removeTransition(s, transition.getFirst(), transition.getSecond());
        }
      }
    }
    lts.removeUnreachableStates();
  }
}
