package MTSAEnactment.ar.uba.dc.lafhis.enactment;

public interface ITransitionEventListener<Action> {
	public void handleTransitionEvent(TransitionEvent<Action> transitionEvent) throws Exception ;
}
