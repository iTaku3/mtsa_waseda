/**
 * 
 */
package MTSTools.ac.ic.doc.commons.collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

/**
 * @author fdario
 *
 */
public class PowerSetTest {

	/**
	 * Test method for {@link MTSTools.ac.ic.doc.commons.collections.PowerSet#size()}.
	 */
	@Test
	public void testSize() {
		Set<Object> baseSet = new HashSet<Object>();
		int expectedSize = 1;
		for(int i = 0;i<10;i++) {
			Set<Set<Object>> powerSet = new PowerSet<Object>(baseSet);
			assertEquals(expectedSize, powerSet.size());
			expectedSize+=expectedSize;
			baseSet.add(new Object());
		}		
	}

	/**
	 * Test method for {@link MTSTools.ac.ic.doc.commons.collections.PowerSet#iterator()}.
	 */
	@Test
	public void testIterator() {
		Set<Integer> baseSet = new HashSet<Integer>(10);
		for(int i=0;i<10;i++) {
			baseSet.add(i);
		}
		
		Set<Integer> impliedBaseSet = new HashSet<Integer>();
		Set<Set<Integer>> impliedPowerSet = new HashSet<Set<Integer>>();
		Set<Set<Integer>> powerSet = new PowerSet<Integer>(baseSet);
		for (Set<Integer> set : powerSet) {
			impliedBaseSet.addAll(set);
			impliedPowerSet.add(set);
		}
		assertEquals(baseSet, impliedBaseSet);
		assertEquals(powerSet.size(), impliedPowerSet.size());
		
	}

	/**
	 * Test method for {@link MTSTools.ac.ic.doc.commons.collections.PowerSet#contains(java.lang.Object)}.
	 */
	@Test
	public void testContainsObject() {
		Set<Integer> baseSet = new HashSet<Integer>(10);
		for(int i=0;i<10;i++) {
			baseSet.add(i);
		}
		
		Set<Set<Integer>> powerSet = new PowerSet<Integer>(baseSet);
		
		this.testContainsObject(powerSet,baseSet.toArray(),new HashSet<Integer>(),0);		
	}

	private void testContainsObject(Set<Set<Integer>> powerSet, Object[] objects, HashSet<Integer> set, int index) {
		if (index == objects.length) {
			assertTrue(powerSet.contains(set));		
		} else {
			this.testContainsObject(powerSet, objects, set, index+1);
			set.add((Integer) objects[index]);
			this.testContainsObject(powerSet, objects, set, index+1);
			set.remove(objects[index]);
		}		
	}

}
