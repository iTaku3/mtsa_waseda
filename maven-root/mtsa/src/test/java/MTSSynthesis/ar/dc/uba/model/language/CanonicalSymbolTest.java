package MTSSynthesis.ar.dc.uba.model.language;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import MTSSynthesis.ar.dc.uba.model.condition.Condition;
import MTSSynthesis.ar.dc.uba.model.condition.ConditionImpl;
import MTSSynthesis.ar.dc.uba.model.condition.Formula;

/**
 * @author gsibay
 *
 */
public class CanonicalSymbolTest {

	private Symbol createNoSymbolAndEmptyConditions() {
		return new CanonicalSymbol(new HashSet<String>());
	}

	private Symbol createSymbolAndEmptyConditions(String strSymbol) {
		return new CanonicalSymbol(new SingleSymbol(strSymbol),new HashSet<String>());
	}

	private Symbol createNoSymbolAndConditions(Set<String> conditionNames) {
		return new CanonicalSymbol(conditionNames);
	}

	private Symbol createSymbolAndConditions(String strSymbol, Set<String> conditionsNames) {
		return new CanonicalSymbol(new SingleSymbol(strSymbol), conditionsNames);
	}

	@Test
	public void testCanonicalSymbolEquals() {
		assertEquals(this.createNoSymbolAndEmptyConditions(), this.createNoSymbolAndEmptyConditions());
		assertEquals(this.createSymbolAndEmptyConditions("tt"), this.createSymbolAndEmptyConditions("tt"));
		
		Set<String> conditions = new HashSet<String>();
		conditions.add("c1");
		
		Set<String> otherConditions = new HashSet<String>();
		otherConditions.add("c1");
		
		assertEquals(this.createNoSymbolAndConditions(conditions), this.createNoSymbolAndConditions(conditions));
		assertEquals(this.createNoSymbolAndConditions(conditions), this.createNoSymbolAndConditions(otherConditions));

		assertEquals(this.createSymbolAndConditions("test", conditions), this.createSymbolAndConditions("test",conditions));
		assertEquals(this.createSymbolAndConditions("test", conditions), this.createSymbolAndConditions("test",otherConditions));
	}
	
	@Test
	public void testCanonicalSymbolNotEquals() {
		assertFalse(this.createNoSymbolAndEmptyConditions().equals(this.createSymbolAndEmptyConditions("tt")));
		
		Set<String> conditions = new HashSet<String>();
		conditions.add("c1");

		assertFalse(this.createSymbolAndEmptyConditions("tt").equals(this.createNoSymbolAndConditions(conditions)));
		
		assertFalse(this.createNoSymbolAndConditions(conditions).equals(this.createSymbolAndConditions("test",conditions)));
	}

}
