package FSP2MTS.ac.ic.doc.mtstools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import ltsa.lts.CompactState;
import ltsa.lts.CompositeState;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSAClient.ac.ic.doc.mtsa.MTSCompiler;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTSConstants;
import MTSTools.ac.ic.doc.mtstools.model.Refinement;
import MTSTools.ac.ic.doc.mtstools.model.SemanticType;
import ltsa.ac.ic.doc.mtstools.util.fsp.AutomataToMTSConverter;

public class MTSChecker {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
//			System.out.println("fspFile queriesFile");
			return;
		}
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).getParent().setLevel(Level.SEVERE);
		MTSChecker checher = new MTSChecker();
		
		List<Pair<String,String>> queries = parseQueriesFile(args[1]);
		
		checher.run(args[0], queries);
	}
	
	private static List<Pair<String, String>> parseQueriesFile(String queriesFile) throws IOException {
		List<Pair<String, String>> result = new LinkedList<Pair<String, String>>();
		BufferedReader input = new BufferedReader(new FileReader(queriesFile));
		
		while(input.ready()) {
			String line = input.readLine();
			if (line.matches("\\s*%.*") || line.matches("\\s*")) {
				continue;
			}
			String split[] = line.split(" ");
			if (split.length != 2) {
				throw new RuntimeException("Invalid line format: " + line);
			}
			result.add(Pair.create(split[0], split[1]));
		}		
		
		return result;
	}

	private Map<String, Refinement> refinementNotions = new LinkedHashMap<String, Refinement>();

	public void run(String modelsFile, List<Pair<String,String>> queries) throws IOException {
	
		Map<String,MTS<Long,String>> MTSs = this.getMTSs(modelsFile);

		this.loadSemantics();
		
	
		for(Pair<String, String> query: queries) {
			MTS<Long,String> mts[] = new MTS[2];
			mts[0] = MTSs.get(query.getFirst());
			mts[1] = MTSs.get(query.getSecond());
			
//			System.out.print(query.getFirst() + "  - " + query.getSecond());
			if (mts[0] != null && mts[1] != null) {
				this.test(mts[0], mts[1]);			
			} else {
//				System.out.println("Error:");
				if (mts[0] == null) {
//					System.out.println(query.getFirst() + " could not be found");
				}
				if (mts[1] == null) {
//					System.out.println(query.getSecond() + " could not be found");
				}
//				System.out.println();
			}
		}
			
	}
	
	private Map<String, MTS<Long, String>> getMTSs(String modelsFile) throws IOException {

		CompositeState state = MTSCompiler.getInstance().compileCompositeState("DEFAULT", new File(modelsFile));
		
		AutomataToMTSConverter converter = AutomataToMTSConverter.getInstance();
		
		Map<String,MTS<Long,String>> MTSs = new HashMap<String,MTS<Long,String>>();
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
		Set<String> invisibleActions = new HashSet<String>();
		invisibleActions.add("_tau");
		invisibleActions.add(MTSConstants.TAU);
		
		this.getRefinementNotions().put("Strong           ", SemanticType.STRONG.getRefinement());
		this.getRefinementNotions().put("Branching        ", SemanticType.BRANCHING.getRefinement(invisibleActions));
		this.getRefinementNotions().put("Weak             ", SemanticType.WEAK.getRefinement(invisibleActions));		
	}

	private <A,S> void test(MTS<A,S> mts1, MTS<A,S> mts2) {
//		System.out.println(" checking for refinement");
		for(Map.Entry<String, Refinement> notion: this.getRefinementNotions().entrySet()) {
			long start = System.nanoTime();
//			System.out.print(notion.getKey() + ": ");
			try {
//				System.out.print(notion.getValue().isARefinement(mts1,mts2));
			} catch (IllegalArgumentException e) {
//				System.out.print("n/a");
			}
//			System.out.println(" \ttime: " + (System.nanoTime() - start) / 1000000 + " (mseg)");
		}		

//		System.out.println();
	}


}
