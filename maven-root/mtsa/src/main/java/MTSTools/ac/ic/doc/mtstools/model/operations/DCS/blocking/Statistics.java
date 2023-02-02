package MTSTools.ac.ic.doc.mtstools.model.operations.DCS.blocking;

/** This class holds statistic information about the heuristic procedure. */
public class Statistics {

    /** Number of expanded states. */
    private int expandedStates; // expanded is usually smaller than the iterations due to the reopening of states, we could also report the number of iterations

    /** Number of states in the generated controller. */
    private int controllerUsedStates;

    /** Number of expanded transitions. */
    private int expandedTransitions;

    /** Number of transitions in the generated controller. */
    private int controllerUsedTransitions;

    /** System wall clock in milliseconds at the start of the procedure. */
    private long started;

    /** System wall clock in milliseconds at the end of the procedure. */
    private long ended;

    /** System wall clock in milliseconds at the start of the heuristic procedure. */
    private long started_heuristic;

    /** System wall clock in milliseconds at the end of the heuristic procedure. */
    private long ended_heuristic;

    /** Execution time in milliseconds of the heuristic procedure. */
    private long heuristic_time;

    /**metrics to detect how well compositional approach works for different test cases*/
    private long findNewGoalsCalls;
    private long findNewErrorsCalls;
    private long propagateErrorsCalls;
    private long propagateGoalsCalls;

    /** during toLive calls, retain the max value of getUsedMemory */
    private long maxMemoryUsed;

    private boolean isRunning = false;

    public void copyValues(Statistics other) {
        expandedStates = other.getExpandedStates();
        controllerUsedStates = other.controllerUsedStates;
        expandedTransitions = other.getExpandedTransitions();
        controllerUsedTransitions = other.controllerUsedTransitions;
        started = other.started;
        ended = other.ended;
        isRunning = other.isRunning;
        findNewGoalsCalls = other.findNewGoalsCalls;
        findNewErrorsCalls = other.findNewErrorsCalls;
        propagateGoalsCalls = other.propagateGoalsCalls;
        propagateErrorsCalls = other.propagateErrorsCalls;
        maxMemoryUsed = other.maxMemoryUsed;
        heuristic_time = other.heuristic_time;
    }

    public void incFindNewGoalsCalls() {
        findNewGoalsCalls++;
    }
    public long getFindNewGoalsCalls() {return findNewGoalsCalls;}
    public void incFindNewErrorsCalls() {
        findNewErrorsCalls++;
    }
    public long getFindNewErrorsCalls() {return findNewErrorsCalls;}
    public void incPropagateGoalsCalls() {
        propagateGoalsCalls++;
    }
    public long getPropagateGoalsCalls() {return propagateGoalsCalls;}
    public void incPropagateErrorsCalls() {
        propagateErrorsCalls++;
    }
    public long getPropagateErrorsCalls() {return propagateErrorsCalls;}


    /** Increments the number of expended states. */
    public void incExpandedStates() {
        expandedStates++;
    }

    /** Increments the number of expended transitions. */
    public void incExpandedTransitions() {
        expandedTransitions++;
    }

    /** Sets the number of states used in the generated controller. */
    public void setControllerUsedStates(int controllerUsedStates) {
        this.controllerUsedStates = controllerUsedStates;
    }

    /** Sets the number of transitions used in the generated controller. */
    public void setControllerUsedTransitions(int controllerUsedTransitions) {
        this.controllerUsedTransitions = controllerUsedTransitions;
        this.toLive();
    }

    /** Marks the start of the procedure to measure elapsed time. */
    public void start() {
        isRunning = true;
        started = System.currentTimeMillis();
    }


    /** Marks the end of the procedure to measure elapsed time. */
    public void end() {
        isRunning = false;
        ended = System.currentTimeMillis();
    }


    /** Indicates if the heuristic procedure is running. */
    public boolean isRunning() {
        return isRunning;
    }

    public void startHeuristicTime() {
        started_heuristic = System.currentTimeMillis();
    }

    public void endHeuristicTime() {
        heuristic_time += System.currentTimeMillis() - started_heuristic;
    }


    /** Returns the number of expanded states (so far). */
    public int getExpandedStates() {
        return expandedStates;
    }

    /** Returns the number of expanded transitions (so far). */
    public int getExpandedTransitions() {
        return expandedTransitions;
    }

    /** Returns the elapsed time from the start of the execution up to now. */
    public long getElapsed() {
        return System.currentTimeMillis() - started;
    }


    /** Returns an approximation of the memory used by the procedure. */
    public long getUsedMemory() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory() /*- base*/;
    }


    /** Resets the statistics. */
    public void clear() {
        expandedStates = 0;
        controllerUsedStates = 0;
        expandedTransitions = 0;
        controllerUsedTransitions = 0;
        started = 0;
        ended = 0;
        findNewGoalsCalls = 0;
        findNewErrorsCalls = 0;
        propagateGoalsCalls = 0;
        propagateErrorsCalls = 0;
        maxMemoryUsed = 0;
        heuristic_time = 0;
    }


    /** Returns a string with the statistic data. */
    @Override
    public String toString() {
        return  "ExpandedStates: " + expandedStates + "\n" +
                "UsedStates: " + controllerUsedStates + "\n" +
                "ExpandedTransitions: " + expandedTransitions + "\n" +
                "UsedTransitions: " + controllerUsedTransitions + "\n" +
                "Elapsed in Synthesis: " + (ended - started) + " ms\n" +
                "findNewGoalsCalls: " + findNewGoalsCalls + ", findNewErrorsCalls: " + findNewErrorsCalls + "\n" +
                "propagateGoalsCalls: " + propagateGoalsCalls + ", propagateErrorsCalls: " + propagateErrorsCalls + "\n" +
                "maxMemoryUsed: " + formatMemory(maxMemoryUsed) + "\n" +
                "heuristicTime: " + heuristic_time + " ms\n";
    }


    /** Returns a string with live statistics. */
    public String toLive() {
        long mem = getUsedMemory();
        if (mem > maxMemoryUsed) maxMemoryUsed = mem;
        return  "  " + expandedStates + " states expanded " +
                " (" + formatTime(getElapsed()) + ", " + formatMemory(mem) + ")";
    }


    /** Returns tab separated values with statistic data (expanded, evaluated, revisited, used, elapsed).*/
    public String toTSV() {
        return controllerUsedStates + "\t" + (ended - started) + "\n";
    }


    /** Returns time in human readable format. */
    private String formatTime(long time) {
        long sec = 1000;
        long min = 60 * sec;
        long hour = 60 * min;
        String result = "";
        String space = "";
        if (time > hour) {
            result += (time / hour) + " h";
            time %= hour;
            space = " ";
        }
        if (time > min) {
            result += space + (time / min) + " min";
            time %= min;
            space = " ";
        }
        if (time > sec) {
            result += space + (time / sec) + " s";
            time %= sec;
        }
        return result;
    }


    /** Returns memory in human readable format. */
    private String formatMemory(long memory) {
        double kilo = 1024;
        double mega = 1024 * kilo;
        double giga = 1024 * mega;
        String result;
        if (memory > giga)
            result = String.format("%02.2f", memory / giga) + " GB";
        else if (memory > mega)
            result = String.format("%02.2f",memory / mega) + " MB";
        else if (memory > kilo)
            result = String.format("%02.2f",memory / kilo) + " KB";
        else
            result = memory + " Bs";
        return result;
    }

}
