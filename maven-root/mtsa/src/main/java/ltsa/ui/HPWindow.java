package ltsa.ui;

// This is an experimental version with progress & LTL property check

import MTSSynthesis.controller.model.ControllerGoal;
import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;
import MTSTools.ac.ic.doc.mtstools.model.SemanticType;
import ltsa.MultiCore.ComputerOptions;
import ltsa.custom.CustomAnimator;
import ltsa.custom.SceneAnimator;
import ltsa.dispatcher.TransitionSystemDispatcher;
import ltsa.editor.ColoredEditorKit;
import ltsa.enactment.EnactmentOptions;
import ltsa.enactment.EnactorFactory;
import ltsa.enactment.MTSAEnactmentSimulation;
import ltsa.enactment.SchedulerFactory;
import ltsa.exploration.Explorer;
import ltsa.exploration.ExplorerDefinition;
import ltsa.exploration.knowledge.Knowledge;
import ltsa.exploration.model.Model;
import ltsa.exploration.strategy.*;
import ltsa.exploration.view.View;
import ltsa.exploration.view.ViewNextConfiguration;
import ltsa.exploration.view.ViewNextConfigurationRandom;
import ltsa.exploration.view.ViewNextConfigurationTrace;
import ltsa.jung.LTSJUNGCanvas;
import ltsa.jung.LTSJUNGCanvas.EnumLayout;
import ltsa.lts.*;
import ltsa.lts.ltl.AssertDefinition;
import ltsa.lts.ltl.FormulaFactory;
import ltsa.lts.util.MTSUtils;
import ltsa.ui.enactment.EnactorOptionsWindows;
import ltsa.ui.update.UpdateGraphSimulation;
import ltsa.updatingControllers.structures.UpdatingControllerCompositeState;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import javax.swing.text.JTextComponent;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.List;

public class HPWindow extends JFrame implements Runnable {
    private static final String VERSION = "j1.2 v14-10-99, amimation support";
    private static final String DEFAULT = "DEFAULT";
    public static HPWindow instance = null;

    JEditorPane input;
    public LTSInputString ltsInputString;
    EditorScrollPane line_numbered_input;

    JTextArea output;
    public WindowOutput ltsOutput;

    JEditorPane manual;
    AlphabetWindow alphabet;
    PrintWindow prints;
    LTSDrawWindow draws;
    LTSLayoutWindow layouts;
    JTabbedPane textIO;
    JToolBar tools;
    JTextField stepscount;

    MTSAEnactmentSimulation<Long, String> enactmentSimulation = new MTSAEnactmentSimulation<Long, String>();
    UpdateGraphSimulation updateGraphSimulation;

    // >>> AMES: Text Search
    FindDialog findDialog;
    // <<< AMES

    JComboBox targetChoice;
    EventManager eman = new EventManager();
    Frame animator = null;
    CompositeState current = null;
    Explorer explorer = null;
    Hashtable<String, ExplorerDefinition> explorerDefinitions = null;
    String run_menu = DEFAULT;
    String asserted = null;

    // >>> AMES: Enhanced Modularity
    Hashtable<String, LabelSet> labelSetConstants = null;
    // <<< AMES

    // Listener for the edits on the current document.
    protected UndoableEditListener undoHandler = new UndoHandler();
    // UndoManager that we add edits to.
    protected UndoManager undo = new UndoManager();

    JMenu file;
    JMenu edit;
    JMenu check;
    JMenu build;
    JMenu window;
    JMenu help;
    JMenu exxx;
    JMenu option;
    JMenu mts;
    JMenu menu_enactment;
    JMenu menu_enactment_enactors;
    JMenu exploration;
    JMenuItem file_new;
    JMenuItem file_open;
    JMenuItem file_save;
    JMenuItem file_saveAs;
    JMenuItem file_export;
    JMenuItem file_exit;
    JMenuItem edit_cut;
    JMenuItem edit_copy;
    JMenuItem edit_paste;
    JMenuItem edit_undo;
    JMenuItem edit_redo;
    JMenuItem check_safe;
    JMenuItem check_progress;
    JMenuItem check_reachable; // check_stop,
    JMenuItem check_deterministic;
    JMenuItem check_legality;
    JMenuItem build_parse;
    JMenuItem build_compile;
    JMenuItem build_compose;
    JMenuItem build_minimise;
    JMenuItem help_about;
    JMenuItem help_version;
    JMenuItem supertrace_options;
    JMenuItem mtsRefinement;
    JMenuItem mtsBisimulation;
    JMenuItem mtsConsistency;
    JMenuItem checkDeadlock;
    JMenuItem menu_enactment_run;
    JMenuItem menu_enactment_options;
    JMenuItem layout_options;

    // >>> AMES: Deadlock Insensitive Analysis
    JMenuItem check_safe_no_deadlock;
    // <<< AMES

    // >>> AMES: multiple ce
    JMenuItem check_safe_multi_ce;
    // <<< AMES

    // >>> AMES: Text Search
    JMenuItem edit_find;
    // <<< AMES

    JMenu check_run;
    JMenuItem file_example;
    JMenu check_liveness;
    JMenu compositionStrategy;
    JMenuItem default_run;
    JMenuItem[] run_items, assert_items;
    String[] run_names, assert_names;
    boolean[] run_enabled;
    JCheckBoxMenuItem setWarnings;
    JCheckBoxMenuItem setWarningsAreErrors;
    JCheckBoxMenuItem setFair;
    JCheckBoxMenuItem setAlphaLTL;
    JCheckBoxMenuItem setSynchLTL;
    JCheckBoxMenuItem setPartialOrder;
    JCheckBoxMenuItem setParallelSynthesis;
    JCheckBoxMenuItem setObsEquiv;
    JCheckBoxMenuItem setReduction;
    JCheckBoxMenuItem setBigFont;
    JCheckBoxMenuItem setDisplayName;
    JCheckBoxMenuItem setNewLabelFormat;
    JCheckBoxMenuItem setAutoRun;
    JCheckBoxMenuItem setMultipleLTS;
    JCheckBoxMenuItem help_manual;
    JCheckBoxMenuItem window_alpha;
    JCheckBoxMenuItem window_print;
    JCheckBoxMenuItem window_draw;
    JCheckBoxMenuItem window_layout;
    JRadioButtonMenuItem strategyDFS, strategyBFS, strategyRandom;
    ButtonGroup strategyGroup;
    JMenuItem maxStateGeneration;
    JMenuItem randomSeed;

    // tool bar buttons - that need to be enabled and disabled
    JButton stopTool,
            parseTool,
            safetyTool, progressTool, cutTool, pasteTool,
            newFileTool,
            openFileTool, saveFileTool, compileTool, composeTool,
            minimizeTool,
            undoTool, redoTool;

    public static final Font FIXED = new Font("Monospaced", Font.PLAIN, 12);
    public static final Font BIG = new Font("Monospaced", Font.BOLD, 20);
    // Font title = new Font("SansSerif",Font.PLAIN,12);

    private AppletButton isApplet = null;

    private ApplicationContext applicationContext = null;
    private boolean fontFlag;
    Font f1 = new Font("Monospaced", Font.PLAIN, 12);
    Font f2 = new Font("Monospaced", Font.BOLD, 20);
    Font f3 = new Font("SansSerif", Font.PLAIN, 12);
    Font f4 = new Font("SansSerif", Font.BOLD, 18);

    public HPWindow(AppletButton isap) {

        isApplet = isap;
        instance = this;

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new CloseWindow());
        getContentPane().setLayout(new BorderLayout());

        // >>> AMES: Text Search
        findDialog = setFindDialog();
        // <<< AMES

        //SymbolTable.init();
        buildEditResultPane();
        buildMenuBar();
        buildToolbar();
        menuEnable(true);
        updateDoState();
        buildDrawAndLayout();
        swapto(0);


        // >>> AMES: SET Compositional Learning, Interface Learning
        // Set<JMenu> menus = new HashSet<JMenu>();
        // menus.add(file); menus.add(edit); menus.add(check);
        // menus.add(build);menus.add(mts);
        // menus.add(window); menus.add(help); menus.add(option);
        //
        // for ( String uiName : Arrays.asList(new String[]{
        // "ames-learning-ui","ames-interface-learning-ui"})) {
        // try {
        // // Initialise so that the absense of the lstar package doesn't stop
        // // compilation or execution.
        // Object compositionUI =
        // Class.forName(System.getProperty(uiName))
        // .getConstructor(
        // LTSInput.class, LTSOutput.class, LTSError.class,
        // LTSManager.class, EventManager.class)
        // .newInstance(this, this, this, this, eman);
        //
        // Map<Component,String> menuItems =
        // (Map) compositionUI.getClass()
        // .getMethod("getMenuItems")
        // .invoke(compositionUI);
        //
        // Set<? extends Component> windows =
        // (Set) compositionUI.getClass()
        // .getMethod("getWindows")
        // .invoke(compositionUI);
        //
        // // Add the necessary menu items.
        // for ( Component c : menuItems.keySet() ) {
        // String menuName = menuItems.get(c);
        // boolean added = false;
        // for ( JMenu menu : menus ) {
        // if (menu.getText().equalsIgnoreCase(menuName)) {
        // menu.add(c);
        // added = true;
        // }
        // }
        // if (!added) {
        // JMenu menu = new JMenu(menuName);
        // menus.add(menu);
        // menu.add(c);
        // }
        // }
        //
        // // Add the necessary tabbed windows.
        // for ( final Component w : windows ) {
        // final String name = w.getName();
        // final JCheckBoxMenuItem item = new JCheckBoxMenuItem(name);
        //
        // item.addActionListener(new ActionListener() {
        // public void actionPerformed(ActionEvent e) {
        // if (item.isSelected()
        // && textIO.indexOfTab(name) < 0) {
        // textIO.addTab(name,w);
        // swapto(textIO.indexOfTab(name));
        //
        // } else if ( !item.isSelected()
        // && textIO.indexOfTab(name) > 0) {
        // swapto(0);
        // textIO.removeTabAt( textIO.indexOfTab(name) );
        // }
        // }
        // });
        // window.add(item);
        // }
        // } catch (Exception e) {
//        // System.out.println("Caught a " + e + " while trying to "
        // + "load/initialise the external module '" + uiName
        // +"'; skipping.");
        // }
        // }
        // <<< AMES

