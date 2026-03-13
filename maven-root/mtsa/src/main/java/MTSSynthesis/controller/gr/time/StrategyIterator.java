package MTSSynthesis.controller.gr.time;

import MTSSynthesis.controller.gr.time.model.Choice;
import MTSSynthesis.controller.gr.time.model.Scheduler;
import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;
import MTSTools.ac.ic.doc.mtstools.model.impl.LTSAdapter;

import java.util.*;

public abstract class StrategyIterator<S,A>{
	protected List<Scheduler<S,A>> schedulers;
	protected Set<S> finalStates;
	protected Set<A> controllableActions;
	protected Set<A> uncontrollableActions;
	protected Map<S , List<Choice<A>>>  choices;
	protected ChoicesBuilder<S,A> choicesBuilder;
	int iter;
	
	public int getIter() {
		return iter;
	}
	
	protected StrategyIterator() {}
	
	public StrategyIterator(MTS<S, A> mts, Set<A> controllableActions, Set<S> finalStates) {
		init(mts, controllableActions, finalStates);
	}

	protected void init(MTS<S, A> mts,Set<A> controllableActions, Set<S> finalStates) {
		iter = 0;
		this.controllableActions = controllableActions;
		this.choicesBuilder = new ControllerChoicesBuilder<S,A>(new LTSAdapter(mts, MTS.TransitionType.REQUIRED), controllableActions, finalStates);
		this.choices = choicesBuilder.getAllChoices();
		this.uncontrollableActions = new HashSet<A>();
		this.finalStates = finalStates;
		for(A action : mts.getActions()){
			if(!this.controllableActions.contains(action))
				this.uncontrollableActions.add(action);
		}
		this.schedulers = this.generate(mts);
	}

	protected  List<Scheduler<S,A>> generate(MTS<S, A> mts){
		return this.generate(mts, mts.getInitialState());
	}
	
	protected List<Scheduler<S,A>> generate(MTS<S, A> mts, S st) {
		List<Scheduler<S, A>> res = new ArrayList<Scheduler<S,A>>();
		//TODO: No there are not empty choices states. 
		if(!this.choices.get(st).isEmpty()){
			BinaryRelation<A, S> transitions = mts.getTransitions(st, TransitionType.REQUIRED);
			for(Choice<A> c: this.getChoices(st)){
				Map<A, List<Scheduler<S,A>>> Ss =  new HashMap<A, List<Scheduler<S,A>>>();
				for (A l: c.getAvailableLabels()){
					Set<S> succs = transitions.getImage(l);
					for (S succ : succs) {
						//TODO: Avoid revisiting states 
						if(!st.equals(succ)){
							List<Scheduler<S,A>> scheds = generate(mts, succ);
							if(!Ss.containsKey(l))
								Ss.put(l, scheds);
							else 
								Ss.put(l, mergeSchedullers(Ss.get(l),scheds));
						}else{
							List<Scheduler<S, A>> sub_res = new ArrayList<Scheduler<S,A>>();
							Scheduler<S, A> s = new Scheduler<S, A>(this);
							s.setChoice(st, new Choice<A>());
							sub_res.add(s);
							Ss.put(l, sub_res);
						}
					}
				}
				List<Scheduler<S,A>> cs = cartesiano(Ss,new HashSet<A>(Ss.keySet()));
				for (Scheduler<S, A> scheduler : cs) {
					scheduler.setChoice(st, c);
					res.add(scheduler);
				}
			}
			return res;
		}else{
			Scheduler<S, A> s = new Scheduler<S, A>(this);
			s.setChoice(st, new Choice<A>());
			res.add(s);
		}
		return res;
	}

	protected List<Choice<A>> getChoices(S st) {
		return choices.get(st);
	}

	private List<Scheduler<S, A>> cartesiano(Map<A, List<Scheduler<S, A>>> ss, Set<A> ks) {
		if(ks.isEmpty())
			return new ArrayList<Scheduler<S,A>>();
		A k =  ks.iterator().next();
		ks.remove(k);
		return mergeSchedullers(ss.get(k),cartesiano(ss, new HashSet<A>(ks)));
	}

	private List<Scheduler<S, A>> mergeSchedullers(List<Scheduler<S, A>> k_list, List<Scheduler<S, A>> c_list) {
		if(c_list.isEmpty())
			return k_list;
		else{
			List<Scheduler<S, A>> res = new ArrayList<Scheduler<S,A>>();
			for (Scheduler<S, A> sl: k_list) {
				for (Scheduler<S, A> sc: c_list) {
					if(compatible(sl,sc))
						res.add(merge(sl,sc));
				}
			}
			return res;
		}
	}

	protected boolean compatible(Scheduler<S, A> sl, Scheduler<S, A> sc) {
		for (S s1: sl.getStates()) {
			if(sc.isDefined(s1) && !sc.getChoice(s1).equals(sl.getChoice(s1))){
				return false;
			}
		}
		return true;
	}

	private Scheduler<S, A> merge(Scheduler<S, A> sl, Scheduler<S, A> sc) {
		Scheduler<S, A> sched = new Scheduler<S,A>(this);
		for (S s : sl.getStates()) {
			sched.setChoice(s, sl.getChoice(s));
		}
		for (S s : sc.getStates()) {
			sched.setChoice(s, sc.getChoice(s));
		}
		return sched;
	}

	protected abstract Map<S , List<Choice<A>>> getChoices();

	public boolean hasNext(){
		 return  iter < this.schedulers.size(); 
	}
	
	public Scheduler<S, A> next(){
		return this.schedulers.get(iter++);
	}
	
	public Scheduler<S, A> peek(){
		return this.schedulers.get(iter);
	}

	public void reset() {
		this.iter = 0;
	}
	
	public Integer getSize(){
		if(schedulers!=null){
			return schedulers.size();
		}else{
			return 0;
		}
	}

	public Set<A> getUncontrollableActions() {
		return uncontrollableActions;
	}

	public Set<A> getControllableActions() {
		return controllableActions;
	}
}
