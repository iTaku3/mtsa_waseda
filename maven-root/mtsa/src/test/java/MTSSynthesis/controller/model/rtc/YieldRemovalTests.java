package MTSSynthesis.controller.model.rtc;

import MTSSynthesis.controller.model.ControllerGoal;
import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSTools.ac.ic.doc.mtstools.model.impl.LTSImpl;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * For a detailed explanation of these tests check YieldRemovalTests.md.
 */
public class YieldRemovalTests {

    @Test
    public void testYieldRemoval1() {
        Set<String> actions = new HashSet<>(Arrays.asList("c", "eY", "cY", "u"));

        ControllerGoal<String> goal = new ControllerGoal<>();
        goal.setControllableActions(new HashSet<>(Arrays.asList("c", "cY")));
        long s1 = 1;
        long s2 = 2;
        long s3 = 3;
        long s4 = 4;
        long u_win_1 = 5;
        long u_win_2 = 6;
        long c_win = 7;

        Set<Long> states = new HashSet<>(Arrays.asList(s1, s2, s3, s4, u_win_1, u_win_2, c_win));
        YieldRemoval<String> yieldRemoval = new YieldRemoval<>(goal, "eY", "cY");

        LTS<Long, String> lts = new LTSImpl<>(s1);
        lts.addActions(actions);
        lts.addStates(states);
        lts.addTransition(s1, "u", u_win_1);
        lts.addTransition(s1, "eY", s2);
        lts.addTransition(s2, "cY", s3);
        lts.addTransition(s3, "u", u_win_2);
        lts.addTransition(s3, "eY", s4);
        lts.addTransition(s4, "c", c_win);
        lts.addTransition(u_win_1, "u", u_win_1);
        lts.addTransition(u_win_2, "u", u_win_2);
        lts.addTransition(c_win, "c", c_win);

        LTS<Long, String> result = yieldRemoval.removeYields(lts);
        result.removeUnreachableStates();
        assertEquals(result.getStates().size(), 2);
        assertEquals(result.getTransitions(s1).size(), 1);
        assertEquals(result.getTransitions(s1).getImage("u").size(), 1);
        assertTrue(result.getTransitions(s1).getImage("u").contains(u_win_2));
    }

    @Test
    public void testYieldRemoval2() {
        Set<String> actions = new HashSet<>(Arrays.asList("c", "eY", "cY", "u"));

        ControllerGoal<String> goal = new ControllerGoal<>();
        goal.setControllableActions(new HashSet<>(Arrays.asList("c", "cY")));
        long s1 = 1;
        long s2 = 2;
        long s3 = 3;
        long s4 = 4;
        long u_win_1 = 5;
        long u_win_2 = 6;
        long c_win_1 = 7;
        long c_win_2 = 7;

        Set<Long> states = new HashSet<>(Arrays.asList(s1, s2, s3, s4, u_win_1, u_win_2, c_win_1, c_win_2));
        YieldRemoval<String> yieldRemoval = new YieldRemoval<>(goal, "eY", "cY");

        LTS<Long, String> lts = new LTSImpl<>(s1);
        lts.addActions(actions);
        lts.addStates(states);
        lts.addTransition(s1, "u", u_win_1);
        lts.addTransition(s1, "eY", s2);
        lts.addTransition(s2, "cY", s3);
        lts.addTransition(s2, "c", c_win_1);
        lts.addTransition(s3, "u", u_win_2);
        lts.addTransition(s3, "eY", s4);
        lts.addTransition(s4, "c", c_win_2);
        lts.addTransition(u_win_1, "u", u_win_1);
        lts.addTransition(u_win_2, "u", u_win_2);
        lts.addTransition(c_win_1, "c", c_win_1);
        lts.addTransition(c_win_2, "c", c_win_2);

        LTS<Long, String> result = yieldRemoval.removeYields(lts);
        result.removeUnreachableStates();
        assertEquals(result.getStates().size(), 3);
        assertEquals(result.getTransitions(s1).size(), 2);
        assertEquals(result.getTransitions(s1).getImage("u").size(), 1);
        assertTrue(result.getTransitions(s1).getImage("u").contains(u_win_2));
        assertEquals(result.getTransitions(s1).getImage("c").size(), 1);
        assertTrue(result.getTransitions(s1).getImage("c").contains(c_win_1));
    }

    @Test
    public void testYieldRemoval3() {
        Set<String> actions = new HashSet<>(Arrays.asList("c", "eY", "cY", "u"));

        ControllerGoal<String> goal = new ControllerGoal<>();
        goal.setControllableActions(new HashSet<>(Arrays.asList("c", "cY")));
        long s1 = 1;
        long s2 = 2;
        long u_win = 5;
        long c_win = 7;

        Set<Long> states = new HashSet<>(Arrays.asList(s1, s2, u_win, c_win));
        YieldRemoval<String> yieldRemoval = new YieldRemoval<>(goal, "eY", "cY");

        LTS<Long, String> lts = new LTSImpl<>(s1);
        lts.addActions(actions);
        lts.addStates(states);
        lts.addTransition(s1, "u", u_win);
        lts.addTransition(s1, "eY", s2);
        lts.addTransition(s2, "c", c_win);
        lts.addTransition(u_win, "u", u_win);
        lts.addTransition(c_win, "c", c_win);

        LTS<Long, String> result = yieldRemoval.removeYields(lts);
        result.removeUnreachableStates();
        assertEquals(result.getStates().size(), 3);
        assertEquals(result.getTransitions(s1).size(), 2);
        assertEquals(result.getTransitions(s1).getImage("u").size(), 1);
        assertTrue(result.getTransitions(s1).getImage("u").contains(u_win));
        assertEquals(result.getTransitions(s1).getImage("c").size(), 1);
        assertTrue(result.getTransitions(s1).getImage("c").contains(c_win));
    }
}