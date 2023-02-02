package MTSAExperiments.ar.uba.dc.lafhis.experiments.jung;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class ExperimentJUNGGameEdgeValue<ID, S> implements JSONAware{
	protected ID id;
	protected S from;
	protected S to;
	
	protected boolean isControllable;
	
	public ExperimentJUNGGameEdgeValue(ID id, S from, S to, boolean isControllable){
		this.id				= id;
		this.isControllable	= isControllable;
		this.from			= from;
		this.to				= to;
	}
	
	public ID getID(){
		return id;
	}
	
	public S getOrigin(){
		return this.from;
	}
	
	public S getDestination(){
		return this.to;
	}
	
	public boolean getIsControllable(){
		return isControllable;
	}
	
	@Override
	public String toString() {
		return id.toString() +(isControllable? "!":"?");
	}

	@Override
	public String toJSONString() {
		return "{\"id\":\""+id.toString()+"\",\"from\":\""+ from +"\",\"to\":\""+ to +"\",\"isControllable\":"+ isControllable +"}";
	}
}
