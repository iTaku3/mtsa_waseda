package MTSTools.ac.ic.doc.mtstools.model.operations.DCS.partialOrderReduction;

import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.impl.LTSAdapter;
import MTSTools.ac.ic.doc.mtstools.model.impl.LTSImpl;
import MTSTools.ac.ic.doc.mtstools.model.impl.MarkedWithIllegalLTSImpl;
import MTSTools.ac.ic.doc.mtstools.model.operations.DCS.blocking.Statistics;
import MTSTools.ac.ic.doc.mtstools.model.operations.DCS.monolithicDirector.DirectedControllerSynthesisMonolithicDirector;
import ltsa.ac.ic.doc.mtstools.util.fsp.AutomataToMTSConverter;
import ltsa.lts.CompositeState;
import ltsa.lts.LTSOutput;

import java.util.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DirectedControllerSynthesisPartialOrderReduction<State, Action> {

    List<MarkedWithIllegalLTSImpl<Long, Action>> ltss;
    Set<Action> alphabet;
    Set<Action> controllables;
    //result
    MarkedWithIllegalLTSImpl<Long,Action> director;
    //For each j: min i such that E_i and E_j are isomorphic
    List<Integer> isomorphismGroup = new ArrayList<>();
    //For each j: if j != isomorphismGroup[j] = i: f_a[j] contains a biyective function that shows that E_i and E_j are isomorphic
    List<Map<Action,Action>> f_a = new ArrayList<>();
    List<Map<Action,Action>> f_a_inverse = new ArrayList<>();

    Map<List<Long>,Long> compactId;

    private Statistics statistics = new Statistics();

    private class IsomorphismComputator {

        public void computeIsomorphism(){
            for(int j = 0; j < ltss.size(); j++){
                isomorphismGroup.add(j);
                f_a.add(new HashMap<>());
                f_a_inverse.add(new HashMap<>());
                for(int i = 0; i < j; i++){
                    if(isomorphismGroup.get(i).equals(i) && areIsomorphic(i,j)) {
                        isomorphismGroup.set(j,i);
                        for(Action l1 : alphabet){
                            if(f_a.get(j).containsKey(l1)) {
                                Action l2 = f_a.get(j).get(l1);
                                f_a_inverse.get(j).put(l2,l1);
                            }
                        }
                        break;
                    }
                }
            }
        }

        //Decides if components E_i and E_j are isomorphic
        //In case they are it computes the renaming function for the actions and renames the states of E_j to satisfy that equivalent states are represented with the same number in both components
        private boolean areIsomorphic(int i, int j){
            if(!satisfyEasyToCheckNecessaryConditionsForIsomorphism(i,j)) return false;
            Set<Pair<Action,Action>> haveSameEffectInOtherComponents = computePairsOfActionsThathaveSameEffectInOtherComponents(i,j);

            //iterar sobre biyecciones de (A_E_i - A_E_j) en (A_E_j - A_E_i) a ver si alguna sirve
            MarkedWithIllegalLTSImpl<Long, Action> E_i = ltss.get(i), E_j= ltss.get(j);
            Set<Action> left = new TreeSet<>(E_i.getActions()); left.removeAll(E_j.getActions());
            Set<Action> right = new TreeSet<>(E_j.getActions()); right.removeAll(E_i.getActions());
            return aBiyectiveFunctionForActionsExist(haveSameEffectInOtherComponents,E_i,E_j,i,j,left,right);
        }

        private boolean aBiyectiveFunctionForActionsExist(Set<Pair<Action,Action>> haveSameEffectInOtherComponents, MarkedWithIllegalLTSImpl<Long, Action> E_i, MarkedWithIllegalLTSImpl<Long, Action> E_j, int i, int j, Set<Action> left,Set<Action> right){
            if(left.isEmpty()) {
                Map<Long,Long> f_s_inverse = new HashMap<>();
                if(areEquivalent(E_i,E_i.getInitialState(),E_j,E_j.getInitialState(),new HashMap<>(),f_s_inverse, j)){
                    renameStates(E_j,j,f_s_inverse);
                    return true;
                }
                return false;
            }
            Action a1 = left.iterator().next();
            left.remove(a1);
            List<Action> possible = new ArrayList<>();
            for(Action a2 : right){
                if(controllables.contains(a1) != controllables.contains(a2)) continue;
                if(!haveSameEffectInOtherComponents.contains(Pair.create(a1,a2))) continue;
                possible.add(a2);
            }
            for(Action a2 : possible) {
                right.remove(a2);
                f_a.get(j).putIfAbsent(a1,a2);
                if(aBiyectiveFunctionForActionsExist(haveSameEffectInOtherComponents,E_i,E_j,i,j,left,right)) return true;
                f_a.get(j).remove(a1);
                right.add(a2);
            }
            left.add(a1);
            return false;
        }

        private void renameStates(MarkedWithIllegalLTSImpl<Long, Action> E, int j, Map<Long,Long> f_s){
            Long initial = f_s.get(E.getInitialState());
            MarkedWithIllegalLTSImpl<Long, Action> Ep = new MarkedWithIllegalLTSImpl<Long, Action>(initial);
            Ep.addStates(E.getStates());
            for(Long s : E.getMarkedStates()) Ep.mark(s);
            for(Long s : E.getIllegalStates()) Ep.makeIllegal(s);
            Ep.addActions(E.getActions());
            for(Long s1 : E.getStates()){
                for(Pair<Action,Long> trans : E.getTransitions(s1)){
                    Action l = trans.getFirst();
                    Long s2 = trans.getSecond();
                    Ep.addTransition(f_s.get(s1),l,f_s.get(s2));
                }
            }
            ltss.set(j,Ep);
        }

        private boolean areEquivalent(MarkedWithIllegalLTSImpl<Long, Action> E_i, Long ei, MarkedWithIllegalLTSImpl<Long, Action> E_j, Long ej, Map<Long,Long> f_s, Map<Long,Long> f_s_inverse, int j){
            if(f_s.containsKey(ei) && f_s.get(ei).equals(ej)) return true;
            if(f_s.containsKey(ei) || f_s_inverse.containsKey(ej)) return false;
            if(E_i.isIllegal(ei) != E_j.isIllegal(ej)) return false;
            if(E_i.isMarked(ei) != E_j.isMarked(ej)) return false;
            f_s.putIfAbsent(ei,ej);
            f_s_inverse.putIfAbsent(ej,ei);
            BinaryRelation<Action,Long> trans_ei = E_i.getTransitions(ei);
            BinaryRelation<Action,Long> trans_ej = E_j.getTransitions(ej);
            for(Pair<Action,Long> step : trans_ei){
                Action l = step.getFirst(); Long ep = step.getSecond();
                Action fl = f_a.get(j).get(l); if(fl == null) fl = l;
                if(trans_ej.getImage(fl).isEmpty()) return false;
                if(!areEquivalent(E_i,ep,E_j,trans_ej.getImage(fl).iterator().next(),f_s,f_s_inverse,j)) return false;
            }
            return true;
        }

        private Set<Pair<Action,Action>> computePairsOfActionsThathaveSameEffectInOtherComponents(int i, int j){
            Set<Pair<Action,Action>> sameEffect = new HashSet<Pair<Action,Action>>();
            for(Action a1 : alphabet) for(Action a2 : alphabet) sameEffect.add(Pair.create(a1,a2));
            for(int k = 0; k < ltss.size(); k++){
                if(k == i || k == j) continue;
                removePairWithDifferentEffect(sameEffect,ltss.get(k));
            }
            return sameEffect;
        }

        private void removePairWithDifferentEffect(Set<Pair<Action,Action>> sameEffect, MarkedWithIllegalLTSImpl<Long, Action> lts){
            for(Action a1 : alphabet) for(Action a2 : alphabet) {
                if(!sameEffect.contains(Pair.create(a1,a2))) continue;
                for(Long s : lts.getStates()){
                    BinaryRelation<Action, Long> trans = lts.getTransitions(s);
                    if(!trans.getImage(a1).equals(trans.getImage(a2))){
                        sameEffect.remove(Pair.create(a1,a2));
                        sameEffect.remove(Pair.create(a2,a1));
                        break;
                    }
                }
            }
        }

        private boolean satisfyEasyToCheckNecessaryConditionsForIsomorphism(int i, int j){
            MarkedWithIllegalLTSImpl<Long, Action> E_i = ltss.get(i), E_j= ltss.get(j);

            if(E_i.getStates().size() != E_j.getStates().size()) return false;
            if(E_i.getIllegalStates().size() != E_j.getIllegalStates().size()) return false;
            if(E_i.getMarkedStates().size() != E_j.getMarkedStates().size()) return false;
            if(E_i.getTransitionsNumber() != E_j.getTransitionsNumber()) return false;
            //tmb chequear que los grados de los estados sean iguales?
            if(E_i.getActions().size() != E_j.getActions().size()) return false;
            Set<Action> leftControllable = new HashSet<>(E_i.getActions());
            leftControllable.removeAll(E_j.getActions());
            leftControllable.retainAll(controllables);
            Set<Action> rightControllable = new HashSet<>(E_j.getActions());
            rightControllable.removeAll(E_i.getActions());
            rightControllable.retainAll(controllables);
            if(leftControllable.size() != rightControllable.size()) return false;

            return true;
        }

    }

    private class CompositionalState {

        CompositionalState(){}
        CompositionalState(List<Long> _state){
            state = new ArrayList<>(_state);
        }

        public List<Long> state = new ArrayList<>();

        public boolean enabled(Action l){
            for(int i = 0; i < ltss.size(); i++){
                MarkedWithIllegalLTSImpl<Long, Action> lts = ltss.get(i);
                Long ei = state.get(i);
                if(lts.getActions().contains(l) && lts.getTransitions(ei).getImage(l).isEmpty()) return false;
            }
            return true;
        }

        public CompositionalState destination(Action l){
            CompositionalState ep = new CompositionalState(state);
            for(int i = 0; i < ltss.size(); i++){
                MarkedWithIllegalLTSImpl<Long, Action> lts = ltss.get(i);
                Long ei = state.get(i);
                if(!lts.getTransitions(ei).getImage(l).isEmpty()) ep.state.set(i,lts.getTransitions(ei).getImage(l).iterator().next());
            }
            return ep;
        }

        public CompositionalState representator(){
            CompositionalState er = new CompositionalState(state);
            for(int i = 0; i < state.size(); i++){
                for(int j = i+1; j < state.size(); j++){
                    if(isomorphismGroup.get(i).equals(isomorphismGroup.get(j))){
                        if(er.state.get(i) > er.state.get(j)) {
                            er.swap(i,j);
                        }
                    }
                }
            }
            return er;
        }

        public boolean shouldBeMarked(){
            for(int i = 0; i < state.size(); i++){
                Long ei = state.get(i);
                if(!ltss.get(i).isMarked(ei)) return false;
            }
            return true;
        }

        public boolean shouldBeIllegal(){
            for(int i = 0; i < state.size(); i++){
                Long ei = state.get(i);
                if(ltss.get(i).isIllegal(ei)) return true;
            }
            return false;
        }

        public void swap(int i, int j){
            Long temp = state.get(i);
            state.set(i,state.get(j));
            state.set(j,temp);
        }
    }

    public LTS<Long,Action> synthesize(
            List<MarkedWithIllegalLTSImpl<Long, Action>> inputLTSs,
            Set<Action> inputControllable){
        ltss = inputLTSs;
        controllables = inputControllable;
        alphabet = new HashSet<>();
        for(MarkedWithIllegalLTSImpl<Long, Action> lts : ltss) alphabet.addAll(lts.getActions());

        statistics.clear();
        statistics.start();

        new IsomorphismComputator().computeIsomorphism();

        MarkedWithIllegalLTSImpl<Long,Action> plant = partialOrderReductionParallelComposition();

        DirectedControllerSynthesisMonolithicDirector<Long,Action> dcs = new DirectedControllerSynthesisMonolithicDirector<Long,Action>();
        LTS<Long,Action> compactDirector = dcs.synthesize(plant,controllables,new HashSet<Action>());

        statistics.end();

        if(compactDirector != null){
            expand(compactDirector);
            statistics.setControllerUsedStates(director.getStates().size());
            statistics.setControllerUsedTransitions(director.getTransitionsNumber());
            assertGoodDirector();
        }

        return director;
    }

    private void expand(LTS<Long,Action> compactDirector){
        director = new MarkedWithIllegalLTSImpl<>(0L);
        director.addActions(alphabet);
        Set<CompositionalState> processing = new HashSet<>(); processing.add(compositionInitial());
        Map<List<Long>,Long> id = new HashMap<>(); id.put(compositionInitial().state,0L);
        Long states = 1L;
        while(!processing.isEmpty()){
            CompositionalState e = processing.iterator().next();
            processing.remove(e);

            for(Action l : alphabet){
                if(e.enabled(l)) {
                    Action fl = rename(e,l);
                    if(!compactDirector.getTransitions(compactId.get(e.representator().state)).getImage(fl).isEmpty()){
                        CompositionalState ep = e.destination(l);
                        if(!id.containsKey(ep.state)){
                            director.addState(states);
                            if(ep.shouldBeMarked()) director.mark(states);
                            assertFalse("Adding illegal state to de director",ep.shouldBeIllegal());
                            id.put(ep.state,states);
                            states++;
                            processing.add(ep);
                        }
                        director.addTransition(id.get(e.state),l,id.get(ep.state));
                    }
                }
            }
        }
    }

    private Action rename(CompositionalState _e, Action l){
        CompositionalState e = new CompositionalState(_e.state);
        for(int i = 0; i < e.state.size(); i++){
            int minpos = i;
            for(int j = i+1; j < e.state.size(); j++){
                if(isomorphismGroup.get(i).equals(isomorphismGroup.get(j)) && e.state.get(j) < e.state.get(minpos))
                    minpos = j;
            }
            l = swappingBiyectiveFunction(i,minpos,l);
            e.swap(i,minpos);
        }
        return l;
    }

    private Action swappingBiyectiveFunction(int i, int j, Action l){
        if(i == j) return l;
        if(i != isomorphismGroup.get(i)){
            if(f_a.get(i).containsKey(l)) l = f_a.get(i).get(l);
            else if(f_a_inverse.get(i).containsKey(l)) l = f_a_inverse.get(i).get(l);
        }
        if(j != isomorphismGroup.get(j)){
            if(f_a.get(j).containsKey(l)) l = f_a.get(j).get(l);
            else if(f_a_inverse.get(j).containsKey(l)) l = f_a_inverse.get(j).get(l);
            if(i != isomorphismGroup.get(i)){
                if(f_a.get(i).containsKey(l)) l = f_a.get(i).get(l);
                else if(f_a_inverse.get(i).containsKey(l)) l = f_a_inverse.get(i).get(l);
            }
        }

        return l;
    }

    private MarkedWithIllegalLTSImpl<Long,Action> partialOrderReductionParallelComposition(){
        MarkedWithIllegalLTSImpl<Long,Action> plant = new MarkedWithIllegalLTSImpl<>(0L);
        plant.addActions(alphabet);
        Set<CompositionalState> processing = new HashSet<>(); processing.add(compositionInitial());
        compactId = new HashMap<>(); compactId.put(compositionInitial().state,0L);
        Long states = 1L;
        statistics.incExpandedStates();
        while(!processing.isEmpty()){
            CompositionalState e = processing.iterator().next();
            processing.remove(e);

            for(Action l : alphabet){
                if(e.enabled(l)) {
                    CompositionalState ep = e.destination(l).representator();
                    if(!compactId.containsKey(ep.state)){
                        plant.addState(states);
                        if(ep.shouldBeMarked()) plant.mark(states);
                        if(ep.shouldBeIllegal()) plant.makeIllegal(states);
                        compactId.put(ep.state,states);
                        states++;
                        processing.add(ep);
                        statistics.incExpandedStates();
                    }
                    plant.addTransition(compactId.get(e.state),l,compactId.get(ep.state));
                    statistics.incExpandedTransitions();
                }
            }
        }

        return plant;
    }

    private CompositionalState compositionInitial(){
        CompositionalState e0 = new CompositionalState();
        for(int i = 0 ; i < ltss.size(); i++) e0.state.add(ltss.get(i).getInitialState());
        return e0;
    }

    private void assertGoodDirector(){
        System.err.println("Director check is enabled and could be slowing down the solution");
        assertDirector();
        assertSolution();
    }

    private void assertDirector(){
        assertTrue("Computed director is not controllable",isControllable());
        assertTrue("we returned a Supervisor, not a Director (>1 controllable action)",isDirected());
        assertEager();
    }

    private boolean isControllable(){
        Set<Pair<Long,List<Long>>> visited = new HashSet<>();
        Set<Pair<Long,List<Long>>> queue = new HashSet<>();
        queue.add(Pair.create(director.getInitialState(),compositionInitial().state));
        while(!queue.isEmpty()){
            Pair<Long,List<Long>> p = queue.iterator().next();
            queue.remove(p);
            visited.add(p);

            Long e1 = p.getFirst();
            CompositionalState e2 = new CompositionalState(p.getSecond());
            for(Action l : alphabet){
                if(!controllables.contains(l) && e2.enabled(l) && director.getTransitions(e1).getImage(l).isEmpty())
                    return false;
                if(!director.getTransitions(e1).getImage(l).isEmpty()){
                    assertTrue("Director has a transition that does not exist in the parallel composition",e2.enabled(l));
                    Long e1p = director.getTransitions(e1).getImage(l).iterator().next();
                    CompositionalState e2p = e2.destination(l);
                    Pair<Long,List<Long>> pp = Pair.create(e1p,e2p.state);
                    if(!queue.contains(pp) && !visited.contains(pp)) queue.add(pp);
                }
            }
        }
        return true;
    }

    private boolean isDirected(){
        for(Long s : director.getStates()) {
            int c = 0;
            for (Pair<Action, Long> trans : director.getTransitions(s)) {
                if (controllables.contains(trans.getFirst())) ++c;
            }
            if(c > 1) return false;
        }
        return true;
    }

    private void assertEager(){

    }

    private void assertSolution(){
        assertTrue("Computed director is not safe",isSafe());
        assertTrue("Computed director does not satisfy nonblocking property", isNonBlocking());
    }

    private boolean isSafe(){
        Set<Pair<Long,List<Long>>> visited = new HashSet<>();
        Set<Pair<Long,List<Long>>> queue = new HashSet<>();
        queue.add(Pair.create(director.getInitialState(),compositionInitial().state));
        while(!queue.isEmpty()){
            Pair<Long,List<Long>> p = queue.iterator().next();
            queue.remove(p);
            visited.add(p);

            Long e1 = p.getFirst();
            CompositionalState e2 = new CompositionalState(p.getSecond());
            if(e2.shouldBeIllegal()) return false;

            for(Pair<Action,Long> trans : director.getTransitions(e1)){
                Action l = trans.getFirst();
                Long e1p = trans.getSecond();
                CompositionalState e2p = e2.destination(l);
                Pair<Long,List<Long>> pp = Pair.create(e1p,e2p.state);
                if(!queue.contains(pp) && !visited.contains(pp)) queue.add(pp);
            }
        }
        return true;
    }

    private boolean isNonBlocking(){
        MarkedWithIllegalLTSImpl<Long,Action> reversedDirector = computeReversedLTS(director);
        Set<Long> processing = new HashSet<>(director.getMarkedStates());
        Set<Long> canReachMarked = new HashSet<>();
        while(!processing.isEmpty()){
            Long e = processing.iterator().next();
            processing.remove(e);
            canReachMarked.add(e);

            for(Pair<Action,Long> trans : reversedDirector.getTransitions(e)){
                Long parent = trans.getSecond();
                if(!processing.contains(parent) && !canReachMarked.contains(parent))
                    processing.add(parent);
            }
        }
        return canReachMarked.equals(director.getStates());
    }

    private MarkedWithIllegalLTSImpl<Long,Action> computeReversedLTS(MarkedWithIllegalLTSImpl<Long,Action> lts){
        MarkedWithIllegalLTSImpl<Long,Action> reverse = new MarkedWithIllegalLTSImpl<>(lts.getInitialState());
        reverse.addStates(lts.getStates());
        reverse.addActions(lts.getActions());
        for (Long s : lts.getStates()){
            for(Pair<Action, Long> child : lts.getTransitions(s)){
                reverse.addTransition(child.getSecond(), child.getFirst(), s);
            }
        }
        return reverse;
    }

    public Statistics getStatistics() {
        return statistics;
    }
}