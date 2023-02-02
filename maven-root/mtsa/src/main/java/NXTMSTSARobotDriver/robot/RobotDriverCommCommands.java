package NXTMSTSARobotDriver.robot;


/**
 * Commands definitions
 * @author Julio
 *
 */
public class RobotDriverCommCommands {
	//Comandos
	public static final int FOLLOW = 1;
	public static final int TURNAROUND = 2;
	public static final int TURN_RIGHT = 3;
	public static final int TURN_LEFT = 4;
	
	//Misc - deprecated
	public static final int SETUP_VAR = 95; //Tolerance(int), ToleranceRGB(double)
	public static final int CALIBRAR = 96;
	public static final int READ_COLOR = 97;
	public static final int FOLLOW_10 = 98;
	public static final int END_OF_COMM= 99;
	
	//Replay Values
	public static final int SUCCESS = 0;
	public static final int FAIL = 1;
	public static final int ROBOT_LOST = 2;
	public static final int UNSUPPORTED_COMMAND = 3;
	
	
	
}
