package ltsa.lts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;

/**
 * Alternative LTS minimiser to {@link Minimiser}, based on partition (signature)
 * refinement instead of Minimiser's pairwise {@code BitSet[] E} equivalence table.
 * <p>
 * This class does not replace {@link Minimiser}: both remain available so that the
 * two algorithms can be run side by side and compared. The pre-processing (optional
 * {@code removeNonDetTau()}, the END/STOP distinguishing marker, tau closure/saturation)
 * and post-processing (quotient construction, END marker removal, reflexive tau removal,
 * {@link MarkedCompactState} handling) intentionally mirror {@link Minimiser} exactly -
 * only the middle step (how equivalent states are discovered) differs.
 * <p>
 * Central data structure: {@code int[] blockOfState}, where {@code blockOfState[s]} is
 * the id of the block (partition class) state {@code s} currently belongs to. No
 * {@code n x n} state-pair relation is ever constructed.
 */
public class SignatureRefinementMinimiser {

    public enum RefinementMode {
        FULL,
        DIRTY
    }

    /** Sentinel target used in a signature for any transition whose destination is
     *  a negative state number (e.g. {@link Declaration#ERROR}). All such transitions
     *  are treated as equivalent to one another, exactly as Minimiser's
     *  {@code is_equivalent()/findSuccessor()} do (they only check {@code next < 0},
     *  never the exact negative value). */
    private static final int ERROR_BLOCK = -1;

    private final CompactState machine;
    private final LTSOutput output;
    private final RefinementMode mode;

    // tau adjacency lists (reflexive transitive closure), mirrors Minimiser.T
    private EventState[] T;

    public SignatureRefinementMinimiser(CompactState machine, LTSOutput output) {
        this(machine, output, RefinementMode.FULL);
    }

    public SignatureRefinementMinimiser(CompactState machine, LTSOutput output, RefinementMode mode) {
        this.machine = machine;
        this.output = output;
        this.mode = mode;
    }

    /* ================================================================== */
    /* tau pre-processing - deliberately duplicated from Minimiser, but only
     * ever calling the already-public EventState/EventStateUtils APIs, so
     * Minimiser.java itself needs no visibility changes.                  */
    /* ================================================================== */

    private void initTau() {
        T = new EventState[machine.states.length];
        for (int i = 0; i < T.length; i++) {
            T[i] = EventState.reachableTau(machine.states, i);
        }
    }

    private void machTau(CompactState m) {
        for (int i = 0; i < m.states.length; i++)
            m.states[i] = EventState.tauAdd(m.states[i], T);
        for (int i = 0; i < m.states.length; i++) {
            m.states[i] = EventStateUtils.union(m.states[i], T[i]);
            m.states[i] = EventState.actionAdd(m.states[i], m.states);
        }
        for (int i = 0; i < m.states.length; i++)
            m.states[i] = EventStateUtils.add(m.states[i], new EventState(Declaration.TAU, i));
        output.out(".");
    }

    /* ================================================================== */
    /* public entry point                                                  */
    /* ================================================================== */

    public CompactState minimise() {
        if (CompositeState.reduceFlag) {
            output.outln("Tau reduction ON");
            machine.removeNonDetTau();
        }
        output.out(machine.name + " minimising (signature refinement, " + mode + ")");
        long start = System.currentTimeMillis();

        // distinguish END state from STOP with self transition using a special label -
        // same trick, same event number, as Minimiser#minimise().
        if (machine.endseq >= 0) {
            int es = machine.endseq;
            machine.states[es] = EventStateUtils.add(machine.states[es], new EventState(machine.alphabet.length, es));
        }
        if (machine.hasTau()) {
            initTau();
            machTau(machine);
            T = null; // release storage
        }

        int n = machine.maxStates;
        int[] block = initialPartition(n);
        int initialBlockCount = 0;
        for (int b : block) if (b + 1 > initialBlockCount) initialBlockCount = b + 1;
        Counter nextBlockId = new Counter(initialBlockCount);

        if (mode == RefinementMode.FULL) {
            runFull(block, n, nextBlockId);
        } else {
            runDirty(block, n, nextBlockId);
        }

        int numBlocks = nextBlockId.lastLabel().intValue();
        // CompactState#START() (and therefore isRefinement()/AutomataToMTSConverter and
        // every other consumer) hardcodes state 0 as the initial state. The "largest
        // sub-block keeps the old id" rule in splitBlock() only minimises id churn - it
        // has no reason to keep state 0's class at id 0, and can reassign it to a fresh
        // id whenever state 0 ends up in a non-largest sub-block. Renumber once, after
        // refinement has fully converged, so the block containing the original state 0
        // becomes the new state 0 (Minimiser#makeNewMachine() gets this for free because
        // it assigns new ids by scanning old state indices in order starting from 0).
        block = normaliseStartState(block, numBlocks);

        CompactState result = makeQuotient(block, numBlocks);

        long finish = System.currentTimeMillis();
        output.outln("");
        output.outln("Minimised States: " + result.maxStates + " in " + (finish - start) + "ms");
        return result;
    }

