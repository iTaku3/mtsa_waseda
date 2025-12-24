package MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.ARDrone.Kinect;

import de.yadrone.base.ARDrone;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;

public class TestImageListener extends JFrame {
  private BufferedImage image = null;

  public TestImageListener(final ARDrone drone) {
    super("YADrone Tutorial");

    setSize(640, 360);
    setVisible(true);
  }

  public void paint(Graphics g) {
    if (image != null) g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
  }
}
