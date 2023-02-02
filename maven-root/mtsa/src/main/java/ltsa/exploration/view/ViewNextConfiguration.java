package ltsa.exploration.view;

import java.util.List;

public abstract class ViewNextConfiguration
{
    public ViewNextConfiguration()
    {

    }

    public abstract String nextAction(List<String> availableActions);
}
