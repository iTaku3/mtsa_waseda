package ltsa.lts;

import MTSSynthesis.controller.model.ControllerGoal;
import ltsa.control.ControllerGoalDefinition;
import ltsa.updatingControllers.UpdateConstants;
import ltsa.updatingControllers.structures.UpdatingControllerCompositeState;
import ltsa.updatingControllers.synthesis.UpdatingControllersUtils;

import java.util.*;

public class UpdatingControllersDefinition extends CompositionExpression {

	private Symbol oldController;
	private Symbol mapping;
	private Symbol oldGoal;
	private Symbol newGoal;
	private List<Symbol> transitionGoals;
	private Boolean nonblocking;

	public UpdatingControllersDefinition(Symbol current) {
		super();
		super.setName(current);
		oldController = new Symbol();
		mapping = new Symbol();
		transitionGoals = new ArrayList<Symbol>();
		nonblocking = false;
	}

	public Set<String> generateUpdatingControllableActions(ControllerGoalDefinition oldGoalDef,
	                                                       ControllerGoalDefinition newGoalDef) {
		Set<String> oldControllableActions = compileSet(oldGoalDef.getControllableActionSet());
		Set<String> newControllableActions = compileSet(newGoalDef.getControllableActionSet());
		Set<String> controllable = new HashSet<String>();
		controllable.addAll(oldControllableActions);
		controllable.addAll(newControllableActions);
		controllable.add(UpdateConstants.STOP_OLD_SPEC);
		controllable.add(UpdateConstants.START_NEW_SPEC);
		controllable.add(UpdateConstants.RECONFIGURE);
		return controllable;
	}

	@Override
	protected CompositeState compose(Vector<Value> actuals) {

		// OLD CONTROLLER
        CompositeState oldC = composeLTS(this.getOldController().toString());

		// MAPPING
		CompositeState mapping = composeLTS(this.getMapping().toString());

		// GOAL
		ControllerGoalDefinition oldGoalDef = ControllerGoalDefinition.getDefinition(this.getOldGoal());
		ControllerGoalDefinition newGoalDef = ControllerGoalDefinition.getDefinition(this.getNewGoal());
		Set<String> controllableSet = this.generateUpdatingControllableActions(oldGoalDef, newGoalDef);
		//Symbol controllableSetSymbol = LTSCompiler.saveControllableSet(controllableSet, this.getName().getName());
		ControllerGoal<String> grGoal = UpdatingControllersUtils.generateGRUpdateGoal(
			this, oldGoalDef, newGoalDef, controllableSet);
		ControllerGoalDefinition safetyGoal = UpdatingControllersUtils.generateSafetyGoalDef(
			this, oldGoalDef, newGoalDef, controllableSet, output);



		UpdatingControllerCompositeState ucce = new UpdatingControllerCompositeState(oldC, mapping, safetyGoal, grGoal, name.getName());
		return ucce;
	}

    private CompositeState composeLTS(String target) {
        CompositionExpression lts = LTSCompiler.getComposite(target);
        return lts.compose(null);
    }

    private HashSet<String> compileSet(Vector<String> actions) {
		if (actions == null)
			Diagnostics.fatal("Set not defined.");
		return new HashSet<String>(actions);
	}
    
	private HashSet<String> compileSet(Symbol setSymbol) {
		Hashtable<?, ?> constants = LabelSet.getConstants();
		LabelSet labelSet = (LabelSet) constants.get(setSymbol.toString());
		if (labelSet == null) {
			Diagnostics.fatal("Set not defined.");
		}
		Vector<String> actions = labelSet.getActions(null);
		return new HashSet<String>(actions);
	}

	public void setOldController(ArrayList<Symbol> oldController) {
		this.oldController = oldController.get(0);
	}

	public void setMapping(ArrayList<Symbol> mapping) {this.mapping = mapping.get(0);}

	public void addTransitionGoal(Symbol safety) {
		this.transitionGoals.add(safety);
	}

	public void setNewGoal(Symbol newGoal) {this.newGoal = newGoal; }

	public void setOldGoal(Symbol oldGoal) { this.oldGoal = oldGoal; }

	public void setNonblocking() {this.nonblocking = true; }

	public Symbol getOldController() {return oldController; }

	public Symbol getMapping() {
		return mapping;
	}

	public List<Symbol> getTransitionGoals() { return transitionGoals; }

	public Symbol getNewGoal() {
		return newGoal;
	}

	public Symbol getOldGoal() {
		return oldGoal;
	}

	public Boolean isNonblocking() {return nonblocking; }

}