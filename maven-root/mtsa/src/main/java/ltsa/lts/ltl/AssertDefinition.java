package ltsa.lts.ltl;
import gov.nasa.ltl.graph.Degeneralize;
import gov.nasa.ltl.graph.Graph;
import gov.nasa.ltl.graph.SCCReduction;
import gov.nasa.ltl.graph.SFSReduction;
import gov.nasa.ltl.graph.Simplify;
import gov.nasa.ltl.graph.SuperSetReduction;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import ltsa.lts.CompactState;
import ltsa.lts.CompositeState;
import ltsa.lts.Diagnostics;
import ltsa.lts.LTSOutput;
import ltsa.lts.LabelSet;
import ltsa.lts.Minimiser;
import ltsa.lts.Symbol;
import ltsa.dispatcher.TransitionSystemDispatcher;

/* -----------------------------------------------------------------------*/

public class AssertDefinition {
    public static final String NOT_DEF = "NOT";
	Symbol name;
    FormulaFactory fac;
	FormulaSyntax ltl_formula;
    CompositeState cached;
	LabelSet alphaExtension;
	Hashtable  init_params;   // initial parameter values name,value
	Vector     params;        // list of parameter names
	private boolean	isConstraint;
	boolean isProperty;

    static Hashtable<String,AssertDefinition> definitions;
	static Hashtable<String,AssertDefinition> constraints;
	public static boolean addAsterisk = true;
    
    private AssertDefinition(Symbol n, FormulaSyntax f, LabelSet ls, Hashtable ip, Vector p, boolean isConstraint, boolean b){
    	  name = n;
    	  ltl_formula = f;
    	  cached = null;
		  alphaExtension = ls;
		  init_params = ip;
		  params = p;
		this.isConstraint = isConstraint;
		isProperty = b;
    }
    
	public static AssertDefinition getDefinition(String definitionName)  {
		return definitions == null ? null : definitions.get(definitionName);
	}
    
	public static AssertDefinition getConstraint(String definitionName)  {
		return constraints == null ? null : constraints.get(definitionName);
	}

    public static void put(Symbol n, FormulaSyntax f, LabelSet ls, Hashtable ip, Vector p, boolean isConstraint, boolean isProperty) {
    	    if(definitions==null) definitions = new Hashtable<String,AssertDefinition>();
			if(constraints==null) constraints = new Hashtable<String,AssertDefinition>();
			if (!isConstraint)  {
    	       if(definitions.put(n.toString(),new AssertDefinition(n,f,ls, ip, p, false, false))!=null) 
                 Diagnostics.fatal ("duplicate LTL property definition: " + n, n);
    	    } else  {
			   if(constraints.put(n.toString(),new AssertDefinition(n,f,ls, ip, p, true, isProperty))!=null) 
				   Diagnostics.fatal("duplicate LTL constraint/property definition: " + n, n);
            } 
    }
	    
    public static void init(){
    	  definitions = null;
		  constraints = null;
    }
    
    public static String[] names() {
		if (definitions==null) return null;
//		int n = definitions.size();
//		String na[];
//		if (n==0) return null; else na = new String[n];
//		Enumeration e = definitions.keys();
//		int i = 0;
//		while (e.hasMoreElements())
//			na[i++] = (String)e.nextElement();
//		
		Enumeration e = definitions.keys();
		List<String> defs = new ArrayList<String>();
		while (e.hasMoreElements()){
			String elem = (String)e.nextElement();
			if (!elem.startsWith(AssertDefinition.NOT_DEF)) {
				defs.add(elem);
			}
		}
		if (defs.size()==0){
			return null;
		}
		String na[] = defs.toArray(new String[0]);
        return na;
    }
	
	public static void compileAll(LTSOutput output)  {
		compileAll(definitions, output);
		compileAll(constraints, output);
	} 
	
	private static void compileAll(Hashtable definitions, LTSOutput output)  {
		if (definitions == null) return;
		Enumeration e = definitions.keys();
        while (e.hasMoreElements())  {
             String name = (String)e.nextElement();
			 AssertDefinition p = (AssertDefinition)definitions.get(name);
			 p.fac = new FormulaFactory();
			 p.fac.setFormula(p.ltl_formula.expand(p.fac,new Hashtable(),p.init_params));
        }
    }
	
    public static CompositeState compile(LTSOutput output, String asserted){
		return compile(definitions,output,asserted, false);
    }
	
	public static void compileConstraints(LTSOutput output, Hashtable compiled)  {
		if (constraints==null) return;
		Enumeration e = constraints.keys();
		while (e.hasMoreElements())  {
		     String name = (String)e.nextElement();
			if (!name.startsWith(AssertDefinition.NOT_DEF)) {
				CompactState cm = compileConstraint(output,name);
				compiled.put(cm.name,cm);
			}
		}
	}
	
