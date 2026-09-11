package ltsa.lts;

import FSP2MTS.ac.ic.doc.mtstools.test.util.TestLTSOuput;
import ltsa.dispatcher.TransitionSystemDispatcher;
import org.junit.Test;

import static MTSTools.ac.ic.doc.mtstools.model.SemanticType.STRONG;
import static MTSTools.ac.ic.doc.mtstools.model.SemanticType.WEAK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Compares the Legacy {@link Minimiser} (BitSet[] E pairwise equivalence) against
 * {@link SignatureRefinementMinimiser} in both {@code FULL} and {@code DIRTY} modes
 * (int[] blockOfState partition refinement).
 * <p>
 * For every scenario below all three algorithms are run on independent clones of the
 * same input (Minimiser and SignatureRefinementMinimiser both mutate their input in
 * place, so a shared input object cannot be reused across runs). We do not require the
 * three results to assign the same numeric state ids - only that they:
 * <ul>
 *   <li>agree on the number of states and transitions after minimisation,</li>
 *   <li>are semantically equivalent to each other (mutual strong refinement - if the
 *       partitions genuinely match, the quotient graphs are isomorphic up to
 *       renumbering, so plain STRONG refinement is the right and strictest check here,
 *       independent of whether tau appears in the original model),</li>
 *   <li>are semantically equivalent to the ORIGINAL (pre-minimisation) model. For that
 *       comparison, tau-free models are checked with STRONG (ordinary strong
 *       bisimulation is exactly what minimisation must preserve when there is no tau to
 *       reason about); models containing tau are checked with WEAK (tau as the silent
 *       action), because Minimiser#minimise() implements an observational-equivalence
 *       style reduction via tau-saturation, not literal strong bisimulation on the raw
 *       input - see docs/research/MINIMISATION.md section 10.</li>
 *   <li>never leak the END-state marker transition (event &gt;= alphabet.length).</li>
 * </ul>
 */
public class SignatureRefinementComparisonTest {

    private static CompactState compile(String fsp, String processName) throws Exception {
        LTSInput input = new LTSInputString(fsp);
        TestLTSOuput output = new TestLTSOuput();
        LTSCompiler compiled = new LTSCompiler(input, output, ".");
        compiled.compile();
        CompositeState cs = compiled.continueCompilation(processName);
        TransitionSystemDispatcher.applyComposition(cs, output);
        return cs.composition;
    }

    private static boolean hasOutOfAlphabetTransition(CompactState cs) {
        for (int i = 0; i < cs.maxStates; i++) {
            EventState p = cs.states[i];
            while (p != null) {
                EventState q = p;
                while (q != null) {
                    if (q.event >= cs.alphabet.length) return true;
                    q = q.nondet;
                }
                p = p.list;
            }
        }
        return false;
    }

    private static final class Results {
        final CompactState legacy;
        final CompactState full;
        final CompactState dirty;

        Results(CompactState legacy, CompactState full, CompactState dirty) {
            this.legacy = legacy;
            this.full = full;
            this.dirty = dirty;
        }
    }

