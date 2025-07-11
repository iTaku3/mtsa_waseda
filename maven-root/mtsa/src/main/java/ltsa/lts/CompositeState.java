package ltsa.lts;

import static ltsa.lts.util.MTSUtils.computeHiddenAlphabet;

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import MTSSynthesis.controller.model.ControllerGoal;
import MTSTools.ac.ic.doc.mtstools.model.operations.DCS.blocking.Statistics;
import ltsa.dispatcher.TransitionSystemDispatcher;

public class CompositeState{

	public static boolean reduceFlag = true;

	public String name;
	public CompactState env; //CompactState 
	public Vector<CompactState> machines; // set of CompactState from which this
										// can be composed
	public CompactState composition; // the result of a composition;
	public Vector<String> hidden; // set of actions concealed in composed
									// version
	public boolean exposeNotHide = false; // expose rather than conceal
	public boolean priorityIsLow = true;
	public boolean makeDeterministic = false; // construct equivalent DFA if
												// NDFA
	public boolean makeOptimistic = false;
	public boolean makeAbstract = false;
	public boolean makeClousure = false;
	public boolean makePessimistic = false;
	public boolean makeMinimal = false;
	public boolean makeCompose = false; // force composition if true
	public boolean isProperty = false;
	public boolean makeController = false;
	public boolean makeRTCController = false;
	public boolean makeRTCAnalysisController = false;
	public boolean checkCompatible = false;
	public boolean isStarEnv = false;
	public boolean isPlant = false;
	public boolean isControlledDet = false;
	public boolean makeMDP = false;
	public boolean makeEnactment = false;
	public boolean makeControlStack = false;
	public boolean isHeuristic = false;
	public boolean isMonolithicDirector = false;
	public boolean isPartialOrderReduction = false;
	public Vector<String> actionsToErrorSet;
	
	public Hashtable<String,Object> controlStackEnvironments;
	public int controlStackSpecificTier = -1;
	public List<String> enactmentControlled;
	    
	public boolean isProbabilistic = false;
	private int compositionType = -1;
	public Vector<String> priorityLabels; // set of actions given priority
	public CompactState alphaStop; // stop process with alphbet of the
									// composition
	protected Vector<String> errorTrace = null;
	public ControllerGoal<String> goal;

	/**
	 * If the isComponent flag is true, then this ProcessSpec represents a
	 * composition of several component processes. The component process can be
	 * built with the componentAlphabet.
	 */
	private boolean makeComponent = false;

	/**
	 * this alphabet is one of the component. It must be a subset of the process
	 * alphabet.
	 */
	private Vector<String> componentAlphabet;

	public CompositeState(){
		
	}
	
	public boolean isMakeComponent() {
		return makeComponent;
	}

	public void setMakeComponent(boolean makeComponent) {
		this.makeComponent = makeComponent;
	}

	public Vector<String> getComponentAlphabet() {
		return componentAlphabet;
	}

	public void setComponentAlphabet(Vector<String> componentAlphabet) {
		this.componentAlphabet = componentAlphabet;
	}

	public CompositeState(Vector<CompactState> v) {
		name = "DEFAULT";
		machines = v;
	}

	public CompositeState(String s, Vector<CompactState> v) {
		name = s;
		machines = v;
		initAlphaStop();
	}

	public Vector<String> getErrorTrace() {
		return errorTrace;
	}

	public void setErrorTrace(List<String> ll) {
		if (ll != null) {
			errorTrace = new Vector<String>();
			errorTrace.addAll(ll);
		}
	}

	public void compose(LTSOutput output) {
		compose(output, false);
	}

	public void compose(LTSOutput output, boolean ignoreAsterisk) {
		if (machines != null && machines.size() > 0) {
			for (Object o : machines) {
				if (isProbabilisticMachine((CompactState) o)) {
					isProbabilistic = true;
					break;
				}
			}
			if (isProbabilistic) {
				for (int j = 0; j < this.machines.size(); j++) {
					CompactState cs = this.machines.get(j);
					this.machines.set(j, CompactStateUtils.convertToProbabilistic(cs));
				}
			}
			Analyser a = new Analyser(this, output, null, ignoreAsterisk);
			composition = a.composeNoHide();
			this.applyLTSOperations(output);
		}
	}

	private boolean isProbabilisticMachine(CompactState cs) {
		for (EventState evSt : cs.states) {
			if (evSt instanceof ProbabilisticEventState)
				return true;
		}

		return false;
	}

