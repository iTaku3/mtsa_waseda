package MTSAExperiments.ar.uba.dc.lafhis.experiments.jung;

import org.json.simple.JSONAware;

public class ExperimentJUNGGameFluent implements JSONAware {
	protected boolean value;
	protected String name;
	
	public ExperimentJUNGGameFluent(String name, boolean value){
		this.name	= name;
		this.value	= value;
	}
	
	public String getName(){
		return name;
	}
	
	public boolean getValue(){
		return value;
	}
	
	@Override
	public String toString() {
		return name + ":" + (value? "1":"0");
	}

	@Override
	public String toJSONString() {
		return "{\"name\":\""+name+"\",\"value\":"+ value +"}";
	}
}
