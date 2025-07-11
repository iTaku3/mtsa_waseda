package ltsa.lts;

import org.junit.Assert;

import java.util.Hashtable;

public class SymbolTable {

    private static Hashtable<String, Integer> keyword;

    //public static void init() {
    static { //static initialiser since the language doesn't change at runtime!
        keyword = new Hashtable<String, Integer>();

        addSymbols("const", Symbol.CONSTANT);
        addSymbols("property", Symbol.PROPERTY);
        addSymbols("range", Symbol.RANGE);
        addSymbols("if", Symbol.IF);
        addSymbols("then", Symbol.THEN);
        addSymbols("else", Symbol.ELSE);
        addSymbols("forall", Symbol.FORALL);
        addSymbols("when", Symbol.WHEN);
        addSymbols("set", Symbol.SET);
        addSymbols("progress", Symbol.PROGRESS);
        addSymbols("menu", Symbol.MENU);
        addSymbols("animation", Symbol.ANIMATION);
        addSymbols("actions", Symbol.ACTIONS);
        addSymbols("controls", Symbol.CONTROLS);
        addSymbols("deterministic", Symbol.DETERMINISTIC);
        addSymbols("minimal", Symbol.MINIMAL);
        addSymbols("compose", Symbol.COMPOSE);
        addSymbols("target", Symbol.TARGET);
        addSymbols("import", Symbol.IMPORT);
        addSymbols("assert", Symbol.ASSERT);
        addSymbols("fluent", Symbol.PREDICATE);
        addSymbols("exists", Symbol.EXISTS);
        addSymbols("rigid", Symbol.RIGID);
        addSymbols("constraint", Symbol.CONSTRAINT);
        addSymbols("ltl_property", Symbol.LTLPROPERTY);
        addSymbols("initially", Symbol.INIT);
        addSymbols("optimistic", Symbol.OPTIMISTIC);
        addSymbols("pessimistic", Symbol.PESSIMISTIC);
        addSymbols("closure", Symbol.CLOSURE);
        addSymbols("abstract", Symbol.ABSTRACT);
        addSymbols("restricts", Symbol.RESTRICTS);
        addSymbols("instances", Symbol.INSTANCES);
        addSymbols("prechart", Symbol.PRECHART);
        addSymbols("mainchart", Symbol.MAINCHART);
        addSymbols("eTS", Symbol.E_TRIGGERED_SCENARIO);
        addSymbols("uTS", Symbol.U_TRIGGERED_SCENARIO);

        addSymbols("distribution", Symbol.DISTRIBUTION);
        addSymbols("systemModel", Symbol.SYSTEM_MODEL);
        addSymbols("outputFileName", Symbol.OUTPUT_FILE_NAME);
        addSymbols("distributedAlphabets", Symbol.DISTRIBUTED_ALPHABETS);
        addSymbols("def", Symbol.DEF);
        addSymbols("foreach", Symbol.FOREACH);

        addSymbols("exploration", Symbol.EXPLORATION);
        addSymbols("environment", Symbol.EXPLORATION_ENVIRONMENT);
        addSymbols("model", Symbol.EXPLORATION_MODEL);
        addSymbols("goal", Symbol.EXPLORATION_GOAL);
        addSymbols("environment_actions", Symbol.EXPLORATION_ENVIRONMENT_ACTIONS);

        addSymbols("component", Symbol.COMPONENT);
        addSymbols("condition", Symbol.CONDITION);
        addSymbols("controller", Symbol.CONTROLLER);
        addSymbols("gr", Symbol.CONTROLLER);
        addSymbols("rtc", Symbol.RTC_CONTROLLER);
        addSymbols("rtcAnalysis", Symbol.RTC_ANALYSIS_CONTROLLER);
        addSymbols("starenv", Symbol.STARENV);
        addSymbols("plant", Symbol.PLANT);
        addSymbols("controllerSpec", Symbol.GOAL);
        addSymbols("comparison", Symbol.COMPARISON);//
        addSymbols("safety", Symbol.SAFETY);
        addSymbols("buchi", Symbol.BUCHI);
        addSymbols("assumption", Symbol.ASSUME);
        addSymbols("failure", Symbol.FAULT);
        addSymbols("liveness", Symbol.GUARANTEE);
        addSymbols("controllable", Symbol.CONTROLLABLE);
        addSymbols("checkCompatibility", Symbol.CHECK_COMPATIBILITY);
        addSymbols("permissive", Symbol.PERMISSIVE);
        addSymbols("controlled_det", Symbol.CONTROLLED_DET);
        addSymbols("nonblocking", Symbol.CONTROLLER_NB);
        addSymbols("lazyness", Symbol.CONTROLLER_LAZYNESS);
        addSymbols("non_transient", Symbol.NON_TRANSIENT);
        addSymbols("reachability", Symbol.REACHABILITY);
        addSymbols("activityFluents", Symbol.ACTIVITY_FLUENTS);
        addSymbols("test_latency", Symbol.TEST_LATENCY);
        addSymbols("exceptionHandling", Symbol.EXCEPTION_HANDLING);
        addSymbols("concurrencyFluents", Symbol.CONCURRENCY_FLUENTS);
        addSymbols("controlstack", Symbol.CONTROL_STACK);
        addSymbols("tier", Symbol.CONTROL_TIER);
        addSymbols("mdp", Symbol.MDP);
        addSymbols("probabilistic", Symbol.PROBABILISTIC);

        //Updating controller problem
        addSymbols("updatingController", Symbol.UPDATING_CONTROLLER);
        addSymbols("oldController", Symbol.OLD_CONTROLLER);
        addSymbols("mapping", Symbol.MAPPING);
        addSymbols("oldGoal", Symbol.OLD_GOAL);
        addSymbols("newGoal", Symbol.NEW_GOAL);
        addSymbols("transition", Symbol.TRANSITION);


        addSymbols("graphUpdate", Symbol.GRAPH_UPDATE);
        addSymbols("initialState", Symbol.GRAPH_INITIAL_STATE);
        addSymbols("transitions", Symbol.GRAPH_TRANSITIONS);
        
        addSymbols("heuristic", Symbol.HEURISTIC);
        addSymbols("monolithicDirector", Symbol.MONOLITHIC_DIRECTOR);
        addSymbols("marking", Symbol.MARKING);
        addSymbols("disturbances", Symbol.DISTURBANCE);
        addSymbols("partialOrderReduction", Symbol.PARTIAL_ORDER_REDUCTION);
    }

    private static void addSymbols(String key, Integer elem) {
        Integer add = keyword.put(key, elem);
        if (null != add) {
            Assert.fail("The key was already present in the Table: " + key);
        }
    }

    public static Integer get(String s) {
        return keyword.get(s);
    }

//    public boolean noRepetitiveKeys() {
//
//        List<String> list = new ArrayList<String>();
//        for (int i = 0; i < keyword.size(); i++) {
//            list.add(keyword.keys().nextElement();
//        }
//        boolean result = keyword.keySet().size() == list.size();
//        return result;
//    }
}