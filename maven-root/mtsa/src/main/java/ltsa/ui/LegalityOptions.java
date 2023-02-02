package ltsa.ui;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import ltsa.lts.CompactState;
import ltsa.lts.LabelSet;

public class LegalityOptions {

    
    public int source;
    public int target;
    public Vector<CompactState> machines;
    private Hashtable<String, LabelSet> labelSetConstants;
    public String actionSet;
    
    public LegalityOptions(Vector<CompactState> machines, Hashtable<String,LabelSet> labelSetConstants) {
        this.machines =  machines;
        this.labelSetConstants = labelSetConstants;
    }

    public boolean isValid() {
        return (source != target) && (actionSet != null);
    }

    public CompactState getSourceModel() {
        // TODO Auto-generated method stub
        return this.machines.get(this.source);
    }

    public CompactState getTargetModel() {
        // TODO Auto-generated method stub
        return this.machines.get(this.target);
    }
    
    public Enumeration<String> getLabelSetNames() {
        return this.labelSetConstants.keys();    
    }
    
}
