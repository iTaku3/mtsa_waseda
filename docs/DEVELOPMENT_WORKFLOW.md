# MTSA Waseda 開発ルール

このリポジトリは研究用コードを含むため、変更内容の追跡可能性と実験の再現性を重視する。

---

## 1. 基本方針

原則として、

```text
1つのブランチ = 1つの目的
```

とする。

例えば、

```text
最小化アルゴリズム実装
README整理
.DS_Store削除
```

は別々のブランチで行う。

---

## 2. SCS研究の基準ブランチ

現在の研究では、

```text
SCS/DynamicPolicy-ApplyMinimize
```

を基準ブランチとする。

このブランチに直接実装するのではなく、新しいブランチを作る。

---

## 3. ブランチ名

用途に応じて以下を使用する。

```text
docs/<内容>
research/<研究実装>
perf/<性能改善・計測>
fix/<バグ修正>
refactor/<リファクタリング>
```

例：

```text
docs/claude-research-guidance
research/dirty-signature-minimiser
perf/minimiser-benchmark
fix/minimiser-end-state
```

---

## 4. ブランチ作成

Dirty-state Signature Refinementを実装する場合：

```bash
git switch SCS/DynamicPolicy-ApplyMinimize
git pull --ff-only origin SCS/DynamicPolicy-ApplyMinimize
git switch -c research/dirty-signature-minimiser
```

---

## 5. 作業開始前

必ず、

```bash
git status
```

を確認する。

未コミット変更が残っている場合は、それが今回の作業に関係するものか確認する。

また、変更対象コードとその呼び出し元を調査してから実装する。

---

## 6. コミット

コミットは、可能な限り意味のある単位に分ける。

例：

```text
test: add differential tests for LTS minimisation
feat: add dirty signature minimiser
perf: avoid recomputing clean signatures
docs: document minimisation experiment
```

以下のような曖昧なメッセージは避ける。

```text
update
fix
changes
test
```

---

## 7. コミット前

最低限、

```bash
git status
git diff --check
git diff
```

を確認する。

その後、関連するテストを実行する。

---

## 8. Pull Request

変更完了後、

```text
作業ブランチ
    ↓
SCS/DynamicPolicy-ApplyMinimize
```

へのPull Requestを作成する。

Pull Requestでは最低限以下を書く。

- 何を変更したか
- なぜ変更したか
- どのアルゴリズム・手法を実装したか
- 何を変更していないか
- どのテストを実行したか
- 正しさをどう検証したか
- 性能をどう比較したか
- 既知の問題

---

## 9. 研究実装で特に避けること

アルゴリズム実装と同じPull Requestで、以下を行わない。

- Javaバージョン変更
- Mavenの大幅変更
- 大規模フォーマット
- 不要コード大量削除
- UI全面整理
- 関係ないリファクタリング
- `.DS_Store` 等の大量cleanup

これらは、アルゴリズム変更による性能・動作差を分析しづらくする。

---

## 10. Claude Codeを使用する場合

Claude Codeはリポジトリルートから起動する。

```bash
cd /path/to/mtsa_waseda
claude
```

作業ブランチへ移動してから起動することを推奨する。

例：

```bash
git switch SCS/DynamicPolicy-ApplyMinimize
git pull --ff-only origin SCS/DynamicPolicy-ApplyMinimize
git switch -c research/dirty-signature-minimiser

claude
```

Claudeには、いきなり実装させない。

まず、

```text
既存実装調査
    ↓
変更案
    ↓
実装
    ↓
テスト
```

の順で進める。

---

## 11. Claudeへの最初の指示例

```text
CLAUDE.md、docs/PROJECT_STRUCTURE.md、
docs/DEVELOPMENT_WORKFLOW.md、
docs/research/MINIMISATION.md を読んでください。

まずコードは変更しないでください。

現在のLTS最小化実装について、

・呼び出し経路
・前処理
・等価性判定
・tau処理
・END/STOP
・ERROR
・非決定的遷移
・quotient LTS生成
・MarkedCompactState

を実際のコードから調査してください。

その後、既存Minimiserを残したまま
Dirty-state Signature Refinementを追加するための
最小限の実装方針を提案してください。
```

---

## 12. Claudeに許可しない操作

ユーザーから明示的な指示がない限り、Claudeは以下を行わない。

```text
git push
git merge
git reset --hard
force push
ブランチ削除
```

まずユーザーがdiffを確認できる状態まで進める。

---

## 13. `.DS_Store` などについて

現在のリポジトリには `.DS_Store` などの環境依存ファイルが存在する。

これらのcleanupは、研究アルゴリズムのPull Requestには含めない。

必要になった場合は、

```text
cleanup/remove-ds-store
```

など、別ブランチで対応する。
