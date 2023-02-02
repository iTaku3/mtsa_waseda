package MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.ARDrone.V1;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class PicWindow extends JFrame{
	
	public BufferedImage image = null;
	
	public PicWindow(){
		setSize(640,360);	    
	    setVisible(true);
	}
	
	public void paint(Graphics g)
    {
		if(image != null){
			g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
		}
    }
}

