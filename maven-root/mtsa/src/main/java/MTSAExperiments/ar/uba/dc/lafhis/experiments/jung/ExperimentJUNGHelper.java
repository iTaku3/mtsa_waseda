package MTSAExperiments.ar.uba.dc.lafhis.experiments.jung;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSAExperiments.ar.uba.dc.lafhis.experiments.exchange.JSONAwareMultiGraph;
import MTSSynthesis.controller.model.LabelledGame;
import MTSSynthesis.controller.model.LabelledGameSolver;
import MTSSynthesis.controller.model.gr.GRGoal;
import edu.uci.ics.jung.graph.util.EdgeType;

public class ExperimentJUNGHelper {

	private static ExperimentJUNGHelper instance;

	public static ExperimentJUNGHelper getInstance() {
		if (instance == null)
			instance = new ExperimentJUNGHelper();
		return instance;
	}
	
	public JSONAwareMultiGraph<ExperimentJUNGGameNodeValue<Long>, ExperimentJUNGGameEdgeValue<String,Long>> getLTSGraph(LTS<Long, String> lts
			, Set<String> controllableActions){
		
		JSONAwareMultiGraph<ExperimentJUNGGameNodeValue<Long>, ExperimentJUNGGameEdgeValue<String,Long>> returnGraph = new JSONAwareMultiGraph<ExperimentJUNGGameNodeValue<Long>, ExperimentJUNGGameEdgeValue<String,Long>>();
		
		ExperimentJUNGGameNodeValue<Long> currentNode		= null;
		ExperimentJUNGGameEdgeValue<String,Long> currentEdge		= null;
		
		List<ExperimentJUNGGameFluent> currentFluents		= null;
		
		Map<Long, ExperimentJUNGGameNodeValue<Long>> game2Graph = new HashMap<Long, ExperimentJUNGGameNodeValue<Long>>(lts.getStates().size());
		
		//add nodes
		for(Long state: lts.getStates()){
			currentFluents	= new ArrayList<ExperimentJUNGGameFluent>();
			currentNode 	= new ExperimentJUNGGameNodeValue<Long>(state, currentFluents
					, true, lts.getInitialState() == state);
			
			returnGraph.addVertex(currentNode);
			
			game2Graph.put(state, currentNode);
		}
		
		
		
		String label;
		//add edges
		for(Long state: lts.getStates()){
			for(Pair<String, Long> neighbour: lts.getTransitions(state)){
				label = neighbour.getFirst();
				currentEdge	= new ExperimentJUNGGameEdgeValue<String,Long>(label, state, neighbour.getSecond(), controllableActions.contains(label));
				if(!returnGraph.addEdge(currentEdge, game2Graph.get(state), game2Graph.get(neighbour.getSecond()), EdgeType.DIRECTED))
					System.out.println("graph is not being modified\n");
			}
		}
		return returnGraph;
	}	
	
	public JSONAwareMultiGraph<ExperimentJUNGGameNodeValue<Long>, ExperimentJUNGGameEdgeValue<String,Long>> getGameGraph(LabelledGameSolver<Long, String, Integer> gameSolver, GRGoal<Long> grGoal, Set<Long> problemWinningStates){
	
		LabelledGame<Long, String> game						= gameSolver.getLabelledGame();
		
		JSONAwareMultiGraph<ExperimentJUNGGameNodeValue<Long>, ExperimentJUNGGameEdgeValue<String,Long>> returnGraph = new JSONAwareMultiGraph<ExperimentJUNGGameNodeValue<Long>, ExperimentJUNGGameEdgeValue<String,Long>>();
		
		ExperimentJUNGGameNodeValue<Long> currentNode		= null;
		ExperimentJUNGGameEdgeValue<String,Long> currentEdge		= null;
		
		List<ExperimentJUNGGameFluent> currentFluents		= null;
		
		Set<Long> initialStates								= game.getInitialStates();
		Set<Long> winningStates								= problemWinningStates;
		
		Map<Long, ExperimentJUNGGameNodeValue<Long>> game2Graph = new HashMap<Long, ExperimentJUNGGameNodeValue<Long>>(game.getStates().size());
		
		int assSize		= grGoal.getAssumptionsQuantity();
		int goalSize	= grGoal.getGuaranteesQuantity();
		
		//add nodes
		for(Long state: game.getStates()){
			currentFluents	= new ArrayList<ExperimentJUNGGameFluent>(assSize + goalSize);
			for(int i = 0; i < assSize; i++){
				currentFluents.add(i,new ExperimentJUNGGameFluent("As " + i
						,grGoal.getAssumption(i+1).contains(state)? true : false));
			}
			for(int i = 0; i < goalSize; i++){
				currentFluents.add(i + assSize,new ExperimentJUNGGameFluent("G " + i
						,grGoal.getGuarantee(i+1).contains(state)? true : false));
			}
			currentNode 	= new ExperimentJUNGGameNodeValue<Long>(state, currentFluents
					, winningStates.contains(state), initialStates.contains(state));
			
			returnGraph.addVertex(currentNode);
			
			game2Graph.put(state, currentNode);
		}
		
		//add edges
		for(Long state: game.getStates()){
			for(Long goodNeighbour: game.getControllableSuccessors(state)){
				currentEdge	= new ExperimentJUNGGameEdgeValue<String,Long>(game.getLabel(state, goodNeighbour), state, goodNeighbour, true);
				returnGraph.addEdge(currentEdge, game2Graph.get(state), game2Graph.get(goodNeighbour), EdgeType.DIRECTED);
			}
			for(Long badNeighbour: game.getUncontrollableSuccessors(state)){
				currentEdge	= new ExperimentJUNGGameEdgeValue<String,Long>(game.getLabel(state, badNeighbour), state, badNeighbour, false);
				if(game2Graph.get(badNeighbour) != null)
					returnGraph.addEdge(currentEdge, game2Graph.get(state), game2Graph.get(badNeighbour), EdgeType.DIRECTED);
				else
					System.out.println("error on "+badNeighbour.toString());
			}			
		}
		return returnGraph;
	}
}
