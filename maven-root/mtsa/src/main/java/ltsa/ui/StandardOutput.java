package ltsa.ui;

import ltsa.lts.LTSOutput;

/**
 * An implementation of LTSOutput which is convenient for command-line
 * execution.
 */
public class StandardOutput implements LTSOutput {
	public void out(String s) {
		// System.out.print(s);
	}
	public void outln(String s) {
		// System.out.println(s);
	}
	public void clearOutput() {	}
}
