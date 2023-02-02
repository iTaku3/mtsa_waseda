package MTSAEnactment.ar.uba.dc.lafhis.enactment.robot;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.naming.ConfigurationException;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.KeyStroke;

import MTSAEnactment.ar.uba.dc.lafhis.enactment.TransitionEvent;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.gui.RecoverEnactorGUI;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.N6.robotProtocol.ConfigureValueMessage;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.N6.robotProtocol.DestinationReachedMessage;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.N6.robotProtocol.FollowLineMessage;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.N6.robotProtocol.IMessageListener;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.N6.robotProtocol.Message;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.N6.robotProtocol.MessageEvent;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.N6.robotProtocol.ReadIRSensorsMessage;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.N6.robotProtocol.ReadIRSensorsReplyMessage;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.N6.robotProtocol.ReadUSSensorMessage;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.N6.robotProtocol.ReadUSSensorReplyMessage;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.N6.robotProtocol.RobotLostMessage;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.N6.robotProtocol.RobotProtocolSession;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.N6.robotProtocol.SetMotorSpeedMessage;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.N6.robotProtocol.TurnAroundMessage;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.N6.robotProtocol.TurnLeftMessage;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.N6.robotProtocol.TurnRightMessage;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.gui.N6RobotGui;


/**
 * Created with IntelliJ IDEA.
 * User: tommy
 * Date: 2/23/13
 * Time: 6:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class N6Robot<State, Action> extends RobotAdapter<State, Action> 
	implements IMessageListener{
	protected N6RobotGui n6RobotGUI;
	
	protected static int FORWARD_SPEED	= 20;
	protected static int TURN_SPEED		= 15;
	protected static int BACK_SPEED		= -10;
	
	protected Action follow;
	protected Action turnLeft;
	protected Action turnRight;
	protected Action turnAround;
	protected Action readSensors;
	protected Action readSensorsReply;
	protected Action readUSSensor;
	protected Action readUSSensorReply;
	protected Action retry;
	protected Action successRetry;
	protected RobotProtocolSession session;
	
    public N6Robot(String name, Action success, Action failure, Action lost
    		, Action follow, Action turnLeft, Action turnRight, Action turnAround
    		, Action readSensors, Action readSensorsReply, Action readUSSensor
    		, Action readUSSensorReply
    		, Action retry
    		, Action successRetry
    		, RobotProtocolSession session) {
        super(name, success, failure, lost);
        this.follow				= follow;
        this.turnLeft			= turnLeft;
        this.turnRight			= turnRight;
        this.turnAround			= turnAround;
        this.readSensors		= readSensors;
        this.readSensorsReply	= readSensorsReply;
        this.readUSSensor		= readUSSensor;
        this.readUSSensorReply	= readUSSensorReply;
        this.retry				= retry;
        this.successRetry		= successRetry;
        this.session			= session;
        session.addMessageListener(this);
    }
    public N6Robot(String name, Action success, Action failure, Action lost
    		, Action follow, Action turnLeft, Action turnRight, Action turnAround
    		, Action readSensors, Action readSensorsReply, Action readUSSensor
    		, Action readUSSensorReply, Action retry, Action successRetry) {
        super(name, success, failure, lost);
       
        this.follow				= follow;
        this.turnLeft			= turnLeft;
        this.turnRight			= turnRight;
        this.turnAround			= turnAround;
        this.readSensors		= readSensors;
        this.readSensorsReply	= readSensorsReply;
        this.readUSSensor		= readUSSensor;
        this.readUSSensorReply	= readUSSensorReply;
        this.retry				= retry;
        this.successRetry		= successRetry;
       
        session	= new RobotProtocolSession(1, 4, "/dev/ttyACM0", "/dev/ttyACM1", "/dev/ttyACM2");
        session.open();      
        session.addMessageListener(this);
    }

	private void activateOptions(Action triggerAction)
	{
		if (n6RobotGUI == null) 
			this.setUp();
		if (!n6RobotGUI.isVisible()) 
			n6RobotGUI.setVisible(true);
		n6RobotGUI.setEnableButtons(true);
		
		n6RobotGUI.appendMessage("Action " + triggerAction.toString());
	}
	
    @Override
	protected void primitiveHandleTransitionEvent(TransitionEvent<Action> transitionEvent)
			throws Exception {
		if(transitionEvent.getAction().equals(follow)){
			session.sendMessage(new FollowLineMessage(session.getRobotId(), session.getMasterId()));
		}else if(transitionEvent.getAction().equals(turnLeft)){
			session.sendMessage(new TurnLeftMessage(session.getRobotId(), session.getMasterId()));
		}else if(transitionEvent.getAction().equals(turnRight)){
			session.sendMessage(new TurnRightMessage(session.getRobotId(), session.getMasterId()));
		}else if(transitionEvent.getAction().equals(turnAround)){
			session.sendMessage(new TurnAroundMessage(session.getRobotId(), session.getMasterId()));
		}else if(transitionEvent.getAction().equals(readSensors)){
			session.sendMessage(new ReadIRSensorsMessage(session.getRobotId(), session.getMasterId()));
		}else if(transitionEvent.getAction().equals(readSensorsReply)){
			session.sendMessage(new ReadIRSensorsReplyMessage(session.getRobotId(), session.getMasterId(),0,0,0));
		}else if(transitionEvent.getAction().equals(readUSSensor)){
			session.sendMessage(new ReadUSSensorMessage(session.getRobotId(), session.getMasterId()));
		}else if(transitionEvent.getAction().equals(readUSSensorReply)){
			session.sendMessage(new ReadUSSensorReplyMessage(session.getRobotId(), session.getMasterId(),0));
		}else if(transitionEvent.getAction().equals(retry)){
			activateOptions(retry);			
		}
	}
    
    
	@Override
	public void eventHandler(MessageEvent messageEvent) {
		// TODO Auto-generated method stub
		Message source	= (Message)messageEvent.getSource();
		if(source instanceof RobotLostMessage || messageEvent.getReply() instanceof RobotLostMessage){
			try {
				fireTransitionEvent(lost);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(source instanceof DestinationReachedMessage || messageEvent.getReply() instanceof DestinationReachedMessage){
			try {
				fireTransitionEvent(success);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(source instanceof ReadIRSensorsReplyMessage || messageEvent.getReply() instanceof ReadIRSensorsReplyMessage ){
			try {
				fireTransitionEvent(readSensorsReply);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}else if(source instanceof ReadUSSensorReplyMessage || messageEvent.getReply() instanceof ReadUSSensorReplyMessage ){
			try {
				fireTransitionEvent(readUSSensorReply);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
	}
	
	public enum Direction {
		   UP("Up", KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0)),
		   DOWN("Down", KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0)),
		   LEFT("Left", KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0)),
		   RIGHT("Right", KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0)),
		   SPACE("Right", KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0));

		   Direction(String text, KeyStroke keyStroke) {
		      this.text = text;
		      this.keyStroke = keyStroke;
		   }
		   private String text;
		   private KeyStroke keyStroke;

		   public String getText() {
		      return text;
		   }

		   public KeyStroke getKeyStroke() {
		      return keyStroke;
		   }

		   @Override
		   public String toString() {
		      return text;
		   }
	}
	
	private class ArrowBinding extends AbstractAction {
		private Direction direction;
		
	    public ArrowBinding(Direction direction) {
	       super(direction.getText());
	       this.direction	= direction;	       
	       putValue(ACTION_COMMAND_KEY, direction.getText());
	    }

	    @Override
	    public void actionPerformed(ActionEvent e) {
	       String actionCommand = e.getActionCommand();
	       switch(direction){
	       case UP:
	    	   session.sendMessage(new SetMotorSpeedMessage(session.getRobotId(), session.getMasterId(),FORWARD_SPEED, FORWARD_SPEED));
	    	   break;
	       case DOWN:
	    	   session.sendMessage(new SetMotorSpeedMessage(session.getRobotId(), session.getMasterId(),BACK_SPEED, BACK_SPEED));
	    	   break;
	       case LEFT:
	    	   session.sendMessage(new SetMotorSpeedMessage(session.getRobotId(), session.getMasterId(),-TURN_SPEED, TURN_SPEED));
	    	   break;
	       case RIGHT:
	    	   session.sendMessage(new SetMotorSpeedMessage(session.getRobotId(), session.getMasterId(),TURN_SPEED, -TURN_SPEED));
	    	   break;
	       case SPACE:
	    	   session.sendMessage(new SetMotorSpeedMessage(session.getRobotId(), session.getMasterId(), 0, 0));
	    	   break;
	       }
 	       	       	       
	    }
	 }	
	
	@Override
	public void setUp() {
	
		n6RobotGUI = new N6RobotGui();
		n6RobotGUI.setTitle(this.getName());
		
		n6RobotGUI.addAction("lost", lost.toString(), new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if (e.getSource() instanceof JButton)
					{
						n6RobotGUI.setEnableButtons(false);
						try {
							fireTransitionEvent(lost);
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					
				}
		});
		n6RobotGUI.addAction("successRetry", successRetry.toString(), new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() instanceof JButton)
				{
					n6RobotGUI.setEnableButtons(false);
					try {
						fireTransitionEvent(successRetry);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				
			}
		});	
		
	       n6RobotGUI.addAction("FORWARD", "go forward", new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					session.sendMessage(new SetMotorSpeedMessage(session.getRobotId(), session.getMasterId(),FORWARD_SPEED, FORWARD_SPEED));
				}
			});
	       n6RobotGUI.addAction("BACKWARD", "go backward", new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					session.sendMessage(new SetMotorSpeedMessage(session.getRobotId(), session.getMasterId(),BACK_SPEED, BACK_SPEED));
				}
			});
	       n6RobotGUI.addAction("LEFT", "go left", new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					session.sendMessage(new SetMotorSpeedMessage(session.getRobotId(), session.getMasterId(),-TURN_SPEED, TURN_SPEED));
				}
			});
	       n6RobotGUI.addAction("RIGHT", "go right", new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					session.sendMessage(new SetMotorSpeedMessage(session.getRobotId(), session.getMasterId(),TURN_SPEED, -TURN_SPEED));
				}
			});
	       n6RobotGUI.addAction("STOP", "stop", new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					session.sendMessage(new SetMotorSpeedMessage(session.getRobotId(), session.getMasterId(), 0, 0));
				}
			});			

		n6RobotGUI.setKeyAction(Direction.UP, new ArrowBinding(Direction.UP));
		n6RobotGUI.setKeyAction(Direction.DOWN, new ArrowBinding(Direction.DOWN));
		n6RobotGUI.setKeyAction(Direction.LEFT, new ArrowBinding(Direction.LEFT));
		n6RobotGUI.setKeyAction(Direction.RIGHT, new ArrowBinding(Direction.RIGHT));
		n6RobotGUI.setKeyAction(Direction.SPACE, new ArrowBinding(Direction.SPACE));
		
		n6RobotGUI.setEnableButtons(false);
		
		n6RobotGUI.setVisible(true);
		
	}
	@Override
	public void tearDown() {
		n6RobotGUI.setVisible(false);
		n6RobotGUI = null;
		
	}


}
