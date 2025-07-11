package ltsa.lts;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/* -----------------------------------------------------------------------*/

public class LabelSet {
    boolean isConstant=false;
    Vector labels;     // list of unevaluates ActionLabelss, null if this is a constant set
    public Vector<String> actions;    // list of action names for an evaluated constant set

    static Hashtable constants; // hashtable of constant sets, <string,LabelSet>

    public LabelSet(Symbol s, Vector<ActionLabels> lbs) {
        labels = lbs;
        if(constants.put(s.toString(),this)!=null) {
            Diagnostics.fatal("duplicate set definition: "+s,s);
        }
        actions = getActions(null);  // name must be null here
        isConstant=true;
        labels = null;
    }

    public LabelSet(Vector lbs) {
        labels = lbs;
    }

    public Vector<String> getActions(Hashtable params) {
        Vector<String> actions2 = getActions(null,params);
		return actions2;
    }

    public Vector<String> getActions(Hashtable locals, Hashtable params) {
      if (isConstant) return actions;
      if (labels ==null) return null;
      Vector<String> v = new Vector<String>();
      Hashtable dd = new Hashtable(); // detect and discard duplicates
      Hashtable mylocals = locals!=null?(Hashtable)locals.clone():null;
      Enumeration<ActionLabels> e = labels.elements();
      while (e.hasMoreElements()) {
         ActionLabels l = e.nextElement();
         l.initContext(mylocals,params);
         while(l.hasMoreNames()) {
            String s = l.nextName();
            if (!dd.containsKey(s)) {
                v.addElement(s);
                dd.put(s,s);
            }
         }
         l.clearContext();
      }
      return v;
    }

    public Vector<String> getActions2(Hashtable params) {
        Vector<String> actions2 = getActions2(null,params);
        return actions2;
    }

    public Vector<String> getActions2(Hashtable locals, Hashtable params) {
        //System.out.println("aaaaa");
        if (isConstant) return actions;
        if (labels ==null) return null;
        Vector<String> v = new Vector<String>();
        //Hashtable dd = new Hashtable(); // detect and discard duplicates
        Hashtable mylocals = locals!=null?(Hashtable)locals.clone():null;
        Enumeration<ActionLabels> e = labels.elements();
        while (e.hasMoreElements()) {
            ActionLabels l = e.nextElement();
            l.initContext(mylocals,params);
            while(l.hasMoreNames()) {
                String s = l.nextName();
                //if (!dd.containsKey(s)) {
                v.addElement(s);
                //dd.put(s,s);
                //}
            }
            l.clearContext();
        }
        return v;
    }

    // >>> AMES: Enhanced Modularity
    public static Hashtable getConstants() {
    	return constants;
    }
    // <<< AMES

}
