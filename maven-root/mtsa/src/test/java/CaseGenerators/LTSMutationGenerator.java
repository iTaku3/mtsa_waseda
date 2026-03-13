package CaseGenerators;

import ltsa.lts.*;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.util.*;

public class LTSMutationGenerator {
    private LTSInput src;
    private ArrayList<Symbol> parsedLTS;
    private ArrayList<MUTATION> mutations;
    private HashMap<Integer, ArrayList<Symbol>> values;
    private String destDir;

    private static final String EXPECTED_NAME = "ExpectedC";
    private static final String FSP_NAME = "C";
    private static final boolean VALIDATE_CONTROLLER = false;

    private static final int ACTIONS = -100;
    private static final int STATES = -200;


    public enum MUTATION {GOAL, CONTROLLABLE} ;

    public LTSMutationGenerator(LTSInput srcLts, ArrayList<MUTATION> mutations) {
        this.src = srcLts;
        this.mutations = mutations;
        parse(this.src);
    }

    public LTSMutationGenerator(LTSInput srcLts, ArrayList<MUTATION> mutations, String destDir) {
        this.src = srcLts;
        this.mutations = mutations;
        this.destDir = destDir;
        parse(this.src);
    }

    public LTSInput mutate() {
        LTSInput newLTS = new LTSInputString(new String());
        return newLTS;
    }



    private Symbol get_next_symbol(Lex lex, ArrayList<Symbol> stackAll, boolean disableStackAll) {
        Symbol sym = lex.next_symbol();
        if (!disableStackAll) {
            stackAll.add(sym);
        }

        return sym;
    }

    void parse(LTSInput lts) {

        ArrayList<Symbol> stackAll = new ArrayList<Symbol>();
        boolean disableStackAll = false;

        Lex lex = new Lex(lts);


        ArrayList<Symbol> stack = new ArrayList<Symbol>();
        HashMap<Integer, ArrayList<Symbol>> values = new HashMap<Integer, ArrayList<Symbol>>();
        Integer current = null;

        Set<Integer> ignoreSym = new HashSet<Integer>();
        ignoreSym.add(Symbol.COMMA);
        ignoreSym.add(Symbol.BECOMES);

        Symbol sym = get_next_symbol(lex, stackAll, disableStackAll);

        while(sym.kind != Symbol.EOFSYM) {
            boolean inGoal = false;

            if (sym.kind == Symbol.ARROW) {
                parseSymbolForAction(sym, values, stackAll);
            }

            if (sym.kind == Symbol.BECOMES) {
                parseSymbolForState(sym, values, stackAll);
            }

            if (sym.kind == Symbol.GOAL) {

                while ((sym.kind != Symbol.LCURLY)) {
                    sym = get_next_symbol(lex, stackAll, disableStackAll);
                }

                stack.add(0, sym);

                while (!stack.isEmpty()) {
                    sym = get_next_symbol(lex, stackAll, disableStackAll);

                    if (sym.kind == Symbol.LCURLY) {
                        stack.add(0, sym);
                    } else if (sym.kind == Symbol.MARKING || sym.kind == Symbol.CONTROLLABLE) {
                        current = sym.kind;
                        disableStackAll = true;
                    } else if (sym.kind == Symbol.RCURLY) {
                        stack.remove(0);
                        disableStackAll = false;
                        current = null;
                    } else {
                        if (current != null) {
                            if (current == Symbol.MARKING || current == Symbol.CONTROLLABLE) {
                                if (!ignoreSym.contains(sym.kind)) {
                                    if (!values.containsKey(current)) {
                                        values.put(current, new ArrayList<Symbol>());
                                    }
                                    values.get(current).add(sym);
                                }
                            }
                        }
                    }

                }
            }
            sym = get_next_symbol(lex, stackAll, disableStackAll);
        }

        this.parsedLTS =  stackAll;
        this.values = values;


    }

    public String generate() throws IOException{
        return generate(this.parsedLTS, this.values, this.mutations);
    }

    private String generate(ArrayList<Symbol> stackAll, HashMap<Integer, ArrayList<Symbol>> values, List<MUTATION> mutations) throws IOException {
        boolean disableOutput = false;
        StringWriter output = new StringWriter();
        Symbol sym;

        while(!stackAll.isEmpty()) {
            sym = stackAll.remove(0);
            String str = getSymbolString(sym);

            if (!stackAll.isEmpty() && getSymbolString(stackAll.get(0)).equals("ExpectedC")) {
                disableOutput = true;
                continue;
            }

            if (!disableOutput && !sym.equals(Symbol.EOFSYM)) {
                output.write(str + " ");
                //System.out.print(str + " ");
            }

            if (sym.kind == Symbol.MARKING) {
                Symbol currentGoal = values.get(Symbol.MARKING).get(0);
                if (mutations.contains(MUTATION.GOAL)) {
                    currentGoal = selectAnyFrom(currentGoal, values.get(ACTIONS));
                }
                output.write("={"+currentGoal+"} ");
                //System.out.print("={"+currentGoal+"} ");
            }
            if (sym.kind == Symbol.CONTROLLABLE) {
                String result = "={";
                ArrayList<Symbol> currentControllables = values.get(Symbol.CONTROLLABLE);
                Symbol toReplace = selectAnyFrom(currentControllables);
                currentControllables.remove(toReplace);

                if (mutations.contains(MUTATION.CONTROLLABLE)) {
                    ArrayList<Symbol> candidates = new ArrayList<>(values.get(ACTIONS));
                    candidates.removeAll(currentControllables);
                    toReplace = selectAnyFrom(candidates);

                }

                for (Symbol action: currentControllables) {
                    result = result+getSymbolString(action)+ ",";
                }
                result += toReplace;

                output.write(result+"} ");
                //System.out.print(result+"} ");
            }

        }
        output.flush();
        StringBuffer sb = output.getBuffer();

        return sb.toString().replace("EOF","");
    }

    private void parseSymbolForAction(Symbol sym,  HashMap<Integer, ArrayList<Symbol>> values, ArrayList<Symbol> stackAll) {
        Symbol prev = stackAll.get(stackAll.size() - 2);
        if (!values.containsKey(-100)) {
            values.put(-100, new ArrayList<Symbol>());
        }
        values.get(-100).add(prev);
    }


    private void parseSymbolForState(Symbol sym,  HashMap<Integer, ArrayList<Symbol>> values, ArrayList<Symbol> stackAll)  {
        if (stackAll.size() -3 >= 0) {
            Symbol prev2 = stackAll.get(stackAll.size() - 3);
            if (prev2.kind == Symbol.COMMA) {
                Symbol prev = stackAll.get(stackAll.size() - 2);

                if (!values.containsKey(-200)) {
                    values.put(-200, new ArrayList<Symbol>());
                }
                values.get(-200).add(prev);
            }
        }

    }

    private Symbol selectAnyFrom(Symbol current, List<Symbol> symbols) {
        List<Symbol> candidates = new ArrayList<Symbol>(symbols);
        if (current != null) {
            candidates.remove(current);
        }
        Random r = new Random();
        r.setSeed(System.currentTimeMillis());

        if (!symbols.isEmpty()) {
            int i = r.nextInt(candidates.size());
            return candidates.get(i);
        } else {
            return current;
        }
    }

    private Symbol selectAnyFrom(List<Symbol> symbols) {
        return selectAnyFrom(null, symbols);
    }


    private String getSymbolString(Symbol sym) {
        String str = sym.getName();
        if (str == null) {
            str = sym.toString();
        }

        return str;
    }
}


