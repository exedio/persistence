/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.misc.ConnectToken.getProperties;
import static com.exedio.cope.misc.ConnectToken.getTokens;
import static com.exedio.cope.misc.ConnectToken.removeProperties;
import static com.exedio.cope.misc.ConnectToken.setProperties;

import java.io.File;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.ConnectProperties;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import com.exedio.cope.junit.CopeAssert;

public class ConnectTokenSetTest extends CopeAssert
{
	private static final class AnItem extends Item
	{
		private AnItem(final ActivationParameters ap) { super(ap); }
		private static final long serialVersionUID = 1l;
	}
	private static final Type<AnItem> type = TypesBound.newType(AnItem.class);
	private static final Model model = new Model(type);

	static
	{
		model.enableSerialization(ConnectTokenSetTest.class, "model");
	}

	public void testIt()
	{
		assertFalse(model.isConnected());
		assertSame(null, getProperties(model));
		assertNotSet();

		final ConnectProperties properties = new ConnectProperties(new File("runtime/utiltest.properties"));
		setProperties(model, properties);
		assertSame(properties, getProperties(model));
		assertEquals(list(), getTokens(model));

		assertSame(properties, removeProperties(model));
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
