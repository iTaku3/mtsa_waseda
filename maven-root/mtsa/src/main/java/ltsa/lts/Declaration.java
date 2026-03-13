package ltsa.lts;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;


/* -----------------------------------------------------------------------*/

public abstract class Declaration {
    public final static int TAU = 0;
    public final static int TAU_MAYBE = 1;
    public final static int ERROR = -1;
    public final static int STOP = 0;
    public final static int SUCCESS = 1;

    public void explicitStates(StateMachine m) {
    }

    ;

    public void crunch(StateMachine m) {
    }

    ; // makes sure aliases refer to the same state

    public void transition(StateMachine m) {
    }

    ;
}

/* -----------------------------------------------------------------------*/

class ProcessSpec extends Declaration {
    Symbol name;
    Hashtable constants;
    Hashtable init_constants = new Hashtable();
    Vector parameters = new Vector();
    Vector<StateDefn> stateDefns = new Vector<StateDefn>();
    LabelSet alphaAdditions;
    LabelSet alphaHidden;
    Vector alphaRelabel;
    boolean isProperty = false;
    boolean isMinimal = false;
    boolean isDeterministic = false;
    boolean isOptimistic = false;
    boolean isPessimistic = false;
    boolean isClousure = false;
    boolean isAbstract = false;
    boolean exposeNotHide = false;
    public boolean isController = false;

    public boolean isProbabilistic = false;
    public boolean isMDP = false;
    public boolean isStarEnv = false;
    public Symbol goal;
    public LabelSet actionsToErrorSet;

    File importFile = null;   // used if the process is imported from a .aut file

    public boolean imported() {
        return importFile != null;
    }

    public String getname() {
        constants = (Hashtable) init_constants.clone();
        StateDefn s = stateDefns.firstElement();
        name = s.name;
        if (s.range != null)
            Diagnostics.fatal("process name cannot be indexed", name);
        return s.name.toString();
    }

    public void explicitStates(StateMachine m) {
        Enumeration<StateDefn> e = stateDefns.elements();
        while (e.hasMoreElements()) {
            Declaration d = e.nextElement();
            d.explicitStates(m);
        }
    }

    public void addAlphabet(StateMachine m) {
        if (alphaAdditions != null) {
            Vector a = alphaAdditions.getActions(constants);
            Enumeration e = a.elements();
            while (e.hasMoreElements()) {
                String s = (String) e.nextElement();
                if (!m.alphabet.containsKey(s))
                    m.alphabet.put(s, m.eventLabel.label());
            }
        }
    }

    public void hideAlphabet(StateMachine m) {
        if (alphaHidden == null) {
            return;
        }
        m.hidden = alphaHidden.getActions(constants);
    }

    public void relabelAlphabet(StateMachine m) {
        if (alphaRelabel == null) return;
        m.relabels = new Relation();
        Enumeration e = alphaRelabel.elements();
        while (e.hasMoreElements()) {
            RelabelDefn r = (RelabelDefn) e.nextElement();
            r.makeRelabels(constants, m.relabels);
        }
    }

    public void crunch(StateMachine m) {
        Enumeration<StateDefn> e = stateDefns.elements();
        while (e.hasMoreElements()) {
            Declaration d = e.nextElement();
            d.crunch(m);
        }
    }

    public void transition(StateMachine m) {
        Enumeration<StateDefn> e = stateDefns.elements();
        while (e.hasMoreElements()) {
            Declaration d = e.nextElement();
            d.transition(m);
        }
    }

    public void doParams(Vector actuals) {
        Enumeration a = actuals.elements();
        Enumeration f = parameters.elements();
        while (a.hasMoreElements() && f.hasMoreElements())
            constants.put(f.nextElement(), a.nextElement());
    }

    public ProcessSpec myclone() {
        ProcessSpec p = new ProcessSpec();
        p.name = name;
        p.constants = (Hashtable) constants.clone();
        p.init_constants = init_constants;
        p.parameters = parameters;
        Enumeration<StateDefn> e = stateDefns.elements();
        while (e.hasMoreElements()) {
            p.stateDefns.addElement(e.nextElement().myclone());
        }

        p.alphaAdditions = alphaAdditions;
        p.alphaHidden = alphaHidden;
        p.alphaRelabel = alphaRelabel;
        p.isProperty = isProperty;
        p.isMinimal = isMinimal;
        p.isDeterministic = isDeterministic;
        p.exposeNotHide = exposeNotHide;
        p.importFile = importFile;
        p.isOptimistic = isOptimistic;
        p.isPessimistic = isPessimistic;
        p.isClousure = isClousure;
        p.isAbstract = isAbstract;
        p.exposeNotHide = exposeNotHide;
        p.isController = isController;
        p.isProbabilistic = isProbabilistic;
        p.isMDP = isMDP;
        p.isStarEnv = isStarEnv;

        return p;
    }

