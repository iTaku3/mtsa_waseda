package MTSA2RATSY.model;

import MTSA2RATSY.model.formula.Formula;
import MTSA2RATSY.model.signal.Signal;

import java.util.HashSet;
import java.util.Set;

public class SymbolicSpecification implements IRATSYSpecification {

	protected Set<Signal> environmentSignals;
	protected Set<Signal> systemSignals;
	
	protected Set<Formula> environmentInitialFormulas;
	protected Set<Formula> systemInitialFormulas;	
	
	protected Set<Formula> environmentSafetyFormulas;
	protected Set<Formula> systemSafetyFormulas;
	
	protected Set<Formula> environmentLivenessFormulas;
	protected Set<Formula> systemLivenessFormulas;	
	
	protected <A> Set<A> copySet(Set<A> originalSet){
		Set<A> newSet	= new HashSet<A>();
		
		for(A a : originalSet){
			newSet.add(a);
		}
		
		return newSet;
	}
	
	protected <A> boolean setRemove(Set<A> originalSet, A element){
		if(!originalSet.contains(element))
			return false;
		originalSet.remove(element);
		return true;
	}
	
	protected <A> boolean setAdd(Set<A> originalSet, A element){
		if(originalSet.contains(element))
			return false;
		originalSet.add(element);
		return true;
	}	
	
	public Set<Signal> getEnvironmentSignalsCopy(){ return copySet(environmentSignals);}
	public boolean containsEnvironmentSignal(Signal signal){ if(signal.getType().isControllable())return false; return environmentSignals.contains(signal);}
	public boolean addEnvironmentSignal(Signal signal){ if(signal.getType().isControllable())return false; return setAdd(environmentSignals, signal);}
	public boolean removeEnvironmentSignal(Signal signal){ if(signal.getType().isControllable())return false; return setRemove(environmentSignals, signal);}

	public Set<Formula> getEnvironmentInitialFormulasCopy(){ return copySet(environmentInitialFormulas);}
	public boolean containsEnvironmentInitialFormula(Formula formula){ if(formula.getType().isControllable())return false; return environmentInitialFormulas.contains(formula);}
	public boolean addEnvironmentInitialFormula(Formula formula){ if(formula.getType().isControllable())return false; return setAdd(environmentInitialFormulas, formula);}
	public boolean removeEnvironmentInitialFormula(Formula formula){ if(formula.getType().isControllable())return false; return setRemove(environmentInitialFormulas, formula);}

	public Set<Formula> getEnvironmentSafetyFormulasCopy(){ return copySet(environmentSafetyFormulas);}
	public boolean containsEnvironmentSafetyFormula(Formula formula){ if(formula.getType().isControllable())return false; return environmentSafetyFormulas.contains(formula);}
	public boolean addEnvironmentSafetyFormula(Formula formula){ if(formula.getType().isControllable())return false; return setAdd(environmentSafetyFormulas, formula);}
	public boolean removeEnvironmentSafetyFormula(Formula formula){ if(formula.getType().isControllable())return false; return setRemove(environmentSafetyFormulas, formula);}

	public Set<Formula> getEnvironmentLivenessFormulasCopy(){ return copySet(environmentLivenessFormulas);}
	public boolean containsEnvironmentLivenessFormula(Formula formula){ if(formula.getType().isControllable())return false; return environmentLivenessFormulas.contains(formula);}
	public boolean addEnvironmentLivenessFormula(Formula formula){ if(formula.getType().isControllable())return false; return setAdd(environmentLivenessFormulas, formula);}
	public boolean removeEnvironmentLivenessFormula(Formula formula){ if(formula.getType().isControllable())return false; return setRemove(environmentLivenessFormulas, formula);}

	public Set<Signal> getSystemSignalsCopy(){ return copySet(systemSignals);}
	public boolean containsSystemSignal(Signal signal){ if(!signal.getType().isControllable())return false; return systemSignals.contains(signal);}
	public boolean addSystemSignal(Signal signal){ if(!signal.getType().isControllable())return false; return setAdd(systemSignals, signal);}
	public boolean removeSystemSignal(Signal signal){ if(!signal.getType().isControllable())return false; return setRemove(systemSignals, signal);}

