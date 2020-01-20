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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TransactionToStringTest extends TestWithEnvironment
{
	public TransactionToStringTest()
	{
		super(CacheIsolationTest.MODEL);
		copeRule.omitTransaction();
	}

	@Test void testWithName()
	{
		final Transaction tx = model.startTransaction("txname");
		final String id = String.valueOf(tx.getID());
		assertEquals("txname", tx.getName());

		assertEquals(id+":txname", tx.toString());
		model.commit();
		assertEquals(id+"(closed):txname", tx.toString());
	}

	@Test void testWithoutName()
	{
		final Transaction tx = model.startTransaction(null);
		final String id = String.valueOf(tx.getID());
		assertEquals(null, tx.getName());

		assertEquals(id+":ANONYMOUS_TRANSACTION", tx.toString());
		model.commit();
		assertEquals(id+"(closed):ANONYMOUS_TRANSACTION", tx.toString());
	}
}
