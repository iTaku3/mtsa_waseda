package EV3MTSARobotDriver.ar.uba.dc.lafhis.enactment.robot.ev3.driver;

import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.Key;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.utility.Delay;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;




/**
 * @author Julio
 *
 */
public class RobotDriverComm {
	/**
	 * Clase encargada de la comunicacion via BlueThooth
	 * Traduce los codigos de comando en llamadas a los comandos de robot
	 * y devuelve los resultados obtenidos al centro de comando
	 * @author Julio
	 *
	 */
	//Referencia 
	private EV3Robot ev3Robot = null;
	
	private static String connected = "Connected";
    private static String waiting = "Waiting...";
    private static String closing = "Closing...";
    
    private static String driver_version = "1.29";
    
    private static int TCP_PORT = 1111;
    
    private GraphicsLCD LCD = LocalEV3.get().getGraphicsLCD();
    
    DataInputStream dis;
    DataOutputStream dos;
    
    
	/**
	 * @return the nxtRobot
	 */
	public EV3Robot getEV3Robot() {
		return ev3Robot;
	}
	/**
	 * @param nxtRobot the nxtRobot to set
	 */
	public void setEV3Robot(EV3Robot nxtRobot) {
		this.ev3Robot = nxtRobot;
	}
    
    
	
    /**
     * Metodo que inicializa la conexion y comienza a escuchar
     * @throws IOException 
     * @throws InterruptedException 
     * 
     */
    public void run() throws IOException, InterruptedException
    {

    	this.getEV3Robot().calibrar();
    	
    	ServerSocket serv = null;
    	Socket socket = null;
		//while (true)
		//{
    	
			try
			{
				//LCD.drawString("Drv " + driver_version,0,0,0);
				//LCD.drawString("Waiting on " + TCP_PORT,0,1,0);
				//LCD.refresh();
//				System.out.println("Start EV3 - " + driver_version);
//				System.out.println("Waiting on " + TCP_PORT);
				
				
				serv = new ServerSocket(TCP_PORT);
			
				socket = serv.accept(); //Wait for Laptop to connect
			
		        
				//LCD.clear();
				//LCD.drawString(connected,0,0,0);
				//LCD.refresh();	
//				System.out.println(connected);
				
				dis = new DataInputStream(socket.getInputStream());
				dos = new DataOutputStream(socket.getOutputStream());
				
				int cmdId=0;
				
				Brick brick = BrickFinder.getDefault();
				Key escape = brick.getKey("Escape");
				
				while (cmdId != RobotDriverCommCommands.END_OF_COMM && !escape.isDown())
				{
//					System.out.println("Reading next command...");
					cmdId = dis.readInt();
//					System.out.println("Command received: " + String.valueOf(cmdId));
		
					
					if (cmdId != RobotDriverCommCommands.END_OF_COMM) 
					{
						this.executeCommand(cmdId);											
						
					}
					
				
					
				}
//				System.out.println("Ending connection...");
				//Retorno OK relacionado al ultimo comando de cierre de sesion 
				//this.executeCommand(cmdId, dos, dis);					
				dos.flush();
				
				dis.close();
				dos.close();
				
				
//				System.out.println("Closing...");
				
				socket.close();
				serv.close();
				
				Delay.msDelay(100);
				
//				System.out.println("Application finished. Press escape button.");
				Button.waitForAnyPress();
				System.exit(0);
				
			} catch (Exception e)
			{
				if (e != null) 
				{
//					System.out.println("Error: " + e.getMessage());
					Button.waitForAnyPress();					
				}
				if (socket != null && !socket.isClosed()) socket.close();
				if (serv!=null && !serv.isClosed()) serv.close();

			} 
		//}
    }
    
    public void sendColorSensorValue(double value)
    {
    	
    	try
    	{
    		dos.writeInt(RobotDriverCommCommands.COLOR_SENSOR_VALUE);
    		dos.writeDouble(value);
    		dos.flush();
    	} catch (Exception e)
    	{
//    		System.out.println("sendColorSensorValue" + e.getMessage());
    	}    	    	
    }
    public void sendIntersectionValue(double value)
    {
    	try
    	{
    		dos.writeInt(RobotDriverCommCommands.COLOR_INTERSECTION_VALUE);
    		dos.writeDouble(value);
    		dos.flush();
    	} catch (Exception e)
    	{
//    		System.out.println("sendIntersectionValue" + e.getMessage());
    	}
    }
    public void sendPathValue(double value)
    {
    	try
    	{
    		dos.writeInt(RobotDriverCommCommands.COLOR_PATH_VALUE);
    		dos.writeDouble(value);
    		dos.flush();
    	} catch (Exception e)
    	{
//    		System.out.println("sendPathValue" + e.getMessage());
    	}
    }
    public void sendOutOfPathValue(double value)
    {
    	try
    	{
    		dos.writeInt(RobotDriverCommCommands.COLOR_OUTOFPATH_VALUE);
    		dos.writeDouble(value);
    		dos.flush();
    	} catch (Exception e)
    	{
//    		System.out.println("sendOutOfPathValue" + e.getMessage());
    	}
    }
    
