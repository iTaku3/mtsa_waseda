package MTSAExperiments.ar.uba.dc.lafhis.experiments.ui;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import MTSAExperiments.ar.uba.dc.lafhis.experiments.exchange.JSONCompatibleObject;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.MultiGraph;

public class GraphTable extends JTable {
	public GraphTable(JSONCompatibleTableModel dataModel) {
		super(dataModel);
	}
	
	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		TableCellRenderer renderer	= null;
		JSONCompatibleTableModel dataModel = (JSONCompatibleTableModel)getModel();
		if(dataModel.getValueAt(row, column) instanceof JSONCompatibleObject)
			if(((JSONCompatibleObject)dataModel.getValueAt(row, column)).getValue() instanceof DirectedSparseMultigraph)
					renderer		= new JUNGGraphVisualizationRenderer();
		if(renderer == null)
			renderer	= super.getCellRenderer(row, column);
		//this.dataModel.addTableModelListener(renderer);
		return renderer;
	}
	
	@Override
	public TableCellEditor getCellEditor(int row, int column) {
		TableCellEditor editor	= null;
		JSONCompatibleTableModel dataModel = (JSONCompatibleTableModel)getModel();
		if(dataModel.getValueAt(row, column) instanceof JSONCompatibleObject)
			if(((JSONCompatibleObject)dataModel.getValueAt(row, column)).getValue() instanceof DirectedSparseMultigraph)
				editor		= new JUNGGraphVisualizationEditor(new JCheckBox("Graph Visualization"));
		if(editor == null)
			editor	= super.getCellEditor(row, column);
		//this.dataModel.addTableModelListener(renderer);
		return editor;
	}

	
}
