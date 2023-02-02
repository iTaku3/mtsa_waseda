package MTSSynthesis.ar.dc.uba.model.lsc;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.ListUtils;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.SetUtils;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.NotPredicate;
import org.apache.commons.collections15.functors.TransformedPredicate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;
import MTSTools.ac.ic.doc.commons.relations.BinaryRelationUtils;
import MTSTools.ac.ic.doc.commons.relations.MapSetBinaryRelation;
import MTSSynthesis.ar.dc.uba.model.condition.Condition;
import MTSSynthesis.ar.dc.uba.model.condition.ConditionImpl;
import MTSSynthesis.ar.dc.uba.model.language.CanonicalSymbol;
import MTSSynthesis.ar.dc.uba.model.language.Language;
import MTSSynthesis.ar.dc.uba.model.language.SingleSymbol;
import MTSSynthesis.ar.dc.uba.model.language.Symbol;
import MTSSynthesis.ar.dc.uba.model.language.Word;

/**
 * Sequence Chart. 
 * The generated language is the chart's set of linearisations
 * @author gsibay
 *
 */
public class Chart {

	private static Transformer<UniqueLocation, Location> TO_LOCATION_TRANSFORMER = new Transformer<UniqueLocation, Location>(){

		@Override
		public Location transform(UniqueLocation uniqueLocation) {
			return uniqueLocation.getLocation();
		}
		
	};

	private static Predicate<UniqueLocation> IS_CONDITION_UNIQUE_LOCATION_PREDICATE = 
		new TransformedPredicate<UniqueLocation, Location>(TO_LOCATION_TRANSFORMER, Location.isConditionPredicate);
	
	/**
	 * Locations can be interactions or conditions.
	 * This list defines a partial order.
	 * All the algorithms assume that there can be, at least, one
	 * condition with no location less than it.
	 */
	private List<UniqueLocation> uniqueLocations;

	private LocationNamingStrategy locationNamingStrategy;

	private Set<List<Location>> linearisations;
	
	private Set<Condition> cachedConditions;
	
	public Chart(List<Location> locations, LocationNamingStrategy locationNamingStrategy) throws ChartLanguageGenerationException {

		// The location's name may not be unique. However when building the partial order (and using it)
		// a unique identifier is necessary. The position is used to get a unique identifier for a Location.
		this.uniqueLocations = Collections.unmodifiableList(this.createChartUniqueLocations(locations));
		this.locationNamingStrategy = locationNamingStrategy;
		this.linearisations = this.calculateLinearisations();
	}
	
	/**
	 * Returns the quantity
	 * @return
	 */
	public int getQtyInteractions() {
		return this.getInteractions().size();
	}
	
	public Set<List<Location>> getLinearisations() {
		return this.linearisations;
	}
	
	/**
	 * Return the conditions appearing in the Chart
	 * @return
	 */
	public Set<Condition> getConditions() {
		
		if(this.cachedConditions == null) {
			HashSet<Condition> conditions = new HashSet<Condition>();
			
			for (UniqueLocation uniqueLocation : this.uniqueLocations) {
				if(IS_CONDITION_UNIQUE_LOCATION_PREDICATE.evaluate(uniqueLocation)) {
					// ConditionLocation are only used for the chart.
					// Condition is used for other operations outside the chart
					ConditionLocation conditionLocation = (ConditionLocation) uniqueLocation.getLocation();
					conditions.add(this.conditionLocationToCondition(conditionLocation));
				}
			}
			this.cachedConditions = conditions;
		}
		
		return this.cachedConditions;
	}
	
	public Set<Symbol> getInteractionSymbols() {
		Set<Symbol> result = new HashSet<Symbol>();
		Set<Interaction> interactions = this.getInteractions();
		for (Interaction interaction : interactions) {
			result.add(new SingleSymbol(interaction.getName(this.locationNamingStrategy)));
		}
		return result;
	}
	
