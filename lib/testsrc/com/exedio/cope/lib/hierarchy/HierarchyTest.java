
package com.exedio.cope.lib.hierarchy;

import com.exedio.cope.lib.DatabaseLibTest;
import com.exedio.cope.lib.IntegrityViolationException;

public class HierarchyTest extends DatabaseLibTest
{
	public void testHierarchy()
			throws IntegrityViolationException
	{
		final FirstSub firstItem = new FirstSub(0);
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
		
		firstItem.delete();
	}

}
