package MTSSynthesis.controller.gr.time;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;

import com.microsoft.z3.ApplyResult;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Goal;
import com.microsoft.z3.Model;
import com.microsoft.z3.RealExpr;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import com.microsoft.z3.Tactic;
import com.microsoft.z3.Z3Exception;

import MTSSynthesis.controller.gr.time.model.Activity;
import MTSSynthesis.controller.gr.time.model.ActivityDefinitions;
import MTSSynthesis.controller.gr.time.model.Result;
import MTSSynthesis.controller.gr.time.model.TimedAutomata;
import MTSSynthesis.controller.gr.time.model.TimedState;
import MTSSynthesis.controller.gr.time.model.TimedTrace;
import MTSSynthesis.controller.gr.time.utils.TimeUtils;

public class GamaComparator<A, S> {
	private Context z3;
	private Map<String,RealExpr> consts;
	private RealExpr URG_VAR;
	private BoolExpr URG_COND;
	private DeltaGenerator deltaGenerator; 
	private DeltaGenerator tempVarGenerator;
	private ActivityDefinitions<A> activityDefinitions; 
	private String stopwatchName1;
	private String stopwatchName2;
	private static final boolean PRINT_GAMA = false;
	private static final boolean PRINT_DETAIL = false;
	private static String URGENT_VAR = "cfkz1";
	private Map<String,RealExpr> tempVar;
	private BoolExpr positive;
	private Set<S> states;
	private int uses = 0;
	private static int limit = 1000;
	private Tactic tac;
	
	public GamaComparator() {
		deltaGenerator = new DeltaGenerator();
		tempVarGenerator = new DeltaGenerator();
		tempVarGenerator.setSymbol("VW40");
		deltaGenerator.changeSymbol();
		try {
			z3 = new Context();
			tac = z3.MkTactic("qe"); // quantifier elimination
			consts = new HashMap<String,RealExpr>(); 
			tempVar = new HashMap<String,RealExpr>();
			URG_VAR = z3.MkRealConst(URGENT_VAR);
			URG_COND = z3.MkLe(URG_VAR, z3.MkReal(0));
			consts.put(URGENT_VAR,URG_VAR);
		} catch (Z3Exception e1) {
			e1.printStackTrace();
			throw new RuntimeException("Could not create Z3 Context or variable.");
		}
	}
	
	
	public GamaComparator(ActivityDefinitions<A> activityDefinitions, Set<S> states, String END_TO_END_NAME1, String END_TO_END_NAME2) {
		this();
		this.activityDefinitions = activityDefinitions;
		boot(activityDefinitions, states, END_TO_END_NAME1, END_TO_END_NAME2);
	}


