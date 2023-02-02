package ltsa.lts.util;

import java.util.BitSet;
import java.util.List;
import java.util.Vector;

import ltsa.lts.Analyser;
import ltsa.lts.Animator;
import ltsa.lts.CompactState;
import ltsa.lts.CompositeState;
import ltsa.lts.EventManager;
import ltsa.lts.LTSOutput;
import ltsa.lts.MyList;
import ltsa.lts.StackCheck;
import ltsa.lts.ltl.FluentTrace;

public class AnimatorDecorator implements Animator {
	private Analyser analyser;
	public int actionChosen() {
		return analyser.actionChosen();
	}
	public String actionNameChosen() {
		return analyser.actionNameChosen();
	}
	public boolean analyse(boolean checkDeadlocks) {
		return analyser.analyse(checkDeadlocks);
	}
	public boolean analyse(FluentTrace tracer) {
		return analyser.analyse(tracer);
	}
	public CompactState compose() {
		return analyser.compose();
	}
	public CompactState composeNoHide() {
		return analyser.composeNoHide();
	}
	public void disablePartialOrder() {
		analyser.disablePartialOrder();
	}
	public void enablePartialOrder() {
		analyser.enablePartialOrder();
	}
	public boolean END(byte[] state) {
		return analyser.END(state);
	}
	public boolean equals(Object obj) {
		return analyser.equals(obj);
	}
	public String[] getAllNames() {
		return alphabetWithNoMaybes();
	}
	private String[] alphabetWithNoMaybes() {
		String[] allNames = analyser.getAllNames();
		String[] result = new String[allNames.length/2];
		for (int i = 0; i < result.length; i++) {
			result[i] = allNames[i];
		}
		return result;
	}
	public String[] getAlphabet() {
		return alphabetWithNoMaybes();
	}
	public List getErrorTrace() {
		return analyser.getErrorTrace();
	}
	public String[] getMenuNames() {
		return analyser.getMenuNames();
	}
	public boolean getPriority() {
		return analyser.getPriority();
	}
	public BitSet getPriorityActions() {
		return analyser.getPriorityActions();
	}
	public Vector getTraceToState(byte[] from, byte[] to) {
		return analyser.getTraceToState(from, to);
	}
	public MyList getTransitions(byte[] state) {
		return analyser.getTransitions(state);
	}
	public String getViolatedProperty() {
		return analyser.getViolatedProperty();
	}
	public boolean hasErrorTrace() {
		return analyser.hasErrorTrace();
	}
	public int hashCode() {
		return analyser.hashCode();
	}
	public BitSet initialise(Vector menu) {
		return analyser.initialise(menu);
	}
	public boolean isAccepting(byte[] state) {
		return analyser.isAccepting(state);
	}
	public boolean isEnd() {
		return analyser.isEnd();
	}
	public boolean isError() {
		return analyser.isError();
	}
	public boolean isPartialOrder() {
		return analyser.isPartialOrder();
	}
	public BitSet menuStep(int choice) {
		return analyser.menuStep(choice);
	}
	public void message(String msg) {
		analyser.message(msg);
	}
	public boolean nonMenuChoice() {
		return analyser.nonMenuChoice();
	}
	public void setStackChecker(StackCheck s) {
		analyser.setStackChecker(s);
	}
	public BitSet singleStep() {
		return analyser.singleStep();
	}
	public byte[] START() {
		return analyser.START();
	}
	public String toString() {
		return analyser.toString();
	}
	public boolean traceChoice() {
		return analyser.traceChoice();
	}
	public BitSet traceStep() {
		return analyser.traceStep();
	}
	public AnimatorDecorator(Analyser analyser) {
		this.analyser = analyser;
	}
	
}
