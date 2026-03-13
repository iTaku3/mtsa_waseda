package MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.N6.robotProtocol;

import java.util.EventObject;

public final class MessageEvent  extends EventObject{	
	private static final long serialVersionUID = -7148804458058335609L;
	
	private Message reply;
	
	public Message getReply(){
		return reply;
	}
	
	public MessageEvent(Object source, Message reply)
	{
		super(source);
		this.reply	= reply;
	}
}