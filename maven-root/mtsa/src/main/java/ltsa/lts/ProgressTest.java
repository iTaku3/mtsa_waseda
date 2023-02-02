package ltsa.lts;

import java.util.BitSet;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class ProgressTest {

    String name;
    Vector pactions;   // vector of strings
    BitSet pset;
    Vector cactions;   //if P then C
    BitSet cset;

    static Vector tests; //  vector of progress tests

    public static void init(){
        tests = new Vector();
    }

    public ProgressTest(String name, Vector pactions, Vector cactions) {
        this.name = name;
        this.cactions = cactions;
        this.pactions = pactions;
        tests.addElement(this);
    }

    //return convert sets of actions to bitset using alphabet
    public static void initTests(String alphabet[]) {
        if (tests==null || tests.size()==0) return;
        // convert alphabet to hashtable
        Hashtable stoi = new Hashtable(alphabet.length);
        for (int i=0; i<alphabet.length; ++i)
            stoi.put(alphabet[i],new Integer(i));
        // for each key
        Enumeration e = tests.elements();
        while(e.hasMoreElements()) {
            ProgressTest p = (ProgressTest) e.nextElement();
            p.pset= alphaToBit(p.pactions,stoi);
            p.cset= alphaToBit(p.cactions,stoi);
        }
     }

    public static boolean noTests() {
        return (tests==null || tests.size()==0);
    }

    private static BitSet alphaToBit(Vector actions, Hashtable stoi) {
        if (actions==null) return null;
        BitSet b = new BitSet(stoi.size());
        Enumeration en = actions.elements();
        while(en.hasMoreElements()) {
             String s = (String)en.nextElement();
             Integer I = (Integer)stoi.get(s);
             if (I!=null) b.set(I.intValue());
        }
        return b;
    }


}