    /**
     * Remaps block ids so that the block containing original state 0 becomes id 0,
     * preserving id 0's status as a compact, contiguous block numbering. Every other
     * block keeps a stable relative order (ascending by its previous id).
     */
    private static int[] normaliseStartState(int[] block, int numBlocks) {
        int startBlock = block[0];
        if (startBlock == 0) return block; // already correct - nothing to do

        int[] remap = new int[numBlocks];
        remap[startBlock] = 0;
        int next = 1;
        for (int oldId = 0; oldId < numBlocks; oldId++) {
            if (oldId == startBlock) continue;
            remap[oldId] = next++;
        }
        int[] result = new int[block.length];
        for (int i = 0; i < block.length; i++) result[i] = remap[block[i]];
        return result;
    }

    /* ================================================================== */
    /* initial partition: states with the same enabled-event set start in  */
    /* the same block. Built as state -> block id directly, no n x n table.*/
    /* ================================================================== */

    private int[] initialPartition(int n) {
        int[] block = new int[n];
        Map<java.util.BitSet, Integer> seen = new LinkedHashMap<>();
        int nextId = 0;
        for (int i = 0; i < n; i++) {
            java.util.BitSet actions = new java.util.BitSet();
            EventState.setActions(machine.states[i], actions);
            Integer id = seen.get(actions);
            if (id == null) {
                id = nextId++;
                seen.put(actions, id);
            }
            block[i] = id;
        }
        return block;
    }

    /* ================================================================== */
    /* signature: canonical, exact-equality set of (event, targetBlock)     */
    /* pairs. Immutable, compares by content (not just hashCode).           */
    /* ================================================================== */

    private static final class Signature {
        private final long[] pairs; // sorted ascending, de-duplicated

