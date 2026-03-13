package MTSAExperiments.ar.uba.dc.lafhis.experiments;

import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import MTSAExperiments.ar.uba.dc.lafhis.experiments.exchange.JSONCompatible;

public class ExperimentSet {
	public static int DEFAULT_THREAD_COUNT	= 30;
	
	protected int threadCount;	
	protected Dictionary<Experiment, ExperimentExecution> experimentExecutions;
	protected Set<Experiment> experiments;
	
	public ExperimentSet(){
		this(DEFAULT_THREAD_COUNT);
	}
	
	public ExperimentSet(int threadCount){
		this.threadCount				= threadCount;
		experiments 					= new HashSet<Experiment>();
		experimentExecutions 			= new Hashtable<Experiment, ExperimentExecution>();
	}
	
	public boolean hasExperiment(Experiment experiment){
		return experiments.contains(experiment);
	}
	
	public void removeExperiment(Experiment experiment){
		if(experiment != null && hasExperiment(experiment)){
			experiments.remove(experiment);
			experimentExecutions.remove(experiment);
		}
	}
	
	public void addExperiment(Experiment experiment, Dictionary<String, JSONCompatible> parameters, String resultsDestination){
		if(experiment != null && !hasExperiment(experiment)){
			experiments.add(experiment);
			experimentExecutions.put(experiment, new ExperimentExecution(experiment, parameters, resultsDestination));
		}
	}
	
	public Set<Experiment> getExperiments(){
		return experiments;
	}
	
	public Dictionary<Experiment,ExperimentExecution> getExperimentExecutions(){
		return experimentExecutions;
	}	
	
	public void runExperiments(){
		runExperiments(false);
	}
	
	public void runExperiments(boolean concurrentRun){
		if(concurrentRun){
			ExecutorService executor = Executors.newFixedThreadPool(threadCount);
	
			for (Experiment experiment:experiments) {
				Runnable worker = experimentExecutions.get(experiment);
				executor.execute(worker);
			}
			executor.shutdown();
	
			while (!executor.isTerminated()) {}		
		}else{
			for (Experiment experiment:experiments) {
				experimentExecutions.get(experiment).run();
			}
		}
	}
}




