package com.xenoage.util;

import static org.junit.Assert.*;

import org.junit.Test;

import com.xenoage.util.SortedList;


/**
 * Test cases for {@link SortedList}.
 *
 * @author Andreas Wenger
 */
public class SortedListTest
{
	
	@Test public void addWithDuplicatesTest()
	{
		boolean duplicates = true;
		//test presorted numbers
		SortedList<Integer> list = new SortedList<Integer>(duplicates);
		list.add(0);
		list.add(1);
		list.add(2);
		list.add(2);
		list.add(3);
		for (int i = 0; i < 5; i++)
		{
			int x = (i > 2 ? i - 1 : i);
			assertEquals(x, list.get(i).intValue());
		}
		//test mixed numbers
		list = new SortedList<Integer>(duplicates);
		list.add(2);
		list.add(1);
		list.add(3);
		list.add(2);
		list.add(0);
		for (int i = 0; i < 5; i++)
		{
			int x = (i > 2 ? i - 1 : i);
			assertEquals(x, list.get(i).intValue());
		}
	  //test reversely presorted numbers
		list = new SortedList<Integer>(duplicates);
		list.add(3);
		list.add(2);
		list.add(2);
		list.add(1);
		list.add(0);
		for (int i = 0; i < 5; i++)
		{
			int x = (i > 2 ? i - 1 : i);
			assertEquals(x, list.get(i).intValue());
		}
	}
	
	
	@Test public void addWithoutDuplicatesTest()
	{
		boolean duplicates = false;
		//test presorted numbers
		SortedList<Integer> list = new SortedList<Integer>(duplicates);
		list.add(0);
		list.add(0);
		list.add(1);
		list.add(2);
		list.add(2);
		list.add(3);
		for (int i = 0; i < 4; i++)
		{
			assertEquals(i, list.get(i).intValue());
		}
		//test mixed numbers
		list = new SortedList<Integer>(duplicates);
		list.add(2);
		list.add(0);
		list.add(1);
		list.add(0);
		list.add(0);
		list.add(3);
		list.add(2);
		list.add(0);
		for (int i = 0; i < 4; i++)
		{
			assertEquals(i, list.get(i).intValue());
		}
	  //test reversely presorted numbers
		list = new SortedList<Integer>(duplicates);
		list.add(3);
		list.add(2);
		list.add(2);
		list.add(1);
		list.add(0);
		list.add(0);
		for (int i = 0; i < 4; i++)
		{
			assertEquals(i, list.get(i).intValue());
		}
	}
	
	
	@Test public void addOrReplaceTest()
	{
		boolean duplicates = false;
		SortedList<Comp> list = new SortedList<Comp>(duplicates);
		//add numbers
		list.addOrReplace(new Comp(0, 100));
		list.addOrReplace(new Comp(0, 101));
		list.addOrReplace(new Comp(1, 102));
		list.addOrReplace(new Comp(2, 103));
		list.addOrReplace(new Comp(2, 104));
		list.addOrReplace(new Comp(3, 105));
		list.addOrReplace(new Comp(2, 106));
		list.addOrReplace(new Comp(0, 107));
		//test
		assertEquals(4, list.getSize());
		assertEquals(107, list.get(0).value);
		assertEquals(102, list.get(1).value);
		assertEquals(106, list.get(2).value);
		assertEquals(105, list.get(3).value);
	}
	
	
	@Test public void mergeTest()
	{
		SortedList<Integer> list1 = new SortedList<Integer>(false);
		list1.add(4);
		list1.add(6);
		list1.add(7);
		SortedList<Integer> list2 = new SortedList<Integer>(true);
		list2.add(5);
		list2.add(7);
		list2.add(7);
		//test with duplicates
		SortedList<Integer> merged = list1.merge(list2, true);
		assertEquals(6, merged.getSize());
		assertEquals(4, merged.get(0).intValue());
		assertEquals(5, merged.get(1).intValue());
		assertEquals(6, merged.get(2).intValue());
		assertEquals(7, merged.get(3).intValue());
		assertEquals(7, merged.get(4).intValue());
		assertEquals(7, merged.get(5).intValue());
		//test without duplicates
		merged = list1.merge(list2, false);
		assertEquals(4, merged.getSize());
		assertEquals(4, merged.get(0).intValue());
		assertEquals(5, merged.get(1).intValue());
		assertEquals(6, merged.get(2).intValue());
		assertEquals(7, merged.get(3).intValue());
	}
	
	
	public class Comp
		implements Comparable<Comp>
	{
		
		private int id;
		private int value;
		
		
		public Comp(int id, int value)
		{
			this.id = id;
			this.value = value;
		}
		
		
		public int getValue()
		{
			return value;
		}


		public int compareTo(Comp o)
		{
			return id < o.id ? -1 : id > o.id ? 1 : 0;
		}
		
	}
	

}
