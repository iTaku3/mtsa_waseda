package MTSA2RATSY.model.signal;

import MTSA2RATSY.model.IRATSYSpecification;
import MTSA2RATSY.model.ControllableType;

public class Signal implements IRATSYSpecification {

	protected ControllableType	type;
	protected SignalDataType			dataType;
	
	protected String					id;
	
	public String getID(){
		return id;
	}
	
	public ControllableType getType(){
		return type;
	}
	
	public SignalDataType getDataType(){
		return dataType;
	}
	
	public Signal(String id, boolean isControllable){
		//boolean as default
		this(id, isControllable, 0, 1);
	}
	
	public Signal(String id, boolean isControllable, int lowerBound, int upperBound){
		this.id			= id;
		this.type		= new ControllableType(isControllable);
		this.dataType	= new SignalDataType(lowerBound, upperBound);
	}
	
	@Override
	public String getDescription() {
		return id + "," + type.getDescription() + "," + dataType.getDescription();
	}

	@Override
	public String getXMLDescription() {
		// TODO Auto-generated method stub
		return "<signal><name>" + id + "</name>" + this.type.getXMLDescription() + this.dataType.getXMLDescription()
			+ "<auto_signal value=\"False\"/><notes></notes></signal>";
	}

}
