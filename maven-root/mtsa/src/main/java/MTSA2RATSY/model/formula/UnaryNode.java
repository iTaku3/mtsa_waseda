package MTSA2RATSY.model.formula;

public class UnaryNode extends FormulaNode {
	protected Operator 		operator;
	protected FormulaNode	node;
	
	public Operator getOperator(){
		return operator;
	}
	
	public FormulaNode getNode(){
		return node;
	}
	
	public UnaryNode(Operator operator, FormulaNode node){
		this.operator	= operator;
		this.node		= node;
	}
	
	@Override
	public String getDescription() {
		return operator.getDescription() + "(" + node.getDescription() + ")";
	}

	@Override
	public String getXMLDescription() {
		// TODO Auto-generated method stub
		return "<node>" + getDescription() + "</node>";
	}

}
