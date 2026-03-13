package MTSSynthesis.controller.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSTools.ac.ic.doc.mtstools.model.impl.MTSAdapter;
import MTSSynthesis.controller.gr.GRRankSystem;
import MTSSynthesis.controller.gr.StrategyState;
import MTSSynthesis.controller.gr.perfect.PerfectInfoGRGameSolver;
import MTSSynthesis.controller.util.GRGameBuilder;
import MTSSynthesis.controller.util.GameStrategyToLTSBuilder;
import MTSSynthesis.controller.util.GenericLTSStrategyStateToStateConverter;
import MTSSynthesis.controller.model.gr.concurrency.GRCGame;


public class PerfectInfoStateSpaceCuttingGRControlProblem<S,A> extends
	GRControProblem<S, A, Integer>{

	protected List<String> stateSpaceCuttingControlProblems;
	protected PerfectInfoGRGameSolver<S> perfectInfoGRControlProblem;
	
	
	public PerfectInfoStateSpaceCuttingGRControlProblem(LTS<S,A> originalEnvironment, ControllerGoal<A> controllerGoal) {
		super(originalEnvironment, controllerGoal);
		stateSpaceCuttingControlProblems = new ArrayList<String>();
	}
		
	public boolean containsStateSpaceCuttingControlProblem(String stateSpaceCuttingControlProblem){
		return stateSpaceCuttingControlProblems.contains(stateSpaceCuttingControlProblem);
	}
	
	public void addStateSpaceCuttingControlProblem(String stateSpaceCuttingControlProblem){
		if(!containsStateSpaceCuttingControlProblem(stateSpaceCuttingControlProblem))
			stateSpaceCuttingControlProblems.add(stateSpaceCuttingControlProblem);
	}
	
	public void removeStateSpaceCuttingControlProblem(String sateSpaceCuttingControlProblem){
		if(containsStateSpaceCuttingControlProblem(sateSpaceCuttingControlProblem))
			stateSpaceCuttingControlProblems.remove(sateSpaceCuttingControlProblem);
	}
	
	protected StateSpaceCuttingControlProblem<S, A> createStateSpaceCuttingControlProblem(
			String gameSolverName) throws NoSuchMethodException,
			SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		Class<?> clazz = Class.forName(gameSolverName);
		Constructor<?> constructor = clazz.getConstructor(LTS.class, ControllerGoal.class);
		Set<S> failures = new HashSet<S>();
		StateSpaceCuttingControlProblem<S,A> instance = (StateSpaceCuttingControlProblem<S, A>) constructor.newInstance(environment, controllerGoal);
		return instance;
	}
	
	@Override
	protected LTS<S,A> primitiveSolve() {
		//cut according to all the predefined game solvers
		for(String s: stateSpaceCuttingControlProblems){
			try {
				StateSpaceCuttingControlProblem<S, A> cuttingControlProblem = createStateSpaceCuttingControlProblem(s);
				cuttingControlProblem.solve();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		GRCGame<S> perfectInfoGRGame = new GRGameBuilder<S,A>().buildGRCGameFrom(new MTSAdapter<S,A>(environment), controllerGoal);
		GRRankSystem<S> grRankSystem = new GRRankSystem<S>(
				perfectInfoGRGame.getStates(), perfectInfoGRGame.getGoal().getGuarantees(),
				perfectInfoGRGame.getGoal().getAssumptions(), perfectInfoGRGame.getGoal().getFailures());
		perfectInfoGRControlProblem = new PerfectInfoGRGameSolver<S>(perfectInfoGRGame, grRankSystem);
		perfectInfoGRControlProblem.solveGame();
		LTS<StrategyState<S, Integer>, A> result = GameStrategyToLTSBuilder
				.getInstance().buildLTSFrom(environment,
						perfectInfoGRControlProblem.buildStrategy());
		return new GenericLTSStrategyStateToStateConverter<S, A, Integer>()
				.transform(result);		
	}	
	
}




