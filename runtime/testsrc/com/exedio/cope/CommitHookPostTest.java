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

import static com.exedio.cope.instrument.Visibility.NONE;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.tojunit.TestSources;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CommitHookPostTest
{
	@Test public void testOne()
	{
		final StringBuilder bf = new StringBuilder();
		model.startTransaction("tx");
		add(1, appender(bf, "one"));

		assertEquals("", bf.toString());
		model.commit();
		assertEquals("one,", bf.toString());
	}

	@Test public void testTwo()
	{
		final StringBuilder bf = new StringBuilder();
		model.startTransaction("tx");
		add(1, appender(bf, "one"));
		add(1, appender(bf, "two"));

		assertEquals("", bf.toString());
		model.commit();
		assertEquals("one,two,", bf.toString());
	}

	@Test public void testDuplicate()
	{
		Runnable two = null;
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

	@Test public void testDuplicateNot()
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

	@Test public void testThrower()
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
		assertNoTransaction();
	}

	@Test public void testRollback()
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

	@Test public void testAddInHook()
	{
		final StringBuilder bf = new StringBuilder();
		model.startTransaction("tx");
		add(1, () ->
		{
			assertNoTransaction();
			bf.append("beforeAdd");
			model.addPostCommitHookIfAbsent(FAIL);
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
					"there is no cope transaction bound to this thread, see Model#startTransaction",
					e.getMessage());
		}
		assertEquals("beforeAdd", bf.toString());
		assertNoTransaction();
	}

	@Test public void testAddPreHookInHook()
	{
		final StringBuilder bf = new StringBuilder();
		model.startTransaction("tx");
		add(1, () ->
		{
			assertNoTransaction();
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
					"there is no cope transaction bound to this thread, see Model#startTransaction",
					e.getMessage());
		}
		assertEquals("beforeAdd", bf.toString());
		assertNoTransaction();
	}

	@Test public void testNullHook()
	{
		model.startTransaction("tx");
		assertEquals(0, model.currentTransaction().getPostCommitHookCount());
		assertEquals(0, model.currentTransaction().getPostCommitHookDuplicates());
		try
		{
			model.addPostCommitHookIfAbsent(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("hook", e.getMessage());
		}
		assertEquals(0, model.currentTransaction().getPostCommitHookCount());
		assertEquals(0, model.currentTransaction().getPostCommitHookDuplicates());
	}

	@SuppressFBWarnings("NP_NULL_PARAM_DEREF_ALL_TARGETS_DANGEROUS")
	@Test public void testNoTransaction()
	{
		try
		{
			model.addPostCommitHookIfAbsent(null);
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
			assertNoTransaction();
			bf.append(value).append(',');
		};
	}

	private static Runnable thrower(final String message)
	{
		return () ->
		{
			assertNoTransaction();
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
		@SuppressWarnings("synthetic-access")
		public void run()
		{
			assertNoTransaction();
			bf.append(value).append('/').append(identity).append(',');
		}
		@Override
		@SuppressFBWarnings({"BC_EQUALS_METHOD_SHOULD_WORK_FOR_ALL_OBJECTS","NP_EQUALS_SHOULD_HANDLE_NULL_ARGUMENT"})
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
		final int previousCount = tx.getPostCommitHookCount();
		final int previousDups  = tx.getPostCommitHookDuplicates();

		assertSame(result, model.addPostCommitHookIfAbsent(hook));
		assertEquals("count", count, tx.getPostCommitHookCount() - previousCount);
		assertEquals("dups",  dups,  tx.getPostCommitHookDuplicates() - previousDups);
		assertEquals("pre count", 0, tx.getPreCommitHookCount());
		assertEquals("pre dups",  0, tx.getPreCommitHookDuplicates());
	}


	@SuppressWarnings("static-method")
	@Before public final void setUp()
	{
		model.connect(ConnectProperties.create(TestSources.minimal()));
	}

	@SuppressWarnings("static-method")
	@After public final void tearDown()
	{
		model.rollbackIfNotCommitted();
		model.disconnect();
	}


	static final Model model = new Model(AnItem.TYPE);

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static class AnItem extends Item
	{
		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		protected AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	static final Runnable FAIL = () -> { fail(); };

	static void assertNoTransaction()
	{
		assertFalse(model.hasCurrentTransaction());
		assertTrue (model.getOpenTransactions().isEmpty());
	}

	static void assertTransaction()
	{
		assertEquals(true, model.hasCurrentTransaction());
		assertEquals(asList(model.currentTransaction()), new ArrayList<>(model.getOpenTransactions()));
	}
}
