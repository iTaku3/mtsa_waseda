package ltsa.ui;

import java.util.AbstractCollection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;

import MTSTools.ac.ic.doc.mtstools.model.SemanticType;
import ltsa.lts.CompactState;
import ltsa.ui.RefinementWindow.Mode;



public class LegalityWindow extends JDialog { //JFrame {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private LegalityOptions legalityOptions;

    
    
    
    public LegalityWindow(HPWindow window, LegalityOptions options) {
    	super(window, true);
    	Vector<String>  a = new Vector<>(Collections.list(options.getLabelSetNames()));
    	initComponents(options.machines, a);
    	this.legalityOptions = options;
    }

    private void buttonCheckActionPerformed(java.awt.event.ActionEvent evt) {
    	int source = comboSourceMachine.getSelectedIndex();
    	int target = comboTargetMachine.getSelectedIndex();
    	//checkLegality(refinesModel, refinedModel);
    	this.legalityOptions.source = source;
    	this.legalityOptions.target = target;
    	this.legalityOptions.actionSet = (String) actionsComboBox.getSelectedItem();
    	
    	this.dispose();
    }

    	    
    protected void initComponents(Vector<CompactState> machines, Vector<String> labels) {
    	jCheckBox1 = new javax.swing.JCheckBox();
    	jPanel2 = new JPanel();
    	actionsComboBox = new JComboBox();
        comboSourceMachine = new JComboBox();
        comboTargetMachine = new JComboBox();
       // jLlabelModelsToCheck = new JLabel();
        labelRefines = new JLabel();
        jPanel1 = new JPanel();
        comboRefinementSemantics = new JComboBox();
        labelActions = new JLabel();
        
        //Until we define different semantics properly.
        comboRefinementSemantics.setVisible(true);
        labelActions.setVisible(true);
        
        jPanel3 = new JPanel();
        buttonCheck = new JButton();
        
        List<String> machineNames = machines.stream().map(elem -> elem.name).collect(Collectors.toList());

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        actionsComboBox.setModel(new DefaultComboBoxModel(labels));

        comboSourceMachine.setModel(new DefaultComboBoxModel(machineNames.toArray()));

        comboTargetMachine.setModel(new DefaultComboBoxModel(machineNames.toArray()));

    	this.setTitle("Models to check legality");
    	labelRefines.setText("is legal with respect to...");
    	//jLlabelModelsToCheck.setText("Models to check consistency");
    	jCheckBox1.setVisible(false);
    	labelRefines.setVisible(true);
        
        
        
        GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(15, 15, 15)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)

                    .add(jPanel2Layout.createSequentialGroup()
                        .add(comboSourceMachine, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)
                        .add(15, 15, 15)
                        .add(labelRefines)
                        .addPreferredGap(LayoutStyle.RELATED, 35, Short.MAX_VALUE)
                        .add(comboTargetMachine, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)
                        .add(15,15,15)
                        .add(jCheckBox1)
                        .add(15, 15, 15)
                        )))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
//                .add(15, 15, 15)
//                .add(jLlabelModelsToCheck)
                .add(15, 15, 15)
                .add(jPanel2Layout.createParallelGroup(GroupLayout.BASELINE)
                    .add(comboSourceMachine, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .add(comboTargetMachine, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .add(labelRefines)
                    .add(15, 15, 15)
                    .add(jCheckBox1))
                .addContainerGap(55, Short.MAX_VALUE))
        );

        //comboRefinementSemantics.setModel(new DefaultComboBoxModel() {});

        labelActions.setText("Actions:");

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(15, 15, 15)
                .add(labelActions, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.RELATED)
                .add(actionsComboBox, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(120, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(15, 15, 15)
                .add(jPanel1Layout.createParallelGroup(GroupLayout.BASELINE)
                    .add(labelActions)
                    .add(actionsComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        buttonCheck.setText("Check");
        buttonCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCheckActionPerformed(evt);
            }
        });

        GroupLayout jPanel3Layout = new GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(GroupLayout.LEADING)
            .add(GroupLayout.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(286, Short.MAX_VALUE)
                .add(buttonCheck)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(GroupLayout.LEADING)
            .add(GroupLayout.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .add(buttonCheck)
                .add(15, 15, 15))
        );

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(GroupLayout.LEADING)
                    .add(GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(jPanel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .add(GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(layout.createParallelGroup(GroupLayout.TRAILING)
                            .add(GroupLayout.LEADING, jPanel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .add(20, 20, 20))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.RELATED)
                .add(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.RELATED)
                .add(jPanel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        
        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton buttonCheck;
    private JComboBox actionsComboBox;
    private JComboBox comboSourceMachine;
    private JComboBox comboTargetMachine;
    private JComboBox comboRefinementSemantics;
    private JLabel jLlabelModelsToCheck;
    private JLabel labelRefines;
    private JLabel labelActions;
    private JPanel jPanel1;
    private JPanel jPanel2;
    private JPanel jPanel3;
    private javax.swing.JCheckBox jCheckBox1;
    // End of variables declaration//GEN-END:variables
}
