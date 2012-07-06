/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import java.util.EnumSet;

import com.exedio.dsmf.Constraint;

public class HierarchyEmptyTest extends AbstractRuntimeTest
{
	static final Model MODEL = new Model(HierarchyEmptySub.TYPE, HierarchyEmptySuper.TYPE);

	public HierarchyEmptyTest()
	{
		super(MODEL);
	}

	public void testHierarchy()
			throws IntegrityViolationException, UniqueViolationException
	{
		// model HierarchyEmptySuper
		assertEquals(null, HierarchyEmptySuper.TYPE.getSupertype());
		assertEqualsUnmodifiable(list(HierarchyEmptySub.TYPE), HierarchyEmptySuper.TYPE.getSubtypes());
		assertEqualsUnmodifiable(list(HierarchyEmptySuper.TYPE, HierarchyEmptySub.TYPE), HierarchyEmptySuper.TYPE.getSubtypesTransitively());
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
		assertEqualsUnmodifiable(list(), HierarchyEmptySub.TYPE.getSubtypes());
		assertEqualsUnmodifiable(list(HierarchyEmptySub.TYPE), HierarchyEmptySub.TYPE.getSubtypesTransitively());
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
		assertCheckUpdateCounters();

		final HierarchyEmptySub subItem = deleteOnTearDown(new HierarchyEmptySub(0));
		assertCheckUpdateCounters();
		assertEquals(0, subItem.getSuperInt());

		subItem.setSuperInt(2);
		assertCheckUpdateCounters();
		assertEquals(2, subItem.getSuperInt());

		assertEquals(list(subItem), subItem.TYPE.search(subItem.superInt.equal(2)));
		assertEquals(list(subItem), subItem.TYPE.search(null));
		assertEquals(list(), subItem.TYPE.search(subItem.superInt.equal(1)));

		final HierarchyEmptySuper superItem = deleteOnTearDown(new HierarchyEmptySuper(3));
		assertCheckUpdateCounters();
		assertEquals(3, superItem.getSuperInt());

		superItem.setSuperInt(4);
		assertCheckUpdateCounters();
		assertEquals(4, superItem.getSuperInt());

		assertEquals(list(superItem), superItem.TYPE.search(superItem.superInt.equal(4)));
		assertEquals(list(subItem), superItem.TYPE.search(superItem.superInt.equal(2)));
		assertContains(superItem, subItem, superItem.TYPE.search(null));
		assertEquals(list(), superItem.TYPE.search(superItem.superInt.equal(1)));
	}

	public void testModel()
	{
		model.checkSchema();

		model.commit();

		if(!postgresql)
		{
			model.dropSchemaConstraints(EnumSet.allOf(Constraint.Type.class));
			model.createSchemaConstraints(EnumSet.allOf(Constraint.Type.class));
			model.dropSchemaConstraints(EnumSet.of(Constraint.Type.PrimaryKey, Constraint.Type.ForeignKey));
			model.createSchemaConstraints(EnumSet.of(Constraint.Type.PrimaryKey, Constraint.Type.ForeignKey));
			model.dropSchemaConstraints(EnumSet.of(Constraint.Type.ForeignKey));
			model.createSchemaConstraints(EnumSet.of(Constraint.Type.ForeignKey));
			model.dropSchemaConstraints(EnumSet.of(Constraint.Type.Unique));
			model.createSchemaConstraints(EnumSet.of(Constraint.Type.Unique));
			model.dropSchemaConstraints(EnumSet.of(Constraint.Type.Check));
			model.createSchemaConstraints(EnumSet.of(Constraint.Type.Check));
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

		final ItemCacheInfo[] itemCacheInfo = model.getItemCacheInfo();
		if(model.getConnectProperties().getItemCacheLimit()>0)
		{
			// must be the same order as in model constructor
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

		model.startTransaction();
	}

}
