/**
 * 
 */
package MTSAEnactment.ar.uba.dc.lafhis.enactment.robot;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import MTSAEnactment.ar.uba.dc.lafhis.enactment.CounterEnactor;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.Enactor;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.TransitionEvent;

/**
 * Virtual robot using User Interface
 * 
 * @author Julio
 *
 */
public class UIRobot<State, Action> extends RobotAdapter<State, Action>  {

	
	private Action follow;
	private Action turnLeft;
	private Action turnRight;
	private Action turnAround;
	
	private Logger logger = LogManager.getLogger(CounterEnactor.class.getName());

	/**
	 * @param performAction the performAction to set
	 */
	private synchronized void setPerformAction(Action performAction) {
		
		logger.info("UIRobot - Action performed: " + performAction.toString());
	}

	public UIRobot(String name, Action success, Action failure, Action lost, Action follow, Action turnLeft, Action turnRight, Action turnAround) {
		super(name, success, failure, lost);
		
		this.follow = follow;		
		this.turnAround = turnAround;
		this.turnLeft = turnLeft;
		this.turnRight = turnRight;
		
	}

	@Override
	protected void primitiveHandleTransitionEvent(
			TransitionEvent<Action> transitionEvent) throws Exception {
		boolean shouldReply = false;
		
		if(transitionEvent.getAction().equals(follow)){			
			this.setPerformAction(follow);
		} else if(transitionEvent.getAction().equals(turnLeft)){
			this.setPerformAction(turnLeft);
		} else if(transitionEvent.getAction().equals(turnRight)){
			this.setPerformAction(turnRight);
		} else if(transitionEvent.getAction().equals(turnAround)){
			this.setPerformAction(turnAround);
		}
		
		try {
			fireTransitionEvent(success);
		} catch (Exception e) {					
			e.printStackTrace();
		}
		
	}

	@Override
	public void setUp() {
		//Nothing to setup
	}

	@Override
	public void tearDown() {

		
	}

}
