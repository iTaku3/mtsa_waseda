package ltsa.ui;

import ltsa.jung.LTSGraph;
import ltsa.jung.LTSJUNGCanvas;
import ltsa.jung.LTSJUNGCanvas.EnumLayout;
import ltsa.jung.LTSJUNGCanvas.EnumMode;
import ltsa.jung.StateVertex;
import ltsa.jung.TransitionEdge;
import ltsa.lts.*;
import org.freehep.graphics2d.VectorGraphics;
import org.freehep.graphicsio.pdf.PDFGraphics2D;
import org.freehep.graphicsio.ps.PSGraphics2D;
import org.freehep.graphicsio.svg.SVGGraphics2D;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * The layout window to display machines according to layout algorithms
 *
 * @author Cédric Delforge
 */
@SuppressWarnings("serial")
public class LTSLayoutWindow extends JSplitPane implements EventClient {
    LTSJUNGCanvas output; //the panel where all the machines are drawn

    EnumLayout layout = EnumLayout.FruchtermanReingold; //current layout selected
    EventManager eman; //the event manager sending events to renew the canvas
    CompositeState cs; //the list of LTS to draw
    int[] lastEvent, prevEvent; //last event received, event before that one
    //used for trace animation
    String lastName; //name of the last event
    int Nmach = 0;  //the number of machines
    int hasC = 0;   //1 if last machine is composition
    CompactState[] sm; //an array of machines

    LTSGraph[] graphs; //array of graphs corresponding to a lts
    boolean[] graphValidity; //true for a graph index if it has been generated already and is still valid

    boolean[] machineHasAction;
    boolean[] machineToDrawSet; //true or false for each machine depending on whether
    //it is part of the multiple LTS composition or not
    Map<Integer, EnumLayout> machineLayout; //maps a machine to the layout it was drawn as

    //default values
    public static boolean fontFlag = false;
    public static boolean singleMode = true;
    public static int stateLimit = 0;

    JList list; //machine list

    Font f1 = new Font("Monospaced", Font.PLAIN, 12);
    Font f2 = new Font("Monospaced", Font.BOLD, 16);
    Font f3 = new Font("SansSerif", Font.PLAIN, 12);
    Font f4 = new Font("SansSerif", Font.BOLD, 16);

    ImageIcon drawIcon;

