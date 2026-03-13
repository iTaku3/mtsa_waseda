package MTSAExperiments.ar.uba.dc.lafhis.experiments.gamePrunning;

import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.lang.NullArgumentException;
import org.json.simple.JSONObject;

import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;
import MTSTools.ac.ic.doc.mtstools.model.impl.LTSAdapter;
import MTSTools.ac.ic.doc.mtstools.model.impl.MTSAdapter;
import ltsa.ac.ic.doc.mtstools.util.fsp.AutomataToMTSConverter;
import ltsa.ac.ic.doc.mtstools.util.fsp.MTSToAutomataConverter;
import MTSAExperiments.ar.uba.dc.lafhis.experiments.Experiment;
import MTSAExperiments.ar.uba.dc.lafhis.experiments.ExperimentLTSHelper;
import MTSAExperiments.ar.uba.dc.lafhis.experiments.exchange.JSONCompatible;
import MTSAExperiments.ar.uba.dc.lafhis.experiments.exchange.JSONCompatibleBoolean;
import MTSAExperiments.ar.uba.dc.lafhis.experiments.exchange.JSONCompatibleInt;
import MTSAExperiments.ar.uba.dc.lafhis.experiments.exchange.JSONCompatibleObject;
import MTSAExperiments.ar.uba.dc.lafhis.experiments.exchange.JSONCompatibleString;
import MTSAExperiments.ar.uba.dc.lafhis.experiments.jung.ExperimentJUNGHelper;
import ltsa.control.util.ControllerUtils;
import MTSSynthesis.controller.gr.perfect.StateSpaceCuttingPerfectInfoOppositeGrControlProblem;
import MTSSynthesis.controller.model.LabelledGameSolver;
import MTSSynthesis.controller.model.ControllerGoal;
import ltsa.lts.CompactState;
import ltsa.lts.CompositeState;
import ltsa.ui.StandardOutput;


public class GamePrunningExperiment extends Experiment {

	public static String SHORT_NAME_PARAM	= "string_shortName";
	
	public static String NO_G_PARAM			= "boolean_runNoG";
	public static String ASSUMPTIONS_PARAM	= "boolean_runAssumptions";
	public static String GR_PARAM			= "boolean_runGR";
	public static String ALL_CONTROLLABLES_PARAM		= "boolean_allControllables";
	public static String RELAX_PARAM		= "boolean_relaxOnAssumptions";
	public static String SELF_LOOP_PARAM	= "boolean_relaxSelfLoops";		
	public static String LTS_LOC_PARAM		= "string_ltsLocation";
	public static String ENV_NAME_PARAM		= "string_envName";
	public static String EXP_NAME_PARAM		= "string_expName";
	public static String CTRL_NAME_PARAM	= "string_ctrlName";
	public static String GOAL_NAME_PARAM	= "string_goalName";
	
	public static String SHORT_NAME_RESULT	= "short_name";
	public static String INIT_TIME_RESULT	= "initial_time";
	public static String END_TIME_RESULT	= "final_time";
	public static String INIT_STATE_RESULT	= "initial_states";
	public static String END_STATE_RESULT	= "end_states";
	public static String ENV_STATE_RESULT	= "environment_states";
	public static String CTRL_STATE_RESULT	= "controller_sates";
	
	public static String GAME_GRAPH_RESULT	= "game_graph";
	public static String ENV_GRAPH_RESULT	= "environment_graph";
	public static String CTRL_GRAPH_RESULT	= "controller_graph";
	
	
	@Override
	public JSONObject toJSONObject() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initializeFromJSONObject(JSONObject jsonObject) {
		// TODO Auto-generated method stub

	}

