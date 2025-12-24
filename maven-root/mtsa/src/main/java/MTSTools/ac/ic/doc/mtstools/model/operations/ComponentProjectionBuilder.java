package MTSTools.ac.ic.doc.mtstools.model.operations;

import MTSTools.ac.ic.doc.mtstools.model.MTS;
import java.util.Set;

/**
 * @author gsibay
 */
public interface ComponentProjectionBuilder {

  /**
   * Builds the component with alphabet componentActions from the centralisedMTS
   *
   * @param centralisedMTS
   * @param componentActions
   * @return
   */
  public abstract MTS<Long, String> buildComponentProjection(
      MTS<Long, String> centralisedMTS, Set<String> componentActions);
}
