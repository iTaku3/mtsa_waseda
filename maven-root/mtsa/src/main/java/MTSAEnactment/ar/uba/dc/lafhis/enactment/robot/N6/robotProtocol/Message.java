package MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.N6.robotProtocol;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.N6.utils.ReflectionUtils;



public abstract class Message extends MessageDispatcher{

	public static String MESSAGE_HEAD_VALUE = "LAFHIS";

	public static int MESSAGE_LENGTH_ERROR		= -2;
	public static int MESSAGE_END_CHAR_NOT_FOUND= -3;
	public static int MESSAGE_ID_UNKNOWN		= -4;
	public static int MESSAGE_REJECTED			= -5;
	public static int MASTER_UNDEFINED			= -6;	

	public static int MESSAGE_HEADER_LENGTH		= 26;

	public static int HEAD_FIELD_LENGTH			= 6;
	public static int LENGTH_FIELD_LENGTH		= 4;
	public static int MID_FIELD_LENGTH			= 4;
	public static int REVISION_FIELD_LENGTH		= 3;
	public static int FLAGS_FIELD_LENGTH		= 1;
	public static int TO_FIELD_LENGTH			= 4;
	public static int FROM_FIELD_LENGTH			= 4;

	private int messageID;
	private int revision;
	private int flags;
	private int to;
	private int from;	

	public static boolean isMessageComplete(String rawMessage){
		if(rawMessage.length() < MESSAGE_HEADER_LENGTH)
			return false;
		int expectedLength	= getLengthFromRawMessage(rawMessage);
		if(expectedLength > rawMessage.length())
			return false;
		return true;
	}
	
	public static int getLengthFromRawMessage(String rawMessage){
		if(rawMessage.length() < MESSAGE_HEADER_LENGTH)
			return -1;
		return Integer.parseInt(rawMessage.substring(HEAD_FIELD_LENGTH, HEAD_FIELD_LENGTH + LENGTH_FIELD_LENGTH));
	}
	
	public static Message deserializeMessage(String rawMessage){
		int startIndex	= HEAD_FIELD_LENGTH + LENGTH_FIELD_LENGTH;
		int endIndex	= startIndex + MID_FIELD_LENGTH;
		int mid	= Integer.parseInt(rawMessage.substring(startIndex, endIndex));
		List<Class>	subClasses = ReflectionUtils.findSubClasses(Message.class.getPackage().getName(), Message.class.getName()
				, 1, 4);
		subClasses.addAll(ReflectionUtils.findSubClasses(Message.class.getPackage().getName(), Message.class.getName()
				, 1, 4, 911));
		subClasses.addAll(ReflectionUtils.findSubClasses(Message.class.getPackage().getName(), Message.class.getName()
				, 1, 4, 911, 911));
		subClasses.addAll(ReflectionUtils.findSubClasses(Message.class.getPackage().getName(), Message.class.getName()
				, 1, 4, 911, 911, 911));
		subClasses.addAll(ReflectionUtils.findSubClasses(Message.class.getPackage().getName(), Message.class.getName()
				, 1, 4, 911, 911, 911, 911));		
		for(int i = 0; i < subClasses.size(); i++){
			Class c	= (Class)subClasses.get(i);
			try {
				int classMID	= c.getField("MESSAGE_ID").getInt(null);
				if(mid == classMID){
					Constructor<Message> ctor	= c.getDeclaredConstructor(String.class);
					return ctor.newInstance(rawMessage);
				}
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	} 
	
	protected Message(){
		this(0,0,0,0);
	}
	
	public Message(int messageId, int revision, int to, int from){
		this(messageId, revision, to, from, 0);
	}
	
	public Message(int messageId, int revision, int to, int from, int flags){
		super();
		this.messageID	= messageId;
		this.revision	= revision;
		this.to			= to;
		this.from		= from;
		this.flags		= flags;
	}
	
	public Message(String serializedMessage){
		initialize(serializedMessage);
	}
	
	public final String getPackedMessage(){
		String packedMessage = "";
		packedMessage	+= writeHeader();
		packedMessage	+= writeData();
		return packedMessage;
	}
	
	public int getMessageLength(){
		return MESSAGE_HEADER_LENGTH;
	}

	public int getMessageID(){
		return messageID;
	}

	public int getRevision(){
		return revision;
	}

	public int getFlags(){
		return flags;
	}

	public int getToAddress(){
		return to;
	}

	public int getFromAddress(){
		return from;
	}	

	private void initialize(String inputString){
		int startIndex		= MESSAGE_HEAD_VALUE.length() + LENGTH_FIELD_LENGTH;
		int endIndex		= startIndex + MID_FIELD_LENGTH;
		messageID			= Integer.parseInt(inputString.substring(startIndex, endIndex),10);
		startIndex			= endIndex;
		endIndex			= startIndex + REVISION_FIELD_LENGTH;
		revision			= Integer.parseInt(inputString.substring(startIndex, endIndex),10);
		startIndex			= endIndex;
		endIndex			= startIndex + FLAGS_FIELD_LENGTH;
		flags				= Integer.parseInt(inputString.substring(startIndex, endIndex),10);
		startIndex			= endIndex;
		endIndex			= startIndex + TO_FIELD_LENGTH;
		to					= Integer.parseInt(inputString.substring(startIndex, endIndex),10);
		startIndex			= endIndex;
		endIndex			= startIndex + FROM_FIELD_LENGTH;
		from				= Integer.parseInt(inputString.substring(startIndex, endIndex),10);
		readData(inputString.substring(endIndex));
	}

	private String writeHeader(){
		return String.format("%s%04d%04d%03d%01d%04d%04d", MESSAGE_HEAD_VALUE, getMessageLength(), messageID, revision, flags, to, from);
	}

	protected abstract void readData(String inputString);
	
	protected abstract String writeData();
	
	protected abstract boolean hasReply();
	
	public void processReply(Message reply){
		fireEvent(new MessageEvent(this, reply));		
	}
}
