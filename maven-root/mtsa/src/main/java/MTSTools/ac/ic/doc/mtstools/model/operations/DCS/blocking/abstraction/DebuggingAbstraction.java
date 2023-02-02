package MTSTools.ac.ic.doc.mtstools.model.operations.DCS.blocking.abstraction;

import MTSTools.ac.ic.doc.mtstools.model.operations.DCS.blocking.*;

import java.util.Comparator;

public class DebuggingAbstraction<State, Action>  implements Abstraction<State, Action> {
    /**
     * This is a mock Abstraction, we only want to make the compostates usable
     * But we dont want to have a good heuristic, we want to order the recommedations as we see fit.
     * Everything else the other Abstractions have, we are omitting it here
     */
    public DebuggingAbstraction() {

    }

    @Override
    public void eval(Compostate<State, Action> compostate) {
        //this needs to set "recommendations", "recommendations", "recommendit" so the compostate can work as usual
        if (!compostate.isEvaluated()) {
            compostate.setupRecommendations(); //initiliazing recommendations, necesary for compostates
            for (HAction<Action> action : compostate.getTransitions()) {
                //the estimates are the first 1 or 3 chars of the action name, so it is easy to order them!
                String characters = action.toString();
                int priorityOfAction = characters.charAt(0);
                if (characters.length() >= 3) {
                    priorityOfAction = characters.charAt(0) + characters.charAt(1) + characters.charAt(2);
                }
                HEstimate estimate = new HEstimate(1, new HDist(priorityOfAction, 1));
                compostate.addRecommendation(action, estimate);
            }
            //compostate.rankRecommendations(); //we dont use the regular rankRecommendations because the ranker is
            //smarter than what we want.
            if (!compostate.recommendations.isEmpty()) {
                compostate.recommendations.sort(new DebuggingRanker());
            }

            //We still have to give a way in the FSPs to enable or disable the DebuggingAbstraction
            compostate.initRecommendations(); //initializing recommendit and recommendation, necesary for compostates
        }
    }

    /**
     * Ranker that doesnt care if the action is controllable or not, it only returns lexicografical order.
     */
    private class DebuggingRanker implements Comparator<Recommendation<Action>> {

        /**
         * Compares two recommendations for ranking purposes.
         * Returns:
         * -1 if r1 is smaller than r2;
         * 0 if r1 and r2 are equal; and
         * 1 if r1 is greater than r2.
         */
        @Override
        public int compare(Recommendation<Action> r1, Recommendation<Action> r2) {
            int c1 = r1.getAction().isControllable() ? 1 : 0;
            int c2 = r2.getAction().isControllable() ? 1 : 0;
            int result = c1 - c2;
            if (result == 0)
                result = r1.compareTo(r2);
            return result;
        }
    }
}
