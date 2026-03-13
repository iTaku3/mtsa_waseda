package MTSSynthesis.controller.model.rtc;

import MTSSynthesis.controller.TransformationRecorder;
import MTSSynthesis.controller.gr.StrategyState;
import MTSSynthesis.controller.model.ControllerGoal;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSTools.ac.ic.doc.mtstools.model.impl.MTSAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * removeYields transforms the strategy LTS to the RTC solution. It removes the environment yields and controller
 * yields actions and adds the required transitions.
 */
public class YieldRemoval<A> {
    private final ControllerGoal<A> goal;
    private final A envYieldsAction;
    private final A contYieldsAction;

    public YieldRemoval(ControllerGoal<A> goal, A envYieldsAction, A contYieldsAction) {
        this.goal = goal;
        this.envYieldsAction = envYieldsAction;
        this.contYieldsAction = contYieldsAction;
    }

    /**
     * removeYields transforms the strategy LTS to the RTC solution. It removes the environment yields and controller
     * yields actions and adds the required transitions.
     */
    public <S> LTS<S, A> removeYields(LTS<S, A> result) {

        List<Runnable> toRemove = new ArrayList<Runnable>();
        List<Runnable> newTransitions = new ArrayList<>();

        result.getStates().forEach((s1) -> {

            // states that succeed s1 with eY or cY
            Set<S> envYieldedStates = result
                    .getTransitions(s1).stream().filter(t -> t.getFirst().equals(envYieldsAction))
                    .map(Pair::getSecond).collect(Collectors.toSet());
            Set<S> contYieldedStates = result
                    .getTransitions(s1).stream().filter(t -> t.getFirst().equals(contYieldsAction))
                    .map(Pair::getSecond).collect(Collectors.toSet());

            // (s1 -> uncontrollable -> s2) should exist only iif
            // (s1 -> uncontrollable -> s2) and [for every s3 (s1 -> eY -> s3) implies that (s3 -|-> cY)]
            // or (s1 -> eY -> s3 -> cY -> s1 -> uncontrollable -> s2)
            // That is, there is no controller yielding done after an environment yielding
            // This checks the first condition and removes uncontrollable actions that don't match.
            // They are readded later if the second condition matches.
            Set<Pair<A, S>> uncontrollableTransitions =
                    result.getTransitions(s1).stream().filter(t -> !t.getFirst().equals(envYieldsAction)
                            && !t.getFirst().equals(contYieldsAction)
                            && !this.goal.getControllableActions().contains(t.getFirst()))
                            .collect(Collectors.toSet());
            if (uncontrollableTransitions.size() > 0 &&
                    envYieldedStates.stream().anyMatch((s3) ->
                            result.getTransitions(s3).stream().anyMatch((t) -> t.getFirst().equals(contYieldsAction))
                    )) {
                uncontrollableTransitions
                        .forEach((t) -> toRemove.add(() ->
                                result.removeTransition(s1, t.getFirst(), t.getSecond())));
            }

            // states that succeed s1 eY successors through a Yc action and then an uncontrollable action
            // (s1 -> eY -> s2 -> cY -> s3 -> uncontrollable -> s4) is transformed to (s1 -> uncontrollable -> s4)
            envYieldedStates.forEach((s2) -> result.getTransitions(s2).stream()
                    .filter(t -> t.getFirst().equals(contYieldsAction))
                    .map(Pair::getSecond)
                    .flatMap((t) -> result.getTransitions(t).stream())
                    .filter(t -> !t.getFirst().equals(envYieldsAction)
                            && !t.getFirst().equals(contYieldsAction)
                            && !this.goal.getControllableActions().contains(t.getFirst()))
                    .forEach(t -> newTransitions.add(() -> result.addTransition(s1, t.getFirst(), t.getSecond())))
            );

            // states that succeed s1 eY successors through a controllable action
            // (s1 -> ey -> s2 -> controllable -> s3) is transformed to (s1 -> controllable -> s3)
            envYieldedStates.forEach((s2) -> result.getTransitions(s2).stream()
                    .filter(t -> !t.getFirst().equals(envYieldsAction)
                            && !t.getFirst().equals(contYieldsAction)
                            && this.goal.getControllableActions().contains(t.getFirst()))
                    .forEach(t -> newTransitions.add(() -> result.addTransition(s1, t.getFirst(), t.getSecond()))));

            // states that succeed s1 cY successors through an uncontrollable action
            // (s1 -> cY -> s2 -> uncontrollable -> s3) is transformed to (s1 -> uncontrollable -> s3)
            contYieldedStates.forEach((s2) -> result.getTransitions(s2).stream()
                    .filter(t -> !t.getFirst().equals(envYieldsAction)
                            && !t.getFirst().equals(contYieldsAction)
                            && !this.goal.getControllableActions().contains(t.getFirst()))
                    .forEach(t -> newTransitions.add(() -> result.addTransition(s1, t.getFirst(), t.getSecond()))));
        });

        // We remove all envYields and contYields transitions
        result.getTransitions().forEach((from, transitions) -> transitions.stream()
                .filter(t -> t.getFirst().equals(envYieldsAction) || t.getFirst().equals(contYieldsAction))
                .forEach((t) -> toRemove.add(() -> result.removeTransition(from, t.getFirst(), t.getSecond()))));

        toRemove.forEach(Runnable::run);
        result.removeAction(envYieldsAction);
        result.removeAction(contYieldsAction);
        newTransitions.forEach(Runnable::run);
        return result;
    }
}