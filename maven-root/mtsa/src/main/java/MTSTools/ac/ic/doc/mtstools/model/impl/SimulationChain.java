package MTSTools.ac.ic.doc.mtstools.model.impl;

import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

public class SimulationChain<A> implements Simulation<A> {

	Collection<Simulation<A>> chain;
	
	public SimulationChain() {
		this.chain = new LinkedList<Simulation<A>>();
	}
	
	public SimulationChain(Collection<Simulation<A>> chain) {
		this.chain = chain;
	}
	
	public SimulationChain<A> add(Simulation<A> simulation) {
		this.chain.add(simulation);
		return this;
	}
	
	public <S1,S2> boolean simulate(MTS<S1,A> mts1, S1 s1, MTS<S2,A> mts2, S2 s2, Set<Pair<S1, S2>> relation) {
		boolean result = true;
		Iterator<Simulation<A>> it = this.chain.iterator();
		while(it.hasNext() && result) {
			result = it.next().simulate(mts1,s1,mts2, s2, relation);
		}
		return result;
	}

}
