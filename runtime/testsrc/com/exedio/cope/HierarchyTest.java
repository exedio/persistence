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

import java.util.EnumSet;

import com.exedio.dsmf.Constraint;

public class HierarchyTest extends AbstractRuntimeTest
{
	public/*for web.xml*/ static final Model MODEL = new Model(
			HierarchyFirstSub.TYPE,
			HierarchySecondSub.TYPE,
			HierarchySuper.TYPE, // deliberately put this type below it's sub types to test correct functionality
			HierarchySingleSuper.TYPE,
			HierarchySingleSub.TYPE
		);

	public HierarchyTest()
	{
		super(MODEL);
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
		assertEqualsUnmodifiable(list(), HierarchyFirstSub.TYPE.getSubTypes());
		assertEqualsUnmodifiable(list(HierarchyFirstSub.TYPE), HierarchyFirstSub.TYPE.getSubTypesTransitively());
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

		assertEquals(map(), model.getHiddenFeatures());
		
		// test persistence
		final HierarchyFirstSub firstItem = deleteOnTearDown(new HierarchyFirstSub(0));
		assertID(0, firstItem);
		assertEquals(0, firstItem.getSuperInt());
		assertEquals(null, firstItem.getFirstSubString());
		assertSame(firstItem, model.getItem(HierarchyFirstSub.TYPE.getID()+".0"));
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
		
		final HierarchySecondSub secondItem = deleteOnTearDown(new HierarchySecondSub(2));
		assertID(1, secondItem);
		assertEquals(2, secondItem.getSuperInt());
		assertEquals(null, secondItem.getFirstSubString());

		final HierarchySecondSub secondItem2 = deleteOnTearDown(new HierarchySecondSub(3));
		assertID(2, secondItem2);

		final HierarchyFirstSub firstItem2 = deleteOnTearDown(new HierarchyFirstSub(4));
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
		
		final HierarchySingleSub singleSub1a = deleteOnTearDown(new HierarchySingleSub());
		singleSub1a.setSubString("a");
		singleSub1a.setSuperInt(Integer.valueOf(1));
		final HierarchySingleSub singleSub1b = deleteOnTearDown(new HierarchySingleSub(1, "b"));
		final HierarchySingleSub singleSub2a = deleteOnTearDown(new HierarchySingleSub(2, "a"));
		if(!noJoinParentheses) assertContains(singleSub1a, singleSub1b, singleSub1a.TYPE.search(HierarchySingleSuper.superInt.equal(1)));
		assertContains(singleSub1a, singleSub1b, HierarchySingleSuper.TYPE.search(HierarchySingleSuper.superInt.equal(1)));
		assertContains(singleSub1a, singleSub2a, singleSub1a.TYPE.search(singleSub1a.subString.equal("a")));
		if(!noJoinParentheses) assertContains(singleSub1a, singleSub1a.TYPE.search(HierarchySingleSuper.superInt.equal(1).and(singleSub1a.subString.equal("a"))));
		
		restartTransaction();
		if(!noJoinParentheses) assertContains(singleSub1a, singleSub1a.TYPE.search(HierarchySingleSuper.superInt.equal(1).and(singleSub1a.subString.equal("a"))));
		assertEquals("a", singleSub2a.getSubString());
		assertEquals(Integer.valueOf(1), singleSub1b.getSuperInt());
		
		// test polymorphic pointers
		assertEquals(null, singleSub1a.getHierarchySuper());
		if(!noJoinParentheses) assertEquals(list((Object)null), new Query<HierarchySuper>(singleSub1a.hierarchySuper, singleSub1a.TYPE, singleSub1a.superInt.equal(1).and(singleSub1a.subString.equal("a"))).search());
		singleSub1a.setHierarchySuper( firstItem );
		assertEquals(firstItem, singleSub1a.getHierarchySuper());
		if(!noJoinParentheses) assertEquals(list(firstItem), new Query<HierarchySuper>(singleSub1a.hierarchySuper, singleSub1a.TYPE, singleSub1a.superInt.equal(1).and(singleSub1a.subString.equal("a"))).search());
		assertEquals(list(singleSub1a), singleSub1a.TYPE.search(singleSub1a.hierarchySuper.equal(firstItem)));
		restartTransaction();
		assertEquals(firstItem, singleSub1a.getHierarchySuper());
		if(!noJoinParentheses) assertEquals(list(firstItem), new Query<HierarchySuper>(singleSub1a.hierarchySuper, singleSub1a.TYPE, singleSub1a.superInt.equal(1).and(singleSub1a.subString.equal("a"))).search());
		assertEquals(list(singleSub1a), singleSub1a.TYPE.search(singleSub1a.hierarchySuper.equal(firstItem)));

		singleSub1a.setHierarchySuper(secondItem2);
		assertEquals(secondItem2, singleSub1a.getHierarchySuper());
		if(!noJoinParentheses) assertEquals(list(secondItem2), new Query<HierarchySuper>(singleSub1a.hierarchySuper, singleSub1a.TYPE, singleSub1a.superInt.equal(1).and(singleSub1a.subString.equal("a"))).search());
		assertEquals(list(singleSub1a), singleSub1a.TYPE.search(singleSub1a.hierarchySuper.equal(secondItem2)));
		restartTransaction();
		assertEquals(secondItem2, singleSub1a.getHierarchySuper());
		if(!noJoinParentheses) assertEquals(list(secondItem2), new Query<HierarchySuper>(singleSub1a.hierarchySuper, singleSub1a.TYPE, singleSub1a.superInt.equal(1).and(singleSub1a.subString.equal("a"))).search());
		assertEquals(list(singleSub1a), singleSub1a.TYPE.search(singleSub1a.hierarchySuper.equal(secondItem2)));

		singleSub1a.setHierarchySuper(null);
		assertEquals(null, singleSub1a.getHierarchySuper());
		if(!noJoinParentheses) assertEquals(list((Object)null), new Query<HierarchySuper>(singleSub1a.hierarchySuper, singleSub1a.TYPE, singleSub1a.superInt.equal(1).and(singleSub1a.subString.equal("a"))).search());
		restartTransaction();
		assertEquals(null, singleSub1a.getHierarchySuper());
		if(!noJoinParentheses) assertEquals(list((Object)null), new Query<HierarchySuper>(singleSub1a.hierarchySuper, singleSub1a.TYPE, singleSub1a.superInt.equal(1).and(singleSub1a.subString.equal("a"))).search());
		
		// test wrong attributes
		try
		{
			firstItem.get(secondItem.firstSubString);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("field "+secondItem.firstSubString+" does not belong to type "+firstItem.TYPE, e.getMessage());
		}
		try
		{
			secondItem.firstSubString.get(firstItem);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("field "+secondItem.firstSubString+" does not belong to type "+firstItem.TYPE, e.getMessage());
		}
		try
		{
			firstItem.set(secondItem.firstSubString, "zack");
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("field "+secondItem.firstSubString+" does not belong to type "+firstItem.TYPE, e.getMessage());
		}
		try
		{
			firstItem.set(secondItem.firstSubString.map("zack"));
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("field "+secondItem.firstSubString+" does not belong to type "+firstItem.TYPE, e.getMessage());
		}
		try
		{
			firstItem.TYPE.newItem(secondItem.firstSubString.map("zack"));
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("field "+secondItem.firstSubString+" does not belong to type "+firstItem.TYPE, e.getMessage());
		}
		try
		{
			HierarchySuper.TYPE.newItem();
			fail();
		}
		catch(RuntimeException e)
		{
			assertEquals(InstantiationException.class, e.getCause().getClass());
		}
	}
	
