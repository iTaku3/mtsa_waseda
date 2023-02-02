package ltsa.lts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import ltsa.lts.util.LTSUtils;

/**
 * DFS Composition Strategy with Legality Check
 *
 */
public class DFSLegalCompositionEngine extends DFSCompositionEngine implements CompositionEngine {
	private int sourceMachineIndex;
	private int targetMachineIndex;
	private List<String> sourceControlledTransitions;
	private List<int[]> illegalStates;

	public DFSLegalCompositionEngine(StateCodec coder, int sourceMachineIndex, int targetMachineIndex, List<String> actionSet) {
		super(coder);

		this.sourceMachineIndex = sourceMachineIndex;
		this.targetMachineIndex = targetMachineIndex;
		this.sourceControlledTransitions = actionSet;
		this.illegalStates = new ArrayList<int[]>();
	}

	public void processNextState() {
		int[] state = coder.decode(getNextState());
		analysed.markNextState(ctx.stateCount++);

		// determine eligible transitions
		List transitions = ModelExplorer.eligibleTransitions(ctx, state);
		this.doStateChecks(state, transitions);
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
	
	private void doStateChecks(int[] state, List<int[]> transitions) {
		
		// get source transitions for state
		int currentSourceStateIndex = state[this.sourceMachineIndex];
		EventState sourceState = ctx.sm[sourceMachineIndex].states[currentSourceStateIndex];
		ArrayList<String> sourceTransitions = getTransitionsForState(ctx.sm[sourceMachineIndex], sourceState);
		List<String> sourceAlphabet = new ArrayList<>(Arrays.asList(ctx.sm[sourceMachineIndex].alphabet));

		// get target transitions for state
		int currentTargetStateIndex = state[targetMachineIndex];
		EventState targetState = ctx.sm[targetMachineIndex].states[currentTargetStateIndex];
		ArrayList<String> targetTransitions = getTransitionsForState(ctx.sm[targetMachineIndex], targetState);
		
		// si targetState tiene una transicion saliente que no esta en controlablesDeA entonces sourceState tambien la tiene
		for (String transition: targetTransitions) {
			if (!sourceControlledTransitions.contains(transition)) {
				if (!sourceTransitions.contains(transition) && sourceAlphabet.contains(transition)) {
					//this.output.outln(String.format("Illegal: %s; action: %s", Arrays.toString(state), transition));
					this.illegalStates.add(state);
					
					break; // comment this if you need to detect every illegal state.
				}
			}
		}
		
	}

	private ArrayList<String> getTransitionsForState(CompactState sm, EventState state) {
		ArrayList<String> trans = new ArrayList<String>();
		EventState next = state;	
		
		while (next != null) {
			//trans.add(sm.alphabet[state.event]);
			trans.add(ctx.actionName[next.event]);
			next = next.list;
		}

		return trans;
	}


	public boolean isLegal() {
		// TODO Auto-generated method stub
		return this.illegalStates.isEmpty();
	}
	
}
