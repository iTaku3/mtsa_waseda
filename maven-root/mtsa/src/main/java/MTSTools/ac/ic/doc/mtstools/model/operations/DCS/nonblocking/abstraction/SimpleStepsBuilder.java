package MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking.abstraction;

import MTSTools.ac.ic.doc.commons.collections.InitMap;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class SimpleStepsBuilder<State, Action> extends BaseStepsBuilder<State, Action> {
  private final Map<
          HState<State, Action>, Map<HAction<State, Action>, Pair<HAction<State, Action>, Integer>>>
      firstActionAndStepsToReachableActionsWithoutMarking;

  private final Map<HState<State, Action>, Pair<HAction<State, Action>, Integer>>
      firstActionAndStepsToClosestMarking;

  public SimpleStepsBuilder(DirectedControllerSynthesisNonBlocking<State, Action> dcs) {
    this(new BaseBuilder<State, Action>(dcs));
  }

  public SimpleStepsBuilder(BaseBuilder<State, Action> builder) {
    super(builder);

    var allTransitions = getTransitions(0, ltss.size());
    var allMarkingActions = getAllMarkingActions();

    firstActionAndStepsToReachableActionsWithoutMarking =
        computeFirstActionAndStepsToReachableActionsWithoutMarking(
            allTransitions, allMarkingActions);
    firstActionAndStepsToClosestMarking =
        extractFirstActionAndStepsToClosestMarking(
            firstActionAndStepsToReachableActionsWithoutMarking, allMarkingActions);
  }

  private static <State, Action>
      Map<HState<State, Action>, Map<HAction<State, Action>, Pair<HAction<State, Action>, Integer>>>
          computeFirstActionAndStepsToReachableActionsWithoutMarking(
              Map<HState<State, Action>, Map<HAction<State, Action>, HState<State, Action>>>
                  transitions,
              Set<HAction<State, Action>> markingActions) {
    var firstActionAndStepsToReachableActionsWithoutMarking =
        new InitMap<
            HState<State, Action>,
            Map<HAction<State, Action>, Pair<HAction<State, Action>, Integer>>>(HashMap.class);

    for (var e : transitions.entrySet()) {
      var s = e.getKey();
      for (var a : e.getValue().keySet()) {
        firstActionAndStepsToReachableActionsWithoutMarking.get(s).put(a, new Pair<>(a, 1));
      }
    }

    for (int i = 2; true; i++) {
      boolean changed = false;

      var oldFirstActionAndStepsToReachableActionsWithoutMarking =
          new HashMap<>(firstActionAndStepsToReachableActionsWithoutMarking);
      for (var e : firstActionAndStepsToReachableActionsWithoutMarking.entrySet()) {
        oldFirstActionAndStepsToReachableActionsWithoutMarking.put(
            e.getKey(), new HashMap<>(e.getValue()));
      }

      for (var e1 : transitions.entrySet()) {
        var s1 = e1.getKey();
        var fromS1 = firstActionAndStepsToReachableActionsWithoutMarking.get(s1);
        for (var e2 : e1.getValue().entrySet()) {
          var a1 = e2.getKey();
          if (markingActions.contains(a1)) continue;
          var s2 = e2.getValue();
          if (s2 == s1 || s2.state.equals(-1L)) continue;
          var oldFromS2 = oldFirstActionAndStepsToReachableActionsWithoutMarking.get(s2);
          for (var a2 : oldFromS2.keySet()) {
            if (fromS1.putIfAbsent(a2, new Pair<>(a1, i)) == null) {
              changed = true;
            }
          }
        }
      }
      if (!changed) break;
    }

    return firstActionAndStepsToReachableActionsWithoutMarking;
  }

  @Override
  public Map<
          HState<State, Action>, Map<HAction<State, Action>, Pair<HAction<State, Action>, Integer>>>
      getFirstActionAndStepsToReachableActionsWithoutMarking() {
    return firstActionAndStepsToReachableActionsWithoutMarking;
  }

  @Override
  public Map<HState<State, Action>, Pair<HAction<State, Action>, Integer>>
      getFirstActionAndStepsToClosestMarking() {
    return firstActionAndStepsToClosestMarking;
  }
}
