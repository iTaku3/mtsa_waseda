package ltsa.updatingControllers.synthesis;

import MTSSynthesis.ar.dc.uba.model.condition.Fluent;
import MTSSynthesis.ar.dc.uba.model.condition.FluentImpl;
import MTSSynthesis.ar.dc.uba.model.language.SingleSymbol;
import MTSSynthesis.ar.dc.uba.model.language.Symbol;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;
import MTSTools.ac.ic.doc.mtstools.model.impl.MTSImpl;
import ltsa.ac.ic.doc.mtstools.util.fsp.AutomataToMTSConverter;
import ltsa.ac.ic.doc.mtstools.util.fsp.MTSToAutomataConverter;
import ltsa.lts.CompactState;
import ltsa.lts.CompositeState;
import ltsa.lts.UpdatingControllersDefinition;
import ltsa.updatingControllers.UpdateConstants;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.*;

public class UpdatingControllersAnimatorUtils {

	public static Map<String, UpdatingControllersDefinition> updateDefinitions = new HashMap<String, UpdatingControllersDefinition>();

	// attributes for animation
	public static boolean hotSwapDone = false;
	public static MTS<Long, String> updateController;
	public static JPanel actualControllableActionsPanel = new JPanel();
	public static JPanel actualEnvironmentActionsPanel  = new JPanel();
    public static Set<String> updControllableActions;
    public static JSpinner controllerSpeed;
    public static JSpinner environmentSpeed;
    public static int pauseEnvironment = 0;

    public static void addControllableButton(String label, ActionListener actionListener)
	{
        actualControllableActionsPanel.add(createButton(label, actionListener));
	}
	public static void addEnvironmentButton(String label, ActionListener actionListener)
	{
        actualEnvironmentActionsPanel.add(createButton(label, actionListener));
	}

	public static void removeActions()
	{
        actualControllableActionsPanel.removeAll();
        actualControllableActionsPanel.revalidate();
        actualControllableActionsPanel.repaint();
        actualEnvironmentActionsPanel.removeAll();
        actualEnvironmentActionsPanel.revalidate();
        actualEnvironmentActionsPanel.repaint();

	}

	private static JButton createButton(String label, ActionListener actionListener)
	{
		JButton btnAction;
		btnAction = new JButton();
		btnAction.setName(label);
		btnAction.setText(label);
		btnAction.addActionListener(actionListener);
		return btnAction;
	}

	public static Long getActualStateFromTheUpdateController(ArrayList<String> executedActions) {

		Long actualState = updateController.getInitialState();

        executedActions.remove(executedActions.size() - 1); // remove the last action to match the current execution

		for (String action : executedActions){

			Set<Long> toStates = updateController.getTransitions(actualState, MTS.TransitionType.REQUIRED).getImage(action);

			if (toStates.size() > 1 || toStates.size() < 1) {
//                System.out.println("Executed Action: " + action +  " does not match with the updating controller");
			} else {
                actualState = toStates.iterator().next();
            }
		}

		return actualState;
	}
	
}