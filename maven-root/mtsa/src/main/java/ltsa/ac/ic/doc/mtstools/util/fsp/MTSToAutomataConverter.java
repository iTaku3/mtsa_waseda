package ltsa.ac.ic.doc.mtstools.util.fsp;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import ltsa.lts.CompactState;
import ltsa.lts.MyHashStack;
import ltsa.lts.MyList;
import ltsa.lts.util.MTSUtils;
import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;

public class MTSToAutomataConverter {

	private static MTSToAutomataConverter instance;
 
	public static final String TAU = "tau";
	public static final String TAU_MAYBE = "tau?";

	public static MTSToAutomataConverter getInstance() {
		if (instance == null) {
			instance = new MTSToAutomataConverter();
		}
		return instance;
	}

	public CompactState convert(MTS<Long, String> mts, String name) {
		/*Set<Long> states = mts.getStates();
		int size = (states.contains(-1L))? states.size()-1 : states.size();
		int endState = -9999;

		Map<Long, Long> indexToState = buildIndexToState(mts.getStates());
		MyHashStack statemap = this.buildStateMap(states, size, indexToState);
		Map<String, Integer> indexToAction = new HashMap<String, Integer>();

		String[] alphabet = this.buildAlphabet(mts.getActions(), indexToAction);
		
		
		// ver que pasa con que una vez que tengo las rel binarias ya no se el
		// tipo
		MyList automataTransitionsList = new MyList();
		this.addTransitions(mts, indexToAction, TransitionType.MAYBE, automataTransitionsList, indexToState);
		this.addTransitions(mts, indexToAction, TransitionType.REQUIRED, automataTransitionsList, indexToState);
		return new CompactState(size, name, statemap, automataTransitionsList, alphabet, endState);*/
	  return convert(mts, name, true);
	}
	
	public CompactState convert(MTS<Long, String> mts, String name, boolean includeMaybes) {
    Set<Long> states = mts.getStates();
    int size = (states.contains(-1L))? states.size()-1 : states.size();
    int endState = -9999;

    Map<Long, Long> indexToState = buildIndexToState(mts.getStates(), mts.getInitialState());
    MyHashStack statemap = this.buildStateMap(states, size, indexToState);
    Map<String, Integer> indexToAction = new HashMap<String, Integer>();

    String[] alphabet;
    if (includeMaybes)
      alphabet = this.buildAlphabet(mts.getActions(), indexToAction);
    else
      alphabet = this.buildAlphabetNoMaybes(mts.getActions(), indexToAction);
    
    // ver que pasa con que una vez que tengo las rel binarias ya no se el
    // tipo
    MyList automataTransitionsList = new MyList();
    if (includeMaybes)
      this.addTransitions(mts, indexToAction, TransitionType.MAYBE, automataTransitionsList, indexToState);
    this.addTransitions(mts, indexToAction, TransitionType.REQUIRED, automataTransitionsList, indexToState);
    return new CompactState(size, name, statemap, automataTransitionsList, alphabet, endState);
  }

	private HashMap<Long, Long> buildIndexToState(Set<Long> states, Long initialState) {
		HashMap<Long, Long> result = new HashMap<Long, Long>();
		SortedSet<Long> sortedSet = new TreeSet<Long>(states);
		long i = 0;
	  result.put(initialState, i);
	  i++;
		for (Iterator<Long> it = sortedSet.iterator(); it.hasNext(); ) {
			Long state = it.next();
			if (state.equals(-1L)){
				result.put(state, -1L);
			} else if (!state.equals(initialState)) { //don't add initial state twice
				result.put(state, i);
				i++;
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private void addTransitions(MTS<Long, String> mts, Map<String, Integer> indexToAction,
			TransitionType transitionType, MyList automataTransitionsList, Map<Long, Long> indexToState) {
		Map<Long, BinaryRelation<String, Long>> transitions = mts.getTransitions(transitionType);
		for (Iterator it = transitions.entrySet().iterator(); it.hasNext();) {
			Entry<Long, BinaryRelation<String, Long>> tranFromState = (Entry<Long, BinaryRelation<String, Long>>) it.next();
			
			Long fromState = tranFromState.getKey();
			
			BinaryRelation<String, Long> neighbors = tranFromState.getValue();
			for (Iterator<Pair<String, Long>> iterator = neighbors.iterator(); iterator.hasNext();) {
				Pair<String, Long> axis = iterator.next();
				// paso el estado de long a int
				// TODO cambiar esto
				if (TransitionType.MAYBE.equals(transitionType)) {
					
					automataTransitionsList.add(indexToState.get(fromState).intValue(), 
							MTSUtils.encode(indexToState.get(axis.getSecond()).intValue()),
							indexToAction.get(MTSUtils.getMaybeAction(axis.getFirst())));
				} else {

					automataTransitionsList.add(indexToState.get(fromState).intValue(), 
							MTSUtils.encode(indexToState.get(axis.getSecond()).intValue()),
							indexToAction.get(axis.getFirst()));
				}

			}
		}
	}
	
	/*
	 * Generates the IAutomata alphabet from a set of actions.
	 */
	private String[] buildAlphabet(Set<String> actions, Map<String, Integer> indexToAction) {
		String[] alphabet;
		if (!actions.contains(TAU)) {
			alphabet = new String[(actions.size() * 2) + 2];
		} else {
			alphabet = new String[(actions.size() * 2)];
		}
		 
		
		// para cada elem e del alfabeto en el IAutomata se pone e y e?
		this.addTaus(indexToAction, alphabet);
		int i = 2;
		for (Iterator<String> it = actions.iterator(); it.hasNext();) {
			String action = it.next();
			if (!TAU.equals(action) && !TAU_MAYBE.equals(action)) {
				alphabet[i] = action;
				indexToAction.put(alphabet[i], i);
				i++;
				alphabet[i] = MTSUtils.getMaybeAction(action);
				indexToAction.put(alphabet[i], i);
				i++;
			}
		}
		return alphabet;
	}
	
	private String[] buildAlphabetNoMaybes(Set<String> actions, Map<String, Integer> indexToAction) {
    String[] alphabet = new String[actions.size()+1];
    alphabet[0] = TAU; //0 is always tau
    indexToAction.put(TAU, 0);
    
    int i = 1;
    for (String action : actions)
    {
      alphabet[i] = action;
      indexToAction.put(alphabet[i], i);
      i++;
    }
    return alphabet;
  }

	private void addTaus(Map<String, Integer> indexToAction, String[] alphabet) {
		alphabet[0] = TAU;
		indexToAction.put("tau", 0);
		alphabet[1] = TAU_MAYBE;
		indexToAction.put("tau?", 1);
	}

	private MyHashStack buildStateMap(Set<Long> states, int size, Map<Long, Long> indexToState) {
		MyHashStack statemap = new MyHashStack(size);

		for (Iterator<Long> it = states.iterator(); it.hasNext();) {
			int id = indexToState.get(it.next()).intValue();
			// cachea ranks
			statemap.pushPut(MTSUtils.encode(id));
			statemap.mark(id);
		}
		return statemap;
	}

}
