/**
 * 
 */
package MTSAEnactment.ar.uba.dc.lafhis.enactment;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import MTSAEnactment.ar.uba.dc.lafhis.enactment.gui.RecoverEnactorGUI;

/**
 * @author Julio
 *
 */
public class RecoverEnactor<State, Action> extends Enactor<State, Action> {

	private RecoverEnactorGUI recoverEnactorGUI;

	private List<Action> triggerActions;	
	private List<Action> optionActions;
	
	private Logger logger = LogManager.getLogger(CounterEnactor.class.getName());
	
	public RecoverEnactor(String name, List<Action> triggerActions, List<Action> optionActions) {
		super(name);
		this.triggerActions = triggerActions;
		this.optionActions = optionActions;		
	}
	public RecoverEnactor(String name, List<Action> triggerActions) {
		super(name);
		this.triggerActions = triggerActions;
		this.optionActions = new ArrayList<Action>();		
	}
	public RecoverEnactor(String name) {
		super(name);
		this.triggerActions = null;
		this.optionActions = new ArrayList<Action>();		
	}
	

	private void activateOptions(Action triggerAction)
	{
		if (this.recoverEnactorGUI == null) this.setUp();
		if (!this.recoverEnactorGUI.isVisible()) this.recoverEnactorGUI.setVisible(true);
		this.recoverEnactorGUI.setEnableButtons(true);
		
		this.recoverEnactorGUI.appendMessage("Action " + triggerAction.toString());

	}
	/* (non-Javadoc)
	 * @see ar.uba.dc.lafhis.enactment.Enactor#primitiveHandleTransitionEvent(ar.uba.dc.lafhis.enactment.TransitionEvent)
	 */
	@Override
	protected void primitiveHandleTransitionEvent(
			TransitionEvent<Action> transitionEvent) throws Exception {
		if (this.getTriggerActions() == null || this.getTriggerActions().size()==0) 
		{
			this.activateOptions(transitionEvent.getAction());
			return;
		}
		for (Action triggerAction : this.getTriggerActions())
		{
			if (transitionEvent.getAction().equals(triggerAction) )
			{				
				this.activateOptions(transitionEvent.getAction());
				return;
			}
			
		}


	}

	/* (non-Javadoc)
	 * @see ar.uba.dc.lafhis.enactment.Enactor#setUp()
	 */
	@Override
	public void setUp() {
		
		if (this.optionActions == null) return;
		
		recoverEnactorGUI = new RecoverEnactorGUI();
		recoverEnactorGUI.setTitle(this.getName());
		
		for (Action action : this.optionActions)
		{
			this.recoverEnactorGUI.addAction(action.toString(), action.toString(), new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if (e.getSource() instanceof JButton)
					{
						JButton sourceButton = (JButton) e.getSource();
						//recoverEnactorGUI.setEnableButtons(false);
						fireAction(sourceButton.getName());
					}
					
				}
			});
		}
		
		if (this.getTriggerActions().size()==0)
			recoverEnactorGUI.setEnableButtons(true);
		else			
			recoverEnactorGUI.setEnableButtons(false);
		
		
		if (this.triggerActions != null)
		{
			this.recoverEnactorGUI.appendMessage("Listening for actions");
		} else
		{
			this.recoverEnactorGUI.appendMessage("No trigger action configured!!");
		}
		recoverEnactorGUI.setVisible(true);
		

	}

	/* (non-Javadoc)
	 * @see ar.uba.dc.lafhis.enactment.Enactor#tearDown()
	 */
	@Override
	public void tearDown() {
		recoverEnactorGUI.setVisible(false);
		recoverEnactorGUI = null;

	}
	
	private void fireAction(String actionName)
	{
		if (this.getOptionActions() == null) return;
		
		for (Action action : this.getOptionActions())
		{			
			if (action.toString().equals(actionName))
			{
				try {
					//recoverEnactorGUI.setEnableButtons(false);
					this.recoverEnactorGUI.appendMessage("Fire action " + action.toString());
					fireTransitionEvent(action);
					
				} catch (Exception e) {
					logger.error("Error firing transition event " + action.toString(), e);

				}
			}
		}
	}

	/**
	 * @return the triggerAction
	 */
	public synchronized List<Action> getTriggerActions() {
		return triggerActions;
	}

	/**
	 * @param triggerAction the triggerAction to set
	 */
	public synchronized void setTriggerActions(List<Action> triggerActions) {
		this.triggerActions = triggerActions;
	}

	/**
	 * @return the optionActions
	 */
	public synchronized List<Action> getOptionActions() {
		return optionActions;
	}

	/**
	 * @param optionActions the optionActions to set
	 */
	public synchronized void setOptionActions(List<Action> optionActions) {
		this.optionActions = optionActions;
	}

	
}
