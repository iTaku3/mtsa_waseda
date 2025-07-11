package ltsa.lts;

import MTSSynthesis.ar.dc.uba.model.condition.Fluent;
import MTSSynthesis.ar.dc.uba.model.condition.FluentImpl;
import MTSSynthesis.ar.dc.uba.model.condition.Formula;
import MTSSynthesis.controller.util.GeneralConstants;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import ltsa.control.ControlStackDefinition;
import ltsa.control.ControlTierDefinition;
import ltsa.control.ControllerDefinition;
import ltsa.control.ControllerGoalDefinition;
import ltsa.dispatcher.TransitionSystemDispatcher;
import ltsa.exploration.ExplorerDefinition;
import ltsa.lts.chart.*;
import ltsa.lts.chart.util.TriggeredScenarioTransformationException;
import ltsa.lts.distribution.DistributionDefinition;
import ltsa.lts.ltl.*;
import ltsa.lts.util.LTSUtils;
import ltsa.ui.StandardOutput;
import ltsa.updatingControllers.structures.UpdateGraphDefinition;
import ltsa.updatingControllers.synthesis.UpdateGraphGenerator;
import ltsa.updatingControllers.synthesis.UpdatingControllersAnimatorUtils;
import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.PredicateUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static ltsa.lts.util.MTSUtils.getMaybeAction;
import static ltsa.lts.util.MTSUtils.getOpositeActionLabel;

public class LTSCompiler {

    private Lex lex;
    private LTSOutput output;
    private String currentDirectory;
    private Symbol current;

    static Hashtable<String, ProcessSpec> processes;
    static Hashtable<String, CompactState> compiled;
    static Hashtable<String, CompositionExpression> composites;
    static Hashtable<String, ExplorerDefinition> explorers;
    private static Hashtable<String, CompositionExpression> allComposites;

    private int compositionType = -1;

    public LTSCompiler(LTSInput input, LTSOutput output, String currentDirectory) {
        lex = new Lex(input);
        this.output = output;
        this.currentDirectory = currentDirectory;
        Diagnostics.init(output);
        SeqProcessRef.output = output;
        StateMachine.output = output;
        Expression.constants = new Hashtable();
        Range.ranges = new Hashtable();
        LabelSet.constants = new Hashtable();
        ProgressDefinition.definitions = new Hashtable();
        MenuDefinition.definitions = new Hashtable();
        Def.init();
        PredicateDefinition.init();
        AssertDefinition.init();
        TriggeredScenarioDefinition.init();
        ControllerDefinition.init();
        ControllerGoalDefinition.init();
        ControlStackDefinition.initDefinitionList();
        DistributionDefinition.init();
    }

    public static LTSCompiler getCompiler(LTSInput input) throws IOException {
        LTSCompiler compiler = new LTSCompiler(input, new StandardOutput(), (new File(".")).getCanonicalPath());
        return compiler;
    }

    private Symbol next_symbol() {
        return (current = lex.next_symbol());
    }

    private void push_symbol() {
        lex.push_symbol();
    }

    public Hashtable<String, CompositionExpression> getComposites() {
        return composites;
    }

    public Hashtable<String, ExplorerDefinition> getExplorers() {
        return explorers;
    }

    public Hashtable<String, ProcessSpec> getProcesses() {
        return processes;
    }

    public static CompositionExpression getComposite(String name) {
        return name != null ? allComposites.get(name) : null;
    }

    public static Hashtable<String, CompactState> getCompiled() {
        return compiled;
    }

    public static Hashtable<String, CompositionExpression> getAllComposites() {
        return allComposites;
    }

    private void error(String errorMsg) {
        Diagnostics.fatal(errorMsg, current);
    }

    private void current_is(int kind, String errorMsg) {
        if (current.kind != kind)
            error(errorMsg);
    }

    private void current_is(Collection<Integer> possibleKind, String errorMsg) {

        if (!CollectionUtils.exists(possibleKind, PredicateUtils.equalPredicate(new Integer(current.kind)))) {
            error(errorMsg);
        }
    }

    /**
     * Compiles the process specified by <i>name</i>.
     *
     * @return
     */
    public void compile() {
        processes = new Hashtable<>(); // processes
        composites = new Hashtable<>(); // composites
        explorers = new Hashtable<>();
        compiled = new Hashtable<>(); // compiled
        allComposites = new Hashtable<>(); // All composites

        doparse(composites, processes, compiled);
    }

    public CompositeState continueCompilation(String name) {

        ProgressDefinition.compile();
        MenuDefinition.compile();
        PredicateDefinition.compileAll();
        AssertDefinition.compileAll(output);
        CompositionExpression ce = composites.get(name);
        if (ce == null && composites.size() > 0) {
            if (explorers.containsKey(name)) {
                ExplorerDefinition explorerDefinition = explorers.get(name);
                Enumeration<CompositionExpression> e = composites.elements();
                CompositionExpression fce = e.nextElement();

                ce = new CompositionExpression();
                ce.name = new Symbol(123, name);
                ce.processes = fce.processes;
                ce.setComposites(fce.getComposites());
                ce.output = fce.output;
                ce.priorityIsLow = true;
                ce.compositionType = 45;
                ce.makeController = true;
                ce.goal = explorerDefinition.getGoal();
                ce.compiledProcesses = new Hashtable<String, CompactState>(0);

                ce.body = new CompositeBody();
                ce.body.procRefs = new Vector<CompositeBody>(explorerDefinition.getView().size() + 1);

                for (int i = 0; i < explorerDefinition.getView().size(); i++) {
                    CompositeBody aCompositeBody = new CompositeBody();
                    aCompositeBody.singleton = new ProcessRef(false, true);
                    aCompositeBody.singleton.name = explorerDefinition.getView().get(i);
                    ce.body.procRefs.add(aCompositeBody);
                }

                for (int i = 0; i < explorerDefinition.getModel().size(); i++) {
                    CompositeBody aCompositeBody = new CompositeBody();
                    aCompositeBody.singleton = new ProcessRef(false, true);
                    aCompositeBody.singleton.name = explorerDefinition.getModel().get(i);
                    ce.body.procRefs.add(aCompositeBody);
                }
            } else {
                Enumeration<CompositionExpression> e = composites.elements();
                ce = e.nextElement();
            }
        }
        if (ce != null) {
            //Is a composition expression.
            return ce.compose(null);
        } else {

            if (explorers.containsKey(name)) {
                ExplorerDefinition explorerDefinition = explorers.get(name);

                ce = new CompositionExpression();
                ce.name = new Symbol(123, name);
                ce.processes = processes;
                ce.output = output;
                ce.priorityIsLow = true;
                ce.compositionType = 45;
                ce.makeController = true;
                ce.goal = explorerDefinition.getGoal();
                ce.compiledProcesses = new Hashtable<String, CompactState>(0);

                ce.body = new CompositeBody();
                ce.body.procRefs = new Vector<CompositeBody>(explorerDefinition.getView().size() + 1);

                for (int i = 0; i < explorerDefinition.getView().size(); i++) {
                    CompositeBody aCompositeBody = new CompositeBody();
                    aCompositeBody.singleton = new ProcessRef(false, true);
                    aCompositeBody.singleton.name = explorerDefinition.getView().get(i);
                    ce.body.procRefs.add(aCompositeBody);
                }

                for (int i = 0; i < explorerDefinition.getModel().size(); i++) {
                    CompositeBody aCompositeBody = new CompositeBody();
                    aCompositeBody.singleton = new ProcessRef(false, true);
                    aCompositeBody.singleton.name = explorerDefinition.getModel().get(i);
                    ce.body.procRefs.add(aCompositeBody);
                }

                return ce.compose(null);
            }

            // There is no composite expression.
            try {
                // All scenarios are synthesised
                this.addAllToCompiled(TriggeredScenarioDefinition.synthesiseAll(output));
            } catch (TriggeredScenarioTransformationException e) {
                throw new RuntimeException(e);
            }

            // All Distributions are compiled
            // try to distribute
            Set<DistributionDefinition> allDistributionDefinitions = DistributionDefinition.getAllDistributionDefinitions();
            for (DistributionDefinition aDistributionDefinition : allDistributionDefinitions) {
                Symbol systemModelId = aDistributionDefinition.getSystemModel();
                // check if the system model has been compiled
                CompactState systemModel = (CompactState) compiled.get(systemModelId.getName());
                if (systemModel == null) {
                    // it needs to be compiled
                    systemModel = this.compileSingleProcess((ProcessSpec) processes.get(systemModelId.getName()));
                }
                Collection<CompactState> distributedComponents = new HashSet<CompactState>();
                boolean isDistributionSuccessful = TransitionSystemDispatcher.tryDistribution(systemModel, aDistributionDefinition, output, distributedComponents);

                // Add the distributed components as compiled
                for (CompactState component : distributedComponents) {
                    compiled.put(component.getName(), component); // add to compiled process
                }

                if (!isDistributionSuccessful) {
                    Diagnostics.fatal("Model " + systemModelId.getName() + " could not be distributed.", systemModelId);
                }
            }

            // All processes are compiled.
            compileProcesses(processes, compiled);
            return noCompositionExpression(compiled);
        }
    }

    public static void makeFluents(Symbol symbol, Set<Fluent> involvedFluents) {
        AssertDefinition def = AssertDefinition.getDefinition(symbol.toString());
        if (def != null && !symbol.toString().equals(GeneralConstants.FALSE) && !symbol.toString().equals(GeneralConstants.TRUE)) {
            adaptFormulaAndCreateFluents(def.getFormula(true), involvedFluents);
        } else {
            PredicateDefinition fdef = PredicateDefinition.get(symbol.toString());
            if (fdef != null) {
                adaptFormulaAndCreateFluents(new FormulaFactory().make(symbol), involvedFluents);
            } else if (symbol.toString().equals(GeneralConstants.FALSE)) {
                involvedFluents.add(new FluentImpl(symbol.toString(), new HashSet<MTSSynthesis.ar.dc.uba.model.language.Symbol>(), new HashSet<MTSSynthesis.ar.dc.uba.model.language.Symbol>(), false));
            } else if (symbol.toString().equals(GeneralConstants.TRUE)) {
                involvedFluents.add(new FluentImpl(symbol.toString(), new HashSet<MTSSynthesis.ar.dc.uba.model.language.Symbol>(), new HashSet<MTSSynthesis.ar.dc.uba.model.language.Symbol>(), true));
            } else {
                //Diagnostics.fatal("Assertion not defined [" + guaranteeDefinition.getName() + "].");
                Diagnostics.fatal("Fluent/assertion not defined [" + symbol + "].");
            }
        }

    }

    /**
     * Returns a CompactState representation of a process given its name or null if no such process is present
     *
     * @param processName the name of the process to be returned as a compactState
     * @return a CompactState representation of an existing process give its name
     */
    public CompactState getProcessCompactStateByName(String processName) {
        if (!processes.containsKey(processName))
            return null;
        ProcessSpec processSpec = processes.get(processName);
        CompactState compiled;
        compiled = getCompactState(processSpec);
        return compiled;
    }

    private static Formula adaptFormulaAndCreateFluents(ltsa.lts.ltl.Formula formula, Set<Fluent> involvedFluents) {
        // create a visitor for the formula
        FormulaTransformerVisitor formulaTransformerVisitor = new FormulaTransformerVisitor();
        formula.accept(formulaTransformerVisitor);

        // After visiting the formula, the visitor has the transformed formula and the involved fluents

        involvedFluents.addAll(formulaTransformerVisitor.getInvolvedFluents());
        return formulaTransformerVisitor.getTransformedFormula();
    }

    /**
     * Add all the CompactState in compiledToBeAdded to the compiled table with
     * the name of the CompactState as key.
     *
     * @param compiledToBeAdded
     */
    private void addAllToCompiled(Collection<CompactState> compiledToBeAdded) {
        for (CompactState compactState : compiledToBeAdded) {
            compiled.put(compactState.getName(), compactState);
        }
    }

    // put compiled definitions in Hashtable compiled
    private void compileProcesses(Hashtable<String, ProcessSpec> h, Hashtable<String, CompactState> compiled) {
        for (ProcessSpec processSpec : h.values()) {
            CompactState compiledProcess = this.compileSingleProcess(processSpec);
            compiled.put(compiledProcess.name, compiledProcess);
        }
        /*CompactState compiledProcess;
        Enumeration e = h.elements();
		while (e.hasMoreElements()) { //***
			processSpec = (ProcessSpec) e.nextElement();
			compiledProcess = this.compileSingleProcess(processSpec);
			compiled.put(compiledProcess.name, compiledProcess);
		}*/
        AssertDefinition.compileConstraints(output, compiled);
    }

    private CompactState compileSingleProcess(ProcessSpec processSpec) {
        CompactState compiled;
        compiled = getCompactState(processSpec);
        return compiled;
    }

