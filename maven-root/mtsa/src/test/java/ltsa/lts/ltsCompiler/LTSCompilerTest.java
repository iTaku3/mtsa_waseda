package ltsa.lts.ltsCompiler;

import FSP2MTS.ac.ic.doc.mtstools.test.util.TestLTSOuput;
import ltsa.lts.LTSCompiler;
import ltsa.lts.LTSException;
import ltsa.lts.LTSInput;
import ltsa.lts.LTSInputString;
import org.junit.Assert;
import org.junit.Test;

// Tests aimed at covering the parser. Ensures that the compiler does not break. Does not check
// semantics.

public class LTSCompilerTest {
  private void parse(String input) {
    LTSInput ltsInput = new LTSInputString(input);
    TestLTSOuput output = new TestLTSOuput();

    LTSCompiler compiled = new LTSCompiler(ltsInput, output, ".");
    try {
      compiled.compile();
    } catch (LTSException e) {
      Assert.fail("Syntax error in " + input + ": " + output.toString() + e.toString());
    }
    Assert.assertTrue("Syntax error in " + input + ": " + output.toString(), output.empty());
  }

  @Test
  public void processesWithSetsOfLabelsParse() {
    String input;
    TestLTSOuput output;

 //   System.out.println("Running parse tests with sets");
    parse("C = ({c,v} -> a -> a -> C).");
    parse("C = ({c,v} -> {a,b} -> a -> C).");
    parse("C = ({c,v} -> {a,b}\\{a} -> a -> C).");
    parse("set B = {e}\n" + "C = ({c,v} -> {s,d}\\{B} -> a -> C).");
    parse("set B = {e}\n" + "C = ({c,v} -> B\\{a,v} -> a -> C).");
    parse("set A = {d}\n" + "set B = {e}\n" + "C = ({c,v} -> B\\A -> a -> C).");
    parse("range Servers = 1..2\n" +
            "ServerThread(N=1) = (\n" +
            "accel.cancel -> ServerThread |\n" +
            "\t\taccel.request -> server[N].request -> WAITING), \n" +
            "WAITING = (\n" +
            "  server[N].response -> {accel[N].response, accel.cancel} -> ServerThread |\n" +
            "  accel.cancel -> ServerThread).\n" +
            "\n" +
            "AceleradorB = (client.request -> accel.request -> accel[i:Servers].response -> accel.cancel -> client.response -> AceleradorB).");
  }





  @Test
  public void closureOfSequentialProcessCompiles() throws Exception {
    parse("closure  A = (a->b->A)\\{a}.");
  }

  @Test
  public void closureOfCompositeProcessCompiles() throws Exception {
    parse("LOWER=(a->b->LOWER). closure ||PL=(LOWER)\\{a}.");
  }

  @Test
  public void abstractOfSequentialProcessCompiles() throws Exception {
    parse("abstract LOWER=(a->b->LOWER).");
  }

  @Test
  public void abstractOfCompositeProcessCompiles() throws Exception {
    parse("LOWER=(a->b->LOWER). abstract ||PL=(LOWER)\\{a}.");
  }

  @Test
  public void deterministicOfSequentialProcessCompiles() throws Exception {
    parse("deterministic LOWER=(a->b->LOWER | a->b->LOWER).");
  }

  @Test
  public void deterministicOfCompositeProcessCompiles() throws Exception {
    parse("LOWER=(a->b->LOWER | a->b->LOWER). deterministic ||PL=(LOWER).");
  }

    @Test
  public void ifThenElseinCompositeProcessCompiles() throws Exception {
    parse("A = (b-> if (1<2) then (a -> A) else (b->A)).");
  }

  @Test
  public void constraintOfLTLFormulaCompiles() throws Exception {
   parse("constraint P = [] (p -> X q)");
  }


}
