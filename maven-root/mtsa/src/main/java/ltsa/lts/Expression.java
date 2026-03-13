package ltsa.lts;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.math.MathContext;
import java.util.Hashtable;
import java.util.Stack;

import ltsa.lts.util.LTSUtils;

/* -----------------------------------------------------------------------*/

public class Expression {
	// TODO EPAVESE get rid of the precision constant and/or make it configurable
	private static MathContext mathCtx= new MathContext(20, RoundingMode.HALF_EVEN);
    static Hashtable constants;

    private static String labelVar(Stack s, Hashtable locals, Hashtable globals) {
        if (s==null) return null;
        if (s.empty()) return null;
        Symbol token = (Symbol)s.peek();
        if (token.kind==Symbol.IDENTIFIER) {
            if (locals!=null) {
                Value vr = (Value)locals.get(token.toString());
                if (vr!=null && vr.isLabel()) {s.pop(); return vr.toString();}
            }
        } else if (token.kind==Symbol.UPPERIDENT) {
            Value vr=null;
            if (globals!=null) vr = (Value)globals.get(token.toString());
            if (vr==null) vr =(Value)constants.get(token.toString());
            if (vr!=null && vr.isLabel()) {s.pop(); return vr.toString();}
        } else if (token.kind==Symbol.LABELCONST) { // this is a label constant
            ActionLabels el = (ActionLabels)token.getAny();
            if (el.hasMultipleValues())
                Diagnostics.fatal ("label constants cannot be sets", token);
            el.initContext(locals,globals);
            s.pop();
            return el.nextName();
        } else if (token.kind == Symbol.AT) { //this is a set index selection
            return indexSet(s,locals,globals);
        }
        return null;
    }
    
    protected static int countSet(Symbol token, Hashtable locals, Hashtable globals) {
    		if (token.kind!=Symbol.LABELCONST) 
    				Diagnostics.fatal ("label set expected", token);
        ActionLabels el = (ActionLabels)token.getAny();
        el.initContext(locals,globals);
        int count = 0;
        while (el.hasMoreNames()) {
        	  ++count;
        	  el.nextName();
        }
        el.clearContext();
        return count;
    }   
    
    protected static String indexSet(Stack s, Hashtable locals, Hashtable globals) {
        s.pop();
        int index = eval(s,locals,globals).intValue();
        Symbol token = (Symbol)s.pop(); 
          if (token.kind!=Symbol.LABELCONST) 
            Diagnostics.fatal ("label set expected", token);
        ActionLabels el = (ActionLabels)token.getAny();
        el.initContext(locals,globals);
        int count = 0;
        String label = null;
        while (el.hasMoreNames()) {
            label = el.nextName();
            if (count == index) break;
            ++count;
        }
        el.clearContext();
        if (count!=index) 
            Diagnostics.fatal ("label set index expression out of range", token);
        return label;
    }   
    	     


    public static BigDecimal evaluate(Stack s, Hashtable locals, Hashtable globals) {
        Stack mine = (Stack)s.clone();
        return eval(mine,locals,globals);
    }

    public static Value getValue(Stack s, Hashtable locals, Hashtable globals) {
        Stack mine = (Stack)s.clone();
        return getVal(mine,locals,globals);
    }
        
    private static Value getVal(Stack s, Hashtable locals, Hashtable globals){
        String str = labelVar(s,locals,globals);
        if (str!=null) return new Value(str);
        return new Value(eval(s,locals,globals));
    }

    private static BigDecimal eval(Stack s, Hashtable locals,Hashtable globals) {
        Symbol token = (Symbol)s.pop();
        switch(token.kind) {
          case Symbol.INT_VALUE:
          case Symbol.DOUBLE_VALUE:
        	  return token.doubleValue();
          case Symbol.IDENTIFIER:
                if (locals==null)
                    Diagnostics.fatal ("no variables defined", token);
                Value variable = (Value)locals.get(token.toString());
                if (variable==null)
                    Diagnostics.fatal ("variable not defined- "+token, token);
                if (variable.isLabel())
                    Diagnostics.fatal ("not integer variable- "+token, token);
                return variable.doubleValue();
          case Symbol.UPPERIDENT:
                Value constant = null;
                if (globals!=null)
                    constant =(Value)globals.get(token.toString());
                if (constant==null)
                    constant =(Value)constants.get(token.toString());
                if (constant==null)
                    Diagnostics.fatal ("constant or parameter not defined- "+token, token);
                if (constant.isLabel())
                    Diagnostics.fatal ("not integer constant or parameter- "+token, token);
                return constant.doubleValue();
          case Symbol.HASH:
          	    return new BigDecimal(countSet((Symbol)s.pop(),locals,globals));
          case Symbol.PLUS:
          case Symbol.MINUS:
          case Symbol.STAR:
          case Symbol.DIVIDE:
          case Symbol.BACKSLASH:
          case Symbol.MODULUS:
          case Symbol.CIRCUMFLEX:
          case Symbol.BITWISE_AND:
          case Symbol.BITWISE_OR:
          case Symbol.SHIFT_LEFT:
          case Symbol.SHIFT_RIGHT:
          case Symbol.LESS_THAN :
          case Symbol.LESS_THAN_EQUAL:
          case Symbol.GREATER_THAN:
          case Symbol.GREATER_THAN_EQUAL:
          case Symbol.EQUALS:
          case Symbol.NOT_EQUAL:
          case Symbol.AND:
          case Symbol.OR:
          case Symbol.POWER:
	           Value right = getVal(s,locals,globals);
	           Value left  = getVal(s,locals,globals);
	           if (right.isNumeric() && left.isNumeric()) {
	               return exec_op(token.kind,left.doubleValue(),right.doubleValue());
	           } else if (token.kind == Symbol.EQUALS || token.kind == Symbol.NOT_EQUAL) {
	                if (token.kind == Symbol.EQUALS)
	                	return left.toString().equals(right.toString()) ? BigDecimal.ONE : BigDecimal.ZERO;
	                else
	                	return left.toString().equals(right.toString()) ? BigDecimal.ZERO : BigDecimal.ONE;
	           } else
	               Diagnostics.fatal ("invalid expression", token);
          case Symbol.QUESTION:
        	   Value elseBranch = getVal(s, locals, globals);
        	   Value thenBranch = getVal(s, locals, globals);
        	   Value condition  = getVal(s, locals, globals);
        	   return (condition.doubleValue().compareTo(BigDecimal.ZERO) != 0 ? thenBranch : elseBranch).doubleValue();
          case Symbol.UNARY_PLUS:
                return eval(s,locals,globals);
          case Symbol.UNARY_MINUS:
                return eval(s,locals,globals).negate();
          case Symbol.PLING:
                return eval(s,locals,globals).compareTo(BigDecimal.ZERO) == 1 ? BigDecimal.ZERO : BigDecimal.ONE;
          case Symbol.SINE:
        	    return new BigDecimal(~eval(s,locals,globals).intValue());
          default:
                Diagnostics.fatal ("invalid expression", token);
        }
        return BigDecimal.ZERO;
    }

