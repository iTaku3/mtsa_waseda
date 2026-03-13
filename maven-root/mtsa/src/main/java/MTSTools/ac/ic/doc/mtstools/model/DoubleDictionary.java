package MTSTools.ac.ic.doc.mtstools.model;

import java.util.HashMap;
import java.util.Map;

public class DoubleDictionary<S,D,V> {
	
	Map<S, HashMap<D, V>> values;
	public DoubleDictionary() {
		values = new HashMap<S,HashMap<D,V>>();
	}
	
	public boolean contains(S s, D d){
		return values.containsKey(s) 
				&& values.get(s).containsKey(d);
	}
	
	public void put(S s, D d, V v){
		HashMap<D,V> dict;
		if(values.containsKey(s)){
			dict = values.get(s);
		}else{
			dict = new HashMap<D,V>();
			values.put(s, dict);
		}
		dict.put(d, v);
	}
	
	public V get(S s, D d){
		if(!contains(s, d)){
			return null;
		}
		return values.get(s).get(d);
	}

}
