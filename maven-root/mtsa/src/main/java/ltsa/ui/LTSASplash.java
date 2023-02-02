package ltsa.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;

public class LTSASplash extends Window {

    private static final long serialVersionUID = 1L;

    final Window thisWindow;

    String label_text = "The Modal Transition Analyser (MTSA) is a fork of the Labelled Transition System Analyzer developed at Imperial College London initially by Jeff Magee.\n\n"
            + "MTSA is a research effort by the Laboratory on Foundations and Tools for Software Engineering (LaFHIS) at University of Buenos Aires";

    // SplashScreen's constructor
    public LTSASplash(Window owner) {
        super(owner);
        thisWindow = this;
        
        // Create a JPanel so we can use a BevelBorder
        JPanel panelForBorder = new JPanel(new BorderLayout());
        panelForBorder.setLayout(new GridBagLayout());
        panelForBorder.add(this.getLabel());
        panelForBorder.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(panelForBorder);
        setSize(400, 300);

        // Plonk it on center of screen
        Dimension WindowSize = getSize(), ScreenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((ScreenSize.width - WindowSize.width) / 2, (ScreenSize.height - WindowSize.height) / 2,
                WindowSize.width, WindowSize.height);
        this.addMouseListener(new Mouse());
        setVisible(true);
    }

    private JComponent getLabel() {
        JTextArea textArea = new JTextArea(2, 20);
        textArea.setText(label_text);
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setOpaque(false);
        textArea.setEditable(false);
        textArea.setFocusable(false);
        textArea.setBackground(UIManager.getColor("Label.background"));
        textArea.setFont(UIManager.getFont("Label.font"));
        textArea.setBorder(UIManager.getBorder("Label.border"));

        return textArea;

    }

    class Mouse extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            thisWindow.setVisible(false);
            thisWindow.dispose();
        }
    }

}