        private Signature(long[] pairs) {
            this.pairs = pairs;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Signature)) return false;
            return Arrays.equals(pairs, ((Signature) o).pairs);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(pairs);
        }
    }

    private static long encode(int event, int targetBlock) {
        return ((long) event << 32) | (targetBlock & 0xFFFFFFFFL);
    }

    private Signature computeSignature(int s, int[] block) {
        ArrayList<Long> pairs = new ArrayList<>();
        EventState p = machine.states[s];
        while (p != null) {
            EventState q = p;
            while (q != null) {
                int targetBlock = (q.next < 0) ? ERROR_BLOCK : block[q.next];
                pairs.add(encode(q.event, targetBlock));
                q = q.nondet;
            }
            p = p.list;
        }
        long[] arr = new long[pairs.size()];
        for (int i = 0; i < arr.length; i++) arr[i] = pairs.get(i);
        Arrays.sort(arr);
        int uniqueLen = 0;
        for (int i = 0; i < arr.length; i++) {
            if (i == 0 || arr[i] != arr[uniqueLen - 1]) {
                arr[uniqueLen++] = arr[i];
            }
        }
        if (uniqueLen != arr.length) arr = Arrays.copyOf(arr, uniqueLen);
        return new Signature(arr);
    }

    /* ================================================================== */
    /* shared block-split logic (section 14): used identically by FULL and */
    /* DIRTY. Splits one block if its members carry more than one distinct */
    /* signature; the largest resulting sub-block keeps the original id,   */
    /* the others get freshly allocated ids (deterministic tie-break by    */
    /* smallest member state number).                                     */
    /* ================================================================== */

    private List<List<Integer>> splitBlock(int blockId, List<Integer> members,
                                            IntFunction<Signature> sigOf,
                                            int[] block, Counter nextBlockId) {
        if (members.size() <= 1) return Collections.singletonList(members);

        LinkedHashMap<Signature, List<Integer>> groups = new LinkedHashMap<>();
        for (int s : members) {
            groups.computeIfAbsent(sigOf.apply(s), k -> new ArrayList<>()).add(s);
        }
        if (groups.size() <= 1) return Collections.singletonList(members);

        List<Integer> largest = null;
        for (List<Integer> g : groups.values()) {
            if (largest == null || isLargerSubBlock(g, largest)) largest = g;
        }

        List<List<Integer>> result = new ArrayList<>(groups.size());
        result.add(largest);
        for (int s : largest) block[s] = blockId;
        for (List<Integer> g : groups.values()) {
            if (g == largest) continue;
            int newId = nextBlockId.label().intValue();
            for (int s : g) block[s] = newId;
            result.add(g);
        }
        return result;
    }

    private static boolean isLargerSubBlock(List<Integer> a, List<Integer> b) {
        if (a.size() != b.size()) return a.size() > b.size();
        return Collections.min(a) < Collections.min(b);
    }

    private static Map<Integer, List<Integer>> groupStatesByBlock(int[] block, int n) {
        Map<Integer, List<Integer>> byBlock = new LinkedHashMap<>();
        for (int s = 0; s < n; s++) {
            byBlock.computeIfAbsent(block[s], k -> new ArrayList<>()).add(s);
        }
        return byBlock;
    }

    /* ================================================================== */
    /* FULL: every round, recompute every state's signature and re-check   */
    /* every block.                                                        */
    /* ================================================================== */

    private void runFull(int[] block, int n, Counter nextBlockId) {
        while (true) {
            Signature[] sig = new Signature[n];
            for (int s = 0; s < n; s++) sig[s] = computeSignature(s, block);

            Map<Integer, List<Integer>> byBlock = groupStatesByBlock(block, n);
            boolean changedAny = false;
            for (Map.Entry<Integer, List<Integer>> e : byBlock.entrySet()) {
                List<List<Integer>> groups = splitBlock(e.getKey(), e.getValue(), s -> sig[s], block, nextBlockId);
                if (groups.size() > 1) changedAny = true;
            }
            if (!changedAny) break;
        }
    }

    /* ================================================================== */
    /* DIRTY: same signature/split/quotient logic as FULL, but each round  */
    /* only recomputes signatures for "dirty" states and only reconsiders  */
    /* blocks touched by a dirty state. Processing per round is synchronous:*/
    /* all dirty signatures are computed against the round-start partition */
    /* before any split is applied.                                        */
    /* ================================================================== */

    private void runDirty(int[] block, int n, Counter nextBlockId) {
        List<Integer>[] pred = buildPredecessors(n);

        // members[blockId] = mutable list of states currently in that block
        ArrayList<List<Integer>> members = new ArrayList<>();
        Map<Integer, List<Integer>> initialGroups = groupStatesByBlock(block, n);
        for (int id = 0; id < nextBlockId.lastLabel().intValue(); id++) {
            List<Integer> g = initialGroups.get(id);
            members.add(g != null ? g : new ArrayList<>());
        }

        Signature[] sigCache = new Signature[n];
        LinkedHashSet<Integer> dirty = new LinkedHashSet<>();
        for (int s = 0; s < n; s++) dirty.add(s);

        // Provable bound, not a heuristic: the partition only ever splits (never merges),
        // block count is non-decreasing and capped at n, and every split increases it by
        // at least 1. Round 0 always runs unconditionally; every further round only runs
        // because the previous round produced at least one split. So the number of
        // rounds is at most 1 + (n - initialBlockCount) <= n. A round count above n would
        // mean this invariant was violated somewhere - i.e. an actual bug, not a slow case.
        int maxRounds = n;
        int rounds = 0;

        while (!dirty.isEmpty()) {
            if (++rounds > maxRounds) {
                throw new IllegalStateException(
                        "SignatureRefinementMinimiser DIRTY mode exceeded the expected round bound - likely a bug");
            }

            // 1. recompute signatures for dirty states only, against the round-start partition
            for (int s : dirty) sigCache[s] = computeSignature(s, block);

            // 2. find blocks touched by a dirty state this round
            LinkedHashSet<Integer> touchedBlocks = new LinkedHashSet<>();
            for (int s : dirty) touchedBlocks.add(block[s]);

            // 3. split each touched block using ALL its members' cached signature
            //    (clean members' cached signatures are still valid: see class javadoc)
            LinkedHashSet<Integer> changedStates = new LinkedHashSet<>();
            for (int blockId : touchedBlocks) {
                List<Integer> memberList = members.get(blockId);
                List<List<Integer>> groups = splitBlock(blockId, memberList, s -> sigCache[s], block, nextBlockId);
                if (groups.size() > 1) {
                    members.set(blockId, groups.get(0));
                    for (int gi = 1; gi < groups.size(); gi++) {
                        List<Integer> g = groups.get(gi);
                        members.add(g);
                        changedStates.addAll(g);
                    }
                }
            }

            // 4. next round's dirty set = predecessors of every state whose block id changed
            LinkedHashSet<Integer> nextDirty = new LinkedHashSet<>();
            for (int t : changedStates) {
                List<Integer> ps = pred[t];
                if (ps != null) nextDirty.addAll(ps);
            }
            dirty = nextDirty;
        }
    }

    @SuppressWarnings("unchecked")
    private List<Integer>[] buildPredecessors(int n) {
        List<Integer>[] pred = new List[n];
        for (int s = 0; s < n; s++) {
            EventState p = machine.states[s];
            while (p != null) {
                EventState q = p;
                while (q != null) {
                    if (q.next >= 0) { // ERROR/negative targets are not real states - never indexed
                        if (pred[q.next] == null) pred[q.next] = new ArrayList<>();
                        pred[q.next].add(s);
                    }
                    q = q.nondet;
                }
                p = p.list;
            }
        }
        return pred;
    }

    /* ================================================================== */
    /* quotient LTS generation directly from int[] blockOfState - no       */
    /* BitSet[] E / n x n relation is built at any point.                  */
    /* ================================================================== */

    private CompactState makeQuotient(int[] block, int numBlocks) {
        Hashtable<Integer, Integer> oldtonew = new Hashtable<>();
        for (int i = 0; i < block.length; i++) oldtonew.put(i, block[i]);

        CompactState m = new CompactState();
        m.name = machine.name;
        m.maxStates = numBlocks;
        m.alphabet = machine.alphabet;
        m.states = new EventState[m.maxStates];

        if (machine.endseq < 0)
            m.endseq = machine.endseq;
        else
            m.endseq = oldtonew.get(machine.endseq).intValue();

        for (int i = 0; i < machine.maxStates; i++) {
            int newi = oldtonew.get(i).intValue();
            EventState tmp = EventStateUtils.renumberStates(machine.states[i], oldtonew);
            m.states[newi] = EventStateUtils.union(m.states[newi], tmp);
        }

        if (machine.endseq >= 0)
            // remove the END marker only after transitions have been copied in above -
            // matches the fixed Minimiser#makeNewMachine() ordering.
            m.states[m.endseq] = EventState.remove(m.states[m.endseq], new EventState(m.alphabet.length, m.endseq));

        for (int i = 0; i < m.maxStates; i++) // remove reflexive tau
            m.states[i] = EventState.remove(m.states[i], new EventState(Declaration.TAU, i));

        return handleMarkedCompactState(m, machine, oldtonew);
    }

    private CompactState handleMarkedCompactState(CompactState m, CompactState sourceMachine,
                                                   Map<Integer, Integer> oldToNew) {
        if (!(sourceMachine instanceof MarkedCompactState)) return m;
        int[] oldMarked = ((MarkedCompactState) sourceMachine).getMarkedStates();
        int[] newMarked = new int[oldMarked.length];
        for (int i = 0; i < oldMarked.length; ++i) {
            newMarked[i] = oldToNew.get(oldMarked[i]).intValue();
        }
        return new MarkedCompactState(m, newMarked);
    }
}
