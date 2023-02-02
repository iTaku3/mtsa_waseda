package parsing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.*;
import org.eclipse.xtext.xbase.lib.Pair;

import tau.smlab.syntech.gameinput.model.Constraint;
import tau.smlab.syntech.gameinput.model.GameInput;
import tau.smlab.syntech.gameinput.model.Player;
import tau.smlab.syntech.gameinput.model.Variable;
import tau.smlab.syntech.gameinputtrans.TranslationProvider;
import tau.smlab.syntech.spectragameinput.ErrorsInSpectraException;
import tau.smlab.syntech.spectragameinput.SpectraInputProviderNoIDE;
import tau.smlab.syntech.spectragameinput.SpectraTranslationException;

/**
 * Example of how to parse spectra specifications.
 */
public class SpectraToMTSATtranslator {

	private static List<String> playerNames = Arrays.asList("sys","aux","env");
	private static int safetyAssumptions = 0;
	private static int justiceAssumptions = 0;
	private static int safetyGuarantees = 0;
	private static int justiceGuarantees = 0;
	private static int initialConstraints = 0;
	private static String folder = "SYNTECH15";
	
	public static void main(String[] args) throws ErrorsInSpectraException, SpectraTranslationException {

	    String currentPath = new File("").getAbsolutePath();
		File benchmark = new File(currentPath.concat("/"+folder));
		List<String> namesList = new ArrayList<String>(Arrays.asList(benchmark.list()));
		for (String auxName : namesList) {
			if (auxName.equals("unrealizable")) {
				File benchmarkUnrealizable = new File(currentPath.concat("/"+folder+"/unrealizable"));
				namesList.remove(auxName);
				namesList.addAll(Arrays.asList(benchmarkUnrealizable.list()).stream().map(s -> "unrealizable/"+s).collect(Collectors.toList()));
				break;
			}
		}
		for (String name : namesList) {
			if(name.equals("unrealizable") 
					|| name.substring(3,14).equals("SelfParking")) {continue;}
			name = name.substring(0, name.length() - 8);//remove .spectra
			
			//for debugging
			//name = "ColorSortLTL2TAG_789_ColorSort";
			
			String specPath = folder + "/" + name + ".spectra";
	
			// get the Xtext-based input parser
			SpectraInputProviderNoIDE sip = new SpectraInputProviderNoIDE();
			// parse (via Xtext) and translate to abstract syntax (Xtext independent)
			GameInput gi = sip.getGameInput(specPath);
	
			System.out.println("\nTranslating " +name+ " to Spectra Kernel.");
			// important step to reduce language features to the Spectra Kernel
			TranslationProvider.translate(gi);
			
			translateToFSP(name, gi);
		}
	}

