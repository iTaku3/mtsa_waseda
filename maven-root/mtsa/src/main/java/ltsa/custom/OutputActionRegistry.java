package ltsa.custom;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import ltsa.lts.Relation;

public class OutputActionRegistry {

    Hashtable outputs = new Hashtable(); //Hashtable of Vectors
    Relation actionMap;
    AnimationMessage msg;

    public OutputActionRegistry(Relation actionMap, AnimationMessage msg) {
        this.actionMap = actionMap;
        this.msg = msg;
    }

    public void register(String name, AnimationAction action) {
        Vector a = (Vector)outputs.get(name);
        if (a!=null) {
            a.addElement(action);
        } else {
            a = new Vector();
            a.addElement(action);
            outputs.put(name,a);
        }
    }

    public void doAction(String name) {
        msg.traceMsg(name);
        Object o = actionMap.get(name);
        if (o==null) {
            return;  //if its not mapped don't do it
            //execute(name);
        } else if (o instanceof String ) {
            execute((String)o);
        } else {
            Vector a = (Vector)o;
            Enumeration e = a.elements();
            while(e.hasMoreElements()) {
                execute((String)e.nextElement());
            }
        }
    }

    private void execute(String name) {
        msg.debugMsg("-action -" + name);
        Vector a = (Vector)outputs.get(name);
        if (a==null) return;
        Enumeration e = a.elements();
        while(e.hasMoreElements()) {
            AnimationAction action = (AnimationAction)e.nextElement();
            action.action();
        }
    }

}
