
package ltsa.lts;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;


/* -----------------------------------------------------------------------*/

class RelabelDefn {
    ActionLabels newlabel;
    ActionLabels oldlabel;
    ActionLabels range;
    Vector defns;

    public void makeRelabels(Hashtable constants, Relation relabels) {
        Hashtable locals = new Hashtable();
        mkRelabels(constants,locals,relabels);
    }

    public void makeRelabels(Hashtable constants, Hashtable locals, Relation relabels) {
        mkRelabels(constants,locals,relabels);
    }


    private void mkRelabels(Hashtable constants, Hashtable locals, Relation relabels) {
        if (range!=null) {
            range.initContext(locals,constants);
            while(range.hasMoreNames()) {
                range.nextName();
                Enumeration e = defns.elements();
                while(e.hasMoreElements()) {
                    RelabelDefn r = (RelabelDefn)e.nextElement();
                    r.mkRelabels(constants,locals,relabels);
                }
            }
            range.clearContext();
        } else {
            newlabel.initContext(locals,constants);
            while(newlabel.hasMoreNames()) {
                String newName=newlabel.nextName();
                oldlabel.initContext(locals,constants);
                while(oldlabel.hasMoreNames()) {
                    String oldName=oldlabel.nextName();
                    relabels.put(oldName,newName);
                }
            }
            newlabel.clearContext();
        }
    }

    public static Relation getRelabels(Vector relabelDefns, Hashtable constants, Hashtable locals){
        if (relabelDefns == null) return null;
        Relation relabels = new Relation();
        Enumeration e = relabelDefns.elements();
        while(e.hasMoreElements()) {
             RelabelDefn r = (RelabelDefn)e.nextElement();
             r.makeRelabels(constants,locals,relabels);
        }
        return relabels;
    }

    public static Relation getRelabels(Vector relabelDefns) {
        return getRelabels(relabelDefns, new Hashtable(), new Hashtable());
    }
}