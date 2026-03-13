package ltsa.lts;

import org.apache.commons.lang.ArrayUtils;

import java.io.PrintStream;
import java.util.BitSet;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import java.util.Vector;

// records transitions in the CompactState class

public class EventState {
	int event;
	int next;
	int machine;
	EventState list; // used to keep list in event order, TAU first
	EventState nondet;// used for additional non-deterministic transitions
	EventState path; // used by analyser & by minimiser

	public EventState(int event, int next) {
		this.event = event;
		this.next = next;
	}

	public Enumeration elements() {
		return new EventStateEnumerator(this);
	}

	public int getEvent() {
		return event;
	}

	public int[] getEvents() {
		int[] events;
		if (this.list == null) {
			events = new int[1];
			events[0] = this.event;
		} else {
			int[] head = new int[1];
			head[0] = this.event;

			int[] tail = this.list.getEvents();

			events = ArrayUtils.addAll(head, tail);
		}

		return events;
	}

	public int getNext() {
		return next;
	}

	public int getNext(int event) {
		if (this.event == event) {
			return this.next;
		} else {
			if (this.list == null)
				throw new IllegalStateException("Invalid action execution");

			return this.list.getNext(event);
		}
	}

	public void updateEventAndNext(int oldEvent, int newEvent, int newNext) {
		if (this.event == oldEvent) {
			this.event = newEvent;
			this.next = newNext;
		} else {
			if (this.list != null)
				this.list.updateEventAndNext(oldEvent, newEvent, newNext);
		}
	}

	public void swapNext(int next1, int next2) {
		if (this.next == next1) {
			this.next = next2;
		} else {
			if (this.next == next2)
				this.next = next1;
		}

		if (this.list != null)
			this.list.swapNext(next1, next2);
	}

	public static EventState remove(EventState head, EventState tr) {
		// remove from head
		if (head == null)
			return head;
		if (head.event == tr.event && head.next == tr.next) {
			if (head.nondet == null)
				return head.list;
			else {
				head.nondet.list = head.list;
				return head.nondet;
			}
		}
		EventState p = head;
		EventState plag = head;
		while (p != null) {
			EventState q = p;
			EventState qlag = p;
			while (q != null) {
				if (q.event == tr.event && q.next == tr.next) {
					if (p == q) { // remove from head of nondet
						if (p.nondet == null) {
							plag.list = p.list;
							return head;
						} else {
							p.nondet.list = p.list;
							plag.list = p.nondet;
							return head;
						}
					} else {
						qlag.nondet = q.nondet;
						return head;
					}
				}
				qlag = q;
				q = q.nondet;
			}
			plag = p;
			p = p.list;
		}
		return head;
	}

	public static EventState removeEvent(EventState anEventState, int anEvent) {
		if (anEventState == null)
			return null;

		EventState cleanEvent = anEventState;
		if (cleanEvent.event == anEvent)
			cleanEvent = cleanEvent.list;
		else
			cleanEvent.list = EventState.removeEvent(cleanEvent.list, anEvent);
		return cleanEvent;
	}

	public static EventState copy(EventState anEventState) {
		EventState aCopy = new EventState(anEventState.event, anEventState.next);
		aCopy.machine = anEventState.machine;

		if (anEventState.list == null)
			aCopy.list = null;
		else
			aCopy.list = EventState.copy(anEventState.list);

		if (anEventState.nondet == null)
			aCopy.nondet = null;
		else
			aCopy.nondet = EventState.copy(anEventState.nondet);

		if (anEventState.path == null)
			aCopy.path = null;
		else
			aCopy.path = EventState.copy(anEventState.path);

		return aCopy;
	}

	public static boolean hasState(EventState head, int next) {
		EventState p = head;
		while (p != null) {
			EventState q = p;
			while (q != null) {
				if (q.next == next)
					return true;
				q = q.nondet;
			}
			p = p.list;
		}
		return false;
	}

