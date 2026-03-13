package MTSAEnactment.ar.uba.dc.lafhis.enactment;

/**
 * Created by lnahabedian on 04/08/16.
 */

import MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.UpdatePanel;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.impl.LTSAdapter;
import ltsa.updatingControllers.synthesis.UpdatingControllersAnimatorUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author lnahabedian
 */
public class UpdateControllerScheduler extends BaseController<Long, String> {

    private Logger logger = LogManager.getLogger(TakeFirstController.class.getName());
    private List<String> optControllableActions;
    private List<String> optEnvironmentActions;
    private ArrayList<String> executedActions = new ArrayList<>();
    private boolean buttonPressed;
    private UpdatePanel updateMenu;


    public UpdateControllerScheduler(String name, LTS<Long, String> lts,
                                     Set<String> controllableActions) {
        super(name, lts, controllableActions);

    }

    public UpdateControllerScheduler(String name) {
        super(name);

    }

    @Override
    public void setUp() {
        super.setUp();
        updateMenu = new UpdatePanel("Update Menu");

        getActualActions();

        makeControllableButtons();
        makeEnvironmentButtons();

        this.setReactItSelf(true);

    }

    private void makeControllableButtons() {

        for (final String action : this.optControllableActions)
            UpdatingControllersAnimatorUtils.addControllableButton(action.toString(), new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (e.getSource() instanceof JButton) {
                        JButton sourceButton = (JButton) e.getSource();
                        String srcName = sourceButton.getName();
                        UpdatingControllersAnimatorUtils.removeActions();

                        fireAction(srcName);
                        executedActions.add(srcName);
                        buttonPressed = true;
                    }

                }
            });

        UpdatingControllersAnimatorUtils.actualControllableActionsPanel.setVisible(true);
        UpdatingControllersAnimatorUtils.actualControllableActionsPanel.revalidate();
        UpdatingControllersAnimatorUtils.actualControllableActionsPanel.repaint();
    }

    private void makeEnvironmentButtons() {

        for (String action : this.optEnvironmentActions)
            UpdatingControllersAnimatorUtils.addEnvironmentButton(action.toString(), new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (e.getSource() instanceof JButton) {
                        JButton sourceButton = (JButton) e.getSource();
                        String srcName = sourceButton.getName();
                        UpdatingControllersAnimatorUtils.removeActions();

                        fireAction(srcName);
                        executedActions.add(srcName);
                        buttonPressed = true;
                    }

                }
            });

        UpdatingControllersAnimatorUtils.actualEnvironmentActionsPanel.setVisible(true);
        UpdatingControllersAnimatorUtils.actualEnvironmentActionsPanel.revalidate();
        UpdatingControllersAnimatorUtils.actualEnvironmentActionsPanel.repaint();
    }

    private List<Pair<String, Long>> getActualActions() {

        Iterator<Pair<String, Long>> stateIterator = lts.getTransitions(currentState).iterator();

        if (!stateIterator.hasNext()) {
            String exceptionString = "";
            logger.error(String.format("[Scheduler::primitiveHandleTransitionEvent] there is no reachable state from currentState %s"
                    , currentState.toString(), exceptionString));

        }

        optControllableActions = new ArrayList<String>();
        optEnvironmentActions = new ArrayList<String>();
        List<Pair<String, Long>> availables = new ArrayList<Pair<String, Long>>();

        while (stateIterator.hasNext()) {
            Pair<String, Long> currentPair = stateIterator.next();

            if (controllableActions.contains(currentPair.getFirst())) {
                optControllableActions.add(currentPair.getFirst());
            } else {
                optEnvironmentActions.add(currentPair.getFirst());
            }
            availables.add(currentPair);

        }

        return availables;
    }

    @Override
    public void takeNextAction() throws Exception {

        buttonPressed = false;

        List<Pair<String, Long>> availables =  getActualActions();
        if (availables.size() == 0) return ;

        chooseActionInterface();

        if (buttonPressed) return; // action is already executed

        executeRandom();

        if (UpdatingControllersAnimatorUtils.hotSwapDone) {
            doHotSwap();
        }
    }

    private void chooseActionInterface() {

        UpdatingControllersAnimatorUtils.removeActions();

        makeControllableButtons();
        makeEnvironmentButtons();

    }

    private void fireAction(String name) {
        if (this.optControllableActions == null || this.optEnvironmentActions == null) return;

        if (optControllableActions.contains(name)) {
            processActionList(name, "----------" + name + "-------->>>>");
        } else if (optEnvironmentActions.contains(name)) {
            processActionList(name, "<<<<------" + name + "------------");
        }

    }

    private void processActionList(String action, String message) {

        logger.info(message);

        try {
            addTransition(new TransitionEvent<String>(this, action));

        } catch (Exception e) {
            logger.error("Error firing transition event " + action, e);
        }

    }

    private void executeRandom() throws Exception {

        Integer contSpeed = (Integer) UpdatingControllersAnimatorUtils.controllerSpeed.getValue();
        Integer envSpeed = (Integer) UpdatingControllersAnimatorUtils.environmentSpeed.getValue();

        Integer waitingTime = Math.max(contSpeed, envSpeed);
        synchronized (this) {
            int i = 10;
            while (i > 0) {
                this.wait(1100 - ( waitingTime * 100));
                if (buttonPressed) return;
                i--;
            }
        }

        List<String> possibles = new ArrayList<>();
        if (contSpeed > 0) possibles.addAll(optControllableActions);
        if (envSpeed > 0 && UpdatingControllersAnimatorUtils.pauseEnvironment == 0) possibles.addAll(optEnvironmentActions);

        if (contSpeed > envSpeed) possibles.addAll(optControllableActions);
        if (contSpeed < envSpeed) possibles.addAll(optEnvironmentActions);
        if (possibles.size() == 0) return;

        int randomPos = (int) (Math.random() * possibles.size());
        String nextAction = possibles.get(randomPos);

        fireAction(nextAction);

        executedActions.add(nextAction);

    }

    private void doHotSwap() {

        logger.info("hot-swap DONE!");
        LTSAdapter<Long, String> toLTS = new LTSAdapter<Long, String>(UpdatingControllersAnimatorUtils.updateController, MTS.TransitionType.REQUIRED);
        setLts(toLTS, UpdatingControllersAnimatorUtils.getActualStateFromTheUpdateController(executedActions), UpdatingControllersAnimatorUtils.updControllableActions);
        UpdatingControllersAnimatorUtils.hotSwapDone = false;

    }


    @Override
    public void tearDown() {
        updateMenu.dispatchEvent(new WindowEvent(updateMenu, WindowEvent.WINDOW_CLOSING));

    }

}

