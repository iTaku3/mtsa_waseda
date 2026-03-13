package MTSSynthesis.controller.gr.time;

import MTSSynthesis.controller.gr.time.model.Choice;
import MTSSynthesis.controller.gr.time.model.Scheduler;
import MTSTools.ac.ic.doc.mtstools.model.MTS;

import java.util.*;


public abstract class RandomStrategyIterator<S, A> extends StrategyIterator<S, A> {
	HashMap<S, Integer> depth;
	Random random;
	static private int SEED_UID = 34496723;
	MTS<S, A> mts;
	Set<Map<S,Integer>> choosed;
	Map<S,Integer> currentChoice;
	protected RandomStrategyIterator(){}
	public RandomStrategyIterator(MTS<S, A> mts, Set<A> controllableActions, Set<S> finalState, ArrayList<Set<A>> relatedActions) {
		//TODO: Check why we are not using related actions.
		this.depth = new HashMap<S, Integer>();
		init(mts, controllableActions, finalState);
	}
	@Override
	protected void init(MTS<S, A> mts, Set<A> controllableActions, Set<S> finalStates) {
		this.random = new Random(mts.getStates().size()*SEED_UID);
		this.choosed = new HashSet<Map<S,Integer>>();
		this.mts = mts;
		super.init(mts, controllableActions, finalStates);
	}
	
	@Override
	protected List<Scheduler<S,A>> generate(MTS<S, A> mts){
		this.currentChoice = new HashMap<S, Integer>();
		return generate(mts,mts.getInitialState());
	}
	
	protected List<Scheduler<S,A>> generate(MTS<S, A> mts, S st, int previous_deepth) {
		if(!depth.containsKey(st)){
			depth.put(st, previous_deepth);
		}
		List<Scheduler<S,A>> schedulers = super.generate(mts, st);
		return schedulers;
	}

	@Override
		public boolean hasNext() {
			if(super.hasNext()){
				return true;
			}
			this.iter = 0;
			generateNext();
			return super.hasNext();
		}
	
	public void generateNext(){
		int tries = 2000;
		//this is the previous choice
		choosed.add(currentChoice);
		List<Scheduler<S,A>> schedulers = null;
		do{
			schedulers = generate(this.mts);
			tries--;
		}while(choosed.contains(currentChoice) && tries > 0);
		
		if(tries > 0){
			this.schedulers = schedulers;
		}else{
			this.schedulers = new ArrayList<Scheduler<S,A>>();
		}
		
	}
	
	@Override
	protected boolean compatible(Scheduler<S, A> sl, Scheduler<S, A> sc) {
		return (super.compatible(sl, sc) && randomChoice());
	}

	private boolean randomChoice() {
		return true;
	}
	
	@Override
	protected List<Choice<A>> getChoices(S st) {
		int choice;
		if (currentChoice.containsKey(st)){
			choice = currentChoice.get(st);
		}else{
			if(this.choices.get(st).size()>1){
				choice = random.nextInt(this.choices.get(st).size());
			}else{
				choice = 0;
			}
			currentChoice.put(st, choice);
		}
		return super.getChoices(st).subList(choice, choice+1);
	}
	
	
	
}
