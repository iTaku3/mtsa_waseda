package MTSAExperiments.ar.uba.dc.lafhis.experiments;

import MTSAExperiments.ar.uba.dc.lafhis.experiments.exchange.JSONCompatible;
import java.util.Dictionary;

public class ExperimentExecution implements Runnable {

  protected Experiment experiment;
  protected Dictionary<String, JSONCompatible> parameters;
  protected String resultsDestination;

  public ExperimentExecution(
      Experiment experiment,
      Dictionary<String, JSONCompatible> parameters,
      String resultsDestination) {
    this.experiment = experiment;
    this.parameters = parameters;
    this.resultsDestination = resultsDestination;
  }

  @Override
  public void run() {
    experiment.runExperiment(parameters, resultsDestination);
  }

  public Dictionary<String, JSONCompatible> getParameters() {
    return parameters;
  }

  public String getResultDestination() {
    return resultsDestination;
  }
}