        // toolbar

    }

    private void buildDrawAndLayout() {
        LTSCanvas.displayName = setDisplayName.isSelected();
        LTSCanvas.newLabelFormat = setNewLabelFormat.isSelected();
        LTSDrawWindow.singleMode = !setMultipleLTS.isSelected();
        LTSLayoutWindow.singleMode = !setMultipleLTS.isSelected();
        newDrawWindow(window_draw.isSelected());
        newLayoutWindow(window_layout.isSelected());
    }

    private FindDialog setFindDialog() {
        return new FindDialog(this) {
            JTextComponent currentTextComponent() {
                String title = textIO.getTitleAt(textIO.getSelectedIndex());
                if (title.equals("Edit"))
                    return input;
                else if (title.equals("Output"))
                    return output;
                else
                    return null;
            }
        };
    }

    private void buildEditResultPane() {
        textIO = new JTabbedPane();

        // edit window for specification source
        input = new JEditorPane();
        
        ltsInputString = new LTSInputString(input.getText());

        input.setEditorKit(new ColoredEditorKit());

        input.setFont(FIXED);
        //input.setFont(BIG);
        input.setBackground(Color.white);
        input.getDocument().addUndoableEditListener(undoHandler);
        undo.setLimit(100); // set maximum undo edits
        // input.setLineWrap(true);
        // input.setWrapStyleWord(true);
        input.setBorder(new EmptyBorder(0, 5, 0, 0));

        line_numbered_input = new EditorScrollPane(input);
        line_numbered_input.setFont(FIXED);

        // results window
        output = new JTextArea("", 30, 100);
        ltsOutput = new WindowOutput(output);
        output.setEditable(false);
        // output.setFont(FIXED);
        // Dipi, only for viva
        output.setFont(BIG);
        output.setBackground(Color.white);
        output.setLineWrap(true);
        output.setWrapStyleWord(true);
        output.setBorder(new EmptyBorder(0, 5, 0, 0));
        JScrollPane outp = new JScrollPane(output,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        textIO.addTab("Edit", line_numbered_input);
        textIO.addTab("Output", outp);
        textIO.addChangeListener(new TabChange());
        textIO.setRequestFocusEnabled(false);
        getContentPane().add("Center", textIO);
    }

    private void buildToolbar() {
        tools = new JToolBar();
        tools.setFloatable(false);
        tools.add(newFileTool = createTool("icon/new.gif", "New file", new NewFileAction()));
        tools.add(openFileTool = createTool("icon/open.gif", "Open file", new OpenFileAction()));
        tools.add(saveFileTool = createTool("icon/save.gif", "Save File", new SaveFileAction()));
        tools.addSeparator();
        tools.add(cutTool = createTool("icon/cut.gif", "Cut", new EditCutAction()));
        tools.add(createTool("icon/copy.gif", "Copy", new EditCopyAction()));
        tools.add(pasteTool = createTool("icon/paste.gif", "Paste", new EditPasteAction()));
        tools.add(undoTool = createTool("icon/undo.gif", "Undo", new UndoAction()));
        tools.add(redoTool = createTool("icon/redo.gif", "Redo", new RedoAction()));
        tools.addSeparator();
        tools.add(parseTool = createTool("icon/parse.gif", "Parse", new DoAction(DO_parse)));
        tools.add(compileTool = createTool("icon/compile.gif", "Compile", new DoAction(DO_compile)));
        tools.add(composeTool = createTool("icon/compose.gif", "Compose", new DoAction(DO_doComposition)));
        tools.add(minimizeTool = createTool("icon/minimize.gif", "Minimize", new DoAction(DO_minimiseComposition)));
        // status field used to name the composition we are working on
        targetChoice = new JComboBox();
        targetChoice.setEditable(false);
        targetChoice.addItem(DEFAULT);
        targetChoice.setToolTipText("Target Composition");
        targetChoice.setRequestFocusEnabled(false);
        targetChoice.addActionListener(new TargetAction());
        tools.add(targetChoice);
        tools.addSeparator();
        tools.add(safetyTool = createTool("icon/safety.gif", "Check safety", new DoAction(DO_safety)));
        tools.add(progressTool = createTool("icon/progress.gif", "Check Progress", new DoAction(DO_progress)));
        tools.add(stopTool = createTool("icon/stop.gif", "Stop", new StopAction()));
        stopTool.setEnabled(false);
        tools.addSeparator();
        tools.add(createTool("icon/alphabet.gif", "Run Arranged Animation", new DoAction(DO_ARRANGED_ANIMATOR)));

        /*
         * tools.addSeparator();
        tools.add(createTool("icon/exploration.gif", "Do Exploration", new DoAction(DO_EXPLORATION)));
        tools.add(createTool("icon/manual.png", "Manual", new DoAction(DO_EXPLORATION_MANUAL)));
        tools.add(stepscount = createTextBox());
        tools.add(createTool("icon/stepover.png", "Step Over", new DoAction(DO_EXPLORATION_STEPOVER)));
        tools.add(createTool("icon/resume.png", "Resume", new DoAction(DO_EXPLORATION_RESUME)));

        //tools.addSeparator();
        //tools.add(createTool("icon/blanker.gif", "Blank Screen", new BlankAction()));
         */
        getContentPane().add("North", tools);
    }

    private void buildMenuBar() {
        JMenuBar mb = new JMenuBar();
        setJMenuBar(mb);
        fileMenu(mb);
        editMenu(mb);
        checkMenu(mb);
        buildMenu(mb);
        windowMenu(mb);
        optionMenu(mb);
        enactmentMenu(mb);
        exploration(mb);
        helpMenu(mb);
    }

    private void exploration(JMenuBar mb) {
        exploration = new JMenu("Exploration");
        mb.add(exploration);
        JMenuItem do_exploration = new JMenuItem("Do Exploration");
        do_exploration.addActionListener(new DoAction(DO_EXPLORATION));
        exploration.add(do_exploration);

        JMenuItem manual_exploration = new JMenuItem("Manual");
        manual_exploration.addActionListener(new DoAction(DO_EXPLORATION_MANUAL));
        exploration.add(manual_exploration);

        JMenuItem stepover = new JMenuItem("Step Over");
        stepover.addActionListener(new DoAction(DO_EXPLORATION_STEPOVER));
        exploration.add(stepover);

        JMenuItem resume = new JMenuItem("Resume");
        resume.addActionListener(new DoAction(DO_EXPLORATION_RESUME));
        exploration.add(resume);
	}

	private void helpMenu(JMenuBar mb) {
        help = new JMenu("Help");
        mb.add(help);
        help_about = new JMenuItem("About");
        help_about.addActionListener(new HelpAboutAction());
        help.add(help_about);
        help_manual = new JCheckBoxMenuItem("Manual");
        help_manual.setSelected(false);
        help_manual.addActionListener(new HelpManualAction());
        help.add(help_manual);
        //help_version = new JMenuItem(VERSION);
        //help.add(help_version);
    }

    private void enactmentMenu(JMenuBar mb) {


        //Try to load Spring ltsa-context.xml file
        try {
            applicationContext = new ClassPathXmlApplicationContext("classpath*:ltsa-context.xml");
        } catch (Exception a) {
            Diagnostics.fatal("Error loading application context:" + a.getMessage());
        }


//        menu_enactment = new JMenu("Enactment");
//        mb.add(menu_enactment);
//        // menu_enactment_enactors = new JMenu("Enactors");
//        // menu_enactment.add(menu_enactment_enactors);
//        // Loads robot enactors
//        // fillEnactorsMenu(menu_enactment_enactors);
//
//        menu_enactment_options = new JMenuItem("Options");
//        menu_enactment_options.addActionListener(new DoAction(DO_ENACTORSOPTIONS));
//        menu_enactment.add(menu_enactment_options);

        menu_enactment_run = new JMenuItem("Run model");
        menu_enactment_run.addActionListener(new DoAction(DO_RUNENACTORS));
//        menu_enactment.add(menu_enactment_run);
        JMenu graphUpdateMenu = new GraphUpdateMenu();
        mb.add(graphUpdateMenu);
        SchedulerFactory<Long, String> schedulerFactory = null;
        if (applicationContext != null)
            schedulerFactory = this.applicationContext.getBean(SchedulerFactory.class);
        if (schedulerFactory != null) {
            List<String> schedulerNames = schedulerFactory.getSchedulersList();
            // Load first scheduler by default
            if (schedulerNames.size() > 0)
                this.enactmentOptions.scheduler = schedulerNames.get(0);
        }

    }

    private void optionMenu(JMenuBar mb) {
        OptionAction opt = new OptionAction();
        option = new JMenu("Options");
        mb.add(option);
        setWarnings = new JCheckBoxMenuItem("Display warning messages");
        setWarnings.addActionListener(opt);
        setWarnings.setSelected(true);
        option.add(setWarnings);
        setWarningsAreErrors = new JCheckBoxMenuItem("Treat warnings as errors");
        setWarningsAreErrors.addActionListener(opt);
        setWarningsAreErrors.setSelected(false);
        option.add(setWarningsAreErrors);
        setFair = new JCheckBoxMenuItem("Fair Choice for LTL check");
        setFair.addActionListener(opt);
        setFair.setSelected(false);
        option.add(setFair);

        ProgressCheck.strongFairFlag = setFair.isSelected();

        setAlphaLTL = new JCheckBoxMenuItem("Alphabet sensitive LTL");
        setAlphaLTL.addActionListener(opt);
        // option.add(setAlphaLTL);
        setAlphaLTL.setSelected(false);
        setSynchLTL = new JCheckBoxMenuItem("Timed LTL");
        setSynchLTL.addActionListener(opt);
        // option.add(setSynchLTL);
        setSynchLTL.setSelected(false);
        setPartialOrder = new JCheckBoxMenuItem("Partial Order Reduction");
        setPartialOrder.addActionListener(opt);
        option.add(setPartialOrder);
        setPartialOrder.setSelected(false);

        // On off Parallel Synthesis
        setParallelSynthesis = new JCheckBoxMenuItem("Parallel Synthesis");
        setParallelSynthesis.addActionListener(opt);
        option.add(setParallelSynthesis);
        setParallelSynthesis.setSelected(true);

        compositionStrategy = new JMenu("Composition Strategy");
        strategyGroup = new ButtonGroup();
        strategyDFS = new JRadioButtonMenuItem("Depth-first");
        strategyBFS = new JRadioButtonMenuItem("Breadth-first");
        strategyRandom = new JRadioButtonMenuItem("Random");
        strategyGroup.add(strategyDFS);
        strategyGroup.add(strategyBFS);
        strategyGroup.add(strategyRandom);
        compositionStrategy.add(strategyDFS);
        compositionStrategy.add(strategyBFS);
        compositionStrategy.add(strategyRandom);
        strategyDFS.setSelected(true);
        strategyDFS.addActionListener(opt);
        strategyBFS.addActionListener(opt);
        strategyRandom.addActionListener(opt);
        option.add(compositionStrategy);

        maxStateGeneration = new JMenuItem("Set max state limit...");
        maxStateGeneration.addActionListener(opt);
        option.add(maxStateGeneration);

        randomSeed = new JMenuItem("Set seed for randomization...");
        randomSeed.addActionListener(opt);
        option.add(randomSeed);

        setObsEquiv = new JCheckBoxMenuItem("Preserve OE for POR composition");
        setObsEquiv.addActionListener(opt);
        option.add(setObsEquiv);
        setObsEquiv.setSelected(true);
        setReduction = new JCheckBoxMenuItem("Enable Tau Reduction (Breaks bisimulation)");
        setReduction.addActionListener(opt);
        option.add(setReduction);
        setReduction.setSelected(false);
        supertrace_options = new JMenuItem("Set Supertrace parameters");
        supertrace_options.addActionListener(new SuperTraceOptionListener());
        option.add(supertrace_options);
        option.addSeparator();
        setBigFont = new JCheckBoxMenuItem("Use big font");
        setBigFont.addActionListener(opt);
        option.add(setBigFont);
        setBigFont.setSelected(false);
        setDisplayName = new JCheckBoxMenuItem("Display name when drawing LTS");
        setDisplayName.addActionListener(opt);
        option.add(setDisplayName);
        setDisplayName.setSelected(true);
        setNewLabelFormat = new JCheckBoxMenuItem(
                "Use V2.0 label format when drawing LTS");
        setNewLabelFormat.addActionListener(opt);
        option.add(setNewLabelFormat);
        setNewLabelFormat.setSelected(false);
        setMultipleLTS = new JCheckBoxMenuItem("Multiple LTS in Draw and Layout windows");
        setMultipleLTS.addActionListener(opt);
        option.add(setMultipleLTS);
        setMultipleLTS.setSelected(false);
        option.addSeparator();
        setAutoRun = new JCheckBoxMenuItem("Auto run actions in Animator");
        setAutoRun.addActionListener(opt);
        option.add(setAutoRun);
        setAutoRun.setSelected(false);

        layout_options = new JMenuItem("Layout parameters");
        layout_options.addActionListener(new LayoutOptionListener());
        option.add(layout_options);
    }

    private void windowMenu(JMenuBar mb) {
        window = new JMenu("Window");
        mb.add(window);
        window_alpha = new JCheckBoxMenuItem("Alphabet");
        window_alpha.setSelected(false);
        window_alpha.addActionListener(new WinAlphabetAction());
        window.add(window_alpha);
        window_print = new JCheckBoxMenuItem("Transitions");
        window_print.setSelected(false);
        window_print.addActionListener(new WinPrintAction());
        window.add(window_print);
        window_draw = new JCheckBoxMenuItem("Draw");
        window_draw.setSelected(true);
        window_draw.addActionListener(new WinDrawAction());
        window.add(window_draw);
        //layout
        window_layout = new JCheckBoxMenuItem("Layout");
        window_layout.setSelected(true);
        window_layout.addActionListener(new WinLayoutAction());
        window.add(window_layout);
    }

    private void buildMenu(JMenuBar mb) {
        build = new JMenu("Build");
        mb.add(build);

        int keyMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();

        build_parse = new JMenuItem("Parse");
        build_parse.addActionListener(new DoAction(DO_parse));
        build.add(build_parse);
        build_compile = new JMenuItem("Compile");
        build_compile.addActionListener(new DoAction(DO_compile));
        build.add(build_compile);
        build_compose = new JMenuItem("Compose");
        build_compose.addActionListener(new DoAction(DO_doComposition));
        build_compose.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, keyMask));
        build.add(build_compose);
        build_minimise = new JMenuItem("Minimise");
        build_minimise.addActionListener(new DoAction(DO_minimiseComposition));
        build.add(build_minimise);
    }

    private void checkMenu(JMenuBar mb) {
        check = new JMenu("Check");
        mb.add(check);

        checkDeadlock = new JMenuItem("Deadlock");
        checkDeadlock.addActionListener(new DoAction(DO_DEADLOCK));
        check.add(checkDeadlock);

        check_safe = new JMenuItem("Safety");
        check_safe.addActionListener(new DoAction(DO_safety));
        check.add(check_safe);


        check_progress = new JMenuItem("Progress");
        check_progress.addActionListener(new DoAction(DO_progress));
        check.add(check_progress);

        check_liveness = new JMenu("LTL");
        if (hasLTL2BuchiJar())
            check.add(check_liveness);
        


        mtsBisimulation = new JMenuItem("Bisimulation");
        mtsBisimulation.addActionListener(new DoAction(DO_BISIMULATION));
        check.add(mtsBisimulation);

        check_deterministic = new JMenuItem("Determinism");
        check_deterministic.addActionListener(new DoAction(DO_deterministic));
        check.add(check_deterministic);

        check_legality = new JMenuItem("Legality");
        check_legality.addActionListener(new DoAction(DO_LEGALITY));
        check.add(check_legality);        
        
        mts = new JMenu("MTS");
        check.add(mts);        

        mtsRefinement = new JMenuItem("Refinement");
        mtsRefinement.addActionListener(new DoAction(DO_REFINEMENT));
        mts.add(mtsRefinement);

        mtsConsistency = new JMenuItem("Consistency");
        mtsConsistency.addActionListener(new DoAction(DO_CONSISTENCY));
        mts.add(mtsConsistency);
        
        check_run = new JMenu("Run");
        check.add(check_run);
        default_run = new JMenuItem(DEFAULT);
        default_run.addActionListener(new ExecuteAction(DEFAULT));
        check_run.add(default_run);
        check_reachable = new JMenuItem("Supertrace");
        check_reachable.addActionListener(new DoAction(DO_reachable));
        check.add(check_reachable);



        // check_stop = new JMenuItem("Stop");
        // check_stop.addActionListener(new StopAction());
        // check_stop.setEnabled(false);
        // check.add(check_stop);
    }

    private void editMenu(JMenuBar mb) {
        edit = new JMenu("Edit");
        mb.add(edit);

        int keyMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
        //int keyMask = ActionEvent.CTRL_MASK

        edit_cut = new JMenuItem("Cut");
        edit_cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, keyMask));
        edit_cut.addActionListener(new EditCutAction());
        edit.add(edit_cut);
        edit_copy = new JMenuItem("Copy");
        edit_copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, keyMask));
        edit_copy.addActionListener(new EditCopyAction());
        edit.add(edit_copy);
        edit_paste = new JMenuItem("Paste");
        edit_paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, keyMask));
        edit_paste.addActionListener(new EditPasteAction());
        edit.add(edit_paste);
        edit.addSeparator();
        edit_undo = new JMenuItem("Undo");
        edit_undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, keyMask));
        edit_undo.addActionListener(new UndoAction());
        edit.add(edit_undo);
        edit_redo = new JMenuItem("Redo");
        edit_redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, keyMask));
        edit_redo.addActionListener(new RedoAction());
        edit.add(edit_redo);

        // >>> AMES: Text Search
        edit_find = new JMenuItem("Find");
        edit_find.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, keyMask));
        edit_find.addActionListener(new EditFindAction());
        edit.add(edit_find);
        // <<< AMES
    }

    private void fileMenu(JMenuBar mb) {
        file = new JMenu("File");
        mb.add(file);

        int keyMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();

        file_new = new JMenuItem("New");
        file_new.addActionListener(new NewFileAction());
        file.add(file_new);
        file_example = new JMenuItem("Open Examples");
        file_example.addActionListener(new ExampleFileAction());
        file.add(file_example);
        file_open = new JMenuItem("Open...");
        file_open.addActionListener(new OpenFileAction());
        file.add(file_open);
        file_save = new JMenuItem("Save");
        file_save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, keyMask));
        file_save.addActionListener(new SaveFileAction());
        file.add(file_save);
        file_saveAs = new JMenuItem("Save as...");
        file_saveAs.addActionListener(new SaveAsFileAction());
        file.add(file_saveAs);
        file_export = new JMenuItem("Export...");
        file_export.addActionListener(new ExportFileAction());
        file.add(file_export);
        file_exit = new JMenuItem("Quit");
        file_exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, keyMask));
        file_exit.addActionListener(new ExitFileAction());
        file.add(file_exit);
    }

    private EnactmentOptions<Long, String> enactmentOptions = new EnactmentOptions<Long, String>();

    // ----------------------------------------------------------------------
    static void centre(Component c) {
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screen = tk.getScreenSize();
        Dimension ltsa = c.getSize();
        double x = (screen.getWidth() - ltsa.getWidth()) / 2;
        double y = (screen.getHeight() - ltsa.getHeight()) / 2;
        c.setLocation((int) x, (int) y);
    }

    // ----------------------------------------------------------------------
    void left(Component c) {
        Point ltsa = getLocationOnScreen();
        ltsa.translate(10, 100);
        c.setLocation(ltsa);
    }

    // -----------------------------------------------------------------------

    protected JTextField createTextBox() {
        JTextField t = new JTextField("1", 1);
        t.setMaximumSize(new Dimension(300, 300));
        return t;
    }

    // ------------------------------------------------------------------------

    void menuEnable(boolean flag) {
        boolean application = (isApplet == null);
        file_new.setEnabled(flag && tabindex == 0);
        file_example.setEnabled(flag && tabindex == 0);
        file_open.setEnabled(application && flag && tabindex == 0);
        file_exit.setEnabled(flag);
        check_safe.setEnabled(flag);

        check_progress.setEnabled(flag);
        check_run.setEnabled(flag);
        check_reachable.setEnabled(flag);
        build_parse.setEnabled(flag);
        build_compile.setEnabled(flag);
        build_compose.setEnabled(flag);
        build_minimise.setEnabled(flag);
        stopTool.setEnabled(true);
        parseTool.setEnabled(flag);
        safetyTool.setEnabled(flag);
        progressTool.setEnabled(flag);
        compileTool.setEnabled(flag);
        composeTool.setEnabled(flag);
        minimizeTool.setEnabled(flag);

        file_save.setEnabled(application);
        file_saveAs.setEnabled(application);
        file_export.setEnabled(application);
        saveFileTool.setEnabled(application);
    }

    // ------------------------------------------------------------------------
    private final static int DO_safety = 1;

    // >>> AMES: Deadlock Insensitive Analysis
    private final static int DO_safety_no_deadlock = 2;
    // <<< AMES

    private final static int DO_execute = 3;
    private final static int DO_reachable = 4;
    private final static int DO_compile = 5;
    private final static int DO_doComposition = 6;
    private final static int DO_minimiseComposition = 7;
    private final static int DO_progress = 8;
    private final static int DO_liveness = 9;
    private final static int DO_parse = 10;

    // Dipi
    private final static int DO_PLUS_CR = 11;
    private final static int DO_PLUS_CA = 12;
    private static final int DO_DETERMINISE = 13;
    private static final int DO_REFINEMENT = 14;
    private static final int DO_DEADLOCK = 15;
    private static final int DO_CONSISTENCY = 16;
    // Dipi

    // Naha
    static final int DO_ARRANGED_ANIMATOR = 19;
    static final int DO_EXPLORATION = 21;
    static final int DO_EXPLORATION_STEPOVER = 25;
    static final int DO_EXPLORATION_RESUME = 23;
    static final int DO_EXPLORATION_MANUAL = 24;
    static final int DO_deterministic = 999;
    static final int DO_LEGALITY = 27;
    // Naha

    private static final int DO_RUNENACTORS = 17;
    private static final int DO_ENACTORSOPTIONS = 18;

    // >>> AMES: multiple ce
    private final static int DO_safety_multi_ce = 20;
    // <<< AMES
    private static final int DO_GRAPH_UPDATE = 22;
    
    
    private static final int DO_BISIMULATION = 26;

    private int theAction = 0;
    private Thread executer;

    private void do_action(int action) {
        menuEnable(false);
        // check_stop.setEnabled(true);
        // stopTool.setEnabled(true);
        theAction = action;
        executer = new Thread(this);
//        executer.setPriority(Thread.NORM_PRIORITY - 1);
        executer.start();
    }

    public void run() {
        try {
            switch (theAction) {
                case DO_safety:
                    showOutput();
                    safety();
                    break;

                // >>> AMES: Deadlock Insensitive Analysis
                case DO_safety_no_deadlock:
                    showOutput();
                    safety(false, false);
                    break;
                // <<< AMES

                // >>> AMES: multiple ce
                case DO_safety_multi_ce:
                    showOutput();
                    safety(false, true);
                    break;
                // <<< AMES

                case DO_ARRANGED_ANIMATOR:
                    animate();
                    break;
                case DO_EXPLORATION:
                    showOutput();
                    compile();
                    doComposition();
                    exploration_new();
                    break;
                case DO_EXPLORATION_STEPOVER:
                    exploration_stepover();
                    break;
                case DO_EXPLORATION_MANUAL:
                    exploration_manual();
                    break;
                case DO_EXPLORATION_RESUME:
                    exploration_resume();
                    break;
                case DO_reachable:
                    showOutput();
                    reachable();
                    break;
                case DO_deterministic:
                    showOutput();
                    checkDeterministic();
                    break;
                case DO_compile:
                    showOutput();
                    compile();
                    break;
                case DO_doComposition:
                    showOutput();
                    doComposition();
                    break;
                case DO_minimiseComposition:
                    showOutput();
                    minimiseComposition();
                    break;
                case DO_progress:
                    showOutput();
                    progress();
                    break;
                case DO_liveness:
                    showOutput();
                    liveness();
                    break;
                case DO_parse:
                    parse();
                    break;
                case DO_PLUS_CR:
                    doApplyPlusCROperator();
                    break;
                case DO_PLUS_CA:
                    doApplyPlusCAOperator();
                    break;
                case DO_DETERMINISE:
                    doDeterminise();
                    break;
                case DO_REFINEMENT:
                    doRefinement();
                    break;
                case DO_BISIMULATION:
                    doBisimulation();
                    break;
                case DO_LEGALITY:
                    doLegality();
                    break;
                case DO_DEADLOCK:
                    showOutput();
                    doDeadlockCheck();
                    break;
                case DO_CONSISTENCY:
                    doConsistency();
                    break;
                case DO_RUNENACTORS:
                    doRunEnactors();
                    break;
                case DO_ENACTORSOPTIONS:
                    doEnactorOptions();
                    break;
                case DO_GRAPH_UPDATE:
                    doGraphUpdate();
                    break;
                case DO_execute:
                    animate();
                    break;
                default:
                    ltsOutput.outln("**** The option selected is not implemented. ");
            }
        } catch (Throwable e) {
            showOutput();
            ltsOutput.outln("**** Runtime Exception: " + e);
            e.printStackTrace();
            current = null;
            explorer = null;
            explorerDefinitions = null;

        } finally {
            menuEnable(true);
        }
        // check_stop.setEnabled(false);
        // stopTool.setEnabled(false);
    }

    private void checkDeterministic() {
        doComposition();
        if (current != null && current.machines.size() > 0) {
            CompactState currentCS = current.getComposition();
            ltsOutput.out(currentCS.getName() + " is ");
            if (currentCS.isNonDeterministic()) {
                ltsOutput.outln("non-deterministic.");
            } else {
                ltsOutput.outln("deterministic.");
            }
        }
    }

    private void doGraphUpdate() {
        updateGraphSimulation = new UpdateGraphSimulation(this, currentDirectory, applicationContext);
        updateGraphSimulation.start();
    }

    // ------------------------------------------------------------------------

    class CloseWindow extends WindowAdapter {


        public void windowClosing(WindowEvent e) {
            quitAll();
        }

        public void windowActivated(WindowEvent e) {
            if (animator != null)
                animator.toFront();
        }
    }

    // ------------------------------------------------------------------------
    private void invalidateState() {
        current = null;
        explorer = null;
        explorerDefinitions = null;
        targetChoice.removeAllItems();
        targetChoice.addItem(DEFAULT);
        check_run.removeAll();
        check_run.add(default_run);
        run_items = null;
        assert_items = null;
        run_names = null;
        check_liveness.removeAll();
        validate();
        eman.post(new LTSEvent(LTSEvent.INVALID, null));
        if (animator != null) {
            animator.dispose();
            animator = null;
        }
    }

    private void postState(CompositeState m) {
        if (animator != null) {
            animator.dispose();
            animator = null;
        }
        eman.post(new LTSEvent(LTSEvent.INVALID, m));
    }

    // ------------------------------------------------------------------------
    // File handling
    // -----------------------------------------------------------------------

    private final static String fileType = "*.lts";
    private String openFile = fileType;
    public String currentDirectory;
    private String savedText = "";

    private void newFile() {
        if (checkSave()) {
            setTitle("MTS Analyser");
            savedText = "";
            openFile = fileType;
            currentDirectory = System.getProperty("user.home");
            input.setText("");
            swapto(0);
            output.setText("");
            invalidateState();
        }
        repaint(); // hack to solve display problem
    }

    public void newExample(String dir, String ex) {
        undo.discardAllEdits();
        input.getDocument().removeUndoableEditListener(undoHandler);
        if (checkSave()) {
            invalidateState();
            ltsOutput.clearOutput();
            currentDirectory = dir;
            doOpenFile(dir, ex, false); //TODO: test
        }
        input.getDocument().addUndoableEditListener(undoHandler);
        updateDoState();
        repaint();
    }

    // ------------------------------------------------------------------------
    private void openAFile() {
        if (checkSave()) {
            invalidateState();
            ltsOutput.clearOutput();
            FileDialog fd = new FileDialog(this, "Select source file:");
            if (currentDirectory != null)
                fd.setDirectory(currentDirectory);
            fd.setFile(fileType);
            fd.setVisible(true);
            doOpenFile(currentDirectory = fd.getDirectory(), fd.getFile(),
                    false);
        }
        repaint(); // hack to solve display problem
    }

    private void doOpenFile(String dir, String f, boolean resource) {
        if (f != null)
            try {
                ltsOutput.clearOutput();
                openFile = f;
                setTitle("MTSA - " + openFile);
                InputStream fin;
                if (!resource)
                    fin = new FileInputStream(dir + openFile);
                else
                    fin = getClass().getClassLoader().getResourceAsStream(dir + "/" + openFile);
                // now turn the FileInputStream into a DataInputStream
                try {
                    BufferedReader myInput = new BufferedReader(
                            new InputStreamReader(fin));
                    try {
                        String thisLine;
                        StringBuffer buff = new StringBuffer();
                        while ((thisLine = myInput.readLine()) != null) {
                            buff.append(thisLine + "\n");
                        }
                        savedText = buff.toString();
                        input.setText(savedText);
                        parse();
                    } catch (Exception e) {
                        ltsOutput.outln("Error reading file: " + e);
                    }
                } // end try
                catch (Exception e) {
                    ltsOutput.outln("Error creating InputStream: " + e);
                }
            } // end try
            catch (Exception e) {
                ltsOutput.outln("Error creating FileInputStream: " + e);
            }

        // >>> AMES: Arbitrary Fixes
        input.setCaretPosition(0);
        // <<< AMES
    }

    // ------------------------------------------------------------------------

    private void saveAsFile() {
        FileDialog fd = new FileDialog(this, "Save file in:", FileDialog.SAVE);
        if (currentDirectory != null)
            fd.setDirectory(currentDirectory);
        fd.setFile(openFile);
        fd.setVisible(true);
        String tmp = fd.getFile();
        if (tmp != null) { // if not cancelled
            currentDirectory = fd.getDirectory();
            openFile = tmp;
            setTitle("MTSA - " + openFile);
            saveFile();
        }
    }

    private void saveFile() {
        if (openFile != null && openFile.equals("*.lts"))
            saveAsFile();
        else if (openFile != null)
            try {
                int i = openFile.indexOf('.', 0);
                if (i > 0)
                    openFile = openFile.substring(0, i) + "." + "lts";
                else
                    openFile = openFile + ".lts";
                String tempname = (currentDirectory == null) ? openFile
                        : currentDirectory + openFile;
                FileOutputStream fout = new FileOutputStream(tempname);
                // now convert the FileOutputStream into a PrintStream
                PrintStream myOutput = new PrintStream(fout);
                savedText = input.getText();
                myOutput.print(savedText);
                myOutput.close();
                fout.close();
                ltsOutput.outln("Saved in: " + tempname);
            } catch (IOException e) {
                ltsOutput.outln("Error saving file: " + e);
            }
    }

    // -------------------------------------------------------------------------

    private void exportFile() {
        String message = "Export as Aldebaran format (.aut) to:";
        FileDialog fd = new FileDialog(this, message, FileDialog.SAVE);
        if (current == null || (!current.isHeuristic && current.composition == null)) {
			JOptionPane.showMessageDialog(this,
					"No target composition to export");
			return;
		}
        String fname = current.composition == null ? current.name : current.composition.name;
		String extension = current.isHeuristic ? ".xml" : ".aut";
		fd.setFile(fname + extension);
		fd.setDirectory(currentDirectory);
		fd.setVisible(true);
		String sn;
		if ((sn = fd.getFile()) != null)
			try {
				int i = sn.indexOf('.', 0);
				if (i == -1)
					sn += extension;
				else
					extension = sn.substring(i,sn.length());
				File file = new File(fd.getDirectory(), sn);
				FileOutputStream fout = new FileOutputStream(file);
				// now convert the FileOutputStream into a PrintStream
				PrintStream myOutput = new PrintStream(fout);
				AbstractTranslator translator = null;
				switch (extension) {
					case ".xml":   translator = new XMLTranslator(); break;
					case ".smv":   translator = new SMVTranslator(); break;
					case ".slugs": translator = new SlugsTranslator(); break;
					case ".py":    translator = new CTLPYTranslator(); break;
					case ".pddl":  translator = new PDDLTranslator(); break;
                    case ".fsp":   translator = new FSPTranslator(); break;
				}
				if (translator != null)
					translator.translate(current, myOutput);
				else
					current.composition.printAUT(myOutput);
				myOutput.close();
				fout.close();
				ltsOutput.outln("Exported to: " + fd.getDirectory() + file);
			} catch (IOException e) {
				ltsOutput.outln("Error exporting file: " + e);
			}
    }

    // ------------------------------------------------------------------------
    // return false if operation cancelled otherwise true
    private boolean checkSave() {
        if (isApplet != null)
            return true;
        if (!savedText.equals(input.getText())) {
            int result = JOptionPane.showConfirmDialog(this,
                    "Do you want to save the contents of " + openFile);
            if (result == JOptionPane.YES_OPTION) {
                saveFile();
                return true;
            } else if (result == JOptionPane.NO_OPTION)
                return true;
            else if (result == JOptionPane.CANCEL_OPTION)
                return false;
        }
        return true;
    }

    // ------------------------------------------------------------------------
    private void doFont() {
        if (setBigFont.getState()) {
            line_numbered_input.setFont(BIG);
            output.setFont(BIG);
        } else {
            line_numbered_input.setFont(FIXED);
            output.setFont(FIXED);
        }
        pack();
        setVisible(true);
    }

    // ------------------------------------------------------------------------

    private void quitAll() {
        if (isApplet != null) {
            this.dispose();
            isApplet.ended();
        } else {
            if (checkSave())
                System.exit(0);
        }
    }

    // ----Event
    // Handling-----------------------------------------------------------

    class NewFileAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            undo.discardAllEdits();
            input.getDocument().removeUndoableEditListener(undoHandler);
            newFile();
            input.getDocument().addUndoableEditListener(undoHandler);
            updateDoState();
        }
    }

    class OpenFileAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            undo.discardAllEdits();
            input.getDocument().removeUndoableEditListener(undoHandler);
            openAFile();
            input.getDocument().addUndoableEditListener(undoHandler);
            updateDoState();
        }
    }

    class ExampleFileAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            undo.discardAllEdits();
            //URL examplesDir = (getClass().getClassLoader().getResource("examples/"));
            //InputStream algo = getClass().getClassLoader().getResourceAsStream("examples/");
            String selection = "";
            SwingUtilities.invokeLater(new FileBrowser("examples", selection));
        }
    }


    class SaveFileAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String pp = textIO.getTitleAt(textIO.getSelectedIndex());
            if (pp.equals("Edit") || pp.equals("Output"))
                saveFile();
            else if (pp.equals("Alphabet"))
                alphabet.saveFile();
            else if (pp.equals("Transitions"))
                prints.saveFile(currentDirectory, ".txt");
            else if (pp.equals("Draw"))
                draws.saveFile();
            else if (pp.equals("Layout"))
                layouts.saveFile();
        }
    }

    class SaveAsFileAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String pp = textIO.getTitleAt(textIO.getSelectedIndex());
            if (pp.equals("Edit"))
                saveAsFile();
        }
    }

    class ExportFileAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String pp = textIO.getTitleAt(textIO.getSelectedIndex());
            if (pp.equals("Edit") || pp.equals("Transitions"))
                exportFile();
        }
    }

    class ExitFileAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            quitAll();
        }
    }

    class DoAction implements ActionListener {
        int actionCode;

        DoAction(int a) {
            actionCode = a;
        }

        public void actionPerformed(ActionEvent e) {
            do_action(actionCode);
        }

    }

    class OptionAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            if (source == setWarnings)
                Diagnostics.warningFlag = setWarnings.isSelected();
            else if (source == setWarningsAreErrors)
                Diagnostics.warningsAreErrors = setWarningsAreErrors
                        .isSelected();
            else if (source == setFair)
                ProgressCheck.strongFairFlag = setFair.isSelected();
            else if (source == setAlphaLTL)
                AssertDefinition.addAsterisk = !setAlphaLTL.isSelected();
            else if (source == setSynchLTL)
                FormulaFactory.normalLTL = !setSynchLTL.isSelected();
            else if (source == setPartialOrder)
                Analyser.partialOrderReduction = setPartialOrder.isSelected();
            else if (source == setParallelSynthesis) {
                if (setParallelSynthesis.isSelected()) {
                    ComputerOptions.getInstance().setAllowedThreads(Runtime.getRuntime().availableProcessors());
                } else {
                    ComputerOptions.getInstance().setAllowedThreads(1);
                }
            } else if (source == setObsEquiv)
                Analyser.preserveObsEquiv = setObsEquiv.isSelected();
            else if (source == setReduction)
                CompositeState.reduceFlag = setReduction.isSelected();
            else if (source == setBigFont) {
                AnimArrangedWindow.fontFlag = setBigFont.isSelected();
                fontFlag = setBigFont.isSelected();
                PrintWindow.fontFlag = setBigFont.isSelected();
                LTSDrawWindow.fontFlag = setBigFont.isSelected();
                LTSCanvas.fontFlag = setBigFont.isSelected();
                doFont();
            } else if (source == setDisplayName) {
                if (draws != null)
                    draws.setDrawName(setDisplayName.isSelected());
                LTSCanvas.displayName = setDisplayName.isSelected();
            } else if (source == setMultipleLTS) {
                LTSDrawWindow.singleMode = !setMultipleLTS.isSelected();
                if (draws != null)
                    draws.setMode(LTSDrawWindow.singleMode);
                LTSLayoutWindow.singleMode = !setMultipleLTS.isSelected();
                if (layouts != null) layouts.setMode(LTSLayoutWindow.singleMode);
            } else if (source == setNewLabelFormat) {
                if (draws != null)
                    draws.setNewLabelFormat(setNewLabelFormat.isSelected());
                LTSCanvas.newLabelFormat = setNewLabelFormat.isSelected();
            } else if (source == strategyBFS) {
                // TODO cleanup composition
                if (strategyBFS.isSelected())
                    Options.setCompositionStrategyClass(Options.CompositionStrategy.BFS_STRATEGY);
            } else if (source == strategyDFS) {
                // TODO cleanup composition
                Options.setCompositionStrategyClass(Options.CompositionStrategy.DFS_STRATEGY);
            } else if (source == strategyRandom) {
                // TODO cleanup composition
                Options.setCompositionStrategyClass(Options.CompositionStrategy.RANDOM_STRATEGY);
            } else if (source == maxStateGeneration) {
                JFrame maxStatesDialog = new MaxStatesDialog();
                maxStatesDialog.setVisible(true);
            } else if (source == randomSeed) {
                JFrame randomSeedDialog = new RandomSeedDialog();
                randomSeedDialog.setVisible(true);
            } 
        }
    }

    class SuperTraceOptionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            setSuperTraceOption();
        }
    }

    class LayoutOptionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            final JFrame f = new JFrame("Layout parameters");
            f.setResizable(false);
            f.setSize(300, 600);
            f.setLocationRelativeTo(null);
            Container container = f.getContentPane();
            container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
            JPanel content = new JPanel();
            content.setLayout(new GridLayout(0, 1));

            JPanel kkpanel = new JPanel();
            kkpanel.setBorder(BorderFactory.createTitledBorder(EnumLayout.KamadaKawai.toString()));
            kkpanel.setLayout(new GridLayout(0, 2));

            kkpanel.add(new JLabel("Length factor"));
            final JSpinner KK_length_factor_spinner = new JSpinner(new SpinnerNumberModel(LTSJUNGCanvas.KK_length_factor, 0.1, 10.0, 0.1));
            kkpanel.add(KK_length_factor_spinner);

