package MTSAEnactment.ar.uba.dc.lafhis.enactment.robot;

import MTSAEnactment.ar.uba.dc.lafhis.enactment.TransitionEvent;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.ARDrone.V1.DroneAPI;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.ARDrone.V1.PicWindow;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.ARDrone.V1.TextWindow;

public class ARDroneRobot<State, Action> extends RobotAdapter<State, Action> 
	/*implements IMessageListener*/{
	DroneAPI drone;

	private PicWindow pWin;;
	private TextWindow tWin; 
	
	protected Action takeoff;
	protected Action land;
	protected Action blink;
	protected Action read;
	protected Action readValue0;
	protected Action readValue1;
	protected Action readValue2;
	
//	protected RobotProtocolSession session;
	
    public ARDroneRobot(String name, Action success, Action failure, Action lost
    		, Action takeoff, Action land, Action blink,  Action read
    		, Action readValue0,Action readValue1,Action readValue2) {
        super(name, success, failure, lost);
        this.takeoff				= takeoff;
        this.land			= land;
        this.blink			= blink;
        this.read		= read;
        this.readValue0	= readValue0;
        this.readValue1	= readValue1;
        this.readValue2	= readValue2;
    }

    @Override
	protected void primitiveHandleTransitionEvent(TransitionEvent<Action> transitionEvent)
			throws Exception {
		if(transitionEvent.getAction().equals(takeoff)){
//			System.out.println("TAKE OFF");
			drone.takeoff();
			drone.hover(5000);
		}else if(transitionEvent.getAction().equals(land)){
//			System.out.println("LAND");
			drone.landing();
			drone.wait(10000);
		}else if(transitionEvent.getAction().equals(blink)){
//			System.out.println("BLINK");
			drone.blinkLED("RED");
		}else if(transitionEvent.getAction().equals(read)){
//			System.out.println("READ");
			int times = 60;
			tWin.text="";
			while(times > 0 && !(tWin.text.equals("Tag 2") || tWin.text.equals("Tag 1") || tWin.text.equals("Tag 3"))
					){
				pWin.image = drone.takeFrontPicture();
				pWin.repaint();
				
				tWin.text = drone.readFront();
				tWin.repaint();

				drone.wait(500);
				times--;
			}
			if(tWin.text.equals("Tag 1")){
				fireTransitionEvent(readValue0);
			}else if(tWin.text.equals("Tag 2")){
				fireTransitionEvent(readValue1);
			}else if(tWin.text.equals("Tag 3")){
				fireTransitionEvent(readValue2);
			}else{
				fireTransitionEvent(failure);
			}
			
		}else if(transitionEvent.getAction().equals(readValue0)){
//			System.out.println("READ VALUE 0");
		}else if(transitionEvent.getAction().equals(readValue1)){
//			System.out.println("READ VALUE 1");
		}else if(transitionEvent.getAction().equals(readValue2)){
//			System.out.println("READ VALUE 2");
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
		if(pWin==null){
			pWin = new PicWindow();
		}else{
			pWin.setVisible(true);
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
        pWin.setVisible(false);
        tWin.setVisible(false);
	}


}
