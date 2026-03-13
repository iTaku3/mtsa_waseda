package MTSAEnactment.ar.uba.dc.lafhis.enactment;

import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.LTS;

public class TakeFirstController<State, Action> extends BaseController<State, Action> {

	private Logger logger = LogManager.getLogger(TakeFirstController.class.getName());
	
	public TakeFirstController(String name, LTS<State, Action> lts, Set<Action> controllableActions){
		super(name, lts, controllableActions);
	}
	
	public TakeFirstController(String name)
	{
		super(name);
		
	}

	
	@Override
	public void takeNextAction() throws Exception{
		Iterator<Pair<Action,State>> stateIterator = lts.getTransitions(currentState).iterator();
		
		if(!stateIterator.hasNext()){
			String exceptionString = "";
			logger.error(String.format("[Scheduler::primitiveHandleTransitionEvent] there is no reachable state from currentState %s"
					, currentState.toString(), exceptionString));
			throw new Exception(exceptionString);
		}		
		while(stateIterator.hasNext()){
			Pair<Action, State> currentPair	= stateIterator.next();
			
			//Take first controllable action 
			if(isControllable(currentPair.getFirst())){				
				Action nextAction = currentPair.getFirst();
				logger.info("Take First Controller (" + this.getName() + ") takeNextAction " + nextAction.toString());
				this.addTransition(new TransitionEvent<Action>(this, nextAction));				
		
			}
		}
		
	}
	

	
}
