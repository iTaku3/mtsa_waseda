package ltsa.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ltsa.lts.CompactState;
import ltsa.lts.CompositeState;
import ltsa.lts.DrawMachine;
import ltsa.lts.EventClient;
import ltsa.lts.EventManager;
import ltsa.lts.LTSCanvas;
import ltsa.lts.LTSEvent;
import ltsa.dclap.Gr2PICT;
import ltsa.dispatcher.TransitionSystemDispatcher;

public class LTSDrawWindow extends JSplitPane implements EventClient {

    LTSCanvas output;
    EventManager eman;
    CompositeState cs;
    int[] lastEvent, prevEvent;
    String lastName;
    int Nmach = 0; // the number of machines
    int hasC = 0; // 1 if last machine is composition
    CompactState[] sm; // an array of machines
    boolean[] machineHasAction;
    boolean[] machineToDrawSet;

    public static boolean fontFlag = false;
    public static boolean singleMode = false;

    JList list;
    JScrollPane left, right;

    Font f1 = new Font("Monospaced", Font.PLAIN, 12);
    Font f2 = new Font("Monospaced", Font.BOLD, 16);
    Font f3 = new Font("SansSerif", Font.PLAIN, 12);
    Font f4 = new Font("SansSerif", Font.BOLD, 16);

    ImageIcon drawIcon;

    public LTSDrawWindow(CompositeState cs, EventManager eman) {
        super();
        this.eman = eman;
        // output canvas
        output = new LTSCanvas(singleMode);
        JPanel outPane = new JPanel();
        outPane.setLayout(new BorderLayout());
        outPane.add("Center", output);
        right = new JScrollPane(outPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        // right.getViewport().putClientProperty("EnableWindowBlit",
        // Boolean.TRUE);
        // scrollable list pane
        list = new JList();
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(new PrintAction());
        list.setCellRenderer(new MyCellRenderer());
        left = new JScrollPane(list, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        JPanel fortools = new JPanel(new BorderLayout());
        fortools.add("Center", right);
        // tool bar
        JToolBar tools = new JToolBar();
        tools.setOrientation(JToolBar.VERTICAL);
        fortools.add("West", tools);
        tools.add(HPWindow.instance.createTool("icon/stretchHorizontal.gif", "Stretch Horizontal",
                new HStretchAction(10)));
        tools.add(HPWindow.instance.createTool("icon/compressHorizontal.gif", "Compress Horizontal",
                new HStretchAction(-10)));
        tools.add(HPWindow.instance.createTool("icon/stretchVertical.gif", "Stretch Vertical", new VStretchAction(10)));
        tools.add(HPWindow.instance.createTool("icon/compressVertical.gif", "Compress Vertical",
                new VStretchAction(-10)));
        if (eman != null)
            eman.addClient(this);
        new_machines(cs);
        setLeftComponent(left);
        setRightComponent(fortools);
        setDividerLocation(200);
        validate();
        output.addKeyListener(new KeyPress());
        output.addMouseListener(new MyMouse());
        drawIcon = getDrawIcon();
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

    // ------------------------------------------------------------------------

    class HStretchAction implements ActionListener {
        int increment;

        HStretchAction(int i) {
            increment = i;
        }

        public void actionPerformed(ActionEvent e) {
            if (output != null)
                output.stretchHorizontal(increment);
        }
    }

    class VStretchAction implements ActionListener {
        int increment;

        VStretchAction(int i) {
            increment = i;
        }

        public void actionPerformed(ActionEvent e) {
            if (output != null)
                output.stretchVertical(increment);
        }
    }

    class PrintAction implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting())
                return;

            int machine = list.getSelectedIndex();
            if (machine < 0 || machine >= Nmach)
                return;

            if (singleMode) {
                if (hasC == 1 && machine == Nmach - 1) {
                    // If the machine we are drawing is the composition we use
                    // a separate logic (for the moment)
                    output.draw_composite(machine, sm[machine], prevEvent, lastEvent, lastName);

                } else
                    output.draw(machine, sm[machine], validMachine(machine, prevEvent),
                            validMachine(machine, lastEvent), lastName);

            } else {
                if (!machineToDrawSet[machine]) {
                    if (hasC == 1 && machine == Nmach - 1)
                        output.draw_composite(machine, sm[machine], prevEvent, lastEvent, lastName);
                    else
                        output.draw(machine, sm[machine], validMachine(machine, prevEvent),
                                validMachine(machine, lastEvent), lastName);
                    machineToDrawSet[machine] = true;
                } else {
                    output.clear(machine);
                    machineToDrawSet[machine] = false;
                }
                list.clearSelection();
            }
        }
    }

    private int validMachine(int machine, int[] event) {
        // if (event != null && machine < (Nmach - hasC))
        if (event != null)
            return event[machine];
        else
            return 0;
    }

    private int[] validMachineComp(int machine, int[] event) {
        // If the machine we are drawing is the composition we use
        // a separate logic (for the moment)
        if (event != null)
            return event;
        else
            return new int[0];
    }