	public Set<Interaction> getInteractions() {
		List<UniqueLocation> uniqueLocationInteractions = new LinkedList<UniqueLocation>();
		CollectionUtils.select(this.getUniqueLocations(), NotPredicate.getInstance(IS_CONDITION_UNIQUE_LOCATION_PREDICATE),
				uniqueLocationInteractions);
		
		Set<Interaction> interactions = new HashSet<Interaction>();
		Iterator<UniqueLocation> uniqueLocationInteractionsIt = uniqueLocationInteractions.iterator();
		while (uniqueLocationInteractionsIt.hasNext()) {
			UniqueLocation uniqueLocationInteraction = (UniqueLocation) uniqueLocationInteractionsIt
					.next();
			interactions.add((Interaction)uniqueLocationInteraction.getLocation());
		}
		return interactions;
	}
	
	public List<Location> getLocations() {
		List<Location> locations = new LinkedList<Location>();
		CollectionUtils.collect(this.getUniqueLocations(), TO_LOCATION_TRANSFORMER, locations);
		return locations;
	}
	


	private List<UniqueLocation> getUniqueLocations() {
		return this.uniqueLocations;
	}

	/**
	 * Gets the generated language from the linearisations
	 * @return
	 */
	public Language getGeneratedLanguage() {
		Set<Word> words = new HashSet<Word>();
		
		for (List<Location> linearisation : this.getLinearisations()) {
			words.add(this.transformToWord(linearisation));
		}
		return new Language(words);
	}
	
	private Set<List<Location>> calculateLinearisations() throws ChartLanguageGenerationException {
		Set<List<Location>> linearisations = new HashSet<List<Location>>();
		
		List<UniqueLocation> uniqueLocations = this.getUniqueLocations();
		
		// Calculate the "greater than" partial order defined by the uniqueLocations.
		BinaryRelation<UniqueLocation, UniqueLocation> greaterThanPO = this
				.buildGreaterThanPartialOrder(uniqueLocations);
		
		// Get the "lower than" partial order
		BinaryRelation<UniqueLocation, UniqueLocation> lowerThanPO = BinaryRelationUtils.getInverseRelation(greaterThanPO);
		
		// Get the starting symbols (the symbols that can be at the start of a word)
		Set<UniqueLocation> firsts = this.getStartingLocationsNotViolatingASAP(uniqueLocations, greaterThanPO);

		Set<List<UniqueLocation>> wordsAsUniqueLocation = new HashSet<List<UniqueLocation>>();

		// for every possible first symbol, calculate the possible continuations and add them
		for (UniqueLocation first : firsts) {
			List<UniqueLocation> word = new LinkedList<UniqueLocation>();
			word.add(first);
			Set<UniqueLocation> remainingLocations = new HashSet<UniqueLocation>(uniqueLocations);
			remainingLocations.remove(first);
			wordsAsUniqueLocation.addAll(this.getContinuations(word, greaterThanPO, lowerThanPO, remainingLocations));
		}
		
		// The set of word of UniqueLocations have to be transformed into a linearisation
		for (List<UniqueLocation> wordAsUniqueLocation : wordsAsUniqueLocation) {
			List<Location> linearisation = new LinkedList<Location>();
			
			CollectionUtils.collect(wordAsUniqueLocation, TO_LOCATION_TRANSFORMER, linearisation);
			ListUtils.unmodifiableList(linearisation);
			
			linearisations.add(linearisation);
		}
		
		return SetUtils.unmodifiableSet(linearisations);
	}


	private Word transformToWord(List<Location> locationWord) {
		List<Symbol> symbolList = new LinkedList<Symbol>();
		for (Location location : locationWord) {
			symbolList.add(this.transformToSymbol(location));
		}
		return new Word(symbolList);
	}

	/**
	 * Transforms the Location to a Symbol
	 * according to the locationNamingStrategy
	 * @param location
	 * @return
	 */
	private Symbol transformToSymbol(Location location) {
		return new SingleSymbol(location.getName(this.locationNamingStrategy));
	}
	
	/**
	 * A chart location can have locations with the same name.
	 * The position and the name uniquely defines a positioned location.
	 * This method returns a List (in the same order as the original)
	 * with this pair representation of positioned location
	 * @return
	 */
	private List<UniqueLocation> createChartUniqueLocations(List<Location> locations) {
		List<UniqueLocation> uniqueLocations = new LinkedList<UniqueLocation>();
		 int pos = 0;
		for (Location location : locations) {
			uniqueLocations.add(new UniqueLocation(pos, location));
			pos++;
		}
		return uniqueLocations;
	}