    private CompactState getCompactState(ProcessSpec processSpec) {
        CompactState compiled;
        if (!processSpec.imported()) {
//            compiled = processSpec.makeCompactState();
            StateMachine one = new StateMachine(processSpec);
            compiled = one.makeCompactState();
            output.outln("Compiled: " + compiled.name);

        } else {
            compiled = new AutCompactState(processSpec.name, processSpec.importFile);
            output.outln("Imported: " + compiled.name);
        }
        return compiled;
    }

    public void parse(Hashtable<String, CompositionExpression> composites, Hashtable<String, ProcessSpec> processes, Hashtable<String, ExplorerDefinition> explorations) {
        doparse(composites, processes, null);
    }

    private void doparse(Hashtable<String, CompositionExpression> composites, Hashtable<String, ProcessSpec> processes, Hashtable<String, CompactState> compiled) {
        ProbabilisticTransition.setLastProbBundle(ProbabilisticTransition.NO_BUNDLE);
        next_symbol();
        try {
            while (current.kind != Symbol.EOFSYM) {
                if (current.kind == Symbol.CONSTANT) {
                    next_symbol();
                    constantDefinition(Expression.constants);
                } else if (current.kind == Symbol.RANGE) {
                    next_symbol();
                    rangeDefinition();
                } else if (current.kind == Symbol.SET) {
                    next_symbol();
                    setDefinition();
                } else if (current.kind == Symbol.PROGRESS) {
                    next_symbol();
                    progressDefinition();
                } else if (current.kind == Symbol.MENU) {
                    next_symbol();
                    menuDefinition();
                } else if (current.kind == Symbol.ANIMATION) {
                    next_symbol();
                    animationDefinition();
                } else if (current.kind == Symbol.ASSERT) {
                    next_symbol();
                    assertDefinition(false, false);
                } else if (current.kind == Symbol.CONSTRAINT) {
                    next_symbol();
                    assertDefinition(true, false);
                } else if (current.kind == Symbol.LTLPROPERTY) {
                    next_symbol();
                    assertDefinition(true, true);
                } else if (current.kind == Symbol.PREDICATE) {
                    next_symbol();
                    predicateDefinition();
                } else if (current.kind == Symbol.DEF) {
                    next_symbol();
                    defDefinition();
                } else if (current.kind == Symbol.GOAL) {
                    next_symbol();

                    current_is(Symbol.UPPERIDENT, "goal identifier expected");

                    this.validateUniqueProcessName(current);
                    ControllerGoalDefinition goal = new ControllerGoalDefinition(current);
                    this.goalDefinition(goal);

                } else if (current.kind == Symbol.EXPLORATION) {
                    next_symbol();

                    current_is(Symbol.UPPERIDENT, "exploration identifier expected");

                    this.validateUniqueProcessName(current);
                    ExplorerDefinition explorerDefinition = new ExplorerDefinition(current);
                    next_symbol();

                    this.explorerDefinition(explorerDefinition);

                    output.outln("Explorer: " + explorerDefinition.getName());

                } else if (current.kind == Symbol.UPDATING_CONTROLLER) {
                    next_symbol();

                    current_is(Symbol.UPPERIDENT, "updating controller identifier expected");

                    UpdatingControllersDefinition cuDefinition = new UpdatingControllersDefinition(current);

                    this.updateControllerDefinition(cuDefinition);

                    UpdatingControllersAnimatorUtils.updateDefinitions.put(cuDefinition.getName().getName(), cuDefinition);

                    if (composites.put(cuDefinition.getName().getName(), cuDefinition) != null) {
                        Diagnostics.fatal("duplicate composite definition: " + cuDefinition.getName(), cuDefinition.getName());
                    } else {
                        if (allComposites != null) {
                            allComposites.put(cuDefinition.getName().getName(), cuDefinition);
                        }
                    }

                } else if (current.kind == Symbol.GRAPH_UPDATE) {
                    expectIdentifier("Graph Update");
                    UpdateGraphDefinition graphDefinition = new UpdateGraphDefinition(current.getName());
                    expectBecomes();
                    expectLeftCurly();
                    graphDefinition.setInitialProblem(parseInitialState());
                    graphDefinition.setTransitions(parseTransitions());
                    expectRightCurly();
                    UpdateGraphGenerator.addGraphDefinition(graphDefinition);
                } else if (current.kind == Symbol.CONTROL_STACK) {

                    ControlStackDefinition def = this.controlStackDefinition();
                    ControlStackDefinition.addDefinition(def);

                    CompositionExpression c = new CompositionExpression();
                    c.name = def.getName();
                    c.setComposites(composites);
                    c.processes = processes;
                    c.compiledProcesses = compiled;
                    c.controlStackEnvironments = new Vector<Symbol>();
                    for (ControlTierDefinition tier : def) {
                        c.controlStackEnvironments.add(tier.getEnvModel());
                    }
                    c.output = output;
                    c.makeControlStack = true;
                    if (allComposites != null) {
                        allComposites.put(c.name.toString(), c);
                    }
                    if (composites.put(c.name.toString(), c) != null) {
                        Diagnostics.fatal("duplicate composite definition: " + c.name, c.name);
                    }

                } else if (current.kind == Symbol.IMPORT) {
                    next_symbol();
                    ProcessSpec p = importDefinition();
                    if (processes.put(p.name.toString(), p) != null) {
                        Diagnostics.fatal(
                                "duplicate process definition: " + p.name, p.name);
                    }
                } else if (current.kind == Symbol.E_TRIGGERED_SCENARIO) {
                    next_symbol();
                    // Check the syntax
                    current_is(Symbol.UPPERIDENT, "chart identifier expected");

                    this.validateUniqueProcessName(current);

                    // create the existential triggeredScenario with the given
                    // identifier
                    TriggeredScenarioDefinition eTSDefinition = new ExistentialTriggeredScenarioDefinition(current);

                    next_symbol();
                    this.triggeredScenarioDefinition(eTSDefinition);
                } else if (current.kind == Symbol.U_TRIGGERED_SCENARIO) {
                    next_symbol();
                    // Check the syntax
                    current_is(Symbol.UPPERIDENT, "chart identifier expected");

                    this.validateUniqueProcessName(current);

                    // create the universal triggered Scenario with the given
                    // identifier
                    TriggeredScenarioDefinition uTSDefinition = new UniversalTriggeredScenarioDefinition(current);

                    next_symbol();
                    this.triggeredScenarioDefinition(uTSDefinition);
                } else if (current.kind == Symbol.DISTRIBUTION) {
                    this.distributionDefinition();
                } else if (current.kind == Symbol.DETERMINISTIC
                        || current.kind == Symbol.MINIMAL || current.kind == Symbol.PROPERTY
                        || current.kind == Symbol.COMPOSE || current.kind == Symbol.OPTIMISTIC
                        || current.kind == Symbol.PESSIMISTIC || LTSUtils.isCompositionExpression(current)
                        || current.kind == Symbol.CLOSURE || current.kind == Symbol.ABSTRACT
                        || current.kind == Symbol.CONTROLLER || current.kind == Symbol.CHECK_COMPATIBILITY
                        || current.kind == Symbol.COMPONENT || current.kind == Symbol.PROBABILISTIC
                        || current.kind == Symbol.MDP || current.kind == Symbol.STARENV
                        || current.kind == Symbol.PLANT || current.kind == Symbol.CONTROLLED_DET
                        || current.kind == Symbol.RTC_CONTROLLER
                        || current.kind == Symbol.RTC_ANALYSIS_CONTROLLER
                        || current.kind == Symbol.HEURISTIC
                        || current.kind == Symbol.MONOLITHIC_DIRECTOR
                        || current.kind == Symbol.PARTIAL_ORDER_REDUCTION
                        ) {
                    // TODO: refactor needed. Some of the operations can be combined, however
                    // the parser does not allow some valid combinations. Also the order of the operations
                    // is not kept when the operations are applied

                    boolean makeDet = false;
                    boolean makeMin = false;
                    boolean makeProp = false;
                    boolean makeComp = false;
                    boolean makeOptimistic = false;
                    boolean makePessimistic = false;
                    boolean makeClousure = false;
                    boolean makeAbstract = false;
                    boolean makeController = false;
                    boolean makeRTCController = false;
                    boolean makeRTCAnalysisController = false;
                    boolean checkCompatible = false;
                    boolean makeComponent = false;
                    boolean probabilistic = false;
                    boolean isMDP = false;
                    boolean isEnactment = false;
                    boolean makeStarEnv = false;
                    boolean makePlant = false;
                    boolean makeControlledDet = false;
                    boolean isHeuristic = false;
                    boolean isMonolithicDirector = false;
                    boolean isPartialOrderReduction = false;
                    Symbol controlledActions = null;
                    LabelSet actionsToErrorSet = null;

                    if (current.kind == Symbol.CLOSURE) {
                        makeClousure = true;
                        next_symbol();
                    }
                    if (current.kind == Symbol.ABSTRACT) {
                        makeAbstract = true;
                        next_symbol();
                    }
                    if (current.kind == Symbol.DETERMINISTIC) {
                        makeDet = true;
                        next_symbol();
                    }
                    if (current.kind == Symbol.MINIMAL) {
                        makeMin = true;
                        next_symbol();
                    }
                    if (current.kind == Symbol.COMPOSE) {
                        makeComp = true;
                        next_symbol();
                    }
                    if (current.kind == Symbol.PROPERTY) {
                        makeProp = true;
                        next_symbol();
                    }
                    if (current.kind == Symbol.OPTIMISTIC) {
                        makeOptimistic = true;
                        next_symbol();
                    }
                    if (current.kind == Symbol.PESSIMISTIC) {
                        makePessimistic = true;
                        next_symbol();
                    }
                    if (current.kind == Symbol.COMPONENT) {
                        makeComponent = true;
                        next_symbol();
                    }
                    if (current.kind == Symbol.CONTROLLER) {
                        makeController = true;
                        next_symbol();
                    }
                    if (current.kind == Symbol.RTC_CONTROLLER) {
                        makeRTCController = true;
                        next_symbol();
                    }
                    if (current.kind == Symbol.RTC_ANALYSIS_CONTROLLER) {
                        makeRTCAnalysisController = true;
                        next_symbol();
                    }
                    if (current.kind == Symbol.STARENV) {
                        makeStarEnv = true;
                        next_symbol();
                    }
                    if (current.kind == Symbol.PLANT) {
                        makePlant = true;
                        next_symbol();
                    }
                    if (current.kind == Symbol.CHECK_COMPATIBILITY) {
                        checkCompatible = true;
                        next_symbol();
                    }
                    if (current.kind == Symbol.CONTROLLED_DET) {
                        makeControlledDet = true;
                        next_symbol();
                    }
                    if (current.kind == Symbol.PROBABILISTIC) {
                        probabilistic = true;
                        next_symbol();
                    }
                    if (current.kind == Symbol.MDP) {
                        isMDP = true;
                        next_symbol();
                    }
                    if (current.kind == Symbol.HEURISTIC) {
    					isHeuristic = true;
    					next_symbol();
    				}
                    if (current.kind == Symbol.MONOLITHIC_DIRECTOR) {
                        isMonolithicDirector = true;
                        next_symbol();
                    }
                    if (current.kind == Symbol.PARTIAL_ORDER_REDUCTION) {
                        isPartialOrderReduction = true;
                        next_symbol();
                    }
                    if (current.kind == Symbol.ENACTMENT) {
                        isEnactment = true;
                        next_symbol();
                        if (current.kind == Symbol.LCURLY) {
                            next_symbol();
                            controlledActions = current;
                            next_symbol();
                            //}
                            next_symbol();
                        }
                    }

                    if (current.kind != Symbol.OR && current.kind != Symbol.PLUS_CA
                            && current.kind != Symbol.PLUS_CR && current.kind != Symbol.MERGE) {
                        ProcessSpec p = stateDefns();
                        if (processes.put(p.name.toString(), p) != null) {
                            Diagnostics.fatal("duplicate process definition: " + p.name, p.name);
                        }
                        p.isProperty = makeProp;
                        p.isMinimal = makeMin;
                        p.isDeterministic = makeDet;
                        p.isOptimistic = makeOptimistic;
                        p.isPessimistic = makePessimistic;
                        p.isClousure = makeClousure;
                        p.isAbstract = makeAbstract;
                        p.isProbabilistic = probabilistic;
                        p.isMDP = isMDP;
                        p.isStarEnv = makeStarEnv;

                        if (makeController || checkCompatible || makePlant || makeControlledDet || makeRTCController) {
                            Diagnostics.fatal("The operation requires a composite model.");
                        }

                        if (makeComponent) {
                            Diagnostics.fatal("A component can only be created from a composite model.");
                        }

                        if (probabilistic && (makeProp || makeMin || makeDet || makeOptimistic ||
                                makePessimistic || makeClousure || makeAbstract)) {
                            Diagnostics.fatal("Probabilistic automata cannot be combined with other options.");
                        }

                        if (probabilistic != isMDP) { // x to account for future probabilistic variations
                            Diagnostics.fatal("Probabilistic automata must be one of: mdp.");
                        }
                    } else if (LTSUtils.isCompositionExpression(current)) {
                        CompositionExpression c = composition();
                        c.setComposites(composites);
                        c.processes = processes;
                        c.compiledProcesses = compiled;
                        c.output = output;
                        c.makeDeterministic = makeDet;
                        c.makeProperty = makeProp;
                        c.makeMinimal = makeMin;
                        c.makeCompose = makeComp;
                        c.makeOptimistic = makeOptimistic;
                        c.makePessimistic = makePessimistic;
                        c.makeClousure = makeClousure;
                        c.makeAbstract = makeAbstract;
                        c.makeMDP = isMDP;
                        c.isProbabilistic = probabilistic;
                        c.makeEnactment = isEnactment;
                        c.enactmentControlled = controlledActions;
                        c.makeController = makeController;
                        c.makeRTCController = makeRTCController;
                        c.makeRTCAnalysisController = makeRTCAnalysisController;
                        c.checkCompatible = checkCompatible;
                        c.isStarEnv = makeStarEnv;
                        c.isPlant = makePlant;
                        c.isControlledDet = makeControlledDet;
                        c.isHeuristic = isHeuristic;
                        c.isMonolithicDirector = isMonolithicDirector;
                        c.isPartialOrderReduction = isPartialOrderReduction;
                        c.setMakeComponent(makeComponent);
                        c.compositionType = compositionType;

                        compositionType = -1;
                        if (allComposites != null) {
                            allComposites.put(c.name.toString(), c);
                        }
                        if (composites.put(c.name.toString(), c) != null) {
                            Diagnostics.fatal("duplicate composite definition: " + c.name, c.name);
                        }
                    }
                } else {
                    ProcessSpec p = stateDefns();
                    if (processes.put(p.name.toString(), p) != null) {
                        Diagnostics.fatal(
                                "duplicate process definition: " + p.name, p.name);
                    }
                }

                next_symbol();
            }
        } catch (DuplicatedTriggeredScenarioDefinitionException e) {
            Diagnostics.fatal("duplicate Chart definition: " + e.getName());
        }
    }