    class KeyPress extends KeyAdapter {
        public void keyPressed(KeyEvent k) {
            if (output == null)
                return;
            int code = k.getKeyCode();
            if (code == KeyEvent.VK_LEFT) {
                output.stretchHorizontal(-5);
            } else if (code == KeyEvent.VK_RIGHT) {
                output.stretchHorizontal(5);
            } else if (code == KeyEvent.VK_UP) {
                output.stretchVertical(-5);
            } else if (code == KeyEvent.VK_DOWN) {
                output.stretchVertical(5);
            } else if (code == KeyEvent.VK_BACK_SPACE) {
                int m = output.clearSelected();
                if (m >= 0) {
                    machineToDrawSet[m] = false;
                    list.repaint();
                }
            }
        }
    }

    class MyMouse extends MouseAdapter {
        public void mouseEntered(MouseEvent e) {
            output.requestFocus();
        }
    }

    /*---------LTS event broadcast action-----------------------------*/

    public void ltsAction(LTSEvent e) {
        switch (e.kind) {
        case LTSEvent.NEWSTATE:
            prevEvent = lastEvent;
            lastEvent = (int[]) e.info;
            lastName = e.name;

            // Get Nmach-hasc for LTS and Nmach for MTS to draw the composite
            int numMachines = TransitionSystemDispatcher.numberMachinesToDraw(this.cs);

            output.select(numMachines, prevEvent, lastEvent, e.name);
            buttonHighlight(e.name);
            break;
        case LTSEvent.INVALID:
            prevEvent = null;
            lastEvent = null;
            cs = (CompositeState) e.info;
            new_machines(cs);
            break;
        case LTSEvent.KILL:
            break;
        default:
            ;
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

    protected void new_machines(CompositeState cs) {
        hasC = (cs != null && cs.composition != null) ? 1 : 0;
        if (cs != null && cs.machines != null && cs.machines.size() > 0) { // get set of machines

            sm = new CompactState[cs.machines.size() + hasC];
            Enumeration e = cs.machines.elements();
            for (int i = 0; e.hasMoreElements(); i++)
                sm[i] = (CompactState) e.nextElement();
            Nmach = sm.length;
            if (hasC == 1)
                sm[Nmach - 1] = cs.composition;
            machineHasAction = new boolean[Nmach];
            machineToDrawSet = new boolean[Nmach];

        } else {
            Nmach = 0;
            machineHasAction = null;
            machineToDrawSet = null;
        }
        DefaultListModel lm = new DefaultListModel();
        for (int i = 0; i < Nmach; i++) {
            if (hasC == 1 && i == (Nmach - 1))
                lm.addElement("||" + sm[i].name);
            else
                lm.addElement(sm[i].name);
        }
        list.setModel(lm);
        output.setMachines(Nmach);
    }

    // ------------------------------------------------------------------------

    public void setDrawName(boolean b) {
        output.setDrawName(b);
    }

    public void setNewLabelFormat(boolean b) {
        output.setNewLabelFormat(b);
    }

    public void setMode(boolean b) {
        singleMode = b;
        output.setMode(b);
        list.clearSelection();
        if (Nmach > 0)
            machineToDrawSet = new boolean[Nmach];
        list.repaint();
    }

    // --------------------------------------------------------------------
    public void removeClient() {
        if (eman != null)
            eman.removeClient(this);
    }

    // ------------------------------------------------------------------------

    class MyCellRenderer extends JLabel implements ListCellRenderer {
        public MyCellRenderer() {
            setOpaque(true);
            setHorizontalTextPosition(SwingConstants.LEFT);
        }

        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                boolean cellHasFocus) {
            setFont(fontFlag ? f4 : f3);
            setText(value.toString());
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

    /* -------------------------------------------- */

    public void saveFile() {
        DrawMachine dm = output.getDrawing();
        if (dm == null) {
            JOptionPane.showMessageDialog(this, "No LTS picture selected to save");
            return;
        }
        FileDialog fd = new FileDialog((Frame) getTopLevelAncestor(), "Save file in:", FileDialog.SAVE);
        if (Nmach > 0) {
            String fname = dm.getMachine().name;
            int colon = fname.indexOf(':', 0);
            if (colon > 0)
                fname = fname.substring(0, colon);
            fd.setFile(fname + ".pct");
        }
        fd.show();
        String file = fd.getFile();
        if (file != null)
            try {
                int i = file.indexOf('.', 0);
                // BUGFIX Dipi
                if (i == -1) {
                    i = file.length() - 1;
                }
                file = file.substring(0, i) + "." + "pct";
                FileOutputStream fout = new FileOutputStream(fd.getDirectory() + file);
                // get picture
                ByteArrayOutputStream baos = new ByteArrayOutputStream(10000);
                Rectangle r = new Rectangle(0, 0, dm.getSize().width, dm.getSize().height);
                Gr2PICT pict = new Gr2PICT(baos, output.getGraphics(), r);
                dm.fileDraw(pict);
                pict.finalize(); // make sure pict end is written
                fout.write(baos.toByteArray());
                fout.flush();
                fout.close();
                // outln("Saved in: "+ fd.getDirectory()+file);
            } catch (IOException e) {
//				System.out.println("Error saving file: " + e);
            }
    }

}