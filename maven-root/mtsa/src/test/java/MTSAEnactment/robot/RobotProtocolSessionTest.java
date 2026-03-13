package MTSAEnactment.robot;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.N6.robotProtocol.RobotProtocolSession;


public class RobotProtocolSessionTest {

	public static int MASTER_ID			= 1;
	public static int ROBOT_ID			= 4;
	
	public static String SERIAL_OUT_LOC	= "/dev/ttyp5";
	public static String SERIAL_IN_LOC	= "/dev/ptyp5";
	
	
	protected RobotProtocolSession session;
	
	
	@Before
	public void setUp() throws Exception {
		session	= new RobotProtocolSession(MASTER_ID, ROBOT_ID, SERIAL_OUT_LOC);
	}

	@After
	public void tearDown() throws Exception {
	}

	//@Test
	public void testGetMasterId() {
		fail("Not yet implemented");
	}

	//@Test
	public void testGetRobotId() {
		fail("Not yet implemented");
	}

	//@Test
	public void testGetSerialLocation() {
		fail("Not yet implemented");
	}

	//@Test
	public void testRobotProtocolSession() {
		fail("Not yet implemented");
	}

	//@Test
	public void testIsConnected() {
		fail("Not yet implemented");
	}

	//@Test
	public void testOpen() {
		fail("Not yet implemented");
	}

	//@Test
	public void testClose() {
		fail("Not yet implemented");
	}

	//@Test
	public void testSendMessage() {
		fail("Not yet implemented");
	}

	//@Test
	public void testEventHandler() {
		fail("Not yet implemented");
	}

	//@Test
	public void testRun() {
		fail("Not yet implemented");
	}

}