// Not useful for connected graphs
//		  kkpanel.add(new JLabel("Distance"));
//		  final JSpinner KK_distance_spinner = new JSpinner(new SpinnerNumberModel(LTSJUNGCanvas.KK_distance,0.1,10.0,0.1));
//		  kkpanel.add(KK_distance_spinner);

            kkpanel.add(new JLabel("Max iterations"));
            final JSpinner kk_it_spinner = new JSpinner(new SpinnerNumberModel(LTSJUNGCanvas.KK_max_iterations, 1, 10000, 1));
            kkpanel.add(kk_it_spinner);

            container.add(kkpanel);

            JPanel frpanel = new JPanel();
            frpanel.setBorder(BorderFactory.createTitledBorder(EnumLayout.FruchtermanReingold.toString()));
            frpanel.setLayout(new GridLayout(0, 2));

            frpanel.add(new JLabel("Attraction"));
            final JSpinner fr_attraction_spinner = new JSpinner(new SpinnerNumberModel(LTSJUNGCanvas.FR_attraction, 0.1, 10.0, 0.05));
            frpanel.add(fr_attraction_spinner);

            frpanel.add(new JLabel("Repulsion"));
            final JSpinner fr_repulsion_spinner = new JSpinner(new SpinnerNumberModel(LTSJUNGCanvas.FR_repulsion, 0.1, 10.0, 0.05));
            frpanel.add(fr_repulsion_spinner);

            frpanel.add(new JLabel("Max iterations"));
            final JSpinner fr_it_spinner = new JSpinner(new SpinnerNumberModel(LTSJUNGCanvas.FR_max_iterations, 1, 10000, 1));
            frpanel.add(fr_it_spinner);

            container.add(frpanel);

