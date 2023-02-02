package MTSSynthesis.controller.model;


import MTSSynthesis.controller.gr.StrategyState;

import java.util.concurrent.BlockingQueue;


class Producer implements Runnable {
    private final BlockingQueue pendingQueue;
    private final BlockingQueue updateStatesQueue;
    private final RankBasedGameSolver rankBasedGameSolver;
    private Boolean running;

    Producer(BlockingQueue q, BlockingQueue u, RankBasedGameSolver rb, Boolean r) {
        pendingQueue = q;
        updateStatesQueue = u;
        rankBasedGameSolver = rb;
        running = r;
    }

    public void run() {
        try {
            while (running) {
                StrategyState state = (StrategyState) updateStatesQueue.take();
                Rank bestRank = rankBasedGameSolver.best(state);
                produce(pendingQueue, state, bestRank);
            }
           /* Rank infinit = null;
            infinit.setToInfinity();
            pendingQueue.put(infinit); //agrego simbolo de EOQ
           */
            stopRun();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void produce(BlockingQueue pendingQueue, StrategyState state, Rank bestRank) {
//        System.out.println("Estoy pasando por el producir");
        rankBasedGameSolver.addPredecessorsTo(pendingQueue, state, bestRank);
        // pendingQueue.put(produce(i));
    }

    private void stopRun() {
        while (!running) {
            Thread.currentThread().interrupt();
            return;
        }
    }
}