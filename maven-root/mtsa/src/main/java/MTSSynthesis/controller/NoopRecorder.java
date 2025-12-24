package MTSSynthesis.controller;

import MTSTools.ac.ic.doc.mtstools.model.MTS;
import java.util.ArrayList;
import java.util.List;
import ltsa.lts.CompactState;

public class NoopRecorder implements TransformationRecorder {
  public <State, Action> void record(MTS<State, Action> mts, String name) {}

  public List<CompactState> getRecords() {
    return new ArrayList<>();
  }
}
