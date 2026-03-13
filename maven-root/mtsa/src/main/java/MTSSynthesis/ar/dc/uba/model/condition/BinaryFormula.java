package MTSSynthesis.ar.dc.uba.model.condition;

/**
 * @author gsibay
 *
 */
public abstract class BinaryFormula implements Formula {

	private Formula leftFormula;
	private Formula rightFormula;

	public BinaryFormula(Formula leftFormula, Formula rightFormula) {
		this.leftFormula = leftFormula;
		this.rightFormula = rightFormula;
	}
	
	public Formula getLeftFormula() {
		return leftFormula;
	}

	public Formula getRightFormula() {
		return rightFormula;
	}
	
	public String toString() {
		StringBuffer result = new StringBuffer();
		result.append(this.getLeftFormula().toString()).append(" ").append(this.operatorToString())
			.append(" ").append(this.getRightFormula().toString());
		return result.toString();
	}

	/**
	 * Returns the string representation of the operator
	 * to be used for showing purposes only
	 * @return
	 */
	protected abstract String operatorToString();
	
}
