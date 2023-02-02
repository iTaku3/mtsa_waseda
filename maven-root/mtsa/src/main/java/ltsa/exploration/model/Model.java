package ltsa.exploration.model;

import ltsa.lts.CompactState;
import ltsa.lts.EventState;

public class Model
{
    private CompactState[] components;
    private Integer[] currentStates;

    //region Constructor
    public Model(CompactState[] components)
    {
        this.components = components;
        this.currentStates = new Integer[components.length];
        for (int i = 0; i < components.length; i++)
            this.currentStates[i] = 0;
    }
    //endregion

    //region Public methods
    public EventState execute(int componentNumber, String nextViewAction)
    {
        if (nextViewAction.equals("WAIT"))
            return EventState.copy(this.components[componentNumber].states[this.currentStates[componentNumber]]);

        String nextAction = nextViewAction;

        int event = -1;
        for (int i = 0; i < this.components[componentNumber].alphabet.length; i++)
            if (nextAction.equals(this.components[componentNumber].alphabet[i]))
                event = i;

        if (event > -1)
        {
            int[] events = this.components[componentNumber].states[this.currentStates[componentNumber]].getEvents();
            for (int anEvent : events)
            {
                if (event == anEvent)
                {
                    this.currentStates[componentNumber] = this.components[componentNumber].states[this.currentStates[componentNumber]].getNext(event);
                    return EventState.copy(this.components[componentNumber].states[this.currentStates[componentNumber]]);
                }
            }
        }

        nextAction = nextAction + "?";
        for (int i = 0; i < this.components[componentNumber].alphabet.length; i++)
            if (nextAction.equals(this.components[componentNumber].alphabet[i]))
                event = i;

        if (event > -1)
        {
            int[] events = this.components[componentNumber].states[this.currentStates[componentNumber]].getEvents();
            for (int anEvent : events)
            {
                if (event == anEvent)
                {
                    this.currentStates[componentNumber] = this.components[componentNumber].states[this.currentStates[componentNumber]].getNext(event);
                    return EventState.copy(this.components[componentNumber].states[this.currentStates[componentNumber]]);
                }
            }
        }

        throw new UnsupportedOperationException("Invalid action");
    }
    public void reset()
    {
        this.currentStates[0] = 0;
    }
    //endregion
}
