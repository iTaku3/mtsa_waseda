package MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking.abstraction;

import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections15.CollectionUtils;

public class GeneralizedPreMarkingDirection<State, Action> extends Abstraction<State, Action> {
  @Override
  public boolean shouldRecomputeEstimatesOnChange() {
    return false;
  }

  // 全てのLTS
  private final List<LTS<State, Action>> ltss;

  // LTS毎の全てのStateからHStateのマッピング
  private final List<Map<State, HState<State, Action>>> stateMaps;

  // LTS毎の全てのHAction
  private final List<Set<HAction<State, Action>>> actions;

  // 全てのトランジション
  private final List<Map<HState<State, Action>, Map<HAction<State, Action>, HState<State, Action>>>>
      transitions;

  // LTS毎の全てのmarking
  private final List<Set<HAction<State, Action>>> markingActions;

  // 各HStateから同LTS内の到達可能なHActionまでの最短ステップ数（パスにmarkingを含まない）
  private final Map<HState<State, Action>, Map<HAction<State, Action>, Integer>>
      stepsToReachableActionsWithoutMarking;

  // 各HAction間の最短ステップ数
  private final Map<HAction<State, Action>, Map<HAction<State, Action>, Integer>>
      stepsBetweenActionsWithoutMarking;

  // 各HStateからmarkingまでの最短ステップ数
  private final Map<HState<State, Action>, Integer> stepsToClosestMarkingAction;

  // 各HAction間の最短ステップ数
  private final Map<HAction<State, Action>, Integer> stepsToClosestMarkingFromAction;

  // pre-marking: HStateがGoal状態となるために、発火が必要なHAction
  // 判定するときControllable/Uncontrollableを考慮する
  private final Map<HState<State, Action>, Set<HAction<State, Action>>> preMarkingActions;

  private final Map<HState<State, Action>, Map<HAction<State, Action>, Set<HAction<State, Action>>>>
      prePreMarkingActions;

  // キャッシュ
  private final HEstimate<State, Action> zeroEstimate;
  private final HEstimate<State, Action> infinityEstimate;

  public GeneralizedPreMarkingDirection(DirectedControllerSynthesisNonBlocking<State, Action> dcs) {
    var builder = new Builder<>(dcs);
    ltss = builder.ltss;
    stateMaps = builder.stateMaps;
    actions = builder.actions;
    transitions = builder.transitions;
    markingActions = builder.markingActions;
    stepsToReachableActionsWithoutMarking = builder.getStepsToReachableActionsWithoutMarking();
    stepsToClosestMarkingAction = builder.getStepsToClosestMarkingAction();
    stepsBetweenActionsWithoutMarking = builder.getStepsBetweenActionsWithoutMarking();
    stepsToClosestMarkingFromAction = builder.getStepsToClosestMarkingFromAction();
    preMarkingActions = builder.preMarkingActions;
    prePreMarkingActions = builder.prePreMarkingActions;
    zeroEstimate = new HEstimate<>(ltss.size(), HDist.zero);
    infinityEstimate = new HEstimate<>(ltss.size(), HDist.chasm);
  }

  public int getAveragePreMarkingActions() {
    var allStatesCount = 0;
    var allTransitionsCount = 0;
    var allPreMarkingActionsCount = 0;

    for (int i = 1; i < ltss.size(); i++) {
      var transitions = this.transitions.get(i);
      for (var s1 : stateMaps.get(i).values()) {
        allStatesCount++;
        for (var s2 : transitions.get(s1).values()) {
          if (!s2.state.equals(-1L)) allTransitionsCount++;
        }
        var preMarkingActions = this.preMarkingActions.get(s1);
        if (preMarkingActions != null) {
          allPreMarkingActionsCount += preMarkingActions.size();
        }
      }
    }

    System.out.println("All States Count: " + allStatesCount);
    System.out.println("All Transitions Count: " + allTransitionsCount);
    System.out.println("All Pre-Marking Actions Count: " + allPreMarkingActionsCount);

    return allPreMarkingActionsCount / allStatesCount;
  }

  // for debug
  private void printActions() {
    for (int i = 1; i < ltss.size(); i++) {
      System.out.println("LTS " + i + ":");
      System.out.println();
      System.out.println(ltss.get(i));
      for (var state : stateMaps.get(i).values()) {
        if (state.state.equals(-1L)) continue;
        System.out.println(" State " + state + ":");
        System.out.println("  Pre-Marking actions: " + preMarkingActions.get(state));
        System.out.println("  Pre-Pre-Marking actions: " + prePreMarkingActions.get(state));
      }
      System.out.println();
    }
  }

  private static class Builder<State, Action> extends SimpleStepsBuilder<State, Action> {
    final Map<HState<State, Action>, Set<HAction<State, Action>>> preMarkingActions;
    final Map<HState<State, Action>, Map<HAction<State, Action>, Set<HAction<State, Action>>>>
        prePreMarkingActions;

