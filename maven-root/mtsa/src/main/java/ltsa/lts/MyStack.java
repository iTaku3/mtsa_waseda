package ltsa.lts;

/*
* Simple low overhead Stack data type used by analyser
*/

class StackEntries {
	final static int N = 1024;
	byte[] val[] = new byte[N][];
	boolean[] marks = new boolean[N];
	int index;
	StackEntries next;
	
	StackEntries(StackEntries se) {
		 index=0;
		 next = se;
	}
	
	boolean empty() {return index==0;}
	
	boolean full()  {return index==N;}
	
  void push(byte[] o) {
  	 val[index] = o;
  	 marks[index]=false;
  	 ++index;
  }
  
  byte[] pop() {
  	--index;
  	return val[index];
  }
  
  byte[] peek() { return val[index-1]; }
  void mark() { marks[index-1] = true;}
  boolean marked() {return marks[index-1];}
		 
}

class MyStack {
	
	protected StackEntries head = null;
	protected int depth = 0;
	
	boolean empty() { return head==null;}
	
	void push(byte[] o) {
		 if (head ==null) {
		 	head = new StackEntries(null);
		 } else if (head.full()) {
		 	head = new StackEntries(head);
		 }
		 head.push(o);
		 ++depth;
	}
	
	byte[] pop() {
		byte[] t = head.pop();
		--depth;
		if (head.empty())head = head.next;
		return t;
	}
	
	byte[] peek() {return head.peek();}
	
  void mark() {head.mark(); }
	
	boolean marked(){return head.marked();}

	int getDepth() {return depth;}
	
}