package ltsa.lts;

import java.math.BigDecimal;

public class EventStateFactory {
    public static EventState createEventState(int event, Transition t) {
//        return new EventState(event, t.to);
        if (t instanceof ProbabilisticTransition) {
            ProbabilisticTransition pTr= (ProbabilisticTransition) t;
            return new ProbabilisticEventState(event, pTr.to, pTr.prob, pTr.probBundle);
        } else {
            return new EventState(event, t.to);
        }

    }

    public static ProbabilisticEventState createProbabilisticEventState(int event, Transition t) {
        ProbabilisticTransition pTr = (ProbabilisticTransition) t;
        return new ProbabilisticEventState(event, pTr.to, pTr.prob, pTr.probBundle);
    }

    public static ProbabilisticEventState createProbabilisticEventState(int event, Transition t, int bundle) {
        ProbabilisticTransition pTr = new ProbabilisticTransition(t.from, t.event, t.to, BigDecimal.ONE, bundle);
        return new ProbabilisticEventState(event, pTr.to, pTr.prob, pTr.probBundle);
    }
}
