package ltsa.lts;

import java.util.BitSet;
import java.util.Hashtable;
import java.util.Map;

public class Minimiser {

    final static int TAU = 0;

    BitSet [] E;  // array of |states| x |states| bits
    BitSet [] A;  // array of |states| x |actions| bits
    EventState [] T;  // tau adjacency lists - stores  reflexive transitive closure
    CompactState machine;
    LTSOutput output;

    public Minimiser(CompactState c, LTSOutput output) {
        machine = c;
        this.output = output;
    }

    // initialise T with transitive closure of tau in machine
    private void initTau() {
        T = new EventState[machine.states.length];
        for (int i = 0; i<T.length; i++) {
            T[i] = EventState.reachableTau(machine.states,i);
        }
    }

    // G=>G' using T
    private CompactState machTau(CompactState m) {
        // do T* a pass

    	//agrega a cada estado en el path los estados alcanzables
        for (int i=0; i<m.states.length; i++)
            m.states[i] = EventState.tauAdd(m.states[i],T);
        for (int i=0; i<m.states.length; i++) {
            //agrega los estados alcanzables como vecinos del estado
            m.states[i] = EventStateUtils.union(m.states[i],T[i]);
            m.states[i] = EventState.actionAdd(m.states[i],m.states);
        }
        for (int i=0; i<m.states.length; i++)
            m.states[i] = EventStateUtils.add(m.states[i],new EventState(Declaration.TAU,i));
        output.out(".");
        return m;
    }

    private CompactState removeTau(CompactState m) {
        for (int i=0; i<m.states.length; i++)
            m.states[i] = EventState.removeTau(m.states[i]);
        return m;
    }

    //first step in initialisation is set up E
    private void initialise() {
        // initialise A such that A[i,a] is true if transition a from state i
        A = new BitSet[machine.maxStates];
        for (int i = 0; i<A.length; i++) {
            A[i] = new BitSet(machine.alphabet.length);
            EventState.setActions(machine.states[i],A[i]);
        }
        E = new BitSet[machine.maxStates];
        for (int i=0; i<E.length; i++)
            E[i] = new BitSet(E.length);
        // set E[i,j] if A[i] = A[j] ie same set of transitions
        for (int i=0; i<E.length; i++) {
            E[i].set(i);
            for(int j=0; j<i; j++)
                if (A[i].equals(A[j])){ E[i].set(j); E[j].set(i); }
        }
        output.out(".");
    }

    private void doMinimise() {
        boolean more=true;
        while (more) {
            output.out(".");
            more = false;
            for (int i=0; i<E.length; i++) {
                //Thread.yield();
                for(int j=0; j<i; j++)
                    if (E[i].get(j)){
                        boolean b = is_equivalent(i,j) && is_equivalent(j,i);
                        if (!b) {
                            more = true;
                            E[i].clear(j);
                            E[j].clear(i);
                        }
                      }
            }
        }
    }

    
    
    
    public CompactState minimiseTauClousure() {
    	CompactState minimise = this.minimise();
    	this.removeTau(minimise);
    	return minimise;
    }
    
    /*
    * minimise using observational equivalence
    */
    public CompactState minimise() {
////    	Added to make minimisation of CompositeState and CompactState be consistent.
    	if (CompositeState.reduceFlag) {
    		output.outln("Tau reduction ON");
    		machine.removeNonDetTau();
    	}
         output.out(machine.name+" minimising");
         long start = System.currentTimeMillis();
         CompactState saved = machine.myclone();
         /* distinguish  end state from STOP with self transition using special label */
         if (machine.endseq>=0) {
            int es = machine.endseq;
            machine.states[es] 
             = EventStateUtils.add(machine.states[es],new EventState(machine.alphabet.length,es));
         }
         if (machine.hasTau()) {
             initTau();
             machine = machTau(machine);
             T=null; // release storage
         }
        initialise();
        doMinimise();
        /*   makeNewMachine() uses machine. If first overwrite machine with saved you loose minimization
        machine = saved;
        CompactState c = makeNewMachine();
        */
        CompactState c = makeNewMachine();
        machine = saved;
        long finish =System.currentTimeMillis();
        output.outln("");
        output.outln("Minimised States: "+c.maxStates+" in "+(finish-start)+"ms");
        return  c;
    }

