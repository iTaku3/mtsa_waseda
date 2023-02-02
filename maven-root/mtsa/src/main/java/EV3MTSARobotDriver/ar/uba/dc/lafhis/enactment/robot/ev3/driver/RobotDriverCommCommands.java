package EV3MTSARobotDriver.ar.uba.dc.lafhis.enactment.robot.ev3.driver;

/**
 * @author Julio
 *
 */
public class RobotDriverCommCommands {
	//Comandos
	public static final int FOLLOW = 1;
	public static final int TURNAROUND = 2;
	public static final int TURN_RIGHT = 3;
	public static final int TURN_LEFT = 4;
	public static final int END_OF_COMM= 99;
	
	//Misc - deprecated
	public static final int CALIBRAR = 96;
	public static final int FOLLOW_10 = 98;

	//Internal
	public static final int SET_TOLERANCIA = 90;	//Controller->Robot (double value)
	public static final int COLOR_SENSOR_VALUE = 91;		//Robot->Controller (double value)
	public static final int COLOR_INTERSECTION_VALUE = 92;		//Robot->Controller (double value)
	public static final int COLOR_PATH_VALUE = 93;		//Robot->Controller (double value)
	public static final int COLOR_OUTOFPATH_VALUE = 94;		//Robot->Controller (double value)
	public static final int DEBUG_MODE_ON = 95;		//Controller->Robot
	public static final int REQUEST_INTERSECTION_VALUE = 80;	//Controller-Robot
	public static final int REQUEST_PATH_VALUE = 81;	//Controller-Robot
	public static final int REQUEST_OUTOFPATH_VALUE = 82;	//Controller-Robot
	public static final int TEST_COLOR = 83;	//Controller->Robot
			
	//Replay Values
	public static final int SUCCESS = 0;
	public static final int FAIL = 1;
	public static final int ROBOT_LOST = 2;
	public static final int UNSUPPORTED_COMMAND = 3;
}
