# MTSA Waseda リポジトリ構造

このドキュメントでは、MTSAの研究開発で主に利用するディレクトリとクラスを整理する。

すべてのクラスを網羅することが目的ではなく、Stepwise Controller Synthesis（SCS）およびLTS最小化の開発で必要になる場所を把握することを目的とする。

---

## 1. リポジトリ全体

主な構造は以下の通り。

```text
mtsa_waseda/
├── CLAUDE.md
├── README.md
├── docs/
│   ├── PROJECT_STRUCTURE.md
│   ├── DEVELOPMENT_WORKFLOW.md
│   └── research/
│       └── MINIMISATION.md
│
└── maven-root/
    └── mtsa/
        ├── pom.xml
        ├── settings.xml
        └── src/
            ├── main/
            │   ├── java/
            │   └── resources/
            │
            └── test/
                ├── java/
                ├── resources/
                └── benchmarks/
```

実際のJavaプロジェクトの中心は、

```text
maven-root/mtsa
```

である。

実装やテストを調査するときは、基本的にここから確認する。

---

## 2. LTSコア

```text
maven-root/mtsa/src/main/java/ltsa/lts/
```

LTS、FSP、LTSA由来の主要処理が存在する。

### Minimiser.java

```text
ltsa/lts/Minimiser.java
```

LTSの最小化処理。

現在の実装では、

```java
BitSet[] E;
```

によって状態対の等価候補を管理している。

また、

```java
BitSet[] A;
```

によって各状態から実行可能な事象を管理する。

大まかな処理は、

```text
候補状態対を作成
        ↓
双模倣条件を満たさない状態対を削除
        ↓
変化がなくなるまで反復
        ↓
残った等価状態を統合
```

という構造になっている。

ただし `minimise()` では、これ以外にもtau処理やEND状態処理などが行われる。

詳細は、

```text
docs/research/MINIMISATION.md
```

を参照する。

---

### CompactState.java

```text
ltsa/lts/CompactState.java
```

MTSA/LTSAで使用されるLTSの中心的なデータ構造。

主なフィールド：

```java
public int maxStates;
public String[] alphabet;
public EventState[] states;
public int endseq;
```

概念的には、

```text
状態集合
事象集合
遷移集合
END状態
```

などを保持する。

この研究ブランチでは、Stepwise Controller Synthesisに関係する追加情報も保持している。

例：

```java
inputModels
inputPartControllers
analyzedModels
componentModels
ideal_monitoredModels
actual_monitoredModels
cost
influence_quantity
num_of_PartController
```

新しい最小化アルゴリズムを実装するときは、これらの研究用情報を不用意に変更しない。

---

### EventState.java

```text
ltsa/lts/EventState.java
```

状態間の遷移を表現する中心的なクラス。

概念的には、

```text
現在状態 --event--> next状態
```

を表す。

同じeventから複数の状態へ遷移する非決定的遷移も表現できる。

新しい最小化アルゴリズムでは、

```text
1つのeventにつき遷移先は1つ
```

とは仮定しないこと。

---

### EventStateUtils.java

```text
ltsa/lts/EventStateUtils.java
```

`EventState` に対する追加操作を提供する。

状態番号の変更や遷移集合の操作など、最小化後のLTS生成でも利用される。

---

### CompositeState.java

```text
ltsa/lts/CompositeState.java
```

複数のLTSの合成結果や、各種LTS操作を管理する。

最小化処理を呼び出す経路の一つでもある。

---

## 3. Dispatcher

```text
maven-root/mtsa/src/main/java/ltsa/dispatcher/
```

### TransitionSystemDispatcher.java

```text
ltsa/dispatcher/TransitionSystemDispatcher.java
```

GUI、LTSAコア、MTSA側の処理などを接続する役割を持つ。

LTSに対する最小化では、概ね以下の経路が存在する。

```text
TransitionSystemDispatcher.minimise(...)
        ↓
new Minimiser(...)
        ↓
Minimiser.minimise()
```

`CompactState` がMTSを表している場合は別の最小化処理が利用される。

したがって、今回のLTS最小化研究では、

```text
LTS用の Minimiser
```

と

```text
MTS用の Minimiser
```

を混同しないこと。

---

## 4. Controller Synthesis

```text
maven-root/mtsa/src/main/java/MTSSynthesis/
```

制御器合成、ゲーム、ControllerGoalなどに関するコードが存在する。

今回の最小化アルゴリズム変更では、可能な限りこの領域を変更しない。

最小化アルゴリズムの変更だけでDCS/SCSの意味を変更しないこと。

---

## 5. MTSTools

```text
maven-root/mtsa/src/main/java/MTSTools/
```

Modal Transition Systemに関するデータ構造・操作・refinementなどが存在する。

MTS用の最小化処理も存在する。

今回の最初の実験対象は、

```text
ltsa.lts.Minimiser
```

によるLTS最小化である。

ただし、データ構造や既存アルゴリズムの参考として調査することは可能。

---

## 6. UI

```text
maven-root/mtsa/src/main/java/ltsa/ui/
```

MTSAのGUIに関するコード。

この研究ブランチには、Stepwise Controller Synthesisに関係するUI実装も含まれている。

例：

```text
StepwiseWindow.java
HPWindow.java
```

新しい最小化アルゴリズムの実装初期段階では、原則としてUIを変更しない。

アルゴリズム切り替え機能が必要になった場合も、まずコードや設定によって切り替え可能にする方法を検討する。

---

## 7. テスト

主なテストは、

```text
maven-root/mtsa/src/test/java/
```

に存在する。

テスト用LTSやその他のリソースは、

```text
maven-root/mtsa/src/test/resources/
```

に存在する。

ベンチマーク関連は、

```text
maven-root/mtsa/src/test/benchmarks/
```

に存在する。

### TestBisimilarFSPs.java

```text
IntegrationTests/TestBisimilarFSPs.java
```

LTS間のbisimilarity/refinementを検証する既存テスト。

新しい最小化アルゴリズムの結果を検証するときに、この周辺の仕組みを参考にする。

---

## 8. ビルド

リポジトリ直下から、

```bash
cd maven-root/mtsa
```

へ移動する。

既存READMEに記載されているビルド方法は、

```bash
mvn clean
mvn install -DskipTests=true
```

である。

テストを含める場合は、

```bash
mvn test
```

を利用する。

---

## 9. 最小化研究で最初に確認する場所

Dirty-state Signature Refinementなどの新しい最小化アルゴリズムを実装するときは、最低限以下を確認する。

```text
maven-root/mtsa/src/main/java/ltsa/lts/Minimiser.java
maven-root/mtsa/src/main/java/ltsa/lts/CompactState.java
maven-root/mtsa/src/main/java/ltsa/lts/EventState.java
maven-root/mtsa/src/main/java/ltsa/lts/EventStateUtils.java
maven-root/mtsa/src/main/java/ltsa/lts/CompositeState.java
maven-root/mtsa/src/main/java/ltsa/dispatcher/TransitionSystemDispatcher.java
maven-root/mtsa/src/test/java/
```

必要に応じて、これらから呼び出されるクラスを追加調査する。
