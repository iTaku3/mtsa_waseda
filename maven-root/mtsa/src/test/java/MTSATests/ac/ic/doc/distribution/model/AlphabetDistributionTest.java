package MTSATests.ac.ic.doc.distribution.model;

import java.util.HashSet;
import java.util.Set;

import MTSSynthesis.ac.ic.doc.distribution.model.AlphabetDistribution;
import org.junit.Test;

/**
 * 
 * AlphabetDistribution tests
 * @author gsibay
 * 
 * 
 */
public class AlphabetDistributionTest {
	
	@Test
	public void testOneAlphabet() {
		Set<Set<String>> alphabets = new HashSet<Set<String>>();
		HashSet<String> alpha1 = new HashSet<String>();
		alpha1.add("a");
		alpha1.add("b");
		alphabets.add(alpha1);
		@SuppressWarnings("unused")
		AlphabetDistribution<String> alphabetDistribution = new AlphabetDistribution<String>(alphabets);
	}

	@Test
	public void testTwoAlphabet() {
		Set<Set<String>> alphabets = new HashSet<Set<String>>();
		HashSet<String> alpha1 = new HashSet<String>();
		alpha1.add("a");
		alpha1.add("b");
		
		HashSet<String> alpha2 = new HashSet<String>();
		alpha2.add("b");
		alpha2.add("c");
		
		alphabets.add(alpha2);
		@SuppressWarnings("unused")
		AlphabetDistribution<String> alphabetDistribution = new AlphabetDistribution<String>(alphabets);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNotEmpty() {
		Set<Set<String>> alphabets = new HashSet<Set<String>>();
		@SuppressWarnings("unused")
		AlphabetDistribution<String> alphabetDistribution = new AlphabetDistribution<String>(alphabets);
	}

	
	@Test(expected = IllegalArgumentException.class)
	public void testAnyAlphabetNotEmpty() {
		Set<Set<String>> alphabets = new HashSet<Set<String>>();
		alphabets.add(new HashSet<String>());
		@SuppressWarnings("unused")
		AlphabetDistribution<String> alphabetDistribution = new AlphabetDistribution<String>(alphabets);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testTwoAlphabetOneEmpty() {
		Set<Set<String>> alphabets = new HashSet<Set<String>>();
		HashSet<String> alpha1 = new HashSet<String>();
		alpha1.add("a");
		alpha1.add("b");
		
		HashSet<String> alpha2 = new HashSet<String>();
		
		alphabets.add(alpha2);
		@SuppressWarnings("unused")
		AlphabetDistribution<String> alphabetDistribution = new AlphabetDistribution<String>(alphabets);
	}

}
