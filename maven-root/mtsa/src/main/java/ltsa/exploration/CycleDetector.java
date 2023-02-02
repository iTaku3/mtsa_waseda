package ltsa.exploration;

import java.util.List;
import java.util.ArrayList;

public class CycleDetector
{
    private List<Integer> secuence;
    private int additionalPeriods;

    //region Constructor
    public CycleDetector(List<Integer> secuence, int additionalPeriods)
    {
        this.secuence = secuence;
        this.additionalPeriods = additionalPeriods;
    }
    //endregion

    //region Public methods
    public Boolean haveCycle()
    {
        List<Integer> prediction = null;
        List<Integer> trace = new ArrayList<>();

        for (Integer current : secuence)
        {
            if (prediction == null)
            {
                if (trace.contains(current))
                    prediction = this.predict(current, trace);
            }
            else
            {
                Integer currentPredicted = prediction.remove(0);
                if (current.equals(currentPredicted))
                {
                    if (prediction.size() == 0)
                        return true;
                }
                else
                {
                    prediction = null;
                }
            }

            trace.add(current);
        }


        return false;
    }
    //endregion

    //region Private methods
    private List<Integer> predict(Integer current, List<Integer> trace)
    {
        Integer index = trace.lastIndexOf(current);
        List<Integer> prediction = trace.subList(index + 1, trace.size());

        for (int i = 0; i < this.additionalPeriods; i++)
        {
            List<Integer> period = trace.subList(index, trace.size());
            prediction.addAll(period);
        }

        List<Integer> predictionCopy = new ArrayList<>();
        predictionCopy.addAll(prediction);
        return predictionCopy;
    }
    //endregion

}
