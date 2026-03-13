package MTSSynthesis.controller.model;

import MTSSynthesis.controller.gr.StrategyState;

import java.util.Queue;
import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


public class RankBasedGameSolverWorker<S, M> implements Runnable {

    private Queue<StrategyState<S, M>> pendingQueue;
    private RankBasedGameSolver<S, M> rankBasedGameSolver;
    private AtomicBoolean waitingElement;
    private Phaser allFinish;


    public RankBasedGameSolverWorker(Queue<StrategyState<S, M>> q, RankBasedGameSolver<S, M> rb, AtomicInteger r, AtomicBoolean we, Phaser af) {
        pendingQueue = q;
        rankBasedGameSolver = rb;
        allFinish = af;
        allFinish.register();
        waitingElement = we;
    }

    public RankBasedGameSolverWorker(Queue<StrategyState<S, M>> pending, RankBasedGameSolver<S, M> smRankBasedGameSolver) {
        pendingQueue = pending;
        rankBasedGameSolver = smRankBasedGameSolver;
    }

    public void run() {
        while (waitingElement.get()) {

            if (pendingQueue.isEmpty()) {
                allFinish.arriveAndDeregister();
                try {
                    synchronized (waitingElement) {
                        waitingElement.wait();
                    }

                } catch (InterruptedException e) {
                    return;
                }
                allFinish.register();
            } else {
                consume();

                synchronized (waitingElement) {
                    waitingElement.notifyAll();
                }
            }

        }
        allFinish.arriveAndDeregister();

    }


    public void consume() {
        StrategyState<S, M> state;

        state = pendingQueue.poll();


        if (state == null) {

            return;
        }


        Rank rank = rankBasedGameSolver.getRank(state);

        if (rank.isInfinity()) {
            return;
        }

        // If current rank is already infinity, it obviously should
        // not be increased.


        // What is the best possible ranking that s could have according to
        // it's successors?

        Rank bestRank = rankBasedGameSolver.best(state);

        // The existing ranking is already higher or equal then nothing needs to be
        // done. Go to the next state in the set of pending


        if (bestRank.compareTo(rankBasedGameSolver.getRank(state)) <= 0) {
            return;
        }

        // set the new ranking of the state to the computed best value
        // If the new value is infinity it can be set for all rankings.

        rankBasedGameSolver.updateRank(state, bestRank);

        rankBasedGameSolver.addPredecessorsTo(pendingQueue, state, bestRank);


    }

}