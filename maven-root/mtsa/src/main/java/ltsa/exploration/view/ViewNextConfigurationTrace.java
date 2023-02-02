package ltsa.exploration.view;

import java.util.List;
import java.util.Random;

public class ViewNextConfigurationTrace extends ViewNextConfiguration
{
    private int next;
    private String[] trace;
    private Random randomGenerator;

    //region Constructor
    public ViewNextConfigurationTrace(String[] trace)
    {
        super();
        this.next = 0;
        this.trace = trace;
        this.randomGenerator = new Random();
    }
    //endregion

    //region Overrides
    @Override
    public String nextAction(List<String> availableActions)
    {
        String nextInTrace = this.trace[this.next];

        this.next++;
        if (this.next == trace.length)
            this.next = 0;

        if (availableActions.contains(nextInTrace))
            return nextInTrace;
        else
            return availableActions.get(randomGenerator.nextInt(availableActions.size()));
    }
    //endregion
}
