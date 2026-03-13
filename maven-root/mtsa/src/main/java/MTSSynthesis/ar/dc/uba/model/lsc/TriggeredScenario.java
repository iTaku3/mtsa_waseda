package MTSSynthesis.ar.dc.uba.model.lsc;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import MTSTools.ac.ic.doc.mtstools.model.MTS;
import MTSSynthesis.ar.dc.uba.model.condition.Fluent;
import MTSSynthesis.ar.dc.uba.model.condition.FluentUtils;
import MTSSynthesis.ar.dc.uba.model.language.Alphabet;
import MTSSynthesis.ar.dc.uba.model.language.Symbol;
import MTSSynthesis.ar.dc.uba.model.structure.SynthesizedState;
import MTSSynthesis.ar.dc.uba.synthesis.SynthesisVisitor;

/**
 * Live Sequence Chart
 * @author gsibay
 *
 */
public abstract class TriggeredScenario {

	private Set<Fluent> fluents;
	private Chart prechart;
	private Chart mainchart;
	private Alphabet alphabet;
	
	public TriggeredScenario(Chart prechart, Chart mainchart, Set<Symbol> restricted, Set<Fluent> fluents) throws IllegalArgumentException {
		this.prechart = prechart;
		this.mainchart = mainchart;
		this.fluents = Collections.unmodifiableSet(fluents);
		this.alphabet = this.calculateAlphabet(prechart, mainchart, restricted, fluents);
	}

	private Alphabet calculateAlphabet(Chart prechart, Chart mainchart,
			Set<Symbol> restricted, Set<Fluent> fluents) {
		Set<Symbol> symbols = new HashSet<Symbol>(restricted);
		symbols.addAll(prechart.getInteractionSymbols());
		symbols.addAll(mainchart.getInteractionSymbols());
		symbols.addAll(FluentUtils.getInstance().getInvolvedSymbols(fluents));
		
		return new Alphabet(symbols);
	}

	public Set<Fluent> getFluents() {
		return this.fluents;
	}
	
	public Chart getPrechart() {
		return this.prechart;
	}

	public Chart getMainchart() {
		return mainchart;
	}

	public Alphabet getAlphabet() {
		return alphabet;
	}

	public abstract MTS<SynthesizedState, Symbol> acceptSynthesisVisitor(SynthesisVisitor synthesisVisitor);
	
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof TriggeredScenario) ) {
			return false;		
		}
		
		TriggeredScenario lsc = (TriggeredScenario) obj;
		
		return new EqualsBuilder()
			.append(this.getPrechart(),lsc.getPrechart())
			.append(this.getMainchart(),lsc.getMainchart())
			.append(this.getAlphabet(), lsc.getAlphabet())
			.append(this.getFluents(), lsc.getFluents())
			.append(this.getClass(), lsc.getClass())
			.isEquals();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		// the class is used for the hash code as 2 triggered scenarios 
		// can have equals fluents, mainchart and prechart and yet be
		// different because one is Existential and the other Universal
		return new HashCodeBuilder().append(
				this.getPrechart()).append(this.getMainchart()).append(
				this.getAlphabet()).append(this.getFluents()).append(this.getClass()).toHashCode();
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Fluents: ").append("\n").append(this.getFluents()).append("\n");
		buffer.append("Prechart: ").append(this.getPrechart().toString()).append("\n");
		buffer.append("Prechart's linearisations: ").append(this.getPrechart().getLinearisations().toString()).append("\n");
		buffer.append("Mainchart: ").append(this.getMainchart().toString()).append("\n");
		buffer.append("Mainchart's linearisations: ").append(this.getMainchart().getLinearisations().toString()).append("\n");
		buffer.append("Alphabet: ").append(this.getAlphabet().toString()).append("\n");
		return buffer.toString();
	}
}
