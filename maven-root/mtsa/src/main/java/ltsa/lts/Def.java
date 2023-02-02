package ltsa.lts;

import java.util.Hashtable;
import java.util.List;
import java.util.Stack;

public class Def {
	
	private static Hashtable<String, Def> defs;
	
	public static void init() {
		defs = new Hashtable<String, Def>();
	}
	
	// Returns true if already defined
	public static boolean put(Def d) {
		return defs.put(d.name, d) != null;
	}
	
	public static Def get(Symbol s) {
		return defs.get(s.getName());
	}
	
	private String name;
	private Hashtable<String, Integer> params;
	private Stack<Symbol> expression;
	
	public Def(String defName) {
		name = defName;
		params = new Hashtable<String, Integer>();
		expression = new Stack<Symbol>();
	}

	public void addParameter(Symbol s) {
		params.put(s.getName(), params.size());
	}
	
	public Stack<Symbol> getExpressionStack() {
		return expression;
	}
	
	public void pushExpressionStack(List<Stack<Symbol>> arguments, Stack<Symbol> stack) {
		for (int i = 0; i < expression.size(); ++i) {
			Symbol s = expression.get(i);
			if (s.kind == Symbol.IDENTIFIER) {
				Integer arg = params.get(s.getName());
				if (arg != null) {
					Stack<Symbol> argumentExpression = arguments.get(arg);
					for (int j = 0; j < argumentExpression.size(); ++j)
						stack.push(argumentExpression.get(j));
					continue;
				}
			}
			stack.push(expression.get(i));
		}
	}
	
	public int getParameterCount() {
		return params.size();
	}
	
}
