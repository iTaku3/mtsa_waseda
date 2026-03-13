package ltsa.lts;

import ltsa.dispatcher.TransitionSystemDispatcher;

import java.math.BigDecimal;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import static ltsa.lts.CompactStateUtils.convertToProbabilistic;
import static ltsa.lts.util.MTSUtils.*;

class StateMachine {

    String name;
    String kludgeName;
    Hashtable<String, Integer> alphabet = new Hashtable<String, Integer>();
    Vector<String> hidden;
    Relation relabels;
    Hashtable<String, Integer> explicit_states = new Hashtable<String, Integer>();
    Hashtable constants; // a bit of a kludge, should not be here
    Counter eventLabel = new Counter(0);
    Counter stateLabel = new Counter(0);
    Vector<Transition> transitions = new Vector<Transition>();
    boolean isProperty = false;
    boolean isMinimal = false;
    boolean isDeterministic = false;
    boolean isOptimistic = false;
    boolean isPessimistic = false;
    boolean isAbstract = false;
    boolean isClousure = false;
    boolean exposeNotHide = false;
    boolean isController = false;

    boolean isProbabilistic = false;
    boolean isStarEnv = false;
    boolean isPlant = false;
    boolean isControlledDet = false;
    boolean isMDP = false;
    Vector<String> actionsToErrorSet;

    Symbol goal;
    Hashtable<Integer, CompactState> sequentialInserts;
    Hashtable<Integer, Integer> preInsertsLast;
    Hashtable<Integer, CompactState> preInsertsMach;
    Hashtable<Integer, Integer> aliases = new Hashtable<Integer, Integer>();

    public static LTSOutput output;

    public StateMachine(ProcessSpec spec, Vector<Value> params) {
        name = spec.getname();
        if (params != null) {
            spec.doParams(params);
            kludgeName = name + paramString(params);
        } else
            kludgeName = name;
        make(spec);
    }

    public StateMachine(ProcessSpec spec) {
        // compute machine name
        name = spec.getname();
        kludgeName = name;
        make(spec);
    }

    private void make(ProcessSpec spec) {
        constants = spec.constants;
        alphabet.put("tau", eventLabel.label());
        // compute explicit states
        spec.explicitStates(this);
        // crunch aliases
        spec.crunch(this);
        // relabel states in contiguous range from zero
        renumber();
        // compute transitions
        spec.transition(this);
        // alphabet extensions
        spec.addAlphabet(this);
        // alphabet relabels;
        spec.relabelAlphabet(this);
        // alphabet concealments
        spec.hideAlphabet(this);
        spec.errorAlphabet(this);
        isProperty = spec.isProperty;
        isMinimal = spec.isMinimal;
        isDeterministic = spec.isDeterministic;
        isOptimistic = spec.isOptimistic;
        isPessimistic = spec.isPessimistic;
        isAbstract = spec.isAbstract;
        isClousure = spec.isClousure;
        exposeNotHide = spec.exposeNotHide;
        isController = spec.isController;
        //isControlledDet = spec.isControlledDet; //Not Defined
        //isPlant = spec.isPlant; //Not Defined
        isProbabilistic = spec.isProbabilistic;
        isMDP = spec.isMDP;
        isStarEnv = spec.isStarEnv;
        goal = spec.goal;
    }

