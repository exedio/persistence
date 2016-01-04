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

import com.exedio.cope.util.Sources;
import java.util.Properties;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Test;

public class ItemCacheLimitTest extends TestCase
{
	@Test public void testNormal()
	{
		model.connect(props(10100));
		assertLimits(10000, 100);
	}

	@Test public void testMuch()
	{
		model.connect(props(101000));
		assertLimits(100000, 1000);
	}

	@Test public void testOverflow()
	{
		model.connect(props(1010000));
		assertLimits(1000000, 10000);
	}

	private static void assertLimits(final int limit1, final int limit2)
	{
		final ItemCacheInfo[] infos = model.getItemCacheInfo();
		assertEquals(2, infos.length);

		assertEquals(Item1.TYPE.getID(), Item1.TYPE, infos[0].getType());
		assertEquals(Item2.TYPE.getID(), Item2.TYPE, infos[1].getType());

		assertEquals(Item1.TYPE.getID(), limit1, infos[0].getLimit());
		assertEquals(Item2.TYPE.getID(), limit2, infos[1].getLimit());
	}

	private static ConnectProperties props(final int limit)
	{
		final Properties source = new Properties();
		source.setProperty("connection.url", "jdbc:hsqldb:mem:ItemCacheLimitOverflowTest");
		source.setProperty("connection.username", "sa");
		source.setProperty("connection.password", "");
		source.setProperty("cache.item.limit", String.valueOf(limit));
		return new ConnectProperties(
				Sources.view(source , "MediaUrlSecretTestSource"),
				null);
	}

	@Override
	@After public final void tearDown() throws Exception
	{
		if(model.isConnected())
			model.disconnect();
		super.tearDown();
	}

	@CopeCacheWeight(10000)
	static class Item1 extends Item
	{
		static final Type<Item1> TYPE = TypesBound.newType(Item1.class);
		private static final long serialVersionUID = 1l;
		private Item1(final ActivationParameters ap) { super(ap); }
	}

	static class Item2 extends Item
	{
		static final Type<Item2> TYPE = TypesBound.newType(Item2.class);
		private static final long serialVersionUID = 1l;
		private Item2(final ActivationParameters ap) { super(ap); }
	}

	private static final Model model = new Model(Item1.TYPE, Item2.TYPE);
}
