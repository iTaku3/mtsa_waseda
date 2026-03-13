package MTSA2RATSY.adapter;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;

import MTSA2RATSY.model.ControllableType;
import MTSA2RATSY.model.SymbolicSpecification;
import MTSA2RATSY.model.formula.BinaryNode;
import MTSA2RATSY.model.formula.Formula;
import MTSA2RATSY.model.formula.FormulaNode;
import MTSA2RATSY.model.formula.Operator;
import MTSA2RATSY.model.formula.SignalNode;
import MTSA2RATSY.model.formula.UnaryNode;
import MTSA2RATSY.model.signal.Signal;
import MTSSynthesis.ar.dc.uba.model.condition.Fluent;
import MTSSynthesis.ar.dc.uba.model.language.Symbol;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.LTS;

public class SymbolicBuilder {
	public static String STATE_PREFIX		= "s_";
	public static String FLUENT_PREFIX		= "f_";
	
	protected MTSAWrapper 			wrapper;
	protected SymbolicSpecification specification;
	
	//dummy signals
	protected Signal trueSignal;
	protected Signal falseSignal;		
	//operators
	protected Operator equalOperator;
	protected Operator andOperator;
	protected Operator orOperator;
	protected Operator impliesOperator;
	protected Operator nextOperator;
	protected Operator alwaysOperator;
	protected Operator eventualOperator;
	//controllableType
	protected ControllableType controllableType;
	protected ControllableType nonControllableType;
	
	public MTSAWrapper getWrapper(){
		return wrapper;
	}
	
	public SymbolicSpecification getSpecification(){
		return specification;
	}
	
	public SymbolicBuilder(MTSAWrapper wrapper){
		this.wrapper		= wrapper;
		this.specification	= new SymbolicSpecification();
		
		initializeOperators();
		
		buildSpecification();
	}
	
	protected void initializeOperators(){
		//dummy signals
		trueSignal		= new Signal("1", true);
		falseSignal		= new Signal("0", true);		
		//operators
		equalOperator	= new Operator("=");
		andOperator		= new Operator("&amp;&amp;");
		orOperator		= new Operator("||");
		impliesOperator	= new Operator("-&gt;");
		nextOperator	= new Operator("X");
		alwaysOperator	= new Operator("G");
		eventualOperator= new Operator("F");
		//controllable types
		controllableType	= new ControllableType(true);
		nonControllableType	= new ControllableType(false);
	}
	
