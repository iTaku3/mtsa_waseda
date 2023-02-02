package MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.N6.robotProtocol;

public class KeepAliveMessage extends Message {	
	public static int MESSAGE_ID				= 911;

	public static int BATTERY_FIELD_LENGTH		= 4;	
	
	private int batteryLeft;
	
	public int getBatteryLeft(){
		return batteryLeft;
	}
	
	public KeepAliveMessage(int to, int from, int batteryLeft){
		super(MESSAGE_ID, 1, to, from);
		this.batteryLeft		= batteryLeft;
	}
	
	public KeepAliveMessage(String serializedMessage){
		super(serializedMessage);
	}
	
	@Override
	public int getMessageLength() {
		return super.getMessageLength() + BATTERY_FIELD_LENGTH;
	}
	
	
	@Override
	protected void readData(String inputString){
		int startIndex		= 0;
		int endIndex		= BATTERY_FIELD_LENGTH;
		batteryLeft			= Integer.parseInt(inputString.substring(startIndex, endIndex),10);
	}

	@Override
	protected String writeData(){
		return String.format("%04d", batteryLeft);
	}

	@Override
	protected boolean hasReply() {
		return false;
	}	
}
