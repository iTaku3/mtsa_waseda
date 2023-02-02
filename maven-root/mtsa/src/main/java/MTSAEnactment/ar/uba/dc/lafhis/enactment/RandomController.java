/**
 * 
 */
package MTSAEnactment.ar.uba.dc.lafhis.enactment;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.LTS;

/**
 * @author Julio
 *
 */
public class RandomController<State, Action> extends BaseController<State, Action> {
	private Logger logger = LogManager.getLogger(RandomController.class.getName());
	
	public RandomController(String name)
	{
		super(name);
		
	}
	public RandomController(LTS<State, Action> lts, Set<Action> controllableActions)
	{
		super("Random Scheduler", lts, controllableActions);
		
	}
	public RandomController(String name, LTS<State, Action> lts, Set<Action> controllableActions)
	{
		super(name, lts, controllableActions);
		
	}
	
	@Override
	public void takeNextAction() throws Exception{
		BinaryRelation<Action, State> states = lts.getTransitions(currentState);
		 
		if (states == null || states.size() == 0)
		{
			String exceptionString = "";			
			throw new Exception(exceptionString);
		}

		String availablesText = "";
		
		List<Pair<Action, State>> availables = new ArrayList<Pair<Action, State>>();
		for (Pair<Action, State> pair : states)
		{
			if(controllableActions.contains(pair.getFirst())){
				availables.add(pair);
				availablesText += "," + pair.getFirst().toString();
			}
		}
		if (availables.size() == 0 ) return;
		

		int randomPos = (int) (Math.random() * availables.size());
		Action nextAction = availables.get(randomPos).getFirst();
		
		
		logger.info("Random Controller (" + this.getName() + ") takeNextAction " + nextAction.toString()  + " out of [" + availablesText + "]");
		
		this.addTransition(new TransitionEvent<Action>(this, nextAction));
				
	}
}
