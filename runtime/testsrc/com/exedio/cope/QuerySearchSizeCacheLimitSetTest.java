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

import static com.exedio.cope.DayItem.TYPE;
import static java.lang.Integer.MIN_VALUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

public class QuerySearchSizeCacheLimitSetTest extends TestWithEnvironment
{
	public QuerySearchSizeCacheLimitSetTest()
	{
		super(DayFieldTest.MODEL);
	}

	@Test void testIt()
	{
		final int defaulT = model.getConnectProperties().getQueryCacheSizeLimit();
		final Query<?> q = TYPE.newQuery();
		assertEquals(defaulT, q.getSearchSizeCacheLimit());

		// limit 5
		q.setSearchSizeCacheLimit(5);
		assertEquals(5, q.getSearchSizeCacheLimit());

		// limit 1
		q.setSearchSizeCacheLimit(1);
		assertEquals(1, q.getSearchSizeCacheLimit());

		// failures
		try
		{
			q.setSearchSizeCacheLimit(0);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("searchSizeCacheLimit must be greater zero, but was 0", e.getMessage());
		}
		assertEquals(1, q.getSearchSizeCacheLimit());
		try
		{
			q.setSearchSizeCacheLimit(MIN_VALUE);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("searchSizeCacheLimit must be greater zero, but was " + MIN_VALUE, e.getMessage());
		}
		assertEquals(1, q.getSearchSizeCacheLimit());
	}
}
