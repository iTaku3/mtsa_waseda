package MTSSynthesis.controller.gr;

import MTSSynthesis.controller.model.Assume;
import MTSSynthesis.controller.model.Assumptions;
import MTSSynthesis.controller.model.Guarantee;
import MTSSynthesis.controller.model.Guarantees;


import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

//TODO: change from Pair<State, Integer> to StrategyState
public class GRRankSystem<State> extends AbstractGRRankSystem<State, Integer> {

    // This is a map from the guarantees (each guarantee is represented here
    // by its index in the array
    // to a ranking function.
    // Equally, it could be an array of RankFunction where the location in the
    // array
    // matches the location of the guarantee in guarantees
    private ConcurrentHashMap<Integer, GRRankFunction<State>> system;

    public GRRankSystem(Set<State> allStates, Guarantees<State> guarantees,
                        Assumptions<State> assumptions, Set<State> failures) {
        initialize(allStates, guarantees, assumptions, failures);
    }

    protected void initialize(Set<State> allStates,
                              Guarantees<State> guarantees, Assumptions<State> assumptions,
                              Set<State> failures) {
        // Initialise the functions for each of the guarantees to the function
        // that sets all values to the minimal value
        this.system = new ConcurrentHashMap<Integer, GRRankFunction<State>>();

        for (int guaranteeId = 1; guaranteeId <= guarantees.size(); guaranteeId++) {
            Guarantee<State> guarantee = guarantees.getGuarantee(guaranteeId);
            int maxAssumptionSize = getMaxAssumptionSize(assumptions, failures, guarantee);
            // The maximal rank that the RankFunction related to this guarantee
            // can have is the size of the maximal assumption
            this.system.put(guaranteeId, new GRRankFunction<State>(allStates,
                    maxAssumptionSize + 1, assumptions.getSize()));
            // this.system.put(guaranteeId, new GRRankFunction<State>(allStates,
            // allStates.size(), assumptions.getSize()));
        }
    }

    protected int getMaxAssumptionSize(Assumptions<State> assumptions,
                                       Set<State> failures, Guarantee<State> guarantee) {
        Set<State> temp = new HashSet<State>();
        temp.addAll(guarantee.getStateSet());
        temp.addAll(failures);

        // For each guarantee compute the assumption that has the maximal
        // number of states that is not in this guarantee
        int maxAssumptionSize = 0; // allStates.size();
        for (int assumptionId = 1; assumptionId <= assumptions.getSize(); ++assumptionId) {
            Assume<State> assume = assumptions.getAssume(assumptionId);
            int assumeMinusGuarantee = assume.getDifferenceSize(temp);
            if (assumeMinusGuarantee > maxAssumptionSize) {
                maxAssumptionSize = assumeMinusGuarantee;
            }
        }
        return maxAssumptionSize;
    }

    @Override
    public String toString() {
        return this.system.toString();
    }

    @Override
    public GRRank getRank(StrategyState<State, Integer> strategyState) {

            return (GRRank) super.getRank(strategyState);

    }

    @Override
    public GRRankContext getContext(StrategyState<State, Integer> strategyState) {
        return (GRRankContext) super.getContext(strategyState);
    }

    @Override
    public GRRank getMax(Set<StrategyState<State, Integer>> strategyStates) {
        return (GRRank) super.getMax(strategyStates);
    }

    @Override
    public GRRank getMin(Set<StrategyState<State, Integer>> strategyStates) {
        return (GRRank) super.getMin(strategyStates);
    }

    @Override
    protected GRRankFunction<State> getRankFunction(Integer rankFunction) {

            return this.system.get(rankFunction);

    }
}
