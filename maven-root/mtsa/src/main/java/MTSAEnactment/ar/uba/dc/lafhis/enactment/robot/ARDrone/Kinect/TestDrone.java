package MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.ARDrone.Kinect;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class TestDrone extends JFrame {
		
	private PicWindow pWin = new PicWindow();
	private TextWindow tWin = new TextWindow();
	private GUIfordrone guid;
	public static void main(String[] args){
		// prepare window
		TestDrone Test = new TestDrone();
		//Test.run();
		Test.test();
	}
		
	public void test(){
		DroneAPI drone = new DroneAPI();
		guid=new GUIfordrone("DroneTest",drone);
		guid.setVisible(true);
	}
	
	public void run(){
		DroneAPI drone = new DroneAPI();
		drone.start();
		
		// Blink LED in red for 3 seconds.
		drone.blinkLED("RED");
		//wait the camera preparation.
		//drone.hover(5000);
		
		int[] a=drone.getPosition();		
		/*while(a==null){
			a=drone.getPosition();
			drone.wait(100);
		}*/
		drone.takeoff();
		drone.hover(500);
		drone.setCurrentDirectAsNorth();

		while(a==null){
			a=drone.getPosition();
			drone.wait(100);
		}
		tWin.text=Integer.toString(a[0])+","+Integer.toString(a[1]);
		tWin.repaint();
		
		try{
			drone.move(0, 2);
			tWin.text = "Success";
		}catch(Exception e){
			
		}
//		System.out.println(tWin.text);
		
		for(int i=0;i<30;i++){
			a =drone.getPosition();
			if(a==null)break;
			tWin.text=Integer.toString(a[0])+","+Integer.toString(a[1]);
//			System.out.println(tWin.text+","+Integer.toString(i));
			tWin.repaint();
			drone.wait(100);
		}//*/
		//tWin.text=Boolean.toString(drone.move(0, 0));
//		System.out.println(tWin.text);
		
		/*
		tWin.text=Boolean.toString(drone.move(0, 1));
		for(int i=0;i<20;i++){
			a =drone.getPosition();
			if(a==null)break;
			tWin.text=Integer.toString(a[0])+","+Integer.toString(a[1]);
			tWin.repaint();
			drone.wait(100);
		}//*/
		//this.command(drone);
		//drone.move(1,2);
		/*
		for(int i=0; i<100; i++){
			pWin.image = drone.takeFrontPicture();
			pWin.repaint();
			drone.test();
			tWin.text = drone.readFront();
			tWin.repaint();
			drone.wait(500);
		}
		*/
		//drone.wait(5000);
//		System.out.println(drone.landing());
		drone.blinkLED("GREEN");
		drone.test();

		drone.stop();
		System.exit(0);
	}
}

class PicWindow extends JFrame{
	
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

class TextWindow extends JPanel{

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

