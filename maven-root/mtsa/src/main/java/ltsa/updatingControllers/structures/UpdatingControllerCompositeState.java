package ltsa.updatingControllers.structures;

import MTSSynthesis.controller.model.ControllerGoal;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import ltsa.control.ControllerGoalDefinition;
import ltsa.lts.CompactState;
import ltsa.lts.CompositeState;
import ltsa.lts.Symbol;
import ltsa.lts.util.MTSUtils;

import java.util.Set;
import java.util.Vector;

public class UpdatingControllerCompositeState extends CompositeState {

	private CompositeState oldController;
	private CompositeState mapping;
	private ControllerGoalDefinition updateSafetyGoals;
	private ControllerGoal<String> updateGRGoal;
	private Set<String> controllableActions;
	private MTS<Long, String> updateEnvironment;


	public UpdatingControllerCompositeState(CompositeState oldController, CompositeState mapping,
											ControllerGoalDefinition safetyGoals, ControllerGoal<String> updateGRGoal, String name) {
		super.setMachines(new Vector<CompactState>());
		this.oldController = oldController;
		this.mapping = mapping;
        this.updateSafetyGoals = safetyGoals;
		this.updateGRGoal = updateGRGoal;
		this.controllableActions = this.updateGRGoal.getControllableActions();

		super.setCompositionType(Symbol.UPDATING_CONTROLLER);
		super.name = name;
	}

	public MTS<Long, String> getUpdateController() {
		return MTSUtils.getMTSComposition(this);
	}

	public MTS<Long, String> getOldController() {
		return MTSUtils.getMTSComposition(oldController);
	}

	public MTS<Long, String> getMapping() {
		return MTSUtils.getMTSComposition(mapping);
	}

	public Set<String> getControllableActions() {
		return controllableActions;
	}

	public ControllerGoalDefinition getUpdateSafetyGoals() {
		return updateSafetyGoals;
	}

	public ControllerGoal<String> getUpdateGRGoal() {
		return updateGRGoal;
	}

	public void setUpdateEnvironment(MTS<Long, String> updateEnvironment) {
		this.updateEnvironment = updateEnvironment;
	}

	@Override
	public UpdatingControllerCompositeState clone() {
		UpdatingControllerCompositeState clone = new UpdatingControllerCompositeState(oldController, mapping, updateSafetyGoals, updateGRGoal, name);
		clone.setCompositionType(getCompositionType());
		clone.makeAbstract = makeAbstract;
		clone.makeClousure = makeClousure;
		clone.makeCompose = makeCompose;
		clone.makeDeterministic = makeDeterministic;
		clone.makeMinimal = makeMinimal;
		clone.makeControlStack = makeControlStack;
		clone.makeOptimistic = makeOptimistic;
		clone.makePessimistic = makePessimistic;
		clone.makeController = makeController;
		clone.setMakeComponent(isMakeComponent());
		clone.setComponentAlphabet(getComponentAlphabet());
		clone.goal = goal;
		clone.controlStackEnvironments = controlStackEnvironments;
		clone.controlStackSpecificTier = controlStackSpecificTier;
		clone.isProbabilistic = isProbabilistic;
		return clone;
	}

}