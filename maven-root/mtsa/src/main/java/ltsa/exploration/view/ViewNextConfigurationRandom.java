package ltsa.exploration.view;

import java.util.List;
import java.util.Random;

public class ViewNextConfigurationRandom extends ViewNextConfiguration
{
    private Random randomGenerator;

    //region Constructor
    public ViewNextConfigurationRandom()
    {
        super();
        this.randomGenerator = new Random();
    }
    //endregion

    //region Overrides
    @Override
    public String nextAction(List<String> availableActions)
    {
        return availableActions.get(randomGenerator.nextInt(availableActions.size()));
    }
    //endregion
}
