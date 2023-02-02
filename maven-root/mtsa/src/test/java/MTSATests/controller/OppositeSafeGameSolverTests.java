package MTSATests.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Set;
import java.util.Vector;

import ltsa.lts.CompactState;
import ltsa.lts.CompositeState;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;

import ltsa.ui.StandardOutput;
import MTSTools.ac.ic.doc.mtstools.model.LTS;
import MTSTools.ac.ic.doc.mtstools.model.MTS.TransitionType;
import MTSTools.ac.ic.doc.mtstools.model.impl.LTSAdapter;
import MTSTools.ac.ic.doc.mtstools.model.impl.LTSSimulationSemantics;
import MTSTools.ac.ic.doc.mtstools.model.impl.MTSAdapter;
import ltsa.ac.ic.doc.mtstools.util.fsp.AutomataToMTSConverter;
import ltsa.ac.ic.doc.mtstools.util.fsp.MTSToAutomataConverter;
import MTSSynthesis.controller.model.ReportingPerfectInfoStateSpaceCuttingGRControlProblem;
import MTSSynthesis.controller.model.ControllerGoal;

public class OppositeSafeGameSolverTests {

	public static String reportString = "";
	public static boolean firstTime = true;
	
	   @BeforeClass
	   public void setUp() throws Exception {
		   reportString = "";
		   firstTime = true;
	   }
	 
	   @AfterClass
	   public void tearDown() throws Exception {
			PrintWriter writer;
			try {
				writer = new PrintWriter("SafetyCuts/Results/general.result", "UTF-8");
				writer.print(reportString);
				writer.close();
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}		   
	      reportString = "";
	      firstTime = false;
	   }	
	
	@DataProvider(name = "oppositeGameTest")
	public Object[][] oppositeSafeGameParameters() {
		return new Object[][] {
				/* */
				{
					"Augmented Biscotti",
					"SafetyCuts/Tests/Augmented-Biscotti.lts",
					"BISCOTTI", "EXP", "C", "G1",
					"SafetyCuts/Results/Augmented-Biscotti.result" } ,				
				{
					"section-4.2-biscotti",
					"SafetyCuts/Tests/dIppolito/section-4.2-biscotti.lts",
					"ENV",
					"EXP",
					"C",
					"G1",
					"SafetyCuts/Results/dIppolito/section-4.2-biscotti.result" },

				{
						"section-4.64-bookstore",
						"SafetyCuts/Tests/dIppolito/section-4.64-bookstore.lts",
						"ENV",
						"EXP",
						"C",
						"G1",
						"SafetyCuts/Results/dIppolito/section-4.64-bookstore.result" },
						
				{
						"section-4.63-autonomousvehicles",
						"SafetyCuts/Tests/dIppolito/section-4.63-autonomousvehicles.lts",
						"PLANT",
						"EXP",
						"C",
						"G1",
						"SafetyCuts/Results/dIppolito/section-4.63-autonomousvehicles.result" },
				{
						"section-4.62-payandship",
						"SafetyCuts/Tests/dIppolito/section-4.62-payandship.lts",
						"ENV",
						"EXP",
						"C",
						"G1",
						"SafetyCuts/Results/dIppolito/section-4.62-payandship.result" },
				
				{
						"section-4.1-travelagency",
						"SafetyCuts/Tests/dIppolito/section-4.1-travelagency.lts",
						"ENV",
						"EXP",
						"C",
						"G1",
						"SafetyCuts/Results/dIppolito/section-4.1-travelagency.result" },
				{
						"section-3.83-bookstore",
						"SafetyCuts/Tests/dIppolito/section-3.83-bookstore.lts",
						"ENV",
						"EXP",
						"C",
						"G1",
						"SafetyCuts/Results/dIppolito/section-3.83-bookstore.result" },
				{
						"section-3.82-purchaseanddelivery",
						"SafetyCuts/Tests/dIppolito/section-3.82-purchaseanddelivery.lts",
						"ENV",
						"EXP",
						"C",
						"G1",
						"SafetyCuts/Results/dIppolito/section-3.82-purchaseanddelivery.result" },
				{
						"section-3.84-productioncell-small",
						"SafetyCuts/Tests/dIppolito/section-3.84-productioncell-small.lts",
						"ENV",
						"EXP",
						"C",
						"Objective",
						"SafetyCuts/Results/dIppolito/section-3.84-productioncell-small.result" },
				{
						"section-3.81-autonomousvehicles",
						"SafetyCuts/Tests/dIppolito/section-3.81-autonomousvehicles.lts",
						"ENV",
						"EXP",
						"C",
						"G1",
						"SafetyCuts/Results/dIppolito/section-3.81-autonomousvehicles.result" },

				{
						"arbiter",
						"SafetyCuts/Tests/arbiter.lts",
						"PROCS", "EXP", "C", "PETERSON",
						"SafetyCuts/Results/arbiter.result" },
				{
						"prod_cell_variant",
						"SafetyCuts/Tests/prod_cell_variant.lts",
						"Plant", "EXP", "C", "G1",
						"SafetyCuts/Results/prod_cell_variant.result" },
				{
						"tomAndJerryV12",
						"SafetyCuts/Tests/TomAndJerryV12.lts",
						"Tom_Jerry", "EXP", "C", "G1",
						"SafetyCuts/Results/TomAndJerryV12.result" },

						{
							"section-4.71-productioncell",
							"SafetyCuts/Tests/dIppolito/section-4.71-productioncell.lts",
							"ENV",
							"EXP",
							"C",
							"G1",
							"SafetyCuts/Results/dIppolito/section-4.71-productioncell.result" },
						{
						"section-4.61-productioncell",
						"SafetyCuts/Tests/dIppolito/section-4.61-productioncell.lts",
						"Plant",
						"EXP",
						"C",
						"G1",
						"SafetyCuts/Results/dIppolito/section-4.61-productioncell.result" },

						{
						"section-3.1-productioncell",
						"SafetyCuts/Tests/dIppolito/section-3.1-productioncell.lts",
						"ENV",
						"EXP",
						"C",
						"Objective",
						"SafetyCuts/Results/dIppolito/section-3.1-productioncell.result" },
						
		};
	}

