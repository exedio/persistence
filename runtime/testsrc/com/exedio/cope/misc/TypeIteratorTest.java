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

package com.exedio.cope.misc;

import static com.exedio.cope.SchemaInfo.primaryKeyColumnValue;
import static com.exedio.cope.misc.QueryAggregatorItem.TYPE;
import static com.exedio.cope.misc.QueryAggregatorItem.intx;
import static com.exedio.cope.misc.QueryIterators.iterateType;
import static com.exedio.cope.misc.QueryIterators.iterateTypeDescending;
import static com.exedio.cope.tojunit.Assert.list;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.Condition;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.tojunit.SI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TypeIteratorTest extends TestWithEnvironment
{
	public TypeIteratorTest()
	{
		super(QueryAggregatorTest.MODEL);
	}

	QueryAggregatorItem item0, item1, item2, item3, item4;

	@BeforeEach final void setUp()
	{
		item0 = new QueryAggregatorItem(0);
		item1 = new QueryAggregatorItem(1);
		item2 = new QueryAggregatorItem(2);
		item3 = new QueryAggregatorItem(3);
		item4 = new QueryAggregatorItem(4);
	}

	@Test void testIt()
	{
		try
		{
			iterateType(null, null, 0);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("type", e.getMessage());
		}
		try
		{
			iterateType(TYPE, null, 0);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("slice must be greater zero, but was 0", e.getMessage());
		}

		assertIt(asList(item0, item1, item2, item3, item4), null);
		assertIt(asList(item0, item1, item2, item3, item4), intx.greater(-1));
		assertIt(asList(item2, item3, item4), intx.greater(1));
		assertIt(asList(item0, item1), intx.less(2));

		{
			final String thisC = SI.pk(TYPE);
			final String intxC = SI.col(intx);
			final String pre = "SELECT " + thisC + " FROM " + SI.tab(TYPE) + " ";
			final String post     = " ORDER BY " + thisC +           (postgresql?" NULLS FIRST":"")        + (hsqldb?" OFFSET 0":"") + " LIMIT 3";
			final String postDesc = " ORDER BY " + thisC + " DESC" + (hsqldb||postgresql?" NULLS LAST":"") + (hsqldb?" OFFSET 0":"") + " LIMIT 3";

			model.setDatabaseListener(databaseListener);
			assertEquals(list(), l(iterateType(TYPE, intx.less(0), 3)));
			assertDatabase(
					pre + "WHERE " + intxC + "<0" + post
			);


			assertEquals(list(), l(iterateTypeDescending(TYPE, intx.less(0), 3)));
			assertDatabase(
					pre + "WHERE " + intxC + "<0" + postDesc
			);


			assertEquals(asList(item0), l(iterateType(TYPE, intx.less(1), 3)));
			assertDatabase(
					pre + "WHERE " + intxC + "<1" + post
			);


			assertEquals(asList(item0), l(iterateTypeDescending(TYPE, intx.less(1), 3)));
			assertDatabase(
					pre + "WHERE " + intxC + "<1" + postDesc
			);


			assertEquals(asList(item0, item1), l(iterateType(TYPE, intx.less(2), 3)));
			assertDatabase(
					pre + "WHERE " + intxC + "<2" + post
			);


			assertEquals(asList(item1, item0), l(iterateTypeDescending(TYPE, intx.less(2), 3)));
			assertDatabase(
					pre + "WHERE " + intxC + "<2" + postDesc
			);


			assertEquals(asList(item0, item1, item2), l(iterateType(TYPE, intx.less(3), 3)));
			assertDatabase(
					pre + "WHERE " + intxC + "<3" + post,
					pre + "WHERE (" + intxC + "<3) AND (" + thisC + ">" + primaryKeyColumnValue(item2) + ")" + post
			);


			assertEquals(asList(item2, item1, item0), l(iterateTypeDescending(TYPE, intx.less(3), 3)));
			assertDatabase(
					pre + "WHERE " + intxC + "<3" + postDesc,
					pre + "WHERE (" + intxC + "<3) AND (" + thisC + "<" + primaryKeyColumnValue(item0) + ")" + postDesc
			);


			assertEquals(asList(item0, item1, item2, item3), l(iterateType(TYPE, intx.less(4), 3)));
			assertDatabase(
					pre + "WHERE " + intxC + "<4" + post,
					pre + "WHERE (" + intxC + "<4) AND (" + thisC + ">" + primaryKeyColumnValue(item2) + ")" + post
			);


			assertEquals(asList(item3, item2, item1, item0), l(iterateTypeDescending(TYPE, intx.less(4), 3)));
			assertDatabase(
					pre + "WHERE " + intxC + "<4" + postDesc,
					pre + "WHERE (" + intxC + "<4) AND (" + thisC + "<" + primaryKeyColumnValue(item1) + ")" + postDesc
			);

		}
	}

	private static void assertIt(final List<QueryAggregatorItem> expected, final Condition c)
	{
		assertEquals(expected, l(iterateType(TYPE, c, 1)));
		assertEquals(expected, l(iterateType(TYPE, c, 2)));
		assertEquals(expected, l(iterateType(TYPE, c, 3)));
		assertEquals(expected, l(iterateType(TYPE, c, 4)));
		assertEquals(expected, l(iterateType(TYPE, c, 5)));
		assertEquals(expected, l(iterateType(TYPE, c, 6)));

		final List<QueryAggregatorItem> expectedDescending = new ArrayList<>(expected);
		Collections.reverse(expectedDescending);
		assertEquals(expectedDescending, l(iterateTypeDescending(TYPE, c, 1)));
		assertEquals(expectedDescending, l(iterateTypeDescending(TYPE, c, 2)));
		assertEquals(expectedDescending, l(iterateTypeDescending(TYPE, c, 3)));
		assertEquals(expectedDescending, l(iterateTypeDescending(TYPE, c, 4)));
		assertEquals(expectedDescending, l(iterateTypeDescending(TYPE, c, 5)));
		assertEquals(expectedDescending, l(iterateTypeDescending(TYPE, c, 6)));
	}

	private static ArrayList<QueryAggregatorItem> l(final Iterator<QueryAggregatorItem> iterator)
	{
		final ArrayList<QueryAggregatorItem> result = new ArrayList<>();
		while(iterator.hasNext())
			result.add(iterator.next());
		try
		{
			iterator.next();
			fail();
		}
		catch(final NoSuchElementException e)
		{
			assertEquals(null, e.getMessage());
		}
		return result;
	}

	private final ArrayList<String> database = new ArrayList<>();

	private final DatabaseListener databaseListener = (sql, parameters, _1, _2, _3, _4) -> {
		String s = sql;
		if(parameters!=null)
			for(final Object p : parameters)
				s = s.replaceFirst("\\?", p.toString());
		database.add(s);
	};

	private void assertDatabase(final String... expected)
	{
		assertEquals(List.of(expected), database);
		database.clear();
	}
}
