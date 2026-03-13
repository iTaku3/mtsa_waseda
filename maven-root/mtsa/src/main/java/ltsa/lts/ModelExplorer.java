package ltsa.lts;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ltsa.lts.util.LTSUtils;

public class ModelExplorer {
	public static boolean isEND(ModelExplorerContext ctx, int[] state) {
		if (!ctx.canTerminate)
			return false;
		for (int i = 0; i < ctx.Nmach; i++) {
			// if (sm[i].endseq < 0)
			// ; // skip
			/*else*/
			if (ctx.sm[i].endseq != state[i])
				return false;
		}
		return true;
	}
	
	public static List eligibleTransitions(ModelExplorerContext ctx, int[] state) {
		List asteriskTransitions = null;
		if (ctx.partial != null) {
			if (ctx.asteriskEvent > 0
					&& EventState.hasEvent(
							ctx.sm[ctx.Nmach - 1].states[state[ctx.Nmach - 1]],
							ctx.asteriskEvent)) {
				// do nothing
			} else {
				List parTrans = ctx.partial.transitions(state);
				if (parTrans != null)
					return parTrans;
			}
		}
		int[] ac = LTSUtils.myclone(ctx.actionCount);
		EventState[] trs = new EventState[ctx.actionCount.length];
		int nsucc = 0; // count of feasible successor transitions
		int highs = 0; // eligible high priority actions
		
		// Legality
		//LegalityCheck legality = new LegalityCheck();
		
		
		// first check number of possible successors
		for (int i = 0; i < ctx.Nmach; i++) {// foreach machine
			EventState p = ctx.sm[i].states[state[i]];
			doLegalityCheck(ctx, i, p);
			while (p != null) { // foreach transition
			    //legality.addTransitions(ctx, i, p);
				EventState tr = p;
				tr.path = trs[tr.event];
				trs[tr.event] = tr;
				ac[tr.event]--;
				if (tr.event != 0 && ac[tr.event] == 0) {
					nsucc++; // ignoring tau, this transition is possible
					// bugfix 26-mar-04 to handle asterisk + priority
					if (ctx.highAction != null && ctx.highAction.get(tr.event)
							&& tr.event != ctx.asteriskEvent)
						++highs;
				}
				
				p = p.list;
			}
		}
		if (nsucc == 0 && trs[0] == null)
			return null; // DEADLOCK - no successor states
		int actionNo = 1;
		List transitions = new ArrayList(8);
		// we include tau if it is high priority or its low and there are no
		// high priority transitions
		if (trs[0] != null) {
			boolean highTau = (ctx.highAction != null && ctx.highAction.get(0));
			if (highTau || highs == 0)
				ModelExplorer.computeTauTransitions(ctx, trs[0], state, transitions);
			if (highTau)
				++highs;
		}
		while (nsucc > 0) { // do this loop once per successor state
			nsucc--;
			// find number of action
			while (ac[actionNo] > 0)
				actionNo++;
			// now compute the state for this action if not excluded tock
			if (highs <= 0 || ctx.highAction.get(actionNo) || actionNo == ctx.acceptEvent) {
				EventState tr = trs[actionNo];
				boolean nonDeterministic = false;
				boolean probabilistic= false;
				while (tr != null) { // test for non determinism or probabilistic transitions
					// tr.path holds all tr (EventStates) that synchronise to make this transition 
					if (tr.nondet != null) {
						nonDeterministic = true;
					}
					if (tr instanceof ProbabilisticEventState) {
						ProbabilisticEventState probTr= (ProbabilisticEventState) tr;
						// if (probTr.probTr != null)
						if (probTr.getProbability().compareTo(BigDecimal.ZERO) != 0 || probTr.probTr != null)
							probabilistic= true;
					}
					
					if (nonDeterministic || probabilistic)
						break;

					tr = tr.path;
				}
				tr = trs[actionNo];
				if (!nonDeterministic && !probabilistic) {
					int[] next = LTSUtils.myclone(state);
					next[ctx.Nmach] = actionNo;
					while (tr != null) {
						next[tr.machine] = tr.next;
						tr = tr.path;
					}
					if (actionNo != ctx.asteriskEvent)
						transitions.add(next);
					else {
						asteriskTransitions = new ArrayList(1);
						asteriskTransitions.add(next);
					}
				} else if (!probabilistic) {
					if (actionNo != ctx.asteriskEvent) {
						computeNonDetTransitions(ctx, tr, state, transitions);
					} else {
						computeNonDetTransitions(ctx, tr, state,
								asteriskTransitions = new ArrayList(4));
					}
				} else {
					// probabilistic (and possibly nondeterministic) transitions
					computeProbabilisticTransitions(ctx, state, tr, state, null, null, transitions);
				}

			}
			++ac[actionNo];
		}
		if (ctx.asteriskEvent < 0)
			return transitions;
		else
			return mergeAsterisk(ctx, transitions, asteriskTransitions);
	}
	