	private LTS<Long, String> exp;
	private String testName;

	//TODO FIX 	@Test(dataProvider = "oppositeGameTest")
	private void OppositeSafeGameTest(String testName, String filename, String mutName,
			String expName, String controllerName, String controllerGoalName,
			String resultFileName) throws Exception, IOException {
		this.testName = testName;
		
		exp = getExpected(filename, expName);		

		LTS<Long, String> mut = getMut(filename, mutName, controllerName,
				controllerGoalName, resultFileName);
		Assert.assertTrue(mut != null);
		LTSTestHelper.getInstance().removeUnusedActionsFromLTS(exp);
		LTSTestHelper.getInstance().removeUnusedActionsFromLTS(mut);

		MTSAdapter<Long, String> mutAdapter = new MTSAdapter<Long, String>(mut);
		MTSAdapter<Long, String> expAdapter = new MTSAdapter<Long, String>(exp);
		LTSSimulationSemantics simulationSemantics = new LTSSimulationSemantics();
		Assert.assertTrue(simulationSemantics.isARefinement(mutAdapter,
				expAdapter));
		Assert.assertTrue(simulationSemantics.isARefinement(expAdapter,
				mutAdapter));
	}

	protected LTS<Long, String> getMut(String filename, String mutName,
			String controllerName, String controllerGoalName,
			String resultFileName) {
		try {
			LTS<Long, String> env = LTSTestHelper.getInstance().getLTSFromFile(
					filename, mutName);
			ControllerGoal<String> controllerGoal = LTSTestHelper
					.getInstance().getGRControllerGoalFromFile(filename,
							controllerName);

			Set<LTS<Long, String>> safetyReqs = LTSTestHelper.getInstance()
					.getSafetyProcessesFromFile(filename, controllerName,
							controllerGoalName);

			CompositeState safetyComposite = new CompositeState(
					"safetyComposite", new Vector<CompactState>());
			if (safetyReqs == null)
				System.out.println("no safetyRequirement");
			else
				for (LTS<Long, String> safetyReq : safetyReqs) {
					safetyComposite.machines.add(MTSToAutomataConverter
							.getInstance().convert(new MTSAdapter(safetyReq),
									"safety_automata"));
				}
			safetyComposite.machines.add(MTSToAutomataConverter
					.getInstance().convert(new MTSAdapter(env),
							"environment"));
			safetyComposite.compose(new StandardOutput());
			env = new LTSAdapter<Long, String>(AutomataToMTSConverter
					.getInstance().convert(safetyComposite.composition),
					TransitionType.POSSIBLE);
			
			LTS<Long, String> envForController = new LTSAdapter<Long, String>(AutomataToMTSConverter
					.getInstance().convert(safetyComposite.composition),
					TransitionType.POSSIBLE); 

			ReportingPerfectInfoStateSpaceCuttingGRControlProblem grControlProblem = new ReportingPerfectInfoStateSpaceCuttingGRControlProblem(
					testName, env, envForController, controllerGoal, resultFileName, exp, -1l, false, false, false, false, false, false, false);


			LTS<Long, String> result = grControlProblem.solve();
			reportString += grControlProblem.getReportString(firstTime, false, "&", "|", "\\\\\n", false);
			if(firstTime)
				firstTime = false;
			return result;
		} catch (Exception e) {
			System.out.print(e.getStackTrace().toString());
			return null;
		}

	}

	protected LTS<Long, String> getExpected(String filename, String expectedName) {
		try {
			return LTSTestHelper.getInstance().getLTSFromFile(filename,
					expectedName);
		} catch (Exception e) {
			System.out.print(e.getStackTrace().toString());
			return null;
		}
	}
}
