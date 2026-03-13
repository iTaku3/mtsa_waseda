package ltsa.ui;

import ltsa.lts.LTSError;
import ltsa.lts.LTSException;

/**
 * An implementation of LTSError which is convinient for command-line execution.
 */
public class StandardError implements LTSError {

	public void displayError(LTSException x) {
		System.err.println(x);
	}

}
