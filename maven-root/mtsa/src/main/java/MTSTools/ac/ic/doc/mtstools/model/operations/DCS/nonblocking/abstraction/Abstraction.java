package MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking.abstraction;

import MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking.Compostate;

/** Abstract class for abstractions. */
public abstract class Abstraction<State, Action> {
    public abstract void eval(Compostate<State, Action> compostate);
}