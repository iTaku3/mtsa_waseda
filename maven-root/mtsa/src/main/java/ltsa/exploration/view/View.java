package ltsa.exploration.view;

import MTSTools.ac.ic.doc.commons.relations.Pair;
import com.google.common.primitives.Ints;
import ltsa.lts.CompactState;

import java.util.ArrayList;
import java.util.HashSet;

public class View
{
    private CompactState[] components;
    private int[] currentStates;
    private ViewNextConfiguration[] nextConfigurations;

    //region Constructor
    public View(CompactState[] components, ViewNextConfiguration[] nextConfigurations)
    {
        this.components = components;
        this.nextConfigurations = nextConfigurations;

        this.currentStates = new int[components.length];
        for (int i = 0; i < components.length; i++)
            this.currentStates[i] = 0;
    }
    //endregion

    //region Getters
    public CompactState[] getComponents()
    {
        return this.components;
    }
    public int[] getCurrentStates()
    {
        return this.currentStates;
    }
    //endregion

    //region Public methods
    public HashSet<String> getCurrentStateActions()
    {
        HashSet<String> currentStateActions = this.getCurrentStateActions(0);
        for (int i = 1; i < this.components.length; i++)
            for (String anAction : this.getCurrentStateActions(i))
                currentStateActions.add(anAction);

        return currentStateActions;
    }
    public HashSet<String> getCurrentStateAvailableActions(int component)
    {
        HashSet<String> currentStateActions = this.getCurrentStateActions(component);

        HashSet<String> lockedActions = new HashSet<>();
        for (String anAction : currentStateActions)
            for (int i = 0; i < components.length; i++)
                if (i != component)
                    for (int j = 0; j < components[i].alphabet.length; j++)
                        if (anAction.equals(components[i].alphabet[j]))
                            if (!Ints.contains(components[i].states[this.currentStates[i]].getEvents(), j))
                                lockedActions.add(anAction);

        HashSet<String> currentStateAvaibleActions = new HashSet<>();
        for (String anAction : currentStateActions)
            if (!lockedActions.contains(anAction))
                currentStateAvaibleActions.add(anAction);

        return currentStateAvaibleActions;
    }
    public Pair<Integer, HashSet<String>> executeControllable(String nextAction, int componentNumber)
    {
        if (!nextAction.equals("WAIT"))
            if (this.moveComponent(componentNumber, nextAction))
                return new Pair<>(currentStates[componentNumber], this.getCurrentStateActions(componentNumber));

        return null;
    }
    public Pair<String, Pair<Integer, HashSet<String>>> executeUncontrollable(int componentNumber)
    {
        String nextUncontrollableAction = this.nextConfigurations[componentNumber].nextAction(new ArrayList<>(this.getCurrentStateAvailableActions(componentNumber)));
        this.moveComponent(componentNumber, nextUncontrollableAction);
        return new Pair<>(nextUncontrollableAction, new Pair<>(currentStates[componentNumber], this.getCurrentStateActions(componentNumber)));
    }
    public void reset()
    {
        this.currentStates[0] = 0;
    }
    //endregion

    //region Private methods
    private HashSet<String> getCurrentStateActions(int component)
    {
        int[] currentStateEvents = this.components[component].states[this.currentStates[component]].getEvents();
        HashSet<String> currentStateActions = new HashSet<>(0);
        for (int currentStateEvent : currentStateEvents)
            currentStateActions.add(this.components[component].alphabet[currentStateEvent]);
        return currentStateActions;
    }
    private Boolean moveComponent(Integer component, String nextAction)
    {
        Boolean isInAlphabet = false;
        for (String anAction : this.components[component].alphabet)
            if (anAction.equals(nextAction))
                isInAlphabet = true;

        if (!isInAlphabet)
            return false;

        int nextEvent = this.components[component].getEvent(nextAction);

        Boolean isInState = false;
        for (Integer anEvent : this.components[component].states[this.currentStates[component]].getEvents())
            if (anEvent == nextEvent)
                isInState = true;

        if (!isInState)
            return false;

        int nextState = this.components[component].states[this.currentStates[component]].getNext(nextEvent);
        this.currentStates[component] = nextState;

        return true;
    }
    //endregion

}
