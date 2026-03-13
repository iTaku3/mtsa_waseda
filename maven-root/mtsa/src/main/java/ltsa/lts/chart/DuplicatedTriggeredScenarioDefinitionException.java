package ltsa.lts.chart;

import ltsa.lts.Symbol;

/**
 * @author gsibay
 *
 */
public class DuplicatedTriggeredScenarioDefinitionException extends Exception {

	private Symbol name;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4577264392588485006L;

	/**
	 * 
	 */
	public DuplicatedTriggeredScenarioDefinitionException(Symbol name) {
		super();
		this.name = name;
	}
	
	public Symbol getName() {
		return this.name;
	}
}