	public void testPolymorphicQueryInvalidation() throws UniqueViolationException
	{
		final HierarchyFirstSub item = deleteOnTearDown(new HierarchyFirstSub(10));
		
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
		model.checkSchema();
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
		
		assertCacheInfo(
				new Type[]{HierarchyFirstSub.TYPE, HierarchySecondSub.TYPE, HierarchySingleSub.TYPE},
				new int []{33333, 33333, 33333});
		
		assertNotNull(model.getQueryCacheInfo());
		assertNotNull(model.getQueryCacheHistogram());
		assertNotNull(model.getConnectionPoolInfo());
		assertNotNull(model.getConnectionPoolInfo().getCounter());
	}
	
	public void testPrimaryKeyInfo()
	{
		if(postgresql) // causes a deadlock on postgresql
			return;
		
		// for flushing the info
		MODEL.dropSchema();
		MODEL.createSchema();
		
		assertInfo(model.getSequenceInfo(), HierarchySuper.TYPE.getThis(), HierarchySingleSuper.TYPE.getThis());
		
		assertInfo(HierarchySuper.TYPE, HierarchySuper.TYPE.getPrimaryKeyInfo());
		assertInfo(HierarchySuper.TYPE, HierarchyFirstSub.TYPE.getPrimaryKeyInfo());
		assertInfo(HierarchySuper.TYPE, HierarchySecondSub.TYPE.getPrimaryKeyInfo());
		assertInfo(HierarchySingleSuper.TYPE, HierarchySingleSuper.TYPE.getPrimaryKeyInfo());
		assertInfo(HierarchySingleSuper.TYPE, HierarchySingleSub.TYPE.getPrimaryKeyInfo());
		
		deleteOnTearDown(new HierarchyFirstSub(0));
		assertInfo(HierarchySuper.TYPE, 1, 0, 0, HierarchySuper.TYPE.getPrimaryKeyInfo());
		assertInfo(HierarchySuper.TYPE, 1, 0, 0, HierarchyFirstSub.TYPE.getPrimaryKeyInfo());
		assertInfo(HierarchySuper.TYPE, 1, 0, 0, HierarchySecondSub.TYPE.getPrimaryKeyInfo());
		assertInfo(HierarchySingleSuper.TYPE, HierarchySingleSuper.TYPE.getPrimaryKeyInfo());
		assertInfo(HierarchySingleSuper.TYPE, HierarchySingleSub.TYPE.getPrimaryKeyInfo());
		
		deleteOnTearDown(new HierarchyFirstSub(1));
		assertInfo(HierarchySuper.TYPE, 2, 0, 1, HierarchySuper.TYPE.getPrimaryKeyInfo());
		assertInfo(HierarchySuper.TYPE, 2, 0, 1, HierarchyFirstSub.TYPE.getPrimaryKeyInfo());
		assertInfo(HierarchySuper.TYPE, 2, 0, 1, HierarchySecondSub.TYPE.getPrimaryKeyInfo());
		assertInfo(HierarchySingleSuper.TYPE, HierarchySingleSuper.TYPE.getPrimaryKeyInfo());
		assertInfo(HierarchySingleSuper.TYPE, HierarchySingleSub.TYPE.getPrimaryKeyInfo());
		
		deleteOnTearDown(new HierarchySingleSub());
		assertInfo(HierarchySuper.TYPE, 2, 0, 1, HierarchySuper.TYPE.getPrimaryKeyInfo());
		assertInfo(HierarchySuper.TYPE, 2, 0, 1, HierarchyFirstSub.TYPE.getPrimaryKeyInfo());
		assertInfo(HierarchySuper.TYPE, 2, 0, 1, HierarchySecondSub.TYPE.getPrimaryKeyInfo());
		assertInfo(HierarchySingleSuper.TYPE, 1, 0, 0, HierarchySingleSuper.TYPE.getPrimaryKeyInfo());
		assertInfo(HierarchySingleSuper.TYPE, 1, 0, 0, HierarchySingleSub.TYPE.getPrimaryKeyInfo());
		
		assertInfo(model.getSequenceInfo(), HierarchySuper.TYPE.getThis(), HierarchySingleSuper.TYPE.getThis());
	}
}