	public static void replaceWithError(EventState head, int sinkState) {
		EventState p = head;
		while (p != null) {
			EventState q = p;
			while (q != null) {
				if (q.next == sinkState)
					q.next = Declaration.ERROR;
				q = q.nondet;
			}
			p = p.list;
		}
	}

	public static EventState offsetSeq(int off, int seq, int max, EventState head) {
		EventState p = head;
		while (p != null) {
			EventState q = p;
			while (q != null) {
				if (q.next >= 0) {
					if (q.next == seq)
						q.next = max;
					else
						q.next += off;
				}
				q = q.nondet;
			}
			p = p.list;
		}
		return head;
	}

	public static EventState replaceNext(EventState anEventState, int oldNext, int newNext) {
		if (anEventState == null)
			return null;

		if (anEventState.next == oldNext)
			anEventState.next = newNext;

		anEventState.list = EventState.replaceNext(anEventState.list, oldNext, newNext);

		return anEventState;
	}

	public static EventState addEvent(EventState anEventState, int anEvent, int aState) {
		EventState updatedEvent = new EventState(anEvent, aState);
		updatedEvent.machine = anEventState.machine;
		updatedEvent.nondet = anEventState.nondet;
		updatedEvent.path = anEventState.path;
		updatedEvent.list = anEventState;
		return updatedEvent;
	}

	public static int toState(EventState head, int next) {
		EventState p = head;
		while (p != null) {
			EventState q = p;
			while (q != null) {
				if (q.next == next)
					return q.event;
				q = q.nondet;
			}
			p = p.list;
		}
		return -1;
	}

	public static int countStates(EventState head, int next) {
		EventState p = head;
		int result = 0;
		while (p != null) {
			EventState q = p;
			while (q != null) {
				if (q.next == next)
					result++;
				q = q.nondet;
			}
			p = p.list;
		}
		return result;
	}

	public static boolean hasEvent(EventState head, int event) {
		EventState p = head;
		while (p != null) {
			if (p.event == event)
				return true;
			p = p.list;
		}
		return false;
	}

	public static boolean isAccepting(EventState head, String[] alphabet) {
		EventState p = head;
		while (p != null) {
			if (alphabet[p.event].charAt(0) == '@')
				return true;
			p = p.list;
		}
		return false;
	}

	public static boolean isTerminal(int state, EventState head) {
		EventState p = head;
		while (p != null) {
			EventState q = p;
			while (q != null) {
				if (q.next != state)
					return false;
				q = q.nondet;
			}
			p = p.list;
		}
		return true;
	}

	public static EventState firstCompState(EventState head, int event, int[] state) {
		EventState p = head;
		while (p != null) {
			if (p.event == event) {
				state[p.machine] = p.next;
				return p.nondet;
			}
			p = p.list;
		}
		return null;
	}

	public static EventState moreCompState(EventState head, int[] state) {
		state[head.machine] = head.next;
		return head.nondet;
	}

	public static boolean hasTau(EventState head) {
		if (head == null)
			return false;
		return (head.event == Declaration.TAU);
	}

	public static boolean hasOnlyTau(EventState head) {
		if (head == null)
			return false;
		// return ((head.getEvent()== Declaration.TAU || head.getEvent()==
		// Declaration.TAU_MAYBE) && head.getList() == null);
		return ((head.event == Declaration.TAU) && head.list == null);
	}

	public static boolean hasOnlyTauAndAccept(EventState head, String[] alphabet) {
		if (head == null)
			return false;
		// if (head.getEvent()!= Declaration.TAU || head.getEvent()!=
		// Declaration.TAU_MAYBE) return false;
		if (head.event != Declaration.TAU)
			return false;
		if (head.list == null)
			return true;
		if (alphabet[head.list.event].charAt(0) != '@')
			return false;
		return (head.list.list == null);
	}

	// precondition is "hasOnlyTauAndAccept"
	public static EventState removeAccept(EventState head) {
		head.list = null;
		return head;
	}

