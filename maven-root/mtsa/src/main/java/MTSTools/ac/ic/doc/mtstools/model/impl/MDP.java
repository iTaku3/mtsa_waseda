package MTSTools.ac.ic.doc.mtstools.model.impl;

import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;
import MTSTools.ac.ic.doc.commons.relations.MapSetBinaryRelation;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.LTS;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.Map.Entry;

/*
Representation of a Markov decision process.

Daniel Sykes 2014
*/

public class MDP extends AbstractTransitionSystem<Long, String> implements LTS<Long, String> {
    private Map<Long, HashSet<ProbabilisticTransition>> transitions = new HashMap<Long, HashSet<ProbabilisticTransition>>();
    private Map<String, Vector<Long>> stateLabels = new HashMap<String, Vector<Long>>();

    public MDP(Long initialState) {
        super(initialState);
    }

    public boolean addTransition(Long from, String label, Long to, double probability, int bundle) {
        if (!getStates().contains(from))
            addState(from);
        if (!getStates().contains(to))
            addState(to);
        if (!getActions().contains(label))
            addAction(label);

        HashSet<ProbabilisticTransition> ts = transitions.get(from);
        if (ts == null) {
            ts = new HashSet<ProbabilisticTransition>();
            transitions.put(from, ts);
        }
        return ts.add(new ProbabilisticTransition(to, label, probability, bundle));
    }

    @Override
    public boolean addTransition(Long from, String label, Long to) {
        return addTransition(from, label, to, 1.0f, 0);
    }

    @Override
    public Map<Long, BinaryRelation<String, Long>> getTransitions() {
        Map<Long, BinaryRelation<String, Long>> ts = new HashMap<Long, BinaryRelation<String, Long>>();
        for (Long s : getStates())
            ts.put(s, getTransitions(s));
        return ts;
    }

    @Override
    public BinaryRelation<String, Long> getTransitions(Long state) {
        BinaryRelation<String, Long> res = new MapSetBinaryRelation<String, Long>();
        if (transitions.get(state) != null)
            for (ProbabilisticTransition t : transitions.get(state))
                res.addPair(t.getAction(), t.getTo());
        return res;
    }

    @Override
    protected BinaryRelation<String, Long> getTransitionsFrom(Long state) {
        return getTransitions(state);
    }

    public Set<ProbabilisticTransition> getTransitionsFrom(long state) {
        Set<ProbabilisticTransition> s = transitions.get(state);
        if (s == null)
            s = Collections.emptySet();
        return s;
    }

    @Override
    protected void removeTransitions(Collection<Long> unreachableStates) {
        for (long state : unreachableStates)
            transitions.remove(state);
    }

    @Override
    public boolean removeTransition(Long from, String label, Long to) {
        Set<ProbabilisticTransition> removes = new HashSet<ProbabilisticTransition>();
        for (ProbabilisticTransition t : transitions.get(from))
            if (t.getAction().equals(label) && t.getTo().equals(to))
                removes.add(t);
        return transitions.get(from).removeAll(removes);
    }

    @Override
    public void removeAction(String action) {
        throw new UnsupportedOperationException(action);
    }

    public List<ProbabilisticTransition> getBundle(long state, ProbabilisticTransition t) {
        List<ProbabilisticTransition> bundle = new Vector<ProbabilisticTransition>();
        for (ProbabilisticTransition t2 : getTransitionsFrom(state))
            if (t2.getAction().equals(t.getAction()) && t2.getBundle() == t.getBundle())
                bundle.add(t2);
        return bundle;
    }

    public void addStateLabel(Long state, String label) {
        System.out.println("Adding label " + state + " " + label);
        if (!stateLabels.containsKey(label))
            stateLabels.put(label, new Vector<Long>());
        if (!stateLabels.get(label).contains(state))
            stateLabels.get(label).add(state);
    }

    public List<String> getLabels(Long state) {
        System.out.println("Looking for labels on " + state);
        List<String> labels = new Vector<String>();
        for (String label : stateLabels.keySet())
            if (stateLabels.get(label).contains(state))
                labels.add(label);
        return labels;
    }

