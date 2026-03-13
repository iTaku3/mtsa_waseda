package MTSATests.controller;

import java.util.HashSet;
import java.util.Set;

import MTSSynthesis.ar.dc.uba.model.condition.Fluent;
import MTSSynthesis.ar.dc.uba.model.condition.FluentImpl;
import MTSSynthesis.ar.dc.uba.model.condition.FluentPropositionalVariable;
import MTSSynthesis.controller.model.ControllerGoal;

public class ComplexTomAndJerry extends BasicTomAndJerryTest {

	public void testComplexTomAndJerry() throws Exception {
		this.synthesiseInput("ComplexTomAndJerry");
	}
	
	@Override
	protected String getFileName() {
		return "TomAndJerryV11-models.lts";
	}
	
	@Override
	protected void setAssumeAndGuarantee(ControllerGoal<String> goal) {
		super.setAssumeAndGuarantee(goal);
		Fluent fluentCatIn4 = new FluentImpl("CatIn.4", ControllerTestsUtils
				.buildSymbolSetFrom(new String[] { "cat_in.4" }), ControllerTestsUtils
				.buildSymbolSetFrom(new String[] { "cat_in.3", "cat_in.2", "cat_in.0",
						"cat_in.1" }), false);
		Fluent fluentCatIn2 = new FluentImpl("CatIn.2", ControllerTestsUtils
				.buildSymbolSetFrom(new String[] { "cat_in.2" }), ControllerTestsUtils
				.buildSymbolSetFrom(new String[] { "cat_in.3", "cat_in.4", "cat_in.0",
						"cat_in.1" }), false);

		Set<Fluent> fluents = new HashSet<Fluent>();
		fluents.add(fluentCatIn4);
		fluents.add(fluentCatIn2);

		goal.addAllFluents(fluents);

		goal.addGuarantee(new FluentPropositionalVariable(fluentCatIn4));
		goal.addGuarantee(new FluentPropositionalVariable(fluentCatIn2));
	}
	
}
