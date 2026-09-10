# LTS最小化アルゴリズム研究

## 1. 目的

Stepwise Controller Synthesis（SCS）では、各段階で生成される部分制御器に最小化を適用し、後続段階へ渡されるLTSの状態数を削減している。

現在の研究では、最小化処理そのもののコストも問題となる。

そこで、

```text
現在の最小化アルゴリズム
```

を、

```text
より効率的な最小化アルゴリズム
```

へ変更できるか検討する。

最初の候補として、

```text
Dirty-stateを利用したSignature Refinement
```

を実装・評価する。

---

## 2. 重要な要求

アルゴリズムを変更する目的は、

```text
最小化時間
主記憶量
```

を削減することである。

最小化によって保存されるべき制御器の意味を変更することは目的ではない。

特に、

```text
制御器が実行可能なアクション列
```

が、最小化アルゴリズム変更によって変化してはいけない。

既存アルゴリズムと新アルゴリズムで、状態番号が同じになる必要はない。

---

## 3. 現在の実装

対象：

```text
maven-root/mtsa/src/main/java/ltsa/lts/Minimiser.java
```

現在は、

```java
BitSet[] E;
```

という状態対の表を利用している。

概念的に、

```text
E[i][j] = true
```

なら、

```text
状態iと状態jはまだ等価候補
```

という意味になる。

---

## 4. 初期候補の作成

現在の実装では、

```java
BitSet[] A;
```

を利用して、各状態から発生可能な事象集合を計算する。

概念的には、

```text
A[i] = 状態iから実行可能な事象集合
```

である。

最初に、

```text
A[i] == A[j]
```

となる状態対だけを等価候補として残す。

つまり、

```text
実行可能な事象集合が異なる状態
```

は最初から等価ではないと判定する。

---

## 5. 等価候補の更新

候補状態対 `(i, j)` について、

```text
i --a--> i'
```

という遷移があれば、

```text
j --a--> j'
```

となる遷移が存在し、

```text
E[i'][j'] == true
```

となる必要がある。

逆方向についても同様に確認する。

条件を満たさない場合、

```text
E[i][j] = false
```

とする。

この処理を、変更がなくなるまで反復する。

概念的には、

```text
候補状態対を作る
    ↓
双模倣条件に反する状態対を削除
    ↓
削除によって別の状態対が非等価になる
    ↓
さらに削除
    ↓
固定点
```

という処理である。

---

## 6. 現在の方式で注目する点

状態数を、

```text
n
```

とすると、状態対は最大、

```text
n × n
```

存在する。

現在は `BitSet` を利用しているため、単純なbooleanの二次元配列より圧縮されている。

ただし、状態対関係そのものは最大で `n^2` 個の候補を扱うため、状態数の増加に対してメモリ・計算コストが増大する可能性がある。

---

## 7. `minimise()` の注意点

現在の `Minimiser#minimise()` は、

```text
状態対の等価性を判定する
```

だけではない。

その前後で特殊な処理を行っている。

したがって、新しいアルゴリズムを実装するときに、

```text
minimise() 全体
```

をそのまま置換してはいけない。

---

## 8. `removeNonDetTau()`

設定によって、

```java
machine.removeNonDetTau();
```

が呼ばれる。

これはtauを含む非決定的な構造を処理する。

新しい最小化でも、この前処理の意味を確認し、必要なら既存処理を維持する。

---

## 9. ENDとSTOP

`machine.endseq >= 0` の場合、現在の最小化ではEND状態とSTOP状態を区別するため、一時的な特殊遷移を追加する。

つまり、

```text
遷移がないから同じ状態
```

としてENDとSTOPを誤って統合しないようにしている。

新アルゴリズムでも、この区別を維持する必要がある。

---

## 10. tau処理

現在の実装では、tau遷移が存在する場合、

```text
tau reachability
tau closure
遷移の展開
```

などを行っている。

ソースコード上の `minimise()` は、

```text
observational equivalence
```

による最小化として記述されている。

一方、状態対を更新する中心部分はbisimulation型の固定点計算になっている。

したがって、

```text
入力LTSそのものに単純なstrong bisimulationを適用
```

するだけでは、現在の `minimise()` と完全に同じ意味にならない可能性がある。

最初の実装では、既存のtau前処理をできるだけ維持し、

```text
状態対による等価状態計算
```

の部分を新アルゴリズムへ変更する方針を優先する。

---

## 11. quotient LTSの生成

現在の `makeNewMachine()` では、

```text
等価状態
```

を1つの状態としてまとめ、新しいLTSを構築する。

主な処理：

