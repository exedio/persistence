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

import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FindItemHierarchyEmptyTest extends TestWithEnvironment
{
	public FindItemHierarchyEmptyTest()
	{
		super(HierarchyEmptyTest.MODEL);
	}

	HierarchyEmptySub subItem;
	HierarchyEmptySuper superItem;

	@BeforeEach final void setUp()
	{
		subItem = new HierarchyEmptySub(0);
		superItem = new HierarchyEmptySuper(3);
	}

	@Test void test() throws NoSuchIDException
	{
		assertSame(subItem, model.getItem("HierarchyEmptySub-0")); // important to test with zero as well
		assertSame(superItem, model.getItem("HierarchyEmptySuper-1"));
		assertIDFails("HierarchyEmptySuper-0",  "item <0> does not exist", false);
		assertIDFails("HierarchyEmptySub-1",    "item <1> does not exist", false);
		assertIDFails("HierarchyEmptySuper-50", "item <50> does not exist", false);
		assertIDFails("HierarchyEmptySuper-" + 2147483646l, "item <2147483646> does not exist", false); // 2^31 - 2
		assertIDFails("HierarchyEmptySuper-" + 2147483647l, "item <2147483647> does not exist", false); // 2^31 - 1
		assertIDFails("HierarchyEmptySuper-" + 2147483648l, "must be less or equal 2147483647", true); // 2^31
		assertIDFails("HierarchyEmptySuper-" + 2147483649l, "must be less or equal 2147483647", true); // 2^31 + 1
		assertIDFails("HierarchyEmptySuper-" + Long.MAX_VALUE, "must be less or equal 2147483647", true);
	}
}
