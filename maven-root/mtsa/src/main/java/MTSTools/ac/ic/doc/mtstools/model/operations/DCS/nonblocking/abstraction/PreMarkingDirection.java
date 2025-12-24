package MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking.abstraction;

import MTSTools.ac.ic.doc.commons.relations.Pair;
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
import java.util.stream.IntStream;

public class PreMarkingDirection<State, Action> extends Abstraction<State, Action> {
  @Override
  public boolean shouldRecomputeEstimatesOnChange() {
    return false;
  }

  // 全てのLTS
  private final List<LTS<State, Action>> ltss;

  // LTS毎の全てのStateからHStateのマッピング
  private final List<Map<State, HState<State, Action>>> stateMaps;

  // LTS毎の全てのアクション
  private final List<Set<HAction<State, Action>>> actions;

  // 全てのトランジション
  private final List<Map<HState<State, Action>, Map<HAction<State, Action>, HState<State, Action>>>>
      transitions;

  // 全てのmarking
  private final Set<HAction<State, Action>> allMarkingActions;

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

  // 各HStateにおける、未到達のpre-marking
  final Map<HState<State, Action>, Set<HAction<State, Action>>> preMarkingActions;

  // キャッシュ
  private final HEstimate<State, Action> zeroEstimate;
  private final HEstimate<State, Action> infinityEstimate;

  public PreMarkingDirection(DirectedControllerSynthesisNonBlocking<State, Action> dcs) {
    var builder = new Builder<>(dcs);
    ltss = builder.ltss;
    stateMaps = builder.stateMaps;
    actions = builder.actions;
    transitions = builder.transitions;
    allMarkingActions = builder.getAllMarkingActions();
    stepsToReachableActionsWithoutMarking = builder.getStepsToReachableActionsWithoutMarking();
    stepsBetweenActionsWithoutMarking = builder.getStepsBetweenActionsWithoutMarking();
    stepsToClosestMarkingAction = builder.getStepsToClosestMarkingAction();
    stepsToClosestMarkingFromAction = builder.getStepsToClosestMarkingFromAction();
    preMarkingActions = builder.preMarkingActions;
    zeroEstimate = new HEstimate<>(ltss.size(), HDist.zero);
    infinityEstimate = new HEstimate<>(ltss.size(), HDist.chasm);
  }

  // for debug
  private void printPreMarkingActions() {
    var allPreMarkingActions = new HashSet<HAction<State, Action>>();
    for (int i = 1; i < ltss.size(); i++) {
      System.out.println("LTS " + i + ":");
      var initialState = stateMaps.get(i).get(ltss.get(i).getInitialState());
      var preMarkingActions = this.preMarkingActions.get(initialState);
      if (preMarkingActions == null) {
        preMarkingActions = new HashSet<>();
      } else {
        allPreMarkingActions.addAll(preMarkingActions);
      }
      System.out.println("  Pre-Marking actions (" + preMarkingActions.size() + "):");
      System.out.println("    " + preMarkingActions);
    }
    var allActions =
        IntStream.range(1, ltss.size())
            .mapToObj(i -> actions.get(i))
            .flatMap(s -> s.stream())
            .collect(Collectors.toSet());
    System.out.println(
        "All Pre-Marking Actions (" + allPreMarkingActions.size() + "/" + allActions.size() + "):");
    System.out.println("  " + allPreMarkingActions);
  }

  private static class Builder<State, Action> extends SimpleStepsBuilder<State, Action> {
    // 各HStateにおける、未到達のpre-marking
    final Map<HState<State, Action>, Set<HAction<State, Action>>> preMarkingActions =
        new HashMap<>();

    Builder(DirectedControllerSynthesisNonBlocking<State, Action> dcs) {
      super(dcs);

      var actionsMayBePreMarking =
          computeActionsMayBePreMarking(
              ltss,
              stateMaps,
              transitions,
              getFirstActionAndStepsToReachableActionsWithoutMarking(),
              markingActions);

      for (int i = 1; i < ltss.size(); i++) {
        var ts = transitions.get(i);
        var rts = buildReverseTransitions(i);
        for (var a : actionsMayBePreMarking.get(i)) {
          var states = computeStatesHavePreMarking(ts, rts, getAllMarkingActions(), a);
          for (var s : states) {
            preMarkingActions.putIfAbsent(s, new HashSet<>());
            preMarkingActions.get(s).add(a);
          }
        }
      }
    }

