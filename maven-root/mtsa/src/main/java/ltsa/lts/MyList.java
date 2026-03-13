package ltsa.lts;

import java.math.BigDecimal;

/* MyList is a speciallized List for the analyser
 *  -- its stores transitions*/

class MyListEntry {
	int fromState;
	byte[] toState;
	int actionNo;
	MyListEntry next;

	MyListEntry(int from, byte[] to, int action) {
		fromState= from;
		toState= to;
		actionNo= action;
		this.next= null;
	}
}

class MyProbListEntry extends MyListEntry {
	int bundle;
	BigDecimal prob;

	MyProbListEntry(int from, byte[] to, int action) {
		super(from, to, action);
	}

	MyProbListEntry(int from, byte[] to, int action, int bundle, BigDecimal prob) {
		super(from, to, action);
		this.bundle= bundle;
		this.prob= prob;
	}
}

public class MyList {
	protected MyListEntry head= null;
	protected MyListEntry tail= null;
	protected int count= 0;

	public MyListEntry peek() {
		return head;
	}
	
	public void add(MyListEntry e) {
		if (head == null) {
			head= tail= e;
		} else {
			tail.next= e;
			tail= e;
		}
		++count;
	}

	public void add(int from, byte[] to, int action) {
		MyListEntry e= new MyListEntry(from, to, action);
		add(e);
	}

	public void add(int from, byte[] to, int action, int bundle, BigDecimal prob) {
		MyProbListEntry e= new MyProbListEntry(from, to, action, bundle, prob);
		add(e);
	}

	public void next() {
		if (head != null)
			head= head.next;
	}

	public boolean empty() {
		return head == null;
	}

	public int getFrom() {
		return head != null ? head.fromState : -1;
	}

	public byte[] getTo() {
		return head != null ? head.toState : null;
	}

	public int getAction() {
		return head != null ? head.actionNo : -1;
	}

	public int size() {
		return count;
	}

	public int getBundle() {
		if (head == null) {
			return ProbabilisticTransition.BUNDLE_ERROR;
		} else if (head instanceof MyProbListEntry) {
			MyProbListEntry probHead= (MyProbListEntry) head;
			return probHead.bundle;
		} else {
			return ProbabilisticTransition.NO_BUNDLE;
		}
	}
	
	public BigDecimal getProb() {
		if (head == null) {
			return BigDecimal.ZERO;
		} else if (head instanceof MyProbListEntry) {
			MyProbListEntry probHead= (MyProbListEntry) head;
			return probHead.prob;
		} else {
			return BigDecimal.ONE;
		}
	}
}