	public void saveSpecification(String outputFileLocation){
		PrintWriter out;
		try {
			out = new PrintWriter(outputFileLocation);
			
			out.println(getSpecification().getXMLDescription());	
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	protected void buildSpecification(){
		Map<String, LTS<Long, String>> ltss		= wrapper.getAllLTSs();
		Map<String, LTS<Long, String>> safety	= wrapper.getSafetyProcesses();
		Set<Fluent> fluentValues				= wrapper.getFluents();
		Set<String> controllableActions			= wrapper.getControllableActions();
		Set<String> nonControllableActions		= new HashSet<String>();
		Set<String> actions						= wrapper.getActions();
		
		
		//mapping-bookkeeping
		Map<String, Fluent> fluents				= new HashMap<String, Fluent>();
		Map<String, SignalNode> actionSignals		= new HashMap<String, SignalNode>();
		Map<String, SignalNode> fluentSignals		= new HashMap<String, SignalNode>();
		Map<String, SignalNode> stateSignals		= new HashMap<String, SignalNode>();
		Map<String, Set<String>> automataAlphabet	= new HashMap<String, Set<String>>();
		Map<String, Set<String>> automataNAlphabet	= new HashMap<String, Set<String>>();
		Map<String, Set<String>> fluentAlphabet		= new HashMap<String, Set<String>>();
		Map<String, Set<String>> fluentNAlphabet	= new HashMap<String, Set<String>>();
		
		for(Fluent fluent : fluentValues){
			fluents.put(fluent.getName(), fluent);
		}
		
		
		//initialize automata-alphabet dictionaries
		for(String key:ltss.keySet()){
			LTS<Long, String> lts	= ltss.get(key);
			automataAlphabet.put(key, lts.getActions());
			automataNAlphabet.put(key, Sets.difference(actions, lts.getActions()));
		}		

		Set<String> fluentActions;
		
		//initialize fluent-alphabet dictionaries
		for(String key: fluents.keySet()){
			Fluent fluent	= fluents.get(key);
			fluentActions	= new HashSet<String>();
			for(Symbol symbol: fluent.getInitiatingActions()){
				fluentActions.add(symbol.toString());
			}
			for(Symbol symbol: fluent.getTerminatingActions()){
				fluentActions.add(symbol.toString());
			}			
			fluentAlphabet.put(key, fluentActions);
			
			fluentNAlphabet.put(key, Sets.difference(actions, fluentActions));
		}				
		
		boolean controllable;
		
		//add action signals
		Signal currentSignal	= null;
		for(String action : actions){
			controllable	= controllableActions.contains(action);
			currentSignal	= new Signal(action, controllable);
			actionSignals.put(action, new SignalNode(currentSignal));
			
			if(controllable){
				specification.addSystemSignal(currentSignal);
			}else{
				nonControllableActions.add(action);
				specification.addEnvironmentSignal(currentSignal);
			}
		}
		
		//add states signals
		for(String key:ltss.keySet()){
			LTS<Long, String> lts	= ltss.get(key);
			int stateCount	= lts.getStates().size();
			currentSignal	= new Signal(STATE_PREFIX + key, true, 1, stateCount);
			stateSignals.put(STATE_PREFIX + key, new SignalNode(currentSignal));
			specification.addSystemSignal(currentSignal);
		}
		
		//add fluents states
		for(String key: fluents.keySet()){
			Fluent fluent	= fluents.get(key);
			currentSignal	= new Signal(FLUENT_PREFIX + fluent.getName(), true);
			stateSignals.put(FLUENT_PREFIX + fluent.getName(), new SignalNode(currentSignal));			
			specification.addSystemSignal(currentSignal);
		}
		
		BinaryNode leftNode		= null;
		BinaryNode currentNode	= null;
		BinaryNode nextNode		= null;
		SignalNode	currentSignalNode;

		//add fluent formulas
		for(String key:fluents.keySet()){
			Fluent fluent	= fluents.get(key);
			
			Set<SignalNode> initiatingSignals	= new HashSet<SignalNode>();
			Set<SignalNode> initiatingControllableSignals		= new HashSet<SignalNode>();
			Set<SignalNode> initiatingNonControllableSignals	= new HashSet<SignalNode>();
			Set<SignalNode> terminatingSignals	= new HashSet<SignalNode>();
			Set<SignalNode> terminatingControllableSignals		= new HashSet<SignalNode>();
			Set<SignalNode> terminatingNonControllableSignals	= new HashSet<SignalNode>();
			Set<SignalNode> fluentActionSignals	= new HashSet<SignalNode>();
			Set<SignalNode> externalSignals		= new HashSet<SignalNode>();
			
			//construct signalNode structs
			for(Symbol symbol: fluent.getInitiatingActions()){
				currentSignalNode	= actionSignals.get(symbol.toString());
				initiatingSignals.add(currentSignalNode);
				fluentActionSignals.add(currentSignalNode);
				if(controllableActions.contains(symbol.toString())){
					initiatingControllableSignals.add(currentSignalNode);
				}else{
					initiatingNonControllableSignals.add(currentSignalNode);
				}
			}
			
			for(Symbol symbol: fluent.getTerminatingActions()){
				currentSignalNode	= actionSignals.get(symbol.toString());
				terminatingSignals.add(currentSignalNode);
				fluentActionSignals.add(currentSignalNode);
				if(controllableActions.contains(symbol.toString())){
					terminatingControllableSignals.add(currentSignalNode);
				}else{
					terminatingNonControllableSignals.add(currentSignalNode);
				}
			}		
			
			for(String action: fluentNAlphabet.get(fluent.getName())){
				externalSignals.add(actionSignals.get(action));
			}
			
			//define current and next node (states)
			currentNode	= new BinaryNode(equalOperator, stateSignals.get(FLUENT_PREFIX + fluent.getName())
					, new SignalNode(fluent.isInitialValue() ? trueSignal : falseSignal));
			nextNode	= new BinaryNode(equalOperator, stateSignals.get(FLUENT_PREFIX + fluent.getName())
					, new SignalNode(fluent.isInitialValue() ? falseSignal : trueSignal));
			
			//add initial formulas
			specification.addSystemInitialFormula(new Formula("init sys fluent " + fluent.getName() + " formula", initialFormula(currentNode, initiatingSignals, fluentActionSignals)
					, new ControllableType(true)));					
			
			//adding transition formulas
			specification.addSystemSafetyFormula(new Formula(fluent.getName() + " initiating sys trans", transitionFormula(currentNode
					, initiatingSignals, nextNode), controllableType));
			specification.addSystemSafetyFormula(new Formula(fluent.getName() + " terminating sys trans",transitionFormula(nextNode
					, terminatingSignals, currentNode), controllableType));
			specification.addSystemSafetyFormula(new Formula(fluent.getName() + " external event supressing trans",selfLoopFormula(currentNode
					, externalSignals), controllableType));
			
			//adding enabling formulas
			specification.addSystemSafetyFormula(new Formula(fluent.getName() + " initiating env enabling", enablingFormula(currentNode, initiatingSignals, terminatingNonControllableSignals
					, initiatingNonControllableSignals), nonControllableType));
			specification.addSystemSafetyFormula(new Formula(fluent.getName() + " initiating sys enabling", enablingFormula(currentNode, initiatingSignals
					, terminatingNonControllableSignals, terminatingControllableSignals, initiatingControllableSignals), controllableType));			
			specification.addSystemSafetyFormula(new Formula(fluent.getName() + " terminating env enabling", enablingFormula(nextNode, terminatingSignals, initiatingNonControllableSignals
					, terminatingNonControllableSignals), nonControllableType));
			specification.addSystemSafetyFormula(new Formula(fluent.getName() + " terminating sys enabling", enablingFormula(nextNode, terminatingSignals
					, initiatingNonControllableSignals, initiatingControllableSignals, terminatingControllableSignals), controllableType));
		}
		
		//add automata transition formulas
		for(String key:ltss.keySet()){
			LTS<Long, String> lts			= ltss.get(key);
			SignalNode automatonSignalNode	= stateSignals.get(STATE_PREFIX + key);
			
			Set<SignalNode> automatonSignals	= new HashSet<SignalNode>();
			Set<SignalNode> externalSignals		= new HashSet<SignalNode>();			
			
			for(String action: automataAlphabet.get(key)){
				automatonSignals.add(actionSignals.get(action));
			}			

			for(String action: automataNAlphabet.get(key)){
				externalSignals.add(actionSignals.get(action));
			}						
			
			for(Long fromState : lts.getTransitions().keySet()){
				//add next transitions
				Iterator<Pair<String, Long>> currentRelationIterator	= lts.getTransitions().get(fromState).iterator();
				Set<SignalNode> enabledLocalActions						= new HashSet<SignalNode>();
				
				while(currentRelationIterator.hasNext()){
					Pair<String,Long> currentPair	= currentRelationIterator.next();
					String currentAction			= currentPair.getFirst();
					Long toState					= currentPair.getSecond();
					
					//enabledLocalActions = 
					
					currentNode	= initializeNumericSignal(automatonSignalNode , fromState);
					nextNode	= initializeNumericSignal(automatonSignalNode , toState);
					
					specification.addSystemSafetyFormula(new Formula(key + " " + String.valueOf(fromState) + " " + currentAction + " sys trans to " + 
							String.valueOf(toState), transitionFormula(currentNode
							, actionSignals.get(currentAction), nextNode), controllableType));					
				}
				//add self loop transition
				specification.addSystemSafetyFormula(new Formula(key + " " + String.valueOf(fromState) + " external event supressing trans",selfLoopFormula(currentNode
						, externalSignals), controllableType));
				//add enabling transition
				
				//TODO: add enabling transitions
			}
		}		
	}
	
	
	//utility methods for formula handling
	private BinaryNode initializeSignal(SignalNode signalNode, boolean value){
		return new BinaryNode(equalOperator, signalNode, new SignalNode(value? trueSignal : falseSignal));
	}

	private BinaryNode initializeNumericSignal(SignalNode signalNode, Long value){
		return new BinaryNode(equalOperator, signalNode, new SignalNode(new Signal(String.valueOf(value), true)));
	}	
	
	private FormulaNode imply(FormulaNode leftNode, FormulaNode rightNode){
		return new BinaryNode(impliesOperator, leftNode, rightNode);
	}	
	
	private FormulaNode next(FormulaNode node){
		return new UnaryNode(nextOperator, node);
	}
	
	private FormulaNode always(FormulaNode node){
		return new UnaryNode(alwaysOperator, node);
	}

	private FormulaNode eventually(FormulaNode node){
		return new UnaryNode(eventualOperator, node);
	}	

	private FormulaNode applySetFormula(Operator operator, Set<FormulaNode> nodes){
		return applySetFormula(operator, nodes, true);
	}	
	
	private FormulaNode applySetFormula(Operator operator, Set<FormulaNode> nodes, boolean encloseFormula){
		FormulaNode leftNode		= null;
		
		//add initial formulas
		for(FormulaNode node : nodes){
			if(leftNode == null){
				leftNode	= node;
			}else{
				leftNode 	= new BinaryNode(operator, leftNode, node, encloseFormula);
			}
		}	
		
		return leftNode;
	}	
	
	private FormulaNode conjunct(FormulaNode leftNode, FormulaNode rightNode){
		return new BinaryNode(andOperator, leftNode, rightNode, false);
	}
	
	private FormulaNode conjunct(Set<FormulaNode> nodes){
		return applySetFormula(andOperator, nodes, false);
	}	
	
	private FormulaNode conjunct(Set<SignalNode> signalNodes, boolean nodesValue){
		Set<FormulaNode> currentSubFormula = new HashSet<FormulaNode>();
		
		for(SignalNode node: signalNodes){
			currentSubFormula.add(initializeSignal(node, nodesValue));	
		}
		
		return conjunct(currentSubFormula);
	}	
	
	private FormulaNode conjunctSignals(Set<SignalNode> signalNodes, boolean value){
		Set<FormulaNode> currentSubFormula = new HashSet<FormulaNode>();
		
		for(SignalNode node: signalNodes){
			currentSubFormula	= new HashSet<FormulaNode>();
			currentSubFormula.add(initializeSignal(node, value));
		}
		
		return conjunct(currentSubFormula);		
	}	
	
	private FormulaNode disjunct(FormulaNode leftNode, FormulaNode rightNode){
		return new BinaryNode(orOperator, leftNode, rightNode);
	}		
	
	private FormulaNode disjunct(Set<FormulaNode> nodes){
		return applySetFormula(orOperator, nodes);
	}
	
	private FormulaNode disjunct(Set<SignalNode> signalNodes, boolean nodesValue){
		Set<FormulaNode> currentSubFormula = new HashSet<FormulaNode>();
		
		for(SignalNode node: signalNodes){
			currentSubFormula.add(initializeSignal(node, nodesValue));	
		}
		
		return disjunct(currentSubFormula);
	}	
	
	private FormulaNode disjunctSignals(Set<SignalNode> signalNodes, boolean value){
		Set<FormulaNode> currentSubFormula = new HashSet<FormulaNode>();
		
		for(SignalNode node: signalNodes){
			currentSubFormula	= new HashSet<FormulaNode>();
			currentSubFormula.add(initializeSignal(node, value));
		}
		
		return disjunct(currentSubFormula);		
	}	
	
	private FormulaNode exclusiveDisjunctSignals(Set<SignalNode> signalNodes){
		Set<FormulaNode> formulaConjunction = new HashSet<FormulaNode>();
		
		Set<FormulaNode> currentSubFormula;
		
		for(SignalNode node: signalNodes){
			currentSubFormula	= new HashSet<FormulaNode>();
			currentSubFormula.add(initializeSignal(node, true));
			for(SignalNode node2: signalNodes){
				if(node2.getSignal().getID().equals(node2.getSignal().getID()))
					continue;
				currentSubFormula.add(initializeSignal(node2, false));	
			}	
			formulaConjunction.add(conjunct(currentSubFormula));
		}
		
		return disjunct(formulaConjunction);
	}

	//(s_i && (only one outgoing event) && (none of the other local events) 
	private FormulaNode initialFormula(FormulaNode currentStateNode, Set<SignalNode> outgoingNodes, Set<SignalNode> completeNodes){
		Set<FormulaNode> leftSideNodes	= new HashSet<FormulaNode>();
		leftSideNodes.add(currentStateNode);
		leftSideNodes.add(exclusiveDisjunctSignals(outgoingNodes));
		leftSideNodes.add(conjunctSignals(Sets.difference(completeNodes,  outgoingNodes), false));
		return conjunct(leftSideNodes);
	}	
	
	//G(s_i && one event to s_j up -> X(only one of the outgoing if there's anay and none of the others)) 	 FOR ENV THETA
	private FormulaNode enablingFormula(FormulaNode currentStateNode, SignalNode outgoingNode, Set<SignalNode> exclusiveEnvNodes, Set<SignalNode> disabledEnvNodes){
		Set<SignalNode> dummySet	= new HashSet<SignalNode>();
		dummySet.add(outgoingNode);
		return enablingFormula(currentStateNode, dummySet, exclusiveEnvNodes, disabledEnvNodes);
	}	
	
	//G(s_i && (at least one event to s_j up) -> X(only one of the outgoing if there's any and none of the others)) 	 FOR ENV THETA
	private FormulaNode enablingFormula(FormulaNode currentStateNode, Set<SignalNode> outgoingNodes, Set<SignalNode> exclusiveEnvNodes, Set<SignalNode> disabledEnvNodes){
		Set<FormulaNode> leftSideNodes	= new HashSet<FormulaNode>();
		leftSideNodes.add(currentStateNode);
		leftSideNodes.add(disjunctSignals(outgoingNodes, true));
		
		FormulaNode rightSideNode		= null;
		if(exclusiveEnvNodes.size() > 0){//set one exclusive outgoing event
			if(disabledEnvNodes.size() > 0){
				rightSideNode				= next(conjunct(exclusiveDisjunctSignals(exclusiveEnvNodes), conjunctSignals(disabledEnvNodes, false)));
			}else{
				rightSideNode				= next(exclusiveDisjunctSignals(exclusiveEnvNodes));
			}
		}else{//if no outgoing event is enabled just let them down
			rightSideNode				= next(conjunctSignals(disabledEnvNodes, false));
		}
		
		return always(imply(conjunct(leftSideNodes), rightSideNode));
	}		

	//G(s_i && one event to s_j up -> X(only one of the outgoing while none of the others || at least one of the others)) 	FOR SYS THETA
	private FormulaNode enablingFormula(FormulaNode currentStateNode, SignalNode outgoingNode, Set<SignalNode> envNodes
			, Set<SignalNode> exclusiveSysNodes, Set<SignalNode> disabledSysNodes){
		Set<SignalNode> dummySet	= new HashSet<SignalNode>();
		dummySet.add(outgoingNode);
		
		return enablingFormula(currentStateNode, dummySet, envNodes, exclusiveSysNodes, disabledSysNodes);
	}
	
	//G(s_i && (at least one event to s_j up) -> X(only one of the outgoing while none of the others || at least one of the others)) 	FOR SYS THETA
	private FormulaNode enablingFormula(FormulaNode currentStateNode, Set<SignalNode> outgoingNodes, Set<SignalNode> envNodes
			, Set<SignalNode> exclusiveSysNodes, Set<SignalNode> disabledSysNodes){
		Set<FormulaNode> leftSideNodes	= new HashSet<FormulaNode>();

		leftSideNodes.add(currentStateNode);
		leftSideNodes.add(disjunctSignals(outgoingNodes, true));
		
		FormulaNode rightSideNode		= null;
		if(exclusiveSysNodes.size() > 0){//set one exclusive outgoing event
			if(envNodes.size() > 0){
				if(disabledSysNodes.size() > 0){
					rightSideNode			= next(conjunct(imply(conjunctSignals(envNodes, false), exclusiveDisjunctSignals(exclusiveSysNodes))
							, conjunctSignals(disabledSysNodes, false)));
				}else{
					rightSideNode			= next(imply(conjunctSignals(envNodes, false), exclusiveDisjunctSignals(exclusiveSysNodes)));
				}
			}else{
				if(disabledSysNodes.size() > 0){
					rightSideNode			= next(conjunct(exclusiveDisjunctSignals(exclusiveSysNodes), conjunctSignals(disabledSysNodes, false)));
				}else{
					rightSideNode			= next(exclusiveDisjunctSignals(exclusiveSysNodes));
				}
			}
		}else{//if no outgoing event is enabled just let them down
			rightSideNode				= next(conjunctSignals(disabledSysNodes, false));
		}		
		
		return always(imply(conjunct(leftSideNodes), rightSideNode));
	}	
	
	
	//G(s_i && (at least one event to s_j up) -> X(s_j)) 
	private FormulaNode transitionFormula(FormulaNode currentStateNode, Set<SignalNode> outgoingNodes, FormulaNode nextStateNode){
		Set<FormulaNode> leftSideNodes	= new HashSet<FormulaNode>();
		leftSideNodes.add(currentStateNode);
		leftSideNodes.add(disjunct(outgoingNodes, true));
		return always(imply(conjunct(leftSideNodes), next(nextStateNode)));
	}
	
	//G(s_i && one event to s_j up -> X(s_j)) 	
	private FormulaNode transitionFormula(FormulaNode currentStateNode, SignalNode outgoingNode, FormulaNode nextStateNode){
		Set<FormulaNode> leftSideNodes	= new HashSet<FormulaNode>();
		leftSideNodes.add(currentStateNode);
		leftSideNodes.add(initializeSignal(outgoingNode, true));
		return always(imply(conjunct(leftSideNodes), next(nextStateNode)));
	}	
	
	// G(s_i && (at least one external up) -> X(s_i))
	private FormulaNode selfLoopFormula(FormulaNode currentStateNode, Set<SignalNode> externalNodes){
		Set<FormulaNode> leftSideNodes	= new HashSet<FormulaNode>();
		leftSideNodes.add(currentStateNode);
		leftSideNodes.add(disjunct(externalNodes, true));
		return always(imply(conjunct(leftSideNodes), next(currentStateNode)));
	}	
}
