package MTSTools.ac.ic.doc.mtstools.model.impl;

import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;

public class ProbabilisticTransition extends Transition<String>
{
  protected int bundle; //i.e. ID for a group of transitions that sum to 1
  protected double probability;
  
  public ProbabilisticTransition(Long to, String action, double probability, int bundle)
  {
    super(to, action, TransitionType.REQUIRED);
    this.probability = probability;
    this.bundle = bundle;
  }

  public double getProbability()
  {
    return probability;
  }

  public int getBundle()
  {
    return bundle;
  }

  public String toString()
  {
    return "To: "+getTo()+" Action: "+getAction()+" Prob: "+probability;
  }
}
