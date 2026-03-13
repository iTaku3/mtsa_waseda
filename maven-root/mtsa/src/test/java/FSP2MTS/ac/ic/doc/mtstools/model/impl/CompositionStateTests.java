package FSP2MTS.ac.ic.doc.mtstools.model.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import MTSTools.ac.ic.doc.mtstools.model.impl.CompositionState;
import junit.framework.TestCase;

public class CompositionStateTests extends TestCase {
	public void testEquals() {
		Vector<Long> estadosA = new Vector<Long>();
		estadosA.add(0L);
		estadosA.add(0L);
		CompositionState a = new CompositionState(new Long(0L), estadosA);
		
		
		List<Long> estadosB = new ArrayList<Long>();
		estadosB.add(0L);
		estadosB.add(0L);
		CompositionState b = new CompositionState(new Long(0L), estadosB);
	
		assertTrue(a.equals(b));

		Set<CompositionState> ready = new HashSet<CompositionState>();

		ready.add(a);
		assertTrue(ready.contains(b));
		
	}
	
	
}