	private void boot(ActivityDefinitions<A> activityDefinitions,
			Set<S> states, String END_TO_END_NAME1, String END_TO_END_NAME2) {
		BoolExpr[] and = new BoolExpr[(states.size()+activityDefinitions.getActivities().size()+2)*2];
		int i=0;
		for (Activity<A> activity : activityDefinitions.getActivities()) {
			String timer = TimeUtils.getTimer(activity.getName());
			String time = TimeUtils.getTimeFromTimer(timer);
			try {
				addConst(time);
				and[i++] = z3.MkLe(z3.MkReal(0),getConst(timer));
				and[i++] = z3.MkLe(z3.MkReal(0),getConst(time));
			} catch (Z3Exception e) {
				e.printStackTrace();
			}
		}
		this.states = states;
		for (S state : states) {
			String label = TimeUtils.getLabelFromState(state);
			String timer = TimeUtils.getTimer(label);
			String time = TimeUtils.getTimeFromTimer(timer);
			try {
				addConst(time);
				and[i++] = z3.MkLe(z3.MkReal(0),getConst(timer));
				and[i++] = z3.MkLe(z3.MkReal(0),getConst(time));
			} catch (Z3Exception e) {
				e.printStackTrace();
			}
		}
		stopwatchName1 = END_TO_END_NAME1;
		stopwatchName2 = END_TO_END_NAME2;
		try {
			String time1 = TimeUtils.getTimeLabel(stopwatchName2);
			String time2  = TimeUtils.getTimeLabel(stopwatchName2);
			String timer1 = TimeUtils.getTimerFromTime(time1);
			String timer2 = TimeUtils.getTimerFromTime(time2);
			addConst(time1);
			addConst(time2);
			and[i++] = z3.MkLe(z3.MkReal(0),getConst(timer1));
			and[i++] = z3.MkLe(z3.MkReal(0),getConst(time1));
			and[i++] = z3.MkLe(z3.MkReal(0),getConst(timer2));
			and[i++] = z3.MkLe(z3.MkReal(0),getConst(time2));
		} catch (Z3Exception e) {
			e.printStackTrace();
		}
		
		try {
			this.positive = z3.MkAnd(and);
		} catch (Z3Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getStopwatchName1() {
		return stopwatchName1;
	}
	
	public String getStopwatchName2() {
		return stopwatchName2;
	}

	public Result compareControllers(BoolExpr gama1, BoolExpr gama2, String END_TO_END_NAME_1, String END_TO_END_NAME_2){
		try {
			if(uses>limit){
				this.disposeContext();
				this.boot(activityDefinitions, states, stopwatchName1, stopwatchName2);
				uses = 0;
			}
			uses++;
			
			String END_TO_END_TIME_1 = "T" + END_TO_END_NAME_1;
			String END_TO_END_TIME_2  = "T" + END_TO_END_NAME_2;
//			BETTER = END_TO_END_TIME_1 +" > "+ END_TO_END_TIME_2;
//			WORSE = END_TO_END_TIME_1 +" < "+ END_TO_END_TIME_2;
			
			List<BoolExpr> truth = new ArrayList<BoolExpr>();
			BoolExpr question1 = z3.MkGt(getConst(END_TO_END_TIME_2), getConst(END_TO_END_TIME_1));
			BoolExpr question2 = z3.MkGt(getConst(END_TO_END_TIME_1), getConst(END_TO_END_TIME_2));
			
			truth.add(gama1);
			truth.add(gama2);
			truth.add(positive);
			
			Pair<Status,Model> s1 = check(truth, question1);
			Pair<Status,Model> s2 = check(truth, question2);
			gama1.Dispose();
			gama2.Dispose();
			question1.Dispose();
			question2.Dispose();
			if(s2.getFirst().equals(Status.SATISFIABLE)){
				if(s1.getFirst().equals(Status.SATISFIABLE)){
					if(PRINT_DETAIL){
//						System.out.println("Gama 1");
//						System.out.println(gama1.toString());
//						System.out.println("Gama 2");
//						System.out.println(gama2.toString());
//						System.out.println("Model B");
//						System.out.println(s2.getSecond().toString());
//						System.out.println("Model W");
//						System.out.println(s1.getSecond().toString());
					}
					return Result.INCOMPARABLES;
				}else{
					return Result.BETTER;
				}
			}else if(s1.getFirst().equals(Status.UNSATISFIABLE)){
				return Result.EQUALLYGOOD;
			}else {
				return Result.WORSE;
			}
			
		} catch (Z3Exception e) {
			e.printStackTrace();
			return Result.EXCEPTION;
		}		 
	}
	
	
	private Pair<Status, Model> check(List<BoolExpr> truth, BoolExpr question) throws Z3Exception {
		Solver solver = z3.MkSolver("");
		for (BoolExpr boolExpr : truth) {
			solver.Assert(boolExpr);
		}
		solver.Assert(question);
		Status status = solver.Check();
		Model model = null;
		if(PRINT_DETAIL && status.equals(Status.SATISFIABLE)){
			model = solver.Model();
		}
		solver.Dispose();
		return new Pair<Status,Model>(status,model);
	}
	
	
	private RealExpr getConst(String ki){
		String key = ki.trim();
		if(consts.containsKey(key))
			return consts.get(key);
		throw new RuntimeException("Not found key " + key);
//		try {
//			String[] parts = (key.trim()).split("\\+");;
//			if(parts.length == 1){
//				RealExpr r = z3.MkRealConst(key);
//				consts.put(key,r);
//				return r;
//			}else{
//				ArithExpr[] res = new ArithExpr[parts.length];
//				int i = 0;
//				for (String part : parts) {
//					res[i] = getConst(part);
//					i++;
//				}
//				return  (RealExpr) z3.MkAdd(res);
//			}
//			
//		} catch (Z3Exception e) {
//			e.printStackTrace();
//			return null;
//		}
	}
	
	
	public BoolExpr calculateGama(MTS<S, A> scheduled, String END_TO_END_NAME, MTS<S, A> original, Set<A> controllableActions, Set<S> finalStates)
					throws Z3Exception {
		Stack<Pair<A, S>> s  = new Stack<Pair<A, S>>();
		Stack<Pair<A, S>> trace  = new Stack<Pair<A, S>>();
		
		String END_TO_END_START = "s"+ END_TO_END_NAME;
		String END_TO_END_END = "e" + END_TO_END_NAME;
		String END_TO_END_TIME = TimeUtils.getTimeLabel(END_TO_END_NAME);
		String END_TO_END_STOPWATCH = TimeUtils.getTimerFromTime(END_TO_END_TIME);
		
		
		Map<S,TimedState> timedStates = new HashMap<S,TimedState>();
		Map<TimedState,Set<String>> actTimers = new HashMap<TimedState,Set<String>>();
		Set<S> winningStates = new HashSet<S>();
		for (S st : finalStates) {
			if(scheduled.getStates().contains(st))
				winningStates.add(st);
		}
		
		Map<TimedState, String> stateTimers = new HashMap<TimedState,String>();
		Set<String> allTimers = new HashSet<String>();
		//End Z3 Definitions
		allTimers.add(END_TO_END_STOPWATCH);
		
		addConst(END_TO_END_TIME);
		
		Set<String> actualTimers =new HashSet<String>(); 
		
		actualTimers.add(END_TO_END_STOPWATCH);
		
		
		TimedState initial = new TimedState(z3.MkTrue());
		Set<RealExpr> actualValuations = new HashSet<RealExpr>();
		
		actualValuations.add(getConst(END_TO_END_STOPWATCH));
		actualValuations.add(getConst(URGENT_VAR));
		TimedState second = new TimedState(URG_COND);
		
		initial.addSuccessor(z3.MkTrue(), actualValuations, END_TO_END_START, second);
		
		actTimers.put(initial, new HashSet<String>());
		actTimers.put(second, actualTimers);
		
		
		//Add successors of initial state
		for (Pair<A, S> transState : scheduled.getTransitions(scheduled.getInitialState(), TransitionType.REQUIRED)) {
			s.push(transState);
			translateTransitionToPTA(timedStates, actTimers, second, transState, original, controllableActions, allTimers, stateTimers);
		}
		
		//DFS to add all the transitions up to the first goal 
		while(!s.isEmpty()){
			Pair<A, S> v = s.pop();
			
			if(finalStates.contains(v.getSecond())){
				if(!s.isEmpty()){
					Pair<A, S> next = s.lastElement();
					while(!trace.isEmpty() && !isSuccessor(trace.lastElement(), next,scheduled)){
						trace.pop();
					}
				}
			}else{
				trace.add(v);
				for (Pair<A, S> pair : scheduled.getTransitions(v.getSecond(), TransitionType.REQUIRED)) {
					s.push(pair);
					translateTransitionToPTA(timedStates, actTimers, timedStates.get(v.getSecond()), pair, original, controllableActions, allTimers, stateTimers);
				}
			}
		}
		
		TimedAutomata a = new TimedAutomata(initial);
		TimedState ending = new TimedState(z3.MkTrue());
		
		for (S winning : winningStates) {
			if(timedStates.containsKey(winning)){
				BoolExpr entranceCondition = z3.MkEq(getConst(END_TO_END_STOPWATCH), getConst(END_TO_END_TIME));
				Set<RealExpr> resetedClocks = new HashSet<RealExpr>();
				resetedClocks.add(getConst(URGENT_VAR));
				timedStates.get(winning).addSuccessor(entranceCondition, resetedClocks, END_TO_END_END ,ending);
			}else{
//				System.out.println("?");
			}
		}
		
		a.addWinning(ending);
		
		return calculateGama(a, allTimers);
	}
	
	
	private BoolExpr calculateGama(TimedAutomata a, Set<String> allTimers) {
		Queue<TimedState> pendings = new LinkedList<TimedState>();		
		Map<TimedState,BoolExpr> calculated = new HashMap<TimedState,BoolExpr>();
		HashSet<TimedState> pendingsH  = new HashSet<TimedState>(); 
		for (TimedState s : a.getWinningStates()) {
			pendings.addAll(s.getPredecessors());
			pendingsH.addAll(s.getPredecessors());
			calculated.put(s, s.getCondition());
		}
		
		
		while(!pendings.isEmpty()){
			try {
				TimedState next = pendings.poll();
				pendingsH.remove(next);
				
				if(!calculated.containsKey(next)){
					boolean allCalculated = true;
					for(TimedState s :next.getSuccessors()){
						allCalculated = allCalculated && calculated.containsKey(s);
					}
					if(allCalculated){
						BoolExpr[] or = new BoolExpr[next.getSuccessors().size()];
						int i = 0;
						for(TimedState s :next.getSuccessors()){
							BoolExpr expr = pre(next, next.getTransition(s), s, calculated.get(s), allTimers);
							or[i] = expr;
							i++;
						}
						if(or.length>1){
							calculated.put(next, z3.MkOr(or));							
						}else{
							calculated.put(next, or[0]);
						}
						pendings.addAll(next.getPredecessors());
						pendingsH.addAll(next.getPredecessors());
					}else{
						pendings.add(next);
						pendingsH.add(next);
					}
				}
				
			} catch (Z3Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		BoolExpr gama = calculated.get(a.getInicial());
		if(PRINT_GAMA){
			Long timestamp = (Calendar.getInstance().getTimeInMillis());
			try {
				printToFile(gama.SExpr().toString(), timestamp+"_GAMA");
			} catch (Z3Exception e) {
				e.printStackTrace();
			}
		}
		return gama;
	}
	
	
	
	private BoolExpr pre(TimedState s1, TimedTrace e, TimedState s2, BoolExpr phi, Set<String> allTimers) {
		return left(s1, cosito(e, leftRight(s2,phi, allTimers)), allTimers);
	}
	
	
	private BoolExpr cosito(TimedTrace e, BoolExpr phi) {
		BoolExpr res = e.getCondition();
		
		try {
			if(!phi.IsTrue() ){
				Set<RealExpr> actualValuations = e.getResetedClocks();
				
				Expr[] from = new Expr[actualValuations.size()];
				Expr[] to = new Expr[actualValuations.size()];
				
				int i = 0;
				
				for (RealExpr symbol : actualValuations) {
					from[i] = symbol;
					to[i] = z3.MkReal(0);
					i++;
				}
				phi = (BoolExpr) phi.Substitute(from, to);
				BoolExpr[] and = new BoolExpr[2];
				and[0] = phi;
				and[1] = res;
				res = z3.MkAnd(and);
			}
		} catch (Z3Exception e1) {
			e1.printStackTrace();
		}
		return res;
	}
	
	
	private BoolExpr leftRight(TimedState s2, BoolExpr phi, Set<String> allTimers) {
		return left(s2,phi, allTimers);
	}
	
	private BoolExpr left(TimedState s2, BoolExpr phi, Set<String> allTimers){
		BoolExpr res = null;
		try {
			if(s2.getCondition().equals(URG_COND)){
				BoolExpr[] expr = new BoolExpr[2];
				expr[0] = s2.getCondition();
				expr[1] = phi;
				res = z3.MkAnd(expr);
			}else{
				res = right(s2, phi, allTimers); 
			}	
		} catch (Z3Exception e1) {
			e1.printStackTrace();
		}
		return res;
	}
	
	private BoolExpr right(TimedState s2, BoolExpr phi, Set<String> allTimers){
		BoolExpr res = null;
		try {
			BoolExpr[] and = new BoolExpr[2];
			and[0] = phi;
			and[1] = s2.getCondition();
			
			res 	= z3Existencial(z3.MkAnd(and), allTimers);
		} catch (Z3Exception e1) {
			e1.printStackTrace();
		}
		return res;
	}
	
	
	private void translateTransitionToPTA(
			Map<S,TimedState> timedStates,
			Map<TimedState, Set<String>> actTimers,
			TimedState previous, Pair<A, S> transState,
			MTS<S, A> original, Set<A> controllableActions, Set<String> allTimers, Map<TimedState, String> stateTimers)
					throws Z3Exception {
		
		TimedState next = null;
		Set<RealExpr> actualValuations = new HashSet<RealExpr>();
		BoolExpr entranceCondition = null;
		BoolExpr condition;
		String nextStateTimer = null;
		Set<String> actualTimers = null;
		S state = transState.getSecond();
		if(timedStates.keySet().contains(state)){
			next = timedStates.get(state);
			actualTimers = actTimers.get(next);
		}else{
			actualTimers = new HashSet<String>(actTimers.get(previous));
		}
		
		A a = transState.getFirst();
		String transition = (String) transState.getFirst();
		
		if(activityDefinitions.isIntitiatingAction(a) || activityDefinitions.isTerminatingAction(a)){
			String timer = TimeUtils.getTimer(activityDefinitions.getAssociatedActivity(a).getName());
			if(activityDefinitions.isIntitiatingAction(a)){
				RealExpr realTimer = getConst(timer);
				actualValuations.add(realTimer);
				actualTimers.add(timer);
				allTimers.add(timer);
				String time = TimeUtils.getTimeFromTimer(timer);
				addConst(time);
				entranceCondition = z3.MkTrue();
			}else{
				entranceCondition = z3.MkEq(getConst(timer), getConst(TimeUtils.getTimeFromTimer(timer)));
				actualTimers.remove(timer);
			}
		}else{
			entranceCondition = z3.MkTrue();
		}
		
		if(TimeUtils.isFinal(original, state) || TimeUtils.isTransient(state, original, controllableActions)){
			condition = URG_COND;
			actualValuations.add(getConst(URGENT_VAR));
		}
		else{
			Set<String> conditionTimers = new HashSet<String>();
			conditionTimers.addAll(actualTimers);
			if(actualTimers.size() == 1){
				String label = TimeUtils.getLabelFromState(state);
				nextStateTimer = TimeUtils.getTimer(label);
				RealExpr realTimer = getConst(nextStateTimer);
				actualValuations.add(realTimer);
				allTimers.add(nextStateTimer);
				String time = TimeUtils.getTimeFromTimer(nextStateTimer);
				addConst(time);
//				System.out.println("Reset: "+ state +", label: " + transition);
				conditionTimers.add(nextStateTimer);
			}
			condition = respectZ3Timers(conditionTimers);
		}
		
//		if(!previous.getCondition().equals(URG_COND) && hasStateTimers(actTimers.get(previous))){
		if(stateTimers.containsKey(previous)){
			String stateTimer = stateTimers.get(previous);
//			System.out.println("Label: "+transition+", Eq"+stateTimer + "-->" + state);
			BoolExpr soujournCondition = z3.MkEq(getConst(stateTimer), getConst(TimeUtils.getTimeFromTimer(stateTimer)));
			
			if(entranceCondition.equals(z3.MkTrue())){
				entranceCondition = soujournCondition;
			}else {
				//TODO: It should not enter to the else branch, since if we are using the state clock 
				//to represent time here means that there are not possible endings.  
				BoolExpr[] entranceConditions = new BoolExpr[2];
				entranceConditions[0] = entranceCondition;
				entranceConditions[1] = soujournCondition;
				entranceCondition = z3.MkAnd(entranceConditions);
			}
		}
		
		if(next == null){
			next =  new TimedState(condition);
			timedStates.put(state, next);
		}else{
			if(!condition.equals(next.getCondition())){
				throw new RuntimeException("The condition of the time state is different depending of the path.");
			}
		}
		
		actTimers.put(next,actualTimers);
		if(nextStateTimer != null){ stateTimers.put(next, nextStateTimer);}
		
		previous.addSuccessor(entranceCondition, actualValuations, transition, next);
	}
	

	private static <A,S> boolean isSuccessor(Pair<A, S> lastElement,
			Pair<A, S> next,
			MTS<S, A> result) {
		for (Pair<A, S> pair : result.getTransitions(lastElement.getSecond(), TransitionType.REQUIRED)) {
			if(pair.equals(next))
				return true;
		}
		return false;
	}
	
	
	private void addConst(String E2EndClock) throws Z3Exception {
		if(!consts.keySet().contains(TimeUtils.getTimerFromTime(E2EndClock)))
			consts.put(TimeUtils.getTimerFromTime(E2EndClock), z3.MkRealConst(TimeUtils.getTimerFromTime(E2EndClock)));
		if(!consts.keySet().contains(E2EndClock))
			consts.put(E2EndClock, z3.MkRealConst(E2EndClock));
	}
	
	
	private BoolExpr respectZ3Timers(Set<String> actualTimers) {
		try {
			if(actualTimers.size() == 0) {
				return z3.MkTrue();
			}else if(actualTimers.size() == 1) {
				for (String timer : actualTimers) {
					return z3.MkLe(getConst(timer), getConst(TimeUtils.getTimeFromTimer(timer)));
				}
			}else {
				BoolExpr[] and = new BoolExpr[actualTimers.size()];
				int i = 0;
				for (String timer : actualTimers) {
					and[i++] = z3.MkLe(getConst(timer), getConst(TimeUtils.getTimeFromTimer(timer)));
				}
				return z3.MkAnd(and);
			}
		} catch (Z3Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
//	private static Map<BoolExpr,BoolExpr> exp = new HashMap<BoolExpr, BoolExpr>();
	
	private BoolExpr z3Existencial(BoolExpr formula, Set<String> allTimers) {
		try {
			String delta = deltaGenerator.getNextDelta();
			RealExpr x = z3.MkRealConst(delta);
			
			//TODO: @ezecastellano: The list of allTimers contains all the possible realTime variables
			//we are using in all the timed automaton. It might improve the performance if we 
			//only try to replace the ones that are in the expression.
			Expr[] from = new Expr[allTimers.size()]; 
			Expr[] to = new Expr[allTimers.size()]; 
			Expr[] tempTo = new Expr[allTimers.size()]; 
			
			int i = 0;
			for (String timer : allTimers) {
				RealExpr t = getConst(timer);
				RealExpr[] add = new RealExpr[2];
				add[0] = t;
				add[1] = x;
				from[i] = t;
				to[i] = z3.MkAdd(add);
				tempTo[i] = getTempConst();
				i++;
			}
			
			BoolExpr[] and = new BoolExpr[2];
			BoolExpr tmp = (BoolExpr) formula.Substitute(from,tempTo);
			and[0] = (BoolExpr) tmp.Substitute(tempTo,to);
			and[1] = z3.MkLe(z3.MkReal(0), x);
			Expr p = z3.MkAnd(and);
			
			Expr ex = z3.MkExists(new Expr[] {x}, p, 1, null, null, null, null);
			
			Goal g = z3.MkGoal(true, true, false);
			g.Assert((BoolExpr)ex);
			
			ApplyResult a = tac.Apply(g); // look at a.Subgoals
			
			BoolExpr[] result = new BoolExpr[a.Subgoals()[0].Formulas().length];
			i=0;
			for (BoolExpr boolExpr : a.Subgoals()[0].Formulas()) {
				result[i++] = boolExpr;
			}
			
			BoolExpr res = (BoolExpr) z3.MkAnd(result);
			a.Dispose();
			g.Dispose();
			p.Dispose();
			ex.Dispose();
			tmp.Dispose();
			i = 0;
			while(i<allTimers.size()) {
				to[i++].Dispose();
			}
			tempVarGenerator.reset();
			
			return res;
		} catch (Z3Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	private Expr getTempConst() throws Z3Exception {
		String name = tempVarGenerator.getNextDelta();
		if(!tempVar.containsKey(name)){
			tempVar.put(name, z3.MkRealConst(name));
		}
		return tempVar.get(name);
	}


	private void printToFile(String content, String name) {
		try {
			String dir = System.getProperty("user.dir");
			
			File file = new File(dir+name+".txt");
			
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
			
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void disposeContext(){
		z3.Dispose();
	}

	public void disposeAll() {
		//Free all variables
		try {
			positive.Dispose();
			for(String key : tempVar.keySet()){
				tempVar.get(key).Dispose();
			}
			for(String key : consts.keySet()){
				consts.get(key).Dispose();
			}
			tac.Dispose();
		} catch (Z3Exception e) {
			e.printStackTrace();
		}
		this.disposeContext();
	}
}