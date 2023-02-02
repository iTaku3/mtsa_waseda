package MTSSynthesis.controller.gr.basics;

/**
 * boolean representation of int 
 * inicial value 0 or 1 
 **/ 
public class DecisionPoints{
	boolean zero;
	boolean[] decisions;
	int length;
	public DecisionPoints(int length, boolean zero){
		this.zero = zero; 
		this.length =length;
		reset();
	}
	
	int a = 0;
	boolean addSingle(){
//		System.out.println(this.toString());
		for(; a < length; a++){
			if(!decisions[a]){
				decisions[(a+length-1)%length] = false;
				decisions[a] = true;
				break;
			}else{
				decisions[a] = false;
			}
		}
		boolean carry = (a==length);

		if(carry)
			reset();
		
		return carry;
	}
	
	boolean add(){
		return addSingle();
	}
	
	boolean addPower(){
//		System.out.println(this.toString());
		int i;
		for(i=0; i < length; i++){
			if(!decisions[i]){
				decisions[i] = true;
				break;
			}else{
				decisions[i] = false;
			}
		}
		boolean carry = (i==length);

		if(carry)
			reset();
		
		return carry;
	}
	
	private void reset() {	
		a=0;
		decisions = new boolean[length];
		if(!zero){
			decisions[0] = true;
		}
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i< length;i++){
			sb.append(" ");
			if(decisions[i])
				sb.append(1);
			else
				sb.append(0);
		}
		
		return sb.toString();
	}
	
}
