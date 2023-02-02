package MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.N6.robotProtocol;

public class ReadUSSensorReplyMessage extends Message {	
	public static int MESSAGE_ID				= 211;

	public static int DISTANCE_FIELD_LENGTH	= 4;
	
	private int distance;
	
	public int getDistance(){
		return distance;
	}
	
	public ReadUSSensorReplyMessage(int to, int from, int distance){
		super(MESSAGE_ID, 1, to, from);
		this.distance	= distance;
	}
	
	public ReadUSSensorReplyMessage(String serializedMessage){
		super(serializedMessage);
	}
	
	@Override
	public int getMessageLength() {
		return super.getMessageLength() + DISTANCE_FIELD_LENGTH;
	}
	
	
	@Override
	protected void readData(String inputString){
		int startIndex		= 0;
		int endIndex		= DISTANCE_FIELD_LENGTH;
		distance			= Integer.parseInt(inputString.substring(startIndex, endIndex),10);
	}

	@Override
	protected String writeData(){
		return String.format("%04d", distance);
	}

	@Override
	protected boolean hasReply() {
		return false;
	}	
}
