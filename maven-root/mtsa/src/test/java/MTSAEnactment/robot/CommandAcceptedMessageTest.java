package MTSAEnactment.robot;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.N6.robotProtocol.CommandAcceptedMessage;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.N6.robotProtocol.Message;


public class CommandAcceptedMessageTest {

	protected static int TO_VALUE			= 1;
	protected static int FROM_VALUE			= 4;
	protected static int MID_VALUE			= 1;
	protected static int EXPECTED_LENGTH	= Message.MESSAGE_HEADER_LENGTH + CommandAcceptedMessage.ACCEPTED_MID_FIELD_LENGTH;
	protected static String SERIAL_MSG		= "LAFHIS003000050011000100040001";
	
	protected CommandAcceptedMessage msg;
	protected ByteArrayInputStream stringReader;
	
	@Before
	public void setUp() throws Exception {
		msg 			= new CommandAcceptedMessage(TO_VALUE, FROM_VALUE, MID_VALUE);
		stringReader	= new ByteArrayInputStream(SERIAL_MSG.getBytes("UTF8"));
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetMessageLength() {
		assertEquals(EXPECTED_LENGTH, msg.getMessageLength());
	}
	
	@Test
	public void testWriteData() {
		CommandAcceptedMessage newMsg	= new CommandAcceptedMessage(SERIAL_MSG);
		assertEquals("Read message equals written message", SERIAL_MSG, newMsg.getPackedMessage());
	}	
	

	@Test
	public void testGetAcceptedMid() {
		assertEquals("Accepted Mid", MID_VALUE, msg.getAcceptedMid());
	}

}