    public LTSLayoutWindow(CompositeState cs, EventManager eman) {

        super();
        drawIcon = getDrawIcon();

        this.eman = eman;
        if (eman != null) {
            eman.addClient(this);
        }
        output = new LTSJUNGCanvas();

        JScrollPane left;
        //scrollable list pane
        list = new JList();
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(new DrawAction());
        list.setCellRenderer(new MyCellRenderer());
        left = new JScrollPane(list,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        JPanel canvasTools = new JPanel(new BorderLayout());
        canvasTools.add("Center", output);

        ArrayList<EnumLayout> layoutTypes = new ArrayList<EnumLayout>();
        for (EnumLayout l : EnumLayout.values()) {
            if (l != EnumLayout.Aggregate)
                layoutTypes.add(l);
        }
        JComboBox layoutTypeComboBox = new JComboBox(layoutTypes.toArray(new EnumLayout[layoutTypes.size()]));
        layoutTypeComboBox.setSelectedItem(layout);
        layoutTypeComboBox.addActionListener(new LayoutPickAction());

        JCheckBox sccCheckBox = new JCheckBox("Color SCC");
        sccCheckBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                output.colorSCC(e.getStateChange() == ItemEvent.SELECTED);
            }
        });

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new RefreshAction());

        JButton stopButton = new JButton("Stop");
        stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                output.stop();
            }
        });
        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                output.clear();
                machineLayout.clear();
                list.clearSelection();
            }
        });
        JCheckBox navigateCheckBox = new JCheckBox("Navigate");
        navigateCheckBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    output.setInteraction(EnumMode.Activate);
                } else {
                    output.setInteraction(EnumMode.Edit);
                }
            }
        });

        JButton reachingButton = new JButton("<<");
        reachingButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                output.reaching();
            }
        });
        JButton previousButton = new JButton("<");
        previousButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                output.previous();
            }
        });
        JButton nextButton = new JButton(">");
        nextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                output.next();
            }
        });
        JButton reachableButton = new JButton(">>");
        reachableButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                output.reachable();
            }
        });
        JButton bZoomIn = new JButton(" + ");
        bZoomIn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                output.zoomIn();
            }
        });
        JButton bZoomOut = new JButton(" - ");
        bZoomOut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                output.zoomOut();
            }
        });
        JButton saveButton = new JButton(">Mem");
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                output.savePositions();
            }
        });
        JButton loadButton = new JButton("<Mem");
        loadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                output.loadPositions();
            }
        });

        JToolBar layoutControls = new JToolBar();
        layoutControls.setOrientation(JToolBar.HORIZONTAL);
        layoutControls.add(layoutTypeComboBox);
        layoutControls.add(bZoomIn);
        layoutControls.add(bZoomOut);
        layoutControls.add(sccCheckBox);
        layoutControls.addSeparator();
        layoutControls.add(refreshButton);
        layoutControls.add(stopButton);
        layoutControls.add(clearButton);
        layoutControls.addSeparator();
        layoutControls.add(navigateCheckBox);
        layoutControls.addSeparator();
        layoutControls.add(reachingButton);
        layoutControls.add(previousButton);
        layoutControls.add(nextButton);
        layoutControls.add(reachableButton);
        layoutControls.addSeparator();
        layoutControls.add(saveButton);
        layoutControls.add(loadButton);

        layoutControls.setFloatable(false);
        canvasTools.add("North", layoutControls);

        setLeftComponent(left);
        setRightComponent(canvasTools);
        setDividerLocation(200);
        setBigFont(fontFlag);
        validate();

        new_machines(cs);

    }

    private ImageIcon getDrawIcon() {
        String icon = "icon/draw.gif";
        HPWindow.instance.verifyFilePath(icon);
        return new ImageIcon(getClass().getClassLoader().getResource(icon));
    }

    public void setCurrentState(int[] currentStateNumbers) {
        this.prevEvent = Arrays.copyOf(currentStateNumbers, currentStateNumbers.length + 2);
        this.lastEvent = Arrays.copyOf(currentStateNumbers, currentStateNumbers.length + 2);

        this.prevEvent[prevEvent.length - 2] = currentStateNumbers[0];
        this.lastEvent[lastEvent.length - 2] = currentStateNumbers[0];

        this.prevEvent[prevEvent.length - 1] = 0;
        this.lastEvent[lastEvent.length - 1] = 0;

        this.lastName = "";
    }

