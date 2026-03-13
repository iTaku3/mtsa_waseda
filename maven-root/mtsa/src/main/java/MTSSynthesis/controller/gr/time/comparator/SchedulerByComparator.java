package MTSSynthesis.controller.gr.time.comparator;

import java.util.Map;
import java.util.Set;

import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;
import MTSTools.ac.ic.doc.mtstools.model.impl.LTSAdapter;
import MTSTools.ac.ic.doc.mtstools.model.impl.MTSAdapter;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Z3Exception;

import MTSSynthesis.controller.gr.time.GamaComparator;
import MTSSynthesis.controller.gr.time.GenericChooser;
import MTSSynthesis.controller.gr.time.ResultCounter;
import MTSSynthesis.controller.gr.time.SkeletonBuilder;
import MTSSynthesis.controller.gr.time.Translator;
import MTSSynthesis.controller.gr.time.model.ComparatorPool;
import MTSSynthesis.controller.gr.time.model.Result;

public class SchedulerByComparator<S,A,P> implements Runnable {

	String END_TO_END_NAME_1; 
	String END_TO_END_NAME_2;
	ResultCounter ctr_stats; 
	Result ctr_res;
	Set<Map<S, Integer>> skeletons;
	ComparatorPool<A, P> comparatorPool;
    MTS<P, A> result1;
	Set<P> finalStates1; 
	MTS<P, A> result2;
	Set<P> finalStates2;
	Integer id;
	Translator<S, P> translator;
	Set<A> controllableActions;
	SkeletonBuilder<S, A, P> skeletonBuilder;
	
    public SchedulerByComparator(String END_TO_END_NAME_1, 
	String END_TO_END_NAME_2,
	ResultCounter ctr_stats,
	Result ctr_res,	ComparatorPool<A, P> comparatorPool,
    MTS<P, A> result1,
	Set<P> finalStates1, 
	MTS<P, A> result2,
	Set<P> finalStates2,
	Integer id,
	Translator<S, P> translator,
	Set<A> controllableActions) {
    	this.END_TO_END_NAME_1 = END_TO_END_NAME_1; 
    	this.END_TO_END_NAME_2 = END_TO_END_NAME_2;
    	this.ctr_stats = ctr_stats; 
    	this.ctr_res = ctr_res;
    	this.skeletons = null;
    	this.skeletonBuilder = null;
    	this.comparatorPool = comparatorPool;
        this.result1 = result1;
    	this.finalStates1 = finalStates1; 
    	this.result2 = result2;
    	this.finalStates2 = finalStates2;
    	this.id = id;
    	this.translator = translator;
    	this.controllableActions = controllableActions;
	}
    
    public SchedulerByComparator(String END_TO_END_NAME_1, 
    		String END_TO_END_NAME_2,
    		ResultCounter ctr_stats,
    		Result ctr_res,
    		Set<Map<S, Integer>> schedulers, 
    		SkeletonBuilder<S, A, P> skeletonBuilder,
    		ComparatorPool<A, P> comparatorPool,
    	    MTS<P, A> result1,
    		Set<P> finalStates1, 
    		MTS<P, A> result2,
    		Set<P> finalStates2,
    		Integer id,
    		Translator<S, P> translator,
    		Set<A> controllableActions) {
    	    	this.END_TO_END_NAME_1 = END_TO_END_NAME_1; 
    	    	this.END_TO_END_NAME_2 = END_TO_END_NAME_2;
    	    	this.ctr_stats = ctr_stats; 
    	    	this.ctr_res = ctr_res;
    	    	this.skeletons = schedulers;
    	    	this.skeletonBuilder = skeletonBuilder;
    	    	this.comparatorPool = comparatorPool;
    	        this.result1 = result1;
    	    	this.finalStates1 = finalStates1; 
    	    	this.result2 = result2;
    	    	this.finalStates2 = finalStates2;
    	    	this.id = id;
    	    	this.translator = translator;
    	    	this.controllableActions = controllableActions;
    		}

	@Override
    public void run() {
        try {
        	Integer id = null; 
        	while(id==null){
        		id = comparatorPool.getComparatorId();
        	}
        	GamaComparator<A, P> comparator = comparatorPool.getComparator(id);
        	if(this.skeletons !=null){
        		for (Map<S,Integer> skeleton : skeletons) {
        			compare(skeletonBuilder.build(skeleton), comparator);
				}
        	}
    		comparatorPool.releaseComparator(id);
		} catch (Z3Exception e) {
//	        System.out.println(Thread.currentThread().getName()+ " had an exception.");
			e.printStackTrace();
		}
    }

	protected Result compare(GenericChooser<S, A, P> scheduler, GamaComparator<A, P> comparator) throws Z3Exception {
		BoolExpr gama1 = getGama(scheduler, result1,finalStates1, END_TO_END_NAME_1, comparator);
		BoolExpr gama2 = getGama(scheduler, result2,finalStates2, END_TO_END_NAME_2, comparator);
		Result res = comparator.compareControllers(gama1,gama2, END_TO_END_NAME_1, END_TO_END_NAME_2);
		ctr_stats.addStat(res);
		if(ctr_stats.contains(Result.INCOMPARABLES) || (ctr_stats.contains(Result.WORSE) && ctr_stats.contains(Result.BETTER))){
			ctr_res = Result.INCOMPARABLES;
			ctr_stats.addStat(Result.INCOMPARABLES);
//			System.out.println("RESULT_1");
//			MTS<P, A> result_1_schedulled = new MTSAdapter<P, A>(scheduler.applyTo(new LTSAdapter<P,A>(result1, TransitionType.REQUIRED),translator));
//			System.out.println(result_1_schedulled.toString());
//			MTS<P, A> heuristic_schedulled = new MTSAdapter<P, A>(scheduler.applyTo(new LTSAdapter<P,A>(result2, TransitionType.REQUIRED),translator));
//			System.out.println("RESULT_2");
//			System.out.println(heuristic_schedulled.toString());
		}
		return ctr_res;
	}

	protected BoolExpr getGama(GenericChooser<S, A, P> scheduler, MTS<P, A> mts, Set<P> finalStates, String stopwatch, GamaComparator<A, P> comparator) throws Z3Exception {
		MTS<P, A> schedulled = new MTSAdapter<P, A>(scheduler.applyTo(new LTSAdapter<P,A>(mts, TransitionType.REQUIRED),translator));
		return comparator.calculateGama(schedulled , stopwatch, mts, controllableActions, finalStates);
	}
}
