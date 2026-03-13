package ltsa.exploration;

import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSSynthesis.controller.model.ControllerGoal;
import ltsa.exploration.knowledge.Knowledge;
import ltsa.exploration.model.Model;
import ltsa.exploration.strategy.Strategy;
import ltsa.exploration.strategy.StrategyManager;
import ltsa.exploration.view.View;
import ltsa.lts.EventState;
import org.apache.commons.lang.ArrayUtils;

import java.util.ArrayList;
import java.util.HashSet;

public class Explorer
{
    private View view;
    private Model model;
    private Knowledge knowledge;
    private ControllerGoal<String> goal;
    private ArrayList<String> traceLastActions, traceLastStates;
    private StrategyManager strategyManager;
    private ArrayList<Integer> traceStatesCurrentStrategy;

    //region Constructor
    public Explorer(View view, Model model, Knowledge knowledge, ControllerGoal<String> goal, StrategyManager strategyManager)
    {
        this.goal = goal;
        this.view = view;
        this.model = model;
        this.knowledge = knowledge;
        this.traceLastStates = new ArrayList<>();
        this.traceLastActions = new ArrayList<>();
        this.strategyManager = strategyManager;
        this.traceStatesCurrentStrategy = new ArrayList<>();
        this.updateKnowledgeFromCurrentStateActions(0, this.view.getCurrentStateActions());
    }
    //endregion

    //region Getters
    public ArrayList<String> getTraceLastStates()
    {
        return this.traceLastStates;
    }
    public ArrayList<String> getTraceLastActions()
    {
        return this.traceLastActions;
    }
    //endregion

    //region Public methods
    public String getMTSControlProblemAnswer()
    {
    	Synthesis synthesis = new Synthesis(this.knowledge.cloneForSynthesisFromCurrentState(), this.goal.copy());
        String mtsControlProblemAnswer = synthesis.getMTSControlProblemAnswer();
        if (mtsControlProblemAnswer.equals("NONE"))
        {
        	synthesis = new Synthesis(this.knowledge.cloneForSynthesisFromStart(), this.goal.copy());
            mtsControlProblemAnswer = synthesis.getMTSControlProblemAnswer();
            if (!mtsControlProblemAnswer.equals("NONE"))
                return "RESET";
        }

        return mtsControlProblemAnswer;
    }
    public void explore()
    {
        if (this.strategyManager.inLoop(this.traceStatesCurrentStrategy))
            this.traceStatesCurrentStrategy = new ArrayList<>();

        Strategy currentStrategy = this.strategyManager.getCurrentStrategy();
        HashSet<String> currentStateAvailableActions = this.view.getCurrentStateAvailableActions(0);
        String nextAction = currentStrategy.chooseNextAction(currentStateAvailableActions);
        this.execute(nextAction);
    }
    public void explore(String nextAction)
    {
        this.execute(nextAction);
    }
    public String[] getAviableActions()
    {
        HashSet<String> currentStateAvailableControllableActions = this.view.getCurrentStateAvailableActions(0);
        String[] array = new String[currentStateAvailableControllableActions.size()];
        array = currentStateAvailableControllableActions.toArray(array);
        return array;
    }
    public void reset()
    {
        this.cleanLastTraces();

        this.view.reset();
        this.model.reset();
        this.knowledge.reset();

        this.updateTracesControllable("RESET", this.knowledge.getCurrentStates()[0]);
    }
    public int[] getCurrentStateNumbers()
    {
        return ArrayUtils.addAll(this.view.getCurrentStates(), this.knowledge.getCurrentStates());
    }
    //endregion

    //region Private methods
    private void execute(String anAction)
    {
        this.cleanLastTraces();

        // Controllable action execution
        for (int i = 0; i < this.view.getComponents().length; i++)
        {
            Pair<Integer, HashSet<String>> viewNextState = this.view.executeControllable(anAction, i);
            if (viewNextState != null)
                this.executeInModelAndKnowledge(i, anAction, viewNextState.getFirst(), viewNextState.getSecond());
            if (i == 0)
                this.updateTracesControllable(anAction, this.knowledge.getCurrentStates()[i]);
        }

        // Uncontrollable actions execution
        for (int i = 1; i < this.view.getComponents().length; i++)
        {
            Pair<String, Pair<Integer, HashSet<String>>> viewComponentNextState = this.view.executeUncontrollable(i);

            String nextAction = viewComponentNextState.getFirst();
            Integer nextViewState = viewComponentNextState.getSecond().getFirst();
            HashSet<String> stateActions = viewComponentNextState.getSecond().getSecond();
            executeInModelAndKnowledge(i, nextAction, nextViewState, stateActions);
            this.updateTracesUncontrollable(nextAction);
        }
    }
    private void executeInModelAndKnowledge(Integer componentNumber, String nextAction, Integer nextViewState, HashSet<String> stateActions)
    {
        EventState nextModelState = this.model.execute(componentNumber, nextAction);
        this.knowledge.execute(componentNumber, nextAction, nextViewState, nextModelState);
        this.updateKnowledgeFromCurrentStateActions(componentNumber, stateActions);
    }
    private void updateTracesControllable(String nextAction, int nextState)
    {
        this.traceLastActions.add(nextAction);
        this.traceLastStates.add(String.valueOf(nextState));

        if (!nextAction.equals("WAIT"))
            this.traceStatesCurrentStrategy.add(nextState);
    }
    private void updateTracesUncontrollable(String nextAction)
    {
        this.traceLastActions.add(nextAction);
        this.traceLastStates.add("");
    }
    private void updateKnowledgeFromCurrentStateActions(Integer componentNumber, HashSet<String> stateActions)
    {
        this.knowledge.updateKnowledgeFromCurrentStateActions(componentNumber, stateActions);
    }
    private void cleanLastTraces()
    {
        this.traceLastStates = new ArrayList<>();
        this.traceLastActions = new ArrayList<>();
    }
    //endregion
}
