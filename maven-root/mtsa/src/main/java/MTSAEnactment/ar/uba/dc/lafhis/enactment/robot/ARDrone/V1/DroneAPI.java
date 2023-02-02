package MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.ARDrone.V1;

import java.awt.image.BufferedImage;
import java.util.Calendar;

import de.yadrone.base.ARDrone;
import de.yadrone.base.IARDrone;
import de.yadrone.base.command.LEDAnimation;
import de.yadrone.base.command.VideoChannel;

public class DroneAPI {

	private IARDrone drone = null;
	private VideoListener vl = null;
	private BarcodeListener bar = null;
	
	public DroneAPI(){
		try{
			drone = new ARDrone();
			
			vl = new VideoListener();
			drone.getVideoManager().addImageListener(vl);
			
			bar = new BarcodeListener();
			drone.getVideoManager().addImageListener(bar);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public boolean start(){
		try{
//			System.out.println("Start the Drone controlling.");
			drone.start();
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean stop(){
		try{
//			System.out.println("Stop the Drone controlling.");
			drone.stop();
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean takeoff(){
		return takeoff(5000);
	}
	
	public boolean takeoff(int time){
		try{
//			System.out.println("Take off the Drone.");
			drone.getCommandManager().takeOff().doFor(time);
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
//	Long start= Calendar.getInstance().getTimeInMillis();
//			Long end = Calendar.getInstance().getTimeInMillis();
//			System.out.println("TakeOff:"+ (end - start));
	public boolean landing(){
		try{
//			System.out.println("Land the Drone.");
			Long start= Calendar.getInstance().getTimeInMillis();
			drone.getCommandManager().landing();
			Long end = Calendar.getInstance().getTimeInMillis();
//			System.out.println("Land:"+ (end - start));

			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean blinkLED(String color){
		try{
//			System.out.println("Blink LED.");
			
			if(color.equals("RED")){
				drone.getCommandManager().setLedsAnimation(LEDAnimation.BLINK_RED, 3, 3).doFor(3000);
				return true;
			}
			else if(color.equals("ORANGE")){
				drone.getCommandManager().setLedsAnimation(LEDAnimation.BLINK_ORANGE, 3, 3).doFor(3000);
				return true;
			}
			else if(color.equals("GREEN")){
				drone.getCommandManager().setLedsAnimation(LEDAnimation.BLINK_GREEN, 3, 3).doFor(3000);
				return true;
			}
			else{
				return false;
			}
			
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public String readFront(){
		drone.getCommandManager().setVideoChannel(VideoChannel.HORI);
		drone.getCommandManager().waitFor(100); // to make sure
		return bar.getResult();
	}
	
	public String readBottom(){
		drone.getCommandManager().setVideoChannel(VideoChannel.VERT);
		drone.getCommandManager().waitFor(100); // to make sure
		return bar.getResult();
	}
	
	public BufferedImage takeFrontPicture(){
		drone.getCommandManager().setVideoChannel(VideoChannel.HORI);
		drone.getCommandManager().waitFor(100); // to make sure
		return vl.getIamge();
	}
	
	public BufferedImage takeBottomPicture(){
		drone.getCommandManager().setVideoChannel(VideoChannel.VERT);
		drone.getCommandManager().waitFor(100); // to make sure
		return vl.getIamge();
	}
	
	// block the thread and execution of commands are blocked
	public void wait(int time){
		drone.getCommandManager().waitFor(time);
	}	
	
	public void hover(int time){
		drone.getCommandManager().hover().doFor(time);
	}
}