//-----------------------------------------------------------------------------
// Listener classes
//-----------------------------------------------------------------------------

    private class LayoutPickAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            @SuppressWarnings("unchecked")
            JComboBox cb = (JComboBox) e.getSource();
            layout = (EnumLayout) cb.getSelectedItem();
        }
    }

    private class RefreshAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            refresh();
        }
    }

    private class DrawAction implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent lse) {
            if (lse.getValueIsAdjusting()) return;
            int machine = list.getSelectedIndex();
            if (machine < 0 || machine >= Nmach) return;

            if (singleMode) {
                if (stateLimit > 0 && sm[machine].maxStates > stateLimit) {
                    int o = JOptionPane.showConfirmDialog(getParent(),
                            "The number of states of this LTS (" + sm[machine].maxStates + ") is above the set limit of " + stateLimit + ".\nDo you want to display it?",
                            "Limit reached",
                            JOptionPane.YES_NO_OPTION);
                    if (o == JOptionPane.NO_OPTION) {
                        return;
                    }
                }
                output.clear();

                LTSGraph graph = makeGraph(machine);

                Set<StateVertex> currentVertex = new HashSet<StateVertex>(1);
                for (StateVertex aVertex : graph.getVertices())
                    try {
                        if (aVertex.getStateName() == lastEvent[machine])
                            currentVertex.add(aVertex);
                    } catch (NullPointerException e) {
                    }
                output.setSelectedStates(currentVertex);

                output.draw(graph, layout);
                machineLayout.clear();
                machineLayout.put(machine, layout);
            } else {
                if (!machineToDrawSet[machine]) { //toggle between whether to draw or remove the machine in multiple display mode
                    if (stateLimit > 0 && sm[machine].maxStates > stateLimit) {
                        int o = JOptionPane.showConfirmDialog(getParent(),
                                "The number of states of this LTS (" + sm[machine].maxStates + ") is above the set limit of " + stateLimit + ".\nDo you want to display it?",
                                "Limit reached",
                                JOptionPane.YES_NO_OPTION);
                        if (o == JOptionPane.NO_OPTION) {
                            return;
                        }
                    }
                    output.draw(makeGraph(machine), layout);
                    machineLayout.put(machine, layout);
                    machineToDrawSet[machine] = true;
                } else {
                    output.clear(makeGraph(machine));
                    machineLayout.remove(machine);
                    machineToDrawSet[machine] = false;
                }
                list.clearSelection();
            }
        }
    }

    private class MyCellRenderer extends JLabel implements ListCellRenderer {
        public MyCellRenderer() {
            setOpaque(true);
            setHorizontalTextPosition(SwingConstants.LEFT);
        }

        public Component getListCellRendererComponent(
                JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            setFont(fontFlag ? f4 : f3);
            setText(value + (machineLayout.containsKey(index) ? " (" + machineLayout.get(index) + ")" : ""));
            setBackground(isSelected ? Color.blue : Color.white);
            setForeground(isSelected ? Color.white : Color.black);
            if (machineHasAction != null && machineHasAction[index]) {
                setBackground(Color.red);
                setForeground(Color.white);
            }
            setForeground(isSelected ? Color.white : Color.black);
            setIcon(machineToDrawSet[index] && !singleMode ? drawIcon : null);
            return this;
        }
    }

    public void refresh() {
        if (singleMode) {
            output.refresh(layout);
            if (machineLayout != null && machineLayout.keySet().size() > 0) {
                Integer machine = machineLayout.keySet().iterator().next();
                machineLayout.clear();
                machineLayout.put(machine, layout);
            }
        } else {
            output.refresh(layout);
            for (Integer machine : machineLayout.keySet()) {
                machineLayout.put(machine, layout);
            }
        }
        list.updateUI();
    }
//-----------------------------------------------------------------------------
// LTS event handlers
//-----------------------------------------------------------------------------

    public void ltsAction(LTSEvent e) {
        switch (e.kind) {
            case LTSEvent.NEWSTATE:
                prevEvent = lastEvent;
                lastEvent = (int[]) e.info;
                lastName = e.name;
                buttonHighlight(lastName);
                if (lastName != null) new_transitions(lastName, prevEvent, lastEvent);
                break;
            case LTSEvent.INVALID:
//				prevEvent = null;
//				lastEvent = null;
                new_machines(cs = (CompositeState) e.info); //info holds the new composite state
                break;
            case LTSEvent.KILL:
                break;
            default:
        }
    }

    /*
     * Retrieves the newly highlighted transition and gives it to the canvas
     */
    private void new_transitions(String label, int[] from, int[] to) {
        final Set<TransitionEdge> transitions = new HashSet<TransitionEdge>();
        final Set<StateVertex> outStates = new HashSet<StateVertex>();

        for (int i = 0; i < sm.length - hasC; i++) {
            final LTSGraph g = makeGraph(i);
            if (machineHasAction[i]) {
                final TransitionEdge e = g.getTransitionFromLabel(label, from != null ? from[i] : 0, to != null ? to[i] : 0);
                if (e != null) {
                    transitions.add(e);
                    //outStates.add(graphs[i].getDest(e));
                }
            }
            outStates.add(g.getStateFromNumber(to != null ? to[i] : 0));
        }

        output.select(transitions, outStates);
    }

    /*
     * Turn a machine into a JUNG graph
     */
    private LTSGraph makeGraph(int machine) {
        if (graphValidity != null && graphValidity[machine]) {
            return graphs[machine];
        } else {
            final CompactState lts = sm[machine];

            final LTSGraph g = new LTSGraph(lts);

            graphValidity[machine] = true;
            graphs[machine] = g;
            return g;
        }
    }

    private void buttonHighlight(String label) {
        if (label == null && machineHasAction != null) {
            for (int i = 0; i < machineHasAction.length; i++)
                machineHasAction[i] = false;
        } else if (machineHasAction != null) {
            for (int i = 0; i < sm.length - hasC; i++)
                machineHasAction[i] = (!label.equals("tau") && sm[i].hasLabel(label));
        }
        list.repaint();
        return;
    }

    /*
     * Renew the members with a CompositeState
     */
    @SuppressWarnings("unchecked")
    private void new_machines(CompositeState cs) {
        hasC = (cs != null && cs.composition != null) ? 1 : 0;
        if (cs != null && cs.machines != null && cs.machines.size() > 0) { // get set of machines
            sm = new CompactState[cs.machines.size() + hasC];

            final Enumeration<CompactState> e = cs.machines.elements();
            for (int i = 0; e.hasMoreElements(); i++)
                sm[i] = e.nextElement();
            Nmach = sm.length;
            if (hasC == 1)
                sm[Nmach - 1] = cs.composition;
            machineHasAction = new boolean[Nmach];
            machineToDrawSet = new boolean[Nmach];
            machineLayout = new HashMap<Integer, EnumLayout>();
            graphValidity = new boolean[Nmach];

            graphs = new LTSGraph[Nmach];
        } else {
            Nmach = 0;
            machineHasAction = null;
            machineToDrawSet = null;
            machineLayout = null;
            graphValidity = null;
            graphs = null;
        }

        DefaultListModel lm = new DefaultListModel();
        for (int i = 0; i < Nmach; i++) {
            if (hasC == 1 && i == (Nmach - 1))
                lm.addElement("||" + sm[i].name);
            else
                lm.addElement(sm[i].name);
        }
        list.setModel(lm);

        output.clear();
    }

    //-----------------------------------------------------------------------------