	public static EventState addNonDetTau(EventState head, EventState states[], BitSet tauOnly) {
		EventState p = head;
		EventState toAdd = null;
		while (p != null) {
			EventState q = p;
			while (q != null) {
				if (q.next > 0 && tauOnly.get(q.next)) {
					int nextS[] = nextState(states[q.next], Declaration.TAU);
					q.next = nextS[0]; // replace transition to next
					for (int i = 1; i < nextS.length; ++i) {
						toAdd = EventStateUtils.add(toAdd, new EventState(q.event, nextS[i]));
					}
				}
				q = q.nondet;
			}
			p = p.list;
		}
		if (toAdd == null)
			return head;
		else
			return EventStateUtils.union(head, toAdd);
	}

	public static boolean hasNonDet(EventState head) {
		EventState p = head;
		while (p != null) {
			if (p.nondet != null)
				return true;
			p = p.list;
		}
		return false;
	}

	public static boolean hasNonDetEvent(EventState head, int event) {
		EventState p = head;
		while (p != null) {
			if (p.event == event && p.nondet != null)
				return true;
			p = p.list;
		}
		return false;
	}

	public static int[] localEnabled(EventState head) {
		EventState p = head;
		int n = 0;
		while (p != null) {
			++n;
			p = p.list;
		}
		if (n == 0)
			return null;
		int[] a = new int[n];
		p = head;
		n = 0;
		while (p != null) {
			a[n++] = p.event;
			p = p.list;
		}
		return a;
	}

	public static void hasEvents(EventState head, BitSet actions) {
		EventState p = head;
		while (p != null) {
			actions.set(p.event);
			p = p.list;
		}
	}

	public static int[] nextState(EventState head, int event) {
		EventState p = head;
		while (p != null) {
			if (p.event == event) {
				EventState q = p;
				int size = 0;
				while (q != null) {
					q = q.nondet;
					++size;
				}
				q = p;
				int n[] = new int[size];
				for (int i = 0; i < n.length; ++i) {
					n[i] = q.next;
					q = q.nondet;
				}
				return n;
			}
			p = p.list;
		}
		return null;
	}

	public static EventState renumberEvents(EventState head, Hashtable<Integer, Integer> oldtonew) {
		EventState p = head;
		EventState newhead = null;
		while (p != null) {
			EventState q = p;
			while (q != null) {
				int event = oldtonew.get(q.event);
				EventState child = new EventState(event, q.next);
				if (q instanceof ProbabilisticEventState) {
					ProbabilisticEventState q2 = (ProbabilisticEventState) q;
					child = new ProbabilisticEventState(event, q2.next, q2.getProbability(), q2.getBundle());
//					// System.out.println("CRITICALLY PRESERVING A PROB (eventstate.rennumber)
					// ev="+event+"/nxt="+q2.next+"/p="+q2.getProbability());
					ProbabilisticEventState r = (ProbabilisticEventState) q2.probTr;
					while (r != null) {
//						// System.out.println("AND ANOTHER (eventstate.rennumber)
						// ev="+oldtonew.get(r.event)+"/nxt="+r.next+"/p="+r.getProbability());
						ProbabilisticEventState child2 = new ProbabilisticEventState(oldtonew.get(r.event), r.next,
								r.getProbability(), r.getBundle());
						child = EventStateUtils.add(child, child2);
						r = (ProbabilisticEventState) r.probTr;
					}
				}
				newhead = EventStateUtils.add(newhead, child);
				q = q.nondet;
			}
			p = p.list;
		}
		return newhead;
	}

