package MTSAExperiments.ar.uba.dc.lafhis.experiments.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import MTSAExperiments.ar.uba.dc.lafhis.experiments.exchange.JSONCompatibleObject;
import MTSAExperiments.ar.uba.dc.lafhis.experiments.visualization.ExperimentJUNGGraphVisualization;

public class JUNGGraphVisualizationRenderer extends JButton implements TableCellRenderer {
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, 
			int row, int column) {

		
		final JSONCompatibleObject jsonGraph = (JSONCompatibleObject)value;
		
		JSONCompatibleTableModel tableModel 	= (JSONCompatibleTableModel)table.getModel();
		
		JButton retValue	= new JButton("Graph Visualization");
		
		retValue.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent ev) {
				// TODO Auto-generated method stub
				JFrame frame = new JFrame("Graph Visualization");
				 frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				 try {
					//size of the screen
					Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
					frame.getContentPane().add((new ExperimentJUNGGraphVisualization(jsonGraph, new Dimension(screenSize.width - 200, screenSize.height - 200))).getVisualComponent());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 frame.pack();
				 frame.setVisible(true);				
			}
		});

		return retValue;
	}

}
