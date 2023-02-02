package MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.ARDrone.Kinect;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;

import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.ARDrone.exceptions.ARDroneException;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.ARDrone.exceptions.FailException;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.ARDrone.exceptions.ObstacleFoundException;
import de.yadrone.base.ARDrone;
import de.yadrone.base.IARDrone;
import de.yadrone.base.command.LEDAnimation;
import de.yadrone.base.command.VideoChannel;

enum Direct{FORWARD,LEFT,RIGHT,BACK,NORTH,EAST,SOUTH,WEST}
public class DroneAPI {
	private float northDirect=0;
	private IARDrone drone = null;
	private VideoListener vl = null;
	private BarcodeListener bar = null;
	private AttitudeGetter gyro =null;
	//private HeightGetter heg=null;
	private BatteryGetter bg=null;
	private PositionGetter pg=null;
	///////////////////////
	private FileManager fm=null;
	////////////////////////
	public DroneAPI(){
		try{
			fm=new FileManager();
			drone = new ARDrone();			
			addListener();

		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void addListener(){
		vl = new VideoListener();
		gyro=new AttitudeGetter();
		bar = new BarcodeListener();
		//heg=new HeightGetter();
		bg=new BatteryGetter();
		pg=new PositionGetter();
		drone.getNavDataManager().addAttitudeListener(gyro);
		//drone.getNavDataManager().addAltitudeListener(heg);
		drone.getNavDataManager().addBatteryListener(bg);
		drone.getVideoManager().addImageListener(vl);
		drone.getVideoManager().addImageListener(bar);
		
		
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
		startLog();
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
	
	public boolean landing(){
		endLog();
		try{
//			System.out.println("Land the Drone.");
			drone.getCommandManager().landing();
			if(gyro.getPitch()<20&&gyro.getRoll()<20)return true;
			else return false;
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
			else if(color.equals("GREEN_RED")){
				drone.getCommandManager().setLedsAnimation(LEDAnimation.BLINK_GREEN_RED, 3, 3).doFor(3000);
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
	public boolean moveForwardSimply(){
		drone.getCommandManager().forward(10).doFor(1000);
		wait(500);
		drone.hover();
		wait(1000);
		return true;
	}
	public void move(int m,int n) throws ARDroneException{
		fm.fileWrite("============start move"+Integer.toString(m)+","+Integer.toString(n)+"===========");
		long start = System.currentTimeMillis();
		long finish=0;
		float th=(float)0.15;
		int timecounta=0;
		float[] targetPosition={(float)m,(float)n+(float)0.5};
		turn(Direct.NORTH);
		while(timecounta<200){
			float[] currentPosition=pg.getPosition();
			if(currentPosition==null){
				drone.hover();
				wait(1000);
				throw new FailException(); 
			}
			float ex=targetPosition[0]-currentPosition[0];
			float ey=targetPosition[1]-currentPosition[1];
			if(ex<th&&ex>-th&&ey<th&&ey>-th){
				drone.hover();
				fm.fileWrite("Success");
//				System.out.println("Success!!target x:"+targetPosition[0]+"target y:"+targetPosition[1]);				
				wait(1000);
				return;
			}else{
				finish=System.currentTimeMillis();
				fm.fileWrite("err of x:"+ex+"err of y:"+ey+"currentTime:"+(finish-start));
//				System.out.println("err of x:"+ex+"err of y:"+ey);
				if(ex>th||ex<-th){
					if(ex>0){
						setDirect(Direct.RIGHT,calcSpeed(ex));
					}
					else if(ex<0){
						setDirect(Direct.LEFT,calcSpeed(ex));
					}
				}else if(ey>th||ey<-th){
					if(ey>0){
						setDirect(Direct.FORWARD,calcSpeed(ey));
					}
					else if(ey<0){
						setDirect(Direct.BACK,calcSpeed(ey));
					}
				}
			}
			String text = this.readFront();
			if(!text.equals(BarcodeListener.NONE)){
				throw new ObstacleFoundException();
			}
			wait(100);
			if(timecounta%10==0){
				hover(500);
				wait(1500);
				//turn(Direct.NORTH);
				fm.fileWrite("stop!!1500ms");
			}
			timecounta++;
		}
		drone.getCommandManager().hover();
		wait(500);
		fm.fileWrite("Failed");
		throw new FailException();
	}
	public void setDirect(Direct d,int speed){
		fm.fileWrite(d.toString());
//		System.out.println(d.toString());
		if(d==Direct.FORWARD){
				drone.getCommandManager().forward(speed);
		}else if(d==Direct.BACK){
				drone.getCommandManager().backward(speed);
		}else if(d==Direct.LEFT){
				drone.getCommandManager().goLeft(speed);
		}else if(d==Direct.RIGHT){
				drone.getCommandManager().goRight(speed);
		}	
	}
	public int calcSpeed(float e){
		int maxSpeed=5,minSpeed=2;
		int s=0;
		if(e<0){
			e=-e;
		}
		s= (int)(e*5+minSpeed>maxSpeed ? maxSpeed : (e*5+minSpeed));
		//fm.fileWrite(Integer.toString(s));
		return s;
	}


	public void moveForward() throws ARDroneException{
		int[] target = pg.getPositionIn2Axis();
		if(target==null)throw new FailException(); 
		target[1]++;
		move(target[0],target[1]);
	}
	
	
	
	
	public boolean turn(Direct d){
		float yawtarget=0;
		float th=5;
		int timecounta=0;
		if(d==Direct.FORWARD||d==Direct.NORTH)yawtarget=northDirect;
		else if(d==Direct.LEFT||d==Direct.WEST){
			yawtarget=northDirect-90;
			if(yawtarget<180)yawtarget=360+yawtarget;
		}
		else if(d==Direct.RIGHT||d==Direct.EAST){
			yawtarget=northDirect+90;
			if(yawtarget>180)yawtarget=-360+yawtarget;
		}
		
		else if(d==Direct.BACK||d==Direct.SOUTH){
			yawtarget=northDirect+180;
			if(yawtarget>180)yawtarget=-360+yawtarget;
		}
		while(timecounta<50){
			float err=yawtarget-gyro.getYaw();
			if(err>180)err-=360;
			else if(err<-180)err+=360;
			if(err<th&&err>-th){
				drone.getCommandManager().hover();
				wait(1000);
				return true;
			}else{
				if(err>0){
					err=err>50? 50:err;
					drone.getCommandManager().spinRight((int)err);
//					System.out.println("Failed with:"+err+"deg");
				}else if(err<0){
					err=err<-50? 50:-err;
					drone.getCommandManager().spinLeft((int)err);
//					System.out.println("Failed with:"+err+"deg");
				}
				wait(100);
				
			}
			timecounta++;
		}
		return false;
		
	}
	public void setCurrentDirectAsNorth(){
		this.northDirect=gyro.getYaw();
	}
	public int[] getPosition(){
		return pg.getPositionIn2Axis();
		
	}
	public float[] getPositionDebug(){
		return pg.getPosition();
	}
	
	public boolean upSimply(){
		drone.getCommandManager().up(30);
		wait(2500);
		drone.getCommandManager().hover();
		wait(500);
		return true ;//alwase true because cannot use height info 
	}
	public boolean downSimply(){
		drone.getCommandManager().down(30);
		wait(2500);
		drone.getCommandManager().hover();
		wait(500);
		return true;//alwase true because cannot use height info
	}
//////**These below APIs are for manual handling**/////////	
	public void downManu(){
		drone.getCommandManager().down(20);
	}
	public void upManu(){
		drone.getCommandManager().up(20);
	}
	public void forwardManu(){
		drone.getCommandManager().forward(20);
	}
	public void backWardManu(){
		drone.getCommandManager().backward(20);
	}
	public void goLeftManu(){
		drone.getCommandManager().goLeft(20);
	}
	public void goRightManu(){
		drone.getCommandManager().goRight(20);
	}
	public void spinLeft(){
		drone.getCommandManager().spinLeft(30);
	}
	public void spinRight(){
		drone.getCommandManager().spinRight(30);
	}
	public int getBatteryInfo(){
		return bg.getBatteryParsentage();
	}

////////////////////////////////////////////////////////////	
	
	public void test(){
//		//System.out.println("Now Battery:"+bg.getBatteryParsentage()+"%");
//		System.out.println("Now yaw:"+gyro.getYaw());
	}
	public void endLog(){
		fm.fileClose();
	}
	public void startLog(){
		fm.fileOpen();
	}
}


class FileManager{
	File f;
	int i=0;
	PrintWriter pw;
	FileManager(){
		f =new File("./log_drone.txt"+Integer.toString(Calendar.MONTH)+Integer.toString(Calendar.DATE)+"_"+Integer.toString(Calendar.HOUR)+Integer.toString(Calendar.MINUTE)+Integer.toString(Calendar.SECOND));	
		try{
			pw=new PrintWriter(new BufferedWriter(new FileWriter(f)));
		}catch(IOException e){
			e.printStackTrace();
		}		
	}
	void fileWrite(String msg){
		pw.println(msg);
	}
	void fileClose(){
		pw.close();
	}
	void fileOpen(){
		Calendar t=Calendar.getInstance();
		int m=t.get(Calendar.MONTH)+1, d=t.get(Calendar.DATE),h=t.get(Calendar.HOUR),mi=t.get(Calendar.MINUTE),s=t.get(Calendar.SECOND);
		
		
		f =new File("./DroneLog_"+Integer.toString(m)+Integer.toString(d)+"_"+Integer.toString(h)+Integer.toString(mi)+Integer.toString(s)+".txt");	
//		System.out.println(Calendar.getInstance().toString());
		try {
			pw=new PrintWriter(new BufferedWriter(new FileWriter(f)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
}