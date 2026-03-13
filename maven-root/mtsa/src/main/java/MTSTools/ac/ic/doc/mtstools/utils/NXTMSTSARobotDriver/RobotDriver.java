package MTSTools.ac.ic.doc.mtstools.utils.NXTMSTSARobotDriver;


import NXTMSTSARobotDriver.robot.NXT2WheelsRobot;
import NXTMSTSARobotDriver.robot.RobotDriverComm;
import lejos.nxt.Button;
import lejos.nxt.LCD;

/**
 * 
 */

/**
 * Entry point for NXT driver
 * Listen remote commands, execute and reply the result
 * 
 * @author Julio
 *
 */
public class RobotDriver {


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		RobotDriverComm comm = new RobotDriverComm();
		comm.setNxtRobot(new NXT2WheelsRobot("NXT"));
		
		try
		{
			comm.run();
			
		} catch (Exception e)
		{
			LCD.clear();
			LCD.drawString("Err: " + e.getMessage() ,0,0);
			LCD.refresh();
			
			Button.waitForAnyPress();
		}
		

	}

}
