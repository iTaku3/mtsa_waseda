package MTSAEnactment.ar.uba.dc.lafhis.enactment.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JTextPane;

public class RecoverEnactorGUI extends JFrame {

	private JPanel contentPane;
	private JTextArea textArea;
	private JPanel panelButtons;
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					RecoverEnactorGUI frame = new RecoverEnactorGUI();
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
	public RecoverEnactorGUI() {
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.5);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		contentPane.add(splitPane, BorderLayout.CENTER);
		
		panelButtons = new JPanel();
		splitPane.setRightComponent(panelButtons);
		
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		JScrollPane ltsOutputPane			= new JScrollPane(textArea);
		ltsOutputPane.setMinimumSize(new Dimension(23, 150));
		
		splitPane.setLeftComponent(ltsOutputPane);
	}

	public JTextArea getTextArea() {
		return textArea;
	}
	
	
	
	public void setEnableButtons(boolean enable)
	{
		for (Component component : panelButtons.getComponents())
		{
			if (component instanceof JButton)
			{
				JButton button = (JButton) component;
				button.setEnabled(enable);
			}
		}
	
	}
	
	public void addAction(String name, String label, ActionListener actionListener)
	{
		JButton btnAction;
		btnAction = new JButton();
		btnAction.setName(label);
		btnAction.setText(label);
		btnAction.addActionListener(actionListener);
		
		panelButtons.add(btnAction);
		
	}
	
	public void appendMessage(String message)
	{
		String newMessage = getTextArea().getText();
		newMessage += message + "\n";
		getTextArea().setText(newMessage);
	}
	
	
	
	
}
