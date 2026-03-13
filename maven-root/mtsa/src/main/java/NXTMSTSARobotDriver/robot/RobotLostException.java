package NXTMSTSARobotDriver.robot;
/**
 * 
 */


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
