package ltsa.exploration.strategy;

import ltsa.exploration.CycleDetector;

import java.util.List;

public class StrategyManager
{
    private Strategy[] strategies;
    private int currentStrategy, additionalPeriodsToDetect;

    //region Constructor
    public StrategyManager(Strategy[] strategies)
    {
        this.currentStrategy = 0;
        this.strategies = strategies;
        this.additionalPeriodsToDetect = 1;
    }
    //endregion

    //region Public methods
    public Boolean inLoop(List<Integer> secuence)
    {
        CycleDetector cycleDetector = new CycleDetector(secuence, this.additionalPeriodsToDetect);
        Boolean loop = cycleDetector.haveCycle();
        if (loop)
            this.changeStrategy();
        return loop;
    }
    public Strategy getCurrentStrategy()
    {
        return this.strategies[this.currentStrategy];
    }
    //endregion

    //region Private methods
    private void changeStrategy()
    {
        this.currentStrategy++;
        if (this.currentStrategy == this.strategies.length)
        {
            this.currentStrategy = 0;
            this.additionalPeriodsToDetect++;
        }
    }
    //endregion
}