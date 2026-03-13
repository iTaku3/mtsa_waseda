package FSP2MTS.ac.ic.doc.mtstools.model.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import MTSTools.ac.ic.doc.mtstools.model.impl.MTSAdapter;
import MTSTools.ac.ic.doc.mtstools.model.impl.MTSImpl;
import MTSTools.ac.ic.doc.mtstools.model.impl.PathBuilder2;
import junit.framework.TestCase;
import MTSTools.ac.ic.doc.commons.relations.Pair;
import FSP2MTS.ac.ic.doc.mtstools.model.LTSExamples;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import FSP2MTS.ac.ic.doc.mtstools.model.MTSExamples;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;

public class PathBuilder2Test extends TestCase {


	public void testAll() {
		System.out.println("MTS_01");
		this.getAllPath(MTSExamples.MTS_01,Collections.singleton(MTSExamples.TAU),10);
		System.out.println("\nMTS)_LOOP");
		this.getAllPath(MTSExamples.MTS_LOOP,Collections.singleton(MTSExamples.TAU),24);
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
		
		Collection<TransitionType> types = new ArrayList<TransitionType>();
		types.add(TransitionType.REQUIRED);
		types.add(TransitionType.POSSIBLE);
		
		Map<TransitionType, PathBuilder2<S, A>> builders = new EnumMap<TransitionType, PathBuilder2<S, A>>(TransitionType.class);
		
		for (TransitionType type : types) {
			builders.put(type, new PathBuilder2<S, A>(mts, type, silentActions));
		}
		
		for(S state:mts.getStates()) {
			for(A action:mts.getActions()) {					
				for(TransitionType type:types) {
					Collection<List<Pair<A,S>>> paths = builders.get(type).getPaths(state, action);
					Iterator<List<Pair<A, S>>> it = paths.iterator();
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
