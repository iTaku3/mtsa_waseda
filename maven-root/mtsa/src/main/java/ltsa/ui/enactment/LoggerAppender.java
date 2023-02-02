/**
 * 
 */
package ltsa.ui.enactment;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;

/**
 * @author Julio
 *
 */
public class LoggerAppender extends WriterAppender {

	static private JTextArea uiOutput;
	
	
	

	/* (non-Javadoc)
	 * @see org.apache.log4j.AppenderSkeleton#append(org.apache.log4j.spi.LoggingEvent)
	 */
	@Override
	public void append(LoggingEvent event) {
		if (event.getLevel().equals(Level.INFO))
		{
			PatternLayout patternLayout = new PatternLayout("%m%n");				
			final String message = patternLayout.format(event);
			
			// Append formatted message to textarea using the Swing Thread.
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {					
					if(uiOutput!=null) uiOutput.append(message);
				}
			});
		}


	}
	

	
	/**
	 * @param uiOutput the uiOutput to set
	 */
	static public void setUiOutput(JTextArea uiOutput) {
		LoggerAppender.uiOutput = uiOutput;
	}

}
