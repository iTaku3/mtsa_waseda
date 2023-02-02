/**
 * 
 */
package ltsa.enactment;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashSet;
import java.util.Set;

import ltsa.ui.HPWindow;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;
import MTSTools.ac.ic.doc.mtstools.model.impl.LTSAdapter;
import ltsa.ac.ic.doc.mtstools.util.fsp.AutomataToMTSConverter;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.BaseController;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.TakeFirstController;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.Enactor;

import ltsa.lts.CompositeState;
import ltsa.ui.enactment.RunEnactorsWindow;

/**
 * Setup and execute a MTSA Enactment simulation
 *  
 * @author Julio
 *
 */
public class MTSAEnactmentSimulation<State, Action> implements Runnable {

	//Simulation Thread 
	private static Thread updateThread;
	private volatile boolean running = true;
	private volatile boolean pauseSimulation = false;
	private BaseController<Long, String> controllerScheduler;
	private Set<Enactor<Long, String>> enactors = new HashSet<Enactor<Long, String>>();
	
	
	private RunEnactorsWindow<State, Action> runEnactorsWindow;

	private Logger logger = Logger.getLogger(this.getClass().getName());

	public void runSimulation(CompositeState currentComposition, 
			ApplicationContext applicationContext, 
			EnactmentOptions<State, Action> enactmentOptions)
	{
		
		if(this.runEnactorsWindow!=null && this.runEnactorsWindow.isVisible())
		{
			java.awt.EventQueue.invokeLater(new Runnable() {
			    @Override
			    public void run() {
			    	runEnactorsWindow.toFront();
			    	runEnactorsWindow.repaint();
			    }
			});
			return;
			
		}
		

		
		EnactorFactory<Long, String> enactorFactory = applicationContext.getBean(EnactorFactory.class);
		if (enactorFactory == null)
		{			
			logger.error("Enactor Factory not found");
			return;
		}

		
		if (enactmentOptions.enactors != null)
		{
			for (String enactorName : enactmentOptions.enactors)
			{
				try {
					
					enactors.add(enactorFactory.getEnactor(enactorName));
				} catch (Exception e) {
					e.printStackTrace();
				}						
			}
		}

		
		MTS<Long, String> mts = AutomataToMTSConverter.getInstance().convert(currentComposition.composition);
		LTSAdapter<Long, String> ltsAdapter = new LTSAdapter<Long, String>(mts, TransitionType.REQUIRED);
		Set<String> controllableActions;
		
		controllableActions = currentComposition.goal.getControllableActions();
		
		
		
		SchedulerFactory<Long, String> schedulerFactory = applicationContext.getBean(SchedulerFactory.class);
		if (schedulerFactory == null)
		{
			
			logger.error("Scheduler factory not found");
			return;
		}
		BaseController<Long, String> controllerScheduler;
		try {
			controllerScheduler = schedulerFactory.getScheduler(enactmentOptions.scheduler, ltsAdapter, controllableActions);
		} catch (Exception e1) {				
			e1.printStackTrace();
			return;
		}
					
		
		for (Enactor<Long, String> enactor : enactors)
		{
			try {
				//controllerScheduler.addTransitionDispatcher(enactor);
				enactor.start();
				controllerScheduler.addTransitionEventListener(enactor);
				enactor.addTransitionEventListener(controllerScheduler);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		this.setControllerScheduler(controllerScheduler);
		
		
		runEnactorsWindow = new RunEnactorsWindow(controllerScheduler);

		runEnactorsWindow.addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent e) {
			}
			
			@Override
			public void windowIconified(WindowEvent e) {
			}
			
			@Override
			public void windowDeiconified(WindowEvent e) {
			}
			
			@Override
			public void windowDeactivated(WindowEvent e) {
			}
			
			@Override
			public void windowClosing(WindowEvent e) {				
				setRunning(false); //Stop the simulation thread
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
			}
			
			@Override
			public void windowActivated(WindowEvent e) {
			}
		});
		
		runEnactorsWindow.getPauseButton().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//Pause Simulation
				setPauseSimulation(!isPauseSimulation());
				
				if (isPauseSimulation())
				{
					getLogger().info("Simulation paused");
					getRunEnactorsWindow().getPauseButton().setText("Resume Simulation");
				} else
				{
					getLogger().info("Simulation resumed");
					getRunEnactorsWindow().getPauseButton().setText("Pause Simulation");
				}
				
			}
		});
		runEnactorsWindow.getStopButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Stop the simulation
				setRunning(false);
			}
		});

				
	
		//TODO: setup controls
		
		runEnactorsWindow.setVisible(true);
		
		//runEnactorsWindow.zoomToFit();
		
		
		logger.info("Simulation setup: ");
		logger.info("Scheduler: " + this.getControllerScheduler().getName());
		
		
		//Start simulation thread
		controllerScheduler.start();
		setRunning(true);
		setPauseSimulation(false);
		
		
		try {
			this.getControllerScheduler().takeNextAction();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		updateThread = new Thread(this);
		updateThread.start();
		
		
	}

	/**
	 * @return the controllerScheduler
	 */
	private synchronized BaseController<Long, String> getControllerScheduler() {
		return controllerScheduler;
	}
	/**
	 * @param controllerScheduler the controllerScheduler to set
	 */
	private synchronized void setControllerScheduler(
			BaseController<Long, String> controllerScheduler) {
		this.controllerScheduler = controllerScheduler;
	}
	

	
	/**
	 * @return the running
	 */
	private synchronized boolean isRunning() {
		return running;
	}

	/**
	 * @param running the running to set
	 */
	private synchronized void setRunning(boolean running) {
		this.running = running;
	}

	/**
	 * @return the pauseSimulation
	 */
	private synchronized boolean isPauseSimulation() {
		return pauseSimulation;
	}

	/**
	 * @param pauseSimulation the pauseSimulation to set
	 */
	private synchronized void setPauseSimulation(boolean pauseSimulation) {
		this.pauseSimulation = pauseSimulation;
	}

	/**
	 * @return the runEnactorsWindow
	 */
	private synchronized RunEnactorsWindow<State, Action> getRunEnactorsWindow() {
		return runEnactorsWindow;
	}

	/**
	 * @param runEnactorsWindow the runEnactorsWindow to set
	 */
	private synchronized void setRunEnactorsWindow(
			RunEnactorsWindow<State, Action> runEnactorsWindow) {
		this.runEnactorsWindow = runEnactorsWindow;
	}

	/**
	 * @return the logger
	 */
	private synchronized Logger getLogger() {
		return logger;
	}

	

	@Override
	public void run() {
		this.getLogger().info("Simulation started");
		
		while(this.isRunning())
		{
			try {
				if (this.isPauseSimulation())
				{
					if (!controllerScheduler.isStop())
						controllerScheduler.stop();
				} else 
					if (controllerScheduler.isStop())
						controllerScheduler.start();
				//TODO: Review simulation thread sleep approach  
				Thread.sleep(100);				
			} catch (Exception e) {
				e.printStackTrace();
			}
		

		}
		
		this.getLogger().info("Simulation ended");
		controllerScheduler.stop();
		controllerScheduler.tearDown();
		for (Enactor<Long, String> enactor : this.enactors)
		{
			enactor.tearDown();
		}
		this.getRunEnactorsWindow().setVisible(false);
		HPWindow.instance.setVisible(true);
		
		
		
				
	}

}