	public static CompactState compileConstraint(LTSOutput output, Symbol name, String refname, Vector pvalues)  {
        if (constraints==null) return null;
		AssertDefinition p = (AssertDefinition)constraints.get(name.toString());	
		if (p==null) return null;
		p.cached = null;
		p.fac = new FormulaFactory();
		if (pvalues!=null)  {
			if (pvalues.size()!=p.params.size())
				Diagnostics.fatal ("Actual parameters do not match formals: "+name, name);					
	    		Hashtable actual_params = new Hashtable();
	    		for (int i=0; i<pvalues.size(); ++i) 
					actual_params.put(p.params.elementAt(i),pvalues.elementAt(i));
				p.fac.setFormula(p.ltl_formula.expand(p.fac,new Hashtable(),actual_params));
		} else  {
			p.fac.setFormula(p.ltl_formula.expand(p.fac,new Hashtable(),p.init_params));
		}
		CompositeState cs = compile(constraints, output, name.toString(), true);
		if (cs==null) return null;
		if (!cs.isProperty && !cs.name.startsWith(AssertDefinition.NOT_DEF))  {
			Diagnostics.fatal ("LTL constraint must be safety: "+p.name, p.name);
		}
		if (!p.isProperty){
			cs.composition.unMakeProperty();
		}
		cs.composition.name = refname;
		return cs.composition;
	}

			
	
	public static CompactState compileConstraint(LTSOutput output, String constraint)  {
		CompositeState cs = compile(constraints, output, constraint, true);
		if (cs==null) return null;
		if (!cs.isProperty)  {
			AssertDefinition p = constraints.get(constraint);
			if (!cs.name.startsWith(AssertDefinition.NOT_DEF)){
				Diagnostics.fatal ("LTL constraint must be safety: "+p.name, p.name);
			}
		}
		if (!cs.isProperty) {
			cs.composition.unMakeProperty();
		}
		return cs.composition;
	}
		
	

	private static CompositeState compile(Hashtable definitions, LTSOutput output, String asserted , boolean isconstraint){
		if (definitions==null || asserted == null) return null;
    	AssertDefinition p = (AssertDefinition)definitions.get(asserted);
    	if (p==null) return null;
    	if (p.cached!=null) return p.cached;
        output.outln("Formula !"+p.name.toString()+" = "+p.fac.getFormula());
		Vector alpha = p.alphaExtension!=null ? p.alphaExtension.getActions(null) : null;
	    if (alpha==null) alpha = new Vector();
		if (addAsterisk && !isconstraint) alpha.add("*");
        GeneralizedBuchiAutomata gba = new GeneralizedBuchiAutomata(p.name.toString(),p.fac, alpha);
        gba.translate();
        //gba.printNodes(output);
        Graph g = gba.Gmake();
		output.outln("GBA " + g.getNodeCount() + " states " + g.getEdgeCount() + " transitions");
        g = SuperSetReduction.reduce(g);
		//output.outln("SSR " + g.getNodeCount() + " states " + g.getEdgeCount() + " transitions");
        Graph g1 = Degeneralize.degeneralize(g);
        //output.outln("DEG " + g1.getNodeCount() + " states " + g1.getEdgeCount() + " transitions");
        g1 = SCCReduction.reduce(g1);
        //output.outln("SCC " + g1.getNodeCount() + " states " + g1.getEdgeCount() + " transitions");
        g1 = Simplify.simplify(g1);
		g1 = SFSReduction.reduce(g1);
        //output.outln("SFS " + g1.getNodeCount() + " states " + g1.getEdgeCount() + " transitions");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Converter c = new Converter(p.name.toString(),g1,gba.getLabelFactory());
        output.outln("Buchi automata:");
        c.printFSP(new PrintStream(baos));
        output.out(baos.toString());
        Vector procs = gba.getLabelFactory().propProcs;
        procs.add(c);
        CompositeState cs = new CompositeState(c.name,procs);
        cs.hidden = gba.getLabelFactory().getPrefix();
        
        PredicateDefinition[] fluents = gba.getLabelFactory().getFluents();        
		cs.setFluentTracer(new FluentTrace(fluents));
        cs.compose(output,true);
        cs.composition.removeNonDetTau();
		output.outln("After Tau elimination = "+cs.composition.maxStates+" state");
	    Minimiser e = new Minimiser(cs.composition,output);
        cs.composition = e.minimise();
		if (cs.composition.isSafetyOnly())  {
			cs.composition.makeSafety();
			cs.composition.removeAtActions();
			cs.determinise(output);
			cs.isProperty = true;
		}
		cs.composition.removeDetCycles("*");
        
		if (p.isConstraint &&  !p.isProperty) {
			CompactState constrained = cs.getComposition();
			//DIPI: temporal, hay que ver cuando aplicamos el constrained de MTS.
			if (ltsa.lts.util.MTSUtils.isMTSRepresentation(constrained)) {
				cs.setComposition(TransitionSystemDispatcher.makeMTSConstraintModel(constrained, output));
			}else{
				cs.composition.removeErrors();
			}
		}

		p.cached = cs;
        return cs;
    }

		


	public Vector getParams() { return params; }

	public FormulaSyntax getLTLFormula() { return ltl_formula; }
	public Formula getFormula(boolean original) {
		if (this.fac != null) {
			if (original) {
				return this.fac.makeNot(this.fac.getFormula());
			} else {
				return this.fac.getFormula();
			}
		} else {
			return null;
		}
	}
	
}



