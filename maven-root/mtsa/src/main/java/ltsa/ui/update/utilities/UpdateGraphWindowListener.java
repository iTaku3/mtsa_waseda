package ltsa.ui.update.utilities;

import MTSSynthesis.controller.util.GeneralConstants;
import org.apache.commons.lang.mutable.MutableInt;
import ltsa.ui.update.UpdateGraphGUI;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * Created by Victor Wjugow on 18/06/15.
 */
public class UpdateGraphWindowListener implements WindowListener {

	private final MutableInt running;
	private final UpdateGraphGUI graphGUI;

	public UpdateGraphWindowListener(MutableInt running, UpdateGraphGUI graphGUI) {
		this.running = running;
		this.graphGUI = graphGUI;
	}

	@Override
	public void windowOpened(WindowEvent e) {
		this.running.setValue(GeneralConstants.OPEN);
	}

	@Override
	public void windowClosing(WindowEvent e) {
		this.running.setValue(GeneralConstants.CLOSING);
		if (graphGUI != null) {
			this.graphGUI.end();
		}
	}

	@Override
	public void windowClosed(WindowEvent e) {
		this.running.setValue(GeneralConstants.CLOSED);
		this.graphGUI.end();
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}
}