    Builder(DirectedControllerSynthesisNonBlocking<State, Action> dcs) {
      super(dcs);
      preMarkingActions = new HashMap<>();
      prePreMarkingActions = new HashMap<>();

      var allActions = actions.stream().flatMap(s -> s.stream()).collect(Collectors.toSet());
      var allTransitions = getTransitions(1, ltss.size());
      var allMarkingActions = getAllMarkingActions();

      var reverseTransitions = buildReverseTransitions(1, ltss.size());
      var controllableStates = buildControllableStates(allTransitions);

      for (var action : allActions) {
        // markingActionであればpre-markingではない
        if (allMarkingActions.contains(action)) continue;

        var statesThatActionIsNotPreMarking =
            computeStatesThatActionIsNotRequiredToTargetActions(
                allTransitions, reverseTransitions, controllableStates, allMarkingActions, action);
        // LTS0の計算は不要
        for (int i = 1; i < ltss.size(); i++) {
          // markingが一部しかない場合、pre-markingと判別できない
          if (markingActions.get(i).size() != allMarkingActions.size()) continue;

          for (var state : stateMaps.get(i).values()) {
            // stateが-1（エラー状態）の計算は不要
            if (state.state.equals(-1L)) continue;
            if (!statesThatActionIsNotPreMarking.contains(state)) {
              var pmas = preMarkingActions.get(state);
              if (pmas == null) {
                pmas = new HashSet<>();
                preMarkingActions.put(state, pmas);
              }
              pmas.add(action);
            }
          }
        }
      }

      // // pre-pre-markingの計算
      // // pre-pre-markingは、pre-markingとpre-pre-markingの両方に対して計算する必要がある
      // var actionsShouldComputePrePreMarking =
      //     preMarkingActions.values().stream().flatMap(s ->
      // s.stream()).collect(Collectors.toSet());
      // var actionsComputedPrePreMarking = new HashSet<HAction<State, Action>>();
      // boolean changed;
      // do {
      //   var actionsWillComputePrePreMarking =
      //       CollectionUtils.subtract(
      //           actionsShouldComputePrePreMarking, actionsComputedPrePreMarking);

      //   for (var action : actionsWillComputePrePreMarking) {
      //     actionsComputedPrePreMarking.add(action);

      //     for (var a : allActions) {
      //       var statesThatActionIsNotPrePreMarking =
      //           computeStatesThatActionIsNotRequiredToTargetActions(
      //               allTransitions,
      //               reverseTransitions,
      //               controllableStates,
      //               new HashSet<>(Arrays.asList(action)),
      //               a);

      //       for (int i = 1; i < ltss.size(); i++) {
      //         // actionもaもLTS iに存在しない場合、計算不可能
      //         if (!actions.get(i).contains(action) || !actions.get(i).contains(a)) continue;

      //         for (var state : stateMaps.get(i).values()) {
      //           // stateが-1（エラー状態）の計算は不要
      //           if (state.state.equals(-1L)) continue;

      //           // stateにとって、actionを発火するまでにaを発火する必要がある場合
      //           if (!statesThatActionIsNotPrePreMarking.contains(state)) {
      //             var ppmasOfState = prePreMarkingActions.get(state);
      //             if (ppmasOfState == null) {
      //               ppmasOfState = new HashMap<>();
      //               prePreMarkingActions.put(state, ppmasOfState);
      //             }
      //             var ppmasOfAction = ppmasOfState.get(action);
      //             if (ppmasOfAction == null) {
      //               ppmasOfAction = new HashSet<>();
      //               ppmasOfState.put(action, ppmasOfAction);
      //             }
      //             ppmasOfAction.add(a);
      //             actionsShouldComputePrePreMarking.add(a);
      //           }
      //         }
      //       }
      //     }
      //   }
      //   changed = actionsShouldComputePrePreMarking.size() !=
      // actionsComputedPrePreMarking.size();
      // } while (changed);
    }

    static <State, Action> Set<HState<State, Action>> buildControllableStates(
        Map<HState<State, Action>, Map<HAction<State, Action>, HState<State, Action>>>
            transitions) {
      var controllableStates = new HashSet<HState<State, Action>>();
      for (var entry : transitions.entrySet()) {
        HState<State, Action> state = entry.getKey();
        if (entry.getValue().keySet().stream().allMatch(a -> a.isControllable())) {
          controllableStates.add(state);
        }
      }
      return controllableStates;
    }