// No useful parameters for TreeLikeLTS, RadialLTS
//		  JPanel treepanel = new JPanel();
//		  treepanel.setBorder(BorderFactory.createTitledBorder(EnumLayout.TreeLikeLTS.toString()));
//		  treepanel.setLayout(new GridLayout(0,2));
//
//		  treepanel.add(new JLabel("Horizontal distance"));
//		  final JSpinner tree_distx_spinner = new JSpinner(new SpinnerNumberModel(LTSJUNGCanvas.Tree_distX,0,1000,1));
//		  treepanel.add(tree_distx_spinner);
//
//		  treepanel.add(new JLabel("Vertical distance"));
//		  final JSpinner tree_disty_spinner = new JSpinner(new SpinnerNumberModel(LTSJUNGCanvas.Tree_distY,0,1000,1));
//		  treepanel.add(tree_disty_spinner);
//
//		  content.add(treepanel);
//
//		  JPanel radialpanel = new JPanel();
//		  radialpanel.setBorder(BorderFactory.createTitledBorder(EnumLayout.RadialLTS.toString()));
//		  radialpanel.setLayout(new GridLayout(0,2));
//
//		  radialpanel.add(new JLabel("Horizontal distance"));
//		  final JSpinner radial_distx_spinner = new JSpinner(new SpinnerNumberModel(LTSJUNGCanvas.Radial_distX,0,1000,1));
//		  radialpanel.add(radial_distx_spinner);
//
//		  radialpanel.add(new JLabel("Vertical distance"));
//		  final JSpinner radial_disty_spinner = new JSpinner(new SpinnerNumberModel(LTSJUNGCanvas.Radial_distY,0,1000,1));
//		  radialpanel.add(radial_disty_spinner);
//
//		  content.add(radialpanel);

