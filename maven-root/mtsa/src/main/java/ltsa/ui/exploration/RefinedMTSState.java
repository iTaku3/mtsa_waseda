package ltsa.ui.exploration;

import java.util.ArrayList;

public class RefinedMTSState {

	Long firstValue; 
	ArrayList<Long> otherValues;
	
	public RefinedMTSState(){
		
		firstValue= new Long(0);
		otherValues = new ArrayList<Long>();
		otherValues.add(new Long(0));
	}
	
	public RefinedMTSState(Long value1, ArrayList<Long> value2 ){
		
		firstValue = value1;
		otherValues = value2;
		
	}
	
	public boolean equals(RefinedMTSState state){
		
		boolean result = (firstValue == state.getFirstValue());
		for (int i = 0; i < otherValues.size(); i++) {
			result = (result && (otherValues.get(i) == state.getOtherValues().get(i)));
		}
		return result;
	}

	public ArrayList<Long> getOtherValues() {
		return otherValues;
	}

	public Long getFirstValue() {
		return firstValue;
	}
	
	public void setFirstValue(Long fV){
		firstValue = fV;
	}
	
	public String toString(){
		String firstPart = "(V_"+firstValue.toString()+", ";
		
		for (Long value : otherValues) {
			firstPart = firstPart+"M_"+value.toString()+", ";
		}		
		return firstPart.substring(0, firstPart.length()-2) + ")";
	}
}
