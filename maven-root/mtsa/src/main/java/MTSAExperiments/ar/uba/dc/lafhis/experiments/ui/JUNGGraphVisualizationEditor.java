package MTSAExperiments.ar.uba.dc.lafhis.experiments.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import MTSAExperiments.ar.uba.dc.lafhis.experiments.exchange.JSONCompatibleObject;
import MTSAExperiments.ar.uba.dc.lafhis.experiments.visualization.ExperimentJUNGGraphVisualization;

public class JUNGGraphVisualizationEditor extends DefaultCellEditor {

	public JUNGGraphVisualizationEditor(JCheckBox checkbox) {
		super(checkbox);
	}
	
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		JFrame frame = new JFrame("Graph Visualization");        
		ExperimentJUNGGraphVisualization component = null;
		try {
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			component 				= new ExperimentJUNGGraphVisualization((JSONCompatibleObject)value, new Dimension(screenSize.width - 200, screenSize.height - 200));
	        JScrollPane scrollPane = new JScrollPane(component.getVisualComponent());		

			frame.setSize(new Dimension(screenSize.width - 200, screenSize.height - 200));			
			
			frame.getContentPane().add(scrollPane);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//frame.pack();
		frame.setExtendedState( frame.getExtendedState()|JFrame.MAXIMIZED_BOTH );
		frame.setVisible(true);		
		return frame;
	}
	
	@Override
	public Object getCellEditorValue() {
		// TODO Auto-generated method stub
		return super.getCellEditorValue();
	}
}