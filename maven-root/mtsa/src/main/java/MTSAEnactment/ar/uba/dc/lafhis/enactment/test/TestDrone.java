package MTSAEnactment.ar.uba.dc.lafhis.enactment.test;

import javax.swing.JFrame;

import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.ARDrone.V1.DroneAPI;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.ARDrone.V1.PicWindow;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.ARDrone.V1.TextWindow;

public class TestDrone extends JFrame {
		
	private PicWindow pWin = new PicWindow();
	private TextWindow tWin = new TextWindow();
	
	public static void main(String[] args){
		// prepare window
		TestDrone Test = new TestDrone();
		Test.run();
	}
		
	public void run(){
		DroneAPI drone = new DroneAPI();
		drone.start();
		
		drone.takeoff();
		
		// Blink LED in red for 3 seconds.
//		drone.blinkLED("RED");
		
		//wait the camera preparation.
		drone.hover(8000);
		
		
//		for(int i=0; i<20; i++){
//			pWin.image = drone.takeFrontPicture();
//			pWin.repaint();
//			
//			tWin.text = drone.readFront();
//			tWin.repaint();
//			System.out.println(tWin.text);
//			drone.wait(500);
//		}
		
		
//		drone.blinkLED("GREEN");
		
		drone.landing();
		
		drone.stop();
		System.exit(0);
	}
}


