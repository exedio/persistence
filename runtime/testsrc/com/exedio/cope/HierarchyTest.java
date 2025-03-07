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

import static com.exedio.cope.RuntimeTester.getQueryCacheInfo;
import static com.exedio.cope.SequenceInfoAssert.assertInfo;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.misc.HiddenFeatures;
import com.exedio.dsmf.Constraint;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

public class HierarchyTest extends TestWithEnvironment
{
	public static final Model MODEL = Model.builder().
		name(HierarchyTest.class).
		add(
			HierarchyFirstSub.TYPE,
			HierarchySecondSub.TYPE,
			HierarchySuper.TYPE, // deliberately put this type below it's sub types to test correct functionality
			HierarchySingleSuper.TYPE,
			HierarchySingleSub.TYPE
		).build();

	public HierarchyTest()
	{
		super(MODEL);
	}

	@Test void testHierarchy()
	{
		// model HierarchySuper
		assertEquals(null, HierarchySuper.TYPE.getSupertype());
		assertEqualsUnmodifiable(list(HierarchyFirstSub.TYPE, HierarchySecondSub.TYPE), HierarchySuper.TYPE.getSubtypes());
		assertEqualsUnmodifiable(list(HierarchySuper.TYPE, HierarchyFirstSub.TYPE, HierarchySecondSub.TYPE), HierarchySuper.TYPE.getSubtypesTransitively());
		assertEqualsUnmodifiable(list(HierarchyFirstSub.TYPE, HierarchySecondSub.TYPE), HierarchySuper.TYPE.getTypesOfInstances());
		assertTrue(HierarchySuper.TYPE.isAssignableFrom(HierarchySuper.TYPE));
		assertTrue(HierarchySuper.TYPE.isAssignableFrom(HierarchyFirstSub.TYPE));
		assertEqualsUnmodifiable(list(HierarchySuper.superInt, HierarchySuper.superString), HierarchySuper.TYPE.getDeclaredFields());
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
		assertEqualsUnmodifiable(list(), HierarchyFirstSub.TYPE.getSubtypes());
		assertEqualsUnmodifiable(list(HierarchyFirstSub.TYPE), HierarchyFirstSub.TYPE.getSubtypesTransitively());
		assertEqualsUnmodifiable(list(HierarchyFirstSub.TYPE), HierarchyFirstSub.TYPE.getTypesOfInstances());
		assertFalse(HierarchyFirstSub.TYPE.isAssignableFrom(HierarchySuper.TYPE));
		assertTrue(HierarchyFirstSub.TYPE.isAssignableFrom(HierarchyFirstSub.TYPE));
		assertFalse(HierarchyFirstSub.TYPE.isAssignableFrom(HierarchySecondSub.TYPE));
		assertFalse(HierarchySecondSub.TYPE.isAssignableFrom(HierarchyFirstSub.TYPE));
		assertEqualsUnmodifiable(list(HierarchyFirstSub.firstSubString), HierarchyFirstSub.TYPE.getDeclaredFields());
		assertEqualsUnmodifiable(list(HierarchySuper.superInt, HierarchySuper.superString, HierarchyFirstSub.firstSubString), HierarchyFirstSub.TYPE.getFields());
		assertEqualsUnmodifiable(list(
				HierarchyFirstSub.firstSubString.getImplicitUniqueConstraint()
			), HierarchyFirstSub.TYPE.getDeclaredUniqueConstraints());
		assertEqualsUnmodifiable(list(
				HierarchySuper.superInt.getImplicitUniqueConstraint(),
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
		assertEquals(HierarchySuper.superInt, HierarchyFirstSub.TYPE.getFeature("superInt"));
		assertEquals(HierarchySuper.superString, HierarchyFirstSub.TYPE.getFeature("superString"));
		assertEquals(HierarchyFirstSub.firstSubString, HierarchyFirstSub.TYPE.getFeature("firstSubString"));
		assertEquals(null, HierarchyFirstSub.TYPE.getFeature("zack"));
		assertFalse(HierarchyFirstSub.TYPE.isAbstract());
		assertEquals(HierarchyFirstSub.TYPE, HierarchyFirstSub.firstSubString.getType());

		HiddenFeatures.accept(model, (k,v) -> { throw new AssertionFailedError(k + "," + v); });
		assertEquals(Map.of(), HiddenFeatures.get(model));

		checkUpdateCountersAbandoned();

		// test persistence
		assertCheckUpdateCounters();

		final HierarchyFirstSub firstItem = new HierarchyFirstSub(0);
		assertCheckUpdateCounters();
		assertEquals(0, firstItem.getSuperInt());
		assertEquals(null, firstItem.getFirstSubString());

		firstItem.setSuperInt(2);
		assertCheckUpdateCounters();
		assertEquals(2, firstItem.getSuperInt());
		assertEquals(null, firstItem.getFirstSubString());

		firstItem.setFirstSubString("firstSubString");
		assertCheckUpdateCounters();
		assertEquals(2, firstItem.getSuperInt());
		assertEquals("firstSubString", firstItem.getFirstSubString());

		restartTransaction();
		assertCheckUpdateCounters();
		assertEquals(2, firstItem.getSuperInt());
		assertEquals("firstSubString", firstItem.getFirstSubString());
		firstItem.setSuperInt(0);

		final HierarchySecondSub secondItem = new HierarchySecondSub(2);
		assertCheckUpdateCounters();
		assertEquals(2, secondItem.getSuperInt());
		assertEquals(null, secondItem.getFirstSubString());

		final HierarchySecondSub secondItem2 = new HierarchySecondSub(3);
		assertCheckUpdateCounters();

		final HierarchyFirstSub firstItem2 = new HierarchyFirstSub(4);
		assertCheckUpdateCounters();

		assertEquals(list(firstItem), HierarchyFirstSub.TYPE.search(HierarchyFirstSub.firstSubString.is("firstSubString")));
		assertEquals(list(), HierarchyFirstSub.TYPE.search(HierarchyFirstSub.firstSubString.is("firstSubStringX")));
		assertContains(firstItem, secondItem, firstItem2, secondItem2, HierarchySuper.TYPE.search(null));

		// model HierarchySingle
		assertEquals(list(HierarchySingleSub.TYPE), HierarchySingleSuper.TYPE.getSubtypes());
		assertEquals(list(HierarchySingleSuper.TYPE, HierarchySingleSub.TYPE), HierarchySingleSuper.TYPE.getSubtypesTransitively());
		assertEquals(list(HierarchySingleSub.TYPE), HierarchySingleSuper.TYPE.getTypesOfInstances());
		assertEquals(list(), HierarchySingleSub.TYPE.getSubtypes());
		assertEquals(list(HierarchySingleSub.TYPE), HierarchySingleSub.TYPE.getSubtypesTransitively());
		assertEquals(list(HierarchySingleSub.TYPE), HierarchySingleSub.TYPE.getTypesOfInstances());
		assertTrue(HierarchySingleSuper.TYPE.isAbstract());
		assertFalse(HierarchySingleSub.TYPE.isAbstract());

		final HierarchySingleSub singleSub1a = new HierarchySingleSub();
		assertCheckUpdateCounters();
		singleSub1a.setSubString("a");
		assertCheckUpdateCounters();
		singleSub1a.setSuperInt(Integer.valueOf(1));
		assertCheckUpdateCounters();
		final HierarchySingleSub singleSub1b = new HierarchySingleSub(1, "b");
		assertCheckUpdateCounters();
		final HierarchySingleSub singleSub2a = new HierarchySingleSub(2, "a");
		assertCheckUpdateCounters();
		assertContains(singleSub1a, singleSub1b, HierarchySingleSub.TYPE.search(HierarchySingleSuper.superInt.is(1)));
		assertContains(singleSub1a, singleSub1b, HierarchySingleSuper.TYPE.search(HierarchySingleSuper.superInt.is(1)));
		assertContains(singleSub1a, singleSub2a, HierarchySingleSub.TYPE.search(HierarchySingleSub.subString.is("a")));
		assertContains(singleSub1a, HierarchySingleSub.TYPE.search(HierarchySingleSuper.superInt.is(1).and(HierarchySingleSub.subString.is("a"))));

		restartTransaction();
		assertCheckUpdateCounters();
		assertContains(singleSub1a, HierarchySingleSub.TYPE.search(HierarchySingleSuper.superInt.is(1).and(HierarchySingleSub.subString.is("a"))));
		assertEquals("a", singleSub2a.getSubString());
		assertEquals(Integer.valueOf(1), singleSub1b.getSuperInt());

		// test polymorphic pointers
		assertEquals(null, singleSub1a.getHierarchySuper());
		assertEquals(list((Object)null), new Query<>(HierarchySingleSub.hierarchySuper, HierarchySingleSub.TYPE, HierarchySingleSuper.superInt.is(1).and(HierarchySingleSub.subString.is("a"))).search());
		singleSub1a.setHierarchySuper( firstItem );
		assertCheckUpdateCounters();
		assertEquals(firstItem, singleSub1a.getHierarchySuper());
		assertEquals(list(firstItem), new Query<>(HierarchySingleSub.hierarchySuper, HierarchySingleSub.TYPE, HierarchySingleSuper.superInt.is(1).and(HierarchySingleSub.subString.is("a"))).search());
		assertEquals(list(singleSub1a), HierarchySingleSub.TYPE.search(HierarchySingleSub.hierarchySuper.is(firstItem)));
		restartTransaction();
		assertCheckUpdateCounters();
		assertEquals(firstItem, singleSub1a.getHierarchySuper());
		assertEquals(list(firstItem), new Query<>(HierarchySingleSub.hierarchySuper, HierarchySingleSub.TYPE, HierarchySingleSuper.superInt.is(1).and(HierarchySingleSub.subString.is("a"))).search());
		assertEquals(list(singleSub1a), HierarchySingleSub.TYPE.search(HierarchySingleSub.hierarchySuper.is(firstItem)));

		singleSub1a.setHierarchySuper(secondItem2);
		assertCheckUpdateCounters();
		assertEquals(secondItem2, singleSub1a.getHierarchySuper());
		assertEquals(list(secondItem2), new Query<>(HierarchySingleSub.hierarchySuper, HierarchySingleSub.TYPE, HierarchySingleSuper.superInt.is(1).and(HierarchySingleSub.subString.is("a"))).search());
		assertEquals(list(singleSub1a), HierarchySingleSub.TYPE.search(HierarchySingleSub.hierarchySuper.is(secondItem2)));
		restartTransaction();
		assertEquals(secondItem2, singleSub1a.getHierarchySuper());
		assertEquals(list(secondItem2), new Query<>(HierarchySingleSub.hierarchySuper, HierarchySingleSub.TYPE, HierarchySingleSuper.superInt.is(1).and(HierarchySingleSub.subString.is("a"))).search());
		assertEquals(list(singleSub1a), HierarchySingleSub.TYPE.search(HierarchySingleSub.hierarchySuper.is(secondItem2)));

		singleSub1a.setHierarchySuper(null);
		assertCheckUpdateCounters();
		assertEquals(null, singleSub1a.getHierarchySuper());
		assertEquals(list((Object)null), new Query<>(HierarchySingleSub.hierarchySuper, HierarchySingleSub.TYPE, HierarchySingleSuper.superInt.is(1).and(HierarchySingleSub.subString.is("a"))).search());
		restartTransaction();
		assertCheckUpdateCounters();
		assertEquals(null, singleSub1a.getHierarchySuper());
		assertEquals(list((Object)null), new Query<>(HierarchySingleSub.hierarchySuper, HierarchySingleSub.TYPE, HierarchySingleSuper.superInt.is(1).and(HierarchySingleSub.subString.is("a"))).search());

		// test wrong attributes
		try
		{
			firstItem.get(HierarchySecondSub.firstSubString);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("field "+HierarchySecondSub.firstSubString+" does not belong to type "+HierarchyFirstSub.TYPE, e.getMessage());
		}
		try
		{
			HierarchySecondSub.firstSubString.get(firstItem);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("field "+HierarchySecondSub.firstSubString+" does not belong to type "+HierarchyFirstSub.TYPE, e.getMessage());
		}
		try
		{
			firstItem.set(HierarchySecondSub.firstSubString, "zack");
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("field "+HierarchySecondSub.firstSubString+" does not belong to type "+HierarchyFirstSub.TYPE, e.getMessage());
		}
		try
		{
			firstItem.set(SetValue.map(HierarchySecondSub.firstSubString, "zack"));
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("field "+HierarchySecondSub.firstSubString+" does not belong to type "+HierarchyFirstSub.TYPE, e.getMessage());
		}
		try
		{
			HierarchyFirstSub.TYPE.newItem(SetValue.map(HierarchySecondSub.firstSubString, "zack"));
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("field "+HierarchySecondSub.firstSubString+" does not belong to type "+HierarchyFirstSub.TYPE, e.getMessage());
		}
		try
		{
			HierarchySuper.TYPE.newItem();
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("cannot create item of abstract type HierarchySuper", e.getMessage());
		}
		assertCheckUpdateCounters();
	}

	@SuppressWarnings("deprecation")
	private void checkUpdateCountersAbandoned()
	{
		assertFails(
				() -> SchemaInfo.checkUpdateCounter(null),
				NullPointerException.class,
				"type"
		);
		for (final Type<?> type: model.getTypes())
		{
			assertFails(
					() -> SchemaInfo.checkUpdateCounter(type),
					RuntimeException.class,
					"update counter consistency between tables has been abandoned");
		}
	}

	@Test void testPolymorphicQueryInvalidation() throws UniqueViolationException
	{
		final HierarchyFirstSub item = new HierarchyFirstSub(10);

		final Query<?> q1 = HierarchySuper.TYPE.newQuery(HierarchySuper.superInt.is(10));
		final Query<?> q2 = HierarchySuper.TYPE.newQuery(HierarchySuper.superInt.is(20));
		assertEquals(list(item), q1.search());
		assertEquals(list(), q2.search());

		item.setSuperInt(20);
		assertEquals(list(), q1.search());
		assertEquals(list(item), q2.search());
	}

	@Test void testModel()
	{
		model.commit();

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
				HierarchyFirstSub.TYPE,
				HierarchySecondSub.TYPE,
				HierarchySuper.TYPE,
				HierarchySingleSuper.TYPE,
				HierarchySingleSub.TYPE
			), model.getTypes());
		assertEqualsUnmodifiable(list(
				HierarchySuper.TYPE,
				HierarchyFirstSub.TYPE,
				HierarchySecondSub.TYPE,
				HierarchySingleSuper.TYPE,
				HierarchySingleSub.TYPE
			), model.getTypesSortedByHierarchy());
		assertEqualsUnmodifiable(list(
				HierarchyFirstSub.TYPE,
				HierarchySecondSub.TYPE,
				HierarchySingleSub.TYPE
			), model.getConcreteTypes());

		{
			final ArrayList<Type<?>> comparableList = new ArrayList<>(model.getTypes());
			assertEquals(list(
					HierarchyFirstSub.TYPE,
					HierarchySecondSub.TYPE,
					HierarchySuper.TYPE,
					HierarchySingleSuper.TYPE,
					HierarchySingleSub.TYPE
				), comparableList);
			Collections.sort(comparableList);
			assertEquals(list(
					HierarchySuper.TYPE,
					HierarchyFirstSub.TYPE,
					HierarchySecondSub.TYPE,
					HierarchySingleSuper.TYPE,
					HierarchySingleSub.TYPE
				), comparableList);
		}

		// must be the same order as in model constructor
		assertCacheInfo(HierarchyFirstSub.TYPE, HierarchySecondSub.TYPE, HierarchySingleSub.TYPE);
		assertNotNull(getQueryCacheInfo(model));
		assertNotNull(model.getQueryCacheHistogram());
		assertNotNull(model.getConnectionPoolInfo());
		assertNotNull(model.getConnectionPoolInfo().getCounter());

		startTransaction();
	}

