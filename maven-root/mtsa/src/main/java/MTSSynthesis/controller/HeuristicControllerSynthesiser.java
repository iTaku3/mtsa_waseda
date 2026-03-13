package MTSSynthesis.controller;

import MTSSynthesis.ar.dc.uba.model.condition.Fluent;
import MTSSynthesis.ar.dc.uba.model.language.Symbol;
import MTSSynthesis.controller.gr.GRGameSolver;
import MTSSynthesis.controller.gr.StrategyState;
import MTSSynthesis.controller.gr.lazy.LazyGRGameSolver;
import MTSSynthesis.controller.model.GameSolver;
import MTSSynthesis.controller.model.Strategy;
import MTSSynthesis.controller.util.GRGameBuilder;
import MTSSynthesis.controller.util.GameStrategyToMTSBuilder;
import MTSSynthesis.controller.gr.time.*;
import MTSSynthesis.controller.gr.time.model.*;
import MTSSynthesis.controller.model.GRGameControlProblem;
import MTSSynthesis.controller.model.gr.ConcurrencyControlProblem;
import MTSSynthesis.controller.model.ControllerGoal;
import MTSSynthesis.controller.model.gr.GRGame;
import MTSSynthesis.controller.model.gr.TransientControlProblem;
import MTSSynthesis.controller.model.gr.concurrency.GRCGame;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;
import MTSTools.ac.ic.doc.mtstools.model.impl.LTSAdapter;
import MTSTools.ac.ic.doc.mtstools.model.impl.MTSAdapter;
import MTSTools.ac.ic.doc.mtstools.model.operations.ParallelComposer;

import java.util.*;

public class HeuristicControllerSynthesiser<S,A>{

	public MTS<S, A> applyHeuristics(MTS<S, A> controller, MTS<S, A> env, ControllerGoal<A> goal) {
		LTS<S,A> realEnvironment = new LTSAdapter<S,A>(env,TransitionType.REQUIRED);
		LTS<S,A> perfectSolution =  new LTSAdapter<S,A>(controller, TransitionType.REQUIRED);
		LTS<S,A> heuristicSolution = null;
		GRGameControlProblem<S,A,Integer> cp = null;
		
		if(goal.isNonTransient()){
			cp = new TransientControlProblem<S, A, Integer>(perfectSolution, goal);
			heuristicSolution =  cp.solve();
		}else if (goal.getConcurrencyFluents() != null && !goal.getConcurrencyFluents().isEmpty()){
			cp = new ConcurrencyControlProblem<S, A, Integer>(perfectSolution, goal);
			heuristicSolution =  cp.solve();
		}else {
			heuristicSolution = generateBestController(perfectSolution,realEnvironment,getActivityDefinition(goal.getActivityFluents()), getFinalStates(goal,perfectSolution), goal.getControllableActions());
		}
		
		if(goal.isTestLatency()){
			compareControllers(goal, realEnvironment, heuristicSolution, perfectSolution);
		}
		if(goal.isReachability()){
			GR1toReachability.transform(heuristicSolution, cp.getGRGame().getGoal().getGuarantee(1).getStateSet());
		}
		
		return new MTSAdapter<S,A>(heuristicSolution);
	}
	
	public MTS<S, A> applyReachabilityPruning(MTS<S, A> controller, ControllerGoal<A> goal) {
		GRCGame<S> cgame = new GRGameBuilder<S, A>().buildGRCGameFrom(controller, goal);
		GR1toReachability.transform(new LTSAdapter<S,A>(controller, TransitionType.REQUIRED), cgame.getGoal().getGuarantee(1).getStateSet());
		return controller;
	}

	private void compareControllers(ControllerGoal<A> goal,
									LTS<S, A> realEnvironment, LTS<S, A> heuristicSolution,
									LTS<S, A> perfectSolution){
		Set<S> perfectFinalStates = getFinalStates(goal, perfectSolution);
		Set<S> heuristicFinalStates = getFinalStates(goal, heuristicSolution);
		compareControllers(goal, realEnvironment, heuristicSolution, perfectSolution, heuristicFinalStates, perfectFinalStates, false);
	}

	private Set<S> getFinalStates(ControllerGoal<A> goal, LTS<S, A> heuristicSolution) {
		GRGame<S> heuristicGRGame = new GRGameBuilder<S,A>().buildGRGameFrom(new MTSAdapter<S,A>(heuristicSolution), goal);
		Set<S> heuristicFinalStates = heuristicGRGame.getGoal().getGuarantee(1).getStateSet();
		return heuristicFinalStates;
	}