	public void applyLTSOperations(LTSOutput output) {
		if (!makeDeterministic && !makeMinimal) {
			applyHiding();
		}

		/*if (isProperty) //restored by das05, and removed in merge ***
        {
          TransitionSystemDispatcher.makeProperty(this, output);
          applyHiding();
        }*/

		// if (makeDeterministic) {
		// applyHiding();
		// TransitionSystemDispatcher.determinise(this, output);
		// } else if (makeMinimal) {
		// applyHiding();
		// TransitionSystemDispatcher.minimise(this, output);
		// } else {
		// applyHiding();
		// }
	}

	public void applyOperations(LTSOutput output, Statistics...stats) {
		if (this.makeComponent) {
			applyHiding();
			TransitionSystemDispatcher.makeComponentModel(this, output);
		}
		if (makeDeterministic) {
			applyHiding();
			TransitionSystemDispatcher.determinise(this, output);
		}
		if (makeMinimal) {
			applyHiding();
			TransitionSystemDispatcher.minimise(this, output);
		}
		if (makeOptimistic) {
			TransitionSystemDispatcher.makeOptimisticModel(this, output);
			applyHiding();
		}
		if (makePessimistic) {
			TransitionSystemDispatcher.makePessimisticModel(this, output);
			applyHiding();
		}
		if (makeClousure) {
			TransitionSystemDispatcher.makeClosureModel(this, output);
			applyHiding();
		}
		if (makeAbstract) {
			TransitionSystemDispatcher.makeAbstractModel(this, output);
			applyHiding();
		}
		if (makeController && !checkCompatible) {
			TransitionSystemDispatcher.synthesise(this, output);
			applyHiding();
		}
		if (makeRTCController) {
			TransitionSystemDispatcher.synthesiseRTCController(this, output);
			applyHiding();
		}
		if (makeRTCAnalysisController) {
			TransitionSystemDispatcher.synthesiseRTCAnalysisController(this, output);
			applyHiding();
		}
        if (makeControlStack) {
        	TransitionSystemDispatcher.synthesiseControlStack(this, output);
        	applyHiding();
        }
        if (isHeuristic) {
        	TransitionSystemDispatcher.hcs(this, output, stats);
        	applyHiding();
        }
		if (isMonolithicDirector) {
			TransitionSystemDispatcher.monolithicDirectorSynthesis(this, output, stats);
			applyHiding();
		}
		if (isPartialOrderReduction) {
			TransitionSystemDispatcher.partialOrderReductionSynthesis(this, output, stats);
			//invocar a mi funcion de sintesis que usa POR
			applyHiding();
		}
        /*if (isProperty) //removed in merge ***
        {
          TransitionSystemDispatcher.makeProperty(this, output);
          applyHiding();
        }*/

		if (isStarEnv) {
			TransitionSystemDispatcher.makeStarEnv(this, output);
			applyHiding();
		}
		if (isPlant) {
			TransitionSystemDispatcher.makePlant(this, output);
			applyHiding();
		}
		if (isControlledDet) {
			TransitionSystemDispatcher.makeControlledDeterminisation(this, output);
			applyHiding();
		}
		if (isProperty) {
			TransitionSystemDispatcher.makeProperty(this, output);
			applyHiding();
		}
		if (checkCompatible) {
			TransitionSystemDispatcher.checkCompatible(this, output);
			applyHiding();		
		} else {
			applyHiding();
		}
		
		
	}

	public void applyOperationsNoText(LTSOutput output) {
		if (this.makeComponent) {
			applyHiding();
			TransitionSystemDispatcher.makeComponentModel(this, output);
		}
		if (makeDeterministic) {
			applyHiding();
			TransitionSystemDispatcher.determinise(this, output);
		}
		if (makeMinimal) {
			applyHiding();
			TransitionSystemDispatcher.minimise(this, output);
		}
		if (makeOptimistic) {
			TransitionSystemDispatcher.makeOptimisticModel(this, output);
			applyHiding();
		}
		if (makePessimistic) {
			TransitionSystemDispatcher.makePessimisticModel(this, output);
			applyHiding();
		}
		if (makeClousure) {
			TransitionSystemDispatcher.makeClosureModel(this, output);
			applyHiding();
		}
		if (makeAbstract) {
			TransitionSystemDispatcher.makeAbstractModel(this, output);
			applyHiding();
		}
		if (makeController && !checkCompatible) {
			TransitionSystemDispatcher.synthesiseGRNoText(this, output);
			applyHiding();
		}
		if (makeRTCController) {
			TransitionSystemDispatcher.synthesiseRTCController(this, output);
			applyHiding();
		}
		if (makeRTCAnalysisController) {
			TransitionSystemDispatcher.synthesiseRTCAnalysisController(this, output);
			applyHiding();
		}
		if (makeControlStack) {
			TransitionSystemDispatcher.synthesiseControlStack(this, output);
			applyHiding();
		}

        /*if (isProperty) //removed in merge ***
        {
          TransitionSystemDispatcher.makeProperty(this, output);
          applyHiding();
        }*/

		if (isStarEnv) {
			TransitionSystemDispatcher.makeStarEnv(this, output);
			applyHiding();
		}
		if (isPlant) {
			TransitionSystemDispatcher.makePlant(this, output);
			applyHiding();
		}
		if (isControlledDet) {
			TransitionSystemDispatcher.makeControlledDeterminisation(this, output);
			applyHiding();
		}
		if (isProperty) {
			TransitionSystemDispatcher.makeProperty(this, output);
			applyHiding();
		}
		if (checkCompatible) {
			TransitionSystemDispatcher.checkCompatible(this, output);
			applyHiding();
		} else {
			applyHiding();
		}


	}

