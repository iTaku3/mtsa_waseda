package ltsa.lts;

import java.util.Map;

public interface StateMap {
	
	public int get(byte[] state);
	public void add(byte[] state) throws UnsupportedOperationException;
	public void add(byte[] state, int depth) throws UnsupportedOperationException;
	public void add(byte[] state, int action, byte[] parent) throws UnsupportedOperationException;	// tracing enabled version of add()
	public boolean empty();
	public byte[] getNextState();
	public void markNextState(int stateNumber);
	public boolean nextStateIsMarked();
	public void removeNextState();
	public boolean contains(byte[] state);
	public int getNextStateNumber();
}
