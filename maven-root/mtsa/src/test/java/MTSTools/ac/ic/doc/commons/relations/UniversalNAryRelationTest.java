package MTSTools.ac.ic.doc.commons.relations;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

public class UniversalNAryRelationTest {

	@Test
	public void testSize() {
		List<Set<Integer>> sets = new LinkedList<Set<Integer>>();
		Set<Integer> set1 = new HashSet<Integer>(Arrays.asList(new Integer[]{1,2}));
		Set<Integer> set2 = new HashSet<Integer>(Arrays.asList(new Integer[]{3,4,5}));
		Set<Integer> set3 = new HashSet<Integer>(Arrays.asList(new Integer[]{6,7,8,9}));
		
		UniversalNAryRelation<Integer> relation = new UniversalNAryRelation<Integer>(Collections.singletonList(set1));
		assertEquals(2, relation.size());
		
		sets.add(set1);
		sets.add(set2);
		relation = new UniversalNAryRelation<Integer>(sets);
		assertEquals(6, relation.size());
		
		sets.add(set3);
		relation = new UniversalNAryRelation<Integer>(sets);
		assertEquals(24, relation.size());
		
	}


	@Test
	public void testContainsObject() {
		List<Set<Integer>> sets = new LinkedList<Set<Integer>>();
		
		Set<Integer> set1 = new HashSet<Integer>(Arrays.asList(new Integer[]{1,2}));
		Set<Integer> set2 = new HashSet<Integer>(Arrays.asList(new Integer[]{3,4,5}));
		Set<Integer> set3 = new HashSet<Integer>(Arrays.asList(new Integer[]{6,7,8,9}));
		
		sets.add(set1);
		sets.add(set2);
		sets.add(set3);
		
		UniversalNAryRelation<Integer> relation = new UniversalNAryRelation<Integer>(sets);
		assertEquals(24, relation.size());
		
		
		List<Integer> tuple = Arrays.asList(new Integer[]{1,3,6});
		
		Assert.assertTrue(relation.contains(tuple));
		
		tuple = Arrays.asList(new Integer[]{1,6,6});
		
		Assert.assertFalse(relation.contains(tuple));
	}

	@Test
	public void testIterator() {
		List<Set<Integer>> sets = new LinkedList<Set<Integer>>();
		
		Set<Integer> set1 = new HashSet<Integer>(Arrays.asList(new Integer[]{1,2}));
		Set<Integer> set2 = new HashSet<Integer>(Arrays.asList(new Integer[]{3,4,5}));
		Set<Integer> set3 = new HashSet<Integer>(Arrays.asList(new Integer[]{6,7,8,9}));
		
		sets.add(set1);
		sets.add(set2);
		sets.add(set3);
		
		UniversalNAryRelation<Integer> relation = new UniversalNAryRelation<Integer>(sets);
		
		Set<List<Integer>> tuples = new HashSet<List<Integer>>();
		
		for (Iterator<List<Integer>> iterator = relation.iterator(); iterator.hasNext();) {
			List<Integer> tuple =  iterator.next();
			tuples.add(tuple);
			System.out.println(tuple);
		}
		
		System.out.println(tuples);
		
		assertEquals(24, tuples.size());
		
	}

}
