package com.xenoage.util.iterators;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.xenoage.util.iterators.MultiIt;


/**
 * Test cases for the {@link MultiIt} class.
 * 
 * @author Andreas Wenger
 */
public class MultiItTest
{

	@Test public void test()
	{
		List<Integer> list1 = Arrays.asList(1, 2, 3);
		List<Integer> list2 = Arrays.asList();
		List<Integer> list3 = Arrays.asList(4);
		List<Integer> list4 = Arrays.asList();
		MultiIt<Integer> it = new MultiIt<Integer>(list1, list2, list3, list4);
		for (int i = 1; i <= 4; i++)
		{
			Assert.assertTrue(it.hasNext());
			Assert.assertEquals(i, it.next().intValue());
		}
		Assert.assertFalse(it.hasNext());
	}
	
}
