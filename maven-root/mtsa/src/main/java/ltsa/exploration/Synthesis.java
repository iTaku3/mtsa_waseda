package ltsa.exploration;

import MTSSynthesis.controller.model.ControllerGoal;
import ltsa.dispatcher.TransitionSystemDispatcher;
import ltsa.lts.CompactState;
import ltsa.lts.CompositeState;
import ltsa.lts.EmptyLTSOuput;
import ltsa.lts.EventState;

import java.util.*;

public class Synthesis
{
    private CompositeState composition;

    //region Constructor
    public Synthesis(CompactState[] mts, ControllerGoal<String> goal_original)
    {
        // Validate goal
        ControllerGoal<String> goal = goal_original.copy();
        for (String anAction : goal.getControllableActions())
            if (anAction.contains("["))
                throw new UnsupportedOperationException("Goal corrupted");

        // Classify actions
        ArrayList<String> controllableActions = new ArrayList<>();
        for (String anAction : goal.getControllableActions())
            controllableActions.add(anAction);

        ArrayList<String> uncontrollableActions = new ArrayList<>();
        for (int i = 1; i < mts.length; i++)
            for (String anAction : mts[i].alphabet)
                if (!anAction.contains("?") && !anAction.contains("tau") && !controllableActions.contains(anAction) && !uncontrollableActions.contains(anAction))
                    uncontrollableActions.add(anAction);

        // Order in goal and mts
        Boolean needOrder = uncontrollableActions.size() > 0;
        if (needOrder)
        {
            controllableActions.add("wait");
            Set<String> goalActions = goal.getControllableActions();
            goalActions.add("wait");
            goal.setControllableActions(goalActions);

            mts[0].alphabet = Arrays.copyOf(mts[0].alphabet, mts[0].alphabet.length + 2);
            mts[0].alphabet[mts[0].alphabet.length - 2] = "wait";
            mts[0].alphabet[mts[0].alphabet.length - 1] = "wait?";
            for (int i = 0; i < mts[0].states.length; i++)
                if (mts[0].states[i] != null)
                    mts[0].states[i].setList(new EventState(mts[0].alphabet.length - 2, i));
        }

        ArrayList<String> allActions = new ArrayList<>(controllableActions);
        allActions.addAll(uncontrollableActions);

        // Create composition
        Vector<CompactState> compositionMachines = new Vector<>();
        Collections.addAll(compositionMachines, mts);

        // Alpha stop
        EventState[] alphaStopStates = new EventState[1];
        CompactState alphaStop = mts[0].myclone();
        alphaStop.maxStates = 1;
        alphaStop.states = alphaStopStates;

        // Composition
        this.composition = new CompositeState(compositionMachines);
        this.composition.goal = goal;
        this.composition.makeController = true;
        this.composition.setCompositionType(47);
        this.composition.alphaStop = alphaStop;
        this.composition.priorityIsLow = true;

        // Order in referee
        if (needOrder)
        {
            CompactState order = new CompactState();
            order.name = "ORDER";
            order.alphabet = Arrays.copyOf(allActions.toArray(new String[allActions.size()]), allActions.size());
            order.maxStates = mts[0].states.length - 1;
            if (order.maxStates < 2)
                order.maxStates = 2;
            order.states = new EventState[order.maxStates];

            // Controllable actions
            for (int stateNumber = 0; stateNumber < order.maxStates - 1; stateNumber++)
            {
                for (int i = 0; i < order.alphabet.length; i++)
                    if (order.alphabet[i].equals(controllableActions.get(0)))
                        order.states[stateNumber] = new EventState(i, stateNumber + 1);

                for (int i = 1; i < controllableActions.size(); i++)
                    for (int j = 0; j < order.alphabet.length; j++)
                        if (order.alphabet[j].equals(controllableActions.get(i)))
                            order.states[stateNumber].setList(new EventState(j, stateNumber + 1));
            }

            // Uncontrollable actions
            for (int i = 0; i < order.alphabet.length; i++)
                if (order.alphabet[i].equals(uncontrollableActions.get(0)))
                    order.states[order.maxStates - 1] = new EventState(i, 0);

            for (int i = 1; i < uncontrollableActions.size(); i++)
                for (int j = 0; j < order.alphabet.length; j++)
                    if (order.alphabet[j].equals(uncontrollableActions.get(i)))
                        order.states[order.maxStates - 1].setList(new EventState(j, 0));

            this.composition.machines.add(order);
        }

        // Sinthesis
        TransitionSystemDispatcher.applyComposition(this.composition, new EmptyLTSOuput());
    }
    //endregion

    //region Public methods
    public CompositeState getComposition()
    {
        return composition;
    }
    public String getMTSControlProblemAnswer()
    {
        return this.composition.composition.getMtsControlProblemAnswer();
    }
    public HashSet<String> getControllerAvailableActions()
    {
        CompactState machine = this.composition.composition;
        HashSet<EventState> states = new HashSet<>();
        states.add(machine.states[0]);

        int oldStatesCount = -1;
        int newStatesCount = -2;
        while (oldStatesCount != newStatesCount)
        {
            oldStatesCount = states.size();
            for (EventState aState : states)
            {
                if (machine.alphabet[aState.getEvent()].contains("["))
                    states.add(machine.states[aState.getNext()]);
                EventState list = aState.getList();
                while (list != null)
                {
                    if (machine.alphabet[list.getEvent()].contains("["))
                        states.add(machine.states[list.getNext()]);
                    list = list.getList();
                }
            }
            newStatesCount = states.size();
        }

        HashSet<String> actions = new HashSet<>();
        for (EventState anEventState : states)
            for (int anEvent : anEventState.getEvents())
                if (!machine.alphabet[anEvent].contains("["))
                    actions.add(machine.alphabet[anEvent].replace("?", ""));
        return actions;
    }
    //endregion

}
