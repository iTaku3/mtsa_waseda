package parsing;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import tau.smlab.syntech.gameinput.model.TypeDef;
import tau.smlab.syntech.gameinput.model.Variable;

public class MyVar {
	private String name;
	private List<String> actions; 
	private TypeDef type;
	private List<String> fluentNames;
	
	public MyVar(String name, List<String> actions, TypeDef type) {
		this.name = name.substring(0, 1).toUpperCase() + name.substring(1);
		this.actions =actions;
		this.type = type;
		if(type.isBoolean()){
			this.fluentNames = new ArrayList<String>();
			this.fluentNames.add(name.toUpperCase());
		}else {
			this.fluentNames = actions.stream().map(s -> s.toUpperCase()).collect(Collectors.toList());
		}
	}
	
	public MyVar(Variable var) {
		this(var.getName(), var.getActions(), var.getType());
	}
	
	public String printActions() {
		return actions.stream().collect(Collectors.joining(", "));
	}	
	
	public String getName() {
		return this.name;
	}
	
	public List<String> getActions() {
		return this.actions;
	}
	
	public TypeDef getType() {
		return this.type;
	}
	
	public List<String> getFluents() {
		return this.fluentNames;
	}
}