    public void errorAlphabet(StateMachine m) {
        if (actionsToErrorSet == null) {
            return;
        }
        m.actionsToErrorSet = actionsToErrorSet.getActions(constants);
    }

}

/* -----------------------------------------------------------------------*/


class StateDefn extends Declaration {
    Symbol name;
    boolean accept = false;
    ActionLabels range;  //use label with no name
    StateExpr stateExpr;

    private void check_put(String s, StateMachine m) {
        if (m.explicit_states.containsKey(s))
            Diagnostics.fatal("duplicate definition -" + name, name);
        else
            m.explicit_states.put(s, m.stateLabel.label());
    }

    public void explicitStates(StateMachine m) {
        if (range == null) {
            String s = name.toString();
            if (s.equals("STOP") || s.equals("ERROR") || s.equals("END"))
                Diagnostics.fatal("reserved local process name -" + name, name);
            check_put(s, m);
        } else {
            Hashtable locals = new Hashtable();
            range.initContext(locals, m.constants);
            while (range.hasMoreNames()) {
                check_put(name.toString() + "." + range.nextName(), m);
            }
            range.clearContext();
        }
    }

    private void crunchAlias(StateExpr st, String n, Hashtable locals, StateMachine m) {
        String s = st.evalName(locals, m);
        Integer i = (Integer) m.explicit_states.get(s);
        if (i == null) {
            if (s.equals("STOP")) {
                m.explicit_states.put("STOP", i = m.stateLabel.label());
            } else if (s.equals("ERROR")) {
                m.explicit_states.put("ERROR", i = new Integer(Declaration.ERROR));
            } else if (s.equals("END")) {
                m.explicit_states.put("END", i = m.stateLabel.label());
            } else {
                m.explicit_states.put("ERROR", i = new Integer(Declaration.ERROR));
                Diagnostics.warning(s + " defined to be ERROR",
                        "definition not found- " + s, st.name);
            }
        }
        CompactState mach = null;
        if (st.processes != null)
            mach = st.makeInserts(locals, m);
        if (mach != null)
            m.preAddSequential((Integer) m.explicit_states.get(n), i, mach);
        else
            m.aliases.put(m.explicit_states.get(n), i);
    }

    public void crunch(StateMachine m) {
        if (stateExpr.name == null && stateExpr.boolexpr == null) return;
        Hashtable locals = new Hashtable();
        if (range == null)
            crunchit(m, locals, stateExpr, name.toString());
        else {
            range.initContext(locals, m.constants);
            while (range.hasMoreNames()) {
                String s = "" + name + "." + range.nextName();
                crunchit(m, locals, stateExpr, s);
            }
            range.clearContext();
        }
    }

    private void crunchit(StateMachine m, Hashtable locals, StateExpr st, String s) {
        if (st.name != null)
            crunchAlias(st, s, locals, m);
        else if (st.boolexpr != null) {
            if (Expression.evaluate(st.boolexpr, locals, m.constants).intValue() != 0)
                st = st.thenpart;
            else
                st = st.elsepart;
            if (st != null) crunchit(m, locals, st, s);
        }
    }

    public void transition(StateMachine m) {
        int from;
        if (stateExpr.name != null) return; //this is an alias definition
        Hashtable locals = new Hashtable();
        if (range == null) {
            from = ((Integer) m.explicit_states.get("" + name)).intValue();
            stateExpr.firstTransition(from, locals, m);
            if (accept) {
                if (!m.alphabet.containsKey("@"))
                    m.alphabet.put("@", m.eventLabel.label());
                Symbol e = new Symbol(Symbol.IDENTIFIER, "@");
                m.transitions.addElement(new Transition(from, e, from));
            }
        } else {
            range.initContext(locals, m.constants);
            while (range.hasMoreNames()) {
                from = ((Integer) m.explicit_states.get("" + name + "." + range.nextName())).intValue();
                stateExpr.firstTransition(from, locals, m);
            }
            range.clearContext();
        }
    }

    public StateDefn myclone() {
        StateDefn sd = new StateDefn();
        sd.name = name;
        sd.accept = accept;
        if (range != null)
            sd.range = range.myclone();
        if (stateExpr != null)
            sd.stateExpr = stateExpr.myclone();
        return sd;
    }


}


