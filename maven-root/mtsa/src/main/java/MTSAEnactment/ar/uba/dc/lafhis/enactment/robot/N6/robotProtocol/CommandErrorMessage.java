package MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.N6.robotProtocol;

public class CommandErrorMessage extends Message {	
	public static int MESSAGE_ID				= 4;

	public static int REJECTED_MID_FIELD_LENGTH	= 4;
	public static int ERROR_CODE_FIELD_LENGTH	= 4;	
	
	private int rejectedMid;
	
	private int errorCode;
	
	public int getRejectedMid(){
		return rejectedMid;
	}
	
	public int getErrorCode(){
		return errorCode;
	}
	
	public CommandErrorMessage(int to, int from, int rejectedMid, int errorCode){
		super(MESSAGE_ID, 1, to, from);
		this.rejectedMid	= rejectedMid;
		this.errorCode		= errorCode;
	}
	
	public CommandErrorMessage(String serializedMessage){
		super(serializedMessage);
	}
	
	@Override
	public int getMessageLength() {
		return super.getMessageLength() + REJECTED_MID_FIELD_LENGTH + ERROR_CODE_FIELD_LENGTH;
	}
	
	
	@Override
	protected void readData(String inputString){
		int startIndex		= 0;
		int endIndex		= REJECTED_MID_FIELD_LENGTH;
		rejectedMid			= Integer.parseInt(inputString.substring(startIndex, endIndex),10);
		startIndex			= endIndex;
		endIndex			= startIndex + ERROR_CODE_FIELD_LENGTH;
		errorCode			= Integer.parseInt(inputString.substring(startIndex, endIndex),10);
	}

	@Override
	protected String writeData(){
		return String.format("%04d%02d", rejectedMid, errorCode);
	}

	@Override
	protected boolean hasReply() {
		return false;
	}
	
	
}