	public static EventState newTransitions(EventState head, Relation oldtonew) {
		EventState p = head;
		EventState newhead = null;
		while (p != null) {
			EventState q = p;
			while (q != null) {
				Object o = oldtonew.get(new Integer(q.event));
				if (o != null) {
					if (o instanceof Integer) {
						newhead = EventStateUtils.add(newhead, new EventState(((Integer) o).intValue(), q.next));
					} else {
						Vector<Integer> v = (Vector<Integer>) o;
						for (Enumeration<Integer> e = v.elements(); e.hasMoreElements();) {
							newhead = EventStateUtils.add(newhead,
									new EventState(((Integer) e.nextElement()).intValue(), q.next));
						}
					}
				}
				q = q.nondet;
			}
			p = p.list;
		}
		return newhead;
	}

	public static EventState offsetEvents(EventState head, int offset) {
		EventState p = head;
		EventState newhead = null;
		while (p != null) {
			EventState q = p;
			while (q != null) {
				q.event = q.event == 0 ? 0 : q.event + offset;
				q = q.nondet;
			}
			p = p.list;
		}
		return newhead;
	}

	/*
	 * head is a state of the process last represents the size of the alphabet.
	 */
	public static EventState addTransToError(EventState head, int last, BitSet toError) {
		EventState p = head;
		EventState newhead = null;
		// Skip TAU y TAU?
		// p.event==Declaration.TAU_MAYBE is a problem when it's not an MTS!
		// skip tau
		if (p != null && p.event == Declaration.TAU) {
			p = p.list;
		}
		int index = 2;
		// This loops goes through all transitions which are ordered by the index of
		// their labels in the alphabet array.
		// Indexes that are do not appear in p.list correspond to labels that are not
		// enabled on the state.
		// final
		while (p != null) {
			// Here we add transitions to error for all labels with index less than the
			// index of the current transition action (p.event) since they are disabled
			if (index < p.event) {
				for (int i = index; i < p.event; i++)
					if (toError == null || toError.get(i)) {
						newhead = EventStateUtils.add(newhead, new EventState(i, Declaration.ERROR));
					}
			}
			index = p.event + 1;
			EventState q = p;
			// Here we copy the actual transition and all nondeterministic ones.
			while (q != null) {
				// Avoid adding maybes.
				newhead = EventStateUtils.add(newhead, new EventState(q.event, q.next));
				q = q.nondet;
			}
			p = p.list;
		}
		// Here we add transitions to error for all labels with index greater than the
		// index of the last transition in the list (i.e. the state)
		for (int i = index; i < last; i++)
			if (toError == null || toError.get(i)) {
				newhead = EventStateUtils.add(newhead, new EventState(i, Declaration.ERROR));
			}
		return newhead;
	}

	// prcondition - no non-deterministic transitions
	public static EventState removeTransToError(EventState head) {
		EventState p = head;
		EventState newHead = null;
		while (p != null) {
			if (p.next != Declaration.ERROR)
				newHead = EventStateUtils.add(newHead, new EventState(p.event, p.next));
			p = p.list;
		}
		return newHead;
	}

	// remove tau actions
	public static EventState removeTau(EventState head) {
		if (head == null)
			return head;
		if (head.event != Declaration.TAU)
			return head;
		return head.list;
	}

	// agrega al path los estados alcanzables y las acciones por las que se mueve
	// add states reachable by next from events
	public static EventState tauAdd(EventState head, EventState[] T) {
		EventState p = head;
		EventState added = null;
		if (p != null && p.event == Declaration.TAU)
			p = p.list; // skip tau
		while (p != null) {
			EventState q = p;
			while (q != null) {
				if (q.next != Declaration.ERROR) {
					EventState t = T[q.next];
					while (t != null) {
						added = push(added, new EventState(p.event, t.next));
						t = t.nondet;
					}
				}
				q = q.nondet;
			}
			p = p.list;
		}
		while (added != null) {
			head = EventStateUtils.add(head, added);
			added = pop(added);
		}
		return head;
	}

	public static void setActions(EventState head, BitSet b) {
		EventState p = head;
		while (p != null) {
			b.set(p.event);
			p = p.list;
		}
	}

