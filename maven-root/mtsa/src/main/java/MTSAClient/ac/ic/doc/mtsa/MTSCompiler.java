package MTSAClient.ac.ic.doc.mtsa;

import MTSTools.ac.ic.doc.mtstools.model.MTS;
import ltsa.ac.ic.doc.mtstools.util.fsp.AutomataToMTSConverter;
import ltsa.dispatcher.TransitionSystemDispatcher;
import ltsa.lts.*;
import ltsa.ui.StandardOutput;

import java.io.File;
import java.io.IOException;

/**
 * Wrapper for LTSCompiler that can produce CompositeState or MTS representations of processes defined in FSP
 */

public class MTSCompiler {
	
	private static MTSCompiler instance = new MTSCompiler();
	public static MTSCompiler getInstance() { return instance; }
	private MTSCompiler() {}


	public MTS<Long, String> compileMTS(String modelName, File inputFile) throws Exception {
		CompositeState compositeState = compileCompositeState(modelName, inputFile);
		return AutomataToMTSConverter.getInstance().convert(compositeState.getComposition());
	}

	public MTS<Long, String> compileMTS(String modelName, String inputString) throws Exception {
		CompositeState compositeState = compileCompositeState(modelName, inputString);
		return AutomataToMTSConverter.getInstance().convert(compositeState.getComposition());
	}
	
	public CompositeState compileCompositeState(String modelName, File inputFile) throws IOException {
		LTSInput input = new FileInput(inputFile);
		return compileCompositeState(modelName, input);
	}
	
	public CompositeState compileCompositeState(String modelName, LTSInput input) throws IOException {
		StandardOutput output = new StandardOutput();
		return compileComposite(modelName, input, output);
	}

	public CompositeState compileCompositeState(String modelName, File inputFile, LTSOutput output) throws IOException {
		LTSInput input = new FileInput(inputFile);
		return compileComposite(modelName, input, output);
	}

	public CompositeState compileCompositeState(String modelName, String inputString) throws IOException {
		return compileComposite(modelName, new LTSInputString(inputString), new StandardOutput());
	}

//	private CompositeState compileComposite(String modelName, LTSInput input) throws IOException {
//		LTSOutput output = new StandardOutput(); 
//		return compileComposite(modelName, input, output);
//	}
	
	public CompositeState compileComposite(String modelName, LTSInput input, LTSOutput output) throws IOException {
		String currentDirectory = (new File(".")).getCanonicalPath();
		LTSCompiler compiler = new LTSCompiler(input, output, currentDirectory);
		compiler.compile();
		CompositeState c = compiler.continueCompilation(modelName);
		TransitionSystemDispatcher.applyComposition(c, output);
		return c;
	}


}