    public String toString() {
        String res = "";
        for (long s : getStates())
            for (ProbabilisticTransition t : getTransitionsFrom(s))
                res += s + "--[" + t.getAction() + ":" + t.probability + " [" + t.bundle + "]]-->" + t.getTo() + "\n";
        return res;
    }

    public static MDP compose(MDP mdp1, MDP mdp2) {
        composition = new MDP(compositionStateNumber(mdp1, mdp1.getInitialState(), mdp2.getInitialState()));
        visited = new Vector<Long>();
        composeStates(mdp1, mdp2, mdp1.getInitialState(), mdp2.getInitialState());
        return composition;
    }

    private static MDP composition; // only here to reduce the number of parameters in recursion
    private static Vector<Long> visited;

    private static long compositionStateNumber(MDP mdp1, long state1, long state2) {
        if (state2 == exceptionState)
            return exceptionState + state1;
        return (state2 * mdp1.getStates().size()) + state1; // y*width + x
    }

    private static void composeStates(MDP mdp1, MDP mdp2, long state1, long state2) // deal with taus***
    {
        long stateC = compositionStateNumber(mdp1, state1, state2);
        if (visited.contains(stateC))
            return;
        visited.add(stateC);
        boolean isDeadlock = false;
        HashSet<ProbabilisticTransition> syncActions = new HashSet<ProbabilisticTransition>();

        for (ProbabilisticTransition t : mdp1.getTransitionsFrom(state1)) {
            if (mdp2.getActions().contains(t.getAction())) // need to sync
                syncActions.add(t); // save them for later
            else {
                composition.addTransition(stateC, t.getAction(), compositionStateNumber(mdp1, t.getTo(), state2),
                        t.probability, t.bundle);
                System.out.println(stateC + "--[" + t.getAction() + ":" + t.probability + "]-->"
                        + compositionStateNumber(mdp1, t.getTo(), state2));
                composeStates(mdp1, mdp2, t.getTo(), state2); // depth first
            }
        }

        for (ProbabilisticTransition t : mdp2.getTransitionsFrom(state2)) {
            if (mdp1.getActions().contains(t.getAction())) // need to sync
            {
                boolean synchronised = false;
                for (ProbabilisticTransition st : syncActions)
                    if (st.getAction().equals(t.getAction())) {
                        synchronised = true;
                        composition.addTransition(stateC, st.getAction(),
                                compositionStateNumber(mdp1, st.getTo(), t.getTo()), t.probability * st.probability,
                                st.bundle);
                        System.out.println(stateC + "--[" + st.getAction() + ":" + (t.probability * st.probability)
                                + "]-->" + compositionStateNumber(mdp1, st.getTo(), t.getTo()));
                        composeStates(mdp1, mdp2, st.getTo(), t.getTo()); // depth first
                    }
                if (!synchronised)
                    isDeadlock = true;
            } else {
                composition.addTransition(stateC, t.getAction(), compositionStateNumber(mdp1, state1, t.getTo()),
                        t.probability, t.bundle);
                System.out.println(stateC + "--[" + t.getAction() + ":" + t.probability + "]-->"
                        + compositionStateNumber(mdp1, state1, t.getTo()));
                composeStates(mdp1, mdp2, state1, t.getTo()); // depth first
            }
        }

        if (isDeadlock)
            System.out.println("Deadlock detected in MDP composition");
    }

    private static final long exceptionState = Long.MAX_VALUE;
    private static BinaryRelation<Long, Long> simulation;

    public static MDP composeAbstraction(MDP r, MDP a, BinaryRelation<Long, Long> simul) {
        visited = new Vector<Long>();
        simulation = simul;
        composition = new MDP(compositionStateNumber(r, r.getInitialState(), a.getInitialState()));
        // composition.addTransition(exceptionState, "tau", exceptionState); //removed
        // 25/4/2014
        System.out.println("Sim rel: " + simul);
        if (simul.size() == 0)
            System.out.println("WARNING: simulation empty");
        composeAbstractionStates(r, a, r.getInitialState(), a.getInitialState());
        return composition;
    }

