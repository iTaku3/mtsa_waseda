package ltsa.lts.ltl;
import java.util.BitSet;

import ltsa.lts.Symbol;

/*
* abstract syntax tree for LTL formlae
*/

abstract public class Formula implements Comparable {
	private int id = -1;

	public void setId(int i) {
		id = i;
	}

	public int getId() {
		return id;
	}

	private int untilsIndex = -1;
	private BitSet rightOfWhichUntil;
	private boolean _visited = false;

	boolean visited() {
		return _visited;
	}

	void setVisited() {
		_visited = true;
	}

	int getUI() {
		return untilsIndex;
	}

	void setUI(int i) {
		untilsIndex = i;
	}

	void setRofUI(int i) {
		if (rightOfWhichUntil == null)
			rightOfWhichUntil = new BitSet();
		rightOfWhichUntil.set(i);
	}

	BitSet getRofWU() {
		return rightOfWhichUntil;
	}

	boolean isRightOfUntil() {
		return rightOfWhichUntil != null;
	}

	public int compareTo(Object obj) {
		return id - ((Formula) obj).id;
	}

	public abstract Formula accept(FormulaVisitor v);

	boolean isLiteral() {
		return false;
	}

	Formula getSub1() {
		return accept(Sub1.get());
	}

	Formula getSub2() {
		return accept(Sub2.get());
	}
}

/*
*  get left sub formula or right for R
*/

class Sub1 implements FormulaVisitor {
	private static Sub1 inst;
	private Sub1(){}
	public static Sub1 get(){ 
		 if (inst==null) inst = new Sub1();
		 return inst;
	}
	public Formula visit(True t) {return null;}
	public Formula visit(False f){return null;}
	public Formula visit(Proposition p){return null;}
	public Formula visit(Not n){return n.getNext();}
	public Formula visit(Next n){return n.getNext();}
	public Formula visit(And a){return a.getLeft();}
	public Formula visit(Or o){return o.getLeft();}
	public Formula visit(Until u){return u.getLeft();}
	public Formula visit(Release r){return r.getRight();}
}

/*
*  get right sub formula or left for R
*/

class Sub2 implements FormulaVisitor {
	private static Sub2 inst;
	private Sub2(){}
	public static Sub2 get(){ 
		 if (inst==null) inst = new Sub2();
		 return inst;
	}
	public Formula visit(True t) {return null;}
	public Formula visit(False f){return null;}
	public Formula visit(Proposition p){return null;}
	public Formula visit(Not n){return null;}
	public Formula visit(Next n){return null;}
	public Formula visit(And a){return a.getRight();}
	public Formula visit(Or o){return o.getRight();}
	public Formula visit(Until u){return u.getRight();}
	public Formula visit(Release r){return r.getLeft();}
}


/*
* represent constant True
*/	

class True extends Formula {
	 private static True t;
	 private True(){}
	 
	 public static True make() {
	 	if (t==null) {t = new True(); t.setId(1);}
	 	return t;
	 }
	 public String toString() { return "true";}
	 public Formula accept(FormulaVisitor v) {return v.visit(this);}
	 boolean isLiteral() {return true;}
}

/*
* represent constant False
*/	

class False extends Formula {
	private static False f;
	 private False(){}
	 
	 public static False make() {
	 	if (f==null) {f = new False(); f.setId(0);}
	 	return f;
	 }
	 public String toString() { return "false";}
	 public Formula accept(FormulaVisitor v) {return v.visit(this);}
	 boolean isLiteral() {return true;}
}

/*
* represent proposition
*/	

class Proposition extends Formula {
	 Symbol sym;
	 Proposition(Symbol s) {sym = s;}
	 public String toString() { return sym.toString();}
	 public Formula accept(FormulaVisitor v) {return v.visit(this);}
	 boolean isLiteral() {return true;}
}

/*
* represent not !
*/	

class Not extends Formula {
	 Formula next;
	 Formula getNext() {return next;}
	 Not(Formula f) {next = f;}
	 public String toString() { return "!"+next.toString();}
	 public Formula accept(FormulaVisitor v) {return v.visit(this);}
	 boolean isLiteral() {return next.isLiteral();}
}

/*
* represent next  X
*/	

class Next extends Formula {
	 Formula next;
	 Formula getNext() {return next;}
	 Next(Formula f) {next = f;}
	 public String toString() { return "X "+next.toString();}
	 public Formula accept(FormulaVisitor v) {return v.visit(this);}
}


/*
* represent or \/ |
*/	

class Or extends Formula {
	 Formula left,right;
	 Formula getLeft() {return left;}
	 Formula getRight() {return right;}
	 Or(Formula l, Formula r) {left=l; right=r;}
	 public String toString() { 	return "("+left.toString()+" | "+right.toString()+")";}
	 public Formula accept(FormulaVisitor v) {return v.visit(this);}
}

/*
* represent and /\ &
*/	

class And extends Formula {
	 Formula left,right;
	 Formula getLeft() {return left;}
	 Formula getRight() {return right;}
	 And(Formula l, Formula r) {left=l; right=r;}
	 public String toString() { 	return "("+left.toString()+" & "+right.toString()+")";}
	 public Formula accept(FormulaVisitor v) {return v.visit(this);}
}

/*
* represent until U
*/	

class Until extends Formula {
	 Formula left,right;
	 Formula getLeft() {return left;}
	 Formula getRight() {return right;}
	 Until(Formula l, Formula r) {left=l; right=r;}
	 public String toString() { 	return "("+left.toString()+" U "+right.toString()+")";}
	 public Formula accept(FormulaVisitor v) {return v.visit(this);}
}

/*
* represent release R
*/	

class Release extends Formula {
	 Formula left,right;
	 Formula getLeft() {return left;}
	 Formula getRight() {return right;}
	 Release(Formula l, Formula r) {left=l; right=r;}
	 public String toString() { 	return "("+left.toString()+" R "+right.toString()+")";}
	 public Formula accept(FormulaVisitor v) {return v.visit(this);}
}