package MTSAExperiments.ar.uba.dc.lafhis.experiments.jung;

import java.util.List;

import org.json.simple.JSONAware;

public class ExperimentJUNGGameNodeValue<ID> implements JSONAware{
	protected ID id;
	protected List<ExperimentJUNGGameFluent> fluentsValuations;
	protected boolean isWinning;
	protected boolean isInitial;
	
	public ExperimentJUNGGameNodeValue(ID id, List<ExperimentJUNGGameFluent> fluentsValuations, boolean isWinning
			, boolean isInitial){
		this.id					= id;
		this.fluentsValuations	= fluentsValuations;
		this.isWinning			= isWinning;
		this.isInitial			= isInitial;
	}
		
	public ID getID(){
		return id;
	}
	
	public List<ExperimentJUNGGameFluent> getFluentsValuations(){
		return fluentsValuations;
	}
	
	public boolean getIsWinning(){
		return isWinning;
	}
	
	public boolean getIsInitial(){
		return isInitial;
	}
	
	@Override
	public String toString() {
		String valuationsString = "";
		for(ExperimentJUNGGameFluent fluent: fluentsValuations){
			if(fluent.getValue()){
				if(valuationsString != null)
					valuationsString += "<br>";
				valuationsString += fluent.toString();
			}
		}
		return "<html>" + id.toString() 
				+ "<br>" + valuationsString + "</html>";
	}

	@Override
	public String toJSONString() {
		String valuationsString = null;
		for(ExperimentJUNGGameFluent fluent: fluentsValuations){
			
			if(valuationsString != null)
				valuationsString += ",";
			else
				valuationsString = "[";
			valuationsString += fluent.toJSONString();
		}
		if(valuationsString != null)
			valuationsString += "]";
		else
			valuationsString = "[]";
		return "{\"id\":\"" + id.toString() + "\"," 
				+ "\"fluentsValuations\":" + valuationsString 
				+ ",\"isWinning\":"+isWinning+",\"isInitial\":"+ isInitial+"}";
	}	
	
}
