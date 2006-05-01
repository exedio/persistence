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

import com.exedio.cope.pattern.RelationSourceItem;
import com.exedio.cope.pattern.RelationTargetItem;

public class DeleteRelationTest extends AbstractLibTest
{
	
	public DeleteRelationTest()
	{
		super(Main.relationModel);
	}

	RelationSourceItem source1;
	RelationTargetItem target1, target2;
	
	public void setUp() throws Exception
	{
		super.setUp();
		deleteOnTearDown(source1 = new RelationSourceItem("source1"));
		deleteOnTearDown(target1 = new RelationTargetItem("target1"));
		deleteOnTearDown(target2 = new RelationTargetItem("target2"));
	}
	
	public void testDelete()
	{
		assertContains(source1.getTarget());
		
		assertTrue(source1.addToTarget(target1));
		assertContains(target1, source1.getTarget());
		
		assertTrue(source1.addToTarget(target2));
		assertContains(target1, target2, source1.getTarget());
	}
	
}
