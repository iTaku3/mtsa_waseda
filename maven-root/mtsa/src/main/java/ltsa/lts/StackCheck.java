package ltsa.lts;

/* this interface presents the  operations
*  to check whether a state is on the stack  during on-the-fly composition
*/

public interface StackCheck {
	
	public boolean onStack(byte[] key); //return true if state is on the stack
	
}