	@Test void testPrimaryKeyInfo()
	{
		MODEL.rollback();
		// for flushing the info
		MODEL.dropSchema();
		MODEL.createSchema();
		startTransaction();

		assertInfo(model.getSequenceInfo(), HierarchySuper.TYPE.getThis(), HierarchySingleSuper.TYPE.getThis());

		assertInfo(HierarchySuper.TYPE, HierarchySuper.TYPE.getPrimaryKeyInfo());
		assertInfo(HierarchySuper.TYPE, HierarchyFirstSub.TYPE.getPrimaryKeyInfo());
		assertInfo(HierarchySuper.TYPE, HierarchySecondSub.TYPE.getPrimaryKeyInfo());
		assertInfo(HierarchySingleSuper.TYPE, HierarchySingleSuper.TYPE.getPrimaryKeyInfo());
		assertInfo(HierarchySingleSuper.TYPE, HierarchySingleSub.TYPE.getPrimaryKeyInfo());

		new HierarchyFirstSub(0);
		assertInfo(HierarchySuper.TYPE, 1, 0, 0, HierarchySuper.TYPE.getPrimaryKeyInfo());
		assertInfo(HierarchySuper.TYPE, 1, 0, 0, HierarchyFirstSub.TYPE.getPrimaryKeyInfo());
		assertInfo(HierarchySuper.TYPE, 1, 0, 0, HierarchySecondSub.TYPE.getPrimaryKeyInfo());
		assertInfo(HierarchySingleSuper.TYPE, HierarchySingleSuper.TYPE.getPrimaryKeyInfo());
		assertInfo(HierarchySingleSuper.TYPE, HierarchySingleSub.TYPE.getPrimaryKeyInfo());

		new HierarchyFirstSub(1);
		assertInfo(HierarchySuper.TYPE, 2, 0, 1, HierarchySuper.TYPE.getPrimaryKeyInfo());
		assertInfo(HierarchySuper.TYPE, 2, 0, 1, HierarchyFirstSub.TYPE.getPrimaryKeyInfo());
		assertInfo(HierarchySuper.TYPE, 2, 0, 1, HierarchySecondSub.TYPE.getPrimaryKeyInfo());
		assertInfo(HierarchySingleSuper.TYPE, HierarchySingleSuper.TYPE.getPrimaryKeyInfo());
		assertInfo(HierarchySingleSuper.TYPE, HierarchySingleSub.TYPE.getPrimaryKeyInfo());

		new HierarchySingleSub();
		assertInfo(HierarchySuper.TYPE, 2, 0, 1, HierarchySuper.TYPE.getPrimaryKeyInfo());
		assertInfo(HierarchySuper.TYPE, 2, 0, 1, HierarchyFirstSub.TYPE.getPrimaryKeyInfo());
		assertInfo(HierarchySuper.TYPE, 2, 0, 1, HierarchySecondSub.TYPE.getPrimaryKeyInfo());
		assertInfo(HierarchySingleSuper.TYPE, 1, 0, 0, HierarchySingleSuper.TYPE.getPrimaryKeyInfo());
		assertInfo(HierarchySingleSuper.TYPE, 1, 0, 0, HierarchySingleSub.TYPE.getPrimaryKeyInfo());

		assertInfo(model.getSequenceInfo(), HierarchySuper.TYPE.getThis(), HierarchySingleSuper.TYPE.getThis());
	}

