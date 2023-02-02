package MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.ARDrone.Kinect;

import de.yadrone.base.navdata.AttitudeListener;


public class AttitudeGetter implements AttitudeListener {
	float pitch=0,roll=0,yaw=0;
	@Override
	public void attitudeUpdated(float arg0, float arg1) {
		
	}

	@Override
	public void attitudeUpdated(float pitch, float roll, float yaw) {
		this.pitch=pitch/1000;
		this.roll=roll/1000;
		this.yaw=yaw/1000;	
	}

	@Override
	public void windCompensation(float arg0, float arg1) {
		// TODO 
		
	}
	
	public float getYaw(){
		return yaw;
	}
	public float getRoll(){
		return roll;
	}
	public float getPitch(){
		return pitch;
	}
}
