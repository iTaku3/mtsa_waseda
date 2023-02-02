package ltsa.lts;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Enumeration;
import java.util.Hashtable;

public class AutCompactState extends CompactState {
	
	public AutCompactState(Symbol name, File autfile) {
        this.name = name.toString();
        BufferedReader bf = null;
        try {
           bf = new BufferedReader(new FileReader(autfile));
        } catch (Exception e){
        	  Diagnostics.fatal("Error opening file"+e, name);
        }
        try {
        	 String header = bf.readLine();
        	 if (header==null) Diagnostics.fatal("file is empty", name);
        	 maxStates = statesAUTheader(header);
        	 states =  new EventState[maxStates];
        	 Hashtable newAlpha = new Hashtable();
        	 Counter c = new Counter(0);
        	 newAlpha.put("tau",c.label());
        	 String line = null;
        	 int trans = transitionsAUTheader(header);
        	 int tc =0;
        	 while((line = bf.readLine())!=null) {
        	 	parseAUTtransition(line,newAlpha,c);
        	 	++tc;
        	 }
        	 if (tc!=trans)
        	 	 Diagnostics.fatal("transitions read different from .aut header", name);
        	 //create new alphabet
        	 alphabet = new String[newAlpha.size()];
	        Enumeration e = newAlpha.keys();
	        while(e.hasMoreElements()) {
	            String s = (String)e.nextElement();
	            int i = ((Integer)newAlpha.get(s)).intValue();
	            alphabet[i] = s;
	        }
        } catch (Exception e){
        	  Diagnostics.fatal("Error reading/translating file"+e, name);
        }
    }
    
    protected int statesAUTheader(String header) {
    	    // des( 0, ts, ns)
    	    int i = header.lastIndexOf(',');
    	    String s = (header.substring(i+1,header.indexOf(')'))).trim();
    	    return Integer.parseInt(s);
    }
    
    protected int transitionsAUTheader(String header) {
    	    // des( 0, ts, ns)
    	    int i = header.indexOf(',');
    	    int j = header.lastIndexOf(',');
    	    String s = (header.substring(i+1,j)).trim();
    	    return Integer.parseInt(s);
    }
    
    protected void parseAUTtransition(String line, Hashtable alpha, Counter c) {
    	   // (from,label,to)
    	   int i = line.indexOf('(');
    	   int j = line.indexOf(',');
    	   String s = (line.substring(i+1,j)).trim();
    	   int from = Integer.parseInt(s);
    	   int k = line.indexOf(',',j+1);
    	   String label = (line.substring(j+1,k)).trim();
    	   if (label.charAt(0)=='"')  //remove quotes
    	      label = (label.substring(1,label.length()-1)).trim();
    	   int l = line.indexOf(')');
    	   s = (line.substring(k+1,l)).trim();
    	   int to = Integer.parseInt(s);
    	   Integer labelid = (Integer)alpha.get(label);
    	   if (labelid == null) {
    	   	 labelid=c.label();
    	   	 alpha.put(label,labelid);
    	   }
    	   states[from] = EventStateUtils.add(states[from],new EventState(labelid.intValue(),to));
    }

}
	