    static <State, Action> List<Set<HAction<State, Action>>> computeActionsMayBePreMarking(
        List<LTS<State, Action>> ltss,
        List<Map<State, HState<State, Action>>> stateMaps,
        List<Map<HState<State, Action>, Map<HAction<State, Action>, HState<State, Action>>>>
            transitions,
        Map<
                HState<State, Action>,
                Map<HAction<State, Action>, Pair<HAction<State, Action>, Integer>>>
            firstActionAndStepsToReachableActionsWithoutMarking,
        List<Set<HAction<State, Action>>> markingActions) {
      var actionsMayBePreMarking = new ArrayList<Set<HAction<State, Action>>>(ltss.size());
      for (int i = 0; i < ltss.size(); i++) {
        actionsMayBePreMarking.add(new HashSet<>());
      }

      var allMarkingActions = markingActions.get(0);

      // i=0は無視
      for (int i = 1; i < ltss.size(); i++) {
        // markingが足りない場合、pre-markingの判定ができない
        if (markingActions.get(i).size() != allMarkingActions.size()) {
          continue;
        }
        var ts = transitions.get(i);

        // 各markingの到達までに到達するaction
        var allReachedActions =
            new ArrayList<Set<HAction<State, Action>>>(allMarkingActions.size());

        for (var ma : allMarkingActions) {
          var reachedActions = new HashSet<HAction<State, Action>>();
          var currentState = stateMaps.get(i).get(ltss.get(i).getInitialState());
          while (true) {
            var a =
                firstActionAndStepsToReachableActionsWithoutMarking
                    .get(currentState)
                    .get(ma)
                    .getFirst();
            if (a == ma) break;
            reachedActions.add(a);
            currentState = ts.get(currentState).get(a);
          }
          allReachedActions.add(reachedActions);
        }

        // allReachedActionsで被っているもののみがpre-markingの可能性がある
        actionsMayBePreMarking.get(i).addAll(allReachedActions.get(0));
        for (int j = 1; i < allMarkingActions.size(); i++) {
          actionsMayBePreMarking.get(i).retainAll(allReachedActions.get(j));
        }
      }

      return actionsMayBePreMarking;
    }

    static <State, Action> Set<HState<State, Action>> computeStatesHavePreMarking(
        Map<HState<State, Action>, Map<HAction<State, Action>, HState<State, Action>>> transitions,
        Map<HState<State, Action>, Map<HAction<State, Action>, Set<HState<State, Action>>>>
            reverseTransitions,
        Set<HAction<State, Action>> markingActions,
        HAction<State, Action> action) {
      // action以外による逆遷移
      var filteredReverseTransitions =
          new HashMap<HState<State, Action>, Set<HState<State, Action>>>();
      for (var e1 : reverseTransitions.entrySet()) {
        var s = e1.getKey();
        var states = new HashSet<HState<State, Action>>();
        for (var e2 : e1.getValue().entrySet()) {
          var a = e2.getKey();
          if (a != action) {
            states.addAll(e2.getValue());
          }
        }
        filteredReverseTransitions.put(s, states);
      }

      var statesDontHavePreMarking = new HashSet<HState<State, Action>>();
      // すぐmarkingが発火できるHStateはpre-markingが必要ない
      for (var e : transitions.entrySet()) {
        var s = e.getKey();
        var as = e.getValue().keySet();
        if (markingActions.stream().anyMatch(ma -> as.contains(ma))) {
          statesDontHavePreMarking.add(s);
        }
      }
      // action以外で遡れるHStateもpre-markingが必要ない
      boolean changed;
      do {
        var oldStatesDontHavePreMarking = new HashSet<>(statesDontHavePreMarking);
        for (var s : oldStatesDontHavePreMarking) {
          statesDontHavePreMarking.addAll(filteredReverseTransitions.get(s));
        }
        changed = statesDontHavePreMarking.size() != oldStatesDontHavePreMarking.size();
      } while (changed);

      var statesHavePreMarking = new HashSet<>(transitions.keySet());
      statesHavePreMarking.removeAll(statesDontHavePreMarking);

      return statesHavePreMarking;
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
    for (var s : states) {
      var pmas = preMarkingActions.get(s);
      if (pmas != null) {
        allPreMarkingActions.addAll(pmas);
      }
    }

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

      // 他のLTSのpre-markingを発火後、最短ステップでmarkingに到達するとした場合
      var preMarkingActions = new HashSet<HAction<State, Action>>();
      for (var pma : allPreMarkingActions) {
        if (actions.get(i).contains(pma)) {
          preMarkingActions.add(pma);
        }
      }
      if (stepsBetweenActionsWithoutMarking == null || stepsToClosestMarkingFromAction == null) {
        // 他のLTSのpre-markingを発火後、1ステップでmarkingに到達するとした場合
        for (var preMarkingAction : preMarkingActions) {
          var steps = stepsToReachableActionsWithoutMarking.get(state).get(preMarkingAction);
          if (steps == null) return infinityEstimate;
          maxSteps = Math.max(maxSteps, steps + 1);
        }
      } else if (!preMarkingActions.isEmpty()) {
        var steps =
            StepsCalculator.calculateLowerBound(
                stepsToReachableActionsWithoutMarking,
                stepsBetweenActionsWithoutMarking,
                stepsToClosestMarkingFromAction,
                state,
                preMarkingActions);
        maxSteps = Math.max(maxSteps, steps);
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