    private LabelSet buildActionsToErrorSet() {
        next_symbol();
        return labelSet();
    }

    private CompositeState noCompositionExpression(Hashtable<String, CompactState> h) {
        Vector<CompactState> v = new Vector<CompactState>(16);
        v.addAll(h.values());
        return new CompositeState(v);
    }

    private CompositionExpression composition() {
        current_is(Symbol.OR, "|| expected");
        next_symbol();
        CompositionExpression c = new CompositionExpression();
        current_is(Symbol.UPPERIDENT, "process identifier expected");
        c.name = current;
        next_symbol();
        paramDefns(c.init_constants, c.parameters);
        current_is(Symbol.BECOMES, "= expected");
        next_symbol();
        c.body = compositebody();
        c.priorityActions = priorityDefn(c);

        this.priorizeMaybeActions(c.priorityActions);
        if (current.kind == Symbol.BACKSLASH || current.kind == Symbol.AT) {
            c.exposeNotHide = (current.kind == Symbol.AT);
            next_symbol();
            c.alphaHidden = labelSet();
//			this.hideMaybeActions(c.alphaHidden);
        }


        if (Symbol.PLING == current.kind) {
            c.actionsToErrorSet = this.buildActionsToErrorSet();
        }

        //Controller Synthesis
        if (Symbol.SINE == current.kind) {
            parseControllerGoal(c);
        }

        if (Symbol.BITWISE_OR == current.kind) {
            next_symbol();
            this.parseComponentAlphabet(c);
        }
        current_is(Symbol.DOT, "dot expected");
        return c;
    }

    private void parseComponentAlphabet(CompositionExpression c) {
        c.setComponentAlphabet(this.labelSet());
    }

    private CompositeBody compositebody() {
        CompositeBody b = new CompositeBody();
        if (current.kind == Symbol.IF) {
            next_symbol();
            b.boolexpr = new Stack<Symbol>();
            expression(b.boolexpr);
            current_is(Symbol.THEN, "keyword then expected");
            next_symbol();
            b.thenpart = compositebody();
            if (current.kind == Symbol.ELSE) {
                next_symbol();
                b.elsepart = compositebody();
            }
        } else if (current.kind == Symbol.FORALL) {
            next_symbol();
            b.range = forallRanges();
            b.thenpart = compositebody();
        } else {
            // get accessors if any
            if (isLabel()) {
                ActionLabels el = labelElement();
                if (current.kind == Symbol.COLON_COLON) {
                    b.accessSet = el;
                    next_symbol();
                    if (isLabel()) {
                        b.prefix = labelElement();
                        current_is(Symbol.COLON, " : expected");
                        next_symbol();
                    }
                } else if (current.kind == Symbol.COLON) {
                    b.prefix = el;
                    next_symbol();
                } else
                    error(" : or :: expected");
            }
            if (current.kind == Symbol.LROUND) {
                b.procRefs = processRefs();
                b.relabelDefns = relabelDefns();
            } else {
                b.singleton = processRef();
                b.relabelDefns = relabelDefns();
            }
        }
        return b;
    }

    private ActionLabels forallRanges() {
        current_is(Symbol.LSQUARE, "range expected");
        ActionLabels head = range();
        ActionLabels next = head;
        while (current.kind == Symbol.LSQUARE) {
            ActionLabels t = range();
            next.addFollower(t);
            next = t;
        }
        return head;
    }

    private Vector<CompositeBody> processRefs() {
        Vector<CompositeBody> procRefs = new Vector<CompositeBody>();
        current_is(Symbol.LROUND, "( expected");
        next_symbol();
        if (current.kind != Symbol.RROUND) {
            procRefs.addElement(compositebody());
            while (LTSUtils.isCompositionExpression(current)) {
                next_symbol();
                procRefs.addElement(compositebody());
            }
            current_is(Symbol.RROUND, ") expected");
        }
        next_symbol();
        return procRefs;
    }

    private Vector<RelabelDefn> relabelDefns() {
        if (current.kind != Symbol.DIVIDE)
            return null;
        next_symbol();
        return relabelSet();
    }

    private LabelSet priorityDefn(CompositionExpression c) {
        if (current.kind != Symbol.SHIFT_RIGHT
                && current.kind != Symbol.SHIFT_LEFT)
            return null;
        if (current.kind == Symbol.SHIFT_LEFT)
            c.priorityIsLow = false;
        next_symbol();
        return labelSet();
    }

    private Vector<RelabelDefn> relabelSet() {
        current_is(Symbol.LCURLY, "{ expected");
        next_symbol();
        Vector<RelabelDefn> v = new Vector<RelabelDefn>();
        relabelBoth(v, relabelDefn());
        while (current.kind == Symbol.COMMA) {
            next_symbol();
            relabelBoth(v, relabelDefn());
        }
        current_is(Symbol.RCURLY, "} expected");
        next_symbol();
        return v;
    }

    private void relabelBoth(Vector<RelabelDefn> v, RelabelDefn relabelDefn) {
        v.addElement(relabelDefn);
        this.relabelMTS(v, relabelDefn);
    }

    private RelabelDefn relabelDefn() {
        RelabelDefn r = new RelabelDefn();
        if (current.kind == Symbol.FORALL) {
            next_symbol();
            r.range = forallRanges();
            r.defns = relabelSet();
        } else {
            r.newlabel = labelElement();
            current_is(Symbol.DIVIDE, "\\ expected");
            next_symbol();
            r.oldlabel = labelElement();
        }
        return r;
    }

    private ProcessRef processRef() {
        ProcessRef p = new ProcessRef();
        current_is(Symbol.UPPERIDENT, "process identifier expected");
        p.name = current;
        next_symbol();
        p.actualParams = actualParameters();
        if (current.kind != Symbol.RROUND) {
            compositionType = current.kind;
        }
        return p;
    }

    private Vector<Stack<Symbol>> actualParameters() {
        if (current.kind != Symbol.LROUND)
            return null;
        Vector<Stack<Symbol>> v = new Vector<Stack<Symbol>>();
        next_symbol();
        Stack<Symbol> stk = new Stack<Symbol>();
        expression(stk);
        v.addElement(stk);
        while (current.kind == Symbol.COMMA) {
            next_symbol();
            stk = new Stack<Symbol>();
            expression(stk);
            v.addElement(stk);
        }
        current_is(Symbol.RROUND, ") - expected");
        next_symbol();
        return v;
    }

    private ProcessSpec stateDefns() {
        ProcessSpec p = new ProcessSpec();
        current_is(Symbol.UPPERIDENT, "process identifier expected");
        Symbol temp = current;
        next_symbol();
        paramDefns(p.init_constants, p.parameters);
        push_symbol();
        current = temp;
        p.stateDefns.addElement(stateDefn());
        if (p.stateDefns.size() > 0 && p.stateDefns.get(0).stateExpr.choices != null) {
            if (p.stateDefns.get(0).stateExpr.choices.get(0) instanceof ProbabilisticChoiceElement) {
                p.isProbabilistic = true;
            }
        }
        while (current.kind == Symbol.COMMA) {
            next_symbol();
            p.stateDefns.addElement(stateDefn());
        }
        if (current.kind == Symbol.PLUS) {
            next_symbol();
            p.alphaAdditions = labelSet();
            // this.addMaybesToAlphabet(p);
        }
        p.alphaRelabel = relabelDefns();
        if (current.kind == Symbol.BACKSLASH || current.kind == Symbol.AT) {
            p.exposeNotHide = (current.kind == Symbol.AT);
            next_symbol();
            p.alphaHidden = labelSet();
//			this.hideMaybeActions(p.alphaHidden);
        }

        if (Symbol.PLING == current.kind) {
            next_symbol();
            p.actionsToErrorSet = labelSet();

        }
        if (Symbol.SINE == current.kind) {
            parseControllerGoal(p);
        }

        p.getname();
        current_is(Symbol.DOT, "dot expected");
        return p;
    }

    private void parseControllerGoal(ProcessSpec p) {
        expectLeftCurly();
        next_symbol();
        current_is(Symbol.UPPERIDENT, "goal identifier expected");
        p.goal = current;
        next_symbol();
        current_is(Symbol.RCURLY, "} expected");
        next_symbol();
    }

    //TODO reutilizar codigo,los metodos son iguales. Pueden hacer una interfaz hasGoal que tenga el metodo setGoal()
    private void parseControllerGoal(CompositionExpression c) {
        expectLeftCurly();
        next_symbol();
        current_is(Symbol.UPPERIDENT, "goal identifier expected");
        c.goal = current;
        next_symbol();
        current_is(Symbol.RCURLY, "} expected");
        next_symbol();
    }

    private boolean isLabelSet() {
        if (current.kind == Symbol.LCURLY)
            return true;
        if (current.kind != Symbol.UPPERIDENT)
            return false;
        return LabelSet.constants.containsKey(current.toString());
    }

    private boolean isLabel() {
        return (isLabelSet() || current.kind == Symbol.IDENTIFIER || current.kind == Symbol.LSQUARE);
    }

    private ProcessSpec importDefinition() {
        current_is(Symbol.UPPERIDENT, "imported process identifier expected");
        ProcessSpec p = new ProcessSpec();
        p.name = current;
        expectBecomes();
        next_symbol();
        current_is(Symbol.STRING_VALUE, " - imported file name expected");
        p.importFile = new File(currentDirectory, current.toString());
        return p;
    }

    private void animationDefinition() {
        current_is(Symbol.UPPERIDENT, "animation identifier expected");
        MenuDefinition m = new MenuDefinition();
        m.name = current;
        expectBecomes();
        next_symbol();
        current_is(Symbol.STRING_VALUE, " - XML file name expected");
        m.params = current;
        next_symbol();
        if (current.kind == Symbol.TARGET) {
            next_symbol();
            current_is(Symbol.UPPERIDENT, " - target composition name expected");
            m.target = current;
            next_symbol();
        }
        if (current.kind == Symbol.COMPOSE) {
            expectLeftCurly();
            next_symbol();
            current_is(Symbol.UPPERIDENT, "animation name expected");
            Symbol name = current;
            next_symbol();
            m.addAnimationPart(name, relabelDefns());
            while (LTSUtils.isOrSymbol(current)) {
                next_symbol();
                current_is(Symbol.UPPERIDENT, "animation name expected");
                name = current;
                next_symbol();
                m.addAnimationPart(name, relabelDefns());
            }
            current_is(Symbol.RCURLY, "} expected");
            next_symbol();
        }
        if (current.kind == Symbol.ACTIONS) {
            next_symbol();
            m.actionMapDefn = relabelSet();
        }
        if (current.kind == Symbol.CONTROLS) {
            next_symbol();
            m.controlMapDefn = relabelSet();
        }
        push_symbol();
        if (MenuDefinition.definitions.put(m.name.toString(), m) != null) {
            Diagnostics.fatal("duplicate menu/animation definition: " + m.name,
                    m.name);
        }
    }

