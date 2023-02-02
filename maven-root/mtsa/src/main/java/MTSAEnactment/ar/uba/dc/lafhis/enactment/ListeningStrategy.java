package MTSAEnactment.ar.uba.dc.lafhis.enactment;

import java.util.Set;

import MTSTools.ac.ic.doc.mtstools.model.LTS;

public abstract class ListeningStrategy<State,Action> {
	private String name;
	
	public String getName(){
		return name;		
	}
	
	public ListeningStrategy(String name){
		this.name	= name;		
	}
	
	abstract Set<String> getDispatchersForCurrentState(LTS<State, Action> mts);
	
}
