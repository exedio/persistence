package com.exedio.cope.lib;

import com.exedio.cope.testmodel.FirstSub;
import com.exedio.cope.testmodel.SecondSub;
import com.exedio.cope.testmodel.Super;

public class HierarchyTest extends DatabaseLibTest
{
	public void testHierarchy()
			throws IntegrityViolationException
	{
		final FirstSub firstItem = new FirstSub(0);
		deleteOnTearDown(firstItem);
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
		deleteOnTearDown(secondItem);
		assertID(1, secondItem);
		assertEquals(2, secondItem.getSuperInt());
		assertEquals(null, secondItem.getFirstSubString());

		final SecondSub secondItem2 = new SecondSub(3);
		deleteOnTearDown(secondItem2);
		assertID(2, secondItem2);

		final FirstSub firstItem2 = new FirstSub(4);
		deleteOnTearDown(firstItem2);
		assertID(3, firstItem2);
		
		assertEquals(list(firstItem), firstItem.TYPE.search(Cope.equal(firstItem.firstSubString, "firstSubString")));
		assertEquals(list(), firstItem.TYPE.search(Cope.equal(firstItem.firstSubString, "firstSubStringX")));
		assertContains(firstItem, secondItem, firstItem2, secondItem2, Super.TYPE.search(null));
	}

}
