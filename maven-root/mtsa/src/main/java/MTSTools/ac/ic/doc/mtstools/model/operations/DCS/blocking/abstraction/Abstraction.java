package MTSTools.ac.ic.doc.mtstools.model.operations.DCS.blocking.abstraction;

import MTSTools.ac.ic.doc.mtstools.model.operations.DCS.blocking.Compostate;

/**
 * Abstract class for abstractions.
 */
public interface Abstraction<State, Action> {
    void eval(Compostate<State, Action> compostate);
}
