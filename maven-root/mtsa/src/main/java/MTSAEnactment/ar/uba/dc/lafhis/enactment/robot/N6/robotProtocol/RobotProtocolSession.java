package MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.N6.robotProtocol;

import java.util.ArrayList;
import java.util.List;

import jssc.SerialPort;
import jssc.SerialPortException;

public class RobotProtocolSession extends MessageDispatcher 
	implements IMessageListener, Runnable{
	
	protected static int SLEEP_AMOUNT		= 33;
	protected static int MESSAGE_CUSHION	= 100;
	protected static int MESSAGE_TIMEOUT	= 2500;
	protected static int MESSAGE_RETRIES	= 4;
	protected static int SERIAL_BUFFER_SIZE	= 512;
	
	protected static Thread updateThread;

	protected SerialPort serial;

	protected String serialLocation;
	
	protected String[] serialLocations;
	
	protected int masterId;
	
	protected int robotId;
	
	private boolean isOpen;
	
	private boolean isOpening;
	
	private boolean isClosing;
	
	protected byte[] serialQueue;
	
	protected int serialQueueHead;
	
	protected int serialQueueTail;
	
	protected List<Message> pendingQueue;
	
	protected Message messageOnCourse;
	
	protected long messageOnCourseTimestamp; 
	
	protected int messageOnCourseRetries;
	
	public int getMasterId(){
		return masterId;
	}
	
	public int getRobotId(){
		return robotId;
	}
	
	public String getSerialLocation(){
		return serialLocation;
	}
	
	public RobotProtocolSession(int masterId, int robotId, String ... serialLocations){
		this.masterId		= masterId;
		this.robotId		= robotId;
		this.serialLocations= serialLocations;
		isOpen				= false;
		isOpening			= false;
		isClosing			= false;
		
		pendingQueue		= new ArrayList<Message>();
		messageOnCourse		= null;
		initializeSerial();
	}
	
	private void initializeSerial(){
		serialQueue		= new byte[SERIAL_BUFFER_SIZE];
		serialQueueHead	= 0;
		serialQueueTail	= 0;
		
		
		for(int i = 0; i < serialLocations.length; i++){
			serialLocation	= serialLocations[i];
	        serial 			= new SerialPort(serialLocation);
	        
	        try {
	        	serial.openPort();//Open serial port
	        	serial.setParams(SerialPort.BAUDRATE_9600, 
	                                 SerialPort.DATABITS_8,
	                                 SerialPort.STOPBITS_1,
	                                 SerialPort.PARITY_NONE);//Set params. Also you can set params by this string: serialPort.setParams(9600, 8, 1, 0);
	    		updateThread		= new Thread(this);
	    		updateThread.start();       
	    		break;
	        }
	        catch (SerialPortException ex) {
	            ex.printStackTrace();
	            serial = null;
	        }  		
        }
	}
	
	public boolean isConnected(){
		return isOpen && serial.isOpened();
	}
	
	public synchronized boolean open(){
		if(!serial.isOpened())
			return false;
		if(!isOpen && !isOpening && !isClosing){
			isOpening						= true;
			CommunicationStartMessage msg	= new CommunicationStartMessage(robotId, masterId);
			msg.addMessageListener(this);
			sendMessage(msg);
			return true;
		}else
			return false;
	}
	
	public synchronized boolean close(){
		if(isOpen && !isOpening && !isClosing){
			isClosing	= true;
			CommunicationStopMessage msg	= new CommunicationStopMessage(robotId, masterId);
			msg.addMessageListener(this);
			sendMessage(msg);			
			return true;
		}else
			return false;
	}
	
	protected void internalClose(){
		if(isClosing){
			isClosing				= false;
			isOpen					= false;
			messageOnCourse			= null;
			messageOnCourseRetries	= 0;
			pendingQueue			= new ArrayList<Message>();			
			try {
				serial.closePort();
			} catch (SerialPortException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		}
	}
	
	public synchronized boolean sendMessage(Message message){
		if(!(message instanceof CommunicationStartMessage || message instanceof CommunicationStopMessage 
				|| isOpen))
			return false;
		
		message.addMessageListener(this);
		
		if(pendingQueue.size() == 0){
			
			try {
				serial.writeString(message.getPackedMessage());
//				System.out.println("|sends|" + message.getPackedMessage());
				Thread.sleep(MESSAGE_CUSHION);
				if(message.hasReply()){
					messageOnCourse	= message;				
					messageOnCourseTimestamp	= System.currentTimeMillis();
				}				
			} catch (SerialPortException e) {
				e.printStackTrace();
				return false;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}else{
			pendingQueue.add(message);
		}
		
		return true;
	}

	public synchronized void eventHandler(MessageEvent messageEvent) {
		Message	source	= (Message)messageEvent.getSource();
		Message reply	= messageEvent.getReply();
		
		source.removeMessageListener(this);
		
		if(source == messageOnCourse){
			messageOnCourse			= null;
			messageOnCourseRetries	= 0;
			
		}
		
		if(source instanceof CommunicationStartMessage && isOpening){
			isOpening	= false;
			isOpen		= true;
		}else if(source instanceof CommunicationStopMessage && isClosing){
			internalClose();
		}
	}

	public void run() {
		while(true){
	    	if(serial == null || !serial.isOpened())
	    		return;
	    	if(messageOnCourse == null && pendingQueue.size() > 0)
	    		sendMessage(pendingQueue.remove(0));
	    	processSerial();
	    	processTimeout();
			try {
				Thread.sleep(SLEEP_AMOUNT);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    	
		}
	}
	
	protected synchronized void processTimeout(){
		if(messageOnCourse == null)
			return;
		long currentTimestamp	= System.currentTimeMillis();
		if(currentTimestamp - messageOnCourseTimestamp > MESSAGE_TIMEOUT){
			if(messageOnCourseRetries > MESSAGE_RETRIES){
				messageOnCourse			= null;
				messageOnCourseRetries	= 0;
				if(messageOnCourse instanceof CommunicationStopMessage){
					internalClose();					
				}
				return;
			}
			messageOnCourseRetries++;
			sendMessage(messageOnCourse);
		}
	}
	
	protected void processSerial(){
    	byte[] inputBytes;
    	int availableBytes 	= 0;
    	try {
			availableBytes	= serial.getInputBufferBytesCount();
			if(availableBytes > 0){
				inputBytes 	= serial.readBytes();
				for(int i = 0; i < availableBytes; i++){
					serialQueue[serialQueueTail]	= inputBytes[i];
					serialQueueTail	= (serialQueueTail + 1) % SERIAL_BUFFER_SIZE;
				}
				int queueLength		= serialQueueTail - serialQueueHead;
				if(serialQueueHead > serialQueueTail){
					queueLength		= SERIAL_BUFFER_SIZE - queueLength;
				}
				if(queueLength >= Message.MESSAGE_HEADER_LENGTH){
					byte[] rawMessageBytes	= new byte[queueLength];
					for(int i = 0; i < queueLength; i++)
						rawMessageBytes[i]	= serialQueue[(serialQueueHead + i) % SERIAL_BUFFER_SIZE];
					String rawMessage		= new String(rawMessageBytes);
					int headIndex			= rawMessage.indexOf(Message.MESSAGE_HEAD_VALUE);
					if(headIndex < 0){
						serialQueueHead		= serialQueueTail;
						return;
					}else{
						serialQueueHead		= (serialQueueHead + headIndex) % SERIAL_BUFFER_SIZE;
						rawMessage			= rawMessage.substring(headIndex);
					}
//					System.out.println("|queued|<<" + new String(rawMessageBytes));					
					if(Message.isMessageComplete(rawMessage)){
						int rawMessageLength	= Message.getLengthFromRawMessage(rawMessage);
						String firstRawMessage	= rawMessage.substring(0, rawMessageLength);
						Message incomingMessage	= Message.deserializeMessage(firstRawMessage);
						serialQueueHead			= (serialQueueHead + rawMessageLength) % SERIAL_BUFFER_SIZE;
//						System.out.println("|pops|<<" + firstRawMessage + (incomingMessage == null? "no incoming message" : " |replies to|<<" + incomingMessage.getPackedMessage()));
						if(incomingMessage != null){
							//dispatch incomingMessage
							MessageEvent outgoingEvent;
							if(messageOnCourse != null)
								outgoingEvent		= new MessageEvent(messageOnCourse, incomingMessage);
							else
								outgoingEvent		= new MessageEvent(incomingMessage, null);
							eventHandler(outgoingEvent);
							fireEvent(outgoingEvent);
						}else{
//							System.out.println(firstRawMessage + " could not be deserialized");
						}
					}
				}
			}
		} catch (SerialPortException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
}