    static <State, Action>
        Set<HState<State, Action>> computeStatesThatActionIsNotRequiredToTargetActions(
            Map<HState<State, Action>, Map<HAction<State, Action>, HState<State, Action>>>
                transitions,
            Map<HState<State, Action>, Map<HAction<State, Action>, Set<HState<State, Action>>>>
                reverseTransitions,
            Set<HState<State, Action>> controllableStates,
            Set<HAction<State, Action>> targetActions,
            HAction<State, Action> action) {
      var statesWithTarget = new HashSet<HState<State, Action>>();
      for (var e1 : transitions.entrySet()) {
        var s1 = e1.getKey();
        var s1IsWithTarget = false;
        for (var e2 : e1.getValue().entrySet()) {
          var a = e2.getKey();
          var s2 = e2.getValue();
          if (s2.state.equals(-1L)) continue;
          if (targetActions.contains(a)) {
            s1IsWithTarget = true;
            break;
          }
        }
        if (s1IsWithTarget) {
          statesWithTarget.add(s1);
        }
      }

      var targetSideStates = new HashSet<>(statesWithTarget);

      // targetSideStatesを逆遷移でたどり拡張
      boolean changed;
      do {
        var oldTargetSideStates = new HashSet<>(targetSideStates);
        for (var state : oldTargetSideStates) {
          var rts = reverseTransitions.get(state);
          for (var e : rts.entrySet()) {
            var a = e.getKey();
            if (a.equals(action) || targetActions.contains(a)) {
              continue;
            }
            targetSideStates.addAll(e.getValue());
          }
        }
        changed = oldTargetSideStates.size() != targetSideStates.size();
      } while (changed);

      if (targetSideStates.isEmpty()) {
        return targetSideStates;
      }

      // Uncontrollableを考慮し、targetSideStatesから除外
      do {
        var oldTargetSideStates = new HashSet<>(targetSideStates);
        for (var state : oldTargetSideStates) {
          if (statesWithTarget.contains(state)) continue;

          var isControllable = controllableStates.contains(state);
          Boolean actionIsRequired = null;
          var destinations = new HashSet<HState<State, Action>>();
          for (var e : transitions.get(state).entrySet()) {
            var a = e.getKey();
            var s = e.getValue();
            if (s.state.equals(-1L) || s.equals(state)) continue;
            if (a.equals(action)) {
              // Uncontrollableの場合、actionが発火できる=actionが必須である
              if (!isControllable) {
                actionIsRequired = true;
                break;
              }
            } else {
              // Controllableの場合、targetSideStatesであるためには、action以外のAction1つ以上でtargetSideStatesに遷移する必要がある
              // Uncontrollableの場合、targetSideStatesであるためには、全てのActionでtargetSideStatesに遷移する必要がある
              destinations.add(s);
            }
          }
          if (actionIsRequired == null) {
            // Controllableが1つでもtargetSideStates内に遷移する場合、また、Uncontrollableが全てtargetSideStates内に遷移する場合、actionの発火は必須ではない
            var size = CollectionUtils.intersection(destinations, oldTargetSideStates).size();
            if (isControllable) {
              actionIsRequired = size == 0;
            } else {
              actionIsRequired = size < destinations.size();
            }
          }
          if (actionIsRequired) {
            targetSideStates.remove(state);
          }
        }
        changed = oldTargetSideStates.size() != targetSideStates.size();
      } while (changed);

      return targetSideStates;
    }
  }

  @Override
  public void eval(
      Compostate<State, Action> compostate, List<Set<State>> knownMarked, List<Set<State>> goals) {
    if (compostate.isEvaluated()) return;

    var compostateStates = new ArrayList<HState<State, Action>>(ltss.size());
    for (int i = 0; i < ltss.size(); i++) {
      var state = compostate.getStates().get(i);
      var hstate = stateMaps.get(i).get(state);
      compostateStates.add(hstate);
    }
    var exploredActions = new HashSet<HAction<State, Action>>();
    for (var p : compostate.getExploredChildren()) {
      var action = p.getFirst();
      exploredActions.add(action);
    }

    compostate.setupRecommendations();

    for (var action : compostate.getTransitions()) {
      if (exploredActions.contains(action)) continue;

      // markingを発火できるならそれが最善
      var allMarkingActions = markingActions.get(0);
      if (allMarkingActions.contains(action)) {
        compostate.addRecommendation(action, zeroEstimate);
        continue;
      }

      var nextStates = new ArrayList<HState<State, Action>>(ltss.size());
      for (int i = 0; i < ltss.size(); i++) {
        var state = compostateStates.get(i);
        var nextState = transitions.get(i).get(state).get(action);
        if (nextState == null) nextState = state;
        if (nextState.state.equals(-1L)) break;
        nextStates.add(nextState);
      }

      // どれかのLTSで遷移できない場合
      if (nextStates.size() < ltss.size()) {
        compostate.addRecommendation(action, infinityEstimate);
        continue;
      }

      compostate.addRecommendation(action, estimate(nextStates));
    }

    compostate.rankRecommendations();
    compostate.initRecommendations();
  }

