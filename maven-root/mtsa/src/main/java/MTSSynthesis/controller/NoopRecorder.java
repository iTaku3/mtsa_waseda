package MTSSynthesis.controller;

import MTSTools.ac.ic.doc.mtstools.model.MTS;
import ltsa.lts.CompactState;

import java.util.ArrayList;
import java.util.List;

public class NoopRecorder implements TransformationRecorder {
    public <State,Action> void record(MTS<State, Action> mts, String name) {
    }

    public List<CompactState> getRecords() {
        return new ArrayList<>();
    }
}
