package ltsa.lts.ltl;
import ltsa.lts.Diagnostics;
import ltsa.lts.Lex;
import ltsa.lts.Symbol;

public class LTLparser {
	
	private Lex lex;
	private FormulaFactory fac;
	private Symbol current;
	
	public LTLparser(Lex l) {
		lex = l;
		fac = new FormulaFactory();
	}
		
  public FormulaFactory parse() {
  	   current = modify(lex.current());
  	   if (current==null) next_symbol();
  	   fac.setFormula(ltl_unary());
  	   return fac;
  }
  
  private Symbol next_symbol () {
    	return (current = modify(lex.next_symbol()));
  }

  private void push_symbol () {
     lex.push_symbol();
  }

  private void current_is (int kind, String errorMsg) {
     if (current.kind != kind)
        Diagnostics.fatal(errorMsg,current);
  }
  
  // do not want X and U to be keywords outside of LTL expressions
  private Symbol modify(Symbol s) {
  	  if (s.kind!=Symbol.UPPERIDENT) return s;
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
  	  	return s;
  	 }

// _______________________________________________________________________________________
// LINEAR TEMPORAL LOGIC EXPRESSION

private Formula ltl_unary() {   // !,<>,[]
	  Symbol op = current;
	  Formula f;
	  switch (current.kind) {
	   case Symbol.PLING:
	   case Symbol.NEXTTIME:
       case Symbol.EVENTUALLY:
       case Symbol.ALWAYS:
		    next_symbol ();
    		return fac.make(null,op,ltl_unary());
       case Symbol.UPPERIDENT:
    		next_symbol();
			if (!PredicateDefinition.contains(op))
				Diagnostics.fatal("proposition not defined "+op,op);
    	    return  fac.make(op);
    	case Symbol.LROUND:
    	  next_symbol ();
    		Formula right = ltl_or ();
    		current_is (Symbol.RROUND, ") expected to end LTL expression");
    		next_symbol();
    		return right;
    	default:
    		Diagnostics.fatal ("syntax error in LTL expression",current);
    	}
    	return null;
  }
  

// _______________________________________________________________________________________
// LTL_AND

private Formula ltl_and () {	// &
	Formula left = ltl_unary();
	while (current.kind == Symbol.AND) {
     Symbol op = current;
		next_symbol ();
		Formula right = ltl_unary ();
	  left = fac.make(left,op,right);
	}
	return left;
}

// _______________________________________________________________________________________
// LOGICAL_OR

private Formula ltl_or () {	// |
	Formula left = ltl_binary ();
	while (current.kind == Symbol.OR) {
		Symbol op = current;
		next_symbol ();
		Formula right = ltl_binary ();
	  left = fac.make(left,op,right);
	}
	return left;
}

// _______________________________________________________________________________________
// LTS_BINARY

private Formula ltl_binary () {	// until, ->
	Formula left = ltl_and ();
	if (current.kind == Symbol.UNTIL || current.kind == Symbol.ARROW || current.kind == Symbol.EQUIVALENT) {
		Symbol op = current;
		next_symbol ();
		Formula right = ltl_and ();
		left = fac.make(left,op,right);
	}
	return left;
}

}