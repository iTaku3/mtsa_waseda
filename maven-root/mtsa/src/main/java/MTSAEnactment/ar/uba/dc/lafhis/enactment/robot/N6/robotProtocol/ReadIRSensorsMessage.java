package MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.N6.robotProtocol;

public class ReadIRSensorsMessage extends Message {	
	public static int MESSAGE_ID				= 208;
	
	public ReadIRSensorsMessage(int to, int from){
		super(MESSAGE_ID, 1, to, from);
	}
	
	public ReadIRSensorsMessage(String serializedMessage){
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
		return true;
	}	
}
