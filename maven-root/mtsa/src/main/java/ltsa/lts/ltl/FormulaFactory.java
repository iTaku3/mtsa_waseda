package ltsa.lts.ltl;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;
import java.util.Vector;

import ltsa.lts.ActionLabels;
import ltsa.lts.Alphabet;
import ltsa.lts.Diagnostics;
import ltsa.lts.Expression;
import ltsa.lts.Symbol;

/*
* factory for LTL formlae
*/

public class FormulaFactory {
	
	NotVisitor nv;
	int id;
	Map subf;   //stores subformula to ensure uniqueness
	SortedSet props; //stores the set of propositions
	Formula formula;	
	Hashtable actionPredicates;
	private boolean hasNext = false;
	static public boolean normalLTL = true;
	
	public FormulaFactory() {
		 nv = new NotVisitor(this);
		 subf = new HashMap();
		 props = new TreeSet();
		 id = 1;
		 actionPredicates = null;
	}
	
	boolean nextInFormula()  { return hasNext;}
	
	public void setFormula(Formula f) {   // generate the negation for verification
		formula = makeNot(f);
	}
	
	public Formula getFormula() {
		return formula;
	}
	
	public Formula make(Symbol sym) {
		  return unique(new Proposition(sym));
	}
	
	public Formula make(Symbol sym, ActionLabels range, Hashtable locals, Hashtable globals)  {
         range.initContext(locals,globals);
		 Formula f = null;
         while(range.hasMoreNames()) {
         	String s = range.nextName();
			Symbol newSym = new Symbol(sym,sym+"."+s);
			if (f==null) 
				f = make(newSym);
			else
			    f = makeOr(f,make(newSym));	
         }
         range.clearContext();
		 return f;
	}
	
	public Formula make(Stack expr, Hashtable locals, Hashtable globals)  {
		if (Expression.evaluate(expr,locals,globals).intValue() > 0) 
			return True.make();
		else
			return False.make();
	}	
	
	
	public Formula make(ActionLabels act, Hashtable locals, Hashtable globals)  {
		if(actionPredicates==null) actionPredicates = new Hashtable();
		Vector av = act.getActions(locals,globals);
		String name = (new Alphabet(av)).toString();
		if (!actionPredicates.containsKey(name))
			actionPredicates.put(name, av);
		return unique(new Proposition(new Symbol(Symbol.UPPERIDENT,name)));
	}
	
	public Formula makeTick()  {
		if(actionPredicates==null) actionPredicates = new Hashtable();
		Vector av = new Vector(1);
		av.add("tick");
		String name = (new Alphabet(av)).toString();
		if (!actionPredicates.containsKey(name))
			actionPredicates.put(name, av);
		return unique(new Proposition(new Symbol(Symbol.UPPERIDENT,name)));
	}

	
	public SortedSet getProps(){
		  return props;
	}
	
	public Formula make(Formula left, Symbol op, Formula right) {
		switch (op.kind) {
			case Symbol.PLING:       
			     return makeNot(right);
			case Symbol.NEXTTIME:
			     if (normalLTL)    
			        return makeNext(right);
				 else
				    return makeNext(makeWeakUntil(makeNot(makeTick()),makeAnd(makeTick(),right)));	
			case Symbol.EVENTUALLY: 
			      if (normalLTL) 
			         return makeEventually(right);
				  else
				     return makeEventually(makeAnd(makeTick(),right)); 
			case Symbol.ALWAYS: 
			      if (normalLTL)     
			          return makeAlways(right);
				  else
				      return makeAlways(makeImplies(makeTick(),right));  
			case Symbol.AND:         
			      return makeAnd(left,right);
			case Symbol.OR:          
			      return makeOr(left,right);
			case Symbol.ARROW:       
			      return makeImplies(left, right);
			case Symbol.UNTIL: 
			      if (normalLTL)      
			         return makeUntil(left,right);
				  else
				     return makeUntil(makeImplies(makeTick(),left),makeAnd(makeTick(),right)); 
			case Symbol.WEAKUNTIL: 
			      if (normalLTL)      
			         return makeWeakUntil(left,right);
			      else
			         return makeWeakUntil(makeImplies(makeTick(),left),makeAnd(makeTick(),right)); 
			case Symbol.EQUIVALENT:  
			      return makeEquivalent(left,right);
			default:
				Diagnostics.fatal ("Unexpected operator in LTL expression: "+op, op);
		} 
		return null;
	}
	
	Formula makeAnd(Formula left, Formula right) {
		 if (left == right) return left; // P/\P
		 if (left == False.make() || right == False.make()) return False.make(); //P/\false
		 if (left == True.make()) return right;
		 if (right == True.make()) return left;
		 if (left == makeNot(right)) return False.make(); //contradiction
		 if ( (left instanceof Next) && (right instanceof Next)) // X a && X b --> X(a && b)
		 	return makeNext(makeAnd( ((Next)left).getNext(),((Next)right).getNext()));
		 if (left.compareTo(right)<0)
         return unique(new And(left,right));
      else 
         return unique(new And(right,left));
	}
	
	Formula makeOr(Formula left, Formula right) {
	 if (left == right) return left; //P\/P
	 if (left == True.make() || right == True.make()) return True.make(); //P\/true
	 if (left == False.make()) return right;
	 if (right == False.make()) return left;
	 if (left == makeNot(right)) return True.make(); //tautology
  	 if (left.compareTo(right)<0)
       return unique(new Or(left,right));
    else 
       return unique(new Or(right,left));
	}
	
