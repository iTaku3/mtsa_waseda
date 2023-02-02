package MTSATests.controller;

import FSP2MTS.ac.ic.doc.mtstools.test.util.TestLTSOuput;
import MTSAClient.ac.ic.doc.mtsa.MTSCompiler;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.impl.LTSSimulationSemantics;
import ltsa.ac.ic.doc.mtstools.util.fsp.AutomataToMTSConverter;
import ltsa.lts.*;
import ltsa.lts.ltl.AssertDefinition;
import ltsa.ui.StandardOutput;
import ltsa.updatingControllers.UpdateConstants;
import ltsa.updatingControllers.structures.graph.UpdateGraph;
import ltsa.updatingControllers.structures.graph.UpdateNode;
import ltsa.updatingControllers.structures.graph.UpdateTransition;
import ltsa.updatingControllers.synthesis.UpdateGraphGenerator;
import org.junit.experimental.categories.Categories;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.Vector;

import static org.testng.Assert.*;

public class UpdatingControllersTests {

    public static final String ext = ".lts";
    public static final String env = "_env";

    @DataProvider(name = "environmentTest")
    public Object[][] environmentParameters() {
        return new Object[][]{{"ControllerUpdate/Tests/adding_actions_test"},
                {"ControllerUpdate/Tests/removing_actions_test"},
                {"ControllerUpdate/Tests/UAV_test"},
                {"ControllerUpdate/Tests/productionCell_test"},
//				{"ControllerUpdate/Tests/GsubsetjG'_test"},
                {"ControllerUpdate/Ghezzi/2015-FSE-PowerPlant-Ghezzi2013"},
                {"ControllerUpdate/Ghezzi/2015-FSE-Railcab-Ghezzi2012"},
                {"ControllerUpdate/2015-FSE-ProductionCell"},
                {"ControllerUpdate/2015-FSE-Wildlife"},
                {"ControllerUpdate/2015-FSE-Workflow"}};
    }



    //TODO FIX     @Test(dataProvider = "environmentTest")
    private void environmentTest(String filename) throws Exception {

        LTSInput updEnv = LTSTestHelper.getInstance().getLtsInput(filename + env + ext);
        LTSInput updCont = LTSTestHelper.getInstance().getLtsInput(filename + ext);
        MTS<Long, String> expectedEnv = MTSCompiler.getInstance().compileMTS("UpdEnv", String.valueOf(updEnv));
        CompositeState compiled = MTSCompiler.getInstance().compileCompositeState("UpdCont", updCont);

        // Get the environment for update E_u
        MTS<Long, String> obtainedEnv = null;
        for (CompactState machine : compiled.machines) {
            if (machine.getName().equals("UPD_CONT_ENVIRONMENT")) {
                obtainedEnv = AutomataToMTSConverter.getInstance().convert(machine);
                break;
            }
        }
        if (obtainedEnv == null) {
            System.out.println("No updating environment after compiling");
            fail();
        }

        // Add the .old actions from the obtained environment to the expected in the alphabet
        Set<String> actionsFromCompiled = obtainedEnv.getActions();
        for (String action : actionsFromCompiled) {
            if (action.contains(UpdateConstants.OLD_LABEL) && !expectedEnv.getActions().contains(action)) {
                expectedEnv.addAction(action);
            }
        }

        LTSSimulationSemantics simulationSemantics = new LTSSimulationSemantics();

        assertTrue(simulationSemantics.isARefinement(expectedEnv, obtainedEnv));
        assertTrue(simulationSemantics.isARefinement(obtainedEnv, expectedEnv));
    }

    @DataProvider(name = "checkUpdatingFormula")
    public Object[][] controllersComparisonParameters() {

        return new Object[][]{

                {"ControllerUpdate/Tests/adding_actions_test"},
                {"ControllerUpdate/Tests/removing_actions_test"},
                {"ControllerUpdate/Tests/UAV_test"},
                {"ControllerUpdate/Tests/productionCell_test"},
//				{"ControllerUpdate/Tests/GsubsetjG'_test"},
                {"ControllerUpdate/Ghezzi/2015-FSE-PowerPlant-Ghezzi2013"},
                {"ControllerUpdate/Ghezzi/2015-FSE-Railcab-Ghezzi2012"},
                //			    {"ControllerUpdate/2015-FSE-ProductionCell"},
                //			    {"ControllerUpdate/2015-FSE-Wildlife"},
                {"ControllerUpdate/2015-FSE-Workflow"},
                {"ControllerUpdate/2015-Tokyo/colorBallsTransfer"}};
    }




