package ltsa.lts.chart;

import ltsa.lts.Symbol;
import ltsa.lts.chart.util.TriggeredScenarioDefinitionToTriggeredScenario;
import ltsa.lts.chart.util.TriggeredScenarioTransformationException;
import MTSSynthesis.ar.dc.uba.model.lsc.TriggeredScenario;
import MTSSynthesis.ar.dc.uba.model.lsc.LocationNamingStrategyImpl;

/**
 * @author gsibay
 *
 */
public class ExistentialTriggeredScenarioDefinition extends TriggeredScenarioDefinition {

    public static void put(TriggeredScenarioDefinition chart) {
    	throw new UnsupportedOperationException("Use the superclass to hold the definitions");
    }
    
	public ExistentialTriggeredScenarioDefinition(Symbol symbol) {
		super(symbol);
	}

	public ExistentialTriggeredScenarioDefinition(String name) {
		super(name);
	}

	@Override
	public TriggeredScenario adapt(LocationNamingStrategyImpl locationNamingStrategyImpl) throws TriggeredScenarioTransformationException {
		return TriggeredScenarioDefinitionToTriggeredScenario.getInstance().transformExistentialTriggeredScenario(this, new LocationNamingStrategyImpl());
	}
	
	
}
