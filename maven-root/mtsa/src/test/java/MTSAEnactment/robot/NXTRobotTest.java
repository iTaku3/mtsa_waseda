/**
 * 
 */
package MTSAEnactment.robot;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import MTSAEnactment.ar.uba.dc.lafhis.enactment.TransitionEvent;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.NXTRobot;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.nxt.NXTRobotComm;

/**
 * @author Julio
 *
 */
public class NXTRobotTest {

	private NXTRobot<String, String> nxtRobot;
	private NXTRobotComm nxtRobotComm;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		nxtRobotComm = new NXTRobotComm();
		nxtRobot = new NXTRobot<String, String>("Robot1", nxtRobotComm, "success", "failure", "lost"
				, "follow", "turnLeft", "turnRight", "turnAround", "calibrate");
		
		
		
	}

	
	/**
	 * Test method for {@link ar.uba.dc.lafhis.enactment.robot.NXTRobot#executeCommand(ar.uba.dc.lafhis.enactment.robot.commands.IEnactmentCommand)}.
	 */
	@Test
	public void testExecuteCommand() {
		
		
		System.out.println("Sending command ");
		try {
			nxtRobot.handleTransitionEvent(new TransitionEvent<String>(this, "follow"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
