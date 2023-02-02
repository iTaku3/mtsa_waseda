package MTSAExperiments.ar.uba.dc.lafhis.experiments.visualization;

import javax.swing.JComponent;

import MTSAExperiments.ar.uba.dc.lafhis.experiments.exchange.JSONCompatible;

public abstract class ExperimentResultVisualization {
	protected JSONCompatible jsonValue;
	protected JComponent visualComponent;
	
	public JComponent getVisualComponent() throws Exception{
		if(visualComponent == null)
			visualComponent = buildComponent(jsonValue);
		return visualComponent;
	}
	
	public ExperimentResultVisualization(JSONCompatible value) throws Exception{
		this.jsonValue = value;
	}
	
	protected abstract JComponent buildComponent(JSONCompatible value) throws Exception;
}