/* -----------------------------------------------------------------------*/

class SeqProcessRef {
    Symbol name;
    Vector actualParams;

    static LTSOutput output;

    SeqProcessRef(Symbol n, Vector params) {
        name = n;
        actualParams = params;
    }

    CompactState instantiate(Hashtable locals, Hashtable constants) {
        //compute parameters
        Vector<Value> actuals = paramValues(locals, constants);
        String refname = (actuals == null) ? name.toString() : name.toString() + StateMachine.paramString(actuals);
        // have we already compiled it?
        CompactState mach = LTSCompiler.compiled.get(refname);
        if (mach == null) {
            // we have not got one so first see if its a defined process
            ProcessSpec p = (ProcessSpec) LTSCompiler.processes.get(name.toString());
            if (p != null) {
                p = p.myclone();
                if (actualParams != null) {  //check that parameter arity is correct
                    if (actualParams.size() != p.parameters.size())
                        Diagnostics.fatal("actuals do not match formal parameters", name);
                }
                StateMachine one = new StateMachine(p, actuals);
                mach = one.makeCompactState();
                output.outln("-- compiled:" + mach.name);
            }
        }
        if (mach == null) {
            CompositionExpression ce = (CompositionExpression) LTSCompiler.composites.get(name.toString());
            if (ce != null) {
                CompositeState cs = ce.compose(actuals);
                mach = cs.create(output);
            }
        }
        if (mach != null) {
            LTSCompiler.compiled.put(mach.name, mach);  // add to compiled processes
            if (!mach.isSequential())
                Diagnostics.fatal("process is not sequential - " + name, name);
            return mach.myclone();
        }
        Diagnostics.fatal("process definition not found- " + name, name);
        return null;
    }

    private Vector<Value> paramValues(Hashtable locals, Hashtable constants) {
        if (actualParams == null) return null;
        Enumeration e = actualParams.elements();
        Vector<Value> v = new Vector<Value>();
        while (e.hasMoreElements()) {
            Stack stk = (Stack) e.nextElement();
            v.addElement(Expression.getValue(stk, locals, constants));
        }
        return v;
    }

}

class StateExpr extends Declaration {
    //if name !=null then no choices
    Vector<SeqProcessRef> processes;
    Symbol name;
    Vector expr; //vector of expressions stacks, one for each subscript
    ActionLabels actions;
    Vector<ChoiceElement> choices;
    Stack boolexpr;
    StateExpr thenpart;
    StateExpr elsepart;

    public void addSeqProcessRef(SeqProcessRef sp) {
        if (processes == null) processes = new Vector<SeqProcessRef>();
        processes.addElement(sp);
    }

    public CompactState makeInserts(Hashtable locals, StateMachine m) {
        Vector<CompactState> seqs = new Vector<CompactState>();
        Enumeration<SeqProcessRef> e = processes.elements();
        while (e.hasMoreElements()) {
            SeqProcessRef sp = e.nextElement();
            CompactState mach = sp.instantiate(locals, m.constants);
            if (!mach.isEnd()) seqs.addElement(mach);
        }
        if (seqs.size() > 0)
            return CompactState.sequentialCompose(seqs);
        return null;
    }

    public Integer instantiate(Integer to, Hashtable locals, StateMachine m) {
        if (processes == null) return to;
        CompactState seqmach = makeInserts(locals, m);
        if (seqmach == null) return to;
        Integer start = m.stateLabel.interval(seqmach.maxStates);
        seqmach.offsetSeq(start.intValue(), to.intValue());
        m.addSequential(start, seqmach);
        return start;
    }

    public void firstTransition(int from, Hashtable locals, StateMachine m) {
        if (boolexpr != null) {
            if (Expression.evaluate(boolexpr, locals, m.constants).intValue() != 0) {
                if (thenpart.name == null)
                    thenpart.firstTransition(from, locals, m);
            } else {
                if (elsepart.name == null)
                    elsepart.firstTransition(from, locals, m);
            }
        } else {
            addTransitions(from, locals, m);
        }
    }

    public void addTransitions(int from, Hashtable locals, StateMachine m) {
        if (actions != null) {
            actions.initContext(locals, m.constants);
            while (actions.hasMoreNames()) {
                actions.nextName();
                addTransition(from, locals, m);
            }
            actions.clearContext();
        } else {
            addTransition(from, locals, m);
        }
    }

