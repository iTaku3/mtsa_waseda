/**
 * 
 */
package MTSAEnactment.ar.uba.dc.lafhis.enactment;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.gui.UIControllerGui;

/**
 * @author Julio
 *
 */
public class UIController<State, Action> extends BaseController<State, Action> {
	
	private UIControllerGui uiControllerGui = null;
	private Logger logger = LogManager.getLogger(TakeFirstController.class.getName());
	private List<Action> optControllableActions;
	private List<Action> optUnControllableActions;
	
	
	public UIController(String name, LTS<State, Action> lts,
			Set<Action> controllableActions) {
		super(name, lts, controllableActions);
		
	}

	public UIController(String name) {
		super(name);		
		
		
	}
	
	@Override
	public void setUp() {
		super.setUp();
		//this.addTransitionEventListener(this);
		this.setReactItSelf(true);
	}
	
	@Override
	public void takeNextAction() throws Exception {
		
		
		Iterator<Pair<Action,State>> stateIterator = lts.getTransitions(currentState).iterator();
				
		if(!stateIterator.hasNext()){
			String exceptionString = "";
			logger.error(String.format("[Scheduler::primitiveHandleTransitionEvent] there is no reachable state from currentState %s"
					, currentState.toString(), exceptionString));
			throw new Exception(exceptionString);
		}		
		
		
		optControllableActions = new ArrayList<Action>();
		optUnControllableActions = new ArrayList<Action>();
		
		while(stateIterator.hasNext()){		
			Pair<Action, State> currentPair	= stateIterator.next();
			
				if(controllableActions.contains(currentPair.getFirst())){
				optControllableActions.add(currentPair.getFirst());
			} else
			{
				optUnControllableActions.add(currentPair.getFirst());
			}			
		}
		
		//Update interface
		updateInterface();
		
	}

	private void updateInterface()
	{
		if (this.uiControllerGui == null) 
		{
			this.uiControllerGui = new UIControllerGui();
			this.uiControllerGui.setTitle(this.getName());
		}
		
		this.uiControllerGui.removeActions();
		
		for (Action action : this.optControllableActions)
			this.uiControllerGui.addControllableAction(action.toString(), action.toString(), new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if (e.getSource() instanceof JButton)
					{
						JButton sourceButton = (JButton) e.getSource();
						String srcName = sourceButton.getName();
						uiControllerGui.removeActions();
						
						fireAction(srcName);
					}
					
				}
			});	
		for (Action action : this.optUnControllableActions)
			this.uiControllerGui.addUnControllableAction(action.toString(), action.toString(), new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if (e.getSource() instanceof JButton)
					{
						JButton sourceButton = (JButton) e.getSource();
						String srcName = sourceButton.getName();
						uiControllerGui.removeActions();
						
						fireAction(srcName);
					}
					
				}
			});	
		this.uiControllerGui.setVisible(true);
	}
	
	private void fireAction(String name)
	{
		if (this.optControllableActions == null || this.optUnControllableActions == null) return;
	
		processActionList(this.optControllableActions, name, "controllable");
		processActionList(this.optUnControllableActions, name, "uncontrollable");
		
	}
	private void processActionList(List<Action> actions, String actionName, String actionType)
	{
		for (Action action : actions)
		{
			if (action.toString().equals(actionName))
			{
				this.uiControllerGui.appendMessage("Fire " + actionType + " action: " +  actionName);
				logger.info("Fire " + actionType + " action: " +  actionName);
				
				try {
					//fireTransitionEvent(action);
					addTransition(new TransitionEvent<Action>(this, action));
					
				} catch (Exception e) {
					logger.error("Error firing transition event " + action.toString(), e);				
				}
				
			}
		}		
	}
	
	
	@Override
	public void tearDown() {
		this.uiControllerGui.dispose();
		this.uiControllerGui.setVisible(false);
		this.uiControllerGui = null;
	}

}
