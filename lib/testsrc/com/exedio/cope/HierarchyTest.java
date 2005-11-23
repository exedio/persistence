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
		// model HierarchySuper
		assertEquals(null, HierarchySuper.TYPE.getSupertype());
		assertEqualsUnmodifiable(list(HierarchyFirstSub.TYPE, HierarchySecondSub.TYPE), HierarchySuper.TYPE.getSubTypes());
		assertTrue(HierarchySuper.TYPE.isAssignableFrom(HierarchySuper.TYPE));
		assertTrue(HierarchySuper.TYPE.isAssignableFrom(HierarchyFirstSub.TYPE));
		assertEqualsUnmodifiable(list(HierarchySuper.superInt, HierarchySuper.superString), HierarchySuper.TYPE.getDeclaredAttributes());
		assertEqualsUnmodifiable(list(HierarchySuper.superInt, HierarchySuper.superString), HierarchySuper.TYPE.getAttributes());
		assertEqualsUnmodifiable(list(HierarchySuper.superInt, HierarchySuper.superString, HierarchySuper.superStringUpper), HierarchySuper.TYPE.getDeclaredFeatures());
		assertEqualsUnmodifiable(list(HierarchySuper.superInt, HierarchySuper.superString, HierarchySuper.superStringUpper), HierarchySuper.TYPE.getFeatures());
		assertEquals(HierarchySuper.TYPE, HierarchySuper.superInt.getType());
		
		// model HierarchyFirstSub
		assertEquals(HierarchySuper.TYPE, HierarchyFirstSub.TYPE.getSupertype());
		assertEqualsUnmodifiable(list(), HierarchyFirstSub.TYPE.getSubTypes());
		assertFalse(HierarchyFirstSub.TYPE.isAssignableFrom(HierarchySuper.TYPE));
		assertTrue(HierarchyFirstSub.TYPE.isAssignableFrom(HierarchyFirstSub.TYPE));
		assertFalse(HierarchyFirstSub.TYPE.isAssignableFrom(HierarchySecondSub.TYPE));
		assertFalse(HierarchySecondSub.TYPE.isAssignableFrom(HierarchyFirstSub.TYPE));
		assertEqualsUnmodifiable(list(HierarchyFirstSub.firstSubString), HierarchyFirstSub.TYPE.getDeclaredAttributes());
		assertEqualsUnmodifiable(list(HierarchySuper.superInt, HierarchySuper.superString, HierarchyFirstSub.firstSubString), HierarchyFirstSub.TYPE.getAttributes());
		assertEqualsUnmodifiable(list(HierarchyFirstSub.firstSubString, HierarchyFirstSub.firstSubStringUpper), HierarchyFirstSub.TYPE.getDeclaredFeatures());
		assertEqualsUnmodifiable(list(HierarchySuper.superInt, HierarchySuper.superString, HierarchySuper.superStringUpper, HierarchyFirstSub.firstSubString, HierarchyFirstSub.firstSubStringUpper), HierarchyFirstSub.TYPE.getFeatures());
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
		
		final HierarchySecondSub secondItem = new HierarchySecondSub(2);
		deleteOnTearDown(secondItem);
		assertID(1, secondItem);
		assertEquals(2, secondItem.getSuperInt());
		assertEquals(null, secondItem.getFirstSubString());

		final HierarchySecondSub secondItem2 = new HierarchySecondSub(3);
		deleteOnTearDown(secondItem2);
		assertID(2, secondItem2);

		final HierarchyFirstSub firstItem2 = new HierarchyFirstSub(4);
		deleteOnTearDown(firstItem2);
		assertID(3, firstItem2);
		
		assertEquals(list(firstItem), firstItem.TYPE.search(firstItem.firstSubString.equal("firstSubString")));
		assertEquals(list(), firstItem.TYPE.search(firstItem.firstSubString.equal("firstSubStringX")));
		assertContains(firstItem, secondItem, firstItem2, secondItem2, HierarchySuper.TYPE.search(null));
		
		// model HierarchySingle
		assertEquals(list(HierarchySingleSub.TYPE), HierarchySingleSuper.TYPE.getSubTypes());
		
		final HierarchySingleSub singleSub1a = new HierarchySingleSub();
		deleteOnTearDown(singleSub1a);
		singleSub1a.setSubString("a");
		singleSub1a.setSuperInt(new Integer(1));
		final HierarchySingleSub singleSub1b = new HierarchySingleSub(1, "b");
		deleteOnTearDown(singleSub1b);
		final HierarchySingleSub singleSub2a = new HierarchySingleSub(2, "a");
		deleteOnTearDown(singleSub2a);
		assertContains(singleSub1a, singleSub1b, singleSub1a.TYPE.search(HierarchySingleSuper.superInt.equal(1)));
		assertContains(singleSub1a, singleSub2a, singleSub1a.TYPE.search(singleSub1a.subString.equal("a")));
		assertContains(singleSub1a, singleSub1a.TYPE.search(HierarchySingleSuper.superInt.equal(1).and(singleSub1a.subString.equal("a"))));
		
		restartTransaction();
		assertContains(singleSub1a, singleSub1a.TYPE.search(HierarchySingleSuper.superInt.equal(1).and(singleSub1a.subString.equal("a"))));
		assertEquals("a", singleSub2a.getSubString());
		assertEquals(new Integer(1), singleSub1b.getSuperInt());
		
		
		singleSub1a.setHierarchySuper( firstItem );
		
		try
		{
			singleSub1a.getHierarchySuper();
		}
		catch( NestingRuntimeException e )
		{
			if( e.getNestedCause() instanceof InstantiationException)
			{
				//TODO: this is a bug
			}
			else
			{
				throw e;
			}
		}		
	}

}
