package ltsa.ui;

import javax.swing.*;
import javax.swing.text.Caret;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public abstract class FindDialog extends JDialog {
	JTextField findField, replaceField;
	JCheckBox matchCaseCheckBox, wrapCheckBox, wholeWordCheckBox;
	JRadioButton 
		upRadioButton, downRadioButton,
		allLinesRadioButton, selectedLinesRadioButton;
	JButton 
		findButton, 
		replaceButton, replaceFindButton, replaceAllButton, 
		closeButton;
	JLabel statusLabel;
		
	public FindDialog(Frame container) {
		super(container, "Find in this page", false);
		
		ButtonGroup 
			directionButtonGroup = new ButtonGroup(),
			scopeButtonGroup = new ButtonGroup();
		JPanel
			inputPanel = new JPanel(),
			directionPanel = new JPanel(),
			scopePanel = new JPanel(),
			optionsPanel = new JPanel(),
			statusPanel = new JPanel();		
		JLabel 
			findLabel = new JLabel("Find:"),
			replaceLabel = new JLabel("Replace With:");
		GridBagLayout
			layout = new GridBagLayout(),
			inputLayout = new GridBagLayout(),
			statusLayout = new GridBagLayout();
		GridBagConstraints
			constraints = new GridBagConstraints();
		
		findField = new JTextField(10);
		findField.setBackground(Color.WHITE);
		replaceField = new JTextField(10);
		replaceField.setBackground(Color.WHITE);
		matchCaseCheckBox = new JCheckBox("Match Case");
		wrapCheckBox = new JCheckBox("Wrap Search");
		wholeWordCheckBox = new JCheckBox("Whole Word");
		upRadioButton = new JRadioButton("Backward");
		downRadioButton = new JRadioButton("Forward");
		allLinesRadioButton = new JRadioButton("All");
		selectedLinesRadioButton = new JRadioButton("Selected Lines");
		findButton = new JButton("Find");
		replaceFindButton = new JButton("Replace/Find");
		replaceButton = new JButton("Replace");
		replaceAllButton = new JButton("Replace All");
		statusLabel = new JLabel("");
		closeButton = new JButton("Close");

		matchCaseCheckBox.setMnemonic('c');
		wrapCheckBox.setMnemonic('p');
		wholeWordCheckBox.setMnemonic('w');
		upRadioButton.setMnemonic('o');
		downRadioButton.setMnemonic('b');
		allLinesRadioButton.setMnemonic('l');
		selectedLinesRadioButton.setMnemonic('t');
		findButton.setMnemonic('n');
		replaceFindButton.setMnemonic('d');
		replaceButton.setMnemonic('r');
		replaceAllButton.setMnemonic('a');
		
		wholeWordCheckBox.setEnabled(false);
		selectedLinesRadioButton.setEnabled(false);
		
		findButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				JTextComponent t = currentTextComponent();
				
				if (t == null)
					return;
				
				// Find the next occurrence
				int startIndex = findNext(
						t.getText(),
						t.getCaret(),
						findField.getText(),
						matchCaseCheckBox.isSelected(), 
						wrapCheckBox.isSelected(),
						wholeWordCheckBox.isSelected(),
						selectedLinesRadioButton.isSelected(),
						downRadioButton.isSelected());
				
				if (startIndex < 0)
					statusLabel.setText("String not found.");
				else {
					t.setSelectionStart(startIndex);
					t.setSelectionEnd(startIndex+findField.getText().length());
					statusLabel.setText("");
				}
			}
		});		
		replaceFindButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JTextComponent t = currentTextComponent();
				
				if (t == null)
					return;
				
				// Replace the selected text
				if (t.getSelectedText() == null)
					;
				else if (matchCaseCheckBox.isSelected()
						&& t.getSelectedText().equals(findField.getText())
						|| t.getSelectedText().equalsIgnoreCase(findField.getText())) {

					int startIndex = t.getSelectionStart();
					
					t.setText( t.getText().substring(0,t.getSelectionStart()) +
							replaceField.getText() +
							t.getText().substring(t.getSelectionEnd()) );

					t.setSelectionStart(startIndex + replaceField.getText().length());
					t.setSelectionEnd(startIndex + replaceField.getText().length());
				}
				
				// Find the next occurrence
				int startIndex = findNext(
						t.getText(),
						t.getCaret(),
						findField.getText(),
						matchCaseCheckBox.isSelected(), 
						wrapCheckBox.isSelected(),
						wholeWordCheckBox.isSelected(),
						selectedLinesRadioButton.isSelected(),
						downRadioButton.isSelected());
				
				if (startIndex < 0)
					statusLabel.setText("String not found.");
				else {
					t.setSelectionStart(startIndex);
					t.setSelectionEnd(startIndex+findField.getText().length());
					statusLabel.setText("");
				}
			}
		});		
		replaceButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JTextComponent t = currentTextComponent();
				
				if (t == null)
					return;

				// Replace the selected text
				if (t.getSelectedText() == null)
					;
				else if (matchCaseCheckBox.isSelected()
						&& t.getSelectedText().equals(findField.getText())
						|| t.getSelectedText().equalsIgnoreCase(findField.getText())) {

					int startIndex = t.getSelectionStart();
					
					t.setText( t.getText().substring(0,t.getSelectionStart()) +
							replaceField.getText() +
							t.getText().substring(t.getSelectionEnd()) );

					t.setSelectionStart(startIndex + replaceField.getText().length());
					t.setSelectionEnd(startIndex + replaceField.getText().length());
				}
			}
		});		
		replaceAllButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JTextComponent t = currentTextComponent();
				
				if (t == null)
					return;
				
				// replace all occurrences
				int startIndex = t.getSelectionStart();
				t.setText(t.getText().replaceAll(
						matchingExpression(findField.getText(),
								matchCaseCheckBox.isSelected()),
						replaceField.getText()));
				t.setSelectionStart(startIndex);
				t.setSelectionEnd(startIndex);
			}
		});
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FindDialog.this.setVisible(false);
			}
		});

		directionButtonGroup.add(upRadioButton);
		directionButtonGroup.add(downRadioButton);
		downRadioButton.setSelected(true);
		
		scopeButtonGroup.add(allLinesRadioButton);
		scopeButtonGroup.add(selectedLinesRadioButton);
		allLinesRadioButton.setSelected(true);

		this.setLayout(layout);
		inputPanel.setLayout(inputLayout);
		directionPanel.setLayout(new BoxLayout(directionPanel,BoxLayout.Y_AXIS));
		scopePanel.setLayout(new BoxLayout(scopePanel,BoxLayout.Y_AXIS));
		optionsPanel.setLayout(new GridLayout(2,2,1,1));
		statusPanel.setLayout(statusLayout);		
		
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(3,3,3,3);
		constraints.weightx = 0;
		constraints.gridx = 1;
		inputLayout.setConstraints(findLabel,constraints);
		constraints.weightx = 1;
		constraints.gridx = 2;
		inputLayout.setConstraints(findField,constraints);
		constraints.weightx = 0;
		constraints.gridx = 1;
		inputLayout.setConstraints(replaceLabel,constraints);
		constraints.weightx = 1;
		constraints.gridx = 2;
		inputLayout.setConstraints(replaceField,constraints);
		constraints.weightx = 0;

		constraints.gridx = 1;
		constraints.gridwidth = 2;
		layout.setConstraints(inputPanel,constraints);
		constraints.weightx = 1;
		constraints.gridwidth = 1;
		layout.setConstraints(directionPanel,constraints);
		constraints.gridx = 2;
		layout.setConstraints(scopePanel,constraints);
		constraints.weightx = 0;
		constraints.gridx = 1;
		constraints.gridwidth = 2;
		layout.setConstraints(optionsPanel,constraints);
		constraints.gridwidth = 1;
		layout.setConstraints(findButton,constraints);
		constraints.gridx = 2;
		layout.setConstraints(replaceFindButton,constraints);
		constraints.gridx = 1;
		layout.setConstraints(replaceButton,constraints);
		constraints.gridx = 2;
		layout.setConstraints(replaceAllButton,constraints);	
		constraints.gridx = 1;
		constraints.gridwidth = 2;
		layout.setConstraints(statusPanel,constraints);	
		
		constraints.weightx = 1;
		constraints.gridx = GridBagConstraints.RELATIVE;
		statusLayout.setConstraints(statusLabel,constraints);	
		constraints.weightx = 0;
		statusLayout.setConstraints(closeButton,constraints);
		
		inputPanel.add(findLabel);
		inputPanel.add(findField);
		findLabel.setLabelFor(findField);
		inputPanel.add(replaceLabel);
		inputPanel.add(replaceField);
		replaceLabel.setLabelFor(replaceField);
		
		directionPanel.add(upRadioButton);
		directionPanel.add(downRadioButton);
		directionPanel.setBorder(BorderFactory.createTitledBorder("Direction"));
		
		scopePanel.add(allLinesRadioButton);
		scopePanel.add(selectedLinesRadioButton);
		scopePanel.setBorder(BorderFactory.createTitledBorder("Scope"));
		
		optionsPanel.add(matchCaseCheckBox);
		optionsPanel.add(wrapCheckBox);
		optionsPanel.add(wholeWordCheckBox);
		optionsPanel.setBorder(BorderFactory.createTitledBorder("Options"));

		statusPanel.add(statusLabel);
		statusPanel.add(closeButton);

		this.add(inputPanel);
		this.add(directionPanel);
		this.add(scopePanel);
		this.add(optionsPanel);
		this.add(findButton);
		this.add(replaceFindButton);
		this.add(replaceButton);
		this.add(replaceAllButton);
		this.add(statusPanel);
		
		int w = Math.max(
				directionPanel.getPreferredSize().width,
				scopePanel.getPreferredSize().width),
			h = Math.max(
				directionPanel.getPreferredSize().height,
				scopePanel.getPreferredSize().height);
		
		directionPanel.setPreferredSize(new Dimension(w,h));
		scopePanel.setPreferredSize(new Dimension(w,h));
		
		this.pack();
		this.setResizable(false);
	}
	
	abstract JTextComponent currentTextComponent();
	
	int findNext(String text, Caret caret, String query, 
			boolean matchCase, boolean wrap, 
			boolean wholeWord, boolean selectedLines, 
			boolean searchDown) {
		
		int caretStart = Math.min(caret.getDot(),caret.getMark()),
			caretEnd = Math.max(caret.getDot(),caret.getMark()),
			offset = 0;
		
		if (selectedLines) {
			text = text.substring(caretStart,caretEnd);
			offset = caretStart;
			caretStart = 0;
			caretEnd = 0;
		}		
		if (!matchCase) {
			text = text.toLowerCase();
			query = query.toLowerCase();
		}
		int index = searchDown
			? text.indexOf(query, caretEnd)
			: text.lastIndexOf(query, Math.max(caretStart-1,0));
		if (index < 0 && wrap)
			index = searchDown
				? text.indexOf(query)
				: text.lastIndexOf(query);
						
		if (index < 0)
			return index;
		else
			return offset + index;
	}
	
	String matchingExpression(String text, boolean matchCase) {
		if (matchCase)
			return text;
		else {
			StringBuilder s = new StringBuilder();
			String 
				upper = text.toUpperCase(),
				lower = text.toLowerCase();
			for (int i=0; i<text.length(); i++)
				s.append("[" + lower.charAt(i) + upper.charAt(i) + "]");
			return s.toString();
		}
	}
}
