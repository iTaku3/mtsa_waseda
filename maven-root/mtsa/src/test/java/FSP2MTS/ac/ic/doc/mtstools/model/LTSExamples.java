package FSP2MTS.ac.ic.doc.mtstools.model;

import java.util.Arrays;
import java.util.Collection;

import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSTools.ac.ic.doc.mtstools.model.impl.LTSImpl;


public class LTSExamples {

	public static final String TAU = "T"; //"\uD835\uDF0F";
	public static final LTS<Integer,String> LTS_01;
	public static final LTS<Integer,String> LTS_04;
	public static final LTS<Integer,String> LTS_04_B;
	public static final LTS<Integer,String> LTS_WEIRD;
	
	static {
		Collection<Integer> states = Arrays.asList(new Integer[]{0,1,2});
		Collection<String> actions = Arrays.asList(new String[]{"a","b",TAU});
		
		LTS_01 = new LTSImpl<Integer,String>(0);
		LTS_01.addActions(actions);
		LTS_01.addStates(states);
		LTS_01.addTransition(0,"a",1);		
		
		LTS_04 = new LTSImpl<Integer,String>(0);
		LTS_04.addActions(actions);
		LTS_04.addStates(states);
		LTS_04.addTransition(0,"a",1);
		LTS_04.addTransition(0,"b",2);
		

		LTS_04_B = new LTSImpl<Integer,String>(0);
		LTS_04_B.addActions(actions);
		LTS_04_B.addStates(states);
		LTS_04_B.addState(3);
		LTS_04_B.addTransition(0,TAU,1);
		LTS_04_B.addTransition(1,"a",2);
		LTS_04_B.addTransition(0,"b",3);
		
		LTS_WEIRD = new LTSImpl<Integer,String>(0);
		LTS_WEIRD.addActions(actions);
		LTS_WEIRD.addStates(states);
		LTS_WEIRD.addAction("c");
		LTS_WEIRD.addTransition(0,"a",1);
		LTS_WEIRD.addTransition(1,"c",2);
	}
	
	
}
