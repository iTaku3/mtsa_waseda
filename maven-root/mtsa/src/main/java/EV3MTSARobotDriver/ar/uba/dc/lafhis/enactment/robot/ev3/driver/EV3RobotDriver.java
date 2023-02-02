package EV3MTSARobotDriver.ar.uba.dc.lafhis.enactment.robot.ev3.driver;



import java.util.ArrayList;

import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.Key;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.Font;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.SampleProvider;
import lejos.robotics.filter.AbstractFilter;
import lejos.utility.Delay;

/**
 * @author Julio
 *
 */
public class EV3RobotDriver {

	private static void intro()
	{
		GraphicsLCD g = LocalEV3.get().getGraphicsLCD();
		g.drawString("EV3 Enacment ", 5, 0, 0);
		g.setFont(Font.getSmallFont());
		 
		g.drawString("Press any key to start", 2, 20, 0);
		
		Button.waitForAnyPress();
		if(Button.ESCAPE.isDown()) System.exit(0);
		g.clear();

	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//intro();
		
		EV3RobotDriver driver = new EV3RobotDriver();
		


	}
	
	//Constructor
	public EV3RobotDriver() {
		RobotDriverComm comm = new RobotDriverComm();
		comm.setEV3Robot(new TwoWheelsRobot("EV3", comm));
		
		try {
			comm.run();
		} catch (Exception e) {
//			System.out.println("Error " + e.getMessage());
			Button.waitForAnyPress();
		} 
		
	}
}
