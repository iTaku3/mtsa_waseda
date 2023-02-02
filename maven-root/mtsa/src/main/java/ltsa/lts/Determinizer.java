package ltsa.lts;

import java.util.BitSet;
import java.util.Hashtable;
import java.util.Vector;

/*
* the class computes a Deterministic Finite State Automata
* from a deterministic  finite state automata
* reference "Introduction to Automata Theory, Languages and Computation"
* John e. Hopcroft & Jeffrey D. Ullman p 21-23
*
* non-deterministic transitions to ERROR state are disgarded
* (but not with Dimitra's mod)
* this treats ERROR in the same way as STOP
*/

public class Determinizer {

    final static int TAU = 0;

    CompactState machine;
    LTSOutput output;

    Vector newStates;      //list of newStates, indexed transition lists (EventState)
    Vector stateSets;      //list of sets of oldStates
    Hashtable map;         // maps sets of oldstates (BitSet) -> new state (Integer)

    int nextState;         //next new state number
    int currentState;      //current state being computed

    public Determinizer(CompactState c, LTSOutput output) {
        machine = c;
        this.output = output;
    }

    public CompactState determine() {
        output.outln("make DFA("+machine.name+")");
        newStates =  new Vector(machine.maxStates*2);
        stateSets  =  new Vector(machine.maxStates*2);
        map = new Hashtable(machine.maxStates*2);
        nextState = 0;
        currentState = 0;
        BitSet st =  new BitSet(); st.set(0); // start state is set with state 0
        addState(st);
        while (currentState<nextState) {
            compute(currentState);
            ++currentState;
        }
        return makeNewMachine();
    }

    protected void compute(int n) {
        BitSet state = (BitSet) stateSets.elementAt(n);
        EventState tr = null; // the set of all transitions from this state set
        EventState newtr = null; // the new transitions from the new state
        for (int i = 0; i<state.size(); ++i) {
            if (state.get(i)) tr = EventStateUtils.union(tr,machine.states[i]);
        }
        EventState action = tr;
        while (action!=null) {   //for each action
            boolean errorState = false;
            BitSet newState = new BitSet();
			/*
            if (action.next!=Declaration.ERROR)
                newState.set(action.next);
            else
                errorState = true;
            EventState nd = action.nondet;
            while (nd!=null) {
                if(nd.next!=Declaration.ERROR) {
                    newState.set(nd.next);
                    errorState = false;
                }
                nd=nd.nondet;
            }
			*/
			// change for Dimitra
            if (action.next!=Declaration.ERROR)
                newState.set(action.next);
            else
                errorState = true;
            EventState nd = action.nondet;
            while (nd!=null) {
                if(nd.next!=Declaration.ERROR) {
                    newState.set(nd.next);
                    //errorState = false;
                } else 
				    errorState = true;
                nd=nd.nondet;
            }
            int newStateId;
            if (errorState)
                newStateId = Declaration.ERROR;
            else
                newStateId = addState(newState);
            newtr = EventStateUtils.add(newtr,new EventState(action.event,newStateId));
            action = action.list;
        }
        newStates.addElement(newtr);
    }

    protected int addState(BitSet bs){
        Integer ii = (Integer)map.get(bs);
        if (ii!=null) return ii.intValue();
        map.put(bs,new Integer(nextState));
        stateSets.addElement(bs);
        ++nextState;
        return nextState-1;
    }

    protected CompactState makeNewMachine() {
        CompactState m = new CompactState();
        m.name = machine.name;
        m.alphabet = new String[machine.alphabet.length];
        for (int i=0; i<machine.alphabet.length; i++) m.alphabet[i]=machine.alphabet[i];
        m.maxStates = nextState;
        m.states = new EventState[m.maxStates];
        for (int i=0;i<m.maxStates; i++) {
           m.states[i] = (EventState)newStates.elementAt(i);
        }
		//compute new end state if any
		if (machine.endseq>=0) 
		{
			BitSet es =  new BitSet();
			es.set(machine.endseq);
			Integer ii = (Integer)map.get(es);
            if (ii!=null) m.endseq = ii.intValue();
		}
        output.outln("DFA("+machine.name+") has "+m.maxStates+" states.");
        return m;
    }


}

