package ltsa.exploration.strategy;

import java.util.HashSet;

public abstract class Strategy
{
    public abstract String chooseNextAction(HashSet<String> availableActions);
}
