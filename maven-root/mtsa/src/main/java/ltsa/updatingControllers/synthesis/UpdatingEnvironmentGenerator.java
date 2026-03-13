package ltsa.updatingControllers.synthesis;

import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.impl.UpdatingEnvironment;
import ltsa.control.util.ControllerUtils;
import ltsa.updatingControllers.UpdateConstants;

import java.util.*;

public class UpdatingEnvironmentGenerator {

    private final MTS<Long, String> oldController;
    private final MTS<Long, String> mapping;

    private UpdatingEnvironment updEnv;
    private Map<Long, Long> mappingToUpdEnv;
	private ArrayList<Long> eParallelCStates; // used for relabeling actions

	public UpdatingEnvironmentGenerator(MTS<Long, String> oldController, MTS<Long, String> mapping) {

        this.oldController = oldController;
        this.mapping = mapping;

        updEnv = new UpdatingEnvironment(oldController);
        mappingToUpdEnv = new HashMap<Long, Long>();
		eParallelCStates = new ArrayList<Long>(updEnv.getStates());

	}

	public void generateEnvironment() {

        linkWithBeginUpdate();
        completeWithMapping();
	}

    /**
     * pre: updEnv is E||C
     * post: updEnv is E||C plus some states of E (only the one that has an incoming beginUpdate transition)
     * states from E has a stopOldSpec, startNewSpec actions
     */
    private void linkWithBeginUpdate() {

        updEnv.addAction(UpdateConstants.BEGIN_UPDATE);
        addBeginUpdateTransition(updEnv.getInitialState(), mapping.getInitialState());
        eParallelCStates.add(updEnv.getInitialState());

        // BFS
		Queue<Pair<Long,Long>> toVisit = new LinkedList<Pair<Long,Long>>();
		Pair<Long, Long> firstState = new Pair(new Long(oldController.getInitialState()), new Long(mapping.getInitialState()));
		toVisit.add(firstState);
		ArrayList<Pair<Long, Long>> discovered = new ArrayList<Pair<Long, Long>>();

		while (!toVisit.isEmpty()) {
			Pair<Long, Long> actual = toVisit.remove();
			if (!discovered.contains(actual)) {
				discovered.add(actual);
				for (Pair<String, Long> action_toState : oldController.getTransitions(actual.getFirst(), MTS.TransitionType.REQUIRED)) {
					toVisit.addAll(nextToVisitInParallelComposition(actual, action_toState));
				}
			}
		}
    }

	private ArrayList<Pair<Long, Long>> nextToVisitInParallelComposition(Pair<Long, Long> actual, Pair<String, Long> transition) {

		ArrayList<Pair<Long, Long>> toVisit = new ArrayList<Pair<Long, Long>>();

		for (Pair<String, Long> action_toStateInMapping : mapping.getTransitions(actual.getSecond(), MTS.TransitionType.REQUIRED)) {

			String actionInMapping = action_toStateInMapping.getFirst();
			Long toStateInMapping = action_toStateInMapping.getSecond();

			if (transition.getFirst().equals(actionInMapping)) {

                //action = action.concat(UpdateControllerSolver.label); // rename the actions so as to
				// distinguish from the controllable in the new problem controller
                eParallelCStates.add(transition.getSecond());

                addBeginUpdateTransition(transition.getSecond(), toStateInMapping);
				toVisit.add(new Pair<Long, Long>(transition.getSecond(), toStateInMapping));
			}
		}

		return toVisit;
	}

	private void addBeginUpdateTransition(Long from, Long originalMappingState) {

        if (mappingToUpdEnv.containsKey(originalMappingState)) {
            Long toState = mappingToUpdEnv.get(originalMappingState);
            updEnv.addTransition(from, UpdateConstants.BEGIN_UPDATE, toState);
        } else {
            Long freshState = updEnv.newState();
            updEnv.addTransition(from, UpdateConstants.BEGIN_UPDATE, freshState);

            mappingToUpdEnv.put(originalMappingState, freshState);
            addStopOldAndStartNewSpecActions(freshState);
        }
    }

    /**
     * pre: updEnv is E||C plus some states of E (only the one that has an incoming beginUpdate transition)
     * post: updEnv is E||C -> E -> E'
     * states from E and E' has a stopOldSpec, startNewSpec actions.
     */
	private void completeWithMapping() {

        for (Long originalOldEnvState : mapping.getStates()) {

            for (Pair<String, Long> action_toState : mapping.getTransitions(originalOldEnvState, MTS.TransitionType.REQUIRED)) {

                if (mappingToUpdEnv.containsKey(originalOldEnvState)) {

                    Long updEnvState = mappingToUpdEnv.get(originalOldEnvState);
                    Set<Long> freshState = addTransitionCreatingNewStates(action_toState, updEnvState);
                    addStopOldAndStartNewSpecActions(freshState);
                } else {

                    Long freshUpdEnvState = addState(originalOldEnvState);
                    addStopOldAndStartNewSpecActions(freshUpdEnvState);
                    Set<Long> freshState = addTransitionCreatingNewStates(action_toState, freshUpdEnvState);
                    addStopOldAndStartNewSpecActions(freshState);

                }
            }
        }
	}

    private void addStopOldAndStartNewSpecActions(Set<Long> freshState) {
        if (freshState.isEmpty()) return;
        addStopOldAndStartNewSpecActions(freshState.iterator().next());
    }

    private void addStopOldAndStartNewSpecActions(Long state) {
        updEnv.addAction(UpdateConstants.STOP_OLD_SPEC);
        updEnv.addAction(UpdateConstants.START_NEW_SPEC);
        updEnv.addTransition(state, UpdateConstants.STOP_OLD_SPEC, state);
        updEnv.addTransition(state, UpdateConstants.START_NEW_SPEC, state);
    }

    /**
     *
     * @param action_toState
     * @param state
     * @return empty set if not fresh state was created. A set with the new state if a fresh state was created
     */
	private Set<Long> addTransitionCreatingNewStates(Pair<String, Long> action_toState, Long state) {

        Set<Long> result = new HashSet<Long>();
		updEnv.addAction(action_toState.getFirst());
		if (!mappingToUpdEnv.containsKey(action_toState.getSecond())) {

			Long freshUpdEnvState = addState(action_toState.getSecond());
			updEnv.addTransition(state, action_toState.getFirst(), freshUpdEnvState);
            result.add(freshUpdEnvState);

		} else {
			updEnv.addTransition(state, action_toState.getFirst(), mappingToUpdEnv.get(action_toState.getSecond()));
		}
        return result;
	}

	private Long addState(Long originalState) {

		Long newState = updEnv.newState();
		mappingToUpdEnv.put(originalState, newState);
		return newState;
	}

	public UpdatingEnvironment getUpdEnv(){
		return updEnv;
	}

}