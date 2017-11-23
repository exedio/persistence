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

public class FindItemHierarchyTest extends TestWithEnvironment
{
	public FindItemHierarchyTest()
	{
		super(HierarchyTest.MODEL);
	}

	HierarchyFirstSub firstItem;
	HierarchySecondSub secondItem;
	HierarchySecondSub secondItem2;
	HierarchyFirstSub firstItem2;

	@BeforeEach final void setUp()
	{
		firstItem = new HierarchyFirstSub(0);
		secondItem = new HierarchySecondSub(2);
		secondItem2 = new HierarchySecondSub(3);
		firstItem2 = new HierarchyFirstSub(4);
	}

	@Test void testHierarchy()
			throws NoSuchIDException
	{
		assertSame(firstItem, model.getItem("HierarchyFirstSub-0"));
		assertSame(firstItem, model.getItem(HierarchyFirstSub.TYPE.getID()+"-0"));
		assertIDFails("HierarchySuper-0", "type is abstract", true);
		assertSame(secondItem, model.getItem("HierarchySecondSub-1"));
		assertSame(secondItem2, model.getItem("HierarchySecondSub-2"));
		assertSame(firstItem2, model.getItem("HierarchyFirstSub-3"));
	}
}
