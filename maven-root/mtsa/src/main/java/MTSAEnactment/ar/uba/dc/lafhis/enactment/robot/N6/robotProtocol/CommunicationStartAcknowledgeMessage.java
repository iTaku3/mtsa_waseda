package MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.N6.robotProtocol;

public class CommunicationStartAcknowledgeMessage extends Message {	
	public static int MESSAGE_ID				= 2;
	
	public CommunicationStartAcknowledgeMessage(int to, int from){
		super(MESSAGE_ID, 1, to, from);
	}
	
	public CommunicationStartAcknowledgeMessage(String serializedMessage){
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
