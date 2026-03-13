package FSP2MTS.ac.ic.doc.mtstools;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import ltsa.lts.CompactState;
import ltsa.lts.CompositeState;
import MTSAClient.ac.ic.doc.mtsa.MTSCompiler;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.Refinement;
import MTSTools.ac.ic.doc.mtstools.model.SemanticType;
import ltsa.ac.ic.doc.mtstools.util.fsp.AutomataToMTSConverter;

public class MTSChecker2 {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		if (args.length < 1) {
//			System.out.println("fspFile1 [fspFile2] ...");
			return;
		}
		MTSChecker2 checher = new MTSChecker2();
		Logger.getLogger("").setLevel(Level.WARNING);
		for(String arg: args) {
//			System.out.println(arg);
			checher.run(arg);
		}
			
	}
	

	private Map<String, Refinement> refinementNotions = new LinkedHashMap<String, Refinement>();

	public void run(String modelsFile) throws IOException {
	
		Map<String,MTS<Long,String>> MTSs = this.getMTSs(modelsFile);

		this.loadSemantics();
		
		for(Map.Entry<String, Refinement> refinement: this.getRefinementNotions().entrySet()) {
			for(Map.Entry<String,MTS<Long,String>> entry1: MTSs.entrySet()) {
				for(Map.Entry<String,MTS<Long,String>> entry2: MTSs.entrySet()) {
					if (entry1.equals(entry2)) {
						continue;
					}
					try {
						if (refinement.getValue().isARefinement(entry1.getValue(), entry2.getValue())) {
//							System.out.println(entry2.getKey() + " refines " + entry1.getKey() + " [" + refinement.getKey() + "]");
						}
					} catch(IllegalArgumentException e) {
					}
				}
			}
		}	
	}

	@SuppressWarnings("unchecked")
	private Map<String, MTS<Long, String>> getMTSs(String modelsFile) throws IOException {

		CompositeState state = MTSCompiler.getInstance().compileCompositeState("DEFAULT", new File(modelsFile));
		
		AutomataToMTSConverter converter = AutomataToMTSConverter.getInstance();
		
		Map<String,MTS<Long,String>> MTSs = new TreeMap<String,MTS<Long,String>>();
		for(CompactState automata:(Vector<CompactState>)state.getMachines()) {
			MTS<Long,String> mts = converter.convert(automata);
			MTSs.put(automata.name,mts);
		}
		
		return MTSs;
	}
	
	private Map<String, Refinement> getRefinementNotions() {
		return this.refinementNotions;
	}


	private void loadSemantics() {
		this.getRefinementNotions().put("Weak Alphabet", SemanticType.WEAK_ALPHABET.getRefinement());
		this.getRefinementNotions().put("Weak", SemanticType.WEAK.getRefinement());
	}



}
