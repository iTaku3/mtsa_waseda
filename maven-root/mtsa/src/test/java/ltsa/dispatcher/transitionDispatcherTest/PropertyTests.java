package ltsa.dispatcher.transitionDispatcherTest;

import static ltsa.dispatcher.TransitionSystemDispatcher.isStronglyBisimilar;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import ltsa.lts.CompositeState;

import org.junit.Test;

import ltsa.ui.StandardOutput;
import MTSAClient.ac.ic.doc.mtsa.MTSCompiler;
import MTSTools.ac.ic.doc.mtstools.model.impl.WeakSimulationSemantics;
import ltsa.ac.ic.doc.mtstools.util.fsp.AutomataToMTSConverter;
import ltsa.dispatcher.TransitionSystemDispatcher;

/**
 * This class tests the functionality of the property keyword by comparing the
 * compose automata with the expected automata.
 * 
 * @author lnahabedian
 * 
 */
public class PropertyTests {
	@Test
	public void testPropertyInComposite() throws Exception {
		AutomataToMTSConverter converter = AutomataToMTSConverter.getInstance();
		String fsp = "LOWER=(a->b->LOWER).property ||PL=LOWER.";
		MTSCompiler compiler = MTSCompiler.getInstance();

		CompositeState compiled = compiler.compileCompositeState("PL", fsp);

		String expectedFSP = "LOWER=(a->b->LOWER).PL=(a->P1|b->ERROR),P1=(b->PL|a->ERROR).";
		CompositeState expected = compiler.compileCompositeState("PL",
				expectedFSP);

		WeakSimulationSemantics weakSimulationSemantics = new WeakSimulationSemantics(
				Collections.emptySet());

		// Check LTS simulation through MTS refinement.
		boolean refinement = TransitionSystemDispatcher.isRefinement(
				converter.convert(compiled.getComposition()), " original ",
				converter.convert(expected.getComposition()), " synthesised ",
				weakSimulationSemantics, new StandardOutput());

		assertTrue(refinement);
	}

	@Test
	public void testPropertyInCompositeWithOthersOperations() throws Exception {
		AutomataToMTSConverter converter = AutomataToMTSConverter.getInstance();
		String fsp = "LOWER=(a ->b-> LOWER)@{a,b}.HIGHER=(b->a->HIGHER)@{a,b}.property ||PL = LOWER.||CHECK = (PL||HIGHER).";
		MTSCompiler compiler = MTSCompiler.getInstance();

		CompositeState compiled = compiler.compileCompositeState("PL", fsp);

		String expectedFSP = "LOWER=(a->b->LOWER).PL=(a->P1|b->ERROR),P1=(b->PL|a->ERROR).HIGHER=(b->a->HIGHER).";
		CompositeState expected = compiler.compileCompositeState("PL",
				expectedFSP);

		WeakSimulationSemantics weakSimulationSemantics = new WeakSimulationSemantics(
				Collections.emptySet());

		// Check LTS simulation through MTS refinement.
		boolean refinement = TransitionSystemDispatcher.isRefinement(
				converter.convert(compiled.getComposition()), " original ",
				converter.convert(expected.getComposition()), " synthesised ",
				weakSimulationSemantics, new StandardOutput());

		assertTrue(refinement);
	}

	@Test
	public void testPropertyInCompositeWithAlotOfStates() throws Exception {
		AutomataToMTSConverter converter = AutomataToMTSConverter.getInstance();
		String fsp = "A=(a->b->c->d->A|b->B|c->C|d->D),B=(a->A),C=(a->A),D=(a->A).property ||PL=A.";
		MTSCompiler compiler = MTSCompiler.getInstance();

		CompositeState compiled = compiler.compileCompositeState("PL", fsp);

		String expectedFSP = "A=(a->AA|b->B|c->C|d->D),B=(a->A|{b,c,d}->ERROR),C=(a->A|{b,c,d}->ERROR),D=(a->A|{b,c,d}->ERROR),AA=(b->AAA|{a,c,d}->ERROR),AAA=(c->AAAA|{a,b,d}->ERROR),AAAA=(d->A|{a,b,c}->ERROR).";
		CompositeState expected = compiler.compileCompositeState("PL",
				expectedFSP);

		WeakSimulationSemantics weakSimulationSemantics = new WeakSimulationSemantics(
				Collections.emptySet());

		boolean refinement = isStronglyBisimilar(
				converter.convert(compiled.getComposition()), " original ",
				converter.convert(expected.getComposition()), " synthesised ", new StandardOutput());

		assertTrue(refinement);
	}

}
