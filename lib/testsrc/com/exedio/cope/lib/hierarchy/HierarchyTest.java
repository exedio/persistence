
package com.exedio.cope.lib.hierarchy;

import com.exedio.cope.lib.DatabaseLibTest;
import com.exedio.cope.lib.IntegrityViolationException;

public class HierarchyTest extends DatabaseLibTest
{
	public void testHierarchy()
			throws IntegrityViolationException
	{
		final Super superItem = new FirstSub(0);
		superItem.delete();
	}

}
