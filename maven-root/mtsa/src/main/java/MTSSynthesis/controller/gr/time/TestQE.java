package MTSSynthesis.controller.gr.time;

import com.microsoft.z3.ApplyResult;
import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Goal;
import com.microsoft.z3.RealExpr;
import com.microsoft.z3.Tactic;
import com.microsoft.z3.Z3Exception;

public class TestQE {

	
	
	public static void main(String[] args) {
		 /* 
		  * from z3 import Ints, Tactic, Exists, And
		  * x, t1, t2 = Ints('x t1 t2')
		  * t = Tactic('qe')
		  * print t(Exists(x, And(t1 < x, x < t2)))
		  * */
		try {
			
//			Context ctx = new Context();
//			RealExpr x2 = ctx.mkRealConst("t1");
//			RealExpr x3 = ctx.mkRealConst("t2");
//			
//			RealExpr[] lamb = new RealExpr[0];
//			
//			BoolExpr a = ctx.mkGe(x2, lamb[0]);
//			BoolExpr b = ctx.mkGe(lamb[0], x3);
//
//			BoolExpr temp = ctx.mkAnd(a , b);
//
//			Tactic t = ctx.mkTactic("qe");
//			Solver s = ctx.mkSolver();
//			BoolExpr e = ctx.mkExists(lamb, temp, 1, null, null, ctx.mkSymbol("Q2"), null);
//			
//			Goal g = ctx.mkGoal(arg0, arg1, arg2);
			
			
			Context z3 = new Context();
			RealExpr t1 = z3.MkRealConst("t1");
			RealExpr t2 = z3.MkRealConst("t2");
			RealExpr t3 = z3.MkRealConst("t3");
			RealExpr x = z3.MkRealConst("x");
			
			BoolExpr[] expr = new BoolExpr[2];
			expr[0] = z3.MkLt(t1,t3);
			expr[1] = z3.MkLt(t3,t2);

			Expr p = z3.MkAnd( expr);
			
			ArithExpr[] s = new ArithExpr[2];
			s[0] = t3;
			s[1] = x;
			
			p.Substitute(t3,z3.MkAdd(s));
			
			p.toString();
			
			Expr ex = z3.MkExists(new Expr[] { x }, p, 1, null, null, null, null);

			Goal g = z3.MkGoal(true, true, false);
			g.Assert((BoolExpr)ex);
			Tactic tac = z3.MkTactic("qe"); // quantifier elimination
			ApplyResult a = tac.Apply(g); // look at a.Subgoals
			
			
			for (BoolExpr boolExpr : a.Subgoals()[0].Formulas()) {
//				System.out.println(boolExpr);
			}
			
			
		} catch (Z3Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