    /** Runs Legacy, FULL and DIRTY on independent clones of {@code original} and checks
     *  the invariants that must hold regardless of the specific scenario. */
    private Results runAllAndAssertCommonInvariants(CompactState original, boolean hasTau) throws Exception {
        TestLTSOuput output = new TestLTSOuput();

        CompactState legacy = new Minimiser(original.myclone(), output).minimise();
        CompactState full = new SignatureRefinementMinimiser(
                original.myclone(), output, SignatureRefinementMinimiser.RefinementMode.FULL).minimise();
        CompactState dirty = new SignatureRefinementMinimiser(
                original.myclone(), output, SignatureRefinementMinimiser.RefinementMode.DIRTY).minimise();

        assertFalse("legacy result must not leak the END marker", hasOutOfAlphabetTransition(legacy));
        assertFalse("FULL result must not leak the END marker", hasOutOfAlphabetTransition(full));
        assertFalse("DIRTY result must not leak the END marker", hasOutOfAlphabetTransition(dirty));

        assertEquals("FULL vs Legacy: state count", legacy.maxStates, full.maxStates);
        assertEquals("FULL vs Legacy: transition count", legacy.ntransitions(), full.ntransitions());
        assertEquals("DIRTY vs FULL: state count", full.maxStates, dirty.maxStates);
        assertEquals("DIRTY vs FULL: transition count", full.ntransitions(), dirty.ntransitions());

        // Cross-algorithm agreement: if all three computed the same partition, their
        // quotient graphs are isomorphic, hence even plain STRONG refinement must hold
        // in both directions - independent of whether the ORIGINAL model has tau.
        assertTrue("legacy refines full (STRONG)", TransitionSystemDispatcher.isRefinement(legacy, full, STRONG, output));
        assertTrue("full refines legacy (STRONG)", TransitionSystemDispatcher.isRefinement(full, legacy, STRONG, output));
        assertTrue("full refines dirty (STRONG)", TransitionSystemDispatcher.isRefinement(full, dirty, STRONG, output));
        assertTrue("dirty refines full (STRONG)", TransitionSystemDispatcher.isRefinement(dirty, full, STRONG, output));

        MTSTools.ac.ic.doc.mtstools.model.SemanticType semantic = hasTau ? WEAK : STRONG;
        assertTrue("original refines legacy (" + semantic + ")",
                TransitionSystemDispatcher.isRefinement(original, legacy, semantic, output));
        assertTrue("legacy refines original (" + semantic + ")",
                TransitionSystemDispatcher.isRefinement(legacy, original, semantic, output));
        assertTrue("original refines full (" + semantic + ")",
                TransitionSystemDispatcher.isRefinement(original, full, semantic, output));
        assertTrue("full refines original (" + semantic + ")",
                TransitionSystemDispatcher.isRefinement(full, original, semantic, output));
        assertTrue("original refines dirty (" + semantic + ")",
                TransitionSystemDispatcher.isRefinement(original, dirty, semantic, output));
        assertTrue("dirty refines original (" + semantic + ")",
                TransitionSystemDispatcher.isRefinement(dirty, original, semantic, output));

        return new Results(legacy, full, dirty);
    }

    @Test
    public void equivalentStatesAreMerged() throws Exception {
        CompactState original = compile(
                "P = (a -> Q | a -> R), Q = (b -> STOP), R = (b -> STOP).\n", "P");
        assertEquals(4, original.maxStates); // P, Q, R, STOP

        Results r = runAllAndAssertCommonInvariants(original, false);
        assertEquals("Q and R must collapse into one state", 3, r.full.maxStates);
    }

    @Test
    public void nonEquivalentStatesAreNotMerged() throws Exception {
        CompactState original = compile(
                "P = (a -> Q | a -> R), Q = (b -> STOP), R = (c -> STOP).\n", "P");
        assertEquals(4, original.maxStates);

        Results r = runAllAndAssertCommonInvariants(original, false);
        assertEquals("nothing should collapse: Q and R differ", 4, r.full.maxStates);
    }

    @Test
    public void selfLoopIsHandled() throws Exception {
        CompactState original = compile("P = (a -> P).\n", "P");
        assertEquals(1, original.maxStates);

        Results r = runAllAndAssertCommonInvariants(original, false);
        assertEquals(1, r.full.maxStates);
    }

