package MTSA2RATSY.adapter;

public class SymbolicAdapterConfiguration {
	protected boolean asynchronous;
	protected boolean precomposeAutomata;
	
	protected String ratsyFileLocation;
	protected String mappingFileLocation;
	protected String mtsaFileLocation;
	
	public boolean isAsynchronous(){
		return asynchronous;
	}
	
	public boolean isPrecomposingAutomata(){
		return precomposeAutomata;
	}
	
	public String getRatsyFileLocation(){
		return ratsyFileLocation;
	}
	
	public String getMappingFileLocation(){
		return mappingFileLocation;
	}
	
	public String getMtsaFileLocation(){
		return mtsaFileLocation;
	}
	
	public SymbolicAdapterConfiguration(boolean asynchronous, boolean precomposeAutomata, String ratsyFileLocation
			, String mappingFileLocation, String mtsaFileLocation){
		this.asynchronous			= asynchronous;
		this.precomposeAutomata		= precomposeAutomata;
		this.ratsyFileLocation		= ratsyFileLocation;
		this.mappingFileLocation	= mappingFileLocation;
	}
}