	private void compareControllers(ControllerGoal<A> goal,
									LTS<S, A> realEnvironment, LTS<S, A> heuristicSolution,
									LTS<S, A> perfectSolution, Set<S> heuristicFinalStates,
									Set<S> perfectFinalStates, boolean sameControllers) {
		
		LTS<Pair<S, S>, A> heuristicComposition = transformToReachability1(realEnvironment, heuristicSolution, heuristicFinalStates);
		LTS<Pair<S, S>, A> perfectComposition = transformToReachability1(realEnvironment, perfectSolution, perfectFinalStates);
		
		Set<A> controllableActions = goal.getControllableActions();
		
		ActivityDefinitions<A> activityDefinitions = getActivityDefinition(goal.getActivityFluents());
		
		
		Translator<S,Pair<S,S>> translator = new TranslatorPair<S,S>();
		
		Set<Pair<S, S>> heuristicComposedFinalStates = compositionFinalStates(heuristicFinalStates, heuristicComposition.getStates());
		Set<Pair<S, S>> perfectComposedFinalStates = compositionFinalStates(perfectFinalStates,perfectComposition.getStates());
		
		pruneRealEnvironment(perfectComposition,realEnvironment);

		if(sameControllers)
			compareWithTheSameSchedulers(goal, realEnvironment, heuristicComposition, perfectComposition, controllableActions, activityDefinitions, translator, heuristicComposedFinalStates, perfectComposedFinalStates);
		else
			compareWithParticularSchedulers(goal, realEnvironment, heuristicComposition, perfectComposition, controllableActions, activityDefinitions, translator, heuristicComposedFinalStates, perfectComposedFinalStates);
	}


	/*@ezecastellano: This method allows to use the same set of schedulers from every pair of controllers.
    It might be useful when you cannot explore all the schedulers.*/
	private void compareWithTheSameSchedulers(ControllerGoal<A> goal, LTS<S, A> realEnvironment, LTS<Pair<S, S>, A> heuristicComposition, LTS<Pair<S, S>, A> perfectComposition, Set<A> controllableActions, ActivityDefinitions<A> activityDefinitions, Translator<S, Pair<S, S>> translator, Set<Pair<S, S>> heuristicComposedFinalStates, Set<Pair<S, S>> perfectComposedFinalStates) {
		Set<GenericChooser<S, A, Pair<S, S>>> schedulers = generateSchedulers(goal.getMaxSchedulers()-1,realEnvironment,controllableActions,activityDefinitions);
		LatencyPresetEvaluator<Pair<S,S>,A,S> evaluator = new LatencyPresetEvaluator<Pair<S,S>,A,S>(
		new MTSAdapter<Pair<S,S>,A>(heuristicComposition),
		new MTSAdapter<Pair<S,S>,A>(perfectComposition),
		heuristicComposedFinalStates, perfectComposedFinalStates,
		activityDefinitions, translator,
		goal.getControllableActions(),
		schedulers, realEnvironment, goal.getMaxSchedulers()-1);
	}

	/*@ezecastellano: This method generates a subset of schedulers for each particular pair of controllers, generating
	schedulers for the subset of reachable states.*/
	private void compareWithParticularSchedulers(ControllerGoal<A> goal, LTS<S, A> realEnvironment, LTS<Pair<S, S>, A> heuristicComposition, LTS<Pair<S, S>, A> perfectComposition, Set<A> controllableActions, ActivityDefinitions<A> activityDefinitions, Translator<S, Pair<S, S>> translator, Set<Pair<S, S>> heuristicComposedFinalStates, Set<Pair<S, S>> perfectComposedFinalStates) {
		LatencyNotPresetEvaluator<S,A> evaluator = new LatencyNotPresetEvaluator<S,A>(
				new MTSAdapter<Pair<S,S>,A>(heuristicComposition),
				new MTSAdapter<Pair<S,S>,A>(perfectComposition),
				heuristicComposedFinalStates, perfectComposedFinalStates,
				activityDefinitions, translator,
		        controllableActions,
		        realEnvironment,goal.getMaxSchedulers()-1);
		evaluator.evaluateLatency(goal.getMaxControllers()-1);
	}

	private Set<GenericChooser<S, A, Pair<S, S>>> generateSchedulers(Integer maxSchedulers, LTS<S, A> realEnvironment, Set<A> controllableActions, ActivityDefinitions<A> activityDefinitions) {
		SchedulerGenerator<S, A> schedulerGenerator = new SchedulerGenerator<S, A>(realEnvironment, controllableActions, activityDefinitions);
//		System.out.println("Estimation: " + schedulerGenerator.getEstimation());

		GenericChooser<S, A, Pair<S, S>> scheduler = schedulerGenerator.next();
		int i = 0;
		while (scheduler != null && i < maxSchedulers) {
			scheduler = schedulerGenerator.next();
			i++;
		}
		return schedulerGenerator.getGenerated();
	}


