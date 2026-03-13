package ltsa.ac.ic.doc.mtstools.util.fsp;

import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.impl.MDP;
import MTSTools.ac.ic.doc.mtstools.model.impl.MTSImpl;
import MTSTools.ac.ic.doc.mtstools.model.impl.ProbabilisticTransition;
import ltsa.lts.CompactState;
import ltsa.lts.MyHashStack;
import ltsa.lts.MyList;
import ltsa.lts.util.MTSUtils;

import java.math.BigDecimal;
import java.util.*;

/**
Very closely based on MTSToAutomataConverter
Daniel Sykes 2014
**/

public class MDPToAutomataConverter
{
  private static MDPToAutomataConverter instance;
  
  public static MDPToAutomataConverter getInstance() {
    if (instance == null) {
      instance = new MDPToAutomataConverter();
    }
    return instance;
  }
  
  public CompactState convert(MDP mdp, String name) {
    Set<Long> states = mdp.getStates();
    int size = (states.contains(-1L))? states.size()-1 : states.size();
    int endState = -9999;

    Map<Long, Long> indexToState = buildIndexToState(mdp.getStates());
    MyHashStack statemap = this.buildStateMap(states, size, indexToState);
    //Map<String, Integer> indexToAction = new HashMap<String, Integer>();

    //String[] alphabet;
    //alphabet = this.buildAlphabet(mdp.getActions(), indexToAction);

    List<String> alphabet = new ArrayList<String>();
    MyList automataTransitionsList = addTransitions(mdp, indexToState, alphabet);
    return new CompactState(size, name, statemap, automataTransitionsList, alphabet.toArray(new String[0]), endState);
  }
  
  private HashMap<Long, Long> buildIndexToState(Set<Long> states) {
    HashMap<Long, Long> result = new HashMap<Long, Long>();
    SortedSet<Long> sortedSet = new TreeSet<Long>(states);
    long i = 0;
    for (Long state : sortedSet) {
      if (state.equals(-1L)){
        result.put(state, -1L);
      } else {
        result.put(state, i);
        i++;
      }
    }
    return result;
  }
  
  private MyList addTransitions(MDP mdp, Map<Long,Long> indexToState, List<String> alphabet)
  {
    MyList res = new MyList();
    Map<String,Integer> indexToLabel = new HashMap<String,Integer>();
    int lastIndex = 0;
    
    for (long from : mdp.getStates())
    {
      for (ProbabilisticTransition t :  mdp.getTransitionsFrom(from))
      {
        String probLabel = t.getAction()+"["+t.getBundle()+",p="+t.getProbability()+"]";
        if (!indexToLabel.containsKey(probLabel))
        {
          alphabet.add(t.getAction());
          indexToLabel.put(probLabel, lastIndex++);
        }
        
        res.add(indexToState.get(from).intValue(), 
            MTSUtils.encode(indexToState.get(t.getTo()).intValue()),
            indexToLabel.get(probLabel), t.getBundle(), BigDecimal.valueOf(t.getProbability()));
      }
    }
    return res;
  }

  public MTS<Long,String> convert(MDP mdp)
  {
    MTS<Long,String> res = new MTSImpl<Long,String>(mdp.getInitialState());
    for (String a : mdp.getActions())
      res.addAction(a);
    for (long s : mdp.getStates())
      res.addState(s);
    for (long s : mdp.getStates())
    {
      for (ProbabilisticTransition t : mdp.getTransitionsFrom(s))
        res.addTransition(s, t.getAction(), t.getTo(), MTS.TransitionType.REQUIRED);
    }
    return res;
  }

  
  /*private String[] buildAlphabet(Set<String> actions, Map<String, Integer> indexToAction) {
    String[] alphabet = new String[actions.size()+1];
    alphabet[0] = MTSToAutomataConverter.TAU; //0 is always tau
    indexToAction.put(MTSToAutomataConverter.TAU, 0);
    
    int i = 1;
    for (String action : actions)
    {
      alphabet[i] = action;
      indexToAction.put(alphabet[i], i);
      i++;
    }
    return alphabet;
  }*/
  
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
