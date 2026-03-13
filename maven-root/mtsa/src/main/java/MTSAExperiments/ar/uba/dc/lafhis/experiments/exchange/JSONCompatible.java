package MTSAExperiments.ar.uba.dc.lafhis.experiments.exchange;

import org.apache.commons.lang.NullArgumentException;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public interface JSONCompatible extends JSONAware{
	public JSONObject toJSONObject();
	public void initializeFromJSONObject(JSONObject jsonObject) throws NullArgumentException; 
}