    private static void composeAbstractionStates(MDP r, MDP a, long state1, long state2) {
        /*
         * if (!simulation.contains(new Pair<Long,Long>(state1, state2)) && state2 !=
         * exceptionState) //latter part added 25/4/2014 {
         * System.out.println("("+state1+","+state2+") NOT IN SIMREL"); return; }
         */
        // System.out.println(state2 == exceptionState);
        long stateC = compositionStateNumber(r, state1, state2);
        if (visited.contains(stateC))
            return;
        visited.add(stateC);
        composition.addState(stateC);

        List<ProbabilisticTransition> processed = new Vector<ProbabilisticTransition>();
        for (ProbabilisticTransition t : r.getTransitionsFrom(state1)) {
            System.out.println("state (" + state1 + "," + state2 + ") got a " + t.getAction()
                    + " transition, looking for bundle " + t.getBundle());
            if (!processed.contains(t)) {
                // find the bundle
                List<ProbabilisticTransition> bundle = new Vector<ProbabilisticTransition>();
                for (ProbabilisticTransition t2 : r.getTransitionsFrom(state1))
                    if (t2.getBundle() == t.getBundle() && t2.getAction().equals(t.getAction()) && !bundle.contains(t2))
                        bundle.add(t2);
                processed.addAll(bundle);

                System.out.println("bundle size is " + bundle.size());

                // find all the matching A states
                Map<ProbabilisticTransition, Vector<Long>> aStates = new HashMap<ProbabilisticTransition, Vector<Long>>();
                for (ProbabilisticTransition t2 : bundle) {
                    aStates.put(t2, new Vector<Long>());
                    for (Pair<String, Long> t3 : a.getTransitions(state2))
                        if (t2.getAction().equals(t3.getFirst())
                                && simulation.contains(new Pair<Long, Long>(t2.getTo(), t3.getSecond()))
                                && !aStates.get(t2).contains(t3.getSecond()))
                            aStates.get(t2).add(t3.getSecond());
                        else if (t2.getAction().equals("tau")
                                && simulation.contains(new Pair<Long, Long>(t2.getTo(), state2))
                                && !aStates.get(t2).contains(state2))
                            aStates.get(t2).add(state2); // a doesn't move on r's tau
                    if (aStates.get(t2).size() == 0) // if there are no ways to do this part of bundle, make it go to
                        // exception
                        aStates.get(t2).add(exceptionState);
                }

                composeAbstractionStates_replicateBundle(r, a, stateC, state2, bundle, aStates);
            }
        }

        /*
         * for (Pair<String,Long> t : a.getTransitions(state2)) //removed 25/4/2014 {
         * Map<Integer,Double> bundleExceptionProbs = new HashMap<Integer,Double>();
         * System.out.println("a action "+t.getFirst()); if
         * (r.getActions().contains(t.getFirst())) {
         * System.out.println("r shares the action"); for (ProbabilisticTransition t2 :
         * r.getTransitionsFrom(state1)) //check if it's enabled { if
         * (bundleExceptionProbs.get(t2.bundle) == null)
         * bundleExceptionProbs.put(t2.bundle, 1.0D);
         *
         * if (t.getFirst().equals(t2.getAction())) //need to sync (taus sync too) {
         * System.out.println("found a sync transition"); if (simulation.contains(new
         * Pair<Long,Long>(t2.getTo(), t.getSecond())) && t2.probability > 0) //only
         * counts as sync if the destination is in simulation {
         * composition.addTransition(stateC, t2.getAction(), compositionStateNumber(r,
         * t2.getTo(), t.getSecond()), t2.probability, t2.bundle);
         * bundleExceptionProbs.put(t2.bundle,
         * bundleExceptionProbs.get(t2.bundle)-t2.probability);
         * System.out.println("("+state1+","+state2+") -- ["+t2.getAction()+" "+t2.
         * getProbability()+"] --> ("+t2.getTo()+","+t.getSecond()+") synced bundle ");
         * composeAbstractionStates(r, a, t2.getTo(), t.getSecond()); //depth first }
         * else {
         * System.out.println(t2.getAction()+" "+t2.getProbability()+"->("+t2.getTo()+
         * ","+t.getSecond()+") NOT IN SIMREL (or prob=0)");
         * //composition.addTransition(stateC, t.getAction(), exceptionState, 1.0D,
         * t.getBundle()); } } } } else {
         * System.out.println("r shares the action ? "+r.getActions().contains(t.
         * getFirst()));
         * //System.out.println("a has transitions from "+state2+"? "+a.getTransitions(
         * state2)); }
         *
         * for (int bundle : bundleExceptionProbs.keySet()) if
         * (bundleExceptionProbs.get(bundle) > 0) {
         * System.out.println("Bundle "+bundle+" "+t.getFirst()+" will go to exception "
         * +bundleExceptionProbs.get(bundle)); composition.addTransition(stateC,
         * t.getFirst(), exceptionState, bundleExceptionProbs.get(bundle), bundle); } }
         */

        /*
         * for (ProbabilisticTransition t : r.getTransitionsFrom(state1)) //catch final
         * ones where A can't move //old rule 2 removed 25/6/2014 { boolean synced =
         * false; for (Pair<String,Long> t2 : a.getTransitions(state2)) if
         * (t2.getFirst().equals(t.getAction()) && simulation.contains(new
         * Pair<Long,Long>(t.getTo(), t2.getSecond()))) //sim condition added 7/5/2014 {
         * synced = true; //new 25/4/2014 composition.addTransition(stateC,
         * t.getAction(), compositionStateNumber(r, t.getTo(), t2.getSecond()),
         * t.probability, t.bundle);
         * System.out.println("("+state1+","+state2+") -- ["+t.getAction()+" "+t.
         * getProbability()+"] --> ("+t.getTo()+","+t2.getSecond()+") synced bundle ");
         * composeAbstractionStates(r, a, t.getTo(), t2.getSecond()); //depth first } if
         * (!synced) { System.out.println("looking at un-sunc "+t.getAction()); if
         * (t.getAction().equals("tau") && simulation.contains(new
         * Pair<Long,Long>(t.getTo(), state2))) //R can move ahead on a tau, only if the
         * destination is in simulation { if (true) //only if the destination is in
         * simulation { composition.addTransition(stateC, t.getAction(),
         * compositionStateNumber(r, t.getTo(), state2), t.getProbability(),
         * t.getBundle()); composeAbstractionStates(r, a, t.getTo(), state2); //depth
         * first } else System.out.println("SIM REL RESTRICTION"); } else {
         * //composition.addTransition(stateC, t.getAction(), exceptionState, 1.0D,
         * t.getBundle()); //removed 25/4/2014 System.out.println("boop"); long next =
         * compositionStateNumber(r, t.getTo(), exceptionState);
         * composition.addTransition(stateC, t.getAction(), next, t.getProbability(),
         * t.getBundle()); composition.addStateLabel(next, "exception");
         * composeAbstractionStates(r, a, t.getTo(), exceptionState); //depth first } }
         * }
         */

        /*
         * Map<String,Double> bundleExceptionProbs = new HashMap<String,Double>();
         * Map<String,String> bundleEvents = new HashMap<String,String>(); for
         * (ProbabilisticTransition t : r.getTransitionsFrom(state1)) { String bundleKey
         * = t.getAction()+t.bundle; if (bundleEvents.get(bundleKey) == null) {
         * bundleExceptionProbs.put(bundleKey, 1.0D); bundleEvents.put(bundleKey,
         * t.getAction()); } System.out.println("r action "+t.getAction()+"/"+t.bundle);
         * if (a.getActions().contains(t.getAction()) && a.getTransitions(state2) !=
         * null) { System.out.println("a shares the action and has some transitions");
         * for (Pair<String,Long> t2 : a.getTransitions(state2)) //check if it's enabled
         * { if (t.getAction().equals(t2.getFirst())) //need to sync (taus sync too) {
         * System.out.println("found a sync transition");
         *
         * if (simulation.contains(new Pair<Long,Long>(t.getTo(), t2.getSecond())))
         * //only counts as sync if the destination is in simulation { if (t.probability
         * > 0) { composition.addTransition(stateC, t.getAction(),
         * compositionStateNumber(r, t.getTo(), t2.getSecond()), t.probability,
         * t.bundle); bundleExceptionProbs.put(bundleKey,
         * bundleExceptionProbs.get(bundleKey)-t.probability);
         * System.out.println("("+state1+","+state2+") -- ["+t.getAction()+" "+t.
         * getProbability()+"] --> ("+t.getTo()+","+t2.getSecond()+") synced bundle "
         * +bundleKey+"="+bundleExceptionProbs.get(bundleKey));
         * //System.out.println("recursing for "+t.getTo()); composeAbstractionStates(r,
         * a, t.getTo(), t2.getSecond()); //depth first } } else {
         * System.out.println(t.getAction()+" "+t.getProbability()+"->("+t.getTo()+","+
         * t2.getSecond()+") NOT IN SIMREL"); //composition.addTransition(stateC,
         * t.getAction(), exceptionState, 1.0D, t.getBundle()); } } } } else { //r is
         * allowed to move ahead without sync (meaning of L^ on a transition) ***
         * System.out.println("a shares the action ? "+a.getActions().contains(t.
         * getAction()));
         * System.out.println("a has transitions from "+state2+"? "+a.getTransitions(
         * state2)); } }
         *
         * for (String bundle : bundleExceptionProbs.keySet()) if
         * (bundleExceptionProbs.get(bundle) > 0) {
         * System.out.println("Bundle "+bundle+" "+bundleEvents.get(bundle)
         * +" will go to exception "+bundleExceptionProbs.get(bundle));
         * composition.addTransition(stateC, bundleEvents.get(bundle), exceptionState,
         * bundleExceptionProbs.get(bundle),
         * Integer.parseInt(bundle.replace(bundleEvents.get(bundle), ""))); }
         */
    }