    public CompactState makeCompactState() {
        CompactState c = new CompactState();
        c.name = kludgeName;
        c.maxStates = stateLabel.lastLabel().intValue();
        Integer ii = (Integer) explicit_states.get("END");
        if (ii != null) c.endseq = ii.intValue();
        c.alphabet = new String[alphabet.size()];
        Enumeration e = alphabet.keys();
        while (e.hasMoreElements()) {
            String s = (String) e.nextElement();
            int j = ((Integer) alphabet.get(s)).intValue();
            if (s.equals("@")) s = "@" + c.name;
            c.alphabet[j] = s;
        }
        if (!isProperty) {
            c.alphabet = getAlphabetWithMaybes(c.getTransitionsLabels());
            //THIS IS WHERE THE SIN OCCURS ***
            //c.alphabet = buildAlphabet(c.getTransitionsLabels());
        } else {
            c.alphabet = addTauMaybeAlphabet(c.getTransitionsLabels());
        }
        alphabet.clear();
        for (int i = 0; i < c.getTransitionsLabels().length; i++) {
            alphabet.put(c.getTransitionsLabels()[i], i);
        }
//        if (isProbabilistic) {
//            c.states = new ProbabilisticEventState[c.maxStates];
//            compactStateTransitions(c, true);
//        } else {
            c.states = new EventState[c.maxStates];
            compactStateTransitions(c, false);
//        }

        if (sequentialInserts != null)
            c.expandSequential(sequentialInserts);
        if (relabels != null)
            c.relabel(relabels);
        applyHiding(c);
        if (isProperty) {
            if (c.isNonDeterministic() || c.hasTau())
                Diagnostics.fatal("primitive property processes must be deterministic: " + name);
            c.makeProperty(this.actionsToErrorSet);
        }
        check_for_ERROR(c);
        c.reachable();
        if (isProperty && isMTSRepresentation(c)) {
            throw new RuntimeException("Properties must be LTSs");
        }
        if (isOptimistic) {
            c = (CompactState) TransitionSystemDispatcher.getOptimisticModel(c, output);
        }
        if (isPessimistic) {
            c = (CompactState) TransitionSystemDispatcher.getPessimistModel(c);
        }
        if (isMinimal) {
            c = TransitionSystemDispatcher.minimise(c, output);
        }
        if (isDeterministic) {
            c = TransitionSystemDispatcher.determinise(c, output);
        }
        if (isClousure) {
            c = TransitionSystemDispatcher.getTauClosure(c, output);
        }
        if (isAbstract) {
            c = TransitionSystemDispatcher.getAbstractModel(c, output);
        }
        if (isStarEnv) {
            c = TransitionSystemDispatcher.getStarEnv(c, output);
        }
//        if (isProbabilistic) {
//            c = convertToProbabilistic(c);
//        }
        
        c = CompactStateUtils.convertIfProbabilistic(c);
        return c;    
    }

    private void compactStateTransitions(CompactState c, boolean isProbabilistic) {
        Enumeration e;
        e = transitions.elements();
        while (e.hasMoreElements()) {
            Transition t = (Transition) e.nextElement();
            String action = "" + t.event;
            if (action.contains(MAYBE_MARK)) {
                action = getMaybeAction(action);
            }
            int ev = ((Integer) alphabet.get(action)).intValue();
//            if (isProbabilistic) {
//                ProbabilisticEventState evSt;
//                if (t instanceof ProbabilisticTransition) {
//                    evSt = EventStateFactory.createProbabilisticEventState(ev, t);
//                } else {
//                    int max = CompactStateUtils.getMaxBundle(c);//Esto no me sirve porque la maqina la estoy generando
//                    //entonces aun no tengo asignados todos los bundles. Necesito tener el max de StateMachine.
//                    evSt = EventStateFactory.createProbabilisticEventState(ev,t, max++);
//                }
//                c.states[t.from] = EventStateUtils.add(c.states[t.from], evSt);
//            } else {
//                EventState evSt = EventStateFactory.createEventState(ev, t);
//                c.states[t.from] = EventStateUtils.add(c.states[t.from], evSt);
//            }
            EventState evSt= EventStateFactory.createEventState(ev, t);
            c.states[t.from] =EventStateUtils.add(c.states[t.from], evSt);
        }
    }

    public void applyHiding(CompactState c) {
        if (hidden != null) {
            computeHiddenAlphabet(hidden);
            if (!exposeNotHide)
                c.conceal(hidden);
            else
                c.expose(hidden);
        }
    }


    // is the first state = ERROR ie P = ERROR?
    void check_for_ERROR(CompactState c) {
        Integer I = (Integer) explicit_states.get(name);
        if (I.intValue() == Declaration.ERROR) {
            c.states = new EventState[1];
            c.maxStates = 1;
            c.states[0]
                    = EventStateUtils.add(c.states[0],
                    new EventState(Declaration.TAU, Declaration.ERROR));
        }
    }

    void addSequential(Integer state, CompactState mach) {
        if (sequentialInserts == null) sequentialInserts = new Hashtable<Integer, CompactState>();
        sequentialInserts.put(state, mach);
    }