    /**
     * Classic example (also used by MTSMinimiserTest) requiring several refinement
     * rounds. Hand-derived equivalence classes (independent of what any of the three
     * implementations under test actually compute - Legacy is NOT used as an oracle
     * here), by expanding the FSP into its 13 raw states and transitions:
     * <pre>
     * X --x--> A ,  X --x--> B
     * A --a--> A1,  A --a--> A2      (A1 = "b->C1|b->D1",  A2 = "b->D2")
     * B --a--> B1,  B --a--> B2      (B1 = "b->C2|b->D3",  B2 = "b->C3")
     * C1--c-->C1,  C2--c-->C2,  C3--c-->C3
     * D1--d-->D1,  D2--d-->D2,  D3--d-->D3
     * </pre>
     * Round-by-round bisimulation reasoning:
     * <ul>
     *   <li>Round 1: {C1,C2,C3} are trivially bisimilar (single 'c' self-loop each) and
     *       merge into one class C&#772;. Likewise {D1,D2,D3} merge into one class D&#772;.
     *       Nothing else can merge yet - A1/A2/B1/B2 all only offer 'b', A/B only offer
     *       'a', X is alone.</li>
     *   <li>Round 2: A1's signature is now {(b,C&#772;),(b,D&#772;)} (reaches both C1&isin;C&#772; and
     *       D1&isin;D&#772;); B1's signature is identically {(b,C&#772;),(b,D&#772;)} (reaches C2&isin;C&#772;
     *       and D3&isin;D&#772;) - so A1~B1, merging into one class P&#772;. A2's signature is
     *       {(b,D&#772;)} only (single target) and B2's is {(b,C&#772;)} only - different from
     *       each other and from P&#772;, so A2 and B2 each stay alone.</li>
     *   <li>Round 3: A's signature becomes {(a,P&#772;),(a,A2)} (reaches A1&isin;P&#772; and A2);
     *       B's becomes {(a,P&#772;),(a,B2)}. Since A2&ne;B2, A's and B's signatures differ -
     *       A and B do NOT merge, unlike a naive guess might assume from their
     *       superficially symmetric structure.</li>
     * </ul>
     * Final classes (8 total, independently derived above): {X}, {A}, {B}, {A1,B1},
     * {A2}, {B2}, {C1,C2,C3}, {D1,D2,D3}. Quotient transition count: X has 2 (to A,B),
     * A has 2 (to {A1,B1}, to A2), B has 2 (to {A1,B1}, to B2), {A1,B1} has 2 (to C&#772;,
     * D&#772;), A2 has 1 (to D&#772;), B2 has 1 (to C&#772;), C&#772; has 1 (self), D&#772; has 1 (self) = 12.
     */
    @Test
    public void multipleRoundsOfRefinementAreNeeded() throws Exception {
        CompactState original = compile(
                "X = (x -> A | x -> B),\n" +
                        "A = (a -> (b -> C1 | b -> D1) | a -> b -> D2),\n" +
                        "B = (a -> (b -> C2 | b -> D3) | a -> b -> C3),\n" +
                        "C1 = (c -> C1),\n" +
                        "C2 = (c -> C2),\n" +
                        "C3 = (c -> C3),\n" +
                        "D1 = (d -> D1),\n" +
                        "D2 = (d -> D2),\n" +
                        "D3 = (d -> D3).\n",
                "X");
        assertEquals(13, original.maxStates);
        assertEquals(18, original.ntransitions());

        Results r = runAllAndAssertCommonInvariants(original, false);
        assertEquals(8, r.full.maxStates);
        assertEquals(12, r.full.ntransitions());
    }

    @Test
    public void nondeterministicTransitionsAreHandled() throws Exception {
        CompactState original = compile(
                "P = (a -> Q | a -> Q), Q = (b -> STOP).\n", "P");
        // duplicate (event,target) pair from the same state - must be de-duplicated,
        // not double-counted, and must not crash signature computation.
        Results r = runAllAndAssertCommonInvariants(original, false);
        assertEquals(3, r.full.maxStates); // P, Q, STOP
    }

