package MTSAEnactment.ar.uba.dc.lafhis.enactment.robot;

import MTSSynthesis.controller.util.GeneralConstants;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import ltsa.control.util.ControlConstants;
import ltsa.dispatcher.TransitionSystemDispatcher;
import ltsa.lts.*;
import ltsa.ui.HPWindow;
import ltsa.updatingControllers.structures.UpdatingControllerCompositeState;
import ltsa.updatingControllers.synthesis.UpdatingControllersAnimatorUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Hashtable;
import java.util.Set;

/**
 * Created by lnahabedian on 7/29/16.
 */
public class UpdatePanel extends JFrame{

    JComboBox updateCombo;
    JTextArea console;
    JPanel rootPanel;
    LTSOutput output;
    JPanel menuPanel;
    MTS<Long,String> updCont;
    Set<String> updControllableActions;


    public UpdatePanel(String title) {

        super(title);

        this.console = this.initConsole();
        JScrollPane scroll = new JScrollPane(console, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        this.output = this.initOutput();
        this.updateCombo = initCombo(HPWindow.instance, System.getProperty("user.dir"));
        this.menuPanel = this.initMenu(HPWindow.instance, System.getProperty("user.dir"));

        this.rootPanel = new JPanel();
        this.rootPanel.setLayout(new BorderLayout(0, 0));
        this.rootPanel.add(menuPanel, BorderLayout.NORTH);
        this.rootPanel.add(scroll, BorderLayout.CENTER);

        super.getContentPane().add(rootPanel, BorderLayout.CENTER);
        super.setSize(800, 600);
        super.setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        updCont = null;

        setVisible(true);

        //super.addWindowListener(new UpdateGraphWindowListener(running, graphGUI));

    }

    private JTextArea initConsole() {
        JTextArea textArea = new JTextArea("");
        textArea.setEditable(false);
        textArea.setFont(HPWindow.FIXED);
        textArea.setBackground(Color.white);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(new EmptyBorder(0, 5, 0, 0));
        return textArea;
    }

    private LTSOutput initOutput() {
        LTSOutput ltsOutput = new LTSOutput() {
            @Override
            public void out(String str) {
                console.append(str);
            }

            @Override
            public void outln(String str) {
                this.out((str != null ? str : GeneralConstants.EMPTY_STRING) + GeneralConstants.LINE_BREAK);
            }

            @Override
            public void clearOutput() {
                console.setText(GeneralConstants.EMPTY_STRING);
            }
        };
        return ltsOutput;
    }

    private JComboBox<String> initCombo(HPWindow input, String currentDirectory) {
        Set<String> updateNames = UpdatingControllersAnimatorUtils.updateDefinitions.keySet();
        if (updateNames.isEmpty()) {
            input.resetInput();
            LTSCompiler compiler = new LTSCompiler(input.ltsInputString, input.ltsOutput, currentDirectory);
            compiler.parse(new Hashtable(), new Hashtable(), new Hashtable());
            updateNames = UpdatingControllersAnimatorUtils.updateDefinitions.keySet();
        }

        JComboBox<String> updateCombo = new JComboBox<String>();
        updateCombo.setEditable(false);
        for (String graph : updateNames) {
            updateCombo.addItem(graph);
        }
        return updateCombo;
    }

    private JPanel initMenu(HPWindow input, String currentDirectory) {
        JPanel menuPanel = new JPanel();
        FlowLayout fl_panel = new FlowLayout(FlowLayout.LEADING, 5, 5);
        menuPanel.setLayout(fl_panel);
        menuPanel.add(this.initCompileButton(input, currentDirectory));
        menuPanel.add(this.hotSwapButton());
        menuPanel.add(this.updateCombo);
        return menuPanel;
    }

    private JButton initCompileButton(final HPWindow input, final String currentDirectory) {
        JButton compileButton = new JButton("Compile");
        final Runnable compileAction = new Runnable() {
            @Override
            public void run() {
                Object selectedItem = updateCombo.getSelectedItem();
                if (selectedItem != null) {
                    input.resetInput();
                    LTSCompiler compiler = new LTSCompiler(input.ltsInputString, input.ltsOutput,  currentDirectory);
                    try {

                        compileUpdateOfController(selectedItem, compiler);

                    } catch (LTSException e) {
                        output.outln(e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        };
        AbstractAction action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Thread thread = new Thread(compileAction);
                thread.start();
            }
        };
        compileButton.addActionListener(action);
        return compileButton;
    }

    private void compileUpdateOfController(Object selectedItem, LTSCompiler compiler) {
        compiler.compile();
        UpdatingControllersDefinition def =  UpdatingControllersAnimatorUtils.updateDefinitions.get(selectedItem);
        UpdatingControllerCompositeState updContCompositeState = (UpdatingControllerCompositeState) compiler
                .continueCompilation(def.getName().getName());
        TransitionSystemDispatcher.applyComposition(updContCompositeState, output);
        if (updContCompositeState.getComposition().getName().endsWith(ControlConstants.NO_CONTROLLER)) {
            throw new LTSException("update controller could not be generated because controller " + def.getName() + " could " +
                    "not be generated");
        }
        updCont = updContCompositeState.getUpdateController();
        updControllableActions = updContCompositeState.getControllableActions();

    }

    private JButton hotSwapButton() {
        JButton startButton = new JButton("Hot-Swap!");
        AbstractAction action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (updCont != null) {
                    doHotSwap();
                } else {
                    output.outln("Please compile before starting the Update Simulation");
                }
            }
        };
        startButton.addActionListener(action);
        return startButton;
    }

    private void doHotSwap() {

        UpdatingControllersAnimatorUtils.hotSwapDone = true;
        UpdatingControllersAnimatorUtils.updateController = updCont;
        UpdatingControllersAnimatorUtils.updControllableActions = updControllableActions;

    }
}
