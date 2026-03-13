package MTSSynthesis.controller.model;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Guarantees<State> implements Iterable<Guarantee<State>> {
	List<Guarantee<State>> list;
	
	public Guarantees(){
		this.list = new LinkedList<Guarantee<State>>();
	}
	
	public Guarantee<State> getGuarantee(int guarantee) {
		if (guarantee > 0 && guarantee <= this.size()) {
			return this.list.get(guarantee-1);
		} else { 
			throw new IllegalArgumentException("Violation access. Incorrect guarantee index.");
		}
	}
	
	public int size() {
		return this.list.size();
	}
	
	public boolean addGuarantee(Guarantee<State> guarantee) {
		boolean retValue = false;
		if (!this.list.contains(guarantee)) {
			retValue = this.list.add(guarantee);
		}
		return retValue;

	}

	public boolean isEmpty() {
		return this.list.isEmpty();
	}
	
	@Override
	public Iterator<Guarantee<State>> iterator() {
		return this.list.iterator();
	}
	
	@Override
	public String toString() {
		return this.list.toString();
	}
}
