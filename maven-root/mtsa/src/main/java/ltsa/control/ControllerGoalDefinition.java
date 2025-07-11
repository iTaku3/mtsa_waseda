package ltsa.control;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import ltsa.lts.Diagnostics;
import ltsa.lts.Symbol;

import org.apache.commons.lang.Validate;

public class ControllerGoalDefinition {
	private Symbol name;
	private Vector<String> controllableActionSet;
	private Vector<String> comparisonActionSet;
	private List<Symbol> safetyDefinitions;
	private List<Symbol> assumeDefinitions;
	private List<Symbol> guaranteeDefinitions;
	private List<Symbol> faultsDefinitions;
	private List<Symbol> buchiDefinitions;
	private boolean exceptionHandling;
	private boolean isPermissive;
	private boolean isNonBlocking;
	private Integer lazyness;
	private boolean nonTransient;
	private boolean testLatency;
	private Integer maxControllers;
	private Integer maxSchedulers;
	private boolean reachability;
	private List<Symbol> concurrencyDefinitions;
	private List<Symbol> activityDefinitions;
	private Vector<String> markingDefinitions;
	private Vector<String> disturbanceActions;
	//TODO should remove test_PARALLEL from here. Already in MTSA
	private int parallel;

	public boolean isNonBlocking() {
		return isNonBlocking;
	}

	public void setNonBlocking(boolean isNonBlocking) {
		this.isNonBlocking = isNonBlocking;
	}

	private static Hashtable<String, ControllerGoalDefinition> definitions = new Hashtable<String, ControllerGoalDefinition>();

	public final static void init() {
		definitions = new Hashtable<String, ControllerGoalDefinition>();
	}

	public ControllerGoalDefinition(Symbol current) {
		this.name = current;
		this.safetyDefinitions = new ArrayList<Symbol>();
		this.assumeDefinitions = new ArrayList<Symbol>();
		this.guaranteeDefinitions = new ArrayList<Symbol>();
		this.faultsDefinitions = new ArrayList<Symbol>();
		this.buchiDefinitions = new ArrayList<Symbol>();
		this.isPermissive = false;
		this.concurrencyDefinitions = new ArrayList<Symbol>();
		this.activityDefinitions = new ArrayList<Symbol>();
		this.lazyness = 0;
		this.nonTransient = false;
		this.testLatency = false;
		this.maxControllers = 0;
		this.maxSchedulers = 0;
		this.reachability = false;
		//TODO should remove test_PARALLEL from here. Already in MTSA
		this.parallel = 0;
	}

	public ControllerGoalDefinition(String name) {
		this(new Symbol(Symbol.UPPERIDENT, name));
	}

	public static void put(ControllerGoalDefinition goal) {
		if (definitions.put(goal.getNameString(), goal) != null) {
			Diagnostics.fatal("duplicate Goal definition: " + goal.getName(),
					goal.getName());
		}
	}

	public static ControllerGoalDefinition getDefinition(Symbol definitionName) {
		if (definitionName == null) {
			throw new IllegalArgumentException("Missing controller goal.");
		}
		ControllerGoalDefinition definition = definitions.get(definitionName
				.getName());
		if (definition == null) {
			throw new IllegalArgumentException(
					"Controller goal definition not found: " + definitionName);
		}
		return definition;
	}

	public List<Symbol> getGuaranteeDefinitions() {
		return guaranteeDefinitions;
	}
	
	public List<Symbol> getConcurrencyDefinitions() {
		return concurrencyDefinitions;
	}
	
	public List<Symbol> getActivityDefinitions() {
		return activityDefinitions;
	}

	public List<Symbol> getFaultsDefinitions() {
		return faultsDefinitions;
	}

	public List<Symbol> getSafetyDefinitions() {
		return safetyDefinitions;
	}

	public List<Symbol> getBuchiDefinitions() {
		return buchiDefinitions;
	}

	public boolean addGuaranteeDefinition(Symbol guaranteeDefinition) {
		if (!this.guaranteeDefinitions.add(guaranteeDefinition)) {
			Diagnostics.fatal(
					"Duplicate Guarantee : " + guaranteeDefinition.getName(),
					guaranteeDefinition.getName());
			return false;
		} else {
			return true;
		}
	}
	
	public boolean addConcurrencyDefinition(Symbol concurrencyDefinition) {
		if (!this.concurrencyDefinitions.add(concurrencyDefinition)) {
			Diagnostics.fatal(
					"Duplicate Concurrency Fluent : " + concurrencyDefinition.getName(),
					concurrencyDefinition.getName());
			return false;
		} else {
			return true;
		}
	}
	
	
	public boolean addActivityDefinition(Symbol activityDefinition) {
		if (!this.activityDefinitions.add(activityDefinition)) {
			Diagnostics.fatal(
					"Duplicate Activity Fluent : " + activityDefinition.getName(),
					activityDefinition.getName());
			return false;
		} else {
			return true;
		}
	}


	public boolean addFaultDefinition(Symbol faultDefinition) {
		if (!this.faultsDefinitions.add(faultDefinition)) {
			Diagnostics.fatal("Duplicate Fault : " + faultDefinition.getName(),
					faultDefinition.getName());
			return false;
		} else {
			return true;
		}
	}

	public boolean addAssumeDefinition(Symbol assumeDefinition) {
		if (!this.assumeDefinitions.add(assumeDefinition)) {
			Diagnostics.fatal(
					"Duplicate Assumption : " + assumeDefinition.getName(),
					assumeDefinition.getName());
			return false;
		} else {
			return true;
		}
	}

