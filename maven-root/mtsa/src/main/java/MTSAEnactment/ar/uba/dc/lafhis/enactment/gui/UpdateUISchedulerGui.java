package MTSAEnactment.ar.uba.dc.lafhis.enactment.gui;

/**
 * Created by lnahabedian on 04/08/16.
 */

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class UpdateUISchedulerGui extends JFrame {

    private JPanel contentPane;
    private JTextArea textArea;
    private JPanel panelButtons;
    private JPanel panelControllable;
    private JPanel panelEnvironment;
    private JLabel lblControllableLabel;
    private JLabel lblEnvironmentLabel;
    private JPanel panel;
    private JPanel panel_1;
    private JScrollPane scrollPane;
    private JScrollPane scrollPane_1;
    private JSpinner waitingTimeInput;


    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIControllerGui frame = new UIControllerGui();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public UpdateUISchedulerGui() {

        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setBounds(100, 100, 1000, 500);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        // time input
        SpinnerModel model = new SpinnerNumberModel(2,1,10,1);
        waitingTimeInput = new JSpinner(model);

        JSplitPane splitPane = new JSplitPane();
        splitPane.setResizeWeight(0.05);
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        contentPane.add(splitPane, BorderLayout.CENTER);

        splitPane.setLeftComponent(waitingTimeInput);
        //

        panelButtons = new JPanel();
        splitPane.setRightComponent(panelButtons);
        panelButtons.setLayout(new GridLayout(1, 3, 0, 0));

        panel = new JPanel();
        panelButtons.add(panel);

        // Controllable menu
        panel.setLayout(new GridLayout(2,1,0,0));

        lblControllableLabel = new JLabel("<html><div style='text-align: center;'>Controller<br/>Actions</div></html>");
        lblControllableLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lblControllableLabel);
        panel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        panelControllable = new JPanel();
        panelControllable.setLayout(new BoxLayout(panelControllable, BoxLayout.Y_AXIS));
        panelControllable.setBackground(Color.LIGHT_GRAY);
        scrollPane = new JScrollPane(panelControllable);
        panel.add(scrollPane);
        //

        // message logger
        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane ltsOutputPane			= new JScrollPane(textArea);
        ltsOutputPane.setMinimumSize(new Dimension(23, 100));

        panelButtons.add(ltsOutputPane);
        //

        // Environment menu
        panel_1 = new JPanel();
        panelButtons.add(panel_1);

        panel_1.setLayout(new GridLayout(2,1,0,0));

        lblEnvironmentLabel = new JLabel("<html><div style='text-align: center;'>Environment<br/>Actions</div></html>");
        lblEnvironmentLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel_1.add(lblEnvironmentLabel);
        panel_1.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        panelEnvironment = new JPanel();
        panelEnvironment.setLayout(new BoxLayout(panelEnvironment, BoxLayout.Y_AXIS));
        panelEnvironment.setBackground(Color.LIGHT_GRAY);
        scrollPane_1 = new JScrollPane(panelEnvironment);
        panel_1.add(scrollPane_1);
        //


    }

    public void appendMessage(String message)
    {
        String newMessage = textArea.getText();
        newMessage += message + "\n";
        textArea.setText(newMessage);
    }

    private JButton createButton(String name, String label, ActionListener actionListener)
    {
        JButton btnAction;
        btnAction = new JButton();
        btnAction.setName(label);
        btnAction.setText(label);
        btnAction.addActionListener(actionListener);
        return btnAction;
    }


    public void addControllableAction(String name, String label, ActionListener actionListener)
    {
        panelControllable.add(createButton(name, label, actionListener));
    }
    public void addUnControllableAction(String name, String label, ActionListener actionListener)
    {
        panelEnvironment.add(createButton(name, label, actionListener));
    }

    public void removeActions()
    {
        panelControllable.removeAll();
        panelControllable.revalidate();
        panelEnvironment.removeAll();
        panelEnvironment.revalidate();
        this.repaint();
    }

    public Integer getWaitingTime() {
        return (Integer) waitingTimeInput.getValue();
    }
}
