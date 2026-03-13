package ltsa.ui;

/* a specialised Applet button to launch LTSA
*/

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.UIManager;

public class AppletButton extends Applet implements Runnable {
    Button button;
    Thread windowThread;
    boolean pleaseCreate = false;
    HPWindow window = null;

    public void init() {

        setLayout(new BorderLayout());
        button = new Button("Launch LTSA");
        button.setFont(new Font("Helvetica", Font.BOLD, 18));
        button.addActionListener(new ButtonAction());
        add("Center",button);
    }

    public void start() {
        if (windowThread == null) {
            windowThread = new Thread(this);
            windowThread.start();
        }
    }

    public void stop() {
        windowThread=null;
        if (window!=null) window.dispose();
    }
    
    public void ended() {
    	if (window!=null) window=null;
    }

    public synchronized void run() {
        while (windowThread != null) {
          while (pleaseCreate == false) {
             try { wait();} catch (InterruptedException e) {}
          }
          pleaseCreate=false;
         try {
           String lf = UIManager.getSystemLookAndFeelClassName();
           UIManager.setLookAndFeel(lf);
         } catch(Exception e) {}
         if (window==null) {
         	  showStatus("Please wait while the window comes up...");
			    window = new HPWindow(this);
		 	    window.setTitle("MTS Analyser");
			    window.pack();
	         HPWindow.centre(window);
	         window.setVisible(true);
	         showStatus("");
         }
        }
    }
    
    synchronized void triggerWindow() {
    	   pleaseCreate = true;
    	   notify();
    }
    
   class ButtonAction implements ActionListener {
    	public void actionPerformed(ActionEvent e) { triggerWindow();}
    }

}