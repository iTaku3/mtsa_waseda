package MTSATests.controller;


public abstract class LTSLTLPropertyTest extends LTSTestHelper {

    protected void run(String filename, String controllerName, String[] properties) {
        for (String property : properties)
            checkCompliance(filename, controllerName, property);
    }

    protected void checkCompliance(String ltsFilename, String controllerName, String property) {
        throw new RuntimeException();
        /*
		//TODO: revisar con nico
		CompositeState c = getCompositeFromFile(ltsFilename, controllerName);
		
		StandardOutput output = new StandardOutput();
		CompositeState ltl_property = AssertDefinition.compile(output, property);
		// Silent compilation for negated formula
		//CompositeState not_ltl_property = AssertDefinition.compile(output, AssertDefinition.NOT_DEF + property);
		Vector<CompactState> machines = new Vector<CompactState>();
		machines.add(c.getComposition());
		CompositeState cs = new CompositeState(c.name, machines );
			
		cs.checkLTL(output, ltl_property);		
		
		assertTrue(cs.getErrorTrace() == null || cs.getErrorTrace().isEmpty());
		*/
    }

}
