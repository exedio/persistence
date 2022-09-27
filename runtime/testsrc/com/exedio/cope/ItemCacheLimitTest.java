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

import static com.exedio.cope.RuntimeTester.getItemCacheStatistics;
import static com.exedio.cope.instrument.Visibility.NONE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.util.Sources;
import java.util.Properties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class ItemCacheLimitTest
{
	@Test void testNormal()
	{
		model.connect(props(10100));
		assertLimits(10100);
	}

	@Test void testMuch()
	{
		model.connect(props(101000));
		assertLimits(101000);
	}

	@Test void testOverflow()
	{
		model.connect(props(1010000));
		assertLimits(1010000);
	}

	private static void assertLimits(final int globalLimit)
	{
		final ItemCacheStatistics statistics = getItemCacheStatistics(model);
		assertEquals(globalLimit, statistics.getLimit());

		final ItemCacheInfo[] infos=statistics.getDetails();
		assertEquals(2, infos.length);

		assertEquals(Item1.TYPE, infos[0].getType(), Item1.TYPE.getID());
		assertEquals(Item2.TYPE, infos[1].getType(), Item2.TYPE.getID());
	}

	private static ConnectProperties props(final int limit)
	{
		final Properties source = new Properties();
		source.setProperty("connection.url", "jdbc:hsqldb:mem:ItemCacheLimitOverflowTest");
		source.setProperty("connection.username", "sa");
		source.setProperty("connection.password", "");
		source.setProperty("cache.item.limit", String.valueOf(limit));
		return ConnectProperties.create(
				Sources.view(source , "MediaUrlSecretTestSource"));
	}

	@AfterEach final void tearDown()
	{
		if(model.isConnected())
			model.disconnect();
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class Item1 extends Item
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<Item1> TYPE = com.exedio.cope.TypesBound.newType(Item1.class,Item1::new);

		@com.exedio.cope.instrument.Generated
		private Item1(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class Item2 extends Item
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<Item2> TYPE = com.exedio.cope.TypesBound.newType(Item2.class,Item2::new);

		@com.exedio.cope.instrument.Generated
		private Item2(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model model = new Model(Item1.TYPE, Item2.TYPE);
}
