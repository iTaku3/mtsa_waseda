package ltsa.lts.distribution;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import ltsa.lts.Diagnostics;
import ltsa.lts.LTSOutput;
import ltsa.lts.LabelSet;
import ltsa.lts.Symbol;

import org.apache.commons.collections15.set.UnmodifiableSet;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * @author gsibay
 *
 */
public class DistributionDefinition {

	private Set<Map.Entry<Symbol, Symbol>> componentsNameAndAlphabet;
	private Symbol systemModel;
	
	private String outputFileName;
	private Set<Symbol> componentsName;
	private Set<String> componentsNameAsString;
	
	private static Set<DistributionDefinition> definitions = new HashSet<DistributionDefinition>();
	

    public static void put(DistributionDefinition definition) {
    	if(definitions.add(definition) == false) {
                Diagnostics.fatal ("duplicate Distribution definition: " + definition.toString(), definition);
    	} 
    }
    
    public static Set<DistributionDefinition> getAllDistributionDefinitions() {
    	return UnmodifiableSet.decorate(DistributionDefinition.definitions); 
    }
    
    public Set<Symbol> getComponentsName() {
    	return UnmodifiableSet.decorate(this.componentsName);
    }
    
    public Set<String> getComponentsNameAsString() {
    	return this.componentsNameAsString;
    }

	public Set<Map.Entry<Symbol, Symbol>> getComponentsNameAndAlphabet() {
		return this.componentsNameAndAlphabet;
	}
	
    /**
     * Gets the distribution definition containing the desired component
     * @param component
     * @return
     */
    public static DistributionDefinition getDistributionDefinitionContainingComponent(Symbol component) {
    	DistributionDefinition result = null;
    	for (DistributionDefinition definition : DistributionDefinition.definitions) {
			Set<String> names = definition.getComponentsNameAsString();
			if(names.contains(component.getName())) {
				result = definition;
				break;
			}
		} 
    	if(result == null){
    		throw new IllegalArgumentException("Component " + component.toString() + " not found in any distribution.");
    	} else {
    		return result;
    	}
    }
    
	public final static void init() {
			definitions = new HashSet<DistributionDefinition>();
	}
	
	/**
	 * Calculates the alphabets from the symbols and returns the pairs: C, Alphabet. where C is the component name
	 * and Alphabet as a set of string. The alphabets are obtained from the id and should be defined elsewhere.
	 * @param output
	 * @return
	 * @throws DistributionTransformationException
	 */
	public Map<String, Set<String>> calculateAndGetComponentsNameAndAlphabet(LTSOutput output) throws DistributionTransformationException {
		// transform the components name and alphabet
		Set<Entry<Symbol, Symbol>> componentsNameAndAlphabetDef = this.getComponentsNameAndAlphabet();
		Map<String, Set<String>> alphabetsByComponentName = new HashMap<String, Set<String>>();
		Hashtable<?, ?> constants = LabelSet.getConstants();
		LabelSet labelSet;
		for (Entry<Symbol, Symbol> nameAndAlphabetDef : componentsNameAndAlphabetDef) {
			// The definition uses symbols. The symbol in the alphabet is the name of the set
			labelSet = (LabelSet) constants.get(nameAndAlphabetDef.getValue().toString());
			if (labelSet==null) {
				throw new DistributionTransformationException("Set of actions for alphabet " + nameAndAlphabetDef.getValue().toString() + 
						" of component " + nameAndAlphabetDef.getKey().toString() + " is not defined.");
			}
			Vector<String> actions = labelSet.getActions(null);
			Set<String> alphabet = new HashSet<String>(actions);
					
					
			alphabetsByComponentName.put(nameAndAlphabetDef.getKey().toString(), alphabet);
		}
		return alphabetsByComponentName;
	}

/*
	private DistributionSpecification<Long, String> transformToDistributionSpecification() throws DistributionTransformationException {
		DistributionSpecification<Long, String> specification = new DistributionSpecificationImpl<Long, String>();
		
		// transform the components name and alphabet
		Set<Entry<Symbol, Symbol>> componentsNameAndAlphabetDef = this.getComponentsNameAndAlphabet();
		Set<Pair<String, Set<String>>> componentsNameAndAlphabetSpec = new HashSet<Pair<String, Set<String>>>();
		Hashtable<?, ?> constants = LabelSet.getConstants();
		LabelSet labelSet;
		for (Entry<Symbol, Symbol> nameAndAlphabetDef : componentsNameAndAlphabetDef) {
			// The definition uses symbols. The symbol in the alphabet is the name of the set
			labelSet = (LabelSet) constants.get(nameAndAlphabetDef.getValue().toString());
			if (labelSet==null) {
				throw new DistributionTransformationException("Set of actions for alphabet " + nameAndAlphabetDef.getValue().toString() + 
						" of component " + nameAndAlphabetDef.getKey().toString() + " is not defined.");
			}
			Vector<String> actions = labelSet.getActions(null);
			Set<String> alphabet = new HashSet<String>(actions);
					
					
			Pair<String, Set<String>> nameAndAlphabetSpec = new Pair<String, Set<String>>(nameAndAlphabetDef.getKey().toString(), alphabet);
			componentsNameAndAlphabetSpec.add(nameAndAlphabetSpec);
		}
		// set the components name and alphabet
		specification.setComponentsNameAndAlphabet(componentsNameAndAlphabetSpec);
		// set the model TODO. ver como traigo algo tan complicado como esto. 
		
		
		return specification;
	}
*/
	