	private void applyHiding() {
		if (composition == null)
			return;
		if (hidden != null) {
			computeHiddenAlphabet(hidden);
			if (!exposeNotHide)
				composition.conceal(hidden);
			else
				composition.expose(hidden);
		}
	}

	public void analyse(boolean checkDeadlocks, LTSOutput output) {
		if (saved != null) {
			machines.remove(saved);
			saved = null;
		}
		if (composition != null) {
			CounterExample ce = new CounterExample(this);
			ce.print(output, checkDeadlocks);
			errorTrace = ce.getErrorTrace();
		} else {
			Analyser a = new Analyser(this, output, null);
			a.analyse(checkDeadlocks);
			this.setErrorTrace(a.getErrorTrace());
		}
	}

	public void checkProgress(LTSOutput output) {
		ProgressCheck cc;
		if (saved != null) {
			machines.remove(saved);
			saved = null;
		}
		if (composition != null) {
			cc = new ProgressCheck(composition, output);
			cc.doProgressCheck();
		} else {
			Automata a = new Analyser(this, output, null);
			cc = new ProgressCheck(a, output);
			cc.doProgressCheck();
		}
		errorTrace = cc.getErrorTrace();
	}

	private CompactState saved = null;

	public void checkLTL(LTSOutput output, CompositeState cs) {
		CompactState ltl_property = cs.composition;
		if (name.equals("DEFAULT") && machines.size() == 0) {
			// debug feature for producing consituent machines
			machines = cs.machines;
			composition = cs.composition;
		} else {
			if (saved != null)
				machines.remove(saved);
			Vector<String> saveHidden = hidden;
			boolean saveExposeNotHide = exposeNotHide;
			hidden = ltl_property.getAlphabetV();
			exposeNotHide = true;
			machines.add(saved = ltl_property);
			Analyser a = new Analyser(this, output, null);
			if (!cs.composition.hasERROR()) {
				// do full liveness check
				ProgressCheck cc = new ProgressCheck(a, output, cs.tracer);
				cc.doLTLCheck();
				errorTrace = cc.getErrorTrace();
			} else {
				// do safety check
				a.analyse(cs.tracer);
				setErrorTrace(a.getErrorTrace());
			}
			hidden = saveHidden;
			exposeNotHide = saveExposeNotHide;
		}
	}

	public void minimise(LTSOutput output) {
		if (composition != null) {
			// change (a ->(tau->P|tau->Q)) to (a->P | a->Q) and (a->tau->P) to
			// a->P
			if (reduceFlag)
				composition.removeNonDetTau();
			Minimiser e = new Minimiser(composition, output);
			composition = e.minimise();
		}
	}

	public void determinise(LTSOutput output) {
		if (composition != null) {
			Minimiser d = new Minimiser(composition, output);
			composition = d.trace_minimise();
			if (isProperty)
				composition.makeProperty(this.actionsToErrorSet);
		}
	}

	public CompactState create(LTSOutput output) {
		TransitionSystemDispatcher.applyComposition(this, output);
		return composition;
	}

	public boolean compositionNotRequired() {
		return (hidden == null && priorityLabels == null && !makeDeterministic
				&& !makeMinimal && !makeCompose && !makeController && !makeRTCController && !makeRTCAnalysisController);
	}

	/*
	 * prefix all constituent machines
	 */
	public void prefixLabels(String prefix) {
    name = prefix+":"+name;
    alphaStop.prefixLabels(prefix);
    for (CompactState mm : machines) {
        mm.prefixLabels(prefix);
    }
}

	/*
	 * add prefix set to all constitutent machines
	 */
	public void addAccess(Vector<String> pset) {
    int n = pset.size();
    if (n==0) return;
    String s = "{";
    int i =0;
    for (String prefix : pset) {
        s = s + prefix;
        i++;
        if (i<n) s = s+",";
    }
    //new name
    name = s+"}::"+name;
    alphaStop.addAccess(pset);
    for (CompactState mm : machines) {
        mm.addAccess(pset);
    }
	}

