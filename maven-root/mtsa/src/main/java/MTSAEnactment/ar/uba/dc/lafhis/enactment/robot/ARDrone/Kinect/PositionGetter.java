package MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.ARDrone.Kinect;

import java.io.IOException;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortIn;

public class PositionGetter {
	private OSCPortIn receiver = null;
	float x=0,y=0,z=0,lastX=0,lastY=0,lastZ=0;
	int timeCount1=0;//for getPositionIn2Axis
	int timeCount2=0;//for getPosition
	public PositionGetter(){
		try{
			receiver = new OSCPortIn(9999);
			receiver.addListener("/sayhello", makeListener());			
			receiver.startListening();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	private OSCListener makeListener(){
		return new OSCListener() {
			public void acceptMessage(java.util.Date time, OSCMessage message) {
				String[] buffer;
				String ob; 
				ob=(String)message.getArguments()[0];
				buffer=ob.split(" ");
				x=Float.valueOf(buffer[0]);
				y=Float.valueOf(buffer[1]);
				z=Float.valueOf(buffer[2]);
			}
		};
	}
	public float[] getPosition(){
//		System.out.println(timeCount2);
		if(lastX==x&&lastY==y){
			timeCount2++;
			if(timeCount2>=20){
				timeCount2=0;
				return null;
			}
		}else {
			lastX=x;lastY=y;
			timeCount2=0;
			
		}
		float[] p={x,y,z};
		return p;
	}
	public int[] getPositionIn2Axis(){
		if(lastX==x&&lastY==y){
			timeCount1++;
			if(timeCount1>=20){
				timeCount1=0;
				return null;
			}
		}else{timeCount1=0;}
		int m =(int)(x+0.5),n=(int)y;
		
		if(x+0.5<0)m--;
		int[] p={m,n};
		return p;
	}
}
