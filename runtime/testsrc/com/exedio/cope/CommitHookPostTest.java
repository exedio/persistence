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
import static com.exedio.cope.tojunit.Assert.assertFails;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.tojunit.TestSources;
import java.util.ArrayList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CommitHookPostTest
{
	@Test void testOne()
	{
		final StringBuilder bf = new StringBuilder();
		assertEquals(false, model.isAddPreCommitHookAllowed());
		model.startTransaction("tx");
		assertEquals(true, model.isAddPreCommitHookAllowed());
		add(1, appender(bf, "one"));

		assertEquals("", bf.toString());
		assertEquals(true, model.isAddPreCommitHookAllowed());
		model.commit();
		assertEquals("one,", bf.toString());
		assertEquals(false, model.isAddPreCommitHookAllowed());
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
		assertEquals(false, model.isAddPreCommitHookAllowed());
		model.startTransaction("tx");
		assertEquals(true, model.isAddPreCommitHookAllowed());
		add(1, appender(bf, "one"));
		add(1, thrower("thrower"));
		add(1, appender(bf, "two"));

		assertEquals("", bf.toString());
		assertTransaction();
		assertEquals(true, model.isAddPreCommitHookAllowed());
		assertFails(
				model::commit,
				IllegalArgumentException.class,
				"thrower");
		assertEquals("one,", bf.toString());
		assertNoTransaction();
		assertEquals(false, model.isAddPreCommitHookAllowed());
	}

	@Test void testRollback()
	{
		final StringBuilder bf = new StringBuilder();
		assertEquals(false, model.isAddPreCommitHookAllowed());
		model.startTransaction("tx");
		assertEquals(true, model.isAddPreCommitHookAllowed());
		add(1, appender(bf, "one"));

		assertEquals("", bf.toString());
		assertTransaction();
		assertEquals(true, model.isAddPreCommitHookAllowed());
		model.rollback();
		assertEquals("", bf.toString());
		assertNoTransaction();
		assertEquals(false, model.isAddPreCommitHookAllowed());
	}

	@Test void testAddInHook()
	{
		final StringBuilder bf = new StringBuilder();
		assertEquals(false, model.isAddPreCommitHookAllowed());
		model.startTransaction("tx");
		assertEquals(true, model.isAddPreCommitHookAllowed());
		add(1, () ->
		{
			assertNoTransaction();
			assertEquals(false, model.isAddPreCommitHookAllowed());
			bf.append("beforeAdd");
			model.addPostCommitHookIfAbsent(FAIL);
			bf.append("afterAdd");
		});

		assertEquals("", bf.toString());
		assertTransaction();
		assertEquals(true, model.isAddPreCommitHookAllowed());
		assertFails(
				model::commit,
				IllegalStateException.class,
				"there is no cope transaction bound to this thread for model " + model + ", " +
				"see Model#startTransaction");
		assertEquals("beforeAdd", bf.toString());
		assertNoTransaction();
		assertEquals(false, model.isAddPreCommitHookAllowed());
	}

	@Test void testAddPreHookInHook()
	{
		final StringBuilder bf = new StringBuilder();
		assertEquals(false, model.isAddPreCommitHookAllowed());
		model.startTransaction("tx");
		assertEquals(true, model.isAddPreCommitHookAllowed());
		add(1, () ->
		{
			assertNoTransaction();
			assertEquals(false, model.isAddPreCommitHookAllowed());
			bf.append("beforeAdd");
			model.addPreCommitHookIfAbsent(FAIL);
			bf.append("afterAdd");
		});

		assertEquals("", bf.toString());
		assertTransaction();
		assertFails(
				model::commit,
				IllegalStateException.class,
				"there is no cope transaction bound to this thread for model " + model + ", " +
				"see Model#startTransaction");
		assertEquals("beforeAdd", bf.toString());
		assertNoTransaction();
		assertEquals(false, model.isAddPreCommitHookAllowed());
	}

	@Test void testNullHook()
	{
		model.startTransaction("tx");
		assertEquals(0, model.currentTransaction().getPostCommitHookCount());
		assertEquals(0, model.currentTransaction().getPostCommitHookDuplicates());
		assertFails(
				() -> model.addPostCommitHookIfAbsent(null),
				NullPointerException.class,
				"hook");
		assertEquals(0, model.currentTransaction().getPostCommitHookCount());
		assertEquals(0, model.currentTransaction().getPostCommitHookDuplicates());
	}

	@Test void testNoTransaction()
	{
		assertFails(
				() -> model.addPostCommitHookIfAbsent(null),
				IllegalStateException.class,
				"there is no cope transaction bound to this thread for model " + model + ", " +
				"see Model#startTransaction");
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

	private record EqualHook(
			StringBuilder bf,
			String value,
			String identity)
			implements Runnable
	{
		@Override
		public void run()
		{
			assertNoTransaction();
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
		final int previousCount = tx.getPostCommitHookCount();
		final int previousDups  = tx.getPostCommitHookDuplicates();

		assertSame(result, model.addPostCommitHookIfAbsent(hook));
		assertEquals(count, tx.getPostCommitHookCount()      - previousCount, "count");
		assertEquals(dups,  tx.getPostCommitHookDuplicates() - previousDups,  "dups");
		assertEquals(0,     tx.getPreCommitHookCount(),      "pre count");
		assertEquals(0,     tx.getPreCommitHookDuplicates(), "pre dups");
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


	static final Model model = new Model(AnItem.TYPE);

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static class AnItem extends Item
	{
		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class,AnItem::new);

		@com.exedio.cope.instrument.Generated
		protected AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	static final Runnable FAIL = Assertions::fail;

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
