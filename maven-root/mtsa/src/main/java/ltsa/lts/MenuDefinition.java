package ltsa.lts;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;


/* -----------------------------------------------------------------------*/

public class MenuDefinition {
    Symbol name;
    ActionLabels actions;
    Symbol params;
    Symbol target;
    Vector actionMapDefn;
    Vector controlMapDefn;
    Vector animations;

    public static Hashtable definitions;

    public static void compile(){
        RunMenu.init();
        Enumeration e = definitions.elements();
        while (e.hasMoreElements()){
            MenuDefinition m = (MenuDefinition)e.nextElement();
            RunMenu.add(m.makeRunMenu());
        }
    }

    public static String[] names() {
        if (definitions==null) return null;
        int n = definitions.size();
        String na[];
        if (n==0) return null; else na = new String[n];
        Enumeration e = definitions.keys();
        int i = 0;
        while (e.hasMoreElements())
            na[i++] = (String)e.nextElement();
        return na;
    }
    
    public static boolean[] enabled(String targ) {
        if (definitions==null) return null;
        int n = definitions.size();
        boolean na[];
        if (n==0) return null; else na = new boolean[n];
        Enumeration e = definitions.keys();
        int i = 0;
        while (e.hasMoreElements()) {
            MenuDefinition m = (MenuDefinition)definitions.get((String)e.nextElement());
            na[i++] = m.target==null?true:targ.equals(m.target.toString());
        } 
        return na;
    }

    public RunMenu makeRunMenu(){
        String na = name.toString();
        if (params==null) {
            Vector a=null;
            a = actions.getActions(null,null);
            return new RunMenu(na,a);
        } else {
            Relation a = RelabelDefn.getRelabels(actionMapDefn);
            Relation c = RelabelDefn.getRelabels(controlMapDefn);
            if (a==null) a = new Relation(); else a = a.inverse();
            if (c==null) c = new Relation(); else c = c.inverse();
            includeParts(a,c);
            return new RunMenu(na,params==null?null:params.toString(),a,c);
        }
    }
    
    protected void includeParts(Relation actions, Relation controls){
        if (animations == null) return;
        Enumeration e = animations.elements();
        while(e.hasMoreElements()) {
           AnimationPart ap = (AnimationPart)e.nextElement();
           ap.makePart();
           actions.union(ap.getActions());
           controls.union(ap.getControls());
        }
    }        
    
    public void addAnimationPart(Symbol n, Vector r) {
      if (animations==null) animations =  new Vector();
      animations.addElement(new AnimationPart(n,r));
    }
    
    class AnimationPart {
      Symbol name;
      Vector relabels;
      RunMenu compiled;
      
      AnimationPart(Symbol n, Vector r) {
        name = n;
        relabels = r;
      }
      
      void makePart() {
        MenuDefinition m = (MenuDefinition)definitions.get(name.toString());
        if (m==null) {
          Diagnostics.fatal ("Animation not found: "+name, name);
          return;
        }
        if (m.params == null) {
          Diagnostics.fatal ("Not an animation: "+name, name);
          return;
        }
        compiled = m.makeRunMenu();
        if (relabels!=null) {
          Relation r = RelabelDefn.getRelabels(relabels);
          if (compiled.actions!=null) compiled.actions.relabel(r);
          if (compiled.controls!=null) compiled.controls.relabel(r);
        }
      }      
    
      Relation getActions() {
        if (compiled!=null) 
           return compiled.actions;
        else
           return null;
      }
      
      Relation getControls() {
        if (compiled!=null)
          return compiled.controls;
        else
          return null;
      }
      
    }// end AnimationPart
        

}