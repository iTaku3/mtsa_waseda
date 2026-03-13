package ltsa.ui.update;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import ltsa.lts.util.LTSUtils;
import ltsa.ui.update.events.EndedEvent;
import ltsa.ui.update.events.NodeClickedEvent;
import ltsa.ui.update.events.StartedEvent;
import ltsa.updatingControllers.structures.graph.UpdateNode;
import ltsa.updatingControllers.structures.graph.UpdateTransition;
import ltsa.updatingControllers.synthesis.UpdatingControllersUtils;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.impl.MTSImpl;
import MTSTools.ac.ic.doc.mtstools.model.impl.MTSQuickClone;
import MTSTools.ac.ic.doc.mtstools.model.impl.MarkedMTS;
import MTSSynthesis.ar.dc.uba.model.condition.Fluent;
import MTSSynthesis.ar.dc.uba.model.condition.FluentUtils;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.BaseController;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.ITransitionEventListener;
import MTSAEnactment.ar.uba.dc.lafhis.enactment.TransitionEvent;
import MTSSynthesis.controller.util.FluentStateValuation;
import ltsa.enactment.SchedulerFactory;

/**
 * Created by Victor Wjugow on 18/06/15.
 */
public class UpdateGraphBidirectionalManager implements ITransitionEventListener<String>, UpdateGraphEventListener {

	private BaseController<Long, String> scheduler;
	private final UpdateGraphGUI graphGUI;
	private final SchedulerFactory<Long, String> schedFactory;
	private byte updateFinished = 0;
	private MTS<Long, String> currentController;
	private final Set<String> controllableActions;
	private UpdateTransition updateTransition;
	private UpdateNode nextUpdateNode;
	private final FluentUtils fluentUtils;
	private UpdateGraphWindow graphWindow;

	public UpdateGraphBidirectionalManager(SchedulerFactory<Long, String> schedulerFactory, UpdateGraphGUI graphGUI) {
		this.schedFactory = schedulerFactory;
		this.graphGUI = graphGUI;
		this.graphGUI.addUpdateGraphEventListener(this);
		this.fluentUtils = FluentUtils.getInstance();
		controllableActions = new HashSet<String>();
	}

	@Override
	public void handleTransitionEvent(TransitionEvent<String> transitionEvent) throws Exception {
		if (!UpdatingControllersUtils.isNotUpdateAction(transitionEvent.getAction())) {
			this.updateFinished++;
			if (this.updateFinished == 3) {
				this.controllableActions.clear();
				this.controllableActions.addAll(updateTransition.getControllableActions());
				MTSImpl<Long, String> chopped = this.cutController((MarkedMTS<Long, String>) this.currentController);
				this.doControllerHotSwap(chopped, this.scheduler.getCurrentState(), this.controllableActions);
				this.nextUpdateNode = null;
				this.updateFinished = 0;
				this.graphGUI.updateFinished();
			}
		}
	}

