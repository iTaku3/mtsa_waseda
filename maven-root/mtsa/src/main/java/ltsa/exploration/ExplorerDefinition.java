package ltsa.exploration;

import ltsa.lts.Symbol;

import java.util.List;

public class ExplorerDefinition
{
    private Symbol name;
    private List<Symbol> view;
    private List<Symbol> model;
    private Symbol goal;
    private List<List<Symbol>> environmentActions;

    //region Constructor
    public ExplorerDefinition(Symbol name)
    {
        this.name = name;
    }
    //endregion

    //region Getters
    public String getName()
    {
        return this.name.toString();
    }
    public List<Symbol> getView()
    {
        return view;
    }
    public List<Symbol> getModel()
    {
        return model;
    }
    public Symbol getGoal()
    {
        return goal;
    }
    public List<List<Symbol>> getEnvironmentActions()
    {
        return this.environmentActions;
    }
    //endregion

    //region Setters
    public void setView(List<Symbol> view)
    {
        this.view = view;
    }
    public void setModel(List<Symbol> model)
    {
        this.model = model;
    }
    public void setGoal(List<Symbol> goal)
    {
        this.goal = goal.get(0);
    }
    public void setEnvironmentActions(List<List<Symbol>> environmentActions)
    {
        this.environmentActions = environmentActions;
    }
    //endregion

}
