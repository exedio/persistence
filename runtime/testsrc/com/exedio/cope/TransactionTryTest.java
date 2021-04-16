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

import static com.exedio.cope.SimpleItem.TYPE;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

public class TransactionTryTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(TYPE);

	public TransactionTryTest()
	{
		super(MODEL);
		copeRule.omitTransaction();
	}

	@Test void testSuccess()
	{
		assertFalse(model.hasCurrentTransaction());

		final SimpleItem item;

		try(TransactionTry tx = model.startTransactionTry("txName"))
		{
			assertTrue(model.hasCurrentTransaction());
			assertContains(TYPE.search());

			item = new SimpleItem("itemName");
			tx.commit();
			assertFalse(model.hasCurrentTransaction());
		}
		assertFalse(model.hasCurrentTransaction());

		model.startTransaction(TransactionTryTest.class.getName());
		assertTrue(item.existsCopeItem());
		assertEquals("itemName", item.getName());
	}

	@Test void testSuccessReturnObject()
	{
		assertFalse(model.hasCurrentTransaction());

		final SimpleItem item;
		final Object result = new Object();

		try(TransactionTry tx = model.startTransactionTry("txName"))
		{
			assertTrue(model.hasCurrentTransaction());
			assertContains(TYPE.search());

			item = new SimpleItem("itemName");
			assertSame(result, tx.commit(result));
			assertFalse(model.hasCurrentTransaction());
		}
		assertFalse(model.hasCurrentTransaction());

		model.startTransaction(TransactionTryTest.class.getName());
		assertTrue(item.existsCopeItem());
		assertEquals("itemName", item.getName());
	}

	@Test void testSuccessReturnInt()
	{
		assertFalse(model.hasCurrentTransaction());

		final SimpleItem item;

		try(TransactionTry tx = model.startTransactionTry("txName"))
		{
			assertTrue(model.hasCurrentTransaction());
			assertContains(TYPE.search());

			item = new SimpleItem("itemName");
			assertEquals(567, tx.commit(567));
			assertFalse(model.hasCurrentTransaction());
		}
		assertFalse(model.hasCurrentTransaction());

		model.startTransaction(TransactionTryTest.class.getName());
		assertTrue(item.existsCopeItem());
		assertEquals("itemName", item.getName());
	}

	@Test void testSuccessReturnLong()
	{
		assertFalse(model.hasCurrentTransaction());

		final SimpleItem item;

		try(TransactionTry tx = model.startTransactionTry("txName"))
		{
			assertTrue(model.hasCurrentTransaction());
			assertContains(TYPE.search());

			item = new SimpleItem("itemName");
			assertEquals(567l, tx.commit(567l));
			assertFalse(model.hasCurrentTransaction());
		}
		assertFalse(model.hasCurrentTransaction());

		model.startTransaction(TransactionTryTest.class.getName());
		assertTrue(item.existsCopeItem());
		assertEquals("itemName", item.getName());
	}

	@Test void testFail()
	{
		assertFalse(model.hasCurrentTransaction());

		SimpleItem item = null;

		try
		{
			try(TransactionTry tx = model.startTransactionTry("txName"))
			{
				assertTrue(model.hasCurrentTransaction());
				assertTrue(tx.hasCurrentTransaction());
				assertContains(TYPE.search());

				item = new SimpleItem("itemName");
				if(returnTrue())
					throw new RuntimeException("exceptionMessage");
			}
			fail();
		}
		catch(final RuntimeException e)
		{
			assertFalse(model.hasCurrentTransaction());
			assertEquals("exceptionMessage", e.getMessage());
		}
		assertFalse(model.hasCurrentTransaction());
		assertNotNull(item);

		model.startTransaction(TransactionTryTest.class.getName());
		assertFalse(item.existsCopeItem());
	}

	@Test void testNoCommit()
	{
		assertFalse(model.hasCurrentTransaction());

		final SimpleItem item;

		try(TransactionTry tx = model.startTransactionTry("txName"))
		{
			assertTrue(model.hasCurrentTransaction());
			assertTrue(tx.hasCurrentTransaction());
			assertContains(TYPE.search());

			item = new SimpleItem("itemName");
			// do not commit
		}
		assertFalse(model.hasCurrentTransaction());
		assertNotNull(item);

		model.startTransaction(TransactionTryTest.class.getName());
		assertFalse(item.existsCopeItem());
	}

	private static boolean returnTrue()
	{
		return true;
	}
}
