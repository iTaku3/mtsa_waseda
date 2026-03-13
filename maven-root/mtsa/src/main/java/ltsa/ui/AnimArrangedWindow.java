package ltsa.ui;

import ltsa.lts.Animator;
import ltsa.lts.RunMenu;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.BitSet;

/**
 * Window to run the simple animator
 */
public class AnimArrangedWindow extends JFrame {

    public static boolean fontFlag = false;

    Box p;
    JTextArea output;
    Animator animator;
    JCheckBox[] choices;
    Font f1;
    BitSet actions;
    Color priority;
    BitSet pactions;
    String[] modelAlphabet;
    JButton step, run;
    public boolean autoRun = false;
    private final static int STEPLIMIT = 64;
    protected boolean traceMode = false;

    AnimArrangedWindow(Animator animator, RunMenu r, boolean auto,
                       boolean traceM) {
        autoRun = auto;
        traceMode = traceM;
        // setFont
        if (fontFlag) {
            f1 = new Font("SansSerif", Font.BOLD, 16); // should be bold - bug
        } else {
            f1 = new Font("SansSerif", Font.BOLD, 12);
        }
        // parameters
        this.animator = animator;
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                actions = animator.initialise(null);
                addArranged();
            }

        });


        setBackground(Color.white);
        getContentPane().setLayout(
                new BoxLayout(getContentPane(), BoxLayout.X_AXIS));
        // trace output
        output = new JTextArea("", 15, 15);
        output.setEditable(false);
        output.setFont(f1);
        output.setBackground(Color.white);
        output.setBorder(new EmptyBorder(0, 5, 0, 0));
        JScrollPane outp = new JScrollPane(output,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        getContentPane().add(outp);
        if (r == null)
            actions = animator.initialise(null);
        else
            actions = animator.initialise(r.alphabet);
        if (traceMode)
            setTitle("Replay Animator");
        else
            setTitle("Animator");
        // buttons
        Box buttons;
        step = new JButton("Step");
        step.setFont(f1);
        step.addActionListener(new AnimAction(traceMode ? -3 : -1));
        run = new JButton("Run");
        run.addActionListener(new AnimAction(traceMode ? -4 : -2));
        run.setFont(f1);
        buttons = Box.createHorizontalBox();
        buttons.add(run);
        buttons.add(step);
        // controls
        String alphabet[] = animator.getMenuNames();
        modelAlphabet = animator.getAllNames();
        if (animator.getPriority())
            priority = Color.cyan;
        else
            priority = Color.pink;
        pactions = animator.getPriorityActions();
        choices = new JCheckBox[alphabet.length];
        p = Box.createVerticalBox();
        choices[0] = null;
        for (int i = 1; i < alphabet.length; i++) {
            JCheckBox box = new JCheckBox(alphabet[i], null, actions.get(i));
            box.setFont(f1);
            box.addActionListener(new AnimAction(i));
            if (traceMode)
                box.setEnabled(false);
            if (pactions != null && pactions.get(i))
                box.setBackground(priority);
            choices[i] = box;
        }

        p.add(Box.createHorizontalStrut(10));
        boolean enable = (animator.nonMenuChoice() || traceMode);
        step.setEnabled(enable);
        run.setEnabled(enable);
        if (empty(actions) && !enable)
            outln("STOP");
        JScrollPane boxes = new JScrollPane(p,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        boxes.setBorder(new EmptyBorder(0, 0, 0, 0));
        Box side = Box.createVerticalBox();
        side.add(buttons);
        side.add(boxes);
        getContentPane().add(side);
        p.setBackground(Color.white);
        validate();
        addArranged();
        if (autoRun)
            dostep(-2);
    }

    private void dostep(int choice) {
        actions = null;
        if (animator.isError())
            return;
        if (choice == -1) {
            actions = animator.singleStep();
            outAction();
        } else if (choice == -2) {
            actions = multiStep(null);
        } else if (choice == -3) {
            actions = animator.traceStep();
            outAction();
        } else if (choice == -4) {
            actions = multiTraceStep(null);
        } else if (!choices[choice].isSelected()) { // must have been toggled from true
            actions = animator.menuStep(choice);
            outAction();

        }
        if (actions == null)
            return;
        for (int j = 1; j < choices.length; j++) {
            choices[j].setSelected(actions.get(j));
        }
        addArranged();
        if (autoRun && !traceMode)
            actions = multiStep(actions);
        if (!traceMode) {
            boolean enable = animator.nonMenuChoice();
            step.setEnabled(enable);
            run.setEnabled(enable);
            if (empty(actions) && !enable && !animator.isError()) {
                if (animator.isEnd())
                    outln("END");
                else
                    outln("STOP");
            }
        } else {
            boolean enable = animator.traceChoice();
            step.setEnabled(enable);
            run.setEnabled(enable);
            if (!enable && !animator.isError()) {
                if (empty(actions)) {
                    if (animator.isEnd())
                        outln("END");
                    else
                        outln("STOP");
                } else {
                    outln("DIVERGED FROM TRACE");
                }
            }
        }

        modelAlphabet = animator.getAllNames();
        repaint();
    }

    private void addArranged() {
        p.removeAll();
        for (JCheckBox box : choices) {
            if (box != null && box.isSelected()) {
                box.setEnabled(true);
                p.add(box);
            }
        }
        for (JCheckBox box : choices) {
            if (box != null && !box.isSelected()) {
                box.setEnabled(false);
                p.add(box);
            }
        }
        p.revalidate();
        p.repaint();
    }

    private BitSet multiStep(BitSet b) {
        int step = 0;
        while (animator.nonMenuChoice()) {
            b = animator.singleStep();
            outAction();
            if (++step > STEPLIMIT) {
                outln("LOOP");
                return b;
            }
        }
        return b;
    }

    private BitSet multiTraceStep(BitSet b) {
        while (animator.traceChoice()) {
            b = animator.traceStep();
            outAction();
        }
        return b;
    }

    // -----MuCSPOutput-------------------------------------------------------------------

    public void out(String str) {
        output.append(str);
    }

    public void outln(String str) {
        output.append(str + "\n");
    }

    public void clearOutput() {
        output.setText("");
    }

    // ------------------------------------------------------------------------

    private boolean empty(BitSet b) {
        for (int i = 0; i < b.size(); ++i)
            if (b.get(i))
                return false;
        return true;
    }

    private void outAction() {
        outln(" " + modelAlphabet[animator.actionChosen()]);
        if (animator.isError())
            outln("ERROR");
    }

    class AnimAction implements ActionListener {
        int choice;

        AnimAction(int id) {
            choice = id;
        }

        public void actionPerformed(ActionEvent e) {
            dostep(choice);
        }
    }

}