package MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.N6.robotProtocol;

public class ReadIRSensorsReplyMessage extends Message {	
	public static int MESSAGE_ID				= 209;

	public static int LEFT_FIELD_LENGTH		= 4;
	public static int RIGHT_FIELD_LENGTH		= 4;
	public static int TOP_FIELD_LENGTH		= 4;
	
	private int leftValue;
	private int rightValue;
	private int topValue;
	
	public int getLeftValue(){
		return leftValue;
	}
	
	public int getRightValue(){
		return rightValue;
	}
	
	public int getTopValue(){
		return topValue;
	}	
	
	public ReadIRSensorsReplyMessage(int to, int from, int leftValue, int rightValue, int topValue){
		super(MESSAGE_ID, 1, to, from);
		this.leftValue		= leftValue;
		this.rightValue		= rightValue;
		this.topValue		= topValue;
	}
	
	public ReadIRSensorsReplyMessage(String serializedMessage){
		super(serializedMessage);
	}
	
	@Override
	public int getMessageLength() {
		return super.getMessageLength() + LEFT_FIELD_LENGTH + RIGHT_FIELD_LENGTH + TOP_FIELD_LENGTH;
	}
	
	
	@Override
	protected void readData(String inputString){
		int startIndex		= 0;
		int endIndex		= LEFT_FIELD_LENGTH;
		leftValue			= Integer.parseInt(inputString.substring(startIndex, endIndex),10);
		startIndex			= endIndex;
		endIndex			= startIndex + RIGHT_FIELD_LENGTH;
		rightValue			= Integer.parseInt(inputString.substring(startIndex, endIndex),10);
		startIndex			= endIndex;
		endIndex			= startIndex + TOP_FIELD_LENGTH;
		topValue			= Integer.parseInt(inputString.substring(startIndex, endIndex),10);		
	}

	@Override
	protected String writeData(){
		return String.format("%04d%04d%04d", leftValue, rightValue, topValue);
	}

	@Override
	protected boolean hasReply() {
		return false;
	}	
}
