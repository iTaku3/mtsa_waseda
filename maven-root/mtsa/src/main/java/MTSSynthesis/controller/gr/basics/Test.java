package MTSSynthesis.controller.gr.basics;

import java.util.ArrayList;
import java.util.List;

public class Test {
	
	public static void main(String[] args) {
		
		List<DecisionPoints> des = new ArrayList<DecisionPoints>(3);
		des.add(new DecisionPoints(2, true));
		des.add(new DecisionPoints(1, false));

		
		ControllableList a = new ControllableList(des);
		int i =0;
		boolean overflow = false;
//		System.out.println("Corrida " + i + ": " + a.toString());
		i++;
		while(!overflow){
			overflow = a.add();
			if(!overflow){
//				System.out.println("Corrida " + i + ": " + a.toString());
				i++;
			}
		}
		
	}
	

}
