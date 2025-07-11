package ltsa.ui;

import MTSTools.ac.ic.doc.mtstools.model.operations.DCS.blocking.Statistics;
import MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking.DCSWithFeatures;
import MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking.FeatureBasedExplorationHeuristic;
import jargs.gnu.CmdLineParser;
import jargs.gnu.CmdLineParser.Option;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

import MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking.DirectedControllerSynthesisNonBlocking;
import MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking.DirectedControllerSynthesisNonBlocking.HeuristicMode;
import ltsa.dispatcher.TransitionSystemDispatcher;
import ltsa.lts.*;

public class LTSABatch
implements LTSManager, LTSInput, LTSOutput, LTSError {

	CompositeState current = null;
	Set<String> compositeNames = new HashSet<String>();
	String currentDirectory = System.getProperty("user.dir");
	Hashtable<String,LabelSet> labelSetConstants = null;
//	SETCompositionalBatch compositionBatch = null;
	String model = "";
	int fPos = -1;
	String fSrc = "\n";

	public LTSABatch (String fileTxt, int modulesCount, boolean memo, boolean ref, String heur, boolean proj) {
		//SymbolTable.init();
//		compositionBatch =
//			new SETCompositionalBatch(this,this,this,this,true,
//								      memo, ref, heur,proj,modulesCount);
		model = fileTxt;
	}

	public void out ( String str ) {
		System.out.print(str);
	}

	public void outln ( String str ) {
		System.out.println(str);
	}

	public void clearOutput () {
		//not needed
	}

	public char nextChar () {
		fPos = fPos + 1;
		if (fPos < fSrc.length ()) {
			return fSrc.charAt (fPos);
		} else {
			//fPos = fPos - 1;
			return '\u0000';
		}
	}

	public char backChar () {
		fPos = fPos - 1;
		if (fPos < 0) {
			fPos = 0;
			return '\u0000';
		}
		else
			return fSrc.charAt (fPos);
	}

	public int getMarker () {
		return fPos;
	}

	public void resetMarker () {
		fPos = -1;
	}


	private void safety() {
		safety(true, false);
	}
	private void safety(boolean checkDeadlock, boolean multiCe) {
		compile();
		if (current != null) {
			//no sirve asi!
			current.analyse(checkDeadlock, this);
		}
	}

	private void compile () {
		if (!parse()) return;
		current = docompile();
	}

	public void displayError(LTSException x) {
		outln("ERROR - "+x.getMessage());
	}

	private CompositeState docompile() {
		fPos = -1;
        fSrc = model;
		CompositeState cs=null;
		LTSCompiler comp=new LTSCompiler(this,this,currentDirectory);
		try {
			comp.compile();
			cs = comp.continueCompilation("ALL");
		} catch (LTSException x) {
			displayError(x);
		}
		return cs;
	}

	private Hashtable doparse () {
		Hashtable cs = new Hashtable();
		Hashtable ps = new Hashtable();
		doparse(cs,ps);
		return cs;
	}

	private void doparse(Hashtable cs, Hashtable ps) {
		fPos = -1;
        fSrc = model;
		LTSCompiler comp = new LTSCompiler(this,this,currentDirectory);
		try {
			comp.parse(cs,ps,null);
		} catch (LTSException x) {
			displayError(x);
			cs=null;
		}
	}

	public void compileIfChange () {
		//not needed
	}

	public boolean parse() {
		// >>> AMES: Enhanced Modularity
		Hashtable cs = new Hashtable();
		Hashtable ps = new Hashtable();
		doparse(cs,ps);
		// <<< AMES

		if (cs==null) return false;
		if (cs.size()==0) {
			compositeNames.add("DEFAULT");
		} else  {
			Enumeration e = cs.keys();
			java.util.List forSort = new ArrayList();
			while( e.hasMoreElements() ) {
				forSort.add( e.nextElement() );
			}
			Collections.sort( forSort );
			for( Iterator i = forSort.iterator() ; i.hasNext() ; ) {
				compositeNames.add((String)i.next());
			}
		}
		current = null;

		return true;
	}

	public CompositeState compile(String name) {
		fPos = -1;
        fSrc = model;
		CompositeState cs=null;
		LTSCompiler comp = new LTSCompiler(this,this,currentDirectory);
		try {
			comp.compile();
			cs = comp.continueCompilation(name);
		} catch (LTSException x) {
			displayError(x);
		}
		return cs;
	}

	/**
	 * Returns the current labeled transition system.
	 */
	public CompositeState getCurrentTarget() {
		return current;
	}

	public Set<String> getCompositeNames() {
		return compositeNames;
	}

	/**
	 * Returns the set of actions which correspond to the label set definition
	 * with the given name.
	 */
	public Set<String> getLabelSet(String name) {
		if (labelSetConstants == null)
			return null;

		Set<String> s = new HashSet<String>();
		LabelSet ls = labelSetConstants.get(name);

		if (ls == null)
			return null;

		for ( String a : (Vector<String>) ls.getActions(null) )
			s.add(a);

		return s;
	}

	public void performAction (final Runnable r, final boolean showOutputPane) {
		//not needed
	}

	public String getTargetChoice() {
		//not needed
		return "";
	}

	public void newMachines(java.util.List<CompactState> machines) {
		//not needed
	}

	private static void showUsage() {
		String usage= "MTSA usage:\n" +
			"\tmtsa.jar -i <FSP FILE> -c <CONTROLLER> -o <OUTPUT FILE> [-m|-d|-g] \n\n" +
			"Options:\n\n" +
			"-c <CONTROLLER>: Specifies the controller goal in use. \n\n" +
			"-o <OUTPUT FILE>: Specifies the output format (*.aut for Aldebaran, " +
			"*.pddl for Planning Domain Definition Language, " +
			"*.xml for a Supremica project, *.smv for MBP, and *.py for Party-elli).\n\n" +
			"-m: Use monotonic abstraction during directed synthesis (ready abstraction by default).\n\n" +
			"-d: Skips synthesis during compositional translation.\n\n" +
			"-g: Launches the IDE.\n\n";
		System.out.print(usage);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CmdLineParser cmdParser= new CmdLineParser();
		Option filename = cmdParser.addStringOption('i', "file");
		Option controller = cmdParser.addStringOption('c', "controller");
		Option output = cmdParser.addStringOption('o', "output");
		Option monotonic = cmdParser.addBooleanOption('m', "monotonic");
		Option ready = cmdParser.addBooleanOption('r', "ready");
		Option dummy = cmdParser.addBooleanOption('d', "dummy");
		Option bfs = cmdParser.addBooleanOption('b', "bfs");
		Option debugging = cmdParser.addBooleanOption('a', "debugging");
		Option gui = cmdParser.addBooleanOption('g', "gui");
		Option help = cmdParser.addBooleanOption('h', "help");

		try {
			cmdParser.parse(args);
		} catch (CmdLineParser.OptionException e) {
			System.out.println("Invalid option: " + e.getMessage() + "\n");
			showUsage();
			System.exit(0);
		}

		String filenameValue = (String)cmdParser.getOptionValue(filename);
		String outputValue = (String)cmdParser.getOptionValue(output);
		String controllerValue = (String)cmdParser.getOptionValue(controller);
		Boolean monotonicValue = (Boolean)cmdParser.getOptionValue(monotonic);
		Boolean readyValue = (Boolean)cmdParser.getOptionValue(ready);
		Boolean bfsValue = (Boolean)cmdParser.getOptionValue(bfs);
		Boolean debuggingValue = (Boolean)cmdParser.getOptionValue(debugging);
		Boolean guiValue = (Boolean)cmdParser.getOptionValue(gui);
		Boolean dummyValue = (Boolean)cmdParser.getOptionValue(dummy);
		Boolean helpValue = (Boolean)cmdParser.getOptionValue(help);

		if (helpValue != null && helpValue) {
			showUsage();
			System.exit(0);
		}

		if (controllerValue == null || controllerValue.isEmpty())
			guiValue = true; // if no controller is selected open gui

		if (guiValue != null && guiValue) {
			String[] arg = {filenameValue};
			HPWindow.main(arg);

		} else {
			String fileTxt = readFile(filenameValue);

			if (dummyValue != null && dummyValue) // skip synthesis (useful for problem translation).
				DirectedControllerSynthesisNonBlocking.mode = HeuristicMode.Dummy;
			else if (monotonicValue != null && monotonicValue)
				DirectedControllerSynthesisNonBlocking.mode = HeuristicMode.Monotonic;
			else if (readyValue != null && readyValue)
				DirectedControllerSynthesisNonBlocking.mode = HeuristicMode.Ready;
			else if (bfsValue != null && bfsValue)
				DirectedControllerSynthesisNonBlocking.mode = HeuristicMode.BFS;
			else if (debuggingValue != null && debuggingValue)
				DirectedControllerSynthesisNonBlocking.mode = HeuristicMode.Debugging;
			process(fileTxt, controllerValue, outputValue);

			System.exit(0);
		}
	}

	private static String readFile(String filename) {
		String result = null;
		try {
			BufferedReader file = new BufferedReader(new FileReader(filename));
			String thisLine;
			StringBuffer buff = new StringBuffer();
			while ((thisLine = file.readLine()) != null)
				buff.append(thisLine+"\n");
			file.close();
			result = buff.toString();
		} catch (Exception e) {
			System.err.print("Error reading FSP file " + filename + ": " + e);
			System.exit(1);
		}
		return result;
	}

	private static void process(final String fileTxt, String controller, String outputFile) {
		CompositeState compositeState= null;
		try {
			compositeState= compileCompositeState(fileTxt, controller);
		} catch (Exception e) {
			System.err.print("Error compiling FSP: " + e);
			System.exit(1);
		}

		if (outputFile == null)
			return;

		int i = outputFile.lastIndexOf(".");
		if (i == -1) {
			System.out.println("Invalid extension for output file.\n");
			showUsage();
			System.exit(1);
		}
		String extension = outputFile.substring(i, outputFile.length());
		AbstractTranslator translator = null;
		switch (extension) {
			case ".xml":   translator = new XMLTranslator(); break;
			case ".pddl":  translator = new PDDLTranslator(true); break;
			//case ".npddl": translator = new NPDDLTranslator(); break;
			case ".smv":   translator = new SMVTranslator(); break;
			case ".py":    translator = new CTLPYTranslator(); break;
			//case ".xltl":  translator = new XLTLTranslator(); break;
			case ".slugs": translator = new SlugsTranslator(); break;
			case ".fsp":   translator = new FSPTranslator(); break;
			default:
				System.out.println("Invalid extension for output file.\n");
				showUsage();
				System.exit(1);
		}

		try {
			PrintStream outStream = new PrintStream(new FileOutputStream(new File(outputFile)));
			if (translator != null)
				translator.translate(compositeState, outStream);
			else
				compositeState.composition.printAUT(outStream);
			outStream.close();
			if (translator instanceof PDDLTranslator) {
				String problemFilePath = outputFile.replace(".pddl", "-p.pddl");
				outStream = new PrintStream(new FileOutputStream(problemFilePath));
				outStream.print(((PDDLTranslator)translator).getProblem());
				outStream.close();
			}
		} catch (Exception e) {
			System.err.print("Error while exporting to file " + outputFile);
			System.exit(1);
		}
	}


	private static CompositeState compileCompositeState(String inputString, String modelName) throws IOException {
		return compileComposite(modelName, new LTSInputString(inputString));
	}

	public static CompositeState compileComposite(String modelName, LTSInput input)
			throws IOException {
		LTSOutput output = new StandardOutput();
		String currentDirectory = (new File(".")).getCanonicalPath();
		LTSCompiler compiler = new LTSCompiler( input , output , currentDirectory );
		//lts.SymbolTable.init();
		compiler.compile();

		long startedComposition = System.currentTimeMillis();
		CompositeState c = compiler.continueCompilation(modelName);
		long endedComposition = System.currentTimeMillis();

		Statistics stats = new Statistics();
		TransitionSystemDispatcher.applyComposition(c, output, stats);

		if(stats.getExpandedStates() != 0){
			//we have stats to output
			System.out.print(stats.toString());
			System.out.print("Composition time: " + (endedComposition - startedComposition) + " ms");
		}
		return c;
	}
}
