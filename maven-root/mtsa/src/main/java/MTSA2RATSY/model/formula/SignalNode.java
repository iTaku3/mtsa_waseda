package MTSA2RATSY.model.formula;

import MTSA2RATSY.model.signal.Signal;

public class SignalNode extends FormulaNode {

	protected Signal signal;
	
	public Signal getSignal(){
		return signal;
	}
	
	public SignalNode(Signal signal){
		this.signal	= signal;
	}
	
	@Override
	public String getDescription() {
		return signal.getID();
	}

	@Override
	public String getXMLDescription() {
		return "<signal>" + getDescription() + "</signal>";
	}

}
