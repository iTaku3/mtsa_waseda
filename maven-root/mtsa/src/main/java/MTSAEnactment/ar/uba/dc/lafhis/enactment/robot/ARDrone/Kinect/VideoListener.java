package MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.ARDrone.Kinect;

import java.awt.image.BufferedImage;

import de.yadrone.base.video.ImageListener;

public class VideoListener implements ImageListener {

	private BufferedImage image = null;

	@Override
	public void imageUpdated(BufferedImage image) {
		this.image = image;
	}
	
	public BufferedImage getIamge(){
		return image;
	}	
}
