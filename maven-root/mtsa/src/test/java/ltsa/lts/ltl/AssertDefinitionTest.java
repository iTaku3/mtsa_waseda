package ltsa.lts.ltl;

import ltsa.lts.Symbol;
import ltsa.ui.StandardOutput;
import org.junit.Test;

import java.util.Arrays;
import java.util.Hashtable;

import static org.junit.Assert.assertTrue;

public class AssertDefinitionTest {
    @Test
    public void init() throws Exception {
        setUpDefAndCons();

        AssertDefinition.init();

        assertTrue(AssertDefinition.getConstraint("+") == null);
        assertTrue(AssertDefinition.getDefinition(",") == null);

    }

    private void setUpDefAndCons() {
        AssertDefinition.init();
        AssertDefinition.constraints = new Hashtable<>();
        AssertDefinition.definitions = new Hashtable<>();

        AssertDefinition.put(new Symbol(Symbol.COMMA), null, null, null, null, true, false);
        //AssertDefinition.constraints.put("A", (AssertDefinition.getConstraint(",")));

        AssertDefinition.put(new Symbol(Symbol.PLUS), null, null, null, null, false, false);
        // AssertDefinition.definitions.put("B", AssertDefinition.getDefinition("+"));
    }

    @Test(expected = NullPointerException.class)
    public void compileAll() throws Exception {
        setUpDefAndCons();
        AssertDefinition.compileAll(new StandardOutput());
    }


    @Test
    public void namesempty() {
        AssertDefinition.definitions = null;
        assertTrue(AssertDefinition.names() == null);
    }

    @Test
    public void namesNotEmpty() {
        setUpDefAndCons();
        String res[] = {"+"};
        assertTrue(Arrays.equals(res, AssertDefinition.names()));
    }

}