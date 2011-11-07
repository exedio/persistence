/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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

import java.sql.SQLException;

public class UniqueDoubleTest extends AbstractRuntimeTest
{
	static final Model MODEL = new Model(UniqueDoubleItem.TYPE);

	static
	{
		MODEL.enableSerialization(UniqueDoubleTest.class, "MODEL");
	}

	public UniqueDoubleTest()
	{
		super(MODEL);
	}

	public void test()
	{
		assertEqualsUnmodifiable(
			list(
				UniqueDoubleItem.TYPE.getThis(),
				UniqueDoubleItem.string,
				UniqueDoubleItem.integer,
				UniqueDoubleItem.doubleUnique
			),
			UniqueDoubleItem.TYPE.getDeclaredFeatures());
		assertEqualsUnmodifiable(
			list(
				UniqueDoubleItem.TYPE.getThis(),
				UniqueDoubleItem.string,
				UniqueDoubleItem.integer,
				UniqueDoubleItem.doubleUnique
			),
			UniqueDoubleItem.TYPE.getFeatures());
		assertEquals("doubleUnique", UniqueDoubleItem.doubleUnique.getName());
		assertEquals(UniqueDoubleItem.TYPE, UniqueDoubleItem.doubleUnique.getType());
		assertEqualsUnmodifiable(
			list(UniqueDoubleItem.string, UniqueDoubleItem.integer),
			UniqueDoubleItem.doubleUnique.getFields());
		assertEqualsUnmodifiable(
			list(UniqueDoubleItem.doubleUnique),
			UniqueDoubleItem.string.getUniqueConstraints());
		assertEqualsUnmodifiable(
			list(UniqueDoubleItem.doubleUnique),
			UniqueDoubleItem.integer.getUniqueConstraints());

		assertSerializedSame(UniqueDoubleItem.doubleUnique, 386);

		assertEquals(null, UniqueDoubleItem.forDoubleUnique("a", 1));

		final UniqueDoubleItem a1 = new UniqueDoubleItem("a", 1);
		assertEquals(a1, UniqueDoubleItem.forDoubleUnique("a", 1));

		final UniqueDoubleItem a2 = new UniqueDoubleItem("a", 2);
		assertEquals(a2, UniqueDoubleItem.forDoubleUnique("a", 2));

		final UniqueDoubleItem b1 = new UniqueDoubleItem("b", 1);
		assertEquals(b1, UniqueDoubleItem.forDoubleUnique("b", 1));

		final UniqueDoubleItem b2 = new UniqueDoubleItem("b", 2);
		assertEquals(b2, UniqueDoubleItem.forDoubleUnique("b", 2));

		assertEquals(b1, UniqueDoubleItem.forDoubleUnique("b", 1));
		try
		{
			new UniqueDoubleItem("b", 1);
			fail();
		}
		catch(final UniqueViolationException e)
		{
			assertEquals(a1.doubleUnique, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals("unique violation for " + a1.doubleUnique, e.getMessage());
			assertCause(e);
		}
		assertEquals(b1, UniqueDoubleItem.forDoubleUnique("b", 1));
		try
		{
			UniqueDoubleItem.TYPE.newItem(
					UniqueDoubleItem.string.map("b"),
					UniqueDoubleItem.integer.map(1)
				);
			fail();
		}
		catch(final UniqueViolationException e)
		{
			assertEquals(a1.doubleUnique, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals("unique violation for " + a1.doubleUnique, e.getMessage());
			assertCause(e);
		}
		assertEquals(b1, UniqueDoubleItem.forDoubleUnique("b", 1));

		try
		{
			b2.setInteger(1);
			fail();
		}
		catch(final UniqueViolationException e)
		{
			assertEquals(a1.doubleUnique, e.getFeature());
			assertEquals(b2, e.getItem());
			assertEquals("unique violation on " + b2 + " for " + a1.doubleUnique, e.getMessage());
			assertCause(e);
		}
		assertEquals(2, b2.getInteger());

		try
		{
			b2.set(b2.integer.map(1));
			fail();
		}
		catch(final UniqueViolationException e)
		{
			assertEquals(a1.doubleUnique, e.getFeature());
			assertEquals(b2, e.getItem());
			assertEquals("unique violation on " + b2 + " for " + a1.doubleUnique, e.getMessage());
			assertCause(e);
		}
		assertEquals(2, b2.getInteger());

		// test setting the value already set
		b2.setInteger(2);
		assertEquals(2, b2.getInteger());

		assertDelete(b2);
		assertDelete(b1);

		final UniqueDoubleItem b1X = new UniqueDoubleItem("b", 1);
		assertEquals(b1X, UniqueDoubleItem.forDoubleUnique("b", 1));

		assertDelete(a2);
		assertDelete(a1);
		assertDelete(b1X);
	}

	private void assertCause(final UniqueViolationException e)
	{
		final Throwable cause = e.getCause();
		if(model.connect().executor.supportsUniqueViolation)
		{
			assertNotNull(e.getCause());
			assertTrue(cause.getClass().getName(), cause instanceof SQLException);
		}
		else
		{
			assertEquals(null, cause);
		}
	}
}
