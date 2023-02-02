package MTSSynthesis.controller.gr.time.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.RealExpr;
import com.microsoft.z3.Z3Exception;

import MTSSynthesis.controller.gr.time.DeltaGenerator;

public class Z3Context {
	private Context z3;
	private RealExpr zero;
	private Set<RealExpr> variables;
	private Map<String,RealExpr> definitions;
	private BoolExpr positive;
	private Integer previousState;
	private DeltaGenerator tempNameGenerator;
	
	private Map<String,RealExpr> temporal;
  	
	public Z3Context() {
		try {
			this.z3 = new Context();
			this.variables = new HashSet<RealExpr>();
			this.definitions = new HashMap<String, RealExpr>();
			this.zero = z3.MkReal(0);
			this.tempNameGenerator = new DeltaGenerator();
			this.tempNameGenerator.setSymbol("VW40");
		} catch (Z3Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Could not creat Z3 context.");
		}
	}
	
	public RealExpr addRealConst(String name) throws Z3Exception{
		RealExpr expr;
		if(definitions.containsKey(name)){
			expr  = z3.MkRealConst(name);
			variables.add(expr);
			definitions.put(name, expr);
		}else {
			expr = definitions.get(name);
		}
		return expr;
	}

	public RealExpr getZero() {
		return zero;
	}

	public BoolExpr isLeZero(RealExpr realExpr) throws Z3Exception {
		if(variables.contains(realExpr)){
			return z3.MkLe(realExpr, zero);
		}else{
			throw new RuntimeException("The variable is not defined in this context.");
		}
	}
	
	public BoolExpr isGeZero(RealExpr realExpr) throws Z3Exception {
		if(variables.contains(realExpr)){
			return z3.MkLe(realExpr, zero);
		}else{
			throw new RuntimeException("The variable is not defined in this context.");
		}
	}
	
	public void free(Expr expr) throws Z3Exception{
		expr.Dispose();
	}
	
	public BoolExpr allPositive() throws Z3Exception{
		if(!previousState.equals(variables.hashCode())){
			free(this.positive);
			BoolExpr[] and = new BoolExpr[variables.size()];
			int i = 0;
			for (RealExpr realExpr : variables) {
				and[i++] = isPositive(realExpr);
			}
			this.positive =z3.MkAnd(and); 
		}
		return this.positive;
	}

	private BoolExpr isPositive(RealExpr realExpr) throws Z3Exception {
		return z3.MkLe(zero,realExpr);
	}
	
	public void dispose() throws Z3Exception{
		for (RealExpr realExpr : variables) {
			realExpr.Dispose();
		}
		z3.Dispose();
	}
	
	public RealExpr getTempConst() throws Z3Exception {
		String name = tempNameGenerator.getNextDelta();
		if(!temporal.containsKey(name)){
			temporal.put(name, z3.MkRealConst(name));
		}
		return temporal.get(name);
	}
	
	public void resetTemporal(){
		tempNameGenerator.reset();
	}
	
	
	
	

}
