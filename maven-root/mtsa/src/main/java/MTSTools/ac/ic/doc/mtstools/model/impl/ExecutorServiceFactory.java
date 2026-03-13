package MTSTools.ac.ic.doc.mtstools.model.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class ExecutorServiceFactory {

	final private static ExecutorService EXECUTOR_SERVICE;
	
	static {
		
		int processors = 2;
		try {
			processors = Integer.parseInt(System.getProperty("ac.ic.doc.mtstools.workers", "2"));
		} catch (NumberFormatException e) {
			processors = 2;
		}
		EXECUTOR_SERVICE = Executors.newFixedThreadPool(processors,
			new ThreadFactory(){
				int id=0;
				@Override
				public Thread newThread(Runnable r) {
					Thread result = new Thread(r);
					result.setDaemon(true);
					result.setName("Simulation worker (" + (++id) + ")");
					return result;
				}
		});
	}

	public static ExecutorService getExecutorService() {
		return EXECUTOR_SERVICE;
	}

	
	
}