	public Set<Formula> getSystemInitialFormulasCopy(){ return copySet(systemInitialFormulas);}
	public boolean containsSystemInitialFormula(Formula formula){ if(!formula.getType().isControllable())return false; return systemInitialFormulas.contains(formula);}
	public boolean addSystemInitialFormula(Formula formula){ if(!formula.getType().isControllable())return false; return setAdd(systemInitialFormulas, formula);}
	public boolean removeSystemInitialFormula(Formula formula){ if(!formula.getType().isControllable())return false; return setRemove(systemInitialFormulas, formula);}

	public Set<Formula> getSystemSafetyFormulasCopy(){ return copySet(systemSafetyFormulas);}
	public boolean containsSystemSafetyFormula(Formula formula){ if(!formula.getType().isControllable())return false; return systemSafetyFormulas.contains(formula);}
	public boolean addSystemSafetyFormula(Formula formula){ if(!formula.getType().isControllable())return false; return setAdd(systemSafetyFormulas, formula);}
	public boolean removeSystemSafetyFormula(Formula formula){ if(!formula.getType().isControllable())return false; return setRemove(systemSafetyFormulas, formula);}

	public Set<Formula> getSystemLivenessFormulasCopy(){ return copySet(systemLivenessFormulas);}
	public boolean containsSystemLivenessFormula(Formula formula){ if(!formula.getType().isControllable())return false; return systemLivenessFormulas.contains(formula);}
	public boolean addSystemLivenessFormula(Formula formula){ if(!formula.getType().isControllable())return false; return setAdd(systemLivenessFormulas, formula);}
	public boolean removeSystemLivenessFormula(Formula formula){ if(!formula.getType().isControllable())return false; return setRemove(systemLivenessFormulas, formula);}

	public SymbolicSpecification() {
		environmentSignals		= new HashSet<Signal>();
		systemSignals			= new HashSet<Signal>();
		
		environmentInitialFormulas	= new HashSet<Formula>();
		systemInitialFormulas		= new HashSet<Formula>();
		environmentSafetyFormulas	= new HashSet<Formula>();
		systemSafetyFormulas		= new HashSet<Formula>();
		environmentLivenessFormulas	= new HashSet<Formula>();
		systemLivenessFormulas		= new HashSet<Formula>();
	}
	
	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getXMLDescription() {
		String content	= "<xml><project><signals>";
		
		for(Signal signal : environmentSignals){
			content += signal.getXMLDescription();
		}

		for(Signal signal : systemSignals){
			content += signal.getXMLDescription();
		}		
		
		content +="</signals><requirements>";
		
		for(Formula formula : environmentInitialFormulas){
			content += formula.getXMLDescription();
		}
		for(Formula formula : systemInitialFormulas){
			content += formula.getXMLDescription();
		}
		for(Formula formula : environmentSafetyFormulas){
			content += formula.getXMLDescription();
		}
		for(Formula formula : systemSafetyFormulas){
			content += formula.getXMLDescription();
		}
		for(Formula formula : environmentLivenessFormulas){
			content += formula.getXMLDescription();
		}
		for(Formula formula : systemLivenessFormulas){
			content += formula.getXMLDescription();
		}
		
		content += "</requirements>";
		
		content += "<property_assurance><possibilities/><assertions/></property_assurance><property_simulation/>"
				+ "<categories><category><name>New</name><editable>no</editable><notes>This is the category of those "
				+ "traces that have been just created</notes></category><category><name>Default</name>"
				+ "<editable>no</editable><notes>This is the default category for traces</notes></category><category>"
				+ "<name>Out of Date</name><editable>no</editable><notes>Contains the traces whose dependencies might "
				+ "be no longer consistent</notes></category><category><name>Trash</name><editable>no</editable><notes>"
				+ "Contains the traces that have been deleted</notes></category></categories><automata/>"
				+ "<active_view>ga</active_view><notes></notes></project><xml>";
		
		return content;
	}

}

