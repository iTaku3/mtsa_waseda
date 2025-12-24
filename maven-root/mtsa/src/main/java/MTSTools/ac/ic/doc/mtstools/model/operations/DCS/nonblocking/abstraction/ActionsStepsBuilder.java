package MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking.abstraction;

import MTSTools.ac.ic.doc.commons.collections.InitMap;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking.DirectedControllerSynthesisNonBlocking;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ActionsStepsBuilder<State, Action> extends BaseStepsBuilder<State, Action> {
  private final Map<HAction<State, Action>, Map<HAction<State, Action>, Integer>>
      stepsBetweenActionsWithoutMarking;

  private final Map<
          HState<State, Action>, Map<HAction<State, Action>, Pair<HAction<State, Action>, Integer>>>
      firstActionAndStepsToReachableActionsWithoutMarking;

  private final Map<HState<State, Action>, Pair<HAction<State, Action>, Integer>>
      firstActionAndStepsToClosestMarking;

  public ActionsStepsBuilder(DirectedControllerSynthesisNonBlocking<State, Action> dcs) {
    super(dcs);

    var allTransitions = getTransitions(0, ltss.size());
    var allMarkingActions = getAllMarkingActions();

    this.stepsBetweenActionsWithoutMarking =
        computeStepsBetweenActionsWithoutMarking(ltss, allTransitions, allMarkingActions);
    this.firstActionAndStepsToReachableActionsWithoutMarking =
        computeFirstActionAndStepsToReachableActionsWithoutMarking(
            allTransitions, stepsBetweenActionsWithoutMarking, allMarkingActions);
    this.firstActionAndStepsToClosestMarking =
        extractFirstActionAndStepsToClosestMarking(
            firstActionAndStepsToReachableActionsWithoutMarking, allMarkingActions);
  }

  private static <State, Action>
      Map<HAction<State, Action>, Map<HAction<State, Action>, Integer>>
          computeStepsBetweenActionsWithoutMarking(
              List<LTS<State, Action>> ltss,
              Map<HState<State, Action>, Map<HAction<State, Action>, HState<State, Action>>>
                  transitions,
              Set<HAction<State, Action>> markingActions) {
    // LTS毎の隣り合うアクション（-1は除く）
    var nextActions =
        new ArrayList<Map<HAction<State, Action>, Set<HAction<State, Action>>>>(ltss.size());
    for (int i = 0; i < ltss.size(); i++) {
      nextActions.add(new InitMap<>(HashSet.class));
    }
    for (var destinations : transitions.values()) {
      for (var e1 : destinations.entrySet()) {
        var action = e1.getKey();
        // markingからの遷移は考慮不要
        if (markingActions.contains(action)) continue;
        var destination = e1.getValue();
        if (destination.state.equals(-1L)) continue;
        var nas = nextActions.get(destination.lts).get(action);
        for (var e2 : transitions.get(destination).entrySet()) {
          var nextAction = e2.getKey();
          var nextDestination = e2.getValue();
          if (nextDestination.state.equals(-1L)) continue;
          nas.add(nextAction);
        }
      }
    }

    // アクション間の距離
    var stepsBetweenActions =
        new HashMap<HAction<State, Action>, Map<HAction<State, Action>, Integer>>();
    for (var nas : nextActions) {
      for (var e : nas.entrySet()) {
        var a1 = e.getKey();
        stepsBetweenActions.putIfAbsent(a1, new HashMap<>());
        var stepsFromA1 = stepsBetweenActions.get(a1);
        for (var a2 : e.getValue()) {
          stepsFromA1.put(a2, 1);
        }
      }
    }

    // いずれ上限回数ではなくwhileで実装したい
    int maxLoop = 0;
    for (int i = 1; i < ltss.size(); i++) {
      maxLoop += ltss.get(i).getStates().size();
    }

    for (int loopCount = 1; loopCount <= maxLoop; loopCount++) {
      // stepsBetweenActionsのコピー
      var oldStepsBetweenActions = new HashMap<>(stepsBetweenActions);
      for (var e : stepsBetweenActions.entrySet()) {
        oldStepsBetweenActions.put(e.getKey(), new HashMap<>(e.getValue()));
      }

      boolean loopChanged = false;

      // LTS毎にアクション間の距離を計算
      for (int i = 1; i < ltss.size(); i++) {
        var iStepsBetweenActions =
            new HashMap<HAction<State, Action>, Map<HAction<State, Action>, Integer>>();

        // 隣り合うアクション間の距離で初期化
        for (var e : nextActions.get(i).entrySet()) {
          var a1 = e.getKey();
          iStepsBetweenActions.putIfAbsent(a1, new HashMap<>());
          var iStepsFromA1 = iStepsBetweenActions.get(a1);
          for (var a2 : e.getValue()) {
            iStepsFromA1.put(a2, oldStepsBetweenActions.get(a1).get(a2));
          }
        }

        // iStepsBetweenActionsの更新
        boolean changed;
        do {
          changed = false;

          // iStepsBetweenActionsのコピー
          var oldIStepsBetweenActions = new HashMap<>(iStepsBetweenActions);
          for (var e : iStepsBetweenActions.entrySet()) {
            oldIStepsBetweenActions.put(e.getKey(), new HashMap<>(e.getValue()));
          }

          // nextActionsを使ってiStepsBetweenActionsを更新
          for (var e1 : nextActions.get(i).entrySet()) {
            var a1 = e1.getKey();
            var stepsFromA1 = iStepsBetweenActions.get(a1);
            for (var a2 : e1.getValue()) {
              // marking経由は考えない
              if (markingActions.contains(a2)) continue;
              // nextActionsなのでnullにはならないはず
              var stepsToA2 = stepsFromA1.get(a2);
              for (var e2 : oldIStepsBetweenActions.get(a2).entrySet()) {
                var a3 = e2.getKey();
                var oldSteps = oldIStepsBetweenActions.get(a1).get(a3);
                var newSteps = stepsToA2 + oldIStepsBetweenActions.get(a2).get(a3);
                if (oldSteps == null || newSteps < oldSteps) {
                  stepsFromA1.put(a3, newSteps);
                  changed = true;
                }
              }
            }
          }
        } while (changed);

        // stepsBetweenActionsの更新
        for (var e1 : iStepsBetweenActions.entrySet()) {
          var a1 = e1.getKey();
          for (var e2 : e1.getValue().entrySet()) {
            var a2 = e2.getKey();
            var steps = e2.getValue();
            // 更新に対する更新も許容したいので、oldStepsBetweenActionsを使わない
            var oldSteps = stepsBetweenActions.get(a1).get(a2);
            if (oldSteps == null || steps > oldSteps) {
              stepsBetweenActions.get(a1).put(a2, steps);
              loopChanged = true;
            }
          }
        }
      }

      // 更新がなければ終了
      if (!loopChanged) {
        System.out.println("StepsBetweenActions: loopCount=" + loopCount);
        break;
      }

      // 前回の更新と今回の更新が同じ場所の場合、無限に再計算する可能性がある
      // 例：
      // LTS1: a -> b -> c
      // LTS2: a -> c -> b
      // の遷移がある場合、ab間とac間を無限に増やそうとする
      // なお、無限に再計算する場合は合成失敗。合成成功する場合はいつかは別のパスを採用し更新がなくなる
      // 更新し続けている部分を一旦無限とし、他のパスを探すことで更新がなくなることを期待できるかもしれない
      // まだ理論化できていないので、上限回数で終了する実装とする
    }

    return stepsBetweenActions;
  }

  private static <State, Action>
      Map<HState<State, Action>, Map<HAction<State, Action>, Pair<HAction<State, Action>, Integer>>>
          computeFirstActionAndStepsToReachableActionsWithoutMarking(
              Map<HState<State, Action>, Map<HAction<State, Action>, HState<State, Action>>>
                  transitions,
              Map<HAction<State, Action>, Map<HAction<State, Action>, Integer>>
                  stepsBetweenActionsWithoutMarking,
              Set<HAction<State, Action>> markingActions) {
    var stepsToReachableActionsWithoutMarking =
        new HashMap<
            HState<State, Action>,
            Map<HAction<State, Action>, Map<HAction<State, Action>, Integer>>>();

    for (var e : transitions.entrySet()) {
      var s = e.getKey();
      for (var a : e.getValue().keySet()) {
        var fromS = stepsToReachableActionsWithoutMarking.get(s);
        if (fromS == null) {
          fromS = new HashMap<>();
          stepsToReachableActionsWithoutMarking.put(s, fromS);
        }
        var fromSToA = fromS.get(a);
        if (fromSToA == null) {
          fromSToA = new HashMap<>();
          fromS.put(a, fromSToA);
        }
        fromSToA.put(a, 1);
      }
    }

    boolean changed;
    do {
      changed = false;

      for (var e1 : transitions.entrySet()) {
        var s1 = e1.getKey();
        var fromS1 = stepsToReachableActionsWithoutMarking.get(s1);
        if (fromS1 == null) {
          fromS1 = new HashMap<>();
          stepsToReachableActionsWithoutMarking.put(s1, fromS1);
        }

        for (var e2 : e1.getValue().entrySet()) {
          var a1 = e2.getKey();
          if (markingActions.contains(a1)) continue;
          var s2 = e2.getValue();
          if (s2 == s1 || s2.state.equals(-1L)) continue;
          var fromS2 = stepsToReachableActionsWithoutMarking.get(s2);
          if (fromS2 == null) continue;

          for (var e3 : fromS2.entrySet()) {
            var a3 = e3.getKey();
            var fromS2ToA3 = e3.getValue();

            for (var e4 : fromS2ToA3.entrySet()) {
              var a2 = e4.getKey();
              var stepsFromS2ToA3ThroughA2 = e4.getValue();
              var stepsFromA1ToA2 = stepsBetweenActionsWithoutMarking.get(a1).get(a2);
              var stepsFromS1ToA3ThroughA1 = stepsFromA1ToA2 + stepsFromS2ToA3ThroughA2;

              var fromS1ToA3 = fromS1.get(a3);
              if (fromS1ToA3 == null) {
                fromS1ToA3 = new HashMap<>();
                fromS1.put(a3, fromS1ToA3);
              }
              var oldStepsFromS1ToA3ThroughA1 = fromS1ToA3.get(a1);

              // 前の値がない or 新しい値の方が小さい場合、更新
              if (oldStepsFromS1ToA3ThroughA1 == null
                  || stepsFromS1ToA3ThroughA1 < oldStepsFromS1ToA3ThroughA1) {
                fromS1ToA3.put(a1, stepsFromS1ToA3ThroughA1);
                changed = true;
              }
            }
          }
        }
      }
    } while (changed);

    // 最小ステップ数のfirstActionを抽出
    var firstActionAndStepsToReachableActionsWithoutMarking =
        new HashMap<
            HState<State, Action>,
            Map<HAction<State, Action>, Pair<HAction<State, Action>, Integer>>>();

    for (var e1 : stepsToReachableActionsWithoutMarking.entrySet()) {
      var s1 = e1.getKey();
      firstActionAndStepsToReachableActionsWithoutMarking.put(s1, new HashMap<>());

      for (var e2 : e1.getValue().entrySet()) {
        var a = e2.getKey();

        Pair<HAction<State, Action>, Integer> p = null;
        for (var e3 : e2.getValue().entrySet()) {
          var firstAction = e3.getKey();
          var steps = e3.getValue();
          if (p == null || steps < p.getSecond()) {
            p = new Pair<>(firstAction, steps);
          }
        }

        if (p != null) {
          firstActionAndStepsToReachableActionsWithoutMarking.get(s1).put(a, p);
        }
      }
    }

    return firstActionAndStepsToReachableActionsWithoutMarking;
  }

  @Override
  public Map<HAction<State, Action>, Map<HAction<State, Action>, Integer>>
      getStepsBetweenActionsWithoutMarking() {
    return stepsBetweenActionsWithoutMarking;
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