    private void menuDefinition() {
        current_is(Symbol.UPPERIDENT, "menu identifier expected");
        MenuDefinition m = new MenuDefinition();
        m.name = current;
        expectBecomes();
        next_symbol();
        m.actions = labelElement();
        push_symbol();
        if (MenuDefinition.definitions.put(m.name.toString(), m) != null) {
            Diagnostics.fatal("duplicate menu/animation definition: " + m.name,
                    m.name);
        }
    }

    private void progressDefinition() {
        current_is(Symbol.UPPERIDENT, "progress test identifier expected");
        ProgressDefinition p = new ProgressDefinition();
        p.name = current;
        next_symbol();
        if (current.kind == Symbol.LSQUARE)
            p.range = forallRanges();
        current_is(Symbol.BECOMES, "= expected");
        next_symbol();
        if (current.kind == Symbol.IF) {
            next_symbol();
            p.pactions = labelElement();
            current_is(Symbol.THEN, "then expected");
            next_symbol();
            p.cactions = labelElement();
        } else {
            p.pactions = labelElement();
        }
        if (ProgressDefinition.definitions.put(p.name.toString(), p) != null) {
            Diagnostics.fatal("duplicate progress test: " + p.name, p.name);
        }
        push_symbol();
    }

    private void setDefinition() {
        current_is(Symbol.UPPERIDENT, "set identifier expected");
        Symbol temp = current;
        expectBecomes();
        next_symbol();
        LabelSet ls = new LabelSet(temp, setValue());
        push_symbol();
    }

    private LabelSet labelSet() {
        return labelSet(false);
    }
    private LabelSet labelSet(boolean consumedLCurly) {
        if (consumedLCurly)
            return  new LabelSet(setValueHavingConsumedLCURLY());
        else if (current.kind == Symbol.LCURLY)
            return new LabelSet(setValue());
        else if (current.kind == Symbol.UPPERIDENT) {
            LabelSet ls = (LabelSet) LabelSet.constants.get(current.toString());
            if (ls == null)
                error("set definition not found for: " + current);
            next_symbol();
            return ls;
        } else {
            error("{ or set identifier expected");
            return null;
        }
    }

    private Vector<ActionLabels> setValue() {
        current_is(Symbol.LCURLY, "{ expected");
        next_symbol();
        return setValueHavingConsumedLCURLY();
    }

    private Vector<ActionLabels> setValueHavingConsumedLCURLY() {
        //Added to process case A = (a -> {b, c} ->A)
        //having consumed the "{" only to discover that this is not a probabilistic transition.
        Vector<ActionLabels> v = new Vector<ActionLabels>();
        v.addElement(labelElement());
        while (current.kind == Symbol.COMMA) {
            next_symbol();
            v.addElement(labelElement());
        }
        current_is(Symbol.RCURLY, "} expected");
        next_symbol();
        return v;
    }

    private ActionLabels labelElement() {
        return labelElement(false);
    }
    private ActionLabels labelElement(boolean allredyConsumedLCurly) {

        if (!allredyConsumedLCurly && current.kind != Symbol.IDENTIFIER && !isLabelSet()
                && current.kind != Symbol.LSQUARE) {
            error("identifier, label set or range expected");
        }

        ActionLabels e = null;
        if (!allredyConsumedLCurly && current.kind == Symbol.IDENTIFIER) {
            String toString = current.toString();
            if ("tau".equals(toString) || "tau?".equals(toString))
                error("'tau' cannot be used as an action label");
            e = new ActionName(current);
            next_symbol();
        } else if (isLabelSet() || allredyConsumedLCurly) {
            LabelSet left;
            if (allredyConsumedLCurly)
                 left = labelSet(true);
            else
                 left = labelSet();
            if (current.kind == Symbol.BACKSLASH) {
                next_symbol();
                LabelSet right = labelSet();
                e = new ActionSetExpr(left, right);
            } else {
                e = new ActionSet(left);
            }
        } else if (current.kind == Symbol.LSQUARE) {
            e = range();
        }

        if (current.kind == Symbol.DOT || current.kind == Symbol.LSQUARE) {
            if (current.kind == Symbol.DOT)
                next_symbol();
            if (e != null)
                e.addFollower(labelElement());
        }

        return e;
    }

    private void constantDefinition(Hashtable<String, Value> p) {
        current_is(Symbol.UPPERIDENT,
                "constant, upper case identifier expected");
        Symbol name = current;
        expectBecomes();
        next_symbol();
        Stack<Symbol> tmp = new Stack<Symbol>();
        simpleExpression(tmp);
        push_symbol();
        if (p.put(name.toString(), Expression.getValue(tmp, null, null)) != null) {
            Diagnostics.fatal("duplicate constant definition: " + name, name);
        }
    }

    private void paramDefns(Hashtable<String, Value> p, Vector<String> parameters) {
        if (current.kind == Symbol.LROUND) {
            next_symbol();
            parameterDefinition(p, parameters);
            while (current.kind == Symbol.COMMA) {
                next_symbol();
                parameterDefinition(p, parameters);
            }
            current_is(Symbol.RROUND, ") expected");
            next_symbol();
        }
    }

    private void parameterDefinition(Hashtable<String, Value> p, Vector<String> parameters) {
        current_is(Symbol.UPPERIDENT,
                "parameter, upper case identifier expected");
        Symbol name = current;
        expectBecomes();
        next_symbol();
        Stack<Symbol> tmp = new Stack<Symbol>();
        expression(tmp);
        push_symbol();
        if (p.put(name.toString(), Expression.getValue(tmp, null, null)) != null) {
            Diagnostics.fatal("duplicate parameter definition: " + name, name);
        }
        if (parameters != null) {
            parameters.addElement(name.toString());
            next_symbol();
        }
    }

    private StateDefn stateDefn() {
        StateDefn s = new StateDefn();
        current_is(Symbol.UPPERIDENT, "process identifier expected");
        s.name = current;
        next_symbol();
        if (current.kind == Symbol.AT) {
            s.accept = true;
            next_symbol();
        }
        if (current.kind == Symbol.DOT || current.kind == Symbol.LSQUARE) {
            if (current.kind == Symbol.DOT)
                next_symbol();
            s.range = labelElement();
        }
        current_is(Symbol.BECOMES, "= expected");
        next_symbol();
        s.stateExpr = stateExpr();
        return s;
    }

    private Stack<Symbol> getEvaluatedExpression() {
        Stack<Symbol> tmp = new Stack<Symbol>();
        simpleExpression(tmp);
        BigDecimal v = Expression.evaluate(tmp, null, null);
        tmp = new Stack<Symbol>();
        if (LTSUtils.isInteger(v)) {
            tmp.push(new Symbol(Symbol.INT_VALUE, v));
        } else {
            tmp.push(new Symbol(Symbol.DOUBLE_VALUE, v));
        }


        return tmp;
    }

    private void defDefinition() {
        current_is(Symbol.UPPERIDENT, "def name, upper case identifier expected");
        Symbol nameSymbol = current;
        Def d = new Def(nameSymbol.getName());
        next_symbol();
        current_is(Symbol.LROUND, "( expected");
        next_symbol();
        while (current.kind != Symbol.RROUND) {
            current_is(Symbol.IDENTIFIER, "identifier expected for def argument");
            d.addParameter(current);
            next_symbol();
            if (current.kind == Symbol.COMMA)
                next_symbol();
            else
                current_is(Symbol.RROUND, ") expected");
        }
        expectBecomes();
        next_symbol();
        expression(d.getExpressionStack());
        if (Def.put(d))
            Diagnostics.fatal("duplicate def definition: " + nameSymbol, nameSymbol);
        push_symbol();
    }

    private void rangeDefinition() {
        current_is(Symbol.UPPERIDENT,
                "range name, upper case identifier expected");
        Symbol name = current;
        expectBecomes();
        next_symbol();
        Range r = new Range();
        r.low = getEvaluatedExpression();
        current_is(Symbol.DOT_DOT, "..  expected");
        next_symbol();
        r.high = getEvaluatedExpression();
        if (Range.ranges.put(name.toString(), r) != null) {
            Diagnostics.fatal("duplicate range definition: " + name, name);
        }
        push_symbol();
    }

    private ActionLabels range() { // this is a mess.. needs to be rewritten
        if (current.kind == Symbol.LSQUARE) {
            next_symbol();
            ActionLabels r;
            Stack<Symbol> low = null;
            Stack<Symbol> high = null;
            if (current.kind != Symbol.IDENTIFIER) {
                if (isLabelSet()) {
                    r = new ActionSet(labelSet());
                } else if (current.kind == Symbol.UPPERIDENT
                        && Range.ranges.containsKey(current.toString())) {
                    r = new ActionRange((Range) Range.ranges.get(current.toString()));
                    next_symbol();
                } else {
                    low = new Stack<Symbol>();
                    expression(low);
                    r = new ActionExpr(low);
                }
                if (current.kind == Symbol.DOT_DOT) {
                    next_symbol();
                    high = new Stack<Symbol>();
                    expression(high);
                    r = new ActionRange(low, high);
                }
            } else {
                Symbol varname = current;
                next_symbol();
                if (current.kind == Symbol.COLON) {
                    next_symbol();
                    if (isLabelSet()) {
                        r = new ActionVarSet(varname, labelSet());
                    } else if (current.kind == Symbol.UPPERIDENT
                            && Range.ranges.containsKey(current.toString())) {
                        r = new ActionVarRange(varname, (Range) Range.ranges.get(current.toString()));
                        next_symbol();
                    } else {
                        low = new Stack<Symbol>();
                        expression(low);
                        current_is(Symbol.DOT_DOT, "..  expected");
                        next_symbol();
                        high = new Stack<Symbol>();
                        expression(high);
                        r = new ActionVarRange(varname, low, high);
                    }
                } else {
                    push_symbol();
                    current = varname;
                    low = new Stack<Symbol>();
                    expression(low);
                    if (current.kind == Symbol.DOT_DOT) {
                        next_symbol();
                        high = new Stack<Symbol>();
                        expression(high);
                        r = new ActionRange(low, high);
                    } else
                        r = new ActionExpr(low);
                }
            }
            current_is(Symbol.RSQUARE, "] expected");
            next_symbol();
            return r;
        } else
            return null;
    }

    private StateExpr stateExpr() {
        StateExpr s = new StateExpr();
        if (current.kind == Symbol.UPPERIDENT)
            stateRef(s);
        else if (current.kind == Symbol.IF) {
            next_symbol();
            s.boolexpr = new Stack<Symbol>();
            expression(s.boolexpr);
            current_is(Symbol.THEN, "keyword then expected");
            next_symbol();
            s.thenpart = stateExpr();
            if (current.kind == Symbol.ELSE) {
                next_symbol();
                s.elsepart = stateExpr();
            } else {
                Symbol stop = new Symbol(Symbol.UPPERIDENT, "STOP");
                StateExpr se = new StateExpr();
                se.name = stop;
                s.elsepart = se;
            }
        } else if (current.kind == Symbol.LROUND) {
            next_symbol();
            if (current.kind == Symbol.FOREACH) {
                next_symbol();
                s.actions = labelElement();
            }
            choiceExpr(s);
            current_is(Symbol.RROUND, ") expected");
            next_symbol();
        } else
            error(" (, if or process identifier expected");

        return s;
    }

    private void stateRef(StateExpr s) {
        current_is(Symbol.UPPERIDENT, "process identifier expected");
        s.name = current;
        next_symbol();
        while (current.kind == Symbol.SEMICOLON
                || current.kind == Symbol.LROUND) {
            s.addSeqProcessRef(new SeqProcessRef(s.name, actualParameters()));
            next_symbol();
            current_is(Symbol.UPPERIDENT, "process identifier expected");
            s.name = current;
            next_symbol();
        }
        if (current.kind == Symbol.LSQUARE) {
            s.expr = new Vector<Stack<Symbol>>();
            while (current.kind == Symbol.LSQUARE) {
                next_symbol();
                Stack<Symbol> x = new Stack<Symbol>();
                expression(x);
                s.expr.addElement(x);
                current_is(Symbol.RSQUARE, "] expected");
                next_symbol();
            }
        }
    }

    private void choiceExpr(StateExpr s) {
        s.choices = new Vector<ChoiceElement>();
        s.choices.addElement(choiceElement());
        while (current.kind == Symbol.BITWISE_OR) {
            next_symbol();
            s.choices.addElement(choiceElement());
        }
    }

