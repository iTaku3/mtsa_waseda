package ltsa.lts;

import java.util.BitSet;
import java.util.Map;

public class ModelExplorerContext {
	public int Nmach;											// number of machines to be composed
	public boolean canTerminate;								// alpha(nonTerm) subset alpha(term)
	public CompactState[] sm;									// array of state machines to be composed
	public PartialOrder partial;
	public int asteriskEvent = -1; 								// number of asterisk event
	public int[] actionCount;									// number of machines which share this action;
	public BitSet highAction = null;							// actions with high priority
	public int acceptEvent = -1;								// number of acceptance label @NAME
	public BitSet visible;										// BitSet of visible actions
	public int endSequence = LTSConstants.NO_SEQUENCE_FOUND;
	public int stateCount = 0;									// number of states analysed
	public MyList compTrans;									// list of transitions
	public boolean[] violated;									// true if this property already violated
	public String[] actionName;
	
	public String[] legalityActions;
}
