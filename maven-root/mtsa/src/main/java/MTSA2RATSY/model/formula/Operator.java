package MTSA2RATSY.model.formula;

import MTSA2RATSY.model.IRATSYSpecification;

public class Operator implements IRATSYSpecification{
	protected String id;
	
	public String getID(){
		return id;
	}
	
	public Operator(String id){
		this.id	= id;
	}

	@Override
	public String getDescription() {
		return id;
	}

	@Override
	public String getXMLDescription() {
		return "<id>"+ id +"</id>";
	}
}
