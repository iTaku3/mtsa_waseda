package MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.N6.robotProtocol;

public class CommandAcceptedMessage extends Message {

	public static int MESSAGE_ID				= 5;

	public static int ACCEPTED_MID_FIELD_LENGTH	= 4;	
	
	private int acceptedMid;
	
	public int getAcceptedMid(){
		return acceptedMid;
	}
	
	public CommandAcceptedMessage(int to, int from, int acceptedMid){
		super(MESSAGE_ID, 1, to, from);
		this.acceptedMid	= acceptedMid;
	}
	
	public CommandAcceptedMessage(String serializedMessage){
		super(serializedMessage);
	}
	
	@Override
	public int getMessageLength() {
		return super.getMessageLength() + ACCEPTED_MID_FIELD_LENGTH;
	}
	
	
	@Override
	protected void readData(String inputString){
		int startIndex		= 0;
		int endIndex		= ACCEPTED_MID_FIELD_LENGTH;
		acceptedMid			= Integer.parseInt(inputString.substring(startIndex, endIndex),10);
	}

	@Override
	protected String writeData(){
		return String.format("%04d", acceptedMid);
	}

	@Override
	protected boolean hasReply() {
		return false;
	}

}