    public void addTransition(int from, Hashtable locals, StateMachine m) {
        Enumeration<ChoiceElement> e = choices.elements();
        while (e.hasMoreElements()) {
            ChoiceElement d = e.nextElement();
            d.addTransition(from, locals, m);
        }
    }

    public void endProbabilisticTransition(int from, Symbol event, Hashtable locals,
                                           StateMachine m, BigDecimal prob, int bundle) {
        // TODO for now this is kept extremely simple. No conditions, no anything
        // TODO this will fail for implicit ERROR states
        if (boolexpr != null) {
            if (Expression.evaluate(boolexpr, locals, m.constants).intValue() != 0)
                thenpart.endProbabilisticTransition(from, event, locals, m, prob, bundle);
            else
                elsepart.endProbabilisticTransition(from, event, locals, m, prob, bundle);
        } else {
            Integer to;
            if (name != null) {
                to = (Integer) m.explicit_states.get(evalName(locals, m));
                if (to == null) {
                    if (evalName(locals, m).equals("STOP")) {
                        m.explicit_states.put("STOP", to = m.stateLabel.label());
                    } else if (evalName(locals, m).equals("ERROR")) {
                        m.explicit_states.put("ERROR", to = new Integer(Declaration.ERROR));
                    } else if (evalName(locals, m).equals("END")) {
                        m.explicit_states.put("END", to = m.stateLabel.label());
                    } else {
                        m.explicit_states.put(evalName(locals, m), to = new Integer(Declaration.ERROR));
                        Diagnostics.warning(evalName(locals, m) + " defined to be ERROR",
                                "definition not found- " + evalName(locals, m), name);
                    }
                }
                to = instantiate(to, locals, m);
                // m.transitions.addElement(new Transition(from,event,to.intValue()));
                m.transitions.addElement(new ProbabilisticTransition(from, event, to.intValue(),
                        prob, bundle));
            } else {
                to = m.stateLabel.label();
                m.transitions.addElement(new ProbabilisticTransition(from, event, to.intValue(),
                        prob, bundle));
                addTransition(to.intValue(), locals, m);
            }
        }
    }

    public void endTransition(int from, Symbol event, Hashtable locals, StateMachine m) {
        if (boolexpr != null) {
            if (Expression.evaluate(boolexpr, locals, m.constants).intValue() != 0)
                thenpart.endTransition(from, event, locals, m);
            else
                elsepart.endTransition(from, event, locals, m);
        } else {
            Integer to;
            if (name != null) {
                to = (Integer) m.explicit_states.get(evalName(locals, m));
                if (to == null) {
                    if (evalName(locals, m).equals("STOP")) {
                        m.explicit_states.put("STOP", to = m.stateLabel.label());
                    } else if (evalName(locals, m).equals("ERROR")) {
                        m.explicit_states.put("ERROR", to = new Integer(Declaration.ERROR));
                    } else if (evalName(locals, m).equals("END")) {
                        m.explicit_states.put("END", to = m.stateLabel.label());
                    } else {
                        m.explicit_states.put(evalName(locals, m), to = new Integer(Declaration.ERROR));
                        Diagnostics.warning(evalName(locals, m) + " defined to be ERROR",
                                "definition not found- " + evalName(locals, m), name);
                    }
                }
                to = instantiate(to, locals, m);
                m.transitions.addElement(new Transition(from, event, to.intValue()));
            } else {
                to = m.stateLabel.label();
                m.transitions.addElement(new Transition(from, event, to.intValue()));
                addTransitions(to.intValue(), locals, m);
            }
        }
    }

    public String evalName(Hashtable locals, StateMachine m) {
        if (expr == null)
            return name.toString();
        else {
            Enumeration e = expr.elements();
            String s = name.toString();
            while (e.hasMoreElements()) {
                Stack x = (Stack) e.nextElement();
                s = s + "." + Expression.getValue(x, locals, m.constants);
            }
            return s;
        }
    }

    public StateExpr myclone() {
        StateExpr se = new StateExpr();
        se.processes = processes;
        se.name = name;
        se.expr = expr;    //expressions are cloned when used
        if (choices != null) {
            se.choices = new Vector<ChoiceElement>();
            Enumeration<ChoiceElement> e = choices.elements();
            while (e.hasMoreElements())
                se.choices.addElement(e.nextElement().myclone());
        }
        se.boolexpr = boolexpr;
        if (thenpart != null) se.thenpart = thenpart.myclone();
        if (elsepart != null) se.elsepart = elsepart.myclone();
        return se;
    }

}


