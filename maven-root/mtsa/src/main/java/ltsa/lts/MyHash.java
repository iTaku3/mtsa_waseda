package ltsa.lts;


/* MyHash is a speciallized Hashtable for the analyser
*  -- assumes no attempt to input duplicate key
*/

class MyHashEntry {
    byte[] key;
    int value;
    MyHashEntry next;

    MyHashEntry(byte[] l) {
        key =l; value=0; next = null;
    }

    MyHashEntry(byte[] l, int v) {
        key =l; value=v; next = null;
    }

 }

public class MyHash implements StackCheck {

    private MyHashEntry [] table;
    private int count =0;

    public MyHash(int size) {
        table = new MyHashEntry[size];
    }


    public void put(byte[] key) {
        MyHashEntry entry = new MyHashEntry(key);
        int hash = StateCodec.hash(key) % table.length;
        entry.next=table[hash];
        table[hash]=entry;
        ++count;
    }

    public void put(byte[] key, int value) {
    	  int hash = StateCodec.hash(key) % table.length;
    	  MyHashEntry entry = table[hash];
        while (entry!=null) {
            if (StateCodec.equals(entry.key,key)) {entry.value = value; return;}
            entry = entry.next;
        }
        entry = new MyHashEntry(key,value);
        entry.next=table[hash];
        table[hash]=entry;
        ++count;
    }
    
    public void remove(byte[] key) {
    	   int hash = StateCodec.hash(key) % table.length;
       MyHashEntry p = table[hash];
       MyHashEntry q = p;
       while (p!=null) {
       	 if (StateCodec.equals(p.key,key)) {
       	 	  if (q==table[hash])
       	 	  	  table[hash]=p.next;
       	 	  	else
       	 	  	  q=p.next;
       	 	  	return;
       	 }
       	 q=p;
       	 p=p.next;
       }
    }

    public boolean onStack(byte[] key){
    	    return containsKey(key);
    }
    
    public boolean containsKey(byte[] key) {
        int hash = StateCodec.hash(key) % table.length;
        MyHashEntry entry = table[hash];
        while (entry!=null) {
            if (StateCodec.equals(entry.key,key)) return true;
            entry = entry.next;
        }
        return false;
    }

    public int get(byte[] key) {
        int hash = StateCodec.hash(key) % table.length;
        MyHashEntry entry = table[hash];
        while (entry!=null) {
            if (StateCodec.equals(entry.key,key)) return entry.value;
            entry = entry.next;
        }
        return -99999;
    }

    public int size() {return count;}

}