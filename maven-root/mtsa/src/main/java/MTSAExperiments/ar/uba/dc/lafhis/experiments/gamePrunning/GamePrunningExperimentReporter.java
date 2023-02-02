package MTSAExperiments.ar.uba.dc.lafhis.experiments.gamePrunning;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import MTSAExperiments.ar.uba.dc.lafhis.experiments.exchange.JSONAwareMultiGraph;
import MTSAExperiments.ar.uba.dc.lafhis.experiments.exchange.JSONCompatible;
import MTSAExperiments.ar.uba.dc.lafhis.experiments.exchange.JSONCompatibleBoolean;
import MTSAExperiments.ar.uba.dc.lafhis.experiments.exchange.JSONCompatibleFloat;
import MTSAExperiments.ar.uba.dc.lafhis.experiments.exchange.JSONCompatibleInt;
import MTSAExperiments.ar.uba.dc.lafhis.experiments.exchange.JSONCompatibleObject;
import MTSAExperiments.ar.uba.dc.lafhis.experiments.exchange.JSONCompatibleString;
import MTSAExperiments.ar.uba.dc.lafhis.experiments.jung.ExperimentJUNGGameEdgeValue;
import MTSAExperiments.ar.uba.dc.lafhis.experiments.jung.ExperimentJUNGGameFluent;
import MTSAExperiments.ar.uba.dc.lafhis.experiments.jung.ExperimentJUNGGameNodeValue;
import MTSAExperiments.ar.uba.dc.lafhis.experiments.ui.GraphTable;
import MTSAExperiments.ar.uba.dc.lafhis.experiments.ui.JSONCompatibleTableModel;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;

public class GamePrunningExperimentReporter {

	public static String RESULTS_FOLDER	= "../ltsa/dist/examples/SafetyCuts/Experiments/results";	
	
