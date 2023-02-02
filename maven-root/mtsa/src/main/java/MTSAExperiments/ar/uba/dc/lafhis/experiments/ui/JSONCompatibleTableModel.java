package MTSAExperiments.ar.uba.dc.lafhis.experiments.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import MTSAExperiments.ar.uba.dc.lafhis.experiments.exchange.JSONCompatible;
import MTSAExperiments.ar.uba.dc.lafhis.experiments.exchange.JSONCompatibleBoolean;
import MTSAExperiments.ar.uba.dc.lafhis.experiments.exchange.JSONCompatibleFloat;
import MTSAExperiments.ar.uba.dc.lafhis.experiments.exchange.JSONCompatibleInt;
import MTSAExperiments.ar.uba.dc.lafhis.experiments.exchange.JSONCompatibleObject;
import MTSAExperiments.ar.uba.dc.lafhis.experiments.exchange.JSONCompatibleString;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.MultiGraph;

public class JSONCompatibleTableModel implements TableModel{
	protected List<List<JSONCompatible>> data;
	protected List<String> keys;
	protected List<TableModelListener> listeners;
	
	public JSONCompatibleTableModel(List<String> keys, List<List<JSONCompatible>> data) throws IllegalArgumentException{
		for(List<JSONCompatible> row: data)
			if(keys.size() != row.size())
				throw new IllegalArgumentException("JSONCompatibleTableModel::JSONCompatibleTableModel data and keys should be of the same size");
		this.data			= data;
		this.keys			= keys;
		this.listeners		= new ArrayList<TableModelListener>();
	}

	public String getColumnName(int col) {
        return keys.get(col);
    }
    
	public int getRowCount() { return data.size(); }
	
    public int getColumnCount() { return keys.size(); }
    
    public Object getValueAt(int row, int col) {
    	JSONCompatible jsonObject = data.get(row).get(col);
    	if(jsonObject instanceof JSONCompatibleBoolean)
    		return ((JSONCompatibleBoolean)jsonObject).getValue();
    	if(jsonObject instanceof JSONCompatibleString)
    		return ((JSONCompatibleString)jsonObject).getValue();
    	if(jsonObject instanceof JSONCompatibleInt)
    		return ((JSONCompatibleInt)jsonObject).getValue();
    	if(jsonObject instanceof JSONCompatibleFloat)
    		return ((JSONCompatibleFloat)jsonObject).getValue();    	
        return data.get(row).get(col);
    }
    
    public boolean isCellEditable(int row, int col){ 
		if(getValueAt(row, col) instanceof JSONCompatibleObject)
			if(((JSONCompatibleObject)getValueAt(row, col)).getValue() instanceof DirectedSparseMultigraph)
					return true;
    	return true; 
    	
    }
    
    public void setValueAt(Object value, int row, int col) {
    	if(!(value instanceof JSONCompatible))
    		return;
    	data.get(row).set(col, (JSONCompatible)value);
        for(TableModelListener listener:listeners){
        	listener.tableChanged(new TableModelEvent(this, row, row, col));
        }
    }

	@Override
	public void addTableModelListener(TableModelListener l) {
		if(!listeners.contains(l))
			listeners.add(l);		
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if(data.size() > 0){
			return data.get(0).get(columnIndex).getClass();
		}
		return null;
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
		if(listeners.contains(l))
			listeners.remove(l);			
	}
}
