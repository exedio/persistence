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

import static com.exedio.cope.TransactionUniqueTest.MyItem.TYPE;
import static com.exedio.cope.instrument.Visibility.PRIVATE;
import static java.util.Arrays.asList;
import static java.util.Locale.ENGLISH;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.tojunit.SI;
import com.exedio.dsmf.SQLRuntimeException;
import java.util.ArrayList;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class TransactionUniqueTest extends TestWithEnvironment
{
	public TransactionUniqueTest()
	{
		super(MODEL);
	}

	@Test void test() throws InterruptedException
	{
		switch(dialect)
		{
			case hsqldb:     testFails();  break;
			case mysql:      testBlocks(); break;
			case oracle:     testBlocks(); break;
			case postgresql: testBlocks(); break;
			default:
				fail("" + dialect);
		}
	}

	private void testFails()
	{
		final MyItem item = MyItem.create();
		item.assertIt();

		final Transaction tx1 = MODEL.leaveTransaction();

		startTransaction();
		try
		{
			// other databases do block below until other transaction ends
			MyItem.create();
			fail();
		}
		catch(final SQLRuntimeException e)
		{
			assertInsert(e);
			Throwable cause = e.getCause();
			assertEquals("transaction rollback: serialization failure", cause.getMessage());
			cause = cause.getCause();
			assertEquals("transaction rollback: serialization failure", cause.getMessage());
			cause = cause.getCause();
			assertNull(cause);
		}
		MODEL.rollback();
		MODEL.joinTransaction(tx1);
		item.assertIt();

		restartTransaction();
		item.assertIt();
	}

	@SuppressWarnings("HardcodedLineSeparator") // OK: newline in sql error
	private void testBlocks() throws InterruptedException
	{
		final MyItem item = MyItem.create();
		item.assertIt();

		final BlockRunner runner = new BlockRunner();
		assertEquals(null, runner.assertIt(false, false));

		final Thread thread = new Thread(runner);
		thread.start();
		Thread.sleep(100);
		assertEquals(Thread.State.RUNNABLE, thread.getState());
		final StackTraceElement[] trace = thread.getStackTrace();
		//for(final StackTraceElement e : trace) System.out.println(e.getClassName() + '-' + e.getMethodName());
		assertTraceContains(MyItem.class.getName(), "create", trace);
		assertTraceContains("java.net.SocketInputStream", "read", trace);
		assertEquals(null, runner.assertIt(true, false));

		commit();
		Thread.sleep(100);
		assertEquals(Thread.State.TERMINATED, thread.getState());
		final Exception failure = runner.assertIt(true, true);
		assertNotNull(failure);
		switch(dialect)
		{
			case mysql:
				if(MODEL.getConnectProperties().isSupportDisabledForUniqueViolation())
				{
					assertInsert(failure);
					assertEquals(
							"Duplicate entry 'collision' for key 'MyItem_field_Unq'",
							dropMariaConnectionId(failure.getCause().getMessage()));
				}
				else
				{
					assertEquals("unique violation for MyItem.fieldImplicitUnique", failure.getMessage());
					assertEquals(UniqueViolationException.class, failure.getClass());
				}
				break;
			case oracle:
				assertInsert(failure);
				final String schema = MODEL.getConnectProperties().getConnectionUsername().toUpperCase(ENGLISH);
				assertEquals(
						"ORA-00001: unique constraint (" + schema + ".MyItem_field_Unq) violated\n",
						failure.getCause().getMessage());
				break;
			case postgresql:
				assertInsert(failure);
				assertEquals(
						"ERROR: duplicate key value violates unique constraint \"MyItem_field_Unq\"\n" +
						"  Detail: Key (\"field\")=(collision) already exists.",
						failure.getCause().getMessage());
				break;
			case hsqldb: // runs testFails
			default:
				fail("" + dialect);
		}
		assertEquals(asList(), new ArrayList<>(MODEL.getOpenTransactions()));

		startTransaction();
		item.assertIt();
	}

	class BlockRunner implements Runnable
	{
		private volatile boolean beforeCreate = false;
		private volatile boolean afterCreate = false;
		private volatile Exception failure;
		private volatile boolean afterRollback = false;

		@Override public void run()
		{
			startTransaction();
			try
			{
				beforeCreate = true;
				MyItem.create();
				afterCreate = true;
			}
			catch(final Exception e)
			{
				failure = e;
			}
			MODEL.rollback();
			afterRollback = true;
		}

		Exception assertIt(
				final boolean beforeCreate,
				final boolean afterRollback)
		{
			assertEquals(beforeCreate, this.beforeCreate);
			assertEquals(false, this.afterCreate);
			assertEquals(afterRollback, this.afterRollback);
			return this.failure;
		}
	}

	private static void assertInsert(final Exception e)
	{
		final String m = e.getMessage();
		assertTrue(m.startsWith("INSERT INTO " + SI.tab(TYPE) + "("), m);
		assertEquals(SQLRuntimeException.class, e.getClass());
	}

	private static void assertTraceContains(
			final String className,
			final String methodName,
			final StackTraceElement[] trace)
	{
		assertTrue(
				Stream.of(trace).anyMatch(t ->
						className.equals(t.getClassName()) &&
						methodName.equals(t.getMethodName())), 
				asList(trace).toString());
	}

	@AfterEach void after()
	{
		model.rollbackIfNotCommitted();

		// TODO may we should put this into CopeModelTest
		for(final Transaction tx : model.getOpenTransactions())
		{
			System.out.println("REMOVE ORPHANED TRANSACTION " + tx);
			model.joinTransaction(tx);
			model.rollbackIfNotCommitted();
		}
	}


	@com.exedio.cope.instrument.WrapperType(constructor=PRIVATE, indent=2, comments=false) // TODO use import, but this is not accepted by javac
	static final class MyItem extends Item
	{
		static final StringField field = new StringField().unique();

		// the name of this method is checked in stack trace
		static MyItem create()
		{
			return new MyItem("collision");
		}

		void assertIt()
		{
			assertEquals("collision", getField());
			assertEquals(asList(this), TYPE.search(null, TYPE.getThis(), true));
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private MyItem(
					@javax.annotation.Nonnull final java.lang.String field)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException,
					com.exedio.cope.UniqueViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				MyItem.field.map(field),
			});
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private MyItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nonnull
		java.lang.String getField()
		{
			return MyItem.field.get(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void setField(@javax.annotation.Nonnull final java.lang.String field)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.UniqueViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			MyItem.field.set(this,field);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		static MyItem forField(@javax.annotation.Nonnull final java.lang.String field)
		{
			return MyItem.field.searchUnique(MyItem.class,field);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nonnull
		static MyItem forFieldStrict(@javax.annotation.Nonnull final java.lang.String field)
				throws
					java.lang.IllegalArgumentException
		{
			return MyItem.field.searchUniqueStrict(MyItem.class,field);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@SuppressWarnings("unused") private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	static final Model MODEL = new Model(TYPE);
}
