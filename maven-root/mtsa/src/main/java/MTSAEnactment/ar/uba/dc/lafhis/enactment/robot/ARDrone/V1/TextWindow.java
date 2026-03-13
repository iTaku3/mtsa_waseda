package MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.ARDrone.V1;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;



public class TextWindow extends JPanel{

	public String text = "";
	
	public TextWindow(){
		JFrame frame = new JFrame();
	    frame.getContentPane().add(this);
		frame.setSize(200,100);    
	    frame.setVisible(true);
	}
	
	public void paint(Graphics g)
	{
		g.clearRect(0, 0, 200, 100);
		g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
		g.setColor(Color.black);
		g.drawString(text, 10, 20);
	}
}