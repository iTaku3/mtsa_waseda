package ltsa.lts.chart.util;

import MTSSynthesis.ar.dc.uba.model.language.Symbol;
import MTSSynthesis.ar.dc.uba.model.lsc.TriggeredScenario;
import MTSSynthesis.ar.dc.uba.model.structure.SynthesizedState;
import MTSSynthesis.ar.dc.uba.synthesis.SynthesisVisitor;
import MTSTools.ac.ic.doc.mtstools.model.MTS;

/**
 * Encapsulates the call to the synthesis algorithm.
 *
 * @author gsibay
 */
public class MTSSynthesiserFacade {

  private static MTSSynthesiserFacade instance = new MTSSynthesiserFacade();

  public static MTSSynthesiserFacade getInstance() {
    return instance;
  }

  private MTSSynthesiserFacade() {}

  public MTS<SynthesizedState, Symbol> synthesise(TriggeredScenario triggeredScenario) {
    return triggeredScenario.acceptSynthesisVisitor(SynthesisVisitor.getInstance());
  }
}
