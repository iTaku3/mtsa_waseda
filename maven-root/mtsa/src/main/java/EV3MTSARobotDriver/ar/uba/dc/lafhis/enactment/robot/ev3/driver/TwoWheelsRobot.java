package EV3MTSARobotDriver.ar.uba.dc.lafhis.enactment.robot.ev3.driver;


import lejos.hardware.BrickFinder;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.navigation.DifferentialPilot;

/**
 * @author Julio
 *
 */
public class TwoWheelsRobot extends EV3Robot{
	private static double wheelDiameter = 4.5;
	private static double trackWidth = 30;
	private static double speed = 8;
	private static double sensorWheelDistance = 6;
	private static String colorSensorPort = "S1";
	
	private static RegulatedMotor leftMotor = new EV3LargeRegulatedMotor(MotorPort.A);;
	private static RegulatedMotor rightMotor = new EV3LargeRegulatedMotor(MotorPort.C);;;
	
	
	public TwoWheelsRobot(String name, RobotDriverComm comm)
	{
		super(name, comm,
				new EV3ColorSensor(BrickFinder.getDefault().getPort(colorSensorPort)),
				new DifferentialPilot(wheelDiameter, trackWidth, leftMotor, rightMotor),
				leftMotor,
				rightMotor,
				sensorWheelDistance				
				);

		this.getPilot().setTravelSpeed(speed);
		
		
	}

}
