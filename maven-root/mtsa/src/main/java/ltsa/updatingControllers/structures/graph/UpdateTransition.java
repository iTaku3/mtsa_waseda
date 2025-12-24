package ltsa.updatingControllers.structures.graph;

import MTSSynthesis.ar.dc.uba.model.condition.Fluent;
import MTSTools.ac.ic.doc.mtstools.model.impl.MarkedMTS;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** Created by Victor Wjugow on 27/05/15. */
public class UpdateTransition {

  private MarkedMTS<Long, String> updateController;
  private List<Fluent> fluents;
  private Set<String> controllableActions;

  public MarkedMTS<Long, String> getUpdateController() {
    return updateController;
  }

  public void setUpdateController(MarkedMTS<Long, String> updateController) {
    this.updateController = updateController;
  }

  public ArrayList<Fluent> getFluents() {
    return (ArrayList<Fluent>) fluents;
  }

  public void setFluents(List<Fluent> fluents) {
    this.fluents = fluents;
  }

  public Set<String> getControllableActions() {
    return controllableActions;
  }

  public void setControllableActions(Set<String> controllableActions) {
    this.controllableActions = controllableActions;
  }
}
