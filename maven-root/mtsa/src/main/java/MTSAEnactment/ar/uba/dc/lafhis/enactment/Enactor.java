package MTSAEnactment.ar.uba.dc.lafhis.enactment;

import java.util.LinkedList;
import java.util.Queue;


/**
 * Represent a common base Enactor.
 * 
 * Extend this class to implement new Enactors 
 * Need to implement primitiveHandleTransitionEvent(), setUp() and tearDown() functions
 * 
 * @author Julio
 *
 * @param <State>
 * @param <Action>
 */
public abstract class Enactor<State, Action> extends TransitionDispatcher<Action> implements ITransitionEventListener<Action>, Runnable {	
	
	private Queue<TransitionEvent<Action>> transitions = new LinkedList<TransitionEvent<Action>>();
	
	private Thread thread;
	private boolean stop = false;
	private boolean reactItSelf = false;
	
	
	/**
	 * Override this function to handle trasitions events
	 * 
	 * @param transitionEvent transition
	 * @throws Exception
	 */
	protected abstract void primitiveHandleTransitionEvent(TransitionEvent<Action> transitionEvent) throws Exception ;
	
	/**
	 * This method is called before start the simulation 
	 * 
	 * 
	 */
	public abstract void setUp();
	
	/**
	 * This method is called when the simulation has been terminated
	 * 
	 * 
	 */
	public abstract void tearDown();
	
	
	
	public Enactor(String name){
		super(name);
		
	}

	@Override
	public void handleTransitionEvent(TransitionEvent<Action> transitionEvent)  throws Exception {
		Object source	= transitionEvent.getSource();
		
		if(source == this && !this.isReactItSelf()) return;

		//Encola el evento para ser procesado 
		addTransition(transitionEvent);
		
	}		
	
	private synchronized Queue<TransitionEvent<Action>> getTransitionsQueue()
	{
		return transitions;
	}
	protected synchronized void addTransition(TransitionEvent<Action> transitionEvent)
	{
//		System.out.println("Enactor (" + this.getName() + ") - add transition event: " + transitionEvent.getAction().toString());
		getTransitionsQueue().add(transitionEvent);
	}
	
	protected synchronized TransitionEvent<Action> getTransition()
	{
		
		return this.getTransitionsQueue().poll();
	}
	
	
	/**
	 * @return the stop
	 */
	public synchronized boolean isStop() {
		return stop;
	}

	public synchronized void stop() {
		this.stop = true;
	}
	
	public synchronized void start() {
		stop = false;
		this.thread = new Thread(this);
		this.thread.start();
	}
	

	public void run()
	{
		while(!this.isStop())
		{
			try {
				TransitionEvent<Action> transition = this.getTransition();
				if (transition != null) 
				{
					
					primitiveHandleTransitionEvent(transition);
				} 
				else 					
					Thread.sleep(100);
				
			} catch (Exception e) {				
				e.printStackTrace();
			}
			
		}
	}

	/**
	 * Pass to primitiveHandleTransitionEvent events trigger by itself
	 * 
	 * Default: false
	 * @return the reactItSelf
	 */
	public synchronized boolean isReactItSelf() {
		return reactItSelf;
	}

	/**
	 * Pass to primitiveHandleTransitionEvent events trigger by itself
	 * 
	 * Default: false
	 * @param reactItSelf the reactItSelf to set
	 */
	public synchronized void setReactItSelf(boolean reactItSelf) {
		this.reactItSelf = reactItSelf;
	}
	
	
	
	
}
