package FSP2MTS.ac.ic.doc.mtstools.model;

import static MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType.POSSIBLE;
import static MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType.REQUIRED;

import java.util.Arrays;
import java.util.Collection;

import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.impl.MTSImpl;
public class MTSExamples {

	public static final String TAU = "T"; //"\uD835\uDF0F";
	
	public static final MTS<Integer,String> MTS_01;
	public static final MTS<Integer,String> MTS_04;
	public static final MTS<Integer,String> MTS_07;
	public static final MTS<Integer,String> MTS_08;
	public static final MTS<Integer,String> MTS_WEIRD;
	public static final MTS<Integer,String> MTS_LOOP;
	
	static {
		Collection<Integer> states = Arrays.asList(new Integer[]{0,1,2});
		Collection<String> actions = Arrays.asList(new String[]{"a","b",TAU});
		
		MTS_01 = new MTSImpl<Integer,String>(0);
		MTS_01.addStates(states);
		MTS_01.addActions(actions);
		MTS_01.addPossible(0,TAU,1);
		MTS_01.addRequired(1,"a",2);
		
		MTS_LOOP = new MTSImpl<Integer,String>(0);
		MTS_LOOP.addStates(states);
		MTS_LOOP.addActions(actions);
		MTS_LOOP.addPossible(0,TAU,1);
		MTS_LOOP.addPossible(0,TAU,2);
		MTS_LOOP.addPossible(1,TAU,0);
		MTS_LOOP.addPossible(1,TAU,2);
		MTS_LOOP.addPossible(2,TAU,0);
		MTS_LOOP.addPossible(2,TAU,1);
		MTS_LOOP.addRequired(1,"a",2);
		
		MTS_04 = new MTSImpl<Integer,String>(0);
		MTS_04.addStates(states);
		MTS_04.addState(3);
		MTS_04.addState(4);
		MTS_04.addActions(actions);
		MTS_04.addPossible(0,TAU,1);
		MTS_04.addRequired(1,"a",2);
		MTS_04.addRequired(1,"b",3);
		MTS_04.addPossible(0,"b",4);
		
		MTS_07 = new MTSImpl<Integer,String>(0);
		MTS_07.addStates(states);
		MTS_07.addActions(actions);
		MTS_07.addPossible(0,TAU,1);
		MTS_07.addRequired(1,"a",1);
		
		MTS_08 = new MTSImpl<Integer,String>(0);
		MTS_08.addStates(states);
		MTS_08.addActions(actions);
		MTS_08.addRequired(0,TAU,1);
		MTS_08.addPossible(1,"b",1);
		MTS_08.addRequired(1,"a",2);
		
		MTS_WEIRD = new MTSImpl<Integer,String>(0);
		MTS_WEIRD.addStates(states);
		MTS_WEIRD.addActions(actions);
		MTS_WEIRD.addAction("c");
		MTS_WEIRD.addState(3);
		MTS_WEIRD.addState(4);
		MTS_WEIRD.addTransition(0,"a",1,POSSIBLE);
		MTS_WEIRD.addTransition(1,TAU,2,REQUIRED);
		MTS_WEIRD.addTransition(2,"c",3,REQUIRED);
		MTS_WEIRD.addTransition(1,"b",4,REQUIRED);
		
	}
	
}
