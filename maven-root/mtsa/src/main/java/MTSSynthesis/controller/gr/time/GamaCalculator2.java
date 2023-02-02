package MTSSynthesis.controller.gr.time;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import MTSTools.ac.ic.doc.mtstools.model.MTS;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Z3Exception;

import MTSSynthesis.controller.gr.GRGameSolver;
import MTSSynthesis.controller.gr.StrategyState;
import MTSSynthesis.controller.gr.time.model.Chooser;
import MTSSynthesis.controller.gr.time.model.Result;
import MTSSynthesis.controller.gr.time.model.Scheduler;
import MTSSynthesis.controller.gr.time.utils.TimeUtils;
import MTSSynthesis.controller.model.ControllerGoal;
import MTSSynthesis.controller.model.gr.GRGame;

public class GamaCalculator2<A,S>{
	
	private int max_schedulers;
	
	private void addStat(Map<Result, Integer> stats, Result res) {
		if(stats.keySet().contains(res)){
			stats.put(res, stats.get(res)+1);
		}else{
			stats.put(res, 1);
		}
	}
	
	public GamaCalculator2() {
		this.max_schedulers = 400;
	}
	
	public GamaCalculator2(Integer max_schedulers) {
		this.max_schedulers = max_schedulers;
	}
	
	public void evaluateLatency(GRGame<S> g,
			ControllerGoal<A> goal,
			GRGameSolver<S> grSolver,
			MTS<StrategyState<S, Integer>, A> heuristic,
			MTS<StrategyState<S, Integer>, A> maximalControllerUsingGR1) {
		//Start Z3 Definitions
		String END_TO_END_NAME_1 = "f1";
		String END_TO_END_NAME_2 = "f2";
		
		Map<Result,Integer> stats = new HashMap<Result,Integer>();
		ArrayList<Set<A>> relatedActions = new ArrayList<Set<A>>();
		Queue<A> pending = new LinkedList<A>();
		pending.addAll(maximalControllerUsingGR1.getActions());
		while(!pending.isEmpty()){
			Set<A> actions = new HashSet<A>();
			A a1 = pending.poll();
			for(A a2: maximalControllerUsingGR1.getActions()){
				if(TimeUtils.getTimeLabel(a1.toString()).equals(TimeUtils.getTimeLabel(a2.toString()))){
					actions.add(a2);
					pending.remove(a2);
				}
			}
			if(actions.size()>1){
				relatedActions.add(actions);
			}
		}
		
		Set<StrategyState<S,Integer>> finalStates = new HashSet<StrategyState<S,Integer>>(); 
		
		for(S s : grSolver.getGRGoal().getGuarantee(1).getStateSet()){
			finalStates.add(new StrategyState<S, Integer>(s, 1));
		}
		
		HeuristicSolutionsSchedulerIterator<StrategyState<S,Integer>,A> heuristicIterator  = new HeuristicSolutionsSchedulerIterator<StrategyState<S,Integer>,A>(heuristic, goal.getControllableActions(), finalStates);
//		System.out.println("#Heuristic Solutions:" + heuristicIterator.getSize());
		boolean doo = true;
		
		HashMap<Integer,BoolExpr> heursticGamas = new HashMap<Integer,BoolExpr>();
		HashMap<Integer,Scheduler<StrategyState<S,Integer>,A>> schedulerChoosed = new HashMap<Integer,Scheduler<StrategyState<S,Integer>,A>>();
		
		GamaComparator<A, StrategyState<S, Integer>> comparator = new GamaComparator<A, StrategyState<S, Integer>>();
		
		while(heuristicIterator.hasNext() && doo){
			Chooser<StrategyState<S,Integer>,A> heuristicScheduler  =  heuristicIterator.next();
			MTS<StrategyState<S, Integer>, A> result2 = heuristicScheduler.applyTo(heuristic);
			try{
				Set<Integer> choosed = chooseSchedulers(g, goal, finalStates, maximalControllerUsingGR1, END_TO_END_NAME_2, relatedActions, heursticGamas, schedulerChoosed, result2);
				preCalculateGamaForHeuristic(goal, END_TO_END_NAME_2, heursticGamas,schedulerChoosed, result2, comparator, finalStates);
				ControllableSchedulerIterator<StrategyState<S,Integer>,A> controllerIterator  = new ControllableSchedulerIterator<StrategyState<S,Integer>,A>(maximalControllerUsingGR1, goal.getControllableActions(), finalStates);
//				System.out.println("#Controllers:" + controllerIterator.getSize());
				while(controllerIterator.hasNext()){
					Chooser<StrategyState<S,Integer>,A> controllerScheduler  =  controllerIterator.next();
					Map<Result, Integer> ctr_stats = new HashMap<Result,Integer>();
					Result ctr_res = null;
					for (Integer sched_id : choosed) {
						MTS<StrategyState<S, Integer>, A> result1 = controllerScheduler.applyTo(maximalControllerUsingGR1);
						Chooser<StrategyState<S,Integer>,A> scheduler = schedulerChoosed.get(sched_id);
						MTS<StrategyState<S, Integer>, A> result_1_schedulled = scheduler.applyTo(result1);
						BoolExpr gama1 = comparator.calculateGama(result_1_schedulled , END_TO_END_NAME_1, result1, goal.getControllableActions(), finalStates);
						Result res = comparator.compareControllers(gama1, heursticGamas.get(sched_id), END_TO_END_NAME_1, END_TO_END_NAME_2);
						addStat(ctr_stats,res);
						if(ctr_stats.keySet().contains(Result.INCOMPARABLES)||
						  (ctr_stats.keySet().contains(Result.WORSE) && ctr_stats.keySet().contains(Result.BETTER))){
							ctr_res = Result.INCOMPARABLES;
							addStat(stats, Result.INCOMPARABLES);
							break;
						}
					}
					if(ctr_res == null){
						if(!ctr_stats.containsKey(Result.INCOMPARABLES) && !ctr_stats.containsKey(Result.WORSE)){
							if(ctr_stats.containsKey(Result.BETTER)){
								ctr_res = Result.BETTER;
							}else{
								ctr_res = Result.EQUALLYGOOD;
							}
						}else if(ctr_stats.keySet().contains(Result.INCOMPARABLES)||
								(ctr_stats.keySet().contains(Result.WORSE) && ctr_stats.keySet().contains(Result.BETTER))){
							ctr_res = Result.INCOMPARABLES;
						}else{
							ctr_res = Result.WORSE;
						}
						addStat(stats, ctr_res);
					}
//					System.out.println("Controller: " + controllerIterator.getIter() + " " + ctr_res);
				}
//				System.out.println(stats.toString());
				doo = false;
			}catch(Exception e){
				e.printStackTrace();
				addStat(stats, Result.EXCEPTION);
//				System.out.println(stats.toString());
			}
		}
	}

