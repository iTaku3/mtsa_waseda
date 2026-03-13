package ltsa.lts;

import java.io.PrintStream;
import java.util.Hashtable;

/**
 * Carried away EventState class specific methods from EventState to account for subclasses
 * @author epavese
 *
 */

public class EventStateUtils {
	public static EventState renumberStates(EventState head, Hashtable oldtonew) {
		EventState p= head;
		EventState newhead= null;
		while (p != null) {
			EventState q= p;
			while (q != null) {
				int next= q.next < 0 ? Declaration.ERROR : ((Integer) oldtonew
						.get(new Integer(q.next))).intValue();
				if (q instanceof ProbabilisticEventState) {
					newhead= EventStateUtils.add(newhead, new ProbabilisticEventState(q.event, next, ((ProbabilisticEventState) q).getProbability(),
																				 ((ProbabilisticEventState) q).getBundle()));
					ProbabilisticEventState probSt= (ProbabilisticEventState) ((ProbabilisticEventState)q).probTr;
					while (probSt != null) {
						next= probSt.next < 0 ? Declaration.ERROR : ((Integer) oldtonew.get(new Integer(probSt.next))).intValue();
						newhead= EventStateUtils.add(newhead, new ProbabilisticEventState(probSt.event, next,
													 ((ProbabilisticEventState) probSt).getProbability(),
													 ((ProbabilisticEventState) probSt).getBundle()));
						probSt= (ProbabilisticEventState) probSt.probTr;
					}
				}
				else {
					newhead= EventStateUtils.add(newhead, new EventState(q.event, next));
				}
				q= q.nondet;
			}
			p= p.list;
		}
		return newhead;
	}

	public static EventState renumberStates(EventState head, MyIntHash oldtonew) {
		EventState p= head;
		EventState newhead= null;
		while (p != null) {
			EventState q= p;
			while (q != null) {
				int next= q.next < 0 ? Declaration.ERROR : oldtonew.get(q.next);
				if (q instanceof ProbabilisticEventState) {
					newhead= EventStateUtils.add(newhead, new ProbabilisticEventState(q.event, next, ((ProbabilisticEventState) q).getProbability(),
							 													((ProbabilisticEventState) q).getBundle()));
					ProbabilisticEventState probSt= (ProbabilisticEventState) ((ProbabilisticEventState)q).probTr;
					while (probSt != null) {
						next= probSt.next < 0 ? Declaration.ERROR : oldtonew.get(probSt.next);
						newhead= EventStateUtils.add(newhead, new ProbabilisticEventState(probSt.event, next,
													 ((ProbabilisticEventState) probSt).getProbability(),
													 ((ProbabilisticEventState) probSt).getBundle()));
						probSt= (ProbabilisticEventState) probSt.probTr;
					}
				} else {
					newhead= EventStateUtils.add(newhead, new EventState(q.event, next));
				}
				q= q.nondet;
			}
			p= p.list;
		}
		return newhead;
	}

	/*----------------------------------------------------------------*/
	/*
	 * depth first Search to return set of reachable states
	 * /*----------------------------------------------------------------
	 */

