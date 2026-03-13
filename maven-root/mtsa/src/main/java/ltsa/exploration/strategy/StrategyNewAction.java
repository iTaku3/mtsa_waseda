package ltsa.exploration.strategy;

import MTSSynthesis.ar.dc.uba.model.condition.Fluent;
import MTSSynthesis.ar.dc.uba.model.condition.FluentImpl;
import MTSSynthesis.ar.dc.uba.model.condition.FluentPropositionalVariable;
import MTSSynthesis.ar.dc.uba.model.language.SingleSymbol;
import MTSSynthesis.ar.dc.uba.model.language.Symbol;
import MTSSynthesis.controller.model.ControllerGoal;
import ltsa.exploration.Synthesis;
import ltsa.exploration.knowledge.Knowledge;
import ltsa.lts.CompactState;
import ltsa.lts.EventState;

import java.util.*;

public class StrategyNewAction extends Strategy
{
    private Knowledge knowledge;
    private ControllerGoal<String> goal;

    //region Constructor
    public StrategyNewAction(Knowledge knowledge, ControllerGoal<String> goal)
    {
        this.knowledge = knowledge;
        this.goal = goal;
    }
    //endregion

    //region Overrides
    @Override
    public String chooseNextAction(HashSet<String> availableActions)
    {
        // The new action is in the current state
        String[] alphabet = this.knowledge.getCmponents()[0].alphabet;
        for (int anEvent : this.knowledge.getCmponents()[0].states[this.knowledge.getCurrentStates()[0]].getEvents())
            for (String anAvailableAction : availableActions)
                if (alphabet[anEvent].equals(anAvailableAction + '?'))
                    return anAvailableAction;

        // The new action is in another state
        return getControllerNextAction(availableActions);
    }
    //endregion

    //region Private methods
    private Boolean fullyExplored(EventState aState, Boolean onlyControllable)
    {
        for (int anEvent : aState.getEvents())
            if (isPossibleAction(anEvent, onlyControllable))
                return false;

        return true;
    }
    private Boolean isPossibleAction(int i, Boolean onlyControllable)
    {
        String action = this.knowledge.getCmponents()[0].alphabet[i];

        if (onlyControllable)
            for (int componentNumber = 1; componentNumber < this.knowledge.getCmponents().length; componentNumber++)
                for (String anAction : this.knowledge.getCmponents()[componentNumber].alphabet)
                    if (Objects.equals(action, anAction))
                        return false;

        return !action.contains("tau") && action.contains("?");
    }
    private String getControllerNextAction(HashSet<String> availableActions)
    {
        CompactState[] machines = this.knowledge.cloneForSynthesisFromCurrentState();
        int stateZero = this.knowledge.getCurrentStates()[0];

        // To controllable action
        if (!this.fullyExplored(machines[0].states[stateZero], true))
        {
            String controllerAction = getActionFromController(machines[0], stateZero, availableActions);
            if (!controllerAction.equals("WAIT"))
                return controllerAction;
        }
        for (int i = 1; i < machines[0].states.length; i++)
        {
            if (!this.fullyExplored(machines[0].states[i], true))
            {
                String controllerAction = getActionFromController(machines[0], i, availableActions);
                 if (!controllerAction.equals("WAIT"))
                    return controllerAction;
            }
        }

        // To shared action
        if (!this.fullyExplored(machines[0].states[stateZero], false))
        {
            String controllerAction = getActionFromController(machines[0], stateZero, availableActions);
            if (!controllerAction.equals("WAIT"))
                return controllerAction;
        }
        for (int i = 1; i < machines[0].states.length; i++)
        {
            if (!this.fullyExplored(machines[0].states[i], false))
            {
                String controllerAction = getActionFromController(machines[0], i, availableActions);
                if (!controllerAction.equals("WAIT"))
                    return controllerAction;
            }
        }

        return "WAIT";
    }
    private CompactState getControllerToState(CompactState machine, int stateNumber)
    {
        // Controlable actions
        ArrayList<String> controllableActions = new ArrayList<>();
        for (String anAction : goal.getControllableActions())
            controllableActions.add(anAction);

        Set<String> goalControllableActions = new HashSet<>();
        goalControllableActions.add("fluent_on");
        for (String anAction : controllableActions)
            goalControllableActions.add(anAction);

        // Fluent
        Set<Symbol> on = new HashSet<>();
        on.add(new SingleSymbol("fluent_on"));

        Set<Symbol> off = new HashSet<>();
        for (String anAction : goalControllableActions)
            if (!anAction.equals("fluent_on"))
                off.add(new SingleSymbol(anAction));

        Fluent fluent = new FluentImpl("F_end", on, off, false);
        Set<Fluent> fluents = new HashSet<>();
        fluents.add(fluent);

        // Formula
        FluentPropositionalVariable formula = new FluentPropositionalVariable(fluent);

        // Goal
        ControllerGoal<String> goal = new ControllerGoal<>();
        goal.addAllFluents(fluents);
        goal.addGuarantee(formula);
        goal.addAllControllableActions(goalControllableActions);

        // Machine
        CompactState compositionMachine = machine.myclone();
        compositionMachine.alphabet = Arrays.copyOf(compositionMachine.alphabet, compositionMachine.alphabet.length + 2);
        compositionMachine.alphabet[compositionMachine.alphabet.length - 2] = "fluent_on?";
        compositionMachine.alphabet[compositionMachine.alphabet.length - 1] = "fluent_on";
        compositionMachine.states[stateNumber] = EventState.addEvent(compositionMachine.states[stateNumber], compositionMachine.alphabet.length - 1, stateNumber);

        // Transform MTS to LTS
        for (int j = 0; j < compositionMachine.states.length; j++)
            for (int k = 0; k < machine.alphabet.length; k++)
                if (machine.alphabet[k].contains("tau") || machine.alphabet[k].contains("?"))
                    compositionMachine.states[j] = EventState.removeEvent(compositionMachine.states[j], k);

        // Sinthesis
        CompactState[] compositionMachineArray = new CompactState[1];
        compositionMachineArray[0] = compositionMachine;
        Synthesis synthesis = new Synthesis(compositionMachineArray, goal);
        return synthesis.getComposition().composition;
    }
    private String getActionFromController(CompactState machine, int stateNumber, HashSet<String> availableActions)
    {
        CompactState controllerToNextUnexploredState = getControllerToState(machine, stateNumber);
        if (controllerToNextUnexploredState.getMtsControlProblemAnswer().equals("ALL"))
            for (int anEvent : controllerToNextUnexploredState.states[0].getEvents())
                if (availableActions.contains(controllerToNextUnexploredState.alphabet[anEvent]))
                    return controllerToNextUnexploredState.alphabet[anEvent];
        return "WAIT";
    }
    //endregion
}