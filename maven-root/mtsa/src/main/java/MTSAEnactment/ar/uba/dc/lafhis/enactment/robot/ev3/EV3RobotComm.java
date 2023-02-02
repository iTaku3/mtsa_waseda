/**
 * 
 */
package MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.ev3;

import java.awt.Robot;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.NXTRobot;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.nxt.NXTCommException;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.nxt.NXTRobotLostException;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.nxt.NXTRobotComm.RobotDriverCommCommands;

/**
 * Communication Layer for EV3 robot
 * @author Julio
 *
 */
public class EV3RobotComm {

	public class RobotDriverCommCommands {
		//Comandos
		public static final int FOLLOW = 1;
		public static final int TURN_BACK = 2;
		public static final int TURN_RIGHT = 3;
		public static final int TURN_LEFT = 4;
		
		public static final int END_OF_COMM= 99;
		
		public static final int SUCCESS = 0;
		public static final int FAIL = 1;
		public static final int ROBOT_LOST = 2;
		public static final int UNSUPPORTED_COMMAND = 3;
		
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
				
	}
	public interface ICommCallBack {
		void onSensorValue(double value);
		void onIntersectionValue(double value);
		void onPathValue(double value);
		void onOutOfPathValue(double value);

	}
	private Socket socket = null;
	private DataOutputStream streamOutput;
	private DataInputStream streamInput;
	
	private String address = "10.0.1.1";
	private int port = 1111;
	
	private Logger logger = LogManager.getLogger(EV3RobotComm.class.getName());
	
	private boolean syncCalls = true;
	private ICommCallBack callBack = null;
	

	public EV3RobotComm() throws EV3RobotCommException
	{
		setupConnection();
	}
	
