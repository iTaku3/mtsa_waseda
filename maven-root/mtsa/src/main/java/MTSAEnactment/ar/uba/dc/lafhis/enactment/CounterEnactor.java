/**
 * 
 */
package MTSAEnactment.ar.uba.dc.lafhis.enactment;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * @author Julio
 *
 */
public class CounterEnactor<State, Action> extends Enactor<State, Action> {

	private int counter = 0;
	private Action resetAction;
	private Logger logger = LogManager.getLogger(CounterEnactor.class.getName());
	
	public CounterEnactor(String name, Action resetAction) {
		super(name);
		this.resetAction = resetAction;
	}

	@Override
	protected void primitiveHandleTransitionEvent(
			TransitionEvent<Action> transitionEvent) throws Exception {
		if (this.resetAction != null && this.resetAction.equals(transitionEvent.getAction()))
		{
			counter = 0;

		} else 
		{
			counter++;

			logger.info("Counter: " + counter + " - " + transitionEvent.getAction().toString());
			
		}
		
	}
	
	@Override
	public void setUp()
	{
		//Re inicio el contador antes de comerzar una simulacion
		counter = 0;
	}

	@Override
	public void tearDown() {
		
		
	}

}
