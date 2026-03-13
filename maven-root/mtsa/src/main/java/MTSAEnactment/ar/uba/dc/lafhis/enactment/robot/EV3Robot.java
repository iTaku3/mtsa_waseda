/**
 * 
 */
package MTSAEnactment.ar.uba.dc.lafhis.enactment.robot;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import MTSAEnactment.ar.uba.dc.lafhis.enactment.CounterEnactor;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.TransitionEvent;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.ev3.EV3RobotComm;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.ev3.EV3RobotCommException;


/**
 * @author Julio
 *
 */
public class EV3Robot<State, Action> extends RobotAdapter<State, Action> {

	protected Action follow;
	protected Action turnLeft;
	protected Action turnRight;
	protected Action turnAround;
	
	private Logger logger = LogManager.getLogger(EV3Robot.class.getName());
	
	private EV3RobotComm comm;
	
	
	public EV3Robot (String name, Action success, Action failure, Action lost, Action follow, Action turnLeft, Action turnRight, Action turnAround)
	{
		super(name, success, failure, lost);
		this.turnAround = turnAround;
		this.turnLeft = turnLeft;
		this.turnRight = turnRight;
		this.follow = follow;
	
		try {
			this.comm = new EV3RobotComm();
		} catch (EV3RobotCommException e) {
			logger.error(e.getMessage(), e);
		}
		
	}

	@Override
	protected void primitiveHandleTransitionEvent(
			TransitionEvent<Action> transitionEvent) throws Exception {
		boolean shouldReply = false;
		logger.info("EV3 action " + transitionEvent.getAction().toString());
		
		try {
			if(transitionEvent.getAction().equals(follow)){
				this.getComm().follow();
				shouldReply = true;
				
			}else if(transitionEvent.getAction().equals(turnLeft)){
				this.getComm().turnLeft();
				shouldReply = true;

			}else if(transitionEvent.getAction().equals(turnRight)){
				this.getComm().turnRight();
				shouldReply = true;
				
			}else if(transitionEvent.getAction().equals(turnAround)){
				this.getComm().turnBack();
				shouldReply = true;
				
			}			
						
			if (shouldReply) fireTransitionEvent(success);
		
		} catch (Exception e) {				
			
			this.logger.error(e.getMessage(), e);
			try {
				fireTransitionEvent(failure);
			} catch (Exception e1) {				
				this.logger.error(e.getMessage(), e1);
			}
		}
		
	}

	@Override
	public void setUp() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tearDown() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @return the comm
	 */
	private EV3RobotComm getComm() {
		return comm;
	}

	/**
	 * @param comm the comm to set
	 */
	private void setComm(EV3RobotComm comm) {
		this.comm = comm;
	}
}
