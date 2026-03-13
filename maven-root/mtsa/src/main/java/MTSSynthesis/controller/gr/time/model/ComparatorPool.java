package MTSSynthesis.controller.gr.time.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import MTSSynthesis.controller.gr.time.GamaComparator;

public class ComparatorPool<A,S> {

	Map<Integer,GamaComparator<A,S>> comparators;
	Stack<Integer> available;
	private String stopwatchName1;
	private String stopwatchName2;
	public ComparatorPool(int cant, ActivityDefinitions<A> activityDefinitions, Set<S> set, String END_TO_END_NAME1, String END_TO_END_NAME2) {
		comparators = new HashMap<Integer,GamaComparator<A,S>>();
		this.stopwatchName1 = END_TO_END_NAME1;
		this.stopwatchName2 = END_TO_END_NAME2;
		this.available = new Stack<Integer>();
		while(cant>0){
			available.add(--cant);
			comparators.put(cant, new GamaComparator<A, S>(activityDefinitions, set, END_TO_END_NAME1,END_TO_END_NAME2));
		}
	}
	
	public String getStopwatchName1() {
		return stopwatchName1;
	}
	
	public String getStopwatchName2() {
		return stopwatchName2;
	}
	
	public synchronized void releaseComparator(Integer id){
		this.getComparator(id).disposeContext();
		available.add(id);
	}
	
	public synchronized Integer getComparatorId(){
		if(available.size() == 0)
			return null;
		return available.pop();
	}
	
	public GamaComparator<A,S> getComparator(Integer id){
		return comparators.get(id);
	}
	
	public void dispose(){
		for (Integer integer : comparators.keySet()) {
			comparators.get(integer).disposeAll();
		}
	}
	
}