	private Set<Integer> chooseSchedulers(GRGame<S> g,
			ControllerGoal<A> goal,
			Set<StrategyState<S, Integer>> finalStates,
			MTS<StrategyState<S, Integer>, A> maximalControllerUsingGR1,
			String END_TO_END_NAME_2, ArrayList<Set<A>> relatedActions,
			HashMap<Integer, BoolExpr> heursticGamas,
			HashMap<Integer, Scheduler<StrategyState<S, Integer>, A>> schedulerChoosed,
			MTS<StrategyState<S, Integer>, A> result2) throws Z3Exception {
		SchedulerIterator<StrategyState<S, Integer>,A> schedulerIterator  = new SchedulerIterator<StrategyState<S, Integer>,A>(maximalControllerUsingGR1, goal.getControllableActions(), finalStates,relatedActions);
//		System.out.println("#Schedullers:" + schedulerIterator.getSize());
		int number = 0;
		int size = schedulerIterator.getSize();
		int total = Math.min(max_schedulers,size);
		Set<Integer> choosed = new HashSet<Integer>();
		Random ram = new Random(size);
		ram.setSeed(size);
		
		
		total=max_schedulers;
		while(schedulerIterator.hasNext() && number<total){
			Scheduler<StrategyState<S, Integer>,A> scheduler = schedulerIterator.next();
			schedulerChoosed.put(number, scheduler);
			choosed.add(number);
//			System.out.println(number);
			number++;
		}
//		System.out.println("#Schedullers Choosed:" + number);

		schedulerIterator = null;
		return choosed;
	}

	private void preCalculateGamaForHeuristic(ControllerGoal<A> goal, String END_TO_END_NAME_2,
											  HashMap<Integer, BoolExpr> heursticGamas,
											  HashMap<Integer, Scheduler<StrategyState<S, Integer>, A>> schedulerChoosed,
											  MTS<StrategyState<S, Integer>, A> result2,
											  GamaComparator<A, StrategyState<S, Integer>> comparator,
											  Set<StrategyState<S, Integer>> finalStates) throws Z3Exception {
		for (Integer key : schedulerChoosed.keySet()) {
			Scheduler<StrategyState<S, Integer>,A> scheduler = schedulerChoosed.get(key);
			MTS<StrategyState<S, Integer>, A> result_2_schedulled = scheduler.applyTo(result2);
			BoolExpr gama2 = comparator.calculateGama(result_2_schedulled , END_TO_END_NAME_2, result2, goal.getControllableActions(),finalStates);
			heursticGamas.put(key, gama2);
		}
	}
}