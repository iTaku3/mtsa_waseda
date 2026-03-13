package ltsa.exploration.knowledge;

import ltsa.lts.EventState;
import ltsa.lts.CompactState;

import java.util.*;

public class Knowledge
{
    private CompactState[] components;
    private int[] currentStates;
    private ArrayList<ArrayList<StateEquivalence>> stateEquivalence;

    //region Constructor
    public Knowledge(CompactState[] components)
    {
        this.components = new CompactState[components.length];
        this.currentStates = new int[components.length];
        this.stateEquivalence = new ArrayList<>(components.length);

        for (int i = 0; i < components.length; i++)
        {
            this.components[i] = components[i];
            this.components[i].maxStates++;
            this.components[i].states = Arrays.copyOf(components[i].states, components[i].states.length + 1);
            this.components[i].states[this.components[i].states.length - 1] = EventState.copy(this.components[i].states[0]);
            this.currentStates[i] = this.components[i].states.length - 1;
            this.stateEquivalence.add(new ArrayList<StateEquivalence>(1));
            this.stateEquivalence.get(i).add(new StateEquivalence(0, this.components[i].states.length - 1));
        }
    }
    //endregion

    //Getters
    public CompactState[] getCmponents()
    {
        return this.components;
    }
    public int[] getCurrentStates()
    {
        return this.currentStates;
    }
    //endregion

    //region Public methods
    public void updateKnowledgeFromCurrentStateActions(Integer componentNumber, HashSet<String> actions)
    {
        // List of available possible actions
        HashSet<String> currentStateActions = new HashSet<>(0);
        for (String action : actions)
            currentStateActions.add(action + "?");

        int[] currentStateEvents = this.components[componentNumber].states[this.currentStates[componentNumber]].getEvents();
        for (int anEvent : currentStateEvents)
        {
            String anAction = this.components[componentNumber].alphabet[anEvent];
            if (anAction.contains("?") && !currentStateActions.contains(anAction))
                this.removeEventFromCurrentState(componentNumber, anEvent);
        }
    }
    public void execute(Integer componentNumber, String nextAction, Integer nextViewState, EventState modelNewEventState)
    {
        if (nextAction.equals("WAIT"))
            return;

        Integer fromState = this.currentStates[componentNumber];

        String nextActionPossible = nextAction + "?";
        int nextActionEvent = this.components[componentNumber].getEvent(nextAction);
        int nextActionEventPossible = this.components[componentNumber].getEvent(nextActionPossible);

        this.changeState(componentNumber, modelNewEventState, nextViewState);
        this.conifirmAction(componentNumber, nextActionEvent, nextActionEvent, fromState, this.currentStates[componentNumber]);
        this.conifirmAction(componentNumber, nextActionEventPossible, nextActionEvent, fromState, this.currentStates[componentNumber]);
    }
    public void reset()
    {
        this.currentStates[0] = this.getFirstStateNumber(0);
    }
    public CompactState[] cloneForSynthesisFromStart()
    {
        CompactState[] clone = new CompactState[components.length];
        for (int i = 0; i < clone.length; i++)
        {
            clone[i] = this.components[i].myclone();
            clone[i].states = Arrays.copyOf(clone[i].states, clone[i].maxStates);
        }

        for (int i = 0; i < this.components.length; i++)
            clone[i].swapStates(0, this.getFirstStateNumber(i));

        return clone;
    }
    public CompactState[] cloneForSynthesisFromCurrentState()
    {
        CompactState[] clone = new CompactState[components.length];
        for (int i = 0; i < clone.length; i++)
        {
            clone[i] = this.components[i].myclone();
            clone[i].states = Arrays.copyOf(clone[i].states, clone[i].maxStates);
        }

        for (int i = 0; i < this.components.length; i++)
            clone[i].swapStates(0, this.currentStates[i]);

        return clone;
    }
    public Integer getFirstStateNumber(Integer componentNumber)
    {
        for (int i = 0; i < components[componentNumber].states.length; i++)
            if (this.stateEquivalence.get(componentNumber).get(i).getViewStateNumber() == 0)
                return this.stateEquivalence.get(componentNumber).get(i).getKnowledgeStateNumber();

        throw new UnsupportedOperationException("Wrong state equivalence");
    }
    //endregion

    //region Private methods
    private void removeEventFromCurrentState(int compunentNumber, int event)
    {
        this.components[compunentNumber].states[this.currentStates[compunentNumber]] = EventState.removeEvent(this.components[compunentNumber].states[this.currentStates[compunentNumber]], event);
    }
    private void changeState(Integer componentNumber, EventState modelNewEventState, Integer nextViewState)
    {
        Integer nextKnowledgeState = null;
        for (int i = 0; i < this.stateEquivalence.get(componentNumber).size(); i++)
            if (this.stateEquivalence.get(componentNumber).get(i).getViewStateNumber().equals(nextViewState))
                nextKnowledgeState = this.stateEquivalence.get(componentNumber).get(i).getKnowledgeStateNumber();

        if (nextKnowledgeState != null)
            this.currentStates[componentNumber] = nextKnowledgeState;
        else
        {
            this.currentStates[componentNumber] = this.components[componentNumber].states.length;
            this.stateEquivalence.get(componentNumber).add(new StateEquivalence(nextViewState, this.currentStates[componentNumber]));
            this.addState(componentNumber, this.currentStates[componentNumber], modelNewEventState);
        }
    }
    private void addState(Integer componentNumber, int nextState, EventState modelNewEventState)
    {
        this.components[componentNumber].states = Arrays.copyOf(this.components[componentNumber].states, nextState + 1);
        this.components[componentNumber].maxStates = nextState + 1;
        this.components[componentNumber].states[nextState] = modelNewEventState;
    }
    private void conifirmAction(Integer componentNumber, int oldEvent, int newEvent, int fromState, int toState)
    {
        this.components[componentNumber].states[fromState].updateEventAndNext(oldEvent, newEvent, toState);
    }
    //endregion
}
