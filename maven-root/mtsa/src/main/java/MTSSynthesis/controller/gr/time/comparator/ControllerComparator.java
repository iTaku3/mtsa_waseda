package MTSSynthesis.controller.gr.time.comparator;

import java.util.Calendar;
import java.util.Map;
import java.util.Set;

import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;
import MTSTools.ac.ic.doc.mtstools.model.impl.LTSAdapter;
import MTSTools.ac.ic.doc.mtstools.model.impl.MTSAdapter;

import com.microsoft.z3.Z3Exception;

import MTSSynthesis.controller.gr.time.ControllerChooser;
import MTSSynthesis.controller.gr.time.GamaComparator;
import MTSSynthesis.controller.gr.time.GenericChooser;
import MTSSynthesis.controller.gr.time.ResultCounter;
import MTSSynthesis.controller.gr.time.SkeletonBuilder;
import MTSSynthesis.controller.gr.time.Translator;
import MTSSynthesis.controller.gr.time.TrivialTranslator;
import MTSSynthesis.controller.gr.time.model.ActivityDefinitions;
import MTSSynthesis.controller.gr.time.model.ComparatorPool;
import MTSSynthesis.controller.gr.time.model.Result;

public abstract class ControllerComparator<S,A,P> implements Runnable {

    ResultCounter stats; 
    MTS<P, A> result1;
    MTS<P, A> maximalController;
	Set<P> finalStates1; 
	MTS<P, A> result2;
	Set<P> finalStates2;
	Integer maxSchedulers;
	Integer id;
	Set<A> controllableActions;
	Translator<S, P> translator;
	Translator<P,P> trivialTranslator;
	ComparatorPool<A, P> comparatorPool;
	Set<ControllerChooser<P, A>> controllerChooser;
	LTS<S,A> realEnvironment;
	ActivityDefinitions<A> activityDefinitions;
	Integer maxThreads;
	
    public ControllerComparator(Integer id, ResultCounter stats, Set<ControllerChooser<P, A>> controllerChooser, MTS<P, A> maximalController,
			Set<P> finalStates1, MTS<P, A> result2,
			Set<P> finalStates2, 
			Integer maxSchedulers, 
			Set<A> controllableActions, 
			ActivityDefinitions<A> activityDefinitions, 
			LTS<S,A> realEnvironment, Translator<S, P> translator, 
			ComparatorPool<A,P> comparator, Integer maxThreads) {
    	this.maximalController = maximalController;
    	this.result1 = null;
    	this.result2 = result2;
    	this.stats = stats;
    	this.finalStates1 = finalStates1;
    	this.finalStates2 = finalStates2;
    	this.maxSchedulers = maxSchedulers;
    	this.id =id;
    	this.controllableActions = controllableActions;
    	this.translator = translator;
    	this.comparatorPool = comparator;
    	this.controllerChooser = controllerChooser;
    	this.realEnvironment = realEnvironment;
    	this.activityDefinitions = activityDefinitions;
    	this.trivialTranslator = new TrivialTranslator<P>();
    	this.maxThreads = maxThreads;
	}

	@Override
    public void run() {
//        System.out.println(Thread.currentThread().getName() + " Start. Command = "+ id.toString());
        try {
        	int i = 0;
        	    	
        	for (ControllerChooser<P, A> controllerScheduler : this.controllerChooser) {
        		this.result1 = new MTSAdapter<P,A>(controllerScheduler.applyTo(new LTSAdapter<P,A>(maximalController, TransitionType.REQUIRED), trivialTranslator));
        		Result ctr_res = compare();
//        		System.out.println("Controller: " + (id + i++) + " " + ctr_res);
//        		comparator.disposeContext();
			}
//    		comparator.disposeAll();
		} catch (Z3Exception e) {
//	        System.out.println(Thread.currentThread().getName()+" had an exception.");
			e.printStackTrace();
		}
//        System.out.println(Thread.currentThread().getName()+" End.");
    }