	private static void doLegalityCheck(ModelExplorerContext ctx, int i, EventState p) {
        // TODO Auto-generated method stub
        
    }

    private static void computeTauTransitions(ModelExplorerContext ctx, EventState first, int[] state, List v) {
		EventState down = first;
		while (down != null) {
			EventState across = down;
			while (across != null) {
				int[] next = LTSUtils.myclone(state);
				next[across.machine] = across.next;
				next[ctx.Nmach] = 0; // tau
				v.add(next);
				across = across.nondet;
			}
			down = down.path;
		}
	}
	
	private static void computeNonDetTransitions(ModelExplorerContext ctx, EventState first, int[] state, List v) {
		EventState tr = first;
		while (tr != null) {
			int[] next = LTSUtils.myclone(state);
			next[tr.machine] = tr.next;
			if (first.path != null) {
				// generate the tree of possible nondet combinations.
				computeNonDetTransitions(ctx, first.path, next, v);
			} else {
				next[ctx.Nmach] = first.event;
				v.add(next);
			}
			tr = tr.nondet;
		}
	}
	
	/**
	 * Computes probabilistic (and potentially nondeterministic) transitions
	 * 
	 * @param ctx
	 * @param first holds the first EventState on the chain of possible synchronisations
	 * @param state
	 * @param v
	 */
	private static void computeProbabilisticTransitions(ModelExplorerContext ctx, int[] sourceStates, EventState first, int[] state, int[] bundles, BigDecimal[] probs, List v) {

		if (!(first instanceof ProbabilisticEventState)) {
			Diagnostics.fatal("Probabilistic transitions expected");
		}

		ProbabilisticEventState tr= (ProbabilisticEventState) first;
		// outer loop nondeterministic, inner loop probabilistic
		while (tr != null) {
			ProbabilisticEventState probTr= tr;
			while (probTr != null) {
				if (bundles == null) {
					bundles= new int[ctx.Nmach];
					for (int i= 0; i < ctx.Nmach; i++)
						bundles[i]= ProbabilisticTransition.NO_BUNDLE;
				}

				if (probs == null)
					probs= new BigDecimal[ctx.Nmach];

				int[] next= LTSUtils.myclone(state);
				next[tr.machine]= probTr.next;
				bundles[tr.machine]= probTr.getBundle();
				probs[tr.machine]= probTr.getProbability();

				if (first.path != null) {
					computeProbabilisticTransitions(ctx, sourceStates, first.path, next, bundles, probs, v);
				} else {
					// last machine
					ProbabilisticEligibleTransition probNext= new ProbabilisticEligibleTransition();
					next[ctx.Nmach]= first.event;
					probNext.next= next;
					probNext.sourceBundles= LTSUtils.myclone(bundles);
					probNext.sourceProbs= LTSUtils.myclone(probs);
					probNext.sourceStates= LTSUtils.myclone(sourceStates);
					v.add(probNext);
				}
				
				probTr= (ProbabilisticEventState) probTr.probTr;
			}

			tr= (ProbabilisticEventState) tr.nondet;
		}
	}
	
	private static List mergeAsterisk(ModelExplorerContext ctx, List transitions, List asteriskTransitions) {
		if (transitions == null || asteriskTransitions == null)
			return transitions;
		if (transitions.size() == 0)
			return null;
		int[] asteriskTransition;
		if (asteriskTransitions.size() == 1) {
			asteriskTransition = (int[]) asteriskTransitions.get(0);
			Iterator e = transitions.iterator();
			while (e.hasNext()) {
				int[] next = (int[]) e.next();
				if (!ctx.visible.get(next[ctx.Nmach])) {
					 // fragile, assumes property is last machine!!
					next[ctx.Nmach - 1] = asteriskTransition[ctx.Nmach - 1];
				}
			}
			return transitions;
		} else {
			Iterator a = asteriskTransitions.iterator();
			List newTransitions = new ArrayList();
			while (a.hasNext()) {
				asteriskTransition = (int[]) a.next();
				Iterator e = transitions.iterator();
				while (e.hasNext()) {
					int[] next = (int[]) e.next();
					if (!ctx.visible.get(next[ctx.Nmach])) {
						// fragile, assumes property is last machine!! 
						next[ctx.Nmach - 1] = asteriskTransition[ctx.Nmach - 1];
					}
					newTransitions.add(LTSUtils.myclone(next));
				}
			}
			return newTransitions;
		}
	}
}
