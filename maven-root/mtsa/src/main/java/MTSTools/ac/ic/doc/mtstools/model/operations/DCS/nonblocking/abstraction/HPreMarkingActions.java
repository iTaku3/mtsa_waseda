package MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking.abstraction;

import MTSTools.ac.ic.doc.commons.relations.Pair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HPreMarkingActions<State, Action> {
  // LTS毎に異なる
  // hashの計算に用いる
  private final List<Pair<HAction<State, Action>, Integer>> factors;

  // ステート毎に異なる
  private final List<Integer> counts;
  private Integer count;

  private final int hash;

  public HPreMarkingActions(
      List<Pair<HAction<State, Action>, Integer>> factors, List<Integer> counts, int hash) {
    this.factors = factors;
    this.counts = counts;
    this.hash = hash;
  }

  public static <State, Action> List<Pair<HAction<State, Action>, Integer>> computeFactors(
      Map<HAction<State, Action>, Integer> preMarkingActions) {
    List<Pair<HAction<State, Action>, Integer>> factors = new ArrayList<>(preMarkingActions.size());

    int prevFactor = 1;
    int prevCount = 0;
    for (Map.Entry<HAction<State, Action>, Integer> entry : preMarkingActions.entrySet()) {
      HAction<State, Action> action = entry.getKey();
      int factor = prevFactor * (prevCount + 1);
      factors.add(new Pair<>(action, factor));
      prevFactor = factor;
      prevCount = entry.getValue();
    }

    return factors;
  }

  public static <State, Action> int computeHash(
      List<Pair<HAction<State, Action>, Integer>> factors, List<Integer> counts) {
    int hash = 0;
    for (int i = 0; i < counts.size(); i++) {
      hash += counts.get(i) * factors.get(i).getSecond();
    }
    return hash;
  }

  // いずれか1つのpre-markingを実行後のHPreMarkingActions
  public Map<HPreMarkingActions<State, Action>, HAction<State, Action>> getNexts() {
    Map<HPreMarkingActions<State, Action>, HAction<State, Action>> nexts = new HashMap<>();
    if (!isEmpty()) {
      for (int i = 0; i < factors.size(); i++) {
        if (counts.get(i) > 0) {
          List<Integer> nextCounts = new ArrayList<>(counts);
          nextCounts.set(i, counts.get(i) - 1);
          nexts.put(
              new HPreMarkingActions<>(factors, nextCounts, hash - factors.get(i).getSecond()),
              factors.get(i).getFirst());
        }
      }
    }
    return nexts;
  }

  // pre-markingが空かどうか
  boolean isEmpty() {
    return hash == 0;
  }

  int getCount() {
    if (count == null) {
      count = 0;
      if (!isEmpty()) {
        for (int i = 0; i < counts.size(); i++) {
          count += counts.get(i);
        }
      }
    }
    return count;
  }

  public Map<HAction<State, Action>, Integer> toMap() {
    Map<HAction<State, Action>, Integer> map = new HashMap<>();
    for (int i = 0; i < factors.size(); i++) {
      map.put(factors.get(i).getFirst(), counts.get(i));
    }
    return map;
  }

  @Override
  public int hashCode() {
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    boolean result = false;
    if (obj != null && obj instanceof HPreMarkingActions) {
      @SuppressWarnings("unchecked")
      HPreMarkingActions<State, Action> other = (HPreMarkingActions<State, Action>) obj;
      result = this.hash == other.hash;
    }
    return result;
  }

  @Override
  public String toString() {
    return toMap().toString();
  }
}