- 旧状態から新状態への対応付け
- 遷移先状態番号の変換
- 統合状態からの遷移の結合
- END状態番号の更新
- reflexive tauの除去

新アルゴリズムでも、最終的に同様のquotient LTSを生成する必要がある。

---

## 12. MarkedCompactState

入力が、

```text
MarkedCompactState
```

の場合、現在の最小化は状態統合後にmarked stateの番号を変更している。

新アルゴリズムでもこの情報を失わないようにする。

---

## 13. ERROR状態

`EventState.next` が通常の状態番号ではなく、負の値を持つ場合がある。

ERRORなどの特殊状態の可能性があるため、新しいsignatureでは、

```text
block[next]
```

と単純に配列アクセスしてはいけない。

既存コードで負の状態番号が何を意味しているかを確認し、明示的に扱うこと。

---

## 14. 非決定的遷移

MTSAのLTSは、

```text
s --a--> t1
s --a--> t2
```

のように、同一事象から複数状態へ遷移できる。

したがってsignatureは、

```text
event → 1つのblock
```

ではなく、

```text
event → 到達可能なblock集合
```

を表現できる必要がある。

---

# 新しい候補：Signature Refinement

## 15. 基本アイデア

状態対を管理する代わりに、

```text
状態のグループ
```

を管理する。

各状態 `s` について、

```text
p(s)
```

を、

```text
現在状態sが所属しているブロックID
```

とする。

例えば、

```text
p(s0) = 0
p(s1) = 0
p(s2) = 1
p(s3) = 1
```

なら、

```text
{s0, s1}
{s2, s3}
```

というpartitionを表す。

---

## 16. Signature

基本的なsignatureは、

```text
Sig_p(s) = {(a, p(t)) | s --a--> t}
```

と考える。

つまり、

```text
どの事象で
現在どのブロックへ遷移できるか
```

によって状態を特徴付ける。

例：

```text
s1 --a--> x
s1 --b--> y

p(x) = 2
p(y) = 4
```

なら、

```text
Sig(s1) = {(a, 2), (b, 4)}
```

となる。

---

## 17. Partition Refinement

同じブロックに所属する状態でも、signatureが異なる場合は別ブロックへ分割する。

例：

```text
Sig(s1) = {(a, 2), (b, 4)}
Sig(s2) = {(a, 2), (b, 5)}
```

なら、`s1` と `s2` は別ブロックへ分割する。

これをpartitionが変化しなくなるまで反復する。

---

# Dirty-state optimisation

## 18. 通常のSignature Refinementの問題

単純な実装では、partitionを更新するたびに、

```text
全状態
```

のsignatureを再計算する。

しかし、多くの状態では前回からsignatureが変化しない可能性がある。

---

## 19. Dirty state

状態 `s` のsignatureは、その遷移先のblock IDによって決まる。

したがって、

```text
s --a--> t
```

について、

```text
p(t)
```

が変化すると、

```text
Sig(s)
```

も変化する可能性がある。

このような状態 `s` を、

```text
dirty state
```

として再計算対象にする。

---

## 20. predecessor

Dirty stateを効率的に見つけるには、

```text
ある状態tへ遷移してくる状態
```

を把握する必要がある。

つまり、

```text
predecessor
```

情報を管理する。

ある状態 `t` のblock IDが変わったら、

```text
Pred(t)
```

に含まれる状態をdirtyにする。

---

## 21. 最大ブロックのIDを維持する

ブロック `B` が、

```text
B1
B2
B3
```

へ分割された場合を考える。

最大のブロックには `B` の元のblock IDをそのまま使用する。

小さいブロックだけ新しいblock IDを割り当てる。

例：

```text
B : 1000状態

↓ 分割

B1 : 900状態
B2 : 70状態
B3 : 30状態
```

の場合、

```text
B1 → 元のID
B2 → 新ID
B3 → 新ID
```

とする。

これにより、多数の状態のblock IDが変更されることを防ぐ。

その結果、

```text
dirtyになるpredecessor
```

を減らすことができる。

---

## 22. 実装方針

最初から既存の `Minimiser` を削除しない。

概念的には、

```text
Legacy Minimiser
Dirty Signature Minimiser
```

を両方利用できるようにする。

最初の実装では、

```text
既存の前処理
    ↓
Dirty-state Signature Refinement
    ↓
互換性のあるquotient生成
```

という構造を目指す。

前処理やquotient生成を既存実装と共通化するかどうかは、まずテスト可能な状態を作ってから判断する。

---

## 23. 正しさのテスト

最低限、以下のLTSを用意する。

### 基本

