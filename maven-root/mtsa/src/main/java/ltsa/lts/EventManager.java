package ltsa.lts;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class EventManager implements Runnable{
    Hashtable clients = new Hashtable();
    Vector queue = new Vector(); // queued messages
    Thread athread;
    boolean stopped=false;

    public EventManager(){
        athread = new Thread(this);
        athread.start();
    }

    public synchronized void addClient(EventClient c) {
        clients.put(c,c);
    }

    public synchronized void removeClient(EventClient c) {
        clients.remove(c);
    }

    public synchronized void post(LTSEvent le) {
        queue.addElement(le);
        notifyAll();
    }

    public void stop() {
        stopped=true;
    }

    private synchronized void dopost() {
        while (queue.size()==0) {
            try{wait();} catch (InterruptedException e) {}
        }
        LTSEvent le = (LTSEvent)queue.firstElement();
        Enumeration e = clients.keys();
        while(e.hasMoreElements()) {
            EventClient c = (EventClient)e.nextElement();
            c.ltsAction(le);
        }
        queue.removeElement(le);
    }

    public void run() {
        while(!stopped) {
            dopost();
        }
    }
}