    private static int lastBundleID = 0;

    private static int nextBundleID() {
        return lastBundleID++;
    }

    private static void composeAbstractionStates_replicateBundle(MDP r, MDP a, long stateC, long state2,
                                                                 List<ProbabilisticTransition> bundle, Map<ProbabilisticTransition, Vector<Long>> aStates) {
        composeAbstractionStates_replicateBundle(r, a, stateC, state2, bundle, aStates, new long[bundle.size()], 0);
    }

    private static void composeAbstractionStates_replicateBundle(MDP r, MDP a, long stateC, long state2,
                                                                 List<ProbabilisticTransition> bundle, Map<ProbabilisticTransition, Vector<Long>> aStates, long assignment[],
                                                                 int index) {
        // there will be less than [aStates.size()]^[bundle.size()] bundles (now that
        // each slot has different set of filling options)

        if (index < assignment.length /* && aStates.size() > 0 */) // not filled the bundle
        {
            for (long s : aStates.get(bundle.get(index))) // iterate over aStates to fill a slot of the bundle, recurse
            // to next slot of bundle
            {
                assignment[index] = s;
                composeAbstractionStates_replicateBundle(r, a, stateC, state2, bundle, aStates, assignment, index + 1);
            }
        } else // filled, can add the bundle and recurse back into main method
        {
            System.out.println("state " + stateC + " ASSIGNMENT OF A-STATES IN THIS BUNDLE " + bundle.get(0).getBundle()
                    + " IS " + Arrays.toString(assignment));
            int bundleID = nextBundleID(); // have to keep local var due to recursions
            for (int i = 0; i < bundle.size(); i++) {
                ProbabilisticTransition t = bundle.get(i);
                long next = assignment[i]; // may be exceptionstate
                long nextC = compositionStateNumber(r, t.getTo(), next);

                if (next != exceptionState)
                    for (String label : a.getLabels(state2)) // copy labels over only if destination is not an exception
                        composition.addStateLabel(stateC, label);

                System.out.println(" adding " + stateC + " -" + t.getAction() + "-> (" + t.getTo() + "," + next + ")");
                composition.addTransition(stateC, t.getAction(), nextC, t.getProbability(), bundleID);
                if (next == exceptionState)
                    composition.addStateLabel(nextC, "exception");
                composeAbstractionStates(r, a, t.getTo(), next); // depth first
            }
        }
    }

