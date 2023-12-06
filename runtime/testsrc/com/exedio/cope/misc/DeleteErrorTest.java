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

import static com.exedio.cope.CacheIsolationItem.TYPE;
import static com.exedio.cope.tojunit.Assert.assertFails;

import com.exedio.cope.CacheIsolationItem;
import com.exedio.cope.CacheIsolationTest;
import com.exedio.cope.Model;
import com.exedio.cope.Query;
import com.exedio.cope.util.AssertionErrorJobContext;
import org.junit.jupiter.api.Test;

public class DeleteErrorTest
{
	@Test void testQueryNull()
	{
		final AssertionErrorJobContext ctx = new AssertionErrorJobContext();
		assertFails(
				() -> Delete.delete(null, 100, "tx", ctx),
				NullPointerException.class,
				"query");
	}

	@Test void testQueryOffset()
	{
		final Query<CacheIsolationItem> q = TYPE.newQuery();
		q.setPageUnlimited(55);
		final AssertionErrorJobContext ctx = new AssertionErrorJobContext();
		assertFails(
				() -> Delete.delete(q, 100, "tx", ctx),
				IllegalArgumentException.class,
				"query with page offset (55) not supported: " +
				"select this from CacheIsolationItem offset '55'");
	}

	@Test void testQueryLimit()
	{
		final Query<CacheIsolationItem> q = TYPE.newQuery();
		q.setPage(0, 66);
		final AssertionErrorJobContext ctx = new AssertionErrorJobContext();
		assertFails(
				() -> Delete.delete(q, 100, "tx", ctx),
				IllegalArgumentException.class,
				"query with page limit (66) not supported: " +
				"select this from CacheIsolationItem limit '66'");
	}

	@Test void testLimitZero()
	{
		final Query<CacheIsolationItem> q = TYPE.newQuery();
		assertFails(
				() -> Delete.delete(q, 0, null, null),
				IllegalArgumentException.class,
				"limit must be greater zero, but was 0");
	}

	@Test void testContextNull()
	{
		final Query<CacheIsolationItem> q = TYPE.newQuery();
		assertFails(
				() -> Delete.delete(q, 100, "tx", null),
				NullPointerException.class,
				"ctx");
	}

	@SuppressWarnings("unused") // OK: just load the model
	private static final Model MODEL = CacheIsolationTest.MODEL;
}
