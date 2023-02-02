package ltsa.exploration.strategy;

import MTSSynthesis.controller.model.ControllerGoal;
import ltsa.exploration.Synthesis;
import ltsa.exploration.knowledge.Knowledge;

import java.util.HashSet;

public class StrategySynthesis extends Strategy
{
    private Knowledge knowledge;
    private ControllerGoal<String> goal;


    //region Constructor
    public StrategySynthesis(Knowledge knowledge, ControllerGoal<String> goal)
    {
        this.knowledge = knowledge;
        this.goal = goal;
    }
    //endregion

    //region Overrides
    @Override
    public String chooseNextAction(HashSet<String> availableActions)
    {
        HashSet<String> controllerAvailableActions = getControllerAvailableActions();
        for (String anAction : availableActions)
            for (String anotherAction : controllerAvailableActions)
                if (anAction.equals(anotherAction))
                    return anAction;
        return "";
    }
    //endregion

    //region Public methods
    public HashSet<String> getControllerAvailableActions()
    {
        Synthesis synthesis = new Synthesis(this.knowledge.cloneForSynthesisFromCurrentState(), this.goal.copy());
        return synthesis.getControllerAvailableActions();
    }
    //endregion
}