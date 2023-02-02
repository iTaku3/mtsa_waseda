package MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.N6.robotProtocol;

import java.util.ArrayList;
import java.util.List;

public class MessageDispatcher {

    private List<IMessageListener> listeners;
    
    public MessageDispatcher(){
		listeners		= new ArrayList<IMessageListener>();    	
    }
    
    public synchronized void addMessageListener(IMessageListener listener){
		listeners.add(listener);
	}
	
	public synchronized void removeMessageListener(IMessageListener listener){
		listeners.remove(listener);
	}

    protected synchronized void fireEvent(MessageEvent messageEvent) 
    {
    	for (IMessageListener listener : listeners)
    	{
    		listener.eventHandler(messageEvent);
    	}
    }		
}
