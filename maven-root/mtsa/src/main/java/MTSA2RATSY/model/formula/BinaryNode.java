package MTSA2RATSY.model.formula;

public class BinaryNode extends FormulaNode {
	protected Operator 		operator;
	protected FormulaNode	leftNode;
	protected FormulaNode	rightNode;
	protected boolean		encloseFormula;
	
	public Operator getOperator(){
		return operator;
	}
	
	public FormulaNode getLeftNode(){
		return leftNode;
	}	
	
	public FormulaNode getRightNode(){
		return rightNode;
	}	
	
	public boolean getEncloseFormula(){
		return encloseFormula;
	}

	public BinaryNode(Operator operator, FormulaNode leftNode, FormulaNode rightNode){
		this(operator, leftNode, rightNode, true);
	}

	public BinaryNode(Operator operator, FormulaNode leftNode, FormulaNode rightNode, boolean encloseFormula){
		this.operator	= operator;
		this.leftNode	= leftNode;
		this.rightNode	= rightNode;
		this.encloseFormula	= encloseFormula;
	}		
	
	@Override
	public String getDescription() {
		return "(" + leftNode.getDescription() + operator.getDescription() + rightNode.getDescription() + ")";
	}

	@Override
	public String getXMLDescription() {
		// TODO Auto-generated method stub
		return "<node>" + getDescription() + "</node>";
	}

}