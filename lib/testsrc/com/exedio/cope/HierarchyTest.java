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


public class HierarchyTest extends AbstractLibTest
{
	public HierarchyTest()
	{
		super(Main.hierarchyModel);
	}
	
	public void testHierarchy()
			throws IntegrityViolationException
	{
		// model Super
		assertEquals(null, Super.TYPE.getSupertype());
		assertEqualsUnmodifiable(list(HierarchyFirstSub.TYPE, SecondSub.TYPE), Super.TYPE.getSubTypes());
		assertEqualsUnmodifiable(list(Super.superInt, Super.superString), Super.TYPE.getDeclaredAttributes());
		assertEqualsUnmodifiable(list(Super.superInt, Super.superString), Super.TYPE.getAttributes());
		assertEqualsUnmodifiable(list(Super.superInt, Super.superString, Super.superStringUpper), Super.TYPE.getDeclaredFeatures());
		assertEqualsUnmodifiable(list(Super.superInt, Super.superString, Super.superStringUpper), Super.TYPE.getFeatures());
		assertEquals(Super.TYPE, Super.superInt.getType());
		
		// model HierarchyFirstSub
		assertEquals(Super.TYPE, HierarchyFirstSub.TYPE.getSupertype());
		assertEqualsUnmodifiable(list(), HierarchyFirstSub.TYPE.getSubTypes());
		assertEqualsUnmodifiable(list(HierarchyFirstSub.firstSubString), HierarchyFirstSub.TYPE.getDeclaredAttributes());
		assertEqualsUnmodifiable(list(Super.superInt, Super.superString, HierarchyFirstSub.firstSubString), HierarchyFirstSub.TYPE.getAttributes());
		assertEqualsUnmodifiable(list(HierarchyFirstSub.firstSubString, HierarchyFirstSub.firstSubStringUpper), HierarchyFirstSub.TYPE.getDeclaredFeatures());
		assertEqualsUnmodifiable(list(Super.superInt, Super.superString, Super.superStringUpper, HierarchyFirstSub.firstSubString, HierarchyFirstSub.firstSubStringUpper), HierarchyFirstSub.TYPE.getFeatures());
		assertEquals(HierarchyFirstSub.TYPE, HierarchyFirstSub.firstSubString.getType());

		final HierarchyFirstSub firstItem = new HierarchyFirstSub(0);
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
		
		restartTransaction();
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

		final HierarchyFirstSub firstItem2 = new HierarchyFirstSub(4);
		deleteOnTearDown(firstItem2);
		assertID(3, firstItem2);
		
		assertEquals(list(firstItem), firstItem.TYPE.search(firstItem.firstSubString.equal("firstSubString")));
		assertEquals(list(), firstItem.TYPE.search(firstItem.firstSubString.equal("firstSubStringX")));
		assertContains(firstItem, secondItem, firstItem2, secondItem2, Super.TYPE.search(null));
	}

	public void testInheritedSearch() throws IntegrityViolationException
	{
		final HierarchyFirstSub firstSubItem1 = new HierarchyFirstSub( 10 );
		final HierarchyFirstSub firstSubItem2 = new HierarchyFirstSub( 10 );
		final HierarchyFirstSub firstSubItem3 = new HierarchyFirstSub( 11 );
		
		try
		{
			assertContains( firstSubItem1, firstSubItem2, HierarchyFirstSub.TYPE.search( Super.superInt.equal( 10 ) ) );
		}
		catch(RuntimeException e)
		{
			// TODO this is a bug
			assertEquals("function Super#superInt belongs to type Super, which is not a type of the query: HierarchyFirstSub, []", e.getMessage());
		}
		
		assertDelete( firstSubItem1 );
		assertDelete( firstSubItem2 );
		assertDelete( firstSubItem3 );		
	}

}