	public static void main(String[] args) {
		String resultsFolder	= null;
		
		if(args.length > 0)
			resultsFolder		= args[0];
		else
			resultsFolder		= RESULTS_FOLDER;
		
		File dir = new File( resultsFolder );
		
		String[] suffixes		= {"result"};
		
		Collection<File> files = FileUtils.listFiles(
				  dir, 
				  new org.apache.commons.io.filefilter.SuffixFileFilter(suffixes), 
				  DirectoryFileFilter.DIRECTORY
				);

		JSONParser parser 			= new JSONParser();
		
		List<List<JSONCompatible>> data	= new ArrayList<List<JSONCompatible>>();
		List<String> keys				= new ArrayList<String>();
		
		List<JSONCompatible> currentRow;
		
		keys.add(GamePrunningExperiment.SHORT_NAME_RESULT);
		keys.add(GamePrunningExperiment.INIT_TIME_RESULT);
		keys.add(GamePrunningExperiment.END_TIME_RESULT);
		
		keys.add(GamePrunningExperiment.INIT_STATE_RESULT);
		keys.add(GamePrunningExperiment.END_STATE_RESULT);
		keys.add(GamePrunningExperiment.ENV_STATE_RESULT);
		keys.add(GamePrunningExperiment.CTRL_STATE_RESULT);
		
		keys.add(GamePrunningExperiment.GAME_GRAPH_RESULT);
		keys.add(GamePrunningExperiment.ENV_GRAPH_RESULT);
		keys.add(GamePrunningExperiment.CTRL_GRAPH_RESULT);
		
		

		
		for(File file: files){
			try {
				JSONObject results 		= (JSONObject) parser.parse(new FileReader(file));
				
				Object short_nameObject				= results.get(GamePrunningExperiment.SHORT_NAME_RESULT);
				
				JSONCompatibleString jsonShortName 	= getJSONCompatibleStringFromJSON(results, GamePrunningExperiment.SHORT_NAME_RESULT);
				JSONCompatibleObject jsonInitTime	= getJSONCompatibleObjectFromJSON(results, GamePrunningExperiment.INIT_TIME_RESULT);
				JSONCompatibleObject jsonEndTime	= getJSONCompatibleObjectFromJSON(results, GamePrunningExperiment.END_TIME_RESULT);
				
				JSONCompatibleInt jsonInitState		= getJSONCompatibleIntFromJSON(results, GamePrunningExperiment.INIT_STATE_RESULT);
				JSONCompatibleInt jsonEndState		= getJSONCompatibleIntFromJSON(results, GamePrunningExperiment.END_STATE_RESULT);
				JSONCompatibleInt jsonEnvState		= getJSONCompatibleIntFromJSON(results, GamePrunningExperiment.ENV_STATE_RESULT);
				JSONCompatibleInt jsonCtrlState		= getJSONCompatibleIntFromJSON(results, GamePrunningExperiment.CTRL_STATE_RESULT);
				
				DirectedSparseMultigraph<ExperimentJUNGGameNodeValue<Long>, ExperimentJUNGGameEdgeValue<String,Long>> jsonGameGraph	= getDirectedGraphFromJSON(results, GamePrunningExperiment.GAME_GRAPH_RESULT);
				DirectedSparseMultigraph<ExperimentJUNGGameNodeValue<Long>, ExperimentJUNGGameEdgeValue<String,Long>> jsonEnvGraph		= getDirectedGraphFromJSON(results, GamePrunningExperiment.ENV_GRAPH_RESULT);
				DirectedSparseMultigraph<ExperimentJUNGGameNodeValue<Long>, ExperimentJUNGGameEdgeValue<String,Long>> jsonCtrlGraph	= getDirectedGraphFromJSON(results, GamePrunningExperiment.CTRL_GRAPH_RESULT);
				
				currentRow							= new ArrayList<JSONCompatible>();
				currentRow.add(jsonShortName);
				currentRow.add(jsonInitTime);
				currentRow.add(jsonEndTime);
				
				currentRow.add(jsonInitState);
				currentRow.add(jsonEndState);
				currentRow.add(jsonEnvState);
				currentRow.add(jsonCtrlState);
				
				currentRow.add(new JSONCompatibleObject(jsonGameGraph));
				currentRow.add(new JSONCompatibleObject(jsonEnvGraph));
				currentRow.add(new JSONCompatibleObject(jsonCtrlGraph));
				
				data.add(currentRow);
				/*				
				DirectedGraph<ExperimentJUNGGameNodeValue<Long>, ExperimentJUNGGameEdgeValue<String,Long>> gameGraph	= (DirectedGraph)jsonGameGraph.getValue();
				DirectedGraph<ExperimentJUNGGameNodeValue<Long>, ExperimentJUNGGameEdgeValue<String,Long>> envGraph	= (DirectedGraph)jsonEnvGraph.getValue();
				DirectedGraph<ExperimentJUNGGameNodeValue<Long>, ExperimentJUNGGameEdgeValue<String,Long>> ctrlGraph	= (DirectedGraph)jsonCtrlGraph.getValue();
				*/
			}catch(Exception e){
//				System.out.println(e.toString());
			}
			
		}
		JSONCompatibleTableModel dataModel	= new JSONCompatibleTableModel(keys, data);
		
		GraphTable graphTable				= new GraphTable(dataModel);
		
        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(graphTable);
 

        
		JFrame frame = new JFrame("Graph Visualization");
		try {
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			frame.setPreferredSize(new Dimension(screenSize.width - 200, screenSize.height - 200));			
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
			frame.getContentPane().add(scrollPane);
		} catch (Exception e) {
			e.printStackTrace();
		}
		frame.pack();
		frame.setVisible(true);	
		
	}
	
	public static JSONCompatibleString getJSONCompatibleStringFromJSON(JSONObject results, String key){
		return new JSONCompatibleString((String)((JSONObject)results.get(key)).get("value"));
	}

	public static JSONCompatibleInt getJSONCompatibleIntFromJSON(JSONObject results, String key){
		long l = (long)((JSONObject)results.get(key)).get("value");
			    if (l < Integer.MIN_VALUE) 
			        l = Integer.MIN_VALUE;
			    if(l > Integer.MAX_VALUE)
			    	l = Integer.MAX_VALUE;
		return new JSONCompatibleInt((int)l);
	}

