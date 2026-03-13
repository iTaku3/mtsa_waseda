package MTSATests.util;


import MTSAClient.ac.ic.doc.mtsa.MTSCompiler;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.impl.UnmodifiableMTS;

/**
 * Models to be used on tests 
 * @author gsibay
 *
 */
public class TestModels {

	//DO NOT CHANGE THE ORDER OF THE STRINGS. THEY ARE USED IN THE TEST CASES.
	//MORE MODELS CAN BE ADDED.
	private final String[] modelsToParse = { 
		"I0 = STOP.", 
		"I1 = (a-> I1).", 
		"I2 = (a?->I2).", 
		"I3 = (a -> b? -> STOP).",
		"I4 = (a-> I4 | b? -> I4).",
		"I5 = (a-> I5 | a? -> STOP).",
		"I6 = (a?-> I6 | b? -> STOP).",
		"I7 = (a-> I7 | a -> STOP).",
		"I8 = (c -> Q1), Q1 = (b? -> Q2 | c? -> Q3), Q2 = (c-> I8), Q3 = (b -> I8).",
		"I9 = (c -> Q1), Q1 = (b? -> Q2 | b? -> Q3), Q2 = (c-> I9), Q3 = (b -> I9).",
		"I10 = (c -> Q1 | c? -> Q2 | c -> Q3), Q1 = (b? -> Q2 | b? -> Q3), Q2 = (c-> I10), Q3 = (b -> I10 | b? -> Q2).",
		
		"I11 = Q1, Q1 = (c? -> Q3 | a? -> Q2), Q2 = (c? -> Q4), Q3 = ( a -> Q4), Q4 = (b -> Q5), Q5 = (d -> Q6 | a? -> Q8 | c? -> Q7), " +
				"Q6 = (a? -> Q9), Q7 = (a? -> Q10), Q8 = (d -> Q9 | c? -> Q10), Q9 = (b -> Q1), Q10 = (b? -> Q1).",
		
		"I12 = (a -> Q1 | c? -> Q2), Q1 = ( c? -> Q3), Q2 = (a -> Q3), Q3 = (b -> STOP).",
		"I13 = (a? -> Q1 | c? -> Q2), Q1 = ( c? -> Q3), Q2 = (a -> Q3), Q3 = (b -> STOP).",
		"I14 = (a -> Q1 | c? -> Q2), Q1 = ( c? -> Q3), Q2 = (a? -> Q3), Q3 = (b -> STOP).",
		"I15 = (a -> Q1 | c? -> Q2), Q1 = ( c -> Q3), Q2 = (a? -> Q3), Q3 = (b -> STOP)."
	};

	private MTS<Long, String>[] parsedModels;
	
	@SuppressWarnings("unchecked")
	private TestModels() {
		this.parsedModels = new MTS[this.getSize()];
	}
	
	private static TestModels instance = new TestModels();
	
	public static TestModels getInstance() {
		return instance;
	}

	public MTS<Long, String> getMTS(int index) throws Exception {
		
		if(this.parsedModels[index] == null) {
			MTSCompiler compiler = MTSCompiler.getInstance();
			this.parsedModels[index] = compiler.compileMTS("I" + (index + 1), this.modelsToParse[index]);
		}
		
		return this.parsedModels[index];
	}
	
	public MTS<Long, String>[] getAllModels() throws Exception {
		for(int i=0; i< this.getSize(); i++) {
			if(this.parsedModels[i] == null) {
				MTSCompiler compiler = MTSCompiler.getInstance();
				this.parsedModels[i] = new UnmodifiableMTS<Long, String>(compiler.compileMTS("I" + (i + 1), this.modelsToParse[i]));
			}
		}
		return this.parsedModels;
	}
	
	public int getSize() {
		return this.modelsToParse.length; 
	}
}
