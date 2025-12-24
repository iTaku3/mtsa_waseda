package MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking.abstraction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StepsCalculator {
  // 計算用の無限大（オーバーフローしない程度の大きな値）
  private static final long INF = 1_000_000_000_000L;

  /**
   * スタートとゴールを固定したTSP（ハミルトン路）の最短距離の下界を算出する
   *
   * @return 理論上の最短距離（下界値）。到達不可能な場合は Integer.MAX_VALUE を返す。
   */
  public static <State, Action> int calculateLowerBound(
      Map<HState<State, Action>, Map<HAction<State, Action>, Integer>> stepsToReachableActions,
      Map<HAction<State, Action>, Map<HAction<State, Action>, Integer>> stepsBetweenActions,
      Map<HAction<State, Action>, Integer> stepsToClosestMarkingFromAction,
      HState<State, Action> state,
      Set<HAction<State, Action>> preMarkingActions) {

    // 1. 登場する全中間ノードをリストアップ
    var nodeList = new ArrayList<>(preMarkingActions);
    int n = nodeList.size(); // 中間ノードの数
    int size = n + 2; // Start + 中間 + Goal

    int startIndex = 0;
    int goalIndex = size - 1;

    // 2. コスト行列の作成 (初期値は全てINF)
    long[][] costMatrix = new long[size][size];
    for (long[] row : costMatrix) {
      Arrays.fill(row, INF);
    }

    // ノードID -> 行列インデックスのマップ (1 ~ n)
    Map<HAction<State, Action>, Integer> nodeToIndex = new HashMap<>();
    for (int i = 0; i < n; i++) {
      nodeToIndex.put(nodeList.get(i), i + 1);
    }

    // --- 行列へ値を埋める ---

    // A. 中間ノード(i) -> 中間ノード(j) および 中間ノード(i) -> Goal
    for (var a1 : preMarkingActions) {
      var a1i = nodeToIndex.get(a1);

      // from Start
      var stepsFromStart = stepsToReachableActions.get(state).get(a1);
      if (stepsFromStart != null) {
        costMatrix[startIndex][a1i] = stepsFromStart;
      }

      // 中間ノード同士
      for (var a2 : preMarkingActions) {
        var a2i = nodeToIndex.get(a2);

        var stepsFromA1ToA2 = stepsBetweenActions.getOrDefault(a1, new HashMap<>()).get(a2);

        if (stepsFromA1ToA2 != null) {
          var stepsToA1 = stepsToReachableActions.get(state).get(a1);
          var stepsToA2 = stepsToReachableActions.get(state).get(a2);
          if (stepsToA1 != null && stepsToA2 != null) {
            var alternativeStepsFromA1ToA2 = stepsToA2 - stepsToA1;
            if (alternativeStepsFromA1ToA2 > stepsFromA1ToA2) {
              stepsFromA1ToA2 = alternativeStepsFromA1ToA2;
            }
          }

          costMatrix[a1i][a2i] = stepsFromA1ToA2;
        }
      }

      // to Goal
      var stepsToGoal = stepsToClosestMarkingFromAction.get(a1);
      if (stepsToGoal != null) {
        costMatrix[a1i][goalIndex] = stepsToGoal;
      }
    }

    // B. Goal -> Start (ダミーエッジ: コスト0)
    // これにより、Goalまで行ったらStartに戻るループが完成し、割当問題として解けるようになる
    costMatrix[goalIndex][startIndex] = 0;

    // 3. ハンガリアン法で最小コストを計算
    var steps = hungarianAlgorithm(costMatrix);
    return steps >= Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) steps;
  }

  /** ハンガリアン法 (O(N^3)) の実装 最小重み完全マッチングを求める */
  private static long hungarianAlgorithm(long[][] matrix) {
    int n = matrix.length;
    long[] u = new long[n + 1];
    long[] v = new long[n + 1];
    int[] p = new int[n + 1];
    int[] way = new int[n + 1];

    for (int i = 1; i <= n; i++) {
      p[0] = i;
      int j0 = 0;
      long[] minv = new long[n + 1];
      Arrays.fill(minv, INF);
      boolean[] used = new boolean[n + 1];

      do {
        used[j0] = true;
        int i0 = p[j0];
        long delta = INF;
        int j1 = 0;

        for (int j = 1; j <= n; j++) {
          if (!used[j]) {
            long cur = matrix[i0 - 1][j - 1] - u[i0] - v[j];
            if (cur < minv[j]) {
              minv[j] = cur;
              way[j] = j0;
            }
            if (minv[j] < delta) {
              delta = minv[j];
              j1 = j;
            }
          }
        }

        for (int j = 0; j <= n; j++) {
          if (used[j]) {
            u[p[j]] += delta;
            v[j] -= delta;
          } else {
            minv[j] -= delta;
          }
        }
        j0 = j1;
      } while (p[j0] != 0);

      do {
        int j1 = way[j0];
        p[j0] = p[j1];
        j0 = j1;
      } while (j0 != 0);
    }

    // マッチングコストの合計（ポテンシャルの和）は u[0] に入るわけではないため
    // -v[0] に答えが集約される実装パターンを使用
    return -v[0];
  }
}
