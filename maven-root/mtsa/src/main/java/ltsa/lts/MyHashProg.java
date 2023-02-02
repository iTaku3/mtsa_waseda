package ltsa.lts;


/* MyHashProg is a speciallized Hashtable progress analysis
* it includes a queue structure through the hash table entries
*  -- assumes no attempt to input duplicate key
*  
*/

class MyHashProgEntry {
    byte[] key;
    int dfn;                 //depth first search number
    int low;                 //low link number
    boolean isReturn;        //boolean
    boolean isProcessed;       //boolean
    MyHashProgEntry next;   //for linking buckets in hash table
    MyHashProgEntry parent; //pointer to node above in BFS

    MyHashProgEntry(byte[] l) {
        key =l; dfn=0; low=0; isReturn=false; isProcessed =false; next = null; parent=null;
    }

    MyHashProgEntry(byte[] l, MyHashProgEntry p) {
        key =l; dfn =0; low=0; isReturn=false; isProcessed =false; next = null; parent=p;
    }

 }

public class MyHashProg implements StackCheck {

    private MyHashProgEntry [] table;
    private int count =0;
    
    public MyHashProg(){
    	  table = new MyHashProgEntry[100001];
    }

    public MyHashProg(int size) {
        table = new MyHashProgEntry[size];
    }

    public void add(byte[] key, MyHashProgEntry parent) {
        MyHashProgEntry entry = new MyHashProgEntry(key, parent);
        //insert in hash table
        int hash = StateCodec.hash(key) % table.length;
        entry.next=table[hash];
        table[hash]=entry;
        ++count;
    }
    
    public MyHashProgEntry get(byte[] key) {
        int hash = StateCodec.hash(key) % table.length;
        MyHashProgEntry entry = table[hash];
        while (entry!=null) {
            if (StateCodec.equals(entry.key,key)) return entry;
            entry = entry.next;
        }
        return null;
    }
    
    public boolean onStack(byte[] key) {
    	  MyHashProgEntry entry = get(key);
    	  if (entry==null) return false;
    	  return (entry.isReturn && !entry.isProcessed);
    }

    public int size() {return count;}

}