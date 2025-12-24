package MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.ARDrone.Kinect;

import de.yadrone.base.video.ImageListener;
import java.awt.image.BufferedImage;

public class VideoListener implements ImageListener {

  private BufferedImage image = null;

  @Override
  public void imageUpdated(BufferedImage image) {
    this.image = image;
  }

  public BufferedImage getIamge() {
    return image;
  }
}
