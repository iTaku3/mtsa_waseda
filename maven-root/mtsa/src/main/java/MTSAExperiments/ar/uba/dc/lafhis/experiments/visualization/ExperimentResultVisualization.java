package MTSAExperiments.ar.uba.dc.lafhis.experiments.visualization;

import MTSAExperiments.ar.uba.dc.lafhis.experiments.exchange.JSONCompatible;
import javax.swing.JComponent;

public abstract class ExperimentResultVisualization {
  protected JSONCompatible jsonValue;
  protected JComponent visualComponent;

  public JComponent getVisualComponent() throws Exception {
    if (visualComponent == null) visualComponent = buildComponent(jsonValue);
    return visualComponent;
  }

  public ExperimentResultVisualization(JSONCompatible value) throws Exception {
    this.jsonValue = value;
  }

  protected abstract JComponent buildComponent(JSONCompatible value) throws Exception;
}
