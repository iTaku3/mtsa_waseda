package MTSSynthesis.controller.gr.basics;

import java.util.List;

public class ControllableList<State>{
	List<StateDecisionPoints<State, Integer>> points;
	int length;
	int actual; 
	public ControllableList(List<StateDecisionPoints<State, Integer>> decisionPoints) {
		this.points = decisionPoints;
		length = decisionPoints.size();
		actual = 0;
	}
	public boolean add(){
		boolean overflow = false;
		boolean add = false;
		int i;
		for(i=0; i < actual+1;i++){
			//if I can add
			if(!points.get(i).add()){
				add = true;
				break;
			}
		}
		
		overflow = !add && (actual == length-1);
		
		if(!add && !overflow){
			actual++;
			points.get(actual).add();
		}
		
		return overflow;
	}
	
	@Override
	public String toString() {
		return points.toString();
	}
	
	public List<StateDecisionPoints<State, Integer>> getPoints() {
		return points;
	}
	
}
