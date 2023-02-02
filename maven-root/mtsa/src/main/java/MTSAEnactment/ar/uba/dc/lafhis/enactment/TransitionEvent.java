package MTSAEnactment.ar.uba.dc.lafhis.enactment;

import java.util.EventObject;

public class TransitionEvent<Action> extends EventObject {

	private static final long serialVersionUID = -4718320944839959506L;

	private Action action;
	
	public Action getAction(){
		return action;
	}
	
	public TransitionEvent(Object source, Action action){
		super(source);
		this.action = action;
	}
}
