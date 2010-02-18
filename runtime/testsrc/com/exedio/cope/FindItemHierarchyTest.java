/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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


public class FindItemHierarchyTest extends AbstractRuntimeTest
{
	public FindItemHierarchyTest()
	{
		super(HierarchyTest.MODEL);
	}
	
	public void testHierarchy()
			throws NoSuchIDException
	{
		// test persistence
		final HierarchyFirstSub firstItem = deleteOnTearDown(new HierarchyFirstSub(0));
		assertID(0, firstItem);
		assertSame(firstItem, model.getItem(HierarchyFirstSub.TYPE.getID()+".0"));
		assertIDFails("HierarchySuper.0", "type is abstract", true);
		
		final HierarchySecondSub secondItem = deleteOnTearDown(new HierarchySecondSub(2));
		assertID(1, secondItem);

		final HierarchySecondSub secondItem2 = deleteOnTearDown(new HierarchySecondSub(3));
		assertID(2, secondItem2);

		final HierarchyFirstSub firstItem2 = deleteOnTearDown(new HierarchyFirstSub(4));
		assertID(3, firstItem2);
	}
}
