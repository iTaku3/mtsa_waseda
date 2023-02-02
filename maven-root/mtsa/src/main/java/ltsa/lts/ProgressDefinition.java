package ltsa.lts;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;


/* -----------------------------------------------------------------------*/

class ProgressDefinition {
    Symbol name;
    ActionLabels pactions;
    ActionLabels cactions; //if P then C
    ActionLabels range;    //range of tests

    static Hashtable definitions;

    public static void compile(){
        ProgressTest.init();
        Enumeration e = definitions.elements();
        while (e.hasMoreElements()){
            ProgressDefinition p = (ProgressDefinition)e.nextElement();
            p.makeProgressTest();
        }
    }

    public void makeProgressTest(){
        Vector pa=null;
        Vector ca=null;
        String na = name.toString();
        if (range==null) {
            pa = pactions.getActions(null,null);
            if (cactions!=null) ca = cactions.getActions(null,null);
            new ProgressTest(na,pa,ca);
        } else {
            Hashtable locals = new Hashtable();
            range.initContext(locals,null);
            while(range.hasMoreNames()) {
                String s = range.nextName();
                pa = pactions.getActions(locals,null);
                if (cactions!=null) ca = cactions.getActions(locals,null);
                new ProgressTest(na+"."+s,pa,ca);
            }
            range.clearContext();
        }
    }
}

