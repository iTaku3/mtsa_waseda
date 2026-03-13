package ltsa.lts;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class RandomSeedDialog extends JFrame {
	
	private static final String CANCEL_BTN= "CANCEL_BTN";
	private static final String OK_BTN= "OK_BTN";
	
	class RandomSeedSpinnerListener implements ChangeListener {
		public void stateChanged(ChangeEvent evt) {
			JSpinner src= (JSpinner) evt.getSource();
			if (((Long) src.getValue()).longValue() < 0) {
				src.setValue(0);
			}
		}
	}
	
	class ButtonListener implements ActionListener {
		RandomSeedDialog mainWindow;
		
		public ButtonListener(RandomSeedDialog parent) {
			mainWindow= parent;
		}
		
		public void actionPerformed(ActionEvent evt) {
			JButton src= (JButton) evt.getSource();
			if (src.getName().equals(CANCEL_BTN)) {
				mainWindow.dispose();
			} else if (src.getName().equals(OK_BTN)) {
				long seed= mainWindow.getRandomSeed();
				if (seed == 0)
					Options.setUseGeneratedSeed(false);
				else
					Options.setRandomSeed(mainWindow.getRandomSeed());
				mainWindow.dispose();
			}
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID= 1L;
	private JPanel jPanel = null;
	private JSpinner randomSeedSpinner;
	
	public RandomSeedDialog() {
		this("Seed for randomization");
	}

	public RandomSeedDialog(String name) {
		super(name);
		initialize();
	}

	private void initialize() {
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setSize(new Dimension(390, 140));
        this.setResizable(false);
        this.setContentPane(getJPanel());
        this.setTitle("Seed for randomization");
        GridBagLayout layout= new GridBagLayout();
        getContentPane().setLayout(layout);
        
        GridBagConstraints constraints= new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.weightx= 0.0;
        constraints.weighty= 1.0;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor= GridBagConstraints.NORTHWEST;
        this.getContentPane().add(new JLabel("Set seed for randomization (0 to clear seed): "), constraints);

        randomSeedSpinner= new JSpinner(new SpinnerNumberModel(new Long(0), new Long(0), new Long(Long.MAX_VALUE), new Long(10)));
        ChangeListener randomSeedListener= new RandomSeedSpinnerListener();
        randomSeedSpinner.addChangeListener(randomSeedListener);
        long seed= Options.getRandomSeed();
        if (seed <= 0) {
        	randomSeedSpinner.setValue(new Long(0));
        } else {
        	randomSeedSpinner.setValue(new Long(seed));
        }

        constraints= new GridBagConstraints();
        constraints.gridx= 1;
        constraints.gridy= 0;
        constraints.gridwidth= 4;
        constraints.gridheight= 1;
        constraints.weightx= 2.0;
        constraints.weighty= 1.0;
        constraints.fill= GridBagConstraints.HORIZONTAL;
        constraints.anchor= GridBagConstraints.NORTHWEST;
        this.getContentPane().add(randomSeedSpinner, constraints);
        
        JButton cancelBtn= new JButton("Cancel");
        cancelBtn.setName(CANCEL_BTN);
        JButton okBtn= new JButton("OK");
        okBtn.setName(OK_BTN);
        ActionListener btnListener= new ButtonListener(this);
        cancelBtn.addActionListener(btnListener);
        okBtn.addActionListener(btnListener);
        
        constraints= new GridBagConstraints();
        constraints.gridx= 3;
        constraints.gridy= 1;
        constraints.gridwidth= 1;
        constraints.gridheight= 1;
        constraints.weightx= 1.0;
        constraints.weighty= 1.0;
        constraints.fill= GridBagConstraints.NONE;
        constraints.anchor= GridBagConstraints.EAST;
        this.getContentPane().add(cancelBtn, constraints);
        
        constraints= new GridBagConstraints();
        constraints.gridx= 4;
        constraints.gridy= 1;
        constraints.gridwidth= 1;
        constraints.gridheight= 1;
        constraints.weightx= 1.0;
        constraints.weighty= 1.0;
        constraints.fill= GridBagConstraints.NONE;
        constraints.anchor= GridBagConstraints.EAST;
        this.getContentPane().add(okBtn, constraints);
	}

	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel= new JPanel();
			jPanel.setLayout(new GridBagLayout());
		}
		return jPanel;
	}
	
	private long getRandomSeed() {
		return ((Long) randomSeedSpinner.getValue()).longValue();
	}
}
