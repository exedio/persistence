/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.DayItem.TYPE;

public class QuerySearchSizeLimitSetTest extends AbstractRuntimeTest
{
	public QuerySearchSizeLimitSetTest()
	{
		super(DayFieldTest.MODEL);
	}

	public void testIt()
	{
		final int defaulT = model.getConnectProperties().getQuerySearchSizeLimit();
		final Query q = TYPE.newQuery();
		assertEquals(defaulT, q.getSearchSizeLimit());

		// limit 5
		q.setSearchSizeLimit(5);
		assertEquals(5, q.getSearchSizeLimit());

		// limit 4
		q.setSearchSizeLimit(4);
		assertEquals(4, q.getSearchSizeLimit());

		try
		{
			q.setSearchSizeLimit(0);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("searchSizeLimit must be greater zero, but was 0", e.getMessage());
		}
		assertEquals(4, q.getSearchSizeLimit());
	}
}