    //TODO FIX  @Test(dataProvider = "checkUpdatingFormula")
    // This test check that the file in targetA can compose UpdCont and check some formulas defined there
    private void checkUpdatingFormula(String filename) throws Exception {

        LTSInput file = LTSTestHelper.getInstance().getLtsInput(filename + ext);

        CompositeState compiled = MTSCompiler.getInstance().compileCompositeState("UpdCont", file);
        LTSOutput output = new TestLTSOuput();

        CompositeState ltlProperty = AssertDefinition.compile(output, "TEST_FINAL_FORMULA");
        compiled.setErrorTrace(new ArrayList<String>());

        Vector<CompactState> machines = new Vector<CompactState>();
        machines.add(compiled.getComposition());
        CompositeState cs = new CompositeState(compiled.name, machines);
        cs.checkLTL(output, ltlProperty);

        assertTrue(cs.getErrorTrace() == null || cs.getErrorTrace().isEmpty());

    }

    @DataProvider(name = "graphGenerator")
    public Object[][] graphGeneratorFiles() {
        return new Object[][]{{"ControllerUpdate/2015-Tokyo/colorBallsTransfer"}};
    }


    //TODO FIX @Test(dataProvider = "graphGenerator")
    public void graphGenerator_producesCorrectGraph(String fileName) throws IOException, ParseException {
        LTSInput input = LTSTestHelper.getInstance().getLtsInput(fileName + ext);
        LTSCompiler compiler = LTSCompiler.getCompiler(input);
        compiler.compile();
        UpdateGraph updateGraph = UpdateGraphGenerator.generateGraph("Graph", compiler, new StandardOutput());

        assertNotNull(updateGraph.getInitialState());
        assertNotNull(updateGraph.getInitialState().getController$environment());
        assertEquals(6, updateGraph.getEdgeCount());
        assertEquals(3, updateGraph.getVertexCount());
        Collection<UpdateNode> vertices = updateGraph.getVertices();
        assertTrue(vertices.contains(updateGraph.getInitialState()));
        for (UpdateNode vertice : vertices) {
            assertNotNull(vertice.getGoalName());
        }
        Collection<UpdateTransition> edges = updateGraph.getEdges();
        for (UpdateTransition edge : edges) {
            assertNotNull(edge.getUpdateController());
        }
    }

