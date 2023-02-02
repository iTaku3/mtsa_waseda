package MTSTools.ac.ic.doc.mtstools.model.operations;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.DoubleDictionary;
import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSTools.ac.ic.doc.mtstools.model.impl.LTSImpl;

public class ParallelComposer<S,A,D> {
	LTS <S,A> environment;
	LTS <D,A> controller;
	public ParallelComposer(LTS<S,A> env, LTS<D,A> controller) {
		this.environment = env;
		this.controller = controller;
	}
	
	
	/** 
	 * This implementation of parallel composition assumes that 
	 * controllers is an LTS that has the same alphabet as environment and 
	 * the environment can always simulate the controller. 
	 **/
	public LTS<Pair<S,D>,A> compose() {
		if(!environment.getActions().equals(controller.getActions())){
			for (A a : controller.getActions()) {
				if(!environment.getActions().contains(a) && !"-1".equals(a)){
					throw new RuntimeException("This implementation of parallel composition assumes that you are composing a controller with the associated environment: " + environment.getActions() +" != "+ controller.getActions());
				}
			}
		}
		
		Pair<S,D> initialState = new Pair<S,D>(environment.getInitialState(), controller.getInitialState());
		LTS<Pair<S,D>,A> result =  new LTSImpl<Pair<S,D>, A>(initialState);
		result.addActions(environment.getActions());
		DoubleDictionary<S, D, Pair<S,D>> states = new DoubleDictionary<S, D, Pair<S,D>>();
		Set<Pair<S,D>> visited = new HashSet<Pair<S,D>>();
		Queue<Pair<S,D>> pending = new LinkedList<Pair<S,D>>();
		
		states.put(environment.getInitialState(), controller.getInitialState(), initialState);
		pending.add(initialState);
		
		while(!pending.isEmpty()){
			Pair<S,D> state = pending.poll();
			visited.add(state);
			BinaryRelation<A,S> eTransitions  = environment.getTransitions(state.getFirst()); 
			BinaryRelation<A,D> cTransitions  = controller.getTransitions(state.getSecond());
			for (Pair<A, D> cTrans : cTransitions) {
				A a = cTrans.getFirst();
				D d = cTrans.getSecond();
				Set<S> envSuccesors = eTransitions.getImage(a);
//				if(envSuccesors == null || envSuccesors.isEmpty()){
//					throw new RuntimeException("The environment couldn't imitate the taking the transition " + a.toString() + " at the state " + d.toString());
//				}
				
				if(envSuccesors == null || envSuccesors.isEmpty()){
//					System.out.println("The environment couldn't imitate the taking the transition " + a.toString() + " at the state " + d.toString());
				}else{
					for (S s : envSuccesors) {
						Pair<S,D> succ;
						if(!states.contains(s,d)){
							succ = new Pair<S,D>(s,d);
							states.put(s, d, succ);
							result.addState(succ);
						}else{
							succ = states.get(s, d);
						}
						result.addTransition(state, a, succ);
						if(!visited.contains(succ)){
							pending.add(succ);
						}
					}
				}
				
			}
		}
		return result;
	}

}
