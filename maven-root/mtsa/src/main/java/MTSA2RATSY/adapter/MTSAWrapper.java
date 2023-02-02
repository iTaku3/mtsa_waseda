package MTSA2RATSY.adapter;

import MTSSynthesis.ar.dc.uba.model.condition.Fluent;
import MTSSynthesis.controller.util.GRGameBuilder;
import MTSSynthesis.controller.model.ControllerGoal;
import MTSSynthesis.controller.model.gr.GRGame;
import MTSSynthesis.controller.model.gr.GRGoal;
import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;
import MTSTools.ac.ic.doc.mtstools.model.impl.LTSAdapter;
import ltsa.ac.ic.doc.mtstools.util.fsp.AutomataToMTSConverter;
import ltsa.control.ControllerGoalDefinition;
import ltsa.dispatcher.TransitionSystemDispatcher;
import ltsa.lts.*;
import ltsa.lts.ltl.AssertDefinition;
import ltsa.ui.StandardOutput;

import java.io.File;
import java.util.*;

public class MTSAWrapper {
	protected String ltsFileLocation;
	protected String controllerName;
	protected String controllerGoalName;
	
	protected File ltsFile;
	
	public String getLtsFileLocation(){ 
		return ltsFileLocation; 
	}
	
	public String getControllerName(){ 
		return controllerName; 
	}
	
	public String getcontrollerGoalName(){ 
		return controllerGoalName; 
	}
	
	public MTSAWrapper(String ltsFileLocation, String controllerName, String controllerGoalName){
		this.ltsFileLocation	= ltsFileLocation;
		this.controllerName		= controllerName;
		this.controllerGoalName	= controllerGoalName;
		
		ltsFile = new File(ltsFileLocation);
	}

