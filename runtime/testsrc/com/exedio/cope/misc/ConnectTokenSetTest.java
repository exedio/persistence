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

package com.exedio.cope.misc;

import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.misc.ConnectToken.getProperties;
import static com.exedio.cope.misc.ConnectToken.getTokens;
import static com.exedio.cope.misc.ConnectToken.issue;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.ConnectProperties;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.tojunit.ConnectTokenRule;
import com.exedio.cope.tojunit.MainRule;
import com.exedio.cope.tojunit.TestSources;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

@MainRule.Tag
public class ConnectTokenSetTest
{
	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class AnItem extends Item
	{
		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class,AnItem::new);

		@com.exedio.cope.instrument.Generated
		private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
	private static final Model model = new Model(AnItem.TYPE);

	static
	{
		model.enableSerialization(ConnectTokenSetTest.class, "model");
	}

	private final ConnectTokenRule ctr = new ConnectTokenRule(model);

	@Test void testNormal()
	{
		assertFalse(model.isConnected());
		assertSame(null, getProperties(model));
		assertNotSet();

		final ConnectProperties properties = ConnectProperties.create(TestSources.minimal());
		ctr.set(properties);
		assertSame(properties, getProperties(model));
		assertEquals(list(), getTokens(model));

		ctr.removeVoid();
		assertSame(null, getProperties(model));
		assertNotSet();
	}

	@Test void testNormalSupplier()
	{
		assertFalse(model.isConnected());
		assertSame(null, getProperties(model));
		assertNotSet();

		final ConnectProperties properties = ConnectProperties.create(TestSources.minimal());
		final AtomicInteger supplied = new AtomicInteger(0);
		ctr.set(() -> { supplied.incrementAndGet(); return properties; });
		assertEquals(0, supplied.get());
		assertSame(properties, getProperties(model));
		assertEquals(1, supplied.get());
		assertEquals(list(), getTokens(model));

		assertEquals(1, supplied.get());
		ctr.removeVoid();
		assertEquals(1, supplied.get());
		assertSame(null, getProperties(model));
		assertNotSet();
		assertEquals(1, supplied.get());
	}

	@Test void testNormalVoid()
	{
		assertFalse(model.isConnected());
		assertSame(null, getProperties(model));
		assertNotSet();

		final ConnectProperties properties = ConnectProperties.create(TestSources.minimal());
		ctr.set(properties);
		assertSame(properties, getProperties(model));
		assertEquals(list(), getTokens(model));

		ctr.removeVoid();
		assertSame(null, getProperties(model));
		assertNotSet();
	}

	@Test void testSupplierReturnsNull()
	{
		assertFalse(model.isConnected());
		assertSame(null, getProperties(model));
		assertNotSet();

		final AtomicInteger supplied = new AtomicInteger(0);
		ctr.set(new Supplier<>()
		{
			@Override
			public ConnectProperties get()
			{
				supplied.incrementAndGet();
				return null;
			}
			@Override
			public String toString()
			{
				return "toStringSupplier";
			}
		});
		assertEquals(0, supplied.get());
		try
		{
			getProperties(model);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(
					"ConnectToken properties supplier for " +
					"com.exedio.cope.misc.ConnectTokenSetTest#model " +
					"returned null: toStringSupplier",
					e.getMessage());
		}
		assertEquals(1, supplied.get());
		assertEquals(list(), getTokens(model));

		assertEquals(1, supplied.get());
		try
		{
			//noinspection resource
			issue(model, "tokenName");
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(
					"ConnectToken properties supplier for " +
					"com.exedio.cope.misc.ConnectTokenSetTest#model " +
					"returned null: toStringSupplier",
					e.getMessage());
		}
		assertEquals(2, supplied.get());
		assertEquals(list(), getTokens(model));
		assertFalse(model.isConnected());

		ctr.removeVoid();
		assertSame(null, getProperties(model));
		assertNotSet();
		assertEquals(2, supplied.get());
	}

	@Test void testSupplierFails()
	{
		assertFalse(model.isConnected());
		assertSame(null, getProperties(model));
		assertNotSet();

		ctr.set(() -> { throw new IllegalStateException("testSupplierFails"); });
		try
		{
			getProperties(model);
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("testSupplierFails", e.getMessage());
		}
		assertEquals(list(), getTokens(model));

		try
		{
			//noinspection resource
			issue(model, "tokenName");
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("testSupplierFails", e.getMessage());
		}
		assertEquals(list(), getTokens(model));
		assertFalse(model.isConnected());

		ctr.removeVoid();
		assertSame(null, getProperties(model));
		assertNotSet();
	}

	@Test void testRemoveWithoutSet()
	{
		assertFalse(model.isConnected());
		assertSame(null, getProperties(model));
		assertNotSet();

		ctr.removeVoid();
		assertSame(null, getProperties(model));
		assertNotSet();
	}

	@Test void testRemoveWithoutSetVoid()
	{
		assertFalse(model.isConnected());
		assertSame(null, getProperties(model));
		assertNotSet();

		ctr.removeVoid();
		assertSame(null, getProperties(model));
		assertNotSet();
	}

	@Test void testRestartID()
	{
		assertFalse(model.isConnected());
		assertSame(null, getProperties(model));
		assertNotSet();

		final ConnectProperties properties = ConnectProperties.create(TestSources.minimal());
		ctr.set(properties);
		assertSame(properties, getProperties(model));
		assertEquals(list(), getTokens(model));

		final ConnectToken token0 = issue(model, "name0");
		assertEquals(0, token0.getID());
		assertEquals(list(token0), getTokens(model));
		assertTrue(model.isConnected());

		token0.returnStrictly();
		assertEquals(list(), getTokens(model));
		assertFalse(model.isConnected());

		final ConnectToken token1 = issue(model, "name1");
		assertNotSame(token0, token1);
		assertEquals(1, token1.getID());
		assertEquals(list(token1), getTokens(model));
		assertTrue(model.isConnected());

		token1.returnStrictly();
		assertEquals(list(), getTokens(model));
		assertFalse(model.isConnected());

		ctr.removeVoid();
		assertSame(null, getProperties(model));
		assertNotSet();

		ctr.set(properties); // restarts token ids
		assertSame(properties, getProperties(model));
		assertEquals(list(), getTokens(model));

		final ConnectToken token0a = issue(model, "name0a");
		assertNotSame(token0, token0a);
		assertEquals(0, token0a.getID());
		assertEquals(list(token0a), getTokens(model));
		assertTrue(model.isConnected());

		token0a.returnStrictly();
		assertEquals(list(), getTokens(model));
		assertFalse(model.isConnected());

		ctr.removeVoid();
		assertSame(null, getProperties(model));
		assertNotSet();
	}

	private static void assertNotSet()
	{
		final String message =
			"No properties set for model " +
			"com.exedio.cope.misc.ConnectTokenSetTest#model, " +
			"use ConnectToken.setProperties.";
		try
		{
			getTokens(model);
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals(message, e.getMessage());
		}
	}
}