	/*
	 * relabel all constituent machines checks to see if it is safe to leave
	 * uncomposed if a relabeling causes synchronization, then the composition
	 * is formed before relabelling is applied
	 */
	public CompactState relabel(Relation oldtonew, LTSOutput output) {
    alphaStop.relabel(oldtonew);
    if (alphaStop.relabelDuplicates() && machines.size()>1) {
        // we have to do the composition, before relabelling
        TransitionSystemDispatcher.applyComposition(this, output);
        composition.relabel(oldtonew);
        return composition;
    } else {
        for (CompactState mm : machines) {
            mm.relabel(oldtonew);
        }
    }
    return null;
	}

	/*
	 * initialise the alphaStop process
	 */
	protected void initAlphaStop() {
    alphaStop = new CompactState();
    alphaStop.name = name;
    alphaStop.maxStates = 1;
    alphaStop.states = new EventState[alphaStop.maxStates]; // statespace for STOP process
    alphaStop.states[0] = null;
    // now define alphabet as union of constituents
    Hashtable<String,String> alpha = new Hashtable<String,String>();
    for (CompactState m  : machines) {
        for (int i=1; i<m.alphabet.length; ++i)
            alpha.put(m.alphabet[i],m.alphabet[i]);
    }
    alphaStop.alphabet = new String[alpha.size()+1];
    alphaStop.alphabet[0] = "tau";
    int j =1;
    for (String s : alpha.keySet()) {
        alphaStop.alphabet[j] = s;
        ++j;
    }
	}

	private ltsa.lts.ltl.FluentTrace tracer;


	public void setFluentTracer(ltsa.lts.ltl.FluentTrace ft) {
		tracer = ft;
	}

	public ltsa.lts.ltl.FluentTrace getFluentTracer() {
		return tracer;
	}

	public CompactState getComposition() {

		return composition;
	}

	public Vector<CompactState> getMachines() {

		return machines;
	}

	// >>> AMES: Enhanced modularity
	public String getName() {
		return name;
	}

	// <<< AMES

	public void setComposition(CompactState compactSate) {
		this.composition = compactSate;
	}

	public void setReduction(boolean b) {

		reduceFlag = b;
	}

	public void setMachines(Vector<CompactState> machines) {
		this.machines = machines;
	}

	public void setCompositionType(int compositionType) {
		this.compositionType = compositionType;
	}

	public int getCompositionType() {
		return compositionType;
	}

	public String toString() {
		return super.toString();
	}

	@Override
	public CompositeState clone()
	{
	  CompositeState c = new CompositeState(getName(), machines);
	  c.setCompositionType(getCompositionType());
	  c.makeAbstract = makeAbstract;
    c.makeClousure = makeClousure;
    c.makeCompose = makeCompose;
    c.makeDeterministic = makeDeterministic;
    c.makeMinimal = makeMinimal;
    c.makeControlStack = makeControlStack;
    c.makeOptimistic = makeOptimistic;
    c.makePessimistic = makePessimistic;
    c.makeController = makeController;
    c.setMakeComponent(isMakeComponent());
    c.setComponentAlphabet(getComponentAlphabet());
    c.goal = goal;
    c.controlStackEnvironments = controlStackEnvironments;
    c.controlStackSpecificTier = controlStackSpecificTier;
    c.isProbabilistic= isProbabilistic;
    c.isHeuristic = isHeuristic;
    c.isMonolithicDirector = isMonolithicDirector;
	c.isPartialOrderReduction = isPartialOrderReduction;
	  return c;
	}
	
	/*//was being used as a clone
	public void setFlags(CompositeState cs) {
		this.makeAbstract = cs.makeAbstract;
		this.makeClousure = cs.makeClousure;
		this.makeCompose = cs.makeCompose;
		this.makeDeterministic = cs.makeDeterministic;
		this.makeMinimal = cs.makeMinimal;
		this.makeControlStack = cs.makeControlStack;
		this.makeOptimistic = cs.makeOptimistic;
		this.makePessimistic = cs.makePessimistic;
		this.makeController = cs.makeController;
		this.makeSyncController = cs.makeSyncController;
		this.setMakeComponent(cs.isMakeComponent());
		this.setComponentAlphabet(cs.getComponentAlphabet());
		this.goal = cs.goal;
		this.isProbabilistic = cs.isProbabilistic;
		this.controlStackEnvironments = cs.controlStackEnvironments;
		this.controlStackSpecificTier = cs.controlStackSpecificTier;
	}*/
}
