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

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.exedio.cope.instrument.WrapperIgnore;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.awt.geom.IllegalPathStateException;
import java.io.File;
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
		catch(final IllegalPathStateException e)
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

	@Test public void testNullHook()
	{
		model.startTransaction("tx");
		assertEquals(0, model.currentTransaction().getPostCommitHookCount());
		try
		{
			model.addPostCommitHook(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("hook", e.getMessage());
		}
		assertEquals(0, model.currentTransaction().getPostCommitHookCount());
	}

	@SuppressFBWarnings("NP_NULL_PARAM_DEREF_ALL_TARGETS_DANGEROUS")
	@Test public void testNoTransaction()
	{
		try
		{
			model.addPostCommitHook(null);
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
			throw new IllegalPathStateException(message);
		};
	}

	private static void add(final int count, final Runnable hook)
	{
		final Transaction tx = model.currentTransaction();
		final int previousCount = tx.getPostCommitHookCount();

		model.addPostCommitHook(hook);
		assertEquals("count", count, tx.getPostCommitHookCount() - previousCount);
	}


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


	@SuppressWarnings("static-method")
	@Before public final void setUp()
	{
		model.connect(new ConnectProperties(new File("runtime/utiltest.properties")));
	}

	@SuppressWarnings("static-method")
	@After public final void tearDown()
	{
		model.rollbackIfNotCommitted();
		model.disconnect();
	}


	private static final Model model = new Model(AnItem.TYPE);

	@WrapperIgnore
	static class AnItem extends Item
	{
		private static final long serialVersionUID = 1l;
		static final Type<AnItem> TYPE = TypesBound.newType(AnItem.class);
		AnItem(final ActivationParameters ap) { super(ap); }
	}
}
