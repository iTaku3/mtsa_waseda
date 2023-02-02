package MTSAEnactment.robot;

import static org.junit.Assert.*;

import java.io.IOException;

import lejos.robotics.Color;

import org.junit.Before;
import org.junit.Test;

import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.nxt.NXTCommException;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.nxt.NXTRobotComm;

public class NXTRobotCommTest {

	private NXTRobotComm nxtRobotComm;
	@Before
	public void setUp() throws Exception {
		
	}

	//@Test
	public void testReadColor() {
		
		try {
			nxtRobotComm = new NXTRobotComm();

		} catch (NXTCommException e) {

			e.printStackTrace();
			return;
		}
		
		
		try 
		{
			int retCode = 0;
			System.out.println("Calibracion...");
			nxtRobotComm.calibrar();
			System.out.println("Color: " + retCode);
			
			
			System.out.println("Send command...");
			Color color = nxtRobotComm.readColor();
			System.out.println("Color: " + color.getRed() + "-" + color.getGreen() + "-" + color.getBlue());
						
			
			System.out.println("Send command...");
			nxtRobotComm.endCommunication();
			System.out.println("Return: " + String.valueOf(retCode));
			
		} catch (IOException oiE)
		{
			System.out.println("Error - " + oiE.getMessage());
		} catch (NXTCommException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		

	}

}
