package MTSATests.controller;

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
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class LTSTestHelper {

	private static LTSTestHelper instance;

	public static LTSTestHelper getInstance() {
		if (instance == null)
			instance = new LTSTestHelper();
		return instance;
	}

	protected CompositeState getCompositeFromFile(String ltsFilename,
			String controllerName) {
		File ltsFile = new File(ltsFilename);
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

	protected LTS<Long, String> getLTSFromFile(String ltsFilename,
			String modelName) {
		// parse input file
		try {
			LTSInput input = getLtsInput(ltsFilename);

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


	protected Set<LTS<Long, String>> getSafetyProcessesFromFile(
			String ltsFilename, String controllerName, String controllerGoalName) {
		// parse input file
		try {
			LTSInput input = getLtsInput(ltsFilename);

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

			Set<LTS<Long, String>> safetyReqs = new HashSet<LTS<Long, String>>();
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
				safetyReqs.add(safe);
			}

			return safetyReqs;
		} catch (Exception e) {
			return null;
		}
	}

	protected Set<String> getControllableActionsFromFile(String ltsFilename,
			String controllerName) {
		// parse input file
		try {
			LTSInput input = getLtsInput(ltsFilename);
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

	protected GRGoal<Long> getGRGoalFromFile(String ltsFilename,
			String controllerName) {
		// parse input file
		try {
			LTSInput input = getLtsInput(ltsFilename);
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

	protected ControllerGoal<String> getGRControllerGoalFromFile(
			String ltsFilename, String controllerName) {
		// parse input file
		try {
			LTSInput input = getLtsInput(ltsFilename);
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

	public LTSInput getLtsInput(String ltsFilename) throws IOException {
		String resourceFolder = new File(".").getCanonicalPath();
		File ltsFile = new File(resourceFolder + "/src/test/resources/" + ltsFilename);
		return new FileInput(ltsFile);
	}

	protected void removeUnusedActionsFromLTS(LTS<Long, String> machine) {
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
