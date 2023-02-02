package ltsa.lts.ltl;

import java.util.Iterator;
import java.util.List;

import ltsa.lts.LTSOutput;

/* -----------------------------------------------------------------------*/

public class FluentTrace {

	PredicateDefinition[] fluents;

	int[] state;

	public FluentTrace(PredicateDefinition f[]) {
		if (f != null) {
			fluents = f;
			state = new int[fluents.length];
		}
	}

	private void initialise() {
		if (state == null)
			return;
		for (int i = 0; i < state.length; ++i)
			state[i] = fluents[i].initial();
	}

	private void update(String a) {
		if (state == null)
			return;
		for (int i = 0; i < state.length; ++i) {
			int res = fluents[i].query(a);
			if (res != 0)
				state[i] = res;
		}
	}

	private String fluentString() {
		if (state == null)
			return "";
		StringBuffer buf = new StringBuffer();
		buf.append("\t\t");
		boolean first = true;
		for (int i = 0; i < state.length; ++i) {
			if (state[i] > 0) {
				if (!first)
					buf.append(" && ");
				buf.append(fluents[i].toString());
				first = false;
			}
		}
		return buf.toString();
	}

	// bugfix - init parameter 26-mar-04
	public void print(LTSOutput out, List trace, boolean init) {
		if (trace == null)
			return;
		if (init)
			initialise();
		Iterator I = trace.iterator();
		while (I.hasNext()) {
			String act = (String) I.next();
			update(act);
			out.outln("\t" + act + fluentString());
		}
	}

}
