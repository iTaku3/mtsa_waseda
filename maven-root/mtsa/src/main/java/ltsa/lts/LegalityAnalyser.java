package ltsa.lts;

import java.util.List;

public class LegalityAnalyser extends Analyser {

    private int targetMachineIndex;
	private int sourceMachineIndex;
	private List<String> actionSet;
	private DFSLegalCompositionEngine engine;

	public LegalityAnalyser(CompositeState cs, LTSOutput output, int source, int target, List<String> actionSet) {
        super(cs, output, null);
        this.sourceMachineIndex = source;
        this.targetMachineIndex = target;
        this.actionSet = actionSet;
        this.engine = new DFSLegalCompositionEngine(coder, sourceMachineIndex, targetMachineIndex, actionSet); 
    }

	public LegalityAnalyser(CompositeState cs, LTSOutput output, String sourceMachineName, String targetMachineName, List<String> actionSet) {
        super(cs, output, null);
        this.sourceMachineIndex = this.getMachineIndex(cs, sourceMachineName);
        this.targetMachineIndex = this.getMachineIndex(cs, targetMachineName);
        this.actionSet = actionSet;
        this.engine = new DFSLegalCompositionEngine(coder, sourceMachineIndex, targetMachineIndex, actionSet); 
    }
	
	private int getMachineIndex(CompositeState cs, String machineName) {
		int i = 0;
		while (i < cs.machines.size()) {
			if (cs.machines.get(i).name.equals(machineName)) break;
			i++;
		}		
		return i;
	}
	
    public CompositionEngine getCompositionEngine() {
		return this.engine;
	}

	public boolean isLegal() {
		// TODO Auto-generated method stub
		return this.engine.isLegal();
	}
    
}
