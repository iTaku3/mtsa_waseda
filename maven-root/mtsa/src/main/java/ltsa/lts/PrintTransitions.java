package ltsa.lts;

import static ltsa.lts.CompactStateUtils.isProbabilisticMachine;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class PrintTransitions {

  CompactState sm;

  public PrintTransitions(CompactState sm) {
    this.sm = sm;
  }

  public void print(LTSOutput output) {
    // ISSUE
    print(output, 9000);
  }

  public void print(LTSOutput output, int MAXPRINT) {
    int linecount = 0;
    // print name
    output.outln("Process:");
    output.outln("\t" + sm.name);
    // print number of states
    output.outln("States:");
    output.outln("\t" + sm.maxStates);

    output.outln("Transitions:");
    output.outln("\t" + sm.name + " = Q0,");

    for (int i = 0; i < sm.maxStates; i++) {
      output.out("\tQ" + i + "\t= ");
      EventState current;
      boolean isProbabilistic = isProbabilisticMachine(sm);
      if (!isProbabilistic) {
        current = EventStateUtils.transpose(sm.states[i]);
      } else {
        // Output for probabilistic EventStates is not optimised for syntax {Actions} ->
        // Qi
        current = sm.states[i];
      }
      if (current == null) {
        if (i == sm.endseq) output.out("END");
        else output.out("STOP");
        if (i < sm.maxStates - 1) output.outln(",");
        else output.outln(".");
      } else {
        output.out("(");
        EventState nextList = current;
        while (current != null) {
          linecount++;
          if (linecount > MAXPRINT) {
            output.outln("EXCEEDED MAXPRINT SETTING");
            return;
          }
          if (isProbabilistic) {
            ProbabilisticEventState prob = (ProbabilisticEventState) current;
            int bundle = prob.getBundle();
            int event = prob.getEvent();

            String[] events = new String[1];
            events[0] = sm.alphabet[event];
            Alphabet a = new Alphabet(events);
            output.out(a.toString() + " -> {");

            while (prob != null) {
              BigDecimal probability = prob.getProbability();
              output.out(
                  ((probability.equals(BigDecimal.valueOf(1))) ? " 1.0" : probability)
                      + " : Q"
                      + prob.next);
              prob = prob.probTr;
              if (prob != null) {
                assert (prob.getBundle() == bundle);
                assert (prob.getEvent() == event);
                output.out(" + ");
              }
            }

            output.out("}");

            // a -> {0.4 : Q14 + 0.6 : Q1}
            if (current.nondet == null) {
              current = nextList.list;
              nextList = current;
            } else {
              current = current.list;
            }
          } else {
            String[] events = EventState.eventsToNext(current, sm.alphabet);
            Alphabet a = new Alphabet(events);
            output.out(a.toString() + " -> ");
            if (current.next < 0) output.out("ERROR");
            else output.out("Q" + current.next);

            current = current.list;
          }

          if (current == null) {
            if (i < sm.maxStates - 1) output.outln("),");
            else {
              output.out(")");
              Set<String> remainingLabelsSet = new HashSet<>();
              Arrays.stream(sm.getAlphabet())
                  .filter(label -> !sm.usesLabel(label))
                  .forEach(remainingLabelsSet::add);
              if (remainingLabelsSet.size() != 0) {
                output.out("+{");
                Iterator<String> I = remainingLabelsSet.iterator();
                String[] alphabetArray = new String[remainingLabelsSet.size()];
                int pos = 0;
                while (I.hasNext()) {
                  alphabetArray[pos++] = I.next();
                }
                Alphabet remaining = new Alphabet(alphabetArray);
                remaining.printExpandLabels(output, 0, true);
                output.out("}.");
              }
            }
          } else {
            output.out("\n\t\t  |");
          }
        }
      }
    }
  }

  private String prettyPrintAlphabet(Set<String> alphabet) {
    if (alphabet.size() == 0) return "";
    String retval = "+{";
    Iterator<String> I = alphabet.iterator();
    while (I.hasNext()) {
      retval = retval + I.next();
      if (I.hasNext()) retval = retval + ", ";
    }
    return retval + "}";
  }
}
