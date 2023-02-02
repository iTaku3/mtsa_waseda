package FSP2MTS.ac.ic.doc.mtstools.model.impl;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import MTSTools.ac.ic.doc.mtstools.model.impl.MTSAdapter;
import MTSTools.ac.ic.doc.mtstools.model.impl.MTSImpl;
import MTSTools.ac.ic.doc.mtstools.model.impl.PathBuilder;
import junit.framework.TestCase;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import FSP2MTS.ac.ic.doc.mtstools.model.LTSExamples;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import FSP2MTS.ac.ic.doc.mtstools.model.MTSExamples;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;

public class PathBuilderTest extends TestCase {


	public void testAll() {
		System.out.println("MTS_01");
		this.getAllPath(MTSExamples.MTS_01,Collections.singleton(MTSExamples.TAU),12);
		System.out.println("\nMTS)_LOOP");
		this.getAllPath(MTSExamples.MTS_LOOP,Collections.singleton(MTSExamples.TAU),84);
		System.out.println("\nLTS_01");
		this.getAllPath(new MTSAdapter<Integer,String>(LTSExamples.LTS_01),Collections.singleton(MTSExamples.TAU),8);
	}
	
	public void testReflexiveSilent() {
		System.out.println("testReflexiveSilent");
		MTS<Integer,String> mts = new MTSImpl<Integer,String>(0);
		mts.addAction("a");
		this.getAllPath(mts,Collections.singleton("a"),2);
	}
	
	
	public <S,A> void getAllPath(MTS<S,A> mts,Set<A> silentActions, int expectedAmount) {
		int amount = 0;
		
		for(S state:mts.getStates()) {
			for(A action:mts.getActions()) {
				for(TransitionType type:TransitionType.values()) {					
					Iterator<List<Pair<A,S>>> it = PathBuilder.getInstance().getPathsIterator(mts,state,action,type,silentActions);
					if (it.hasNext()) {
						System.out.println("State: " + state + "  Action: " + action + "  Type:" + type);
					}
					while(it.hasNext()) {
						this.showPath(it.next());
						amount++;
					}
				}
			}
		}
		assertEquals(expectedAmount,amount);
	}
	
	public <A,S> void showPath(List<Pair<A,S>> path) {
		for(Pair<A,S> transition:path) {
			System.out.print(transition);
		}
		System.out.println();
	}
	
	

}
