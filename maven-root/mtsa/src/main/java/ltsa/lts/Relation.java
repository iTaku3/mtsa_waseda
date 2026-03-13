package ltsa.lts;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class Relation extends Hashtable {
    boolean is_relation = false; //false if 1-many in hash

    //handle relations
     public Object put(Object  key, Object  value) {
        if (!containsKey(key)) {
            return super.put(key,value);
        } else {    //its a one to many
            if (!is_relation) is_relation =true;
            Object o = get(key);
            if (o instanceof Vector) {
                Vector v = (Vector) o;
                if (!v.contains(value)) v.addElement(value);
            } else {
                Vector v = new Vector(4);
                v.addElement(o);
                if(!v.equals(o)) v.addElement(value);
                super.put(key,v);
            }
            return o;
        }
     }
         

     public boolean isRelation() {return is_relation;};

     public Relation inverse() {
        Relation inv = new Relation();
        Enumeration k = keys();
        while (k.hasMoreElements()) {
            Object key = k.nextElement();
            Object val = get(key);
            if (!(val instanceof Vector)) {
                inv.put(val,key);
            } else {
                Enumeration v = ((Vector)val).elements();
                while (v.hasMoreElements()) {
                    inv.put(v.nextElement(),key);
                }
            }
        }
        return inv;
     }
     
     public void union(Relation r) {
       if (r==null) return;
       Enumeration k = r.keys();
       while (k.hasMoreElements()) {
         Object key = k.nextElement();
         Object val = r.get(key);
         putValues(key,val);
       }
     }
     
      public void relabel(Relation r) {    //r maps oldkey to new key(s)
       Enumeration k = keys();
       while (k.hasMoreElements()) {
         String oldkey = (String)k.nextElement();  //old key
         Object values = get(oldkey);
         if (r.containsKey(oldkey)) {
             Object newkey = r.get(oldkey);       //new keys
             remove(oldkey);
             if (!(newkey instanceof Vector)) {
                putValues(newkey,values);
             } else {
                Enumeration v = ((Vector)newkey).elements();
                while (v.hasMoreElements()) {
                  putValues(v.nextElement(),values);
                }
             }
         } else if (hasPrefix(oldkey,r)) {
            Object newkey = r.get(prefix(oldkey,r));
            if (!(newkey instanceof Vector)) {
                String nk = prefixReplace(oldkey,(String)newkey,r);           
                putValues(nk,values);
             } else {
                Enumeration v = ((Vector)newkey).elements();
                while (v.hasMoreElements()) {
                  String nk = prefixReplace(oldkey,(String)v.nextElement(),r);
                  putValues(nk,values);
                }
             }
         }   
       }
      }

     protected void putValues(Object  key, Object values) {
       if (!(values instanceof Vector)) {
           put(key,values);
       } else {
         Enumeration v = ((Vector)values).elements();
         while (v.hasMoreElements()) {
             put(key,v.nextElement());
         }
       }
     }
     
     static private String prefixReplace(String s, String np, Hashtable oldtonew) {
        int prefix_end = maximalPrefix(s,oldtonew);
        if (prefix_end<0) return s;
        return np + s.substring(prefix_end);
    }

    static private int maximalPrefix(String s, Hashtable oldtonew) {
        int prefix_end = s.lastIndexOf('.');
        if (prefix_end<0) return prefix_end;
        if (oldtonew.containsKey(s.substring(0,prefix_end)))
            return prefix_end;
        else
            return maximalPrefix(s.substring(0,prefix_end),oldtonew);
    }

    static private boolean hasPrefix(String s, Hashtable oldtonew) {
       return (maximalPrefix(s,oldtonew)>=0);
    }
    
    static private String prefix(String s, Hashtable oldtonew) {
       return s.substring(0,maximalPrefix(s,oldtonew));
    }

   
}