package MTSATests.mts;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import junit.framework.TestCase;
import ltsa.lts.CompositeState;
import ltsa.ui.StandardOutput;
import MTSAClient.ac.ic.doc.mtsa.MTSCompiler;
import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSTools.ac.ic.doc.mtstools.model.MTSConstants;
import MTSTools.ac.ic.doc.mtstools.model.impl.CompositionRuleApplier;
import MTSTools.ac.ic.doc.mtstools.model.impl.MTSMultipleComposer;
import MTSTools.ac.ic.doc.mtstools.model.impl.WeakBisimulationSemantics;
import ltsa.ac.ic.doc.mtstools.util.fsp.AutomataToMTSConverter;
import MTSSynthesis.ar.dc.uba.model.condition.Fluent;
import MTSSynthesis.ar.dc.uba.model.condition.FluentImpl;
import ltsa.control.util.ControllerUtils;
import MTSATests.controller.ControllerTestsUtils;
import ltsa.dispatcher.TransitionSystemDispatcher;

public class TopStatesTests extends TestCase {
	public void testNoTopStates() throws Exception {

		Fluent fluentNorthFar = new FluentImpl("A",
				ControllerTestsUtils.buildSymbolSetFrom(new String[] { "a" }),
				ControllerTestsUtils.buildSymbolSetFrom(new String[] { "b" }),
				false);

		String withTopStates = "M = (b-> AB), AB = (a->AB | b->M).";
		String noTopStates = "	A = Q0,	Q0	= (b -> Q1),	Q1	= (b -> Q0		  |a -> Q2),	Q2	= (b -> Q0		  |a -> Q2).";

		MTSCompiler compiler = MTSCompiler.getInstance();
		MTS<Long, String> mtsWithTopStates = compiler.compileMTS("M",
				withTopStates);
		MTS<Long, String> mtsNoTop = compiler.compileMTS("A", noTopStates);

		MTS<Long, String> removeTopStates = ControllerUtils.removeTopStates(
				mtsWithTopStates, Collections.singleton(fluentNorthFar));

		WeakBisimulationSemantics bisimulationSemantics = new WeakBisimulationSemantics(
				Collections.emptySet());

		boolean bisimilar = TransitionSystemDispatcher.isRefinement(mtsNoTop,
				" original ", removeTopStates, " synthesised ",
				bisimulationSemantics, new StandardOutput());

		assertTrue(bisimilar);

	}

