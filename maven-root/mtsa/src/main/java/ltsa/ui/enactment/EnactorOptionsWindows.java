package ltsa.ui.enactment;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;

import ltsa.enactment.EnactmentOptions;
import ltsa.enactment.EnactorFactory;
import ltsa.enactment.SchedulerFactory;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.ListSelectionModel;

import ltsa.ui.HPWindow;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JList;

public class EnactorOptionsWindows extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private EnactmentOptions<Long, String> options;
	private SchedulerFactory schedulerFactory;
	private EnactorFactory enactorFactory;
	private JComboBox cbSchedulers;
	private JButton okButton;
	private JList enactorList;
	
	

	/**
	 * Create the dialog.
	 */
	public EnactorOptionsWindows(EnactmentOptions<Long, String> options, HPWindow parent) {
		super(parent, "Enactment Options", true);
		
		this.schedulerFactory = parent.getApplicationContext().getBean(SchedulerFactory.class);
		this.enactorFactory = parent.getApplicationContext().getBean(EnactorFactory.class);
		
		setBounds(100, 100, 450, 232);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));
		{
			JLabel lblEnactors = new JLabel("Enactors:");
			contentPanel.add(lblEnactors, "4, 2");
		}
		{
			
			List<String> enactorNames = new ArrayList<String>();
			if (enactorFactory != null)
			{
				enactorNames = this.enactorFactory.getEnactorNames();
			}
			enactorList = new JList(enactorNames.toArray(new String[0]));
			enactorList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			enactorList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
			JScrollPane scrollPane = new JScrollPane(enactorList);
			
			//scrollPane.add(enactorList, "8, 2, fill, fill");
			scrollPane.setPreferredSize(new Dimension(250, 80));
			
			contentPanel.add(scrollPane, "8, 2, fill, fill");
		}
		{
			JLabel lblScheduler = new JLabel("Scheduler:");
			contentPanel.add(lblScheduler, "4, 4");
		}
		{
			cbSchedulers = new JComboBox();

			if (schedulerFactory != null)
			{
				List<String> schedulersList;
				schedulersList = schedulerFactory.getSchedulersList();
				for (String scheduler : schedulersList)
					cbSchedulers.addItem(scheduler);
			}
					
			
			contentPanel.add(cbSchedulers, "8, 4, fill, default");
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("OK");				
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						setVisible(false);
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		
		
	}

	public JComboBox getCbSchedulers() {
		return cbSchedulers;
	}
	
	public JButton getOkButton() {
		return okButton;
	}
	public JList getEnactorList() {
		return enactorList;
	}
}