    private ChoiceElement choiceElement() {
        boolean isProbabilistic = false;
        ChoiceElement first = new ChoiceElement();
        if (current.kind == Symbol.WHEN) {
            next_symbol();
            first.guard = new Stack<Symbol>();
            expression(first.guard);
        }
        first.action = labelElement();
        current_is(Symbol.ARROW, "-> expected");
        ChoiceElement next = first;
        ChoiceElement last = first;
        next_symbol();
        // TODO EPAVESE for now I only restrict to ONE transition per transformation. This really asks for a yacc approach
        // we expect targets of the form {<float constant> : UPPERIDENT (+ <float constant> : UPPERIDENT)* }
        // TODO EPAVESE this is not ok, isProbabilistic should be globally defined as a machine, not a transition
        if (current.kind == Symbol.LCURLY) {
            isProbabilistic = true;
            next_symbol();
        }

        if (isProbabilistic &&
                (current.kind == Symbol.DOUBLE_VALUE ||
                        current.kind == Symbol.UPPERIDENT)) {
            ProbabilisticChoiceElement newFirst = new ProbabilisticChoiceElement(first);
            int bundle = ProbabilisticTransition.getNextProbBundle();

            BigDecimal totalProbs = BigDecimal.ZERO;
            StateExpr stateExpression = new StateExpr();
            stateExpression.choices = new Vector<ChoiceElement>();
            while (current.kind == Symbol.DOUBLE_VALUE ||
                    current.kind == Symbol.UPPERIDENT) {
                BigDecimal nextProb;
                if (current.kind == Symbol.DOUBLE_VALUE)
                    nextProb = current.doubleValue();
                else {
                    if (!Expression.constants.containsKey(current.toString())) {
                        error("Identifier " + current.toString() + " is undefined");
                    }
                    Value val = (Value) Expression.constants.get(current.toString());
                    nextProb = val.doubleValue();
                }
                totalProbs = totalProbs.add(nextProb);
                next_symbol();
                current_is(Symbol.COLON, "':' expected");
                next_symbol();
                current_is(Symbol.UPPERIDENT, "process identifier expected");

                // TODO get the process identifier, build the (Probabilistic)ChoiceElement
                stateExpression = stateExpr();
                newFirst.addProbabilisticChoice(nextProb, bundle, stateExpression);

                if (current.kind != Symbol.PLUS && current.kind != Symbol.RCURLY)
                    error("'+', '}' expected");
                if (current.kind == Symbol.PLUS) {
                    next_symbol();
                    if (current.kind != Symbol.DOUBLE_VALUE && current.kind != Symbol.UPPERIDENT)
                        error("Float constant expected");
                }
            }
            if (totalProbs.compareTo(BigDecimal.ONE) != 0)
                error("Probabilities should add up to 1 -- " + totalProbs.toString());

            current_is(Symbol.RCURLY, "} expected");
            next_symbol();
            return newFirst;
        } else {
            while (current.kind == Symbol.IDENTIFIER
                    || current.kind == Symbol.LSQUARE
                    || isLabelSet()) {
                StateExpr ex = new StateExpr();
                next = new ChoiceElement();
                 if (isProbabilistic) {
                      // Corresponds to the case where "{" was consumed but it
                     // is not the start of a probabilistic transition.
                     // it must then be the start of a set.
                    next.action = labelElement(true);
                    isProbabilistic = false;
                  }
                else
                    next.action = labelElement();
                ex.choices = new Vector<ChoiceElement>();
                ex.choices.addElement(next);
                last.stateExpr = ex;
                last = next;
                current_is(Symbol.ARROW, "-> expected");
                next_symbol();
            }
            next.stateExpr = stateExpr();
            return first;
        }
    }

    private Symbol event() {
        current_is(Symbol.IDENTIFIER, "event identifier expected");
        Symbol e = current;
        next_symbol();
        return e;
    }

    // LABELCONSTANT -------------------------------

    private ActionLabels labelConstant() {
        next_symbol();
        ActionLabels el = labelElement();
        if (el != null) {
            return el;
        } else
            error("label definition expected");
        return null;
    }

    // set selection @(set , expr)
    private void set_select(Stack<Symbol> expr) {
        Symbol op = current;
        next_symbol();
        current_is(Symbol.LROUND, "( expected to start set index selection");
        Symbol temp = current; // preserve marker
        temp.setAny(labelConstant());
        temp.kind = Symbol.LABELCONST;
        expr.push(temp);
        current_is(Symbol.COMMA, ", expected before set index expression");
        next_symbol();
        expression(expr);
        current_is(Symbol.RROUND, ") expected to end set index selection");
        next_symbol();
        expr.push(op);
    }

    // UNARY ---------------------------------
    private void unary(Stack<Symbol> expr) { // +, -, identifier,
        Symbol unary_operator;
        switch (current.kind) {
            case Symbol.PLUS:
                unary_operator = current;
                unary_operator.kind = Symbol.UNARY_PLUS;
                next_symbol();
                break;
            case Symbol.MINUS:
                unary_operator = current;
                unary_operator.kind = Symbol.UNARY_MINUS;
                next_symbol();
                break;
            case Symbol.PLING:
            case Symbol.SINE:
                unary_operator = current;
                next_symbol();
                break;

            default:
                unary_operator = null;
        }
        switch (current.kind) {
            case Symbol.UPPERIDENT:
                Def d = Def.get(current);
                if (d != null) {
                    next_symbol();
                    current_is(Symbol.LROUND, "( expected to assign def arguments");
                    next_symbol();
                    List<Stack<Symbol>> arguments = new ArrayList<Stack<Symbol>>();
                    while (current.kind != Symbol.RROUND) {
                        Stack<Symbol> arg = new Stack<Symbol>();
                        arguments.add(arg);
                        expression(arg);
                        if (arguments.size() < d.getParameterCount()) {
                            current_is(Symbol.COMMA, "',' expected delimiting def arguments");
                            next_symbol();
                        } else {
                            current_is(Symbol.RROUND, ") expected to end def arguments");
                        }
                    }
                    next_symbol();
                    d.pushExpressionStack(arguments, expr);
                    break;
                }
            case Symbol.IDENTIFIER:
            case Symbol.INT_VALUE:
            case Symbol.DOUBLE_VALUE:
                expr.push(current);
                next_symbol();
                break;
            case Symbol.LROUND:
                next_symbol();
                expression(expr);
                current_is(Symbol.RROUND, ") expected to end expression");
                next_symbol();
                break;
            case Symbol.HASH:
                unary_operator = new Symbol(current);
            case Symbol.QUOTE: // this is a labelConstant
                Symbol temp = current; // preserve marker
                temp.setAny(labelConstant());
                temp.kind = Symbol.LABELCONST;
                expr.push(temp);
                break;
            case Symbol.AT:
                set_select(expr);
                break;
            default:
                error("syntax error in expression");
        }

        if (unary_operator != null)
            expr.push(unary_operator);
    }

    // POWERS / ROOTS
    private void exponential(Stack<Symbol> expr) { // **
        unary(expr);
        while (current.kind == Symbol.POWER) {
            Symbol op = current;
            next_symbol();
            unary(expr);
            expr.push(op);
        }
    }

    // MULTIPLICATIVE
    private void multiplicative(Stack<Symbol> expr) { // *, /, %
        exponential(expr);
        while (
                current.kind == Symbol.STAR ||
                        current.kind == Symbol.DIVIDE ||
                        current.kind == Symbol.BACKSLASH ||
                        current.kind == Symbol.MODULUS
                ) {
            Symbol op = current;
            next_symbol();
            exponential(expr);
            expr.push(op);
        }
    }

    // _______________________________________________________________________________________
    // ADDITIVE

    private void additive(Stack<Symbol> expr) { // +, -
        multiplicative(expr);
        while (current.kind == Symbol.PLUS || current.kind == Symbol.MINUS) {
            Symbol op = current;
            next_symbol();
            multiplicative(expr);
            expr.push(op);
        }
    }

    // _______________________________________________________________________________________
    // SHIFT

    private void shift(Stack<Symbol> expr) { // <<, >>
        additive(expr);
        while (current.kind == Symbol.SHIFT_LEFT
                || current.kind == Symbol.SHIFT_RIGHT) {
            Symbol op = current;
            next_symbol();
            additive(expr);
            expr.push(op);
        }
    }

    // _______________________________________________________________________________________
    // RELATIONAL

    private void relational(Stack<Symbol> expr) { // <, <=, >, >=
        shift(expr);
        while (current.kind == Symbol.LESS_THAN
                || current.kind == Symbol.LESS_THAN_EQUAL
                || current.kind == Symbol.GREATER_THAN
                || current.kind == Symbol.GREATER_THAN_EQUAL) {
            Symbol op = current;
            next_symbol();
            shift(expr);
            expr.push(op);
        }
    }

    // _______________________________________________________________________________________
    // EQUALITY

    private void equality(Stack<Symbol> expr) { // ==, !=
        relational(expr);
        while (current.kind == Symbol.EQUALS
                || current.kind == Symbol.NOT_EQUAL) {
            Symbol op = current;
            next_symbol();
            relational(expr);
            expr.push(op);
        }
    }

    // _______________________________________________________________________________________
    // AND

    private void and(Stack<Symbol> expr) { // &
        equality(expr);
        while (current.kind == Symbol.BITWISE_AND) {
            Symbol op = current;
            next_symbol();
            equality(expr);
            expr.push(op);
        }
    }

    // _______________________________________________________________________________________
    // EXCLUSIVE_OR

    private void exclusive_or(Stack<Symbol> expr) { // ^
        and(expr);
        while (current.kind == Symbol.CIRCUMFLEX) {
            Symbol op = current;
            next_symbol();
            and(expr);
            expr.push(op);
        }
    }

    // _______________________________________________________________________________________
    // INCLUSIVE_OR

    private void inclusive_or(Stack<Symbol> expr) { // |
        exclusive_or(expr);
        while (current.kind == Symbol.BITWISE_OR) {
            Symbol op = current;
            next_symbol();
            exclusive_or(expr);
            expr.push(op);
        }
    }

    // _______________________________________________________________________________________
    // LOGICAL_AND

    private void logical_and(Stack<Symbol> expr) { // &&
        inclusive_or(expr);
        while (current.kind == Symbol.AND) {
            Symbol op = current;
            next_symbol();
            inclusive_or(expr);
            expr.push(op);
        }
    }

    // _______________________________________________________________________________________
    // LOGICAL_OR

    private void logical_or(Stack<Symbol> expr) { // ||
        logical_and(expr);
        while (current.kind == Symbol.OR) {
            Symbol op = current;
            next_symbol();
            logical_and(expr);
            expr.push(op);
        }
    }

    // _______________________________________________________________________________________
    // TERNARY_CONDITIOAL

    private void ternary_conditional(Stack<Symbol> expr) { // _ ? _ : _
        logical_or(expr);
        if (current.kind == Symbol.QUESTION) {
            Symbol op1 = current;
            next_symbol();
            ternary_conditional(expr);

            current_is(Symbol.COLON, "':' expected");
            next_symbol();
            ternary_conditional(expr);

            expr.push(op1);
        }
    }

    // _______________________________________________________________________________________
    // EXPRESSION

    private void expression(Stack<Symbol> expr) {
        ternary_conditional(expr);
    }

    // this is used to avoid a syntax problem
    // when a parallel composition
    // follows a range or constant definition e.g.
    // const N = 3
    // ||S = (P || Q)
    private void simpleExpression(Stack<Symbol> expr) {
        additive(expr);
    }

    // _______________________________________________________________________________________
    // LINEAR TEMPORAL LOGIC ASSERTIONS

    private void assertDefinition(boolean isConstraint, boolean isProperty) {
        Hashtable<String, Value> initparams = new Hashtable<String, Value>();
        Vector<String> params = new Vector<String>();
        LabelSet ls = null;

        current_is(Symbol.UPPERIDENT, "LTL property identifier expected");
        Symbol name = current;
        next_symbol();
        paramDefns(initparams, params);
        current_is(Symbol.BECOMES, "= expected");
        next_symbol_mod();

        FormulaSyntax formula = ltl_unary();

        if (current.kind == Symbol.PLUS) {
            next_symbol();
            ls = labelSet();
        }
        push_symbol();
        this.validateUniqueProcessName(name);

        AssertDefinition.put(name, formula, ls, initparams, params,
                isConstraint, isProperty);

        // Negation of the formula
        if (!(isConstraint && isProperty)) {
            Symbol notName = new Symbol(name);
            notName.setString(AssertDefinition.NOT_DEF + notName.getName());
            Symbol s = new Symbol(Symbol.PLING);
            FormulaSyntax notF = FormulaSyntax.make(null, s, formula);

            this.validateUniqueProcessName(notName);
            AssertDefinition.put(notName, notF, ls, initparams, params, isConstraint, isProperty);
        }
    }

