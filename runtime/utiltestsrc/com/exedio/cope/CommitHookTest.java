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

import com.exedio.cope.junit.CopeAssert;
import java.awt.geom.IllegalPathStateException;
import java.io.File;
import java.util.ArrayList;

public class CommitHookTest extends CopeAssert
{
	public void testOne()
	{
		final StringBuilder bf = new StringBuilder();
		model.startTransaction("tx");
		assertEquals(0, model.currentTransaction().getCommitHookCount());
		model.addCommitHook(appender(bf, "one"));
		assertEquals(1, model.currentTransaction().getCommitHookCount());

		assertEquals("", bf.toString());
		model.commit();
		assertEquals("one,", bf.toString());
	}

	public void testTwo()
	{
		final StringBuilder bf = new StringBuilder();
		model.startTransaction("tx");
		assertEquals(0, model.currentTransaction().getCommitHookCount());
		model.addCommitHook(appender(bf, "one"));
		assertEquals(1, model.currentTransaction().getCommitHookCount());
		model.addCommitHook(appender(bf, "two"));
		assertEquals(2, model.currentTransaction().getCommitHookCount());

		assertEquals("", bf.toString());
		model.commit();
		assertEquals("one,two,", bf.toString());
	}

	public void testThrower()
	{
		final StringBuilder bf = new StringBuilder();
		model.startTransaction("tx");
		model.addCommitHook(appender(bf, "one"));
		model.addCommitHook(thrower("thrower"));
		model.addCommitHook(appender(bf, "two"));

		assertEquals("", bf.toString());
		assertEquals(true, model.hasCurrentTransaction());
		assertEquals(list(model.currentTransaction()), new ArrayList<>(model.getOpenTransactions()));
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
		assertEquals(false, model.hasCurrentTransaction());
		assertEquals(list(), new ArrayList<>(model.getOpenTransactions()));
	}

	public void testRollback()
	{
		final StringBuilder bf = new StringBuilder();
		model.startTransaction("tx");
		model.addCommitHook(appender(bf, "one"));

		assertEquals("", bf.toString());
		assertEquals(true, model.hasCurrentTransaction());
		model.rollback();
		assertEquals("", bf.toString());
		assertEquals(false, model.hasCurrentTransaction());
	}

	public void testNullHook()
	{
		model.startTransaction("tx");
		assertEquals(0, model.currentTransaction().getCommitHookCount());
		try
		{
			model.addCommitHook(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("hook", e.getMessage());
		}
		assertEquals(0, model.currentTransaction().getCommitHookCount());
	}

	public void testNoTransaction()
	{
		try
		{
			model.addCommitHook(null);
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("there is no cope transaction bound to this thread, see Model#startTransaction", e.getMessage());
		}
	}


	private static Runnable appender(final StringBuilder bf, final String value)
	{
		return new Runnable(){
			@Override
			public void run()
			{
				bf.append(value).append(',');
			}
		};
	}

	private static Runnable thrower(final String message)
	{
		return new Runnable(){
			@Override
			public void run()
			{
				throw new IllegalPathStateException(message);
			}
		};
	}




	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		model.connect(new ConnectProperties(new File("runtime/utiltest.properties")));
	}

	@Override
	protected void tearDown() throws Exception
	{
		model.rollbackIfNotCommitted();
		model.disconnect();
		super.tearDown();
	}


	private static final Model model = new Model(AnItem.TYPE);

	static class AnItem extends Item
	{
		private static final long serialVersionUID = 1l;
		static final Type<AnItem> TYPE = TypesBound.newType(AnItem.class);
		AnItem(final ActivationParameters ap) { super(ap); }
	}
}