/* -----------------------------------------------------------------------*/
class ChoiceElement extends Declaration {
    Stack guard;
    ActionLabels action;
    StateExpr stateExpr;

    private void add(int from, Hashtable locals, StateMachine m, ActionLabels action) {
        action.initContext(locals, m.constants);
        while (action.hasMoreNames()) {
            String s = action.nextName();
            Symbol e = new Symbol(Symbol.IDENTIFIER, s);
            if (!m.alphabet.containsKey(s))
                m.alphabet.put(s, m.eventLabel.label());
            stateExpr.endTransition(from, e, locals, m);
        }
        action.clearContext();
    }

    private void add(int from, Hashtable locals, StateMachine m, String s) {
        Symbol e = new Symbol(Symbol.IDENTIFIER, s);
        if (!m.alphabet.containsKey(s))
            m.alphabet.put(s, m.eventLabel.label());
        stateExpr.endTransition(from, e, locals, m);
    }


    public void addTransition(int from, Hashtable locals, StateMachine m) {
        if (guard == null || Expression.evaluate(guard, locals, m.constants).intValue() != 0) {
            if (action != null) {
                add(from, locals, m, action);
            }
        }
    }

    public ChoiceElement myclone() {
        ChoiceElement ce = new ChoiceElement();
        ce.guard = guard;
        if (action != null)
            ce.action = action.myclone();
        if (stateExpr != null)
            ce.stateExpr = stateExpr.myclone();
        return ce;
    }
}

/* -----------------------------------------------------------------------*/
class ProbabilisticChoiceElement extends ChoiceElement {
    Map<BigDecimal, List<StateExpr>> probabilisticChoices = new HashMap<BigDecimal, List<StateExpr>>();    // Map<BigDecimal, StateExpr>
    int bundle = ProbabilisticTransition.NO_BUNDLE;

    public ProbabilisticChoiceElement(ChoiceElement elem) {
        this.action = elem.action;
        this.guard = elem.guard;
        this.stateExpr = elem.stateExpr;
    }

    public void addProbabilisticChoice(BigDecimal prob, int bundle, StateExpr stateEx) {
        this.bundle = bundle;

        List<StateExpr> probTrans = probabilisticChoices.get(prob);
        if (probTrans == null) {
            probTrans = new ArrayList<StateExpr>();
            probabilisticChoices.put(prob, probTrans);
        }
        probTrans.add(stateEx);
    }

    private void add(int from, Hashtable locals, StateMachine m, ActionLabels action) {
        action.initContext(locals, m.constants);
        while (action.hasMoreNames()) {
            String s = action.nextName();
            Symbol e = new Symbol(Symbol.IDENTIFIER, s);
            if (!m.alphabet.containsKey(s))
                m.alphabet.put(s, m.eventLabel.label());
            endProbabilisticTransition(from, e, locals, m);
        }
        action.clearContext();
    }

    private void endProbabilisticTransition(int from, Symbol e, Hashtable locals, StateMachine m) {
        for (Object o1 : probabilisticChoices.entrySet()) {
            Entry entry = (Entry) o1;
            Collection stExCol = (Collection) entry.getValue();
            for (Object o2 : stExCol) {
                StateExpr stEx = (StateExpr) o2;
                // stEx.endTransition(from, e, locals, m);
                stEx.endProbabilisticTransition(from, e, locals, m,
                        (BigDecimal) entry.getKey(), bundle);
            }
        }
    }

    private void add(int from, Hashtable locals, StateMachine m, String s) {
        Symbol e = new Symbol(Symbol.IDENTIFIER, s);
        if (!m.alphabet.containsKey(s))
            m.alphabet.put(s, m.eventLabel.label());
        // stateExpr.endTransition(from, e, locals, m);
        for (Object obj : probabilisticChoices.entrySet()) {
            Entry entry = (Entry) obj;
            StateExpr stEx = (StateExpr) entry.getValue();
            stEx.endTransition(from, e, locals, m);
        }
    }

    @Override
    public void addTransition(int from, Hashtable locals, StateMachine m) {
        if (guard == null || Expression.evaluate(guard, locals, m.constants).intValue() != 0) {
            if (action != null) {
                add(from, locals, m, action);
            }
        }
    }

    @Override
    public ChoiceElement myclone() {
        ChoiceElement ce = new ChoiceElement();
        ce.guard = guard;
        if (action != null)
            ce.action = action.myclone();
        if (stateExpr != null)
            ce.stateExpr = stateExpr.myclone();
        return ce;
    }
}