	protected CompositeState getComposite() {
		try {
			LTSInput input = new FileInput(ltsFile);
			StandardOutput output = new StandardOutput();
			String currentDirectory = (new File(".")).getCanonicalPath();
			LTSCompiler compiler = new LTSCompiler(input, output,
					currentDirectory);
			compiler.compile();

			// get model name
			CompositeState c = compiler.continueCompilation(controllerName);
			TransitionSystemDispatcher.applyComposition(c, output);
			return c;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Map<String, LTS<Long, String>> getAllLTSs() {
		Map<String, LTS<Long, String>> ltss	= new HashMap<String, LTS<Long, String>>();
		// parse input file
		try {
			StandardOutput output = new StandardOutput();
			String currentDirectory = (new File(".")).getCanonicalPath();
			
			LTSInput input = new FileInput(ltsFile);


			LTSCompiler compiler = new LTSCompiler(input, output,
					currentDirectory);
			compiler.compile();
			// get model name
			Enumeration<String> keys = compiler.getComposites().keys();
			while(keys.hasMoreElements()){
				String key	= keys.nextElement();
				ltss.put(key, getLTS(key));
			}
		} catch (Exception e) {
			return null;
		}
		return ltss;
	}	
	
	public LTS<Long, String> getLTS(String modelName) {
		// parse input file
		try {
			LTSInput input = new FileInput(ltsFile);

			StandardOutput output = new StandardOutput();
			String currentDirectory = (new File(".")).getCanonicalPath();
			LTSCompiler compiler = new LTSCompiler(input, output,
					currentDirectory);
			compiler.compile();
			// get model name
			CompactState c = compiler.getProcessCompactStateByName(modelName);
			if (c == null) {
				CompositeState compo = compiler.continueCompilation(modelName);
				compo.compose(output);
				if (compo != null)
					c = compo.composition;
			}
			// get model lts
			LTSAdapter<Long, String> env = new LTSAdapter<Long, String>(
					AutomataToMTSConverter.getInstance().convert(c),
					TransitionType.POSSIBLE);
			return env;
		} catch (Exception e) {
			return null;
		}
	}

	public Map<String, LTS<Long, String>> getSafetyProcesses() {
		// parse input file
		try {
			LTSInput input = new FileInput(ltsFile);

			StandardOutput output = new StandardOutput();
			String currentDirectory = (new File(".")).getCanonicalPath();
			LTSCompiler compiler = new LTSCompiler(input, output,
					currentDirectory);
			compiler.compile();
			compiler.continueCompilation(controllerName);
			// CompositeState c = compiler.continueCompilation(controllerName);
			// TransitionSystemDispatcher.applyComposition(c, output);
			// get model name
			Symbol controllerSymbol = new Symbol();
			controllerSymbol.setString(controllerGoalName);
			ControllerGoalDefinition goalDefinition = ControllerGoalDefinition
					.getDefinition(controllerSymbol);

			Map<String,LTS<Long, String>> safetyReqs = new HashMap<String,LTS<Long, String>>();
			
			for (Symbol safetyDef : goalDefinition.getSafetyDefinitions()) {
				CompactState c = compiler
						.getProcessCompactStateByName(safetyDef.getName());
				// TODO:what happens with assertions? they are not automata
				if (c == null)
					c = AssertDefinition.compileConstraint(output,
							safetyDef.getName());

				LTSAdapter<Long, String> safe = new LTSAdapter<Long, String>(
						AutomataToMTSConverter.getInstance().convert(c),
						TransitionType.POSSIBLE);
				safetyReqs.put(safetyDef.getName(), safe);
			}

			return safetyReqs;
		} catch (Exception e) {
			return null;
		}
	}

	public Set<String> getActions() {
		try {
			LTSInput input = new FileInput(ltsFile);
			StandardOutput output = new StandardOutput();
			String currentDirectory = (new File(".")).getCanonicalPath();
			LTSCompiler compiler = new LTSCompiler(input, output,
					currentDirectory);
			compiler.compile();

			// get model name
			CompositeState c = compiler.continueCompilation(controllerName);
			TransitionSystemDispatcher.applyComposition(c, output);
			Set<String> actions	= new HashSet<String>();
			
			String[] elements	= c.composition.getTransitionsLabels();
			for(int i = 0; i < elements.length; i++){
				actions.add(elements[i]);
			}
			return actions;
		} catch (Exception e) {
			return null;
		}

	}	
	
	public Set<String> getControllableActions() {
		try {
			LTSInput input = new FileInput(ltsFile);
			StandardOutput output = new StandardOutput();
			String currentDirectory = (new File(".")).getCanonicalPath();
			LTSCompiler compiler = new LTSCompiler(input, output,
					currentDirectory);
			compiler.compile();

			// get model name
			CompositeState c = compiler.continueCompilation(controllerName);
			TransitionSystemDispatcher.applyComposition(c, output);
			return c.goal.getControllableActions();
		} catch (Exception e) {
			return null;
		}

	}

	public GRGoal<Long> getGRGoal() {
		try {
			LTSInput input = new FileInput(ltsFile);
			StandardOutput output = new StandardOutput();
			String currentDirectory = (new File(".")).getCanonicalPath();
			LTSCompiler compiler = new LTSCompiler(input, output,
					currentDirectory);
			compiler.compile();

			// get model name
			CompositeState c = compiler.continueCompilation(controllerName);
			TransitionSystemDispatcher.applyComposition(c, output);
			GRGameBuilder<Long, String> grGameBuilder = new GRGameBuilder<Long, String>();
			GRGame<Long> grGame = grGameBuilder
					.buildGRGameFrom(AutomataToMTSConverter.getInstance()
							.convert(c.composition), c.goal);
			return grGame.getGoal();
		} catch (Exception e) {
			return null;
		}

	}

	public ControllerGoal<String> getGRControllerGoal() {
		try {
			LTSInput input = new FileInput(ltsFile);
			StandardOutput output = new StandardOutput();
			String currentDirectory = (new File(".")).getCanonicalPath();
			LTSCompiler compiler = new LTSCompiler(input, output,
					currentDirectory);
			compiler.compile();

			// get model name
			CompositeState c = compiler.continueCompilation(controllerName);
			TransitionSystemDispatcher.applyComposition(c, output);
			return c.goal;
		} catch (Exception e) {
			return null;
		}
	}
	
	public Set<Fluent> getFluents() {
		try {
			LTSInput input = new FileInput(ltsFile);
			StandardOutput output = new StandardOutput();
			String currentDirectory = (new File(".")).getCanonicalPath();
			LTSCompiler compiler = new LTSCompiler(input, output,
					currentDirectory);
			compiler.compile();

			// get model name
			CompositeState c = compiler.continueCompilation(controllerName);
			TransitionSystemDispatcher.applyComposition(c, output);
			
			return c.goal.getFluents();
		} catch (Exception e) {
			return null;
		}
	}

	public void removeUnusedActionsFromLTS(LTS<Long, String> machine) {
		Set<String> actionsToRemove = new HashSet<String>();
		// remove unused actions in order to pass equivalence check for the
		// result
		for (String action : machine.getActions()) {
			boolean hasAction = false;
			for (Long s : machine.getStates()) {
				Set<Long> image = machine.getTransitions(s).getImage(action);
				if (image != null && image != Collections.EMPTY_SET) {
					if (image.size() > 0) {
						hasAction = true;
						break;
					}
				}
			}
			if (!hasAction)
				actionsToRemove.add(action);
		}
		for (String actionToRemove : actionsToRemove)
			machine.removeAction(actionToRemove);
	}
	
}
