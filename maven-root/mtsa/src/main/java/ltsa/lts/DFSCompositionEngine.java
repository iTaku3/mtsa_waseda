package ltsa.lts;

import java.util.List;

import ltsa.lts.util.LTSUtils;

/**
 * DFS Composition Strategy
 * @author epavese
 *
 */
public class DFSCompositionEngine implements CompositionEngine {
	protected StateMap analysed;
	protected StateCodec coder;
	protected ModelExplorerContext ctx;
	protected boolean deadlockDetected;
	protected long maxStateGeneration;
	LTSOutput output;
	
	public DFSCompositionEngine(StateCodec coder) {
		this.coder= coder;
		analysed= new MyHashStack(100001);
		// maxStateGeneration= LTSConstants.NO_MAX_STATE_GENERATION;
		maxStateGeneration= Options.getMaxStatesGeneration();
	}
	
	// @Override
	public void initialize() {
	}
	
	public void setOutput(LTSOutput output) {
		this.output= output;
	}

	
	// @Override
	public void teardown() {
		analysed= null;
	}
	
	// @Override
	public StackCheck getStackChecker() {
		if (analysed instanceof StackCheck)
			return (StackCheck) analysed;
		else
			return null;
	}
	
	// @Override
	public StateMap getExploredStates() {
		return analysed;
	}
	
	// @Override
	public void add(byte[] state) {
		analysed.add(state);
	}
	
	// @Override
	public void add(byte[] state, int depth) {
		analysed.add(state, depth);
	}
	
	// @Override
	public byte[] getNextState() {
		return analysed.getNextState();
	}
	
	// @Override
	public boolean nextStateIsMarked() {
		return analysed.nextStateIsMarked();
	}
	
	// @Override
	public void removeNextState() {
		analysed.removeNextState();
	}
	
	// @Override
	public boolean deadlockDetected() {
		return deadlockDetected;
	}
	
	public void processNextState() {
		int[] state = coder.decode(getNextState());
		analysed.markNextState(ctx.stateCount++);

		// determine eligible transitions
		List transitions = ModelExplorer.eligibleTransitions(ctx, state);
		if (transitions == null) {
			if (!ModelExplorer.isEND(ctx, state)) {
				deadlockDetected = true;
			} else { // this is the end state
				if (ctx.endSequence < 0)
					ctx.endSequence = ctx.stateCount - 1;
				else {
					analysed.markNextState(ctx.endSequence);
					--ctx.stateCount;
				}
			}
		} else {
			CompositionEngineCommon.processTransitions(coder, ctx, transitions, analysed);
		}
	}
	
	public String getExplorationStatistics() {
		// return "Depth " + analysed.getDepth() + " ";
		return "";
	}
	
	// @Override
	public void setModelExplorerContext(ModelExplorerContext ctx) {
		this.ctx= ctx;
	}
	
	// @Override
	public ModelExplorerContext getModelExplorerContext() {
		return ctx;
	}
	
	// @Override
	public void setMaxStateGeneration(long maxStates) {
		maxStateGeneration= maxStates;
	}
	
	// @Override
	public long getMaxStateGeneration() {
		return maxStateGeneration;
	}
	
	// @Override
	public void pruneUnfinishedStates() {
		// TODO can be improved.
		int tauIndex= 0;
		for (int i= 0; i < ctx.actionName.length; i++) {
			if (ctx.actionName[i].equals("tau")) {
				tauIndex= i;
				break;
			}
		}

		ctx.stateCount++;
		int[] trapState= null;
		byte[] trapStateCode= null;
		while (!analysed.empty()) {
			if (!analysed.nextStateIsMarked()) {
				if (analysed.getNextStateNumber() == -1) {
					analysed.markNextState(ctx.stateCount++);
				}

				if (trapState == null) {
					byte[] nextState= analysed.getNextState();
					trapState= LTSUtils.myclone(coder.decode(nextState));
					for (int i= 0; i < trapState.length; i++) {
						trapState[i]= LTSConstants.TRAP_STATE;
					}
					
					trapStateCode= coder.encode(trapState);
				}
				
				ctx.compTrans.add(analysed.getNextStateNumber(), trapStateCode, tauIndex);
			}
			analysed.removeNextState();
		}
	}
}
