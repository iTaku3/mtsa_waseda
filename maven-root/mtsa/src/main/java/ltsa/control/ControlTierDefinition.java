package ltsa.control;

import java.util.List;

import ltsa.lts.Symbol;

public class ControlTierDefinition
{
  private final Symbol envModel;
  private final Symbol goal;
  private List<String> initialTrace;
  
  public ControlTierDefinition(Symbol env, Symbol goal)
  {
    this.envModel = env;
    this.goal = goal;
  }

  public Symbol getEnvModel()
  {
    return envModel;
  }

  public Symbol getGoal()
  {
    return goal;
  }
  
  public void setInitialTrace(List<String> trace)
  {
    initialTrace = trace;
  }
  
  public List<String> getInitialTrace()
  {
    return initialTrace;
  }
}
