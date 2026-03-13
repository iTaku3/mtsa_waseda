package FSP2MTS.ac.ic.doc.mtstools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ltsa.lts.util.MTSUtils;

import org.apache.commons.collections.CollectionUtils;

import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;
import MTSTools.ac.ic.doc.mtstools.model.impl.MTSImpl;
import FSP2MTS.ac.ic.doc.mtstools.test.util.MTSTestBase;
import ltsa.ac.ic.doc.mtstools.util.fsp.MTSToAutomataConverter;

public class MTSUtilsTest extends MTSTestBase {

	public void testDoMaybe() throws Exception {
		assertEquals("hola?", MTSUtils.getMaybeAction("hola"));
	}
	
	public void testBuildAlphabetWithoutMaybeAndNotMaybe() throws Exception {
		List<String> result = new ArrayList<String>();
		List<String> expected = new ArrayList<String>();
		CollectionUtils.addAll(result, MTSUtils.getAlphabetWithMaybes(new String[]{"tau", "b?", "a" }));
		CollectionUtils.addAll(expected, new String[]{"tau", "a", "b?", "a?", "b", "tau?"});

		Collections.sort(result);
		Collections.sort(expected);
		
		assertEquals(expected, result);
	}

	public void testBuildAlphabetWithMaybeAndNotMaybe() throws Exception {
		List<String> result = new ArrayList<String>();
		List<String> expected = new ArrayList<String>();
		CollectionUtils.addAll(result, MTSUtils.getAlphabetWithMaybes(new String[]{"tau","b", "b?", "a" }));
		CollectionUtils.addAll(expected, new String[]{"tau", "a", "b?", "a?", "b", "tau?"});
		Collections.sort(result);
		Collections.sort(expected);
		assertEquals(expected, result);
	}

	public void testIsMTSRepresentation() throws Exception {
		MTSImpl<Long, String> mts = this.buildBasicMTS();
		assertFalse(MTSUtils.isMTSRepresentation(MTSToAutomataConverter.getInstance().convert(mts, "dipi")));
		
		mts.addTransition(ESTADO_CERO, "tau", ESTADO_CERO, TransitionType.MAYBE);
		assertTrue(MTSUtils.isMTSRepresentation(MTSToAutomataConverter.getInstance().convert(mts, "dipi")));
	}

}
