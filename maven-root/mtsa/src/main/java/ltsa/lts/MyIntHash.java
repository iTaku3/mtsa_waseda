package ltsa.lts;


/* MyHash is a speciallized Hashtable for the analyser
*  -- assumes no attempt to input duplicate key
*/

class MyIntHashEntry {
    int key;
    int value;
    MyIntHashEntry next;

    MyIntHashEntry(int l) {
        key =l; value=0; next = null;
    }

    MyIntHashEntry(int l, int v) {
        key =l; value=v; next = null;
    }

 }

public class MyIntHash {

    private MyIntHashEntry [] table;
    private int count =0;

    public MyIntHash(int size) {
        table = new MyIntHashEntry[size];
    }


    public void put(int key) {
        MyIntHashEntry entry = new MyIntHashEntry(key);
        int hash = key % table.length;
        entry.next=table[hash];
        table[hash]=entry;
        ++count;
    }

    public void put(int key, int value) {
    	  int hash = key % table.length;
    	  MyIntHashEntry entry = table[hash];
        while (entry!=null) {
            if (entry.key == key) {entry.value = value; return;};
            entry = entry.next;
        }
        entry = new MyIntHashEntry(key,value);
        entry.next=table[hash];
        table[hash]=entry;
        ++count;
    }

    public boolean containsKey(int key) {
        int hash = key % table.length;
        MyIntHashEntry entry = table[hash];
        while (entry!=null) {
            if (entry.key == key) return true;
            entry = entry.next;
        }
        return false;
    }

    public int get(int key) {
        int hash = key % table.length;
        MyIntHashEntry entry = table[hash];
        while (entry!=null) {
            if (entry.key == key) return entry.value;
            entry = entry.next;
        }
        return -99999;
    }

    public int size() {return count;}

}