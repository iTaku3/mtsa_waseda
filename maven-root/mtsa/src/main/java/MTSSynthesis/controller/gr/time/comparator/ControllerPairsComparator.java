package MTSSynthesis.controller.gr.time.comparator;

import MTSSynthesis.controller.gr.time.*;
import MTSSynthesis.controller.gr.time.model.ActivityDefinitions;
import MTSSynthesis.controller.gr.time.model.ComparatorPool;
import MTSSynthesis.controller.gr.time.model.Result;
import MTSSynthesis.controller.gr.time.utils.PruningUtils;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;
import MTSTools.ac.ic.doc.mtstools.model.impl.LTSAdapter;
import com.microsoft.z3.Z3Exception;

import java.util.Map;
import java.util.Set;

public class ControllerPairsComparator<S,A> extends ControllerComparator<S, A, Pair<S,S>> {
	SchedulerGeneratorLight<S, A> schedulerGenerator;
	PruningUtils<S,A> utils;
	public ControllerPairsComparator(final Integer id, ResultCounter stats,Set<ControllerChooser<Pair<S, S>, A>> controllerChooser, final MTS<Pair<S, S>, A> maximalController,
			final Set<Pair<S, S>> perfectFinalStates, final MTS<Pair<S, S>, A> result2,
			final Set<Pair<S, S>> heuristicFinalStates, final Integer maxSchedulers,
			final Set<A> controllableActions,
			final ActivityDefinitions<A> activityDefinitions,
			final LTS<S, A> realEnvironment, final Translator<S, Pair<S, S>> translator, ComparatorPool<A, Pair<S,S>> comparatorPool, Integer maxThreads) {
		super(id, stats, controllerChooser, maximalController, perfectFinalStates, result2, heuristicFinalStates,
				maxSchedulers, controllableActions, activityDefinitions,
				realEnvironment, translator, comparatorPool, maxThreads);
		this.utils = new PruningUtils<S,A>();

	}
	
	@Override
	protected Result compare() throws Z3Exception {
		LTS<S,A> prunedEnvironment = utils.pruneRealEnvironment(new LTSAdapter<Pair<S, S>,A>(result1,TransitionType.REQUIRED), new LTSAdapter<Pair<S, S>,A>(result2,TransitionType.REQUIRED), realEnvironment);
		this.schedulerGenerator = new SchedulerGeneratorLight<S,A>(prunedEnvironment, controllableActions, activityDefinitions);
		return super.compare();
	}
	
	@Override
	protected GenericChooser<S, A, Pair<S, S>> getScheduler() 	
	{
		Map<S,Integer> tmp = schedulerGenerator.next();
		if(tmp != null)
			return 	schedulerGenerator.build(tmp);
		return null;
	}

	@Override
	protected Set<Map<S,Integer>> getSkeletons(int cant) {
		return schedulerGenerator.next(cant);
	}

	@Override
	protected Set<Map<S, Integer>> getLastSkeletons() {
		return schedulerGenerator.getLasts();
	}

	@Override
	protected SkeletonBuilder<S, A, Pair<S, S>> getSkeletonBuilder() {
		return schedulerGenerator;
	}


}
