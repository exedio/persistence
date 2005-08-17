/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.testmodel.FirstSub;
import com.exedio.cope.testmodel.SecondSub;
import com.exedio.cope.testmodel.Super;

public class HierarchyTest extends TestmodelTest
{
	public void testHierarchy()
			throws IntegrityViolationException
	{
		// model Super
		assertEquals(null, Super.TYPE.getSupertype());
		assertEqualsUnmodifiable(list(FirstSub.TYPE, SecondSub.TYPE), Super.TYPE.getSubTypes());
		assertEqualsUnmodifiable(list(Super.superInt, Super.superString), Super.TYPE.getDeclaredAttributes());
		assertEqualsUnmodifiable(list(Super.superInt, Super.superString), Super.TYPE.getAttributes());
		assertEqualsUnmodifiable(list(Super.superInt, Super.superString), Super.TYPE.getDeclaredFeatures());
		assertEqualsUnmodifiable(list(Super.superInt, Super.superString), Super.TYPE.getFeatures());
		assertEquals(Super.TYPE, Super.superInt.getType());
		
		// model FirstSub
		assertEquals(Super.TYPE, FirstSub.TYPE.getSupertype());
		assertEqualsUnmodifiable(list(), FirstSub.TYPE.getSubTypes());
		assertEqualsUnmodifiable(list(FirstSub.firstSubString), FirstSub.TYPE.getDeclaredAttributes());
		assertEqualsUnmodifiable(list(Super.superInt, Super.superString, FirstSub.firstSubString), FirstSub.TYPE.getAttributes());
		assertEqualsUnmodifiable(list(FirstSub.firstSubString), FirstSub.TYPE.getDeclaredFeatures());
		assertEqualsUnmodifiable(list(Super.superInt, Super.superString, FirstSub.firstSubString), FirstSub.TYPE.getFeatures());
		assertEquals(FirstSub.TYPE, FirstSub.firstSubString.getType());

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
		
		firstItem.passivateCopeItem();
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
		
		assertEquals(list(firstItem), firstItem.TYPE.search(firstItem.firstSubString.equal("firstSubString")));
		assertEquals(list(), firstItem.TYPE.search(firstItem.firstSubString.equal("firstSubStringX")));
		assertContains(firstItem, secondItem, firstItem2, secondItem2, Super.TYPE.search(null));
	}

}
