package MTSSynthesis.controller.model;

import java.util.LinkedList;
import java.util.List;

public class Assumptions<State> {

	private List<Assume<State>> list;

	public Assumptions() {
		this.list = new LinkedList<Assume<State>>();
	}

	public Assume<State> getAssume(int assumeId) {
		if (assumeId > 0 && assumeId <= this.getSize()) {
			return this.list.get(assumeId-1);
		} else { 
			throw new IllegalArgumentException("Violation access. Incorrect guarantee index.");
		}
	}

	public int getSize() {
		return this.list.size();
	}
	
	public boolean isEmpty() {
		return this.list.isEmpty();
	}

	public boolean addAssume(Assume<State> assume) {
		boolean retValue = false;
		if (!this.list.contains(assume)) {
			retValue = this.list.add(assume);
		}
		return retValue;
	}

	@Override
	public String toString() {
		return this.list.toString();
	}
}
