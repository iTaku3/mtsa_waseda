package ltsa.lts;

import FSP2MTS.ac.ic.doc.mtstools.test.util.TestLTSOuput;
import ltsa.dispatcher.TransitionSystemDispatcher;
import org.junit.Test;

import static MTSTools.ac.ic.doc.mtstools.model.SemanticType.STRONG;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Regression tests for the END-marker leak in Minimiser#makeNewMachine().
 * <p>
 * Minimiser#minimise() temporarily tags the END state with a sentinel
 * transition (event == alphabet.length) so that it is not merged with a
 * STOP state during equivalence computation. makeNewMachine() is supposed
 * to strip that sentinel transition back out before returning the quotient
 * machine, but the removal used to run before the transitions were copied
 * into the new machine, making it a no-op: the sentinel transition then
 * leaked into the final result and made it unsafe to pass to code that
 * indexes alphabet[event] (e.g. isRefinement/printAUT).
 */
public class MinimiserEndMarkerTest {

    private CompactState compile(String fsp, String processName) throws Exception {
        LTSInput input = new LTSInputString(fsp);
        TestLTSOuput output = new TestLTSOuput();
        LTSCompiler compiled = new LTSCompiler(input, output, ".");
        compiled.compile();
        CompositeState cs = compiled.continueCompilation(processName);
        TransitionSystemDispatcher.applyComposition(cs, output);
        return cs.composition;
    }

    private boolean hasOutOfAlphabetTransition(CompactState cs) {
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

    @Test
    public void endMarkerDoesNotLeakIntoMinimisedResult() throws Exception {
        CompactState original = compile("P = (a -> END).\n", "P");
        TestLTSOuput output = new TestLTSOuput();

        Minimiser m = new Minimiser(original.myclone(), output);
        CompactState result = m.minimise();

        assertFalse("no transition should reference an event index outside the alphabet",
                hasOutOfAlphabetTransition(result));
        assertTrue("END state must be preserved (endseq >= 0)", result.endseq >= 0);
        assertTrue("END state should be reachable via 'a'",
                EventState.hasState(result.states[0], result.endseq));

        // The result must now be usable as an ordinary LTS: isRefinement() and
        // printAUT() used to throw ArrayIndexOutOfBoundsException on the leaked marker.
        boolean refines = TransitionSystemDispatcher.isRefinement(result, result, STRONG, output);
        assertTrue("a minimised model must strongly refine itself", refines);

        java.io.ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
        result.printAUT(new java.io.PrintStream(buf));
        assertTrue("printAUT should have produced output", buf.size() > 0);
    }

    @Test
    public void endAndStopAreNotMergedByMinimisation() throws Exception {
        // Without the END marker, END and STOP would both look like
        // "no outgoing transitions" and could be wrongly merged into one state.
        CompactState original = compile("P = (a -> END | b -> STOP).\n", "P");
        TestLTSOuput output = new TestLTSOuput();

        assertEquals("sanity check on the unminimised composition", 3, original.maxStates);

        Minimiser m = new Minimiser(original.myclone(), output);
        CompactState result = m.minimise();

        assertFalse("no leaked END marker transition", hasOutOfAlphabetTransition(result));
        assertEquals("END and STOP must remain distinct states after minimisation",
                3, result.maxStates);
    }

    @Test
    public void minimisationWithoutEndStateIsUnaffected() throws Exception {
        // No endseq at all: behaviour here must be completely unchanged by the fix.
        CompactState original = compile(
                "P = (a -> Q | a -> R), Q = (b -> STOP), R = (b -> STOP).\n", "P");
        TestLTSOuput output = new TestLTSOuput();

        assertTrue("sanity check: this model has no END state", original.endseq < 0);
        assertEquals("sanity check on the unminimised composition (P, Q, R, STOP)",
                4, original.maxStates);

        Minimiser m = new Minimiser(original.myclone(), output);
        CompactState result = m.minimise();

        assertFalse(hasOutOfAlphabetTransition(result));
        assertTrue(result.endseq < 0);
        // Q and R are bisimilar and should still be collapsed into one state (P, {Q,R}, STOP).
        assertEquals(3, result.maxStates);

        boolean refinesForward = TransitionSystemDispatcher.isRefinement(result, original, STRONG, output);
        boolean refinesBackward = TransitionSystemDispatcher.isRefinement(original, result, STRONG, output);
        assertTrue("minimised model must be a strong refinement of the original", refinesForward);
        assertTrue("original model must be a strong refinement of the minimised model", refinesBackward);
    }
}