	private static void translateToFSP(String name, GameInput gi) {
		safetyAssumptions = 0;
		justiceAssumptions = 0;
		safetyGuarantees = 0;
		justiceGuarantees = 0;
		initialConstraints = 0;
		
		String filename = "./translated/" + folder + "/" + name + ".fsp";
		PrintWriter out;
		File f = new File(filename);
		if(!f.isFile()) { 
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {			
			Map<String, List<MyVar>> playersMyVars = new HashMap<String, List<MyVar>>(Map.of(
					"sys",new ArrayList<MyVar>(),
					"aux",new ArrayList<MyVar>(),
					"env",new ArrayList<MyVar>()));			
			List<String> sysActions = getActions(gi.getSys().getVars(), playersMyVars.get("sys"));
			List<String> auxActions = getActions(gi.getAux().getVars(), playersMyVars.get("aux"));
			List<String> envActions = getActions(gi.getEnv().getVars(), playersMyVars.get("env"));
			sysActions.add("tick");
			envActions.add("tock");
			List<String> controllableActions = new ArrayList<String>(sysActions);
			controllableActions.addAll(auxActions);
			
			out = new PrintWriter(f);
			out.println("//" + name + " automatically_translated \n");
			printActions(out, controllableActions, "ControlledActions");
			printActions(out, envActions, "UncontrolledActions");
			out.println("set AllActions = {ControlledActions, UncontrolledActions}\n");
			
			printVars(out, playersMyVars);
			
			printClock(out);
			
			printInitialValues(out, gi);
			
			printGuarantees(out, gi);
			printAssumptions(out, gi);
			
			printCompositionsAndGoal(out, gi);
			
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
			fixNumbers(Path.of(filename));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void fixNumbers(Path filepath) throws IOException {
		//this is needed because some names in spectra look like sys_const.5.name
		//but in MTSA we need them to be sys_const[5].name
		String aux = Files.readString(filepath);
		Pattern p = Pattern.compile("\\.(\\d+)\\.");
		Matcher m = p.matcher(aux);
		String ans = m.replaceAll(match -> "["+match.group(1)+"].");
		Files.writeString(filepath, ans);
	}
	
	private static void printCompositionsAndGoal(PrintWriter out, GameInput gi) {
		String initials = initialConstraints>0 ? "|| Initial_Values " : ""; 
		out.println("\n\n||Domain_independent = (Full_Clock).\n"
				+ "||Env = (Domain_independent " + initials + "||\n"
				+ "		"+getSafety() + "). //safety assumptions and guarantees\n"
				+ "\n"
				+ "controllerSpec Goal = {\n"
				+ "       assumption = {"
				+ getLivenessAsm(false) + "}  //user liveness assumptions + A_clock\n"
				+ "       liveness = {"+getLivenessGar(false)+"}  //user liveeness guarantees\n"
				+ "       controllable = {ControlledActions}\n"
				+ "}\n\n"
				+ "heuristic ||Control = (Env)~{Goal}.\n"
				+ "checkCompatibility ||CheckControl = (Env)~{Goal}.\n"
				+ "\n"
				+ "||System = (Control || Env).\n"
				+ "\n"
				+ "assert Check = (("+getLivenessAsm(true)+") -> ("+getLivenessGar(true)+"))\n"
				+ "assert ASM =   ("+getLivenessAsm(true)+")\n"
				+ "assert GNT = ("+getLivenessGar(true)+")\n"
				+ "progress Time  = {tick}");
	}
	
	private static String getLivenessGar(boolean isForAssert) {
		List<String> justice = new ArrayList<String>();
		String extra = isForAssert ? "[]<>" : "";
		for(Integer i=0; i<justiceGuarantees; i++) {
			justice.add(extra+"G_l"+i.toString());
		}
		if(isForAssert) {
			return justice.stream().collect(Collectors.joining(" && "));
		}else {
			return justice.stream().collect(Collectors.joining(", "));
		}
	}
	
	private static String getLivenessAsm(boolean isForAssert) {
		List<String> justice = new ArrayList<String>();
		String extra = isForAssert ? "[]<>" : "";
		for(Integer i=0; i<justiceAssumptions; i++) {
			justice.add(extra+"A_l"+i.toString());
		}
		justice.add("A_clock");
		if(isForAssert) {
			return justice.stream().collect(Collectors.joining(" && "));
		}else {
			return justice.stream().collect(Collectors.joining(", "));
		}
	}
	
	private static String getSafety() {
		List<String> safety = new ArrayList<String>();
		for(Integer i=0; i<safetyAssumptions; i++) {
			safety.add("A"+i.toString()+"_min");
		}
		for(Integer i=0; i<safetyGuarantees; i++) {
			safety.add("G"+i.toString()+"_min");
		}
		return safety.stream().collect(Collectors.joining(" || "));
	}
	
	private static String minimal(String prop) {
		return "minimal ||"+ prop + "_min = "+ prop + ".";
	}
	
	private static void printGuarantees(PrintWriter out, GameInput gi) { 
		List<Player> guaranteePlayers = Arrays.asList(gi.getSys(), gi.getAux());
		for (Player p : guaranteePlayers) {
			for (Constraint cons : p.getConstraints()) {
				if (cons.isSafety()) {
					MyConstraint myCons = new MyConstraint(cons,"tick",safetyGuarantees);
					safetyGuarantees += myCons.getSubConstraints().size();
					out.println("//property divided into " + myCons.getSubConstraints().size() + " subproperties.");
					for (Pair<String, String> subCons : myCons.getSubConstraints()) {
						out.println("ltl_property " + subCons.getKey() + " = "+ subCons.getValue());
						out.println(minimal(subCons.getKey()));
					}
				} else if(cons.isJustice()) {
					MyConstraint myCons = new MyConstraint(cons,"tick",justiceGuarantees);
					justiceGuarantees += myCons.getSubConstraints().size();
					out.println("//property divided into " + myCons.getSubConstraints().size() + " subproperties.");
					for (Pair<String, String> subCons : myCons.getSubConstraints()) {
						out.println("assert " + subCons.getKey() + " = "+ subCons.getValue());
					}
				}
				out.println("");
			}
		}
		if(justiceGuarantees == 0) {//if there are no justice guarantees, add a trivial one
			out.println("fluent True = <tick, tock>");
			out.println("assert G_l0 = (True || !True)");
			justiceGuarantees += 1;
		}
	}
	
	private static void printAssumptions(PrintWriter out, GameInput gi) { 
		for (Constraint cons : gi.getEnv().getConstraints()) {
			if (cons.isSafety()) {
				MyConstraint myCons = new MyConstraint(cons,"tock",safetyAssumptions);
				safetyAssumptions += myCons.getSubConstraints().size();
				out.println("//property divided into " + myCons.getSubConstraints().size() + " subproperties.");
				for (Pair<String, String> subCons : myCons.getSubConstraints()) {
					out.println("constraint " + subCons.getKey() + " = "+ subCons.getValue());
					out.println(minimal(subCons.getKey()));
				}
			} else if(cons.isJustice()) {
				MyConstraint myCons = new MyConstraint(cons,"tock",justiceAssumptions);
				justiceAssumptions += myCons.getSubConstraints().size();
				out.println("//property divided into " + myCons.getSubConstraints().size() + " subproperties.");
				for (Pair<String, String> subCons : myCons.getSubConstraints()) {
					out.println("assert " + subCons.getKey() + " = "+ subCons.getValue());
				}
			}
			out.println("");
		}
	}
	
	private static void printInitialValues(PrintWriter out, GameInput gi) {
		List<String> initialNames = new ArrayList<String>(); 
		for (String pName : playerNames) {
			Player p = gi.getPlayer(pName);
			String typeOfProp = "ltl_property";
			String clock = "tick";
			if (pName == "env") {
				typeOfProp = "constraint";
				clock = "tock";
			}
			for (Constraint cons : p.getConstraints()) {
				if (cons.isInitial()) {
					MyConstraint myCons = new MyConstraint(cons, clock, initialConstraints);
					initialConstraints += myCons.getSubConstraints().size();
					out.println("//property divided into " + myCons.getSubConstraints().size() + " subproperties.");
					for (Pair<String, String> subCons : myCons.getSubConstraints()) {
						out.println(typeOfProp + subCons.getKey() + " = "+ subCons.getValue());
						out.println(minimal(subCons.getKey()));
						initialNames.add(subCons.getKey()+"_min");
					}
				}
			}
		}
		if (initialConstraints > 0) { //if there are initial constraints
			String composition = initialNames.stream().collect(Collectors.joining(" || "));
			out.println("//=======Starting values=======\n"
						+ "||Initial_Values = ("+composition+").\n\n");
		}
	}
	
	private static void printClock(PrintWriter out) {
		out.println("\n\n// move from synchronous play to asynchronous representation\n"
				+ "Clock = Env_turn,\n"
				+ "Env_turn = (tock -> Sys_turn | {UncontrolledActions}\\{tock} -> Env_turn),\n"
				+ "Sys_turn = (tick -> Env_turn | {ControlledActions}\\{tick} -> Sys_turn).\n"
				+ "\n"
				+ "Turns(N=1) = Controller[N],\n"
				+ "Controller[i:1..N] = ({ControlledActions}\\{tick} -> Controller[i-1] | tick -> Controller[N]),\n"
				+ "Controller[0] = (tick -> Controller[N]).\n"
				+ "\n"
				+ "||Full_Clock = (Clock || Turns(#ControlledActions)).//|| UTurns(#UncontrolledActions+2)).\n"
				+ "\n"
				+ "//fluent Tick = <tick, AllActions\\{tick}>\n"
				+ "\n"
				+ "//assumption, env eventually gives control to sys\n"
				+ "fluent Tock = <tock, tick>\n"
				+ "assert A_clock = (Tock)\n"
				+ "\n"
				+ "//==================LTL Properties=============================\n");
	}
	
	private static void printVars(PrintWriter out, Map<String, List<MyVar>> playersMyVars) {
		for(List<MyVar> player : playersMyVars.values()) {
			for(MyVar v : player) {
				String varActions = v.getName().replaceAll("\\.", "_")+"_Actions";
				out.println("\n"+"set "+varActions+" = {"+v.printActions()+"}");
				for(String fluent : v.getFluents()) {
					out.println("fluent "+fluent.replaceAll("\\.", "_")+
							" = <"+fluent.toLowerCase()+", "+varActions+"\\{"+fluent.toLowerCase()+"}>");
				}
			}
		}
	}
	
	//Also build Set of MyVar for the respective player
	private static List<String> getActions(List<Variable> vars, List<MyVar> playersMyVars){
		List<String> answer = new ArrayList<String>();
		for (Variable var : vars) {
			List<String> current = var.getActions();
			answer.addAll(current);
			playersMyVars.add(new MyVar(var.getName(), current, var.getType()));
		}
		return answer;
	}
	
	private static void printActions(PrintWriter out, List<String> actions, String name) {
		String ans = actions.stream().collect(Collectors.joining(", "));
		out.println("set "+name+" = {"+ans+"}");
	}
}
