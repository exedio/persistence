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

import static com.exedio.cope.PlusIntegerItem.TYPE;
import static com.exedio.cope.PlusIntegerItem.numA;
import static com.exedio.cope.PlusIntegerItem.numC;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static com.exedio.cope.tojunit.Assert.assertContainsList;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.exedio.dsmf.SQLRuntimeException;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class DistinctOrderByTest extends TestWithEnvironment
{
	public DistinctOrderByTest()
	{
		super(PlusIntegerTest.MODEL);
	}

	private PlusIntegerItem item1;
	private PlusIntegerItem item2;
	private PlusIntegerItem item3;
	private Query<PlusIntegerItem> query;

	@Before public final void setUp()
	{
		item1 = new PlusIntegerItem(2, 4, 5);
		item2 = new PlusIntegerItem(1, 4, 5);
		item3 = new PlusIntegerItem(1, 4, 5);

		query = TYPE.newQuery();
		final Join join = query.join(TYPE);
		join.setCondition(numC.equal(numC.bind(join)));
	}

	@Test public void testVanilla()
	{
		assertEquals(
				"select this from PlusIntegerItem " +
				"join PlusIntegerItem p1 on numC=p1.numC",
				query.toString());
		assertContainsList(asList(item1, item1, item1, item2, item2, item2, item3, item3, item3), query.search());
	}

	@Test public void testDistinct()
	{
		query.setDistinct(true);

		assertEquals(
				"select distinct this from PlusIntegerItem " +
				"join PlusIntegerItem p1 on numC=p1.numC",
				query.toString());
		assertContains(item1, item2, item3, query.search());
	}

	@Test public void testOrderBy()
	{
		query.setOrderBy(numA, true);

		assertEquals(
				"select this from PlusIntegerItem " +
				"join PlusIntegerItem p1 on numC=p1.numC " +
				"order by numA",
				query.toString());
		assertContainsList(asList(item1, item1, item1, item2, item2, item2, item3, item3, item3), query.search());
	}

	@Test public void testDistinctOrderBy()
	{
		assertEquals(
				"select this from PlusIntegerItem " +
				"join PlusIntegerItem p1 on numC=p1.numC",
				query.toString());

		query.setDistinct(true);
		query.setOrderBy(numA, true);

		assertEquals(
				"select distinct this from PlusIntegerItem " +
				"join PlusIntegerItem p1 on numC=p1.numC " +
				"order by numA",
				query.toString());

		switch(dialect)
		{
			case hsqldb:
				notAllowed(query,
						"invalid ORDER BY expression");
				break;
			case mysql:
				assertEquals(
						"SELECT DISTINCT PlusIntegerItem0.`this` " +
						"FROM `PlusIntegerItem` PlusIntegerItem0 " +
						"JOIN `PlusIntegerItem` PlusIntegerItem1 ON PlusIntegerItem0.`numC`=PlusIntegerItem1.`numC` " +
						"ORDER BY PlusIntegerItem0.`numA`",
						SchemaInfo.search(query));
				assertContains(item2, item3, item1, query.search());
				break;
			case oracle:
				notAllowed(query,
						"ORA-01791: not a SELECTed expression\n");
				break;
			case postgresql:
				notAllowed(query,
						"ERROR: for SELECT DISTINCT, ORDER BY expressions must appear in select list\n" +
						"  Position: 181");
				break;
			default:
				throw new RuntimeException(dialect.name());
		}
	}


	static void notAllowed(final Query<?> query, final String message)
	{
		try
		{
			final List<?> result = query.search();
			fail("search is expected to fail, but returned " + result);
		}
		catch(final SQLRuntimeException e)
		{
			assertEquals(message, e.getCause().getMessage());
		}
	}
}
