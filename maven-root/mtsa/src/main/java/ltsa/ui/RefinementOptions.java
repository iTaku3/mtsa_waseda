package ltsa.ui;

import MTSTools.ac.ic.doc.mtstools.model.SemanticType;


public class RefinementOptions {
	private int refinedModel = -1;
	private int refinesModel = -1;
	private boolean bidirectionalCheck;
	private SemanticType refinementSemantic;
	
	/**
	 * @return the refinedModel
	 */
	protected int getRefinedModel() {
		return refinedModel;
	}
	/**
	 * @param refinedModel the refinedModel to set
	 */
	protected void setRefinedModel(int refinedModel) {
		this.refinedModel = refinedModel;
	}
	/**
	 * @return the refinesModel
	 */
	protected int getRefinesModel() {
		return refinesModel;
	}
	/**
	 * @param refinesModel the refinesModel to set
	 */
	protected void setRefinesModel(int refinesModel) {
		this.refinesModel = refinesModel;
	}
	/**
	 * @return the refinementSemantic
	 */
	protected SemanticType getRefinementSemantic() {
		return refinementSemantic;
	}
	/**
	 * @param refinementSemantic the refinementSemantic to set
	 */
	protected void setRefinementSemantic(SemanticType refinementSemantic) {
		this.refinementSemantic = refinementSemantic;
	}
	public boolean isValid() {
		return getRefinedModel()>=0 && getRefinesModel()>=0 && getRefinementSemantic()!=null;
	}
	/**
	 * @return the bidirectionalCheck
	 */
	protected boolean isBidirectionalCheck() {
		return bidirectionalCheck;
	}
	/**
	 * @param bidirectionalCheck the bidirectionalCheck to set
	 */
	protected void setBidirectionalCheck(boolean bidirectionalCheck) {
		this.bidirectionalCheck = bidirectionalCheck;
	}
	
}
