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

import static com.exedio.cope.misc.QueryAggregatorItem.TYPE;
import static com.exedio.cope.misc.QueryAggregatorItem.intx;
import static com.exedio.cope.misc.QueryIterators.iterateTypeDescendingTransactionally;
import static com.exedio.cope.misc.QueryIterators.iterateTypeTransactionally;
import static java.util.Arrays.asList;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.Condition;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.TransactionTry;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressFBWarnings("UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR")
public class TypeIteratorTransactionallyTest extends TestWithEnvironment
{
	public TypeIteratorTransactionallyTest()
	{
		super(QueryAggregatorTest.MODEL);
		copeRule.omitTransaction();
	}

	QueryAggregatorItem item0, item1, item2, item3, item4;

	@BeforeEach final void setUp()
	{
		try(TransactionTry tx = model.startTransactionTry(getClass().getName()))
		{
			item0 = new QueryAggregatorItem(0);
			item1 = new QueryAggregatorItem(1);
			item2 = new QueryAggregatorItem(2);
			item3 = new QueryAggregatorItem(3);
			item4 = new QueryAggregatorItem(4);
			tx.commit();
		}
	}

	@AfterEach final void tearDown()
	{
		try(TransactionTry tx = model.startTransactionTry(getClass().getName()))
		{
			item0.deleteCopeItem();
			item1.deleteCopeItem();
			item2.deleteCopeItem();
			item3.deleteCopeItem();
			item4.deleteCopeItem();
			tx.commit();
		}
	}

	@Test void testIt()
	{
		try
		{
			iterateTypeTransactionally(null, null, 0);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("type", e.getMessage());
		}
		try
		{
			iterateTypeTransactionally(TYPE, null, 0);
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
	}

	private static void assertIt(final List<QueryAggregatorItem> expected, final Condition c)
	{
		assertEquals(expected, l(iterateTypeTransactionally(TYPE, c, 1)));
		assertEquals(expected, l(iterateTypeTransactionally(TYPE, c, 2)));
		assertEquals(expected, l(iterateTypeTransactionally(TYPE, c, 3)));
		assertEquals(expected, l(iterateTypeTransactionally(TYPE, c, 4)));
		assertEquals(expected, l(iterateTypeTransactionally(TYPE, c, 5)));
		assertEquals(expected, l(iterateTypeTransactionally(TYPE, c, 6)));

		final List<QueryAggregatorItem> expectedDescending = new ArrayList<>(expected);
		Collections.reverse(expectedDescending);
		assertEquals(expectedDescending, l(iterateTypeDescendingTransactionally(TYPE, c, 1)));
		assertEquals(expectedDescending, l(iterateTypeDescendingTransactionally(TYPE, c, 2)));
		assertEquals(expectedDescending, l(iterateTypeDescendingTransactionally(TYPE, c, 3)));
		assertEquals(expectedDescending, l(iterateTypeDescendingTransactionally(TYPE, c, 4)));
		assertEquals(expectedDescending, l(iterateTypeDescendingTransactionally(TYPE, c, 5)));
		assertEquals(expectedDescending, l(iterateTypeDescendingTransactionally(TYPE, c, 6)));
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
}