	/**
	 * From the set of linearisation of this chart it generates
	 * and returns the set of operational words. The word contains CanonicalSymbol.
	 * 
	 * @return
	 */
	public Set<Word> calculateOperationalLanguageWords() {
		Set<Word> words = new HashSet<Word>();
		
		// Get the chart's bounded linearisations
		Set<List<Location>> linearisations = this.getLinearisations();

		// each linearisation has to be transformed into a 
		// operational word
		for (List<Location> linearisation : linearisations) {
			words.add(this.transformLinearisationToOperationalWord(linearisation));
		}
		
		return words;
	}

	/**
	 * From a linearisation creates an operational word.
	 * The first symbol is always epsilon, it will have 
	 * conditions if there are initial conditions.
	 * 
	 * @param linearisations
	 * @return
	 */
	private Word transformLinearisationToOperationalWord(List<Location> linearisation) {
		List<Symbol> symbols = new LinkedList<Symbol>();
		
		Set<String> currentConditionsNames = new HashSet<String>();
		Iterator<Location> iterator = linearisation.iterator();
		Interaction currentInteraction = null;
		
		// Get the first conditions.
		while (iterator.hasNext() && currentInteraction == null) {
			Location location = iterator.next();
			if(Location.isConditionPredicate.evaluate(location)){
				currentConditionsNames.add(((ConditionLocation)location).getConditionName());
			} else {
				// The first interaction was found. 
				currentInteraction = (Interaction) location;
			}
		}
		// The first symbol is the initial epsilon with the initial conditions
		symbols.add(new CanonicalSymbol(currentConditionsNames));
		
		if(currentInteraction != null) {
			// there are interactions, not only conditions

			// create a new currentConditions
			currentConditionsNames = new HashSet<String>();

			Symbol symbol;
			
			while(iterator.hasNext()) {
				Location location = iterator.next();
				if(Location.isConditionPredicate.evaluate(location)){
					currentConditionsNames.add(((ConditionLocation)location).getConditionName());
				} else {
					// The next interaction was found
					
					// create the symbol for the current interaction and the conditions
					symbol = this.transformToSymbol(currentInteraction);
					symbols.add(new CanonicalSymbol(symbol, currentConditionsNames));
					
					// the next interaction is now the current interaction
					currentInteraction = (Interaction) location;

					// create a new currentConditions
					currentConditionsNames = new HashSet<String>();
				}
			}
			// There are no more locations. Create the symbol with the last interaction
			// and the conditions
			symbol = this.transformToSymbol(currentInteraction);
			symbols.add(new CanonicalSymbol(symbol, currentConditionsNames));
		}
		
		return new Word(symbols);
	}
	
	private Condition conditionLocationToCondition(ConditionLocation conditionLocation) {
		return new ConditionImpl(conditionLocation.getName(this.locationNamingStrategy), 
				conditionLocation.getFormula());
	}
	
