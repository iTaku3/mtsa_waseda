package ltsa.lts;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import ltsa.lts.util.LTSUtils;

public class ProbabilisticTransition extends Transition {
    int probBundle;
    BigDecimal prob;
    
    public static final int NO_BUNDLE= -1;
    public static final int BUNDLE_ERROR= -2;
    
    private static int lastProbBundle= NO_BUNDLE;
    private static Map composedBundles= new HashMap();
    
    ProbabilisticTransition() {
    }

    ProbabilisticTransition(int from) {
    	this.from = from;
    }

    ProbabilisticTransition(int from, Symbol event, int to) {
        this.from =from;
        this.to = to;
        this.event = event; 
    }
    
    ProbabilisticTransition(int from, Symbol event, int to, BigDecimal prob) {
        this.from =from;
        this.to = to;
        this.event = event;
        this.prob= prob; 
    }

    ProbabilisticTransition(int from, Symbol event, int to, BigDecimal prob, int probBundle) {
        this.from =from;
        this.to = to;
        this.event = event;
        this.prob= prob;
        this.probBundle= probBundle;
    }

    public void setProbability(BigDecimal prob) {
    	this.prob= prob;
    }
    
    public BigDecimal getProbability() {
    	return prob;
    }
    
    public void setBundle(int bundle) {
    	this.probBundle= bundle;
    }
    
    public int getBundle() {
    	return probBundle;
    }
    
    public static int getLastProbBundle() {
    	return lastProbBundle;
    }
    
    public static int getNextProbBundle() {
    	return ++lastProbBundle;
    }
    
    public static void setLastProbBundle(int bundle) {
    	lastProbBundle= bundle;
    }

    public String toString() {
        return "" + from + " --{" + event + "," + probBundle + "} " + prob.toString() + "--> " + to;
    }
    
    public static int composeBundles(int[] sourceStates, int[] bundles) {
    	int[] sortedBundles= LTSUtils.myclone(bundles);
    	int composedBundle;
    	Arrays.sort(sortedBundles);
    	String bundlesStr= Arrays.toString(sortedBundles);
    	String stateStr= Arrays.toString(sourceStates);
    	Object bundlesForStates= composedBundles.get(stateStr);
    	if (bundlesForStates == null) {
    		bundlesForStates= new HashMap();
    		composedBundles.put(stateStr, bundlesForStates);
    	}

    	Object bundle= ((Map) bundlesForStates).get(bundlesStr);
    	if (bundle == null) {
    		composedBundle= ++lastProbBundle;
    		// composedBundles.put(arrayStr, composedBundle);
    		((Map) bundlesForStates).put(bundlesStr, composedBundle);
    	} else {
    		composedBundle= ((Integer) bundle).intValue();
    	}

    	return composedBundle;
    }

    public static BigDecimal composeProbs(BigDecimal[] probs) {
    	BigDecimal prob= BigDecimal.ONE;
    	for (BigDecimal srcProb : probs) {
    		if (srcProb != null) {
    			// may be null if internal / not shared
    			prob= prob.multiply(srcProb);
    		}
    	}
    	
    	return prob;
    }
}
