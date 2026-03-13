package MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.N6.robotProtocol;

public class SetMotorSpeedMessage extends Message {	
	public static int MESSAGE_ID				= 201;

	public static int LEFT_FIELD_LENGTH			= 4;
	public static int RIGHT_FIELD_LENGTH		= 4;	
	
	private int leftSpeed;
	
	private int rightSpeed;
	
	public int getLeftSpeed(){
		return leftSpeed;
	}
	
	public int getRightSpeed(){
		return rightSpeed;
	}
	
	public SetMotorSpeedMessage(int to, int from, int leftSpeed, int rightSpeed){
		super(MESSAGE_ID, 1, to, from);
		this.leftSpeed		= leftSpeed;
		this.rightSpeed		= rightSpeed;
	}
	
	public SetMotorSpeedMessage(String serializedMessage){
		super(serializedMessage);
	}
	
	@Override
	public int getMessageLength() {
		return super.getMessageLength() + LEFT_FIELD_LENGTH + RIGHT_FIELD_LENGTH;
	}
	
	
	@Override
	protected void readData(String inputString){
		int startIndex		= 0;
		int endIndex		= LEFT_FIELD_LENGTH;
		leftSpeed			= Integer.parseInt(inputString.substring(startIndex, endIndex),10);
		startIndex			= endIndex;
		endIndex			= startIndex + RIGHT_FIELD_LENGTH;
		rightSpeed			= Integer.parseInt(inputString.substring(startIndex, endIndex),10);
	}

	@Override
	protected String writeData(){
		return String.format("%04d%04d", leftSpeed, rightSpeed);
	}

	@Override
	protected boolean hasReply() {
		return true;
	}	
}
