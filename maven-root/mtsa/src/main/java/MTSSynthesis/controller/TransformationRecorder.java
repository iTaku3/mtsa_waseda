package MTSSynthesis.controller;

import MTSTools.ac.ic.doc.mtstools.model.MTS;
import ltsa.lts.CompactState;

import java.util.List;

/**
 * TransformationRecorder records mts transformations required to solve synthesis problems.
 * The records are returned in CompactState form.
 */
public interface TransformationRecorder {
    /**
     * record makes a copy of the received mts and stores it
     */
    <State,Action> void record(MTS<State,Action> mts, String name);

    /**
     * Retrieves the recorded states from oldest to newest
     */
    List<CompactState> getRecords();
}