    /*
    * generate minimized trace equivalent deterministic automata
    */
    public CompactState trace_minimise() {
        boolean must_minimize = false;
        //convert to trace equivalent NFA without tau
        if (machine.hasTau()) {
            must_minimize = true;
            output.out("Eliminating tau");
            initTau();
            machine = machTau(machine);
            machine = removeTau(machine);
            T=null; // release storage
        }
        //convert NFA to DFA
        if (must_minimize || machine.isNonDeterministic()) {
           must_minimize = true;
           Determinizer d = new Determinizer(machine,output);
           machine = d.determine();
        }
        // now minimise
        if (must_minimize)
            return minimise();
        else
            return machine;
    }

    private boolean is_equivalent(int i, int j) {
       EventState p = machine.states[i];
       while(p!=null) {
            EventState tr = p;
            while (tr!=null) {
                if (!findSuccessor(j,tr)) return false;
                tr=tr.nondet;
            }
            p=p.list;
        }
        return true;
    }

    private boolean findSuccessor(int j,EventState tr) {
        EventState p = machine.states[j];  //find event
        while(p.event!=tr.event) p=p.list;
        while (p!=null) {
            if (tr.next<0) {
                if (p.next<0) return true;
            } else {
                if (p.next>=0) {
                    //pregunto en la matriz de las maybes si tiene transiciones al reves
                    if (E[tr.next].get(p.next))return true;
                }
            }
            p=p.nondet;
        }
        return false;
    }

    private CompactState makeNewMachine() {
        Hashtable oldtonew = new Hashtable();
        Hashtable newtoold = new Hashtable();
        Counter newSt = new Counter(0);
        for (int i=0; i<E.length; i++) {
                Integer oldIndex = new Integer(i);
                Integer newIndex = (Integer)oldtonew.get(oldIndex);
                if (newIndex==null) {
                    oldtonew.put(oldIndex,newIndex=newSt.label());
                    newtoold.put(newIndex,oldIndex);
                }
            for(int j=0; j<E.length; j++) {
                if (E[i].get(j)) oldtonew.put(new Integer(j),newIndex);
            }
        }
        CompactState m = new CompactState();
        m.name = machine.name;
        m.maxStates = newtoold.size();
        m.alphabet = machine.alphabet;
        m.states = new EventState[m.maxStates];
        if (machine.endseq<0) 
          m.endseq = machine.endseq;
        else {
          m.endseq = ((Integer)oldtonew.get(new Integer(machine.endseq))).intValue();
          /* remove marking transition */
          m.states[m.endseq] 
             = EventState.remove(m.states[m.endseq],new EventState(m.alphabet.length,m.endseq));
        }
          
        for (int i = 0; i<machine.maxStates; i++) {
            int newi = ((Integer)oldtonew.get(new Integer(i))).intValue();
            EventState tmp = EventStateUtils.renumberStates(machine.states[i],oldtonew);
            m.states[newi] = EventStateUtils.union(m.states[newi],tmp);
        }

        for (int i = 0; i<m.maxStates; i++)   // remove reflexive tau
            m.states[i] = EventState.remove(m.states[i],new EventState(Declaration.TAU,i));
	    CompactState response = handleMarkedCompactState(m, machine, oldtonew);
	    return response;
    }

	private CompactState handleMarkedCompactState(CompactState m, CompactState machine, Map<Integer, Integer>
			oldToNew) {
		CompactState response = m;
		if (machine instanceof MarkedCompactState) {
			int[] markedStates = ((MarkedCompactState) machine).getMarkedStates();
			for (int i = 0; i < markedStates.length; ++i) {
				markedStates[i] = oldToNew.get(markedStates[i]);
			}
			response = new MarkedCompactState(m, markedStates);
		}
		return response;
	}
	
    public void print(LTSOutput output) {
        privPrint(output,E);
    }

    private void privPrint(LTSOutput output, BitSet[] E) {
        if (E.length>20) return;
        char [] buf = new char[E.length*2];
        for(int i=0; i<E.length*2; i++) buf[i]=' ';
        output.outln("E:");
        output.out("       ");
        for(int i=0; i<E.length; i++) output.out(" "+i);
        output.outln("");
        for(int i=0; i<E.length; i++){
            output.out("State "+i+" ");
            for(int j=0;j<E.length;j++)
                if(E[i].get(j)) buf[j*2]='1'; else buf[j*2]=' ';
            output.outln(new String(buf));
        }
    }

}