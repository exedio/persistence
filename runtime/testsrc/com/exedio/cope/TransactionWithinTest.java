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

import static com.exedio.cope.TransactionTryTest.MODEL;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

@NullMarked
public class TransactionWithinTest extends TestWithEnvironment
{
	public TransactionWithinTest()
	{
		super(MODEL);
		copeRule.omitTransaction();
	}

	@Test void testWithinTransactionRunnableCommit()
	{
		model.withinTransaction("txName", () ->
		{
			//noinspection Convert2MethodRef
			setWithin();
		});
		assertFalse(model.hasCurrentTransaction());

		model.startTransaction(TransactionWithinTest.class.getName());
		assertEquals("withinName", item.getName());
	}

	@Test void testWithinTransactionRunnableRollback()
	{
		assertFails(
				() -> model.withinTransaction("txName", (Runnable)(() ->
				{
					setWithin();
					throw new IllegalArgumentException("tx causing rollback");
				})),
				IllegalArgumentException.class,
				"tx causing rollback");
		assertFalse(model.hasCurrentTransaction());

		model.startTransaction(TransactionWithinTest.class.getName());
		assertEquals("beforeName", item.getName());
	}

	@Test void testWithinTransactionSupplierCommit()
	{
		//noinspection RedundantCast
		assertSame("RESULT", model.withinTransaction("txName", (Supplier<String>)(() ->
		{
			setWithin();
			return "RESULT";
		})));
		assertFalse(model.hasCurrentTransaction());

		model.startTransaction(TransactionWithinTest.class.getName());
		assertEquals("withinName", item.getName());
	}

	@Test void testWithinTransactionSupplierRollback()
	{
		assertFails(
				() -> model.withinTransaction("txName", (Supplier<String>)(() ->
				{
					setWithin();
					throw new IllegalArgumentException("tx causing rollback");
				})),
				IllegalArgumentException.class,
				"tx causing rollback");
		assertFalse(model.hasCurrentTransaction());

		model.startTransaction(TransactionWithinTest.class.getName());
		assertEquals("beforeName", item.getName());
	}

	@Test void testWithinTransactionIntSupplierCommit()
	{
		//noinspection RedundantCast
		assertEquals(55, model.withinTransaction("txName", (IntSupplier)(() ->
		{
			setWithin();
			return 55;
		})));
		assertFalse(model.hasCurrentTransaction());

		model.startTransaction(TransactionWithinTest.class.getName());
		assertEquals("withinName", item.getName());
	}

	@Test void testWithinTransactionIntSupplierRollback()
	{
		assertFails(
				() -> model.withinTransaction("txName", (IntSupplier)(() ->
				{
					setWithin();
					throw new IllegalArgumentException("tx causing rollback");
				})),
				IllegalArgumentException.class,
				"tx causing rollback");
		assertFalse(model.hasCurrentTransaction());

		model.startTransaction(TransactionWithinTest.class.getName());
		assertEquals("beforeName", item.getName());
	}

	@Test void testWithinTransactionLongSupplierCommit()
	{
		//noinspection RedundantCast
		assertEquals(55l, model.withinTransaction("txName", (LongSupplier)(() ->
		{
			setWithin();
			return 55l;
		})));
		assertFalse(model.hasCurrentTransaction());

		model.startTransaction(TransactionWithinTest.class.getName());
		assertEquals("withinName", item.getName());
	}

	@Test void testWithinTransactionLongSupplierRollback()
	{
		assertFails(
				() -> model.withinTransaction("txName", (LongSupplier)(() ->
				{
					setWithin();
					throw new IllegalArgumentException("tx causing rollback");
				})),
				IllegalArgumentException.class,
				"tx causing rollback");
		assertFalse(model.hasCurrentTransaction());

		model.startTransaction(TransactionWithinTest.class.getName());
		assertEquals("beforeName", item.getName());
	}

	@Test void testWithinTransactionBooleanSupplierCommit()
	{
		//noinspection RedundantCast
		assertEquals(true, model.withinTransaction("txName", (BooleanSupplier)(() ->
		{
			setWithin();
			return true;
		})));
		assertFalse(model.hasCurrentTransaction());

		model.startTransaction(TransactionWithinTest.class.getName());
		assertEquals("withinName", item.getName());
	}

	@Test void testWithinTransactionBooleanSupplierRollback()
	{
		assertFails(
				() -> model.withinTransaction("txName", (BooleanSupplier)(() ->
				{
					setWithin();
					throw new IllegalArgumentException("tx causing rollback");
				})),
				IllegalArgumentException.class,
				"tx causing rollback");
		assertFalse(model.hasCurrentTransaction());

		model.startTransaction(TransactionWithinTest.class.getName());
		assertEquals("beforeName", item.getName());
	}

	@BeforeEach void beforeEach()
	{
		try(var tx = model.startTransactionTry(TransactionWithinTest.class.getName()))
		{
			item = new SimpleItem("beforeName");
			tx.commit();
		}
	}

	private void setWithin()
	{
		assertTrue(model.hasCurrentTransaction());
		assertEquals("txName", model.currentTransaction().getName());
		item.setName("withinName");
	}

	private SimpleItem item;

	@Test void testWithinTransactionNameNull()
	{
		assertFails(() -> model.withinTransaction(null, (Runnable          )(() -> { throw new AssertionFailedError(); })), NullPointerException.class, "name");
		assertFails(() -> model.withinTransaction(null, (       Supplier<?>)(() -> { throw new AssertionFailedError(); })), NullPointerException.class, "name");
		assertFails(() -> model.withinTransaction(null, (    IntSupplier   )(() -> { throw new AssertionFailedError(); })), NullPointerException.class, "name");
		assertFails(() -> model.withinTransaction(null, (   LongSupplier   )(() -> { throw new AssertionFailedError(); })), NullPointerException.class, "name");
		assertFails(() -> model.withinTransaction(null, (BooleanSupplier   )(() -> { throw new AssertionFailedError(); })), NullPointerException.class, "name");
	}
}
