/**
 * 
 */
package MTSTools.ac.ic.doc.mtstools.model.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import org.apache.commons.collections15.Predicate;

import MTSTools.ac.ic.doc.commons.relations.BinaryRelation;
import MTSTools.ac.ic.doc.commons.relations.MapSetBinaryRelation;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.commons.relations.UniversalBinaryRelation;
import MTSTools.ac.ic.doc.mtstools.model.MTS;

/**
 * 
 * 
 * @author fdario
 * 
 */
class FixedPointRelationConstructor implements RelationConstructor {

	private Simulation<?> simulation;
	private Logger logger;

	public FixedPointRelationConstructor(Simulation<?> simulation) {
		this.simulation = simulation;
		this.logger = Logger.getLogger(this.getClass().getCanonicalName());
	}

	public <S1, S2, A> BinaryRelation<S1, S2> getLargestRelation(MTS<S1, A> mts1, MTS<S2, A> mts2) {

		Simulation<?> sim = this.getSimulation();		
		BinaryRelation<S1, S2> result = new UniversalBinaryRelation<S1, S2>(mts1.getStates(), mts2.getStates());
		

		int oldSize, size = result.size();
		do {
			oldSize = size;
			Predicate<Pair<S1, S2>> predicate = new SimulationPredicate<S1, S2, A>(mts1, mts2, result, sim);
			result = this.filter(result, predicate);
			size = result.size();
		} while (oldSize != size);

		this.logger.info("R=" + result.toString());
				
		return result;
	}

	protected Simulation<?> getSimulation() {
		return simulation;
	}


	public <S1, S2, A> boolean isValidRelation(MTS<S1, A> mts1, MTS<S2, A> mts2, BinaryRelation<S1, S2> relation) {
		boolean wasInterrupted = false;
		Simulation<?> sim = this.getSimulation();		
		Predicate<Pair<S1, S2>> predicate = new SimulationPredicate<S1, S2, A>(mts1, mts2, relation, sim);
		List<Future<Pair<S1, S2>>> futures = new ArrayList<Future<Pair<S1, S2>>>(relation.size());
		
		CompletionService<Pair<S1, S2>> service = new ExecutorCompletionService<Pair<S1, S2>>(ExecutorServiceFactory.getExecutorService());
		
		boolean result = true;
		try {			
			for(Pair<S1,S2> pair: relation) {
				futures.add(service.submit(new PredicateCollable<Pair<S1, S2>>(predicate, pair)));
			}
			for( int i=0; i < futures.size(); i++) {
				Future<Pair<S1, S2>> future;
				try {
					future = service.take(); 
				} catch (InterruptedException e) {
					wasInterrupted = true;
					i--;
					continue;
				}					
				while(true) {
					try {
						result = future.get() != null;
						break;
					} catch (InterruptedException e) {
						wasInterrupted = true;
					}					 
				}
				if (!result) {
					break;
				}
			}			 
		} catch (ExecutionException e) {
			e.printStackTrace();
			result = false; // TODO review
		} finally {
			for (Future<Pair<S1, S2>> future: futures) {
				future.cancel(true);
			}
		}
		if (wasInterrupted) {
			Thread.currentThread().interrupt();
		}
		return result;
	}

	public <S1, S2> BinaryRelation<S1, S2> filter(BinaryRelation<S1, S2> originalRelation, Predicate<Pair<S1, S2>> predicate) {
		BinaryRelation<S1, S2> result = new MapSetBinaryRelation<S1, S2>();
		boolean wasInterrupted = false;
		
		List<Future<Pair<S1, S2>>> futures = new ArrayList<Future<Pair<S1, S2>>>(originalRelation.size());		
		CompletionService<Pair<S1, S2>> service = new ExecutorCompletionService<Pair<S1, S2>>(ExecutorServiceFactory.getExecutorService());
		
		try {			
			for(Pair<S1,S2> pair: originalRelation) {
				futures.add(service.submit(new PredicateCollable<Pair<S1, S2>>(predicate, pair)));
			}
			for( int i=0; i < futures.size(); i++) {
				Future<Pair<S1, S2>> future;
				try {
					future = service.take(); 
				} catch (InterruptedException e) {
					wasInterrupted = true;
					i--;
					continue;
				}					
				do {
					try {
						Pair<S1, S2> elem = future.get();
						if (elem != null) {
							result.add(elem);
						}
						break;
					} catch (InterruptedException e) {
						wasInterrupted = true;
					}					 
				} while(true);
			}			 
		} catch (ExecutionException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			for (Future<Pair<S1, S2>> future: futures) {
				future.cancel(true);
			}
		}
		if (wasInterrupted) {
			Thread.currentThread().interrupt();
		}
		return result;		
		
	}
	
	
	private static class PredicateCollable<E> implements Callable<E> {
		private E elem;
		private Predicate<E> predicate;
		PredicateCollable(Predicate<E> predicate, E elem)  {
			this.predicate = predicate;
			this.elem = elem;
		}
		
		@Override
		public E call() throws Exception {
			if (this.predicate.evaluate(this.elem)) {
				return this.elem;
			} else {
				return null;
			}
		}
		
	}
	
}
