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

import static com.exedio.cope.CommitHookPostTest.FAIL;
import static com.exedio.cope.CommitHookPostTest.assertNoTransaction;
import static com.exedio.cope.CommitHookPostTest.assertTransaction;
import static com.exedio.cope.CommitHookPostTest.model;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.tojunit.TestSources;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CommitHookPreTest
{
	@Test void testOne()
	{
		final StringBuilder bf = new StringBuilder();
		model.startTransaction("tx");
		add(1, appender(bf, "one"));

		assertEquals("", bf.toString());
		model.commit();
		assertEquals("one,", bf.toString());
	}

	@Test void testTwo()
	{
		final StringBuilder bf = new StringBuilder();
		model.startTransaction("tx");
		add(1, appender(bf, "one"));
		add(1, appender(bf, "two"));

		assertEquals("", bf.toString());
		model.commit();
		assertEquals("one,two,", bf.toString());
	}

	@Test void testDuplicate()
	{
		final Runnable two;
		final StringBuilder bf = new StringBuilder();
		model.startTransaction("tx");
		add(1, appender(bf, "one"));
		add(1, two = appender(bf, "two", "same"));
		add(1, appender(bf, "three"));
		add(two, 0, 1, appender(bf, "four", "same"));
		add(1, appender(bf, "five"));

		assertEquals("", bf.toString());
		model.commit();
		assertEquals("one,two/same,three,five,", bf.toString());
	}

	@Test void testDuplicateNot()
	{
		final StringBuilder bf = new StringBuilder();
		model.startTransaction("tx");
		add(1, appender(bf, "one"));
		add(1, appender(bf, "two", "same"));
		add(1, appender(bf, "three"));
		add(1, appender(bf, "four", "other"));
		add(1, appender(bf, "five"));

		assertEquals("", bf.toString());
		model.commit();
		assertEquals("one,two/same,three,four/other,five,", bf.toString());
	}

	@Test void testThrower()
	{
		final StringBuilder bf = new StringBuilder();
		model.startTransaction("tx");
		add(1, appender(bf, "one"));
		add(1, thrower("thrower"));
		add(1, appender(bf, "two"));

		assertEquals("", bf.toString());
		assertTransaction();
		try
		{
			model.commit();
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("thrower", e.getMessage());
		}
		assertEquals("one,", bf.toString());
		assertTransaction();
	}

	@Test void testRollback()
	{
		final StringBuilder bf = new StringBuilder();
		model.startTransaction("tx");
		add(1, appender(bf, "one"));

		assertEquals("", bf.toString());
		assertTransaction();
		model.rollback();
		assertEquals("", bf.toString());
		assertNoTransaction();
	}

	@Test void testAddInHook()
	{
		final StringBuilder bf = new StringBuilder();
		model.startTransaction("tx");
		add(1, () ->
		{
			assertTransaction();
			bf.append("beforeAdd");
			model.addPreCommitHookIfAbsent(FAIL);
			bf.append("afterAdd");
		});

		assertEquals("", bf.toString());
		assertTransaction();
		try
		{
			model.commit();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals(
					"hooks are or have been handled",
					e.getMessage());
		}
		assertEquals("beforeAdd", bf.toString());
		assertTransaction();
	}

	@Test void testAddPostHookInHook()
	{
		final StringBuilder bf = new StringBuilder();
		model.startTransaction("tx");
		add(1, () ->
		{
			assertTransaction();
			bf.append("beforeAdd");
			model.addPostCommitHookIfAbsent(() ->
			{
				assertNoTransaction();
				bf.append("inPost");
			});
			bf.append("afterAdd");
		});

		assertEquals("", bf.toString());
		assertTransaction();
		model.commit();
		assertEquals("beforeAddafterAddinPost", bf.toString());
		assertNoTransaction();
	}

	@Test void testNullHook()
	{
		model.startTransaction("tx");
		assertEquals(0, model.currentTransaction().getPreCommitHookCount());
		assertEquals(0, model.currentTransaction().getPreCommitHookDuplicates());
		try
		{
			model.addPreCommitHookIfAbsent(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("hook", e.getMessage());
		}
		assertEquals(0, model.currentTransaction().getPreCommitHookCount());
		assertEquals(0, model.currentTransaction().getPreCommitHookDuplicates());
	}

	@Test void testNoTransaction()
	{
		try
		{
			model.addPreCommitHookIfAbsent(null);
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("there is no cope transaction bound to this thread, see Model#startTransaction", e.getMessage());
		}
	}


	private static Runnable appender(final StringBuilder bf, final String value)
	{
		return () ->
		{
			assertTransaction();
			bf.append(value).append(',');
		};
	}

	private static Runnable thrower(final String message)
	{
		return () ->
		{
			assertTransaction();
			throw new IllegalArgumentException(message);
		};
	}

	private static Runnable appender(final StringBuilder bf, final String value, final String identity)
	{
		return new EqualHook(bf, value, identity);
	}

	private static final class EqualHook implements Runnable
	{
		final StringBuilder bf;
		final String value;
		final String identity;

		EqualHook(final StringBuilder bf, final String value, final String identity)
		{
			this.bf = bf;
			this.value = value;
			this.identity = identity;
		}
		@Override
		public void run()
		{
			assertTransaction();
			bf.append(value).append('/').append(identity).append(',');
		}
		@Override
		@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
		public boolean equals(final Object o)
		{
			return identity.equals(((EqualHook)o).identity);
		}
		@Override
		public int hashCode()
		{
			return identity.hashCode();
		}
	}

	private static void add(final int count, final Runnable hook)
	{
		add(hook, count, 0, hook);
	}

	private static void add(
			final Runnable result, final int count, final int dups,
			final Runnable hook)
	{
		final Transaction tx = model.currentTransaction();
		final int previousCount = tx.getPreCommitHookCount();
		final int previousDups  = tx.getPreCommitHookDuplicates();

		assertSame(result, model.addPreCommitHookIfAbsent(hook));
		assertEquals(count, tx.getPreCommitHookCount()      - previousCount, "count");
		assertEquals(dups,  tx.getPreCommitHookDuplicates() - previousDups,  "dups");
		assertEquals(0,     tx.getPostCommitHookCount(),      "post count");
		assertEquals(0,     tx.getPostCommitHookDuplicates(), "post dups");
	}


	@BeforeEach final void setUp()
	{
		model.connect(ConnectProperties.create(TestSources.minimal()));
	}

	@AfterEach final void tearDown()
	{
		model.rollbackIfNotCommitted();
		model.disconnect();
	}
}
