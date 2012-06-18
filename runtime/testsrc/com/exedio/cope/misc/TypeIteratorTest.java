/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.misc.QueryAggregatorItem.TYPE;
import static com.exedio.cope.misc.QueryAggregatorItem.intx;
import static com.exedio.cope.misc.TypeIterator.iterate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import com.exedio.cope.AbstractRuntimeTest;
import com.exedio.cope.Condition;
import com.exedio.cope.QueryInfo;
import com.exedio.cope.Transaction;

public class TypeIteratorTest extends AbstractRuntimeTest
{
	public TypeIteratorTest()
	{
		super(QueryAggregatorTest.MODEL);
	}

	QueryAggregatorItem item0, item1, item2, item3, item4;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		item0 = deleteOnTearDown(new QueryAggregatorItem(0));
		item1 = deleteOnTearDown(new QueryAggregatorItem(1));
		item2 = deleteOnTearDown(new QueryAggregatorItem(2));
		item3 = deleteOnTearDown(new QueryAggregatorItem(3));
		item4 = deleteOnTearDown(new QueryAggregatorItem(4));
	}

	public void testIt()
	{
		try
		{
			iterate(null, null, 0);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("type", e.getMessage());
		}
		try
		{
			iterate(TYPE, null, 0);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("slice must be greater 0, but was 0", e.getMessage());
		}

		assertIt(listg(item0, item1, item2, item3, item4), null);
		assertIt(listg(item0, item1, item2, item3, item4), intx.greater(-1));
		assertIt(listg(item2, item3, item4), intx.greater(1));
		assertIt(listg(item0, item1), intx.less(2));

		{
			final Transaction tx = model.currentTransaction();
			final String pre = "select this from QueryAggregatorItem ";
			final String post = " order by this limit '3'";

			tx.setQueryInfoEnabled(true);
			assertEquals(list(), l(iterate(TYPE, intx.less(0), 3)));
			assertEquals(list(
					pre + "where intx<'0'" + post),
				toString(tx.getQueryInfos()));
			tx.setQueryInfoEnabled(false);

			tx.setQueryInfoEnabled(true);
			assertEquals(listg(item0), l(iterate(TYPE, intx.less(1), 3)));
			assertEquals(list(
					pre + "where intx<'1'" + post),
				toString(tx.getQueryInfos()));
			tx.setQueryInfoEnabled(false);

			tx.setQueryInfoEnabled(true);
			assertEquals(listg(item0, item1), l(iterate(TYPE, intx.less(2), 3)));
			assertEquals(list(
					pre + "where intx<'2'" + post),
				toString(tx.getQueryInfos()));
			tx.setQueryInfoEnabled(false);

			tx.setQueryInfoEnabled(true);
			assertEquals(listg(item0, item1, item2), l(iterate(TYPE, intx.less(3), 3)));
			assertEquals(list(
					pre + "where intx<'3'" + post,
					pre + "where (intx<'3' AND this>'" + item2 + "')" + post),
				toString(tx.getQueryInfos()));
			tx.setQueryInfoEnabled(false);

			tx.setQueryInfoEnabled(true);
			assertEquals(listg(item0, item1, item2, item3), l(iterate(TYPE, intx.less(4), 3)));
			assertEquals(list(
					pre + "where intx<'4'" + post,
					pre + "where (intx<'4' AND this>'" + item2 + "')" + post),
				toString(tx.getQueryInfos()));
			tx.setQueryInfoEnabled(false);
		}
	}

	private static void assertIt(final List<QueryAggregatorItem> expected, final Condition c)
	{
		assertEquals(expected, l(iterate(TYPE, c, 1)));
		assertEquals(expected, l(iterate(TYPE, c, 2)));
		assertEquals(expected, l(iterate(TYPE, c, 3)));
		assertEquals(expected, l(iterate(TYPE, c, 4)));
		assertEquals(expected, l(iterate(TYPE, c, 5)));
		assertEquals(expected, l(iterate(TYPE, c, 6)));
	}

	private static ArrayList<QueryAggregatorItem> l(final Iterator<QueryAggregatorItem> iterator)
	{
		final ArrayList<QueryAggregatorItem> result = new ArrayList<QueryAggregatorItem>();
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

	private static List<String> toString(final List<QueryInfo> l)
	{
		final ArrayList<String> result = new ArrayList<String>(l.size());
		for(final QueryInfo q : l)
			result.add(q.getText());
		return result;
	}
}
