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

import java.util.Arrays;

import com.exedio.cope.AbstractLibTest;
import com.exedio.cope.Feature;
import com.exedio.cope.Model;

public class RelationTest extends AbstractLibTest
{
	static final Model MODEL = new Model(
			RelationItem.TYPE,
			RelationSelfItem.TYPE,
			VectorRelationItem.TYPE,
			RelationSourceItem.TYPE,
			RelationSourceSubItem.TYPE,
			RelationTargetItem.TYPE,
			RelationTargetSubItem.TYPE);
	
	public RelationTest()
	{
		super(MODEL);
	}

	RelationSourceItem source1, source2;
	RelationTargetItem target1, target2;
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		deleteOnTearDown(source1 = new RelationSourceItem("source1"));
		deleteOnTearDown(source2 = new RelationSourceItem("source2"));
		deleteOnTearDown(new RelationSourceItem("sourceX"));
		deleteOnTearDown(target1 = new RelationTargetItem("target1"));
		deleteOnTearDown(target2 = new RelationTargetItem("target2"));
		deleteOnTearDown(new RelationTargetItem("targetX"));
	}
	
	public void testRelation()
	{
		// test model
		assertEquals(Arrays.asList(new Feature[]{
				RelationItem.TYPE.getThis(),
				RelationItem.source,
				RelationItem.target,
				RelationItem.relation,
				RelationItem.relation.getUniqueConstraint(),
			}), RelationItem.TYPE.getFeatures());
		assertEquals(Arrays.asList(new Feature[]{
				RelationItem.TYPE.getThis(),
				RelationItem.source,
				RelationItem.target,
				RelationItem.relation,
				RelationItem.relation.getUniqueConstraint(),
			}), RelationItem.TYPE.getDeclaredFeatures());
		
		assertSame(RelationItem.source, RelationItem.relation.getSource());
		assertSame(RelationItem.target, RelationItem.relation.getTarget());

		assertEquals(RelationItem.TYPE, RelationItem.source.getType());
		assertEquals(RelationItem.TYPE, RelationItem.target.getType());
		assertEquals(RelationItem.TYPE, RelationItem.relation.getUniqueConstraint().getType());
		assertEquals(RelationItem.TYPE, RelationItem.relation.getType());

		assertEquals("source", RelationItem.source.getName());
		assertEquals("target", RelationItem.target.getName());
		assertEquals("relationUniqueConstraint", RelationItem.relation.getUniqueConstraint().getName());
		assertEquals("relation", RelationItem.relation.getName());

		assertEqualsUnmodifiable(list(RelationItem.relation), RelationItem.source.getPatterns());
		assertEqualsUnmodifiable(list(RelationItem.relation), RelationItem.target.getPatterns());
		
		assertEqualsUnmodifiable(map(RelationItem.relation, Relation.IS_SOURCE, RelationSelfItem.relation, Relation.IS_SOURCE|Relation.IS_TARGET), Relation.getDeclaredRelations(RelationSourceItem.TYPE));
		assertEqualsUnmodifiable(map(), Relation.getDeclaredRelations(RelationSourceSubItem.TYPE));
		assertEqualsUnmodifiable(map(RelationItem.relation, Relation.IS_TARGET), Relation.getDeclaredRelations(RelationTargetItem.TYPE));
		assertEqualsUnmodifiable(map(), Relation.getDeclaredRelations(RelationTargetSubItem.TYPE));
		assertEqualsUnmodifiable(map(), Relation.getDeclaredRelations(RelationItem.TYPE));
		
		assertEqualsUnmodifiable(map(RelationItem.relation, Relation.IS_SOURCE, RelationSelfItem.relation, Relation.IS_SOURCE|Relation.IS_TARGET), Relation.getRelations(RelationSourceItem.TYPE));
		assertEqualsUnmodifiable(map(RelationItem.relation, Relation.IS_SOURCE, RelationSelfItem.relation, Relation.IS_SOURCE|Relation.IS_TARGET), Relation.getRelations(RelationSourceSubItem.TYPE));
		assertEqualsUnmodifiable(map(RelationItem.relation, Relation.IS_TARGET), Relation.getRelations(RelationTargetItem.TYPE));
		assertEqualsUnmodifiable(map(RelationItem.relation, Relation.IS_TARGET), Relation.getRelations(RelationTargetSubItem.TYPE));
		assertEqualsUnmodifiable(map(), Relation.getRelations(RelationItem.TYPE));
		
		// test persistence
		assertContains(source1.getTarget());
		assertContains(source2.getTarget());
		assertContains(target1.getSource());
		assertContains(target2.getSource());
		
		assertTrue(source1.addToTarget(target1));
		assertContains(target1, source1.getTarget());
		assertContains(source1, target1.getSource());
		assertContains(source2.getTarget());
		assertContains(target2.getSource());
		
		assertFalse(source1.addToTarget(target1));
		assertContains(target1, source1.getTarget());
		assertContains(source1, target1.getSource());
		assertContains(source2.getTarget());
		assertContains(target2.getSource());
		
		assertTrue(source1.addToTarget(target2));
		assertContains(target1, target2, source1.getTarget());
		assertContains(source1, target1.getSource());
		assertContains(source2.getTarget());
		assertContains(source1, target2.getSource());
		
		assertTrue(target2.addToSource(source2));
		assertContains(target1, target2, source1.getTarget());
		assertContains(source1, target1.getSource());
		assertContains(target2, source2.getTarget());
		assertContains(source1, source2, target2.getSource());
		
		assertFalse(target2.addToSource(source2));
		assertContains(target1, target2, source1.getTarget());
		assertContains(source1, target1.getSource());
		assertContains(target2, source2.getTarget());
		assertContains(source1, source2, target2.getSource());
		
		assertTrue(target1.removeFromSource(source1));
		assertContains(target2, source1.getTarget());
		assertContains(target1.getSource());
		assertContains(target2, source2.getTarget());
		assertContains(source1, source2, target2.getSource());
		
		assertFalse(target1.removeFromSource(source1));
		assertContains(target2, source1.getTarget());
		assertContains(target1.getSource());
		assertContains(target2, source2.getTarget());
		assertContains(source1, source2, target2.getSource());
		
		assertTrue(source1.removeFromTarget(target2));
		assertContains(source1.getTarget());
		assertContains(target1.getSource());
		assertContains(target2, source2.getTarget());
		assertContains(source2, target2.getSource());
		
		source2.setTarget(listg(target1));
		assertContains(source1.getTarget());
		assertContains(source2, target1.getSource());
		assertContains(target1, source2.getTarget());
		assertContains(target2.getSource());
		
		source2.setTarget(listg(target2, target1));
		assertContains(source1.getTarget());
		assertContains(source2, target1.getSource());
		assertContains(target1, target2, source2.getTarget());
		assertContains(source2, target2.getSource());
		
		target1.setSource(listg(source1));
		assertContains(target1, source1.getTarget());
		assertContains(source1, target1.getSource());
		assertContains(target2, source2.getTarget());
		assertContains(source2, target2.getSource());
		
		target1.setSource(listg(source2, source1));
		assertContains(target1, source1.getTarget());
		assertContains(source1, source2, target1.getSource());
		assertContains(target1, target2, source2.getTarget());
		assertContains(source2, target2.getSource());
	}
	
}
