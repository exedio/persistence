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

import static com.exedio.cope.Query.newQuery;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.list;
import static com.exedio.cope.tojunit.EqualsAssert.assertEqualsAndHash;
import static com.exedio.cope.tojunit.EqualsAssert.assertNotEqualsAndHash;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.testmodel.PointerItem;
import com.exedio.cope.testmodel.PointerTargetItem;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JoinTest extends TestmodelTest
{
	PointerItem item1a;
	PointerItem item1b;
	PointerTargetItem item2a;
	PointerTargetItem item2b;

	@BeforeEach final void setUp()
	{
		item2a = new PointerTargetItem("item2a");
		item2b = new PointerTargetItem("item1a2b");
		item1a = new PointerItem("item1a2b", item2a);
		item1b = new PointerItem("item1b", item2b);
	}

	@Test void testJoin()
	{
		// test conditions
		assertEqualsAndHash(PointerItem.pointer.isTarget(), PointerItem.pointer.isTarget());
		assertNotEqualsAndHash(PointerItem.pointer.isTarget(), PointerItem.pointer2.isTarget());

		{
			final Query<PointerTargetItem> query = PointerTargetItem.TYPE.newQuery(null);
			assertEqualsUnmodifiable(list(), query.getJoins());
			final Condition joinCondition = PointerItem.code.isNotNull();
			final Join join = query.join(PointerItem.TYPE, joinCondition);
			assertSame( joinCondition, join.getCondition() );
			assertEqualsUnmodifiable(list(join), query.getJoins());
			assertContains(item2b, item2a, item2b, item2a, query.search());
		}
		{
			final Query<PointerTargetItem> query = PointerTargetItem.TYPE.newQuery(null);
			query.join(PointerItem.TYPE, PointerItem.pointer.isTarget());
			assertContains(item2b, item2a, query.search());
		}
		{
			final Query<PointerItem> query = new Query<>(PointerItem.TYPE.getThis(), PointerTargetItem.TYPE, null);
			query.join(PointerItem.TYPE, PointerItem.pointer.isTarget());
			assertContains(item1b, item1a, query.search());
		}
		{
			final Query<PointerTargetItem> query = PointerTargetItem.TYPE.newQuery(null);
			query.join(PointerItem.TYPE, PointerItem.code.is(PointerTargetItem.code));
			assertContains(item2b, query.search());
		}
		{
			final Query<PointerItem> query = PointerItem.TYPE.newQuery(PointerTargetItem.code.is("item2a"));
			query.join(PointerTargetItem.TYPE, PointerItem.pointer.isTarget());
			assertContains(item1a, query.search());
		}
		{
			// test join needed for orderby only
			final Query<PointerItem> query = PointerItem.TYPE.newQuery(null);
			query.join(PointerTargetItem.TYPE, PointerItem.pointer.isTarget());
			query.setOrderBy(PointerTargetItem.code, true);
			assertEquals(list(item1b, item1a), query.search());
			query.setOrderBy(PointerTargetItem.code, false);
			assertEquals(list(item1a, item1b), query.search());
		}
		{
			final Query<List<Object>> query = newQuery(new Function<?>[]{PointerTargetItem.code, PointerItem.TYPE.getThis(), PointerItem.code}, PointerTargetItem.TYPE, null);
			query.join(PointerItem.TYPE, PointerItem.pointer.isTarget());
			assertContains(
					list("item1a2b", item1b, "item1b"),
					list("item2a", item1a, "item1a2b"),
					query.search());
		}
	}
}
