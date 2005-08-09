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

import com.exedio.cope.testmodel.PointerItem;
import com.exedio.cope.testmodel.PointerTargetItem;

public class JoinMultipleTest extends TestmodelTest
{
	PointerItem source;
	PointerTargetItem target1;
	PointerTargetItem target2;

	protected void setUp() throws Exception
	{
		super.setUp();
		deleteOnTearDown(target1 = new PointerTargetItem("target1"));
		deleteOnTearDown(target2 = new PointerTargetItem("target2"));
		deleteOnTearDown(source = new PointerItem("source", target1));
		source.setPointer2(target2);
	}

	public void testMultipleJoin()
	{
		{
			final Query query = new Query(source.TYPE, null);
			assertEquals(list(), query.getJoins());

			final Join join1 = query.join(target1.TYPE);
			assertEquals(list(join1), query.getJoins());
			join1.setCondition(Cope.join(source.pointer, join1));
			assertEquals(list(source), query.search());

			final Join join2 = query.join(target2.TYPE);
			assertEquals(list(join1, join2), query.getJoins());
			join2.setCondition(Cope.join(source.pointer2, join2));
			assertEquals(list(source), query.search());

			//query.setCondition(Cope.equal(target1.code, "target1")); TODO
			//assertEquals(list(source), infoSearch(query));
		}
	}
	
}
