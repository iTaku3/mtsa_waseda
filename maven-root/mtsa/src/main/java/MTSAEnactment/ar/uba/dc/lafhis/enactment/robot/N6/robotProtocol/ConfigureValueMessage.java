package MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.N6.robotProtocol;

public class ConfigureValueMessage extends Message {	
	public static int MESSAGE_ID				= 212;

	public static int KEY_FIELD_LENGTH		= 4;
	public static int VALUE_FIELD_LENGTH		= 4;
	
	/* CONFIGURATION KEYS */
	/* N6 CONFIGURATION */
	public static int N6_LE_WHITE_R     		= 1;
	public static  int N6_LE_WHITE_L     		= 2;
	public static  int N6_GE_BLACK_R     		= 3;
	public static  int N6_GE_BLACK_L     		= 4;
	public static  int N6_LE_GREY_R      		= 5;
	public static  int N6_LE_GREY_L      		= 6;
	public static  int N6_GE_GREY_R      		= 7;
	public static  int N6_GE_GREY_L      		= 8;
	/* FOLLOW LINE CONFIGURATION */
	public static  int FOLLOWLINECOMMAND_FOLLOW_SPEED	= 101;
	public static  int FOLLOWLINECOMMAND_FIXING_SPEED	= 102;
	public static  int FOLLOWLINECOMMAND_START_DELAY		= 103;
	/* TURN AROUND CONFIGURATION */
	public static  int TURNAROUNDCOMMAND_BACK_SPEED		= 201;
	public static  int TURNAROUNDCOMMAND_LEFT_SPEED		= 202;
	public static  int TURNAROUNDCOMMAND_RIGHT_SPEED		= 203;
	public static  int TURNAROUNDCOMMAND_TURN_DELAY		= 204;
	public static  int TURNAROUNDCOMMAND_BACK_DELAY		= 205;
	public static  int TURNAROUNDCOMMAND_SEARCH_DELAY	= 206;
	/* TURN LEFT CONFIGURATION */
	public static  int TURNLEFTCOMMAND_LEFT_SPEED	= 301;
	public static  int TURNLEFTCOMMAND_RIGHT_SPEED	= 302;
	public static  int TURNLEFTCOMMAND_BACK_SPEED	= 303;
	public static  int TURNLEFTCOMMAND_FORTH_SPEED	= 304;
	public static  int TURNLEFTCOMMAND_BACK_DELAY	= 305;
	public static  int TURNLEFTCOMMAND_FORTH_DELAY	= 306;
	public static  int TURNLEFTCOMMAND_BLIND_DELAY	= 307;
	public static  int TURNLEFTCOMMAND_SEARCH_DELAY	= 308;
	/* TURN RIGHT CONFIGURATION */
	public static  int TURNRIGHTCOMMAND_LEFT_SPEED	= 401;
	public static  int TURNRIGHTCOMMAND_RIGHT_SPEED	= 402;
	public static  int TURNRIGHTCOMMAND_BACK_SPEED	= 403;
	public static  int TURNRIGHTCOMMAND_FORTH_SPEED	= 404;
	public static  int TURNRIGHTCOMMAND_BACK_DELAY	= 405;
	public static  int TURNRIGHTCOMMAND_FORTH_DELAY	= 406;
	public static  int TURNRIGHTCOMMAND_BLIND_DELAY	= 407;
	public static  int TURNRIGHTCOMMAND_SEARCH_DELAY	= 408;	
	
	private int key;
	private int value;
	
	public int getKey(){
		return key;
	}
	
	public int getValue(){
		return value;
	}
	
	
	public ConfigureValueMessage(int to, int from, int key, int value){
		super(MESSAGE_ID, 1, to, from);
		this.key		= key;
		this.value		= value;
	}
	
	public ConfigureValueMessage(String serializedMessage){
		super(serializedMessage);
	}
	
	@Override
	public int getMessageLength() {
		return super.getMessageLength() + KEY_FIELD_LENGTH + VALUE_FIELD_LENGTH;
	}
	
	
	@Override
	protected void readData(String inputString){
		int startIndex		= 0;
		int endIndex		= KEY_FIELD_LENGTH;
		key					= Integer.parseInt(inputString.substring(startIndex, endIndex),10);
		startIndex			= endIndex;
		endIndex			= startIndex + VALUE_FIELD_LENGTH;
		value				= Integer.parseInt(inputString.substring(startIndex, endIndex),10);
	}

	@Override
	protected String writeData(){
		return String.format("%04d%04d", key, value);
	}

	@Override
	protected boolean hasReply() {
		return true;
	}	
}

