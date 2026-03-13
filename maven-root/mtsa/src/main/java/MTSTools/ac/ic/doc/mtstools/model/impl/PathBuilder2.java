package MTSTools.ac.ic.doc.mtstools.model.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;

/**
 * 
 * PathBuilder2 calculates all required or possible paths in a MTS.
 * It only calculates loop free paths, and it does not assume any
 * implicit self-loop transition via silent actions.
 * The advantage of this class over the original PathBuilder is that
 * it calculates all the paths once and caches those values. The other 
 * two differences are that PathBuilder returns path that might contain 
 * a loop (the last transition ends in an intermediate node), and always
 * assumes a self-loop transition by the silent actions. 
 * 
 * @author Dario
 *
 * @param <S> States type
 * @param <A> Labels type
 */
public class PathBuilder2<S, A> {
	
	private final Map<Pair<A,S>, Collection<List<Pair<A, S>>>> paths;
	
	public PathBuilder2(final MTS<S, A> mts,final TransitionType transitionType, final Set<A> silentActions) {
		this.paths = new HashMap<Pair<A,S>, Collection<List<Pair<A,S>>>>();
		
		Map<S, Collection<List<Pair<A,S>>>> tauPaths = getTauPaths(mts, transitionType, silentActions);
		
		// Add all the tau paths to the silent actions
		for (A tau : silentActions) {
			for (Map.Entry<S, Collection<List<Pair<A,S>>>> entry : tauPaths.entrySet()) {
				this.paths.put(Pair.create(tau, entry.getKey()), entry.getValue());
			}
		}
		
		// Extends all tau paths with a non silent action
		Set<A> noSilent = new HashSet<A>(mts.getActions());
		noSilent.removeAll(silentActions);
		for (A label : noSilent) {
			for (Map.Entry<S, Collection<List<Pair<A,S>>>> entry : tauPaths.entrySet()) {
				Collection<List<Pair<A,S>>> extendedPaths = extenedPath(mts, transitionType, entry.getValue(), label);
				this.paths.put(Pair.create(label, entry.getKey()), extendedPaths);
			}
		}
		
		// Makes all the paths sets unmodifiable
		for (Map.Entry<Pair<A,S>, Collection<List<Pair<A,S>>>> entry : this.paths.entrySet()) {			
			this.paths.put(entry.getKey(), Collections.unmodifiableCollection(entry.getValue()));
		}
	}
	
	private Collection<List<Pair<A, S>>> extenedPath(MTS<S, A> mts,
			TransitionType transitionType, Collection<List<Pair<A, S>>> paths,
			A label) {

		Collection<List<Pair<A, S>>> result = new ArrayList<List<Pair<A,S>>>();
		
		for (List<Pair<A, S>> path : paths) {
			S mn = path.get(path.size()-1).getSecond();		// Last state of the tau path
			Collection<S> endStates = mts.getTransitions(mn, transitionType).getImage(label);	// collection of nodes reachable via label from the last state of the path
			for (S s0 : endStates) {
				List<Pair<A, S>> newPath = new ArrayList<Pair<A,S>>(path.size()+1);
				newPath.addAll(path);
				newPath.add(Pair.create(label, s0));
				result.add(newPath);
			}
		}
		
		return result;
	}

	private Map<S, Collection<List<Pair<A, S>>>> getTauPaths(MTS<S, A> mts,
			TransitionType transitionType, Set<A> silentActions) {
		Map<S, Collection<List<Pair<A, S>>>> result = new HashMap<S, Collection<List<Pair<A,S>>>>();
		
		Queue< List<Pair<A, S>>> toExpand = new LinkedList<List<Pair<A,S>>>();
	
		for (S state : mts.getStates()) {
			toExpand.add(Collections.singletonList(new Pair<A,S>(null, state)));
		}
	
		while(!toExpand.isEmpty()) {
			List<Pair<A, S>> path = toExpand.poll();			
			addPath(result, path);
			
			S lastState = path.get(path.size()-1).getSecond();
			for (A tau: silentActions) {
				Collection<S> endStates = mts.getTransitions(lastState, transitionType).getImage(tau);
				for (S s : endStates) {
					if ( !contains(path, s)) {
						List<Pair<A, S>> newPath = new ArrayList<Pair<A,S>>(path.size()+1);
						newPath.addAll(path);
						newPath.add(Pair.create(tau, s));
						toExpand.add(newPath);
					}
				}
			}
			
		}
		
		return result;
	}

	private boolean contains(List<Pair<A, S>> path, S endState) {
		for (Pair<A, S> pair : path) {
			if ( pair.getSecond().equals(endState)) {
				return true;
			}
		}
		return false;
	}

	private void addPath(Map<S, Collection<List<Pair<A, S>>>> result,
			List<Pair<A, S>> path) {
		
		S initialState = path.get(0).getSecond();
		Collection<List<Pair<A, S>>> paths = result.get(initialState);
		if ( paths == null) {
			paths = new ArrayList<List<Pair<A,S>>>();
			result.put(initialState, paths);
		}
		paths.add(path);
	}

	public Collection<List<Pair<A, S>>> getPaths(final S state, final A label) {
		Collection<List<Pair<A, S>>> result = paths.get(Pair.create(label, state));
		if ( result != null) {
			return result;
		}
		return Collections.emptyList();
	}

}
