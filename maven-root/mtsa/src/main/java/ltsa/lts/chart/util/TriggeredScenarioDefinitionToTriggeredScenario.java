package ltsa.lts.chart.util;

import MTSSynthesis.ar.dc.uba.model.condition.Fluent;
import MTSSynthesis.ar.dc.uba.model.condition.Formula;
import MTSSynthesis.ar.dc.uba.model.language.SingleSymbol;
import MTSSynthesis.ar.dc.uba.model.language.Symbol;
import MTSSynthesis.ar.dc.uba.model.lsc.Chart;
import MTSSynthesis.ar.dc.uba.model.lsc.ChartLanguageGenerationException;
import MTSSynthesis.ar.dc.uba.model.lsc.ExistentialTriggeredScenario;
import MTSSynthesis.ar.dc.uba.model.lsc.LocationNamingStrategy;
import MTSSynthesis.ar.dc.uba.model.lsc.TriggeredScenario;
import MTSSynthesis.ar.dc.uba.model.lsc.UniversalTriggeredScenario;
import ltsa.lts.chart.ConditionDefinition;
import ltsa.lts.chart.ConditionLocation;
import ltsa.lts.chart.Interaction;
import ltsa.lts.chart.Location;
import ltsa.lts.chart.TriggeredScenarioDefinition;
import ltsa.lts.ltl.FormulaFactory;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Singleton.
 * Takes a TriggeredScenarioDefinition from MTSA and
 * creates an TriggeredScenario to be used by the Synthesiser.
 * 
 * @author gsibay
 *
 */
public class TriggeredScenarioDefinitionToTriggeredScenario {

	static private TriggeredScenarioDefinitionToTriggeredScenario instance = new TriggeredScenarioDefinitionToTriggeredScenario();
	static public TriggeredScenarioDefinitionToTriggeredScenario getInstance() { return instance; }

	private TriggeredScenarioDefinitionToTriggeredScenario() {}
	
	public TriggeredScenario transformExistentialTriggeredScenario(TriggeredScenarioDefinition triggeredScenario, LocationNamingStrategy namingStrategy) 
		throws TriggeredScenarioTransformationException {
		// Fluents involved in the TriggeredScenario
		Set<Fluent> involvedFluents = new HashSet<Fluent>();
		
		Chart prechart, mainchart;
		try {
			prechart = new Chart(
					this.transformLocations(triggeredScenario.getPrechart().getLocations().iterator(), triggeredScenario, involvedFluents),
					namingStrategy);
			mainchart = new Chart(
				this.transformLocations(triggeredScenario.getMainchart().getLocations().iterator(), triggeredScenario, involvedFluents),
				namingStrategy);
		
		} catch (ChartLanguageGenerationException e) {
			throw new TriggeredScenarioTransformationException(e);
		}
		
		Set<Symbol> restricted = this.transformRestricted(triggeredScenario.getRestricted(), namingStrategy);
		
		return new ExistentialTriggeredScenario(prechart, mainchart, restricted, involvedFluents);

	}
	
	public TriggeredScenario transformUniversalTriggeredScenario(TriggeredScenarioDefinition uTS, LocationNamingStrategy namingStrategy) throws TriggeredScenarioTransformationException {
		// Fluents involved in the TriggeredScenario
		Set<Fluent> involvedFluents = new HashSet<Fluent>();
		
		Chart prechart, mainchart;
		try {
			prechart = new Chart(
					this.transformLocations(uTS.getPrechart().getLocations().iterator(), uTS, involvedFluents),
					namingStrategy);
			mainchart = new Chart(
				this.transformLocations(uTS.getMainchart().getLocations().iterator(), uTS, involvedFluents),
				namingStrategy);
		
		} catch (ChartLanguageGenerationException e) {
			throw new TriggeredScenarioTransformationException(e);
		}
		
		Set<Symbol> restricted = this.transformRestricted(uTS.getRestricted(), namingStrategy);
		
		return new UniversalTriggeredScenario(prechart, mainchart, restricted, involvedFluents);
	}
	
	/**
	 * Transforms the restricted interactions to a set of
	 * restricted Symbol
	 * @param restricteds
	 * @param namingStrategy
	 * @return
	 */
	private Set<Symbol> transformRestricted(Set<Interaction> restricteds,
			LocationNamingStrategy namingStrategy) {
		Set<Symbol> restrictedSymbols = new HashSet<Symbol>();

		for (Interaction restricted : restricteds) {
			MTSSynthesis.ar.dc.uba.model.lsc.Interaction interaction = new MTSSynthesis.ar.dc.uba.model.lsc.Interaction(restricted.getSource(),
					restricted.getMessage(), restricted.getTarget());
			restrictedSymbols.add(new SingleSymbol(interaction.getName(namingStrategy)));
		}
		
		return restrictedSymbols;
	}

	/**
	 * Transforms each Location and returns the transformed List
	 * in the order returned by the Iterator
	 * @param locations
	 * @param lscDefinition 
	 * @param involvedFluents
	 * @return
	 */
	private List<MTSSynthesis.ar.dc.uba.model.lsc.Location> transformLocations(
			Iterator<? extends Location > locations, TriggeredScenarioDefinition lscDefinition, Set<Fluent> involvedFluents) {
		List<MTSSynthesis.ar.dc.uba.model.lsc.Location> result = new LinkedList<MTSSynthesis.ar.dc.uba.model.lsc.Location>();
		while (locations.hasNext()) {
			Location location = locations.next();
			// in involved fluents the fluent acociated with a location condition will be added
			result.add(this.transformLocation(location, lscDefinition, involvedFluents));
		}
		return result;
	}

	/** 
	 * @param location
	 * @param lscDefinition 
	 * @param involvedFluents 
	 * @return
	 */
	private MTSSynthesis.ar.dc.uba.model.lsc.Location transformLocation(
			Location location, TriggeredScenarioDefinition lscDefinition, Set<Fluent> involvedFluents) {
		if(Location.isConditionPredicate.evaluate(location)) {
			// If it's a condition location the formula must be transformed
			ConditionLocation conditionLocation = (ConditionLocation) location;
			ConditionDefinition conditionDefinition = lscDefinition.getConditionDefinition(conditionLocation.getId());
			Formula formula = FormulaUtils.adaptFormulaAndCreateFluents(conditionDefinition.getFplFormula().expand(new
					FormulaFactory(), new Hashtable(), new Hashtable()), involvedFluents);
			return new MTSSynthesis.ar.dc.uba.model.lsc.ConditionLocation(conditionLocation.getId(), conditionLocation.getInstances(), formula);
		} else if(!Location.isConditionPredicate.evaluate(location)) {
			Interaction interaction = (Interaction) location;
			return new MTSSynthesis.ar.dc.uba.model.lsc.Interaction(interaction.getSource(), interaction.getMessage(), interaction.getTarget());
		} else {
			throw new IllegalArgumentException("Unknown location type: " + location); 
		}
	}
}
