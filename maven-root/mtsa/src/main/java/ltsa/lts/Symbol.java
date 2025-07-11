package ltsa.lts;

import java.awt.Color;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class Symbol {


	public int kind;
	public int startPos; // start offset of token in input
	public int endPos = -1; // end offset of token in input

	private String string; // holds identifiers as well as string literals
	private int longValue;
	private BigDecimal doubleValue;
	private Object any; // add additional information

	public Symbol() {
		this.kind = UNKNOWN_TYPE;
	};

	public Symbol(Symbol copy) {
		this.kind = copy.kind;
		this.startPos = copy.startPos;
		this.endPos = copy.endPos;
		this.string = copy.string;
		this.longValue = copy.longValue;
		this.doubleValue = copy.doubleValue;
		this.any = copy.any;
	}

	public Symbol(Symbol copy, String name) {
		this(copy);
		this.string = name;
	}

	public Symbol(int kind) {
		this.kind = kind;
		this.startPos = -1;
		this.string = null;
		this.longValue = 0;
		this.doubleValue = BigDecimal.ZERO;
	}

	public Symbol(int kind, String s) {
		this.kind = kind;
		this.startPos = -1;
		this.string = s;
		this.longValue = 0;
		this.doubleValue = BigDecimal.ZERO;
	}

	public Symbol(int kind, double v) {
		this.kind = kind;
		this.startPos = -1;
		this.string = null;
		this.doubleValue = new BigDecimal(v);
	}

	public Symbol(int kind, BigDecimal v) {
		this.kind= kind;
		this.startPos= -1;
		this.string= null;
		this.doubleValue= v;
	}

	public void setString(String s) {
		string = s;
	}

	public String getName() {
		return this.string;
	}

	public void setValue(BigDecimal val) {
		doubleValue= val;
	}

	public BigDecimal doubleValue() {
		return doubleValue;
	}

	public int intValue() {
		return longValue;
	}

	public void setAny(Object o) {
		any = o;
	}

	public Object getAny() {
		return any;
	}

	// _______________________________________________________________________________________
	// isScalarType

	public boolean isScalarType() {
		switch (kind) {
		case BOOLEAN_TYPE:
		case DOUBLE_TYPE:
		case INT_TYPE:
		case STRING_TYPE:
			return true;

		default:
			return false;
		}
	}

	/*--------------------------------------------------------*/

	private Color commentColor = new Color(102, 153, 153);
	private Color upperColor = new Color(0, 0, 160);
	private static Set<Integer> blueSymbols = new HashSet<Integer>();
	public Color getColor() {
		if (kind > 0 && kind <= INIT || blueSymbols.contains(kind))
			return Color.blue;
		else if (kind == COMMENT)
			return commentColor;
		else if (kind == INT_VALUE || kind == STRING_VALUE || kind == DOUBLE_VALUE)
			return Color.red;
		else if (kind == UPPERIDENT)
			return upperColor;
		else
			return Color.black;
	}

	// _______________________________________________________________________________________
	// TOSTRING

	public String toString() {
		switch (kind) {

		// _______________________________________________________________________________________
		// Keywords
		case CONSTANT:
			return "const";
		case PROPERTY:
			return "property";
		case RANGE:
			return "range";
		case IF:
			return "if";
		case THEN:
			return "then";
		case ELSE:
			return "else";
		case FORALL:
			return "forall";
		case WHEN:
			return "when";
		case SET:
			return string == null ? "set" : string;
		case PROGRESS:
			return "progress";
		case MENU:
			return "menu";
		case ANIMATION:
			return "animation";
		case ACTIONS:
			return "actions";
		case CONTROLS:
			return "controls";
		case DETERMINISTIC:
			return "determinstic";
		case OPTIMISTIC:
			return "optimistic";
		case PESSIMISTIC:
			return "pessimistic";
		case CLOSURE:
			return "clousure";
		case ABSTRACT:
			return "abstract";
		case PLUS_CA:
			return "+ca";
		case PLUS_CR:
			return "+cr";
		case MERGE:
			return "++";
		case MINIMAL:
			return "minimal";
		case COMPOSE:
			return "compose";
		case TARGET:
			return "target";
		case IMPORT:
			return "import";
		case UNTIL:
			return "U";
		case ASSERT:
			return "assert";
		case PREDICATE:
			return "fluent";
		case NEXTTIME:
			return "X";
		case EXISTS:
			return "exists";
		case RIGID:
			return "rigid";
		case CONSTRAINT:
			return "constraint";
		case LTLPROPERTY:
			return "ltl_property";
		case INIT:
			return "initially";
		case CONTROLLER:
			return "controller";
		case RTC_CONTROLLER:
			return "Synchronous Controller";
		case STARENV:
			return "starenv";
		case PLANT:
			return "plant";
		case CHECK_COMPATIBILITY:
			return "check compatibility";
		case GOAL:
			return "controller spec";
		case UPDATING_CONTROLLER:
			return "updating controller";
		case ASSUME:
			return "assumption";
		case FAULT:
			return "failure";
		case GUARANTEE:
			return "liveness";
		case SAFETY:
			return "safety";
		case OLD_CONTROLLER:
			return "old controller";
		case MAPPING:
			return "mapping";
		case OLD_GOAL:
			return "old goal";
		case NEW_GOAL:
			return "new goal";
		case TRANSITION:
			return "transition";
		case GRAPH_UPDATE:
			return "graph update";
		case GRAPH_INITIAL_STATE:
			return "initial state";
		case GRAPH_TRANSITIONS:
			return "transitions";
		case PERMISSIVE:
			return "permissive";
		case CONTROLLABLE:
			return "controllable";
		case COMPARISON:
			return "comparison";
		case CONTROLLED_DET:
			return "controllable determinisation";
		case CONTROLLER_NB:
			return "Nonblocking controller";
		case CONTROLLER_LAZYNESS:
			return "Controller Lazyness value";
		case EXCEPTION_HANDLING:
			return "Exception Handling controller";
		case CONTROL_STACK:
      return "controlstack";
		case CONTROL_TIER:
      return "tier";
		case ENACTMENT:
      return "enactment";
		case COMPONENT:
			return "component";
		case DEF:
			return "def";
		case FOREACH:
			return "foreach";
		case HEURISTIC:
			return "heuristic";
		case MARKING:
			return "marking";
		case BOOLEAN_TYPE:
			return "boolean";
		case DOUBLE_TYPE:
			return "double";
		case INT_TYPE:
			return "int";
		case STRING_TYPE:
			return "string";
		case UNKNOWN_TYPE:
			return "unknown";

			// _______________________________________________________________________________________

		case UPPERIDENT:
			return string;
		case IDENTIFIER:
			return string;
		case LABELCONST:
			return string;
		case INT_VALUE:
			return doubleValue.intValue() + "";
		case DOUBLE_VALUE:
			return doubleValue + "";
		case STRING_VALUE:
			return string;

		// _______________________________________________________________________________________
		// Charts

		case PRECHART:
			return "prechart";
		case MAINCHART:
			return "mainchart";
		case E_TRIGGERED_SCENARIO:
			return "eTS";
		case U_TRIGGERED_SCENARIO:
			return "uTS";
		case RESTRICTS:
			return "restricts";
		case INSTANCES:
			return "instances";
		case CONDITION:
			return "condition";

		// _______________________________________________________________________________________
		// Distribution
		case DISTRIBUTION:
			return "distribution";
		case SYSTEM_MODEL:
			return "systemModel";
		case OUTPUT_FILE_NAME:
			return "outputFileName";
		case DISTRIBUTED_ALPHABETS:
			return "distributedAlphabets";

		// _______________________________________________________________________________________
		// Expression symbols

		case UNARY_MINUS:
			return "-";
		case UNARY_PLUS:
			return "+";
		case PLUS:
			return "+";
		case MINUS:
			return "-";
		case STAR:
			return "*";
		case DIVIDE:
			return "/";
		case MODULUS:
			return "%";
		case POWER:
			return "$";
		case CIRCUMFLEX:
			return "^";
		case SINE:
			return "~";
		case QUESTION:
			return "?";
		case COLON:
			return ":";
		case COLON_COLON:
			return "::";
		case COMMA:
			return ",";
		case OR:
			return "||";
		case BITWISE_OR:
			return "|";
		case AND:
			return "&&";
		case BITWISE_AND:
			return "&";
		case NOT_EQUAL:
			return "!=";
		case PLING:
			return "!";
		case LESS_THAN_EQUAL:
			return "<=";
		case LESS_THAN:
			return "<";
		case SHIFT_LEFT:
			return "<<";
		case GREATER_THAN_EQUAL:
			return ">=";
		case GREATER_THAN:
			return ">";
		case SHIFT_RIGHT:
			return ">>";
		case EQUALS:
			return "==";
		case LROUND:
			return "(";
		case RROUND:
			return ")";
		case QUOTE:
			return "'";
		case HASH:
			return "#";
		case EVENTUALLY:
			return "<>";
		case ALWAYS:
			return "[]";
		case EQUIVALENT:
			return "<->";
		case WEAKUNTIL:
			return "W";

			// _______________________________________________________________________________________
			// Others

		case LCURLY:
			return "{";
		case RCURLY:
			return "}";
		case LSQUARE:
			return "[";
		case RSQUARE:
			return "]";
		case BECOMES:
			return "=";
		case SEMICOLON:
			return ";";
		case DOT:
			return ".";
		case DOT_DOT:
			return "..";
		case AT:
			return "@";
		case ARROW:
			return "->";
		case BACKSLASH:
			return "\\";
			// _______________________________________________________________________________________
			// Special

		// _______________________________________________________________________________________
		// Probabilistic
		case PROBABILISTIC:
			return "probabilistic";
		case MDP:
			return "mdp";
		case EOFSYM:
			return "EOF";
		default:
			return "ERROR";
		}
	}

	// _______________________________________________________________________________________
	// Keywords
	public static final int CONSTANT = 1;
	public static final int PROPERTY = 2;
	public static final int RANGE = 3;
	public static final int IF = 4;
	public static final int THEN = 5;
	public static final int ELSE = 6;
	public static final int FORALL = 7;
	public static final int WHEN = 8;
	public static final int SET = 9;
	public static final int PROGRESS = 10;
	public static final int MENU = 11;
	public static final int ANIMATION = 12;
	public static final int ACTIONS = 13;
	public static final int CONTROLS = 14;
	public static final int DETERMINISTIC = 15;
	public static final int MINIMAL = 16;
	public static final int COMPOSE = 17;
	public static final int TARGET = 18;
	public static final int IMPORT = 19;
	public static final int UNTIL = 20;
	public static final int ASSERT = 21;
	public static final int PREDICATE = 22;
	public static final int NEXTTIME = 23;
	public static final int EXISTS = 24;
	public static final int RIGID = 25;
	public static final int CONSTRAINT = 26;
	public static final int INIT = 27;
	public static final int LTLPROPERTY = 28;
	public static final int COMPONENT = 29;
	public static final int DEF = 30;
	public static final int FOREACH = 31;

	public static final int OPTIMISTIC = 1000;
	public static final int PESSIMISTIC = 1001;
	public static final int PLUS_CR = 1002;
	public static final int PLUS_CA = 1003;
	public static final int CLOSURE = 1004;
	public static final int ABSTRACT = 1005;
	public static final int MERGE = 1006;

	public static final int BOOLEAN_TYPE = 102;
	public static final int DOUBLE_TYPE = 103;
	public static final int INT_TYPE = 104;
	public static final int STRING_TYPE = 105;
	public static final int UNKNOWN_TYPE = 106;

	public static final int UPPERIDENT = 123;
	public static final int IDENTIFIER = 124;

	// Charts
	public static final int E_TRIGGERED_SCENARIO = 2000; // Existential Triggered Scenario 
	public static final int U_TRIGGERED_SCENARIO = 2001; // Universal Triggered Scenario
	public static final int PRECHART = 2002;
	public static final int MAINCHART = 2003;
	public static final int INSTANCES = 2004;
	public static final int RESTRICTS = 2005;
	public static final int CONDITION = 2006;
	
	// Distribution 
	public static final int DISTRIBUTION = 2500;
	public static final int DISTRIBUTED_ALPHABETS = 2501;
	public static final int SYSTEM_MODEL = 2502;
	public static final int OUTPUT_FILE_NAME = 2503;

	//ControllerSynthesis
	public static final int CONTROLLER = 3000;
	public static final int GOAL = 3001;
	public static final int SAFETY = 3002;
	public static final int ASSUME = 3003;
	public static final int GUARANTEE = 3004;
	public static final int CONTROLLABLE = 3005;
	public static final int FAULT = 3006;
	public static final int CHECK_COMPATIBILITY = 3007;
	public static final int STARENV = 3008;
	public static final int PLANT = 3009;
	public static final int PERMISSIVE = 3010;
	public static final int CONTROLLED_DET = 3011;
	public static final int CONTROLLER_NB = 3012;
	public static final int EXCEPTION_HANDLING = 3013;
	public static final int RTC_CONTROLLER = 3014;
	public static final int CONCURRENCY_FLUENTS = 3015;
	public static final int CONTROLLER_LAZYNESS = 3016;
	public static final int NON_TRANSIENT = 3017;
	public static final int REACHABILITY = 3018;
	public static final int TEST_LATENCY = 3019;
	public static final int ACTIVITY_FLUENTS = 3020;
	public static final int BUCHI = 3021;
	public static final int RTC_ANALYSIS_CONTROLLER = 3022;

	public static final int COMPARISON = 3023;//

	public static final int CONTROL_STACK = 3101;
	public static final int CONTROL_TIER = 3102;
	
	public static final int ENACTMENT = 3103; 
	// _______________________________________________________________________________________
	// Updating controller symbols
	
	public static final int UPDATING_CONTROLLER = 3200; // 3200 series are for updating controller problem
	public static final int OLD_CONTROLLER = 3201;
	public static final int MAPPING = 3202;
	public static final int OLD_GOAL = 3205;
	public static final int NEW_GOAL = 3206;
	public static final int TRANSITION = 3207; //transition T

	//public static final int UPDATE_FLUENTS = 3208;
	public static final int GRAPH_UPDATE = 3211;
	public static final int GRAPH_INITIAL_STATE = 3212;
	public static final int GRAPH_TRANSITIONS = 3213;
	
	// Exploration
	public static final int EXPLORATION = 3300;
	public static final int EXPLORATION_ENVIRONMENT = 3301;
	public static final int EXPLORATION_MODEL = 3302;
	public static final int EXPLORATION_GOAL = 3303;
	public static final int EXPLORATION_ENVIRONMENT_ACTIONS = 3304;

	// Heuristic control synthesis
	public static final int HEURISTIC = 3400;
	public static final int MARKING = 3401;
	public static final int MONOLITHIC_DIRECTOR = 3402;
	public static final int DISTURBANCE = 3403;
	public static final int PARTIAL_ORDER_REDUCTION = 3404;
	
	// _______________________________________________________________________________________
	// Expression symbols

	public static final int UNARY_MINUS = 33; // unary -
	public static final int UNARY_PLUS = 34; // unary +
	public static final int PLUS = 35; // +
	public static final int MINUS = 36; // + -
	public static final int STAR = 37; // *
	public static final int DIVIDE = 38; // /
	public static final int MODULUS = 39; // %
	public static final int CIRCUMFLEX = 40; // ^
	public static final int SINE = 41; // ~
	public static final int QUESTION = 42; // 3
	public static final int COLON = 43; // :
	public static final int COMMA = 44; // ,
	public static final int OR = 45; // ||
	public static final int BITWISE_OR = 46; // |
	public static final int AND = 47; // &&
	public static final int BITWISE_AND = 48; // &
	public static final int NOT_EQUAL = 49; // !=
	public static final int PLING = 50; // !
	public static final int LESS_THAN_EQUAL = 51; // <=
	public static final int LESS_THAN = 52; // <
	public static final int SHIFT_LEFT = 53; // <<
	public static final int GREATER_THAN_EQUAL = 54; // >=
	public static final int GREATER_THAN = 55; // >
	public static final int SHIFT_RIGHT = 56; // >>
	public static final int EQUALS = 57; // ==
	public static final int LROUND = 58; // (
	public static final int RROUND = 59; // )
	public static final int POWER= 6000; // **

	// _______________________________________________________________________________________
	// Others

	public static final int LCURLY = 60; // {
	public static final int RCURLY = 61; // }
	public static final int LSQUARE = 62; // [
	public static final int RSQUARE = 63; // ]
	public static final int BECOMES = 64; // =
	public static final int SEMICOLON = 65; // ;
	public static final int DOT = 66; // .
	public static final int DOT_DOT = 67; // ..
	public static final int AT = 68; // @
	public static final int ARROW = 69; // ->
	public static final int BACKSLASH = 70; // \
	public static final int COLON_COLON = 71; // ::
	public static final int QUOTE = 72; // '
	public static final int HASH = 73; // #

	// ________________________________________________________________________________________
	// Linear Temporal Logic Symbols
	public static final int EVENTUALLY = 74; // <>
	public static final int ALWAYS = 75; // []
	public static final int EQUIVALENT = 76; // <->
	public static final int WEAKUNTIL = 77; // W
	// _______________________________________________________________________________________
	// Special
	public static final int LABELCONST = 98;
	public static final int EOFSYM = 99;
	public static final int COMMENT = 100;

	// _______________________________________________________________________________________
	// _______________________________________________________________________________________

	public static final int INT_VALUE = 125;
	public static final int DOUBLE_VALUE = 126;
	public static final int STRING_VALUE = 127;

	// _______________________________________________________________________________________
	// Probabilistic
	public static final int PROBABILISTIC= 5000;
	public static final int MDP= 5001;
	
	
	static {
		blueSymbols.add(OPTIMISTIC);
		blueSymbols.add(COMPARISON);//
		blueSymbols.add(PESSIMISTIC);
		blueSymbols.add(CLOSURE);
		blueSymbols.add(ABSTRACT);
		blueSymbols.add(GOAL);
		blueSymbols.add(ASSUME);
		blueSymbols.add(BUCHI);
		blueSymbols.add(GUARANTEE);
		blueSymbols.add(SAFETY);
		blueSymbols.add(CONTROLLER);
		blueSymbols.add(CONTROLLABLE);
		blueSymbols.add(FAULT);
		blueSymbols.add(CHECK_COMPATIBILITY);
		blueSymbols.add(LTLPROPERTY);
		blueSymbols.add(COMPONENT);
		blueSymbols.add(U_TRIGGERED_SCENARIO);
		blueSymbols.add(E_TRIGGERED_SCENARIO);
		blueSymbols.add(PRECHART);
		blueSymbols.add(MAINCHART);
		blueSymbols.add(RESTRICTS);
		blueSymbols.add(DISTRIBUTION);
		blueSymbols.add(SYSTEM_MODEL);
		blueSymbols.add(OUTPUT_FILE_NAME);
		blueSymbols.add(DISTRIBUTED_ALPHABETS);
		blueSymbols.add(INSTANCES);
		blueSymbols.add(CONDITION);
		blueSymbols.add(PROBABILISTIC);
		blueSymbols.add(MDP);
		blueSymbols.add(STARENV);
		blueSymbols.add(PLANT);
		blueSymbols.add(PERMISSIVE);
		blueSymbols.add(CONTROLLED_DET);
		blueSymbols.add(CONTROLLER_NB);
		blueSymbols.add(EXCEPTION_HANDLING);
		blueSymbols.add(CONTROL_STACK);
		blueSymbols.add(CONTROL_TIER);
		blueSymbols.add(RTC_CONTROLLER);
		blueSymbols.add(RTC_ANALYSIS_CONTROLLER);
		blueSymbols.add(CONCURRENCY_FLUENTS);
		blueSymbols.add(ACTIVITY_FLUENTS);
		blueSymbols.add(UPDATING_CONTROLLER);
		blueSymbols.add(CONTROLLER_LAZYNESS);
		blueSymbols.add(NON_TRANSIENT);
		blueSymbols.add(DEF);
		blueSymbols.add(REACHABILITY);
		blueSymbols.add(TEST_LATENCY);
		blueSymbols.add(OLD_CONTROLLER);
		blueSymbols.add(OLD_GOAL);
		blueSymbols.add(NEW_GOAL);
		blueSymbols.add(TRANSITION);
		blueSymbols.add(MAPPING);
		blueSymbols.add(GRAPH_UPDATE);
		blueSymbols.add(GRAPH_INITIAL_STATE);
		blueSymbols.add(GRAPH_TRANSITIONS);
		blueSymbols.add(FOREACH);
		blueSymbols.add(HEURISTIC);
		blueSymbols.add(MONOLITHIC_DIRECTOR);
		blueSymbols.add(PARTIAL_ORDER_REDUCTION);
		blueSymbols.add(MARKING);
		blueSymbols.add(DISTURBANCE);
		blueSymbols.add(ENACTMENT);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Symbol symbol = (Symbol) o;

		if (kind != symbol.kind) {
			return false;
		}
		return !(string != null ? !string.equals(symbol.string) : symbol.string != null);
	}

	@Override
	public int hashCode() {
		int result = kind;
		result = 31 * result + (string != null ? string.hashCode() : 0);
		return result;
	}
}