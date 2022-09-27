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
import static com.exedio.cope.misc.ConnectToken.issueIfConnected;
import static com.exedio.cope.tojunit.Assert.list;
import static com.exedio.cope.tojunit.TestSources.describe;
import static com.exedio.cope.tojunit.TestSources.single;
import static com.exedio.cope.util.Sources.cascade;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.ConnectProperties;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.tojunit.TestSources;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ConnectTokenEqualityTest
{
	private ConnectProperties properties1;
	private ConnectToken token1;

	@BeforeEach void before()
	{
		properties1 = ConnectProperties.create(describe("DESC1", TestSources.minimal()));
		PROPERTIES.set(properties1);
		ConnectToken.setProperties(MODEL, PROPERTIES::get);
		token1 = issue(MODEL, "tokenName1");
	}

	@AfterEach void after()
	{
		ConnectToken.removePropertiesVoid(MODEL);
		PROPERTIES.set(null);
		if(MODEL.isConnected())
			MODEL.disconnect();
	}


	@Test void testSame()
	{
		final ConnectToken token2 = issue(MODEL, "tokenName2");
		assertTrue(MODEL.isConnected());
		assertSame(properties1, MODEL.getConnectProperties());
		assertSame(properties1, getProperties(MODEL));
		assertEquals(list(token1, token2), getTokens(MODEL));
	}

	@Test void testSameIfConnected()
	{
		final ConnectToken token2 = issueIfConnected(MODEL, "tokenName2");
		assertTrue(MODEL.isConnected());
		assertSame(properties1, MODEL.getConnectProperties());
		assertSame(properties1, getProperties(MODEL));
		assertEquals(list(token1, token2), getTokens(MODEL));
	}

	@Test void testNotSameButEqual()
	{
		final ConnectProperties properties2 = ConnectProperties.create(TestSources.minimal());
		PROPERTIES.set(properties2);
		assertTrue(MODEL.isConnected());
		assertSame(properties1, MODEL.getConnectProperties());
		assertSame(properties2, getProperties(MODEL));
		assertEquals(list(token1), getTokens(MODEL));

		final ConnectToken token2 = issue(MODEL, "tokenName2");
		assertTrue(MODEL.isConnected());
		assertSame(properties1, MODEL.getConnectProperties());
		assertSame(properties2, getProperties(MODEL));
		assertEquals(list(token1, token2), getTokens(MODEL));
	}

	@Test void testNotSameButEqualIfConnected()
	{
		final ConnectProperties properties2 = ConnectProperties.create(TestSources.minimal());
		PROPERTIES.set(properties2);
		assertTrue(MODEL.isConnected());
		assertSame(properties1, MODEL.getConnectProperties());
		assertSame(properties2, getProperties(MODEL));
		assertEquals(list(token1), getTokens(MODEL));

		final ConnectToken token2 = issueIfConnected(MODEL, "tokenName2");
		assertTrue(MODEL.isConnected());
		assertSame(properties1, MODEL.getConnectProperties());
		assertSame(properties2, getProperties(MODEL));
		assertEquals(list(token1, token2), getTokens(MODEL));
	}

	@Test void testNotEqual()
	{
		final ConnectProperties properties2 = ConnectProperties.create(describe("DESC2", cascade(
				single("connection.url", "jdbc:hsqldb:mem:copeutiltestNotEqual"),
				TestSources.minimal()
		)));
		PROPERTIES.set(properties2);
		assertTrue(MODEL.isConnected());
		assertSame(properties1, MODEL.getConnectProperties());
		assertSame(properties2, getProperties(MODEL));
		assertEquals(list(token1), getTokens(MODEL));

		final ConnectToken token2 = issue(MODEL, "tokenName2");
		assertTrue(MODEL.isConnected());
		assertSame(properties1, MODEL.getConnectProperties());
		assertSame(properties2, getProperties(MODEL));
		assertEquals(list(token1, token2), getTokens(MODEL));
	}

	@Test void testNotEqualIfConnected()
	{
		final ConnectProperties properties2 = ConnectProperties.create(cascade(
				single("connection.url", "jdbc:hsqldb:mem:copeutiltestNotEqual"),
				TestSources.minimal()
		));
		PROPERTIES.set(properties2);
		assertTrue(MODEL.isConnected());
		assertSame(properties1, MODEL.getConnectProperties());
		assertSame(properties2, getProperties(MODEL));
		assertEquals(list(token1), getTokens(MODEL));

		final ConnectToken token2 = issueIfConnected(MODEL, "tokenName2");
		assertTrue(MODEL.isConnected());
		assertSame(properties1, MODEL.getConnectProperties());
		assertSame(properties2, getProperties(MODEL));
		assertEquals(list(token1, token2), getTokens(MODEL));
	}


	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class MyItem extends Item
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class,MyItem::new);

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model MODEL = new Model(MyItem.TYPE);

	private static final AtomicReference<ConnectProperties> PROPERTIES = new AtomicReference<>();
}