	public void testComponeBien() throws Exception {
		String noTopDO = "NotTopDO = TRUE,TRUE = (do -> TRUE | dc -> FALSE),FALSE = (do -> TRUE | dc -> FALSE).";
		String noTopFarNorth = "NotTopFarNorth = FALSE,	TRUE = (northFar -> TRUE | {northClose, southClose, southFar} -> FALSE),FALSE = (northFar -> TRUE | {northClose, southClose, southFar} -> FALSE).";
		String noTopFarSouth = "NotTopFarSouth = FALSE,TRUE = (southFar -> TRUE | {northClose, southClose, northFar} -> FALSE),FALSE = (southFar -> TRUE | {northClose, southClose, northFar} -> FALSE).";
		String model = "M = ToState1,ToState1 = (northFar -> State1),State1 = (ms -> do -> ToState2 | ms-> dc -> ToState3),ToState4 = (northFar -> State4),State4 = (ms -> do -> ToState2 | ms-> dc -> ToState3 ),ToState2 = (northClose -> State2),State2 = (mn -> do -> ToState1 | mn -> dc -> ToState4 | ms -> do -> ToState5 | ms -> dc -> ToState8), ToState3 = (northClose -> State3),State3 = (mn -> do -> ToState1 | mn -> ToState4 ),ToState5 = (southClose -> State5),State5 = (mn -> do -> ToState2 | mn -> dc -> ToState3 | ms -> do -> ToState6 | ms -> dc -> ToState7), 	ToState8 = (southClose -> State8),State8 = (ms -> do -> ToState6 | ms -> dc -> ToState7),	ToState6 = (southFar -> State6),State6 = (mn -> do -> ToState5 | mn-> dc -> ToState8),ToState7 = (southFar -> State7),State7 = (mn -> do -> ToState5 | ms-> dc -> ToState8 ).";
		MTSCompiler compiler = MTSCompiler.getInstance();

		CompositeState compiledNoTopDO = compiler.compileCompositeState(
				"NotTopDO", noTopDO);
		CompositeState compiledNoTopFarNorth = compiler.compileCompositeState(
				"NoTopFarNorth", noTopFarNorth);
		CompositeState compiledNoTopFarSouth = compiler.compileCompositeState(
				"NoTopFarSouth", noTopFarSouth);
		CompositeState compiledModel = compiler.compileCompositeState("M",
				model);

		Vector machines = new Vector();
		machines.add(compiledNoTopDO.getComposition());
		machines.add(compiledNoTopFarNorth.getComposition());
		machines.add(compiledNoTopFarSouth.getComposition());
		machines.add(compiledModel.getComposition());

		CompositeState c = new CompositeState(machines);
		c.compose(new StandardOutput());
		MTS<Long, String> compositeCompositionTopFree = AutomataToMTSConverter
				.getInstance().convert(c.getComposition());

		MTS<Long, String> mtsCompiledNoTopDO = compiler.compileMTS("MoTopDO",
				noTopDO);
		MTS<Long, String> mtscompiledNoTopFarNorth = compiler.compileMTS(
				"NoTopFarNorth", noTopFarNorth);
		MTS<Long, String> mtscompiledNoTopFarSouth = compiler.compileMTS(
				"NoTopFarSouth", noTopFarSouth);
		MTS<Long, String> mtscompiledModel = compiler.compileMTS("M", model);

		List toCompose = new LinkedList();
		toCompose.add(mtsCompiledNoTopDO);
		toCompose.add(mtscompiledNoTopFarNorth);
		toCompose.add(mtscompiledNoTopFarSouth);
		toCompose.add(mtscompiledModel);

		CompositionRuleApplier compositionRuleApplier = new CompositionRuleApplier();
		MTS mtsCompositionTopFree = new MTSMultipleComposer(
				compositionRuleApplier).compose(toCompose);

		assertBisimilar(compositeCompositionTopFree, mtsCompositionTopFree);

		Fluent fluentDoorClosed = new FluentImpl("DoorClosed",
				ControllerTestsUtils.buildSymbolSetFrom(new String[] { "do" }),
				ControllerTestsUtils.buildSymbolSetFrom(new String[] { "dc" }),
				true);
		Fluent fluentNorthFar = new FluentImpl("NorthFar",
				ControllerTestsUtils
						.buildSymbolSetFrom(new String[] { "northFar" }),
				ControllerTestsUtils.buildSymbolSetFrom(new String[] {
						"northClose", "southClose", "southFar" }), false);
		Fluent fluentSouthFar = new FluentImpl("SouthFar",
				ControllerTestsUtils
						.buildSymbolSetFrom(new String[] { "southFar" }),
				ControllerTestsUtils.buildSymbolSetFrom(new String[] {
						"northClose", "southClose", "northFar" }), false);

		MTS<Long, String> noTopForDoorClosed = ControllerUtils
				.getModelFrom(fluentDoorClosed);
		noTopForDoorClosed.addAction(MTSConstants.TAU);
		MTS<Long, String> noTopForNorthFar = ControllerUtils
				.getModelFrom(fluentNorthFar);
		noTopForNorthFar.addAction(MTSConstants.TAU);
		MTS<Long, String> noTopForSouthFar = ControllerUtils
				.getModelFrom(fluentSouthFar);
		noTopForSouthFar.addAction(MTSConstants.TAU);

		assertBisimilar(noTopForDoorClosed, mtsCompiledNoTopDO);
		assertBisimilar(noTopForNorthFar, mtscompiledNoTopFarNorth);
		assertBisimilar(noTopForSouthFar, mtscompiledNoTopFarSouth);

		Set<Fluent> fluents = new HashSet<Fluent>();
		fluents.add(fluentSouthFar);
		fluents.add(fluentNorthFar);
		fluents.add(fluentDoorClosed);

		MTS<Long, String> removeTopStates = ControllerUtils.removeTopStates(
				mtscompiledModel, fluents);

		assertBisimilar(removeTopStates, mtsCompositionTopFree);
	}

	private void assertBisimilar(MTS<Long, String> convert, MTS compose) {
		WeakBisimulationSemantics bisimulationSemantics = new WeakBisimulationSemantics(
				Collections.emptySet());

		boolean bisimilar = TransitionSystemDispatcher.isRefinement(convert,
				" original ", compose, " synthesised ", bisimulationSemantics,
				new StandardOutput());

		assertTrue(bisimilar);
	}

}
