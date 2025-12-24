package MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking.abstraction;

import MTSTools.ac.ic.doc.commons.collections.InitMap;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class BaseStepsBuilder<State, Action> extends BaseBuilder<State, Action> {
  public BaseStepsBuilder(DirectedControllerSynthesisNonBlocking<State, Action> dcs) {
    super(dcs);
  }

  public BaseStepsBuilder(BaseBuilder<State, Action> builder) {
    super(builder);
  }

  public Map<HAction<State, Action>, Map<HAction<State, Action>, Integer>>
      getStepsBetweenActionsWithoutMarking() {
    return null;
  }

  public Map<HAction<State, Action>, Integer> getStepsToClosestMarkingFromAction() {
    var stepsBetweenActionsWithoutMarking = getStepsBetweenActionsWithoutMarking();
    if (stepsBetweenActionsWithoutMarking == null) return null;

    var stepsToClosestMarkingFromAction = new HashMap<HAction<State, Action>, Integer>();
    for (var e1 : stepsBetweenActionsWithoutMarking.entrySet()) {
      var a1 = e1.getKey();
      var minSteps = Integer.MAX_VALUE;
      for (var e2 : e1.getValue().entrySet()) {
        var a2 = e2.getKey();
        var steps = e2.getValue();
        if (getAllMarkingActions().contains(a2)) {
          minSteps = Math.min(minSteps, steps);
        }
      }
      if (minSteps != Integer.MAX_VALUE) {
        stepsToClosestMarkingFromAction.put(a1, minSteps);
      }
    }
    return stepsToClosestMarkingFromAction;
  }

  // 各ステートから全ての到達可能なアクションまでの最短ルートの最初のアクションとステップ数
  // markingを通る遷移、-1への遷移は除く
  public abstract Map<
          HState<State, Action>, Map<HAction<State, Action>, Pair<HAction<State, Action>, Integer>>>
      getFirstActionAndStepsToReachableActionsWithoutMarking();

  // 各ステートから全ての到達可能なアクションまでの最短ルートのステップ数
  // markingを通る遷移、-1への遷移は除く
  public Map<HState<State, Action>, Map<HAction<State, Action>, Integer>>
      getStepsToReachableActionsWithoutMarking() {
    var stepsToReachableActionsWithoutMarking =
        new InitMap<HState<State, Action>, Map<HAction<State, Action>, Integer>>(HashMap.class);

    for (var e1 : getFirstActionAndStepsToReachableActionsWithoutMarking().entrySet()) {
      var s = e1.getKey();
      var fromS = stepsToReachableActionsWithoutMarking.get(s);
      for (var e2 : e1.getValue().entrySet()) {
        var a = e2.getKey();
        var steps = e2.getValue().getSecond();
        fromS.put(a, steps);
      }
    }

    return stepsToReachableActionsWithoutMarking;
  }

  // 各ステートからmarkingまでの最短ルートの最初のアクションとステップ数
  // -1への遷移は除く
  public abstract Map<HState<State, Action>, Pair<HAction<State, Action>, Integer>>
      getFirstActionAndStepsToClosestMarking();

  // 各ステートからmarkingまでの最短ルートのステップ数
  // -1への遷移は除く
  public Map<HState<State, Action>, Integer> getStepsToClosestMarkingAction() {

    var stepsToClosestMarkingAction = new HashMap<HState<State, Action>, Integer>();

    for (var e : getFirstActionAndStepsToClosestMarking().entrySet()) {
      var s = e.getKey();
      var steps = e.getValue().getSecond();
      stepsToClosestMarkingAction.put(s, steps);
    }

    return stepsToClosestMarkingAction;
  }

  public Void printFirstActionAndStepsToReachableActionsWithoutMarking() {
    var firstActionAndStepsToReachableActionsWithoutMarking =
        getFirstActionAndStepsToReachableActionsWithoutMarking();

    for (int i = 1; i < ltss.size(); i++) {
      System.out.println("LTS " + i + ":");
      var states = stateMaps.get(i).values();
      for (var s : states) {
        if (s.state.equals(-1L)) continue;
        System.out.println("  " + s);
        var fromS = firstActionAndStepsToReachableActionsWithoutMarking.get(s);
        for (var e2 : fromS.entrySet()) {
          var a = e2.getKey();
          var p = e2.getValue();
          System.out.println("   → " + a + ": " + p.getSecond() + "(-" + p.getFirst() + "→)");
        }
      }
    }
    return null;
  }

  public static <State, Action>
      Map<HState<State, Action>, Pair<HAction<State, Action>, Integer>>
          extractFirstActionAndStepsToClosestMarking(
              Map<
                      HState<State, Action>,
                      Map<HAction<State, Action>, Pair<HAction<State, Action>, Integer>>>
                  firstActionAndStepsToReachableActionsWithoutMarking,
              Set<HAction<State, Action>> markingActions) {
    var firstActionAndStepsToClosestMarking =
        new HashMap<HState<State, Action>, Pair<HAction<State, Action>, Integer>>();

    for (var e1 : firstActionAndStepsToReachableActionsWithoutMarking.entrySet()) {
      var s = e1.getKey();
      Pair<HAction<State, Action>, Integer> firstActionAndSteps = null;
      for (var e2 : e1.getValue().entrySet()) {
        var a = e2.getKey();
        var p = e2.getValue();
        if (markingActions.contains(a)) {
          if (firstActionAndSteps == null || p.getSecond() < firstActionAndSteps.getSecond()) {
            firstActionAndSteps = p;
          }
        }
      }
      if (firstActionAndSteps != null) {
        firstActionAndStepsToClosestMarking.put(s, firstActionAndSteps);
      }
    }

    return firstActionAndStepsToClosestMarking;
  }
}