	@Override
	public void handleUpdateGraphEvent(StartedEvent event) {
		//Start UIcontroller with the machine in the initial node
		UpdateNode initialNode = event.getInitialNode();
		LTS<Long, String> c$$e = LTSUtils.toLts(initialNode.getController$environment());
		try {
			this.scheduler = schedFactory.getScheduler("UI Controller", c$$e, initialNode.getControllableActions());
			this.scheduler.setUp();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.scheduler.addTransitionEventListener(this);
		this.currentController = initialNode.getController$environment();
		this.scheduler.start();
		try {
			this.scheduler.takeNextAction();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Replaces current machine with the Cu that will perform the update to the clicked node
	 *
	 * @param event
	 */
	@Override
	public void handleUpdateGraphEvent(NodeClickedEvent event) {
		MTS<Long, String> mts;
		if (updateTransition == null) {
			//If this is the first update, take the clean, currently being executed, C
			mts = currentController;
		} else {
			// Else take the last Cu used, which was actually chopped to the marked states, and make the initial state
			// be the one calculated in the last update. This way the fluents will reflect the correct values when
			// mapping states.
			mts = new MTSQuickClone(updateTransition.getUpdateController());
			mts.setInitialState(currentController.getInitialState());
		}
		this.updateTransition = event.getUpdateTransition();
		this.nextUpdateNode = event.getNextNode();
		MarkedMTS<Long, String> updateController = updateTransition.getUpdateController();
		Long initState = mapCurrentStateToUpdateController(updateController, mts, updateTransition.getFluents(),
			scheduler.getCurrentState());
		this.controllableActions.clear();
		this.controllableActions.addAll(updateTransition.getControllableActions());
		this.doControllerHotSwap(updateController, initState, controllableActions);
	}

	@Override
	public void handleUpdateGraphEvent(EndedEvent event) {
		if (this.scheduler != null) {
			this.scheduler.stop();
			this.scheduler.tearDown();
			this.scheduler.removeAllTransitionEventListener();
			this.scheduler = null;
		}
		this.graphGUI.removeAllListeners();
	}

	private void doControllerHotSwap(MTS<Long, String> updateController, Long nextState, Set<String> controllable) {
		this.currentController = updateController;
		MTSImpl updateControllerCopy = new MTSQuickClone(updateController);
		updateControllerCopy.setInitialState(nextState);
		this.scheduler.setLts(LTSUtils.toLts(updateControllerCopy));
		this.scheduler.setControllableActions(controllable);
		try {
			this.scheduler.takeNextAction();
		} catch (Exception e) {
			e.printStackTrace();
			graphWindow.showError("There was a problem switching the Controllers. Please check the logs and start again.");
		}
	}

	/*
	 * Cut the current Cu states to those strictly necessary for it to behave as C'.
	 * This method is called when an update finishes.
	 * @param currentController
	 * @return
	 */
	private MTSImpl<Long, String> cutController(MarkedMTS<Long, String> currentController) {
		Long currentState = this.scheduler.getCurrentState();
		Long newInitialState = mapUpdateInitialState(currentController, updateTransition.getFluents(), this
			.nextUpdateNode.getController$environment(), currentState);
		MTSImpl<Long, String> chopped = currentController.chopNotMarkedStates(newInitialState, currentState);
		//Remove unnecessary controllable actions.
		for (Iterator<String> it = this.controllableActions.iterator(); it.hasNext(); ) {
			String action = it.next();
			if (!chopped.getActions().contains(action)) {
				it.remove();
			}
		}
		return chopped;
	}

	/*
	 *
	 * @param updtController
	 * @param fluentPairs
	 * @param c$$e
	 * @return one of the states in updtController that has the same fluent values as the C' it mimics,
	 * which is the c$$e parameter. If no state can be found, just retrieve a state where all the fluents that
	 * are initially true have the same value in both machines (heuristic that can be improved). This solves
	 * the problem fluents that are initially true and turn to false at some point, but after the update
	 * if we don't do this, we could be in a state where the fluent is true, but should be false.
	 */
	private Long mapUpdateInitialState(MarkedMTS<Long, String> updtController, ArrayList<Fluent> fluentPairs,
				MTS<Long, String> c$$e, Long currentState) {
		ArrayList<Fluent> fluentsList = fluentPairs;
		Set<Long> sameStates = mapStatesByFluents(fluentsList, updtController, c$$e, c$$e.getInitialState(),
			updtController.getMarkedStates(currentState));
		if (sameStates.isEmpty()) {
			ArrayList<Fluent> newFluents = new ArrayList<Fluent>();
			for (Iterator<Fluent> it = fluentsList.iterator(); it.hasNext(); ) {
				Fluent fluent = it.next();
				//TODO use a better heuristic
				if (fluent.isInitialValue()) {
					newFluents.add(fluent);
				}
			}
			sameStates = mapStatesByFluents(newFluents, updtController, c$$e, c$$e.getInitialState(), updtController
				.getMarkedStates(currentState));
		}
		return sameStates.isEmpty() ? null : sameStates.iterator().next();
	}

	private Long mapCurrentStateToUpdateController(MTS<Long, String> updtController, MTS<Long, String> mts,
												   ArrayList<Fluent> fluentsPairs, Long currentState) {
		ArrayList<Fluent> fluents = fluentsPairs;
		Set<Long> states = mapStatesByFluents(fluents, updtController, mts, currentState, updtController.getStates());
		if (states.size() > 1) {
			// Since we're using the previous Cu instead of the current chopped C (mts parameter), there will surely be
			// more than 1  matching state. Here we filter out the states where the update has already happened by
			// expecting false in the update fluents.
			ArrayList<Fluent> updateFluents = new ArrayList<Fluent>(UpdatingControllersUtils.UPDATE_FLUENTS);
			MTSImpl<Long, String> updateFalse = new MTSImpl<Long, String>(0l);
			states = mapStatesByFluents(updateFluents, updtController, updateFalse, 0l, states);
		}
		if (states.isEmpty()) {
			throw new RuntimeException("Could not find corresponding state for next controller.");
		}
		return states.iterator().next();
	}

	private Set<Long> mapStatesByFluents(ArrayList<Fluent> fluents, MTS<Long, String> updateController, MTS<Long,
		String> mts, Long currentState, Collection<Long> states) {
		Set<Long> sameStates = new HashSet<Long>();
		FluentStateValuation<Long> cuValuation = fluentUtils.buildValuation(updateController, fluents);
		FluentStateValuation<Long> currentValuation = fluentUtils.buildValuation(mts, fluents);
		ArrayList<Boolean> currentStateValuation = currentValuation.getFluentsFromState(currentState, fluents);
		for (Long state : states) {
			ArrayList<Boolean> contUpdStateValuation = cuValuation.getFluentsFromState(state, fluents);
			if (Arrays.deepEquals(currentStateValuation.toArray(), contUpdStateValuation.toArray())) {
				sameStates.add(state);
			}
		}
		//TODO check if states is bigger than 1 and show in console!
		return sameStates;
	}

	private ArrayList<Fluent> extractFluents(ArrayList<Pair<Fluent, Fluent>> fluentsPairs) {
		Set<Fluent> fluentsSet = new HashSet<Fluent>();
		for (Pair<Fluent, Fluent> fluent : fluentsPairs) {
			fluentsSet.add(fluent.getFirst());
			fluentsSet.add(fluent.getSecond());
		}
		return new ArrayList<Fluent>(fluentsSet);
	}

	public void setGraphWindow(UpdateGraphWindow graphWindow) {
		this.graphWindow = graphWindow;
	}
}