    /**
     * Validates that there is no process or composite process with name
     * designated by <code>processName</code> If so it reports the fatal error
     */
    private void validateUniqueProcessName(Symbol processName) {
        if (processes != null && processes.get(processName.toString()) != null
                || composites != null
                && composites.get(processName.toString()) != null ||
                (AssertDefinition.getDefinition(processName.toString()) != null) ||
                (TriggeredScenarioDefinition.contains(processName)) ||
                DistributionDefinition.contains(processName)
                ) { // XXX: shouldn't controller definitions be here too?
            Diagnostics.fatal("name already defined  " + processName.toString(), processName);
        }
    }


    // do not want X and U to be keywords outside of LTL expressions
    private Symbol modify(Symbol s) {
        if (s.kind != Symbol.UPPERIDENT)
            return s;
        if (s.toString().equals("X")) {
            Symbol nx = new Symbol(s);
            nx.kind = Symbol.NEXTTIME;
            return nx;
        }
        if (s.toString().equals("U")) {
            Symbol ut = new Symbol(s);
            ut.kind = Symbol.UNTIL;
            return ut;
        }
        if (s.toString().equals("W")) {
            Symbol wut = new Symbol(s);
            wut.kind = Symbol.WEAKUNTIL;
            return wut;
        }

        return s;
    }

    private void next_symbol_mod() {
        next_symbol();
        current = modify(current);
    }

    // _______________________________________________________________________________________
    // LINEAR TEMPORAL LOGIC EXPRESSION

    private FormulaSyntax ltl_unary() { // !,<>,[]
        Symbol op = current;
        switch (current.kind) {
            case Symbol.PLING:
            case Symbol.NEXTTIME:
            case Symbol.EVENTUALLY:
            case Symbol.ALWAYS:
                next_symbol_mod();
                return FormulaSyntax.make(null, op, ltl_unary());
            case Symbol.UPPERIDENT:
                next_symbol_mod();
                if (current.kind == Symbol.LSQUARE) {
                    ActionLabels range = forallRanges();
                    current = modify(current);
                    return FormulaSyntax.make(op, range);
                } else if (current.kind == Symbol.LROUND) {
                    Vector<Stack<Symbol>> actparams = actualParameters();
                    return FormulaSyntax.make(op, actparams);
                } else {
                    return FormulaSyntax.make(op);
                }
            case Symbol.LROUND:
                next_symbol_mod();
                FormulaSyntax right = ltl_or();
                current_is(Symbol.RROUND, ") expected to end LTL expression");
                next_symbol_mod();
                return right;
            case Symbol.IDENTIFIER:
            case Symbol.LSQUARE:
            case Symbol.LCURLY:
                ActionLabels ts = labelElement();
                push_symbol();
                next_symbol_mod();
                return FormulaSyntax.make(ts);
            case Symbol.EXISTS:
                next_symbol_mod();
                ActionLabels ff = forallRanges();
                push_symbol();
                next_symbol_mod();
                return FormulaSyntax.make(new Symbol(Symbol.OR), ff, ltl_unary());
            case Symbol.FORALL:
                next_symbol_mod();
                ff = forallRanges();
                push_symbol();
                next_symbol_mod();
                return FormulaSyntax.make(new Symbol(Symbol.AND), ff, ltl_unary());
            case Symbol.RIGID:
                next_symbol_mod();
                Stack<Symbol> tmp = new Stack<Symbol>();
                simpleExpression(tmp);
                push_symbol();
                next_symbol_mod();
                return FormulaSyntax.makeE(op, tmp);
            default:
                Diagnostics.fatal("syntax error in LTL expression", current);
        }
        return null;
    }

    // _______________________________________________________________________________________
    // LTL_AND

    private FormulaSyntax ltl_and() { // &
        FormulaSyntax left = ltl_unary();
        while (current.kind == Symbol.AND) {
            Symbol op = current;
            next_symbol_mod();
            FormulaSyntax right = ltl_unary();
            left = FormulaSyntax.make(left, op, right);
        }
        return left;
    }

    // _______________________________________________________________________________________
    // LTL_OR

    private FormulaSyntax ltl_or() { // |
        FormulaSyntax left = ltl_binary();
        while (LTSUtils.isOrSymbol(current)) {
            Symbol op = current;
            next_symbol_mod();
            FormulaSyntax right = ltl_binary();
            left = FormulaSyntax.make(left, op, right);
        }
        return left;
    }

    // _______________________________________________________________________________________
    // LTS_BINARY

    private FormulaSyntax ltl_binary() { // until, ->
        FormulaSyntax left = ltl_and();
        if (current.kind == Symbol.UNTIL || current.kind == Symbol.WEAKUNTIL
                || current.kind == Symbol.ARROW
                || current.kind == Symbol.EQUIVALENT) {
            Symbol op = current;
            next_symbol_mod();
            FormulaSyntax right = ltl_and();
            left = FormulaSyntax.make(left, op, right);
        }
        return left;
    }

    //
    // ___________________________________________________________________________________
    // STATE PREDICATE DEFINITIONS

    private void predicateDefinition() {
        current_is(Symbol.UPPERIDENT, "predicate identifier expected");
        Symbol name = current;
        ActionLabels range = null;
        next_symbol();
        if (current.kind == Symbol.LSQUARE)
            range = forallRanges();
        current_is(Symbol.BECOMES, "= expected");
        next_symbol();
        current_is(Symbol.LESS_THAN, "< expected");
        next_symbol();
        ActionLabels ts = labelElement();
        current_is(Symbol.COMMA, ", expected");
        next_symbol();
        ActionLabels fs = labelElement();
        current_is(Symbol.GREATER_THAN, "> expected");
        next_symbol();
        if (current.kind == Symbol.INIT) {
            next_symbol();
            Stack<Symbol> tmp = new Stack<Symbol>();
            simpleExpression(tmp);
            push_symbol();
            PredicateDefinition.put(name, range, ts, fs, tmp);
        } else {
            push_symbol();
            PredicateDefinition.put(name, range, ts, fs, null);
        }
    }

    // *******************************************************************************************************/
    // MTS operations
    // *******************************************************************************************************/


    private ActionLabels getMaybeActionLabels(ActionLabels actionLabel) {
        ActionLabels result = null;
        if (actionLabel instanceof ActionName) {
            ActionName actionName = (ActionName) actionLabel;
            Symbol symbol = actionName.name;
            result = new ActionName(new Symbol(symbol, getMaybeAction(symbol.getName())));
        } else if (actionLabel instanceof ActionSet) {
            ActionSet actionSet = (ActionSet) actionLabel;
            Vector<ActionLabels> maybeSetLabels = new Vector<ActionLabels>();
            LabelSet setLabel = actionSet.set;
            if (setLabel.labels != null) {
                for (Object e : setLabel.labels) {
                    maybeSetLabels.add(getMaybeActionLabels((ActionLabels) e));
                }
            }
            result = new ActionSet(new LabelSet(maybeSetLabels));
        }
        return result;
    }

    /*
     * This method extends the set of labels to be relabeled with the maybe or
     * the required labels.
     */
    private void relabelMTS(Vector<RelabelDefn> relabels, RelabelDefn relabelDefn) {
        RelabelDefn relabelMaybe = new RelabelDefn();
        relabelMaybe.oldlabel = getMaybeActionLabels(relabelDefn.oldlabel);
        relabelMaybe.newlabel = getMaybeActionLabels(relabelDefn.newlabel);
        if (relabelMaybe.oldlabel != null && relabelMaybe.newlabel != null) {
            relabels.add(relabelMaybe);
        } else {
            // FIXME: MTSs add a '?' to action names, but currently expression actions are not supported
            //        A possibility is to extend the computeName method in order to take into account MTSs
            String message = "Relabeling with maybe actions can only be made over labels and sets.";
            Diagnostics.warning(message, message, null);
        }
    }

    private void priorizeMaybeActions(LabelSet priorityActions) {
        // Only ActionName expected.
        if (priorityActions != null) {
            this.processMTSActions(priorityActions.labels);
        }
    }

    private void processMTSActions(Vector<ActionName> allLabels) {
        Set<ActionName> addLabels = new HashSet<ActionName>();
        for (Iterator<ActionName> it = allLabels.iterator(); it.hasNext(); ) {
            ActionLabels action = (ActionLabels) it.next();
            if (action instanceof ActionSet) {
                this.processActionSet((ActionSet) action, addLabels);
            } else if (action instanceof ActionName) {
                processActionName((ActionName) action, addLabels);
            } else {
                throw new RuntimeException(
                        "Action to hide is instance of class: " + action.getClass());
            }
        }
        allLabels.clear();
        allLabels.addAll(addLabels);
    }

    private void processActionSet(ActionSet actionLabels, Set<ActionName> addLabels) {
        ActionSet actionSet = actionLabels;
        for (Iterator<String> it = actionSet.getActions(null, null).iterator(); it.hasNext(); ) {
            String actionName = it.next();
            String name = getMaybeAction(actionName);
            ActionName tempActionName1 = new ActionName(new Symbol(Symbol.STRING_VALUE, name));
            //tempActionName1.follower = actionName.follower;
            addLabels.add(tempActionName1);

            actionName = getOpositeActionLabel(name);
            addLabels.add(new ActionName(new Symbol(Symbol.STRING_VALUE,
                    actionName)));
        }
    }

    /**
     * Computes the set of actions defined by <i>actionsName</i>
     * and add for each of them the action with opposite maybe condition.
     * <p>
     * For example, if <i>actionsName</i> is the label "a", it returns "a?".
     */
    public void processActionName(ActionName actionName, Set<ActionName> toAdd) {

        String name = actionName.name.toString();
        name = getMaybeAction(name);
        ActionName tempActionName1 = new ActionName(new Symbol(actionName.name, name));
        tempActionName1.follower = actionName.follower;
        toAdd.add(tempActionName1);

        name = getOpositeActionLabel(name);
        ActionName tempActionName2 = new ActionName(new Symbol(actionName.name, name));
        tempActionName2.follower = actionName.follower;
        toAdd.add(tempActionName2);
    }

    /*
     * Not used yet
     */
    private void controllerDefinition(ControllerDefinition controllerDefinition) {
        current_is(Symbol.BECOMES, "= expected after controller identifier");
        expectLeftCurly();
        next_symbol();

        current_is(Symbol.UPPERIDENT, "goal identifier expected");
        controllerDefinition.setProcess(current);
        next_symbol();

        current_is(Symbol.COMMA, ", expected after goal identifier");
        next_symbol();

        current_is(Symbol.UPPERIDENT, "goal identifier expected");
        controllerDefinition.setGoal(current);
        next_symbol();

        current_is(Symbol.RCURLY, "} expected");
        next_symbol();

        ControllerDefinition.put(controllerDefinition);

        push_symbol();
    }

    private void goalDefinition(ControllerGoalDefinition goal) {
        this.expectBecomes();
        expectLeftCurly();
        next_symbol();
        while ((current.kind != Symbol.RCURLY)) {
            if (current.kind == Symbol.DISTURBANCE) {
                goal.setDisturbanceActions(this.parseActionSet());
            } else if (current.kind == Symbol.MARKING) {
        		goal.setMarkingDefinitions(this.parseActionSet());
        	} else if (current.kind == Symbol.PERMISSIVE) {
                goal.setPermissive();
                next_symbol();
            } else if (current.kind == Symbol.SAFETY) {
                goal.setSafetyDefinitions(this.controllerSubGoal());
            } else if (current.kind == Symbol.FAULT) {
                goal.setFaultsDefinitions(this.controllerSubGoal());
            } else if (current.kind == Symbol.ASSUME) {
                goal.setAssumeDefinitions(this.controllerSubGoal());
            } else if (current.kind == Symbol.GUARANTEE) {
                goal.setGuaranteeDefinitions(this.controllerSubGoal());
            } else if (current.kind == Symbol.EXCEPTION_HANDLING) {
                next_symbol();
                goal.setExceptionHandling(true);
            } else if (current.kind == Symbol.CONTROLLER_NB) {
                next_symbol();
                goal.setNonBlocking(true);
            } else if (current.kind == Symbol.CONCURRENCY_FLUENTS) {
                goal.setConcurrencyDefinitions(this.controllerSubGoal());
            } else if (current.kind == Symbol.CONTROLLER_LAZYNESS) {
                goal.setLazyness(this.controllerSubValue());
            } else if (current.kind == Symbol.NON_TRANSIENT) {
                next_symbol();
                goal.setNonTransient(true);
            } else if (current.kind == Symbol.ACTIVITY_FLUENTS) {
                goal.setActivityDefinitions(this.controllerSubGoal());
            } else if (current.kind == Symbol.REACHABILITY) {
                next_symbol();
                goal.setReachability(true);
            } else if (current.kind == Symbol.TEST_LATENCY) {
                next_symbol();
                goal.setTestLatency(true);
                Pair<Integer, Integer> value = this.controllerSubPair();
                goal.setMaxSchedulers(value.getFirst());
                goal.setMaxControllers(value.getSecond());
            } else if (current.kind == Symbol.CONTROLLABLE) {
                //System.out.println("aaaaaaa");
                goal.setControllableActionSet(this.parseActionSet());
                //this.parseControllableActionSet(goal);
            } else if (current.kind == Symbol.BUCHI) {
        	    goal.setBuchiDefinitions(this.controllerSubGoal());
            } else if (current.kind == Symbol.COMPARISON){
                //System.out.println("yatteruyoLTScompiler");
                goal.comparisonActionSet(this.parseActionSet2());
                
                //System.out.println(goal.getComparisonActionSet());
            } else
                error("Controller symbol expected");

        }
        ControllerGoalDefinition.put(goal);
    }

