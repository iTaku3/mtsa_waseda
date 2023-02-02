package MTSSynthesis.controller.gr.time.utils;

import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSTools.ac.ic.doc.mtstools.model.impl.LTSImpl;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by ezecastellano on 11/05/16.
 */
public class PruningUtils<S,A> {
    public LTS<S,A> pruneRealEnvironment(LTS<Pair<S, S>, A> result1, LTS<Pair<S, S>, A> result2, LTS<S, A> realEnvironment) {
        LTS<S,A> lts = new LTSImpl<S, A>(realEnvironment.getInitialState());
        lts.addStates(realEnvironment.getStates());
        lts.addActions(realEnvironment.getActions());
        for (S state : realEnvironment.getStates()) {
            Set<A> enabled = new HashSet<A>();
            enabledFrom(result1, state, enabled);
            enabledFrom(result2, state, enabled);
            for(Pair<A,S> t : realEnvironment.getTransitions(state)){
                if(enabled.contains(t.getFirst())){
                    lts.addTransition(state, t.getFirst(), t.getSecond());
                }
            }
        }
        lts.removeUnreachableStates();
        return lts;
    }

    private void enabledFrom(LTS<Pair<S, S>, A> result2, S state, Set<A> enabled) {
        for (Pair<S,S> p : result2.getStates()) {
            if(p.getFirst().equals(state)){
                for(Pair<A,Pair<S,S>> t :result2.getTransitions(p)){
                    enabled.add(t.getFirst());
                }
            }
        }
    }

}
