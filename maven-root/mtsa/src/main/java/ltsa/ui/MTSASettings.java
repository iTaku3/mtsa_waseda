package ltsa.ui;

/**
 * ONLY to share configuration settings across MTSA.
 * Singleton, do not manually instantiate, use "getInstance()".
 * Do not use "getters and setters" use public variables.
 * Update variables values from HPWindow and LTSABatch.
 * @threadCountGameSolver int value to use multiple threads when calculating game rank
 */

public class MTSASettings {

    private static MTSASettings instance = null;

    public int threadCountGameSolver;

    /**
     * Set default values for new variables.
     */
    private MTSASettings() {
        threadCountGameSolver = 1;
    }

    public static MTSASettings getInstance() {
        if(instance == null) {
            instance = new MTSASettings();
        }
        return instance;
    }

}