	@Test void testDeleteSchema()
	{
		model.checkEmptySchema();


		final HierarchyFirstSub firstA = new HierarchyFirstSub(0);
		final HierarchyFirstSub firstB = new HierarchyFirstSub(4);
		new HierarchySecondSub(2);
		new HierarchySecondSub(3);
		final HierarchySingleSub singleA = new HierarchySingleSub();
		final HierarchySingleSub singleB = new HierarchySingleSub(2, "a");
		singleA.setHierarchySuper(firstA);
		singleB.setHierarchySuper(firstB);

		try
		{
			model.checkEmptySchema();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals(
					"schema not empty: HierarchySuper:4, HierarchyFirstSub:2, HierarchySecondSub:2, HierarchySingleSuper:2, HierarchySingleSub:2",
					e.getMessage());
		}
		assertTrue(firstA.existsCopeItem());
		assertTrue(firstB.existsCopeItem());
		assertTrue(singleA.existsCopeItem());
		assertTrue(singleB.existsCopeItem());


		try
		{
			model.deleteSchema();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("must not be called within a transaction: tx:com.exedio.cope.HierarchyTest for model " + model, e.getMessage());
		}
		assertTrue(firstA.existsCopeItem());
		assertTrue(firstB.existsCopeItem());
		assertTrue(singleA.existsCopeItem());
		assertTrue(singleB.existsCopeItem());


		model.commit();
		model.deleteSchema();
		model.startTransaction("testDeleteSchema");
		model.checkEmptySchema();

		assertFalse(firstA.existsCopeItem());
		assertFalse(firstB.existsCopeItem());
		assertFalse(singleA.existsCopeItem());
		assertFalse(singleB.existsCopeItem());
	}