    //	@DataProvider(name = "controllersComparisonTest")
    //	public Object[][] controllersComparisonParameters() {
    //
    //		return new Object[][] {
    //
    //		// GC: Ghezzi; TC: endProcedureWhileRunning; OR: GC or TC;
    //		{"GC", "TrueC", "ControllerUpdate/Ghezzi/2015-FSE-PowerPlant-Ghezzi2013"},
    //		{"OC", "TrueC", "ControllerUpdate/Ghezzi/2015-FSE-PowerPlant-Ghezzi2013"},
    //		{"TC", "TrueC", "ControllerUpdate/Ghezzi/2015-FSE-PowerPlant-Ghezzi2013"},
    //		{"TC", "OC", "ControllerUpdate/Ghezzi/2015-FSE-PowerPlant-Ghezzi2013"},
    //		{"GC", "OC", "ControllerUpdate/Ghezzi/2015-FSE-PowerPlant-Ghezzi2013"},
    //
    //		// GC: Ghezzi; TC: updateBeforeStopOldSpec; OC: GC or TC;
    //		{"GC", "TrueC", "ControllerUpdate/Ghezzi/2015-FSE-Railcab-Ghezzi2012"},
    //		{"OC", "TrueC", "ControllerUpdate/Ghezzi/2015-FSE-Railcab-Ghezzi2012"},
    //		{"TC", "TrueC", "ControllerUpdate/Ghezzi/2015-FSE-Railcab-Ghezzi2012"},
    //		{"TC", "OC", "ControllerUpdate/Ghezzi/2015-FSE-Railcab-Ghezzi2012"},
    //		{"GC", "OC", "ControllerUpdate/Ghezzi/2015-FSE-Railcab-Ghezzi2012"},
    //		{"OC", "GC", "ControllerUpdate/Ghezzi/2015-FSE-Railcab-Ghezzi2012"},
    //		{"GC", "TC", "ControllerUpdate/Ghezzi/2015-FSE-Railcab-Ghezzi2012"},
    //		{"TC", "GC", "ControllerUpdate/Ghezzi/2015-FSE-Railcab-Ghezzi2012"},
    //
    //		// LC: LowBeforeUpdate; NGFC: updateBeforeStopOldSpec; NSC: NoScanWhileUpdate; NMC: NoMoveWhileUpdate; GC:
    // Ghezzi
    //		{"LC", "TrueC", "ControllerUpdate/2015-FSE-Wildlife"},
    //		{"TrueC", "LC", "ControllerUpdate/2015-FSE-Wildlife"},
    //		{"NGFC", "TrueC", "ControllerUpdate/2015-FSE-Wildlife"},
    //		{"NSC", "TrueC", "ControllerUpdate/2015-FSE-Wildlife"},
    //		{"NMC", "TrueC", "ControllerUpdate/2015-FSE-Wildlife"},
    //		{"GC", "TrueC", "ControllerUpdate/2015-FSE-Wildlife"},
    //		{"NGFC", "LC", "ControllerUpdate/2015-FSE-Wildlife"},
    //		{"NSC", "LC", "ControllerUpdate/2015-FSE-Wildlife"},
    //		{"NMC", "LC", "ControllerUpdate/2015-FSE-Wildlife"},
    //		{"GC", "LC", "ControllerUpdate/2015-FSE-Wildlife"},
    //		{"NGFC", "NSC", "ControllerUpdate/2015-FSE-Wildlife"},
    //		{"NGFC", "NMC", "ControllerUpdate/2015-FSE-Wildlife"},
    //		{"NGFC", "GC", "ControllerUpdate/2015-FSE-Wildlife"},
    //		{"NMC", "NSC", "ControllerUpdate/2015-FSE-Wildlife"},
    //
    //		// LC: LowBeforeUpdate; NGFC: updateBeforeStopOldSpec; NSC: NoScanWhileUpdate; NMC: NoMoveWhileUpdate; GC:
    // Ghezzi
    //		{"LC", "TrueC", "ControllerUpdate/2015-FSE-WildlifeV2"},
    //		{"TrueC", "LC", "ControllerUpdate/2015-FSE-WildlifeV2"},
    //		{"NGFC", "TrueC", "ControllerUpdate/2015-FSE-WildlifeV2"},
    //		{"NSC", "TrueC", "ControllerUpdate/2015-FSE-WildlifeV2"},
    //		{"TrueC", "NSC", "ControllerUpdate/2015-FSE-WildlifeV2"},
    //		{"NMC", "TrueC", "ControllerUpdate/2015-FSE-WildlifeV2"},
    //		{"NGFC", "LC", "ControllerUpdate/2015-FSE-WildlifeV2"},
    //		{"NSC", "LC", "ControllerUpdate/2015-FSE-WildlifeV2"},
    //		{"LC", "NSC", "ControllerUpdate/2015-FSE-WildlifeV2"},
    //		{"NMC", "LC", "ControllerUpdate/2015-FSE-WildlifeV2"},
    //		{"NGFC", "NSC", "ControllerUpdate/2015-FSE-WildlifeV2"},
    //		{"NGFC", "NMC", "ControllerUpdate/2015-FSE-WildlifeV2"},
    //		{"NMC", "NSC", "ControllerUpdate/2015-FSE-WildlifeV2"},
    //
    //		// EC: EmptyController; UFC: updateBeforeStopOldSpec; KOC: KeepOldSpec; CASAP: changeASAP
    //		{"UFC", "TrueC", "ControllerUpdate/2015-FSE-ProductionCell"},
    //		{"KOC", "TrueC", "ControllerUpdate/2015-FSE-ProductionCell"},
    //		{"CASAP", "TrueC", "ControllerUpdate/2015-FSE-ProductionCell"},
    //		{"UFC", "KOC", "ControllerUpdate/2015-FSE-ProductionCell"},
    //		{"UFC", "CASAP", "ControllerUpdate/2015-FSE-ProductionCell"},
    //		{"KOC", "CASAP", "ControllerUpdate/2015-FSE-ProductionCell"},
    //		{"CASAP", "KOC", "ControllerUpdate/2015-FSE-ProductionCell"}
    //
    //		};
    //	}

    //	@Test(dataProvider = "controllersComparisonTest")
    //	// This test check that targetA \subeset targetB
    //	private void controllerComparisonTest(String targetA, String targetB, String filename) throws Exception{
    //		MTS<Long, String> compiledA = MTSCompiler.getInstance().compileMTS(targetA, getFile(filename));
    //		MTS<Long, String> compiledB = MTSCompiler.getInstance().compileMTS(targetB, getFile(filename));
    //
    //		LTSSimulationSemantics simulationSemantics = new LTSSimulationSemantics();
    //		boolean result = simulationSemantics.isARefinement(compiledB, compiledA);
    //
    //		assertTrue(result);
    //
    //	}


}