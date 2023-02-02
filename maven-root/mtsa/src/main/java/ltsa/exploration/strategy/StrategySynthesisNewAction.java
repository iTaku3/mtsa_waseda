package ltsa.exploration.strategy;

import java.util.HashSet;

public class StrategySynthesisNewAction extends Strategy
{
    private StrategySynthesis strategySynthesis;
    private StrategyNewAction strategyNewAction;

    //region Constructor
    public StrategySynthesisNewAction(StrategySynthesis strategySynthesis, StrategyNewAction strategyNewAction)
    {
        this.strategySynthesis = strategySynthesis;
        this.strategyNewAction = strategyNewAction;
    }
    //endregion

    //region Overrides
    @Override
    public String chooseNextAction(HashSet<String> availableActions)
    {
        HashSet<String> controllerAvailableActions = this.strategySynthesis.getControllerAvailableActions();

        HashSet<String> controllableAviableActions = new HashSet<>();
        for (String anAction : controllerAvailableActions)
            if (availableActions.contains(anAction))
                controllableAviableActions.add(anAction);

        if (controllableAviableActions.size() == 0)
        	controllableAviableActions = availableActions;
        
        return this.strategyNewAction.chooseNextAction(controllableAviableActions);
    }
    //endregion
}