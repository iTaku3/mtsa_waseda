/**
 * 
 */
package MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.nxt;

/**
 * @author Julio
 *
 */
public class NXTRobotLostException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 342432432423L;
	public NXTRobotLostException()
	{
		super("Robot Lost");
	}
	public NXTRobotLostException(String message)
	{
		super(message);
	}
}
