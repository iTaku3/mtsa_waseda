package MTSSynthesis.ar.dc.uba.model.condition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import MTSSynthesis.ar.dc.uba.model.language.SingleSymbol;
import MTSSynthesis.ar.dc.uba.model.language.Symbol;

/**
 * @author gsibay
 *
 */
public class ZetaFunctionTest {

	@Test
	public void testFluentsEquals() {
		Fluent aFluent = this.createFluent("a,b,c", "w,z", "phi_1", true);
		Fluent sameAsAFluent = this.createFluent("a,b,c", "w,z", "phi_1", true);
		
		Fluent differentFluent = this.createFluent("a,b,c", "w,z", "phi_40", true);
		Fluent veryDifferentFluent = this.createFluent("r,y,x", "k,z", "phi_42", false);
		Fluent invertedFluentAnotherName = this.createFluent("w,z", "a,b,c", "phi_40", true);
		
		assertEquals(aFluent, sameAsAFluent);
		assertEquals(aFluent, sameAsAFluent);
		assertEquals(sameAsAFluent, sameAsAFluent);
		
		assertTrue(!aFluent.equals(differentFluent));
		assertTrue(!aFluent.equals(veryDifferentFluent));
		assertTrue(!aFluent.equals(invertedFluentAnotherName));
		
	}
	
	@Test
	public void testEquals() {
		Set<Fluent> fluents = new HashSet<Fluent>();
		fluents.add(this.createFluent("a,b,c", "w,z", "phi_1", true));
		fluents.add(this.createFluent("a,b,c", "w,z", "phi_40", true));
		fluents.add(this.createFluent("r,y,x", "k,z", "phi_42", false));
		fluents.add(this.createFluent("w,z", "a,b,c", "phi_43", true));
		
		ZetaFunction zf1 = new ZetaFunction(fluents);
		
		ZetaFunction zf2 = new ZetaFunction(fluents);
		
		Set<Fluent> moreFluents = new HashSet<Fluent>();
		moreFluents.add(this.createFluent("a,b,c", "w,z", "phi_1", true));
		moreFluents.add(this.createFluent("a,b,c", "w,z", "phi_40", true));
		moreFluents.add(this.createFluent("r,y,x", "k,z", "phi_42", false));
		moreFluents.add(this.createFluent("w,z", "a,b,c", "phi_43", true));
		ZetaFunction zf3 = new ZetaFunction(moreFluents);
		
		assertEquals(zf1, zf1);
		assertEquals(zf1, zf2);
		assertEquals(zf1, zf3);
		
		assertEquals(zf2, zf1);
		assertEquals(zf2, zf2);
		assertEquals(zf2, zf3);
		
		assertEquals(zf3, zf1);
		assertEquals(zf3, zf2);
		assertEquals(zf3, zf3);
		
		fluents = new HashSet<Fluent>();
		fluents.add(this.createFluent("a,b,c", "w,z", "phi_1", false));
		fluents.add(this.createFluent("a,b,c", "w,z", "phi_40", false));
		fluents.add(this.createFluent("r,y,x", "k,z", "phi_42", false));
		fluents.add(this.createFluent("w,z", "a,b,c", "phi_43", false));
		ZetaFunction zf4 = new ZetaFunction(fluents);
		assertTrue(!zf1.equals(zf4));
		assertTrue(!zf2.equals(zf4));
		assertTrue(!zf3.equals(zf4));
		
		fluents = new HashSet<Fluent>();
		fluents.add(this.createFluent("a,b,c", "w,z", "phi_1", false));
		fluents.add(this.createFluent("a,b,c", "w,z", "phi_40", false));
		fluents.add(this.createFluent("r,y,x", "k,z", "phi_42", false));
		ZetaFunction zf5 = new ZetaFunction(fluents);
		assertTrue(!zf1.equals(zf5));
		assertTrue(!zf2.equals(zf5));
		assertTrue(!zf3.equals(zf5));
		assertTrue(!zf4.equals(zf5));
		
		fluents = new HashSet<Fluent>();
		fluents.add(this.createFluent("r,s,t", "w,z", "test1", false));
		fluents.add(this.createFluent("k,w", "z", "test2", true));
		ZetaFunction zf6 = new ZetaFunction(fluents);
		assertTrue(!zf1.equals(zf6));
		assertTrue(!zf2.equals(zf6));
		assertTrue(!zf3.equals(zf6));
		assertTrue(!zf4.equals(zf6));
		assertTrue(!zf5.equals(zf6));
	}
	
	@Test
	public void testCopy() {
		Set<Fluent> fluents = new HashSet<Fluent>();
		fluents.add(this.createFluent("a,b,c", "w,z", "phi_1", true));
		fluents.add(this.createFluent("a,b,c", "w,z", "phi_40", true));
		fluents.add(this.createFluent("r,y,x", "k,z", "phi_42", true));
		
		ZetaFunction zf1 = new ZetaFunction(fluents);
		ZetaFunction zf2 = zf1.getCopy();
		
		assertEquals(zf1, zf2);
		
		zf2.apply(new SingleSymbol("w"));
		
		
		assertTrue(!zf2.equals(zf1));
		
		fluents = new HashSet<Fluent>();
		fluents.add(this.createFluent("a,b,c", "w,z", "phi_1", false));
		fluents.add(this.createFluent("a,b,c", "w,z", "phi_40", false));
		fluents.add(this.createFluent("r,y,x", "k,z", "phi_42", false));
		ZetaFunction zf3 = new ZetaFunction(fluents);
		
		assertTrue(!zf2.equals(zf3));
		
		zf2.apply(new SingleSymbol("k"));
		
		assertTrue(!zf2.equals(zf1));
		assertEquals(zf2, zf3);
		
		zf2.apply(new SingleSymbol("b"));
		assertTrue(!zf2.equals(zf1));
		assertTrue(!zf2.equals(zf3));
		
		zf2.apply(new SingleSymbol("y"));
		assertTrue(!zf2.equals(zf3));
		assertEquals(zf2, zf1);
	}
	
	private Fluent createFluent(String initiating, String terminating, String name, boolean initialValue) {
		return new FluentImpl(name, this.toSymbolSet(initiating.split(",")), this.toSymbolSet(terminating.split(",")), initialValue);
	}

	private Set<Symbol> toSymbolSet(String[] symbolsAsString) {
		Set<Symbol> symbolSet = new HashSet<Symbol>();
		for (int i = 0; i < symbolsAsString.length; i++) {
			String symbolAsString = StringUtils.trim(symbolsAsString[i]);
			symbolSet.add(new SingleSymbol(symbolAsString));
		}
		return symbolSet;
	}
}