    // disregards the simulation; much like the tier completion, except the
    // completion
    // uses an implicit general R which has everything enabled always
    public static MDP composeEnactment(MDP r, MDP a, List<String> controlledActions) {
        visited = new Vector<Long>();
        composition = new MDP(compositionStateNumber(r, r.getInitialState(), a.getInitialState()));
        composeEnactmentStates(r, a, r.getInitialState(), a.getInitialState(), controlledActions);
        return composition;
    }

    private static void composeEnactmentStates(MDP r, MDP a, long state1, long state2, List<String> controlledActions) {
        long stateC = compositionStateNumber(r, state1, state2);
        if (visited.contains(stateC))
            return;
        visited.add(stateC);
        composition.addState(stateC);

        for (ProbabilisticTransition t : r.getTransitionsFrom(state1)) {
            if (state2 == exceptionState) // we're already in exception, allow R to move -- RULE 3
            {
                long next = compositionStateNumber(r, t.getTo(), exceptionState);
                composition.addTransition(stateC, t.getAction(), next, t.probability, t.bundle);
                composition.addStateLabel(next, "exception");
                composeEnactmentStates(r, a, t.getTo(), exceptionState, controlledActions); // depth first
            } else {
                boolean synced = false;
                for (Pair<String, Long> t2 : a.getTransitions(state2))
                    if (t2.getFirst().equals(t.getAction())) // RULE 1
                    {
                        // System.out.println("State "+stateC+" is C-state "+state2);
                        for (String label : a.getLabels(state2)) // copy labels over only if destination is not an
                            // exception
                            composition.addStateLabel(stateC, label);

                        synced = true;
                        // System.out.println(stateC+"("+state1+","+state2+")=="+t.getAction()+","+t.probability+","+t.bundle+"==>"+compositionStateNumber(r,
                        // t.getTo(), t2.getSecond())+"("+t.getTo()+","+t2.getSecond()+")");
                        composition.addTransition(stateC, t.getAction(),
                                compositionStateNumber(r, t.getTo(), t2.getSecond()), t.probability, t.bundle);
                        composeEnactmentStates(r, a, t.getTo(), t2.getSecond(), controlledActions); // depth first
                    } else if (t.getAction().equals("tau")) // RULE 2
                    {
                        synced = true;
                        composition.addTransition(stateC, t.getAction(), compositionStateNumber(r, t.getTo(), state2),
                                t.probability, t.bundle);
                        composeEnactmentStates(r, a, t.getTo(), state2, controlledActions); // depth first
                    }
                if (!synced) {
                    if (!controlledActions.contains(t.getAction())) // A cannot handle R's uncontrolled action -- RULE 4
                    {
                        long next = compositionStateNumber(r, t.getTo(), exceptionState);
                        composition.addTransition(stateC, t.getAction(), next, t.getProbability(), t.getBundle());
                        composition.addStateLabel(next, "exception");
                        composeEnactmentStates(r, a, t.getTo(), exceptionState, controlledActions); // depth first;
                        // recurse to allow
                        // R to continue
                        // after exception
                    } else // A cannot handle R's controlled action -- RULE 5
                    {
                        // check there are no other controlled R actions that A can sync with; if
                        // intersection empty then do a nop
                        boolean syncPossible = false;
                        for (ProbabilisticTransition t3 : r.getTransitionsFrom(state1))
                            if (controlledActions.contains(t3.getAction()))
                                for (Pair<String, Long> t2 : a.getTransitions(state2))
                                    if (t2.getFirst().equals(t3.getAction()))
                                        syncPossible = true; // don't need to handle it here, will be dealt with in
                        // outer loop
                        if (!syncPossible) {
                            System.out.println("inserting nop state " + stateC);
                            composition.addTransition(stateC, t.getAction(), stateC, t.getProbability(), t.getBundle()); // nop
                            composition.addStateLabel(stateC, "cexception");
                        }
                    }
                }
            }
        }
    }

