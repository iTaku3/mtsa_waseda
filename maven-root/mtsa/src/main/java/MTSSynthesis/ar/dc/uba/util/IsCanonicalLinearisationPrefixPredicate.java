package MTSSynthesis.ar.dc.uba.util;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.SetUtils;
import org.apache.commons.lang.builder.EqualsBuilder;

import MTSSynthesis.ar.dc.uba.model.language.CanonicalSymbol;
import MTSSynthesis.ar.dc.uba.model.language.Symbol;
import MTSSynthesis.ar.dc.uba.model.language.Word;
import MTSSynthesis.ar.dc.uba.model.lsc.Chart;

/**
 * Returns true if the word is prefix of some
 * prechart's linerisation
 * @author gsibay
 *
 */
public class IsCanonicalLinearisationPrefixPredicate implements Predicate<Word> {

	/**
	 * Each bounded canonical linearisation prefix 
	 * stands for a set of canonical linearisation prefix.
	 * TODO: GS rename
	 */
	private Set<Word> boundedCanonicalLinearisationPrefixes;

	public IsCanonicalLinearisationPrefixPredicate(Chart prechart) {
		Set<Word> prefixes = WordUtils.getInstance().preffixes(prechart.calculateOperationalLanguageWords());
		//TODO: GS is the empty word a prefix? don't think so.
		this.boundedCanonicalLinearisationPrefixes = SetUtils.unmodifiableSet(prefixes);
	}
	
	@Override
	public boolean evaluate(Word word) {
		boolean isCanonicalLinearisationPrefix = false;
		
		Iterator<Word> boundedLinearisationsPrefixIt = this.boundedCanonicalLinearisationPrefixes.iterator();

		while(boundedLinearisationsPrefixIt.hasNext() && !isCanonicalLinearisationPrefix) {
			Word boundedLinearisationPrefix = boundedLinearisationsPrefixIt.next();
			isCanonicalLinearisationPrefix = this.isIncluded(word, boundedLinearisationPrefix);
		}
		return isCanonicalLinearisationPrefix;
	}

	private boolean isIncluded(Word word, Word boundedLinearisationPrefix) {
		// must have equal size
		if(word.length() != boundedLinearisationPrefix.length()) {
			return false;
		} else {
			boolean included = true;
			List<Symbol> wordSymbols = word.getSymbols();
			List<Symbol> boundedLinearisationSymbols = boundedLinearisationPrefix.getSymbols();

			// each pair of canonical symbol must:
			// a) have the same symbol (could be null for the epsilon case)
			// b) All conditions in the bounded linearisation prefix must be in the word

			Iterator<Symbol> boundedLinearisationSymbolsIt = boundedLinearisationSymbols.iterator();
			Iterator<Symbol> wordSymbolsIt = wordSymbols.iterator();
			while(boundedLinearisationSymbolsIt.hasNext() && included) {
				// both must be canonical symbols
				CanonicalSymbol currentWordSymbol = (CanonicalSymbol) wordSymbolsIt.next();
				CanonicalSymbol currentBoundedLinearisationSymbol = (CanonicalSymbol) boundedLinearisationSymbolsIt.next();
				
				// check a) and b)
				included = included && 
					(new EqualsBuilder().append(currentWordSymbol.getSymbol(), currentBoundedLinearisationSymbol.getSymbol()).isEquals()) && 
						(CollectionUtils.isSubCollection(currentBoundedLinearisationSymbol.getConditionsNames(), currentWordSymbol.getConditionsNames()));
			}
			return included;
		}
	}
	
}
