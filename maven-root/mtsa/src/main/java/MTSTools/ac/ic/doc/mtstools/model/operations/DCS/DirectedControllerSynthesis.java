package MTSTools.ac.ic.doc.mtstools.model.operations.DCS;

import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSTools.ac.ic.doc.mtstools.model.operations.DCS.blocking.Statistics;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public abstract class DirectedControllerSynthesis<State, Action> {
    @SuppressWarnings("unchecked")
    public abstract LTS<Long, Action> synthesize(
        List<LTS<State, Action>> ltss,
        Set<Action> controllable,
        boolean reachability,
        HashMap<Integer, Integer> guarantees,
        HashMap<Integer, Integer> assumptions,
        List<String> comparison);

    public abstract Statistics getStatistics();
}
