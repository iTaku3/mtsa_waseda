package ltsa.lts;

import java.util.List;
import java.util.Arrays;
import java.util.Iterator;

public class CompositionEngineCommon {

	/**
	 * for lack of a better name :(
	 */
	public static void processTransitions(StateCodec coder, ModelExplorerContext ctx, List transitions, StateMap analysed) {
		Iterator e= transitions.iterator();
		while (e.hasNext()) {
			Object next= e.next();
			int[] nextState= null;
			ProbabilisticEligibleTransition nextProbTr= null;
			if (next instanceof int[]) {
				nextState= (int[]) next;
			} else if (next instanceof ProbabilisticEligibleTransition) {
				nextProbTr= (ProbabilisticEligibleTransition) next;
				nextState= nextProbTr.next;
			} else {
				Diagnostics.fatal("Nondeterministic or probabilistic transition expected");
			}

			byte[] code= coder.encode(nextState);
			// TODO compTrans is only nondeterministic, need to create some probabilistic transitions
			if (next instanceof int[]) {
				ctx.compTrans.add(ctx.stateCount - 1, code, nextState[ctx.Nmach]);
			} else {
				ctx.compTrans.add(ctx.stateCount - 1, code, nextState[ctx.Nmach],
								  ProbabilisticTransition.composeBundles(nextProbTr.sourceStates, nextProbTr.sourceBundles),
								  ProbabilisticTransition.composeProbs(nextProbTr.sourceProbs));
			}

			if (code == null) {
				int i= 0;
				while (nextState[i] >= 0)
					i++;
				if (!ctx.violated[i]) {
					// TODO move away and log
					// output.outln("  property " + ctx.sm[i].name +
					// " violation.");
				}
				ctx.violated[i]= true;
			} else if (!analysed.contains(code)) {
				analysed.add(code);

			}
		}
	}
}
