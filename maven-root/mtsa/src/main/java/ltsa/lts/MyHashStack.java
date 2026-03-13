package ltsa.lts;

import java.util.HashMap;
import java.util.Map;

/* MyHash is a speciallized Hashtable/Stack for the composition in the analyser
* it includes a stack structure through the hash table entries
*  -- assumes no attempt to input duplicate key
*  
*/

class MyHashStackEntry extends StateMapEntry {
    MyHashStackEntry next;   //for linking buckets in hash table
    MyHashStackEntry link;   //for queue linked list
  
    MyHashStackEntry(byte[] l) {
        key =l; stateNumber=-1; next = null; link = null; marked=false;
    }

    MyHashStackEntry(byte[] l, int n) {
        key =l; stateNumber =n; next = null; link =null; marked=false;
    }
 }

public class MyHashStack implements StackCheck, StateMap {

    private MyHashStackEntry [] table;
    private int count =0;
    private int depth =0;
    private MyHashStackEntry head = null;
    
    private Map stateMappings;

    public MyHashStack(int size) {
        table = new MyHashStackEntry[size];
        stateMappings = new HashMap<>();
    }

    // @Override
    public void add(byte[] key) {
    	pushPut(key);
    }
    
    // @Override
    public void add(byte[] key, int depth) {
    	MyHashStackEntry entry= pushPut(key);
    	entry.depth= depth;
    }
    
    // @Override
    public void add(byte[] key, int action, byte[] parent) {
    	throw new UnsupportedOperationException();
    }
    
    public MyHashStackEntry pushPut(byte[] key) {
        MyHashStackEntry entry = new MyHashStackEntry(key);
        //insert in hash table
        int hash = StateCodec.hash(key) % table.length;
        entry.next=table[hash];
        table[hash]=entry;
        ++count;
        //insert in stack
        entry.link = head;
        head = entry;
        ++depth;
        
        return entry;
    }
    
    // @Override
    public void removeNextState() {
    	pop();
    }
    
    public void pop() { //remove from head of queue
    	if (head==null)
    		return;
    	head.marked = false;
    	head = head.link;
    	--depth;
    }
    
    // @Override
    public byte[] getNextState() {
    	return peek();
    }
    
    public byte[] peek() { //remove from head of queue
       return head.key;
    }

    // @Override
    public int getNextStateNumber() {
    	return head.stateNumber;
    }
    
    // @Override
    public void markNextState(int stateNumber) {
    	mark(stateNumber);
    }
    
    public void mark(int id) {
    	   head.marked = true;
    	   head.stateNumber = id;
    	   
    	   stateMappings.put(id, head.key);
    }
    
    // @Override
    public boolean nextStateIsMarked() {
    	return marked();
    }
    
    public boolean marked() {
    	   return head.marked;
    }

    // @Override
    public boolean empty() {
    	return head==null;
    }
    
    // @Override
    public boolean contains(byte[] key) {
    	return containsKey(key);
    }
    
    public boolean containsKey(byte[] key) {
       int hash = StateCodec.hash(key) % table.length;
        MyHashStackEntry entry = table[hash];
        while (entry!=null) {
            if (StateCodec.equals(entry.key,key)) return true;
            entry = entry.next;
        }
        return false;
    }
    
    public boolean onStack(byte[] key) {
       int hash = StateCodec.hash(key) % table.length;
        MyHashStackEntry entry = table[hash];
        while (entry!=null) {
            if (StateCodec.equals(entry.key,key)) return entry.marked;
            entry = entry.next;
        }
        return false;
    }
    
    // @Override
    public int get(byte[] key) {
       int hash = StateCodec.hash(key) % table.length;
        MyHashStackEntry entry = table[hash];
        while (entry!=null) {
            if (StateCodec.equals(entry.key,key)) return entry.stateNumber;
            entry = entry.next;
        }
        return LTSConstants.NO_SEQUENCE_FOUND;
    }


    public int size() {return count;}
   
    
    public int getDepth() {
    	  return depth;
    }
    
    public Map getStateMapping() {
    	return this.stateMappings;
    }

}