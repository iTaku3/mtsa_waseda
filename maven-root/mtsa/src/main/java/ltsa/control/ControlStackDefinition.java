package ltsa.control;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import ltsa.lts.Diagnostics;
import ltsa.lts.LabelSet;
import ltsa.lts.Symbol;

public class ControlStackDefinition implements Iterable<ControlTierDefinition>
{
  //static stuff:
  
  private static Hashtable<String,ControlStackDefinition> controlStacks = new Hashtable<String,ControlStackDefinition>();
  
  public static void initDefinitionList()
  {
    controlStacks = new Hashtable<String,ControlStackDefinition>();
  }
  
  public static void addDefinition(ControlStackDefinition def)
  {
    if (controlStacks.get(def.name.toString()) != null)
      Diagnostics.fatal("Duplicate control stack definition: "+def.name, def.name);
    
    controlStacks.put(def.name.toString(), def);
  }
  
  public static ControlStackDefinition getDefinition(String name)
  {
    if (name == null)
      throw new IllegalArgumentException("Missing name in getDefinition()");
    
    ControlStackDefinition def = controlStacks.get(name);
    if (def == null)
      throw new IllegalArgumentException("Control stack definition not found: " + name);
    
    return def;
  }
  
  //non static stuff:
  
  private final Symbol name;
  private final Symbol controllableActionSet;
  private final List<ControlTierDefinition> tiers = new ArrayList<ControlTierDefinition>();
  
  public ControlStackDefinition(Symbol name, Symbol controls)
  {
    this.name = name;
    this.controllableActionSet = controls;
  }

  public Symbol getName()
  {
    return name;
  }

  public Symbol getControllableActionSet()
  {
    return controllableActionSet;
  }

  public List<String> getControllableActions()
  {
    LabelSet labelSet = (LabelSet) LabelSet.getConstants().get(controllableActionSet.toString());
    return labelSet.getActions(null);
  }
  
  public List<ControlTierDefinition> getTiers()
  {
    return tiers;
  }
  
  public void addTier(ControlTierDefinition tier)
  {
    tiers.add(tier);
  }
  
  public Iterator<ControlTierDefinition> iterator()
  {
    return tiers.iterator();
  }
  
  public int tierCount()
  {
    return tiers.size();
  }
}
