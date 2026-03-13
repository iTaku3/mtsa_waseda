package MTSSynthesis.controller.gr.time;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Model;
import com.microsoft.z3.RealExpr;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import com.microsoft.z3.Z3Exception;

import MTSSynthesis.controller.gr.StrategyState;
import MTSSynthesis.controller.model.gr.GRGame;

public abstract class RuleBasedCalculator<A,S> {
	
	private static String controllerLabel;
	private static Set<String> allTimers = new HashSet<String>();
	private static boolean LOG = true;

	private static Context z3;
	private static Map<String,RealExpr> consts;

	private static RealExpr getConst(String ki){
		String key = ki.trim();
		if(consts.containsKey(key))
			return consts.get(key);
		try {
			String[] parts = (key.trim()).split("\\+");;
			if(parts.length == 1){
//				System.out.println(key);
				RealExpr r = z3.MkRealConst(key);
				consts.put(key,r);
				return r;
			}else{
				ArithExpr[] res = new ArithExpr[parts.length];
				int i = 0;
				for (String part : parts) {
					res[i] = getConst(part);
					i++;
				}
				return (RealExpr) z3.MkAdd(res);
			}
			
		} catch (Z3Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	 public static <A,S> void calculateControllerExpression(GRGame<S> game, MTS<StrategyState<S, Integer>, A> result ){
		 getControllerExpression(game, result, "C");
	 }

	 
	 private static  <A,S> List<String> getTraceString(Stack<Pair<A, StrategyState<S, Integer>>> trace){
		 List<String> strange = new ArrayList<String>(); 
		 for (Pair<A, StrategyState<S, Integer>> pair : trace) {
			 strange.add(pair.getFirst().toString());
		 }
		 return strange;
	 }	 
	 
	 private static  <A,S> List<Set<String>> getNTTraceString(List<Set<Pair<A, StrategyState<S, Integer>>>> nTTrace){
		 List<Set<String>> filtered = new ArrayList<Set<String>>(); 
		 for (Set<Pair<A, StrategyState<S, Integer>>> set : nTTrace) {
			 Set<String> actual = new HashSet<String>();
			 for (Pair<A, StrategyState<S, Integer>> pair : set) {
				 actual.add(pair.getFirst().toString());
			}
			 filtered.add(actual);
		 }
		 return filtered;
	 }	 
	 
	 public static <A,S> void compareControllers(GRGame<S> game, ArrayList<MTS<StrategyState<S, Integer>, A>> results ){
		 try {
			 
			 MTS<StrategyState<S, Integer>, A> result1 = results.get(0);
			 MTS<StrategyState<S, Integer>, A> result2 = results.get(1);

			 String E2EndClock1 = "Tf1";
			 String E2EndClock2  = "Tf2";

			 //Start Z3 Definitions
			 consts = new HashMap<String,RealExpr>(); 
			 z3 = new Context();
			 allTimers = new HashSet<String>();

			 BoolExpr gama1 = getControllerExpression(game, result1, E2EndClock1);
			 BoolExpr gama2 = getControllerExpression(game, result2, E2EndClock2);
			 
			 BoolExpr question1 = z3.MkGt(getConst(E2EndClock2), getConst(E2EndClock1));
			 

			 List<BoolExpr> truth = new ArrayList<BoolExpr>();
			 truth.add(gama1);
			 truth.add(gama2);
			 Status s1 = check(truth, question1);

			 BoolExpr question2 = z3.MkGt(getConst(E2EndClock1), getConst(E2EndClock2));
			 Status s2 = check(truth, question2);
			 
//			 System.out.println(E2EndClock1 +" < "+ E2EndClock2 + ":" + s1);
//			 System.out.println(E2EndClock1 +" > "+ E2EndClock2 + ":" + s2);

		 } catch (Z3Exception e) {
			 e.printStackTrace();
			 return;
		 }
		 
	 }
	  
	 
	 private static Status check(List<BoolExpr> truth, BoolExpr question) throws Z3Exception {
		 Solver solver = z3.MkSolver("");
		 for (BoolExpr boolExpr : truth) {
			solver.Assert(boolExpr);
		 }
		 solver.Assert(question);
		 Status status = solver.Check();
		 if(status.equals(Status.SATISFIABLE)){
//			 System.out.println(solver.Model());
		 }
		 return status;
	}


	public static Model getModel(BoolExpr e){
		 Solver solver2;
		try {
			solver2 = z3.MkSolver("A");
			solver2.Assert(e);
			solver2.Check();
			return  solver2.Model();
		} catch (Z3Exception e1) {
//			System.out.println(e+": NO MODEL");
		}
		return null;
	 } 


	 public static <A,S> BoolExpr getControllerExpression(GRGame<S> game, MTS<StrategyState<S, Integer>, A> result, String controller){
		 Stack<Pair<A, StrategyState<S, Integer>>> s  = new Stack<Pair<A, StrategyState<S, Integer>>>();
		 Stack<Pair<A, StrategyState<S, Integer>>> trace  = new Stack<Pair<A, StrategyState<S, Integer>>>();
		 

		 setControllerLabel(controller);
		 
		 StringBuffer sb = new StringBuffer();
		 Set<BoolExpr> z3Ands = new HashSet<BoolExpr>();
		 Set<String> allTimes = new HashSet<String>();
		 Set<Pair<Set<BoolExpr>, Set<BoolExpr>>> repes = new HashSet<Pair<Set<BoolExpr>, Set<BoolExpr>>>(); 
		 //Add successors of initial state
		 for (Pair<A, StrategyState<S, Integer>> pair : result.getTransitions(result.getInitialState(), TransitionType.REQUIRED)) {
			 s.push(pair);
		 }
		 
		 while(!s.isEmpty()){
			 Pair<A, StrategyState<S, Integer>> v = s.pop();
			 if(game.getGoal().getGuarantee(1).contains(v.getSecond().getState())){
				 
				 List<String> lesta = getTraceString(trace);
				 String tracestring = lesta.toString();
				 List<Set<Pair<A, StrategyState<S, Integer>>>> nTTrace = getNTTrace(trace,result);
				 List<Set<String>> nesta = getNTTraceString(nTTrace);
				 String timeExp = getTimeExpression(nTTrace);
				 List<BoolExpr> expression = getTimeExpressionList(nTTrace);
				 addIfNot(timeExp,allTimes);
				 List<BoolExpr> conditions = getTimeConditions(nTTrace, trace);
				 
				 BoolExpr b = z3TransformList(conditions,expression);
				 Model model = getModel(b);
				 if(LOG){
					 sb.append("Trace:" + tracestring + "\n");
					 sb.append("NTTrace:" + nesta + "\n");
					 sb.append("Conditions:" + b + "\n");
					 if(model != null){
						 sb.append("Model:" + model + "\n");
					 }else{
						 sb.append("Model:" + "UNSATISFIABLE" + "\n");
					 }
					 sb.append("--------------------------------------------------------"+ "\n");
				 }
				 Pair<Set<BoolExpr>, Set<BoolExpr>> p = new Pair<Set<BoolExpr>, Set<BoolExpr>>(new HashSet<BoolExpr>(conditions), new HashSet<BoolExpr>(expression));
				 if(!repes.contains(p)){
					 z3Ands.add(z3TransformList(conditions,expression));
					 repes.add(p);
				 }
				 
				 if(!s.isEmpty()){
					 Pair<A, StrategyState<S, Integer>> next = s.lastElement();
					 while(!trace.isEmpty() && !isSuccessor(trace.lastElement(), next,result)){
						 trace.pop();
					 }
				 }
			 }else{
				 trace.add(v);
				 for (Pair<A, StrategyState<S, Integer>> pair : result.getTransitions(v.getSecond(), TransitionType.REQUIRED)) {
					 s.push(pair);
				 }
			 }
		 }
		 
		 String dir = System.getProperty("user.dir");
		 String name = "log_" + (Calendar.getInstance().getTimeInMillis());
		 printToFile(sb.toString(), dir+name+".txt");
		 
		 return getZ3PyStrExp(z3Ands);
	 }
	
	
 	private static BoolExpr getZ3PyStrExp(Collection<BoolExpr> conjunciones) {
		try {
			BoolExpr[] or = new BoolExpr[conjunciones.size()];
			int i =0;
			for (BoolExpr expresion : conjunciones) {
				or[i] = expresion;
				i++;
			}
			
			return z3.MkOr(or);
		} catch (Z3Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	

	private static BoolExpr z3TransformList(List<BoolExpr> conditions, List<BoolExpr> expresions) {
		try {
			BoolExpr[] and = new BoolExpr[conditions.size()+expresions.size()];
			int i =0;
			for (BoolExpr condition : conditions) {
				and[i] = condition;
				i++;
			}
			for (BoolExpr expresion : expresions) {
				and[i] = expresion;
				i++;
			}
			
			return z3.MkAnd(and);
		} catch (Z3Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}


	private static <A,S> List<BoolExpr> getTimeConditions(List<Set<Pair<A, StrategyState<S, Integer>>>> nTTrace, Stack<Pair<A, StrategyState<S, Integer>>> trace){
		 List<BoolExpr> relations = new ArrayList<BoolExpr>();
		 List<BoolExpr> timeRelations = new ArrayList<BoolExpr>();
		 HashMap<String, Pair<Integer,Integer>> positions = new HashMap<String, Pair<Integer,Integer>>();
		 ArrayList<Set<String>> ends = new ArrayList<Set<String>>(nTTrace.size());
		 for (int i=0; i<nTTrace.size(); i++) {
			 ends.add(i, new HashSet<String>());
		 }
		 
		 for (int i = nTTrace.size()-1; i >=0 ; i--) {
			 Set<Pair<A, StrategyState<S, Integer>>> ntState = nTTrace.get(i);
			 
			 //First addTimeZeroExpresions
			 try {
				 Set<Pair<A, StrategyState<S, Integer>>> instantEndings = getInstantEndings(ntState);
				 for (Pair<A, StrategyState<S, Integer>> pair : instantEndings) {
					 String timeLabel = getTimeLabel((String) pair.getFirst());
					 allTimers.add(timeLabel);
					 timeRelations.add(z3.MkEq(getConst(timeLabel),z3.MkReal(0)));
				 }
			 } catch (Z3Exception e) {
				 // TODO Auto-generated catch block
				 e.printStackTrace();
			 }
			 
			 //Generates positions table of not instant
			 Set<Pair<A, StrategyState<S, Integer>>> endings = getEndings(ntState);
			 for (Pair<A, StrategyState<S, Integer>> pair : endings) {
				 String timeLabel = getTimeLabel((String) pair.getFirst());
				 allTimers.add(timeLabel);
				 Integer start = h((String) pair.getFirst(),nTTrace,i);
				 positions.put(timeLabel, new Pair<Integer,Integer>(start,i));
				 ends.get(i).add(timeLabel);
			 }
		 }
		 
		 addTimeRelations(trace, timeRelations, positions);
		 HashMap<String, Pair<Integer,Integer>> morePositions =  getMorePositions(positions, ends);
		 addRelations(nTTrace, relations, morePositions);
		 relations.addAll(timeRelations);
		 
		 return relations;
		 
	 }

	private static <A,S> void addRelations(List<Set<Pair<A, StrategyState<S, Integer>>>> nTTrace,List<BoolExpr> relations, HashMap<String, Pair<Integer, Integer>> positions) {
		for (String labelA : positions.keySet()) {
			for (String labelB : positions.keySet()) {
				if(labelA.compareTo(labelB) < 0 && isNotRedundantComparison(labelA,labelB)){
					BoolExpr rel = getRelation(nTTrace, labelA, labelB, positions.get(labelA), positions.get(labelB));
					if(rel != null)
						relations.add(rel);
				}
			}
		 }
	}

	private static boolean isNotRedundantComparison(String labelA, String labelB) {
		String[] timeExpressionsA = (labelA.trim()).split("\\+");
		String[] timeExpressionsB = (labelB.trim()).split("\\+");
		return isNotOneOfTheOthers(timeExpressionsA,timeExpressionsB);
	}

	private static boolean isNotOneOfTheOthers(String[] timeExpressionsA,String[] timeExpressionsB) {
		boolean result = true;
		int i = 0;
		int j = 0;
		while(result && i < timeExpressionsA.length){
			while(result && j < timeExpressionsB.length){
				//I don't know why... but sometimes are spaces after the first trim
				result = !timeExpressionsA[i].trim().equals(timeExpressionsB[j].trim());
				j++;
			}
			i++;
		}
		return result;
	}


	private static <A,S> void addTimeRelations(Stack<Pair<A, StrategyState<S, Integer>>> trace,List<BoolExpr> timeRelations, HashMap<String, Pair<Integer, Integer>> positions) {
		try {
			
			for (String labelA : positions.keySet()) {
				Pair<Integer,Integer> position = positions.get(labelA);
				if(position.getFirst() == null || position.getSecond() == null){
//					System.out.println(labelA);
				}
				int start = position.getFirst();
				int end = position.getSecond();
				if(end - start > 1 || (end - start == 1 && startAnActionBefore(labelA,trace))){
					timeRelations.add(z3.MkGt(getConst(labelA),z3.MkReal(0)));
				}else{
					timeRelations.add(z3.MkGe(getConst(labelA),z3.MkReal(0)));
				}
			}
		} catch (Z3Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	 
	 private static HashMap<String, Pair<Integer, Integer>> getMorePositions(HashMap<String, Pair<Integer, Integer>> positions, ArrayList<Set<String>> ends) {
		 HashMap<String, Pair<Integer, Integer>> result = new HashMap<String, Pair<Integer,Integer>>();
		 boolean[] visited = new boolean[ends.size()];
		 ArrayList<HashMap<String, Pair<Integer, Integer>>>  expresions = new ArrayList<HashMap<String, Pair<Integer,Integer>>>(ends.size());
		 for (int i = 0; i < ends.size(); i++) {
			expresions.add(i,new HashMap<String, Pair<Integer,Integer>>());
		 }
		 
		 for (int i = ends.size()-1; i >=0; i--) {
			 result.putAll(calculatePositionExp(i, visited, positions, ends, expresions));
		 }
		 
		 result.putAll(positions);
		 
		 return result;
	}


	private static synchronized HashMap<String, Pair<Integer, Integer>>  calculatePositionExp(int i, boolean[] visited, HashMap<String, Pair<Integer, Integer>> positions, ArrayList<Set<String>> ends, ArrayList<HashMap<String, Pair<Integer, Integer>>> expresions) {
		HashMap<String, Pair<Integer, Integer>> result = new HashMap<String, Pair<Integer,Integer>>();
		if(visited[i]){
			return expresions.get(i);
		}
		else if(i!=0){
			for (String label : ends.get(i)) {
				int start = positions.get(label).getFirst();
				HashMap<String, Pair<Integer,Integer>>  subRes = calculatePositionExp(start, visited, positions, ends, expresions);
				result.put(label, positions.get(label));
				if(!subRes.isEmpty()){
					for (String string : subRes.keySet()) {
						result.put(string + " + " + label, new Pair<Integer,Integer>(subRes.get(string).getFirst(), positions.get(label).getSecond()));
					}
				}
			}
			expresions.add(i, result);
			visited[i] = true;
		}
		return result;
	}


	private static <A,S> boolean startAnActionBefore(String timeLabel ,Stack<Pair<A, StrategyState<S, Integer>>> trace){
		 boolean isAnStartBefore = false;
		 for (Pair<A, StrategyState<S, Integer>> pair : trace) {
			String action = (String) pair.getFirst();
			String timeLabelAction = getTimeLabel(action);
			if(isEnding(action) &&  timeLabelAction.equals(timeLabel))
				return isAnStartBefore;
			isAnStartBefore = isStarting(action) && !timeLabelAction.equals(timeLabel);
		}
		 return false;
	 }
	 
	 
	 private static <A,S> BoolExpr getRelation(List<Set<Pair<A, StrategyState<S, Integer>>>> nTTrace, String labelA, String labelB, Pair<Integer,Integer> positionsA, Pair<Integer,Integer> positionsB){
		 Integer startA = positionsA.getFirst();
		 Integer startB = positionsB.getFirst();
		 Integer endA = positionsA.getSecond();
		 Integer endB = positionsB.getSecond();
		 BoolExpr res = compareOneDirection(nTTrace, startA, startB, endA, endB, labelA, labelB);
		 if(res != null){
			 return res;
				 
		 }else{
			 res = compareOneDirection(nTTrace, startB, startA, endB, endA, labelB, labelA);
			 if(res != null)
				 return res;
			 else
				 return null;
		 }
	 }

	 private static <A,S> Set<String>getSetActionsFrom(Set<Pair<A, StrategyState<S, Integer>>> nTTraceState){
		 Set<String> res = new HashSet<String>();
		 for (Pair<A, StrategyState<S, Integer>> set : nTTraceState) {
			 String label = (String) set.getFirst();
			 if(isEnding(label)){
				 res.add(getEndFromTimeLabel(getTimeLabel(label)));
			 }else if(isStarting(label)){
				 res.add(getStartFromTimeLabel(getTimeLabel(label)));
			 }
		 }
		 return res;
	 }

	private static <A,S> BoolExpr compareOneDirection(List<Set<Pair<A, StrategyState<S, Integer>>>> nTTrace,
			Integer startA, Integer startB, Integer endA, Integer endB,String labelA, String labelB) {

		Integer originalEndA = endA;
		Integer originalEndB = endB;
		Integer originalStartA = startA;
		Integer originalStartB = startB;
		if(labelA.contains("+") || labelB.contains("+")){
			Set<String> removableLabels = new HashSet<String>();
			Set<String> borders = new HashSet<String>();
			borders.addAll(getBordersFrom(labelA));
			borders.addAll(getBordersFrom(labelB));
			removableLabels.addAll(allTransitionsFrom(labelA, borders));
			removableLabels.addAll(allTransitionsFrom(labelB, borders));
			
			
			for(int i = 0; i< nTTrace.size(); i++){
				Set<String> ntActions = getSetActionsFrom(nTTrace.get(i));
				if(removableLabels.containsAll(ntActions)){
					if(i < originalEndA){
						endA--;
					}
					if(i < originalEndB){
						endB--;
					}
					if(i < originalStartA){
						startA--;
					}
					if(i < originalStartB){
						startB--;
					}
				} 
			}

		}
		
		try {
			if(startA.equals(startB) && endA.equals(endB)){
				return z3.MkEq(getConst(labelB),getConst(labelA));
				//R2,R6?
			}else if((startA.equals(startB) && endInNextAlone(endA, endB,originalEndB,nTTrace, labelA, labelB))
					|| (endA.equals(endB) && startA < startB && hasRealEnds(nTTrace,originalStartB))){
				return z3.MkLe(getConst(labelB),getConst(labelA));
//			 return "<=";
			}else if((startA.equals(startB) && endA-endB >= 1) || (startA< startB && endB < endA) || (endA.equals(endB) && startA < startB)){
				return z3.MkLt(getConst(labelB),getConst(labelA));
//			 return "<";
			}
		} catch (Z3Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	private static Set<String> getBordersFrom(String labelA) {
		Set<String> res = new HashSet<String>();
		String[] timeExpressions= labelA.trim().split("\\+");
		res.add(getStartFromTimeLabel(timeExpressions[0]));
		res.add(getStartFromTimeLabel(timeExpressions[timeExpressions.length-1]));
		return res;
	}

	private static Set<String> allTransitionsFrom(String labelA, Set<String> borders) {
		Set<String> result = new HashSet<String>();
		String[] timeExpressions= labelA.trim().split("\\+");
		for(int i=0; i < timeExpressions.length; i++){
			String start = getStartFromTimeLabel(timeExpressions[i]);
			String end = getEndFromTimeLabel(timeExpressions[i]);
			if(!borders.contains(end))
				result.add(end);
			if(!borders.contains(start))
				result.add(start);
		}
		return result;
	}


	private static <A,S>boolean hasRealEnds(List<Set<Pair<A, StrategyState<S, Integer>>>> nTTrace, Integer startB) {
		return getEndings(nTTrace.get(startB)).size() > 0;
	}

	private static <A,S> boolean endInNextAlone(Integer endA, Integer endB,Integer realEndB, List<Set<Pair<A, StrategyState<S, Integer>>>> nTTrace, String labelA, String labelB) {
		return endB.equals(endA-1) && nTTrace.get(realEndB).size() == 1;
	}

	private static void addIfNot(String string, Set<String> allTimes) {
		boolean exists = false;
		for (String string2 : allTimes) {
			if(isTheSame(string,string2)){
				exists = true;
				break;
			}
		}
		if(!exists)
			allTimes.add(string);
	}

	private static boolean isTheSame(String string, String string2) { 
		Map<String,Integer> timeExp1 = getMultiSet(string2);
		Map<String,Integer> timeExp2 = getMultiSet(string);
		
		return timeExp1.equals(timeExp2);
	}

	private static Map<String, Integer> getMultiSet(String string2) {
		String[] timeExpressions2 = string2.trim().split("\\+");
		Map<String,Integer> timeExpM = new HashMap<String,Integer>();
		for (String string3 : timeExpressions2) {
			if(timeExpM.containsKey(string3))
				timeExpM.put(string3, timeExpM.get(string3)+1);
			else
				timeExpM.put(string3,+1);
		}
		return timeExpM;
	}

	private static <A,S> String getTimeExpression(List<Set<Pair<A, StrategyState<S, Integer>>>> ntTrace) {
		return 	 g(ntTrace,ntTrace.size()-1);
	}
	
	private static <A,S> List<BoolExpr> getTimeExpressionList(List<Set<Pair<A, StrategyState<S, Integer>>>> ntTrace) {
		List<BoolExpr> result = new ArrayList<BoolExpr>();
		try {
		for (RealExpr phi : g2(ntTrace,ntTrace.size()-1)) {
				result.add(z3.MkEq(getConst(getControllerLabel()) , phi));
		}
		} catch (Z3Exception e) {
			e.printStackTrace();
		}
		return 	result;
	}
	
	
	private static RealExpr z3Add(RealExpr a, RealExpr b){
		try {
			ArithExpr[] add = new ArithExpr[2];
			add[0] = a;
			add[1] = b;
			return (RealExpr) z3.MkAdd(add);
		} catch (Z3Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	private static <A,S> List<RealExpr> g2(List<Set<Pair<A, StrategyState<S, Integer>>>> ntTrace, int i) {
		Set<Pair<A, StrategyState<S, Integer>>> endings = getEndings(ntTrace.get(i));
		List<RealExpr> result = new ArrayList<RealExpr>();
		if(ntTrace.indexOf(ntTrace.get(i)) == 0){
			return new ArrayList<RealExpr>();
		}else if(endings.size()>=1){
			for (Pair<A, StrategyState<S, Integer>> pair : endings) {
				String action = (String) pair.getFirst();
				List<RealExpr> rest = g2(ntTrace,h(action,ntTrace,i));
				if(!rest.isEmpty()){
					for (RealExpr psi : rest) {
						result.add( z3Add(psi,getConst(getTimeLabel(action))));
					}
				}else{
					result.add(getConst(getTimeLabel(action)));
				}
			}
			return result;
		}else{
			return g2(ntTrace,i-1);
		}
	}
	
	private static <A,S> String g(List<Set<Pair<A, StrategyState<S, Integer>>>> ntTrace, int i) {
		Set<Pair<A, StrategyState<S, Integer>>> endings = getEndings(ntTrace.get(i));
		if(ntTrace.indexOf(ntTrace.get(i)) == 0){
			return "0 ";
		}else if(endings.size()==1){
			for (Pair<A, StrategyState<S, Integer>> pair : endings) {
				String action = (String)pair.getFirst();
				return g(ntTrace,h(action,ntTrace,i)) +" + "+ getTimeLabel(action) + " ";
			}
		}else if(endings.size()>=1){
			StringBuffer sb = new StringBuffer();
			sb.append("( ");
			for (Pair<A, StrategyState<S, Integer>> pair : endings) {
				String action = (String) pair.getFirst();
				sb.append(g(ntTrace,h(action,ntTrace,i)) +" + "+ getTimeLabel(action)+ " ");
				sb.append("|");
			}
			sb.replace(sb.length()-1, sb.length(), ")");
			return sb.toString();
		}else{
			return g(ntTrace,i-1);
		}
		return "";
	}
	

	private static <A,S> Integer h(String action,List<Set<Pair<A, StrategyState<S, Integer>>>> ntTrace,int i2) {
		String start = getStartTransition(action);
		for (int i = i2-1; i >=0 ; i--) {
			if(isTransition(start, ntTrace.get(i))){
				return i;
			}
		}
		return null;
	}


	private static <A,S> Set<Pair<A, StrategyState<S, Integer>>> getEndings(Set<Pair<A, StrategyState<S, Integer>>> actual) {
		Set<Pair<A, StrategyState<S, Integer>>> result = new HashSet<Pair<A, StrategyState<S, Integer>>>();
		for (Pair<A, StrategyState<S, Integer>> pair : actual) {
			String action = (String) pair.getFirst();
			if((isEnding(action) && !isTransition(getStartTransition(action), actual))){
				result.add(pair);
			}
		}
		return result;
	}
	
	
	private static <A,S> Set<Pair<A, StrategyState<S, Integer>>> getInstantEndings(Set<Pair<A, StrategyState<S, Integer>>> actual) {
		Set<Pair<A, StrategyState<S, Integer>>> result = new HashSet<Pair<A, StrategyState<S, Integer>>>();
		for (Pair<A, StrategyState<S, Integer>> pair : actual) {
			String action = (String) pair.getFirst();
			if((isEnding(action) && isTransition(getStartTransition(action), actual))){
				result.add(pair);
			}
		}
		return result;
	}

	private static boolean isEnding(String action) {
		String actionNoId = removeInicialIds(action);
		return actionNoId.startsWith("e");
	}
	
	private static boolean isStarting(String action) {
		String actionNoId = removeInicialIds(action);
		return actionNoId.startsWith("s");
	}

	private static String getStartTransition(String action) {
		return action.replaceFirst("e", "s");
	}
	
	private static String getStartFromTimeLabel(String action) {
		return action.replaceFirst("T", "s");
	}
	
	private static String getEndFromTimeLabel(String action) {
		return action.replaceFirst("T", "e");
	}

	private static String getTimeLabel(String action){
		if(isEnding(action))
			return "T" + action.replaceFirst("e", "").replace(".", "");
		else 
			return "T" + action.replaceFirst("s", "").replace(".", "");
	}

	private static <A,S> boolean isTransition(String action,Set<Pair<A, StrategyState<S, Integer>>> actual) {
		for (Pair<A, StrategyState<S, Integer>> pair : actual) {
			if(pair.getFirst().equals(action))
				return true;
		}
		return false;
	}

	private static <A,S> List<Set<Pair<A, StrategyState<S, Integer>>>> getNTTrace(Stack<Pair<A, StrategyState<S, Integer>>> trace, MTS<StrategyState<S, Integer>, A> result){
		 List<Set<Pair<A, StrategyState<S, Integer>>>> tms  = new LinkedList<Set<Pair<A, StrategyState<S, Integer>>>>();
		 Set<Pair<A, StrategyState<S, Integer>>> seq = new HashSet<Pair<A, StrategyState<S, Integer>>>();
		 for (Pair<A, StrategyState<S, Integer>> a : trace) {
			 seq.add(a);
			 if(isNotTransient(a,result)){
				 tms.add(seq);
				 seq = new HashSet<Pair<A, StrategyState<S, Integer>>>();
			 }
		 }
		 if(!seq.isEmpty())
			 tms.add(seq);
		 return tms;
	 }

	 private static <A,S> boolean isNotTransient(Pair<A, StrategyState<S, Integer>> a, MTS<StrategyState<S, Integer>, A> result){
		 	 for (Pair<A, StrategyState<S, Integer>> pair : result.getTransitions(a.getSecond(), TransitionType.REQUIRED)) {
		 		String action = ((String) pair.getFirst());
				 if(!isEnding(action))
					 return false;
			 }
		 return true;
	 }

	private static String removeInicialIds(String string) {
		return string.replaceFirst("\\d+.", "");
	}

	private static <A,S> boolean isSuccessor(Pair<A, StrategyState<S, Integer>> lastElement,
			Pair<A, StrategyState<S, Integer>> next,
			MTS<StrategyState<S, Integer>, A> result) {
		 for (Pair<A, StrategyState<S, Integer>> pair : result.getTransitions(lastElement.getSecond(), TransitionType.REQUIRED)) {
			 if(pair.equals(next))
				 return true;
		 }
		return false;
	}


	public static String getControllerLabel() {
		return controllerLabel;
	}


	public static void setControllerLabel(String controllerLabel) {
		RuleBasedCalculator.controllerLabel = controllerLabel;
	}
	
	
	
	static String getTimeFromTimer(String timer){
		return timer.replaceFirst("t","T");
	}

	
	private static void printToFile(String content, String name) {
		try {
 
			File file = new File(name);
 
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
	
	
}