	@Override
	public String toJSONString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Dictionary<String, JSONCompatible> primitiveRunExperiment(Dictionary<String, JSONCompatible> parameters) throws NullArgumentException{
		if(parameters.get(SHORT_NAME_PARAM) == null || parameters.get(NO_G_PARAM) == null || 
				parameters.get(ASSUMPTIONS_PARAM) == null || parameters.get(GR_PARAM) == null || 
				parameters.get(LTS_LOC_PARAM) == null || parameters.get(ENV_NAME_PARAM) == null || 
				parameters.get(EXP_NAME_PARAM) == null || parameters.get(CTRL_NAME_PARAM) == null || 
				parameters.get(GOAL_NAME_PARAM) == null || parameters.get(ALL_CONTROLLABLES_PARAM) == null || parameters.get(RELAX_PARAM) == null || parameters.get(SELF_LOOP_PARAM) == null){
			throw new NullArgumentException("GamePrunningExperiment::primitiveRunExperiment Input parameter missing");
		}
		
		JSONCompatibleBoolean runNoG			= (JSONCompatibleBoolean)(parameters.get(NO_G_PARAM));
		JSONCompatibleBoolean runAssumptions	= (JSONCompatibleBoolean)(parameters.get(ASSUMPTIONS_PARAM));
		JSONCompatibleBoolean runGR				= (JSONCompatibleBoolean)(parameters.get(GR_PARAM));
		
		JSONCompatibleBoolean allControllables	= (JSONCompatibleBoolean)(parameters.get(ALL_CONTROLLABLES_PARAM));
		JSONCompatibleBoolean relaxOnAssumptions= (JSONCompatibleBoolean)(parameters.get(RELAX_PARAM));
		JSONCompatibleBoolean relaxSelfLoops	= (JSONCompatibleBoolean)(parameters.get(SELF_LOOP_PARAM));
		
		JSONCompatibleString filename			= (JSONCompatibleString)(parameters.get(LTS_LOC_PARAM));
		JSONCompatibleString controllerName		= (JSONCompatibleString)(parameters.get(CTRL_NAME_PARAM));
		JSONCompatibleString environmentName	= (JSONCompatibleString)(parameters.get(ENV_NAME_PARAM));
		JSONCompatibleString expectedName		= (JSONCompatibleString)(parameters.get(EXP_NAME_PARAM));
		JSONCompatibleString goalsDefinitionName= (JSONCompatibleString)(parameters.get(GOAL_NAME_PARAM));
		
		LTS<Long, String> env = ExperimentLTSHelper.getInstance().getLTSFromFile(
				filename.getValue(), environmentName.getValue());
		ControllerGoal<String> controllerGoal = ExperimentLTSHelper
				.getInstance().getGRControllerGoalFromFile(filename.getValue(),
						controllerName.getValue());

		Set<LTS<Long, String>> safetyReqs = ExperimentLTSHelper.getInstance()
				.getSafetyProcessesFromFile(filename.getValue(), controllerName.getValue(),
						goalsDefinitionName.getValue());

		CompositeState safetyComposite = new CompositeState(
				"safetyComposite", new Vector<CompactState>());
		if (safetyReqs == null)
			System.out.println("no safetyRequirement");
		else
			for (LTS<Long, String> safetyReq : safetyReqs) {
				safetyComposite.machines.add(MTSToAutomataConverter
						.getInstance().convert(new MTSAdapter(safetyReq),
								"safety_automata"));
			}
		safetyComposite.machines.add(MTSToAutomataConverter
				.getInstance().convert(new MTSAdapter(env),
						"environment"));
		safetyComposite.compose(new StandardOutput());
		MTS<Long, String> envMTS	= AutomataToMTSConverter.getInstance().convert(safetyComposite.composition);
		env = new LTSAdapter<Long, String>(ControllerUtils.embedFluents(envMTS, controllerGoal, new StandardOutput()),
				TransitionType.POSSIBLE);
		
		MTS<Long, String> envForCtrlMTS	= AutomataToMTSConverter.getInstance().convert(safetyComposite.composition);
		
		LTS<Long, String> envForController = new LTSAdapter<Long, String>(ControllerUtils.embedFluents(envForCtrlMTS, controllerGoal, new StandardOutput()),
				TransitionType.POSSIBLE);

		Long trapState = -1L;
		
		
		results			= new Hashtable<String, JSONCompatible>();

		results.put(SHORT_NAME_RESULT, (JSONCompatibleString)(parameters.get(SHORT_NAME_PARAM)));

		//TODO: relaxonassumptions is fixed
		StateSpaceCuttingPerfectInfoOppositeGrControlProblem grControlProblem = new StateSpaceCuttingPerfectInfoOppositeGrControlProblem(
				env, controllerGoal, trapState, runNoG.getValue(), runAssumptions.getValue(), runGR.getValue(), allControllables.getValue(),
				relaxOnAssumptions.getValue(), relaxSelfLoops.getValue());
		LTS<Long,String> controller	= grControlProblem.solve();
		
		results.put(GAME_GRAPH_RESULT, new JSONCompatibleObject(
				ExperimentJUNGHelper.getInstance().getGameGraph((LabelledGameSolver<Long, String,Integer>)grControlProblem.getGameSolver(), grControlProblem.getGRGoal()
						, grControlProblem.getWinningStates())
				));
		results.put(ENV_GRAPH_RESULT, new JSONCompatibleObject(
				ExperimentJUNGHelper.getInstance().getLTSGraph(env, controllerGoal.getControllableActions())
				));		

		
		results.put(INIT_TIME_RESULT, new JSONCompatibleString(String.valueOf(new Date())));

		

		results.put(END_TIME_RESULT, new JSONCompatibleString(String.valueOf(new Date())));
		
		results.put(INIT_STATE_RESULT, new JSONCompatibleInt(grControlProblem.getGameSolver().getGame().getStates().size()));
		results.put(END_STATE_RESULT, new JSONCompatibleInt(grControlProblem.getGameSolver().getWinningStates().size()));
		results.put(ENV_STATE_RESULT, new JSONCompatibleInt(env.getStates().size()));
		results.put(CTRL_STATE_RESULT, new JSONCompatibleInt(controller.getStates().size()));
		
		results.put(CTRL_GRAPH_RESULT, new JSONCompatibleObject(
				ExperimentJUNGHelper.getInstance().getLTSGraph(controller, controllerGoal.getControllableActions())
				));
		
		return results;
	}
	
	public GamePrunningExperiment(String name){
		super(name);
	}

}
