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

import static com.exedio.cope.CompareConditionItem.TYPE;
import static com.exedio.cope.CompareConditionItem.intx;
import static com.exedio.cope.CompareConditionItem.string;
import static com.exedio.cope.Query.newQuery;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SelectBindTest extends TestWithEnvironment
{
	public SelectBindTest()
	{
		super(CompareConditionTest.MODEL);
	}

	@BeforeEach final void setUp()
	{
		new CompareConditionItem("string1", 1, 11l, 2.1, null, null, null);
		new CompareConditionItem("string2", 2, 12l, 2.2, null, null, null);
	}

	@Test void testIt()
	{
		{
			final Query<List<Object>> q = newQuery(new Function<?>[]{string, intx}, TYPE, null);
			final Join j = q.join(TYPE);
			j.setCondition(string.bind(j).greater(string));
			q.setOrderBy(string, false);
			assertEquals(
					"select string,intx from CompareConditionItem " +
					"join CompareConditionItem c1 on c1.string>string " +
					"order by string desc",
					q.toString());

			final Collection<List<Object>> result = q.search();
			final Iterator<List<Object>> i = result.iterator();

			assertEqualsUnmodifiable(list("string1", 1), i.next());
		}
		{
			final Query<List<Object>> q = newQuery(new Function<?>[]{string, intx}, TYPE, null);
			final Join j = q.join(TYPE); j.setCondition(string.bind(j).greater(string));
			q.setSelects(new Function<?>[]{string, intx, string.bind(j), intx.bind(j)});
			q.setOrderBy(string, false);
			assertEquals(
					"select string,intx,c1.string,c1.intx from CompareConditionItem " +
					"join CompareConditionItem c1 on c1.string>string " +
					"order by string desc",
					q.toString());

			final Collection<List<Object>> result = q.search();
			final Iterator<List<Object>> i = result.iterator();

			assertEqualsUnmodifiable(list("string1", 1, "string2", 2), i.next());
		}
		{
			final Query<List<Object>> q = newQuery(new Function<?>[]{string, intx}, TYPE, null);
			final Join j1 = q.join(TYPE); j1.setCondition(string.bind(j1).greater(string));
			final Join j2 = q.join(TYPE); j2.setCondition(string.bind(j2).greater(string));
			q.setSelects(new Function<?>[]{string, intx, string.bind(j1), intx.bind(j1), string.bind(j2), intx.bind(j2)});
			q.setOrderBy(string, false);
			assertEquals(
					"select string,intx,c1.string,c1.intx,c2.string,c2.intx from CompareConditionItem " +
					"join CompareConditionItem c1 on c1.string>string " +
					"join CompareConditionItem c2 on c2.string>string " +
					"order by string desc",
					q.toString());

			final Collection<List<Object>> result = q.search();
			final Iterator<List<Object>> i = result.iterator();

			assertEqualsUnmodifiable(list("string1", 1, "string2", 2, "string2", 2), i.next());
		}
	}
}
