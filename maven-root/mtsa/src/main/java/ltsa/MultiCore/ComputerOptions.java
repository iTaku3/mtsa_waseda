package ltsa.MultiCore;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ONLY to share configuration settings across MTSA.
 * Singleton, do not manually instantiate, use "getInstance()".
 * Update variables values from HPWindow and LTSABatch.
 *
 * @allowedThreads int value to use concurrent threads.
 */

public class ComputerOptions {

    private static ComputerOptions instance = null;

    private int allowedThreads = Runtime.getRuntime().availableProcessors();

    public ConcurrentHashMap<Object, ReentrantLock> rankStatesLock = new ConcurrentHashMap<Object, ReentrantLock>();

    /**
     * Initialize default values for new variables.
     */
    private ComputerOptions() {
        this.setAllowedThreads(Runtime.getRuntime().availableProcessors());
    }

    public static ComputerOptions getInstance() {
        if (instance == null) {
            instance = new ComputerOptions();
        }
        return instance;
    }

    public int getAllowedThreads() {
        return allowedThreads;
    }


    public void setAllowedThreads(int allowedThreads) throws NumberFormatException {
        if (allowedThreads > 0)
            this.allowedThreads = allowedThreads;
    }

}