	private void setupConnection() throws EV3RobotCommException 
	{
		if (socket != null )
			throw new EV3RobotCommException("Connection already created");

		try {
			socket = new Socket(address, port);			

			streamOutput = new DataOutputStream(socket.getOutputStream());
			streamInput = new DataInputStream(socket.getInputStream());

		} catch (UnknownHostException e) {
			logger.error(e.getMessage(), e);
			throw new EV3RobotCommException(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new EV3RobotCommException(e.getMessage());
		}
		
		
	}

	private boolean isConnected()
	{
		return (socket != null || socket.isConnected());
	}
	

	private void sendCommand(int command) throws EV3RobotLostException, EV3RobotCommException
	{
		if (!isConnected()) setupConnection();
		
		try {
			streamOutput.writeInt(command);
			streamOutput.flush();
			
			if (this.isSyncCalls())
			{
				//lee resultado
				int cmdResp = streamInput.readInt();
				
				if (cmdResp == RobotDriverCommCommands.SUCCESS)
					return;
				else if (cmdResp == RobotDriverCommCommands.ROBOT_LOST)
					throw new EV3RobotLostException("Robot lost");
				else 
					//throw new EV3RobotCommException(String.valueOf(cmdResp));
					this.processInputValue(cmdResp);
			}
			
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new EV3RobotCommException(e.getMessage());
			
		}

	}
	
	private void sendCommand(int command, double value) throws EV3RobotLostException, EV3RobotCommException
	{
		if (!isConnected()) setupConnection();
		
		try {
			streamOutput.writeInt(command);
			streamOutput.writeDouble(value);
			streamOutput.flush();
			
			if (this.isSyncCalls())
			{
				//lee resultado
				int cmdResp = streamInput.readInt();
				
				if (cmdResp == RobotDriverCommCommands.SUCCESS)
					return;
				else if (cmdResp == RobotDriverCommCommands.ROBOT_LOST)
					throw new EV3RobotLostException("Robot lost");
				else
					throw new EV3RobotCommException(String.valueOf(cmdResp));
			}
			
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new EV3RobotCommException(e.getMessage());
			
		}

	}
	public void testColor() throws EV3RobotLostException, EV3RobotCommException
	{
		this.sendCommand(RobotDriverCommCommands.TEST_COLOR);
	}
	public void follow() throws EV3RobotCommException, EV3RobotLostException
	{
		this.sendCommand(RobotDriverCommCommands.FOLLOW);
	}
	
	public void turnBack() throws EV3RobotLostException, EV3RobotCommException 
	{
		this.sendCommand(RobotDriverCommCommands.TURN_BACK);
	}
	
	public void turnLeft() throws EV3RobotLostException, EV3RobotCommException
	{
		this.sendCommand(RobotDriverCommCommands.TURN_LEFT);
	}
	
	public void turnRight() throws EV3RobotLostException, EV3RobotCommException 
	{
		this.sendCommand(RobotDriverCommCommands.TURN_RIGHT);
	}
	

	public void sendDebugMode() throws EV3RobotLostException, EV3RobotCommException
	{
		this.sendCommand(RobotDriverCommCommands.DEBUG_MODE_ON);
	}
	
	public void sendTolerancia(double value) throws EV3RobotLostException, EV3RobotCommException
	{
		this.sendCommand(RobotDriverCommCommands.SET_TOLERANCIA, value);
	}
	
	public void sendRequestPathColor() throws EV3RobotLostException, EV3RobotCommException
	{
		this.sendCommand(RobotDriverCommCommands.REQUEST_PATH_VALUE);
	}
	
	public void sendRequestIntersectionColor() throws EV3RobotLostException, EV3RobotCommException 
	{
		this.sendCommand(RobotDriverCommCommands.REQUEST_INTERSECTION_VALUE);
	}
	
	public void sendRequestOutOfPathColor() throws EV3RobotLostException, EV3RobotCommException
	{
		this.sendCommand(RobotDriverCommCommands.REQUEST_OUTOFPATH_VALUE);
	}
	
	private void processInputValue(int command) throws EV3RobotCommException
	{
		double value;
		try
		{
			switch(command)
			{
			case RobotDriverCommCommands.COLOR_SENSOR_VALUE:
				value = streamInput.readDouble();
				if (this.getCallBack() != null) this.getCallBack().onSensorValue(value); 				
				break;
			case RobotDriverCommCommands.COLOR_INTERSECTION_VALUE:
				value = streamInput.readDouble();
				if (this.getCallBack() != null) this.getCallBack().onIntersectionValue(value); 				
				break;
			case RobotDriverCommCommands.COLOR_OUTOFPATH_VALUE:
				value = streamInput.readDouble();
				if (this.getCallBack() != null) this.getCallBack().onOutOfPathValue(value); 				
				break;
			case RobotDriverCommCommands.COLOR_PATH_VALUE:
				value = streamInput.readDouble();
				if (this.getCallBack() != null) this.getCallBack().onPathValue(value); 				
				break;
				
			default:
				throw new Exception("Invalid command");
			}
		}
		catch (Exception e)
		{
			throw new EV3RobotCommException(e.getMessage());
		}
	}
	
	public void verifyInput() throws EV3RobotCommException
	{
		try
		{
		
			
			while(this.streamInput.available()>0)
			{
				int readCmd = this.streamInput.readInt();
				this.processInputValue(readCmd);

			}
			
		} catch(Exception e)
		{
			throw new EV3RobotCommException(e.getMessage());
		}
	}
	
	
	/**
	 * @return the syncCalls
	 */
	public boolean isSyncCalls() {
		return syncCalls;
	}

	/**
	 * @param syncCalls the syncCalls to set
	 */
	public void setSyncCalls(boolean syncCalls) {
		this.syncCalls = syncCalls;
	}

	
	/**
	 * @return the callBack
	 */
	private ICommCallBack getCallBack() {
		return callBack;
	}

	/**
	 * @param callBack the callBack to set
	 */
	public void setCallBack(ICommCallBack callBack) {
		this.callBack = callBack;
	}

	private void closeConnection()
	{
		if (this.isConnected())
		{
			try {
				socket.close();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
			socket = null;
			
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
	
}
