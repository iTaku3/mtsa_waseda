/* AMES: Enhanced Modularity */
package ltsa.lts;

import java.util.List;
import java.util.Set;

public interface LTSManager {
	//TODO this is not being used, either use for something apart from just implementing it, or delete it.
	void performAction(Runnable r, boolean showOutputPane);
	String getTargetChoice();
	CompositeState compile(String name);
	void newMachines(List<CompactState> machines);
//	Set<String> getLabelSet(String name);
}
