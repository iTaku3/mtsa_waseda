package ltsa.lts;

import java.math.BigDecimal;

public class CompactStateUtils {

	/**
	 * This method assumes that in the CompactState structure any 
	 * EventState could be probabilistic. 
	 * It is likely that checking only the first level of EventStates would be enough.
	 *  
	 * @param cs
	 * @return
	 */
	public static boolean isProbabilisticMachine(CompactState cs) {
		for (EventState from : cs.states) {
			EventState p = from;
			while (p != null) {
				EventState q = p;
				while (q != null) {
					if (q instanceof ProbabilisticEventState) {
						return true;
					}
					q = q.nondet;
				}
				p = p.list;
			}
		}
		return false;
	}

	public static int getMaxBundle(CompactState cs) {
		int res = 0;

		for (EventState from : cs.states) {
			EventState p = from;
			while (p != null) {
				EventState q = p;
				while (q != null) {
					if (q instanceof ProbabilisticEventState) {
						ProbabilisticEventState prob = (ProbabilisticEventState) q;
						if (prob.getBundle() > res) {
							res = prob.getBundle();
						}
					}
					q = q.nondet;
				}
				p = p.list;
			}
		}
		return res;
	}

	private static ProbabilisticEventState copy(EventState es) {
		ProbabilisticEventState res = null;
		if (es != null) {
			if (es instanceof ProbabilisticEventState) {
				ProbabilisticEventState probCurr = (ProbabilisticEventState) es;
				res = new ProbabilisticEventState(probCurr.event, probCurr.next, probCurr.getProbability(),
						probCurr.getBundle());
				res.probTr = copy(probCurr.probTr);
			} else {
				res = new ProbabilisticEventState(es.event, es.next, BigDecimal.valueOf(1), ++newBundle);
			}
			res.list = copy(es.list);
			res.nondet = copy(es.nondet);
		}
		return res;
	}

	
	private static EventState probToNonProb(EventState es) {
		EventState res = null;
		if (es != null) {
			res = new EventState(es.event, es.next);
			res.list = probToNonProb(es.list);
			res.nondet = probToNonProb(es.nondet);
			if (es instanceof ProbabilisticEventState) {
				ProbabilisticEventState p = (ProbabilisticEventState) es;
				EventState curr = res;
				while (curr.nondet != null) {
					curr = curr.nondet;
				}
				curr.nondet = probToNonProb(p.probTr);
			}
		}
		return res;
	}

	// Hack.
	private static int newBundle = 0;
	
	public static CompactState convertIfProbabilistic(CompactState c) {
		if (isProbabilisticMachine(c)) {
			c = convertToProbabilistic(c);
		}
		return c;
	}

	public static CompactState convertToProbabilistic(CompactState c) {
		EventState[] newStates = new ProbabilisticEventState[c.states.length];
		newBundle = getMaxBundle(c);
		for (int i = 0; i < c.states.length; i++) {
			newStates[i] = copy(c.states[i]);
		}
		c.states = newStates;
		return c;
	}

	public static CompactState convertToNonProbabilistic(CompactState c) {
		EventState[] newStates = new EventState[c.states.length];
		for (int i = 0; i < c.states.length; i++) {
			newStates[i] = probToNonProb(c.states[i]);
		}
		c.states = newStates;
		return c;
	}

	
	
}