	Formula makeUntil(Formula left, Formula right) {
		 if (right==False.make()) return False.make();  // P U false = false
		 if ( (left instanceof Next) && (right instanceof Next)) // X a U X b --> X(a U b)
		 	return makeNext(makeUntil(((Next)left).getNext(),((Next)right).getNext()));
      return unique(new Until(left,right));
	}
	
	Formula makeWeakUntil(Formula left, Formula right) {
		//return makeOr(makeAlways(left),makeUntil(left,right));
		return makeRelease(right,makeOr(left,right));
	}
	
	Formula makeRelease(Formula left, Formula right) {
      return unique(new Release(left,right));
	}
	
	Formula makeImplies(Formula left, Formula right) {
		  return makeOr(makeNot(left),right);
	}
	
	Formula makeEquivalent(Formula left, Formula right) {
		  return makeAnd(makeImplies(left,right),makeImplies(right,left));
	}
	
	Formula makeEventually(Formula right) {
		  return makeUntil(True.make(),right);
	}
	
	Formula makeAlways(Formula right) {
		  return makeRelease(False.make(),right);
	}
	
	Formula makeNot(Formula right) {
		 return right.accept(nv);
	}
	
	Formula makeNot(Proposition p) {
		 return unique(new Not(p));
	}
	
	Formula makeNext(Formula right) {
		hasNext = true;
		return unique(new Next(right));
	}
	
	int processUntils(Formula f, List untils) { 
		f.accept(new UntilVisitor(this,untils));
		return untils.size();
	}
	
	boolean specialCaseV(Formula f, Set s) {
      Formula ff = makeRelease(False.make(), f);
      return s.contains(ff);
  }
    
  boolean syntaxImplied(Formula f, SortedSet one, SortedSet two) {
  	  if (f==null) return true;
    if (f instanceof True) return true;
    if (one.contains(f))   return true;
    if (f.isLiteral()) return false;
    Formula a = f.getSub1();
    Formula b = f.getSub2();
    Formula c = ((f instanceof Until) || (f instanceof Release)) ? f : null;
    boolean bf = syntaxImplied(b,one,two);
    boolean af = syntaxImplied(a,one,two);
    boolean cf;
    if(c != null) {
       if(two != null)
            cf = two.contains(c);
        else
            cf = false;
    } else
        cf = true;
    if ((f instanceof Until) || (f instanceof Or))
    	  return bf || af && cf;
    if (f instanceof Release) 
    	  return af && bf || af && cf;
    	if (f instanceof And) 
    	  return af && bf; 
    if (f instanceof Next) {
    	  if(a != null){
         if(two != null)
            return two.contains(a);
          else
            return false;
       } else {
         return true;
       }
    }
    return false;
  }
	
  private int newId(){return ++id;}
  
	private Formula unique(Formula f) {
     String s = f.toString();
     if (subf.containsKey(s))
         return (Formula)subf.get(s);
     else {
     	  f.setId(newId());
         subf.put(s, f);
         if (f instanceof Proposition) props.add(f);
         return f;
     }
  } 
  
}

/*
* Not visitor pushes negation inside operators to get negative normal form
*/

class NotVisitor implements FormulaVisitor {
	private FormulaFactory fac;
	NotVisitor(FormulaFactory f){fac = f;}
	
	public Formula visit(True t) 
	  {return False.make();}
	public Formula visit(False f)
	  {return True.make();}
	public Formula visit(Proposition p)
	  {return fac.makeNot(p);}
	public Formula visit(Not n)
	  {return n.getNext();}
	public Formula visit(Next n)
	  {return fac.makeNext(fac.makeNot(n.getNext()));}
	public Formula visit(And a)
	  {return fac.makeOr(fac.makeNot(a.getLeft()), fac.makeNot(a.getRight()));}
	public Formula visit(Or o)
	  {return fac.makeAnd(fac.makeNot(o.getLeft()), fac.makeNot(o.getRight()));}
	public Formula visit(Until u)
	  {return fac.makeRelease(fac.makeNot(u.getLeft()), fac.makeNot(u.getRight()));}
	public Formula visit(Release r)
	  {return fac.makeUntil(fac.makeNot(r.getLeft()), fac.makeNot(r.getRight()));}
}

/*
* Untils visitor computes the untils indexes
*/

class UntilVisitor implements FormulaVisitor {
	private FormulaFactory fac;
	private List ll;
	UntilVisitor(FormulaFactory f, List l){fac = f; ll = l;}
	
	public Formula visit(True t) 
	  {return t;}
	public Formula visit(False f)
	  {return f;}
	public Formula visit(Proposition p)
	  {return p;}
	public Formula visit(Not n) {
		n.getNext().accept(this); 
	  return n;
	}
	public Formula visit(Next n) {
		n.getNext().accept(this); 
	  return n;
	}
	public Formula visit(And a) {
	  a.getLeft().accept(this); 
	  a.getRight().accept(this); 
	  return a;
	}
	public Formula visit(Or o) {
	  o.getLeft().accept(this); 
	  o.getRight().accept(this); 
	  return o;
	}
	public Formula visit(Until u) {
		if (!u.visited()) {
			u.setVisited();
			ll.add(u);
			u.setUI(ll.size()-1);
			u.getRight().setRofUI(ll.size()-1);
		    u.getLeft().accept(this); 
		    u.getRight().accept(this); 
		}
	  return u;
	}
	public Formula visit(Release r) {
	  r.getLeft().accept(this); 
	  r.getRight().accept(this); 
	  return r;
	}
}
