package MTSA2RATSY.model;

public class ControllableType implements IRATSYSpecification {
	protected boolean controllable;
		
	public boolean isControllable(){
		return controllable;
	}
	
	public ControllableType(boolean isControllable){
		controllable	= isControllable;
	}

	@Override
	public String getDescription() {
		return (controllable? "S" : "E");
	}

	@Override
	public String getXMLDescription() {
		// TODO Auto-generated method stub
		return "<kind>" + getDescription() + "</kind>";
	}
}
