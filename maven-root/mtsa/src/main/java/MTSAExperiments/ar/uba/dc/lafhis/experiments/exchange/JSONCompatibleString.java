package MTSAExperiments.ar.uba.dc.lafhis.experiments.exchange;

import org.apache.commons.lang.NullArgumentException;
import org.json.simple.JSONObject;

public class JSONCompatibleString implements JSONCompatible {
	public static String VALUE_PARAM	= "value";
	
	protected String value;
	protected JSONObject jsonObject;
	
	public JSONCompatibleString(String value) {
		this.value		= value;
		this.jsonObject	= new JSONObject();
		jsonObject.put(VALUE_PARAM, this.value);
	}
	
	public String getValue(){
		return value;
	}
	
	@Override
	public String toJSONString() {
		return jsonObject.toJSONString();
	}

	@Override
	public JSONObject toJSONObject() {
		return jsonObject;
	}

	@Override
	public void initializeFromJSONObject(JSONObject jsonObject) throws NullArgumentException{
		if(!jsonObject.containsKey(VALUE_PARAM)){
			throw new NullArgumentException("JSONCompatibleString::initializeFromJSONObject input object not properly structured");
		}
		this.jsonObject.put(VALUE_PARAM, jsonObject.get(VALUE_PARAM));
	}

}
