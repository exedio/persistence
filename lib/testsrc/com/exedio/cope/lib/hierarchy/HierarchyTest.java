
package com.exedio.cope.lib.hierarchy;

import com.exedio.cope.lib.DatabaseLibTest;
import com.exedio.cope.lib.IntegrityViolationException;

public class HierarchyTest extends DatabaseLibTest
{
	public void testHierarchy()
			throws IntegrityViolationException
	{
		final FirstSub firstItem = new FirstSub(0);
		assertID(0, firstItem);
		assertEquals(0, firstItem.getSuperInt());
		assertEquals(null, firstItem.getFirstSubString());
		
		firstItem.setSuperInt(2);
		assertEquals(2, firstItem.getSuperInt());
		assertEquals(null, firstItem.getFirstSubString());
		
		firstItem.setFirstSubString("firstSubString");
		assertEquals(2, firstItem.getSuperInt());
		assertEquals("firstSubString", firstItem.getFirstSubString());
		
		firstItem.passivate();
		assertEquals(2, firstItem.getSuperInt());
		assertEquals("firstSubString", firstItem.getFirstSubString());
		
		final SecondSub secondItem = new SecondSub(2);
		assertID(1, secondItem);
		assertEquals(2, secondItem.getSuperInt());
		assertEquals(null, secondItem.getFirstSubString());

		final SecondSub secondItem2 = new SecondSub(3);
		assertID(2, secondItem2);

		final FirstSub firstItem2 = new FirstSub(4);
		assertID(3, firstItem2);

		firstItem2.delete();
		secondItem2.delete();
		secondItem.delete();
		firstItem.delete();
	}

}
