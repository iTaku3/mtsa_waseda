package MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.N6.robotProtocol;

public class RobotStatusMessage extends Message {	
	public static int MESSAGE_ID				= 211;

	public static int LEFT_SPEED_FIELD_LENGTH		= 4;
	public static int RIGHT_SPEED_FIELD_LENGTH	= 4;
	public static int LEFT_IR_FIELD_LENGTH		= 4;
	public static int RIGHT_IR_FIELD_LENGTH		= 4;
	public static int BATTERY_FIELD_LENGTH		= 4;
	
	private int leftSpeedValue;
	private int rightSpeedValue;
	private int leftIRValue;
	private int rightIRValue;
	private int batteryValue;
	
	public int getLeftSpeedValue(){
		return leftSpeedValue;
	}
	
	public int getRightSpeedValue(){
		return rightSpeedValue;
	}
	
	public int getLeftIRValue(){
		return leftIRValue;
	}
	
	public int getRightIRValue(){
		return rightIRValue;
	}
	
	public int getBatteryValue(){
		return batteryValue;
	}	
	
	public RobotStatusMessage(int to, int from, int leftSppedValue, int rightSpeedValue
			, int leftIRValue, int rightIRValue, int batteryValue){
		super(MESSAGE_ID, 1, to, from);
		this.leftSpeedValue		= leftSpeedValue;
		this.rightSpeedValue	= rightSpeedValue;
		this.leftIRValue		= leftIRValue;
		this.rightIRValue		= rightIRValue;		
		this.batteryValue		= batteryValue;
	}
	
	public RobotStatusMessage(String serializedMessage){
		super(serializedMessage);
	}
	
	@Override
	public int getMessageLength() {
		return super.getMessageLength() + LEFT_SPEED_FIELD_LENGTH + RIGHT_SPEED_FIELD_LENGTH
				 + LEFT_IR_FIELD_LENGTH + RIGHT_IR_FIELD_LENGTH + BATTERY_FIELD_LENGTH;
	}
	
	
	@Override
	protected void readData(String inputString){
		int startIndex		= 0;
		int endIndex		= LEFT_SPEED_FIELD_LENGTH;
		leftSpeedValue		= Integer.parseInt(inputString.substring(startIndex, endIndex),10);
		startIndex			= endIndex;
		endIndex			= startIndex + RIGHT_SPEED_FIELD_LENGTH;
		rightSpeedValue		= Integer.parseInt(inputString.substring(startIndex, endIndex),10);
		startIndex			= endIndex;
		endIndex			= startIndex + LEFT_IR_FIELD_LENGTH;
		leftIRValue			= Integer.parseInt(inputString.substring(startIndex, endIndex),10);
		startIndex			= endIndex;
		endIndex			= startIndex + RIGHT_IR_FIELD_LENGTH;
		rightIRValue		= Integer.parseInt(inputString.substring(startIndex, endIndex),10);
		startIndex			= endIndex;
		endIndex			= startIndex + BATTERY_FIELD_LENGTH;
		batteryValue		= Integer.parseInt(inputString.substring(startIndex, endIndex),10);		
	}

	@Override
	protected String writeData(){
		return String.format("%04d%04d%04d%04d%04d", leftSpeedValue, rightSpeedValue
				, leftIRValue, rightIRValue, batteryValue);
	}

	@Override
	protected boolean hasReply() {
		return false;
	}	
}