    public void writePrismFile(String filename, String modName) {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(filename));

            String modelName = modName.substring(0, Math.min(2, modName.length()));

            final String stateVar = "state_" + modelName; // try to make them unique when combined with
            // other LTSs

            writer.println("mdp //generated by MTSA");
            writer.println("module " + modName);
            // writer.println(stateVar + ": [0.." + (2 * exceptionState) + "] init 0;");
            int size = this.getStates().size() + 1;
            writer.println(stateVar + ": [-1.." + size + "] init -1;");
            writer.println("[start] " + stateVar + "=-1 -> 1.0: (" + stateVar + "'=" + this.getInitialState() + ");");

            List<Long> miracleStates = new Vector<Long>(); // temp hack
            List<Long> goalStates = new Vector<Long>(); // temp hack

            List<ProbabilisticTransition> visited = new Vector<ProbabilisticTransition>();
            for (long state : this.getStates()) {
                for (ProbabilisticTransition t : getTransitionsFrom(state)) {
                    if (!visited.contains(t)) {
                        List<ProbabilisticTransition> bundle = getBundle(state, t);
                        visited.addAll(bundle);
                        String line = "[" + prismifyLabel(t.getAction()) + "] " + stateVar + "=" + state + " -> ";
                        for (ProbabilisticTransition t2 : bundle) {
                            line += t2.getProbability() + ":(" + stateVar + "'=" + t2.getTo() + ") + ";
                        }
                        line = line.substring(0, line.length() - 3); // remove last +
                        writer.println(line + ";");
                    }
                }
            }

