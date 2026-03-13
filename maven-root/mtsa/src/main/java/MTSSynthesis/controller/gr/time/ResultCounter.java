package MTSSynthesis.controller.gr.time;

import java.util.HashMap;
import java.util.Map;

import MTSSynthesis.controller.gr.time.model.Result;

public class ResultCounter {
	Map<Result,Integer> stats; 
	
	public ResultCounter() {
		stats = new HashMap<Result,Integer>();
	}
	
	public synchronized void addStat(Result res) {
		if(stats.keySet().contains(res)){
			stats.put(res, stats.get(res)+1);
		}else{
			stats.put(res, 1);
		}
	}
	
	public boolean contains(Result res) {
		return stats.keySet().contains(res);
	}
	
	@Override
	public String toString() {
		return stats.toString();
	}
	
}