	public boolean addSafetyDefinition(Symbol safetyDefinition) {
		if (!this.safetyDefinitions.add(safetyDefinition)) {
			Diagnostics.fatal("duplicate Safety requirement: "
					+ safetyDefinition.getName(), safetyDefinition.getName());
			return false;
		} else {
			return true;
		}
	}

	public boolean addBuchiDefinition(Symbol buchiDefinition) {
		if (!this.buchiDefinitions.add(buchiDefinition)) {
			Diagnostics.fatal("duplicate Buchi requirement: "
					+ buchiDefinition.getName(), buchiDefinition.getName());
			return false;
		} else {
			return true;
		}
	}
	
	public Vector<String> getMarkingDefinitions() {
		return markingDefinitions;
	}

	public Vector<String> getDisturbanceActions() {
		return disturbanceActions;
	}

	public List<Symbol> getAssumeDefinitions() {
		return assumeDefinitions;
	}

	public Symbol getName() {
		return this.name;
	}

	public String getNameString() {
		return name.getName();
	}

	public void setSafetyDefinitions(List<Symbol> safetyDefinitions) {
		this.safetyDefinitions = safetyDefinitions;
	}

	public void setAssumeDefinitions(List<Symbol> assumeDefinitions) {
		this.assumeDefinitions = assumeDefinitions;
	}

	public void setBuchiDefinitions(List<Symbol> buchiDefinitions) {
		this.buchiDefinitions = buchiDefinitions;
	}

	public void setGuaranteeDefinitions(List<Symbol> guaranteeDefinitions) {
		this.guaranteeDefinitions = guaranteeDefinitions;
	}
	
	public void setConcurrencyDefinitions(List<Symbol> concurrencyDefinitions) {
		this.concurrencyDefinitions = concurrencyDefinitions;
	}
	
	public void setActivityDefinitions(List<Symbol> activityDefinitions) {
		this.activityDefinitions = activityDefinitions;
	}
	
	public void setMarkingDefinitions(Vector<String> markingDefinitions) {
		this.markingDefinitions = markingDefinitions;
	}

	public void setDisturbanceActions(Vector<String> disturbanceActions) {
		this.disturbanceActions = disturbanceActions;
	}

	public Vector<String> getControllableActionSet() {
		return controllableActionSet;
	}
	public Vector<String> getComparisonActionSet() {
		return comparisonActionSet;
	}

	public void setControllableActionSet(Vector<String> controllable) {
		this.controllableActionSet = controllable;
	}
	public void comparisonActionSet(Vector<String> comparison) {//ここ通っている
		this.comparisonActionSet = comparison;
		//System.out.println("syhajhfa");
		//System.out.println(comparison);
	}

	public void setFaultsDefinitions(List<Symbol> faultsDefinitions) {
		this.faultsDefinitions = faultsDefinitions;
	}

	public void setPermissive() {
		this.isPermissive = true;
	}

	public boolean isPermissive() {
		return isPermissive;
	}

	public boolean isExceptionHandling() {
		return exceptionHandling;
	}

	public void setExceptionHandling(boolean exceptionHandling) {
		this.exceptionHandling = exceptionHandling;
	}

	public static void addDefinition(String name, ControllerGoalDefinition controllerGoalDef) {
		definitions.put(name, controllerGoalDef);
	}
	
	public static void setDefinitions(
			Hashtable<String, ControllerGoalDefinition> definitions) {
		ControllerGoalDefinition.definitions = definitions;
	}
	
	public void setLazyness(Integer lazyness) {
		Validate.isTrue(lazyness >= 0,"Lazyness value must be greater than zero. Where zero means no lazyness.");
		this.lazyness = lazyness;
	}
	
	public Integer getLazyness() {
		return lazyness;
	}
	
	public boolean isNonTransient() {
		return nonTransient;
	}
	
	public boolean isReachability() {
		return reachability;
	}
	
	public boolean isTestLatency() {
		return testLatency;
	}

	public void setTestLatency(boolean testLatency) {
		this.testLatency = testLatency;
	}

	/**
	 *
	 * @param maxControllers a number greater than zero.
	 *                    Use for test_latency = {int, int}
	 */
	public void setMaxControllers(Integer maxControllers) {
		Validate.isTrue(maxControllers > 0,"The number of controllers value must be greater than zero.");
		this.maxControllers = maxControllers;
	}

	/**
	 *
	 * @param maxSchedulers a number greater than zero.
	 *                    Use for test_latency = {int, int}
	 */
	public void setMaxSchedulers(Integer maxSchedulers) {
		Validate.isTrue(maxSchedulers > 0,"The number of schedulers value must be greater than zero.");
		this.maxSchedulers = maxSchedulers;
	}
	
	public Integer getMaxControllers() {
		return maxControllers;
	}
	
	public Integer getMaxSchedulers() {
		return maxSchedulers;
	}
	
	public void setReachability(boolean reachability) {
		this.reachability = reachability;
	}
	
	public void setNonTransient(boolean nonTransient) {
		this.nonTransient = nonTransient;
	}
	//TODO should remove test_PARALLEL from here. Already in MTSA
	public void setParallel(int parallel) {
		this.parallel = parallel;
	}

	/*public boolean isParallel() {
		return parallel;
	}*/
	//TODO should remove test_PARALLEL from here. Already in MTSA
	public int parallelRankingThreads() {
		return parallel;
	}
}