            writer.println("endmodule");

            // it is not clear why this was needed.
            // if (exceptionState != -1)
            // writer.println("\nlabel \"exception\" = " + stateVar + ">=" + exceptionState
            // + ";");

            for (String label : stateLabels.keySet()) {
                String line = "\nlabel \"" + prismifyLabel(label) + "\" = ";
                for (Long state : stateLabels.get(label))
                    line += stateVar + "=" + state + " | ";
                line = line.substring(0, line.length() - 3);
                writer.println(line + ";");
            }

            writer.println("rewards \"total_time\" ");
            writer.println(stateVar + ">-1 : 1;");
            writer.println("endrewards");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public MDP renumber() {
        Map<Long, Long> oldToNew = this.generateRenumberedStates();
        MDP result = new MDP(oldToNew.get(this.getInitialState()));
        this.generateRenumberedTransitions(result, oldToNew);
        this.generateRenumberedStateLabels(result, oldToNew);
        return result;
    }

    private void generateRenumberedStateLabels(MDP result, Map<Long, Long> oldToNew) {
        for (Entry<String, Vector<Long>> entry : this.stateLabels.entrySet()) {
            Vector<Long> vector = new Vector<Long>();
            for (Long oldStates : entry.getValue()) {
                vector.add(oldToNew.get(oldStates));
            }
            result.stateLabels.put(entry.getKey(), vector);
        }
    }

    private void generateRenumberedTransitions(MDP result, Map<Long, Long> oldToNew) {
        for (long oldState : this.getStates()) {
            long newFrom = oldToNew.get(oldState);

            for (ProbabilisticTransition oldTransition : getTransitionsFrom(oldState)) {
                long newTo = oldToNew.get(oldTransition.getTo());

                result.addTransition(newFrom, oldTransition.getAction(), newTo, oldTransition.getProbability(),
                        oldTransition.getBundle());
            }
        }
    }

    private Map<Long, Long> generateRenumberedStates() {
        Map<Long, Long> oldToNew = new HashMap<>();
        long nextState = 0;
        for (long oldState : this.getStates()) {
            if (!oldToNew.containsKey(oldState)) {
                oldToNew.put(oldState, nextState++);
            }
        }
        return oldToNew;
    }

    private static String prismifyLabel(String label) {
        return label.replaceAll("\\.", "_").replaceAll(" \\| ", "_OR_").replaceAll(" \\& ", "_AND_").replaceAll("!",
                "NOT_");
    }
}
