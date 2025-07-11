package MTSSynthesis.controller.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import MTSSynthesis.ar.dc.uba.model.condition.Fluent;
import MTSSynthesis.ar.dc.uba.model.condition.Formula;

/**
 * This Goal represents the goal as defined in the model.
 * It is basically a holder for all assumptions and fluents related to the goal.
 *
 * It does not interpret what type of controller to build.
 * @param <Action>
 */
public class ControllerGoal<Action> implements Cloneable {
	private List<Formula> faults;
	private List<Formula> assumptions;
	private List<Formula> guarantees;
	private List<Formula> buchi;

	private Set<Fluent> fluents;
	private Set<Action> controllableActions;

	private List<String> comparisonactions;////////////////////
	private Set<Fluent> fluentsInFaults;
	private boolean isPermissive;
	private boolean isNonBlocking;
	private boolean exceptionHandling;
	private Set<Fluent> concurrencyFluents;
	private Set<Fluent> activityFluents;
	private Integer lazyness;
	private boolean nonTransient;
	private boolean testLatency;
	private Integer maxControllers;
	private Integer maxSchedulers;
	private boolean reachability;
	private Set<Action> marking;
	private Set<Action> disturbances;
	
	
	public ControllerGoal() {
		this.faults = new ArrayList<>();
		this.assumptions = new ArrayList<>();
		this.guarantees = new ArrayList<>();
		this.buchi = new ArrayList<>();

		this.fluents = new HashSet<>();
		this.controllableActions = new HashSet<>();
		this.comparisonactions = new ArrayList<>();/////////////////
		this.concurrencyFluents = new HashSet<>();
		this.activityFluents = new HashSet<>();
		this.lazyness = 0;
		this.testLatency = false;
		this.maxControllers = 0;
		this.maxSchedulers = 0;
		this.reachability = false;
		this.marking = new HashSet<>();
		this.disturbances = new HashSet<>();
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		ControllerGoal<Action> clone = new ControllerGoal<>();
		clone.faults = this.faults;
		clone.assumptions = this.assumptions;
		clone.guarantees = this.guarantees;
		clone.buchi = this.buchi;
		clone.fluents = this.fluents;
		clone.controllableActions = this.controllableActions;
		clone.concurrencyFluents = this.concurrencyFluents;
		clone.activityFluents = this.activityFluents;
		clone.lazyness = this.lazyness;
		clone.nonTransient = this.nonTransient;
		clone.testLatency = this.testLatency;
		clone.maxControllers = this.maxControllers;
		clone.maxSchedulers = this.maxSchedulers;
		clone.reachability = this.reachability;
		clone.marking = this.marking;
		clone.disturbances = this.disturbances;
		return clone;
	}
	
	public ControllerGoal<Action> cloneWithAssumptionsAsGoals(){
		ControllerGoal<Action> reducedGoal;
		try {
			reducedGoal = ((ControllerGoal<Action>)this.clone());
			reducedGoal.guarantees = reducedGoal.assumptions;
			reducedGoal.assumptions = new ArrayList<Formula>();
			reducedGoal.assumptions.add(Formula.TRUE_FORMULA);
			
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			reducedGoal = this;
			e.printStackTrace();
		}		
		return reducedGoal;
	}
	
	public boolean isNonTransient() {
		return nonTransient;
	}
	
	public void setNonTransient(boolean nonTransient) {
		this.nonTransient = nonTransient;
	}
	
	public void setReachability(boolean reachability) {
		this.reachability = reachability;
	}
	
	public boolean isReachability() {
		return reachability;
	}
	
	public void setTestLatency(Integer maxSchedulers, Integer maxControllers) {
		this.testLatency = true;
		this.maxControllers = maxControllers;
		this.maxSchedulers = maxSchedulers;
	}
	
	public boolean isTestLatency() {
		return testLatency;
	}
	
	public Integer getMaxControllers() {
		return maxControllers;
	}
	
	public Integer getMaxSchedulers() {
		return maxSchedulers;
	}
	
	public boolean isNonBlocking() {
		return isNonBlocking;
	}

	public void setNonBlocking(boolean isNonBlocking) {
		this.isNonBlocking = isNonBlocking;
	}

	public boolean addAssume(Formula assume) {
		return this.assumptions.add(assume);
	}

	public boolean addGuarantee(Formula guarantee) {
		return this.guarantees.add(guarantee);
	}

