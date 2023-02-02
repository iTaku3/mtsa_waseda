package MTSAExperiments.ar.uba.dc.lafhis.experiments.exchange;

import org.apache.commons.lang.NullArgumentException;
import org.json.simple.JSONObject;

public class JSONCompatibleObject implements JSONCompatible {
	public static String VALUE_PARAM	= "value";
	
	protected Object value;
	protected JSONObject jsonObject;
	
	public JSONCompatibleObject(Object value) {
		this.value		= value;
		this.jsonObject	= new JSONObject();
		jsonObject.put(VALUE_PARAM, this.value);
	}
	
	public Object getValue(){
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
		this.jsonObject = jsonObject ;
	}

}
