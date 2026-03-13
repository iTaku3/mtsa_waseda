package MTSSynthesis.controller.gr.time;

import java.util.Set;

import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS;

import com.microsoft.z3.Z3Exception;

import MTSSynthesis.controller.gr.time.comparator.ControllerComparator;
import MTSSynthesis.controller.gr.time.model.ActivityDefinitions;
import MTSSynthesis.controller.gr.time.model.ComparatorPool;

public class LatencyPresetEvaluator<P,A,S> extends LatencyEvaluator<P,A,S> {
	protected Set<GenericChooser<S, A, P>> schedulers;
	
	public LatencyPresetEvaluator(MTS<P, A> heuristic, MTS<P, A> maximalControllerUsingGR1,Set<P> heuristicFinalStates, Set<P> perfectFinalStates, ActivityDefinitions<A> activityDefinitions, Translator<S, P> translator, Set<A> controllableAction, Set<GenericChooser<S, A, P>> schedulers, LTS<S,A> realEnvironment,Integer maxSchedulers) {
		super(heuristic, maximalControllerUsingGR1, heuristicFinalStates, perfectFinalStates, activityDefinitions, translator, controllableAction, maxSchedulers, realEnvironment);
		this.schedulers = schedulers;
	}

	protected Set<GenericChooser<S, A, P>> getSchedulers() {
		return schedulers;
	}
	
	protected void preCalculateGamaForHeuristic() throws Z3Exception {
//		heuristicGamas = new HashMap<GenericChooser<S,A,P>,BoolExpr>();
//		for (GenericChooser<S,A,P> scheduler : getSchedulers()) {
//			MTS<P, A> result_2_schedulled = new MTSAdapter<P,A>(scheduler.applyTo(new LTSAdapter<P,A>(result2, TransitionType.REQUIRED),translator));
//			BoolExpr gama2 = comparator.calculateGama(result_2_schedulled , END_TO_END_NAME_2, result2, controllableActions, heuristicFinalStates);
//			if(gama2 == null){
//				throw new RuntimeException("Error generating Gama of the scheduled heuristic solution");
//			}
//			heuristicGamas.put(scheduler, gama2);
//		}
	}

	@Override
	protected Set<GenericChooser<S, A, P>> getSchedulers(MTS<P, A> result1, MTS<P, A> result2) {
		return getSchedulers();
	}
	
	@Override
	protected GenericChooser<S, A, P> getScheduler() {
		//TODO To be implemented if needed.
		throw new RuntimeException("Method not implemented");
	}

	@Override
	protected ControllerComparator<S, A, P> getControllerComparator(int i,
			Set<ControllerChooser<P, A>> controllerChooser,
			ResultCounter stats, MTS<P, A> result1, Set<P> perfectFinalStates2,
			MTS<P, A> result2, Set<P> heuristicFinalStates2,
			Integer maxSchedulers2, Set<A> controllableActions2,
			ActivityDefinitions<A> activityDefinitions2,
			LTS<S, A> realEnvironment2, Translator<S, P> translator2,
			ComparatorPool<A, P> comparator2, Integer maxThreads) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Method not implemented");
	}

}
