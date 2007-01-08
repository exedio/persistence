/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.pattern;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.exedio.cope.AbstractLibTest;
import com.exedio.cope.Feature;
import com.exedio.cope.Function;
import com.exedio.cope.Query;

public class VectorRelationTest extends AbstractLibTest
{
	
	public VectorRelationTest()
	{
		super(RelationTest.MODEL);
	}

	RelationSourceItem source1, source2;
	RelationTargetItem target1, target2, target3;
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		deleteOnTearDown(source1 = new RelationSourceItem("source1"));
		deleteOnTearDown(source2 = new RelationSourceItem("source2"));
		deleteOnTearDown(new RelationSourceItem("sourceX"));
		deleteOnTearDown(target1 = new RelationTargetItem("target1"));
		deleteOnTearDown(target2 = new RelationTargetItem("target2"));
		deleteOnTearDown(target3 = new RelationTargetItem("target3"));
		deleteOnTearDown(new RelationTargetItem("targetX"));
	}
	
	public void testRelation()
	{
		// test model
		assertEquals(Arrays.asList(new Feature[]{
				VectorRelationItem.TYPE.getThis(),
				VectorRelationItem.vectorSource,
				VectorRelationItem.vectorTarget,
				VectorRelationItem.relation,
				VectorRelationItem.relation.getOrder(),
				VectorRelationItem.relation.getUniqueConstraint(),
			}), VectorRelationItem.TYPE.getFeatures());
		assertEquals(Arrays.asList(new Feature[]{
				VectorRelationItem.TYPE.getThis(),
				VectorRelationItem.vectorSource,
				VectorRelationItem.vectorTarget,
				VectorRelationItem.relation,
				VectorRelationItem.relation.getOrder(),
				VectorRelationItem.relation.getUniqueConstraint(),
			}), VectorRelationItem.TYPE.getDeclaredFeatures());
		
		assertSame(VectorRelationItem.vectorSource, VectorRelationItem.relation.getSource());
		assertSame(VectorRelationItem.vectorTarget, VectorRelationItem.relation.getTarget());

		assertEquals(VectorRelationItem.TYPE, VectorRelationItem.vectorSource.getType());
		assertEquals(VectorRelationItem.TYPE, VectorRelationItem.vectorTarget.getType());
		assertEquals(VectorRelationItem.TYPE, VectorRelationItem.relation.getOrder().getType());
		assertEquals(VectorRelationItem.TYPE, VectorRelationItem.relation.getUniqueConstraint().getType());
		assertEquals(VectorRelationItem.TYPE, VectorRelationItem.relation.getType());

		assertEquals("vectorSource", VectorRelationItem.vectorSource.getName());
		assertEquals("vectorTarget", VectorRelationItem.vectorTarget.getName());
		assertEquals("relationOrder", VectorRelationItem.relation.getOrder().getName());
		assertEquals("relationUniqueConstraint", VectorRelationItem.relation.getUniqueConstraint().getName());
		assertEquals("relation", VectorRelationItem.relation.getName());

		assertEqualsUnmodifiable(list(VectorRelationItem.relation), VectorRelationItem.vectorSource.getPatterns());
		assertEqualsUnmodifiable(list(VectorRelationItem.relation), VectorRelationItem.vectorTarget.getPatterns());
		assertEqualsUnmodifiable(list(VectorRelationItem.relation), VectorRelationItem.relation.getOrder().getPatterns());
		
		assertEqualsUnmodifiable(list(VectorRelationItem.relation), VectorRelation.getDeclaredRelations(RelationSourceItem.TYPE));
		assertEqualsUnmodifiable(list(), VectorRelation.getDeclaredRelations(RelationSourceSubItem.TYPE));
		assertEqualsUnmodifiable(list(), VectorRelation.getDeclaredRelations(RelationTargetItem.TYPE));
		assertEqualsUnmodifiable(list(), VectorRelation.getDeclaredRelations(RelationTargetSubItem.TYPE));
		assertEqualsUnmodifiable(list(), VectorRelation.getDeclaredRelations(VectorRelationItem.TYPE));
		
		assertEqualsUnmodifiable(list(VectorRelationItem.relation), VectorRelation.getRelations(RelationSourceItem.TYPE));
		assertEqualsUnmodifiable(list(VectorRelationItem.relation), VectorRelation.getRelations(RelationSourceSubItem.TYPE));
		assertEqualsUnmodifiable(list(), VectorRelation.getRelations(RelationTargetItem.TYPE));
		assertEqualsUnmodifiable(list(), VectorRelation.getRelations(RelationTargetSubItem.TYPE));
		assertEqualsUnmodifiable(list(), VectorRelation.getRelations(VectorRelationItem.TYPE));
		
		// test persistence
		assertContains(source1.getVectorTarget());
		assertContains(source2.getVectorTarget());
		assertContains(target1.getVectorSource());
		assertContains(target2.getVectorSource());
		
		source1.setVectorTarget(listg(target1, target2));
		assertIt(
				new RelationSourceItem[]{source1, source1},
				new RelationTargetItem[]{target1, target2},
				new int[]{0, 1});
		assertEquals(list(target1, target2), source1.getVectorTarget());
		assertEquals(list(), source2.getVectorTarget());
		assertEquals(list(source1), target1.getVectorSource());
		assertEquals(list(source1), target2.getVectorSource());
		assertEquals(list(), target3.getVectorSource());
		
		source1.setVectorTarget(listg(target2, target3));
		assertIt(
				new RelationSourceItem[]{source1, source1},
				new RelationTargetItem[]{target2, target3},
				new int[]{0, 1});
		assertEquals(list(target2, target3), source1.getVectorTarget());
		assertEquals(list(), source2.getVectorTarget());
		assertEquals(list(), target1.getVectorSource());
		assertEquals(list(source1), target2.getVectorSource());
		assertEquals(list(source1), target3.getVectorSource());
	}

	private static void assertIt(
			final RelationSourceItem[] expectedSources,
			final RelationTargetItem[] expectedTargets,
			final int[] expectedOrders)
	{
		final Query<VectorRelationItem> q = VectorRelationItem.TYPE.newQuery(null);
		q.setOrderBy(
				new Function[]{VectorRelationItem.vectorSource, VectorRelationItem.vectorTarget, VectorRelationItem.relation.getOrder()},
				new boolean[]{true, true, true});
		
		final ArrayList<RelationSourceItem> actualSources = new ArrayList<RelationSourceItem>();
		final ArrayList<RelationTargetItem> actualTargets = new ArrayList<RelationTargetItem>();
		final ArrayList<Integer> actualOrders = new ArrayList<Integer>();
		
		final List<? extends VectorRelationItem> actualTupels = q.search();
		for(final VectorRelationItem actualTupel : actualTupels)
		{
			actualSources.add(actualTupel.getVectorSource());
			actualTargets.add(actualTupel.getVectorTarget());
			actualOrders.add(actualTupel.relation.getOrder().get(actualTupel));
		}
		final ArrayList<Integer> expectedOrderList = new ArrayList<Integer>();
		for(final int order : expectedOrders)
			expectedOrderList.add(order);
		
		assertEquals(Arrays.asList(expectedSources), actualSources);
		assertEquals(Arrays.asList(expectedTargets), actualTargets);
		assertEquals(expectedOrderList, actualOrders);
	}
	
}
