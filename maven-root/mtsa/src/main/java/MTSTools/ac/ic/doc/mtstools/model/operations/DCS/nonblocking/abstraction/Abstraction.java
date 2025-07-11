package MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking.abstraction;

import java.util.Set;
import java.util.List;

import MTSTools.ac.ic.doc.mtstools.model.operations.DCS.nonblocking.Compostate;

/** Abstract class for abstractions. */
public abstract class Abstraction<State, Action> {
    public abstract void eval(Compostate<State, Action> compostate, List<Set<State>> knownMarked, List<Set<State>> goals, List<String> comparison);
}