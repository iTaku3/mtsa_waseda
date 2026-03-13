package MTSSynthesis.controller.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import MTSSynthesis.ar.dc.uba.model.condition.Fluent;
import MTSSynthesis.ar.dc.uba.model.condition.PropositionalVariable;
import MTSSynthesis.ar.dc.uba.model.condition.Valuation;

public class FluentStateValuation<State> implements Valuation {
	
	private Map<State, Set<Fluent>> statesFromFluents = new HashMap<State, Set<Fluent>>();
	private State actualState;

	public FluentStateValuation(Set<State> states) {
		for (State state : states) {
			this.statesFromFluents.put(state, new HashSet<Fluent>());
		}
	}
	
	public void setActualState(State newActualState) {
		this.actualState  = newActualState;
	}
	public boolean addHoldingFluent(State state, Fluent fluent) {
		return this.statesFromFluents.get(state).add(fluent);
	}
	
	public boolean isTrue(State state, Fluent fluent) {
		return this.statesFromFluents.get(state).contains(fluent);
	}
	
	public ArrayList<Boolean> getFluentsFromState(State state, List<Fluent> fluents) {
		
		ArrayList<Boolean> result = new ArrayList<Boolean>();
		for (Fluent fluent : fluents) {
			
			String name = fluent.getName().toString();
			if (name.equals(GeneralConstants.FALSE))
				result.add(false);
			else if (name.equals(GeneralConstants.TRUE))
				result.add(true);
			else
				result.add(isTrue(state, fluent));
		}
		
		return result;
		
	}

	public Set<Fluent> getFluentsFromState(State state) { return statesFromFluents.get(state); }

	public Map<State, Set<Fluent>> getStatesFromFluents(){
		return this.statesFromFluents;
	}
	
	
	/* (non-Javadoc)
	 * @see ar.dc.uba.model.condition.Valuation#getValuation(ar.dc.uba.model.condition.PropositionalVariable)
	 */
	@Override
	public boolean getValuation(PropositionalVariable variable) {
		for (Fluent fluent : this.statesFromFluents.get(this.actualState)) {
			if (fluent.getName().equals(variable.getName())) {
				return true;
			}
		}
		return false;
	}
	
	public Set<State> getStates() {
	  return statesFromFluents.keySet();
	}

    public void addState(State state){
        statesFromFluents.put(state, new HashSet<Fluent>());
    }
}
