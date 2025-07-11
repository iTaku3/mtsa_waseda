package MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking.abstraction;

import static java.util.Collections.sort;

import java.util.Comparator;
import java.util.Set;

import MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking.Compostate;
import MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking.abstraction.Recommendation;
import java.util.List;


public class BFSAbstraction<State, Action> extends Abstraction<State, Action> {
    int counter;

    /** This is a mock Abstraction, we only want to make the compostates usable
     * But we dont want to have a good heuristic, we want to order the recommedations as we see fit.
     * Everything else the other Abstractions have, we are omitting it here*/
    public BFSAbstraction(){
        counter=0;
    }

    @Override
    public void eval(Compostate<State, Action> compostate, List<Set<State>> knownMarked, List<Set<State>> goals, List<String> comparison) {
        //this needs to set "recommendations", "recommendations", "recommendit" so the compostate can work as usual
        if (!compostate.isEvaluated()) {
            compostate.setupRecommendations(comparison); //initiliazing recommendations, necesary for compostates
            for (HAction<State, Action> action : compostate.getTransitions()) {
                int priorityOfAction = counter;
                ++counter; //if we passed int MAX_VALUE
                if (counter<0) counter=0;
                HEstimate<State, Action> estimate = new HEstimate<State, Action>(1, new HDist(priorityOfAction, 1));
                compostate.addRecommendation(action, estimate);
            }
            //compostate.rankRecommendations(); //we dont use the regular rankRecommendations because the ranker is
            //smarter than what we want.
            if (!compostate.recommendations.isEmpty()) {
                sort(compostate.recommendations, new BFSRanker());
            }

            compostate.initRecommendations(); //initializing recommendit and recommendation, necesary for compostates
        }
    }

    /** Ranker that doesnt care if the action is controllable or not, it only returns lexicografical order. */
    private class BFSRanker implements Comparator<Recommendation<State, Action>> {

        /** Compares two recommendations for ranking purposes.
         *  Returns:
         *   -1 if r1 is smaller than r2;
         *    0 if r1 and r2 are equal; and
         *    1 if r1 is greater than r2. */
        @Override
        public int compare(Recommendation<State, Action> r1, Recommendation<State, Action> r2) {
            return r1.compareTo(r2);
        }

    }
}