    @Test
    public void errorTransitionsAreHandled() throws Exception {
        CompactState original = compile(
                "TOP = (x -> P | y -> R),\n" +
                        "P = (a -> ERROR),\n" +
                        "R = (a -> ERROR).\n",
                "TOP");
        assertEquals(3, original.maxStates); // TOP, P, R (ERROR is a sentinel, not a real state)

        Results r = runAllAndAssertCommonInvariants(original, false);
        assertEquals("P and R must collapse: both are 'a -> ERROR' only", 2, r.full.maxStates);
    }

    @Test
    public void endAndStopAreNotMerged() throws Exception {
        CompactState original = compile("P = (a -> END | b -> STOP).\n", "P");
        assertEquals(3, original.maxStates);

        Results r = runAllAndAssertCommonInvariants(original, false);
        assertEquals("END and STOP must remain distinct states", 3, r.full.maxStates);
        assertTrue(r.legacy.endseq >= 0);
        assertTrue(r.full.endseq >= 0);
        assertTrue(r.dirty.endseq >= 0);
    }

    @Test
    public void tauIsCollapsedLikeLegacy() throws Exception {
        // Q only has a hidden (= tau after hiding {b}) transition to STOP: a canonical
        // removeNonDetTau() collapse target, verified against the ORIGINAL via WEAK.
        CompactState original = compile(
                "P = (a -> Q),\n" +
                        "Q = (b -> STOP).\n" +
                        "||H = (P)\\{b}.\n",
                "H");

        Results r = runAllAndAssertCommonInvariants(original, true);
        assertEquals(2, r.full.maxStates);
        assertFalse("tau should have been fully eliminated in this case", r.full.hasTau());
    }

    @Test
    public void residualBranchingTauIsPreserved() throws Exception {
        // P keeps a genuine branching tau after hiding {b} alongside action 'a': this
        // tau is NOT collapsible (hasOnlyTauAndAccept is false for P) and must survive.
        CompactState original = compile(
                "P = (a -> P | b -> Q),\n" +
                        "Q = (c -> Q).\n" +
                        "||H = (P)\\{b}.\n",
                "H");

        Results r = runAllAndAssertCommonInvariants(original, true);
        assertTrue("the branching tau on P must still be present after minimisation", r.full.hasTau());
    }

    @Test
    public void markedCompactStateIsPreserved() throws Exception {
        CompactState original = compile(
                "P = (a -> Q | a -> R), Q = (b -> STOP), R = (b -> STOP).\n", "P");
        TestLTSOuput output = new TestLTSOuput();

        // CompactState#myclone() always returns a plain CompactState (it does not
        // preserve the MarkedCompactState subtype), and both minimisers mutate the
        // markedStates array they are given in place - so each algorithm run needs its
        // own clone wrapped in its own fresh MarkedCompactState.
        MarkedCompactState forLegacy = new MarkedCompactState(original.myclone(), new int[]{1});
        MarkedCompactState forFull = new MarkedCompactState(original.myclone(), new int[]{1});
        MarkedCompactState forDirty = new MarkedCompactState(original.myclone(), new int[]{1});

        CompactState legacy = new Minimiser(forLegacy, output).minimise();
        CompactState full = new SignatureRefinementMinimiser(
                forFull, output, SignatureRefinementMinimiser.RefinementMode.FULL).minimise();
        CompactState dirty = new SignatureRefinementMinimiser(
                forDirty, output, SignatureRefinementMinimiser.RefinementMode.DIRTY).minimise();

        assertTrue(legacy instanceof MarkedCompactState);
        assertTrue(full instanceof MarkedCompactState);
        assertTrue(dirty instanceof MarkedCompactState);

        int[] legacyMarked = ((MarkedCompactState) legacy).getMarkedStates();
        int[] fullMarked = ((MarkedCompactState) full).getMarkedStates();
        int[] dirtyMarked = ((MarkedCompactState) dirty).getMarkedStates();

        assertEquals(1, legacyMarked.length);
        assertEquals(1, fullMarked.length);
        assertEquals(1, dirtyMarked.length);

        assertEquals(3, full.maxStates); // Q and R still collapse
        assertEquals(legacy.maxStates, full.maxStates);
        assertEquals(full.maxStates, dirty.maxStates);

        // Structural check, not just bounds: the ORIGINAL marked state was Q (index 1),
        // whose equivalence class is uniquely characterised in this model as "the state
        // reached by (all of) P's 'a' transition(s), itself having a single outgoing 'b'
        // transition to a state with no outgoing transitions (STOP)". Verify the
        // reported marked index actually IS that state - not merely a valid index - in
        // each of the three results.
        //
        // Note: P's two ORIGINAL 'a' transitions (to Q and to R) correctly collapse into
        // a single 'a' transition once Q and R are recognised as equivalent and given the
        // same new state number (EventStateUtils.add() de-duplicates same-event,
        // same-target entries) - so P must NOT be assumed to still be non-deterministic
        // after minimisation; only "every 'a' transition from P leads to the QR class" is
        // checked, however many of them survive.
        assertMarkedIndexIsTheQRClass(legacy, legacyMarked[0]);
        assertMarkedIndexIsTheQRClass(full, fullMarked[0]);
        assertMarkedIndexIsTheQRClass(dirty, dirtyMarked[0]);
    }

