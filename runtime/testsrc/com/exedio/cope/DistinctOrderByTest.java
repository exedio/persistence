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
import static java.util.Arrays.asList;

import com.exedio.dsmf.SQLRuntimeException;

public class DistinctOrderByTest extends AbstractRuntimeModelTest
{
	public DistinctOrderByTest()
	{
		super(PlusIntegerTest.MODEL);
	}

	private PlusIntegerItem item1;
	private PlusIntegerItem item2;
	private Query<PlusIntegerItem> query;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		item1 = deleteOnTearDown(new PlusIntegerItem(2, 4, 5));
		item2 = deleteOnTearDown(new PlusIntegerItem(1, 4, 5));

		query = TYPE.newQuery();
		final Join join = query.join(TYPE);
		join.setCondition(numC.equal(numC.bind(join)));
	}

	public void testDistinct()
	{
		query.setDistinct(true);

		assertContains(item1, item2, query.search());
	}

	public void testOrderBy()
	{
		query.setOrderBy(numA, true);

		assertContains(item1, item1, item2, item2, query.search());
	}

	public void testDistinctOrderBy()
	{
		query.setDistinct(true);
		query.setOrderBy(numA, true);

		switch(dialect)
		{
			case hsqldb:
				try
				{
					query.search();
					fail();
				}
				catch(final SQLRuntimeException e)
				{
					assertEquals("invalid ORDER BY expression", e.getCause().getMessage());
				}
				break;
			case mysql:
				assertEquals(asList(item2, item1), query.search());
				break;
			case oracle:
				try
				{
					query.search();
					fail();
				}
				catch(final SQLRuntimeException e)
				{
					final String cause = e.getCause().getMessage();
					assertTrue(cause, cause.startsWith("ORA-01791: "));
				}
				break;
			case postgresql:
				try
				{
					query.search();
					fail();
				}
				catch(final SQLRuntimeException e)
				{
					final String cause = e.getCause().getMessage();
					assertTrue(cause, cause.startsWith(
							"ERROR: for SELECT DISTINCT, ORDER BY expressions must appear in select list"));
				}
				break;
			default:
				throw new RuntimeException(dialect.name());
		}
	}
}
