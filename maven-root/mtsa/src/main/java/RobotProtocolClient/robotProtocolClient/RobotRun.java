package RobotProtocolClient.robotProtocolClient;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import org.jfree.data.time.Second;

import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.VideoInputFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_highgui;
import com.googlecode.javacv.cpp.opencv_highgui.CvCapture;

public class RobotRun<Action> {
	private int minTraceToFailure;
	
	protected Set<Action> failureActions;
	
	public void addFailureAction(Action action) throws Exception{
		if(trace.size() > 0){
			throw new Exception("action should not be added once the trace is started");
		}
		if(failureActions.contains(action))
			return;
		failureActions.add(action);
	}

	protected Set<Action> controllableActions;

	public void addControllableAction(Action action) throws Exception{
		if(trace.size() > 0){
			throw new Exception("action should not be added once the trace is started");
		}
		if(controllableActions.contains(action))
			return;
		controllableActions.add(action);
	}	
	
	protected List<ActionRun<Action>> trace;
	public List<ActionRun<Action>> getTrace(){
		return trace;
	}

	public List<ActionRun<Action>> getControllableTrace(){
		List<ActionRun<Action>> controllableTrace	= new ArrayList<ActionRun<Action>>();
		for(ActionRun<Action> actionRun : trace){
			if(controllableActions.contains(actionRun.getAction()))
				controllableTrace.add(actionRun);
		}		
		return controllableTrace;
	}
	
	
	private BufferedImage getCurrentImage(){
		IplImage image			= null;
		BufferedImage bufImg	= null;
        try {
    		OpenCVFrameGrabber grabber	= OpenCVFrameGrabber.createDefault(0);        	
			grabber.start();
	        image 						= grabber.grab();
	        bufImg						= image.getBufferedImage();
	        grabber.stop();
		} catch (com.googlecode.javacv.FrameGrabber.Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
      
        return bufImg;
	}
	
	public void addAction(Action action){
		BufferedImage bufferedImage = getCurrentImage();
		if(bufferedImage != null)
			trace.add(new ActionRun<Action>(action, bufferedImage));
		else
			trace.add(new ActionRun<Action>(action));
		if(failureActions.contains(action)){
			minTraceToFailure	= trace.size();
		}
	}
	
	public int getMinTraceToFailure(){
		
		return minTraceToFailure;
	}
	
	public String getFormattedString(){
		String outputString = "ACTION\tTIME\tSTATUS\n";
		outputString 		+= "================================\n";
		outputString		+= getFormattedActions();
		outputString 		+= "================================\n";
		outputString 		+= "FIRST FAILURE AT: " + minTraceToFailure + "\n";
		outputString 		+= "LENGTH: " + trace.size() + "\n";
		outputString 		+= "CONTROLLABLE LENGTH: " + getControllableTrace().size() + "\n";
		if(trace.size() > 1){
			long secs		= trace.get(trace.size()-1).getStartingTime().getTime() - trace.get(0).getStartingTime().getTime();
			outputString 	+= "RUNNING TIME: " + (secs/1000) + "s\n";
		}
		return outputString;
	}
	
	public String getFormattedActions(){
		String outputString = "";
		for(ActionRun<Action> actionRun : trace){
			outputString		+= getFormattedAction(actionRun);
		}
		return outputString;		
	}
	
	public boolean saveFilesToLocation(String location, String prefix){
	    try {		
			File imageFile;
	
			for(ActionRun<Action> actionRun : trace){
				if(actionRun.getActionImage() != null){
					imageFile = new File(location + "/" + prefix + "_" + actionRun.getStartingTime().toString() + "_" + actionRun.getAction().toString() + ".png");
					ImageIO.write(actionRun.getActionImage(), "png", imageFile);
				}
			}
			File runFile	= new File(location + "/" + prefix + ".run");		
			PrintWriter out	= new PrintWriter(runFile);
			out.print(getFormattedString());
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return true;
	}
	
	public String getFormattedAction(ActionRun<Action> actionRun){
		String outputString = "";
		outputString		+= actionRun.getAction() +  "\t" + actionRun.getStartingTime() + "\t";
		if(failureActions.contains(actionRun.getAction())){
			outputString	+= "<FAILURE>\t";
		}else{
			outputString	+= "<OK>\t";				
		}
		if(controllableActions.contains(actionRun.getAction())){
			outputString	+= "<CONTROLLABLE>\n";
		}else{
			outputString	+= "<NONCONTROLLABLE>\n";				
		}
		return outputString;		
	}
	
	public RobotRun(){
		minTraceToFailure	= -1;
		trace				= new ArrayList<ActionRun<Action>>();
		failureActions		= new HashSet<Action>();
		controllableActions	= new HashSet<Action>();
	}
}
