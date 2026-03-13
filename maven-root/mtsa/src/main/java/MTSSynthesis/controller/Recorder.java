package MTSSynthesis.controller;

import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.utils.GenericMTSToLongStringMTSConverter;
import ltsa.ac.ic.doc.mtstools.util.fsp.MTSToAutomataConverter;
import ltsa.lts.CompactState;

import java.util.ArrayList;
import java.util.List;

public class Recorder implements TransformationRecorder {

    private final List<CompactState> records;

    public Recorder() {
        this.records = new ArrayList<>();
    }

    public <State,Action> void record(MTS<State, Action> mts, String name) {
        CompactState record = MTSToAutomataConverter.getInstance()
                .convert(new GenericMTSToLongStringMTSConverter<State, Action>().transform(mts), name);
        records.add(record.myclone());
    }

    public List<CompactState> getRecords() {
        return records;
    }
}