    private void explorerDefinition(ExplorerDefinition explorerDefinition) {
        current_is(Symbol.BECOMES, "= expected after explorer identifier");
        next_symbol();

        current_is(Symbol.LCURLY, "{ expected");
        next_symbol();

        current_is(Symbol.EXPLORATION_ENVIRONMENT, "environment expected");
        next_symbol();
        explorerDefinition.setView(this.componentsNotEmpty());
        next_symbol();

        current_is(Symbol.EXPLORATION_MODEL, "model expected");
        next_symbol();
        explorerDefinition.setModel(this.componentsByCount(explorerDefinition.getView().size()));
        next_symbol();

        current_is(Symbol.EXPLORATION_GOAL, "goal expected");
        next_symbol();
        explorerDefinition.setGoal(this.componentsByCount(1));

        if (current.kind == Symbol.COMMA) {
            next_symbol();
            current_is(Symbol.EXPLORATION_ENVIRONMENT_ACTIONS, "actions expected");
            next_symbol();
            explorerDefinition.setEnvironmentActions(this.listsOfComponentsNotEmpty());
        }

        explorers.put(explorerDefinition.getName(), explorerDefinition);

        current_is(Symbol.RCURLY, "} expected");
    }


    private Pair<Integer, Integer> controllerSubPair() {
        current_is(Symbol.BECOMES, "= expected");
        next_symbol();

        current_is(Symbol.LCURLY, "{ expected");
        next_symbol();

        current_is(Symbol.INT_VALUE, "Expected max number of schedulers value int.");
        Integer schedulers = current.doubleValue().intValue();
        next_symbol();

        current_is(Symbol.COMMA, ", expected");
        next_symbol();

        current_is(Symbol.INT_VALUE, "Expected max number of controllers value int.");
        Integer controllers = current.doubleValue().intValue();
        next_symbol();

        current_is(Symbol.RCURLY, "} expected");
        next_symbol();


        return new Pair<Integer, Integer>(schedulers, controllers);
    }

    private void updateControllerDefinition(UpdatingControllersDefinition ucDefinition) {

        expectBecomes();
        expectLeftCurly();
        next_symbol();

        if (current.kind == Symbol.OLD_CONTROLLER) {
            ucDefinition.setOldController(this.controllerSubUpdateController());
            current_is(Symbol.COMMA, ", expected");
            next_symbol();
        }

        if (current.kind == Symbol.MAPPING) {
            ucDefinition.setMapping(this.controllerSubUpdateController());
            current_is(Symbol.COMMA, ", expected");
            next_symbol();
        }

        if (current.kind == Symbol.OLD_GOAL) {
            this.expectBecomes();
            next_symbol();
            current_is(Symbol.UPPERIDENT, "old goal identifier expected");
            ucDefinition.setOldGoal(current);
            next_symbol();
            current_is(Symbol.COMMA, ", expected");
            next_symbol();
        }
        if (current.kind == Symbol.NEW_GOAL) {
            this.expectBecomes();
            next_symbol();
            current_is(Symbol.UPPERIDENT, "new goal identifier expected");
            ucDefinition.setNewGoal(current);
            next_symbol();
            current_is(Symbol.COMMA, ", expected");
            next_symbol();
        }
        if (current.kind == Symbol.TRANSITION) {
            this.expectBecomes();
            next_symbol();
            current_is(Symbol.UPPERIDENT, "T definition expected");
            ucDefinition.addTransitionGoal(current);
            next_symbol();
            current_is(Symbol.COMMA, ", expected");
            next_symbol();
        }

        if (current.kind == Symbol.CONTROLLER_NB) {
            next_symbol();
            ucDefinition.setNonblocking();
        }


        current_is(Symbol.RCURLY, "} expected");
    }

    private Symbol parseInitialState() {
        next_symbol();
        current_is(Symbol.GRAPH_INITIAL_STATE, "initialState expected");
        expectBecomes();
        expectIdentifier("Initial State");
        return current;
    }

    private List<Symbol> parseTransitions() {
        next_symbol();
        current_is(Symbol.GRAPH_TRANSITIONS, "transitions expected");
        expectBecomes();
        expectLeftCurly();
        List<Symbol> list = new ArrayList<Symbol>();
        next_symbol();
        while (current.kind == Symbol.UPPERIDENT) {
            list.add(current);
            next_symbol();
            if (current.kind == Symbol.COMMA) {
                next_symbol();
            }
        }
        current_is(Symbol.RCURLY, "} expected");
        return list;
    }

    private Integer controllerSubValue() {
        expectBecomes();
        next_symbol();
        current_is(Symbol.INT_VALUE, "Expected lazyness value int.");
        Integer value = current.doubleValue().intValue();
        next_symbol();
        return value;
    }

//    private void parseControllableActionSet(ControllerGoalDefinition goal) {
//        current_is(Symbol.CONTROLLABLE, "controllable action set expected");
//        next_symbol();
//
//        current_is(Symbol.BECOMES, "= expected for controllable action set");
//        expectLeftCurly();
//        next_symbol();
//
//        current_is(Symbol.UPPERIDENT, "action set identifier expected");
//        goal.setControllableActionSet(current);
//        next_symbol();
//
//        current_is(Symbol.RCURLY, "} expected");
//        next_symbol();
//    }

    private List<List<Symbol>> listsOfComponentsNotEmpty() {
        List<List<Symbol>> listOfDefinitions = new ArrayList<>();

        current_is(Symbol.BECOMES, "= expected");
        next_symbol();

        current_is(Symbol.LCURLY, "{ expected");
        next_symbol();

        current_is(Symbol.LCURLY, "{ expected");
        while (current.kind == Symbol.LCURLY || current.kind == Symbol.COMMA) {
            List<Symbol> definitions = new ArrayList<>();

            if (current.kind == Symbol.COMMA)
                next_symbol();

            current_is(Symbol.LCURLY, "{ expected");
            next_symbol();
            boolean finish = false;

            current_is(Symbol.IDENTIFIER, "non empty set expected");
            definitions.add(current);
            next_symbol();
            if (current.kind != Symbol.COMMA)
                finish = true;
            else
                next_symbol();

            while (current.kind == Symbol.IDENTIFIER && !finish) {
                definitions.add(current);
                next_symbol();
                if (current.kind != Symbol.COMMA)
                    break;
                next_symbol();
            }
            current_is(Symbol.RCURLY, "} expected");
            next_symbol();

            listOfDefinitions.add(definitions);
        }

        current_is(Symbol.RCURLY, "} expected");
        next_symbol();

        return listOfDefinitions;
    }
    
	private Vector<String> parseActionSet() {
		expectBecomes();
		next_symbol();
		Vector<String> actions = labelSet().getActions(null);
		return actions;
	}

    private Vector<String> parseActionSet2() {//おそらくここでアクションをとってきているアクションをとってきている
        expectBecomes();//=
        next_symbol();//次のシンボル探索
        Vector<String> actions = labelSet().getActions2(null);//ここでおそらく撮っている
        //System.out.println("ACTIONS");
        //System.out.println(actions);
        return actions;
    }

    private List<Symbol> controllerSubGoal() {
        expectBecomes();
        List<Symbol> definitions = new ArrayList<Symbol>();
        expectLeftCurly();
        next_symbol();
        boolean finish = false;
        while (current.kind == Symbol.UPPERIDENT &&
                !finish) {
            definitions.add(current);
            next_symbol();
            if (current.kind != Symbol.COMMA) {
                finish = true;
                break;
            }
            next_symbol();
        }
        current_is(Symbol.RCURLY, "} expected");
        next_symbol();

        return definitions;
    }

    private List<Symbol> componentsNotEmpty() {
        List<Symbol> definitions = new ArrayList<Symbol>();
        current_is(Symbol.BECOMES, "= expected");
        next_symbol();

        current_is(Symbol.LCURLY, "{ expected");
        next_symbol();
        boolean finish = false;

        current_is(Symbol.UPPERIDENT, "non empty set expected");
        definitions.add(current);
        next_symbol();
        if (current.kind != Symbol.COMMA)
            finish = true;
        else
            next_symbol();

        while (current.kind == Symbol.UPPERIDENT && !finish) {
            definitions.add(current);
            next_symbol();
            if (current.kind != Symbol.COMMA) {
                finish = true;
                break;
            }
            next_symbol();
        }
        current_is(Symbol.RCURLY, "} expected");
        next_symbol();

        return definitions;
    }

    private List<Symbol> componentsByCount(int count) {
        List<Symbol> definitions = new ArrayList<Symbol>();
        current_is(Symbol.BECOMES, "= expected");
        next_symbol();

        current_is(Symbol.LCURLY, "{ expected");
        next_symbol();
        boolean finish = false;

        current_is(Symbol.UPPERIDENT, "non empty set expected");
        definitions.add(current);
        next_symbol();
        if (current.kind != Symbol.COMMA)
            finish = true;
        else
            next_symbol();

        while (current.kind == Symbol.UPPERIDENT && !finish) {
            definitions.add(current);
            next_symbol();
            if (current.kind != Symbol.COMMA) {
                finish = true;
                break;
            }
            next_symbol();
        }
        current_is(Symbol.RCURLY, "} expected");
        next_symbol();

        if (definitions.size() != count)
            error("View and Model haven't the same number of components");

        return definitions;
    }

    private ArrayList<Symbol> controllerSubUpdateController() {
        this.expectBecomes();
        next_symbol();
        ArrayList<Symbol> definitions = new ArrayList<Symbol>();
        current_is(Symbol.UPPERIDENT, "Upperident expected");
        definitions.add(current);
        next_symbol();

        return definitions;

    }

    private ArrayList<Pair<Symbol, Symbol>> controllerFluentsUpdateController() {
        ArrayList<Pair<Symbol, Symbol>> definitions = new ArrayList<Pair<Symbol, Symbol>>();
        current_is(Symbol.BECOMES, "= expected");
        next_symbol();

        current_is(Symbol.LCURLY, "{ expected");
        next_symbol();
        boolean finish = false;
        while (current.kind == Symbol.LCURLY && !finish) {
            boolean innerFinish = false;
            current_is(Symbol.LCURLY, "{ expected");
            next_symbol();
            Symbol first = null;
            Symbol second = null;
            if (current.kind == Symbol.UPPERIDENT) {
                first = current;
                next_symbol();
            }
            current_is(Symbol.COMMA, ", expected");
            next_symbol();
            if (current.kind == Symbol.UPPERIDENT) {
                second = current;
                next_symbol();
            }
            Pair<Symbol, Symbol> subDefinition = new Pair<Symbol, Symbol>(first, second);
            definitions.add(subDefinition);
            next_symbol();
            if (current.kind != Symbol.COMMA) {
                finish = true;
                break;
            }
            next_symbol();
        }
        current_is(Symbol.RCURLY, "} expected");
        next_symbol();

        return definitions;
    }

    private ArrayList<Symbol> controllerCheckTraceUpdateController() {
        expectBecomes();
        ArrayList<Symbol> definitions = new ArrayList<Symbol>();
        expectLeftCurly();
        next_symbol();
        boolean finish = false;
        while (current.kind == Symbol.IDENTIFIER && !finish) {
            definitions.add(current);
            next_symbol();
            if (current.kind == Symbol.RCURLY) {
                finish = true;
                break;
            } else if (current.kind == Symbol.DOT) {
                while (current.kind != Symbol.COMMA && current.kind != Symbol.RCURLY) {
                    Symbol lastDef = definitions.get(definitions.size() - 1);
                    String parametricValue = lastDef.toString() + ".";
                    next_symbol();
                    parametricValue = parametricValue + current.toString();
                    definitions.remove(definitions.size() - 1);
                    definitions.add(new Symbol(Symbol.IDENTIFIER, parametricValue));
                    next_symbol();
                }
            }
            next_symbol();
        }
        current_is(Symbol.RCURLY, "} expected");
        next_symbol();

        return definitions;
    }

