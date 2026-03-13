package ltsa.exploration.knowledge;

public class StateEquivalence
{
    private Integer viewStateNumber;
    private Integer knowledgeStateNumber;

    //region Constructor
    public StateEquivalence(Integer viewStateNumber, Integer knowledgeStateNumber)
    {
        this.viewStateNumber = viewStateNumber;
        this.knowledgeStateNumber = knowledgeStateNumber;
    }
    //endregion

    //region Getters
    public Integer getViewStateNumber()
    {
        return viewStateNumber;
    }
    public Integer getKnowledgeStateNumber()
    {
        return knowledgeStateNumber;
    }
    //endregion
}
