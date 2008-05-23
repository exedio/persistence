/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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



public class HierarchyEmptyTest extends AbstractRuntimeTest
{
	static final Model MODEL = new Model(HierarchyEmptySub.TYPE, HierarchyEmptySuper.TYPE);

	public HierarchyEmptyTest()
	{
		super(MODEL);
	}
	
	public void testHierarchy()
			throws IntegrityViolationException, UniqueViolationException, NoSuchIDException
	{
		// model HierarchyEmptySuper
		assertEquals(null, HierarchyEmptySuper.TYPE.getSupertype());
		assertEqualsUnmodifiable(list(HierarchyEmptySub.TYPE), HierarchyEmptySuper.TYPE.getSubTypes());
		assertEqualsUnmodifiable(list(HierarchyEmptySuper.TYPE, HierarchyEmptySub.TYPE), HierarchyEmptySuper.TYPE.getSubTypesTransitively());
		assertEqualsUnmodifiable(list(HierarchyEmptySuper.TYPE, HierarchyEmptySub.TYPE), HierarchyEmptySuper.TYPE.getTypesOfInstances());
		assertTrue(HierarchyEmptySuper.TYPE.isAssignableFrom(HierarchyEmptySuper.TYPE));
		assertTrue(HierarchyEmptySuper.TYPE.isAssignableFrom(HierarchyEmptySuper.TYPE));
		assertEqualsUnmodifiable(list(HierarchyEmptySuper.superInt), HierarchyEmptySuper.TYPE.getDeclaredFields());
		assertEqualsUnmodifiable(list(HierarchyEmptySuper.superInt), HierarchyEmptySuper.TYPE.getFields());
		assertEqualsUnmodifiable(list(
				HierarchyEmptySuper.superInt.getImplicitUniqueConstraint()
			), HierarchyEmptySuper.TYPE.getDeclaredUniqueConstraints());
		assertEqualsUnmodifiable(list(
				HierarchyEmptySuper.superInt.getImplicitUniqueConstraint()
			), HierarchyEmptySuper.TYPE.getUniqueConstraints());
		assertEqualsUnmodifiable(list(
				HierarchyEmptySuper.TYPE.getThis(),
				HierarchyEmptySuper.superInt,
				HierarchyEmptySuper.superInt.getImplicitUniqueConstraint()
			), HierarchyEmptySuper.TYPE.getDeclaredFeatures());
		assertEqualsUnmodifiable(list(
				HierarchyEmptySuper.TYPE.getThis(),
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
		assertEqualsUnmodifiable(list(HierarchyEmptySub.TYPE), HierarchyEmptySub.TYPE.getSubTypesTransitively());
		assertEqualsUnmodifiable(list(HierarchyEmptySub.TYPE), HierarchyEmptySub.TYPE.getTypesOfInstances());
		assertFalse(HierarchyEmptySub.TYPE.isAssignableFrom(HierarchyEmptySuper.TYPE));
		assertTrue(HierarchyEmptySub.TYPE.isAssignableFrom(HierarchyEmptySub.TYPE));
		assertFalse(HierarchyEmptySub.TYPE.isAssignableFrom(HierarchySecondSub.TYPE));
		assertFalse(HierarchySecondSub.TYPE.isAssignableFrom(HierarchyEmptySub.TYPE));
		assertEqualsUnmodifiable(list(), HierarchyEmptySub.TYPE.getDeclaredFields());
		assertEqualsUnmodifiable(list(HierarchyEmptySuper.superInt), HierarchyEmptySub.TYPE.getFields());
		assertEqualsUnmodifiable(list(), HierarchyEmptySub.TYPE.getDeclaredUniqueConstraints());
		assertEqualsUnmodifiable(list(HierarchyEmptySub.superInt.getImplicitUniqueConstraint()), HierarchyEmptySub.TYPE.getUniqueConstraints());
		assertEqualsUnmodifiable(list(HierarchyEmptySub.TYPE.getThis()), HierarchyEmptySub.TYPE.getDeclaredFeatures());
		assertEqualsUnmodifiable(list(
				HierarchyEmptySub.TYPE.getThis(),
				HierarchyEmptySuper.superInt,
				HierarchyEmptySuper.superInt.getImplicitUniqueConstraint()
			), HierarchyEmptySub.TYPE.getFeatures());
		assertEquals(null, HierarchyEmptySub.TYPE.getDeclaredFeature("superInt"));
		assertEquals(null, HierarchyEmptySub.TYPE.getDeclaredFeature("superString"));
		assertEquals(null, HierarchyEmptySub.TYPE.getDeclaredFeature("zack"));
		assertEquals(HierarchyEmptySub.superInt, HierarchyEmptySub.TYPE.getFeature("superInt"));
		assertEquals(null, HierarchyEmptySub.TYPE.getFeature("zack"));
		assertFalse(HierarchyEmptySub.TYPE.isAbstract());

		// test persistence
		final HierarchyEmptySub subItem = deleteOnTearDown(new HierarchyEmptySub(0));
		assertID(0, subItem);
		assertEquals(0, subItem.getSuperInt());
		
		subItem.setSuperInt(2);
		assertEquals(2, subItem.getSuperInt());
		
		if(!noJoinParentheses) assertEquals(list(subItem), subItem.TYPE.search(subItem.superInt.equal(2)));
		assertEquals(list(subItem), subItem.TYPE.search(null));
		if(!noJoinParentheses) assertEquals(list(), subItem.TYPE.search(subItem.superInt.equal(1)));
		
		final HierarchyEmptySuper superItem = deleteOnTearDown(new HierarchyEmptySuper(3));
		assertID(1, superItem);
		assertEquals(3, superItem.getSuperInt());
		
		superItem.setSuperInt(4);
		assertEquals(4, superItem.getSuperInt());
		
		assertEquals(list(superItem), superItem.TYPE.search(superItem.superInt.equal(4)));
		assertEquals(list(subItem), superItem.TYPE.search(superItem.superInt.equal(2)));
		assertContains(superItem, subItem, superItem.TYPE.search(null));
		assertEquals(list(), superItem.TYPE.search(superItem.superInt.equal(1)));
		
		// test getItem
		assertSame(subItem, model.getItem("HierarchyEmptySub.0"));
		assertSame(superItem, model.getItem("HierarchyEmptySuper.1"));
		assertIDFails("HierarchyEmptySuper.0",  "item <0> does not exist", false);
		assertIDFails("HierarchyEmptySub.1",    "item <1> does not exist", false);
		assertIDFails("noDotInThisString",      "no separator '.' in id", true);
		assertIDFails("noSuchType.x",           "type <noSuchType> does not exist", true);
		assertIDFails("HierarchyEmptySuper.x",  "wrong number format <x>", true);
		assertIDFails("HierarchyEmptySuper.92386591832651832659213865193456293456",
															 "wrong number format <92386591832651832659213865193456293456>", true);
		assertIDFails("HierarchyEmptySuper.-1", "must be positive", true);
		assertIDFails("HierarchyEmptySuper.50", "item <50> does not exist", false);
		assertIDFails("HierarchyEmptySuper." + Long.MIN_VALUE, "must be positive", true);
		assertIDFails("HierarchyEmptySuper." + 2147483646l, "item <2147483646> does not exist", false); // 2^31 - 2
		assertIDFails("HierarchyEmptySuper." + 2147483647l, "item <2147483647> does not exist", false); // 2^31 - 1
		assertIDFails("HierarchyEmptySuper." + 2147483648l, "does not fit in 31 bit", true); // 2^31
		assertIDFails("HierarchyEmptySuper." + 2147483649l, "does not fit in 31 bit", true); // 2^31 + 1
		assertIDFails("HierarchyEmptySuper." + Long.MAX_VALUE, "does not fit in 31 bit", true);
	}
	
	public void testModel()
	{
		model.checkDatabase();
		if(!postgresql)
		{
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
		}
		
		assertEqualsUnmodifiable(list(
				HierarchyEmptySub.TYPE,
				HierarchyEmptySuper.TYPE
			), model.getTypes());
		assertEqualsUnmodifiable(list(
				HierarchyEmptySuper.TYPE,
				HierarchyEmptySub.TYPE
			), model.getTypesSortedByHierarchy());
		assertEqualsUnmodifiable(list(
				HierarchyEmptySub.TYPE,
				HierarchyEmptySuper.TYPE
			), model.getConcreteTypes());
		
		final CacheInfo[] itemCacheInfo = model.getItemCacheInfo();
		if(model.getProperties().getItemCacheLimit()>0)
		{
			assertEquals(HierarchyEmptySub.TYPE, itemCacheInfo[0].getType());
			assertEquals(HierarchyEmptySuper.TYPE, itemCacheInfo[1].getType());
			assertEquals(2, itemCacheInfo.length);
		}
		else
			assertEquals(0, itemCacheInfo.length);
		
		assertNotNull(model.getQueryCacheInfo());
		assertNotNull(model.getQueryCacheHistogram());
		assertNotNull(model.getConnectionPoolInfo());
		assertNotNull(model.getConnectionPoolInfo().getCounter());
	}
	
}