//		  container.add(content);

            final JButton okbutton = new JButton("Ok");
            final JButton cancelbutton = new JButton("Cancel");
            final JButton applybutton = new JButton("Apply");

            class ButtonOptionListener implements ActionListener {
                public void actionPerformed(ActionEvent e) {
                    if (e.getSource() == applybutton) {
                        updateValues();
                        if (layouts != null) {
                            layouts.refresh();
                        }
                    } else if (e.getSource() == okbutton) {
                        updateValues();
                        f.dispose();
                    } else if (e.getSource() == cancelbutton) {
                        f.dispose();
                    }
                }

                private void updateValues() {
                    try {
                        Object o = KK_length_factor_spinner.getValue();
                        if (o == null) return;
                        LTSJUNGCanvas.KK_length_factor = Double.parseDouble(o.toString()) < 0 ? 0 : (Double.parseDouble(o.toString()) > 10 ? 10 : Double.parseDouble(o.toString()));
                        //if (layouts!=null) layouts.getCanvas().setOptions(LTSJUNGCanvas.LayoutOptions.KK_length_factor,LTSJUNGCanvas.KK_length_factor);
                    } catch (NumberFormatException ignored) {
                    }
//		    	  try {
//		  	    	  Object o = KK_distance_spinner.getValue();
//		  	    	  if (o==null) return;
//		  	    	  LTSJUNGCanvas.KK_distance = Double.parseDouble(o.toString()) < 0 ? 0 : (Double.parseDouble(o.toString()) > 10 ? 10 : Double.parseDouble(o.toString()));
//		  	    	  //if (layouts!=null) layouts.setKK_distance(LTSLayoutWindow.KK_distance);
//		    	  } catch(NumberFormatException nfe) {}
                    try {
                        Object o = kk_it_spinner.getValue();
                        if (o == null) return;
                        LTSJUNGCanvas.KK_max_iterations = Integer.parseInt(o.toString()) < 0 ? 0 : (Integer.parseInt(o.toString()) > 10000 ? 10000 : Integer.parseInt(o.toString()));
                        //if (layouts!=null) layouts.setKK_max_iterations(LTSLayoutWindow.KK_max_iterations);
                    } catch (NumberFormatException ignored) {
                    }
                    try {
                        Object o = fr_attraction_spinner.getValue();
                        if (o == null) return;
                        LTSJUNGCanvas.FR_attraction = Double.parseDouble(o.toString()) < 0 ? 0 : (Double.parseDouble(o.toString()) > 10 ? 10 : Double.parseDouble(o.toString()));
                        //if (layouts!=null) layouts.setFR_attraction(LTSLayoutWindow.FR_attraction);
                    } catch (NumberFormatException ignored) {
                    }
                    try {
                        Object o = fr_repulsion_spinner.getValue();
                        if (o == null) return;
                        LTSJUNGCanvas.FR_repulsion = Double.parseDouble(o.toString()) < 0 ? 0 : (Double.parseDouble(o.toString()) > 10 ? 10 : Double.parseDouble(o.toString()));
                        //if (layouts!=null) layouts.setFR_repulsion(LTSLayoutWindow.FR_repulsion);
                    } catch (NumberFormatException ignored) {
                    }
                    try {
                        Object o = fr_it_spinner.getValue();
                        if (o == null) return;
                        LTSJUNGCanvas.FR_max_iterations = Integer.parseInt(o.toString()) < 0 ? 0 : (Integer.parseInt(o.toString()) > 10000 ? 10000 : Integer.parseInt(o.toString()));
                        //if (layouts!=null) layouts.setFR_max_iterations(LTSLayoutWindow.FR_max_iterations);
                    } catch (NumberFormatException ignored) {
                    }
//		    	  try {
//		  	    	  Object o = tree_distx_spinner.getValue();
//		  	    	  if (o==null) return;
//		  	    	  LTSJUNGCanvas.Tree_distX = Integer.parseInt(o.toString()) < 0 ? 0 : (Integer.parseInt(o.toString()) > 1000 ? 1000 : Integer.parseInt(o.toString()));
//		  	    	  //if (layouts!=null) layouts.setFR_max_iterations(LTSLayoutWindow.FR_max_iterations);
//		    	  } catch(NumberFormatException nfe) {}
//		    	  try {
//		  	    	  Object o = tree_disty_spinner.getValue();
//		  	    	  if (o==null) return;
//		  	    	  LTSJUNGCanvas.Tree_distY = Integer.parseInt(o.toString()) < 0 ? 0 : (Integer.parseInt(o.toString()) > 1000 ? 1000 : Integer.parseInt(o.toString()));
//		  	    	  //if (layouts!=null) layouts.setFR_max_iterations(LTSLayoutWindow.FR_max_iterations);
//		    	  } catch(NumberFormatException nfe) {}
//		    	  try {
//		  	    	  Object o = radial_distx_spinner.getValue();
//		  	    	  if (o==null) return;
//		  	    	  LTSJUNGCanvas.Radial_distX = Integer.parseInt(o.toString()) < 0 ? 0 : (Integer.parseInt(o.toString()) > 1000 ? 1000 : Integer.parseInt(o.toString()));
//		  	    	  //if (layouts!=null) layouts.setFR_max_iterations(LTSLayoutWindow.FR_max_iterations);
//		    	  } catch(NumberFormatException nfe) {}
//		    	  try {
//		  	    	  Object o = radial_disty_spinner.getValue();
//		  	    	  if (o==null) return;
//		  	    	  LTSJUNGCanvas.Radial_distY = Integer.parseInt(o.toString()) < 0 ? 0 : (Integer.parseInt(o.toString()) > 1000 ? 1000 : Integer.parseInt(o.toString()));
//		  	    	  //if (layouts!=null) layouts.setFR_max_iterations(LTSLayoutWindow.FR_max_iterations);
//		    	  } catch(NumberFormatException nfe) {}
                }
            }

            JPanel buttonpanel = new JPanel();
            buttonpanel.setLayout(new GridLayout(0, 3));

            applybutton.addActionListener(new ButtonOptionListener());
            okbutton.addActionListener(new ButtonOptionListener());
            cancelbutton.addActionListener(new ButtonOptionListener());
            buttonpanel.add(applybutton);
            buttonpanel.add(okbutton);
            buttonpanel.add(cancelbutton);

            container.add(buttonpanel);

            f.pack();
            f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            f.setVisible(true);


        }
    }

    class WinAlphabetAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            newAlphabetWindow(window_alpha.isSelected());
        }
    }

    class WinPrintAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            newPrintWindow(window_print.isSelected());
        }
    }

    class WinDrawAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            newDrawWindow(window_draw.isSelected());
        }
    }

    class WinLayoutAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            newLayoutWindow(window_layout.isSelected());
        }
    }

    class HelpAboutAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            aboutDialog();
        }
    }

    class BlankAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            blankit();
        }
    }

    class HelpManualAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            displayManual(help_manual.isSelected());
        }
    }

    class StopAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (executer != null) {
                //executer = null;
//                executer.interrupt();
                ltsOutput.outln("\n\t-- stop requested");
                executer.stop();
                // throw new RuntimeException("Stop Requested");
//                int attempts = 0;
//                while (attempts++ < 20 && executer.isAlive()) {
//                    try {
//                        ltsOutput.outln(".");
//                        Thread.sleep(500);
//                    } catch (InterruptedException e1) {
//                        break;
//                    }
//                }
//                ltsOutput.outln("\n");
                // if (executer != null && executer.isAlive()) {
                // executer.stop();
                // menuEnable(true);
                // check_stop.setEnabled(false);
                // stopTool.setEnabled(false);
                // outln("\n\t-- process was killed");
                // executer = null;
                // } {
                // outln("\n\t-- process stopped by itself");
                // }

            }
        }
    }

    class ExecuteAction implements ActionListener {
        String runtarget;

        ExecuteAction(String s) {
            runtarget = s;
        }

        public void actionPerformed(ActionEvent e) {
            run_menu = runtarget;
            do_action(DO_execute);
        }
    }

    class LivenessAction implements ActionListener {
        String asserttarget;

        LivenessAction(String s) {
            asserttarget = s;
        }

        public void actionPerformed(ActionEvent e) {
            asserted = asserttarget;
            do_action(DO_liveness);
        }
    }

    class EditCutAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            input.cut();
        }
    }

    class EditCopyAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String pp = textIO.getTitleAt(textIO.getSelectedIndex());
            if (pp.equals("Edit"))
                input.copy();
            else if (pp.equals("Output"))
                output.copy();
            else if (pp.equals("Manual"))
                manual.copy();
            else if (pp.equals("Alphabet"))
                alphabet.copy();
            else if (pp.equals("Transitions"))
                prints.copy();
        }
    }

    class EditPasteAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            input.paste();
        }
    }

    class TargetAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String choice = (String) targetChoice.getSelectedItem();
            if (choice == null)
                return;
            run_enabled = MenuDefinition.enabled(choice);
            if (run_items != null && run_enabled != null) {
                if (run_items.length == run_enabled.length)
                    for (int i = 0; i < run_items.length; ++i)
                        run_items[i].setEnabled(run_enabled[i]);
            }
        }
    }

    // --------------------------------------------------------------------
    // undo editor stuff

    class UndoHandler implements UndoableEditListener {
        public void undoableEditHappened(UndoableEditEvent e) {
            undo.addEdit(e.getEdit());
            updateDoState();
        }
    }

    class UndoAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                undo.undo();
            } catch (CannotUndoException ignored) {
            }
            updateDoState();
        }
    }

    class RedoAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                undo.redo();
            } catch (CannotUndoException ignored) {
            }
            updateDoState();
        }
    }

    // >>> AMES: Text Search
    class EditFindAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            centre(findDialog);
            findDialog.setVisible(true);
        }
    }

    // <<< AMES

    protected void updateDoState() {
        edit_undo.setEnabled(undo.canUndo());
        undoTool.setEnabled(undo.canUndo());
        edit_redo.setEnabled(undo.canRedo());
        redoTool.setEnabled(undo.canRedo());
    }

    // ------------------------------------------------------------------------
    private int tabindex = 0;

    class OutputShow implements Runnable {
        public void run() {
            swapto(1);
        }
    }

    public void showOutput() {
        SwingUtilities.invokeLater(new OutputShow());
    }

    private void swapto(int i) {
        if (i == tabindex)
            return;
        textIO.setBackgroundAt(i, Color.green);
        if (tabindex != i && tabindex < textIO.getTabCount())
            textIO.setBackgroundAt(tabindex, Color.lightGray);
        tabindex = i;
        setEditControls(tabindex);
        textIO.setSelectedIndex(i);
    }

    class TabChange implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            int i = textIO.getSelectedIndex();
            if (i == tabindex)
                return;
            textIO.setBackgroundAt(i, Color.green);
            textIO.setBackgroundAt(tabindex, Color.lightGray);
            tabindex = i;
            setEditControls(tabindex);
        }
    }

    private void setEditControls(int tabindex) {
        boolean app = (isApplet == null);
        String pp = textIO.getTitleAt(tabindex);
        boolean b = (tabindex == 0);
        edit_cut.setEnabled(b);
        cutTool.setEnabled(b);
        edit_paste.setEnabled(b);
        pasteTool.setEnabled(b);
        file_new.setEnabled(b);
        file_example.setEnabled(b);
        file_open.setEnabled(app && b);
        file_saveAs.setEnabled(app && b);
        file_export.setEnabled(app && (b || pp.equals("Transitions")));
        newFileTool.setEnabled(b);
        openFileTool.setEnabled(app && b);
        edit_undo.setEnabled(b && undo.canUndo());
        undoTool.setEnabled(b && undo.canUndo());
        edit_redo.setEnabled(b && undo.canRedo());
        redoTool.setEnabled(b && undo.canRedo());
        if (b)
            SwingUtilities.invokeLater(new RequestFocus());
    }

    class RequestFocus implements Runnable {
        public void run() {
            input.requestFocusInWindow();
        }
    }


    // <<< AMES

    public void resetInput() {
        ltsInputString = new LTSInputString(input.getText());
    }

    // ------------------------------------------------------------------------

    private boolean compile() {
        ltsOutput.clearOutput();
        current = docompile();
        if (current == null) {
            return false;
        }
        postState(current);
        int[] current_states = current.machines.size() > 0 ? new int[current.machines.size()] : new int[]{0};
        for (int i = 0; i < current.machines.size(); i++)
            current_states[i] = 0;
        layouts.setCurrentState(current_states);
        return true;
    }

    /* AMES: promoted visibility from private to implement lts.LTSOutput */


    private CompositeState docompile() {
        resetInput();
        CompositeState cs = null;
        LTSCompiler comp = new LTSCompiler(ltsInputString, ltsOutput, currentDirectory);
        try {
            comp.compile();
            if (!parse(comp.getComposites(), comp.getProcesses(), comp.getExplorers())) {
                return null;
            }

            cs = comp.continueCompilation((String) targetChoice.getSelectedItem());

        } catch (LTSCompositionException x) {
            ltsOutput.outln("Construction of " + targetChoice.getSelectedItem() + " aborted.");
            cs = null;
            return cs;
        } catch (LTSException x) {
            cs = null;
            displayError(x, ltsInputString.getMarker());
            return cs;
        }
        return cs;
    }

    private void doparse(Hashtable cs, Hashtable ps, Hashtable ex) {
        resetInput();
        ltsOutput.clearOutput();
        LTSCompiler comp = new LTSCompiler(ltsInputString, ltsOutput, currentDirectory);
        try {
            comp.parse(cs, ps, ex);

        } catch (LTSException x) {
            cs = null;
            displayError(x, ltsInputString.getMarker());
        }
    }

    private void displayError(LTSException x, int marker) {
        if (marker > -1) {
            int lineNumber = getLineNumber(marker);

            ltsOutput.outln("ERROR line:" + lineNumber + " - " + x.getMessage());
            input.select(marker, marker + 1);
        } else {
            ltsOutput.outln("ERROR: " + x.getMessage());
        }

        swapto(textIO.indexOfTab("Output"));
    }

    private int getLineNumber(int marker) {
        int lineNumber = 1;
        String parse = ltsInputString.getSource();
        String delims = "\n";
        String[] lines = parse.split(delims);
        int search = 0;

        for (int i = 0; i < lines.length; i++) {
            search = lines[i].length() + search;
            if (marker <= search) {
                break;
            }
            lineNumber++;
        }
        return lineNumber;
    }

    // <<< AMES

    private String lastCompiled = "";

    private boolean compileIfChange() {
        String tmp = input.getText();
        if (current == null || !tmp.equals(lastCompiled)
                || !current.getName().equals(targetChoice.getSelectedItem())) {
            lastCompiled = tmp;
            return compile();
        }

        return true;
    }

    // ------------------------------------------------------------------------

    // >>> AMES: Deadlock Insensitive Analysis, multiple ce
    private void safety() {
        safety(true, false);
    }

    private void safety(boolean checkDeadlock, boolean multiCe) {
        ltsOutput.clearOutput();
        if (compileIfChange() && current != null) {
            TransitionSystemDispatcher.checkSafety(current, ltsOutput);

        }
    }

    // <<< AMES

    // ------------------------------------------------------------------------

    private void progress() {
        ltsOutput.clearOutput();
        if (compileIfChange() && current != null) {
            TransitionSystemDispatcher.checkProgress(current, ltsOutput); // sihay
            // MTS
            // deberia
            // dar
            // exception
            // , en
            // otro
            // caso,
            // analiser
            // .
            // checkprogress
            // o
            // algo
            // asi.
        }
    }

    // ------------------------------------------------------------------------

    private void liveness() {
        ltsOutput.clearOutput();
        compileIfChange();
        long initialTime = System.currentTimeMillis();
        ltsOutput.outln("Checking that "+ current.name +" satisfies FLTL property " + asserted);
        CompositeState ltl_property = AssertDefinition.compile(ltsOutput, asserted);
        // Silent compilation for negated formula
        CompositeState not_ltl_property = AssertDefinition.compile(
                new EmptyLTSOuput(), AssertDefinition.NOT_DEF + asserted);
        if (current != null && ltl_property != null) {
            TransitionSystemDispatcher.checkFLTL(current, ltl_property,
                    not_ltl_property, this.setFair.isSelected(), ltsOutput);
            postState(current);
        }
        ltsOutput.outln("Model check took " + (System.currentTimeMillis() - initialTime) + "ms.");

    }

    // ------------------------------------------------------------------------

    private void minimiseComposition() {
        ltsOutput.clearOutput();
        compileIfChange();
        if (compileIfChange() && current != null) {
            if (current.composition == null)
                TransitionSystemDispatcher.applyComposition(current, ltsOutput);
            TransitionSystemDispatcher.minimise(current, ltsOutput);
            postState(current);
        }
    }

    // ------------------------------------------------------------------------

    private void doComposition() {
        ltsOutput.clearOutput();
        compileIfChange();
        if (current != null) {
            try {
                TransitionSystemDispatcher.applyComposition(current, ltsOutput);
                
            } catch (LTSCompositionException e) {
                return;
            }
            
            boolean isControllable = current.composition != null;
            if (!isControllable) {
                return;
                //throw new LTSException("Composition not controllable.");
                
            }
            
            postState(current);
            int[] current_states = new int[current.machines.size() + 1];
            for (int i = 0; i < current.machines.size() + 1; i++)
                current_states[i] = 0;
            layouts.setCurrentState(current_states);
        }
    }


    // ------------------------------------------------------------------------
    private boolean checkReplay(Animator a) {
        if (a.hasErrorTrace()) {
            int result = JOptionPane.showConfirmDialog(this,
                    "Do you want to replay the error trace?");
            if (result == JOptionPane.YES_OPTION) {
                return true;
            } else if (result == JOptionPane.NO_OPTION)
                return false;
            else if (result == JOptionPane.CANCEL_OPTION)
                return false;
        }
        return false;
    }

    private void exploration_new() {
        // Definition
        String choice = (String) targetChoice.getSelectedItem();
        if (explorerDefinitions == null || !explorerDefinitions.containsKey(choice)) {
            ltsOutput.outln(choice + " is not a valid explorer");
            return;
        }
        ExplorerDefinition explorerDefinition = explorerDefinitions.get(choice);

        // View
        CompactState[] components = new CompactState[explorerDefinition.getView().size()];
        for (int i = 0; i < explorerDefinition.getView().size(); i++)
            for (int j = 0; j < current.machines.size(); j++)
                if (explorerDefinition.getView().get(i).getName().equals(current.machines.elementAt(j).getName()))
                    components[i] = current.machines.elementAt(j);

        List<List<Symbol>> environmentActions = explorerDefinition.getEnvironmentActions();
        ViewNextConfiguration[] view_configurations = new ViewNextConfiguration[current.machines.size() - 1];
        for (int i = 1; i < components.length; i++) {
            if (environmentActions != null && environmentActions.size() > i - 1) {
                String[] trace = new String[environmentActions.get(i - 1).size()];
                for (int j = 0; j < trace.length; j++)
                    trace[j] = environmentActions.get(i - 1).get(j).toString();
                view_configurations[i] = new ViewNextConfigurationTrace(trace);
            } else
                view_configurations[i] = new ViewNextConfigurationRandom();
        }

        View view = new View(components, view_configurations);

        // Model
        CompactState[] knowledge_configurations = new CompactState[explorerDefinition.getModel().size()];
        for (int i = 0; i < explorerDefinition.getView().size(); i++)
            for (int j = 0; j < current.machines.size(); j++)
                if (explorerDefinition.getModel().get(i).getName().equals(current.machines.elementAt(j).getName()))
                    knowledge_configurations[i] = current.machines.elementAt(j);

        CompactState[] model_configurations = new CompactState[knowledge_configurations.length];
        for (int i = 0; i < model_configurations.length; i++)
            model_configurations[i] = knowledge_configurations[i].myclone();

        Model model = new Model(model_configurations);

        // Knowledge
        Knowledge knowledge = new Knowledge(knowledge_configurations);

        // Goal
        ControllerGoal<String> goal = current.goal;
        HashSet<String> controlableActions = new HashSet<>(0);
        for (String anAction : goal.getControllableActions())
            if (!anAction.contains("["))
                controlableActions.add(anAction);
        goal.setControllableActions(controlableActions);

        // Strategy manager
        Strategy[] strategies = new Strategy[1];
        strategies[0] = new StrategySynthesisNewAction(new StrategySynthesis(knowledge, goal.copy()), new StrategyNewAction(knowledge, goal.copy()));
        //strategies[0] = new StrategyNewAction(knowledge, goal.copy());
        StrategyManager strategyManager = new StrategyManager(strategies);

        // Explorer
        this.explorer = new Explorer(view, model, knowledge, goal, strategyManager);
        String action = "      ";
        String state = String.valueOf(knowledge.getCurrentStates()[0]);
        while (state.length() < 3)
            state = " " + state;
        ltsOutput.outln(action + "  ->  " + state);

        current.composition = null;
        current.makeController = false;
        postState(current);
        draws.setCurrentState(this.explorer.getCurrentStateNumbers());
        layouts.setCurrentState(this.explorer.getCurrentStateNumbers());
    }

    private void exploration_manual() {
        if (this.explorer == null)
            throw new UnsupportedOperationException("Primero hay que explorar");

        String mtsControlProblemAnswer = this.explorer.getMTSControlProblemAnswer();

        if (mtsControlProblemAnswer.equals("ALL")) {
            ltsOutput.outln("All implementations can be controlled");
            return;
        }

        if (mtsControlProblemAnswer.equals("NONE")) {
            ltsOutput.outln("There is no controller for model for the given setting");
            return;
        }

        String[] aviableActions = this.explorer.getAviableActions();
        int choice = JOptionPane.showOptionDialog(null, "Choose the next action", "Next action", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, aviableActions, aviableActions[0]);

        if (choice == -1)
            return;

        this.explorer.explore(aviableActions[choice]);

        exploration_output();
    }

    private String exploration_stepover() {
        if (this.explorer == null)
            throw new UnsupportedOperationException("Primero hay que explorar");

        String mtsControlProblemAnswer = "";
        Integer steps_count = getStepsCount();
        for (int i = 0; i < steps_count; i++) {
            mtsControlProblemAnswer = this.explorer.getMTSControlProblemAnswer();

            if (mtsControlProblemAnswer.equals("ALL")) {
                ltsOutput.outln("All implementations can be controlled");
                break;
            }

            if (mtsControlProblemAnswer.equals("NONE")) {
                ltsOutput.outln("There is no controller for model for the given setting");
                break;
            }

            if (mtsControlProblemAnswer.equals("RESET"))
                explorer.reset();

            if (mtsControlProblemAnswer.equals("SOME"))
                this.explorer.explore();

            exploration_output();
        }

        return mtsControlProblemAnswer;
    }

    private void exploration_resume() {
        String mtsControlProblemAnswer = exploration_stepover();
        while (mtsControlProblemAnswer.equals("SOME") || mtsControlProblemAnswer.equals("RESET"))
            mtsControlProblemAnswer = exploration_stepover();
    }

    private void exploration_output() {
        ArrayList<String> traceStates = this.explorer.getTraceLastStates();
        ArrayList<String> traceActions = this.explorer.getTraceLastActions();

        for (int j = 0; j < traceStates.size(); j++) {
            String action = traceActions.get(j);

            while (action.length() < 6)
                action = action + " ";

            String state = traceStates.get(j);
            while (state.length() < 3)
                state = " " + state;

            if (Objects.equals(state.trim(), ""))
                ltsOutput.outln(action);
            else
                ltsOutput.outln(action + "  ->  " + state);
        }

        postState(current);
        draws.setCurrentState(this.explorer.getCurrentStateNumbers());
        layouts.setCurrentState(this.explorer.getCurrentStateNumbers());
    }

    private Integer getStepsCount() {
        Integer count_integer = 1;
        try {
            String count_string = this.stepscount.getText();
            count_integer = Integer.parseInt(count_string);
        } catch (NumberFormatException ignored) {

        }
        if (count_integer < 1)
            count_integer = 1;
        return count_integer;
    }

    private void animate() {
        ltsOutput.clearOutput();
        compileIfChange();
        boolean replay = false;
        if (current != null) {
            if (current instanceof UpdatingControllerCompositeState && current.getComposition() == null) {
                throw new UnsupportedOperationException("Please, do parallel composition before animate");
            }
            if (current.machines.size() > 1 && MTSUtils.isMTSRepresentation(current)) {
                throw new UnsupportedOperationException("Animation for more than one MTS is not developed yet");
            }

            CompositeState animatedCurrent = current;
            //postState(animatedCurrent);

            Animator anim = TransitionSystemDispatcher.generateAnimator(
                    animatedCurrent, ltsOutput, eman);

            RunMenu r = null;
            if (!(anim instanceof MTSAnimator)) {
                replay = checkReplay(anim);
                if (animator != null) {
                    animator.dispose();
                    animator = null;
                }
                if (RunMenu.menus != null)
                    r = (RunMenu) RunMenu.menus.get(run_menu);
            }
            if (r != null && r.isCustom()) {
                animator = createCustom(anim, r.params, r.actions, r.controls, replay);
            } else {

                animator = new AnimArrangedWindow(anim, r, setAutoRun.getState(), replay);
            }
        }
        if (animator != null) {
            animator.pack();
            left(animator);
            animator.setVisible(true);
        }
    }

    private Frame createCustom(Animator anim, String params, Relation actions,
                               Relation controls, boolean replay) {
        CustomAnimator window = null;
        try {
            window = new SceneAnimator();
            File f;
            if (params != null)
                f = new File(currentDirectory, params);
            else
                f = null;
            window.init(anim, f, actions, controls, replay);
            return window;
        } catch (Exception e) {
            ltsOutput.outln("** Failed to create instance of Scene Animator" + e);
            return null;
        }
    }

    // ------------------------------------------------------------------------

    private void reachable() {
        ltsOutput.clearOutput();
        compileIfChange();
        if (current != null && current.machines.size() > 0) {
            Analyser a = new Analyser(current, ltsOutput, null);
            SuperTrace s = new SuperTrace(a, ltsOutput);
            current.setErrorTrace(s.getErrorTrace());
        }
    }

    // ------------------------------------------------------------------------

    private void newDrawWindow(boolean disp) {
        if (disp && textIO.indexOfTab("Draw") < 0) {
            // create Text window
            draws = new LTSDrawWindow(current, eman);
            textIO.addTab("Draw", draws);
            swapto(textIO.indexOfTab("Draw"));
        } else if (!disp && textIO.indexOfTab("Draw") > 0) {
            swapto(0);
            textIO.removeTabAt(textIO.indexOfTab("Draw"));
            draws.removeClient();
            draws = null;
        }
    }

    // ------------------------------------------------------------------------

    private void newLayoutWindow(boolean disp) {
        if (disp && textIO.indexOfTab("Layout") < 0) {
            // create Text window
            layouts = new LTSLayoutWindow(current, eman);
            textIO.addTab("Layout", layouts);
            swapto(textIO.indexOfTab("Layout"));
        } else if (!disp && textIO.indexOfTab("Layout") > 0) {
            swapto(0);
            textIO.removeTabAt(textIO.indexOfTab("Layout"));
            layouts.removeClient();
            layouts = null;
        }
    }

    private void newPrintWindow(boolean disp) {
        if (disp && textIO.indexOfTab("Transitions") < 0) {
            // create Text window
            prints = new PrintWindow(current, eman);
            textIO.addTab("Transitions", prints);
            swapto(textIO.indexOfTab("Transitions"));
        } else if (!disp && textIO.indexOfTab("Transitions") > 0) {
            swapto(0);
            textIO.removeTabAt(textIO.indexOfTab("Transitions"));
            prints.removeClient();
            prints = null;
        }
    }

    // ------------------------------------------------------------------------

    private void newAlphabetWindow(boolean disp) {
        if (disp && textIO.indexOfTab("Alphabet") < 0) {
            // create Alphabet window
            alphabet = new AlphabetWindow(current, eman);
            textIO.addTab("Alphabet", alphabet);
            swapto(textIO.indexOfTab("Alphabet"));
        } else if (!disp && textIO.indexOfTab("Alphabet") > 0) {
            swapto(0);
            textIO.removeTabAt(textIO.indexOfTab("Alphabet"));
            alphabet.removeClient();
            alphabet = null;
        }
    }

    // ------------------------------------------------------------------------

    private void aboutDialog() {
        LTSASplash d = new LTSASplash(this);
        d.setVisible(true);
    }

    // ------------------------------------------------------------------------

    private void blankit() {
        LTSABlanker d = new LTSABlanker(this);
        d.setVisible(true);
    }

    // ------------------------------------------------------------------------

    private void setSuperTraceOption() {
        try {
            String o = (String) JOptionPane.showInputDialog(this,
                    "Enter Hashtable size (Kilobytes):",
                    "Supertrace parameters", JOptionPane.PLAIN_MESSAGE, null,
                    null, "" + SuperTrace.getHashSize());
            if (o == null)
                return;
            SuperTrace.setHashSize(Integer.parseInt(o));
            o = (String) JOptionPane.showInputDialog(this,
                    "Enter bound for search depth size:",
                    "Supertrace parameters", JOptionPane.PLAIN_MESSAGE, null,
                    null, "" + SuperTrace.getDepthBound());
            if (o == null)
                return;
            SuperTrace.setDepthBound(Integer.parseInt(o));
        } catch (NumberFormatException ignored) {
        }
    }

    // -------------------------------------------------------------------------

    private void doOptimist() {
        if (compileIfChange() && current != null) {
            TransitionSystemDispatcher.makeOptimisticModel(current, ltsOutput);
            postState(current);
        }
    }

    private void doPesimist() {
        if (compileIfChange() && current != null) {
            TransitionSystemDispatcher.makePessimisticModel(current, ltsOutput);
            postState(current);
        }
    }

    private void doApplyPlusCROperator() {
        if (compileIfChange() && current != null) {
            TransitionSystemDispatcher.applyPlusCROperator(current, ltsOutput);
            postState(current);
        }
    }

    private void doApplyPlusCAOperator() {
        if (compileIfChange() && current != null) {
            TransitionSystemDispatcher.applyPlusCAOperator(current, ltsOutput);
            postState(current);
        }
    }

    private void doDeterminise() {
        if (compileIfChange() && current != null) {
            TransitionSystemDispatcher.determinise(current, ltsOutput);
            postState(current);
        }
    }

    private void doDeadlockCheck() {
        if (compileIfChange() && current != null) {
            TransitionSystemDispatcher.hasCompositionDeadlockFreeImplementations(current, ltsOutput);
        }
    }

    private void doRefinement() {
        if (compileIfChange() && current != null) {
            RefinementOptions refinementOptions = new RefinementOptions();
            String[] models = getMachinesNames();

            final RefinementWindow refinementWindow = new RefinementWindow(
                    this, refinementOptions, models, SemanticType.values(),
                    RefinementWindow.Mode.REFINEMENT);
            refinementWindow.setLocation(this.getX() + 100, this.getY() + 100);
            refinementWindow.pack();
            refinementWindow.setVisible(true);
            if (refinementOptions.isValid()) {
                // temporal: translate to probabilistic.

                CompactState refines = selectMachine(refinementOptions.getRefinesModel());
                CompactState refined = selectMachine(refinementOptions.getRefinedModel());
                boolean isThereAProb = false;
                if (CompactStateUtils.isProbabilisticMachine(refines)) {
                    refines = CompactStateUtils.convertToNonProbabilistic(refines);
                    isThereAProb = true;
                }
                if (CompactStateUtils.isProbabilisticMachine(refined)) {
                    refined = CompactStateUtils.convertToNonProbabilistic(refined);
                    isThereAProb = true;
                }
                if (isThereAProb) {
                    ltsOutput.outln("One of the models is probabilistic. Nondeterministic-based Bisimulation will be applied.");
                }

                boolean areRefinement = TransitionSystemDispatcher.isRefinement(refines, refined,
                        refinementOptions.getRefinementSemantic(), ltsOutput);
                if (refinementOptions.isBidirectionalCheck()) {
                    areRefinement &= TransitionSystemDispatcher.isRefinement(refined, refines,
                            refinementOptions.getRefinementSemantic(), ltsOutput);
                    if (areRefinement) {
                        ltsOutput.outln(" ");
                        ltsOutput.outln("Models equivalent by simulation.");
                    }
                }
            }

            postState(current);
        }
    }
    
    private void doBisimulation() {
        if (compileIfChange() && current != null) {
            RefinementOptions refinementOptions = new RefinementOptions();
            String[] models = getMachinesNames();
            
            SemanticType[] semanticTypes = {SemanticType.STRONG, SemanticType.WEAK};

            final BisimulationWindow refinementWindow = new BisimulationWindow(
                this, refinementOptions, models,  semanticTypes
            );
            refinementWindow.setLocation(this.getX() + 100, this.getY() + 100);
            refinementWindow.pack();
            refinementWindow.setVisible(true);
            if (refinementOptions.isValid()) {
                // temporal: translate to probabilistic.

                CompactState refines = selectMachine(refinementOptions.getRefinesModel());
                CompactState refined = selectMachine(refinementOptions.getRefinedModel());
                boolean isThereAProb = false;
                if (CompactStateUtils.isProbabilisticMachine(refines)) {
                    refines = CompactStateUtils.convertToNonProbabilistic(refines);
                    isThereAProb = true;
                }
                if (CompactStateUtils.isProbabilisticMachine(refined)) {
                    refined = CompactStateUtils.convertToNonProbabilistic(refined);
                    isThereAProb = true;
                }

                showOutput();

                if (isThereAProb) {
                    ltsOutput.outln("One of the models is probabilistic. Nondeterministic-based bisimulation will be applied.");
                }

                if (!(new HashSet<String>(Arrays.asList(refines.getAlphabet())).equals(new HashSet<String>(Arrays.asList(refined.getAlphabet()))))) {
                    ltsOutput.outln("Models " + refines.getName() + " and " + refined.getName() + " have different alphabet. Cannot proceed with bisimulation check.");
               } else {
                   ltsOutput.outln(" ");
                   BinaryRelation<?, ?> refRel = TransitionSystemDispatcher.getRefinement(refines, refined,
                           refinementOptions.getRefinementSemantic(), ltsOutput);

                   if (refRel != null) {
                       ltsOutput.outln("Models " + refines.getName() + " and " + refined.getName() + " are (" + refinementOptions.getRefinementSemantic().toString() + ") bisimilar.");
                       ltsOutput.out("Bisimulation relation:");
                       ltsOutput.outln(refRel.toString());
                   } else {
                       ltsOutput.outln("Models " + refines.getName() + " and " + refined.getName() + " are not bisimilar.");
                   }
               }
            }

            postState(current);
        }
    }

    private void doLegality() {
        if (compileIfChange() && current != null) {
            LegalityOptions legalityOptions = new LegalityOptions(current.machines, labelSetConstants);
            final LegalityWindow legalityWindow = new LegalityWindow(this, legalityOptions);
            legalityWindow.setLocation(this.getX() + 100, this.getY() + 100);
            legalityWindow.pack();
            legalityWindow.setVisible(true);
            if (legalityOptions.isValid()) {
            	LabelSet actionSet = labelSetConstants.get(legalityOptions.actionSet);

                showOutput();
                ltsOutput.outln("==== Legality check ====");
                List<String> actions = (List<String>) actionSet.actions;

                LegalityAnalyser a = new LegalityAnalyser(current, ltsOutput, legalityOptions.source, legalityOptions.target, actions);
                a.composeNoHide();

                String resultMessage;
                if (a.isLegal()) 
                	resultMessage = String.format("Result: Yes, %s is legal w.r.t %s with controlled actions %s",
                		current.machines.get(legalityOptions.source).name,
                    	current.machines.get(legalityOptions.target).name,
                    	legalityOptions.actionSet);
                else 
                	resultMessage = String.format("Result: No, %s is not legal w.r.t %s with controlled actions %s",
                			current.machines.get(legalityOptions.source).name,
                        	current.machines.get(legalityOptions.target).name,
                        	legalityOptions.actionSet);

                ltsOutput.outln(resultMessage);
                ltsOutput.outln("==== Legality check finished ====");
            }
        }
    }

    private void doConsistency() {
        if (compileIfChange() && current != null) {
            RefinementOptions refinementOptions = new RefinementOptions();
            String[] models = getMachinesNames();

            final RefinementWindow refinementWindow = new RefinementWindow(
                    this, refinementOptions, models, SemanticType.values(),
                    RefinementWindow.Mode.CONSISTENCY);
            refinementWindow.setLocation(this.getX() + 100, this.getY() + 100);
            refinementWindow.pack();
            refinementWindow.setVisible(true);
            if (refinementOptions.isValid()) {
                ltsOutput.outln("Checking Consistency");
                TransitionSystemDispatcher.areConsistent(
                        selectMachine(refinementOptions.getRefinesModel()),
                        selectMachine(refinementOptions.getRefinedModel()),
                        refinementOptions.getRefinementSemantic(), ltsOutput);
            }

            postState(current);
        }
    }

    private void doRunEnactors() {

        if (current == null || current.composition == null
                || current.goal == null) {
            ltsOutput.outln("There is no composite state or goal available");
            return;
        }

        this.setVisible(false);
        if (this.enactmentSimulation != null)
            this.enactmentSimulation.runSimulation(current, getApplicationContext(), enactmentOptions);

    }

    private void doEnactorOptions() {
        final EnactorOptionsWindows enactorOptionsWindow = new EnactorOptionsWindows(
                enactmentOptions, this);

        if (this.enactmentOptions.scheduler != null
                && !this.enactmentOptions.scheduler.isEmpty())
            enactorOptionsWindow.getCbSchedulers().setSelectedItem(
                    enactmentOptions.scheduler);

        if (this.enactmentOptions.enactors != null) {
            List<Integer> selected = new ArrayList<Integer>();
            for (String enactor : this.enactmentOptions.enactors) {
                for (int i = 0; i < enactorOptionsWindow.getEnactorList()
                        .getModel().getSize(); i++)
                    if (enactor.equals(enactorOptionsWindow.getEnactorList()
                            .getModel().getElementAt(i)))
                        selected.add(i);
            }
            int[] selectIndices = new int[selected.size()];
            for (int i = 0; i < selected.size(); i++)
                selectIndices[i] = selected.get(i).intValue();

            enactorOptionsWindow.getEnactorList().setSelectedIndices(
                    selectIndices);

        }

        enactorOptionsWindow.getOkButton().addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {

                        enactmentOptions.scheduler = (String) enactorOptionsWindow
                                .getCbSchedulers().getSelectedItem();
                        int[] selectedIndices = enactorOptionsWindow
                                .getEnactorList().getSelectedIndices();
                        enactmentOptions.enactors.clear();
                        for (int i = 0; i < selectedIndices.length; i++) {
                            enactmentOptions.enactors.add(enactorOptionsWindow
                                    .getEnactorList().getModel()
                                    .getElementAt(selectedIndices[i])
                                    .toString());
                        }
                        enactorOptionsWindow.setVisible(false);
                    }
                });
        enactorOptionsWindow.setVisible(true);

    }

    private String[] getMachinesNames() {
        CompactState composition = current.composition;
        int size = current.machines.size();
        if (composition != null) {
            size++;
        }
        String[] result = new String[size];

        int i = 0;
        for (Iterator it = current.machines.iterator(); it.hasNext(); i++) {
            CompactState compactState = (CompactState) it.next();
            result[i] = compactState.name;
        }
        if (composition != null) {
            result[i] = composition.name;
        }
        return result;
    }

    private CompactState selectMachine(int machineIndex) {
        CompactState result = null;
        Vector machines = current.machines;
        if (machineIndex < machines.size()) {
            return (CompactState) machines.get(machineIndex);
        } else {
            return current.composition;
        }
    }

    // ------------------------------------------------------------------------

    private boolean parse() {
        return parse(null, null, null);
    }

    /* AMES: promoted visibility from private to implement lts.LTSManager */
    public boolean parse(Hashtable cs, Hashtable ps, Hashtable ex) {

//TODO: separate methods when parsing empty or non valid file
        String oldChoice = (String) targetChoice.getSelectedItem();

        if (cs == null && ps == null) {
            cs = new Hashtable();
            ps = new Hashtable();
            ex = new Hashtable();
            doparse(cs, ps, ex);
        }

        if (cs == null)
            return false;
        targetChoice.removeAllItems();

        if (ex.size() == 0) {
            if (cs.size() == 0) {
                targetChoice.addItem(DEFAULT);
            } else {
                Enumeration e = cs.keys();
                java.util.List forSort = new ArrayList();
                while (e.hasMoreElements()) {
                    forSort.add(e.nextElement());
                }
                Collections.sort(forSort);
                for (Object aForSort : forSort) {
                    targetChoice.addItem(aForSort);
                }
            }
        } else {
            Enumeration e = ex.keys();
            java.util.List forSort = new ArrayList();
            while (e.hasMoreElements()) {
                forSort.add(e.nextElement());
            }
            Collections.sort(forSort);
            for (Object aForSort : forSort) {
                targetChoice.addItem(aForSort);
            }
        }

        if (oldChoice != null) {
            if (!oldChoice.equals(DEFAULT) && (ex.containsKey(oldChoice) || (ex.size() == 0 && cs.containsKey(oldChoice))))
                targetChoice.setSelectedItem(oldChoice);
        }
        current = null;
        explorer = null;
        explorerDefinitions = ex;

        // >>> AMES: Enhanced Modularity
        eman.post(new LTSEvent(LTSEvent.NEWCOMPOSITES, cs.keySet()));
        eman.post(new LTSEvent(LTSEvent.NEWPROCESSES, ps.keySet()));
        eman.post(new LTSEvent(LTSEvent.NEWLABELSETS,
                (labelSetConstants = LabelSet.getConstants()).keySet()));
        // <<< AMES

        // deal with run menu
        check_run.removeAll();
        run_names = MenuDefinition.names();
        run_enabled = MenuDefinition.enabled((String) targetChoice
                .getSelectedItem());
        check_run.add(default_run);
        if (run_names != null) {
            run_items = new JMenuItem[run_names.length];
            for (int i = 0; i < run_names.length; ++i) {
                run_items[i] = new JMenuItem(run_names[i]);
                run_items[i].setEnabled(run_enabled[i]);
                run_items[i].addActionListener(new ExecuteAction(run_names[i]));
                check_run.add(run_items[i]);
            }
        }
        // deal with assert menu
        check_liveness.removeAll();
        assert_names = AssertDefinition.names();
        if (assert_names != null) {
            assert_items = new JMenuItem[assert_names.length];
            for (int i = 0; i < assert_names.length; ++i) {
                assert_items[i] = new JMenuItem(assert_names[i]);
                assert_items[i].addActionListener(new LivenessAction(
                        assert_names[i]));
                check_liveness.add(assert_items[i]);
            }
        }
        // validate(); //seems to cause deadlock in GUI
        return true;
    }

    // ------------------------------------------------------------------------

    private void displayManual(boolean dispman) {
        if (dispman && textIO.indexOfTab("Manual") < 0) {
            // create Manual window
            manual = new JEditorPane();
            manual.setEditable(false);
            manual.addHyperlinkListener(new Hyperactive());
            JScrollPane mm = new JScrollPane(manual,
                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            textIO.addTab("Manual", mm);
            swapto(textIO.indexOfTab("Manual"));
            URL man = this.getClass().getClassLoader().getResource("doc/User-manual.html");
            try {
                manual.setPage(man);
                // outln("URL: "+man);
            } catch (java.io.IOException e) {
                ltsOutput.outln("" + e);
            }
        } else if (!dispman && textIO.indexOfTab("Manual") > 0) {
            swapto(0);
            textIO.removeTabAt(textIO.indexOfTab("Manual"));
            manual = null;
        }
    }

    class Hyperactive implements HyperlinkListener {

        public void hyperlinkUpdate(HyperlinkEvent e) {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                JEditorPane pane = (JEditorPane) e.getSource();
                try {
                    java.net.URL u = e.getURL();
                    // outln("URL: "+u);
                    if (u.toString().startsWith("http")) {
                    	Desktop.getDesktop().browse(u.toURI());
                    } else
                    	pane.setPage(u);
                } catch (Throwable t) {
                    ltsOutput.outln("" + e);
                }
            }
        }
    }

    // ------------------------------------------------------------------------

    public static void main(String[] args) {

        String lf = UIManager.getSystemLookAndFeelClassName();
        try {
            UIManager.setLookAndFeel(lf);
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }

        HPWindow window = new HPWindow(null);
        window.setTitle("MTS Analyser");
        window.pack();
        HPWindow.centre(window);
        window.setVisible(true);
        if (args.length > 0) {
            SwingUtilities.invokeLater(new ScheduleOpenFile(window, args[0]));
        } else {
            window.currentDirectory = System.getProperty("user.home");
        }
    }

    static class ScheduleOpenFile implements Runnable {
        HPWindow window;
        String arg;

        ScheduleOpenFile(HPWindow window, String arg) {
            this.window = window;
            this.arg = arg;
        }

        public void run() {
            window.doOpenFile("", arg, false);
        }
    }

    private boolean hasLTL2BuchiJar() {
        try {
            new gov.nasa.ltl.graph.Graph();
            return true;
        } catch (NoClassDefFoundError e) {
            return false; //no exception thrown
        }
    }

    // >>> AMES: Enhanced Modularity

    public CompositeState compile(String name) {
        resetInput();
        CompositeState cs = null;
        LTSCompiler comp = new LTSCompiler(ltsInputString, ltsOutput, currentDirectory);
        try {
            comp.compile();
            cs = comp.continueCompilation(name);
        } catch (LTSException x) {
            displayError(x, ltsInputString.getMarker());
            return cs;
        }
        return cs;
    }

    /**
     * Returns the currently selected item from the targets selection box.
     */
    public String getTargetChoice() {
        return (String) targetChoice.getSelectedItem();
    }

    /**
     * Updates the various display windows and animators with the given
     * machines.
     */
    public void newMachines(java.util.List<CompactState> machines) {
        CompositeState c = new CompositeState(
                new Vector<>(machines));
        postState(c);
        this.current = c;
    }

    /**
     * Returns the set of actions which correspond to the label set definition
     * with the given name.
     */
    public Set<String> getLabelSet(String name) {
        if (labelSetConstants == null)
            return null;

        Set<String> s = new HashSet<>();
        LabelSet ls = labelSetConstants.get(name);

        if (ls == null)
            return null;

        for (String a : ls.getActions(null))
            s.add(a);

        return s;
    }


    private void fillEnactorsMenu(JMenuItem parent_menu) {
        EnactorFactory<Long, String> enactorFactory = applicationContext
                .getBean(EnactorFactory.class);
        for (String enactorName : enactorFactory.getEnactorNames()) {
            JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(enactorName);
            menuItem.setSelected(false);
            parent_menu.add(menuItem);

        }

    }

    /**
     * Returns the instantiated Spring Application Context
     *
     * @return the Application Context
     */
    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    //TODO move this class to a file. refactor needed to organize the menu related code.
    class GraphUpdateMenu extends JMenu {
        JMenuItem startMenu = new JMenuItem("Begin Simulation");

        public GraphUpdateMenu() {
            super("Update");
            startMenu.addActionListener(new DoAction(DO_GRAPH_UPDATE));
            this.add(startMenu);
        }
    }


    protected JButton createTool(String icon, String tip, ActionListener act) {
        verifyFilePath(icon);

        JButton b = new JButton();
        try {
            URL url = getClass().getClassLoader().getResource(icon);
            Image img = ImageIO.read(url);
            b.setIcon(new ImageIcon(img));
            b.setAlignmentY(0.5f);
        } catch (Exception ex) {
            ltsOutput.outln("Cannot load icon: " + icon + ex.getMessage());
        }
        b.setRequestFocusEnabled(false);
        b.setMargin(new Insets(0, 0, 0, 0));
        b.setToolTipText(tip);
        b.addActionListener(act);
        return b;
    }

    public void verifyFilePath(String icon) {
        try {
            new File(icon).toURI().toURL();
        } catch (MalformedURLException e) {
            ltsOutput.outln("**** Malformed icon file: " + e);
        }
    }

    //--------------------------------------------------------------------
    public void setBigFont(boolean b) {
        fontFlag = b;
        if (fontFlag) {
            output.setFont(f2);
            prints.list.setFont(f4);
            alphabet.list.setFont(f4);
        } else {
            output.setFont(f1);
            prints.list.setFont(f3);
            alphabet.list.setFont(f3);
        }
    }

//--------------------------------------------------------------------

}
