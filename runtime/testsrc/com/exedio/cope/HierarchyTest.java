/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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
import com.exedio.dsmf.Constraint;

public class HierarchyTest extends AbstractLibTest
{
	public HierarchyTest()
	{
		super(Main.hierarchyModel);
	}
	
	public void testHierarchy()
			throws NoSuchIDException
	{
		// model HierarchySuper
		assertEquals(null, HierarchySuper.TYPE.getSupertype());
		assertEqualsUnmodifiable(list(HierarchyFirstSub.TYPE, HierarchySecondSub.TYPE), HierarchySuper.TYPE.getSubTypes());
		assertEqualsUnmodifiable(list(HierarchySuper.TYPE, HierarchyFirstSub.TYPE, HierarchySecondSub.TYPE), HierarchySuper.TYPE.getSubTypesTransitively());
		assertEqualsUnmodifiable(list(HierarchyFirstSub.TYPE, HierarchySecondSub.TYPE), HierarchySuper.TYPE.getTypesOfInstances());
		assertTrue(HierarchySuper.TYPE.isAssignableFrom(HierarchySuper.TYPE));
		assertTrue(HierarchySuper.TYPE.isAssignableFrom(HierarchyFirstSub.TYPE));
		assertEqualsUnmodifiable(list(HierarchySuper.superInt, HierarchySuper.superString), HierarchySuper.TYPE.getDeclaredAttributes());
		assertEqualsUnmodifiable(list(HierarchySuper.superInt, HierarchySuper.superString), HierarchySuper.TYPE.getFields());
		assertEqualsUnmodifiable(list(
				HierarchySuper.superInt.getImplicitUniqueConstraint()
			), HierarchySuper.TYPE.getDeclaredUniqueConstraints());
		assertEqualsUnmodifiable(list(
				HierarchySuper.superInt.getImplicitUniqueConstraint()
			), HierarchySuper.TYPE.getUniqueConstraints());
		assertEqualsUnmodifiable(list(
				HierarchySuper.TYPE.getThis(),
				HierarchySuper.superInt,
				HierarchySuper.superInt.getImplicitUniqueConstraint(),
				HierarchySuper.superString,
				HierarchySuper.superStringUpper
			), HierarchySuper.TYPE.getDeclaredFeatures());
		assertEqualsUnmodifiable(list(
				HierarchySuper.TYPE.getThis(),
				HierarchySuper.superInt,
				HierarchySuper.superInt.getImplicitUniqueConstraint(),
				HierarchySuper.superString,
				HierarchySuper.superStringUpper
			), HierarchySuper.TYPE.getFeatures());
		assertEquals(HierarchySuper.superInt, HierarchySuper.TYPE.getDeclaredFeature("superInt"));
		assertEquals(HierarchySuper.superString, HierarchySuper.TYPE.getDeclaredFeature("superString"));
		assertEquals(null, HierarchySuper.TYPE.getDeclaredFeature("firstSubString"));
		assertEquals(null, HierarchySuper.TYPE.getDeclaredFeature("zack"));
		assertEquals(HierarchySuper.superInt, HierarchySuper.TYPE.getFeature("superInt"));
		assertEquals(HierarchySuper.superString, HierarchySuper.TYPE.getFeature("superString"));
		assertEquals(null, HierarchySuper.TYPE.getFeature("firstSubString"));
		assertEquals(null, HierarchySuper.TYPE.getFeature("zack"));
		assertTrue(HierarchySuper.TYPE.isAbstract());
		assertEquals(HierarchySuper.TYPE, HierarchySuper.superInt.getType());
		
		// model HierarchyFirstSub
		assertEquals(HierarchySuper.TYPE, HierarchyFirstSub.TYPE.getSupertype());
		assertEqualsUnmodifiable(list(), HierarchyFirstSub.TYPE.getSubTypes());
		assertEqualsUnmodifiable(list(HierarchyFirstSub.TYPE), HierarchyFirstSub.TYPE.getSubTypesTransitively());
		assertEqualsUnmodifiable(list(HierarchyFirstSub.TYPE), HierarchyFirstSub.TYPE.getTypesOfInstances());
		assertFalse(HierarchyFirstSub.TYPE.isAssignableFrom(HierarchySuper.TYPE));
		assertTrue(HierarchyFirstSub.TYPE.isAssignableFrom(HierarchyFirstSub.TYPE));
		assertFalse(HierarchyFirstSub.TYPE.isAssignableFrom(HierarchySecondSub.TYPE));
		assertFalse(HierarchySecondSub.TYPE.isAssignableFrom(HierarchyFirstSub.TYPE));
		assertEqualsUnmodifiable(list(HierarchyFirstSub.firstSubString), HierarchyFirstSub.TYPE.getDeclaredAttributes());
		assertEqualsUnmodifiable(list(HierarchySuper.superInt, HierarchySuper.superString, HierarchyFirstSub.firstSubString), HierarchyFirstSub.TYPE.getFields());
		assertEqualsUnmodifiable(list(
				HierarchyFirstSub.firstSubString.getImplicitUniqueConstraint()
			), HierarchyFirstSub.TYPE.getDeclaredUniqueConstraints());
		assertEqualsUnmodifiable(list(
				HierarchyFirstSub.superInt.getImplicitUniqueConstraint(),
				HierarchyFirstSub.firstSubString.getImplicitUniqueConstraint()
			), HierarchyFirstSub.TYPE.getUniqueConstraints());
		assertEqualsUnmodifiable(list(
				HierarchyFirstSub.TYPE.getThis(),
				HierarchyFirstSub.firstSubString,
				HierarchyFirstSub.firstSubString.getImplicitUniqueConstraint(),
				HierarchyFirstSub.firstSubStringUpper
			), HierarchyFirstSub.TYPE.getDeclaredFeatures());
		assertEqualsUnmodifiable(list(
				HierarchyFirstSub.TYPE.getThis(),
				HierarchySuper.superInt,
				HierarchySuper.superInt.getImplicitUniqueConstraint(),
				HierarchySuper.superString,
				HierarchySuper.superStringUpper,
				HierarchyFirstSub.firstSubString,
				HierarchyFirstSub.firstSubString.getImplicitUniqueConstraint(),
				HierarchyFirstSub.firstSubStringUpper
			), HierarchyFirstSub.TYPE.getFeatures());
		assertEquals(null, HierarchyFirstSub.TYPE.getDeclaredFeature("superInt"));
		assertEquals(null, HierarchyFirstSub.TYPE.getDeclaredFeature("superString"));
		assertEquals(HierarchyFirstSub.firstSubString, HierarchyFirstSub.TYPE.getDeclaredFeature("firstSubString"));
		assertEquals(null, HierarchyFirstSub.TYPE.getDeclaredFeature("zack"));
		assertEquals(HierarchyFirstSub.superInt, HierarchyFirstSub.TYPE.getFeature("superInt"));
		assertEquals(HierarchyFirstSub.superString, HierarchyFirstSub.TYPE.getFeature("superString"));
		assertEquals(HierarchyFirstSub.firstSubString, HierarchyFirstSub.TYPE.getFeature("firstSubString"));
		assertEquals(null, HierarchyFirstSub.TYPE.getFeature("zack"));
		assertFalse(HierarchyFirstSub.TYPE.isAbstract());
		assertEquals(HierarchyFirstSub.TYPE, HierarchyFirstSub.firstSubString.getType());

		final HierarchyFirstSub firstItem = new HierarchyFirstSub(0);
		deleteOnTearDown(firstItem);
		assertID(0, firstItem);
		assertEquals(0, firstItem.getSuperInt());
		assertEquals(null, firstItem.getFirstSubString());
		assertSame(firstItem, model.findByID(HierarchyFirstSub.TYPE.getID()+".0"));
		assertIDFails("HierarchySuper.0", "type is abstract", true);
		
		firstItem.setSuperInt(2);
		assertEquals(2, firstItem.getSuperInt());
		assertEquals(null, firstItem.getFirstSubString());
		
		firstItem.setFirstSubString("firstSubString");
		assertEquals(2, firstItem.getSuperInt());
		assertEquals("firstSubString", firstItem.getFirstSubString());
		
		restartTransaction();
		assertEquals(2, firstItem.getSuperInt());
		assertEquals("firstSubString", firstItem.getFirstSubString());
		firstItem.setSuperInt(0);
		
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
		assertEquals(list(HierarchySingleSuper.TYPE, HierarchySingleSub.TYPE), HierarchySingleSuper.TYPE.getSubTypesTransitively());
		assertEquals(list(HierarchySingleSub.TYPE), HierarchySingleSuper.TYPE.getTypesOfInstances());
		assertEquals(list(), HierarchySingleSub.TYPE.getSubTypes());
		assertEquals(list(HierarchySingleSub.TYPE), HierarchySingleSub.TYPE.getSubTypesTransitively());
		assertEquals(list(HierarchySingleSub.TYPE), HierarchySingleSub.TYPE.getTypesOfInstances());
		assertTrue(HierarchySingleSuper.TYPE.isAbstract());
		assertFalse(HierarchySingleSub.TYPE.isAbstract());
		
		final HierarchySingleSub singleSub1a = new HierarchySingleSub();
		deleteOnTearDown(singleSub1a);
		singleSub1a.setSubString("a");
		singleSub1a.setSuperInt(Integer.valueOf(1));
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
		assertEquals(Integer.valueOf(1), singleSub1b.getSuperInt());
		
		// test polymorphic pointers
		assertEquals(null, singleSub1a.getHierarchySuper());
		assertEquals(list((Object)null), new Query<HierarchySuper>(singleSub1a.hierarchySuper, singleSub1a.TYPE, singleSub1a.superInt.equal(1).and(singleSub1a.subString.equal("a"))).search());
		singleSub1a.setHierarchySuper( firstItem );
		assertEquals(firstItem, singleSub1a.getHierarchySuper());
		assertEquals(list(firstItem), new Query<HierarchySuper>(singleSub1a.hierarchySuper, singleSub1a.TYPE, singleSub1a.superInt.equal(1).and(singleSub1a.subString.equal("a"))).search());
		assertEquals(list(singleSub1a), singleSub1a.TYPE.search(singleSub1a.hierarchySuper.equal(firstItem)));
		restartTransaction();
		assertEquals(firstItem, singleSub1a.getHierarchySuper());
		assertEquals(list(firstItem), new Query<HierarchySuper>(singleSub1a.hierarchySuper, singleSub1a.TYPE, singleSub1a.superInt.equal(1).and(singleSub1a.subString.equal("a"))).search());
		assertEquals(list(singleSub1a), singleSub1a.TYPE.search(singleSub1a.hierarchySuper.equal(firstItem)));

		singleSub1a.setHierarchySuper(secondItem2);
		assertEquals(secondItem2, singleSub1a.getHierarchySuper());
		assertEquals(list(secondItem2), new Query<HierarchySuper>(singleSub1a.hierarchySuper, singleSub1a.TYPE, singleSub1a.superInt.equal(1).and(singleSub1a.subString.equal("a"))).search());
		assertEquals(list(singleSub1a), singleSub1a.TYPE.search(singleSub1a.hierarchySuper.equal(secondItem2)));
		restartTransaction();
		assertEquals(secondItem2, singleSub1a.getHierarchySuper());
		assertEquals(list(secondItem2), new Query<HierarchySuper>(singleSub1a.hierarchySuper, singleSub1a.TYPE, singleSub1a.superInt.equal(1).and(singleSub1a.subString.equal("a"))).search());
		assertEquals(list(singleSub1a), singleSub1a.TYPE.search(singleSub1a.hierarchySuper.equal(secondItem2)));

		singleSub1a.setHierarchySuper(null);
		assertEquals(null, singleSub1a.getHierarchySuper());
		assertEquals(list((Object)null), new Query<HierarchySuper>(singleSub1a.hierarchySuper, singleSub1a.TYPE, singleSub1a.superInt.equal(1).and(singleSub1a.subString.equal("a"))).search());
		restartTransaction();
		assertEquals(null, singleSub1a.getHierarchySuper());
		assertEquals(list((Object)null), new Query<HierarchySuper>(singleSub1a.hierarchySuper, singleSub1a.TYPE, singleSub1a.superInt.equal(1).and(singleSub1a.subString.equal("a"))).search());
		
		// test wrong attributes
		try
		{
			firstItem.get(secondItem.firstSubString);
			fail();
		}
		catch(RuntimeException e)
		{
			assertEquals("attribute "+secondItem.firstSubString+" does not belong to type "+firstItem.TYPE, e.getMessage());
		}
		try
		{
			secondItem.firstSubString.get(firstItem);
			fail();
		}
		catch(RuntimeException e)
		{
			assertEquals("attribute "+secondItem.firstSubString+" does not belong to type "+firstItem.TYPE, e.getMessage());
		}
		try
		{
			firstItem.set(secondItem.firstSubString, "zack");
			fail();
		}
		catch(RuntimeException e)
		{
			assertEquals("attribute "+secondItem.firstSubString+" does not belong to type "+firstItem.TYPE, e.getMessage());
		}
		try
		{
			firstItem.set(new SetValue[]{secondItem.firstSubString.map("zack")});
			fail();
		}
		catch(RuntimeException e)
		{
			assertEquals("attribute "+secondItem.firstSubString+" does not belong to type "+firstItem.TYPE, e.getMessage());
		}
		try
		{
			firstItem.TYPE.newItem(new SetValue[]{secondItem.firstSubString.map("zack")});
			fail();
		}
		catch(RuntimeException e)
		{
			assertEquals("attribute "+secondItem.firstSubString+" does not belong to type "+firstItem.TYPE, e.getMessage());
		}
	}
	