	/**
	 * Creates the definition. Each component is paired with the alphabet in the same position:
	 * Component in position i will have alphabet in position i
	 * @param systemMTS
	 * @param componentsName
	 * @param alphabets
	 * @param outputFileName
	 */
	public DistributionDefinition(Symbol systemMTS, List<Symbol> componentsName, 	List<Symbol> alphabets, String outputFileName) {
		this.initialise(componentsName, alphabets);
		this.systemModel = systemMTS;
		this.outputFileName = outputFileName;
	}

	/**
	 * Creates the definition. Each component is paired with the alphabet in the same position:
	 * Component in position i will have alphabet in position i
	 * @param systemMTS
	 * @param componentsName
	 * @param alphabets
	 */
	public DistributionDefinition(Symbol systemMTS, List<Symbol> componentsName, 	List<Symbol> alphabets) {
		this.initialise(componentsName, alphabets);
		this.systemModel = systemMTS;
	}
	
	@SuppressWarnings("unused")
	private DistributionDefinition() {
	}

	private void initialise(List<Symbol> componentsName, List<Symbol> alphabets) {
		Validate.isTrue(componentsName.size() == alphabets.size(), "Components and alphabets should have the same size");
		this.componentsNameAndAlphabet = new HashSet<Map.Entry<Symbol, Symbol>>();
		this.componentsName = new HashSet<Symbol>();
		this.componentsNameAsString = new HashSet<String>();
		for(int i = 0; i < componentsName.size(); i++) {
			Symbol currentComponentName = componentsName.get(i);
			Map.Entry<Symbol, Symbol> entry = new ComponentAndAlphabet(currentComponentName, alphabets.get(i));
			this.componentsNameAndAlphabet.add(entry);
			this.componentsName.add(currentComponentName);
			this.componentsNameAsString.add(currentComponentName.toString());
			
		}
	}

	/**
	 * Returns true iff there is a definition containing a component named processName
	 * @param processName
	 * @return
	 */
	public static boolean contains(Symbol processName) {
		boolean result = false;
		for (DistributionDefinition definition : DistributionDefinition.definitions) {
			Set<String> componentsName = definition.getComponentsNameAsString();
			for (String componentName : componentsName) {
				result = result || componentName.equals(processName.toString());
			}
		}
		return result;
	}
	
	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		result.append("System model: ").append(this.getSystemModel().toString()).append(".\n Distribution Specification: ").append("{ ");
		for (Map.Entry<Symbol, Symbol> componentAndAlphabet : this.componentsNameAndAlphabet) {
			result.append("<").append(componentAndAlphabet.getKey()).append(",").append(componentAndAlphabet.getValue()).append("> ");
		}
		result.append("}");
		return result.toString();
	}
	
	public Symbol getSystemModel() {
		return systemModel;
	}

	public void setSystemModel(Symbol systemModel) {
		this.systemModel = systemModel;
	}

	@Override
	public int hashCode(){
	    return new HashCodeBuilder(17, 31).append(this.getComponentsNameAsString()).toHashCode();
	}

	@Override
	public boolean equals(final Object obj){
	    if(obj instanceof DistributionDefinition){
			final DistributionDefinition other = (DistributionDefinition) obj;
	        return new EqualsBuilder()
	            .append(this.getComponentsNameAsString(), other.getComponentsNameAsString()).isEquals();
	    } else{
	        return false;
	    }
	}

	protected class ComponentAndAlphabet implements Map.Entry<Symbol, Symbol> {

		private Symbol component;
		private Symbol alphabet;
		
		public ComponentAndAlphabet(Symbol component, Symbol alphabet) {
			this.component = component;
			this.alphabet = alphabet;
		}
		
		@Override
		public Symbol getKey() {
			return this.component;
		}

		@Override
		public Symbol getValue() {
			return this.alphabet;
		}

		@Override
		public Symbol setValue(Symbol value) {
			throw new UnsupportedOperationException();
		}
		
	}
}
