package ltsa.control;

import java.util.Hashtable;

import ltsa.lts.Diagnostics;
import ltsa.lts.Symbol;

public class ControllerDefinition {
	
	private Symbol name;
	private Symbol process;
	private Symbol goal;

	public ControllerDefinition(Symbol current) {
		this.name = current;
	}
	
	private static Hashtable<String, ControllerDefinition> definitions = 
		new Hashtable<String, ControllerDefinition>();
	
	public final static void init() {
		definitions = new Hashtable<String, ControllerDefinition>();
	}

	public static boolean contains(Symbol defName) {
		return definitions.containsKey(defName);
	}

	public static void put(ControllerDefinition controller) {
    	if(definitions.put(controller.getNameString(), controller)!=null) {
                Diagnostics.fatal ("duplicate Controller definition: "+ controller.getName(), controller.getName());
    	} 
    }
    
    private String getNameString() {
		return this.getName().getName();
	}

	public static ControllerDefinition getDefinition(Symbol definitionName) {
    	ControllerDefinition definition = definitions.get(definitionName.getName());
		if(definition == null) {
			throw new IllegalArgumentException("Controller Goal definition not found: " + definitionName);
		}
		return definition;
	}

	public Symbol getName() {
		return name;
	}

	public Symbol getProcess() {
		return process;
	}

	public void setProcess(Symbol process) {
		this.process = process;
	}

	public Symbol getGoal() {
		return goal;
	}

	public void setGoal(Symbol goal) {
		this.goal = goal;
	}
}
