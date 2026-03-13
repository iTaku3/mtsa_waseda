package MTSTools.ac.ic.doc.commons.relations;

import java.util.Arrays;
import java.util.List;



import org.junit.Assert;
import org.junit.Test;


public class HashNAryRelationTest {
	
	@Test
	public void tetAdd() {
		List<Integer> list1 = Arrays.asList(new Integer[]{1,2,3});
		List<Integer> list2 = Arrays.asList(new Integer[]{1,2,3});
		List<Integer> list3 = Arrays.asList(new Integer[]{2,2,3});
		
		NAryRelation<Integer> relation = new HashNAryRelation<Integer>(3);
		
		Assert.assertFalse(relation.contains(list1));
		relation.add(list1);
		Assert.assertTrue(relation.contains(list1));
		relation.remove(list2);
		Assert.assertFalse(relation.contains(list1));
		relation.add(list1);
		relation.add(list2);
		relation.add(list3);
		
		for (List<Integer> list : relation) {
			System.out.println(list);
		}
		
		
	}

}