	public boolean addBuchi(Formula buchi) {
		return this.buchi.add(buchi);
	}

	public boolean addAllFluents(Set<Fluent> involvedFluents) {
		return this.fluents.addAll(involvedFluents);
	}

	public Set<Fluent> getFluents() {
		return this.fluents;
	}

	public boolean addAllActivityFluents(Set<Fluent> fluents){
		return this.activityFluents.addAll(fluents);
	}
	
	public Set<Fluent> getActivityFluents() {
		return activityFluents;
	}
	
	
	public boolean addAllConcurrencyFluents(Set<Fluent> concurrencyFluents) {
		return this.concurrencyFluents.addAll(concurrencyFluents);
	}
	
	public Set<Fluent> getConcurrencyFluents() {
		return this.concurrencyFluents;
	}
	
	public List<Formula> getAssumptions() {
		return assumptions;
	}

	public List<Formula> getGuarantees() {
		return guarantees;
	}

	public List<Formula> getBuchi() {
		return buchi;
	}
	public List<String> getComparisonactions() {////////////
		//System.out.println("getCOm");
		//System.out.println(comparisonactions);
		return comparisonactions;
	}

	public void setComparisonActions(List<String> comparisonactions)//////////
	{
		this.comparisonactions = comparisonactions;
	}

	public Set<Action> getControllableActions() {
		return controllableActions;
	}

	public void setControllableActions(Set<Action> controllableActions)
	{
		this.controllableActions = controllableActions;
	}

	public void addAllControllableActions(Set<Action> controllableActions) {
		this.controllableActions.addAll(controllableActions);
	}

	public void addAllComparisonActions(List<String> comparison) {
		//System.out.println("before");
		//System.out.println(comparison);

		this.comparisonactions.addAll(comparison);
		//System.out.println("after");
		//System.out.println(comparisonactions);
	}

	public void addFault(Formula faultFormula) {
		this.faults.add(faultFormula);
	}

	public List<Formula> getFaults() {
		return this.faults;
	}

	public void addAllFluentsInFaults(Set<Fluent> fluentsInFaults) {
		this.fluentsInFaults = fluentsInFaults;
	}

	public Set<Fluent> getFluentsInFaults() {
		return this.fluentsInFaults;
	}

	public boolean isPermissive() {
		return isPermissive;
	}

	public void setPermissive(boolean isPermissive) {
		this.isPermissive = isPermissive;
	}


	public boolean isExceptionHandling() {
		return exceptionHandling;
	}


	public void setExceptionHandling(boolean exceptionHandling) {
		this.exceptionHandling = exceptionHandling;
	}

	public Integer getLazyness() {
		return lazyness;
	}
	
	public void setLazyness(Integer lazyness) {
		this.lazyness = lazyness;
	}
	
	public void setMarking(Set<Action> marking) {
		this.marking = marking;
	}

	public void setDisturbances(Set<Action> disturbances) {
		this.disturbances = disturbances;
	}

	public Set<Action> getMarking() {
		return marking;
	}

	public Set<Action> getDisturbances() {
		return disturbances;
	}

	public ControllerGoal<Action> copy()
	{
		ControllerGoal<Action> copy = new ControllerGoal<>();
		copy.isPermissive = this.isPermissive;
		copy.isNonBlocking = this.isNonBlocking;
		copy.exceptionHandling = this.exceptionHandling;
		copy.nonTransient = this.nonTransient;
		copy.lazyness = this.lazyness;
		copy.faults = copyListFormula(this.faults);
		copy.assumptions = copyListFormula(this.assumptions);
		copy.guarantees = copyListFormula(this.guarantees);
		copy.fluents = copySetaFluent(this.fluents);
		copy.fluentsInFaults = copySetaFluent(this.fluentsInFaults);
		copy.concurrencyFluents = copySetaFluent(this.concurrencyFluents);

		if (this.controllableActions == null) {
			copy.controllableActions = null;
		} else {
			copy.controllableActions = new HashSet<>(this.controllableActions);
		}

		return copy;
	}

	private List<Formula> copyListFormula(List<Formula> list)
	{
		if (list == null) {
			return null;
		}

		return new ArrayList<>(list);
	}
	private Set<Fluent> copySetaFluent(Set<Fluent> set)
	{
		if (set == null)
			return null;

		return new HashSet<>(set);
	}
}
