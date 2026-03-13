package ltsa.lts;

import java.util.Vector;

/**
 *  This interface presents the common operations
 *  between composed automata & on-the-fly composition
 */
public interface Automata {
	
	//returns the alphabet
	public String[] getTransitionsLabels();
	
	//returns the transitions from a particular state
	public MyList getTransitions(byte[] state);
	
	//returns name of violated property if ERROR found in getTransitions
	public String getViolatedProperty();
	
	//returns shortest trace to  state (vector of Strings)
	public Vector getTraceToState(byte[] from, byte[] to);
	
	//returns true if  END state
	public  boolean END(byte[] state);
	
	//returns true if Accepting state
	public  boolean isAccepting(byte[] state);
	
	//return the number of the START state
	public byte[] START();
	
	//set the Stack Checker for partial order reduction
	public void setStackChecker(StackCheck s);
	
	//returns true if partial order reduction
	public boolean isPartialOrder();
	
	//diable partial order 
	public void disablePartialOrder();
	
	//enable partial order 
	public void enablePartialOrder();
		
}