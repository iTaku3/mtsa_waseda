package MTSAEnactment.ar.uba.dc.lafhis.enactment.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.BoxLayout;
import java.awt.Color;

public class UIControllerGui extends JFrame {

	private JPanel contentPane;
	private JTextArea textArea;
	private JPanel panelButtons;
	private JPanel panelControllable;
	private JPanel panelUnControllable;
	private JLabel lblControllableLabel;
	private JLabel lblUncontrollableLabel;
	private JPanel panel;
	private JPanel panel_1;
	private JScrollPane scrollPane;
	private JScrollPane scrollPane_1;


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
	public UIControllerGui() {
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.3);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		contentPane.add(splitPane, BorderLayout.CENTER);
		
		panelButtons = new JPanel();
		splitPane.setRightComponent(panelButtons);
		panelButtons.setLayout(new GridLayout(1, 2, 0, 0));
		
		panel = new JPanel();
		panelButtons.add(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		lblControllableLabel = new JLabel("Controllables");
		panel.add(lblControllableLabel);

		panelControllable = new JPanel();
		panelControllable.setLayout(new BoxLayout(panelControllable, BoxLayout.Y_AXIS));
		panelControllable.setBackground(Color.LIGHT_GRAY);
		scrollPane = new JScrollPane(panelControllable);
		panel.add(scrollPane);
		
		panel_1 = new JPanel();
		panelButtons.add(panel_1);
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.X_AXIS));
		
		lblUncontrollableLabel = new JLabel("Uncontrollables");
		panel_1.add(lblUncontrollableLabel);
		
		panelUnControllable = new JPanel();
		panelUnControllable.setLayout(new BoxLayout(panelUnControllable, BoxLayout.Y_AXIS));
		panelUnControllable.setBackground(Color.LIGHT_GRAY);
		scrollPane_1 = new JScrollPane(panelUnControllable);
		panel_1.add(scrollPane_1);
		
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		JScrollPane ltsOutputPane			= new JScrollPane(textArea);
		ltsOutputPane.setMinimumSize(new Dimension(23, 100));
		
		splitPane.setLeftComponent(ltsOutputPane);
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
		panelUnControllable.add(createButton(name, label, actionListener));				
	}
	
	public void removeActions()
	{
		panelControllable.removeAll();
		panelControllable.revalidate();
		panelUnControllable.removeAll();
		panelUnControllable.revalidate();
		this.repaint();
	}
}