	@Test void testDeleteSchemaForTest()
	{
		model.checkEmptySchema();


		final HierarchyFirstSub firstA = new HierarchyFirstSub(0);
		final HierarchyFirstSub firstB = new HierarchyFirstSub(4);
		new HierarchySecondSub(2);
		new HierarchySecondSub(3);
		final HierarchySingleSub singleA = new HierarchySingleSub();
		final HierarchySingleSub singleB = new HierarchySingleSub(2, "a");
		singleA.setHierarchySuper(firstA);
		singleB.setHierarchySuper(firstB);

		try
		{
			model.checkEmptySchema();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals(
					"schema not empty: HierarchySuper:4, HierarchyFirstSub:2, HierarchySecondSub:2, HierarchySingleSuper:2, HierarchySingleSub:2",
					e.getMessage());
		}
		assertTrue(firstA.existsCopeItem());
		assertTrue(firstB.existsCopeItem());
		assertTrue(singleA.existsCopeItem());
		assertTrue(singleB.existsCopeItem());


		try
		{
			model.deleteSchemaForTest();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("must not be called within a transaction: tx:com.exedio.cope.HierarchyTest for model " + model, e.getMessage());
		}
		assertTrue(firstA.existsCopeItem());
		assertTrue(firstB.existsCopeItem());
		assertTrue(singleA.existsCopeItem());
		assertTrue(singleB.existsCopeItem());


		model.commit();
		model.deleteSchemaForTest();
		model.startTransaction("testDeleteSchema");
		model.checkEmptySchema();

		assertFalse(firstA.existsCopeItem());
		assertFalse(firstB.existsCopeItem());
		assertFalse(singleA.existsCopeItem());
		assertFalse(singleB.existsCopeItem());
	}
}
