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
		assertEqualsUnmodifiable(list(HierarchyFirstSub.TYPE, HierarchySecondSub.TYPE), HierarchySuper.TYPE.getTypesOfInstances());
		assertTrue(HierarchySuper.TYPE.isAssignableFrom(HierarchySuper.TYPE));
		assertTrue(HierarchySuper.TYPE.isAssignableFrom(HierarchyFirstSub.TYPE));
		assertEqualsUnmodifiable(list(HierarchySuper.superInt, HierarchySuper.superString), HierarchySuper.TYPE.getDeclaredAttributes());
		assertEqualsUnmodifiable(list(HierarchySuper.superInt, HierarchySuper.superString), HierarchySuper.TYPE.getAttributes());
		assertEqualsUnmodifiable(list(HierarchySuper.superInt, HierarchySuper.superString, HierarchySuper.superStringUpper), HierarchySuper.TYPE.getDeclaredFeatures());
		assertEqualsUnmodifiable(list(HierarchySuper.superInt, HierarchySuper.superString, HierarchySuper.superStringUpper), HierarchySuper.TYPE.getFeatures());
		assertTrue(HierarchySuper.TYPE.isAbstract());
		assertEquals(HierarchySuper.TYPE, HierarchySuper.superInt.getType());
		
		// model HierarchyFirstSub
		assertEquals(HierarchySuper.TYPE, HierarchyFirstSub.TYPE.getSupertype());
		assertEqualsUnmodifiable(list(), HierarchyFirstSub.TYPE.getSubTypes());
		assertEqualsUnmodifiable(list(HierarchyFirstSub.TYPE), HierarchyFirstSub.TYPE.getTypesOfInstances());
		assertFalse(HierarchyFirstSub.TYPE.isAssignableFrom(HierarchySuper.TYPE));
		assertTrue(HierarchyFirstSub.TYPE.isAssignableFrom(HierarchyFirstSub.TYPE));
		assertFalse(HierarchyFirstSub.TYPE.isAssignableFrom(HierarchySecondSub.TYPE));
		assertFalse(HierarchySecondSub.TYPE.isAssignableFrom(HierarchyFirstSub.TYPE));
		assertEqualsUnmodifiable(list(HierarchyFirstSub.firstSubString), HierarchyFirstSub.TYPE.getDeclaredAttributes());
		assertEqualsUnmodifiable(list(HierarchySuper.superInt, HierarchySuper.superString, HierarchyFirstSub.firstSubString), HierarchyFirstSub.TYPE.getAttributes());
		assertEqualsUnmodifiable(list(HierarchyFirstSub.firstSubString, HierarchyFirstSub.firstSubStringUpper), HierarchyFirstSub.TYPE.getDeclaredFeatures());
		assertEqualsUnmodifiable(list(HierarchySuper.superInt, HierarchySuper.superString, HierarchySuper.superStringUpper, HierarchyFirstSub.firstSubString, HierarchyFirstSub.firstSubStringUpper), HierarchyFirstSub.TYPE.getFeatures());
		assertFalse(HierarchyFirstSub.TYPE.isAbstract());
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
		assertEquals(list(HierarchySingleSub.TYPE), HierarchySingleSuper.TYPE.getTypesOfInstances());
		assertEquals(list(), HierarchySingleSub.TYPE.getSubTypes());
		assertEquals(list(HierarchySingleSub.TYPE), HierarchySingleSub.TYPE.getTypesOfInstances());
		assertTrue(HierarchySingleSuper.TYPE.isAbstract());
		assertFalse(HierarchySingleSub.TYPE.isAbstract());
		
		final HierarchySingleSub singleSub1a = new HierarchySingleSub();
		deleteOnTearDown(singleSub1a);
		singleSub1a.setSubString("a");
		singleSub1a.setSuperInt(new Integer(1));
		final HierarchySingleSub singleSub1b = new HierarchySingleSub(1, "b");
		deleteOnTearDown(singleSub1b);
		final HierarchySingleSub singleSub2a = new HierarchySingleSub(2, "a");
		deleteOnTearDown(singleSub2a);
		assertContains(singleSub1a, singleSub1b, singleSub1a.TYPE.search(HierarchySingleSuper.superInt.equal(1)));
		assertContains(singleSub1a, singleSub1b, HierarchySingleSuper.TYPE.search(HierarchySingleSuper.superInt.equal(1)));
		assertContains(singleSub1a, singleSub2a, singleSub1a.TYPE.search(singleSub1a.subString.equal("a")));
		assertContains(singleSub1a, singleSub1a.TYPE.search(HierarchySingleSuper.superInt.equal(1).and(singleSub1a.subString.equal("a"))));
		
		restartTransaction();
		assertContains(singleSub1a, singleSub1a.TYPE.search(HierarchySingleSuper.superInt.equal(1).and(singleSub1a.subString.equal("a"))));
		assertEquals("a", singleSub2a.getSubString());
		assertEquals(new Integer(1), singleSub1b.getSuperInt());
		
		// test polymorphic pointers
		assertEquals(null, singleSub1a.getHierarchySuper());
		assertEquals(list(null), new Query(singleSub1a.hierarchySuper, singleSub1a.TYPE, singleSub1a.superInt.equal(1).and(singleSub1a.subString.equal("a"))).search());
		singleSub1a.setHierarchySuper( firstItem );
		assertEquals(firstItem, singleSub1a.getHierarchySuper());
		assertEquals(list(firstItem), new Query(singleSub1a.hierarchySuper, singleSub1a.TYPE, singleSub1a.superInt.equal(1).and(singleSub1a.subString.equal("a"))).search());
		assertEquals(list(singleSub1a), singleSub1a.TYPE.search(singleSub1a.hierarchySuper.equal(firstItem)));
		restartTransaction();
		assertEquals(firstItem, singleSub1a.getHierarchySuper());
		assertEquals(list(firstItem), new Query(singleSub1a.hierarchySuper, singleSub1a.TYPE, singleSub1a.superInt.equal(1).and(singleSub1a.subString.equal("a"))).search());
		assertEquals(list(singleSub1a), singleSub1a.TYPE.search(singleSub1a.hierarchySuper.equal(firstItem)));

		singleSub1a.setHierarchySuper(secondItem2);
		assertEquals(secondItem2, singleSub1a.getHierarchySuper());
		assertEquals(list(secondItem2), new Query(singleSub1a.hierarchySuper, singleSub1a.TYPE, singleSub1a.superInt.equal(1).and(singleSub1a.subString.equal("a"))).search());
		assertEquals(list(singleSub1a), singleSub1a.TYPE.search(singleSub1a.hierarchySuper.equal(secondItem2)));
		restartTransaction();
		assertEquals(secondItem2, singleSub1a.getHierarchySuper());
		assertEquals(list(secondItem2), new Query(singleSub1a.hierarchySuper, singleSub1a.TYPE, singleSub1a.superInt.equal(1).and(singleSub1a.subString.equal("a"))).search());
		assertEquals(list(singleSub1a), singleSub1a.TYPE.search(singleSub1a.hierarchySuper.equal(secondItem2)));

		singleSub1a.setHierarchySuper(null);
		assertEquals(null, singleSub1a.getHierarchySuper());
		assertEquals(list(null), new Query(singleSub1a.hierarchySuper, singleSub1a.TYPE, singleSub1a.superInt.equal(1).and(singleSub1a.subString.equal("a"))).search());
		restartTransaction();
		assertEquals(null, singleSub1a.getHierarchySuper());
		assertEquals(list(null), new Query(singleSub1a.hierarchySuper, singleSub1a.TYPE, singleSub1a.superInt.equal(1).and(singleSub1a.subString.equal("a"))).search());
	}
	
	public void testPolymorphicQueryInvalidation()
	{
		final HierarchyFirstSub item = new HierarchyFirstSub(10);
		deleteOnTearDown(item);
		
		final Query q1 = new Query(item.TYPE, item.superInt.equal(10));
		final Query q2 = new Query(item.TYPE, item.superInt.equal(20));
		assertEquals(list(item), q1.search());
		assertEquals(list(), q2.search());
		
		item.setSuperInt(20);
		assertEquals(list(), q1.search()); // TODO: assertEquals(list(item), q1.search());
		assertEquals(list(item), q2.search()); // TODO: assertEquals(list(), q2.search());
	}

}
