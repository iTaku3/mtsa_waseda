package MTSSynthesis.controller.model;

import MTSTools.ac.ic.doc.mtstools.model.LTS;

public class OppositeSafeControlProblem extends
		StateSpaceCuttingControlProblem<Long, String> {

	protected boolean relaxAllControllables;
	protected boolean relaxOnAssumptions;
	protected boolean relaxSelfLoops;
	
	public OppositeSafeControlProblem(LTS<Long, String> env,
									  ControllerGoal<String> controllerGoal, Long trapState, boolean relaxAllControllables, boolean relaxOnAssumptions, boolean relaxSelfLoops) {
		super(env, controllerGoal, trapState);
		this.relaxAllControllables	= relaxAllControllables;
		this.relaxOnAssumptions = relaxOnAssumptions;
		this.relaxSelfLoops		= relaxSelfLoops;
	}

	@Override
	protected LabelledGameSolver<Long, String, Integer> buildGameSolver() {
		if(gameSolver == null)
			gameSolver = new OppositeSafeGameSolver(environment,
				controllerGoal.getControllableActions(), buildGuarantees(), buildAssumptions(), relaxAllControllables, relaxOnAssumptions, relaxSelfLoops);
		return gameSolver;
	}

}
