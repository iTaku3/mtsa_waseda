package MTSAEnactment.ar.uba.dc.lafhis.enactment.robot;

import MTSAEnactment.ar.uba.dc.lafhis.enactment.TransitionEvent;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.ARDrone.Kinect.DroneAPI;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.ARDrone.V1.PicWindow;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.ARDrone.V1.TextWindow;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.ARDrone.exceptions.ARDroneException;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.ARDrone.exceptions.FailException;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.ARDrone.exceptions.ObstacleFoundException;


public class ARDroneRobotMoving<State, Action> extends RobotAdapter<State, Action> 
	/*implements IMessageListener*/{
	DroneAPI drone;

	private PicWindow frontWin;
//	private PicWindow bottomWin;

	private TextWindow tWin; 
	
	protected Action takeoff;
	protected Action land;
	protected Action blink;
	protected Action read;
	protected Action readValue0;
	protected Action readValue1;
	protected Action readValue2;
	protected Action takeoffOLD;
	protected Action landOLD;
	protected Action blinkOLD;
	protected Action readOLD;
	protected Action lowBattery;
	protected Action charge;
	protected Action moveTo0;
	protected Action moveTo1;
	protected Action moveTo2;
	protected Action moveTo3;
	protected Action moveToOLD0;
	protected Action moveToOLD1;
	protected Action moveToOLD2;
	protected Action moveToOLD3;
	protected Action resetOLD;
	protected Action reset;
	protected Action avoid;
	protected Action obstacle;

//	protected RobotProtocolSession session;
	
	private int m_last = 0;
	private int n_last = 0;
	
    public ARDroneRobotMoving(String name, Action success, Action failure, Action lost
   	   	 , Action takeoff, Action land, Action blink,  Action read
   	   	 , Action readValue0,Action readValue1,Action readValue2,
   	   	 Action takeoffOLD, Action landOLD, Action blinkOLD,  Action readOLD,
   	   	 Action lowBattery, Action charge,
   	   	 Action moveTo0,Action moveTo1,Action moveTo2,Action moveTo3,
   	   	 Action moveToOLD0,Action moveToOLD1,Action moveToOLD2,Action moveToOLD3,
   	   	 Action reset, Action avoid, Action obstacle, Action resetOLD) {
   	       super(name, success, failure, lost);
   	       this.takeoff	= takeoff;
   	       this.land	 = land;
   	       this.blink	 = blink;
   	       this.read	 = read;
   	       this.readValue0	= readValue0;
   	       this.readValue1	= readValue1;
   	       this.readValue2	= readValue2;
   	       this.takeoffOLD	= takeoffOLD;
   	       this.landOLD	 = landOLD;
   	       this.blinkOLD	 = blinkOLD;
   	       this.readOLD	 = readOLD;
   	       this.lowBattery = lowBattery;
   	       this.charge     = charge;
   	       this.moveTo0 = moveTo0;
   	       this.moveTo1 = moveTo1;
   	       this.moveTo2 = moveTo2;
   	       this.moveTo3 = moveTo3;
   	       this.moveToOLD0 = moveToOLD0;
   	       this.moveToOLD1 = moveToOLD1;
   	       this.moveToOLD2 = moveToOLD2;
   	       this.moveToOLD3 = moveToOLD3;
   	       this.reset  = reset;
   	       this.avoid = avoid;
   	       this.obstacle = obstacle;
   	       this.resetOLD  = resetOLD;
   	   }

    
    @Override
	protected void primitiveHandleTransitionEvent(TransitionEvent<Action> transitionEvent)
			throws Exception {
		if(transitionEvent.getAction().equals(takeoff) || transitionEvent.getAction().equals(takeoffOLD)){
//			System.out.println("TAKE OFF");
			if(!drone.takeoff()){
				//Failure
//				System.out.println("FailTakeOff");
				this.tearDown();
			}
			drone.hover(5000);
		}else if(transitionEvent.getAction().equals(land) || transitionEvent.getAction().equals(landOLD)){
//			System.out.println("LAND");
			if(!drone.landing()){
				//Failure
//				System.out.println("FailLand");
				this.tearDown();
			}
		}else if(transitionEvent.getAction().equals(blink) || transitionEvent.getAction().equals(blinkOLD)){
//			System.out.println("BLINK");
			if(drone.blinkLED("ORANGE")){
//				System.out.println("SuccessBlink");
			}else{
//				System.out.println("FailBlink");
			}
			drone.hover(500);
		}else if(transitionEvent.getAction().equals(read) || transitionEvent.getAction().equals(readOLD)){
//			System.out.println("READ");
			int times = 500;
			tWin.text="";
			while(times > 0 && isLabel(tWin.text)){
				frontWin.image = drone.takeBottomPicture();
				frontWin.repaint();
				
				tWin.text = drone.readBottom();
				tWin.repaint();
				drone.hover(500);

				times--;
			}
			interpretate(tWin.text);
			
		}else if(transitionEvent.getAction().equals(readValue0)){
//			System.out.println("READ VALUE 0");
		}else if(transitionEvent.getAction().equals(readValue1)){
//			System.out.println("READ VALUE 1");
		}else if(transitionEvent.getAction().equals(readValue2)){
//			System.out.println("READ VALUE 2");
		}else if(transitionEvent.getAction().equals(lowBattery)){
//			System.out.println("LOW BATTERY");
			drone.blinkLED("RED");
		}else if(transitionEvent.getAction().equals(charge)){
//			System.out.println("CHARGE");
			drone.blinkLED("GREEN");
		}else if (transitionEvent.getAction().equals(moveTo0)||transitionEvent.getAction().equals(moveToOLD0)){
//			System.out.println("MOVE TO 0");
			moveTo(0, 0);
		}else if (transitionEvent.getAction().equals(moveTo1)||transitionEvent.getAction().equals(moveToOLD1)){
//			System.out.println("MOVE TO 1");
			moveTo(0, 2);
		}else if (transitionEvent.getAction().equals(moveTo2)||transitionEvent.getAction().equals(moveToOLD2)){
			moveTo(2,2);
		}else if (transitionEvent.getAction().equals(moveTo3)||transitionEvent.getAction().equals(moveToOLD3)){
//			System.out.println("MOVE TO 3");
			moveTo(2,0);
		}else if (transitionEvent.getAction().equals(obstacle)){
			drone.blinkLED("GREEN_RED");
//			System.out.println("OBSTACLE");
		}else if (transitionEvent.getAction().equals(avoid)){
//			System.out.println("AVOID");
			drone.upSimply();
			drone.move(m_last, n_last);
			drone.downSimply();
		}else if (transitionEvent.getAction().equals(reset) || transitionEvent.getAction().equals(resetOLD)){
//			System.out.println("RESET");
			drone.landing();
			drone.wait(2000);
		}
	}

	private void moveTo(int m, int n) throws ARDroneException, Exception {
		try{
			m_last = m; n_last = n;
			drone.move(m,n);
			fireTransitionEvent(success);
		}catch(FailException e){
			fireTransitionEvent(failure);
		}catch(ObstacleFoundException e){
			fireTransitionEvent(obstacle);
		}
	}

	private boolean isLabel(String text) {
		return !(tWin.text.equalsIgnoreCase("Tag 2") || tWin.text.equalsIgnoreCase("Tag 1") || tWin.text.equalsIgnoreCase("Tag 0"));
	}

	private void interpretate(String text) throws Exception {
		if(text.equalsIgnoreCase("Tag 0")){
			fireTransitionEvent(readValue0);
		}else if(text.equalsIgnoreCase("Tag 1")){
			fireTransitionEvent(readValue1);
		}else if(text.equalsIgnoreCase("Tag 2")){
			fireTransitionEvent(readValue2);
		}else{
			fireTransitionEvent(failure);
		}
	}
    
//	@Override
//	public void eventHandler(MessageEvent messageEvent) {
//		// TODO Auto-generated method stub
//		Message source	= (Message) messageEvent.getSource();
//		if(source instanceof RobotLostMessage || messageEvent.getReply() instanceof RobotLostMessage){
//			try {
//				fireTransitionEvent(lost);
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}else if(source instanceof DestinationReachedMessage || messageEvent.getReply() instanceof DestinationReachedMessage){
//			try {
//				fireTransitionEvent(success);
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}else if(source instanceof ReadIRSensorsReplyMessage || messageEvent.getReply() instanceof ReadIRSensorsReplyMessage ){
//			try {
//				fireTransitionEvent(readValue0);
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}			
//		}		
//	}
	
	
	@Override
	public void setUp() {
//		System.out.println("SET UP");
		if(frontWin==null){
			frontWin = new PicWindow();
		}else{
			frontWin.setVisible(true);
		}
		if(tWin==null){
			tWin = new TextWindow();
		}else{
			tWin.setVisible(true);
		}
		drone = new DroneAPI();
        drone.start();
	}
	@Override
	public void tearDown() {
//		System.out.println("TEAR DOWN");
        drone.stop();
        frontWin.setVisible(false);
        tWin.setVisible(false);
	}


}
