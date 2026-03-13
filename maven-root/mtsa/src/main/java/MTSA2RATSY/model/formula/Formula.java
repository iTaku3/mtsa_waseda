package MTSA2RATSY.model.formula;

import MTSA2RATSY.model.ControllableType;
import MTSA2RATSY.model.IRATSYSpecification;

public class Formula implements IRATSYSpecification {
	protected String 			id;
	protected FormulaNode		rootNode;
	protected ControllableType	type;
	
	public String getID(){
		return id;
	}
	
	public FormulaNode getRootNode(){
		return rootNode;
	}
	
	public ControllableType getType(){
		return type;
	}
	
	public Formula(String id, FormulaNode rootNode, ControllableType type){
		this.id			= id;
		this.rootNode	= rootNode;
		this.type		= type;
	}
	
	protected String getControllableDefinition(){
		return (type.isControllable())? "G" : "A"; 
	}
	
	@Override
	public String getDescription() {
		return id + "," + getControllableDefinition() + "," +  rootNode.getDescription();
	}

	@Override
	public String getXMLDescription() {
		return "<requirement><name>" + id + "</name><property>" + rootNode.getDescription() 
			+ "</property><kind>" + getControllableDefinition() + "</kind>"
			+ "<base_automaton_name></base_automaton_name><notes></notes><toggled>1</toggled></requirement>";
	}

}
