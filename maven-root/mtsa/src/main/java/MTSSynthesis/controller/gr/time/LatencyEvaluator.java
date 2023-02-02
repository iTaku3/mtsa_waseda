package MTSSynthesis.controller.gr.time;

import MTSSynthesis.controller.gr.time.comparator.ControllerComparator;
import MTSSynthesis.controller.gr.time.model.ActivityDefinitions;
import MTSSynthesis.controller.gr.time.model.Chooser;
import MTSSynthesis.controller.gr.time.model.ComparatorPool;
import MTSSynthesis.controller.gr.time.model.Result;
import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;
import MTSTools.ac.ic.doc.mtstools.model.impl.LTSAdapter;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Z3Exception;

import java.util.HashMap;
import java.util.Set;

public abstract class LatencyEvaluator<P,A,S>{

	protected MTS<P, A> heuristic;
	protected MTS<P, A> maximalControllerUsingGR1;
	protected Set<P> heuristicFinalStates; 
	protected Set<P> perfectFinalStates;
	protected Translator<S, P> translator;
	protected ActivityDefinitions<A> activityDefinitions;
	protected Set<A> controllableActions;
	protected HashMap<GenericChooser<S,A,P>,BoolExpr> heuristicGamas;
	protected Integer maxSchedulers;
	LTS<S,A> realEnvironment;
	ComparatorPool<A,P> comparatorPool;
	private int maxThreadsByScheduler;
	private int numberOfThreadsByControllers;

	
	public LatencyEvaluator(MTS<P, A> heuristic, MTS<P, A> maximalControllerUsingGR1,Set<P> heuristicFinalStates, Set<P> perfectFinalStates, ActivityDefinitions<A> activityDefinitions, Translator<S, P> translator2, Set<A> controllableActions, Integer maxSchedulers, LTS<S,A> realEnvironment ) {
		this.heuristic = heuristic;
		this.maximalControllerUsingGR1 = maximalControllerUsingGR1;
		this.heuristicFinalStates = heuristicFinalStates;
		this.perfectFinalStates = perfectFinalStates;
		this.translator = translator2;
		this.activityDefinitions = activityDefinitions;
		this.controllableActions = controllableActions;
		this.maxSchedulers = maxSchedulers;
		this.realEnvironment = realEnvironment;
		this.maxThreadsByScheduler = 8;
		this.numberOfThreadsByControllers =1;
	}
	
	public void evaluateLatency(Integer maxControllers) {
		//Start Z3 Definitions
		ResultCounter stats = new ResultCounter();
		HeuristicSolutionsSchedulerIterator<P,A> heuristicIterator  = new HeuristicSolutionsSchedulerIterator<P,A>(heuristic, controllableActions ,heuristicFinalStates);
//		System.out.println("#Heuristic Solutions:" + heuristicIterator.getSize());
		boolean doo = true;
		this.comparatorPool = new ComparatorPool<A,P>(maxThreadsByScheduler*numberOfThreadsByControllers
				,activityDefinitions, maximalControllerUsingGR1.getStates(),"f1","f2");
//		Translator<P,P> trivialTranslator = new TrivialTranslator<P>();
		while(heuristicIterator.hasNext() && doo){
			Chooser<P,A> heuristicScheduler  =  heuristicIterator.next();
			MTS<P, A> result2 = heuristicScheduler.applyTo(heuristic);
			try{
				preCalculateGamaForHeuristic();
				ControllerGenerator<P, A> controllerGenerator = new ControllerGenerator<P, A>(new LTSAdapter<P,A>(maximalControllerUsingGR1, TransitionType.REQUIRED), controllableActions,perfectFinalStates);
				int warningBetter = 0, warningEq = 0, warningWorse= 0 ;
				int i = 0;
				int size = maxControllers/numberOfThreadsByControllers;
//		        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreadsByControllers);
				while(!controllerGenerator.next(size).isEmpty() && i < maxControllers){
					ControllerComparator<S,A,P> controllerTask = getControllerComparator(i,controllerGenerator.getLasts() ,stats, maximalControllerUsingGR1, perfectFinalStates, result2, heuristicFinalStates, 
							maxSchedulers, controllableActions, activityDefinitions,realEnvironment, translator, comparatorPool, maxThreadsByScheduler);
//					executor.execute(controllerTask);
					controllerTask.run();
					i+=controllerGenerator.getLasts().size();
				}
//		        executor.shutdown();
//		        while (!executor.isTerminated()) {
//		        }
//		        System.out.println("Finished all threads");
//				System.out.println(stats.toString());
//				System.out.println("Warnings: Eq " + warningEq + " - Better" + warningBetter + " - Worse "+ warningWorse);
//				System.out.println("#Controllers: " + controllerGenerator.getGenerated().size());
				System.gc();
				doo = false;
			}catch(Exception e){
				e.printStackTrace();
				stats.addStat(Result.EXCEPTION);
//				System.out.println(stats.toString());
			}
		}
		this.comparatorPool.dispose();
	}

//	protected BoolExpr getGama2(GenericChooser<S,A,P> scheduler) throws Z3Exception {
//		return heuristicGamas.get(scheduler);
//	}
	
	protected abstract ControllerComparator<S, A, P> getControllerComparator(int i, Set<ControllerChooser<P, A>> controllerChooser, ResultCounter stats,
			MTS<P, A> result1, Set<P> perfectFinalStates2, MTS<P, A> result2,
			Set<P> heuristicFinalStates2, Integer maxSchedulers2,
			Set<A> controllableActions2,
			ActivityDefinitions<A> activityDefinitions2,
			LTS<S, A> realEnvironment2, Translator<S, P> translator2, ComparatorPool<A, P> comparator2,  Integer maxThreads);

	protected abstract Set<GenericChooser<S, A, P>> getSchedulers(MTS<P, A> result1, MTS<P, A> result2);
	
	protected abstract void preCalculateGamaForHeuristic() throws Z3Exception;
	
	protected abstract GenericChooser<S, A, P> getScheduler();
	
}