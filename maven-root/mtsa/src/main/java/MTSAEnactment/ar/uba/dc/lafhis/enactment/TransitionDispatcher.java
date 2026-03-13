package MTSAEnactment.ar.uba.dc.lafhis.enactment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

//TODO: distinct between controllable non controllable actions
public abstract class TransitionDispatcher<Action> {
	private String name;
	
	private List<ITransitionEventListener<Action>> listeners;
	
	public String getName(){
		return name;		
	}
	
	public TransitionDispatcher(String name){
		this.name	= name;	
		listeners	= new ArrayList<ITransitionEventListener<Action>>(0);
	}
	
	public synchronized void addTransitionEventListener(ITransitionEventListener<Action> listener){
		
		if(!listeners.contains(listener))
			listeners.add(listener);
	}
	
	public synchronized void removeTransitionEventListener(ITransitionEventListener<Action> listener){
		if(listeners.contains(listener))
			listeners.remove(listener);
	}
	
	public synchronized void removeAllTransitionEventListener()
	{
		listeners.clear();
	}
	
	public void setListeners(List<ITransitionEventListener<Action>> listeners)
	{
		this.listeners = listeners;
	}
	
	protected void fireTransitionEvent(Action action) throws Exception{
		fireTransitionEvent(new TransitionEvent<Action>(this, action));
	}
	
	protected synchronized List<ITransitionEventListener<Action>> getListeners()
	{
		return this.listeners;
	}
	
	protected void fireTransitionEvent(TransitionEvent<Action> transitionEvent) throws Exception{
		TransitionEvent<Action> event = new TransitionEvent<Action>(transitionEvent.getSource()
				, transitionEvent.getAction());
	    for(ITransitionEventListener<Action> listener : this.getListeners()){
	      listener.handleTransitionEvent(event);
	    }		
	}
}
