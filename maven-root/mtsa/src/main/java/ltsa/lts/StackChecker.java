package ltsa.lts;

/* 
* pachage stack check and state coding for PartialOrder
*/

public class StackChecker {
	
		StateCodec coder;
		StackCheck checker;
		
		public StackChecker(StateCodec coder, StackCheck checker) {
			this.coder = coder;
			this.checker = checker;
		}
		
	   public boolean onStack(int[] state) {
	   	  byte[] code = coder.encode(state);
	   	  if (code==null) return false;
	   	  return checker.onStack(code);
	   }
	
}