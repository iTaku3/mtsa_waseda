package MTSATests.controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import MTSATests.util.TestConstants;
import MTSSynthesis.ar.dc.uba.model.condition.Fluent;
import MTSSynthesis.ar.dc.uba.model.condition.FluentImpl;
import MTSSynthesis.ar.dc.uba.model.condition.FluentPropositionalVariable;
import MTSSynthesis.controller.model.ControllerGoal;

public class BasicTomAndJerryTest extends MTSParsedTestsBase {

	public void testTomAndJerry() throws Exception {
		this.synthesiseInput("BasicTomAndJerry");
	}

	@Override
	protected void setAssumeAndGuarantee(ControllerGoal<String> goal) {
		Fluent fluentMouseIn4 = new FluentImpl("MouseIn.4", ControllerTestsUtils
				.buildSymbolSetFrom(new String[] { "mouse_in.4" }), ControllerTestsUtils
				.buildSymbolSetFrom(new String[] { "mouse_in.3", "mouse_in.2", "mouse_in.0",
						"mouse_in.1" }), false);
		Fluent fluentMouseIn2 = new FluentImpl("MouseIn.2", ControllerTestsUtils
				.buildSymbolSetFrom(new String[] { "mouse_in.2" }), ControllerTestsUtils
				.buildSymbolSetFrom(new String[] { "mouse_in.3", "mouse_in.4", "mouse_in.0",
						"mouse_in.1" }), false);

		Set<Fluent> fluents = new HashSet<Fluent>();
		fluents.add(fluentMouseIn4);
		fluents.add(fluentMouseIn2);

		goal.addAllFluents(fluents);

		goal.addGuarantee(new FluentPropositionalVariable(fluentMouseIn4));
		goal.addGuarantee(new FluentPropositionalVariable(fluentMouseIn2));
	}

	@Override
	protected void setControllableActions(ControllerGoal<String> goal) {
		Set<String> controllable = ControllerTestsUtils.buildStringsSetFrom(new String[] { "c1",
				"c2", "c3", "c4", "c5", "c6", "c7a", "c7b", "m1", "m2", "m3", "m4", "m5", "m6" });
		goal.addAllControllableActions(controllable);
	}

	@Override
	protected String getInputModel() throws IOException {
		String result = null;
		BufferedReader br = null;
		try {
			FileReader fr = new FileReader(TestConstants.fileFrom(getFileName()));
			br = new BufferedReader(fr);
			String line = br.readLine();
			StringBuffer sb = new StringBuffer();
			while (line !=null) {
				sb.append(line);
				line = br.readLine();
			}
			result = sb.toString();
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		} finally {
			br.close();
		}
		return result;
	}

	protected String getFileName() {
		return "TomAndJerryV10-models.lts";
	}
}