    private static BigDecimal exec_op(int op, BigDecimal left, BigDecimal right) {
    	// check for integer-needing operations
    	if (op == Symbol.CIRCUMFLEX || op == Symbol.BITWISE_AND || op == Symbol.BITWISE_OR ||
    		op == Symbol.SHIFT_LEFT || op == Symbol.SHIFT_RIGHT || op == Symbol.AND || op == Symbol.OR) {
    		if (!LTSUtils.isInteger(left) || !LTSUtils.isInteger(right)) {
    			Diagnostics.fatal("ERROR: Operation " + op + " needs integer operands.");
    		}
    	}

        switch(op) {
          case Symbol.PLUS:             	return left.add(right);
          case Symbol.MINUS:            	return left.subtract(right);
          case Symbol.STAR:             	return left.multiply(right);
          case Symbol.POWER:
        	  // exponent cannot be noninteger
        	  if (!LTSUtils.isInteger(right)) {
        		  Diagnostics.fatal("Exponent must be an integer value");
        	  }

        	  // BigDecimal does not allow negative exponents (?). Workaround
        	  if (right.intValue() >= 0)
        		  return left.pow(right.intValue());
        	  else {
        		  BigDecimal inverse= BigDecimal.ONE.divide(left, mathCtx);
        		  return inverse.pow(-right.intValue());
        	  }

          case Symbol.BACKSLASH:            return left.divideToIntegralValue(right, mathCtx);
          case Symbol.DIVIDE:           	return left.divide(right, mathCtx);
          case Symbol.MODULUS:          	return left.remainder(right, mathCtx);
          case Symbol.CIRCUMFLEX:			return new BigDecimal(left.intValue() ^ right.intValue());
          case Symbol.BITWISE_AND:      	return new BigDecimal(left.intValue() & right.intValue());
          case Symbol.BITWISE_OR:       	return new BigDecimal(left.intValue() | right.intValue());
          case Symbol.SHIFT_RIGHT:      	return new BigDecimal(left.intValue() >> right.intValue());
          case Symbol.SHIFT_LEFT:       	return new BigDecimal(left.intValue() << right.intValue());
          case Symbol.LESS_THAN :       	return left.compareTo(right) == -1 ? BigDecimal.ONE : BigDecimal.ZERO;
          case Symbol.LESS_THAN_EQUAL: 		return left.compareTo(right) != 1 ? BigDecimal.ONE : BigDecimal.ZERO;
          case Symbol.GREATER_THAN:     	return left.compareTo(right) == 1 ? BigDecimal.ONE : BigDecimal.ZERO;
          case Symbol.GREATER_THAN_EQUAL:	return left.compareTo(right) != -1 ? BigDecimal.ONE : BigDecimal.ZERO;
          case Symbol.EQUALS:           	return left.compareTo(right) == 0 ? BigDecimal.ONE : BigDecimal.ZERO;
          case Symbol.NOT_EQUAL:        	return left.compareTo(right) != 0 ? BigDecimal.ONE : BigDecimal.ZERO;
          case Symbol.AND:              	return left.compareTo(BigDecimal.ZERO) != 0 && right.compareTo(BigDecimal.ZERO) !=0 ? BigDecimal.ONE : BigDecimal.ZERO;
          case Symbol.OR:               	return left.compareTo(BigDecimal.ZERO) != 0 || right.compareTo(BigDecimal.ZERO) !=0 ? BigDecimal.ONE : BigDecimal.ZERO;
        }
        return BigDecimal.ZERO;
    }

}
