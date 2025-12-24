package MTSTools.ac.ic.doc.mtstools.model.impl;

import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;
import java.util.List;
import java.util.Vector;

public interface TransitionRulesApplier {

  public abstract TransitionType applyCompositionRules(
      Pair<Long, TransitionType> transitionOnActualAction,
      Pair<Vector<Long>, TransitionType> acumulatedState);

  public abstract TransitionType applyCompositionRules(TransitionType transitionType);

  public abstract <State, Action> boolean composableModels(List<MTS<State, Action>> mtss);

  public abstract <State, Action> boolean composableStates(
      List<MTS<State, Action>> mtss, CompositionState state);

  public abstract <State, Action> void cleanUp(MTS<State, Action> mts);
}