//	Parameter setters
//-----------------------------------------------------------------------------
    public void setBigFont(boolean b) {
        fontFlag = b;
        //output.setBigFont(b);
    }

    public void setDrawName(boolean b) {
        //output.setDrawName(b);
    }

    public void setNewLabelFormat(boolean b) {
        //output.setNewLabelFormat(b);
    }

    public void setMode(boolean b) {
        singleMode = b;
        output.setMode(b);

        if (machineLayout != null)
            machineLayout.clear();

        list.clearSelection();
        if (Nmach > 0) {
            machineToDrawSet = new boolean[Nmach];
        }
        list.repaint();
    }

    public void removeClient() {
        if (eman != null) {
            eman.removeClient(this);
        }
    }

    public LTSJUNGCanvas getCanvas() {
        return output;
    }

//-----------------------------------------------------------------------------
// File saver
//-----------------------------------------------------------------------------

    public void saveFile() {
        final Object[] possibilities = {"png", "svg", "pdf"};
        final String s = (String) JOptionPane.showInputDialog(
                getParent(),
                "Choose a format",
                "Save LTS",
                JOptionPane.PLAIN_MESSAGE,
                null,
                possibilities,
                "png");

        if ((s != null) && (s.length() > 0)) {
            try {
                final JFileChooser fc = new JFileChooser();
                fc.setSelectedFile(new File("output." + s));
                int returnVal = fc.showSaveDialog(getTopLevelAncestor());
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    if (s.equals("png")) {
                        savePng(file);
                    } else if (s.equals("svg")) {
                        saveSvg(file);
                    } else if (s.equals("ps")) {
                        saveEps(file);
                    } else if (s.equals("pdf")) {
                        savePdf(file);
                    }
                }
            } catch (IOException ioe) {
            }
        }

    }

    private void savePng(File f) throws IOException {
        final BufferedImage bi = output.getImage();
        ImageIO.write(bi, "png", f);
    }

    private void saveSvg(File f) throws IOException {
        final VectorGraphics g = new SVGGraphics2D(f, new Dimension(output.getSize()));
        g.startExport();
        output.print(g);
        g.setBackground(new Color(255, 255, 255, 0));
        g.endExport();
    }

    private void saveEps(File f) throws FileNotFoundException {
        final VectorGraphics g = new PSGraphics2D(f, new Dimension(output.getSize()));
        g.startExport();
        output.print(g);
        g.setBackground(new Color(255, 255, 255, 0));
        g.rotate(90);
        g.endExport();
    }

    private void savePdf(File f) throws FileNotFoundException {
        final VectorGraphics g = new PDFGraphics2D(f, new Dimension(output.getSize()));
        g.startExport();
        output.print(g);
        g.setBackground(new Color(255, 255, 255, 0));
        g.endExport();
    }
}