	/**
	 * From the <code>word</code>, will find all the continuations adding 
	 * <code>symbolsQtyLeft</code> symbols from <code>remainingLocations</code>
	 * using the partial order <code>greaterThanPO</code> 
	 * @param word
	 * @param greaterThanPO 
	 * @param lowerThanPO 
	 * @param remainingLocations 
	 * @param symbolsQtyLeft
	 * @return
	 */
	private Set<List<UniqueLocation>> getContinuations(List<UniqueLocation> word,
			BinaryRelation<UniqueLocation, UniqueLocation> greaterThanPO,
			BinaryRelation<UniqueLocation, UniqueLocation> lowerThanPO, Set<UniqueLocation> remainingLocations) {
		Set<List<UniqueLocation>> continuations = new HashSet<List<UniqueLocation>>();

		if (remainingLocations.isEmpty()) {
			continuations.add(word);
		} else {
			// check for every remainingLocation if it's a possible continuation of the word
			for (UniqueLocation symbol : remainingLocations) {
				// If 
				// a) the symbol is not part of the word yet
				// b) all the locations that should happen immediately before have happened
				// then the symbol can come next in the word.
				
				if( this.canComeNext(symbol, word, greaterThanPO) ) {
					// It Satisfies a) and b)

					// Add the symbol to the continuation and create a new remaining locations 
					List<UniqueLocation> continuation = new LinkedList<UniqueLocation>(word);
					Set<UniqueLocation> newRemainingLocations = new HashSet<UniqueLocation>(remainingLocations);
					 
					continuation.add(symbol);
					newRemainingLocations.remove(symbol);
					 
					// If there are Conditions that can happen next, they must (ASAP condition' semantic)
					 
					// Get the (immediately) greater conditions
					Set<UniqueLocation> greaterLocations = lowerThanPO.getImage(symbol);
					Collection<UniqueLocation> greaterConditions = new HashSet<UniqueLocation>(); 
					
					this.selectConditions(greaterLocations, greaterConditions);
					 
					while(!greaterConditions.isEmpty()) {

						// get and remove one from the greaterConditions set.
						Iterator<UniqueLocation> greaterConditionsIt = greaterConditions.iterator();
						UniqueLocation greaterCondition = greaterConditionsIt.next();
						greaterConditionsIt.remove();
						
						// if the condition can come next, it must (ASAP condition' semantic)
						if( this.canComeNext(greaterCondition, continuation, greaterThanPO)) {
							continuation.add(greaterCondition);
							newRemainingLocations.remove(greaterCondition);
							
							// If from that point on there are more conditions, add them (ASAP semantic).
							greaterLocations = lowerThanPO.getImage(greaterCondition);
							this.selectConditions(greaterLocations, greaterConditions);
						}
					}
					
					// Get the continuations from the new word, and add them.
					continuations.addAll(this.getContinuations(continuation, greaterThanPO, lowerThanPO, newRemainingLocations));
				}
			}

		}
		
		return continuations;
	}


	/**
 	 * If  
     * a) the symbol is not part of the word yet
	 * b) all the locations that should happen immediately before have happened
	 * then the symbol can come next in the word.
	 * 
	 * @param symbol
	 * @param word
	 * @param greaterThanPO
	 * @return
	 */
	private boolean canComeNext(UniqueLocation symbol,
			Collection<UniqueLocation> word, BinaryRelation<UniqueLocation, UniqueLocation> greaterThanPO) {
		Set<UniqueLocation> lowerLocations = greaterThanPO.getImage(symbol);
		return ( (!word.contains(symbol)) && CollectionUtils.isSubCollection(lowerLocations, word));
	}

	/**
	 * Gets the symbols that comes first in some
	 * word in the chart's generated language and does not
	 * violate ASAP condition' semantic
	 * @param chart
	 * @param uniqueLocations
	 * @param greaterThanPO
	 * @return
	 * @throws ChartLanguageGenerationException 
	 */
	private Set<UniqueLocation> getStartingLocationsNotViolatingASAP(
			List<UniqueLocation> uniqueLocations,
			BinaryRelation<UniqueLocation, UniqueLocation> greaterThanPO) throws ChartLanguageGenerationException {
		Set<UniqueLocation> startingLocations = this.getStartingLocations(uniqueLocations, greaterThanPO);
		
		// If there are conditions, only those are the starting symbols
		Set<UniqueLocation> startingConditions = new HashSet<UniqueLocation>();
		this.selectConditions(startingLocations, startingConditions);
		
		// If there is a starting condition, only that is the starting symbol (ASAP condition' semantic)
		if(startingConditions.isEmpty()) {
			return startingLocations;
		} else {
			return startingConditions;
		}
	}

	/**
	 * Select from the Collection the ones that 
	 * are Conditions 
	 * @param locations
	 * @return
	 */
	private void selectConditions(
			Iterable<UniqueLocation> locations, Collection<UniqueLocation> output) {
		CollectionUtils.select(locations, Chart.IS_CONDITION_UNIQUE_LOCATION_PREDICATE, output);
	}