	public void testPolymorphicQueryInvalidation() throws UniqueViolationException
	{
		final HierarchyFirstSub item = new HierarchyFirstSub(10);
		deleteOnTearDown(item);
		
		final Query q1 = HierarchySuper.TYPE.newQuery(item.superInt.equal(10));
		final Query q2 = HierarchySuper.TYPE.newQuery(item.superInt.equal(20));
		assertEquals(list(item), q1.search());
		assertEquals(list(), q2.search());
		
		item.setSuperInt(20);
		assertEquals(list(), q1.search());
		assertEquals(list(item), q2.search());
	}

	public void testModel()
	{
		model.checkDatabase();
		model.dropDatabaseConstraints(Constraint.MASK_ALL);
		model.createDatabaseConstraints(Constraint.MASK_ALL);
		model.dropDatabaseConstraints(Constraint.MASK_PK|Constraint.MASK_FK);
		model.createDatabaseConstraints(Constraint.MASK_PK|Constraint.MASK_FK);
		model.dropDatabaseConstraints(Constraint.MASK_FK);
		model.createDatabaseConstraints(Constraint.MASK_FK);
		model.dropDatabaseConstraints(Constraint.MASK_UNIQUE);
		model.createDatabaseConstraints(Constraint.MASK_UNIQUE);
		model.dropDatabaseConstraints(Constraint.MASK_CHECK);
		model.createDatabaseConstraints(Constraint.MASK_CHECK);
		
		assertEquals(list(
				HierarchyFirstSub.TYPE,
				HierarchySecondSub.TYPE,
				HierarchySuper.TYPE,
				HierarchySingleSuper.TYPE,
				HierarchySingleSub.TYPE
			), model.getTypes());
		assertEquals(list(
				HierarchyFirstSub.TYPE,
				HierarchySecondSub.TYPE,
				HierarchySingleSub.TYPE
			), model.getConcreteTypes());
		
		final CacheInfo[] cacheInfo = model.getCacheInfo();
		assertEquals(HierarchyFirstSub.TYPE, cacheInfo[0].getType());
		assertEquals(HierarchySecondSub.TYPE, cacheInfo[1].getType());
		assertEquals(HierarchySingleSub.TYPE, cacheInfo[2].getType());
		
		assertNotNull(model.getCacheQueryInfo());
		assertNotNull(model.getCacheQueryHistogram());
		assertNotNull(model.getConnectionPoolInfo());
		assertNotNull(model.getConnectionPoolInfo().getCounter());
	}
	
}