	private void pruneRealEnvironment(LTS<Pair<S, S>, A> perfectComposition, LTS<S, A> realEnvironment) {
		Map<S,Set<Pair<A,S>>> transitionsToRemove = new HashMap<S,Set<Pair<A,S>>>();
		Set<S> finalStates = new HashSet<S>();
		Set<S> notFinalStates = new HashSet<S>();
		for (S state : realEnvironment.getStates()) {
			Set<A> enabled = new HashSet<A>();
			for (Pair<S,S> p : perfectComposition.getStates()) {
				if(p.getFirst().equals(state)){
					if(perfectComposition.getTransitions(p).isEmpty()){
						finalStates.add(state);
					}else{
						notFinalStates.add(state);
						for(Pair<A,Pair<S,S>> t :perfectComposition.getTransitions(p)){
							enabled.add(t.getFirst());
						}
					}
				}
			}
			Set<Pair<A,S>> toRemove = new HashSet<Pair<A,S>>();
			for(Pair<A,S> t : realEnvironment.getTransitions(state)){
				if(!enabled.contains(t.getFirst())){
					toRemove.add(t);
				}
			}
			transitionsToRemove.put(state, toRemove);
		}
		for (S s : transitionsToRemove.keySet()) {
			for (Pair<A,S> t : transitionsToRemove.get(s)) {
				realEnvironment.removeTransition(s, t.getFirst(), t.getSecond());
			}
		}
		for (S s : finalStates) {
			if(notFinalStates.contains(s)){
//				System.out.println("End and something more");
			}else{
//				System.out.println("Prunning something..");
				for(Pair<A,S> transition: realEnvironment.getTransitions(s)){
					realEnvironment.removeTransition(s, transition.getFirst(), transition.getSecond());
				}
			}
		}
		realEnvironment.removeUnreachableStates();}




	private LTS<Pair<S, S>, A> transformToReachability1(
			LTS<S, A> realEnvironment, LTS<S, A> perfectSolution,
			Set<S> perfectFinalStates) {
		GR1toReachability.transform(perfectSolution,perfectFinalStates);
		ParallelComposer<S, A, S> perfectComposer = new ParallelComposer<S, A, S>(realEnvironment, perfectSolution);
		LTS<Pair<S,S>,A> perfectComposition = perfectComposer.compose();
		return perfectComposition;
	}
	
	
	
	private Set<Pair<S, S>> compositionFinalStates(Set<S> originalFinalStates, Set<Pair<S, S>> compositionStates) {
		Set<Pair<S,S>> heuristicComposedFinalStates = new HashSet<Pair<S,S>>();
		for (S s : originalFinalStates) {
			for (Pair<S,S> p : compositionStates) {
				if(p.getSecond().equals(s)){
					heuristicComposedFinalStates.add(p);
				}
			}
		}
		return heuristicComposedFinalStates;
	}

	private MTS<StrategyState<S, Integer>, A> getResult(MTS<S, A> plant,
			GameSolver<S, Integer> gameSolver,
			GRGameSolver<S> grSolver) {
		Strategy<S,Integer> strategy = gameSolver.buildStrategy();
		
		Set<Pair<StrategyState<S, Integer>, StrategyState<S, Integer>>> worseRank = grSolver.getWorseRank();
		
		int lazyness = 0;
		try {
			lazyness = ((LazyGRGameSolver<S>) grSolver).getMaxLazyness();
		} catch (Exception e) {
//			System.out.println("This model doesn't have lazyness.");
		}
		
		MTS<StrategyState<S, Integer>, A> result = GameStrategyToMTSBuilder.getInstance().buildMTSFrom(plant, strategy, worseRank, lazyness);
		return result;
	}

	@SuppressWarnings("unchecked")
	private ActivityDefinitions<A> getActivityDefinition(Set<Fluent> activityFluents) {
		Set<Activity<A>> activities = new HashSet<Activity<A>>();
		for (Fluent fluent : activityFluents) {
			Set<A> initiatingActions = new HashSet<A>();
			for (Symbol a : fluent.getInitiatingActions()) {
				initiatingActions.add((A) a.toString());
			}
			Set<A> terminatingActions = new HashSet<A>();
			for (Symbol a : fluent.getTerminatingActions()) {
				terminatingActions.add((A) a.toString());
			}
			activities.add(new Activity<A>(fluent.getName(),initiatingActions,terminatingActions)); 
		}
		return new ActivityDefinitions<A>(activities);
	}



