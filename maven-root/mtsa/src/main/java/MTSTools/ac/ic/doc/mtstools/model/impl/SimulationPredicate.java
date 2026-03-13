package MTSTools.ac.ic.doc.mtstools.model.impl;

import java.util.Set;

import org.apache.commons.collections15.Predicate;

import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;

public class SimulationPredicate<S1, S2, A> implements Predicate<Pair<S1, S2>> {

	private Set<Pair<S1, S2>> relation;
	private Simulation simulation;
	private MTS<S1, A> mts1;
	private MTS<S2, A> mts2;

	public SimulationPredicate(MTS<S1, A> mts1, MTS<S2, A> mts2,
			Set<Pair<S1, S2>> relation, Simulation simulation) {
		this.mts1 = mts1;
		this.mts2 = mts2;
		this.relation = relation;
		this.simulation = simulation;
	}

	public boolean evaluate(Pair<S1, S2> pair) {
		return this.simulation.simulate(this.mts1, pair.getFirst(), this.mts2,
				pair.getSecond(), this.relation);
	}

}
