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

public class DistinctOrderByTest extends AbstractRuntimeTest
{
	public DistinctOrderByTest()
	{
		super(PlusIntegerTest.MODEL);
	}

	private PlusIntegerItem item1;
	private PlusIntegerItem item2;
	private Query<PlusIntegerItem> q;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		item1 = deleteOnTearDown(new PlusIntegerItem(2, 4, 5));
		item2 = deleteOnTearDown(new PlusIntegerItem(1, 4, 5));

		q = TYPE.newQuery();
		final Join join = q.join(TYPE);
		join.setCondition(numC.equal(numC.bind(join)));
	}

	public void testDistinct()
	{
		q.setDistinct(true);

		assertContains(item1, item2, q.search());
	}

	public void testDistinctOrderBy()
	{
		q.setDistinct(true);
		q.setOrderBy(numA, true);

		switch(dialect)
		{
			case hsqldb:
				try
				{
					q.search();
					fail();
				}
				catch(final SQLRuntimeException e)
				{
					assertEquals("invalid ORDER BY expression", e.getCause().getMessage());
				}
				break;
			case mysql:
				assertEquals(asList(item2, item1), q.search());
				break;
			case oracle:
				try
				{
					q.search();
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
					q.search();
					fail();
				}
				catch(final SQLRuntimeException e)
				{
					model.rollback();
					model.startTransaction("DistinctOrderByTest");
					dontDeleteOnTearDown(item1);
					dontDeleteOnTearDown(item2);
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