    /*
     controlstack ||PROCNAME@{CONTSET} = {
       tier (ENVNAME, GOALNAME)
       tier (ENVNAME, GOALNAME)
     }

     controlstack ||PROCNAME@{CONTSET}(action, action) = {
     tier (ENVNAME, GOALNAME)
     tier (ENVNAME, GOALNAME)
   }
    */
    private ControlStackDefinition controlStackDefinition() {
        next_symbol();

        current_is(Symbol.OR, "|| expected after controlstack");
        next_symbol();

        current_is(Symbol.UPPERIDENT, "process identifier expected");
        Symbol name = current;
        next_symbol();

        current_is(Symbol.AT, "@ expected");
        expectLeftCurly();
        next_symbol();

        current_is(Symbol.UPPERIDENT, "controlled action set expected");
        Symbol contSet = current;
        next_symbol();

        current_is(Symbol.RCURLY, "} expected");
        expectBecomes();
        expectLeftCurly();
        ControlStackDefinition def = new ControlStackDefinition(name, contSet);

        next_symbol();

        int tiers = 0;
        while (current.kind == Symbol.CONTROL_TIER) {
            tiers++;
            def.addTier(controlTierDefinition());
        }

        current_is(Symbol.RCURLY, "} expected");
        //next_symbol(); //it's swallowed elsewhere

        return def;
    }

    private ControlTierDefinition controlTierDefinition() {
        next_symbol();

        current_is(Symbol.LROUND, "( expected");
        next_symbol();

        current_is(Symbol.UPPERIDENT, "process identifier expected");
        Symbol env = current;
        next_symbol();

        current_is(Symbol.COMMA, ", expected");
        next_symbol();

        current_is(Symbol.UPPERIDENT, "goal identifier expected");
        Symbol goal = current;
        next_symbol();

        ControlTierDefinition def = new ControlTierDefinition(env, goal);

        if (current.kind == Symbol.COMMA) {
            expectLeftCurly();
            next_symbol();

            List<String> initialTrace = new ArrayList<String>();
            while (current.kind == Symbol.IDENTIFIER) {
                //only flat dotted IDs
                String label = current.toString();
                next_symbol();
                while (current.kind == Symbol.DOT) {
                    next_symbol();

                    current_is(Symbol.IDENTIFIER, "action label expected");
                    label += "." + current.toString();
                    next_symbol();
                }
                initialTrace.add(label);

                //if a comma, consume it
                if (current.kind == Symbol.COMMA)
                    next_symbol();
            }

            current_is(Symbol.RCURLY, "} expected");
            next_symbol();

            def.setInitialTrace(initialTrace);
        }

        current_is(Symbol.RROUND, ") expected");
        next_symbol();

        return def;
    }

    /**
     *
     */
    private void distributionDefinition() {
        // parse the components name
        List<Symbol> componentsName = new LinkedList<Symbol>();

        List<Integer> expected = new LinkedList<Integer>();
        expected.add(Symbol.BECOMES);
        expected.add(Symbol.COMMA);

        do {
            next_symbol();
            // Check the syntax and that the process name for the component is unique
            current_is(Symbol.UPPERIDENT, "component identifier expected");
            this.validateUniqueProcessName(current);

            componentsName.add(current);
            next_symbol();

            // next symbol should be = or ,
            current_is(expected, "= or , expected");

        } while (current.kind != Symbol.BECOMES);
        current_is(Symbol.BECOMES, "= expected");

        expectLeftCurly();
        // parse body
        next_symbol();

        // parse components alphabet
        List<Symbol> alphabets = this.parseDistributedAlphabets();

        if (alphabets.size() != componentsName.size()) {
            error("There should be one and only one alphabet for each component. Components: " + componentsName + " . Alphabets: " + alphabets);
        }

        // parse system model name
        current_is(Symbol.SYSTEM_MODEL, "systemModel expected");

        expectBecomes();

        next_symbol();
        current_is(Symbol.UPPERIDENT, "component identifier expected");
        Symbol systemModel = current;

        // parse output file name
        String outputFileName;
        next_symbol();

        if (current.equals(Symbol.OUTPUT_FILE_NAME)) {
            expectBecomes();
            next_symbol();
            current_is(Symbol.STRING_VALUE, "String with file name expected");
            outputFileName = current.toString();
            if (outputFileName == null) {
                error("Problem parsing output file name");
            } else {
                try {
                    File testFile = new File(outputFileName);
                    testFile.createNewFile();
                    testFile.canWrite();
                } catch (Exception e) {
                    error("Problem handling file with name " + outputFileName + ". " + e.getMessage());
                }
            }
            next_symbol();

        } else {
            outputFileName = null;
        }

        current_is(Symbol.RCURLY, "} expected");


        DistributionDefinition distributionDefinition;
        // create the distribution
        if (outputFileName != null) {
            distributionDefinition = new DistributionDefinition(systemModel, componentsName, alphabets, outputFileName);
        } else {
            distributionDefinition = new DistributionDefinition(systemModel, componentsName, alphabets);
        }
        DistributionDefinition.put(distributionDefinition);
    }

    private List<Symbol> parseDistributedAlphabets() {
        current_is(Symbol.DISTRIBUTED_ALPHABETS, "distributedAlphabets expected");
        expectBecomes();

        expectLeftCurly();
        List<Integer> expected = new LinkedList<Integer>();
        expected.add(Symbol.RCURLY);
        expected.add(Symbol.COMMA);

        List<Symbol> result = new ArrayList<Symbol>();
        do {
            next_symbol();
            // Check the syntax and that the process name for the component is unique
            current_is(Symbol.UPPERIDENT, "set identifier expected");

            result.add(current);
            next_symbol();

            // next symbol should be } or ,
            current_is(expected, "} or , expected");

        } while (current.kind != Symbol.RCURLY);

        current_is(Symbol.RCURLY, "} expected");
        next_symbol();
        return result;
    }

    /**
     * Parses a Triggered Scenario
     */
    private void triggeredScenarioDefinition(TriggeredScenarioDefinition triggeredScenarioDefinition) throws DuplicatedTriggeredScenarioDefinitionException {

        current_is(Symbol.BECOMES, "= expected after chart identifier");
        expectLeftCurly();
        next_symbol();

        // Instances must come next
        triggeredScenarioDefinition.setInstances(this.chartInstancesValues());

        // Conditions (if any) comes next
        while (current.kind == Symbol.CONDITION) {
            next_symbol();
            triggeredScenarioDefinition.addConditionDefinition(this
                    .conditionDefinition());
        }

        // Prechart must come next
        current_is(Symbol.PRECHART, "prechart expected");
        triggeredScenarioDefinition.setPrechart(this.basicChartDefinition(true,
                triggeredScenarioDefinition));

        // Mainchart must come next
        next_symbol();
        current_is(Symbol.MAINCHART, "mainchart expected");
        triggeredScenarioDefinition.setMainchart(this.basicChartDefinition(
                false, triggeredScenarioDefinition));

        triggeredScenarioDefinition.setRestricted(this
                .restrictsDefinition(triggeredScenarioDefinition));

        expectRightCurly();
        next_symbol();

        TriggeredScenarioDefinition.put(triggeredScenarioDefinition);

        push_symbol();
    }

    private ConditionDefinition conditionDefinition() {
        current_is(Symbol.UPPERIDENT, "Identifier expected");
        Symbol name = current;

        expectBecomes();

        next_symbol_mod();
        FormulaSyntax formula = ltl_unary();

        if (!formula.isPropositionalLogic()) {
            error("Condition must be a Fluent Propositional Logic formula");
            return null;
        }
        ConditionDefinition conditionDefinition = new ConditionDefinition(name
                .getName(), formula);

        return conditionDefinition;
    }

    private Set<Interaction> restrictsDefinition(TriggeredScenarioDefinition tsDefinition) {
        Set<Interaction> result = new HashSet<Interaction>();

        if (current.kind == Symbol.RESTRICTS) {
            expectLeftCurly();
            next_symbol();

            while (current.kind != Symbol.RCURLY) {
                try {
                    result.add((Interaction) locationValue(false, tsDefinition));
                } catch (ClassCastException e) {
                    error("Restrictions can only be Interactions");
                    return null;
                }
            }
        }
        return result;
    }

    private BasicChartDefinition basicChartDefinition(boolean isPrechartDefinition, TriggeredScenarioDefinition tsDefinition) {
        expectLeftCurly();
        next_symbol();
        BasicChartDefinition chartDefinition = new BasicChartDefinition();
        chartDefinition.addLocation(locationValue(isPrechartDefinition, tsDefinition));

        while (current.kind != Symbol.RCURLY) {
            chartDefinition.addLocation(locationValue(isPrechartDefinition, tsDefinition));
        }
        return chartDefinition;
    }

    private Location locationValue(boolean isPrechartDefinition, TriggeredScenarioDefinition tsDefinition) {
        // A location can be a Condition or an Interaction
        // Interactions are of the form: UPPERIDENT -> IDENT -> UPPERIDENT
        // Conditions are like: UPPERIDENT [UPPERIDENT UPPERIDENT...]. The first ident is the name of the condition and then
        // comes the set of instances that the condition synchronises.
        Symbol previous = this.identifier();

        if (current.kind == Symbol.ARROW) {
            // Location is an Interaction
            next_symbol();

            // previous symbol is the interaction's source
            String source = previous.getName();
            String message = this.event().getName();

            current_is(Symbol.ARROW, "-> expected");
            next_symbol();

            String target = this.identifier().getName();

            return new Interaction(source, message, target);
        } else if (current.kind == Symbol.LSQUARE) {
            // Conditions can only be placed in the Prechart
            if (!isPrechartDefinition) {
                error("Conditions can only be placed in the Prechart");
                return null;
            } else {
                // Location is a Condition
                next_symbol();

                // previous symbol is the condition's identifier
                if (!tsDefinition.hasCondition(previous.getName())) {
                    // Condition must be defined previously in the TriggeredScenario.
                    error("Condition not defined: " + previous.getName());
                    return null;
                } else {
                    String conditionName = previous.getName();

                    // get the instances synchronising with this condition
                    Set<String> instances = new HashSet<String>();

                    // at least there must be an instance
                    instances.add(this.identifier().getName());
                    while (current.kind != Symbol.RSQUARE) {
                        instances.add(this.identifier().getName());
                    }
                    next_symbol();

                    return new ConditionLocation(conditionName, instances);
                }
            }
        } else {
            error("-> or [ expected");
            return null;
        }
    }

    private NamedFPLFormula guaranteeDefinition() {
        return namedFPLFormula();
    }

    private NamedFPLFormula assumeDefinition() {
        return namedFPLFormula();
    }

    private NamedFPLFormula namedFPLFormula() {
        current_is(Symbol.UPPERIDENT, "Identifier expected");
        Symbol name = current;

        expectBecomes();

        // should we allow parametrized FLTL formulas?
        next_symbol_mod();
        FormulaSyntax formula = ltl_unary();

        if (!formula.isPropositionalLogic()) {
            error(name + " must be a Fluent Propositional Logic formula");
            return null;
        }

        return new NamedFPLFormula(name.getName(), formula);
    }

    /**
     * Checks that next symbol is an identifier and gets it.
     */
    private Symbol identifier() {
        current_is(Symbol.UPPERIDENT, "identifier expected");
        Symbol identifier = current;
        next_symbol();
        return identifier;
    }

    private Set<String> chartInstancesValues() {
        next_symbol();
        current_is(Symbol.INSTANCES, "instances expected");
        expectLeftCurly();
        next_symbol();
        Set<String> instances = new HashSet<String>();
        while (current.kind == Symbol.UPPERIDENT) {
            instances.add(current.toString());
            next_symbol();
        }
        current_is(Symbol.RCURLY, "} expected");
        next_symbol();
        return instances;
    }

    static Symbol saveControllableSet(Set<String> controllableSet, String name) {
        Symbol updateControllableSetSymbol = new Symbol(Symbol.SET, "controller_update_" + name + "_controllable_set");
        Vector<ActionLabels> vector = new Vector();
        for (String action : controllableSet) {
            ActionName actionName = new ActionName(new Symbol(Symbol.IDENTIFIER, action));
            vector.add(actionName);
        }
        new LabelSet(updateControllableSetSymbol, vector);
        return updateControllableSetSymbol;
    }

    private void expectBecomes() {
        next_symbol();
        current_is(Symbol.BECOMES, "= expected");
    }

    private void expectIdentifier(String errorMsg) {
        next_symbol();
        current_is(Symbol.UPPERIDENT, errorMsg + " identifier expected");
    }

    private void expectLeftCurly() {
        next_symbol();
        current_is(Symbol.LCURLY, "{ expected");
    }

    private void expectRightCurly() {
        next_symbol();
        current_is(Symbol.RCURLY, "} expected");
    }

    private void expectLeftParenthesis() {
        next_symbol();
        current_is(Symbol.LROUND, "( expected");
    }

    private void expectRightParenthesis() {
        next_symbol();
        current_is(Symbol.RROUND, ") expected");
    }

    private void expectComma() {
        next_symbol();
        current_is(Symbol.COMMA, ", expected");
    }

    public List<String> getCompositesNames() {
        ArrayList<String> result = new ArrayList<String>();
        for (String name : getComposites().keySet()) {
            result.add(name);
        }
        return result;
    }
}
