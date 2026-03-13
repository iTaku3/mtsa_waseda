package MTSAExperiments.ar.uba.dc.lafhis.experiments.gamePrunning;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Dictionary;
import java.util.Hashtable;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import MTSAExperiments.ar.uba.dc.lafhis.experiments.ExperimentSet;
import MTSAExperiments.ar.uba.dc.lafhis.experiments.exchange.JSONCompatible;
import MTSAExperiments.ar.uba.dc.lafhis.experiments.exchange.JSONCompatibleBoolean;
import MTSAExperiments.ar.uba.dc.lafhis.experiments.exchange.JSONCompatibleString;

public class GamePrunningExperimentLauncher {
	
	public static String PARAM_FILE_NAME	= "../ltsa/dist/examples/SafetyCuts/Experiments/parameters/test0i.params";
	
	protected static ExperimentSet experimentSet;
	
	public static void main(String[] args) {
		String parametersFileName	= null;
		
		if(args.length > 0)
			parametersFileName		= args[0];
		else
			parametersFileName		= PARAM_FILE_NAME;
		
		JSONParser parser 			= new JSONParser();
		try {
			JSONObject jsonObject 		= (JSONObject) parser.parse(new FileReader(parametersFileName));

			experimentSet				= new ExperimentSet();
			
			JSONArray jsonArray			= (JSONArray)jsonObject.get("parameters");
			
			for(Object object: jsonArray){
				JSONObject jsonElement					= (JSONObject)object;
				GamePrunningExperiment firstExperiment 	= new GamePrunningExperiment((String)jsonElement.get("name"));
				
				Dictionary<String, JSONCompatible> parameters = new Hashtable<String, JSONCompatible>();
				
				parameters.put(GamePrunningExperiment.SHORT_NAME_PARAM
						, new JSONCompatibleString((String)jsonElement.get("shortName")));
				parameters.put(GamePrunningExperiment.ASSUMPTIONS_PARAM
						, new JSONCompatibleBoolean((boolean)jsonElement.get("runAssumptions")));
				parameters.put(GamePrunningExperiment.NO_G_PARAM
						, new JSONCompatibleBoolean((boolean)jsonElement.get("runNoG")));
				parameters.put(GamePrunningExperiment.GR_PARAM
						, new JSONCompatibleBoolean((boolean)jsonElement.get("runGR")));
				parameters.put(GamePrunningExperiment.RELAX_PARAM
						, new JSONCompatibleBoolean((boolean)jsonElement.get("relaxOnAssumptions")));
				parameters.put(GamePrunningExperiment.SELF_LOOP_PARAM
						, new JSONCompatibleBoolean((boolean)jsonElement.get("relaxSelfLoops")));				
				parameters.put(GamePrunningExperiment.CTRL_NAME_PARAM
						, new JSONCompatibleString((String)jsonElement.get("ctrlName")));
				parameters.put(GamePrunningExperiment.ENV_NAME_PARAM
						, new JSONCompatibleString((String)jsonElement.get("envName")));
				parameters.put(GamePrunningExperiment.EXP_NAME_PARAM
						, new JSONCompatibleString((String)jsonElement.get("expName")));
				parameters.put(GamePrunningExperiment.GOAL_NAME_PARAM
						, new JSONCompatibleString((String)jsonElement.get("goalName")));
				parameters.put(GamePrunningExperiment.LTS_LOC_PARAM
						, new JSONCompatibleString((String)jsonElement.get("ltsLocation")));
				
				experimentSet.addExperiment(firstExperiment, parameters, (String)jsonElement.get("resultDestination"));
				
			}
			
			experimentSet.runExperiments();
						
			
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  	
			
	}

}