    void preAddSequential(Integer start, Integer end, CompactState mach) {
        if (preInsertsLast == null) preInsertsLast = new Hashtable<Integer, Integer>();
        if (preInsertsMach == null) preInsertsMach = new Hashtable<Integer, CompactState>();
        preInsertsLast.put(start, end);
        preInsertsMach.put(start, mach);
    }

    private void insertSequential(int[] map) {
        if (preInsertsMach == null) return;
        Enumeration e = preInsertsMach.keys();
        while (e.hasMoreElements()) {
            Integer start = (Integer) e.nextElement();
            CompactState mach = (CompactState) preInsertsMach.get(start);
            Integer end = (Integer) preInsertsLast.get(start);
            Integer newStart = new Integer(map[start.intValue()]);
            mach.offsetSeq(
                    newStart.intValue(),
                    end.intValue() >= 0 ? map[end.intValue()] : end.intValue()
            );
            addSequential(newStart, mach);
        }
    }

    private Integer number(Integer alias, Counter newLabel) {
        if (preInsertsMach == null)
            return newLabel.label();
        CompactState mach = (CompactState) preInsertsMach.get(alias);
        if (mach == null)
            return newLabel.label();
        return newLabel.interval(mach.maxStates);
    }

    private void crunch(int index, int[] map) {
        int newi = map[index];
        while (newi >= 0 && newi != map[newi])
            newi = map[newi];
        map[index] = newi;
    }

    private void renumber() { //relabel states
        int map[] = new int[stateLabel.lastLabel().intValue()];
        for (int i = 0; i < map.length; ++i)
            map[i] = i;
        //apply alias
        Enumeration e = aliases.keys();
        while (e.hasMoreElements()) {
            Integer targ = (Integer) e.nextElement();
            Integer alias = (Integer) aliases.get(targ);
            map[targ.intValue()] = alias.intValue();
        }
        //crunch aliases
        for (int i = 0; i < map.length; ++i)
            crunch(i, map);
        //renumber
        Counter newLabel = new Counter(0);
        Hashtable<Integer, Integer> oldnew = new Hashtable<Integer, Integer>();
        for (int i = 0; i < map.length; ++i) {
            Integer alias = new Integer(map[i]);
            if (!oldnew.containsKey(alias)) {
                Integer newi = map[i] >= 0 ? number(alias, newLabel) : new Integer(-1);
                oldnew.put(alias, newi);
                map[i] = newi.intValue();
            } else {
                Integer newi = (Integer) oldnew.get(alias);
                map[i] = newi.intValue();
            }
        }
        // create offset insert sequential processes
        insertSequential(map);
        // renumber state/local process lookip table
        e = explicit_states.keys();
        while (e.hasMoreElements()) {
            String s = (String) e.nextElement();
            Integer ii = (Integer) explicit_states.get(s);
            if (ii.intValue() >= 0)
                explicit_states.put(s, new Integer(map[ii.intValue()]));
        }
        stateLabel = newLabel;
    }

    public void print(LTSOutput output) {
        // print name
        output.outln("PROCESS: " + name);
        // print alphabet
        output.outln("ALPHABET:");
        Enumeration e = alphabet.keys();
        while (e.hasMoreElements()) {
            String s = (String) e.nextElement();
            output.outln("\t" + alphabet.get(s) + "\t" + s);
        }
        // print states
        output.outln("EXPLICIT STATES:");
        e = explicit_states.keys();
        while (e.hasMoreElements()) {
            String s = (String) e.nextElement();
            output.outln("\t" + explicit_states.get(s) + "\t" + s);
        }
        // print transitions
        output.outln("TRANSITIONS:");
        e = transitions.elements();
        while (e.hasMoreElements()) {
            Transition t = (Transition) e.nextElement();
            output.outln("\t" + t);
        }
    }

    static String paramString(Vector<Value> v) {
        int max = v.size() - 1;
        StringBuffer buf = new StringBuffer();
        Enumeration e = v.elements();
        buf.append("(");
        for (int i = 0; i <= max; i++) {
            String s = e.nextElement().toString();
            buf.append(s);
            if (i < max) {
                buf.append(",");
            }
        }
        buf.append(")");
        return buf.toString();
    }

}
