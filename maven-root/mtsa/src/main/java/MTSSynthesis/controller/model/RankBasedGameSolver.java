package MTSSynthesis.controller.model;

import MTSSynthesis.ar.dc.uba.util.ConcurrentSetQueue;
import MTSSynthesis.controller.gr.StrategyState;
import ltsa.MultiCore.ComputerOptions;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class RankBasedGameSolver<S, M> implements GameSolver<S, M> {

    protected static final long TIME_TO_LOG = 60000;
    private boolean gameSolved;


    protected abstract void addPredecessorsTo(Queue<StrategyState<S, M>> pending, StrategyState<S, M> strategyState, Rank bestRank);

    protected abstract void updateRank(StrategyState<S, M> strategyState, Rank bestRank);

    protected abstract void initialise(Queue<StrategyState<S, M>> pending);

    protected abstract Rank best(StrategyState<S, M> strategyState);

    protected abstract Rank getRank(StrategyState<S, M> strategyState);

    protected abstract Set<S> getGameStates();

    public boolean isGameSolved() {
        return gameSolved;
    }


    // Testing quantity of processed states in MultiCore implementation
    AtomicInteger processed = new AtomicInteger(0);


    void buildLockSystem() {
        for (S state :
                this.getGame().getStates()) {
            ComputerOptions.getInstance().rankStatesLock.put(state, new ReentrantLock(true));
        }
    }

    @Override
    public void solveGame() {

        if (this.isGameSolved()) return;


        int nThreads = ComputerOptions.getInstance().getAllowedThreads();


        //Use in AbstractRankFunction to lock when writing new rank
        buildLockSystem();


        long initialTime = System.currentTimeMillis();
        //log(initialTime);


        Queue<StrategyState<S, M>> pending = new ConcurrentSetQueue<>(getGameStates().size(), nThreads);


        this.initialise(pending);

        synthesis(nThreads, pending);

        long finalTime = System.currentTimeMillis();

        long totalTime = finalTime - initialTime;

        log(totalTime);

    }

    private void synthesis(int nThreads, Queue<StrategyState<S, M>> pending) {
        if (nThreads > 1) {
            parallelRankingSolving(nThreads, pending);
        } else {
            sequentialRankingSolving(pending);
        }
    }


    private void parallelRankingSolving(Integer nThreads, Queue<StrategyState<S, M>> pending) {
        // If you would like to use a Future for the sake of cancellability but not provide a usable result,
        // you can declare types of the form Future<?> and return null as a result of the underlying task.
        AtomicInteger running = new AtomicInteger(nThreads);
        Phaser allFinish = new Phaser(1);
        AtomicBoolean waitingElement = new AtomicBoolean(true);

        ExecutorService taskPool = Executors.newFixedThreadPool(nThreads);
        Future[] futureArray = new Future[nThreads];


        for (int i = 0; i < nThreads; i++) {
            futureArray[i] = taskPool.submit(new RankBasedGameSolverWorker<S, M>(pending, this, running, waitingElement, allFinish));
        }


        taskPool.shutdown();


        allFinish.arriveAndAwaitAdvance();
        allFinish.forceTermination();
        gameSolved = true;

        //TODO: change when it timeout or not.

        for (int i = 0; i < nThreads; i++) {
            if (!futureArray[i].isDone()) {
                futureArray[i].cancel(true);
            }
        }

        while (!taskPool.isTerminated()) {
//            System.out.println("Aun no termino ... ");
        }

    }

    private void sequentialRankingSolving(Queue<StrategyState<S, M>> pending) {
        RankBasedGameSolverWorker<S, M> sequentialWorker = new RankBasedGameSolverWorker<S, M>(pending, this);

        while (!pending.isEmpty()) {
            sequentialWorker.consume();
        }

        gameSolved = true;
    }


    private long log(long time) {
            Logger.getAnonymousLogger().log(Level.FINER, "Synthesis time: " + time + " milliseconds.");
            return time;
    }

    /* (non-Javadoc)
     * @see controller.GameSolver#getWinningStates()
     */
    public Set<S> getWinningStates() {
        Set<S> winning = new HashSet<S>();
        if (isGameSolved()) {
            for (S state : this.getGameStates()) {
                if (isWinning(state)) {
                    winning.add(state);
                }
            }
        }
        return winning;

    }


}