    protected Result compare() throws Z3Exception {
		String END_TO_END_NAME_1 = comparatorPool.getStopwatchName1();
		String END_TO_END_NAME_2 = comparatorPool.getStopwatchName2();
		ResultCounter ctr_stats = new ResultCounter();
		Result ctr_res = null;
		int sused = 0;
        GenericChooser<S,A,P> scheduler = getScheduler();
        
        //First we are going to try to see if this is a case of Incomparable to avoid exploring all the schedulers.
        int exploringMax = Math.min(25,maxSchedulers);
        SchedulerByComparator<S, A, P> singleComparator = new SchedulerByComparator<S,A,P>(END_TO_END_NAME_1, 
        		END_TO_END_NAME_2,
        		ctr_stats,
        		ctr_res, comparatorPool,
        		result1,
        		finalStates1, 
        		result2,
        		finalStates2,
        		id,
        		translator,
        		controllableActions);
        
    	Integer id = null; 
    	while(id==null){
    		id = comparatorPool.getComparatorId();
    	}
    	GamaComparator<A, P> comparator = comparatorPool.getComparator(id);
		while(scheduler!=null && sused < exploringMax && !(ctr_stats.contains(Result.INCOMPARABLES))){
			singleComparator.compare(scheduler, comparator);
			sused++;
			scheduler = getScheduler();
		}
		comparatorPool.releaseComparator(id);
		
		
		
		
		if(!(ctr_stats.contains(Result.INCOMPARABLES))){
//			System.out.println("Start Generation" + Calendar.getInstance().getTime().toString());
			getSkeletons(1000000);
//			System.out.println("End Generation" + Calendar.getInstance().getTime().toString());
			
//			ExecutorService executor = Executors.newFixedThreadPool(maxThreads);
//			int sizeOf = 2000;
//			while(!getSchedulers(sizeOf).isEmpty() && sused < maxSchedulers && !(ctr_stats.contains(Result.INCOMPARABLES))){
//				System.out.println("Start Comparison" + Calendar.getInstance().getTime().toString());
				SchedulerByComparator<S, A, P> schedulerByTask = new SchedulerByComparator<S,A,P>(END_TO_END_NAME_1, 
						END_TO_END_NAME_2,
						ctr_stats,
						ctr_res, getLastSkeletons(), getSkeletonBuilder(), comparatorPool ,
						result1,
						finalStates1, 
						result2,
						finalStates2,
						id,
						translator,
						controllableActions);
//				executor.execute(schedulerByTask);
				schedulerByTask.run();
				sused+=getLastSkeletons().size();
//				System.out.println("End Comparison" + Calendar.getInstance().getTime().toString());
//			}
//			executor.shutdown();
//			while (!executor.isTerminated()) {
//			}
//			System.out.println("Finished all threads");
		}
		
//		System.out.println("Schedulers: " + sused /* + "/" + sgen*/);
		if(ctr_res == null){
			if(!ctr_stats.contains(Result.INCOMPARABLES) && !ctr_stats.contains(Result.WORSE)){
				if(ctr_stats.contains(Result.BETTER)){
					if(sused>=maxSchedulers)
						ctr_res = Result.BETTER_WARNING;
					else
						ctr_res = Result.BETTER;
				}else{
					if(sused>=maxSchedulers)
						ctr_res = Result.EQUALLYGOOD_WARNING;
					else
						ctr_res = Result.EQUALLYGOOD;
				}
			}else if(ctr_stats.contains(Result.INCOMPARABLES)||
					(ctr_stats.contains(Result.WORSE) && ctr_stats.contains(Result.BETTER))){
				ctr_res = Result.INCOMPARABLES;
			}else{
				if(sused>=maxSchedulers)
					ctr_res = Result.WORSE_WARNING;
				else
					ctr_res = Result.WORSE;
			}
		}
		stats.addStat(ctr_res);
		return ctr_res;
	}

	protected abstract GenericChooser<S, A, P> getScheduler();
	
	protected abstract Set<Map<S,Integer>> getSkeletons(int cant);
	
	protected abstract Set<Map<S,Integer>> getLastSkeletons();
	
	protected abstract SkeletonBuilder<S, A, P> getSkeletonBuilder();
}
