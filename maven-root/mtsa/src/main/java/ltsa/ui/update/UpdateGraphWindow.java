package ltsa.ui.update;

import MTSSynthesis.controller.util.GeneralConstants;
import ltsa.lts.LTSCompiler;
import ltsa.lts.LTSException;
import ltsa.lts.LTSOutput;
import ltsa.ui.HPWindow;
import ltsa.ui.update.utilities.UpdateGraphWindowListener;
import ltsa.updatingControllers.structures.graph.UpdateGraph;
import ltsa.updatingControllers.synthesis.UpdateGraphGenerator;
import org.apache.commons.lang.mutable.MutableInt;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.ParseException;
import java.util.Hashtable;
import java.util.Set;

/**
 * Created by Victor Wjugow on 13/06/15.
 */
public class UpdateGraphWindow extends JFrame {

    private static final String START_TEXT = "Start";
    private static final String COMPILE_TEXT = "Compile";
    private static final String TITLE = "Update Simulation";
    private final JPanel rootPanel;
    private final JPanel menuPanel;
    private final JComboBox<String> graphsCombo;
    private final JTextArea console;
    private final LTSOutput output;
    private final UpdateGraphGUI graphGUI;
    private final JTabbedPane tabs;

    public UpdateGraphWindow(HPWindow input, String currentDirectory, MutableInt running, UpdateGraphGUI graphGUI)
            throws HeadlessException {
        super(TITLE);
        this.console = this.initConsole();
        //		this.scrollPanel = this.initScrollPanel(this.console);
        this.output = this.initOutput();
        this.graphsCombo = initCombo(input, currentDirectory);
        this.menuPanel = this.initMenu(input, currentDirectory);
        this.graphGUI = graphGUI;
        this.tabs = this.initTabs(console, this.graphGUI);

        this.rootPanel = new JPanel();
        this.rootPanel.setLayout(new BorderLayout(0, 0));
        this.rootPanel.add(menuPanel, BorderLayout.NORTH);
        this.rootPanel.add(tabs, BorderLayout.CENTER);

        super.getContentPane().add(rootPanel, BorderLayout.CENTER);
        super.setSize(800, 600);
        super.setLocationRelativeTo(null);

        super.addWindowListener(new UpdateGraphWindowListener(running, graphGUI));
    }

    private JTabbedPane initTabs(JTextArea console, UpdateGraphGUI graphGUI) {
        JTabbedPane jTabbedPane = new JTabbedPane();
        JScrollPane scrollPanel1 = this.initScrollPanel(console);
        JScrollPane scrollPanel2 = this.initScrollPanel(graphGUI);
        jTabbedPane.addTab("Console", scrollPanel1);
        jTabbedPane.addTab("Graph", scrollPanel2);
        return jTabbedPane;
    }

    private JPanel initMenu(HPWindow input, String currentDirectory) {
        JPanel menuPanel = new JPanel();
        FlowLayout fl_panel = new FlowLayout(FlowLayout.LEADING, 5, 5);
        menuPanel.setLayout(fl_panel);
        menuPanel.add(this.initCompileButton(input, currentDirectory));
        menuPanel.add(this.initStartButton());
        menuPanel.add(this.graphsCombo);
        return menuPanel;
    }

    private JScrollPane initScrollPanel(JComponent console) {
        JScrollPane scrollPane = new JScrollPane(console);
        scrollPane.setAutoscrolls(true);
        scrollPane.setMinimumSize(new Dimension(23, 100));
        return scrollPane;
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

    private JComboBox<String> initCombo(HPWindow input, String currentDirectory) {
        Set<String> graphNames = UpdateGraphGenerator.getGraphNames();
        if (graphNames.isEmpty()) {
            input.resetInput();
            LTSCompiler compiler = new LTSCompiler(input.ltsInputString, input.ltsOutput, currentDirectory);
            compiler.parse(new Hashtable(), new Hashtable(), new Hashtable());
            graphNames = UpdateGraphGenerator.getGraphNames();
        }

        JComboBox<String> graphsCombo = new JComboBox<String>();
        graphsCombo.setEditable(false);
        for (String graph : graphNames) {
            graphsCombo.addItem(graph);
        }
        return graphsCombo;
    }

    private JButton initStartButton() {
        JButton startButton = new JButton(START_TEXT);
        AbstractAction action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object selectedItem = graphsCombo.getSelectedItem();
                if (selectedItem != null) {
                    UpdateGraph updateGraph = UpdateGraphGenerator.getGraph((String) selectedItem);
                    if (updateGraph != null) {
                        graphGUI.init(updateGraph);
                        repaint();
                        tabs.setSelectedIndex(1);
                    } else {
                        output.outln("Please compile before starting the Update Simulation");
                    }
                }
            }
        };
        startButton.addActionListener(action);
        return startButton;
    }

    private JButton initCompileButton(final HPWindow input, final String currentDirectory) {
        JButton compileButton = new JButton(COMPILE_TEXT);
        final Runnable compileAction = new Runnable() {
            @Override
            public void run() {
                Object selectedItem = graphsCombo.getSelectedItem();
                if (selectedItem != null) {
                    input.resetInput();
                    LTSCompiler compiler = new LTSCompiler(input.ltsInputString, input.ltsOutput, currentDirectory);
                    try {
                        compiler.compile();
                        UpdateGraphGenerator.generateGraph((String) selectedItem, compiler, output);
                    } catch (ParseException e) {
                        output.outln(e.getMessage());
                        e.printStackTrace();
                    } catch (LTSException e) {
                        output.outln(e.getMessage());
                        e.printStackTrace();
                    }
                    tabs.setSelectedIndex(0);
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

    public void showError(String s) {
        output.outln(s);
        tabs.setSelectedIndex(0);
    }
}