	// add actions reachable by tau
	public static EventState actionAdd(EventState head, EventState[] states) {
		if (head == null || head.event != Declaration.TAU)
			return head; // no tau
		EventState tau = head;
		while (tau != null) {
			if (tau.next != Declaration.ERROR)
				head = EventStateUtils.union(head, states[tau.next]);
			tau = tau.nondet;
		}
		return head;
	}

	// only applicable to a transposed list
	// returns set of event names to next state
	public static String[] eventsToNext(EventState from, String[] alphabet) {
		EventState q = from;
		int size = 0;
		while (q != null) {
			q = q.nondet;
			++size;
		}
		q = from;
		String s[] = new String[size];
		for (int i = 0; i < s.length; ++i) {
			s[i] = alphabet[q.event];
			q = q.nondet;
		}
		return s;
	}

	// only applicable to a transposed list
	// returns set of event names to next state
	// omit accepting label
	public static String[] eventsToNextNoAccept(EventState from, String[] alphabet) {
		EventState q = from;
		int size = 0;
		while (q != null) {
			if (alphabet[q.event].charAt(0) != '@')
				++size;
			q = q.nondet;
		}
		q = from;
		String s[] = new String[size];
		for (int i = 0; i < s.length; ++i) {
			if (alphabet[q.event].charAt(0) != '@')
				s[i] = alphabet[q.event];
			else
				--i;
			q = q.nondet;
		}
		return s;
	}

	/* -------------------------------------------------------------- */
	// Stack using path
	/* -------------------------------------------------------------- */

	public static EventState push(EventState head, EventState es) {
		if (head == null)
			es.path = es;
		else
			es.path = head;
		return head = es;
	}

	public static boolean inStack(EventState es) {
		return (es.path != null);
	}

	public static EventState pop(EventState head) {
		if (head == null)
			return head;
		EventState es = head;
		head = es.path;
		es.path = null;
		if (head == es)
			return null;
		else
			return head;
	}

	/*-------------------------------------------------------------*/
	// compute all states reachable from state k
	/*-------------------------------------------------------------*/
	// lo hace devolviendo transiciones TAU a los estados alcanzables
	public static EventState reachableTau(EventState[] states, int k) {
		EventState head = states[k];
		if (head == null || head.event != Declaration.TAU)
			return null;
		BitSet visited = new BitSet(states.length);
		visited.set(k);
		EventState stack = null;
		while (head != null) {
			stack = push(stack, head);
			head = head.nondet;
		}
		// armo una pila con todos los estados que se llegan desde el estado k
		while (stack != null) {
			int j = stack.next;
			head = EventStateUtils.add(head, new EventState(Declaration.TAU, j));
			stack = pop(stack);
			if (j != Declaration.ERROR) {
				visited.set(j);
				EventState t = states[j];
				if (t != null && t.event == Declaration.TAU)
					while (t != null) {
						if (!inStack(t)) {
							if (t.next < 0 || !visited.get(t.next))
								stack = push(stack, t);
						}
						t = t.nondet;
					}
			}
		}
		return head;
	}

	/* -------------------------------------------------------------- */
	// Queue using path
	/* -------------------------------------------------------------- */

	private static EventState addtail(EventState tail, EventState es) {
		es.path = null;
		if (tail != null)
			tail.path = es;
		return es;
	}

	private static EventState removehead(EventState head) {
		if (head == null)
			return head;
		EventState es = head;
		head = es.path;
		return head;
	}

	/*-------------------------------------------------------------*/
	// breadth first search of states from 0, return trace to deadlock/error
	/*-------------------------------------------------------------*/

	public static int search(EventState trace, EventState[] states, int fromState, int findState, int ignoreState) {
		return search(trace, states, fromState, findState, ignoreState, true);
	}

