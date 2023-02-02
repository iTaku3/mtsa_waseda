package MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.ARDrone.Kinect;

import de.yadrone.base.navdata.BatteryListener;


public class BatteryGetter implements BatteryListener {
	int remain;
	@Override
	public void batteryLevelChanged(int arg0) {
		// TODO 
		remain=arg0;
		
	}

	@Override
	public void voltageChanged(int arg0) {
		// TODO 
		
	}
	public int getBatteryParsentage(){
		return remain;
	}

}
