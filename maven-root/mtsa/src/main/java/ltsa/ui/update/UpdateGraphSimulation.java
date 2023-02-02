package ltsa.ui.update;

import MTSSynthesis.controller.util.GeneralConstants;
import ltsa.enactment.SchedulerFactory;
import org.apache.commons.lang.mutable.MutableInt;
import org.springframework.context.ApplicationContext;
import ltsa.ui.HPWindow;

import static MTSSynthesis.controller.util.GeneralConstants.OPEN;

/**
 * Created by Victor Wjugow on 15/06/15.
 */
public class UpdateGraphSimulation implements Runnable {

	private static final MutableInt running = new MutableInt(GeneralConstants.CLOSED);
	private final HPWindow input;
	private final String directory;
	private final SchedulerFactory<Long, String> schedulerFactory;

	public UpdateGraphSimulation(HPWindow hpWindow, String currentDirectory, ApplicationContext applicationContext) {
		this.input = hpWindow;
		this.directory = currentDirectory;
		this.schedulerFactory = applicationContext.getBean(SchedulerFactory.class);
	}

	public void start() {
		Thread thread = new Thread(this);
		thread.start();
	}

	@Override
	public void run() {
		if (running.intValue() != OPEN) {
			running.setValue(OPEN);
			initUI();
		}
	}

	private void initUI() {
		UpdateGraphGUI graphGUI = new UpdateGraphGUI();
		UpdateGraphBidirectionalManager dispatcher = new UpdateGraphBidirectionalManager(schedulerFactory, graphGUI);
		final UpdateGraphWindow graphWindow = new UpdateGraphWindow(input, directory, running, graphGUI);
		dispatcher.setGraphWindow(graphWindow);
		graphWindow.setVisible(true);
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				graphWindow.toFront();
				graphWindow.repaint();
			}
		});
	}
}