	/**
	 * Gets the symbols that comes first in some
	 * word in the chart's generated language.
	 * @param chart
	 * @param uniqueLocations
	 * @param greaterThanPO
	 * @return
	 */
	private Set<UniqueLocation> getStartingLocations(
			List<UniqueLocation> uniqueLocations,
			BinaryRelation<UniqueLocation, UniqueLocation> greaterThanPO) {
		Set<UniqueLocation> startingLocations = new HashSet<UniqueLocation>();
		
		// The starting symbols are the ones with no element "greater than" them
		for (UniqueLocation uniqueLocation : uniqueLocations) {
			if(greaterThanPO.getImage(uniqueLocation).isEmpty()) {
				// Is not "greater than" any element. It's a starting symbol.
				startingLocations.add(uniqueLocation);
			}
		}
		return startingLocations;
	}

	
	/**
	 * Calculates the partial order defined by the chart.
	 * The interactions are considered instantaneous.
	 * The relation means <b>greater than<b>
	 * @param instances 
	 * @param prechart
	 * @return
	 */
	private BinaryRelation<UniqueLocation, UniqueLocation> buildGreaterThanPartialOrder(
			List<UniqueLocation> uniqueLocations) {
		MapSetBinaryRelation<UniqueLocation, UniqueLocation> partialOrder = 
			new MapSetBinaryRelation<UniqueLocation, UniqueLocation>();

		Set<String> instances = this.calculateInstances(uniqueLocations);
		
		// For every instance A, if the location loc1 appears immediately before
		// location loc2 in A then
		// loc2 is related to (is greater than) loc1.
		for (String instance : instances) {
			UniqueLocation loc1;
			UniqueLocation loc2;
			List<UniqueLocation> instanceLocations = this
					.getInstanceLocations(instance, uniqueLocations);

			if (!instanceLocations.isEmpty()) {
				Iterator<UniqueLocation> instanceLocationsIt = instanceLocations
						.iterator();
				loc1 = instanceLocationsIt.next();
				while (instanceLocationsIt.hasNext()) {
					loc2 = instanceLocationsIt.next();
					// loc2 is greater than loc1
					partialOrder.addPair(loc2, loc1);
					loc1 = loc2;
				}
			}
		}
		return partialOrder;
	}

	/**
	 * Calculates the instances in the Chart
	 * @return
	 */
	private Set<String> calculateInstances(Collection<UniqueLocation> locations) {
		HashSet<String> instances = new HashSet<String>();
		for (UniqueLocation uniqueLocation : locations) {
			instances.addAll(uniqueLocation.getLocation().getInstances());
		}
		return instances; 
	}

	/**
	 * Filters the list keeping just the pairs with Locations affecting the instance 
	 * 
	 * @param instance
	 * @param uniqueLocations
	 * @return
	 */
	private List<UniqueLocation> getInstanceLocations(String instance,
			List<UniqueLocation> uniqueLocations) {
		List<UniqueLocation> instanceLocations = new LinkedList<UniqueLocation>();
		for (UniqueLocation uniqueLocation : uniqueLocations) {
			if (uniqueLocation.getLocation().getInstances().contains(instance)) {
				instanceLocations.add(uniqueLocation);
			}
		}
		return instanceLocations;
	}

	public String toString() {
		return this.getUniqueLocations().toString();
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Chart) ) {
			return false;		
		}
		
		Chart chart = (Chart) obj;
		
		return new EqualsBuilder()
			.append(this.getLinearisations(), chart.getLinearisations())
			.isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder()
			.append(this.getLinearisations())
			.toHashCode();
	}
	
	private class UniqueLocation {
		
		private int position;
		private Location location;
		
		public UniqueLocation(int position, Location location) {
			this.position = position;
			this.location = location;
		}
		
		public int getPosition() {
			return this.position;
		}
		
		public Location getLocation() {
			return this.location;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!(obj instanceof UniqueLocation) ) {
				return false;		
			}
			
			UniqueLocation uniqueLocation = (UniqueLocation) obj;
			
			return new EqualsBuilder()
				.append(this.getPosition(), uniqueLocation.getPosition())
				.append(this.getLocation(), uniqueLocation.getLocation())
				.isEquals();
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			return new HashCodeBuilder()
				.append(this.getPosition())
				.append(this.getLocation())
				.toHashCode();
		}
		
		public String toString() {
			return "<" + this.getPosition() + "," + this.getLocation() + ">";
		}
	}
}
