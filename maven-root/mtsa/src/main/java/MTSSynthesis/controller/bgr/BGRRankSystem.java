package MTSSynthesis.controller.bgr;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import MTSSynthesis.controller.gr.AbstractGRRankSystem;
import MTSSynthesis.controller.gr.StrategyState;
import MTSSynthesis.controller.model.Assume;
import MTSSynthesis.controller.model.Assumptions;
import MTSSynthesis.controller.model.Guarantee;
import MTSSynthesis.controller.model.Guarantees;

//TODO: StrategyState---remove it, and leave just state and memory. 
public class BGRRankSystem<State> extends AbstractGRRankSystem<State, Integer> {

	private Map<Integer, BGRRankFunction<State>> rankSystem;

	public BGRRankSystem(Set<State> allStates, Guarantees<State> guarantees,
                         Assumptions<State> assumptions, Set<State> failures,
                         Set<State> buchi) {
		this.rankSystem = new HashMap<Integer, BGRRankFunction<State>>();

		//TODO: Ensure that all buchi states are contained in allStates.
		int notBuchiSize = allStates.size() - buchi.size();

		for (int guaranteeId = 1; guaranteeId <= guarantees.size(); guaranteeId++) {
			Guarantee<State> guarantee = guarantees.getGuarantee(guaranteeId);
			Set<State> tempGuarantee = new HashSet<State>();

			tempGuarantee.addAll(guarantee.getStateSet());
			tempGuarantee.addAll(failures);
			
			// For each guarantee compute the assumption that has the maximal
			// number of states that is not in this guarantee
			int maxAssumptionSize = 0; // allStates.size();
			int maxNotBuchiSize = 0;
			for (int assumptionId = 1; assumptionId <= assumptions.getSize(); ++assumptionId) {
				Assume<State> assume = assumptions.getAssume(assumptionId);
				int assumeMinusGuarantee = assume.getDifferenceSize(tempGuarantee);
				int assumeAndNotBuchiSize = assume.getDifferenceSize(buchi);
				int notBuchiAndNotAssume = notBuchiSize - assumeAndNotBuchiSize;
				if (assumeMinusGuarantee > maxAssumptionSize) {
					maxAssumptionSize = assumeMinusGuarantee;
				}
				if (notBuchiAndNotAssume > maxNotBuchiSize) {
					maxNotBuchiSize = notBuchiAndNotAssume;
				}
			}
			// The maximal rank that the RankFunction related to this guarantee
			// can have is the size of the maximal assumption
			this.rankSystem.put(guaranteeId, new BGRRankFunction<State>(
					allStates, maxAssumptionSize + 1, assumptions.getSize(), maxNotBuchiSize));
		}
		this.rankSystem.put(guarantees.size() + 1,new BGRRankFunction<State>(
				allStates,2,1,notBuchiSize));
	}

	@Override
	public BGRRank getMax(Set<StrategyState<State,Integer>> strategyStates) {
		return (BGRRank) super.getMax(strategyStates);
	}

	@Override
	public BGRRank getMin(Set<StrategyState<State,Integer>> strategyStates) {
		return (BGRRank) super.getMin(strategyStates);
	}

	// TODO increase,isinfinty, and set have the sema code here and in
	// GRRankSystem, they should be implemented
	// in a common abstract class. For that, an abstract implementation of
	// StrategyState is required and also pending.


	@Override
	public BGRRank getRank(StrategyState<State,Integer> strategyState) {
		return (BGRRank) super.getRank(strategyState);
	}

	@Override
	protected BGRRankFunction<State> getRankFunction(Integer rankFunction) {
		return this.rankSystem.get(rankFunction);
	}

	@Override
	public BGRRankContext getContext(StrategyState<State, Integer> strategyState) {
		return (BGRRankContext) super.getContext(strategyState);
	}
	
	@Override
	public String toString() {
		return this.rankSystem.toString();
	}


}

