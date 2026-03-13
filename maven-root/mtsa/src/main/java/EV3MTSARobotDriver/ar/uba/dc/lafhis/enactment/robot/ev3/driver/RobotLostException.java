package EV3MTSARobotDriver.ar.uba.dc.lafhis.enactment.robot.ev3.driver;

/**
 * Excepcion cuando el robot se pierde
 * @author Julio
 *
 */
public class RobotLostException extends Exception {
	private static final String MESSAGE = "Robot lost";
	
	public RobotLostException()
	{
		super(MESSAGE);
	}
	public RobotLostException(String message)
	{
		super(message);
	}
}