	public static int search(EventState trace, EventState[] states, int fromState, int findState, int ignoreState,
			boolean checkDeadlocks) {
		EventState zero = new EventState(0, fromState);
		EventState head = zero;
		EventState tail = zero;
		int res = Declaration.SUCCESS;
		// int id = 0;
		EventState val[] = new EventState[states.length + 1]; // shift by 1 so ERROR is 0
		while (head != null) {
			int k = head.next;
			val[k + 1] = head; // the event that got us here
			if (k < 0 || k == findState) {
				if (!checkDeadlocks) {
					res = Declaration.ERROR;
					break;// ERROR
				} else {
					head = removehead(head);
				}
			} else {
				EventState t = states[k];
				if (checkDeadlocks && t == null && k != ignoreState) {
					res = Declaration.STOP;
					break;
				}
				; // DEADLOCK
				while (t != null) {
					EventState q = t;
					while (q != null) {
						if (val[q.next + 1] == null) { // not visited or in queue
							q.machine = k; // backward pointer to source state
							tail = addtail(tail, q);
							val[q.next + 1] = zero;
						}
						q = q.nondet;
					}
					t = t.list;
				}
				head = removehead(head);
			}
		}
		if (head == null)
			return res;
		EventState stack = null;
		EventState ts = head;
		while (ts.next != fromState) {
			stack = push(stack, ts);
			ts = val[ts.machine + 1];
		}
		trace.path = stack;
		return res;
	}

	/*-------------------------------------------------------------*/
	// print a path of EventStates
	/*-------------------------------------------------------------*/
	public static void printPath(EventState head, String[] alpha, LTSOutput output) {
		EventState q = head;
		while (q != null) {
			output.outln("\t" + alpha[q.event]);
			q = pop(q);
		}
	}

	public static Vector<String> getPath(EventState head, String[] alpha) {
		EventState q = head;
		Vector<String> v = new Vector<String>();
		while (q != null) {
			v.addElement(alpha[q.event]);
			q = pop(q);
		}
		return v;
	}

	public void setList(EventState list) {
		if (this.list == null)
			this.list = list;
		else
			this.list.setList(list);
	}

	public EventState getList() {
		return this.list;
	}
}

final class EventStateEnumerator implements Enumeration<EventState> {
	EventState es;
	EventState list;

	EventStateEnumerator(EventState es) {
		this.es = es;
		if (es != null)
			list = es.list;
	}

	public boolean hasMoreElements() {
		return es != null;
	}

	public EventState nextElement() {
		if (es != null) {
			EventState temp = es;
			// if (es instanceof ProbabilisticEventState &&
			// ((ProbabilisticEventState) es).probTr != null) {
			// // TODO navigate probabilistic transitions first
			// } else

			if (es.nondet != null)
				es = es.nondet;
			else {
				es = list;
				if (es != null)
					list = list.list;
			}
			return temp;
		}
		throw new NoSuchElementException("EventStateEnumerator");
	}

	// to = to U from
	//Agrega al to, todos los eventState a los que llega el from 
	public static EventState union(EventState to, EventState from){
		EventState res = to;
		EventState p =from;
		while(p!=null) {
			EventState q = p;
			while(q!=null) {
				EventState evSt;
				if (q instanceof ProbabilisticEventState) {
					ProbabilisticEventState probQ= (ProbabilisticEventState) q;
					evSt= new ProbabilisticEventState(probQ.event, probQ.next, probQ.getProbability(), probQ.getBundle());
					res= EventStateUtils.add(res, evSt);

					ProbabilisticEventState probSt= (ProbabilisticEventState) probQ.probTr;
					while (probSt != null) {
						evSt= new ProbabilisticEventState(probSt.event, probSt.next, probSt.getProbability(), probSt.getBundle());
						res= EventStateUtils.add(res, evSt);
						probSt= (ProbabilisticEventState) probSt.probTr;
					}
				} else {
					evSt= new EventState(q.event, q.next);
					res= EventStateUtils.add(res, evSt);
				}

				q=q.nondet;
			}
			p =  p.list;
		}
		return res;
	}
}