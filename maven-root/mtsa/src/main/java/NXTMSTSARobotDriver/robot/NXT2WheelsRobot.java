package NXTMSTSARobotDriver.robot;



import lejos.nxt.ColorSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.robotics.navigation.DifferentialPilot;

/**
 * Modela robot LEGO configurado con 2 ruedas 
 * Version Mayo-2013
 * 
 * @author Julio
 *
 */
public class NXT2WheelsRobot extends NXTRobot {
	
	private static double wheelDiameter = 4.5;
	private static double trackWidth = 30;
	private static double speed = 8;
	private static double sensorWheelDistance = 6;
	
	public NXT2WheelsRobot(String name) {
		super(name);
		DifferentialPilot pilot = new DifferentialPilot(wheelDiameter, trackWidth, Motor.A, Motor.C);
		
		pilot.setTravelSpeed(speed);
		
		ColorSensor colorSensor = new ColorSensor(SensorPort.S1);
		this.setColorSensor(colorSensor);
		this.setPilot(pilot);
		this.setSensorWheelDistance(sensorWheelDistance);
	}
	
	

}
