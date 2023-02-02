package ltsa.lts;

public interface CompositionEngine {
	public void initialize();
	public void teardown();
	
	public StateMap getExploredStates();
	public StackCheck getStackChecker();
	public void add(byte[] state);
	public void add(byte[] state, int depth);
	
	public byte[] getNextState();
	public void removeNextState();
	public boolean nextStateIsMarked();
	
	public void processNextState();
	
	public String getExplorationStatistics();
	
	public void setModelExplorerContext(ModelExplorerContext ctx);
	public ModelExplorerContext getModelExplorerContext();
	
	public boolean deadlockDetected();
	public void setMaxStateGeneration(long maxStates);
	public long getMaxStateGeneration();
	
	public void pruneUnfinishedStates();
	
	public void setOutput(LTSOutput output);
}