	public static JSONCompatibleFloat getJSONCompatibleFloatFromJSON(JSONObject results, String key){
		return new JSONCompatibleFloat((float)((JSONObject)results.get(key)).get("value"));
	}	
	
	public static JSONCompatibleObject getJSONCompatibleObjectFromJSON(JSONObject results, String key){
		return new JSONCompatibleObject((Object)((JSONObject)results.get(key)).get("value"));
	}

	public static JSONCompatibleBoolean getJSONCompatibleBooleanFromJSON(JSONObject results, String key){
		return new JSONCompatibleBoolean((boolean)((JSONObject)results.get(key)).get("value"));
	}	
	
	public static DirectedSparseMultigraph<ExperimentJUNGGameNodeValue<Long>, ExperimentJUNGGameEdgeValue<String,Long>> getDirectedGraphFromJSON(JSONObject results, String key){
		JSONCompatibleObject object = getJSONCompatibleObjectFromJSON(results, key);
		JSONObject jsonObject		= (JSONObject)object.getValue();
		JSONArray verticesObject	= (JSONArray)jsonObject.get("Vertices");
		JSONArray edgesObject		= (JSONArray)jsonObject.get("Edges");
		
		JSONAwareMultiGraph<ExperimentJUNGGameNodeValue<Long>, ExperimentJUNGGameEdgeValue<String,Long>> returnGraph = new JSONAwareMultiGraph<ExperimentJUNGGameNodeValue<Long>, ExperimentJUNGGameEdgeValue<String,Long>>();
		
		ExperimentJUNGGameNodeValue<Long> currentNode		= null;
		ExperimentJUNGGameEdgeValue<String,Long> currentEdge		= null;
		JSONArray currentFluentsObject						= null;
		
		List<ExperimentJUNGGameFluent> currentFluents		= null;
		
		Map<Long, ExperimentJUNGGameNodeValue<Long>> json2Graph = new HashMap<Long, ExperimentJUNGGameNodeValue<Long>>();
		
		JSONObject vertex									= null;
		JSONObject edge										= null;
		List<ExperimentJUNGGameFluent> nodeFluents			= null;
		JSONObject currentFluent							= null;
		
		//add nodes
		for(int i = 0; i < verticesObject.size(); i++){
			vertex					= (JSONObject)verticesObject.get(i);
			currentFluentsObject	= (JSONArray)vertex.get("fluentsValuations");
			
			nodeFluents				= new ArrayList<ExperimentJUNGGameFluent>();
			
			Long vertexId			= Long.parseLong((String)vertex.get("id"));
			for(int j = 0; j < currentFluentsObject.size(); j++){
				currentFluent		= (JSONObject)currentFluentsObject.get(j);
				nodeFluents.add(new ExperimentJUNGGameFluent((String)currentFluent.get("name")
						, ((boolean)currentFluent.get("value"))));
				
			}
			
			currentNode				= new ExperimentJUNGGameNodeValue<Long>(vertexId, nodeFluents
					, (boolean)vertex.get("isWinning"), (boolean)vertex.get("isInitial"));
			
			returnGraph.addVertex(currentNode);
			
			json2Graph.put(vertexId, currentNode);
		}
		
		//add edges
		for(int i = 0; i < edgesObject.size(); i++){
			edge					= (JSONObject)edgesObject.get(i);
			Long fromState			= Long.parseLong((String)edge.get("from"));
			Long toState			= Long.parseLong((String)edge.get("to"));
			currentEdge				= new ExperimentJUNGGameEdgeValue<String,Long>((String)edge.get("id"), fromState, toState, (boolean)edge.get("isControllable"));
			
			returnGraph.addEdge(currentEdge, json2Graph.get(fromState), json2Graph.get(toState));
		}
		return returnGraph;		
	}
	
}