    /** Confirms {@code markedIndex} is exactly the merged {Q,R} class in a minimised
     *  "P = (a -> Q | a -> R), Q = (b -> STOP), R = (b -> STOP)." result: states are
     *  classified by their single distinguishing event label ('a' identifies P, 'b'
     *  identifies the {Q,R} class, no outgoing transitions identifies STOP). */
    private void assertMarkedIndexIsTheQRClass(CompactState cs, int markedIndex) {
        int qrClass = -1, pClass = -1, stopClass = -1;
        for (int i = 0; i < cs.maxStates; i++) {
            EventState head = cs.states[i];
            if (head == null) {
                assertEquals("only STOP should have zero outgoing transitions", -1, stopClass);
                stopClass = i;
                continue;
            }
            assertTrue("no other event should appear on a surviving state", head.list == null);
            String label = cs.alphabet[head.event];
            if (label.equals("b")) {
                assertEquals("only one state should have the 'b' transition", -1, qrClass);
                qrClass = i;
            } else {
                assertEquals("a", label);
                assertEquals("only P should have the 'a' transition", -1, pClass);
                pClass = i;
            }
        }
        assertTrue("all three shapes must be present", qrClass >= 0 && pClass >= 0 && stopClass >= 0);
        assertEquals("the marked index must be the merged {Q,R} class", qrClass, markedIndex);

        // every surviving 'a' transition from P must lead to the QR class, and the QR
        // class's 'b' transition must lead to the STOP class.
        for (EventState q = cs.states[pClass]; q != null; q = q.nondet) {
            assertEquals(qrClass, q.next);
        }
        assertEquals(stopClass, cs.states[qrClass].next);
    }