	private LTS<S,A> generateBestController(LTS<S,A> controller, LTS<S,A> environment, ActivityDefinitions<A> activityDefinitions, Set<S> finalStates, Set<A> controllableActions){
		DoubleLinkedLTS<S,A> bestController = new DoubleLinkedLTS<S, A>(controller);
		//TODO: Think about changing the implementation to use Pair<S,S> as state, so we can use the comparison framework straight.
		LTS<Pair<S,S>, A> composed = transformToReachability1(environment,controller,finalStates);
		Set<S> visited = new HashSet<S>();
		Queue<S> pruningCandidates = new LinkedList<S>();
		pruningCandidates.addAll(finalStates);
		ControllerChoicesBuilder<S,A> builder = new ControllerChoicesBuilder<S,A>(bestController,controllableActions, finalStates);
		Map<S,List<Choice<A>>> choices=  builder.getAllChoices();
		while(!pruningCandidates.isEmpty()){
			S state = pruningCandidates.poll();
			if( !visited.contains(state)
				&& isReachable(bestController, bestController.getInitialState(), state) &&
				isReachable(bestController, state, finalStates) && hasChoices(state,choices)){
				Choice<A> choice = getAChoice(bestController, environment, choices, state);
				removeOtherTransitions(state,bestController,choice);
			}
			addNonVisitedStates(state, bestController, pruningCandidates,visited);
		}
		bestController.removeUnreachableStates();
		return bestController;
	}

	private void addNonVisitedStates(S state, DoubleLinkedLTS<S,A> controller, Queue<S> pruningCandidates, Set<S> visited){
		for(S predecessor: controller.getPredecessors(state)){
			if(!visited.contains(predecessor)){
				pruningCandidates.add(predecessor);
			}
		}

	}

	private Choice<A> getAChoice(LTS<S, A> controller, LTS<S, A> environment, Map<S, List<Choice<A>>> choices, S state) {
		List<Choice<A>> alternatives = choices.get(state);
		Ranking ranking = new Ranking(alternatives);
		for(int i=0; i < alternatives.size(); i++){
            Choice<A> aChoice  = alternatives.get(i);
            for(int j = i+1; j < alternatives.size(); j++) {
                Choice<A> otherChoice = alternatives.get(j);
                Result result = partialComparison(state, aChoice, otherChoice, controller, environment);
                //@ezecastellano: We are considering a tie as zero points.
				//Should we consider it as something positive?
                if (result.equals(Result.BETTER)) {
                    ranking.increase(aChoice);
                }else if (result.equals(Result.WORSE)){
                    ranking.increase(otherChoice);
                }
            }
        }
		Set<Choice<A>> bestChoices = ranking.best();
		return choose(bestChoices);
	}

	private void removeOtherTransitions(S state, DoubleLinkedLTS<S,A> controller, Choice<A> choice){
		for(Pair<A,S> transition : controller.getTransitions(state)){
			if(choice.getAvailableLabels().contains(transition.getFirst())){
				controller.removeTransition(state,transition.getFirst(),transition.getSecond());
			}
		}
	}


	private Choice<A> choose(Set<Choice<A>> choices){
		//@ezecastellano: I choose one of them randomly.
		//Should I consider all of them while analysing the ancestors?
		for(Choice<A> choice: choices) {
			return choice;
		}
		return null;
	}

	private Result partialComparison(S state, Choice<A> aChoice, Choice<A> otherChoice, LTS<S,A> controller, LTS<S,A> environment){
		//TODO: Implement this method.
		return null;
	}

	private boolean isReachable(DoubleLinkedLTS<S,A> lts, S source, S sink){
		return lts.getReachableStatesBy(source).contains(sink);
	}

	private boolean hasChoices(S state, Map<S,List<Choice<A>>> choices){
		return choices.get(state).size()>1;
	}

	private boolean isReachable(DoubleLinkedLTS<S,A> lts, S source, Set<S> sinks){
		for (S sink: sinks) {
			if(isReachable(lts, source, sink)){
				return true;
			}
		}
		return false;
	}

	private class Ranking {
		Map<Choice<A>, Integer> ranking = new HashMap<Choice<A>,Integer>();
		private Ranking(List<Choice<A>> choices){
			for(Choice<A> choice: choices){
				ranking.put(choice,0);
			}
		}

		private void increase(Choice<A> choice){
			ranking.put(choice, (ranking.get(choice)+1));
		}

		private Set<Choice<A>> best(){
			Integer maxRank = getMaxRank();
			Set<Choice<A>> bestChoices = new HashSet<Choice<A>>();
			for (Choice<A> choice: ranking.keySet()) {
				if(ranking.get(choice).equals(maxRank)){
					bestChoices.add(choice);
				}
			}
			return  bestChoices;
		}

		private Integer getMaxRank(){
			Integer maxRank = 0;
			for(Integer rank : ranking.values()){
				if(maxRank < rank)
					maxRank = rank;
			}
			return maxRank;
		}

	}
	
}
