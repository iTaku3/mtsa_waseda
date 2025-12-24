package MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking.abstraction;

import MTSTools.ac.ic.doc.commons.collections.InitMap;
import MTSTools.ac.ic.doc.commons.collections.InitMap.Factory;
import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSTools.ac.ic.doc.mtstools.model.impl.MarkedLTSImpl;
import MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections15.CollectionUtils;

public class BaseBuilder<State, Action> {
  public final List<LTS<State, Action>> ltss;
  public final List<Map<State, HState<State, Action>>> stateMaps;
  public final List<Set<HAction<State, Action>>> actions;
  public final List<Map<HState<State, Action>, Map<HAction<State, Action>, HState<State, Action>>>>
      transitions;
  public final List<Set<HAction<State, Action>>> markingActions;

  public Set<HAction<State, Action>> getAllMarkingActions() {
    return markingActions.get(0);
  }

  public BaseBuilder(DirectedControllerSynthesisNonBlocking<State, Action> dcs) {
    ltss = dcs.ltss;
    stateMaps = buildStateMaps(ltss, dcs.defaultTargets);
    actions = buildActions(ltss, dcs.alphabet);
    transitions = buildTransitions(ltss, dcs.alphabet, stateMaps);
    markingActions = extractMarkingActions(ltss, dcs.alphabet, actions);
  }

  public BaseBuilder(BaseBuilder<State, Action> builder) {
    this.ltss = builder.ltss;
    this.stateMaps = builder.stateMaps;
    this.actions = builder.actions;
    this.transitions = builder.transitions;
    this.markingActions = builder.markingActions;
  }

  private static <State, Action> List<Map<State, HState<State, Action>>> buildStateMaps(
      List<LTS<State, Action>> ltss, List<Set<State>> defaultTargets) {
    var stateMaps = new ArrayList<Map<State, HState<State, Action>>>(ltss.size());
    int hash = 0;
    for (int lts = 0; lts < ltss.size(); lts++) {
      var stateMap = new HashMap<State, HState<State, Action>>();
      for (var state : ltss.get(lts).getStates()) {
        stateMap.put(
            state,
            new HState<State, Action>(
                lts, state, hash, defaultTargets.get(lts).contains(state), ltss));
        hash++;
      }
      stateMaps.add(stateMap);
    }
    return stateMaps;
  }

  private static <State, Action> List<Set<HAction<State, Action>>> buildActions(
      List<LTS<State, Action>> ltss, Alphabet<State, Action> alphabet) {
    var actions = new ArrayList<Set<HAction<State, Action>>>(ltss.size());
    for (int lts = 0; lts < ltss.size(); lts++) {
      var currentActions = new HashSet<HAction<State, Action>>();
      for (var br : ltss.get(lts).getTransitions().values()) {
        for (var p : br) {
          currentActions.add(alphabet.getHAction(p.getFirst()));
        }
      }
      actions.add(currentActions);
    }
    return actions;
  }

  private static <State, Action>
      List<Map<HState<State, Action>, Map<HAction<State, Action>, HState<State, Action>>>>
          buildTransitions(
              List<LTS<State, Action>> ltss,
              Alphabet<State, Action> alphabet,
              List<Map<State, HState<State, Action>>> stateMaps) {
    var transitions =
        new ArrayList<
            Map<HState<State, Action>, Map<HAction<State, Action>, HState<State, Action>>>>(
            ltss.size());
    for (int i = 0; i < ltss.size(); i++) {
      transitions.add(
          new InitMap<HState<State, Action>, Map<HAction<State, Action>, HState<State, Action>>>(
              HashMap.class));
    }

    for (int i = 0; i < ltss.size(); i++) {
      var ts = transitions.get(i);
      var stateMap = stateMaps.get(i);
      for (var entry : ltss.get(i).getTransitions().entrySet()) {
        var s1 = stateMap.get(entry.getKey());
        for (var transition : entry.getValue()) {
          var a = alphabet.getHAction(transition.getFirst());
          var s2 = stateMap.get(transition.getSecond());
          ts.get(s1).put(a, s2);
        }
      }
    }
    return transitions;
  }

  // HACK: markingActionsはLTS0から抜き出すのではなく、親から引数として渡されるべき
  private static <State, Action> List<Set<HAction<State, Action>>> extractMarkingActions(
      List<LTS<State, Action>> ltss,
      Alphabet<State, Action> alphabet,
      List<Set<HAction<State, Action>>> actions) {
    var allMarkingActions = new HashSet<HAction<State, Action>>();
    // 最初のLTSはmarkingのみを考慮したもの
    var markedLts = (MarkedLTSImpl<State, Action>) ltss.get(0);
    var markedStates = markedLts.getMarkedStates();
    for (var transition : markedLts.getTransitions(markedLts.getInitialState())) {
      if (markedStates.contains(transition.getSecond())) {
        allMarkingActions.add(alphabet.getHAction(transition.getFirst()));
      }
    }

    var markingActions = new ArrayList<Set<HAction<State, Action>>>(actions.size());
    for (int lts = 0; lts < actions.size(); lts++) {
      markingActions.add(
          new HashSet<>(CollectionUtils.intersection(actions.get(lts), allMarkingActions)));
    }
    return markingActions;
  }

  public Map<HState<State, Action>, Map<HAction<State, Action>, HState<State, Action>>>
      getTransitions(int startInclusive, int endExclusive) {
    var transitions =
        new HashMap<HState<State, Action>, Map<HAction<State, Action>, HState<State, Action>>>();

    for (int i = startInclusive; i < endExclusive; i++) {
      transitions.putAll(this.transitions.get(i));
    }

    return transitions;
  }

  public Map<HState<State, Action>, Map<HAction<State, Action>, Set<HState<State, Action>>>>
      buildReverseTransitions(int i) {
    var reverseTransitions =
        new InitMap<HState<State, Action>, Map<HAction<State, Action>, Set<HState<State, Action>>>>(
            new Factory<>() {
              @Override
              public Map<HAction<State, Action>, Set<HState<State, Action>>> newInstance() {
                return new InitMap<>(HashSet.class);
              }
            });

    for (var e1 : transitions.get(i).entrySet()) {
      var s1 = e1.getKey();
      for (var e2 : e1.getValue().entrySet()) {
        var a = e2.getKey();
        var s2 = e2.getValue();
        reverseTransitions.get(s2).get(a).add(s1);
      }
    }

    return reverseTransitions;
  }

  public Map<HState<State, Action>, Map<HAction<State, Action>, Set<HState<State, Action>>>>
      buildReverseTransitions(int startInclusive, int endExclusive) {
    var reverseTransitions =
        new HashMap<
            HState<State, Action>, Map<HAction<State, Action>, Set<HState<State, Action>>>>();

    for (int i = startInclusive; i < endExclusive; i++) {
      reverseTransitions.putAll(buildReverseTransitions(i));
    }

    return reverseTransitions;
  }
}
