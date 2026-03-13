package RobotProtocolClient.robotProtocolClient;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Date;

public class ActionRun<Action> {
	protected Action action;
	protected Date startingTime;
	protected BufferedImage actionImage;
	
	public Action getAction(){
		return action;
	}
	
	public Date getStartingTime(){
		return startingTime;
	}
	
	public BufferedImage getActionImage(){
		return actionImage;
	}
	
	public ActionRun(Action action){
		this.action			= action;
		this.startingTime	= new Date();
	}
	
	public ActionRun(Action action, BufferedImage actionImage){
		this(action);
		this.actionImage	= actionImage;
	}	
}
