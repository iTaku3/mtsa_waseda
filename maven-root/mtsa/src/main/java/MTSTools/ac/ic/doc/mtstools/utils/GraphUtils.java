package MTSTools.ac.ic.doc.mtstools.utils;

import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.impl.MTSImpl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Victor Wjugow on 20/05/15.
 */
public class GraphUtils {

	/**
	 * @param sccs the strongly connected components of the mts. See {@link GraphUtils#getStronglyConnectedComponents}
	 * @param mts
	 * @return The terminal set of the strongly connected components passed, if there is one.
	 */
	public static <V, E> Set<Set<V>> getTerminalSets(Set<Set<V>> sccs, MTS<V, E> mts) {
		//build mts representing the sccs
		Set<Set<V>> terminalStronglyConnectedComponent = new HashSet<Set<V>>();
		for (Set<V> scc : sccs) {
			if (isTerminalComponent(scc, mts)) {
				terminalStronglyConnectedComponent.add(scc);
			}
		}
		return terminalStronglyConnectedComponent;
	}

	/**
	 * @param <V> States class
	 * @param <E> Transitions class
	 * @param mts to get the Strongly Connected Components from
	 * @return The set of SCCS (which are sets of vertexes)
	 */
	public static <V, E> Set<Set<V>> getStronglyConnectedComponents(MTS<V, E> mts) {
		//build the sccs
		TarjanAlgorithm<V, E> TarjanAlgorithm = new TarjanAlgorithm<V, E>();
		TarjanAlgorithm.buildComponentGraph(mts, null);
		//remove duplicates
		Map<V, Set<V>> componentMembership = TarjanAlgorithm.getComponentMembership();
		Collection<Set<V>> repeated = componentMembership.values();
		Set<Set<V>> sccs = new HashSet<Set<V>>();
		sccs.addAll(repeated);
		return sccs;
	}

	/**
	 * @param scc a strongly connected component
	 * @param mts
	 * @return whether the scc has any node with a transition leaving the scc.
	 */
	private static <V, E> boolean isTerminalComponent(Set<V> scc, MTS<V, E> mts) {
		boolean response = true;
		for (V v : scc) {
			for (Pair<E, V> transition : mts.getTransitions(v, MTS.TransitionType.REQUIRED)) {
				if (!scc.contains(transition.getSecond())) {
					response = false;
					break;
				}
			}
		}
		return response;
	}

	/**
	 * @param states  a set of the baseMts nodes
	 * @param baseMts the mts from which we are getting a smaller mts
	 * @return an MTS built with the states present in states and that maintains the transitions present in baseMts
	 */
	public static <V, E> MTS<V, E> fromSetToMts(Set<V> states, MTS<V, E> baseMts) {
		V initialState = states.iterator().next();
		MTS<V, E> mts = new MTSImpl<V, E>(initialState);
		for (V state : states) {
			BinaryRelation<E, V> transitions = baseMts.getTransitions(state, MTS.TransitionType.REQUIRED);
			for (Pair<E, V> transition : transitions) {
				V toState = transition.getSecond();
				if (states.contains(toState)) {
					mts.addState(state);
					mts.addAction(transition.getFirst());
					mts.addState(toState);
					mts.addRequired(state, transition.getFirst(), toState);
				}
			}
		}
		return mts;
	}
}