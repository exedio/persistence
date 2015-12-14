/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.exedio.cope;

import org.junit.Test;

public class FindItemHierarchyEmptyTest extends AbstractRuntimeModelTest
{
	public FindItemHierarchyEmptyTest()
	{
		super(HierarchyEmptyTest.MODEL);
	}

	HierarchyEmptySub subItem;
	HierarchyEmptySuper superItem;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		subItem = new HierarchyEmptySub(0);
		superItem = new HierarchyEmptySuper(3);
	}

	@Test public void test() throws NoSuchIDException
	{
		assertSame(subItem, model.getItem("HierarchyEmptySub-0")); // important to test with zero as well
		assertSame(superItem, model.getItem("HierarchyEmptySuper-1"));
		assertIDFails("HierarchyEmptySuper-0",  "item <0> does not exist", false);
		assertIDFails("HierarchyEmptySub-1",    "item <1> does not exist", false);
		assertIDFails("noDotInThisString",      "no separator '-' in id", true);
		assertIDFails("noSuchType-x",           "type <noSuchType> does not exist", true);
		assertIDFails("HierarchyEmptySuper-x",  "wrong number format <x>", true);
		assertIDFails("HierarchyEmptySuper-",   "wrong number format <>" , true);
		assertIDFails("HierarchyEmptySuper-92386591832651832659213865193456293456",
															 "wrong number format <92386591832651832659213865193456293456>", true);
		assertIDFails("HierarchyEmptySuper--1", "type <HierarchyEmptySuper-> does not exist", true);
		assertIDFails("HierarchyEmptySuper-50", "item <50> does not exist", false);
		assertIDFails("HierarchyEmptySuper-" + Long.MIN_VALUE, "type <HierarchyEmptySuper-> does not exist", true);
		assertIDFails("HierarchyEmptySuper-" + 2147483646l, "item <2147483646> does not exist", false); // 2^31 - 2
		assertIDFails("HierarchyEmptySuper-" + 2147483647l, "item <2147483647> does not exist", false); // 2^31 - 1
		assertIDFails("HierarchyEmptySuper-" + 2147483648l, "does not fit in 31 bit", true); // 2^31
		assertIDFails("HierarchyEmptySuper-" + 2147483649l, "does not fit in 31 bit", true); // 2^31 + 1
		assertIDFails("HierarchyEmptySuper-" + Long.MAX_VALUE, "does not fit in 31 bit", true);
		assertIDFails("HierarchyEmptySub-00"   , "has leading zeros", true);
		assertIDFails("HierarchyEmptySub-000"  , "has leading zeros", true);
		assertIDFails("HierarchyEmptySuper-01" , "has leading zeros", true);
		assertIDFails("HierarchyEmptySuper-001", "has leading zeros", true);
	}
}
