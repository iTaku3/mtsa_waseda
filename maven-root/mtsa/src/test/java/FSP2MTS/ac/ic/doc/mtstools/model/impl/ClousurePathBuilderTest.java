package FSP2MTS.ac.ic.doc.mtstools.model.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import MTSTools.ac.ic.doc.commons.relations.Pair;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;
import MTSTools.ac.ic.doc.mtstools.model.MTSConstants;
import FSP2MTS.ac.ic.doc.mtstools.test.util.MTSTestBase;
import FSP2MTS.ac.ic.doc.mtstools.test.util.MTSTestUtils;
import MTSTools.ac.ic.doc.mtstools.model.impl.ClousurePathBuilder;


public class ClousurePathBuilderTest extends MTSTestBase {
	
	public void testAll() throws Exception {
		String sourceString = "M = ( m -> STOP | l -> STOP | _tau? -> l -> STOP)\\{_tau}.\r\n";
		MTS<Long, String> mtsM = MTSTestUtils.buildMTSFrom(sourceString, ltsOutput);
		Set<String> silentActions = new HashSet<String>();
		silentActions.add(MTSConstants.TAU);
		silentActions.add("a");
		Set<String> actions = new HashSet<String>(mtsM.getActions());
		actions.add("a");
		this.getAllPath(mtsM, silentActions, actions, 21);
		
	}
	
	public <S,A> void getAllPath(MTS<S,A> mts,Set<A> silentActions, Set<A> actions, int expectedAmount) {
		int amount = 0;
		
		for(S state:mts.getStates()) {
			for(A action:actions) {
				for(TransitionType type:TransitionType.values()) {					
					Iterator<List<Pair<A,S>>> it = ClousurePathBuilder.getInstance().getPathsIterator(mts,state,action,type,silentActions);
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
