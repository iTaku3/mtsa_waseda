package MTSA2RATSY.model.signal;

import MTSA2RATSY.model.IRATSYSpecification;

public class SignalDataType implements IRATSYSpecification{
	protected int lowerBound;
	protected int upperBound;
	
	protected boolean booleanData;
	
	public int getLowerBound(){
		return lowerBound;
	}
	
	public int getUpperBound(){
		return upperBound;
	}
	
	public boolean isBooleanData(){
		return lowerBound == 0 && upperBound == 1;
	}
	
	public SignalDataType(){
		//boolean as default
		this.lowerBound	= 0;
		this.upperBound	= 1;
	}
	
	public SignalDataType(int lowerBound, int upperBound){
		this.lowerBound	= lowerBound;
		this.upperBound	= upperBound;
	}

	@Override
	public String getDescription() {
		return (isBooleanData() ? "boolean" : String.valueOf(lowerBound) + ".." 
				+ String.valueOf(upperBound));
	}

	@Override
	public String getXMLDescription() {
		// TODO Auto-generated method stub
		return "<type>" + getDescription() + "</type>";
	}
}
