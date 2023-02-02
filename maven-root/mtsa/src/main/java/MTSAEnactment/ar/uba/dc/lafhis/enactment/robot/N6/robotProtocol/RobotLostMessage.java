package MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.N6.robotProtocol;

public class RobotLostMessage extends Message {	
	public static int MESSAGE_ID				= 204;
	
	public RobotLostMessage(int to, int from){
		super(MESSAGE_ID, 1, to, from);
	}
	
	public RobotLostMessage(String serializedMessage){
		super(serializedMessage);
	}
	
	@Override
	public int getMessageLength() {
		return super.getMessageLength();
	}
	
	
	@Override
	protected void readData(String inputString){
	}

	@Override
	protected String writeData(){
		return "";
	}

	@Override
	protected boolean hasReply() {
		return false;
	}	
}