    private void executeCommand(int cmdId) throws IOException
    {
//    	System.out.println("Execute command");

    	//TODO: mejorar interpretacion y traduccion de comandos
    	try {
        	//Verifico si tengo una referencia al NXT Robot
    		if (this.getEV3Robot() == null) 
			{
//    			System.out.println("Robot is not defined");
	    		dos.writeInt(RobotDriverCommCommands.FAIL);
	    		dos.flush();
	    		return;
	    		
			}
		
	    	switch (cmdId)
	    	{
	    	case RobotDriverCommCommands.FOLLOW:
//	    		System.out.println("Follow");
	    		this.getEV3Robot().avanzarHastaGris();
	    		dos.writeInt(RobotDriverCommCommands.SUCCESS);
//	    		System.out.println("Response sent");
	    		break;
	
	    	case RobotDriverCommCommands.TURNAROUND:
//	    		System.out.println("Turnaround");
	    		this.getEV3Robot().darVuelta();
	    		dos.writeInt(RobotDriverCommCommands.SUCCESS);
	    		break;
	    		
	    	case RobotDriverCommCommands.TURN_RIGHT:
//	    		System.out.println("Turn right");
	    		this.getEV3Robot().doblarDerecha();
	    		dos.writeInt(RobotDriverCommCommands.SUCCESS);
	    		break;
	    		
	    	case RobotDriverCommCommands.TURN_LEFT:
//	    		System.out.println("Turn left");
	    		this.getEV3Robot().doblarIzquierda();
	    		dos.writeInt(RobotDriverCommCommands.SUCCESS);
	    		break;
	
	    	case RobotDriverCommCommands.FOLLOW_10:
	    		
	    		this.getEV3Robot().avanzar(10);
	    		dos.writeInt(RobotDriverCommCommands.SUCCESS);
	    		break;
	    		
	    	
	    	case RobotDriverCommCommands.CALIBRAR:
	    		this.getEV3Robot().calibrar();
	    		dos.writeInt(RobotDriverCommCommands.SUCCESS);
	    		break;
	    		
	    	case RobotDriverCommCommands.DEBUG_MODE_ON:
	    		this.getEV3Robot().setDebugMode(true);
	    		break;
	    		
	    	case RobotDriverCommCommands.END_OF_COMM:
	    		dos.writeInt(RobotDriverCommCommands.SUCCESS);
	    		break;
	    	case RobotDriverCommCommands.REQUEST_PATH_VALUE:
	    		dos.writeInt(RobotDriverCommCommands.COLOR_PATH_VALUE);
	    		dos.writeDouble(this.getEV3Robot().getPathColor());
	    		break;
	    	case RobotDriverCommCommands.REQUEST_INTERSECTION_VALUE:
	    		dos.writeInt(RobotDriverCommCommands.COLOR_INTERSECTION_VALUE);
	    		dos.writeDouble(this.getEV3Robot().getIntersectionColor());
	    		break;
	    	case RobotDriverCommCommands.REQUEST_OUTOFPATH_VALUE:
	    		dos.writeInt(RobotDriverCommCommands.COLOR_OUTOFPATH_VALUE);
	    		dos.writeDouble(this.getEV3Robot().getOutOfPath());
	    		break;
	    	case RobotDriverCommCommands.TEST_COLOR:
	    		this.getEV3Robot().testReadColor();
	    		break;
	    	default:
	    		dos.writeInt(RobotDriverCommCommands.UNSUPPORTED_COMMAND);
	    		break;
	    	}
	    	
    	} catch (Exception e)
    	{
//    		System.out.println("Error execute: " + e.getMessage());
    		dos.writeInt(RobotDriverCommCommands.FAIL); 
    	}
//    	System.out.println("Flush");
    	dos.flush();
//    	System.out.println("End execute");
    }
	

}
