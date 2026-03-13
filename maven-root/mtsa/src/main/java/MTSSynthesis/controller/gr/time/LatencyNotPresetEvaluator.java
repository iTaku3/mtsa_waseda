package MTSSynthesis.controller.gr.time;

import MTSSynthesis.controller.gr.time.comparator.ControllerComparator;
import MTSSynthesis.controller.gr.time.comparator.ControllerPairsComparator;
import MTSSynthesis.controller.gr.time.model.ActivityDefinitions;
import MTSSynthesis.controller.gr.time.model.ComparatorPool;
import MTSSynthesis.controller.gr.time.utils.PruningUtils;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;
import MTSTools.ac.ic.doc.mtstools.model.impl.LTSAdapter;
import com.microsoft.z3.Z3Exception;

import java.util.Set;

public class LatencyNotPresetEvaluator<S,A> extends LatencyEvaluator<Pair<S,S>,A,S> {
	
	protected LTS<S,A> realEnvironment;
	protected SchedulerGenerator<S, A> schedulerGenerator;
	protected PruningUtils<S,A> utils;
	public LatencyNotPresetEvaluator(MTS<Pair<S,S>, A> heuristic, MTS<Pair<S,S>, A> maximalControllerUsingGR1,Set<Pair<S,S>> heuristicFinalStates, Set<Pair<S,S>> perfectFinalStates, ActivityDefinitions<A> activityDefinitions, Translator<S, Pair<S,S>> translator, Set<A> controllableAction, LTS<S,A> realEnvironment, Integer maxSchedulers) {
		super(heuristic, maximalControllerUsingGR1, heuristicFinalStates, perfectFinalStates, activityDefinitions, translator, controllableAction, maxSchedulers, realEnvironment);
		this.utils = new PruningUtils<S,A>();
	}

	protected GenericChooser<S, A, Pair<S,S>> getScheduler(){
		return schedulerGenerator.next();
	}

	private Set<GenericChooser<S, A, Pair<S, S>>> generateSchedulers(Integer maxSchedulers, LTS<S, A> environment, Set<A> controllableActions, ActivityDefinitions<A> activityDefinitions) {
		SchedulerGenerator<S, A> schedulerGenerator = new SchedulerGenerator<S,A>(environment, controllableActions, activityDefinitions);
//		System.out.println("Estimation: " + schedulerGenerator.getEstimation());
		GenericChooser<S, A, Pair<S,S>> scheduler = schedulerGenerator.next();
		int i = 0;
		while(scheduler!=null && i< maxSchedulers){
			scheduler = schedulerGenerator.next();
			i++;
		}
		return schedulerGenerator.getGenerated();
	}

	@Override
	protected Set<GenericChooser<S, A, Pair<S, S>>> getSchedulers(MTS<Pair<S, S>, A> result1, MTS<Pair<S, S>, A> result2) {
		LTS<S,A> prunedEnvironment = utils.pruneRealEnvironment(new LTSAdapter<Pair<S,S>,A>(result1,TransitionType.REQUIRED), new LTSAdapter<Pair<S,S>,A>(result2,TransitionType.REQUIRED), realEnvironment);
		return generateSchedulers(maxSchedulers,prunedEnvironment,controllableActions,activityDefinitions);
	}

	@Override
	protected void preCalculateGamaForHeuristic() throws Z3Exception {
		
	}

	@Override
	protected ControllerComparator<S, A, Pair<S, S>> getControllerComparator(int i,Set<ControllerChooser<Pair<S, S>, A>> controllerChooser, ResultCounter stats,
			MTS<Pair<S, S>, A> maximalController, Set<Pair<S, S>> perfectFinalStates,
			MTS<Pair<S, S>, A> result2, Set<Pair<S, S>> heuristicFinalStates,
			Integer maxSchedulers, Set<A> controllableActions,
			ActivityDefinitions<A> activityDefinitions,
			LTS<S, A> realEnvironment, Translator<S, Pair<S, S>> translator, ComparatorPool<A, Pair<S,S>> comparator, Integer maxThreads) {
		return  new ControllerPairsComparator<S,A>(i, stats, controllerChooser, maximalController, perfectFinalStates, result2, heuristicFinalStates,maxSchedulers, controllableActions, activityDefinitions,realEnvironment, translator, comparator, maxThreads);
	}

}