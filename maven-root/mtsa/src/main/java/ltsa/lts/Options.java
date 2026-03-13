package ltsa.lts;

public class Options {
    public enum CompositionStrategy {
        BFS_STRATEGY, DFS_STRATEGY, RANDOM_STRATEGY;
    }

    private static final String dfsClass = "ltsa.lts.DFSCompositionEngine";
    private static final String bfsClass = "ltsa.lts.BFSCompositionEngine";
    private static final String randomClass = "ltsa.lts.RandomCompositionEngine";

    private static long maxStatesGeneration = LTSConstants.NO_MAX_STATE_GENERATION;
    private static boolean useGeneratedSeed = false;
    private static long randomSeed = 0;

    private static String compositionClass = dfsClass;

    public static String getCompositionStrategyClass() {
        return compositionClass;
    }

    public static void setCompositionStrategyClass(CompositionStrategy strategy) {
        switch (strategy) {
            case BFS_STRATEGY:
                compositionClass = bfsClass;
                break;
            case RANDOM_STRATEGY:
                compositionClass = randomClass;
                break;
            case DFS_STRATEGY:
            default:
                compositionClass = dfsClass;
                break;
        }
    }

    public static long getMaxStatesGeneration() {
        return maxStatesGeneration;
    }

    public static void setMaxStatesGeneration(long maxStates) {
        maxStatesGeneration = maxStates <= 0 ? LTSConstants.NO_MAX_STATE_GENERATION : maxStates;
    }


    public static void setUseGeneratedSeed(boolean useSeed) {
        useGeneratedSeed = useSeed;
    }

    public static boolean useGeneratedSeed() {
        return useGeneratedSeed && randomSeed != 0;
    }

    public static long getRandomSeed() {
        return randomSeed;
    }

    public static void setRandomSeed(long seed) {
        randomSeed = seed;
        useGeneratedSeed = true;
    }
}
