package MTSAEnactment.ar.uba.dc.lafhis.enactment;

import MTSTools.ac.ic.doc.mtstools.model.LTS;
import java.util.Set;

public abstract class ListeningStrategy<State, Action> {
  private String name;

  public String getName() {
    return name;
  }

  public ListeningStrategy(String name) {
    this.name = name;
  }

  abstract Set<String> getDispatchersForCurrentState(LTS<State, Action> mts);
}
