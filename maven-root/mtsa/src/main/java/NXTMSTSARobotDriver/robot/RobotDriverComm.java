package NXTMSTSARobotDriver.robot;



import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


import lejos.nxt.Button;
import lejos.nxt.ColorSensor.Color;
import lejos.nxt.LCD;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;

/**
 * 
 */

/**
 * Clase encargada de la comunicacion via BlueThooth
 * Traduce los codigos de comando en llamadas a los comandos de robot
 * y devuelve los resultados obtenidos al centro de comando
 * @author Julio
 *
 */
public class RobotDriverComm {
	//Referencia 
	private NXTRobot nxtRobot = null;
	
	private static String connected = "Connected";
    private static String waiting = "Waiting...";
    private static String closing = "Closing...";
    
    private static String driver_version = "1.29";
    
	/**
	 * @return the nxtRobot
	 */
	public NXTRobot getNxtRobot() {
		return nxtRobot;
	}
	/**
	 * @param nxtRobot the nxtRobot to set
	 */
	public void setNxtRobot(NXTRobot nxtRobot) {
		this.nxtRobot = nxtRobot;
	}
    
    
	
    /**
     * Metodo que inicializa la conexion y comienza a escuchar
     * @throws IOException 
     * @throws InterruptedException 
     * 
     */
    public void run() throws IOException, InterruptedException
    {

    	this.getNxtRobot().calibrar();
    	
		while (true)
		{
			try
			{
				LCD.drawString(waiting + "-" + driver_version,0,0);
				LCD.refresh();

		        BTConnection btc = Bluetooth.waitForConnection();
		        
				LCD.clear();
				LCD.drawString(connected,0,0);
				LCD.refresh();	

				DataInputStream dis = btc.openDataInputStream();
				DataOutputStream dos = btc.openDataOutputStream();
				
				int cmdId=0;
				while (cmdId != RobotDriverCommCommands.END_OF_COMM)
				{
					cmdId = dis.readInt();
					LCD.drawInt(cmdId,7,0,1);
					LCD.refresh();
		
					
					if (cmdId != RobotDriverCommCommands.END_OF_COMM) 
					{
						this.executeCommand(cmdId, dos, dis);					
						
						
					}
					
				}
				//Retorno OK relacionado al ultimo comando de cierre de sesion 
				this.executeCommand(cmdId, dos, dis);					
				dos.flush();
				
				dis.close();
				dos.close();
				
				
				LCD.clear();
				LCD.drawString(closing,0,0);
				LCD.refresh();
				btc.close();
				LCD.clear();

			} catch (Exception e)
			{
		

			} 
		}
    }
    
    private void executeCommand(int cmdId, DataOutputStream dos, DataInputStream dis) throws IOException
    {

    	//TODO: mejorar interpretacion y traduccion de comandos
    	try {
        	//Verifico si tengo una referencia al NXT Robot
    		if (this.getNxtRobot() == null) 
			{
	    		dos.writeInt(RobotDriverCommCommands.FAIL);
	    		dos.flush();
	    		return;
	    		
			}
		
	    	switch (cmdId)
	    	{
	    	case RobotDriverCommCommands.FOLLOW:
	    		this.getNxtRobot().avanzarHastaGris();
	    		dos.writeInt(RobotDriverCommCommands.SUCCESS);
	    		break;
	
	    	case RobotDriverCommCommands.TURNAROUND:
	    		this.getNxtRobot().darVuelta();
	    		dos.writeInt(RobotDriverCommCommands.SUCCESS);
	    		break;
	    		
	    	case RobotDriverCommCommands.TURN_RIGHT:
	    		this.getNxtRobot().doblarDerecha();
	    		dos.writeInt(RobotDriverCommCommands.SUCCESS);
	    		break;
	    		
	    	case RobotDriverCommCommands.TURN_LEFT:
	    		this.getNxtRobot().doblarIzquierda();
	    		dos.writeInt(RobotDriverCommCommands.SUCCESS);
	    		break;
	
	    	case RobotDriverCommCommands.FOLLOW_10:
	    		this.getNxtRobot().avanzar(10);
	    		dos.writeInt(RobotDriverCommCommands.SUCCESS);
	    		break;
	    		
	    	case RobotDriverCommCommands.READ_COLOR:
	    		Color color= this.getNxtRobot().readColor();
	    		dos.writeInt(color.getRed());
	    		dos.writeInt(color.getGreen());
	    		dos.writeInt(color.getBlue());
	    		break;
	    	
	    	case RobotDriverCommCommands.CALIBRAR:
	    		this.getNxtRobot().calibrar();
	    		dos.writeInt(RobotDriverCommCommands.SUCCESS);
	    		break;
	    		
	    	case RobotDriverCommCommands.SETUP_VAR:
	    		this.getNxtRobot().setTolerancia(dis.readInt());
	    		this.getNxtRobot().setToleranciaRGB(dis.readDouble());
	    		dos.writeInt(RobotDriverCommCommands.SUCCESS);
	    		break;
	    		
	    		
	    	case RobotDriverCommCommands.END_OF_COMM:
	    		dos.writeInt(RobotDriverCommCommands.SUCCESS);
	    		break;
	    		
	    	default:
	    		dos.writeInt(RobotDriverCommCommands.UNSUPPORTED_COMMAND);
	    		break;
	    	}
	    	
    	} catch (RobotLostException rle)
    	{
    		dos.writeInt(RobotDriverCommCommands.ROBOT_LOST);
    	} catch (Exception e)
    	{
    		dos.writeInt(RobotDriverCommCommands.FAIL); 
    	}
    	dos.flush();
    }
	
}