	public static MyIntHash reachable(EventState[] states) {
		int ns= 0; // newstate
		MyIntHash visited= new MyIntHash(states.length);
		EventState stack= null;
		stack= EventState.push(stack, new EventState(0, 0));
		while (stack != null) {
			int v= stack.next;
			stack= EventState.pop(stack);
			if (!visited.containsKey(v)) {
				visited.put(v, ns++);
				EventState p= states[v];
				while (p != null) {
					EventState q= p;
					while (q != null) {
						if (q.next >= 0 && !visited.containsKey(q.next))
							stack= EventState.push(stack, q);
						
						if (q instanceof ProbabilisticEventState) {
							ProbabilisticEventState probSt= (ProbabilisticEventState) ((ProbabilisticEventState) q).probTr;
							while (probSt != null) {
								if (probSt.next >= 0 && !visited.containsKey(probSt.next))
									stack= EventState.push(stack, probSt);
								probSt= (ProbabilisticEventState) probSt.probTr;
							}
						}

						q= q.nondet;
					}
					p= p.list;
				}
			}
		}
		return visited;
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

    // the following is not very OO but efficient
    // duplicates are discarded
    public static EventState add(EventState head, EventState tr) {
    	// list is ordered by events
    	// branches may spawn on a given EventState:
    	//	-- nondet branches for transitions under the same event
    	//  -- prob branches for transitions on the same prob bundle (only ProbabilisticEventState)
        if (head==null || tr.event<head.event) {
            tr.list = head;
            return tr;
        }

        EventState p= head;
        while (p.list != null && p.event != tr.event && tr.event >= p.list.event)
        	p=p.list;

        if (p.event == tr.event) {
        	// check if probabilistic
        	if (tr instanceof ProbabilisticEventState) {
        		// p must also be ProbabilisticEventState
        		ProbabilisticEventState newP, newTr;
        		try {
        			newP= (ProbabilisticEventState) p;
        			newTr= (ProbabilisticEventState) tr;
                	// check if it is for existing bundle
        			while (newP != null && newP.event == newTr.event && newTr.getBundle() != newP.getBundle()) {
        				newP= (ProbabilisticEventState) newP.nondet;
        			}
        			
        			if (newP != null) {
        				// it is a known bundle
        				newTr.probTr= newP.probTr;
        				newP.probTr= newTr;
        			} else {
        				// is a new bundle, a nondet transition on event
        				newTr.nondet= p.nondet;
        				p.nondet= newTr;
        			}
        		} catch (ClassCastException e) {
        			Diagnostics.fatal("Probabilistic transitions expected", e);
        		}
        	} else {
        		// add to nondet
        		EventState q = p;
        		if (q.next == tr.next) return head;
        		while(q.nondet!=null) {
        			q=q.nondet;
        			if (q.next == tr.next) return head;
        		}
        		q.nondet=tr;
        	}
        } else {    // unknown event, add after p
        	tr.list = p.list;
        	p.list = tr;
        }

        return head;
    }
    
    //normally, EventState lists are sorted by event with
    //the nondet list containing lists of different next states
    // for the same event
    // transpose creates a new list sorted by next
    public static EventState transpose(EventState from) {
        EventState res = null;
        EventState p =from;
        while(p!=null) {
            EventState q = p;
            while(q!=null) {
                if (q instanceof ProbabilisticEventState) {
                	ProbabilisticEventState probQ= (ProbabilisticEventState) q;
                	do {
                		res= EventStateUtils.add(res, new ProbabilisticEventState(probQ.next, probQ.event,
                											probQ.getProbability(), probQ.getBundle()));
                    	probQ= (ProbabilisticEventState) probQ.probTr;
                	} while (probQ != null);
                } else {
                	res = EventStateUtils.add(res,new EventState(q.next,q.event)); //swap event & next
                }
                q=q.nondet;
            }
            p =  p.list;
        }
        // now walk through the list a swap event & next back again
        p =res;
        while(p!=null) {
            EventState q = p;
            while (q!=null) {
                int n = q.next; q.next=q.event; q.event =n;
                if (q instanceof ProbabilisticEventState) {
                	ProbabilisticEventState probQ= (ProbabilisticEventState) q;
                	probQ= (ProbabilisticEventState) probQ.probTr;
                	while (probQ != null) {
                		n= probQ.next; probQ.next= probQ.event; probQ.event= n;
                		probQ= (ProbabilisticEventState) probQ.probTr;
                	}
                }
                q=q.nondet;
            }
            p=p.list;
        }
        return res;
    }
    
    public static void printAUT(EventState head, int from, String[] alpha, PrintStream output) {
    	EventState p =head;
    	ProbabilisticEventState probP;
        while(p!=null) {
            EventState q = p;
            while (q!=null) {

                if (q instanceof ProbabilisticEventState) {
            		probP= (ProbabilisticEventState) q;
            		while (probP != null) {
	                	output.print("(" + from + "," + alpha[probP.event] + "{" + probP.getBundle() + ":" + probP.getProbability() + "}," +
	                				 probP.next + ")\n");
	                	probP= (ProbabilisticEventState) probP.probTr;
            		}
            		
                } else {
                	output.print("(" + from + "," + alpha[q.event] + "," + q.next + ")\n");                	
                }
                q=q.nondet;
            }
            p=p.list;
        }
    }

    public static int count(EventState head) {
        EventState p= head;
        int n =0;
        while (p != null) {
            EventState q= p;
            while (q!=null) {
                n++;
                if (q instanceof ProbabilisticEventState) {
                	ProbabilisticEventState probP= (ProbabilisticEventState) q;
                	probP= (ProbabilisticEventState) probP.probTr;
                	while (probP != null) {
                		n++;
                		probP= (ProbabilisticEventState) probP.probTr;
                	}
                }
                q=q.nondet;
            }
            p=p.list;
        }
        return n;
    }

}
