package FSP2MTS.ac.ic.doc.mtstools.test.util;

import ltsa.dispatcher.TransitionSystemDispatcher;
import ltsa.lts.CompositeState;
import ltsa.lts.LTSOutput;
import MTSAClient.ac.ic.doc.mtsa.MTSCompiler;

public class LTSATestUtils {
	
	public static CompositeState buildAutomataFromSource(String stringSource, String modelName) throws Exception {
		return MTSCompiler.getInstance().compileCompositeState(modelName, stringSource);
	}
	
	public static CompositeState buildAutomataFromSource(String stringSource) throws Exception {
		return buildAutomataFromSource(stringSource, "DEFAULT");
	}
	
	public static CompositeState buildCompositeState(String sourceString, LTSOutput ltsOutput) throws Exception {
		CompositeState composite = buildAutomataFromSource(sourceString);
		TransitionSystemDispatcher.parallelComposition(composite, ltsOutput);
		return composite;
	}
}
