/**
 * 
 */
package MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.nxt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import lejos.nxt.ColorSensor.Color;
import lejos.pc.comm.NXTCommLogListener;
import lejos.pc.comm.NXTConnector;


/**
 * @author Julio
 *
 */
public class NXTRobotComm {


	//Codificacion de los comandos - Estos codigos deben estar en sintonia
	//	con el driver
	//TODO: Revisar si es posible mejorar dise�o
	public class RobotDriverCommCommands {
		//Comandos
		public static final int FOLLOW = 1;
		public static final int TURN_BACK = 2;
		public static final int TURN_RIGHT = 3;
		public static final int TURN_LEFT = 4;
		
		public static final int SETUP_VAR = 95; //Tolerancia(int), ToleranciaRGB(double)
		public static final int CALIBRAR = 96;
		public static final int READ_COLOR = 97;	//Retorna 3 int RGB respectivamente
		public static final int FOLLOW_10 = 98;
		public static final int END_OF_COMM= 99;
		
		public static final int SUCCESS = 0;
		public static final int FAIL = 1;
		public static final int ROBOT_LOST = 2;
		public static final int UNSUPPORTED_COMMAND = 3;
				
	}
	
	
	private NXTConnector conn;
	private DataOutputStream streamOutput;
	private DataInputStream streamInput;
	
	private String address = "";
	private String port = "";
	
	boolean connected = false;
	public void setupConnection() throws NXTCommException
	{
		
			
		conn = new NXTConnector();
		
		conn.addLogListener(new NXTCommLogListener(){

			public void logEvent(String message) {
//				System.out.println("BTSend Log.listener: "+message);
				
			}

			public void logEvent(Throwable throwable) {
//				System.out.println("BTSend Log.listener - stack trace: ");
				 throwable.printStackTrace();
				
			}
			
		} 
		);
		
		
	}
	
	private void checkConnection() throws NXTCommException
	{
	
		if (!connected)
		{
			String connStr = "btspp://";
			if (this.address != "") connStr += this.address;
			if (this.port != "") connStr += ":" + this.port;
			
//			System.out.println("Connecting to: " + connStr);
			
			connected = conn.connectTo(connStr);
			if (!connected) {
				throw new NXTCommException("Failed to connect to any NXT");
			}		
			streamOutput = new DataOutputStream(conn.getOutputStream());
			streamInput = new DataInputStream(conn.getInputStream());
		}		
	}
	
	
	public void follow() throws IOException, NXTRobotLostException, NXTCommException 
	{
		checkConnection();
		
		streamOutput.writeInt(RobotDriverCommCommands.FOLLOW);
		streamOutput.flush();
		//lee resultado
		int cmdResp = streamInput.readInt();
		
		if (cmdResp == RobotDriverCommCommands.SUCCESS)
			return;
		else if (cmdResp == RobotDriverCommCommands.ROBOT_LOST)
			throw new NXTRobotLostException();
		else
			throw new NXTCommException(cmdResp);
		
		
	}
	
	public void turnBack() throws IOException, NXTRobotLostException, NXTCommException
	{
		checkConnection();
		
		streamOutput.writeInt(RobotDriverCommCommands.TURN_BACK);
		streamOutput.flush();
		//lee resultado
		int cmdResp = streamInput.readInt();

		if (cmdResp == RobotDriverCommCommands.SUCCESS)
			return;
		else if (cmdResp == RobotDriverCommCommands.ROBOT_LOST)
			throw new NXTRobotLostException();
		else
			throw new NXTCommException(cmdResp);

	}
	
	public void turnLeft() throws IOException, NXTRobotLostException, NXTCommException
	{
		checkConnection();
		
		streamOutput.writeInt(RobotDriverCommCommands.TURN_LEFT);
		streamOutput.flush();
		//lee resultado
		int cmdResp = streamInput.readInt();

		if (cmdResp == RobotDriverCommCommands.SUCCESS)
			return;
		else if (cmdResp == RobotDriverCommCommands.ROBOT_LOST)
			throw new NXTRobotLostException();
		else
			throw new NXTCommException(cmdResp);

	}
	
	public void turnRight() throws IOException, NXTRobotLostException, NXTCommException
	{
		checkConnection();
		
		streamOutput.writeInt(RobotDriverCommCommands.TURN_RIGHT);
		streamOutput.flush();
		
		//lee resultado
		int cmdResp = streamInput.readInt();

		if (cmdResp == RobotDriverCommCommands.SUCCESS)
			return;
		else if (cmdResp == RobotDriverCommCommands.ROBOT_LOST)
			throw new NXTRobotLostException();
		else
			throw new NXTCommException(cmdResp);

	}
	
	public Color readColor() throws IOException, NXTCommException
	{
		checkConnection();
		
		streamOutput.writeInt(RobotDriverCommCommands.READ_COLOR);
		streamOutput.flush();
		
		//lee resultado color en 3 enteros
		int red = streamInput.readInt();
		int green = streamInput.readInt();
		int blue = streamInput.readInt();
		
//		//System.out.println(red + "," + green + "," + blue);
		return new Color(red,green,blue, 0, 0);	
		
	}
	
	public void endCommunication() throws IOException, NXTCommException
	{
		streamOutput.writeInt(RobotDriverCommCommands.END_OF_COMM);
		streamOutput.flush();
		int cmdResp = streamInput.readInt();
		if (cmdResp == RobotDriverCommCommands.SUCCESS)
			return;
		else
			throw new NXTCommException();
			
		
	}
	
	public void calibrar() throws IOException, NXTCommException
	{
		checkConnection();
		
		streamOutput.writeInt(RobotDriverCommCommands.CALIBRAR);
		streamOutput.flush();
		int cmdResp = streamInput.readInt();
		
		if (cmdResp == RobotDriverCommCommands.SUCCESS)
			return;
		else
			throw new NXTCommException();

	}
	public void setupVars(int tolerancia, double toleranciaRGB)  throws IOException, NXTCommException
	{
		checkConnection();		
		streamOutput.writeInt(RobotDriverCommCommands.SETUP_VAR);
		streamOutput.writeInt(tolerancia);
		streamOutput.writeDouble(toleranciaRGB);
		streamOutput.flush();
		int cmdResp = streamInput.readInt();
		
		if (cmdResp == RobotDriverCommCommands.SUCCESS)
			return;
		else
			throw new NXTCommException();
	}
	
	private void closeConnection()
	{
		try {
			streamInput.close();
			streamOutput.close();
			conn.close();
		} catch (IOException ioe) {
//			System.out.println("IOException closing connection:");
//			System.out.println(ioe.getMessage());
		}
        
	}
		
	
	 @Override
	    protected void finalize() throws Throwable {
	        try{
	        	
	        	closeConnection();
	            
	        }catch(Throwable t){
	            throw t;
	        }finally{
	            
	            super.finalize();
	        }
	     
	 }
	public NXTRobotComm() throws NXTCommException {
		
		setupConnection();
				
	}

	public NXTRobotComm(String address, String port) throws NXTCommException {
		
		this.address = address;
		this.port = port;
		setupConnection();
				
	}


		
	
}