  private final Map<Integer, HDist> dists = new HashMap<>();

  private HDist buildDist(int steps) {
    var dist = dists.get(steps);
    if (dist == null) {
      if (steps == 0) dist = HDist.zero;
      else if (steps == Integer.MAX_VALUE) dist = HDist.chasm;
      else dist = new HDist(1, steps);
      dists.put(steps, dist);
    }
    return dist;
  }

  private HEstimate<State, Action> estimate(List<HState<State, Action>> states) {
    var allPreMarkingActions = new HashSet<HAction<State, Action>>();
    for (var state : states) {
      var pmas = preMarkingActions.get(state);
      if (pmas != null) allPreMarkingActions.addAll(pmas);
    }

    // // pre-pre-markingの拡張
    // var actionsShouldGetPrePreMarking = new HashSet<>(allPreMarkingActions);
    // while (!actionsShouldGetPrePreMarking.isEmpty()) {
    //   var nextActionsShouldGetPrePreMarking = new HashSet<HAction<State, Action>>();

    //   for (int i = 1; i < ltss.size(); i++) {
    //     var state = states.get(i);
    //     var ppmasOfState = prePreMarkingActions.get(state);
    //     if (ppmasOfState == null) continue;
    //     for (var action : actionsShouldGetPrePreMarking) {
    //       var ppmasOfAction = ppmasOfState.get(action);
    //       if (ppmasOfAction != null) {
    //         for (var ppma : ppmasOfAction) {
    //           if (allPreMarkingActions.add(ppma)) {
    //             nextActionsShouldGetPrePreMarking.add(ppma);
    //           }
    //         }
    //       }
    //     }
    //   }

    //   actionsShouldGetPrePreMarking = nextActionsShouldGetPrePreMarking;
    // }

    var estimate = new HEstimate<State, Action>(ltss.size(), HDist.chasm);

    // LTS0は考慮しない
    estimate.set(0, buildDist(1));

    for (int i = 1; i < ltss.size(); i++) {
      var state = states.get(i);

      int maxSteps = 1;

      // markingまでの最短ルートの場合
      var stepsToClosestMarkingAction = this.stepsToClosestMarkingAction.get(state);
      if (stepsToClosestMarkingAction != null) {
        maxSteps = stepsToClosestMarkingAction;
      }

      // 他のLTSのpre-markingを発火後、最短でmarkingに到達するとした場合
      var preMarkingActions = new HashSet<HAction<State, Action>>();
      for (var pma : allPreMarkingActions) {
        if (actions.get(i).contains(pma)) {
          preMarkingActions.add(pma);
        }
      }
      for (var preMarkingAction : preMarkingActions) {
        var steps = stepsToReachableActionsWithoutMarking.get(state).get(preMarkingAction);
        if (steps == null) return infinityEstimate;
        Integer additionalSteps = 1;
        if (stepsToClosestMarkingFromAction != null) {
          additionalSteps = stepsToClosestMarkingFromAction.get(preMarkingAction);
          if (additionalSteps == null) return infinityEstimate;
        }
        maxSteps = Math.max(maxSteps, steps + additionalSteps);
      }

      estimate.set(i, buildDist(maxSteps));
    }

    estimate.sortDescending();
    return estimate;
  }

  public static class CompostateRanker<State, Action>
      implements Comparator<Compostate<State, Action>> {
    @Override
    public int compare(Compostate<State, Action> c1, Compostate<State, Action> c2) {
      Recommendation<State, Action> r1 = c1.peekRecommendation();
      Recommendation<State, Action> r2 = c2.peekRecommendation();

      if (r1 == null) return 1;
      if (r2 == null) return -1;

      int controllable1 = r1.getAction().isControllable() ? 1 : 0;
      int controllable2 = r2.getAction().isControllable() ? 1 : 0;
      int result = controllable1 - controllable2;

      if (result == 0 && controllable1 == 0) {
        result = r2.compareTo(r1);
      } else if (result == 0 && controllable1 == 1) {
        if (c1.uncontrollablesCount == 0 && c2.uncontrollablesCount > 0) {
          result = -1;
        } else if (c2.uncontrollablesCount == 0 && c1.uncontrollablesCount > 0) {
          result = 1;
        } else if (c1.getControllablesExpandedCount() == 0
            && c2.getControllablesExpandedCount() > 0) {
          result = -1;
        } else if (c2.getControllablesExpandedCount() == 0
            && c1.getControllablesExpandedCount() > 0) {
          result = 1;
        } else {
          result = r1.compareTo(r2);
        }
      }

      if (result == 0) result = c1.getDepth() - c2.getDepth();

      return result;
    }
  }
}