    /**
     * Regression test for a bug found via random differential testing (not by any of
     * the hand-picked scenarios above): the initial block containing state 0 can split
     * during refinement with state 0 ending up in the SMALLER (non-majority) sub-block.
     * Since {@link CompactState#START()} (and therefore {@code isRefinement()} /
     * {@code AutomataToMTSConverter}) hardcodes state 0 as the initial state,
     * {@link SignatureRefinementMinimiser} must guarantee that whichever equivalence
     * class contains the ORIGINAL state 0 becomes the NEW state 0 - even though
     * {@code splitBlock()}'s "largest sub-block keeps the old id" rule (churn
     * minimisation, not start-state preservation) would otherwise leave the old
     * (low) id with a different class.
     * <p>
     * Legacy {@link Minimiser#makeNewMachine()} gets this for free: it assigns new ids
     * by scanning old state indices in ascending order, so old state 0 is always the
     * very first index encountered and therefore always receives new id 0, regardless
     * of its equivalence class's size. {@code SignatureRefinementMinimiser} restores
     * the same guarantee with an explicit post-refinement renumbering step
     * ({@code normaliseStartState()}), which this test exercises directly.
     * <p>
     * Hand-verified fixed point for the tau-free LTS built by
     * {@link #buildMinorityStartStateExample()} (5 states, no tau):
     * <pre>
     * 0 --a--> 3     1 --a--> 4     2 --a--> 4     3 --b--> 3     4 --c--> 4
     * </pre>
     * States 0, 1 and 2 all start in the same initial block (identical enabled action
     * set {a}); states 3 and 4 are singletons from round 0 (distinct action sets {b}
     * and {c}). In the first refinement round, 0's target (3) and 1/2's target (4) are
     * found to be in different blocks, so {0,1,2} splits into {0} (size 1) and {1,2}
     * (size 2). The majority group {1,2} keeps the block's original (low) id under the
     * "largest sub-block keeps the old id" rule - state 0, the actual start state, is
     * exactly the minority member this test is designed to exercise. Final partition:
     * {0}, {1,2}, {3}, {4} - 4 classes, 4 transitions (0-&gt;{3}, {1,2}-&gt;{4},
     * {3}-&gt;{3}, {4}-&gt;{4}).
     */
    @Test
    public void startStateSurvivesWhenItIsTheMinoritySubBlock() throws Exception {
        CompactState original = buildMinorityStartStateExample();
        assertEquals(5, original.maxStates);
        assertEquals(5, original.ntransitions());

        Results r = runAllAndAssertCommonInvariants(original, false);

        assertEquals(4, r.full.maxStates);
        assertEquals(4, r.dirty.maxStates);
        assertEquals(4, r.full.ntransitions());
        assertEquals(4, r.dirty.ntransitions());

        // Structural check that the MEANING of the start state was preserved, not just
        // that isRefinement (already checked bidirectionally above, for both FULL and
        // DIRTY, by runAllAndAssertCommonInvariants) happened to accept some relation.
        // New state 0 must have exactly original state 0's local behaviour: a single,
        // deterministic 'a' transition to a state whose own single transition is 'b'
        // (the {3} class) - NOT the {1,2} class's behaviour ('a' to the {4} class,
        // whose own transition is 'c').
        assertStartStateIsOriginalState0Class(r.full);
        assertStartStateIsOriginalState0Class(r.dirty);
    }

    private static CompactState buildMinorityStartStateExample() {
        CompactState cs = new CompactState();
        cs.name = "MinorityStart";
        cs.alphabet = new String[]{"tau", "a", "b", "c"};
        cs.maxStates = 5;
        cs.endseq = -9999;
        cs.states = new EventState[5];
        cs.states[0] = EventStateUtils.add(cs.states[0], new EventState(1, 3)); // 0 --a--> 3
        cs.states[1] = EventStateUtils.add(cs.states[1], new EventState(1, 4)); // 1 --a--> 4
        cs.states[2] = EventStateUtils.add(cs.states[2], new EventState(1, 4)); // 2 --a--> 4
        cs.states[3] = EventStateUtils.add(cs.states[3], new EventState(2, 3)); // 3 --b--> 3
        cs.states[4] = EventStateUtils.add(cs.states[4], new EventState(3, 4)); // 4 --c--> 4
        return cs;
    }

    private static void assertStartStateIsOriginalState0Class(CompactState result) {
        EventState head0 = result.states[0];
        assertTrue("state 0 must have a transition", head0 != null);
        assertNull("state 0's class must carry only one event here", head0.list);
        assertNull("state 0's class must be deterministic here", head0.nondet);
        assertEquals("a", result.alphabet[head0.event]);

        EventState targetHead = result.states[head0.next];
        assertTrue("the {3}-class target must have a transition", targetHead != null);
        assertEquals("state 0 must lead to the {3} class (event 'b'), not the {1,2}/{4} class",
                "b", result.alphabet[targetHead.event]);
    }
}