- 明らかに双模倣等価な状態
- 全状態を統合できるケース
- 1状態も統合できないケース
- self-loop

### Refinement

- 最初は同じsignatureだが、後のpartition分割によって非等価になる状態
- 非等価性が複数段階にわたって伝播するケース

### 非決定性

```text
s --a--> t1
s --a--> t2
```

のようなLTS。

### 特殊状態

- ERROR
- END
- STOP
- ENDとSTOPが両方存在するケース
- MarkedCompactState

### tau

- tauなし
- tauあり
- tauを含む非決定的構造

### 実際の研究モデル

- SCSによって生成された部分制御器

---

## 24. Legacyとの比較

同じ入力LTSについて、

```text
Legacy Minimiser
```

と、

```text
Dirty Signature Minimiser
```

を両方実行する。

最小化後の状態番号そのものを比較するのではなく、

```text
意味的に等価なLTSになっているか
```

を確認する。

既存の、

```text
IntegrationTests/TestBisimilarFSPs.java
```

などのbisimulation/refinement検証を参考にする。

---

## 25. アクション列の確認

研究上重要なのは、

```text
最小化アルゴリズムを変更しても
制御器が実行可能なアクション列が変化しない
```

ことである。

有限長のtrace列挙はデバッグには利用できる。

ただし、

```text
ある長さまでtraceが一致した
```

ことだけでは一般的なbisimulationの証明にはならない。

可能な限り既存のequivalence/refinement判定を使って意味的に検証する。

---

# 性能評価

## 26. 共通で取得する値

入力LTSについて、

```text
n = 状態数
m = 遷移数
```

を記録する。

各アルゴリズムについて、

- 最小化前状態数
- 最小化前遷移数
- 最小化後状態数
- 最小化後遷移数
- 最小化単体の実行時間
- メモリ使用量

を比較する。

---

## 27. Dirty Signature固有の値

可能であれば、

- refinement回数
- signature計算回数
- dirty stateになった回数
- 各反復でのdirty state数
- block split回数
- 最大dirty state数

なども記録する。

これにより、

```text
なぜ速くなったのか
```

または、

```text
なぜ速くならなかったのか
```

を分析できる。

---

## 28. SCS全体との切り分け

最小化アルゴリズムを評価するときは、

```text
SCS全体の実行時間
```

だけを測らない。

少なくとも、

```text
最小化処理単体
```

の時間を別に測る。

最終的には、

```text
最小化単体
SCS全体
```

の両方を見る。

---

# 計算量

## 29. 現在方式

現在の方式は、

```text
状態対
```

を明示的に管理する。

状態数を `n` とすると、候補関係の規模は最大、

```text
O(n^2)
```

となる。

ただし現在のJava実装では `BitSet` を用いて候補関係を圧縮して保持しているため、実際のメモリ使用量は単純なboolean行列とは異なる。

実行時間については、`EventState` 操作や反復回数も含めて別途解析する必要がある。

---

## 30. Dirty-state Signature Refinement

参考としている一般的なDirty-state型Signature Refinementでは、

```text
n = 状態数
m = 遷移数
k = 1状態から出る遷移数の最大値
```

としたとき、

```text
O(k m log n)
```

型の計算量が議論される。

ただし、MTSAへの実装で同じ計算量になるとは、実装する前から断定しない。

使用するデータ構造やsignature生成方法によって変化する。

---

## 31. 比較候補

LTS専用の別候補として、

```text
Valmari
Simple Bisimilarity Minimization in O(m log n) Time
```

がある。

将来的には、

```text
Legacy pair relation

vs

Dirty-state Signature Refinement

vs

Valmari-style partition refinement
```

という比較も検討する。

---

# 参考文献

## Dirty-state / Signature Refinement

Jules Jacobs and Thorsten Wißmann,

**Fast Coalgebraic Bisimilarity Minimization**

Proceedings of the ACM on Programming Languages, POPL 2023.

DOI:

```text
10.1145/3571245
```

## LTS専用の高速bisimulation minimization

Antti Valmari,

**Simple Bisimilarity Minimization in O(m log n) Time**

Fundamenta Informaticae, 105(3), 2010.

DOI:

```text
10.3233/FI-2010-369
```

---

## 32. 研究上の優先順位

本研究で最も重要なのは、

```text
高速なアルゴリズムを実装すること
```

そのものではなく、

```text
既存の最小化が保存している振る舞いを維持したまま
時間・主記憶量を改善すること
```

である。

そのため、新アルゴリズムの性能評価より先に、

```text
既存アルゴリズムと意味的に互換であること
```

を検証する。
