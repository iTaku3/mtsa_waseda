package MTSAEnactment.ar.uba.dc.lafhis.enactment.robot;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import MTSAEnactment.ar.uba.dc.lafhis.enactment.CounterEnactor;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.TransitionEvent;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.nxt.NXTCommException;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.nxt.NXTRobotComm;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.nxt.NXTRobotLostException;



/**
 * 
 * @author Julio
 *
 */
public class NXTRobot<State, Action> extends RobotAdapter<State, Action> {
	
	private NXTRobotComm comm;
	protected Action follow;
	protected Action turnLeft;
	protected Action turnRight;
	protected Action turnAround;

	protected Action calibrar;

	private Logger logger = LogManager.getLogger(NXTRobot.class.getName());
	
	/**
	 * @return the comm
	 */
	public NXTRobotComm getComm() {
		return comm;
	}


	/**
	 * @param comm the comm to set
	 */
	public void setComm(NXTRobotComm comm) {
		this.comm = comm;
		
		
	}


    public NXTRobot(String name, NXTRobotComm comm, Action success, Action failure, Action lost
    		, Action follow, Action turnLeft, Action turnRight, Action turnAround, Action calibrar) {
        super(name, success, failure, lost);
        this.setComm(comm);
        this.follow		= follow;
        this.turnLeft	= turnLeft;
        this.turnRight	= turnRight;
        this.turnAround	= turnAround;
        this.calibrar = calibrar;

        
    }

    public NXTRobot(String name, Action success, Action failure, Action lost
    		, Action follow, Action turnLeft, Action turnRight, Action turnAround, Action calibrar) throws NXTCommException
    {
    	super(name, success, failure, lost);
    	this.setComm(new NXTRobotComm());
        this.follow		= follow;
        this.turnLeft	= turnLeft;
        this.turnRight	= turnRight;
        this.turnAround	= turnAround;
        this.calibrar = calibrar;
        
    
    }

    public NXTRobot(String name, Action success, Action failure, Action lost
    		, Action follow, Action turnLeft, Action turnRight, Action turnAround, Action calibrar, String address, String port) throws NXTCommException
    {
    	super(name, success, failure, lost);
    	this.setComm(new NXTRobotComm(address, port));
        this.follow		= follow;
        this.turnLeft	= turnLeft;
        this.turnRight	= turnRight;
        this.turnAround	= turnAround;
        this.calibrar = calibrar;
        
    }

    
	@Override
	protected void primitiveHandleTransitionEvent(TransitionEvent<Action> transitionEvent) 
	{
		
		boolean shouldReply = false;
		logger.info("NXT action " + transitionEvent.getAction().toString());
		
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
				
			}else if(transitionEvent.getAction().equals(calibrar)){
				this.getComm().calibrar();
				shouldReply = true;

			}
			
						
			if (shouldReply) fireTransitionEvent(success);
		} catch (NXTRobotLostException robotLostException)
		{
			try {
				fireTransitionEvent(lost);
			} catch (Exception e1) {								
				this.logger.error(e1.getMessage(), e1);
			}
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
		// Nothing to setup
		
	}


	@Override
	public void tearDown() {
		// Nothing to do
		
	}


	
}
