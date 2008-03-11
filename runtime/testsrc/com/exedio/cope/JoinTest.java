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

import java.util.List;

import com.exedio.cope.testmodel.PointerItem;
import com.exedio.cope.testmodel.PointerTargetItem;

public class JoinTest extends TestmodelTest
{
	PointerItem item1a;
	PointerItem item1b;
	PointerTargetItem item2a;
	PointerTargetItem item2b;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		item2a = deleteOnTearDown(new PointerTargetItem("item2a"));
		item2b = deleteOnTearDown(new PointerTargetItem("item1a2b"));
		item1a = deleteOnTearDown(new PointerItem("item1a2b", item2a));
		item1b = deleteOnTearDown(new PointerItem("item1b", item2b));
	}

	public void testJoin()
	{
		// test conditions
		assertEquals(PointerItem.pointer.equalTarget(), PointerItem.pointer.equalTarget());
		assertNotEquals(PointerItem.pointer.equalTarget(), PointerItem.pointer2.equalTarget());
		
		{
			final Query<PointerTargetItem> query = PointerTargetItem.TYPE.newQuery(null);
			assertEqualsUnmodifiable(list(), query.getJoins());
			final Join join = query.join(PointerItem.TYPE, PointerItem.code.isNotNull());
			assertEqualsUnmodifiable(list(join), query.getJoins());
			assertContains(item2b, item2a, item2b, item2a, query.search());
		}
		{
			final Query<PointerTargetItem> query = PointerTargetItem.TYPE.newQuery(null);
			query.join(PointerItem.TYPE, PointerItem.pointer.equalTarget());
			assertContains(item2b, item2a, query.search());
		}
		{
			final Query<PointerItem> query = new Query<PointerItem>(PointerItem.TYPE.getThis(), PointerTargetItem.TYPE, null);
			query.join(PointerItem.TYPE, PointerItem.pointer.equalTarget());
			assertContains(item1b, item1a, query.search());
		}
		{
			final Query<PointerTargetItem> query = PointerTargetItem.TYPE.newQuery(null);
			query.join(PointerItem.TYPE, PointerItem.code.equal(PointerTargetItem.code));
			assertContains(item2b, query.search());
		}
		{
			final Query<PointerItem> query = PointerItem.TYPE.newQuery(PointerTargetItem.code.equal("item2a"));
			query.join(PointerTargetItem.TYPE, PointerItem.pointer.equalTarget());
			assertContains(item1a, query.search());
		}
		{
			// test join needed for orderby only
			final Query query = PointerItem.TYPE.newQuery(null);
			query.join(PointerTargetItem.TYPE, PointerItem.pointer.equalTarget());
			query.setOrderBy(PointerTargetItem.code, true);
			assertEquals(list(item1b, item1a), query.search());
			query.setOrderBy(PointerTargetItem.code, false);
			assertEquals(list(item1a, item1b), query.search());
		}
		{
			final Query<List> query = new Query<List>(new Function[]{PointerTargetItem.code, PointerItem.TYPE.getThis(), PointerItem.code}, PointerTargetItem.TYPE, null);
			query.join(PointerItem.TYPE, PointerItem.pointer.equalTarget());
			assertContains(
					list("item1a2b", item1b, "item1b"),
					list("item2a", item1a, "item1a2b"),
					query.search());
		}
	}

}
