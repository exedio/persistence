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

import com.exedio.cope.util.CacheInfo;



public class HierarchyEmptyTest extends AbstractLibTest
{
	public HierarchyEmptyTest()
	{
		super(Main.hierarchyEmptyModel);
	}
	
	public void testHierarchy()
			throws IntegrityViolationException, UniqueViolationException, NoSuchIDException
	{
		// model HierarchyEmptySuper
		assertEquals(null, HierarchyEmptySuper.TYPE.getSupertype());
		assertEqualsUnmodifiable(list(HierarchyEmptySub.TYPE), HierarchyEmptySuper.TYPE.getSubTypes());
		assertEqualsUnmodifiable(list(HierarchyEmptySuper.TYPE, HierarchyEmptySub.TYPE), HierarchyEmptySuper.TYPE.getTypesOfInstances());
		assertTrue(HierarchyEmptySuper.TYPE.isAssignableFrom(HierarchyEmptySuper.TYPE));
		assertTrue(HierarchyEmptySuper.TYPE.isAssignableFrom(HierarchyEmptySuper.TYPE));
		assertEqualsUnmodifiable(list(HierarchyEmptySuper.superInt), HierarchyEmptySuper.TYPE.getDeclaredAttributes());
		assertEqualsUnmodifiable(list(HierarchyEmptySuper.superInt), HierarchyEmptySuper.TYPE.getAttributes());
		assertEqualsUnmodifiable(list(
				HierarchyEmptySuper.superInt.getImplicitUniqueConstraint()
			), HierarchyEmptySuper.TYPE.getDeclaredUniqueConstraints());
		assertEqualsUnmodifiable(list(
				HierarchyEmptySuper.superInt.getImplicitUniqueConstraint()
			), HierarchyEmptySuper.TYPE.getUniqueConstraints());
		assertEqualsUnmodifiable(list(
				HierarchyEmptySuper.superInt,
				HierarchyEmptySuper.superInt.getImplicitUniqueConstraint()
			), HierarchyEmptySuper.TYPE.getDeclaredFeatures());
		assertEqualsUnmodifiable(list(
				HierarchyEmptySuper.superInt,
				HierarchyEmptySuper.superInt.getImplicitUniqueConstraint()
			), HierarchyEmptySuper.TYPE.getFeatures());
		assertEquals(HierarchyEmptySuper.superInt, HierarchyEmptySuper.TYPE.getDeclaredFeature("superInt"));
		assertEquals(null, HierarchyEmptySuper.TYPE.getDeclaredFeature("zack"));
		assertEquals(HierarchyEmptySuper.superInt, HierarchyEmptySuper.TYPE.getFeature("superInt"));
		assertEquals(null, HierarchyEmptySuper.TYPE.getFeature("zack"));
		assertFalse(HierarchyEmptySuper.TYPE.isAbstract());
		assertEquals(HierarchyEmptySuper.TYPE, HierarchyEmptySuper.superInt.getType());
		
		// model HierarchyEmptySub
		assertEquals(HierarchyEmptySuper.TYPE, HierarchyEmptySub.TYPE.getSupertype());
		assertEqualsUnmodifiable(list(), HierarchyEmptySub.TYPE.getSubTypes());
		assertEqualsUnmodifiable(list(HierarchyEmptySub.TYPE), HierarchyEmptySub.TYPE.getTypesOfInstances());
		assertFalse(HierarchyEmptySub.TYPE.isAssignableFrom(HierarchyEmptySuper.TYPE));
		assertTrue(HierarchyEmptySub.TYPE.isAssignableFrom(HierarchyEmptySub.TYPE));
		assertFalse(HierarchyEmptySub.TYPE.isAssignableFrom(HierarchySecondSub.TYPE));
		assertFalse(HierarchySecondSub.TYPE.isAssignableFrom(HierarchyEmptySub.TYPE));
		assertEqualsUnmodifiable(list(), HierarchyEmptySub.TYPE.getDeclaredAttributes());
		assertEqualsUnmodifiable(list(HierarchyEmptySuper.superInt), HierarchyEmptySub.TYPE.getAttributes());
		assertEqualsUnmodifiable(list(), HierarchyEmptySub.TYPE.getDeclaredUniqueConstraints());
		assertEqualsUnmodifiable(list(HierarchyEmptySub.superInt.getImplicitUniqueConstraint()), HierarchyEmptySub.TYPE.getUniqueConstraints());
		assertEqualsUnmodifiable(list(), HierarchyEmptySub.TYPE.getDeclaredFeatures());
		assertEqualsUnmodifiable(list(
				HierarchyEmptySuper.superInt,
				HierarchyEmptySuper.superInt.getImplicitUniqueConstraint()
			), HierarchyEmptySub.TYPE.getFeatures());
		assertEquals(null, HierarchyEmptySub.TYPE.getDeclaredFeature("superInt"));
		assertEquals(null, HierarchyEmptySub.TYPE.getDeclaredFeature("superString"));
		assertEquals(null, HierarchyEmptySub.TYPE.getDeclaredFeature("zack"));
		assertEquals(HierarchyEmptySub.superInt, HierarchyEmptySub.TYPE.getFeature("superInt"));
		assertEquals(null, HierarchyEmptySub.TYPE.getFeature("zack"));
		assertFalse(HierarchyEmptySub.TYPE.isAbstract());

		final HierarchyEmptySub subItem = new HierarchyEmptySub(0);
		deleteOnTearDown(subItem);
		assertID(0, subItem);
		assertEquals(0, subItem.getSuperInt());
		
		subItem.setSuperInt(2);
		assertEquals(2, subItem.getSuperInt());
		
		assertEquals(list(subItem), subItem.TYPE.search(subItem.superInt.equal(2)));
		assertEquals(list(subItem), subItem.TYPE.search(null));
		assertEquals(list(), subItem.TYPE.search(subItem.superInt.equal(1)));
		
		final HierarchyEmptySuper superItem = new HierarchyEmptySuper(3);
		deleteOnTearDown(superItem);
		assertID(1, superItem);
		assertEquals(3, superItem.getSuperInt());
		
		superItem.setSuperInt(4);
		assertEquals(4, superItem.getSuperInt());
		
		assertEquals(list(superItem), superItem.TYPE.search(superItem.superInt.equal(4)));
		assertEquals(list(subItem), superItem.TYPE.search(superItem.superInt.equal(2)));
		assertContains(superItem, subItem, superItem.TYPE.search(null));
		assertEquals(list(), superItem.TYPE.search(superItem.superInt.equal(1)));
		
		// test findByID
		assertSame(subItem, model.findByID("HierarchyEmptySub.0"));
		assertSame(superItem, model.findByID("HierarchyEmptySuper.1"));
		assertIDFails("HierarchyEmptySuper.0",  "no such id <HierarchyEmptySuper.0>, item <0> does not exist", false);
		assertIDFails("HierarchyEmptySub.1",    "no such id <HierarchyEmptySub.1>, item <1> does not exist", false);
		assertIDFails("noDotInThisString",      "no such id <noDotInThisString>, no dot in id", true);
		assertIDFails("noSuchType.x",           "no such id <noSuchType.x>, no such type noSuchType", true);
		assertIDFails("HierarchyEmptySuper.x",  "no such id <HierarchyEmptySuper.x>, wrong number format <x>", true);
		assertIDFails("HierarchyEmptySuper.92386591832651832659213865193456293456", "no such id <HierarchyEmptySuper.92386591832651832659213865193456293456>, wrong number format <92386591832651832659213865193456293456>", true);
		assertIDFails("HierarchyEmptySuper.-1", "no such id number <-1>, must be positive", true);
		assertIDFails("HierarchyEmptySuper.50", "no such id <HierarchyEmptySuper.50>, item <50> does not exist", false);
	}
	
	public void testModel()
	{
		model.checkDatabase();
		model.dropDatabaseConstraints();
		model.createDatabaseConstraints();
		
		assertEquals(list(
				HierarchyEmptySub.TYPE,
				HierarchyEmptySuper.TYPE
			), model.getTypes());
		assertEquals(list(
				HierarchyEmptySub.TYPE,
				HierarchyEmptySuper.TYPE
			), model.getConcreteTypes());
		
		final CacheInfo[] cacheInfo = model.getCacheInfo();
		assertEquals(HierarchyEmptySub.TYPE, cacheInfo[0].getType());
		assertEquals(HierarchyEmptySuper.TYPE, cacheInfo[1].getType());
		assertEquals(2, cacheInfo.length);
		
		assertNotNull(model.getCacheQueryInfo());
		model.getCacheQueryHistogram();
		assertNotNull(model.getConnectionPoolInfo());
		assertNotNull(model.getConnectionPoolInfo().getCounter());
	}
	
}
