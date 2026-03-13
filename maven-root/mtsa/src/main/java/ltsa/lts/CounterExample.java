package ltsa.lts;
import java.util.Enumeration;
import java.util.Vector;

public class CounterExample {

    protected CompositeState mach;
    protected Vector<String> errorTrace = null;

    public CounterExample(CompositeState m) {
        mach = m;
    }

    public void print(LTSOutput output , boolean checkDeadlocks ) {
        EventState trace = new EventState(0,0);
        int findState = Declaration.ERROR;
        if (checkDeadlocks){
        	findState = Integer.MIN_VALUE;
        }
        int result = EventState.search(
                         trace,
                         mach.composition.states,
                         	0,
                         findState,
                         mach.composition.endseq,
                         checkDeadlocks
                     );
        errorTrace = null;
        switch(result) {
        case Declaration.SUCCESS:
            output.outln("No deadlocks/errors");
            break;
        case Declaration.STOP:
           output.outln("Trace to DEADLOCK:");
           errorTrace = EventState.getPath(trace.path,mach.composition.alphabet);
           printPath(output,errorTrace);
           break;
        case Declaration.ERROR:
           errorTrace = EventState.getPath(trace.path,mach.composition.alphabet);
           String name = findComponent(errorTrace);
           output.outln("Trace to property violation in "+name+":");
           printPath(output,errorTrace);
           break;
        }
    }

    private void printPath(LTSOutput output, Vector<String> v) {
        Enumeration<String> e = v.elements();
        while (e.hasMoreElements())
            output.outln("\t"+e.nextElement());
    }

    private String findComponent(Vector<String> trace) {
        Enumeration<CompactState> e = mach.machines.elements();
        while (e.hasMoreElements()) {
            CompactState cs = e.nextElement();
            if (cs.isErrorTrace(trace)) return cs.name;
        }
        return "?";
    }
    
    public Vector<String> getErrorTrace(){ return errorTrace;}
}

