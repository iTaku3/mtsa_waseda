package MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.ARDrone.Kinect;

import de.yadrone.base.navdata.Altitude;
import de.yadrone.base.navdata.AltitudeListener;


public class HeightGetter implements AltitudeListener {
	int height=0;
	float altitude=0;
	@Override
	public void receivedAltitude(int arg0) {
		// TODO 
		//this.height=arg0;
	}

	@Override
	public void receivedExtendedAltitude(Altitude al) {
		this.height=al.getRaw();
//		System.out.println(al.toString());
		
	}
	public int getHeight(){
		return height;
	}
	public float getAltitude(){
		return altitude;
	}

}
