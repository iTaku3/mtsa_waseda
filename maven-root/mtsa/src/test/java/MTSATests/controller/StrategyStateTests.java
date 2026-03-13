package MTSATests.controller;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.commons.lang.math.RandomUtils;
import org.junit.Test;

import MTSSynthesis.controller.gr.StrategyState;

public class StrategyStateTests extends TestCase {
	
	private static int limit = 1000;
	
	@Test
	public void testSymetricIntegerStrategyState() {
		for (int i = 0; i < limit; i++) {
			int x = RandomUtils.nextInt();
			int y = RandomUtils.nextInt();
			StrategyState<Integer,Integer> s1 = new StrategyState<Integer, Integer>(x, y);
			StrategyState<Integer,Integer> s2 = new StrategyState<Integer, Integer>(x, y);
			assertTrue(s1.equals(s2) && s2.equals(s1));
			assertTrue(s2.hashCode() == s1.hashCode());
		}
	}
	
	@Test
	public void testIntegerStrategyStateHashSet() {
		for (int i = 0; i < limit; i++) {
			int x = RandomUtils.nextInt();
			int y = RandomUtils.nextInt();
			StrategyState<Integer,Integer> s1 = new StrategyState<Integer, Integer>(x, y);
			StrategyState<Integer,Integer> s2 = new StrategyState<Integer, Integer>(x, y);
			Set<StrategyState<Integer, Integer>> set = new HashSet<StrategyState<Integer, Integer>>();
			set.add(s1);
			assertTrue(set.contains(s2));
			set.add(s2);
			assertTrue(set.size() == 1);
			set.remove(s2);
			assertTrue(set.size() == 0);
		}
	}
	
	@Test
	public void testIntegerStrategyState() {
		for (int i = 0; i < limit; i++) {
			Integer x1 = RandomUtils.nextInt();
			Integer y1 = RandomUtils.nextInt();
			Integer x2 = RandomUtils.nextInt();
			Integer y2 = RandomUtils.nextInt();
			
			StrategyState<Integer,Integer> s1 = new StrategyState<Integer, Integer>(x1, y1);
			StrategyState<Integer,Integer> s2 = new StrategyState<Integer, Integer>(x2, y2);
			
			if(x1.equals(x2) && y2.equals(y1)){
				assertTrue(s1.equals(s2) && s2.equals(s1));
				assertTrue(s2.hashCode() == s1.hashCode());
			}			
			else{
				assertFalse(s1.equals(s2) || s2.equals(s1));
				assertFalse(s2.hashCode() == s1.hashCode());
			}
		}
	}
}