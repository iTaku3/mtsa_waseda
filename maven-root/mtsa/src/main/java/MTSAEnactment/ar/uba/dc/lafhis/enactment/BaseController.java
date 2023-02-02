/**
 * 
 */
package MTSAEnactment.ar.uba.dc.lafhis.enactment;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import MTSTools.ac.ic.doc.mtstools.model.LTS;

/**
 * Base Controller 
 * 
 * Extend this class to implement controllers who react and update the lts state
 * 
 * @author Julio
 *
 */
public abstract class BaseController<State, Action> extends Enactor<State, Action> {
	protected State currentState;	
	protected LTS<State, Action> lts;		
	protected Set<Action> controllableActions;	
	private Logger logger = LogManager.getLogger(BaseController.class.getName());
	private boolean fowardEvents = true;
	
	
	
	public BaseController(String name) {
		super(name);
		this.controllableActions = new HashSet<Action>();
	}
	
	public BaseController(String name, LTS<State, Action> lts, Set<Action> controllableActions) {
		super(name);
		setLts(lts);
		setControllableActions(controllableActions);
	}

	public Set<Action> getControllableActions(){
		return controllableActions;
	}

	public boolean isControllable(Action action){
		return controllableActions.contains(action);
	}	
	
	public void setControllableActions(Set<Action> controllableActions)
	{
		this.controllableActions = controllableActions;
	}
	
	public LTS<State, Action> getLts()
	{
		return this.lts;
	}
	
	public void setLts(LTS<State, Action> lts)
	{

		this.lts		= lts;		
		currentState	= lts.getInitialState();

	}

	public void setLts(LTS<State, Action> lts, State actualState, Set<Action> controllableActions){

		this.lts = lts;
		currentState = actualState;
		this.controllableActions = controllableActions;

	}
	
	/**
	 * @return scheduler current state
	 */
	public State getCurrentState()
	{
		return currentState;
	}

	@Override
	public void setUp() {
		currentState	= lts.getInitialState();		
	}

	@Override
	public void tearDown() {
				
	}
	
	@Override
	public synchronized void primitiveHandleTransitionEvent(TransitionEvent<Action> transitionEvent) throws Exception {
		Action action	= transitionEvent.getAction();
		Object source	= transitionEvent.getSource();
			
		Set<State> states = lts.getTransitions(currentState).getImage(action);
		
		if(states.size() <= 0){
			String exceptionString = "";			
			logger.error(String.format("[Scheduler::primitiveHandleTransitionEvent] there is no reachable state from currentState %s"
					+ " taking incoming action %s", currentState.toString(), action.toString(), exceptionString));
			
			throw new Exception(exceptionString);
		}
		
		//Assume deterministic lts
		currentState	= states.iterator().next();
		
		//if (this.isFowardEvents() && this != source) fireTransitionEvent(transitionEvent);
		if (this.isFowardEvents()) fireTransitionEvent(transitionEvent);
		
		//Thread.sleep(300);
		takeNextAction();
		
	}	
	
	
	//All concrete controllers need to implement next action
	public abstract void takeNextAction() throws Exception;

	/**
	 * Forward event to enactors
	 * 
	 * @return the fowardEvents
	 */
	public synchronized boolean isFowardEvents() {
		return fowardEvents;
	}

	/**
	 * Forward event to enactors
	 * 
	 * @param fowardEvents the fowardEvents to set
	 */
	public synchronized void setFowardEvents(boolean fowardEvents) {
		this.fowardEvents = fowardEvents;
	}
	
	
}
